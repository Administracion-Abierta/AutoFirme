/**
 * OCSP/CRL parsing against DER produced by openssl (#136). Regenerate the fixtures with
 * `test/fixtures/revocation/generate.sh` (needs `openssl`); `test/fixtures/verify-fixtures.sh`
 * cross-checks them with `openssl ocsp -respin` / `openssl crl` in CI.
 *
 * The two forged/rogue fixtures are the point: a responder that the issuer did not sign
 * must never be able to turn a revoked certificate into `good`.
 */
import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { afterEach, describe, expect, it } from 'vitest';
import { checkRevocation, clearRevocationCache, parseCrl, parseOcspResponse } from '#lib/verify/revocation';
import { parseCertificate } from '#lib/verify/x509';

const dir = resolve(process.cwd(), 'test/fixtures/revocation') + '/';
const fixture = (name: string) => new Uint8Array(readFileSync(dir + name));
const SERIAL = 0x39ded1487d2ecc4366f66475fb53a6777fd788a5n;

const ca = fixture('ca.der');
const leaf = fixture('leaf.der');
const caCert = parseCertificate(ca);

afterEach(() => clearRevocationCache());

describe('OCSP response parsing', () => {
	it('reports the revoked serial with the responder time', async () => {
		const result = await parseOcspResponse(fixture('resp.der'), SERIAL, caCert);

		expect(result.status).toBe('revoked');
		expect(result.via).toBe('ocsp');
		expect(result.responderTime).toMatch(/^\d{4}-\d{2}-\d{2}T/);
	});

	// A `good` CertStatus is a primitive [0] IMPLICIT NULL (tag 0x80), not the constructed
	// context tag the revoked SEQUENCE uses — the two must not be conflated.
	it('reports a genuinely good serial as good', async () => {
		expect((await parseOcspResponse(fixture('good_ca.der'), SERIAL, caCert)).status).toBe('good');
	});

	it('rejects a rogue responder claiming good for the revoked serial', async () => {
		const result = await parseOcspResponse(fixture('forged_embed.der'), SERIAL, caCert);

		expect(result.status).toBe('unknown');
		expect(result.detail).toMatch(/not verifiable/);
	});

	it('never throws or says good on a tampered response', async () => {
		const tampered = fixture('resp.der');
		tampered[60] ^= 0xff;

		expect((await parseOcspResponse(tampered, SERIAL, caCert)).status).toBe('unknown');
	});

	it('says unknown for a serial the response does not cover', async () => {
		expect((await parseOcspResponse(fixture('resp.der'), 0x1234n, caCert)).status).toBe('unknown');
	});
});

describe('CRL parsing', () => {
	it('reports a listed serial as revoked', async () => {
		const result = await parseCrl(fixture('crl.der'), SERIAL, caCert);

		expect(result.status).toBe('revoked');
		expect(result.responderTime).toMatch(/^\d{4}-\d{2}-\d{2}T/);
	});

	it('reports an unlisted serial as good', async () => {
		expect((await parseCrl(fixture('crl.der'), 0x1234n, caCert)).status).toBe('good');
	});
});

describe('checkRevocation', () => {
	it('falls back to the CRL when every OCSP responder is unreachable', async () => {
		const result = await checkRevocation(leaf, ca, {
			transport: async (url: string) => {
				if (url.includes('ocsp')) throw new Error('ECONNREFUSED');
				return fixture('crl.der');
			}
		});

		expect(result.status).toBe('revoked');
		expect(result.via).toBe('crl');
	});

	it('reports unknown — never good — when every responder is unreachable', async () => {
		const result = await checkRevocation(leaf, ca, {
			transport: async () => {
				throw new Error('ECONNREFUSED');
			}
		});

		expect(result.status).toBe('unknown');
		expect(result.detail).toContain('offline');
	});

	it('caches a verdict instead of re-fetching it', async () => {
		let calls = 0;
		const transport = async (url: string) => {
			calls += 1;
			if (url.includes('ocsp')) return fixture('resp.der');
			throw new Error('unexpected CRL fetch');
		};

		// The fixture's producedAt is stamped when generate.sh last ran; the cached
		// verdict expires producedAt + 1 day (no nextUpdate). Pin the clock just
		// after producedAt so the cache window never lapses between regeneration
		// and test run — run 351 failed exactly 25h after the fixture was made.
		const probe = await parseOcspResponse(fixture('resp.der'), SERIAL, caCert);
		const now = new Date(Date.parse(probe.responderTime ?? '') + 60 * 60_000);

		const first = await checkRevocation(leaf, ca, { transport, now });
		const second = await checkRevocation(leaf, ca, { transport, now });

		expect(first.status).toBe('revoked');
		expect(second).toEqual(first);
		expect(calls).toBe(1);
	});
});
