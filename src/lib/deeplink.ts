/**
 * `afirma://` deep-link bridge (Refs #103). Desktop shell only; every import
 * of `@tauri-apps/api` is lazy behind the `__TAURI_INTERNALS__` probe so the
 * web bundle never loads it (same pattern as `save-file.ts`).
 *
 * Flow: the shell parses + gates each portal URL and emits
 * `deeplink://request`. This module surfaces the gated request, triggers the
 * native per-request confirmation (`deeplink_confirm`), and manages the
 * per-domain allow-list. Nothing here signs: a confirmed request still needs
 * the user to pick a certificate and sign inside the app.
 */

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
