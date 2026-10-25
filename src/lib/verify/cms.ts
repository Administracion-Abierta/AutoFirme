/**
 * CAdES (.csig) verification: CMS `SignedData` over a detached or embedded document.
 *
 * Checks: structure, signature over the DER re-encoded `signedAttrs`, `messageDigest`
 * against the content, signer certificate, validity window, chain to a trust anchor,
 * and `signingTime`.
 */

import { TAG, at, children, hex, integer, octets, oid, parse, time, wrap, type Der } from './asn1';
import {
	algorithmForOid,
	buildChain,
	parseCertificate,
	verifySignature,
	type Certificate,
	type SignatureAlgorithm
} from './x509';
import { DIGEST_OIDS, digestBytes } from './digest';
import { trustedRoots } from './roots';
import type { Bytes, Check, Signer, VerificationReport, VerifyOptions } from './types';
import { summarize } from './types';
import { verifyTimeStampImprint } from '../timestamp';

const OID_SIGNED_DATA = '1.2.840.113549.1.7.2';
const OID_MESSAGE_DIGEST = '1.2.840.113549.1.9.4';
const OID_CONTENT_TYPE = '1.2.840.113549.1.9.3';
const OID_SIGNING_TIME = '1.2.840.113549.1.9.5';
/** Bare rsaEncryption carries no hash; the digest algorithm attribute supplies it. */
const OID_RSA_ENCRYPTION = '1.2.840.113549.1.1.1';
/** `id-aa-timeStampToken`: CAdES-T over the signature, CAdES-C over the X Long refs. */
const OID_TIME_STAMP_TOKEN = '1.2.840.113549.1.9.16.2.14';
const OID_COMPLETE_CERTIFICATE_REFS = '1.2.840.113549.1.9.16.2.21';
const OID_COMPLETE_REVOCATION_REFS = '1.2.840.113549.1.9.16.2.22';
/** `id-aa-ets-escTimeStamp` — CAdES-C time-stamp over signature + refs (RFC 5126 §6.3.5). */
const OID_ESC_TIME_STAMP = '1.2.840.113549.1.9.16.2.25';
/** `id-aa-ets-certCRLTimestamp` — time-stamp over the refs alone (RFC 5126 §6.3.6). */
const OID_CERT_CRL_TIME_STAMP = '1.2.840.113549.1.9.16.2.26';

interface Attribute {
	type: string;
	values: Der[];
	/** `attrType` TLV plus `attrValues` SET TLV — the bytes RFC 5126 §6.3.5/§6.3.6 hash. */
	imprint: Bytes;
}

function parseAttribute(attribute: Der): Attribute {
	return {
		type: oid(at(attribute, 0)),
		values: children(at(attribute, 1)),
		imprint: concatRaw([at(attribute, 0).raw, at(attribute, 1).raw])
	};
}

/** `unsignedAttrs [1] IMPLICIT`, after the signature — absent on BES signatures. */
function parseUnsigned(signerFields: Der[]): Attribute[] {
	const node = signerFields.find((field) => field.tag === 0xa1);
	return node ? children(node).map(parseAttribute) : [];
}

interface SignedData {
	version: bigint;
	eContentType: string;
	eContent: Bytes | null;
	certificates: Certificate[];
	digestAlgorithmOid: string;
	signatureAlgorithmOid: string;
	signedAttributes: Attribute[];
	/** Re-encoded as a SET OF — the exact bytes the signature covers. */
	signedAttributesRaw: Bytes;
	/** `unsignedAttrs [1]`, in wire order — timestamps and X Long refs live here, never signed. */
	unsignedAttributes: Attribute[];
	signature: Bytes;
	issuerDer: Bytes | null;
	serial: string | null;
}

