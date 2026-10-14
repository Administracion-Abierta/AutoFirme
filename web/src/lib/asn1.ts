/**
 * Minimal DER reader for PKCS#12 (RFC 7292) and X.509 (RFC 5280) — no dependencies.
 *
 * Only what those two formats need: low tag numbers, definite lengths, the usual string types.
 * Anything else raises instead of guessing.
 */

export interface Der {
	/** Identifier octet: class | constructed | tag number. */
	tag: number;
	constructed: boolean;
	/** Content octets (header excluded). */
	value: Uint8Array;
	/** The whole TLV, header included — needed to re-emit nested structures verbatim. */
	raw: Uint8Array;
	children: Der[];
}

const UNSUPPORTED_LENGTH = 'unsupported DER length (indefinite or > 4 octets)';

export function readTlv(bytes: Uint8Array, offset: number): [Der, number] {
	if (offset + 2 > bytes.length) throw new Error('truncated DER value');
	const tag = bytes[offset];
	if ((tag & 0x1f) === 0x1f) throw new Error('high-tag-number DER values are not supported');
	const constructed = (tag & 0x20) !== 0;
	let cursor = offset + 1;
	const first = bytes[cursor++];
	let length: number;
	if (first === 0x80) throw new Error(UNSUPPORTED_LENGTH);
	if (first & 0x80) {
		const octets = first & 0x7f;
		if (octets > 4) throw new Error(UNSUPPORTED_LENGTH);
		length = 0;
		for (let i = 0; i < octets; i++) length = length * 256 + bytes[cursor++];
	} else {
		length = first;
	}
	const end = cursor + length;
	if (end > bytes.length) throw new Error('truncated DER value');
	const node: Der = {
		tag,
		constructed,
		value: bytes.subarray(cursor, end),
		raw: bytes.subarray(offset, end),
		children: []
	};
	if (constructed) {
		let inner = cursor;
		while (inner < end) {
			const [child, next] = readTlv(bytes, inner);
			node.children.push(child);
			inner = next;
		}
	}
	return [node, end];
}

export function parseDer(bytes: Uint8Array): Der {
	const [node, end] = readTlv(bytes, 0);
	if (end !== bytes.length) throw new Error('trailing bytes after DER value');
	return node;
}

/** A `[n] EXPLICIT` wrapper: returns the single inner value, transparently. */
export function explicit(node: Der, tag: number): Der {
	if (node.tag === tag && node.constructed && node.children.length === 1) return node.children[0];
	return node;
}

export function oid(node: Der): string {
	if (node.tag !== 0x06) throw new Error(`expected OID, got tag ${node.tag}`);
	const bytes = node.value;
	if (bytes.length === 0) throw new Error('empty OID');
	const parts: number[] = [Math.floor(bytes[0] / 40), bytes[0] % 40];
	let value = 0;
	for (let i = 1; i < bytes.length; i++) {
		value = value * 128 + (bytes[i] & 0x7f);
		if ((bytes[i] & 0x80) === 0) {
			parts.push(value);
			value = 0;
		}
	}
	return parts.join('.');
}

/** Unsigned big-endian INTEGER/DEFAULT value as bytes (leading zero of the sign byte kept out). */
export function integerBytes(node: Der): Uint8Array {
	let value = node.value;
	while (value.length > 1 && value[0] === 0) value = value.subarray(1);
	return value;
}

export function integer(node: Der): number {
	const bytes = integerBytes(node);
	let value = 0;
	for (const byte of bytes) value = value * 256 + byte;
	return value;
}

/** Directory strings. Anything exotic (UniversalString, TeletexString) is decoded as Latin-1. */
export function string(node: Der): string {
	switch (node.tag) {
		case 0x0c:
		case 0x13:
		case 0x16:
			return new TextDecoder().decode(node.value);
		case 0x1e: {
			let out = '';
			for (let i = 0; i + 1 < node.value.length; i += 2) {
				out += String.fromCharCode((node.value[i] << 8) | node.value[i + 1]);
			}
			return out;
		}
		case 0x14:
		case 0x1c:
			return new TextDecoder('latin1').decode(
				node.tag === 0x1c ? node.value.filter((_, i) => i % 4 === 0) : node.value
			);
		default:
			throw new Error(`unsupported DER string tag ${node.tag}`);
	}
}
