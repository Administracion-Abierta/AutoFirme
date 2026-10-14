/** Domain types for the sign flow. Signature engines land in #5–#11; this file is the seam. */

export type SignFormat = 'cades' | 'xades' | 'pades';

export interface SignFormatOption {
	id: SignFormat;
	label: string;
	extension: string;
	/** Issue that implements the format. */
	issue: number;
}

export const SIGN_FORMATS: readonly SignFormatOption[] = [
	{ id: 'cades', label: 'CAdES-BES', extension: '.csig', issue: 6 },
	{ id: 'xades', label: 'XAdES-EPES', extension: '.xsig', issue: 7 },
	{ id: 'pades', label: 'PAdES-B-B', extension: '.pdf', issue: 11 }
];

export interface SelectedFile {
	name: string;
	size: number;
	type: string;
}

/** Filled in by #5 (PKCS#12 / certificate selection). */
export interface SignerCert {
	subject: string;
	issuer: string;
	notAfter: string;
}

export interface SignResult {
	ok: boolean;
	message: string;
}

export function toSelectedFile(file: File): SelectedFile {
	return { name: file.name, size: file.size, type: file.type };
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
