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
