/**
 * One signature-level vocabulary across CAdES, XAdES and PAdES (Refs #139).
 *
 * EU DSS names the same ladder per family (`CAdES-X` on the #125 fixtures,
 * `XAdES-BASELINE-T` on `xades-t.xsig`); the UI, the public API and MCP
 * `autofirma_verify` all read the single `level` field instead of family labels.
 *
 * Levels come from evidence actually present, never from inference:
 * - `T` needs a timestamp over the signature value that *verified* (CAdES-T token,
 *   XAdES `SignatureTimeStamp` over `ds:SignatureValue`).
 * - `XL` needs the X-level validation data *and* a timestamp attesting it: complete
 *   certificate + revocation refs covered by an X-level timestamp (CAdES), the
 *   `Complete*Refs` elements alongside a `SigAndRefsTimeStamp`/`RefsOnlyTimeStamp`
 *   (XAdES), a PDF document security store (`/DSS`) under a verified signature or
 *   document timestamp. Validation data present with no attestation is `CAdES-C`, not
 *   `XL` — the #125 `cades-xlong2-oldoid.p7s` control is the proof.
 * - `LTA` needs archive evidence on top of a verified base: the CAdES archive-timestamp
 *   attribute, the XAdES `ArchiveTimeStamp` element, a PAdES `DocTimeStamp`. A lone
 *   signature timestamp never implies LTA.
 *
 * `ponytail:` the XAdES refs/archive elements and the PAdES DSS/DocTimeStamp markers are read
 * from real structure (parsed infoset, PDF object bodies), but their tokens are presence-checked,
 * not re-validated — no reader for them exists here yet; #142 (P4.8) owns that when it mints X/LTA.
 */

import { parseCms } from './cms';
import { extractPadesSignatures } from './pades';
import type { ArtifactFormat, Bytes, Check } from './types';

/** `B` = no timestamp; `unknown` = the artifact failed structural parsing. */
export type SignatureLevel = 'B' | 'T' | 'XL' | 'LTA' | 'unknown';

/** Decision-table input: every flag is observed evidence, never a guess. */
export interface LevelEvidence {
	structureOk: boolean;
	/** A timestamp over the signature value verified (messageImprint match). */
	signatureTimestampVerified: boolean;
	/** X-level validation data present *and* attested by a timestamp. */
	xLevelAttested: boolean;
	/** Archive evidence present (CAdES attribute, XAdES element, PDF DocTimeStamp). */
	archivePresent: boolean;
}

export function levelFromEvidence(evidence: LevelEvidence): SignatureLevel {
	if (!evidence.structureOk) return 'unknown';
	const timed = evidence.signatureTimestampVerified || evidence.xLevelAttested;
	if (evidence.archivePresent && timed) return 'LTA';
	if (evidence.xLevelAttested) return 'XL';
	if (evidence.signatureTimestampVerified) return 'T';
	return 'B';
}

/** `id-aa-ets-archiveTimestamp` (RFC 5126 §6.4) — the CAdES-A/LTA attribute. */
const OID_ARCHIVE_TIMESTAMP = '1.2.840.113549.1.9.16.2.48';
const OID_COMPLETE_CERTIFICATE_REFS = '1.2.840.113549.1.9.16.2.21';
const OID_COMPLETE_REVOCATION_REFS = '1.2.840.113549.1.9.16.2.22';

function structureOk(checks: Check[]): boolean {
	return checks.some((check) => check.id === 'structure' && check.status === 'pass');
}

/** The passing `timestamp` checks, split by which imprint their detail names. */
function verifiedTimestamps(checks: Check[]): { signature: boolean; refs: boolean } {
	let signature = false;
	let refs = false;
	for (const check of checks) {
		if (check.id !== 'timestamp' || check.status !== 'pass') continue;
		// CAdES "covers the signature value" / XAdES "covers the canonicalised ds:SignatureValue";
		// the X-level forms name "the certificate and revocation refs" (RFC 5126 §6.3.5/§6.3.6).
		if (/signature\s?value/i.test(check.detail)) signature = true;
		if (/refs/i.test(check.detail)) refs = true;
	}
	return { signature, refs };
}

/** The unsigned attributes of one CMS, or null when it is not CMS `SignedData` at all. */
function unsignedOids(cms: Bytes): string[] | null {
	try {
		return parseCms(cms).unsignedAttributes.map((attribute) => attribute.type);
	} catch {
		return null;
	}
}

function cadesEvidence(cms: Bytes, checks: Check[]): LevelEvidence {
	const { signature, refs } = verifiedTimestamps(checks);
	const unsigned = unsignedOids(cms) ?? [];
	// Both refs attributes are required: one alone is an incomplete X Long (RFC 5126 §6.2.1/§6.2.2).
	const refsPresent =
		unsigned.includes(OID_COMPLETE_CERTIFICATE_REFS) && unsigned.includes(OID_COMPLETE_REVOCATION_REFS);
	return {
		structureOk: structureOk(checks),
		signatureTimestampVerified: signature,
		xLevelAttested: refsPresent && refs,
		archivePresent: unsigned.includes(OID_ARCHIVE_TIMESTAMP)
	};
}

/**
 * The XAdES namespaces (XAdES 1.1.1-1.4.1 carry the version in the URI) every long-form
 * element below must bind to *exactly*. A prefix test would also accept anyone's
 * `http://uri.etsi.org/01903evil`, which is not XAdES at all.
 */
const XADES_NS = /^http:\/\/uri\.etsi\.org\/01903(?:\/v\d+(?:\.\d+)*)?#?$/;

