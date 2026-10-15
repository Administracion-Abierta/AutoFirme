/**
 * CAdES-BES detached signatures (RFC 5652 CMS + RFC 5035 / ETSI EN 319 122), built by hand.
 *
 * The document digest and the RSA/ECDSA signature come from WebCrypto; the signed attributes,
 * the `SignerInfo` and the `SignedData` wrapper are DER-encoded here. Only what CAdES-BES with
 * SHA-256 needs is implemented: `id-data` content, detached (the document is never embedded,
 * only its digest travels), and the four signed attributes the reference implementation uses —
 * `contentType`, `messageDigest`, `signingTime` and `signingCertificateV2`. Setting `timeStamp`
 * adds the RFC 3161 `id-aa-timeStampToken` unsigned attribute, i.e. CAdES-T.
 */

import {
	type Bytes,
	derInteger,
	explicitTagged,
	implicitTagged,
	objectId,
	octetString,
	seq,
	setOf,
	tlv,
	utcTime
} from './asn1';
import { type SignerCert, signBytes } from './sign';
import { requestTimeStamp, timeStampAttributes } from './timestamp';
import { parseCertificate } from './x509';

const OID_SIGNED_DATA = '1.2.840.113549.1.7.2';
const OID_DATA = '1.2.840.113549.1.7.1';
const OID_CONTENT_TYPE = '1.2.840.113549.1.9.3';
const OID_MESSAGE_DIGEST = '1.2.840.113549.1.9.4';
const OID_SIGNING_TIME = '1.2.840.113549.1.9.5';
const OID_SIGNING_CERTIFICATE_V2 = '1.2.840.113549.1.9.16.2.47';
const OID_SHA256 = '2.16.840.1.101.3.4.2.1';
const OID_RSA_ENCRYPTION = '1.2.840.113549.1.1.1';
const OID_ECDSA_WITH_SHA256 = '1.2.840.10045.4.3.2';

/** DER NULL — the parameters RSA algorithm identifiers carry. */
const NULL_PARAMS = tlv(0x05, new Uint8Array(0));

const HASH = 'SHA-256';

export interface CadesSignOptions {
	/** Defaults to now; injectable so a signature can be reproduced in tests. */
	signingTime?: Date;
	/**
	 * RFC 3161 TSA URL. When set, the signature value is timestamped — the CAdES-BES becomes a
	 * CAdES-T. A same-origin proxy may be needed, since most TSAs send no CORS headers.
	 */
	timeStamp?: string;
}

async function sha256(data: Bytes): Promise<Bytes> {
	return new Uint8Array(await crypto.subtle.digest(HASH, data));
}

/** WebCrypto returns ECDSA as `r || s`; CMS wants a DER `SEQUENCE { r, s }`. */
function ecdsaSignature(raw: Bytes): Bytes {
	const half = raw.length / 2;
	return seq(derInteger(raw.subarray(0, half)), derInteger(raw.subarray(half)));
}

function attribute(id: string, ...values: Bytes[]): Bytes {
	return seq(objectId(id), setOf(...values));
}

/**
 * Detached CAdES-BES over `content`, as DER `ContentInfo`. Signing time is the only
 * non-deterministic input, so two calls with the same `options` produce the same bytes.
 */
export async function signCadesDetached(
	cert: SignerCert,
	content: Bytes,
	options: CadesSignOptions = {}
): Promise<Bytes> {
	if (!cert.key || !cert.algorithm) {
		throw new Error(`certificate ${cert.commonName} has no private key`);
	}
	if (cert.algorithm.hash !== HASH) {
		throw new Error(`CAdES-BES is SHA-256 here, but the key is bound to ${cert.algorithm.hash}`);
	}
	const certificate = parseCertificate(cert.der);
	const ecdsa = cert.algorithm.name === 'ECDSA';

	// ESSCertIDv2 locates the signer by certificate hash, and offers the issuer and serial as
	// a hint the verifier can use without hashing every certificate (RFC 5035 §4.2.1).
	const issuerSerial = seq(seq(explicitTagged(4, certificate.issuerDer)), certificate.serialDer);
	const essCertIdV2 = seq(octetString(await sha256(cert.der)), issuerSerial);
	// SigningCertificateV2 wraps its certificate ids in a `certs` SEQUENCE (RFC 5035 §4.2).
	const signingCertificateV2 = seq(seq(essCertIdV2));

	const signedAttrs = setOf(
		attribute(OID_CONTENT_TYPE, objectId(OID_DATA)),
		attribute(OID_MESSAGE_DIGEST, octetString(await sha256(content))),
		attribute(OID_SIGNING_TIME, utcTime(options.signingTime ?? new Date())),
		attribute(OID_SIGNING_CERTIFICATE_V2, signingCertificateV2)
	);

	// CMS signs the DER of the `SET OF`, not of the `[0] IMPLICIT` field that carries it.
	const raw = await signBytes(cert, signedAttrs);
	const signature = ecdsa ? ecdsaSignature(raw) : raw;
	// The TSA timestamps that signature value, never the document (ETSI EN 319 122-1 §5.4).
	const unsignedAttributes = options.timeStamp
		? timeStampAttributes(await requestTimeStamp(options.timeStamp, signature))
		: null;

	const signerInfos = setOf(
		seq(
			derInteger(1), // version: IssuerAndSerialNumber form
			seq(certificate.issuerDer, certificate.serialDer),
			seq(objectId(OID_SHA256)), // digestAlgorithm, NULL parameters omitted (RFC 5754 §2)
			implicitTagged(0, signedAttrs),
			ecdsa
				? seq(objectId(OID_ECDSA_WITH_SHA256))
				: seq(objectId(OID_RSA_ENCRYPTION), NULL_PARAMS),
			octetString(signature),
			// `unsignedAttrs [1] IMPLICIT SET OF Attribute`, after the signature (RFC 5652 §5.3).
			...(unsignedAttributes ? [implicitTagged(1, unsignedAttributes)] : [])
		)
	);

	const signedData = seq(
		derInteger(1), // version: id-data content plus IssuerAndSerialNumber signers
		setOf(seq(objectId(OID_SHA256))), // digestAlgorithms
		seq(objectId(OID_DATA)), // encapContentInfo — detached, so no eContent
		implicitTagged(0, setOf(cert.der)), // certificates: the signer only, detached chain is out of scope
		signerInfos
	);

	return seq(objectId(OID_SIGNED_DATA), explicitTagged(0, signedData));
}
