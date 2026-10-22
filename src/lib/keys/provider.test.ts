import { describe, expect, it } from 'vitest';
import type { Bytes } from '../asn1';
import { signBytes, type SignerCert } from '../sign';
import type { KeyProvider } from './provider';

/**
 * The seam contract, pinned: the bytes crossing `signDigest` are the ones the backend signs.
 * The software backend feeds them to WebCrypto, which applies the bound hash itself, so the
 * provider — never the caller — hashes and what travels the seam is the document, not a digest.
 */

function testCert(overrides: Partial<SignerCert> = {}): SignerCert {
	return {
		id: 'test',
		commonName: 'Test',
		subject: 'CN=Test',
		issuer: 'CN=Test',
		serial: '01',
		notBefore: new Date(0),
		notAfter: new Date(0),
		expired: false,
		notYetValid: false,
		der: new Uint8Array(0),
		key: null,
		algorithm: null,
		...overrides
	};
}

const DOCUMENT = new TextEncoder().encode('AutoFirma Web seam contract test');

describe('KeyProvider seam', () => {
	it('hands the provider the document, not a precomputed digest', async () => {
		const seen: Bytes[] = [];
		const provider: KeyProvider = {
			metadata: { kind: 'software', id: 'spy', label: 'Spy' },
			list: async () => [],
			getCert: async () => null,
			signDigest: async (_ref, data) => {
				seen.push(data);
				return new Uint8Array([1]);
			}
		};

		expect(await signBytes(testCert({ provider }), DOCUMENT)).toEqual(new Uint8Array([1]));
		expect(seen).toHaveLength(1);
		expect(seen[0]).toEqual(DOCUMENT);
		// The reading the old doc claimed: if a pre-hash crossed the seam, this would match instead.
		expect(seen[0]).not.toEqual(new Uint8Array(await crypto.subtle.digest('SHA-256', DOCUMENT)));
	});

	it('hashes inside the default software backend: its signature opens over the document', async () => {
		const pair = await crypto.subtle.generateKey(
			{ name: 'RSASSA-PKCS1-v1_5', modulusLength: 2048, publicExponent: new Uint8Array([1, 0, 1]), hash: 'SHA-256' },
			true,
			['sign', 'verify']
		);
		const cert = testCert({
			key: pair.privateKey,
			algorithm: { name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' }
		});

		const signature = await signBytes(cert, DOCUMENT);

		expect(await crypto.subtle.verify('RSASSA-PKCS1-v1_5', pair.publicKey, signature, DOCUMENT)).toBe(true);
		const digest = new Uint8Array(await crypto.subtle.digest('SHA-256', DOCUMENT));
		expect(await crypto.subtle.verify('RSASSA-PKCS1-v1_5', pair.publicKey, signature, digest)).toBe(false);
	});

	it('keeps carrying the document whatever hash the caller picks', async () => {
		const seen: Bytes[] = [];
		const provider: KeyProvider = {
			metadata: { kind: 'software', id: 'spy', label: 'Spy' },
			list: async () => [],
			getCert: async () => null,
			signDigest: async (_ref, data) => {
				seen.push(data);
				return new Uint8Array(0);
			}
		};

		await signBytes(testCert({ provider }), DOCUMENT, 'SHA-512');

		expect(seen[0]).toEqual(DOCUMENT);
	});
});
