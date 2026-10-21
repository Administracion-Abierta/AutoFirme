/**
 * PKCS#12 (PFX) container reader — RFC 7292, no dependencies.
 *
 * WebCrypto has no PKCS#12 support, so the container is walked by hand: the shrouded private key
 * is decrypted and every X.509 certificate is kept. Modern containers use PBES2 (PBKDF2 + AES);
 * what older Windows/FNMT tools write uses PBES1 (the SHA-1 key derivation of Appendix B.2 plus
 * DES/RC2/RC4), whose primitives live in `./legacy-pbe`. The container MAC (Appendix B.3) is
 * verified when present, so a wrong PIN fails fast instead of decrypting to garbage.
 *
 * This module is a parser only. The seam treats it as the container backend: `parsePkcs12`'s
 * output is what `src/lib/keys/pkcs12.ts` adapts to a `KeyProvider` (Refs #97).
 */

import { type Bytes, type Der, concatBytes, explicit, integer, oid, parseDer, readTlv } from './asn1';
import {
	HASH_PARAMS,
	type LegacyHash,
	pkcs12Kdf,
	rc2CbcDecrypt,
	rc4,
	tripleDesCbcDecrypt,
	unpadPkcs7
} from './legacy-pbe';

export type Pkcs12ErrorCode =
	| 'not-pkcs12'
	| 'wrong-pin'
	| 'unsupported-pbe'
	| 'unsupported-cipher'
	| 'unsupported-key'
	| 'no-cert'
	| 'no-key'
	| 'key-mismatch';

export class Pkcs12Error extends Error {
	readonly code: Pkcs12ErrorCode;

	constructor(code: Pkcs12ErrorCode, message: string) {
		super(message);
		this.name = 'Pkcs12Error';
		this.code = code;
	}
}

export interface Pkcs12Contents {
	/** X.509 DER of every certificate in the container. */
	certificates: Bytes[];
	/**
	 * Decrypted key material: PKCS#8 `PrivateKeyInfo` DER when the bag holds PKCS#8, otherwise
	 * the raw contents of the bag (PKCS#1 RSAPrivateKey or SEC1 ECPrivateKey, as the writer
	 * stored it). `keystore.ts` sniffs which shape it is.
	 */
	privateKey: Bytes;
}

const LOG_PREFIX = '[autofirme:pkcs12]';

function debug(message: string): void {
	console.debug(`${LOG_PREFIX} ${message}`);
}

/** Every throw goes through here so the code and message reach the console before the stack unwinds. */
function fail(code: Pkcs12ErrorCode, message: string): Pkcs12Error {
	console.error(`${LOG_PREFIX} ${code}: ${message}`);
	return new Pkcs12Error(code, message);
}

export type HashName = 'SHA-1' | 'SHA-256' | 'SHA-384' | 'SHA-512';

const PBKDF2_PRF: Record<string, HashName> = {
	'1.2.840.113549.2.7': 'SHA-1',
	'1.2.840.113549.2.9': 'SHA-256',
	'1.2.840.113549.2.10': 'SHA-384',
	'1.2.840.113549.2.11': 'SHA-512'
};

const CIPHERS: Record<string, { name: string; bits: number }> = {
	'2.16.840.1.101.3.4.1.2': { name: 'AES-CBC', bits: 128 },
	'2.16.840.1.101.3.4.1.22': { name: 'AES-CBC', bits: 192 },
	'2.16.840.1.101.3.4.1.42': { name: 'AES-CBC', bits: 256 }
};

interface Pbes1Scheme {
	name: string;
	cipher: '3des' | 'rc2' | 'rc4';
	keyLength: number;
	/** RC2 only: the effective key bits, which set both expansion and search space. */
	effectiveBits?: number;
}

/** RFC 7292 A.3/A.4 — the PBES1 schemes, each a SHA-1 KDF over a fixed-size key (and IV). */
const PBES1: Record<string, Pbes1Scheme> = {
	'1.2.840.113549.1.12.1.1': { name: 'pbeWithSHA1And128BitRC4', cipher: 'rc4', keyLength: 16 },
	'1.2.840.113549.1.12.1.2': { name: 'pbeWithSHA1And40BitRC4', cipher: 'rc4', keyLength: 5 },
	'1.2.840.113549.1.12.1.3': { name: 'pbeWithSHA1And3-KeyTripleDES-CBC', cipher: '3des', keyLength: 24 },
	'1.2.840.113549.1.12.1.4': { name: 'pbeWithSHA1And2-KeyTripleDES-CBC', cipher: '3des', keyLength: 16 },
	'1.2.840.113549.1.12.1.5': { name: 'pbeWithSHA1And128BitRC2-CBC', cipher: 'rc2', keyLength: 16, effectiveBits: 128 },
	'1.2.840.113549.1.12.1.6': { name: 'pbeWithSHA1And40BitRC2-CBC', cipher: 'rc2', keyLength: 5, effectiveBits: 40 }
};

