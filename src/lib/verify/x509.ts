/** X.509 parsing, WebCrypto key import, and chain building to bundled trust anchors. */

import { TAG, at, children, hex, integer, octets, oid, parse, time, type Der } from './asn1';
import type { HashName } from './digest';
import type { Bytes } from './types';

export interface Certificate {
	raw: Bytes;
	tbs: Bytes;
	serial: string;
	subject: string;
	issuer: string;
	subjectDer: Bytes;
	issuerDer: Bytes;
	notBefore: string;
	notAfter: string;
	spki: Bytes;
	/** Named curve of the subject public key (ECDSA keys only). */
	keyCurve: NonNullable<SignatureAlgorithm['namedCurve']> | null;
	signatureAlgorithm: SignatureAlgorithm | null;
	signatureAlgorithmOid: string;
	signature: Bytes;
}

export interface SignatureAlgorithm {
	/** WebCrypto algorithm name; `null` when the browser cannot verify it. */
	name: 'RSASSA-PKCS1-v1_5' | 'ECDSA' | null;
	hash: HashName;
	namedCurve?: 'P-256' | 'P-384' | 'P-521';
}

const ALGORITHMS: Record<string, { name: SignatureAlgorithm['name']; hash: SignatureAlgorithm['hash'] }> = {
	'1.2.840.113549.1.1.5': { name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-1' },
	'1.2.840.113549.1.1.11': { name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' },
	'1.2.840.113549.1.1.12': { name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-384' },
	'1.2.840.113549.1.1.13': { name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-512' },
	'1.2.840.113549.1.1.10': { name: null, hash: 'SHA-256' }, // RSASSA-PSS: WebCrypto needs the params, unsupported here
	'1.2.840.10045.4.1': { name: 'ECDSA', hash: 'SHA-1' },
	'1.2.840.10045.4.3.2': { name: 'ECDSA', hash: 'SHA-256' },
	'1.2.840.10045.4.3.3': { name: 'ECDSA', hash: 'SHA-384' },
	'1.2.840.10045.4.3.4': { name: 'ECDSA', hash: 'SHA-512' }
};

const CURVES: Record<string, { namedCurve: NonNullable<SignatureAlgorithm['namedCurve']>; size: number }> = {
	'1.2.840.10045.3.1.7': { namedCurve: 'P-256', size: 32 },
	'1.3.132.0.34': { namedCurve: 'P-384', size: 48 },
	'1.3.132.0.35': { namedCurve: 'P-521', size: 66 }
};

const DN_NAMES: Record<string, string> = {
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

/** Reads the DER text-ish string types that appear in distinguished names. */
function dnValue(node: Der): string {
	if (node.tag === TAG.UTF8_STRING) return new TextDecoder().decode(node.content);
	if (node.tag === TAG.PRINTABLE_STRING || node.tag === TAG.IA5_STRING) {
		return String.fromCharCode(...node.content);
	}
	if (node.tag === TAG.BIT_STRING || node.tag === TAG.OCTET_STRING) return hex(octets(node));
	return hex(node.content);
}

/** RFC 4514 rendering: RDNs reversed (most specific first), multi-valued RDNs joined with `+`. */
export function distinguishedName(node: Der): string {
	return children(node)
		.map((rdn) =>
			children(rdn)
				.map((attribute) => {
					const key = oid(at(attribute, 0));
					const value = dnValue(at(attribute, 1));
					const escaped = value.replace(/([,+"\\<>;])/g, '\\$1');
					return `${DN_NAMES[key] ?? key}=${escaped}`;
				})
				.join('+')
		)
		.reverse()
		.join(', ');
}

/** XMLDSig names signature algorithms by URI, CMS by OID; both land on the same WebCrypto pair. */
const ALGORITHM_URIS: Record<string, { name: SignatureAlgorithm['name']; hash: SignatureAlgorithm['hash'] }> = {
	'http://www.w3.org/2000/09/xmldsig#rsa-sha1': { name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-1' },
	'http://www.w3.org/2001/04/xmldsig-more#rsa-sha256': { name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' },
	'http://www.w3.org/2001/04/xmldsig-more#rsa-sha384': { name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-384' },
	'http://www.w3.org/2001/04/xmldsig-more#rsa-sha512': { name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-512' },
	'http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256': { name: 'ECDSA', hash: 'SHA-256' },
	'http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384': { name: 'ECDSA', hash: 'SHA-384' },
	'http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512': { name: 'ECDSA', hash: 'SHA-512' }
};

/** Identifier → WebCrypto-verifiable algorithm, or `null` when the browser cannot verify it. */
export function algorithmForOid(algorithmOid: string): SignatureAlgorithm | null {
	const known = ALGORITHMS[algorithmOid];
	return known ? { name: known.name, hash: known.hash } : null;
}

export function algorithmForUri(uri: string): SignatureAlgorithm | null {
	const known = ALGORITHM_URIS[uri];
	return known ? { name: known.name, hash: known.hash } : null;
}

export function signatureAlgorithmOf(node: Der): { algorithm: SignatureAlgorithm | null; oid: string } {
	const algorithmOid = oid(at(node, 0));
	return { algorithm: algorithmForOid(algorithmOid), oid: algorithmOid };
}

/**
 * TBSCertificate ::= SEQUENCE { [0] version, serialNumber, signature, issuer,
 * validity, subject, subjectPublicKeyInfo, [1]/[2] unique ids, [3] extensions }
 */
export function parseCertificate(der: Bytes): Certificate {
	const certificate = parse(der).node;
	const tbs = at(certificate, 0);
	// [0] version is the only context tag before the fixed fields; [1]/[2]/[3] follow the SPKI.
	const fields = children(tbs).filter((node) => node.tag !== 0xa0);
	const [serialNode, signatureAlgorithmNode, issuerNode, validityNode, subjectNode, spkiNode] = fields;
	const { algorithm, oid: signatureAlgorithmOid } = signatureAlgorithmOf(signatureAlgorithmNode);
	// The subject key's curve lives in the SPKI algorithm parameters, not in the signature algorithm.
	const spkiParameters = children(at(spkiNode, 0))[1];
	const keyCurve =
		spkiParameters && spkiParameters.tag === TAG.OID ? (CURVES[oid(spkiParameters)]?.namedCurve ?? null) : null;
	return {
		raw: der,
		tbs: tbs.raw,
		serial: hex(serialNode.content),
		subject: distinguishedName(subjectNode),
		issuer: distinguishedName(issuerNode),
		subjectDer: subjectNode.raw,
		issuerDer: issuerNode.raw,
		notBefore: time(at(validityNode, 0)),
		notAfter: time(at(validityNode, 1)),
		spki: spkiNode.raw,
		keyCurve,
		signatureAlgorithm: algorithm,
		signatureAlgorithmOid,
		signature: octets(at(certificate, 2))
	};
}

function sameBytes(a: Bytes, b: Bytes): boolean {
	if (a.length !== b.length) return false;
	for (let i = 0; i < a.length; i++) if (a[i] !== b[i]) return false;
	return true;
}

/** ECDSA signatures travel as `SEQUENCE { r, s }`; WebCrypto wants fixed-width `r || s`. */
function ecdsaToRaw(signature: Bytes, size: number): Bytes {
	try {
		const [r, s] = children(parse(signature).node);
		const raw = new Uint8Array(size * 2);
		for (const [index, part] of [r, s].entries()) {
			const bytes = integer(part).toString(16).padStart(size * 2, '0');
			for (let i = 0; i < size; i++) raw[index * size + i] = parseInt(bytes.slice(i * 2, i * 2 + 2), 16);
		}
		return raw;
	} catch {
		return signature;
	}
}

export async function verifySignature(
	certificate: Certificate,
	algorithm: SignatureAlgorithm | null,
	data: Bytes,
	signature: Bytes
): Promise<boolean> {
	if (!algorithm || !algorithm.name) return false;
	const hash = algorithm.hash;
	if (algorithm.name === 'RSASSA-PKCS1-v1_5') {
		const key = await crypto.subtle.importKey(
			'spki',
			certificate.spki,
			{ name: 'RSASSA-PKCS1-v1_5', hash },
			false,
			['verify']
		);
		return crypto.subtle.verify('RSASSA-PKCS1-v1_5', key, signature, data);
	}
	const namedCurve = algorithm.namedCurve ?? certificate.keyCurve;
	if (!namedCurve) return false;
	const size = Object.values(CURVES).find((curve) => curve.namedCurve === namedCurve)?.size ?? 32;
	const key = await crypto.subtle.importKey('spki', certificate.spki, { name: 'ECDSA', namedCurve }, false, [
		'verify'
	]);
	return crypto.subtle.verify({ name: 'ECDSA', hash }, key, ecdsaToRaw(signature, size), data);
}

export interface ChainResult {
	chain: Certificate[];
	trusted: boolean;
	reason: string;
}

/**
 * Walks issuer links by comparing raw `Name` DER (byte equality is what CAs sign
 * over), verifying each link's signature, then matches the terminating self-signed
 * certificate against the trust anchors.
 */
export async function buildChain(
	signer: Certificate,
	pool: Certificate[],
	roots: Certificate[]
): Promise<ChainResult> {
	const chain: Certificate[] = [signer];
	const candidates = [...pool, ...roots];
	for (let depth = 0; depth < 8; depth++) {
		const current = chain[chain.length - 1];
		if (sameBytes(current.subjectDer, current.issuerDer)) {
			const trusted = roots.some((root) => sameBytes(root.raw, current.raw));
			return {
				chain,
				trusted,
				reason: trusted
					? `chain terminates in trust anchor ${current.subject}`
					: `self-signed root not in trust store: ${current.subject}`
			};
		}
		const issuer = candidates.find((candidate) => sameBytes(candidate.subjectDer, current.issuerDer));
		if (!issuer) {
			return { chain, trusted: false, reason: `issuer certificate not included: ${current.issuer}` };
		}
		const linkValid = await verifySignature(
			issuer,
			issuer.signatureAlgorithm,
			current.tbs,
			current.signature
		).catch(() => false);
		if (!linkValid) {
			return { chain, trusted: false, reason: `signature of ${current.subject} not verifiable by ${issuer.subject}` };
		}
		chain.push(issuer);
	}
	return { chain, trusted: false, reason: 'issuer chain longer than 8 certificates' };
}
