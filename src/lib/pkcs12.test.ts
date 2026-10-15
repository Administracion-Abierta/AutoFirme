import { execFileSync } from 'node:child_process';
import { readFileSync, rmSync } from 'node:fs';
import { join } from 'node:path';
import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { concatBytes, readTlv, tlv, type Bytes, type Der } from './asn1';
import { desEncryptBlock, rc2EncryptBlock, rc4 } from './legacy-pbe';
import { loadSignerSession } from './keystore';
import { Pkcs12Error, parsePkcs12 } from './pkcs12';
import { signBytes } from './sign';
import {
	createPkcs12Fixtures,
	FIXTURE_PIN,
	LEAF_COMMON_NAME,
	writeFixtureFile,
	type Pkcs12Fixtures
} from '../test/fixtures';

let fixtures: Pkcs12Fixtures;

beforeAll(() => {
	fixtures = createPkcs12Fixtures();
}, 60_000);

afterAll(() => rmSync(fixtures.dir, { recursive: true, force: true }));

async function codeOf(run: () => Promise<unknown>): Promise<string> {
	try {
		await run();
	} catch (error) {
		if (error instanceof Pkcs12Error) return error.code;
		throw error;
	}
	return 'no error';
}

describe('PKCS#12 loading', () => {
	it('lists every certificate of the container and pairs the key with its own', async () => {
		const session = await loadSignerSession(fixtures.modern, FIXTURE_PIN);

		expect(session.certificates).toHaveLength(2);
		const leaf = session.certificates.find((cert) => cert.commonName === LEAF_COMMON_NAME);
		expect(leaf).toBeDefined();
		expect(leaf?.subject).toBe(`CN=${LEAF_COMMON_NAME}, O=AutoFirme, C=ES`);
		expect(leaf?.issuer).toBe(leaf?.subject); // self-signed
		expect(leaf?.key).not.toBeNull();
		expect(leaf?.algorithm).toEqual({ name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' });
		expect(leaf?.expired).toBe(false);
		expect(leaf?.notYetValid).toBe(false);
		expect(session.signer).toBe(leaf);

		// The CA certificate of the chain is listed but carries no key.
		const ca = session.certificates.find((cert) => cert.commonName !== LEAF_COMMON_NAME);
		expect(ca?.key).toBeNull();
		expect(ca?.algorithm).toBeNull();

		// Dates and validity come from the certificate itself, cross-checked by openssl.
		expect(leaf?.notBefore.toISOString()).toBe(fixtures.notBefore);
		expect(leaf?.notAfter.toISOString()).toBe(fixtures.notAfter);
	});

	it('imports the key as non-extractable and signing-only', async () => {
		const { signer } = await loadSignerSession(fixtures.modern, FIXTURE_PIN);

		expect(signer.key?.extractable).toBe(false);
		expect(signer.key?.usages).toEqual(['sign']);
		// Non-extractable means the key material cannot be read back out of the browser.
		await expect(crypto.subtle.exportKey('pkcs8', signer.key!)).rejects.toThrow();
	});

	it('signs with the imported key, and openssl verifies the signature', async () => {
		const { signer } = await loadSignerSession(fixtures.modern, FIXTURE_PIN);
		const message = new TextEncoder().encode('AutoFirma Web PKCS#12 smoke test');
		const signature = await signBytes(signer, message);

		expect(signature).toHaveLength(256); // RSA-2048
		const messagePath = writeFixtureFile(fixtures.dir, 'message.bin', message);
		const signaturePath = writeFixtureFile(fixtures.dir, 'signature.bin', signature);

		execFileSync('openssl', [
			'dgst', '-sha256', '-verify', 'leaf.pub.pem', '-signature', signaturePath, messagePath
		], { cwd: fixtures.dir, stdio: 'pipe' });

		// A different message must not verify with that signature.
		writeFixtureFile(fixtures.dir, 'tampered.bin', new TextEncoder().encode('AutoFirma Web PKCS#12 smoke tesT'));
		expect(() =>
			execFileSync('openssl', [
				'dgst', '-sha256', '-verify', 'leaf.pub.pem', '-signature', signaturePath, 'tampered.bin'
			], { cwd: fixtures.dir, stdio: 'pipe' })
		).toThrow();
	});

	it('uses the certificate embedded in the file, not a re-parsed copy', async () => {
		const { signer } = await loadSignerSession(fixtures.modern, FIXTURE_PIN);

		// Our DER must be byte-identical to what openssl re-encodes (`-outform DER`).
		execFileSync('openssl', [
			'x509', '-in', 'leaf.pem', '-outform', 'DER', '-out', 'leaf.der'
		], { cwd: fixtures.dir, stdio: 'pipe' });
		expect(Buffer.from(signer.der)).toEqual(readFileSync(`${fixtures.dir}/leaf.der`));
	});

	it('reports a wrong PIN as such', async () => {
		expect(await codeOf(() => loadSignerSession(fixtures.modern, '0000'))).toBe('wrong-pin');
	});

	it('rejects a file that is not a PKCS#12 container', async () => {
		const junk = new TextEncoder().encode('this is not a PKCS#12 container at all');
		expect(await codeOf(() => loadSignerSession(junk, FIXTURE_PIN))).toBe('not-pkcs12');
	});

	it('loads an expired certificate but flags it', async () => {
		const { signer } = await loadSignerSession(fixtures.modern, FIXTURE_PIN);
		const later = new Date(signer.notAfter.getTime() + 1000);

		const session = await loadSignerSession(fixtures.modern, FIXTURE_PIN, { now: later });
		expect(session.signer.expired).toBe(true);
		expect(session.signer.key).not.toBeNull(); // warning only: signing stays available
	});

	it('loads a container whose certificate expired years ago, and still signs with it (#40)', async () => {
		const session = await loadSignerSession(fixtures.expired, FIXTURE_PIN);

		expect(session.signer.expired).toBe(true);
		expect(session.signer.notYetValid).toBe(false);
		expect(session.signer.notAfter.toISOString()).toBe(fixtures.expiredNotAfter);
		// The whole point: an expired certificate warns, it does not take the key away.
		expect(session.signer.key).not.toBeNull();
		const signature = await signBytes(session.signer, new TextEncoder().encode('caducado'));
		expect(signature).toHaveLength(256);
	});

	it('handles an EC P-256 container', async () => {
		const session = await loadSignerSession(fixtures.ec, FIXTURE_PIN);

		expect(session.signer.commonName).toBe('EC Prueba');
		expect(session.signer.algorithm).toEqual({
			name: 'ECDSA',
			hash: 'SHA-256',
			namedCurve: 'P-256'
		});
		const signature = await signBytes(session.signer, new TextEncoder().encode('EC'));
		expect(signature.length).toBe(64); // raw r||s, IEEE P1363
	});

	it('gives every certificate a unique id, even when serials collide', async () => {
		const session = await loadSignerSession(fixtures.dupSerial, FIXTURE_PIN);
		expect(session.certificates).toHaveLength(2);
		// Serials are only unique per issuer (RFC 5280). The picker's keyed list throws on
		// duplicate keys, so the certificate id must not be the serial alone.
		expect(new Set(session.certificates.map((certificate) => certificate.id)).size).toBe(2);
	});

	it('ignores trailing NUL padding after the container', async () => {
		const padded = new Uint8Array(fixtures.modern.length + 4);
		padded.set(fixtures.modern);
		padded.set([0, 0, 0, 0], fixtures.modern.length);

		const session = await loadSignerSession(padded, FIXTURE_PIN);
		expect(session.certificates).toHaveLength(2);
	});

	it('loads a BER container with indefinite lengths and chunked strings', async () => {
		// What Windows' PKCS#12 exporter writes, and what the FNMT citizen keystore of #39 is: no
		// definite lengths above the leaves, and every long string split into chunks.
		const ber = toBer(readTlv(fixtures.modern, 0)[0]);
		expect(ber.length).toBeGreaterThan(fixtures.modern.length); // the EOC markers and chunk headers

		const direct = await loadSignerSession(fixtures.modern, FIXTURE_PIN);
		const session = await loadSignerSession(ber, FIXTURE_PIN);
		expect(session.signer.der).toEqual(direct.signer.der);
		expect(session.certificates.map((certificate) => certificate.der)).toEqual(
			direct.certificates.map((certificate) => certificate.der)
		);

		// The key came through the BER path as real PKCS#8, and openssl accepts its signature.
		const message = new TextEncoder().encode('AutoFirma Web BER smoke');
		const signature = await signBytes(session.signer, message);
		writeFixtureFile(fixtures.dir, 'ber-message.bin', message);
		const signaturePath = writeFixtureFile(fixtures.dir, 'ber-signature.bin', signature);
		execFileSync(
			'openssl',
			['dgst', '-sha256', '-verify', 'leaf.pub.pem', '-signature', signaturePath, 'ber-message.bin'],
			{ cwd: fixtures.dir, stdio: 'pipe' }
		);
	});

	it('keeps the children of an indefinite context tag instead of flattening it as a string', () => {
		// `a3 80 … 00 00` — `certBag [3]` with an indefinite length. Its number collides with
		// universal BIT STRING, which must not turn the context tag into a chunked primitive.
		const [node] = readTlv(new Uint8Array([0xa3, 0x80, 0x30, 0x03, 0x02, 0x01, 0x05, 0x00, 0x00]), 0);
		expect(node.tag).toBe(0xa3);
		expect(node.constructed).toBe(true);
		expect(node.children.map((child) => child.tag)).toEqual([0x30]);
	});
});

const EOC = new Uint8Array([0x00, 0x00]);
const BER_CHUNK = 1000;

/**
 * DER → the BER the CryptoAPI writer emits: constructed values lose their length (`… 80 … 00 00`) and
 * every long OCTET STRING becomes a constructed one holding the value in `BER_CHUNK`-byte pieces.
 * `0x80` is the implicit `[0]` of `encryptedContent` in its definite (primitive) form.
 */
function toBer(node: Der): Bytes {
	if (node.tag === 0x04 || node.tag === 0x80) {
		const chunks: Bytes[] = [];
		for (let offset = 0; offset < node.value.length; offset += BER_CHUNK) {
			chunks.push(tlv(0x04, node.value.subarray(offset, offset + BER_CHUNK)));
		}
		return concatBytes([new Uint8Array([node.tag | 0x20, 0x80]), ...chunks, EOC]);
	}
	if (node.constructed) {
		return concatBytes([new Uint8Array([node.tag, 0x80]), ...node.children.map(toBer), EOC]);
	}
	return node.raw;
}

/** A container whose PBEs are named explicitly — what Windows and FNMT actually write. */
function exportPbes1(name: string, keyPbe: string, certPbe: string): Uint8Array<ArrayBuffer> {
	execFileSync(
		'openssl',
		[
			'pkcs12', '-export', '-inkey', 'leaf.key', '-in', 'leaf.pem', '-certfile', 'ca.pem',
			'-out', name, '-passout', `pass:${FIXTURE_PIN}`, '-keypbe', keyPbe, '-certpbe', certPbe,
			'-macalg', 'sha1', '-legacy'
		],
		{ cwd: fixtures.dir, stdio: 'pipe' }
	);
	return new Uint8Array(readFileSync(join(fixtures.dir, name)));
}

describe('PKCS#12 legacy PBES1 (SHA-1 KDF + 3DES/RC2)', () => {
	it('loads a 3DES container and the decrypted key is real PKCS#8', async () => {
		const container = exportPbes1('pbes1-3des.p12', 'PBE-SHA1-3DES', 'PBE-SHA1-3DES');
		const { certificates, privateKey } = await parsePkcs12(container, FIXTURE_PIN);

		expect(certificates).toHaveLength(2);
		// The proof that the DES/KDF output is the genuine key: WebCrypto accepts it.
		await expect(
			crypto.subtle.importKey(
				'pkcs8',
				privateKey,
				{ name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' },
				false,
				['sign']
			)
		).resolves.toBeDefined();
	});

	it('loads the RC2 128-bit variant', async () => {
		const container = exportPbes1('pbes1-rc2.p12', 'PBE-SHA1-RC2-128', 'PBE-SHA1-RC2-128');
		const { certificates, privateKey } = await parsePkcs12(container, FIXTURE_PIN);

		expect(certificates).toHaveLength(2);
		await expect(
			crypto.subtle.importKey(
				'pkcs8',
				privateKey,
				{ name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' },
				false,
				['sign']
			)
		).resolves.toBeDefined();
	});

	it('loads the -legacy container (3DES keys, RC2-40 certificates)', async () => {
		const session = await loadSignerSession(fixtures.legacy, FIXTURE_PIN);

		expect(session.certificates).toHaveLength(1);
		expect(session.signer.commonName).toBe(LEAF_COMMON_NAME);
	});

	it('loads the RC4 variants (stream cipher, no padding)', async () => {
		const container = exportPbes1('pbes1-rc4.p12', 'PBE-SHA1-RC4-128', 'PBE-SHA1-RC4-40');
		const { certificates, privateKey } = await parsePkcs12(container, FIXTURE_PIN);

		expect(certificates).toHaveLength(2);
		await expect(
			crypto.subtle.importKey(
				'pkcs8',
				privateKey,
				{ name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' },
				false,
				['sign']
			)
		).resolves.toBeDefined();
	});

	it('reports a wrong PIN on the RC4 container as wrong-pin', async () => {
		// RC4 cannot tell a bad key from a good one by itself; only the MAC can.
		const container = exportPbes1('pbes1-rc4.p12', 'PBE-SHA1-RC4-128', 'PBE-SHA1-RC4-40');
		expect(await codeOf(() => parsePkcs12(container, '0000'))).toBe('wrong-pin');
	});

	it('reports a wrong PIN on a 3DES container as wrong-pin', async () => {
		const container = exportPbes1('pbes1-3des.p12', 'PBE-SHA1-3DES', 'PBE-SHA1-3DES');
		expect(await codeOf(() => parsePkcs12(container, '0000'))).toBe('wrong-pin');
	});

	it('reports a wrong PIN on the RC2-40 container as wrong-pin', async () => {
		expect(await codeOf(() => loadSignerSession(fixtures.legacy, '0000'))).toBe('wrong-pin');
	});

	it('rejects an altered MacData even with the right PIN (the MAC is really checked)', async () => {
		const container = exportPbes1('pbes1-3des.p12', 'PBE-SHA1-3DES', 'PBE-SHA1-3DES');
		// MacData is the last field, and its trailing `iterations` INTEGER is the final byte.
		// Nothing else reads it, so a container whose MAC were not verified would still load.
		expect(container[container.length - 1]).toBe(0);
		const tampered = container.slice();
		tampered[tampered.length - 1] = 1;

		expect(await codeOf(() => parsePkcs12(tampered, FIXTURE_PIN))).toBe('wrong-pin');
	});
});

describe('PBES1 primitives, known-answer vectors', () => {
	const bytes = (hex: string) =>
		new Uint8Array(hex.match(/../g)!.map((pair) => parseInt(pair, 16)));
	const hex = (value: Uint8Array) =>
		[...value].map((byte) => byte.toString(16).padStart(2, '0')).join('');

	it('DES matches the published ECB vector', () => {
		expect(hex(desEncryptBlock(bytes('133457799bbcdff1'), bytes('0123456789abcdef')))).toBe(
			'85e813540f0ab405'
		);
	});

	it('RC2 key expansion matches RFC 2268', () => {
		expect(
			hex(rc2EncryptBlock(bytes('88bca90e90875a7f0f79c384627bafb2'), 128, bytes('0000000000000000')))
		).toBe('2269552ab0f85ca6');
		// Byte order matters here: RFC 2268 vectors are little-endian words.
		expect(hex(rc2EncryptBlock(bytes('3000000000000000'), 64, bytes('1000000000000001')))).toBe(
			'30649edf9be7d2c2'
		);
	});

	it('RC4 matches the published stream vector', () => {
		const encoder = new TextEncoder();
		expect(hex(rc4(encoder.encode('Key'), encoder.encode('Plaintext')))).toBe('bbf316e8d940af0ad3');
	});
});