/** DigestInfo algorithms accepted for the container MAC — the same set the KDF can hash with. */
const MAC_HASHES: Record<string, LegacyHash | undefined> = {
	'1.3.14.3.2.26': 'SHA-1',
	'2.16.840.1.101.3.4.2.1': 'SHA-256',
	'2.16.840.1.101.3.4.2.2': 'SHA-384',
	'2.16.840.1.101.3.4.2.3': 'SHA-512'
};

const OID_DATA = '1.2.840.113549.1.7.1';
const OID_ENCRYPTED_DATA = '1.2.840.113549.1.7.6';
const OID_BAG_KEY = '1.2.840.113549.1.12.10.1.1';
const OID_BAG_SHROUDED_KEY = '1.2.840.113549.1.12.10.1.2';
const OID_BAG_CERT = '1.2.840.113549.1.12.10.1.3';
const OID_X509_CERTIFICATE = '1.2.840.113549.1.9.22.1';
const OID_PBES2 = '1.2.840.113549.1.5.13';
const OID_PBKDF2 = '1.2.840.113549.1.5.12';

async function deriveKey(
	password: string,
	salt: Bytes,
	iterations: number,
	length: number,
	hash: HashName
): Promise<Bytes> {
	const base = await crypto.subtle.importKey(
		'raw',
		new TextEncoder().encode(password),
		'PBKDF2',
		false,
		['deriveBits']
	);
	const bits = await crypto.subtle.deriveBits(
		{ name: 'PBKDF2', salt, iterations, hash },
		base,
		length * 8
	);
	return new Uint8Array(bits);
}

async function decryptPbes2(params: Der, data: Bytes, password: string): Promise<Bytes> {
	const kdf = params.children[0];
	if (oid(kdf.children[0]) !== OID_PBKDF2) {
		throw fail('unsupported-pbe', 'PKCS#12 key derivation is not PBKDF2');
	}
	const kdfParams = kdf.children[1];
	const salt = kdfParams.children[0].value;
	const iterations = integer(kdfParams.children[1]);
	let keyBits: number | undefined;
	let hash: HashName = 'SHA-1';
	for (const param of kdfParams.children.slice(2)) {
		if (param.tag === 0x02) keyBits = integer(param);
		else if (param.tag === 0x30) {
			const prf = PBKDF2_PRF[oid(param.children[0])];
			if (!prf) throw fail('unsupported-pbe', 'unsupported PBKDF2 pseudorandom function');
			hash = prf;
		}
	}

	const scheme = params.children[1];
	const cipherOid = oid(scheme.children[0]);
	const cipher = CIPHERS[cipherOid];
	if (!cipher) throw fail('unsupported-cipher', `unsupported content cipher ${cipherOid}`);
	debug(`PBES2 with ${cipher.name}-${cipher.bits} (${cipherOid}), ${hash}, ${iterations} iterations`);
	const iv = scheme.children[1].value;
	const key = await deriveKey(password, salt, iterations, (keyBits ?? cipher.bits) / 8, hash);
	const aes = await crypto.subtle.importKey('raw', key, { name: cipher.name }, false, ['decrypt']);
	try {
		return new Uint8Array(await crypto.subtle.decrypt({ name: cipher.name, iv }, aes, data));
	} catch {
		throw fail('wrong-pin', 'wrong password (content could not be decrypted)');
	}
}

async function decryptPbes1(
	scheme: Pbes1Scheme,
	params: Der,
	data: Bytes,
	password: string
): Promise<Bytes> {
	const salt = params.children[0].value;
	const iterations = integer(params.children[1]);
	const key = await pkcs12Kdf(password, salt, iterations, 1, scheme.keyLength, 'SHA-1');

	if (scheme.cipher === 'rc4') return rc4(key, data);

	const iv = await pkcs12Kdf(password, salt, iterations, 2, 8, 'SHA-1');
	const plain =
		scheme.cipher === '3des'
			? tripleDesCbcDecrypt(key, iv, data)
			: rc2CbcDecrypt(key, scheme.effectiveBits!, iv, data);
	const unpadded = unpadPkcs7(plain);
	if (!unpadded) throw fail('wrong-pin', `${scheme.name}: padding is wrong (wrong password)`);
	return unpadded;
}

async function decrypt(algorithm: Der, data: Bytes, password: string): Promise<Bytes> {
	const id = oid(algorithm.children[0]);
	if (id === OID_PBES2) return decryptPbes2(algorithm.children[1], data, password);
	const scheme = PBES1[id];
	if (scheme) {
		debug(`PBES1 ${scheme.name} (${id}), ${scheme.keyLength}-byte key`);
		return decryptPbes1(scheme, algorithm.children[1], data, password);
	}
	throw fail('unsupported-pbe', `unsupported PKCS#12 encryption ${id}`);
}

/**
 * RFC 7292 B.3 — HMAC over the authSafe contents, keyed through the same KDF (id 3). A mismatch
 * means the password is wrong (or the file was tampered with); absent MacData is not an error.
 */
