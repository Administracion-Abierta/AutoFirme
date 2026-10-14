import type { Bytes } from './asn1';

/** Domain types for the sign flow. Signature engines land in #6–#11; this file is the seam. */

export type SignFormat = 'cades' | 'xades' | 'pades';

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
	{ id: 'pades', label: 'PAdES-B-B', extension: '.pdf', issue: 11, implemented: false }
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
	hash: 'SHA-256' | 'SHA-384' | 'SHA-512';
	/** ECDSA only. */
	namedCurve?: 'P-256' | 'P-384' | 'P-521';
}

/** One certificate of a PKCS#12 container, ready for the picker and for the sign flow. */
export interface SignerCert {
	/** Certificate serial number, hex — stable identifier inside the container. */
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
	algorithm: SigningAlgorithm | null;
}

export interface SignerSession {
	certificates: SignerCert[];
	/** The certificate owning the private key — the one the user must select to sign. */
	signer: SignerCert;
}

export interface SignResult {
	ok: boolean;
	message: string;
}

/** Sign raw bytes with the selected certificate's key. Format wrapping happens in #6–#11. */
export async function signBytes(cert: SignerCert, data: Bytes): Promise<Bytes> {
	if (!cert.key || !cert.algorithm) {
		throw new Error(`certificate ${cert.commonName} has no private key`);
	}
	return new Uint8Array(await crypto.subtle.sign(cert.algorithm, cert.key, data));
}

export function toSelectedFile(file: File): SelectedFile {
	return { name: file.name, size: file.size, type: file.type, source: file };
}

/** Signed output for the format, as a download — nothing leaves the page. */
export function download(name: string, data: string | Bytes, type: string): void {
	const url = URL.createObjectURL(new Blob([data], { type }));
	const link = document.createElement('a');
	link.href = url;
	link.download = name;
	link.click();
	URL.revokeObjectURL(url);
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
