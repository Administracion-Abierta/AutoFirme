/**
 * Minimal DER reader for PKCS#12 (RFC 7292) and X.509 (RFC 5280) — no dependencies.
 *
 * Only what those two formats need: low tag numbers, definite lengths, the usual string types.
 * Anything else raises instead of guessing.
 */

/** Bytes backed by a real `ArrayBuffer` — what WebCrypto and `Blob` accept without a cast. */
export type Bytes = Uint8Array<ArrayBuffer>;

export interface Der {
	/** Identifier octet: class | constructed | tag number. */
	tag: number;
	constructed: boolean;
	/** Content octets (header excluded). */
	value: Bytes;
	/** The whole TLV, header included — needed to re-emit nested structures verbatim. */
	raw: Bytes;
	children: Der[];
}

const UNSUPPORTED_LENGTH = 'unsupported length (indefinite on a primitive value, or > 4 octets)';

/** Universal tags whose BER form may split the value across chunks (X.690 §8.2.1, bit/octet/char strings). */
const CHUNKED_TAGS: Record<number, true> = {
	0x03: true, // BIT STRING
	0x04: true, // OCTET STRING
	0x0c: true, // UTF8String
	0x13: true, // PrintableString
	0x14: true, // TeletexString
	0x16: true, // IA5String
	0x1a: true, // VisibleString
	0x1b: true, // GeneralString
	0x1e: true // BMPString
};

export function readTlv(bytes: Bytes, offset: number): [Der, number] {
	if (offset + 2 > bytes.length) throw new Error('truncated DER value');
	const tag = bytes[offset];
	if ((tag & 0x1f) === 0x1f) throw new Error('high-tag-number DER values are not supported');
	const constructed = (tag & 0x20) !== 0;
	let cursor = offset + 1;
	const first = bytes[cursor++];
	let length: number;
	if (first === 0x80) {
		// BER indefinite length (X.690 §8.1.3.6): the real-world PKCS#12 envelopes exported by
		// Windows and older Java are written this way, down to OCTET STRINGs split into chunks. A
		// constructed *string* is flattened back to the primitive form the rest of the reader
		// expects; any other constructed value keeps its children, and its content octets are the
		// ones they encode.
		if (!constructed) throw new Error(UNSUPPORTED_LENGTH);
		const children: Der[] = [];
		let inner = cursor;
		for (;;) {
			if (inner + 2 > bytes.length) throw new Error('truncated DER value');
			if (bytes[inner] === 0x00 && bytes[inner + 1] === 0x00) break;
			const [child, next] = readTlv(bytes, inner);
			children.push(child);
			inner = next;
		}
		const end = inner + 2;
		// Only universal strings chunk (X.690 §8.2.1); a context tag like `a3` (`certBag [3]`)
		// shares its number with BIT STRING and must keep its children.
		const chunked = (tag & 0xc0) === 0 && CHUNKED_TAGS[tag & 0x1f] === true;
		return [
			{
				tag: chunked ? tag & ~0x20 : tag,
				constructed: !chunked,
				value: chunked ? concatBytes(children.map((child) => child.value)) : bytes.subarray(cursor, inner),
				raw: bytes.subarray(offset, end),
				children: chunked ? [] : children
			},
			end
		];
	}
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

export function parseDer(bytes: Bytes): Der {
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
export function integerBytes(node: Der): Bytes {
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

/* ---------------------------------------------------------------------------------------------
 * Writer — the mirror of the reader above, only what CMS/CAdES needs (RFC 5652, RFC 5035).
 * ------------------------------------------------------------------------------------------- */

function lengthBytes(length: number): Bytes {
	if (length < 0x80) return new Uint8Array([length]);
	const octets: number[] = [];
	for (let value = length; value > 0; value = Math.floor(value / 256)) octets.unshift(value & 0xff);
	return new Uint8Array([0x80 | octets.length, ...octets]);
}

/** Concatenate byte strings into one `Bytes`. */
export function concatBytes(parts: Bytes[]): Bytes {
	const out = new Uint8Array(parts.reduce((total, part) => total + part.length, 0));
	let offset = 0;
	for (const part of parts) {
		out.set(part, offset);
		offset += part.length;
	}
	return out;
}

export function tlv(tag: number, content: Bytes): Bytes {
	return concatBytes([new Uint8Array([tag]), lengthBytes(content.length), content]);
}

/** Content octets of a TLV, header skipped. */
export function derContent(value: Bytes): Bytes {
	const length = value[1];
	const header = (length & 0x80) === 0 ? 2 : 2 + (length & 0x7f);
	return value.subarray(header);
}

export function seq(...parts: Bytes[]): Bytes {
	return tlv(0x30, concatBytes(parts));
}

function compareBytes(a: Bytes, b: Bytes): number {
	const shared = Math.min(a.length, b.length);
	for (let i = 0; i < shared; i++) if (a[i] !== b[i]) return a[i] - b[i];
	return a.length - b.length;
}

/** `SET OF` — der-sorted, as DER requires. */
export function setOf(...parts: Bytes[]): Bytes {
	return tlv(0x31, concatBytes([...parts].sort(compareBytes)));
}

/** Only what RFC 5652/RFC 5280 OIDs need: two arcs plus base-128 continuation. */
export function objectId(dotted: string): Bytes {
	const arcs = dotted.split('.').map(Number);
	const octets = [arcs[0] * 40 + arcs[1]];
	for (const arc of arcs.slice(2)) {
		const group: number[] = [];
		let value = arc;
		do {
			group.unshift(value & 0x7f);
			value = Math.floor(value / 128);
		} while (value > 0);
		for (let i = 0; i < group.length - 1; i++) group[i] |= 0x80;
		octets.push(...group);
	}
	return tlv(0x06, new Uint8Array(octets));
}

/** Minimal two's-complement INTEGER (0x00 prefixed when the top bit is set). */
export function derInteger(value: number | Bytes): Bytes {
	if (typeof value === 'number') {
		const octets: number[] = [];
		let rest = value;
		do {
			octets.unshift(rest & 0xff);
			rest = Math.floor(rest / 256);
		} while (rest > 0);
		if (octets[0] & 0x80) octets.unshift(0);
		return tlv(0x02, new Uint8Array(octets));
	}
	let start = 0;
	while (start < value.length - 1 && value[start] === 0) start++;
	const body = value.subarray(start);
	return tlv(0x02, (body[0] & 0x80) !== 0 ? concatBytes([new Uint8Array([0]), body]) : body);
}

export function octetString(value: Bytes): Bytes {
	return tlv(0x04, value);
}

/** DER `UTCTime`, `YYMMDDHHMMSSZ` — valid until 2049. */
export function utcTime(date: Date): Bytes {
	const pad = (value: number) => String(value).padStart(2, '0');
	const text =
		`${pad(date.getUTCFullYear() % 100)}${pad(date.getUTCMonth() + 1)}${pad(date.getUTCDate())}` +
		`${pad(date.getUTCHours())}${pad(date.getUTCMinutes())}${pad(date.getUTCSeconds())}Z`;
	return tlv(0x17, new TextEncoder().encode(text));
}

/** `[n] IMPLICIT` — the value's own tag is replaced by the context tag. */
export function implicitTagged(n: number, value: Bytes): Bytes {
	return tlv(0xa0 | n, derContent(value));
}

/** `[n] EXPLICIT` — the value keeps its tag inside the context wrapper. */
export function explicitTagged(n: number, value: Bytes): Bytes {
	return tlv(0xa0 | n, value);
}
