/**
 * Legacy PKCS#12 primitives — RFC 7292 Appendix B.2 key derivation plus the ciphers WebCrypto
 * does not expose: DES (and 2/3-key triple DES), RC2 (RFC 2268) and RC4.
 *
 * Hand-written because `crypto.subtle` has neither DES, RC2 nor RC4, and the PBES1 schemes that
 * older Windows/FNMT exports use are built exactly from these. All of it is key material only —
 * no message data is hashed with it.
 */

import { type Bytes, concatBytes } from './asn1';

/* ---------------------------------------------------------------------------------------------
 * RFC 7292 B.2 — the SHA-1 based PKCS#12 key derivation (NOT PBKDF2).
 * ------------------------------------------------------------------------------------------- */

export type LegacyHash = 'SHA-1' | 'SHA-256' | 'SHA-384' | 'SHA-512';

/** Per-hash `u` (digest size) and `v` (block size), as Appendix B.2 uses them. */
export const HASH_PARAMS: Record<LegacyHash, { u: number; v: number }> = {
	'SHA-1': { u: 20, v: 64 },
	'SHA-256': { u: 32, v: 64 },
	'SHA-384': { u: 48, v: 128 },
	'SHA-512': { u: 64, v: 128 }
};

/** The password as PKCS#12 wants it: BMPString (UTF-16BE) with the two-byte NUL terminator. */
function toBmpString(password: string): Bytes {
	const out = new Uint8Array(password.length * 2 + 2);
	for (let i = 0; i < password.length; i++) {
		const code = password.charCodeAt(i);
		out[i * 2] = code >> 8;
		out[i * 2 + 1] = code & 0xff;
	}
	return out;
}

async function digest(hash: LegacyHash, data: Bytes): Promise<Bytes> {
	return new Uint8Array(await crypto.subtle.digest(hash, data));
}

/**
 * RFC 7292 B.2. `id` is 1 for key material, 2 for the IV and 3 for the MAC key; `iterations` and
 * `salt` come from the container's own algorithm identifier.
 */
export async function pkcs12Kdf(
	password: string,
	salt: Bytes,
	iterations: number,
	id: 1 | 2 | 3,
	length: number,
	hash: LegacyHash
): Promise<Bytes> {
	const { u, v } = HASH_PARAMS[hash];
	const pass = toBmpString(password);

	// I = salt and password, each repeated to a whole number of hash blocks.
	const saltLength = salt.length ? v * Math.ceil(salt.length / v) : 0;
	const passLength = pass.length ? v * Math.ceil(pass.length / v) : 0;
	const I = new Uint8Array(saltLength + passLength);
	for (let i = 0; i < saltLength; i++) I[i] = salt[i % salt.length];
	for (let i = 0; i < passLength; i++) I[saltLength + i] = pass[i % pass.length];

	const diversifier = new Uint8Array(v).fill(id);
	const out = new Uint8Array(length);
	let offset = 0;
	while (offset < length) {
		let A = await digest(hash, concatBytes([diversifier, I]));
		for (let round = 1; round < iterations; round++) A = await digest(hash, A);

		const take = Math.min(u, length - offset);
		out.set(A.subarray(0, take), offset);
		offset += take;
		if (offset >= length) break;

		// I_j = (I_j + B + 1) mod 2^(8v), one v-byte block at a time (carry does not cross).
		const B = new Uint8Array(v);
		for (let i = 0; i < v; i++) B[i] = A[i % u];
		for (let start = 0; start < I.length; start += v) {
			let carry = 1;
			for (let k = v - 1; k >= 0; k--) {
				carry += B[k] + I[start + k];
				I[start + k] = carry & 0xff;
				carry >>= 8;
			}
		}
	}
	return out;
}

/* ---------------------------------------------------------------------------------------------
 * Block modes — shared by DES and RC2 (8-byte blocks).
 * ------------------------------------------------------------------------------------------- */

type BlockCipher = (block: Bytes) => Bytes;

function cbcDecrypt(decryptBlock: BlockCipher, iv: Bytes, data: Bytes): Bytes {
	if (data.length === 0 || data.length % 8 !== 0) {
		throw new Error(`CBC ciphertext of ${data.length} bytes is not a whole number of blocks`);
	}
	const out = new Uint8Array(data.length);
	let previous = iv;
	for (let offset = 0; offset < data.length; offset += 8) {
		const block = data.subarray(offset, offset + 8);
		const plain = decryptBlock(block);
		for (let i = 0; i < 8; i++) out[offset + i] = plain[i] ^ previous[i];
		previous = block;
	}
	return out;
}

