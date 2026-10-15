/**
 * The MCP endpoint preference: where a deployed `autofirme-mcp` listens, and the bearer token that
 * opens it. Kept in this browser the same way the theme is.
 *
 * It is a convenience, not a secret store: the app never sends these anywhere, it only remembers
 * them so the operator can re-read the settings when wiring an agent. The real secret lives in the
 * server's environment (`AUTOFIRME_MCP_TOKEN`), never in the page.
 */

export const MCP_KEY = 'autofirme-mcp';

export interface McpSettings {
	/** Endpoint of the deployed server, typically `https://host/mcp`. */
	url: string;
	/** Bearer token the server was started with. Displayed masked. */
	token: string;
}

const EMPTY: McpSettings = { url: '', token: '' };

/** The stored endpoint, or empty fields when there is none (including during prerender). */
export function readMcp(): McpSettings {
	try {
		const stored = localStorage.getItem(MCP_KEY);
		if (!stored) return EMPTY;
		const parsed: unknown = JSON.parse(stored);
		if (typeof parsed !== 'object' || parsed === null) return EMPTY;
		const { url, token } = parsed as Partial<McpSettings>;
		return { url: typeof url === 'string' ? url : '', token: typeof token === 'string' ? token : '' };
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
