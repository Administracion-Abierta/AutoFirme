/** X.509 certificate summary for the picker, plus the public-key material used to pair a
 * certificate with the private key inside a PKCS#12 container. No dependencies. */

import { type Bytes, type Der, integerBytes, oid, parseDer, string } from './asn1';

export type PublicKey =
	| { kind: 'rsa'; modulus: Bytes; exponent: Bytes }
	| { kind: 'ec'; point: Bytes }
	| { kind: 'other' };

export interface Certificate {
	der: Bytes;
	/** Subject common name, or the full subject when the DN has no CN. */
	commonName: string;
	subject: string;
	issuer: string;
	serial: string;
	/** Issuer `Name` and serial number as they appear in the certificate — the
	 * `IssuerAndSerialNumber` of a CMS `SignerInfo` and of `ESSCertIDv2`. */
	issuerDer: Bytes;
	serialDer: Bytes;
	notBefore: Date;
	notAfter: Date;
	publicKey: PublicKey;
	/** OID of the SubjectPublicKeyInfo algorithm. */
	spkiAlgorithm: string;
}

const OID_RSA = '1.2.840.113549.1.1.1';
const OID_EC_PUBLIC_KEY = '1.2.840.10045.2.1';

const NAME_OIDS: Record<string, string> = {
	'2.5.4.3': 'CN',
	'2.5.4.4': 'SN',
	'2.5.4.5': 'serialNumber',
	'2.5.4.6': 'C',
	'2.5.4.7': 'L',
	'2.5.4.8': 'ST',
	'2.5.4.9': 'street',
	'2.5.4.10': 'O',
	'2.5.4.11': 'OU',
	'2.5.4.17': 'postalCode',
	'2.5.4.42': 'GN',
	'2.5.4.97': 'organizationIdentifier',
	'1.2.840.113549.1.9.1': 'E'
};

const OID_CN = '2.5.4.3';

function namePairs(node: Der): Array<[string, string]> {
	const pairs: Array<[string, string]> = [];
	for (const set of node.children) {
		for (const attribute of set.children) {
			const value = attribute.children[1];
			// NumericString is legal in certificate names; decode it as ASCII for display only.
			pairs.push([oid(attribute.children[0]), value.tag === 0x12 ? new TextDecoder().decode(value.value) : string(value)]);
		}
	}
	return pairs;
}

/** RFC 4514-style, most specific first: `CN=…, O=…, C=ES`. */
function formatName(node: Der): string {
	return namePairs(node)
		.reverse()
		.map(([id, value]) => `${NAME_OIDS[id] ?? id}=${value}`)
		.join(', ');
}

/**
 * UTCTime/GeneralizedTime (RFC 5280 §4.1.2.5). Seconds and the trailing `Z` are optional in the
 * wild and Windows-issued certificates encode a local offset (`20250401000000+0200`) instead of
 * `Z`, so the offset is parsed rather than rejected: those certificates used to fail the whole
 * container with "unsupported certificate time".
 */
function parseTime(node: Der): Date {
	const text = new TextDecoder().decode(node.value);
	const match = /^(\d{2}|\d{4})(\d{2})(\d{2})(\d{2})(\d{2})(\d{2})?(?:([Zz])|([+-])(\d{2})(\d{2}))?$/.exec(
		text
	);
	if (!match) throw new Error(`unsupported certificate time "${text}"`);
	let year = Number(match[1]);
	if (match[1].length === 2) year += year < 50 ? 2000 : 1900;
	const utc = Date.UTC(
		year,
		Number(match[2]) - 1,
		Number(match[3]),
		Number(match[4]),
		Number(match[5]),
		Number(match[6] ?? 0)
	);
	if (!match[8]) return new Date(utc);
	// The stamp reads as local time: subtract the offset to get the instant it names.
	const offset = (Number(match[9]) * 60 + Number(match[10])) * 60_000;
	return new Date(match[8] === '+' ? utc - offset : utc + offset);
}

function parsePublicKey(spki: Der): { publicKey: PublicKey; algorithm: string } {
	const algorithm = oid(spki.children[0].children[0]);
	// BIT STRING: one unused-bits octet, then the key.
	const key = spki.children[1].value.subarray(1);
	if (algorithm === OID_RSA) {
		const rsa = parseDer(key);
		return {
			algorithm,
			publicKey: {
				kind: 'rsa',
				modulus: integerBytes(rsa.children[0]),
				exponent: integerBytes(rsa.children[1])
			}
		};
	}
	if (algorithm === OID_EC_PUBLIC_KEY) {
		return { algorithm, publicKey: { kind: 'ec', point: key } };
	}
	return { algorithm, publicKey: { kind: 'other' } };
}

export function parseCertificate(der: Bytes): Certificate {
	const certificate = parseDer(der);
	const tbs = certificate.children[0];
	let index = tbs.children[0].tag === 0xa0 ? 1 : 0; // [0] EXPLICIT version
	const serial = tbs.children[index++];
	index++; // signature AlgorithmIdentifier
	const issuer = tbs.children[index++];
	const validity = tbs.children[index++];
	const subject = tbs.children[index++];
	const spki = tbs.children[index];

	const { publicKey, algorithm } = parsePublicKey(spki);
	const pairs = namePairs(subject);
	const subjectName = formatName(subject);
	const cn = pairs.find(([id]) => id === OID_CN)?.[1];

	return {
		der,
		commonName: cn ?? subjectName,
		subject: subjectName,
		issuer: formatName(issuer),
		serial: [...integerBytes(serial)].map((byte) => byte.toString(16).padStart(2, '0')).join(''),
		issuerDer: issuer.raw,
		serialDer: serial.raw,
		notBefore: parseTime(validity.children[0]),
		notAfter: parseTime(validity.children[1]),
		publicKey,
		spkiAlgorithm: algorithm
	};
}

function sameBytes(a: Bytes, b: Bytes): boolean {
	return a.length === b.length && a.every((byte, i) => byte === b[i]);
}

/** True when both describe the same public key — how a certificate is paired with a private key. */
export function samePublicKey(a: PublicKey, b: PublicKey): boolean {
	if (a.kind === 'rsa' && b.kind === 'rsa') {
		return sameBytes(a.modulus, b.modulus) && sameBytes(a.exponent, b.exponent);
	}
	if (a.kind === 'ec' && b.kind === 'ec') return sameBytes(a.point, b.point);
	return false;
}

/** CMS needs the encoded name, never a decoded display name (which may use unusual string tags). */
export function certificateIdentity(der: Bytes): Pick<Certificate, 'issuerDer' | 'serialDer'> {
 const certificate = parseDer(der);
 const tbs = certificate.children[0];
 const offset = tbs.children[0].tag === 0xa0 ? 1 : 0;
 const serial = tbs.children[offset];
 const issuer = tbs.children[offset + 2];
 if (certificate.tag !== 0x30 || tbs.tag !== 0x30 || serial.tag !== 0x02 || issuer.tag !== 0x30) throw new Error('invalid certificate');
 return { issuerDer: issuer.raw, serialDer: serial.raw };
}