/**
 * The XAdES element names actually parsed out of the document. Reading the text instead
 * (regex over the raw bytes) also matches markup quoted inside comments, CDATA, signed
 * `xades:Object` content and attribute values — text anyone can write into a plain
 * XAdES-BES artifact, which would then be reported XL.
 */
function xadesElements(xml: Bytes): string[] {
	const document = new DOMParser().parseFromString(
		new TextDecoder('utf-8', { fatal: false }).decode(xml),
		'application/xml'
	);
	if (document.getElementsByTagName('parsererror').length > 0) throw new Error('malformed XML');
	return Array.from(document.getElementsByTagName('*'))
		.filter((element) => XADES_NS.test(element.namespaceURI ?? ''))
		.map((element) => element.localName);
}

function xadesEvidence(xml: Bytes, checks: Check[]): LevelEvidence {
	const { signature } = verifiedTimestamps(checks);
	const elements = xadesElements(xml);
	const has = (localName: string): boolean => elements.includes(localName);
	const completeRefs = has('CompleteCertificateRefs') && has('CompleteRevocationRefs');
	// The refs timestamp is what turns CAdES-C-shaped data into X Long: presence only, because
	// no reader for its token exists here yet (see the file header).
	const refsTimestamp = has('SigAndRefsTimeStamp') || has('RefsOnlyTimeStamp');
	return {
		structureOk: structureOk(checks),
		signatureTimestampVerified: signature,
		xLevelAttested: completeRefs && refsTimestamp,
		archivePresent: has('ArchiveTimeStamp')
	};
}

/**
 * The `N G obj … endobj` dictionary bodies up to the last `%%EOF`, with the data that only looks
 * like syntax removed: `%` comments, literal-string contents and each body's stream payload. A
 * PDF's markers (`/DSS`, `/Type /DocTimeStamp`) live in object dictionaries; a comment, a string,
 * a stream payload (arbitrary compressed data) or bytes appended after the final `%%EOF` are not
 * structure, and matching them — even a fake `N G obj` header written inside one — would let a
 * plain B-B document claim XL/LTA.
 */
function pdfObjectBodies(pdf: Bytes): string {
	const text = new TextDecoder('utf-8', { fatal: false }).decode(pdf);
	const eof = text.lastIndexOf('%%EOF');
	const document = eof < 0 ? text : text.slice(0, eof);
	// `%` runs a comment to the end of its line (ISO 32000-1 §7.2.4) and `( )` delimits a literal
	// string; neither is syntax, so a fake object header inside one must not open a body.
	const syntax = document.replace(/%.*$/gm, '').replace(/\((?:\\.|[^\\()])*\)/g, '()');
	const header = /\d+\s+\d+\s+obj(?![a-z])/g;
	const bodies: string[] = [];
	for (let match = header.exec(syntax); match; match = header.exec(syntax)) {
		const end = syntax.indexOf('endobj', header.lastIndex);
		if (end < 0) break; // a truncated tail is not structure: claim nothing from it
		const body = syntax.slice(header.lastIndex, end);
		// Only the dictionary before the `stream` keyword is PDF syntax; the payload after it is
		// arbitrary data. The lookbehind keeps `endstream` from matching.
		const stream = /(?<![a-zA-Z])stream\r?\n/.exec(body);
		bodies.push(stream ? body.slice(0, stream.index) : body);
		header.lastIndex = end;
	}
	return bodies.join('\n');
}

function padesEvidence(pdf: Bytes, checks: Check[]): LevelEvidence {
	const { signature, refs } = verifiedTimestamps(checks);
	let cmsRefs = false;
	let cmsArchive = false;
	for (const embedded of extractPadesSignatures(pdf)) {
		const unsigned = unsignedOids(embedded.cms) ?? [];
		cmsRefs =
			cmsRefs ||
			(unsigned.includes(OID_COMPLETE_CERTIFICATE_REFS) && unsigned.includes(OID_COMPLETE_REVOCATION_REFS));
		cmsArchive = cmsArchive || unsigned.includes(OID_ARCHIVE_TIMESTAMP);
	}
	// A `/Type /DocTimeStamp` dictionary holds a bare RFC 3161 token, not CMS `SignedData`;
	// `unsignedOids` returns null for that CMS, so the object scan below is its only evidence.
	const bodies = pdfObjectBodies(pdf);
	const dss = /\/DSS\s+(\d+\s+\d+\s+R|<<)/.test(bodies);
	const docTimestamp = /\/Type\s*\/DocTimeStamp(?![a-zA-Z])/.test(bodies);
	return {
		structureOk: structureOk(checks),
		signatureTimestampVerified: signature,
		// B-LT is cumulative on B-T (EN 319 142-1 §6): a DSS dictionary is LTV *data*, so it only
		// carries XL when the timestamp beneath it is there too — a bare `/DSS` is CAdES-C-shaped.
		xLevelAttested: (cmsRefs && refs) || (dss && (signature || docTimestamp)),
		archivePresent: docTimestamp || cmsArchive
	};
}

/**
 * The level of a verified artifact. Never throws: a truncated or unrecognised artifact
 * reports `unknown` instead of rejecting, whatever the parsers below decide to do.
 */
export function detectLevel(format: ArtifactFormat, bytes: Bytes, checks: Check[]): SignatureLevel {
	try {
		switch (format) {
			case 'cades':
				return levelFromEvidence(cadesEvidence(bytes, checks));
			case 'xades':
				return levelFromEvidence(xadesEvidence(bytes, checks));
			case 'pdf':
				return levelFromEvidence(padesEvidence(bytes, checks));
			default:
				return 'unknown';
		}
	} catch {
		return 'unknown';
	}
}