/** RFC 5652 padding: every trailing byte equals the pad length. Returns null when it does not. */
export function unpadPkcs7(data: Bytes): Bytes | null {
	if (data.length === 0) return null;
	const pad = data[data.length - 1];
	if (pad === 0 || pad > 8 || pad > data.length) return null;
	for (let i = data.length - pad; i < data.length; i++) if (data[i] !== pad) return null;
	return data.subarray(0, data.length - pad);
}

/* ---------------------------------------------------------------------------------------------
 * DES and triple DES.
 * ------------------------------------------------------------------------------------------- */

const DES_IP = [
	58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6,
	64, 56, 48, 40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3,
	61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7
];
const DES_FP = [
	40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30,
	37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27,
	34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25
];
const DES_E = [
	32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17,
	18, 19, 20, 21, 20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1
];
const DES_P = [
	16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19,
	13, 30, 6, 22, 11, 4, 25
];
const DES_PC1 = [
	57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3,
	60, 52, 44, 36, 63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37,
	29, 21, 13, 5, 28, 20, 12, 4
];
const DES_PC2 = [
	14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2, 41,
	52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32
];
const DES_SHIFTS = [1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1];
const DES_SBOX = [
	[14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7, 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8, 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0, 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13],
	[15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10, 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5, 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15, 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9],
	[10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8, 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1, 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7, 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12],
	[7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15, 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9, 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4, 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14],
	[2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9, 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6, 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14, 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3],
	[12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11, 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8, 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6, 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13],
	[4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1, 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6, 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2, 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12],
	[13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7, 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2, 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8, 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11]
];

/** Bit `table[i]` of `input` (1-based, MSB first) becomes bit `i` of the result. */
function permute(input: bigint, table: readonly number[], inputBits: number): bigint {
	let out = 0n;
	for (const position of table) out = (out << 1n) | ((input >> BigInt(inputBits - position)) & 1n);
	return out;
}

function bytesToBlock(bytes: Bytes, offset: number): bigint {
	let value = 0n;
	for (let i = 0; i < 8; i++) value = (value << 8n) | BigInt(bytes[offset + i]);
	return value;
}

function blockToBytes(value: bigint, out: Uint8Array, offset: number): void {
	for (let i = 7; i >= 0; i--) {
		out[offset + i] = Number(value & 0xffn);
		value >>= 8n;
	}
}

function desSubkeys(key: Bytes): bigint[] {
	let bits = 0n;
	for (let i = 0; i < 8; i++) bits = (bits << 8n) | BigInt(key[i]);

	const cd = permute(bits, DES_PC1, 64);
	let c = cd >> 28n;
	let d = cd & 0xfffffffn;
	const subkeys: bigint[] = [];
	for (let round = 0; round < 16; round++) {
		const shift = BigInt(DES_SHIFTS[round]);
		c = ((c << shift) | (c >> (28n - shift))) & 0xfffffffn;
		d = ((d << shift) | (d >> (28n - shift))) & 0xfffffffn;
		subkeys.push(permute((c << 28n) | d, DES_PC2, 56));
	}
	return subkeys;
}

function desRound(right: bigint, subkey: bigint): bigint {
	const mixed = permute(right, DES_E, 32) ^ subkey;
	let substituted = 0n;
	for (let box = 0; box < 8; box++) {
		const six = Number((mixed >> BigInt(42 - box * 6)) & 0x3fn);
		const row = ((six & 0x20) >> 4) | (six & 1);
		const column = (six >> 1) & 0xf;
		substituted = (substituted << 4n) | BigInt(DES_SBOX[box][row * 16 + column]);
	}
	return permute(substituted, DES_P, 32);
}

/** Feistel network; the subkey order decides encrypt (forward) or decrypt (reversed). */
function desCrypt(block: bigint, subkeys: bigint[]): bigint {
	const permuted = permute(block, DES_IP, 64);
	let left = permuted >> 32n;
	let right = permuted & 0xffffffffn;
	for (const subkey of subkeys) {
		const next = left ^ desRound(right, subkey);
		left = right;
		right = next;
	}
	return permute((right << 32n) | left, DES_FP, 64);
}

function desBlock(subkeys: bigint[], block: Bytes): Bytes {
	const out = new Uint8Array(8);
	blockToBytes(desCrypt(bytesToBlock(block, 0), subkeys), out, 0);
	return out;
}

/** One DES ECB block — exported for the known-answer tests. */
export function desEncryptBlock(key: Bytes, block: Bytes): Bytes {
	return desBlock(desSubkeys(key), block);
}