export function parseCms(der: Bytes): SignedData {
	const contentInfo = parse(der).node;
	if (oid(at(contentInfo, 0)) !== OID_SIGNED_DATA) throw new Error('not a CMS SignedData object');
	const signedData = at(at(contentInfo, 1), 0);
	const fields = children(signedData);
	// Fields in order: INTEGER version, SET digestAlgorithms, SEQUENCE encapContentInfo,
	// optional [0] certificates, optional [1] crls, SET signerInfos.
	const encapsulate = fields.find((node) => node.tag === TAG.SEQUENCE);
	if (!encapsulate) throw new Error('SignedData has no EncapsulatedContentInfo');
	const encapsulated = children(encapsulate);
	const eContentWrapper = encapsulated.find((node) => node.tag === 0xa0);
	const certificatesWrapper = fields.find((node) => node.tag === 0xa0);
	const signerInfo = at(fields[fields.length - 1], 0);
	const signerFields = children(signerInfo);
	const signedAttributesNode = signerFields.find((node) => node.tag === 0xa0);
	const attributesAt = signedAttributesNode ? signerFields.indexOf(signedAttributesNode) : 2;
	const sid = signerFields[1];
	const issuerAndSerial = sid.tag === TAG.SEQUENCE ? children(sid) : null;
	const certificates: Certificate[] = [];
	for (const certificate of certificatesWrapper ? children(certificatesWrapper) : []) {
		try {
			certificates.push(parseCertificate(certificate.raw));
		} catch {
			// Skip unreadable certificates; the chain check reports the consequence.
		}
	}
	return {
		version: integer(fields[0]),
		eContentType: oid(encapsulated[0]),
		eContent: eContentWrapper ? octets(at(eContentWrapper, 0)) : null,
		certificates,
		digestAlgorithmOid: oid(at(signerFields[2], 0)),
		signatureAlgorithmOid: oid(at(signerFields[attributesAt + 1], 0)),
		signedAttributes: (signedAttributesNode ? children(signedAttributesNode) : []).map(parseAttribute),
		signedAttributesRaw: signedAttributesNode
			? wrap(TAG.SET, signedAttributesNode.content)
			: new Uint8Array(),
		unsignedAttributes: parseUnsigned(signerFields),
		signature: octets(signerFields[attributesAt + 2]),
		issuerDer: issuerAndSerial ? issuerAndSerial[0].raw : null,
		serial: issuerAndSerial ? hex(issuerAndSerial[1].content) : null
	};
}

function sameBytes(a: Bytes, b: Bytes): boolean {
	if (a.length !== b.length) return false;
	for (let i = 0; i < a.length; i++) if (a[i] !== b[i]) return false;
	return true;
}

function concatRaw(parts: Bytes[]): Bytes {
	const out = new Uint8Array(parts.reduce((total, part) => total + part.length, 0));
	let offset = 0;
	for (const part of parts) {
		out.set(part, offset);
		offset += part.length;
	}
	return out;
}

/**
 * Every timestamps unsigned attribute is checked against the imprint RFC 5126 defines for its
 * own OID: `id-aa-timeStampToken` covers the signature value (CAdES-T); `id-aa-ets-escTimeStamp`
 * covers signature, CAdES-T token and refs (X Long type 1, §6.3.5); `id-aa-ets-certCRLTimestamp`
 * covers the refs alone (type 2, §6.3.6). Anywhere else it lives — under the CAdES-T OID, over a
 * different canonicalisation or another document — fails with its reason attached.
 */
