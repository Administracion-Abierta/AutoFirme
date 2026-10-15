/**
 * PAdES verification for signed PDFs (issue #11).
 *
 * Every signature dictionary carries `/ByteRange [start1 len1 start2 len2]` (ISO 32000-1 §12.8.1):
 * the two spans are exactly the bytes the CMS in `/Contents` covers — the gap between them is the
 * hex literal itself, excluded from the digest. The CMS is a detached CAdES over those bytes, so
 * verification defers to `verifyCades` with the range bytes as content; only the PDF-side
 * extraction lives here.
 *
 * Discovery is a byte scan for `/ByteRange`, not an xref walk: candidates are kept only when the
 * gap delimits a `<…>` hex string the ranges line up with, so stream data that happens to spell
 * the keyword cannot pass as a signature.
 */

import { verifyCades } from './cms';
import type { Bytes, Check, VerificationReport, VerifyOptions } from './types';
import { summarize } from './types';

export interface PadesSignature {
	/** `/ByteRange [start1 length1 start2 length2]` as written in the document. */
	byteRange: [number, number, number, number];
	/** The CMS `SignedData` from `/Contents`, its zero padding left for the DER reader. */
	cms: Bytes;
	/** Everything the two ranges cover — the bytes behind the signed messageDigest. */
	content: Bytes;
}

const KEYWORD = [...'/ByteRange'].map((character) => character.charCodeAt(0));

function isWhitespace(byte: number): boolean {
	return byte === 0x20 || byte === 0x09 || byte === 0x0a || byte === 0x0d || byte === 0x2c; // incl. ','
}

function hexValue(byte: number): number {
	if (byte >= 0x30 && byte <= 0x39) return byte - 0x30;
	if (byte >= 0x41 && byte <= 0x46) return byte - 0x37;
	if (byte >= 0x61 && byte <= 0x66) return byte - 0x57;
	return -1;
}

/** Reads `[n n n n]` right after `from`, or null when the text is not four plain integers. */
function readRanges(pdf: Bytes, from: number): [number, number, number, number] | null {
	let cursor = from;
	while (cursor < pdf.length && isWhitespace(pdf[cursor])) cursor += 1;
	if (pdf[cursor] !== 0x5b) return null; // '['
	cursor += 1;
	const values: number[] = [];
	while (values.length < 4) {
		while (cursor < pdf.length && isWhitespace(pdf[cursor])) cursor += 1;
		const start = cursor;
		while (cursor < pdf.length && pdf[cursor] >= 0x30 && pdf[cursor] <= 0x39) cursor += 1;
		if (cursor === start) return null;
		// Spans this long do not exist in a browser-held document, and the parse must stay bounded.
		if (cursor - start > 10) return null;
		values.push(Number(String.fromCharCode(...pdf.subarray(start, cursor))));
	}
	while (cursor < pdf.length && isWhitespace(pdf[cursor])) cursor += 1;
	if (pdf[cursor] !== 0x5d) return null; // ']'
	return values as [number, number, number, number];
}

/**
 * The gap `[start1 + len1 … start2)` must open with `<`, run on hex digits/whitespace only and
 * close with `>` as its last byte — that is the `/Contents` literal, and nothing else.
 * Returns the decoded bytes of the literal.
 */
function readContentsLiteral(pdf: Bytes, gapStart: number, gapEnd: number): Bytes | null {
	if (gapEnd - gapStart < 3 || pdf[gapStart] !== 0x3c || pdf[gapEnd - 1] !== 0x3e) return null;
	const digits: number[] = [];
	for (let cursor = gapStart + 1; cursor < gapEnd - 1; cursor += 1) {
		const byte = pdf[cursor];
		if (byte === 0x20 || byte === 0x09 || byte === 0x0a || byte === 0x0d) continue;
		const nibble = hexValue(byte);
		if (nibble < 0) return null;
		digits.push(nibble);
	}
	if (digits.length === 0 || digits.length % 2 !== 0) return null;
	const out = new Uint8Array(digits.length / 2);
	for (let index = 0; index < out.length; index += 1) out[index] = digits[index * 2] * 16 + digits[index * 2 + 1];
	return out;
}

function concat(a: Bytes, b: Bytes): Bytes {
	const out = new Uint8Array(a.length + b.length);
	out.set(a, 0);
	out.set(b, a.length);
	return out;
}

/** Every well-formed signature embedded in the PDF, in document order. */
export function extractPadesSignatures(pdf: Bytes): PadesSignature[] {
	const found: PadesSignature[] = [];
	for (let offset = 0; offset + KEYWORD.length < pdf.length; offset += 1) {
		if (pdf[offset] !== KEYWORD[0]) continue;
		let matched = true;
		for (let index = 1; index < KEYWORD.length; index += 1) {
			if (pdf[offset + index] !== KEYWORD[index]) {
				matched = false;
				break;
			}
		}
		if (!matched) continue;
		const byteRange = readRanges(pdf, offset + KEYWORD.length);
		if (!byteRange) continue;
		const [start1, length1, start2, length2] = byteRange;
		// A signing ByteRange starts at the head of the file and never runs past its end.
		if (start1 !== 0 || length1 <= 0 || length2 <= 0 || start2 < length1 || start2 + length2 > pdf.length) continue;
		const contents = readContentsLiteral(pdf, length1, start2);
		if (!contents) continue;
		found.push({
			byteRange,
			cms: contents,
			content: concat(pdf.subarray(0, length1), pdf.subarray(start2, start2 + length2))
		});
		offset = start2; // the ranges cover everything up to the next signature, if any
	}
	return found;
}

export async function verifyPades(bytes: Bytes, options: VerifyOptions = {}): Promise<VerificationReport> {
	const signatures = extractPadesSignatures(bytes);
	if (signatures.length === 0) {
		return {
			format: 'pdf',
			ok: false,
			status: 'invalid',
			signer: null,
			signingTime: null,
			checks: [{ id: 'structure', status: 'fail', detail: 'this PDF carries no signature to verify' }]
		};
	}

	const reports: VerificationReport[] = [];
	for (const signature of signatures) {
		reports.push(await verifyCades(signature.cms, { ...options, detached: signature.content }));
	}

	const many = signatures.length > 1;
	const checks: Check[] = reports.flatMap((report, index) =>
		report.checks.map((check) => {
			const label = many ? `signature ${index + 1}/${signatures.length}: ` : '';
			if (check.id === 'structure') {
				const ranges = signatures[index].byteRange.join(' ');
				return { ...check, detail: `${label}ByteRange [${ranges}]: ${check.detail}` };
			}
			return { ...check, detail: `${label}${check.detail}` };
		})
	);

	const last = reports[reports.length - 1];
	const clock = options.now ?? new Date();
	const expired = last.signer !== null && clock > new Date(last.signer.notAfter);
	return { format: 'pdf', ...summarize(checks, expired), checks, signer: last.signer, signingTime: last.signingTime };
}
