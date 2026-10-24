import type { Bytes } from './asn1';
import { softwareProvider, type KeyProvider } from './keys/provider';

/** Domain types for the sign flow. Signature engines land in #6–#11; this file is the seam. */

export type SignFormat = 'cades' | 'xades' | 'pades' | 'ooxml';

export interface SignFormatOption {
	id: SignFormat;
	label: string;
	extension: string;
	/** Issue that implements the format. */
	issue: number;
	/** False while the format is still a placeholder in the UI. */
	implemented: boolean;
}

export const SIGN_FORMATS: readonly SignFormatOption[] = [
	{ id: 'cades', label: 'CAdES-BES', extension: '.csig', issue: 6, implemented: true },
	{ id: 'xades', label: 'XAdES-EPES', extension: '.xsig', issue: 7, implemented: true },
	{ id: 'pades', label: 'PAdES-B-B', extension: '.pdf', issue: 11, implemented: true },
	// OOXML signs in place (the signed bytes are still a .docx/.xlsx), so there is no
	// appended extension; batch output keeps the original file name. Refs #127.
	{ id: 'ooxml', label: 'OOXML', extension: '', issue: 127, implemented: true }
];

export interface SelectedFile {
	name: string;
	size: number;
	type: string;
	/** The picked file itself — signing reads its bytes. */
	source: File;
}

/** WebCrypto parameters used for both importing and signing with a certificate's private key. */
export interface SigningAlgorithm {
	name: 'RSASSA-PKCS1-v1_5' | 'ECDSA';
	hash: 'SHA-1' | 'SHA-256' | 'SHA-384' | 'SHA-512';
	/** ECDSA only. */
	namedCurve?: 'P-256' | 'P-384' | 'P-521';
}

/** One certificate of a PKCS#12 container, ready for the picker and for the sign flow. */
export interface SignerCert {
	/** Unique inside the container (position + serial hex) — the picker's list key. */
	id: string;
	commonName: string;
	subject: string;
	issuer: string;
	serial: string;
	notBefore: Date;
	notAfter: Date;
	expired: boolean;
	notYetValid: boolean;
	/** X.509 DER, to be embedded in the signature. */
	der: Bytes;
	/** Non-extractable signing key; null for the certificates of the chain that are not the signer. */
	key: CryptoKey | null;
	/** RSA view imported with SHA-512, for the PK1 step of a three-phase signature. */
	sha512Key?: CryptoKey | null;
	/** Legacy portals still request SHA-1; WebCrypto binds the hash to the key. */
	sha1Key?: CryptoKey | null;
	algorithm: SigningAlgorithm | null;
	/**
	 * Key source that owns this certificate's key. Absent means the key already sits on
	 * `key` / `sha1Key` / `sha512Key` — the software backend (Refs #97).
	 */
	provider?: KeyProvider;
}

export interface SignerSession {
	certificates: SignerCert[];
	/** The certificate owning the private key — the one the user must select to sign. */
	signer: SignerCert;
}

/**
 * JCA name of the certificate's signing algorithm, e.g. `SHA256withRSA` — what a manifest reports
 * when it has no engine to ask. The pairing is fixed by the curve/RSA + hash WebCrypto was given.
 */
export function signingAlgorithmName(cert: SignerCert): string {
	if (!cert.algorithm) {
		throw new Error(`certificate ${cert.commonName} has no signing algorithm`);
	}
	const { name, hash } = cert.algorithm;
	return `${hash.replace('-', '')}with${name === 'ECDSA' ? 'ECDSA' : 'RSA'}`;
}

/** Moved onto the seam; re-exported so existing importers keep working. */
export { signingKey } from './keys/provider';

/** Sign raw bytes with the selected certificate's key. Format wrapping happens in #6–#11. */
export async function signBytes(
	cert: SignerCert,
	data: Bytes,
	hash: SigningAlgorithm['hash'] = cert.algorithm?.hash ?? 'SHA-256'
): Promise<Bytes> {
	return (cert.provider ?? softwareProvider).signDigest(cert, data, hash);
}

export function toSelectedFile(file: File): SelectedFile {
	return { name: file.name, size: file.size, type: file.type, source: file };
}

export function formatBytes(bytes: number): string {
	if (bytes < 1024) return `${bytes} B`;
	const units = ['kB', 'MB', 'GB'];
	let value = bytes / 1024;
	let unit = 0;
	while (value >= 1024 && unit < units.length - 1) {
		value /= 1024;
		unit += 1;
	}
	return `${value.toFixed(value < 10 ? 1 : 0)} ${units[unit]}`;
}