async function verifyTimestamps(data: SignedData): Promise<Check[]> {
	const stamps = data.unsignedAttributes.filter((attribute) =>
		[OID_TIME_STAMP_TOKEN, OID_ESC_TIME_STAMP, OID_CERT_CRL_TIME_STAMP].includes(attribute.type)
	);
	if (stamps.length === 0) {
		return [{ id: 'timestamp', status: 'info', detail: 'no unsigned timestamp — CAdES-BES' }];
	}
	const certRefs = data.unsignedAttributes.find((attribute) => attribute.type === OID_COMPLETE_CERTIFICATE_REFS);
	const revRefs = data.unsignedAttributes.find((attribute) => attribute.type === OID_COMPLETE_REVOCATION_REFS);
	// Concatenated `attrType`+`attrValues` of both refs attributes (RFC 5126 §6.3.5/§6.3.6 NOTE 2).
	const refs = certRefs && revRefs ? concatRaw([certRefs.imprint, revRefs.imprint]) : null;
	const simple = data.unsignedAttributes.filter((attribute) => attribute.type === OID_TIME_STAMP_TOKEN);
	const imprints: { type: string; label: string; bytes: Bytes }[] = [
		{ type: OID_TIME_STAMP_TOKEN, label: 'the signature value', bytes: data.signature }
	];
	if (refs) imprints.push({ type: OID_CERT_CRL_TIME_STAMP, label: 'the certificate and revocation refs', bytes: refs });
	if (refs && simple.length > 0) {
		imprints.push({
			type: OID_ESC_TIME_STAMP,
			label: 'the signature value, its CAdES-T timestamp and the certificate and revocation refs',
			bytes: concatRaw([data.signature, simple[0].imprint, refs])
		});
	}
	const checks: Check[] = [];
	for (const stamp of stamps) {
		const raw = stamp.values[0]?.raw;
		if (!raw) {
			checks.push({ id: 'timestamp', status: 'fail', detail: 'timestamp attribute carries no token' });
			continue;
		}
		let matched: string | null = null;
		let reason = '';
		// A token may legitimately satisfy more than one OID's imprint, so try them all; only the
		// candidates valid for this attribute's own OID are accepted.
		for (const imprint of imprints.filter((candidate) => candidate.type === stamp.type)) {
			try {
				await verifyTimeStampImprint(raw, imprint.bytes);
				matched = imprint.label;
				break;
			} catch (error) {
				reason = (error as Error).message;
			}
		}
		checks.push({
			id: 'timestamp',
			status: matched ? 'pass' : 'fail',
			detail: matched
				? `timestamp token covers ${matched}`
				: `timestamp token covers neither the signature value nor the refs: ${reason}`
		});
	}
	return checks;
}

