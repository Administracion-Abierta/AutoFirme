/**
 * CAdES-BES cross-verification: every signature this module produces is fed back through
 * `openssl cms -verify`, which is the acceptance oracle (#6), and its signed attributes are
 * compared with a known-good vector generated once by OpenSSL itself (test/fixtures/cades/).
 */

import { execFileSync } from 'node:child_process';
import { createHash } from 'node:crypto';
import { readFileSync, rmSync, writeFileSync } from 'node:fs';
import { join, resolve } from 'node:path';
import { afterAll, beforeAll, describe, expect, it, vi } from 'vitest';
import { type Bytes, type Der, concatBytes, derContent, explicit, oid, parseDer } from './asn1';
import { signCadesDetached } from './cms';
import { loadSignerSession } from './keystore';
import { OID_TIME_STAMP_TOKEN } from './timestamp';
import {
	cmsTimeStamp,
	createCadesFixtures,
	createTsaFixtures,
	FIXTURE_PIN,
	tsVerifyToken,
	type CadesFixtures,
	writeFixtureFile
} from '../test/fixtures';

const OID_DATA = '1.2.840.113549.1.7.1';
const OID_CONTENT_TYPE = '1.2.840.113549.1.9.3';
const OID_MESSAGE_DIGEST = '1.2.840.113549.1.9.4';
const OID_SIGNING_TIME = '1.2.840.113549.1.9.5';
const OID_SIGNING_CERTIFICATE_V2 = '1.2.840.113549.1.9.16.2.47';
const OID_CERTIFICATE_REFS = '1.2.840.113549.1.9.16.2.21';
const OID_REVOCATION_REFS = '1.2.840.113549.1.9.16.2.22';
/** `id-aa-ets-escTimeStamp` — the CAdES-C time-stamp CAdES-X Long type 1 carries. */
const OID_ESC_TIME_STAMP = '1.2.840.113549.1.9.16.2.25';
/** `id-aa-ets-certCRLTimestamp` — the refs-only time-stamp type 2 carries. */
const OID_CERT_CRL_TIME_STAMP = '1.2.840.113549.1.9.16.2.26';
const SIGNING_TIME = new Date(Date.UTC(2026, 9, 14, 12, 0, 0));
/** Vitest runs with the project directory as cwd; `import.meta.url` is an http URL under jsdom. */
const GOLDEN_DIR = resolve(process.cwd(), 'src/test/fixtures/cades');

let fixtures: CadesFixtures;

beforeAll(() => {
	fixtures = createCadesFixtures();
}, 60_000);

afterAll(() => rmSync(fixtures.dir, { recursive: true, force: true }));

/** `null` when openssl accepts the signature, its diagnostics otherwise. */
function verify(signature: Bytes, contentPath: string, caPath: string, extra: string[] = []): string | null {
	const signaturePath = join(fixtures.dir, 'signature.csig');
	writeFileSync(signaturePath, signature);
	try {
		execFileSync(
			'openssl',
			[
				'cms', '-verify', '-inform', 'DER', '-in', signaturePath, '-content', contentPath,
				'-CAfile', caPath, '-purpose', 'any', '-binary', '-out', join(fixtures.dir, 'verified.bin'),
				...extra
			],
			{ stdio: 'pipe' }
		);
		return null;
	} catch (error) {
		return (error as { stderr?: Buffer }).stderr?.toString() ?? String(error);
	}
}

function signedData(signature: Bytes): Der {
	return explicit(parseDer(signature).children[1], 0xa0);
}

function signerInfo(signature: Bytes): Der {
	return signedData(signature).children[4].children[0];
}

function attributesByOid(signature: Bytes): Map<string, Der> {
	const attributes = signerInfo(signature).children[3].children;
	return new Map(attributes.map((attribute) => [oid(attribute.children[0]), attribute]));
}

function attributeValue(attributes: Map<string, Der>, id: string): Der {
	return attributes.get(id)!.children[1].children[0];
}

