/**
 * Certificate time parsing: openssl writes `Z` stamps, but Windows-issued certificates encode a
 * local offset (`20250101000000+0200`) instead. Those used to abort the whole container, so the
 * offset path is exercised through a real `parseCertificate` call on a synthetic certificate.
 */

import { describe, expect, it } from 'vitest';
import { type Bytes, concatBytes, derInteger, objectId, parseDer, seq, setOf, tlv } from './asn1';
import { parseCertificate } from './x509';

/** OID of a name attribute (CN) and of the RSA public-key algorithm. */
const OID_CN = '2.5.4.3';
const OID_RSA = '1.2.840.113549.1.1.1';

const utf8 = (text: string): Bytes => new Uint8Array(new TextEncoder().encode(text));

function name(value: string): Bytes {
	return seq(setOf(seq(objectId(OID_CN), tlv(0x0c, utf8(value)))));
}

/** `parseCertificate` only reads the TBSCertificate, so an empty signature value is enough. */
function certificate(notBefore: Bytes, notAfter: Bytes): Bytes {
	const algorithm = seq(objectId(OID_RSA), tlv(0x05, new Uint8Array()));
	const publicKey = tlv(0x03, concatBytes([new Uint8Array([0]), seq(derInteger(1), derInteger(65537))]));
	const tbs = seq(
		tlv(0xa0, derInteger(2)), // [0] EXPLICIT version
		derInteger(4097),
		algorithm,
		name('Emisor'),
		seq(notBefore, notAfter),
		name('Sujeto'),
		seq(algorithm, publicKey)
	);
	return seq(tbs, algorithm, tlv(0x03, new Uint8Array([0, 0])));
}

const generalizedTime = (text: string): Bytes => tlv(0x18, utf8(text));

describe('certificate times', () => {
	it('reads a UTC offset stamp as the instant it names', () => {
		const parsed = parseCertificate(
			certificate(generalizedTime('20250101000000+0200'), generalizedTime('20260101000000+0200'))
		);

		// 00:00 at UTC+2 is 22:00 the day before, in UTC.
		expect(parsed.notBefore.toISOString()).toBe('2024-12-31T22:00:00.000Z');
		expect(parsed.notAfter.toISOString()).toBe('2025-12-31T22:00:00.000Z');
	});

	it('reads a negative offset', () => {
		const parsed = parseCertificate(
			certificate(generalizedTime('20250101000000-0500'), generalizedTime('20260101000000-0500'))
		);

		expect(parsed.notBefore.toISOString()).toBe('2025-01-01T05:00:00.000Z');
	});

	it('reads the usual UTCTime with a trailing Z, with and without seconds', () => {
		const withSeconds = parseCertificate(
			certificate(tlv(0x17, utf8('250101000000Z')), tlv(0x17, utf8('260101000000Z')))
		);
		const withoutSeconds = parseCertificate(
			certificate(tlv(0x17, utf8('2501010000Z')), tlv(0x17, utf8('2601010000Z')))
		);

		expect(withSeconds.notBefore.toISOString()).toBe('2025-01-01T00:00:00.000Z');
		expect(withoutSeconds.notBefore.toISOString()).toBe('2025-01-01T00:00:00.000Z');
	});
});

it('extracts CMS issuer and serial without decoding legal NumericString names', async () => {
 const { certificateIdentity } = await import('./x509');
 const issuer = seq(setOf(seq(objectId(OID_CN), tlv(0x12, utf8('12345')))));
 const algorithm = seq(objectId(OID_RSA), tlv(0x05, new Uint8Array()));
 const der = seq(seq(tlv(0xa0, derInteger(2)), derInteger(4097), algorithm, issuer), algorithm);
 expect(certificateIdentity(der)).toEqual({ issuerDer: issuer, serialDer: derInteger(4097) });
});

it('shows legal NumericString names when importing a certificate', () => {
 const original = certificate(generalizedTime('20250101000000Z'), generalizedTime('20300101000000Z'));
 const parsed = parseDer(original);
 const children = parsed.children[0].children.map((node) => node.raw);
 children[3] = seq(setOf(seq(objectId(OID_CN), tlv(0x12, utf8('12345')))));
 const encoded = seq(seq(...children), ...parsed.children.slice(1).map((node) => node.raw));
 expect(parseCertificate(encoded).issuer).toBe('CN=12345');
});
