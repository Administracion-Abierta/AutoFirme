/**
 * Key-bag shapes: PKCS#12 bags hold PKCS#8 in theory, but Windows and older OpenSSL put a bare
 * `RSAPrivateKey` (PKCS#1) or `ECPrivateKey` (SEC1) in them, and those containers used to fail
 * with "the file could not be read". Each shape is checked against a real key from openssl, and
 * every normalized key must still import in WebCrypto.
 */

import { execFileSync } from 'node:child_process';
import { mkdtempSync, readFileSync, rmSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import type { Bytes } from './asn1';
import { parseCertificate } from './x509';
import { samePublicKey } from './x509';
import { readPrivateKey } from './keystore';

let dir: string;

function openssl(args: string[]): void {
	execFileSync('openssl', args, { cwd: dir, stdio: 'pipe' });
}

function bytes(name: string): Bytes {
	return new Uint8Array(readFileSync(join(dir, name)));
}

beforeAll(() => {
	dir = mkdtempSync(join(tmpdir(), 'autofirme-keys-'));
	openssl([
		'req', '-x509', '-newkey', 'rsa:2048', '-keyout', 'rsa.key', '-out', 'rsa.pem',
		'-days', '1', '-nodes', '-sha256', '-subj', '/C=ES/O=AutoFirme/CN=Forma de la clave'
	]);
	openssl(['rsa', '-in', 'rsa.key', '-traditional', '-outform', 'DER', '-out', 'rsa-pkcs1.der']);
	openssl(['ecparam', '-genkey', '-name', 'prime256v1', '-noout', '-out', 'ec.key']);
	openssl(['ec', '-in', 'ec.key', '-outform', 'DER', '-out', 'ec-sec1.der']);
}, 60_000);

afterAll(() => rmSync(dir, { recursive: true, force: true }));

describe('private key bags', () => {
	it('reads a bare PKCS#1 RSAPrivateKey and matches the certificate it belongs to', async () => {
		const info = readPrivateKey(bytes('rsa-pkcs1.der'));

		expect(info.algorithm).toEqual({ name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' });

		// The public half read out of the bare key is the one the certificate carries.
		openssl(['x509', '-in', 'rsa.pem', '-outform', 'DER', '-out', 'rsa.der']);
		expect(samePublicKey(parseCertificate(bytes('rsa.der')).publicKey, info.publicKey)).toBe(true);

		// And WebCrypto accepts it: the bare key is wrapped in PKCS#8 on the way through.
		const key = await crypto.subtle.importKey('pkcs8', info.der, info.algorithm, false, ['sign']);
		expect(key.algorithm.name).toBe('RSASSA-PKCS1-v1_5');
	});

	it('wraps a bare SEC1 ECPrivateKey in PKCS#8 so WebCrypto can import it', async () => {
		const info = readPrivateKey(bytes('ec-sec1.der'));

		expect(info.algorithm).toEqual({ name: 'ECDSA', hash: 'SHA-256', namedCurve: 'P-256' });

		const key = await crypto.subtle.importKey('pkcs8', info.der, info.algorithm, false, ['sign']);
		expect(key.algorithm.name).toBe('ECDSA');
		expect((key.algorithm as EcKeyAlgorithm).namedCurve).toBe('P-256');
	});

	it('still reads a PKCS#8 PrivateKeyInfo', async () => {
		// `openssl genpkey` writes PKCS#8, which is the shape the bags are supposed to hold.
		openssl(['genpkey', '-algorithm', 'RSA', '-pkeyopt', 'rsa_keygen_bits:2048', '-out', 'pkcs8.pem']);
		openssl(['pkcs8', '-topk8', '-nocrypt', '-in', 'pkcs8.pem', '-outform', 'DER', '-out', 'pkcs8.der']);

		const info = readPrivateKey(bytes('pkcs8.der'));
		expect(info.algorithm).toEqual({ name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' });
		expect(info.publicKey.kind).toBe('rsa');
	});
});