describe('CAdES-BES detached signing', () => {
	it('produces a signature openssl cms -verify accepts, detached and with ESS checked', async () => {
		const { signer } = await loadSignerSession(fixtures.p12, FIXTURE_PIN);
		const signature = await signCadesDetached(signer, fixtures.content, { signingTime: SIGNING_TIME });

		expect(verify(signature, fixtures.contentPath, fixtures.caPath)).toBeNull();
		// `-cades` additionally checks the signing-certificate attribute of the signer.
		expect(verify(signature, fixtures.contentPath, fixtures.caPath, ['-cades'])).toBeNull();
	});

	it('carries contentType, messageDigest, signingTime and signingCertificateV2', async () => {
		const { signer } = await loadSignerSession(fixtures.p12, FIXTURE_PIN);
		const signature = await signCadesDetached(signer, fixtures.content, { signingTime: SIGNING_TIME });
		const attributes = attributesByOid(signature);

		expect([...attributes.keys()].sort()).toEqual(
			[OID_CONTENT_TYPE, OID_MESSAGE_DIGEST, OID_SIGNING_CERTIFICATE_V2, OID_SIGNING_TIME].sort()
		);
		expect(oid(attributeValue(attributes, OID_CONTENT_TYPE))).toBe(OID_DATA);
		expect([...attributeValue(attributes, OID_MESSAGE_DIGEST).value]).toEqual([
			...createHash('sha256').update(fixtures.content).digest()
		]);
		expect(new TextDecoder().decode(attributeValue(attributes, OID_SIGNING_TIME).value)).toBe(
			'261014120000Z'
		);

		// Detached: the document travels only as the digest, never as eContent.
		const encapContentInfo = signedData(signature).children[2];
		expect(encapContentInfo.children).toHaveLength(1);
		expect(oid(encapContentInfo.children[0])).toBe(OID_DATA);
		expect(signature).toEqual(await signCadesDetached(signer, fixtures.content, { signingTime: SIGNING_TIME }));
	});

	it('signs with an EC key too (raw WebCrypto r||s re-encoded as a DER SEQUENCE)', async () => {
		const { signer } = await loadSignerSession(fixtures.ecP12, FIXTURE_PIN);
		const signature = await signCadesDetached(signer, fixtures.content, { signingTime: SIGNING_TIME });

		expect(signer.algorithm?.name).toBe('ECDSA');
		expect(verify(signature, fixtures.contentPath, fixtures.caPath, ['-cades'])).toBeNull();
	});

	it('is rejected by openssl when the document does not match', async () => {
		const { signer } = await loadSignerSession(fixtures.p12, FIXTURE_PIN);
		const signature = await signCadesDetached(signer, fixtures.content, { signingTime: SIGNING_TIME });
		const tampered = writeFixtureFile(fixtures.dir, 'tampered.bin', new TextEncoder().encode('otro documento\n'));

		expect(verify(signature, tampered, fixtures.caPath)).toMatch(/failure/i);
	});

	it('adds an RFC 3161 timestamp over the signature value when a TSA is set (CAdES-T)', async () => {
		const tsa = createTsaFixtures();
		try {
			const { signer } = await loadSignerSession(fixtures.p12, FIXTURE_PIN);
			vi.stubGlobal('fetch', async (_url: string, init: RequestInit) => {
				const query = init.body;
				if (!(query instanceof Uint8Array)) throw new Error('the timestamp request has no DER body');
				return new Response(tsa.respond(query), { status: 200 });
			});

			const signature = await signCadesDetached(signer, fixtures.content, {
				signingTime: SIGNING_TIME,
				timeStamp: 'https://tsa.test/'
			});
			vi.unstubAllGlobals();

			// The timestamp is an *unsigned* attribute: the signed attributes, and therefore the
			// signature value, are the same ones a BES signature carries, and openssl still accepts it.
			expect(verify(signature, fixtures.contentPath, fixtures.caPath, ['-cades'])).toBeNull();
			expect([...attributesByOid(signature).keys()].sort()).toEqual(
				[OID_CONTENT_TYPE, OID_MESSAGE_DIGEST, OID_SIGNING_CERTIFICATE_V2, OID_SIGNING_TIME].sort()
			);

			const unsignedAttrs = signerInfo(signature).children[6];
			expect(oid(unsignedAttrs.children[0].children[0])).toBe(OID_TIME_STAMP_TOKEN);

			// The token covers the signature value openssl just verified.
			const { signature: value, token } = cmsTimeStamp(signature);
			expect(tsVerifyToken(tsa.dir, tsa.certPath, token, value)).toMatch(/Verification: OK/);
		} finally {
			vi.unstubAllGlobals();
			rmSync(tsa.dir, { recursive: true, force: true });
		}
	});

	it('refuses to sign with a certificate that has no private key', async () => {
		const session = await loadSignerSession(fixtures.p12, FIXTURE_PIN);
		const withoutKey = session.certificates.find((certificate) => certificate.key === null)!;

		await expect(signCadesDetached(withoutKey, fixtures.content)).rejects.toThrow('no private key');
	});

	it('adds certificate and revocation refs for X Long type 1', async () => {
		const tsa = createTsaFixtures();
		try {
			const { signer } = await loadSignerSession(fixtures.p12, FIXTURE_PIN);
			vi.stubGlobal('fetch', async (_url: string, init: RequestInit) => {
				const query = init.body;
				if (!(query instanceof Uint8Array)) throw new Error('the timestamp request has no DER body');
				return new Response(tsa.respond(query), { status: 200 });
			});

			const signature = await signCadesDetached(signer, fixtures.content, {
				signingTime: SIGNING_TIME,
				timeStamp: 'https://tsa.test/',
				xLong: 1
			});
			vi.unstubAllGlobals();

			expect(verify(signature, fixtures.contentPath, fixtures.caPath, ['-cades'])).toBeNull();
			const unsigned = new Map(
				signerInfo(signature).children[6].children.map((attribute) => [oid(attribute.children[0]), attribute])
			);
			expect([...unsigned.keys()].sort()).toEqual(
				[OID_TIME_STAMP_TOKEN, OID_CERTIFICATE_REFS, OID_REVOCATION_REFS, OID_ESC_TIME_STAMP].sort()
			);
			// One OtherCertID sequence per embedded certificate; openssl parses the refs structure.
			expect(unsigned.get(OID_CERTIFICATE_REFS)!.children[1].children[0].tag).toBe(0x30);

			// SET OF sorts attributes, so locate the tokens by OID instead of position.
			const timeStampAttr = unsigned.get(OID_TIME_STAMP_TOKEN)!;
			const token = timeStampAttr.children[1].children[0].raw;
			const { signature: value } = cmsTimeStamp(signature);
			// The CAdES-T token still covers the bare signature value (RFC 5126 §6.1).
			expect(tsVerifyToken(tsa.dir, tsa.certPath, token, value)).toMatch(/Verification: OK/);

			// The escTimeStamp covers the signature, that token and both refs — each attribute as
			// attrType+attrValues without its outer SEQUENCE (RFC 5126 §6.3.5 NOTE 2).
			const covered = concatBytes([
				value,
				derContent(timeStampAttr.raw),
				derContent(unsigned.get(OID_CERTIFICATE_REFS)!.raw),
				derContent(unsigned.get(OID_REVOCATION_REFS)!.raw)
			]);
			const xToken = unsigned.get(OID_ESC_TIME_STAMP)!.children[1].children[0].raw;
			expect(tsVerifyToken(tsa.dir, tsa.certPath, xToken, covered)).toMatch(/Verification: OK/);
		} finally {
			vi.unstubAllGlobals();
			rmSync(tsa.dir, { recursive: true, force: true });
		}
	});

	it('timestamps the refs for X Long type 2 (CAdES-C timestamp)', async () => {
		const tsa = createTsaFixtures();
		try {
			const { signer } = await loadSignerSession(fixtures.p12, FIXTURE_PIN);
			vi.stubGlobal('fetch', async (_url: string, init: RequestInit) => {
				const query = init.body;
				if (!(query instanceof Uint8Array)) throw new Error('the timestamp request has no DER body');
				return new Response(tsa.respond(query), { status: 200 });
			});

			const signature = await signCadesDetached(signer, fixtures.content, {
				signingTime: SIGNING_TIME,
				timeStamp: 'https://tsa.test/',
				xLong: 2
			});
			vi.unstubAllGlobals();

			expect(verify(signature, fixtures.contentPath, fixtures.caPath, ['-cades'])).toBeNull();
			const unsigned = signerInfo(signature).children[6].children;
			const byOid = (id: string) => unsigned.filter((attribute) => oid(attribute.children[0]) === id);
			// One CAdES-T token over the signature, one certCRLTimestamp over the refs.
			expect(unsigned.map((attribute) => oid(attribute.children[0])).sort()).toEqual(
				[OID_TIME_STAMP_TOKEN, OID_CERT_CRL_TIME_STAMP, OID_CERTIFICATE_REFS, OID_REVOCATION_REFS].sort()
			);
			const refs = concatBytes([
				derContent(byOid(OID_CERTIFICATE_REFS)[0].raw),
				derContent(byOid(OID_REVOCATION_REFS)[0].raw)
			]);
			const token = byOid(OID_CERT_CRL_TIME_STAMP)[0].children[1].children[0].raw;
			// Type 2 covers the refs alone — the signature is not part of the imprint.
			expect(tsVerifyToken(tsa.dir, tsa.certPath, token, refs)).toMatch(/Verification: OK/);
		} finally {
			vi.unstubAllGlobals();
			rmSync(tsa.dir, { recursive: true, force: true });
		}
	});
	it('refuses X Long without a TSA (it builds on CAdES-T)', async () => {
		const { signer } = await loadSignerSession(fixtures.p12, FIXTURE_PIN);
		await expect(
			signCadesDetached(signer, fixtures.content, { signingTime: SIGNING_TIME, xLong: 1 })
		).rejects.toThrow(/builds on CAdES-T/);
	});
});

