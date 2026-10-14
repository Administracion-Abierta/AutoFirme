import { execFileSync } from 'node:child_process';
import { readFileSync, rmSync } from 'node:fs';
import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { loadSignerSession } from './keystore';
import { Pkcs12Error } from './pkcs12';
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

	it('explains that legacy PBES1 containers cannot be decrypted', async () => {
		const error = await loadSignerSession(fixtures.legacy, FIXTURE_PIN).catch((reason) => reason);
		expect(error).toBeInstanceOf(Pkcs12Error);
		expect(error.code).toBe('unsupported-pbe');
		expect(error.message).toContain('Re-export');
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
});
