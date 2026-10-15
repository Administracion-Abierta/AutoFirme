/**
 * The three tools, as plain async functions.
 *
 * Everything an MCP client can reach goes through here, so the transport layer stays a thin
 * adapter and the tests can call the real thing without a JSON-RPC round trip.
 */

import { signCadesDetached } from '../../src/lib/cms.ts';
import { detectFormat, verifyArtifact } from '../../src/lib/verify/index.ts';
import type { VerificationReport } from '../../src/lib/verify/types.ts';
import { audit } from './audit';
import type { CertStore, CertSummary } from './certstore';
import type { ServerConfig } from './config';
import { type Bytes, ToolError, readSource, type Source } from './input';

export type SignFormat = 'cades' | 'xades' | 'pades';

/** Formats this slice can actually produce, and the honest reason for the rest. */
const FORMAT_SUPPORT: Record<SignFormat, string | null> = {
	cades: null,
	xades: 'XAdES needs a DOM (DOMParser/XMLSerializer); this stdio build has none — Refs #28',
	pades: 'PAdES is not wired into this server yet; the engine can do it — Refs #28'
};

export interface SignInput extends Source {
	format: SignFormat;
	cert_id: string;
	/** ISO 8601; defaults to now. Deterministic bytes make signatures reproducible in tests. */
	signing_time?: string;
}

export interface SignResult {
	format: SignFormat;
	cert_id: string;
	common_name: string;
	/** CAdES-BES here is *detached*: verification needs the original document again. */
	detached: boolean;
	signed_b64: string;
	bytes: number;
	sha256: string;
	document_sha256: string;
	signing_time: string;
	/** True when the certificate was outside its validity window — the signature is still made. */
	cert_expired: boolean;
}

async function sha256Hex(bytes: Bytes): Promise<string> {
	const digest = new Uint8Array(await crypto.subtle.digest('SHA-256', bytes));
	return [...digest].map((byte) => byte.toString(16).padStart(2, '0')).join('');
}

/** Node has no DOM, and the engine's XAdES/PDF paths need one. Say so instead of guessing. */
function domUnavailable(): boolean {
	return typeof globalThis.DOMParser === 'undefined';
}

export async function signDocument(
	config: ServerConfig,
	store: CertStore,
	input: SignInput
): Promise<SignResult> {
	const unsupported = FORMAT_SUPPORT[input.format];
	if (unsupported) {
		audit(config, { event: 'sign', ok: false, format: input.format, certId: input.cert_id, error: 'unsupported_format' });
		throw new ToolError('unsupported_format', unsupported);
	}
	const signingTime = input.signing_time ? new Date(input.signing_time) : new Date();
	if (Number.isNaN(signingTime.getTime())) {
		throw new ToolError('bad_signing_time', `signing_time ${JSON.stringify(input.signing_time)} is not a date`);
	}

	const document = await readSource(config, input, 'document');
	const cert = store.signer(input.cert_id);
	const signed = await signCadesDetached(cert, document, { signingTime });
	const result: SignResult = {
		format: 'cades',
		cert_id: input.cert_id,
		common_name: cert.commonName,
		detached: true,
		signed_b64: Buffer.from(signed).toString('base64'),
		bytes: signed.length,
		sha256: await sha256Hex(signed),
		document_sha256: await sha256Hex(document),
		signing_time: signingTime.toISOString(),
		cert_expired: cert.expired
	};
	audit(config, {
		event: 'sign',
		ok: true,
		certId: input.cert_id,
		format: 'cades',
		documentSha256: result.document_sha256,
		bytes: result.bytes
	});
	return result;
}

export interface ListCertsResult {
	certificates: CertSummary[];
	/** Containers that failed to load and why. Empty when everything opened. */
	errors: string[];
	signing_ready: boolean;
}

export function listCerts(config: ServerConfig, store: CertStore): ListCertsResult {
	const result: ListCertsResult = {
		certificates: store.certificates,
		errors: store.errors,
		signing_ready: store.signingReady
	};
	audit(config, { event: 'list_certs', ok: true, detail: `${result.certificates.length} certificate(s)` });
	return result;
}

export interface VerifyInput {
	/** The signed artifact. Exactly one of `artifact_b64`/`path`. */
	artifact_b64?: string;
	path?: string;
	/** The signed document, for a detached signature (CAdES-BES here always is one). */
	detached_b64?: string;
	detached_path?: string;
}

export interface VerifyResult {
	report: VerificationReport;
	/** False when the format is recognised but this build cannot check it — never silently `ok`. */
	supported: boolean;
	document_sha256: string | null;
}

export async function verifyDocument(config: ServerConfig, input: VerifyInput): Promise<VerifyResult> {
	const artifact = await readSource(
		config,
		{ file_b64: input.artifact_b64, path: input.path },
		'artifact'
	);
	// `pickSource` rejects "both channels at once", so build the detached source from whichever
	// fields are present and let it do the validating.
	const detachedSource: Source = {};
	if (typeof input.detached_b64 === 'string' && input.detached_b64 !== '') {
		detachedSource.file_b64 = input.detached_b64;
	}
	if (typeof input.detached_path === 'string' && input.detached_path !== '') {
		detachedSource.path = input.detached_path;
	}
	const hasDetached = detachedSource.file_b64 !== undefined || detachedSource.path !== undefined;
	const detached: Bytes | null = hasDetached ? await readSource(config, detachedSource, 'detached document') : null;
	const documentSha256 = detached ? await sha256Hex(detached) : null;

	const format = detectFormat(artifact);
	if (format === 'xades' && domUnavailable()) {
		audit(config, { event: 'verify', ok: false, format, error: 'dom_unavailable' });
		return {
			report: {
				format,
				ok: false,
				status: 'invalid',
				signer: null,
				signingTime: null,
				checks: [
					{
						id: 'unsupported',
						status: 'info',
						detail: 'XAdES verification needs a DOM (DOMParser); this stdio build has none — Refs #28'
					}
				]
			},
			supported: false,
			document_sha256: documentSha256
		};
	}

	const report = await verifyArtifact(artifact, { detached, trustedRoots: config.trustedRoots });
	audit(config, {
		event: 'verify',
		ok: report.ok,
		format: report.format,
		documentSha256: documentSha256 ?? undefined,
		bytes: artifact.length
	});
	return { report, supported: report.format !== 'pdf', document_sha256: documentSha256 };
}
