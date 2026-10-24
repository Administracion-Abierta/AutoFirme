/**
 * `afirma://` deep-link bridge (Refs #103). Desktop shell only; every import
 * of `@tauri-apps/api` is lazy behind the `__TAURI_INTERNALS__` probe so the
 * web bundle never loads it (same pattern as `save-file.ts`).
 *
 * Flow: the shell parses + gates each portal URL and emits
 * `deeplink://request`. This module surfaces the gated request, triggers the
 * native per-request confirmation (`deeplink_confirm`), manages the
 * per-domain allow-list, and pulls the portal's document payload (`dat`) out
 * of a confirmed request so the existing engine signs it. Result delivery
 * back to the portal's servlet is not implemented here (Refs #103 gap note).
 */

import type { SignFormat } from './sign';
import { isDesktop } from './desktop';

/** Who owns the `afirma://` scheme. Mirrors the Rust `SchemeStatus`. */
export type SchemeStatus = 'ours' | 'none' | { other: { handler: string | null } };

/** A parsed + gated portal request emitted by the shell. */
export interface GatedRequest {
	op: string;
	origin: string | null;
	allowed: boolean;
	params: [string, string][];
}

/** A refused URL: parse failure or forbidden op, shown never silently dropped. */
export interface RefusedRequest {
	raw: string;
	reason: string;
}

export type IncomingRequest = { gated: GatedRequest } | { refused: RefusedRequest };

function isGated(payload: unknown): payload is GatedRequest {
	return (
		typeof payload === 'object' &&
		payload !== null &&
		typeof (payload as GatedRequest).op === 'string' &&
		Array.isArray((payload as GatedRequest).params)
	);
}

function isRefused(payload: unknown): payload is RefusedRequest {
	return (
		typeof payload === 'object' &&
		payload !== null &&
		typeof (payload as RefusedRequest).raw === 'string' &&
		typeof (payload as RefusedRequest).reason === 'string'
	);
}

/** Normalise the raw event payload into a gated or refused request. */
export function classifyIncoming(payload: unknown): IncomingRequest | null {
	if (isGated(payload)) return { gated: payload as GatedRequest };
	if (isRefused(payload)) return { refused: payload as RefusedRequest };
	return null;
}

async function invoke<T>(command: string, args?: Record<string, unknown>): Promise<T> {
	const { invoke } = await import('@tauri-apps/api/core');
	return invoke<T>(command, args);
}

/** Current `afirma://` owner. `null` outside the desktop shell. */
export async function deeplinkStatus(): Promise<SchemeStatus | null> {
	if (!isDesktop()) return null;
	return invoke<SchemeStatus>('deeplink_status');
}

/** Allow-listed portal domains, sorted. Empty outside the desktop shell. */
export async function deeplinkList(): Promise<string[]> {
	if (!isDesktop()) return [];
	return invoke<string[]>('deeplink_list');
}

export async function deeplinkRemove(domain: string): Promise<boolean> {
	if (!isDesktop()) return false;
	return invoke<boolean>('deeplink_remove', { domain });
}

/**
 * Trigger the native per-request confirmation dialog (op + origin + params).
 * `remember = true` allow-lists ONLY this request's domain — never "all".
 * Returns the user's decision.
 */
export async function deeplinkConfirm(
	gated: GatedRequest,
	allow: boolean,
	remember: boolean
): Promise<boolean> {
	if (!isDesktop()) return false;
	return invoke<boolean>('deeplink_confirm', { gated, allow, remember });
}

/**
 * Listen for gated portal requests from the shell. Returns a detach function;
 * no-op (empty detach) outside the desktop shell.
 */
export async function listenDeepLinks(
	listener: (request: IncomingRequest) => void
): Promise<() => void> {
	if (!isDesktop()) return () => {};
	const { listen } = await import('@tauri-apps/api/event');
	return listen<unknown>('deeplink://request', (event) => {
		const incoming = classifyIncoming(event.payload);
		if (incoming) listener(incoming);
	});
}

/** A confirmed request's document, ready for the sign flow. */
export interface PortalPayload {
	file: File;
	/** `format` param mapped to an engine format; `null` keeps the current one. */
	format: SignFormat | null;
}

/** Ops that carry a document to sign (AutoFirma 1.9 `ProtocolInvocationLauncher`). */
const SIGN_OPS = new Set(['sign', 'cosign', 'countersign', 'signandsave']);

async function gunzip(bytes: Uint8Array<ArrayBuffer>): Promise<Uint8Array<ArrayBuffer> | null> {
	try {
		const source = new ReadableStream<Uint8Array<ArrayBuffer>>({
			start(controller) {
				controller.enqueue(bytes);
				controller.close();
			}
		});
		const reader = source.pipeThrough(new DecompressionStream('gzip')).getReader();
		const chunks: Uint8Array[] = [];
		let total = 0;
		for (;;) {
			const { done, value } = await reader.read();
			if (done) break;
			chunks.push(value);
			total += value.byteLength;
		}
		const out = new Uint8Array(total);
		let offset = 0;
		for (const chunk of chunks) {
			out.set(chunk, offset);
			offset += chunk.byteLength;
		}
		return out;
	} catch {
		return null;
	}
}

/**
 * Document payload of a confirmed `afirma://` request: the `dat` param is
 * base64 document bytes (AutoFirma 1.9 `UrlParameters`), optionally gzipped
 * when `gzip` is set. Returns `null` when the request carries no loadable
 * payload (e.g. `fileid` servlet retrieval — needs a network fetch, out of
 * this slice) or the bytes are malformed, so the caller can refuse + show.
 */
export async function payloadFromGated(gated: GatedRequest): Promise<PortalPayload | null> {
	if (!SIGN_OPS.has(gated.op)) return null;
	const params = new Map(gated.params);
	const dat = params.get('dat');
	if (!dat) return null;
	let bytes: Uint8Array<ArrayBuffer>;
	try {
		const binary = atob(dat.replaceAll(/\s/g, ''));
		bytes = new Uint8Array(binary.length);
		for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
	} catch {
		return null;
	}
	const gzip = (params.get('gzip') ?? '').toLowerCase();
	if (gzip === 'true' || gzip === '1') {
		const inflated = await gunzip(bytes);
		if (!inflated) return null;
		bytes = inflated;
	}
	if (bytes.length === 0) return null;
	// Portal formats arrive as `CAdES`/`XAdES`/`PAdES` (any case).
	const raw = (params.get('format') ?? '').toLowerCase();
	const format = raw.includes('pades')
		? 'pades'
		: raw.includes('xades')
			? 'xades'
			: raw.includes('cades')
				? 'cades'
				: null;
	// Strip path separators: the name only labels the in-memory file.
	const name = (params.get('filename') ?? '').replaceAll(/[/\\]/g, '-').slice(0, 128);
	return { file: new File([bytes], name || 'documento'), format };
}
