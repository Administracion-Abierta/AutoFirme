/**
 * CAdES-BES signatures (RFC 5652 CMS + RFC 5035 / ETSI EN 319 122), built by hand.
 *
 * The document digest and the RSA/ECDSA signature come from WebCrypto; the signed attributes,
 * the `SignerInfo` and the `SignedData` wrapper are DER-encoded here. Only what CAdES-BES with
 * SHA-256 needs is implemented: `id-data` content (detached by default), and the four signed attributes the reference implementation uses —
 * `contentType`, `messageDigest`, `signingTime` and `signingCertificateV2`. Setting `timeStamp`
 * adds the RFC 3161 `id-aa-timeStampToken` unsigned attribute, i.e. CAdES-T.
 */

import {
	type Bytes,
	concatBytes,
	derContent,
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
import { OID_TIME_STAMP_TOKEN, requestTimeStamp } from './timestamp';
import { certificateIdentity } from './x509';

const OID_SIGNED_DATA = '1.2.840.113549.1.7.2';
const OID_DATA = '1.2.840.113549.1.7.1';
const OID_CONTENT_TYPE = '1.2.840.113549.1.9.3';
const OID_MESSAGE_DIGEST = '1.2.840.113549.1.9.4';
const OID_SIGNING_TIME = '1.2.840.113549.1.9.5';
const OID_SIGNING_CERTIFICATE_V2 = '1.2.840.113549.1.9.16.2.47';
/** `id-aa-ets-certificateRefs` — CompleteCertificateRefs (RFC 5126 §6.2.1). */
const OID_COMPLETE_CERTIFICATE_REFS = '1.2.840.113549.1.9.16.2.21';
/** `id-aa-ets-revocationRefs` — CompleteRevocationRefs (RFC 5126 §6.2.2). */
const OID_COMPLETE_REVOCATION_REFS = '1.2.840.113549.1.9.16.2.22';
/** `id-aa-ets-escTimeStamp` — CAdES-C time-stamp (RFC 5126 §6.3.5); X Long type 1. */
const OID_ESC_TIME_STAMP = '1.2.840.113549.1.9.16.2.25';
/** `id-aa-ets-certCRLTimestamp` — time-stamped certs/CRLs refs (RFC 5126 §6.3.6); type 2. */
const OID_CERT_CRL_TIME_STAMP = '1.2.840.113549.1.9.16.2.26';
const OID_SHA256 = '2.16.840.1.101.3.4.2.1';
const OID_RSA_ENCRYPTION = '1.2.840.113549.1.1.1';
const OID_ECDSA_WITH_SHA256 = '1.2.840.10045.4.3.2';

/** DER NULL — the parameters RSA algorithm identifiers carry. */
const NULL_PARAMS = tlv(0x05, new Uint8Array(0));

const HASH = 'SHA-256';

export interface CadesSignOptions {
	/** Embed content for AutoScript implicit mode; detached remains the default. */
	attached?: boolean;
	/** Public certificates to include alongside the signer. */
	chain?: readonly SignerCert[];
	/** Defaults to now; injectable so a signature can be reproduced in tests. */
	signingTime?: Date;
	/**
	 * RFC 3161 TSA URL. When set, the signature value is timestamped — the CAdES-BES becomes a
	 * CAdES-T. A same-origin proxy may be needed, since most TSAs send no CORS headers.
	 */
	timeStamp?: string;
	/**
	 * CAdES-X Long type 1 or 2 (RFC 5126 §6). Builds on `timeStamp`: type 1 adds the certificate
	 * and revocation refs over every embedded certificate; type 2 additionally timestamps those
	 * refs (the CAdES-C timestamp). Revocation refs stay empty — no CRL/OCSP material is fetched
	 * (issue #23 owns XL/LTA).
	 */
	xLong?: 1 | 2;
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
 * `OtherCertID` (RFC 5126 §6.2.1): SHA-256 of the certificate plus its issuer/serial, the same
 * hint shape `ESSCertIDv2` uses. One entry per embedded certificate, signer included.
 */
async function otherCertId(der: Bytes): Promise<Bytes> {
	const identity = certificateIdentity(der);
	const issuerSerial = seq(seq(explicitTagged(4, identity.issuerDer)), identity.serialDer);
	return seq(seq(seq(objectId(OID_SHA256)), octetString(await sha256(der))), issuerSerial);
}

/**
 * The two refs values X Long type 1 adds (RFC 5126 §6.2). Revocation refs are an empty
 * `SEQUENCE` — no CRL/OCSP material is fetched here (issue #23 owns XL/LTA), and the empty form
 * keeps the structure a verifier can still walk.
 */
async function xLongRefs(certificates: readonly Bytes[]): Promise<{ certRefs: Bytes; revRefs: Bytes }> {
	return {
		certRefs: seq(...(await Promise.all(certificates.map(otherCertId)))),
		revRefs: seq()
	};
}

/**
 * CAdES-BES over `content`, detached by default, as DER `ContentInfo`. Signing time is the only
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
	const certificate = certificateIdentity(cert.der);
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
	if (options.xLong !== undefined && !options.timeStamp) {
		throw new Error('CAdES-X Long builds on CAdES-T: set timeStamp alongside xLong');
	}
	const unsigned: Bytes[] = [];
	if (options.timeStamp) {
		const token = await requestTimeStamp(options.timeStamp, signature);
		unsigned.push(attribute(OID_TIME_STAMP_TOKEN, token));
		if (options.xLong !== undefined) {
			const embedded = [cert, ...(options.chain ?? [])].filter((entry, index, all) =>
				all.findIndex((other) => other.der.length === entry.der.length && other.der.every((b, i) => b === entry.der[i])) === index
			).map((entry) => entry.der);
			const { certRefs, revRefs } = await xLongRefs(embedded);
			const certRefsAttr = attribute(OID_COMPLETE_CERTIFICATE_REFS, certRefs);
			const revRefsAttr = attribute(OID_COMPLETE_REVOCATION_REFS, revRefs);
			unsigned.push(certRefsAttr, revRefsAttr);
			// RFC 5126 §6.3.5/§6.3.6 NOTE 2 hashes each referenced attribute as `attrType` plus
			// `attrValues`, without the outer SEQUENCE — `derContent` strips exactly that header.
			const refs = concatBytes([derContent(certRefsAttr), derContent(revRefsAttr)]);
			unsigned.push(
				attribute(
					// Type 2 covers the refs alone; type 1 adds the signature and its CAdES-T token.
					options.xLong === 1 ? OID_ESC_TIME_STAMP : OID_CERT_CRL_TIME_STAMP,
					await requestTimeStamp(
						options.timeStamp,
						options.xLong === 1
							? concatBytes([signature, derContent(attribute(OID_TIME_STAMP_TOKEN, token)), refs])
							: refs
					)
				)
			);
		}
	}
	const unsignedAttributes = unsigned.length > 0 ? setOf(...unsigned) : null;

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
		seq(objectId(OID_DATA), ...(options.attached ? [explicitTagged(0, octetString(content))] : [])),
		implicitTagged(0, setOf(...[cert, ...(options.chain ?? [])].filter((entry, index, all) =>
			all.findIndex((other) => other.der.length === entry.der.length && other.der.every((b, i) => b === entry.der[i])) === index
		).map((entry) => entry.der))),
		signerInfos
	);

	return seq(objectId(OID_SIGNED_DATA), explicitTagged(0, signedData));
}