/**
 * DES-EDE3-CBC decryption. A 16-byte key is the 2-key form, expanded to K1||K2||K1.
 */
export function tripleDesCbcDecrypt(key: Bytes, iv: Bytes, data: Bytes): Bytes {
	const k1 = desSubkeys(key.subarray(0, 8)).reverse();
	const k2 = desSubkeys(key.subarray(8, 16));
	const k3 = desSubkeys(key.length >= 24 ? key.subarray(16, 24) : key.subarray(0, 8)).reverse();
	return cbcDecrypt((block) => desBlock(k1, desBlock(k2, desBlock(k3, block))), iv, data);
}

/* ---------------------------------------------------------------------------------------------
 * RC2 (RFC 2268) — effective key bits drive both the expansion and the search space.
 * ------------------------------------------------------------------------------------------- */

const RC2_PITABLE = new Uint8Array([
	0xd9, 0x78, 0xf9, 0xc4, 0x19, 0xdd, 0xb5, 0xed, 0x28, 0xe9, 0xfd, 0x79, 0x4a, 0xa0, 0xd8, 0x9d,
	0xc6, 0x7e, 0x37, 0x83, 0x2b, 0x76, 0x53, 0x8e, 0x62, 0x4c, 0x64, 0x88, 0x44, 0x8b, 0xfb, 0xa2,
	0x17, 0x9a, 0x59, 0xf5, 0x87, 0xb3, 0x4f, 0x13, 0x61, 0x45, 0x6d, 0x8d, 0x09, 0x81, 0x7d, 0x32,
	0xbd, 0x8f, 0x40, 0xeb, 0x86, 0xb7, 0x7b, 0x0b, 0xf0, 0x95, 0x21, 0x22, 0x5c, 0x6b, 0x4e, 0x82,
	0x54, 0xd6, 0x65, 0x93, 0xce, 0x60, 0xb2, 0x1c, 0x73, 0x56, 0xc0, 0x14, 0xa7, 0x8c, 0xf1, 0xdc,
	0x12, 0x75, 0xca, 0x1f, 0x3b, 0xbe, 0xe4, 0xd1, 0x42, 0x3d, 0xd4, 0x30, 0xa3, 0x3c, 0xb6, 0x26,
	0x6f, 0xbf, 0x0e, 0xda, 0x46, 0x69, 0x07, 0x57, 0x27, 0xf2, 0x1d, 0x9b, 0xbc, 0x94, 0x43, 0x03,
	0xf8, 0x11, 0xc7, 0xf6, 0x90, 0xef, 0x3e, 0xe7, 0x06, 0xc3, 0xd5, 0x2f, 0xc8, 0x66, 0x1e, 0xd7,
	0x08, 0xe8, 0xea, 0xde, 0x80, 0x52, 0xee, 0xf7, 0x84, 0xaa, 0x72, 0xac, 0x35, 0x4d, 0x6a, 0x2a,
	0x96, 0x1a, 0xd2, 0x71, 0x5a, 0x15, 0x49, 0x74, 0x4b, 0x9f, 0xd0, 0x5e, 0x04, 0x18, 0xa4, 0xec,
	0xc2, 0xe0, 0x41, 0x6e, 0x0f, 0x51, 0xcb, 0xcc, 0x24, 0x91, 0xaf, 0x50, 0xa1, 0xf4, 0x70, 0x39,
	0x99, 0x7c, 0x3a, 0x85, 0x23, 0xb8, 0xb4, 0x7a, 0xfc, 0x02, 0x36, 0x5b, 0x25, 0x55, 0x97, 0x31,
	0x2d, 0x5d, 0xfa, 0x98, 0xe3, 0x8a, 0x92, 0xae, 0x05, 0xdf, 0x29, 0x10, 0x67, 0x6c, 0xba, 0xc9,
	0xd3, 0x00, 0xe6, 0xcf, 0xe1, 0x9e, 0xa8, 0x2c, 0x63, 0x16, 0x01, 0x3f, 0x58, 0xe2, 0x89, 0xa9,
	0x0d, 0x38, 0x34, 0x1b, 0xab, 0x33, 0xff, 0xb0, 0xbb, 0x48, 0x0c, 0x5f, 0xb9, 0xb1, 0xcd, 0x2e,
	0xc5, 0xf3, 0xdb, 0x47, 0xe5, 0xa5, 0x9c, 0x77, 0x0a, 0xa6, 0x20, 0x68, 0xfe, 0x7f, 0xc1, 0xad
]);

const RC2_ROTATION = [1, 2, 3, 5];

