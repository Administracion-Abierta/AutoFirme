/**
 * The MCP endpoint preference: where a deployed `autofirma-mcp` listens, and the bearer token that
 * opens it. Kept in this browser the same way the theme is.
 *
 * The web app only stores these so the operator can re-read them when wiring an agent. In the
 * extension the same values configure the outbound `/bridge` socket, which is the one place they
 * are sent — to the operator's own server, never anywhere else.
 */

import { isExtension, rpc } from '#lib/extension/runtime';

export const MCP_KEY = 'autofirme-mcp';

export interface McpSettings {
	/** Endpoint of the deployed server, typically `https://host/mcp`. */
	url: string;
	/** Bearer token the server was started with. Displayed masked. */
	token: string;
	/** Extension only: connect the MCP bridge when the operator asks for it. */
	enabled: boolean;
}

export interface BridgeStatus {
	enabled: boolean;
	online: boolean;
	error: string;
}

const EMPTY: McpSettings = { url: '', token: '', enabled: false };

/** The stored endpoint, or empty fields when there is none (including during prerender). */
export function readMcp(): McpSettings {
	try {
		const stored = localStorage.getItem(MCP_KEY);
		if (!stored) return EMPTY;
		const parsed: unknown = JSON.parse(stored);
		if (typeof parsed !== 'object' || parsed === null) return EMPTY;
		const { url, token, enabled } = parsed as Partial<McpSettings>;
		return {
			url: typeof url === 'string' ? url : '',
			token: typeof token === 'string' ? token : '',
			enabled: enabled === true
		};
	} catch {
		return EMPTY;
	}
}

export function saveMcp(settings: McpSettings): void {
	try {
		localStorage.setItem(MCP_KEY, JSON.stringify(settings));
	} catch {
		// Prerender or a storage-denied browser: the dialog still works for this session.
	}
}

/** Ask the service worker to (re)connect the bridge with the stored address and token. */
export async function configureMcpBridge(settings: McpSettings): Promise<BridgeStatus> {
	if (!isExtension()) return { enabled: false, online: false, error: '' };
	return rpc<BridgeStatus>({ type: 'mcp:configure', settings });
}

export async function mcpBridgeStatus(): Promise<BridgeStatus> {
	if (!isExtension()) return { enabled: false, online: false, error: '' };
	return rpc<BridgeStatus>({ type: 'mcp:status' });
}

/** Live status from the service worker; returns a detach function. */
export function listenMcpBridge(listener: (status: BridgeStatus) => void): () => void {
	const chrome = (globalThis as typeof globalThis & {
		chrome?: { runtime?: { onMessage?: { addListener(fn: (message: unknown) => void): void; removeListener(fn: (message: unknown) => void): void } } };
	}).chrome;
	const onMessage = (message: unknown) => {
		if (typeof message !== 'object' || message === null) return;
		const event = message as { type?: unknown; status?: BridgeStatus };
		if (event.type === 'mcp:status' && event.status) listener(event.status);
	};
	chrome?.runtime?.onMessage?.addListener(onMessage);
	return () => chrome?.runtime?.onMessage?.removeListener(onMessage);
}
