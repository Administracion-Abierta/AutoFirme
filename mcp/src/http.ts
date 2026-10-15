/**
 * The remote transport: MCP over Streamable HTTP (spec 2025-03-26), stateless, bearer-authenticated.
 *
 * The handler is a Web-standard `(Request) => Response`, so the same function serves Node (this
 * repo's deployment) and any other platform with `fetch` — the runtime adapter is the only piece
 * that differs. Every request carries its own bearer token check and its own short-lived transport;
 * there is no session table to leak, replay or keep alive across replicas.
 *
 * Threat model: whoever holds the token can sign with the loaded certificate. Keep the endpoint
 * behind TLS (the operator's tunnel) and treat the token as the certificate's remote PIN.
 */

import { WebStandardStreamableHTTPServerTransport } from '@modelcontextprotocol/sdk/server/webStandardStreamableHttp.js';
import { audit } from './audit';
import type { CertStore } from './certstore';
import type { ServerConfig } from './config';
import { buildServer } from './surface';

export const MCP_PATH = '/mcp';

const json = (status: number, body: unknown): Response =>
	new Response(JSON.stringify(body), {
		status,
		headers: { 'content-type': 'application/json' }
	});

/**
 * Constant-time bearer check: both sides are digested first, so the comparison neither leaks the
 * token's length nor short-circuits on the first differing byte.
 */
async function tokenMatches(presented: string, expected: string): Promise<boolean> {
	const encoder = new TextEncoder();
	const [a, b] = await Promise.all([
		crypto.subtle.digest('SHA-256', encoder.encode(presented)),
		crypto.subtle.digest('SHA-256', encoder.encode(expected))
	]);
	const left = new Uint8Array(a);
	const right = new Uint8Array(b);
	let diff = 0;
	for (let i = 0; i < left.length; i += 1) diff |= left[i] ^ right[i];
	return diff === 0;
}

/** The bearer token of a request, or `null` when the header is missing or not a bearer one. */
function bearer(request: Request): string | null {
	const header = request.headers.get('authorization');
	if (!header) return null;
	const [scheme, value] = header.split(' ');
	return scheme?.toLowerCase() === 'bearer' && value ? value : null;
}

/**
 * `POST /mcp` and nothing else. A body carries a base64 document plus its JSON envelope on top of
 * the tool layer's own cap, so the transport's limit is that cap inflated by the encoding.
 */
export function createHttpHandler(config: ServerConfig, store: CertStore): (request: Request) => Promise<Response> {
	const maxRequestBodySize = Math.ceil((config.maxBytes * 4) / 3) + 64 * 1024;

	return async (request) => {
		const { pathname } = new URL(request.url);
		if (pathname !== MCP_PATH) return json(404, { error: 'not_found', message: `only ${MCP_PATH} is served` });

		if (!config.token) {
			audit(config, { event: 'http', ok: false, error: 'no_token_configured' });
			return json(503, { error: 'not_configured', message: 'AUTOFIRME_MCP_TOKEN is not set' });
		}
		const presented = bearer(request);
		if (!presented || !(await tokenMatches(presented, config.token))) {
			audit(config, { event: 'http', ok: false, error: 'unauthorized' });
			return new Response(JSON.stringify({ error: 'unauthorized' }), {
				status: 401,
				headers: { 'content-type': 'application/json', 'www-authenticate': 'Bearer' }
			});
		}

		// One transport per request: stateless mode has nothing to resume, so nothing to keep.
		const transport = new WebStandardStreamableHTTPServerTransport({
			sessionIdGenerator: undefined,
			enableJsonResponse: true,
			maxRequestBodySize
		});
		const server = buildServer(config, store);
		await server.connect(transport);
		try {
			return await transport.handleRequest(request);
		} finally {
			await transport.close();
			await server.close();
		}
	};
}
