/**
 * PKCS#12 → WebCrypto signing session.
 *
 * The container is parsed in memory, the private key is imported as **non-extractable** and only
 * usable for signing, and each certificate of the file is paired with the key (if any) by
 * comparing public-key material. Nothing leaves the browser.
 */

import {
	type Bytes,
	type Der,
	derInteger,
	explicit,
	integerBytes,
	objectId,
	octetString,
	oid,
	parseDer,
	seq,
	tlv
} from './asn1';
import { Pkcs12Error, parsePkcs12 } from './pkcs12';
import type { SignerCert, SignerSession, SigningAlgorithm } from './sign';
import { type Certificate, type PublicKey, parseCertificate, samePublicKey } from './x509';

const OID_RSA = '1.2.840.113549.1.1.1';
const OID_EC_PUBLIC_KEY = '1.2.840.10045.2.1';

const CURVES: Record<string, SigningAlgorithm['namedCurve']> = {
	'1.2.840.10045.3.1.7': 'P-256',
	'1.3.132.0.34': 'P-384',
	'1.3.132.0.35': 'P-521'
};

/** SHA-256 is the hash the signature formats require; it is bound at key import. */
const SIGNING_HASH = 'SHA-256' as const;

/**
 * What the key bag held, normalized for WebCrypto. PKCS#12 bags are *supposed* to hold PKCS#8,
 * but Windows and older OpenSSL write the bare `RSAPrivateKey` (PKCS#1) or `ECPrivateKey` (SEC1)
 * instead — those files used to fail with "the file could not be read".
 */
interface PrivateKeyInfo {
	algorithm: SigningAlgorithm;
	publicKey: PublicKey;
	/** Always PKCS#8: bare PKCS#1/SEC1 keys are wrapped before they reach WebCrypto (Node has no
	 * `pkcs1` import format, and the browser one is not worth depending on). */
	der: Bytes;
}

/** PKCS#8 `PrivateKeyInfo` → the algorithm, its public half and how to import it. */
function fromPkcs8(der: Bytes, info: Der): PrivateKeyInfo {
	const algorithmOid = oid(info.children[1].children[0]);
	const key = info.children[2].value;

	if (algorithmOid === OID_RSA) {
		const rsa = parseDer(key);
		return {
			algorithm: { name: 'RSASSA-PKCS1-v1_5', hash: SIGNING_HASH },
			publicKey: {
				kind: 'rsa',
				modulus: integerBytes(rsa.children[1]),
				exponent: integerBytes(rsa.children[2])
			},
			der
		};
	}

	if (algorithmOid === OID_EC_PUBLIC_KEY) {
		const namedCurve = CURVES[oid(info.children[1].children[1])];
		if (!namedCurve) throw new Pkcs12Error('unsupported-key', 'unsupported elliptic curve');
		const ec = parseDer(key);
		const point = ec.children.find((child) => child.tag === 0xa1);
		if (!point) throw new Pkcs12Error('unsupported-key', 'the EC private key has no public point');
		return {
			algorithm: { name: 'ECDSA', hash: SIGNING_HASH, namedCurve },
			publicKey: { kind: 'ec', point: explicit(point, 0xa1).value.subarray(1) },
			der
		};
	}

	throw new Pkcs12Error('unsupported-key', `unsupported private key algorithm ${algorithmOid}`);
}

/**
 * A bare key as found in the bag. `children[1]` tells the three apart: SEQUENCE for PKCS#8's
 * AlgorithmIdentifier, INTEGER for PKCS#1's modulus, OCTET STRING for SEC1's private key.
 */
export function readPrivateKey(der: Bytes): PrivateKeyInfo {
	const info = parseDer(der);
	const second = info.children[1];

	if (second?.tag === 0x30) return fromPkcs8(der, info);

	if (second?.tag === 0x02) {
		// PKCS#1 RSAPrivateKey: version, n, e, …
		log('key bag holds a bare PKCS#1 RSAPrivateKey');
		return {
			algorithm: { name: 'RSASSA-PKCS1-v1_5', hash: SIGNING_HASH },
			publicKey: {
				kind: 'rsa',
				modulus: integerBytes(second),
				exponent: integerBytes(info.children[2])
			},
			der: pkcs8Envelope(seq(objectId(OID_RSA), tlv(0x05, new Uint8Array())), der)
		};
	}

	if (second?.tag === 0x04) {
		// SEC1 ECPrivateKey: version, privateKey, [0] curve, [1] public point. WebCrypto has no
		// SEC1 import, so it is wrapped in the PKCS#8 envelope the curve identifier belongs to.
		const parameters = info.children.find((child) => child.tag === 0xa0);
		const point = info.children.find((child) => child.tag === 0xa1);
		const namedCurve = parameters ? CURVES[oid(explicit(parameters, 0xa0))] : undefined;
		if (!namedCurve || !point) {
			throw new Pkcs12Error('unsupported-key', 'the EC private key is missing its curve or point');
		}
		const wrapped = pkcs8Envelope(
			seq(objectId(OID_EC_PUBLIC_KEY), objectId(curveOid(namedCurve))),
			der
		);
		log('key bag holds a bare SEC1 ECPrivateKey; wrapped in PKCS#8', namedCurve);
		const publicPoint = explicit(point, 0xa1).value.subarray(1);
		// Certificates carry the uncompressed point, so a compressed one here would never pair with
		// its certificate; say so in the log instead of failing with a bare key-mismatch.
		if (publicPoint[0] === 0x02 || publicPoint[0] === 0x03) {
			console.error(
				'[autofirme:keystore] the EC private key carries a compressed public point; it cannot be matched against a certificate'
			);
		}
		return {
			algorithm: { name: 'ECDSA', hash: SIGNING_HASH, namedCurve },
			publicKey: { kind: 'ec', point: publicPoint },
			der: wrapped
		};
	}

	throw new Pkcs12Error('unsupported-key', `unrecognised private key structure (tag ${second?.tag})`);
}