function rc2ExpandKey(key: Bytes, effectiveBits: number): number[] {
	const L = new Uint8Array(128);
	L.set(key.subarray(0, Math.min(key.length, 128)));
	const supplied = Math.max(key.length, 1);
	for (let i = supplied; i < 128; i++) L[i] = RC2_PITABLE[(L[i - 1] + L[i - supplied]) & 0xff];

	const bytes = Math.ceil(effectiveBits / 8);
	const mask = 255 % 2 ** (8 + effectiveBits - 8 * bytes);
	L[128 - bytes] = RC2_PITABLE[L[128 - bytes] & mask];
	for (let i = 127 - bytes; i >= 0; i--) L[i] = RC2_PITABLE[L[i + 1] ^ L[i + bytes]];

	const K: number[] = [];
	for (let i = 0; i < 64; i++) K.push(L[2 * i] + 256 * L[2 * i + 1]);
	return K;
}

function rc2Words(block: Bytes): number[] {
	return [
		block[0] + 256 * block[1],
		block[2] + 256 * block[3],
		block[4] + 256 * block[5],
		block[6] + 256 * block[7]
	];
}

function rc2Bytes(words: number[]): Bytes {
	const out = new Uint8Array(8);
	for (let i = 0; i < 4; i++) {
		out[2 * i] = words[i] & 0xff;
		out[2 * i + 1] = words[i] >> 8;
	}
	return out;
}

function rc2EncryptWords(K: number[], words: number[]): number[] {
	const r = [...words];
	let j = 0;
	for (let round = 0; round < 16; round++) {
		for (let i = 0; i < 4; i++) {
			const composite = (r[(i + 3) % 4] & r[(i + 2) % 4]) + (~r[(i + 3) % 4] & r[(i + 1) % 4]);
			const sum = (r[i] + K[j] + composite) & 0xffff;
			const shift = RC2_ROTATION[i];
			r[i] = ((sum << shift) | (sum >>> (16 - shift))) & 0xffff;
			j++;
		}
		if (round === 4 || round === 10) {
			for (let i = 0; i < 4; i++) r[i] = (r[i] + K[r[(i + 3) % 4] & 63]) & 0xffff;
		}
	}
	return r;
}

function rc2DecryptWords(K: number[], words: number[]): number[] {
	const r = [...words];
	let j = 63;
	for (let round = 0; round < 16; round++) {
		for (let i = 3; i >= 0; i--) {
			const shift = RC2_ROTATION[i];
			const rotated = ((r[i] >>> shift) | (r[i] << (16 - shift))) & 0xffff;
			const composite = (r[(i + 3) % 4] & r[(i + 2) % 4]) + (~r[(i + 3) % 4] & r[(i + 1) % 4]);
			r[i] = (rotated - K[j] - composite) & 0xffff;
			j--;
		}
		if (round === 4 || round === 10) {
			for (let i = 3; i >= 0; i--) r[i] = (r[i] - K[r[(i + 3) % 4] & 63]) & 0xffff;
		}
	}
	return r;
}

/** One RC2 ECB block — exported for the known-answer tests. */
export function rc2EncryptBlock(key: Bytes, effectiveBits: number, block: Bytes): Bytes {
	return rc2Bytes(rc2EncryptWords(rc2ExpandKey(key, effectiveBits), rc2Words(block)));
}

/** RC2-CBC decryption with `effectiveBits` as PKCS#12 spells it (128 or 40). */
export function rc2CbcDecrypt(key: Bytes, effectiveBits: number, iv: Bytes, data: Bytes): Bytes {
	const expanded = rc2ExpandKey(key, effectiveBits);
	return cbcDecrypt((block) => rc2Bytes(rc2DecryptWords(expanded, rc2Words(block))), iv, data);
}

/* ---------------------------------------------------------------------------------------------
 * RC4.
 * ------------------------------------------------------------------------------------------- */

export function rc4(key: Bytes, data: Bytes): Bytes {
	const state = new Uint8Array(256);
	for (let i = 0; i < 256; i++) state[i] = i;
	let j = 0;
	for (let i = 0; i < 256; i++) {
		j = (j + state[i] + key[i % key.length]) & 0xff;
		const swap = state[i];
		state[i] = state[j];
		state[j] = swap;
	}

	const out = new Uint8Array(data.length);
	let i = 0;
	j = 0;
	for (let n = 0; n < data.length; n++) {
		i = (i + 1) & 0xff;
		j = (j + state[i]) & 0xff;
		const swap = state[i];
		state[i] = state[j];
		state[j] = swap;
		out[n] = data[n] ^ state[(state[i] + state[j]) & 0xff];
	}
	return out;
}
