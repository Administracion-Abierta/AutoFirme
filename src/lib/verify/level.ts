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
 *   (XAdES), the document security store (PAdES). Refs present with no attestation is
 *   `CAdES-C`, not `XL` — the #125 `cades-xlong2-oldoid.p7s` control is the proof.
 * - `LTA` needs archive evidence on top of a verified base: the CAdES archive-timestamp
 *   attribute, the XAdES `ArchiveTimeStamp` element, a PAdES `DocTimeStamp`. A lone
 *   signature timestamp never implies LTA.
 *
 * `ponytail:` the XAdES refs/archive tokens are presence-checked, not re-validated —
 * `verifyXades` has no reader for them yet; #142 (P4.8) owns that when it mints X/LTA.
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

/** The long-form XAdES elements that carry X-level validation data or archive evidence. */
function elementPresent(text: string, localName: string): boolean {
	return new RegExp(`<(?:[\\w-]+:)?${localName}[\\s>/]`).test(text);
}

function xadesEvidence(xml: Bytes, checks: Check[]): LevelEvidence {
	const { signature } = verifiedTimestamps(checks);
	const text = new TextDecoder('utf-8', { fatal: false }).decode(xml);
	const completeRefs =
		elementPresent(text, 'CompleteCertificateRefs') && elementPresent(text, 'CompleteRevocationRefs');
	// The refs timestamp is what turns CAdES-C-shaped data into X Long: presence only, because
	// no reader for its token exists here yet (see the file header).
	const refsTimestamp = elementPresent(text, 'SigAndRefsTimeStamp') || elementPresent(text, 'RefsOnlyTimeStamp');
	return {
		structureOk: structureOk(checks),
		signatureTimestampVerified: signature,
		xLevelAttested: completeRefs && refsTimestamp,
		archivePresent: elementPresent(text, 'ArchiveTimeStamp')
	};
}

function padesEvidence(pdf: Bytes, checks: Check[]): LevelEvidence {
	const { signature, refs } = verifiedTimestamps(checks);
	let cmsRefs = false;
	let cmsArchive = false;
	for (const embedded of extractPadesSignatures(pdf)) {
		// A `/DocTimeStamp` dictionary holds a bare RFC 3161 token, not CMS `SignedData`;
		// `unsignedOids` returns null for it and the byte scan below sees it instead.
		const unsigned = unsignedOids(embedded.cms) ?? [];
		cmsRefs =
			cmsRefs ||
			(unsigned.includes(OID_COMPLETE_CERTIFICATE_REFS) && unsigned.includes(OID_COMPLETE_REVOCATION_REFS));
		cmsArchive = cmsArchive || unsigned.includes(OID_ARCHIVE_TIMESTAMP);
	}
	// Lossy decode is fine: every marker below is pure ASCII and survives replacement.
	const text = new TextDecoder('utf-8', { fatal: false }).decode(pdf);
	const dss = /\/DSS\s+(\d+\s+\d+\s+R|<<)/.test(text);
	const docTimestamp = /\/DocTimeStamp|\/ETSI\.RFC3161/.test(text);
	return {
		structureOk: structureOk(checks),
		signatureTimestampVerified: signature,
		xLevelAttested: dss || (cmsRefs && refs),
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
