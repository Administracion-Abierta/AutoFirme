/** Sniffs the signed artifact from its leading bytes; no file extension is trusted. */

import { parseCms } from './cms';
import type { ArtifactFormat, Bytes } from './types';

function startsWith(bytes: Bytes, text: string): boolean {
	if (bytes.length < text.length) return false;
	for (let i = 0; i < text.length; i++) if (bytes[i] !== text.charCodeAt(i)) return false;
	return true;
}

export function detectFormat(bytes: Bytes): ArtifactFormat {
	if (startsWith(bytes, '%PDF-')) return 'pdf';
	const head = new TextDecoder('utf-8', { fatal: false })
		.decode(bytes.subarray(0, Math.min(bytes.length, 8192)))
		.replace(/^\uFEFF/, '')
		.trimStart();
	if (head.startsWith('<?xml') || head.startsWith('<')) {
		return /<(\w+:)?Signature[\s>]/.test(head) ? 'xades' : 'unknown';
	}
	try {
		parseCms(bytes);
		return 'cades';
	} catch {
		// A truncated or corrupt CMS still starts with a SEQUENCE header; the verifier
		// then reports "structure unreadable" instead of "not a signed artifact".
		return bytes[0] === 0x30 ? 'cades' : 'unknown';
	}
}

/**
 * Signing-side format decision: what the signer should offer for these bytes. Magic first
 * (ZIP members keep their names uncompressed, so a bounded substring search is enough — no
 * unzip), the file extension only as a tiebreak, `cades` as the default. Verification stays
 * on `detectFormat`; OOXML verification itself is explicitly unsupported (AutoFirma 1.9).
 */
export type SignSuggestion = 'ooxml' | 'odf' | 'asic' | 'pades' | 'xades' | 'cades';

function sliceHas(bytes: Bytes, start: number, end: number, text: string): boolean {
	const stop = Math.min(end, bytes.length) - text.length;
	outer: for (let i = Math.max(0, start); i <= stop; i++) {
		for (let j = 0; j < text.length; j++) if (bytes[i + j] !== text.charCodeAt(j)) continue outer;
		return true;
	}
	return false;
}

const OOOXML_EXT = /\.(docx|docm|dotx|dotm|xlsx|xlsm|xltx|xltm|pptx|pptm|potx|potm|ppsx|ppsm|vsdx|vsdm)$/i;
const ODF_EXT = /\.(odt|ods|odp|odg|odc|odf|odi|odm|ott|ots|otp|otg)$/i;
const ASIC_EXT = /\.(asice|asics|sce|scs|bdoc)$/i;

export function suggestSignFormat(fileName: string, bytes: Bytes): SignSuggestion {
	const name = fileName.toLowerCase();
	if (
		bytes.length >= 4 &&
		bytes[0] === 0x50 &&
		bytes[1] === 0x4b &&
		bytes[2] === 0x03 &&
		bytes[3] === 0x04
	) {
		// Local headers live at the front, the central directory (every name again) at the back.
		const small = bytes.length <= 72 * 1024;
		const has = (text: string): boolean =>
			small
				? sliceHas(bytes, 0, bytes.length, text)
				: sliceHas(bytes, 0, 8192, text) || sliceHas(bytes, bytes.length - 65536, bytes.length, text);
		if (has('[Content_Types].xml')) return 'ooxml';
		if (has('application/vnd.etsi.asic') || has('META-INF/signatures')) return 'asic';
		if (has('application/vnd.oasis.opendocument') || (has('META-INF/manifest.xml') && has('mimetype')))
			return 'odf';
		if (OOOXML_EXT.test(name)) return 'ooxml';
		if (ASIC_EXT.test(name)) return 'asic';
		if (ODF_EXT.test(name)) return 'odf';
		return 'cades';
	}
	if (startsWith(bytes, '%PDF-')) return 'pades';
	const head = new TextDecoder('utf-8', { fatal: false })
		.decode(bytes.subarray(0, Math.min(bytes.length, 8192)))
		.replace(/^\uFEFF/, '')
		.trimStart();
	if (head.startsWith('<?xml') || head.startsWith('<')) return 'xades';
	if (OOOXML_EXT.test(name)) return 'ooxml';
	if (name.endsWith('.pdf')) return 'pades';
	if (name.endsWith('.xml')) return 'xades';
	if (ODF_EXT.test(name)) return 'odf';
	if (ASIC_EXT.test(name)) return 'asic';
	return 'cades';
}
