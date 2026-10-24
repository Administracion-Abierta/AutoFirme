/**
 * PKCS#11 provider + certificate filters (issue #100). The provider is exercised through its own
 * `CryptokiModule` seam — a fake bridge standing in for the native one — over certificates
 * generated on the fly with openssl, so nothing binary is committed and the filter verdicts are
 * cross-checked against the extensions openssl was asked to write.
 *
 * The provider's sign path is proven end to end: the fake bridge signs with the fixture's real
 * RSA key exactly as the token would (hash + sign in one call, which is what the named mechanisms
 * do), and `openssl dgst -verify` checks the bytes the provider returned. That is the provider
 * boundary, not the app's signing path: `cms.ts`/`sign.ts` still require a WebCrypto `key`, so
 * vendoring a token key into a signature is a `sign.ts` step (#97). No module path is hardcoded in
 * the provider: every test passes one in, and the missing-path case is a typed error.
 */

import { execFileSync } from 'node:child_process';
import { createPrivateKey, sign as nodeSign } from 'node:crypto';
import { mkdtempSync, readFileSync, rmSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { cleanup, render } from '@testing-library/svelte';
import { afterAll, beforeAll, describe, expect, it, vi } from 'vitest';
import { integerBytes, parseDer, type Bytes } from '../asn1';
import { certificateSource, PKCS12_SOURCE } from '../certstore';
import { ecdsaRawToDer } from '../der';
import Pkcs11Badge from './pkcs11-badge.svelte';
import {
	certFilterReason,
	isModulePath,
	objectToSignerCert,
	objectsToSignerCerts,
	Pkcs11Error,
	Pkcs11Provider,
	pkcs11Source,
	SIGNING_MECHANISMS,
	signOnly,
	unavailableModule,
	type CryptokiModule,
	type Pkcs11Object
} from './pkcs11';

const MODULE_PATH = '/usr/lib/x86_64-linux-gnu/opensc-pkcs11.so';
const BADGE = 'pkcs11:opensc-pkcs11.so';

interface Probes {
	dir: string;
	/** DER of every fixture, keyed by label. */
	der: Map<string, Bytes>;
	/** `CKA_ID` per label, as a token would report it. */
	keyIds: Map<string, string>;
	publicKeyPem: string;
}

function openssl(args: string[], cwd: string): string {
	return execFileSync('openssl', args, { cwd, stdio: 'pipe' }).toString();
}

/**
 * One RSA key shared by every fixture (the filter only reads extensions, and the sign test needs
 * one real key), plus seven self-signed certificates whose extensions are the cases under test.
 * `-not_before`/`-not_after` pin the two skewed validity windows; OpenSSL ≥ 3.4 has them.
 */
function createProbes(): Probes {
	const dir = mkdtempSync(join(tmpdir(), 'autofirme-p11-'));
	openssl(['genrsa', '-traditional', '-out', 'key.pem', '2048'], dir);
	openssl(['rsa', '-in', 'key.pem', '-pubout', '-out', 'key.pub.pem'], dir);
	// One EC key as well: the ECDSA path takes a different mechanism and a different payload.
	openssl(['ecparam', '-name', 'prime256v1', '-genkey', '-noout', '-out', 'ec.pem'], dir);

	const spec: Array<[string, string, string[]]> = [
		['Signer', 'basicConstraints=critical,CA:FALSE\nkeyUsage=critical,digitalSignature\nextendedKeyUsage=codeSigning', ['-days', '3650']],
		// digitalSignature missing, keyEncipherment only: a decryption-only key cannot sign.
		['Encipher', 'basicConstraints=critical,CA:FALSE\nkeyUsage=critical,keyEncipherment', ['-days', '3650']],
		// digitalSignature present, but the EKU only allows TLS server use.
		['Tls', 'basicConstraints=critical,CA:FALSE\nkeyUsage=critical,digitalSignature\nextendedKeyUsage=serverAuth', ['-days', '3650']],
		// RFC 9336 `id-kp-documentSigning`: what a qualified document-signing certificate carries
		// instead of the RFC 5280 codeSigning/emailProtection that predate it.
		['Document', 'basicConstraints=critical,CA:FALSE\nkeyUsage=critical,digitalSignature\nextendedKeyUsage=1.3.6.1.5.5.7.3.36', ['-days', '3650']],
		['Authority', 'basicConstraints=critical,CA:TRUE\nkeyUsage=critical,keyCertSign,cRLSign', ['-days', '3650']],
		// A window entirely in the past, and one entirely in the future: both are excludes.
		['Expired', 'basicConstraints=critical,CA:FALSE\nkeyUsage=critical,digitalSignature', ['-not_before', '20240101000000Z', '-not_after', '20250101000000Z']],
		['Future', 'basicConstraints=critical,CA:FALSE\nkeyUsage=critical,digitalSignature', ['-not_before', '20270101000000Z', '-not_after', '20280101000000Z']]
	];

	const der = new Map<string, Bytes>();
	const keyIds = new Map<string, string>();
	spec.forEach(([label, extensions, window], index) => {
		const name = label.toLowerCase();
		openssl([
			'req', '-x509', '-key', 'key.pem', '-out', `${name}.pem`, ...window, '-sha256',
			'-set_serial', `0x1${index + 1}`,
			'-subj', `/C=ES/O=AutoFirme/CN=${name}`,
			'-addext', extensions
		], dir);
		openssl(['x509', '-in', `${name}.pem`, '-outform', 'DER', '-out', `${name}.der`], dir);
		der.set(label, new Uint8Array(readFileSync(join(dir, `${name}.der`))));
		keyIds.set(label, `${index + 1}`);
	});

	// A key kind the provider has no mechanism for. Ed25519 is one OpenSSL writes and
	// `parseCertificate` reports as `other`, so it stands in for any such token key.
	openssl(['genpkey', '-algorithm', 'ED25519', '-out', 'other.pem'], dir);
	openssl([
		'req', '-x509', '-key', 'other.pem', '-out', 'other.pem.crt', '-days', '3650', '-sha256',
		'-set_serial', '0x1f', '-subj', '/C=ES/O=AutoFirme/CN=other',
		'-addext', 'basicConstraints=critical,CA:FALSE',
		'-addext', 'keyUsage=critical,digitalSignature'
	], dir);
	openssl(['x509', '-in', 'other.pem.crt', '-outform', 'DER', '-out', 'other.der'], dir);
	der.set('Other', new Uint8Array(readFileSync(join(dir, 'other.der'))));
	keyIds.set('Other', 'ff');

	return { dir, der, keyIds, publicKeyPem: readFileSync(join(dir, 'key.pub.pem'), 'utf8') };
}

let probes: Probes;

beforeAll(() => {
	probes = createProbes();
}, 60_000);

afterAll(() => rmSync(probes.dir, { recursive: true, force: true }));

function objectFor(label: string): Pkcs11Object {
	return { keyId: probes.keyIds.get(label)!, label, der: probes.der.get(label)! };
}

function certFor(label: string, slotId = 0) {
	return objectToSignerCert(objectFor(label), BADGE, slotId)!;
}

/** The bridge a real token would provide: real RSA signing with the fixture key, one call. */
function signingBridge(
	sign = vi.fn(async (_slotId: number, _keyId: string, _mechanism: string, data: Bytes) =>
		new Uint8Array(
			nodeSign('sha256', data, createPrivateKey(readFileSync(join(probes.dir, 'key.pem'), 'utf8')))
		)
	)
): CryptokiModule {
	return {
		listSlots: async () => [
			{
				id: 0,
				description: 'AutoFirme Virtual Reader 0',
				token: { label: 'Tarjeta', manufacturer: 'ACME' }
			},
			{ id: 1, description: 'AutoFirme Virtual Reader 1', token: null }
		],
		listCertificates: async (slotId) =>
			slotId === 0 ? ['Signer', 'Encipher', 'Tls', 'Authority', 'Expired', 'Future'].map(objectFor) : [],
		sign,
		login: async (_slotId, pin) => {
			if (pin !== '1234') throw new Pkcs11Error('wrong-pin', 'the token rejected the PIN');
		}
	};
}

describe('module path', () => {
	it('builds the badge from the module file name, never the full path', () => {
		expect(pkcs11Source(MODULE_PATH)).toBe(BADGE);
		expect(pkcs11Source('C:\\Windows\\System32\\eIDPKCS11.dll')).toBe('pkcs11:eIDPKCS11.dll');
		expect(pkcs11Source('/usr/local/lib/libsofthsm2.so')).toBe('pkcs11:libsofthsm2.so');
	});

	it('is required: no module path, no provider', () => {
		expect(() => new Pkcs11Provider({ modulePath: '' })).toThrow(Pkcs11Error);
		expect(() => pkcs11Source('/usr/lib/')).toThrow(Pkcs11Error);
	});

	it('recognises a shared library the OS loader could open', () => {
		expect(isModulePath(MODULE_PATH)).toBe(true);
		expect(isModulePath('/usr/lib/opensc-pkcs11.so.1.3')).toBe(true);
		expect(isModulePath('/Users/x/Library/eID.dylib')).toBe(true);
		expect(isModulePath('/usr/bin/opensc-tool')).toBe(false);
		expect(isModulePath('opensc-pkcs11')).toBe(false);
	});

	it('reports an unloaded module as unavailable instead of pretending it works', async () => {
		const provider = new Pkcs11Provider({ modulePath: MODULE_PATH });
		await expect(provider.slots()).rejects.toMatchObject({ code: 'unavailable' });
		await expect(provider.certificates(0)).rejects.toMatchObject({ code: 'unavailable' });
		expect(provider.sourceLabel).toBe(BADGE);
		await expect(unavailableModule(MODULE_PATH).login(0, '1234')).rejects.toBeInstanceOf(Pkcs11Error);
	});
});

describe('provider enumeration', () => {
	it('lists slots with and without a token, keeping the empty reader visible', async () => {
		const provider = new Pkcs11Provider({ modulePath: MODULE_PATH, bridge: signingBridge() });
		expect(await provider.slots()).toEqual([
			{
				id: 0,
				description: 'AutoFirme Virtual Reader 0',
				token: { label: 'Tarjeta', manufacturer: 'ACME' }
			},
			{ id: 1, description: 'AutoFirme Virtual Reader 1', token: null }
		]);
	});

	it('refuses a slot that has no token instead of listing nothing', async () => {
		const provider = new Pkcs11Provider({ modulePath: MODULE_PATH, bridge: signingBridge() });
		await expect(provider.certificates(1)).rejects.toMatchObject({ code: 'no-token' });
		await expect(provider.certificates(9)).rejects.toMatchObject({ code: 'no-token' });
	});

	it('logs in through the callback and never keeps the PIN', async () => {
		const login = vi.fn(async () => '1234');
		const provider = new Pkcs11Provider({
			modulePath: MODULE_PATH,
			bridge: signingBridge(),
			login
		});
		expect(login).not.toHaveBeenCalled();
		await provider.certificates(0);
		expect(login).toHaveBeenCalledTimes(1);
		expect(login).toHaveBeenCalledWith(
			expect.objectContaining({ id: 0, token: expect.objectContaining({ label: 'Tarjeta' }) })
		);
		// A second listing asks again: the provider holds no PIN of its own.
		await provider.certificates(0);
		expect(login).toHaveBeenCalledTimes(2);
	});

	it('surfaces a wrong PIN as its own code', async () => {
		const provider = new Pkcs11Provider({ modulePath: MODULE_PATH, bridge: signingBridge() });
		await expect(provider.certificates(0, '0000')).rejects.toMatchObject({ code: 'wrong-pin' });
	});

	it('lists the certificates of the token, sign-only, newest expiry first', async () => {
		const provider = new Pkcs11Provider({ modulePath: MODULE_PATH, bridge: signingBridge() });
		const certs = await provider.certificates(0, '1234');
		// The alias the token reports wins over the certificate's own CN; the CA, keyUsage,
		// EKU, expired and not-yet-valid rows are gone.
		expect(certs.map((cert) => cert.commonName)).toEqual(['Signer']);
		expect(certs.map((cert) => cert.source)).toEqual([BADGE]);
		expect(certs[0].tokenRef).toEqual({ slotId: 0, keyId: probes.keyIds.get('Signer') });
		// The key is on the token, not in a WebCrypto handle — and the algorithm is still known.
		expect(certs[0].key).toBeNull();
		expect(certs[0].algorithm).toEqual({ name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' });
	});
});

describe('certificate filters', () => {
	it('accepts a digitalSignature + codeSigning certificate that is valid now', () => {
		expect(certFilterReason(certFor('Signer'))).toBeNull();
		expect(signOnly([certFor('Signer'), certFor('Future')]).map((cert) => cert.commonName)).toEqual([
			'Signer'
		]);
	});

	it('hides a certificate whose keyUsage forbids signing', () => {
		expect(certFilterReason(certFor('Encipher'))).toBe('key-usage');
	});

	it('hides a certificate whose EKU has no signing purpose', () => {
		expect(certFilterReason(certFor('Tls'))).toBe('eku');
	});

	it('accepts a qualified document-signing certificate (RFC 9336 id-kp-documentSigning)', () => {
		expect(certFilterReason(certFor('Document'))).toBeNull();
	});

	it('hides a CA certificate', () => {
		expect(certFilterReason(certFor('Authority'))).toBe('ca');
	});

	it('hides expired and not-yet-valid certificates', () => {
		expect(certFilterReason(certFor('Expired'))).toBe('expired');
		expect(certFilterReason(certFor('Future'))).toBe('not-yet-valid');
		// The same certificate, judged at a moment inside its own window, is fine.
		expect(certFilterReason(certFor('Expired'), Date.UTC(2024, 5, 1))).toBeNull();
	});

	it('hides a certificate with no key anywhere, and one whose DER cannot be read', () => {
		const tokenCert = certFor('Signer');
		expect(certFilterReason({ ...tokenCert, tokenRef: undefined })).toBe('no-key');
		expect(certFilterReason({ ...tokenCert, der: new Uint8Array([0x30, 0x01, 0x00]) })).toBe(
			'unreadable'
		);
		expect(certFilterReason({ ...tokenCert, notAfter: new Date('nope') })).toBe('unreadable');
	});
});

describe('token objects', () => {
	it('collapses alias-only duplicates, keeping the newest', () => {
		// Two issues of one alias: the picker shows one row, holding the later certificate.
		const certs = objectsToSignerCerts(
			[
				{ keyId: 'a', label: 'Firma', der: probes.der.get('Expired')! },
				{ keyId: 'b', label: 'Firma', der: probes.der.get('Signer')! }
			],
			BADGE,
			0
		);
		expect(certs).toHaveLength(1);
		expect(certs[0].tokenRef.keyId).toBe('b');
	});

	it('keeps unlabelled objects as their own rows', () => {
		const certs = objectsToSignerCerts(
			[
				{ keyId: 'a', label: '', der: probes.der.get('Signer')! },
				{ keyId: 'b', label: '', der: probes.der.get('Tls')! }
			],
			BADGE,
			0
		);
		expect(certs).toHaveLength(2);
		expect(certs.every((cert) => cert.commonName.length > 0)).toBe(true);
	});

	it('drops an object whose DER is not a certificate instead of failing the listing', () => {
		const certs = objectsToSignerCerts(
			[
				{ keyId: 'a', label: 'Roto', der: new Uint8Array([0x30, 0x01, 0x00]) },
				{ keyId: 'b', label: 'Firma', der: probes.der.get('Signer')! }
			],
			BADGE,
			3
		);
		expect(certs.map((cert) => cert.tokenRef.keyId)).toEqual(['b']);
		expect(certs[0].tokenRef.slotId).toBe(3);
	});

	it('reads a single object through the same mapping', () => {
		const one = objectToSignerCert(
			{ keyId: probes.keyIds.get('Signer')!, label: 'Única', der: probes.der.get('Signer')! },
			BADGE,
			7
		);
		expect(one?.commonName).toBe('Única');
		expect(one?.tokenRef.slotId).toBe(7);
		expect(objectToSignerCert({ keyId: 'x', label: 'no', der: new Uint8Array([1]) }, BADGE, 0)).toBeNull();
	});

	it('drops a certificate whose key kind has no mechanism, instead of offering RSA for it', () => {
		// A DSA or Ed25519 token key would be handed `CKM_SHA256_RSA_PKCS` and fail at
		// `C_SignInit` — after the user had already picked it in the picker.
		expect(objectToSignerCert(objectFor('Other'), BADGE, 0)).toBeNull();
		const listed = objectsToSignerCerts([objectFor('Other'), objectFor('Signer')], BADGE, 0);
		expect(listed.map((cert) => cert.commonName)).toEqual(['Signer']);
	});
});

describe('signing through the provider', () => {
	it('signs the bytes with the token key and openssl verifies them', async () => {
		const provider = new Pkcs11Provider({ modulePath: MODULE_PATH, bridge: signingBridge() });
		const [cert] = await provider.certificates(0, '1234');
		const data = new TextEncoder().encode('factura 2026-01 — 1.234,56 €');

		const signature = await provider.sign(cert, data);
		expect(signature.length).toBe(256); // RSA-2048

		const sigPath = join(probes.dir, 'signature.bin');
		const dataPath = join(probes.dir, 'data.bin');
		writeFileSync(sigPath, signature);
		writeFileSync(dataPath, data);
		const verdict = openssl(
			['dgst', '-sha256', '-verify', 'key.pub.pem', '-signature', sigPath, dataPath],
			probes.dir
		);
		expect(verdict.trim()).toBe('Verified OK');
	});

	it('asks for the raw hashing mechanism per key type, and hashes only for ECDSA', async () => {
		const sign = vi.fn(async () => new Uint8Array([1, 2, 3]));
		const provider = new Pkcs11Provider({ modulePath: MODULE_PATH, bridge: signingBridge(sign) });
		const [cert] = await provider.certificates(0, '1234');
		const data = new TextEncoder().encode('factura');
		await provider.sign(cert, data);
		// RSA: the mechanism hashes internally, so `C_Sign` gets the to-be-signed bytes as they are.
		expect(sign).toHaveBeenCalledWith(0, probes.keyIds.get('Signer'), SIGNING_MECHANISMS.rsa, data);
		// EC: `CKM_ECDSA` signs whatever it is handed, so the provider sends the SHA-256 digest —
		// never the digest as the *data* of a mechanism that would hash it a second time.
		await provider.sign({ ...cert, algorithm: { name: 'ECDSA', hash: 'SHA-256' } }, data);
		expect(sign).toHaveBeenLastCalledWith(
			0,
			probes.keyIds.get('Signer'),
			SIGNING_MECHANISMS.ec,
			new Uint8Array(await crypto.subtle.digest('SHA-256', data))
		);
		// A SHA-384 certificate is refused: the mechanism pair binds SHA-256, and signing under
		// another hash would produce a signature the CMS digest OID contradicts.
		await expect(
			provider.sign({ ...cert, algorithm: { name: 'ECDSA', hash: 'SHA-384' } }, data)
		).rejects.toMatchObject({ code: 'sign-failed' });
	});

	it('signs with an EC key over the hash and openssl verifies the token form', async () => {
		// What a real module does: raw `CKM_ECDSA` over the SHA-256 digest, `r || s` back. openssl
		// produces the signature here so both the mechanism/payload contract and the returned bytes
		// have a third-party oracle — the SoftHSM 2.6 answer replayed without the hardware.
		const sign = vi.fn(async (_slot: number, _key: string, _mechanism: string, digest: Bytes) => {
			writeFileSync(join(probes.dir, 'ec-payload.bin'), digest);
			openssl(
				['pkeyutl', '-sign', '-inkey', 'ec.pem', '-in', 'ec-payload.bin', '-out', 'ec-sig.der'],
				probes.dir
			);
			// DER `SEQUENCE { r, s }` → the token form `r || s`, each coordinate left-padded to 32.
			const [r, s] = parseDer(new Uint8Array(readFileSync(join(probes.dir, 'ec-sig.der')))).children;
			const raw = new Uint8Array(64);
			raw.set(integerBytes(r), 32 - integerBytes(r).length);
			raw.set(integerBytes(s), 64 - integerBytes(s).length);
			return raw;
		});
		const provider = new Pkcs11Provider({ modulePath: MODULE_PATH, bridge: signingBridge(sign) });
		const [cert] = await provider.certificates(0, '1234');
		const data = new TextEncoder().encode('factura 2026-01 — EC');
		const raw = await provider.sign({ ...cert, algorithm: { name: 'ECDSA', hash: 'SHA-256' } }, data);
		// Raw `CKM_ECDSA` gets the digest, not the document: 32 bytes for SHA-256.
		expect(sign.mock.calls[0][2]).toBe(SIGNING_MECHANISMS.ec);
		expect(sign.mock.calls[0][3]).toEqual(new Uint8Array(await crypto.subtle.digest('SHA-256', data)));
		expect(raw.length).toBe(64); // P-256: two 32-byte coordinates

		// The token form is `r || s`; X.509/CMS wants `SEQUENCE { r, s }` (`der.ts`'s
		// `ecdsaRawToDer`, mirrored locally in `cms.ts`). What this proves is that the bytes a
		// token returns convert to a DER signature openssl accepts — NOT that the CMS path
		// consumes them: `signCadesDetached` (and `signBytes` below it) throws "has no private
		// key" for a `key: null` signer before any conversion, so vendoring the token into the
		// CMS path is a separate step in `sign.ts` (#97).
		openssl(['ec', '-in', 'ec.pem', '-pubout', '-out', 'ec.pub.pem'], probes.dir);
		writeFileSync(join(probes.dir, 'ec-data.bin'), data);
		writeFileSync(join(probes.dir, 'ec-signature.der'), ecdsaRawToDer(raw, 32));
		const verdict = openssl(
			['dgst', '-sha256', '-verify', 'ec.pub.pem', '-signature', 'ec-signature.der', 'ec-data.bin'],
			probes.dir
		);
		expect(verdict.trim()).toBe('Verified OK');
	});

	it('refuses a certificate that belongs to another module or slot', async () => {
		const provider = new Pkcs11Provider({ modulePath: MODULE_PATH, bridge: signingBridge() });
		const [cert] = await provider.certificates(0, '1234');
		await expect(
			provider.sign({ ...cert, source: 'pkcs11:other.so' }, new Uint8Array())
		).rejects.toMatchObject({ code: 'sign-failed' });
		await expect(
			provider.sign({ ...cert, tokenRef: { slotId: -1, keyId: 'x' } }, new Uint8Array())
		).rejects.toMatchObject({ code: 'sign-failed' });
	});
});

describe('certificate sources for the badge', () => {
	it('defaults an upload to pkcs12 and passes a token source through', () => {
		expect(certificateSource(undefined)).toBe(PKCS12_SOURCE);
		expect(certificateSource(certFor('Signer').source)).toBe(BADGE);
	});
});

describe('source badge', () => {
	it('renders pkcs11:<module> with the slot identity, and nothing for an upload', () => {
		const { container } = render(Pkcs11Badge, {
			props: { source: BADGE, slot: { id: 0, description: 'AutoFirme Virtual Reader 0' } }
		});
		const badge = container.querySelector('[data-cert-source]');
		expect(badge?.textContent).toBe(BADGE);
		expect(badge?.getAttribute('title')).toBe(`${BADGE} — AutoFirme Virtual Reader 0 (slot 0)`);

		// A PKCS#12 upload: the file name is the badge, so this renders nothing at all.
		cleanup();
		expect(
			render(Pkcs11Badge, { props: { source: PKCS12_SOURCE } }).container.textContent?.trim()
		).toBe('');
	});
});
