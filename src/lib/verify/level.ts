/**
 * One signature-level vocabulary across CAdES, XAdES and PAdES (Refs #139).
 *
 * EU DSS names the same ladder `BASELINE-B / BASELINE-T / ... / X / XL / A` per family
 * (`CAdES-X` on the #125 fixtures, `XAdES-BASELINE-T` on `xades-t.xsig`); the UI, the
 * public API and MCP `autofirma_verify` all read the single `level` field instead.
 *
 * Levels come from evidence actually present, never from inference:
 * - `T` needs a verified signature timestamp (CAdES-T token over the signature value,
 *   `SignatureTimeStamp` over `SignatureValue`).
 * - `XL` needs verified X-level evidence on top: the complete certificate+revocation
 *   refs covered by an X timestamp (CAdES), the `Complete*Refs` elements (XAdES), the
 *   document security store (PAdES).
 * - `LTA` needs archive evidence on top of a verified base: the CAdES archive-timestamp
 *   attribute, the XAdES `ArchiveTimeStamp` element, a PAdES `DocTimeStamp`. A lone
 *   signature timestamp never implies LTA.
 *
 * `ponytail:` archive-token imprints are presence-checked here, not re-validated —
 * #142 (P4.8) owns the cryptographic re-validation when it mints XL/LTA.
 */

import { parseCms } from './cms';
import { extractPadesSignatures } from './pades';
import type { ArtifactFormat, Bytes, Check } from './types';

/** `B` = no timestamp; `unknown` = the artifact failed structural parsing. */
export type SignatureLevel = 'B' | 'T' | 'XL' | 'LTA' | 'unknown';

/** The decision-table input: every flag is observed evidence, never a guess. */
export interface LevelEvidence {
	structureOk: boolean;
	/** A timestamp over the signature itself verified (messageImprint match). */
	signatureTimestampVerified: boolean;
	/** X-level validation-data refs present (CAdES attrs, XAdES elements, PDF DSS). */
	xRefsPresent: boolean;
	/** A timestamp covering those refs verified. */
	xTimestampVerified: boolean;
	/** Archive evidence present (CAdES attribute, XAdES element, PDF DocTimeStamp). */
	archivePresent: boolean;
}

export function levelFromEvidence(evidence: LevelEvidence): SignatureLevel {
	if (!evidence.structureOk) return 'unknown';
	if (evidence.archivePresent && (evidence.signatureTimestampVerified || evidence.xTimestampVerified))
		return 'LTA';
	if (evidence.xRefsPresent && (evidence.signatureTimestampVerified || evidence.xTimestampVerified))
		return 'XL';
	if (evidence.signatureTimestampVerified) return 'T';
	return 'B';
}

/** `id-aa-ets-archiveTimestamp` (RFC 5126 §6.4) — the CAdES-A/LTA attribute. */
const OID_ARCHIVE_TIMESTAMP = '1.2.840.113549.1.9.16.2.48';
const OID_COMPLETE_CERTIFICATE_REFS = '1.2.840.113549.1.9.16.2.21';
const OID_COMPLETE_REVOCATION_REFS = '1.2.840.113549.1.9.16.2.22';

function timestampEvidence(checks: Check[]): { sig: boolean; x: boolean } {
	let sig = false;
	let x = false;
	for (const check of checks) {
		if (check.id !== 'timestamp' || check.status !== 'pass') continue;
		// CAdES: "covers the signature value" (TST) / "... and the certificate and
		// revocation refs" (esc); XAdES: "covers the canonicalised ds:SignatureValue".
		if (/signature\s?value/i.test(check.detail)) sig = true;
		if (/refs/i.test(check.detail)) x = true;
	}
	return { sig, x };
}

function structureOk(checks: Check[]): boolean {
	return checks.some((check) => check.id === 'structure' && check.status === 'pass');
}