/** `PrivateKeyInfo` around a bare key: version, the algorithm identifier, the key octets. */
function pkcs8Envelope(algorithm: Bytes, key: Bytes): Bytes {
	return seq(derInteger(0), algorithm, octetString(key));
}

function curveOid(namedCurve: NonNullable<SigningAlgorithm['namedCurve']>): string {
	const entry = Object.entries(CURVES).find(([, name]) => name === namedCurve);
	if (!entry) throw new Pkcs12Error('unsupported-key', `unsupported elliptic curve ${namedCurve}`);
	return entry[0];
}

function toSignerCert(certificate: Certificate, algorithm: SigningAlgorithm | null, key: CryptoKey | null, sha512Key: CryptoKey | null, sha1Key: CryptoKey | null, now: Date, position: number): SignerCert {
	return {
		// Position-prefixed: serial numbers are only unique per issuer (RFC 5280), so one
		// container may hold two certificates with the same serial; the picker keys its
		// list by id and a keyed each throws on duplicates.
		id: `${position}:${certificate.serial}`,
		commonName: certificate.commonName,
		subject: certificate.subject,
		issuer: certificate.issuer,
		serial: certificate.serial,
		notBefore: certificate.notBefore,
		notAfter: certificate.notAfter,
		expired: certificate.notAfter.getTime() < now.getTime(),
		notYetValid: certificate.notBefore.getTime() > now.getTime(),
		der: certificate.der,
		key,
		sha512Key,
		sha1Key,
		algorithm
	};
}

export interface LoadOptions {
	/** Overrides "now" for validity checks — tests and expiry warnings. */
	now?: Date;
}

/** Parse a PKCS#12/PFX file and import its private key. Throws `Pkcs12Error` with a typed code. */
export async function loadSignerSession(
	bytes: Bytes,
	pin: string,
	options: LoadOptions = {}
): Promise<SignerSession> {
	log('loading container', { bytes: bytes.length, pin: pin.length });
	const { certificates, privateKey } = await parsePkcs12(bytes, pin);
	log('container parsed', { certificates: certificates.length, keyBytes: privateKey.length });
	const info = readPrivateKey(privateKey);
	log('key recognised', { algorithm: info.algorithm });
	const key = await crypto.subtle
		.importKey('pkcs8', info.der, info.algorithm, false, ['sign'])
		.catch((reason: unknown) => {
			console.error('[autofirme:keystore] importing the private key failed', {
				algorithm: info.algorithm,
				reason
			});
			throw new Pkcs12Error('unsupported-key', 'the browser cannot import this private key');
		});

	// Three-phase servers hand back the signed attributes and expect PKCS#1 v1.5 with the
	// hash named by the page (SHA-512 in Aragon's case). WebCrypto binds the hash to the
	// imported key, so keep a second non-extractable view instead of re-importing raw DER.
	const sha512Key = info.algorithm.name === 'RSASSA-PKCS1-v1_5'
		? await crypto.subtle
			.importKey('pkcs8', info.der, { name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-512' }, false, ['sign'])
			.catch(() => null)
		: null;
	// Older portals (CORES' compatibility test, for one) still ask for SHA1withRSA XAdES.
	// Same non-extractable key material, separate hash binding; absent in browsers that refuse.
	const sha1Key = info.algorithm.name === 'RSASSA-PKCS1-v1_5'
		? await crypto.subtle
			.importKey('pkcs8', info.der, { name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-1' }, false, ['sign'])
			.catch(() => null)
		: null;

	const parsed = certificates.map((der, position) => {
		try {
			return parseCertificate(der);
		} catch (reason) {
			console.error('[autofirme:keystore] certificate could not be parsed', { position, reason });
			throw reason;
		}
	});
	log('certificates parsed', parsed.map((certificate) => certificate.commonName));
	const owning = parsed.filter((certificate) => samePublicKey(certificate.publicKey, info.publicKey));
	if (owning.length === 0) {
		console.error('[autofirme:keystore] no certificate matches the private key', {
			certificates: parsed.map((certificate) => ({
				kind: certificate.publicKey.kind,
				algorithm: certificate.spkiAlgorithm,
				commonName: certificate.commonName
			})),
			key: { kind: info.publicKey.kind, algorithm: info.algorithm }
		});
		throw new Pkcs12Error(
			'key-mismatch',
			'the private key in this container belongs to none of its certificates'
		);
	}

	const now = options.now ?? new Date();
	const signerCerts = parsed.map((certificate, position) =>
		samePublicKey(certificate.publicKey, info.publicKey)
			? toSignerCert(certificate, info.algorithm, key, sha512Key, sha1Key, now, position)
			: toSignerCert(certificate, null, null, null, null, now, position)
	);
	const signer = signerCerts.find((certificate) => certificate.key !== null);
	if (!signer) throw new Pkcs12Error('key-mismatch', 'no signable certificate in this container');
	log('session ready', { signer: signer.commonName, chain: signerCerts.length });
	return { certificates: signerCerts, signer };
}

/** Diagnostics the user asked for: every step of the container → session path, prefixed. */
function log(message: string, details?: unknown): void {
	console.debug(`[autofirme:keystore] ${message}`, details ?? '');
}
