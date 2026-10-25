/**
 * XAdES (.xsig) verification: XMLDSig core (C14N + digests + SignatureValue) plus the
 * XAdES `SigningTime` property. Transform support: `enveloped-signature`, inclusive
 * C14N 1.0/1.1 and exclusive C14N; comments are not part of the verified form.
 */

import { canonicalize, withoutSignature, type C14nOptions } from './c14n';
import { DIGEST_URIS, digestBytes, type DigestAlgorithm } from './digest';
import { fromBase64, fromPem, hex } from './asn1';
import {
	algorithmForUri,
	buildChain,
	parseCertificate,
	verifySignature,
	type Certificate,
	type SignatureAlgorithm
} from './x509';
import { trustedRoots } from './roots';
import type { Bytes, Check, Signer, VerificationReport, VerifyOptions } from './types';
import { verifyTimeStampImprint } from '../timestamp';
import { summarize } from './types';

const DS = 'http://www.w3.org/2000/09/xmldsig#';
const XADES_PREFIX = 'http://uri.etsi.org/01903';
const TRANSFORM_ENVELOPED = `${DS}enveloped-signature`;
const C14N_URIS: Record<string, C14nOptions | 'unsupported'> = {
	'http://www.w3.org/TR/2001/REC-xml-c14n-20010315': {},
	'http://www.w3.org/2006/12/xml-c14n11': {},
	'http://www.w3.org/2001/10/xml-exc-c14n#': { exclusive: true },
	'http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments': 'unsupported',
	'http://www.w3.org/2001/10/xml-exc-c14n#WithComments': 'unsupported',
	'http://www.w3.org/2006/12/xml-c14n11#WithComments': 'unsupported'
};

function failed(format: VerificationReport['format'], id: Check['id'], detail: string): VerificationReport {
	return { format, ok: false, status: 'invalid', signer: null, signingTime: null, checks: [{ id, status: 'fail', detail }] };
}

function childByLocalName(parent: Element, namespace: string, localName: string): Element | null {
	for (const child of Array.from(parent.children)) {
		if (child.localName === localName && child.namespaceURI === namespace) return child;
	}
	return null;
}

