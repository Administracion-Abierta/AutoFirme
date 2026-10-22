import type { Bytes } from '../asn1';
import type { SignerCert, SigningAlgorithm } from '../sign';

/**
 * One interface for every key source — `.p12` files today, cards, tokens and OS
 * stores in the child issues — so the engine never learns where a key lives.
 *
 * Refs #97 (parent phase #20 tracks; this issue is the unit of work).
 */

/**
 * Which backend owns the key. This union is the kind contract: every provider's
 * `metadata.kind` is checked against it, so a new backend cannot invent a kind silently.
 */
export type ProviderKind = 'pkcs12' | 'software';

/** Static description of a provider; shown in the picker and logs, never branched on in `sign.ts`. */
export interface KeyProviderMetadata {
	kind: ProviderKind;
	/** Stable id for logs/diagnostics, e.g. `pkcs12`. */
	id: string;
	/** Human label for the picker, e.g. `PKCS#12 file`. */
	label: string;
}

/**
 * Key source seam. `list`/`getCert` expose certificates as metadata-only views;
 * `signDigest` is the only path to a signature.
 *
 * The bytes handed to `signDigest` are the ones the backend signs: the software
 * backend forwards them to WebCrypto, which applies `hash` internally, so for it
 * they are document bytes and it is the provider — not the caller — that hashes.
 * A backend that must instead be given a precomputed digest (cards, tokens, OS
 * stores) hashes at its own boundary before calling into its hardware.
 */
export interface KeyProvider {
	metadata: KeyProviderMetadata;
	/** Every certificate the source currently offers. */
	list(): Promise<readonly SignerCert[]>;
	/** One certificate by its session-unique `id`, or null when unknown. */
	getCert(id: string): Promise<SignerCert | null>;
	/**
	 * Sign `data` with the key behind `ref`. The hash is explicit (WebCrypto binds
	 * it at import); the default follows the certificate.
	 */
	signDigest(ref: SignerCert, data: Bytes, hash?: SigningAlgorithm['hash']): Promise<Bytes>;
}

/**
 * The non-extractable key view bound to `hash`, or null when the certificate has none.
 * RSA keys are imported once per hash because WebCrypto binds the hash at import time.
 */
export function signingKey(cert: SignerCert, hash: SigningAlgorithm['hash']): CryptoKey | null {
	if (!cert.algorithm) return null;
	if (hash === cert.algorithm.hash) return cert.key;
	if (hash === 'SHA-1') return cert.sha1Key ?? null;
	if (hash === 'SHA-512') return cert.sha512Key ?? null;
	return null;
}

/** Shared software backend: the exact WebCrypto sign body `signBytes` always used. */
export async function softwareSignDigest(
	cert: SignerCert,
	data: Bytes,
	hash: SigningAlgorithm['hash'] = cert.algorithm?.hash ?? 'SHA-256'
): Promise<Bytes> {
	const key = signingKey(cert, hash);
	if (!key || !cert.algorithm) {
		throw new Error(`certificate ${cert.commonName} has no private key`);
	}
	const algorithm: AlgorithmIdentifier | EcdsaParams =
		cert.algorithm.name === 'ECDSA' ? { name: 'ECDSA', hash } : { name: 'RSASSA-PKCS1-v1_5' };
	return new Uint8Array(await crypto.subtle.sign(algorithm, key, data));
}

/**
 * Fallback for certificates that already carry their key: holds no key source of its own,
 * so `list` is empty and `getCert` always misses. Session-bound sources (the PKCS#12
 * adapter here, cards and OS stores later) answer those two.
 */
export const softwareProvider: KeyProvider = {
	metadata: { kind: 'software', id: 'software', label: 'Software key' },
	async list(): Promise<readonly SignerCert[]> {
		return [];
	},
	async getCert(): Promise<SignerCert | null> {
		return null;
	},
	signDigest: softwareSignDigest
};
