/**
 * CAdES-BES cross-verification: every signature this module produces is fed back through
 * `openssl cms -verify`, which is the acceptance oracle (#6), and its signed attributes are
 * compared with a known-good vector generated once by OpenSSL itself (test/fixtures/cades/).
 */

import { execFileSync } from 'node:child_process';
import { createHash } from 'node:crypto';
import { readFileSync, rmSync, writeFileSync } from 'node:fs';
import { join, resolve } from 'node:path';
import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { type Bytes, type Der, explicit, oid, parseDer } from './asn1';
import { signCadesDetached } from './cms';
import { loadSignerSession } from './keystore';
import { createCadesFixtures, FIXTURE_PIN, type CadesFixtures, writeFixtureFile } from '../test/fixtures';

const OID_DATA = '1.2.840.113549.1.7.1';
const OID_CONTENT_TYPE = '1.2.840.113549.1.9.3';
const OID_MESSAGE_DIGEST = '1.2.840.113549.1.9.4';
const OID_SIGNING_TIME = '1.2.840.113549.1.9.5';
const OID_SIGNING_CERTIFICATE_V2 = '1.2.840.113549.1.9.16.2.47';

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

	it('refuses to sign with a certificate that has no private key', async () => {
		const session = await loadSignerSession(fixtures.p12, FIXTURE_PIN);
		const withoutKey = session.certificates.find((certificate) => certificate.key === null)!;

		await expect(signCadesDetached(withoutKey, fixtures.content)).rejects.toThrow('no private key');
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