/** Same-document reference resolution: `""` → whole document, `#id` → the element carrying that ID. */
function resolveReference(document: Document, uri: string): Node | null {
	if (uri === '' || uri === '#xpointer(/)') return document;
	if (!uri.startsWith('#')) return null;
	const id = decodeURIComponent(uri.slice(1)).replace(/^xpointer\(id\(['"]?/, '').replace(/['"]?\)$/, '');
	for (const element of Array.from(document.getElementsByTagName('*'))) {
		if (
			element.getAttribute('Id') === id ||
			element.getAttribute('ID') === id ||
			element.getAttribute('id') === id
		) {
			return element;
		}
	}
	return null;
}

/**
 * Realm-safe node test: DOM nodes expose `nodeType`, byte buffers never do — and
 * `instanceof Uint8Array` is false for a Node realm's buffer when the page has its own.
 */
function isNode(value: Node | Bytes): value is Node {
	return typeof (value as Node).nodeType === 'number';
}

/** Applies the `Transforms` chain of one `Reference`; returns the bytes the digest covers. */
function transformChain(
	node: Node,
	transforms: { algorithm: string; prefixes: string[] }[],
	signature: Element
): Bytes {
	let current: Node | Bytes = node;
	if (transforms.length === 0) {
		// A node-set argument is canonicalized before hashing (XMLDSig §4.4.3).
		return new TextEncoder().encode(canonicalize(node));
	}
	for (const transform of transforms) {
		if (transform.algorithm === TRANSFORM_ENVELOPED) {
			if (!isNode(current)) throw new Error('enveloped-signature transform after canonicalization');
			const owner = current.nodeType === 9 ? (current as Document) : current.ownerDocument;
			if (!owner) throw new Error('enveloped-signature transform without a document');
			current = withoutSignature(owner, signature);
			continue;
		}
		const options = C14N_URIS[transform.algorithm];
		if (options === undefined) throw new Error(`unsupported transform ${transform.algorithm}`);
		if (options === 'unsupported') throw new Error(`unsupported transform (comments) ${transform.algorithm}`);
		if (!isNode(current)) throw new Error('canonicalization applied to an octet stream');
		current = new TextEncoder().encode(canonicalize(current, { ...options, inclusivePrefixes: transform.prefixes }));
	}
	return isNode(current) ? new TextEncoder().encode(canonicalize(current)) : current;
}

function parseTransforms(reference: Element): { algorithm: string; prefixes: string[] }[] {
	const transforms = childByLocalName(reference, DS, 'Transforms');
	if (!transforms) return [];
	return Array.from(transforms.children)
		.filter((transform) => transform.localName === 'Transform')
		.map((transform) => {
			const inclusive = childByLocalName(transform, DS, 'InclusiveNamespaces');
			return {
				algorithm: transform.getAttribute('Algorithm') ?? '',
				prefixes: (inclusive?.getAttribute('PrefixList') ?? '').split(/\s+/).filter(Boolean)
			};
		});
}

function certificatesFromKeyInfo(signature: Element): Certificate[] {
	const certificates: Certificate[] = [];
	for (const element of Array.from(signature.getElementsByTagName('*'))) {
		if (element.localName !== 'X509Certificate') continue;
		const pem = `-----BEGIN CERTIFICATE-----\n${element.textContent ?? ''}\n-----END CERTIFICATE-----`;
		for (const der of fromPem(pem)) {
			try {
				certificates.push(parseCertificate(der));
			} catch {
				// Ignore a certificate the parser rejects; the chain check reports the consequence.
			}
		}
	}
	return certificates;
}

function sameBytes(a: Bytes, b: Bytes): boolean {
	if (a.length !== b.length) return false;
	for (let i = 0; i < a.length; i++) if (a[i] !== b[i]) return false;
	return true;
}

export async function verifyXades(bytes: Bytes, options: VerifyOptions = {}): Promise<VerificationReport> {
	let document: Document;
	try {
		document = new DOMParser().parseFromString(new TextDecoder().decode(bytes), 'application/xml');
		if (document.getElementsByTagName('parsererror').length > 0) throw new Error('malformed XML');
	} catch (error) {
		return failed('xades', 'structure', `XML unreadable: ${(error as Error).message}`);
	}

	const signatures = Array.from(document.getElementsByTagNameNS(DS, 'Signature'));
	const signature = signatures[0];
	if (!signature) return failed('xades', 'structure', 'no ds:Signature element found');
	const signedInfo = childByLocalName(signature, DS, 'SignedInfo');
	if (!signedInfo) return failed('xades', 'structure', 'ds:Signature has no ds:SignedInfo');

	const references = Array.from(signedInfo.children).filter((child) => child.localName === 'Reference');
	const checks: Check[] = [
		{
			id: 'structure',
			status: 'pass',
			detail: `XMLDSig signature, ${signatures.length} ds:Signature element(s), ${references.length} ds:Reference(s)`
		}
	];

	const certificates = certificatesFromKeyInfo(signature);
	let certificate: Certificate | null = certificates[0] ?? null;
	let signingTime: string | null = null;

	const canonicalizationUri = childByLocalName(signedInfo, DS, 'CanonicalizationMethod')?.getAttribute('Algorithm') ?? '';
	const canonicalization = C14N_URIS[canonicalizationUri];
	const signatureMethodUri = childByLocalName(signedInfo, DS, 'SignatureMethod')?.getAttribute('Algorithm') ?? '';
	const signatureValueElement = childByLocalName(signature, DS, 'SignatureValue');
	if (!signatureValueElement) {
		checks.push({ id: 'signature', status: 'fail', detail: 'ds:SignatureValue is missing' });
	} else if (canonicalization === undefined || canonicalization === 'unsupported') {
		checks.push({ id: 'signature', status: 'fail', detail: `unsupported CanonicalizationMethod ${canonicalizationUri}` });
	} else if (certificates.length === 0) {
		checks.push({ id: 'signature', status: 'fail', detail: 'no X509Certificate in ds:KeyInfo — cannot verify' });
	} else {
		const declared = algorithmForUri(signatureMethodUri);
		const canonicalSignedInfo = new TextEncoder().encode(canonicalize(signedInfo, canonicalization));
		const signatureValue = fromBase64(signatureValueElement.textContent ?? '');
		// KeyInfo may carry a whole chain: the signer is whichever certificate verifies.
		let verified: { certificate: Certificate; algorithm: SignatureAlgorithm } | null = null;
		for (const candidate of certificates) {
			const algorithm: SignatureAlgorithm | null = declared
				? { ...declared, namedCurve: declared.namedCurve ?? candidate.keyCurve ?? undefined }
				: null;
			if (!algorithm) break;
			if (await verifySignature(candidate, algorithm, canonicalSignedInfo, signatureValue).catch(() => false)) {
				verified = { certificate: candidate, algorithm };
				break;
			}
		}
		if (verified) certificate = verified.certificate;
		checks.push({
			id: 'signature',
			status: verified ? 'pass' : 'fail',
			detail: verified
				? `${verified.algorithm.name} / ${verified.algorithm.hash} over c14n(ds:SignedInfo) with ${verified.certificate.subject}`
				: `SignatureValue does not verify over c14n(ds:SignedInfo) against ${certificates.length} certificate(s) (${
						declared ? `${declared.hash}` : `unsupported SignatureMethod ${signatureMethodUri}`
					})`
		});
	}

	for (const reference of references) {
		const uri = reference.getAttribute('URI') ?? '';
		const type = reference.getAttribute('Type') ?? '';
		const digestMethodUri = childByLocalName(reference, DS, 'DigestMethod')?.getAttribute('Algorithm') ?? '';
		const digest: DigestAlgorithm | undefined = DIGEST_URIS[digestMethodUri];
		const expected = fromBase64(childByLocalName(reference, DS, 'DigestValue')?.textContent ?? '');
		const label = `${uri === '' ? '(whole document)' : uri}${type ? ` [${type.split(/[#/]/).pop()}]` : ''}`;
		const target = resolveReference(document, uri);
		if (!target) {
			checks.push({ id: 'reference', status: 'fail', detail: `${label}: reference cannot be resolved` });
			continue;
		}
		if (!digest) {
			checks.push({ id: 'reference', status: 'fail', detail: `${label}: unsupported DigestMethod ${digestMethodUri}` });
			continue;
		}
		try {
			const actual = await digestBytes(digest.hash, transformChain(target, parseTransforms(reference), signature));
			const match = sameBytes(actual, expected);
			checks.push({
				id: 'reference',
				status: match ? 'pass' : 'fail',
				detail: match
					? `${label}: ${digest.label} digest matches (${hex(expected).slice(0, 16)}…)`
					: `${label}: DigestValue ${hex(expected).slice(0, 16)}… ≠ ${digest.label} of the transformed reference ${hex(actual).slice(0, 16)}…`
			});
		} catch (error) {
			checks.push({ id: 'reference', status: 'fail', detail: `${label}: ${(error as Error).message}` });
		}
	}

	const signatureTimeStamp = Array.from(document.getElementsByTagName('*')).find(
		(element) => element.localName === 'SignatureTimeStamp' && (element.namespaceURI ?? '').startsWith(XADES_PREFIX)
	);
	if (!signatureTimeStamp) {
		checks.push({ id: 'timestamp', status: 'info', detail: 'no xades:SignatureTimeStamp — XAdES-BES/EPES' });
	} else if (!signatureValueElement) {
		checks.push({ id: 'timestamp', status: 'fail', detail: 'xades:SignatureTimeStamp present but ds:SignatureValue is missing' });
	} else {
		const stampC14nUri =
			childByLocalName(signatureTimeStamp, DS, 'CanonicalizationMethod')?.getAttribute('Algorithm') ?? '';
		const stampC14n = C14N_URIS[stampC14nUri];
		const encapsulated = Array.from(signatureTimeStamp.getElementsByTagName('*')).find(
			(element) => element.localName === 'EncapsulatedTimeStamp'
		);
		if (stampC14n === undefined || stampC14n === 'unsupported') {
			checks.push({
				id: 'timestamp',
				status: 'fail',
				detail: `unsupported SignatureTimeStamp CanonicalizationMethod ${stampC14nUri}`
			});
		} else if (!encapsulated?.textContent?.trim()) {
			checks.push({ id: 'timestamp', status: 'fail', detail: 'xades:SignatureTimeStamp carries no EncapsulatedTimeStamp' });
		} else {
			try {
				const token = fromBase64(encapsulated.textContent);
				const stamped = new TextEncoder().encode(canonicalize(signatureValueElement, stampC14n));
				await verifyTimeStampImprint(token, stamped);
				checks.push({
					id: 'timestamp',
					status: 'pass',
					detail: `SignatureTimeStamp covers the canonicalised ds:SignatureValue (${stampC14nUri})`
				});
			} catch (error) {
				checks.push({ id: 'timestamp', status: 'fail', detail: `SignatureTimeStamp rejected: ${(error as Error).message}` });
			}
		}
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
			certificates: certificates.length
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
		const chain = await buildChain(certificate, certificates, trustedRoots(options.trustedRoots ?? undefined)).catch(
			(error: Error) => ({ chain: [certificate], trusted: false, reason: `chain check failed: ${error.message}` })
		);
		checks.push({
			id: 'certChain',
			status: chain.trusted ? 'pass' : 'fail',
			detail: `${chain.chain.length} certificate(s): ${chain.reason}`
		});
	}

	const signingTimeElement = Array.from(document.getElementsByTagName('*')).find(
		(element) => element.localName === 'SigningTime' && (element.namespaceURI ?? '').startsWith(XADES_PREFIX)
	);
	if (signingTimeElement?.textContent?.trim()) {
		signingTime = signingTimeElement.textContent.trim();
		checks.push({ id: 'signingTime', status: 'pass', detail: `${signingTime} (xades:SigningTime)` });
	} else {
		checks.push({ id: 'signingTime', status: 'info', detail: 'no xades:SigningTime property' });
	}

	return { format: 'xades', ...summarize(checks, expired), checks, signer, signingTime };
}
