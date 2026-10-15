/**
 * Minimal DER reader for the structures a signature verifier needs: X.509
 * certificates, CMS `ContentInfo`/`SignedData`, XMLDSig `X509Certificate` blobs.
 * Hand-rolled because the app ships zero crypto dependencies (AGENTS.md hard rule).
 */

import type { Bytes } from './types';

export class DerError extends Error {}

export interface Der {
	/** Tag octet; single-octet tags only, which is all X.509/CMS uses here. */
	tag: number;
	/** Content octets. */
	content: Bytes;
	/** Full TLV octets — the bytes a signature or digest is actually computed over. */
	raw: Bytes;
}

export const TAG = {
	INTEGER: 0x02,
	BIT_STRING: 0x03,
	OCTET_STRING: 0x04,
	NULL: 0x05,
	OID: 0x06,
	UTF8_STRING: 0x0c,
	SEQUENCE: 0x30,
	SET: 0x31,
	PRINTABLE_STRING: 0x13,
	IA5_STRING: 0x16,
	UTC_TIME: 0x17,
	GENERALIZED_TIME: 0x18
} as const;

/** Reads one TLV at `at`; returns the node plus the offset just past it. */
export function parse(buf: Bytes, at = 0): { node: Der; next: number } {
	if (at + 2 > buf.length) throw new DerError('truncated DER header');
	const tag = buf[at];
	let i = at + 1;
	let length = buf[i++];
	if (length & 0x80) {
		const octets = length & 0x7f;
		if (octets === 0 || octets > 4) throw new DerError(`unsupported DER length form (${octets})`);
		length = 0;
		for (let k = 0; k < octets; k++) length = length * 256 + buf[i++];
	}
	const start = i;
	const end = start + length;
	if (end > buf.length) throw new DerError('DER element runs past the buffer');
	return { node: { tag, content: buf.subarray(start, end), raw: buf.subarray(at, end) }, next: end };
}

/** DER-encodes a constructed element around `content` (used to rebuild signedAttrs as a SET). */
export function wrap(tag: number, content: Bytes): Bytes {
	const head = [tag];
	let length = content.length;
	if (length < 0x80) head.push(length);
	else {
		const bytes: number[] = [];
		while (length > 0) {
			bytes.unshift(length & 0xff);
			length >>>= 8;
		}
		head.push(0x80 | bytes.length, ...bytes);
	}
	const out = new Uint8Array(head.length + content.length);
	out.set(head);
	out.set(content, head.length);
	return out;
}

/** Every direct child of a constructed element. */
export function children(node: Der): Der[] {
	if ((node.tag & 0x20) === 0) throw new DerError(`tag 0x${node.tag.toString(16)} is not constructed`);
	const out: Der[] = [];
	let at = 0;
	while (at < node.content.length) {
		const parsed = parse(node.content, at);
		out.push(parsed.node);
		at = parsed.next;
	}
	return out;
}

export function at(node: Der, index: number): Der {
	const list = children(node);
	const found = list[index];
	if (!found) throw new DerError(`element ${index} missing (tag 0x${node.tag.toString(16)})`);
	return found;
}

/**
 * Content of an OCTET STRING or BIT STRING with the BIT STRING's unused-bit
 * octet removed (the payload of a signature, public key or embedded document).
 */
export function octets(node: Der): Bytes {
	if (node.tag === TAG.BIT_STRING) {
		const unused = node.content[0];
		if (unused > 7) throw new DerError('BIT STRING declares more than 7 unused bits');
		return node.content.subarray(1);
	}
	if (node.tag !== TAG.OCTET_STRING) throw new DerError(`expected OCTET/BIT STRING, got 0x${node.tag.toString(16)}`);
	return node.content;
}

export function oid(node: Der): string {
	if (node.tag !== TAG.OID) throw new DerError(`expected OID, got 0x${node.tag.toString(16)}`);
	const bytes = node.content;
	if (bytes.length === 0) throw new DerError('empty OID');
	const parts = [Math.floor(bytes[0] / 40), bytes[0] % 40];
	let current = 0;
	for (let i = 1; i < bytes.length; i++) {
		current = current * 128 + (bytes[i] & 0x7f);
		if ((bytes[i] & 0x80) === 0) {
			parts.push(current);
			current = 0;
		}
	}
	return parts.join('.');
}

export function integer(node: Der): bigint {
	if (node.tag !== TAG.INTEGER) throw new DerError(`expected INTEGER, got 0x${node.tag.toString(16)}`);
	let result = 0n;
	for (const byte of node.content) result = (result << 8n) | BigInt(byte);
	return result;
}

export function hex(bytes: Bytes): string {
	return Array.from(bytes, (byte) => byte.toString(16).padStart(2, '0')).join('');
}

/** UTCTime / GeneralizedTime → ISO-8601 Z string. */
export function time(node: Der): string {
	const text = new TextDecoder().decode(node.content).trim();
	const digits = text.replace(/[^0-9]/g, '');
	const offset = text.slice(digits.length + 1);
	const year =
		node.tag === TAG.UTC_TIME
			? Number(digits.slice(0, 2)) + (Number(digits.slice(0, 2)) >= 50 ? 1900 : 2000)
			: Number(digits.slice(0, 4));
	const rest = node.tag === TAG.UTC_TIME ? digits.slice(2) : digits.slice(4);
	const [month, day, hour, minute, second] = [0, 2, 4, 6, 8].map((index) =>
		Number((rest.slice(index, index + 2) || '0').padEnd(2, '0'))
	);
	const utc = Date.UTC(year, month - 1, day, hour, minute, second);
	if (offset.startsWith('+') || offset.startsWith('-')) {
		const sign = offset.startsWith('-') ? -1 : 1;
		const [hh, mm] = offset.slice(1).split(':');
		return new Date(utc - sign * (Number(hh) * 60 + Number(mm)) * 60_000).toISOString();
	}
	return new Date(utc).toISOString();
}

/** DER content bytes → base64 (btoa only accepts binary strings). */
export function toBase64(bytes: Bytes): string {
	let binary = '';
	for (const byte of bytes) binary += String.fromCharCode(byte);
	return btoa(binary);
}

export function fromBase64(text: string): Bytes {
	const binary = atob(text.replace(/\s+/g, ''));
	const out = new Uint8Array(binary.length);
	for (let i = 0; i < binary.length; i++) out[i] = binary.charCodeAt(i);
	return out;
}

/** PEM blocks of `-----BEGIN <label>-----` documents, decoded to DER. */
export function fromPem(pem: string): Bytes[] {
	const out: Bytes[] = [];
	const pattern = /-----BEGIN ([^-]+)-----([A-Za-z0-9+/=\s]+)-----END \1-----/g;
	for (const match of pem.matchAll(pattern)) out.push(fromBase64(match[2]));
	return out;
}

export async function sha256(bytes: Bytes): Promise<Bytes> {
	return new Uint8Array(await crypto.subtle.digest('SHA-256', bytes));
}
