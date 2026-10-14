/**
 * PKCS#12 → WebCrypto signing session.
 *
 * The container is parsed in memory, the private key is imported as **non-extractable** and only
 * usable for signing, and each certificate of the file is paired with the key (if any) by
 * comparing public-key material. Nothing leaves the browser.
 */

import { type Bytes, explicit, integerBytes, oid, parseDer } from './asn1';
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

interface PrivateKeyInfo {
	algorithm: SigningAlgorithm;
	publicKey: PublicKey;
}

function readPrivateKeyInfo(der: Bytes): PrivateKeyInfo {
	const info = parseDer(der);
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
			}
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
			publicKey: { kind: 'ec', point: explicit(point, 0xa1).value.subarray(1) }
		};
	}

	throw new Pkcs12Error('unsupported-key', `unsupported private key algorithm ${algorithmOid}`);
}

function toSignerCert(certificate: Certificate, algorithm: SigningAlgorithm | null, key: CryptoKey | null, now: Date): SignerCert {
	return {
		id: certificate.serial,
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
	const { certificates, privateKey } = await parsePkcs12(bytes, pin);
	const info = readPrivateKeyInfo(privateKey);
	const key = await crypto.subtle
		.importKey('pkcs8', privateKey, info.algorithm, false, ['sign'])
		.catch(() => {
			throw new Pkcs12Error('unsupported-key', 'the browser cannot import this private key');
		});

	const parsed = certificates.map(parseCertificate);
	if (!parsed.some((certificate) => samePublicKey(certificate.publicKey, info.publicKey))) {
		throw new Pkcs12Error(
			'key-mismatch',
			'the private key in this container belongs to none of its certificates'
		);
	}

	const now = options.now ?? new Date();
	const signerCerts = parsed.map((certificate) =>
		samePublicKey(certificate.publicKey, info.publicKey)
			? toSignerCert(certificate, info.algorithm, key, now)
			: toSignerCert(certificate, null, null, now)
	);
	const signer = signerCerts.find((certificate) => certificate.key !== null);
	if (!signer) throw new Pkcs12Error('key-mismatch', 'no signable certificate in this container');
	return { certificates: signerCerts, signer };
}
