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

const OID_SIGNED_DATA = '1.2.840.113549.1.7.2';
const OID_MESSAGE_DIGEST = '1.2.840.113549.1.9.4';
const OID_CONTENT_TYPE = '1.2.840.113549.1.9.3';
const OID_SIGNING_TIME = '1.2.840.113549.1.9.5';
/** Bare rsaEncryption carries no hash; the digest algorithm attribute supplies it. */
const OID_RSA_ENCRYPTION = '1.2.840.113549.1.1.1';

interface Attribute {
	type: string;
	values: Der[];
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
		signedAttributes: (signedAttributesNode ? children(signedAttributesNode) : []).map((attribute) => ({
			type: oid(at(attribute, 0)),
			values: children(at(attribute, 1))
		})),
		signedAttributesRaw: signedAttributesNode
			? wrap(TAG.SET, signedAttributesNode.content)
			: new Uint8Array(),
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

export async function verifyCades(bytes: Bytes, options: VerifyOptions = {}): Promise<VerificationReport> {
	const checks: Check[] = [];
	let data: SignedData;
	try {
		data = parseCms(bytes);
	} catch (error) {
		return {
			format: 'cades',
			ok: false,
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
		const within = now >= new Date(certificate.notBefore) && now <= new Date(certificate.notAfter);
		checks.push({
			id: 'certValidity',
			status: within ? 'pass' : 'fail',
			detail: within
				? `valid ${certificate.notBefore} → ${certificate.notAfter}`
				: `certificate outside its validity window (${certificate.notBefore} → ${certificate.notAfter})`
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

	return { format: 'cades', ok: checks.every((entry) => entry.status !== 'fail'), checks, signer, signingTime };
}