function cadesEvidence(bytes: Bytes, checks: Check[]): LevelEvidence {
	const { sig, x } = timestampEvidence(checks);
	let xRefs = false;
	let archive = false;
	try {
		const unsigned = parseCms(bytes).unsignedAttributes.map((attribute) => attribute.type);
		xRefs = unsigned.includes(OID_COMPLETE_CERTIFICATE_REFS) && unsigned.includes(OID_COMPLETE_REVOCATION_REFS);
		archive = unsigned.includes(OID_ARCHIVE_TIMESTAMP);
	} catch {
		// Unparseable here means `structureOk` is already false; stay silent, report `unknown`.
	}
	return {
		structureOk: structureOk(checks),
		signatureTimestampVerified: sig,
		xRefsPresent: xRefs,
		xTimestampVerified: x,
		archivePresent: archive
	};
}

/** XAdES long-form elements, matched by local name whatever prefix the signer used. */
const XADES_REF_ELEMENTS = [
	'CompleteCertificateRefs',
	'CompleteRevocationRefs',
	'AttributeCertificateRefs',
	'AttributeRevocationRefs',
	'SigAndRefsTimeStamp',
	'RefsOnlyTimeStamp'
];

function elementPresent(text: string, localName: string): boolean {
	return new RegExp(`<(?:[\\w-]+:)?${localName}[\\s>/]`).test(text);
}

function xadesEvidence(bytes: Bytes, checks: Check[]): LevelEvidence {
	const { sig, x } = timestampEvidence(checks);
	let xRefs = false;
	let archive = false;
	try {
		const text = new TextDecoder('utf-8', { fatal: false }).decode(bytes);
		const complete =
			(elementPresent(text, 'CompleteCertificateRefs') && elementPresent(text, 'CompleteRevocationRefs')) ||
			(elementPresent(text, 'AttributeCertificateRefs') && elementPresent(text, 'AttributeRevocationRefs'));
		xRefs =
			complete || elementPresent(text, 'SigAndRefsTimeStamp') || elementPresent(text, 'RefsOnlyTimeStamp');
		archive = elementPresent(text, 'ArchiveTimeStamp');
	} catch {
		// As above: an undecodable artifact cannot have passed structure either.
	}
	return {
		structureOk: structureOk(checks),
		signatureTimestampVerified: sig,
		xRefsPresent: xRefs,
		xTimestampVerified: x,
		archivePresent: archive
	};
}

function padesEvidence(bytes: Bytes, checks: Check[]): LevelEvidence {
	const { sig, x } = timestampEvidence(checks);
	let cmsXRefs = false;
	let cmsArchive = false;
	try {
		// `DocTimeStamp` dictionaries carry a bare RFC 3161 token, not CMS `SignedData`:
		// `parseCms` throws on them and they contribute no CAdES evidence — the byte scan
		// below is what sees them.
		for (const signature of extractPadesSignatures(bytes)) {
			try {
				const unsigned = parseCms(signature.cms).unsignedAttributes.map((attribute) => attribute.type);
				cmsXRefs =
					cmsXRefs ||
					(unsigned.includes(OID_COMPLETE_CERTIFICATE_REFS) &&
						unsigned.includes(OID_COMPLETE_REVOCATION_REFS));
				cmsArchive = cmsArchive || unsigned.includes(OID_ARCHIVE_TIMESTAMP);
			} catch {
				continue;
			}
		}
	} catch {
		// No extractable signatures: `structureOk` already says so.
	}
	let text = '';
	try {
		// Lossy decode is fine: the markers below are pure ASCII and survive replacement.
		text = new TextDecoder('utf-8', { fatal: false }).decode(bytes);
	} catch {
		text = '';
	}
	// The document security store is an inline `<<…>>` dict or an indirect object; a content
	// stream spelling `/DSS` is contrived enough to accept the residual false-positive risk.
	const dss = /\/DSS\s+(\d+\s+\d+\s+R|<<)/.test(text);
	const docTimestamp = /\/DocTimeStamp|\/ETSI\.RFC3161/.test(text);
	return {
		structureOk: structureOk(checks),
		signatureTimestampVerified: sig,
		xRefsPresent: cmsXRefs || dss,
		xTimestampVerified: x,
		archivePresent: cmsArchive || docTimestamp
	};
}

/**
 * The level for a verified artifact. Never throws: anything unexpected (including a
 * truncated CMS mid-parse) reports `unknown` instead of rejecting.
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