async function verifyMac(macData: Der, password: string, contents: Bytes): Promise<void> {
	const digestInfo = macData.children[0];
	const hashOid = oid(digestInfo.children[0].children[0]);
	const hash = MAC_HASHES[hashOid];
	if (!hash) {
		debug(`MacData digest ${hashOid} is not supported: MAC not verified`);
		return;
	}
	const salt = macData.children[1].value;
	const iterations = macData.children.length > 2 ? integer(macData.children[2]) : 1;
	debug(`MacData present: ${hash}, ${iterations} iterations`);
	const key = await pkcs12Kdf(password, salt, iterations, 3, HASH_PARAMS[hash].u, hash);
	const hmac = await crypto.subtle.importKey('raw', key, { name: 'HMAC', hash }, false, ['sign']);
	const actual = new Uint8Array(await crypto.subtle.sign('HMAC', hmac, contents));
	const expected = digestInfo.children[1].value;
	const matches =
		actual.length === expected.length && actual.every((byte, index) => byte === expected[index]);
	if (!matches) throw fail('wrong-pin', `${hash} MAC mismatch (wrong password or altered file)`);
	debug('MacData verified');
}

/** ContentInfo → SafeContents (decrypting `encryptedData` when needed). */
async function openSafeContents(contentInfo: Der, password: string): Promise<Der> {
	const type = oid(contentInfo.children[0]);
	debug(`ContentInfo ${type}`);
	const inner = explicit(contentInfo.children[1], 0xa0);
	if (type === OID_DATA) {
		if (inner.tag !== 0x04) throw fail('not-pkcs12', 'malformed PKCS#12 contents');
		return parseDer(inner.value);
	}
	if (type === OID_ENCRYPTED_DATA) {
		const encryptedContentInfo = inner.children[1];
		const algorithm = encryptedContentInfo.children[1];
		// BER writers chunk the implicit `[0]` OCTET STRING of `encryptedContent`: the ciphertext is
		// the concatenation of the chunks, not the chunks themselves (the parser flattens the
		// universal `24 80` form already, this is the context-tagged one).
		const content = encryptedContentInfo.children[2];
		const encrypted = content.constructed
			? concatBytes(content.children.map((child) => child.value))
			: content.value;
		return parseDer(await decrypt(algorithm, encrypted, password));
	}
	throw fail('not-pkcs12', `unsupported PKCS#12 content type ${type}`);
}

/** SafeBag → X.509 DER, or null for bags we do not consume (CRLs, secrets, friendly names). */
function readCertificateBag(bag: Der): Bytes | null {
	const value = explicit(bag.children[1], 0xa0);
	if (oid(value.children[0]) !== OID_X509_CERTIFICATE) return null;
	return explicit(value.children[1], 0xa0).value;
}

async function readKeyBag(bag: Der, password: string): Promise<Bytes> {
	const value = explicit(bag.children[1], 0xa0);
	if (oid(bag.children[0]) === OID_BAG_KEY) {
		debug(`unencrypted key bag: ${value.raw.length} bytes of key DER`);
		return value.raw;
	}
	const key = await decrypt(value.children[0], value.children[1].value, password);
	debug(`key bag decrypted: ${key.length} bytes of key DER`);
	return key;
}

export async function parsePkcs12(bytes: Bytes, password: string): Promise<Pkcs12Contents> {
	let pfx: Der;
	let end = 0;
	try {
		// Only the first TLV is read: real-world files are padded with NULs or whitespace.
		[pfx, end] = readTlv(bytes, 0);
	} catch (error) {
		throw fail('not-pkcs12', `not a PKCS#12 container: ${(error as Error).message}`);
	}
	if (end < bytes.length) {
		debug(`ignoring ${bytes.length - end} trailing byte(s) after the container`);
	}
	if (pfx.tag !== 0x30 || pfx.children.length < 2) {
		throw fail('not-pkcs12', 'not a PKCS#12 container');
	}
	debug(
		`container: tag 0x${pfx.tag.toString(16)}, ${pfx.raw.length} bytes, ` +
			`version ${integer(pfx.children[0])}`
	);

	const authSafe = explicit(pfx.children[1].children[1], 0xa0);
	debug(`authSafe ContentInfo ${oid(pfx.children[1].children[0])}`);
	if (authSafe.tag !== 0x04) throw fail('not-pkcs12', 'malformed PKCS#12 contents');
	if (pfx.children.length > 2) await verifyMac(pfx.children[2], password, authSafe.value);
	else debug('MacData absent: MAC not verified');

	const certificates: Bytes[] = [];
	let privateKey: Bytes | undefined;
	for (const contentInfo of parseDer(authSafe.value).children) {
		const contents = await openSafeContents(contentInfo, password);
		for (const bag of contents.children) {
			const bagId = oid(bag.children[0]);
			debug(`SafeBag ${bagId}`);
			if (bagId === OID_BAG_SHROUDED_KEY || bagId === OID_BAG_KEY) {
				privateKey = await readKeyBag(bag, password);
			} else if (bagId === OID_BAG_CERT) {
				const certificate = readCertificateBag(bag);
				if (certificate) certificates.push(certificate);
			}
		}
	}

	debug(`${certificates.length} certificate(s), private key ${privateKey ? 'present' : 'missing'}`);
	if (!privateKey) throw fail('no-key', 'the container holds no private key');
	if (certificates.length === 0) throw fail('no-cert', 'the container holds no certificate');
	return { certificates, privateKey };
}