export async function verifyCades(bytes: Bytes, options: VerifyOptions = {}): Promise<VerificationReport> {
	const checks: Check[] = [];
	let data: SignedData;
	try {
		data = parseCms(bytes);
	} catch (error) {
		return {
			format: 'cades',
			ok: false,
			status: 'invalid',
			signer: null,
			signingTime: null,
			checks: [{ id: 'structure', status: 'fail', detail: `CMS structure unreadable: ${(error as Error).message}` }]
		};
	}

	const digestInfo = DIGEST_OIDS[data.digestAlgorithmOid];
	const certificate =
		data.certificates.find(
			(candidate) =>
				data.serial !== null &&
				data.issuerDer !== null &&
				candidate.serial.replace(/^0+/, '') === data.serial.replace(/^0+/, '') &&
				sameBytes(candidate.issuerDer, data.issuerDer)
		) ?? (data.certificates.length === 1 ? data.certificates[0] : null);

	checks.push({
		id: 'structure',
		status: 'pass',
		detail: `CMS SignedData v${data.version}, ${data.certificates.length} certificate(s), content ${
			data.eContent ? `${data.eContent.length} B embedded` : 'detached'
		}`
	});

	const content = data.eContent ?? options.detached ?? null;
	const messageDigest = data.signedAttributes.find((attribute) => attribute.type === OID_MESSAGE_DIGEST);
	if (!digestInfo) {
		checks.push({ id: 'contentDigest', status: 'fail', detail: `unsupported digest algorithm ${data.digestAlgorithmOid}` });
	} else if (!content) {
		checks.push({
			id: 'contentDigest',
			status: 'info',
			detail: 'detached signature — drop the original document to check the content digest'
		});
	} else if (!messageDigest) {
		checks.push({ id: 'contentDigest', status: 'fail', detail: 'signed attribute messageDigest is missing' });
	} else {
		const expected = messageDigest.values[0]?.content ?? new Uint8Array();
		const actual = await digestBytes(digestInfo.hash, content);
		const match = sameBytes(actual, expected);
		checks.push({
			id: 'contentDigest',
			status: match ? 'pass' : 'fail',
			detail: match
				? `${digestInfo.label} of the content matches the signed messageDigest (${hex(expected).slice(0, 16)}…)`
				: `messageDigest ${hex(expected).slice(0, 16)}… ≠ ${digestInfo.label} of the content ${hex(actual).slice(0, 16)}…`
		});
	}

	const contentType = data.signedAttributes.find((attribute) => attribute.type === OID_CONTENT_TYPE);
	const declaredContentType = contentType ? oid(contentType.values[0]) : null;
	if (declaredContentType && declaredContentType !== data.eContentType) {
		checks.push({
			id: 'signature',
			status: 'fail',
			detail: `contentType attribute ${declaredContentType} contradicts eContentType ${data.eContentType}`
		});
	} else if (!certificate) {
		checks.push({ id: 'signature', status: 'fail', detail: 'signer certificate not found in the SignedData certificate set' });
	} else {
		const declared = algorithmForOid(data.signatureAlgorithmOid);
		const effective: SignatureAlgorithm | null =
			declared && declared.name
				? { ...declared, hash: digestInfo?.hash ?? declared.hash }
				: data.signatureAlgorithmOid === OID_RSA_ENCRYPTION && digestInfo
					? { name: 'RSASSA-PKCS1-v1_5', hash: digestInfo.hash }
					: null;
		const valid = effective
			? await verifySignature(certificate, effective, data.signedAttributesRaw, data.signature).catch(() => false)
			: false;
		checks.push({
			id: 'signature',
			status: valid ? 'pass' : 'fail',
			detail: valid
				? `${effective?.name} / ${effective?.hash} over signedAttrs (${data.signedAttributes.length} attributes)`
				: `signature over signedAttrs does not verify with ${certificate.subject} (${
						effective ? `${effective.name} / ${effective.hash}` : `unsupported algorithm ${data.signatureAlgorithmOid}`
					})`
		});
	}

	let signer: Signer | null = null;
	/** Signer certificate past its `notAfter` — turns the report into `valid-but-expired` (#40). */
	let expired = false;
	if (certificate) {
		signer = {
			subject: certificate.subject,
			issuer: certificate.issuer,
			serial: certificate.serial,
			notBefore: certificate.notBefore,
			notAfter: certificate.notAfter,
			certificates: data.certificates.length
		};
		checks.push({ id: 'signerCert', status: 'pass', detail: `${certificate.subject} — serial ${certificate.serial}` });
		const now = options.now ?? new Date();
		expired = now > new Date(certificate.notAfter);
		const notYetValid = now < new Date(certificate.notBefore);
		// Expiry warns, it never fails: the signature is cryptographically valid and AutoFirma signs
		// and verifies it all the same (#40).
		checks.push({
			id: 'certValidity',
			status: expired || notYetValid ? 'warn' : 'pass',
			detail: expired
				? `the signer certificate expired ${certificate.notAfter} — the signature itself still verifies`
				: notYetValid
					? `the signer certificate is not valid before ${certificate.notBefore} — the signature itself still verifies`
					: `valid ${certificate.notBefore} → ${certificate.notAfter}`
		});
		const chain = await buildChain(certificate, data.certificates, trustedRoots(options.trustedRoots ?? undefined)).catch(
			(error: Error) => ({ chain: [certificate], trusted: false, reason: `chain check failed: ${error.message}` })
		);
		checks.push({
			id: 'certChain',
			status: chain.trusted ? 'pass' : 'fail',
			detail: `${chain.chain.length} certificate(s): ${chain.reason}`
		});
	}

	const signingTimeAttribute = data.signedAttributes.find((attribute) => attribute.type === OID_SIGNING_TIME);
	let signingTime: string | null = null;
	if (!signingTimeAttribute) {
		checks.push({ id: 'signingTime', status: 'info', detail: 'no signingTime signed attribute' });
	} else {
		try {
			signingTime = time(signingTimeAttribute.values[0]);
			checks.push({ id: 'signingTime', status: 'pass', detail: `${signingTime} (signed attribute)` });
		} catch (error) {
			checks.push({ id: 'signingTime', status: 'fail', detail: `signingTime unreadable: ${(error as Error).message}` });
		}
	}

	for (const check of await verifyTimestamps(data)) checks.push(check);

	return { format: 'cades', ...summarize(checks, expired), checks, signer, signingTime };
}