describe('golden vector (generated once by OpenSSL, test/fixtures/cades/)', () => {
	const goldenPath = join(GOLDEN_DIR, 'golden.csig');

	it('is accepted by openssl and shares the CAdES-BES attribute set with our output', async () => {
		const golden = new Uint8Array(readFileSync(goldenPath));
		const content = join(GOLDEN_DIR, 'content.txt');
		const caPath = join(GOLDEN_DIR, 'ca.pem');

		expect(verify(golden, content, caPath, ['-cades'])).toBeNull();

		const goldenAttributes = [...attributesByOid(golden).keys()];
		const expected = [OID_CONTENT_TYPE, OID_MESSAGE_DIGEST, OID_SIGNING_CERTIFICATE_V2, OID_SIGNING_TIME];
		for (const id of expected) expect(goldenAttributes).toContain(id);
		expect(oid(signedData(golden).children[2].children[0])).toBe(OID_DATA);
		expect(signedData(golden).children[2].children).toHaveLength(1);

		// Our own output carries the same four attributes, plus nothing else.
		const { signer } = await loadSignerSession(fixtures.p12, FIXTURE_PIN);
		const ours = await signCadesDetached(signer, new Uint8Array(readFileSync(content)), {
			signingTime: SIGNING_TIME
		});
		expect([...attributesByOid(ours).keys()].sort()).toEqual([...expected].sort());
	});

	it('rejects the golden vector when the document does not match', () => {
		const golden = new Uint8Array(readFileSync(goldenPath));
		const tampered = writeFixtureFile(fixtures.dir, 'tampered-golden.bin', new TextEncoder().encode('nope\n'));

		expect(verify(golden, tampered, join(GOLDEN_DIR, 'ca.pem'))).toMatch(/failure/i);
	});
});


describe('extension CAdES content and certificate chain', () => {
 it('embeds implicit content and the certificate chain, independently verified by OpenSSL', async () => {
  const session = await loadSignerSession(fixtures.p12, FIXTURE_PIN);
  const signature = await signCadesDetached(session.signer, fixtures.content, { attached: true, chain: session.certificates });
  expect(signedData(signature).children[2].children).toHaveLength(2);
  expect(signedData(signature).children[3].children).toHaveLength(session.certificates.length);
  const path = join(fixtures.dir, 'attached.csig');
  writeFileSync(path, signature);
  execFileSync('openssl', ['cms', '-verify', '-inform', 'DER', '-in', path, '-CAfile', fixtures.caPath, '-purpose', 'any', '-binary', '-cades', '-out', join(fixtures.dir, 'attached.bin')], { stdio: 'pipe' });
  expect(new Uint8Array(readFileSync(join(fixtures.dir, 'attached.bin')))).toEqual(fixtures.content);
 });
});
