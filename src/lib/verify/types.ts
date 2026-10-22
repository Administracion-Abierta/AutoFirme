/** Report shape shared by the CAdES/XAdES verifiers and the report UI. */

export type ArtifactFormat = 'cades' | 'xades' | 'pdf' | 'unknown';

/**
 * Byte buffers are typed as `ArrayBuffer`-backed so they can be handed straight to
 * `crypto.subtle` (which rejects the `SharedArrayBuffer`-capable default of `Uint8Array`).
 */
export type Bytes = Uint8Array<ArrayBuffer>;

/**
 * `pass` → green, `warn` → amber (the check failed a policy, not the cryptography: an expired
 * signer certificate, #40), `fail` → red, `info` → neutral (not verifiable / not applicable,
 * reason attached).
 */
export type CheckStatus = 'pass' | 'warn' | 'fail' | 'info';

export type CheckId =
	| 'structure'
	| 'contentDigest'
	| 'signature'
	| 'reference'
	| 'signerCert'
	| 'certValidity'
	| 'certChain'
	| 'signingTime'
	| 'timestamp'
	/** The artifact is recognised but this runtime cannot inspect it. */
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

/**
 * Overall verdict. `valid-but-expired` is the AutoFirma behaviour for a signature whose signer
 * certificate is outside its validity window: the cryptography holds, the certificate does not.
 */
export type VerificationStatus = 'valid' | 'valid-but-expired' | 'invalid';

export interface VerificationReport {
	format: ArtifactFormat;
	/** True when every check is `pass` (an `info` or `warn` check never flips this). */
	ok: boolean;
	/** `ok` in words — `valid-but-expired` warns without failing. Always agrees with `ok`. */
	status: VerificationStatus;
	checks: Check[];
	signer: Signer | null;
	signingTime: string | null;
}

/** Fold the checks into the report's verdict: a `warn` check never invalidates a signature. */
export function summarize(checks: Check[], expired: boolean): { status: VerificationStatus; ok: boolean } {
	if (checks.some((entry) => entry.status === 'fail')) return { status: 'invalid', ok: false };
	return expired ? { status: 'valid-but-expired', ok: true } : { status: 'valid', ok: true };
}

export interface VerifyOptions {
	/** Original document for detached signatures. */
	detached?: Bytes | null;
	/** PEM trust anchors; defaults to the bundled root store. */
	trustedRoots?: string[] | null;
	/** Clock override, mainly for certificate-validity tests. */
	now?: Date;
}
