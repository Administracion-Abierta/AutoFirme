/**
 * Minimal DER (X.690) writer and reader.
 *
 * Just enough ASN.1 for a CMS `SignedData` (RFC 5652) and for pulling the issuer and serial out
 * of an X.509 certificate — the browser has no certificate API, and PAdES (`../pades.ts`) needs
 * `IssuerAndSerialNumber` plus `ESSCertIDv2`. Not a general-purpose ASN.1 toolkit; no BER
 * indefinite lengths, no schema-driven decoding.
 */

export type Bytes = Uint8Array<ArrayBuffer>;

export const SEQUENCE = 0x30;
export const SET = 0x31;
export const INTEGER = 0x02;
export const OCTET_STRING = 0x04;
export const OBJECT_IDENTIFIER = 0x06;
export const UTCTIME = 0x17;
export const GENERALIZED_TIME = 0x18;

/** TLV header + content. */
export interface Tlv {
	tag: number;
	start: number;
	contentStart: number;
	/** Offset one past the content. */
	end: number;
}

const encoder = new TextEncoder();

export function concat(parts: readonly Bytes[]): Bytes {
	let total = 0;
	for (const part of parts) total += part.length;
	const out = new Uint8Array(total);
	let offset = 0;
	for (const part of parts) {
		out.set(part, offset);
		offset += part.length;
	}
	return out;
}

export function toBytes(value: ArrayBuffer | ArrayBufferView): Bytes {
	if (ArrayBuffer.isView(value)) {
		const copy = new Uint8Array(value.byteLength);
		copy.set(new Uint8Array(value.buffer as ArrayBuffer, value.byteOffset, value.byteLength));
		return copy;
	}
	return new Uint8Array(value.slice(0));
}

function tlv(tag: number, content: Bytes): Bytes {
	return concat([new Uint8Array([tag]), derLength(content.length), content]);
}

function derLength(length: number): Bytes {
	if (length < 0x80) return new Uint8Array([length]);
	const bytes: number[] = [];
	for (let value = length; value > 0; value = Math.floor(value / 256)) bytes.unshift(value % 256);
	return new Uint8Array([0x80 | bytes.length, ...bytes]);
}

export function seq(...parts: Bytes[]): Bytes {
	return tlv(SEQUENCE, concat(parts));
}

export function set(...parts: Bytes[]): Bytes {
	return tlv(SET, concat(parts));
}

/** DER `SET OF`: members sorted by their encoded bytes (X.690 §11.6). */
export function setOf(parts: readonly Bytes[]): Bytes {
	return set(...[...parts].sort(compareBytes));
}

function compareBytes(a: Bytes, b: Bytes): number {
	const len = Math.min(a.length, b.length);
	for (let i = 0; i < len; i += 1) {
		if (a[i] !== b[i]) return a[i] - b[i];
	}
	return a.length - b.length;
}

export function octet(content: Bytes): Bytes {
	return tlv(OCTET_STRING, content);
}

/** Read an OID literal `1.2.840.113549.1.7.2` into its DER content. */
function encodeOid(dotted: string): Bytes {
	const arcs = dotted.split('.').map(Number);
	const out: number[] = [arcs[0] * 40 + arcs[1]];
	for (const arc of arcs.slice(2)) {
		const chunk: number[] = [];
		for (let value = arc; value > 0; value = Math.floor(value / 128)) chunk.unshift(value % 128);
		for (let i = 0; i < chunk.length - 1; i += 1) chunk[i] |= 0x80;
		out.push(...chunk);
	}
	return new Uint8Array(out);
}

export function oid(dotted: string): Bytes {
	return tlv(OBJECT_IDENTIFIER, encodeOid(dotted));
}

/** Canonical DER `INTEGER` from big-endian magnitude bytes. */
export function intBytes(magnitude: Bytes): Bytes {
	let start = 0;
	while (start < magnitude.length - 1 && magnitude[start] === 0) start += 1;
	const trimmed = magnitude[start] & 0x80 ? concat([new Uint8Array([0]), magnitude.subarray(start)]) : magnitude.subarray(start);
	return tlv(INTEGER, trimmed);
}

