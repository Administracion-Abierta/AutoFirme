/** Report shape shared by the CAdES/XAdES verifiers and the report UI. */

export type ArtifactFormat = 'cades' | 'xades' | 'pdf' | 'unknown';

/**
 * Byte buffers are typed as `ArrayBuffer`-backed so they can be handed straight to
 * `crypto.subtle` (which rejects the `SharedArrayBuffer`-capable default of `Uint8Array`).
 */
export type Bytes = Uint8Array<ArrayBuffer>;

/** `pass` → green, `fail` → red, `info` → neutral (not verifiable / not applicable, reason attached). */
export type CheckStatus = 'pass' | 'fail' | 'info';

export type CheckId =
	| 'structure'
	| 'contentDigest'
	| 'signature'
	| 'reference'
	| 'signerCert'
	| 'certValidity'
	| 'certChain'
	| 'signingTime'
	| 'unsupported';

export interface Check {
	id: CheckId;
	status: CheckStatus;
	/** Technical facts — not translated: algorithms, DNs, timestamps, digests. */
	detail: string;
}

export interface Signer {
	subject: string;
	issuer: string;
	serial: string;
	notBefore: string;
	notAfter: string;
	certificates: number;
}

export interface VerificationReport {
	format: ArtifactFormat;
	/** True when every check is `pass` (an `info` check never flips this). */
	ok: boolean;
	checks: Check[];
	signer: Signer | null;
	signingTime: string | null;
}

export interface VerifyOptions {
	/** Original document for detached signatures. */
	detached?: Bytes | null;
	/** PEM trust anchors; defaults to the bundled root store. */
	trustedRoots?: string[] | null;
	/** Clock override, mainly for certificate-validity tests. */
	now?: Date;
}
