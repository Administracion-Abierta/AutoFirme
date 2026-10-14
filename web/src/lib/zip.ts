/**
 * Minimal ZIP writer — STORE (no compression) entries, UTF-8 names, classic local-header +
 * central-directory + EOCD layout (APPNOTE 6.3.3).
 *
 * Why hand-rolled: no ZIP API exists in the browser and the issue forbids new dependencies.
 * `ponytail:` STORE keeps this to ~90 lines; switch to DEFLATE via `CompressionStream('deflate-raw')`
 * if signed outputs ever get big enough that download size matters.
 */

const LOCAL_HEADER = 0x04034b50;
const CENTRAL_HEADER = 0x02014b50;
const END_OF_CENTRAL = 0x06054b50;
/** Bit 11: file name is UTF-8. */
const UTF8_FLAG = 0x0800;

const CRC_TABLE = /* @__PURE__ */ (() => {
	const table = new Uint32Array(256);
	for (let index = 0; index < 256; index += 1) {
		let value = index;
		for (let bit = 0; bit < 8; bit += 1) {
			value = value & 1 ? 0xedb88320 ^ (value >>> 1) : value >>> 1;
		}
		table[index] = value >>> 0;
	}
	return table;
})();

/** CRC-32 (IEEE 802.3), the value ZIP stores for every entry. */
export function crc32(data: Uint8Array): number {
	let crc = 0xffffffff;
	for (let index = 0; index < data.length; index += 1) {
		crc = CRC_TABLE[(crc ^ data[index]) & 0xff] ^ (crc >>> 8);
	}
	return (crc ^ 0xffffffff) >>> 0;
}

export interface ZipEntry {
	/** Path inside the archive; '/' separates directories. */
	name: string;
	data: Uint8Array;
}

/** MS-DOS date/time pair, ZIP's only timestamp format. */
function dosDateTime(date: Date): { time: number; date: number } {
	return {
		time: (date.getHours() << 11) | (date.getMinutes() << 5) | (date.getSeconds() >> 1),
		date: ((date.getFullYear() - 1980) << 9) | ((date.getMonth() + 1) << 5) | date.getDate()
	};
}

export function zipStore(entries: ZipEntry[], now = new Date()): Uint8Array<ArrayBuffer> {
	const encoder = new TextEncoder();
	const stamp = dosDateTime(now);
	const chunks: Uint8Array[] = [];
	const central: Uint8Array[] = [];
	let offset = 0;

	for (const entry of entries) {
		const name = encoder.encode(entry.name);
		const crc = crc32(entry.data);

		const local = new Uint8Array(30 + name.length);
		const localView = new DataView(local.buffer);
		localView.setUint32(0, LOCAL_HEADER, true);
		localView.setUint16(4, 20, true); // version needed
		localView.setUint16(6, UTF8_FLAG, true);
		localView.setUint16(8, 0, true); // method: STORE
		localView.setUint16(10, stamp.time, true);
		localView.setUint16(12, stamp.date, true);
		localView.setUint32(14, crc, true);
		localView.setUint32(18, entry.data.length, true); // compressed size
		localView.setUint32(22, entry.data.length, true); // uncompressed size
		localView.setUint16(26, name.length, true);
		localView.setUint16(28, 0, true); // extra field length
		local.set(name, 30);
		chunks.push(local, entry.data);

		const directoryEntry = new Uint8Array(46 + name.length);
		const directoryView = new DataView(directoryEntry.buffer);
		directoryView.setUint32(0, CENTRAL_HEADER, true);
		directoryView.setUint16(4, 20, true); // version made by
		directoryView.setUint16(6, 20, true); // version needed
		directoryView.setUint16(8, UTF8_FLAG, true);
		directoryView.setUint16(10, 0, true); // method: STORE
		directoryView.setUint16(12, stamp.time, true);
		directoryView.setUint16(14, stamp.date, true);
		directoryView.setUint32(16, crc, true);
		directoryView.setUint32(20, entry.data.length, true);
		directoryView.setUint32(24, entry.data.length, true);
		directoryView.setUint16(28, name.length, true);
		directoryView.setUint16(30, 0, true); // extra field length
		directoryView.setUint16(32, 0, true); // comment length
		directoryView.setUint16(34, 0, true); // disk number
		directoryView.setUint16(36, 0, true); // internal attributes
		directoryView.setUint32(38, 0, true); // external attributes
		directoryView.setUint32(42, offset, true);
		directoryEntry.set(name, 46);
		central.push(directoryEntry);

		offset += local.length + entry.data.length;
	}

	const directorySize = central.reduce((total, entry) => total + entry.length, 0);
	const end = new Uint8Array(22);
	const endView = new DataView(end.buffer);
	endView.setUint32(0, END_OF_CENTRAL, true);
	endView.setUint16(4, 0, true); // this disk
	endView.setUint16(6, 0, true); // disk with central directory
	endView.setUint16(8, entries.length, true);
	endView.setUint16(10, entries.length, true);
	endView.setUint32(12, directorySize, true);
	endView.setUint32(16, offset, true);
	endView.setUint16(20, 0, true); // comment length

	const archive = new Uint8Array(offset + directorySize + end.length);
	let cursor = 0;
	for (const chunk of [...chunks, ...central, end]) {
		archive.set(chunk, cursor);
		cursor += chunk.length;
	}
	return archive;
}