export function int(value: number): Bytes {
	const bytes: number[] = [];
	for (let v = value; v > 0; v = Math.floor(v / 256)) bytes.unshift(v % 256);
	if (bytes.length === 0) bytes.push(0);
	return intBytes(new Uint8Array(bytes));
}

/** Context-specific tag; `constructed` picks `[n]` vs the IMPLICIT primitive form. */
export function contextTag(number: number, content: Bytes, constructed = true): Bytes {
	return tlv((constructed ? 0xa0 : 0x80) | number, content);
}

/** X.509 `Time`: UTCTime for 1950–2049, GeneralizedTime outside (RFC 5280 §4.1.2.5). */
export function time(date: Date): Bytes {
	const year = date.getUTCFullYear();
	const stamp =
		pad(year % 100, 2) + timeTail(date);
	if (year >= 1950 && year < 2050) return tlv(UTCTIME, encoder.encode(stamp));
	return tlv(GENERALIZED_TIME, encoder.encode(String(year).padStart(4, '0') + timeTail(date)));
}

function timeTail(date: Date): string {
	return (
		pad(date.getUTCMonth() + 1, 2) +
		pad(date.getUTCDate(), 2) +
		pad(date.getUTCHours(), 2) +
		pad(date.getUTCMinutes(), 2) +
		pad(date.getUTCSeconds(), 2) +
		'Z'
	);
}

function pad(value: number, width: number): string {
	return String(value).padStart(width, '0');
}

export function readTlv(bytes: Bytes, offset = 0): Tlv {
	const tag = bytes[offset];
	let length = bytes[offset + 1];
	let cursor = offset + 2;
	if (length & 0x80) {
		const count = length & 0x7f;
		if (count === 0 || count > 4) throw new Error('unsupported DER length form');
		length = 0;
		for (let i = 0; i < count; i += 1) length = length * 256 + bytes[cursor + i];
		cursor += count;
	}
	const contentStart = cursor;
	const end = contentStart + length;
	if (tag === undefined || end > bytes.length) throw new Error('truncated DER');
	return { tag, start: offset, contentStart, end };
}

export function content(bytes: Bytes, item: Tlv): Bytes {
	return bytes.subarray(item.contentStart, item.end);
}

/** `IssuerAndSerialNumber` inputs of an X.509 `Certificate` (RFC 5280 §4.1). */
export function certIssuerAndSerial(certificate: Bytes): { issuer: Bytes; serial: Bytes } {
	const cert = readTlv(certificate);
	if (cert.tag !== SEQUENCE) throw new Error('not a DER certificate');
	const tbs = readTlv(certificate, cert.contentStart);
	if (tbs.tag !== SEQUENCE) throw new Error('missing tbsCertificate');
	let cursor = tbs.contentStart;
	const first = readTlv(certificate, cursor);
	// [0] EXPLICIT Version is optional (v1 certificates omit it).
	if ((first.tag & 0xe0) === 0xa0) cursor = first.end;
	const serial = readTlv(certificate, cursor);
	if (serial.tag !== INTEGER) throw new Error('missing certificate serialNumber');
	cursor = serial.end;
	cursor = readTlv(certificate, cursor).end; // signatureAlgorithm
	const issuer = readTlv(certificate, cursor);
	if (issuer.tag !== SEQUENCE) throw new Error('missing certificate issuer');
	return {
		serial: content(certificate, serial),
		issuer: certificate.subarray(issuer.start, issuer.end)
	};
}

/** WebCrypto ECDSA emits `r || s`; X.509/CMS wants `SEQUENCE { INTEGER r, INTEGER s }`. */
export function ecdsaRawToDer(raw: Bytes, coordinateBytes: number): Bytes {
	if (raw.length !== coordinateBytes * 2) throw new Error('unexpected ECDSA signature length');
	return seq(intBytes(raw.subarray(0, coordinateBytes)), intBytes(raw.subarray(coordinateBytes)));
}
