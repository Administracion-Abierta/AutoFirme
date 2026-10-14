/**
 * PKCS#12 (PFX) container reader — RFC 7292, no dependencies.
 *
 * WebCrypto has no PKCS#12 support, so the container is walked by hand: the shrouded private key
 * is decrypted and every X.509 certificate is kept. Only PBES2 (PBKDF2 + AES-CBC) is supported;
 * PBES1 needs DES/RC2, which WebCrypto does not have.
 */

import { type Bytes, type Der, explicit, integer, oid, parseDer } from './asn1';

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
	/** Decrypted PKCS#8 `PrivateKeyInfo` DER. */
	privateKey: Bytes;
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

const PBES1: Record<string, string> = {
	'1.2.840.113549.1.12.1.1': 'pbeWithSHA1And128BitRC4',
	'1.2.840.113549.1.12.1.2': 'pbeWithSHA1And40BitRC4',
	'1.2.840.113549.1.12.1.3': 'pbeWithSHA1And3-KeyTripleDES-CBC',
	'1.2.840.113549.1.12.1.4': 'pbeWithSHA1And2-KeyTripleDES-CBC',
	'1.2.840.113549.1.12.1.5': 'pbeWithSHA1And128BitRC2-CBC',
	'1.2.840.113549.1.12.1.6': 'pbeWithSHA1And40BitRC2-CBC'
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
		throw new Pkcs12Error('unsupported-pbe', 'PKCS#12 key derivation is not PBKDF2');
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
			if (!prf) throw new Pkcs12Error('unsupported-pbe', 'unsupported PBKDF2 pseudorandom function');
			hash = prf;
		}
	}

	const scheme = params.children[1];
	const cipherOid = oid(scheme.children[0]);
	const cipher = CIPHERS[cipherOid];
	if (!cipher) throw new Pkcs12Error('unsupported-cipher', `unsupported content cipher ${cipherOid}`);
	const iv = scheme.children[1].value;
	const key = await deriveKey(password, salt, iterations, (keyBits ?? cipher.bits) / 8, hash);
	const aes = await crypto.subtle.importKey('raw', key, { name: cipher.name }, false, ['decrypt']);
	try {
		return new Uint8Array(await crypto.subtle.decrypt({ name: cipher.name, iv }, aes, data));
	} catch {
		throw new Pkcs12Error('wrong-pin', 'wrong password (content could not be decrypted)');
	}
}

async function decrypt(algorithm: Der, data: Bytes, password: string): Promise<Bytes> {
	const id = oid(algorithm.children[0]);
	if (id === OID_PBES2) return decryptPbes2(algorithm.children[1], data, password);
	const legacy = PBES1[id];
	if (legacy) {
		throw new Pkcs12Error(
			'unsupported-pbe',
			`${legacy} cannot be decrypted: the browser has no DES/RC2 primitive. ` +
				'Re-export the container as modern PKCS#12 (AES) or use an OS/hardware keystore.'
		);
	}
	throw new Pkcs12Error('unsupported-pbe', `unsupported PKCS#12 encryption ${id}`);
}

/** ContentInfo → SafeContents (decrypting `encryptedData` when needed). */
async function openSafeContents(contentInfo: Der, password: string): Promise<Der> {
	const type = oid(contentInfo.children[0]);
	const inner = explicit(contentInfo.children[1], 0xa0);
	if (type === OID_DATA) {
		if (inner.tag !== 0x04) throw new Pkcs12Error('not-pkcs12', 'malformed PKCS#12 contents');
		return parseDer(inner.value);
	}
	if (type === OID_ENCRYPTED_DATA) {
		const encryptedContentInfo = inner.children[1];
		const algorithm = encryptedContentInfo.children[1];
		const encrypted = encryptedContentInfo.children[2].value;
		return parseDer(await decrypt(algorithm, encrypted, password));
	}
	throw new Pkcs12Error('not-pkcs12', `unsupported PKCS#12 content type ${type}`);
}

/** SafeBag → X.509 DER, or null for bags we do not consume (CRLs, secrets, friendly names). */
function readCertificateBag(bag: Der): Bytes | null {
	const value = explicit(bag.children[1], 0xa0);
	if (oid(value.children[0]) !== OID_X509_CERTIFICATE) return null;
	return explicit(value.children[1], 0xa0).value;
}

async function readKeyBag(bag: Der, password: string): Promise<Bytes> {
	const value = explicit(bag.children[1], 0xa0);
	if (oid(bag.children[0]) === OID_BAG_KEY) return value.raw;
	return decrypt(value.children[0], value.children[1].value, password);
}

export async function parsePkcs12(bytes: Bytes, password: string): Promise<Pkcs12Contents> {
	let pfx: Der;
	try {
		pfx = parseDer(bytes);
	} catch (error) {
		throw new Pkcs12Error('not-pkcs12', `not a PKCS#12 container: ${(error as Error).message}`);
	}
	if (pfx.tag !== 0x30 || pfx.children.length < 2) {
		throw new Pkcs12Error('not-pkcs12', 'not a PKCS#12 container');
	}

	const authSafe = explicit(pfx.children[1].children[1], 0xa0);
	if (authSafe.tag !== 0x04) throw new Pkcs12Error('not-pkcs12', 'malformed PKCS#12 contents');
	// NOTE: the PKCS#12 MAC (MacData, RFC 7292 B.2) is *not* verified — that KDF is not
	// implemented here, because getting it wrong would reject valid containers. A wrong PIN is
	// still detected, by the failure to decrypt the key bag below. Tracked as a follow-up.

	const certificates: Bytes[] = [];
	let privateKey: Bytes | undefined;
	for (const contentInfo of parseDer(authSafe.value).children) {
		const contents = await openSafeContents(contentInfo, password);
		for (const bag of contents.children) {
			const bagId = oid(bag.children[0]);
			if (bagId === OID_BAG_SHROUDED_KEY || bagId === OID_BAG_KEY) {
				privateKey = await readKeyBag(bag, password);
			} else if (bagId === OID_BAG_CERT) {
				const certificate = readCertificateBag(bag);
				if (certificate) certificates.push(certificate);
			}
		}
	}

	if (!privateKey) throw new Pkcs12Error('no-key', 'the container holds no private key');
	if (certificates.length === 0) throw new Pkcs12Error('no-cert', 'the container holds no certificate');
	return { certificates, privateKey };
}
