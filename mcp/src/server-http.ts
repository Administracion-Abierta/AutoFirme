#!/usr/bin/env node
/**
 * AutoFirma Web as a remote MCP server: Streamable HTTP on `POST /mcp`, bearer-authenticated.
 *
 * This is the deployment entry (k3s, or `node dist/server-http.js` behind the operator's tunnel).
 * The transport itself lives in `http.ts` as a Web-standard fetch handler; this file is only the
 * Node `http` adapter plus the startup checks, so the signing engine and the tool surface are
 * exactly the ones the stdio build runs.
 */

import { createServer, type IncomingMessage, type ServerResponse } from 'node:http';
import { pathToFileURL } from 'node:url';
import { audit } from './audit';
import { BridgeHub } from './bridge';
import { loadConfig, type ServerConfig } from './config';
import { createHttpHandler } from './http';
import { loadStore } from './surface';
import type { CertStore } from './certstore';

/** Headers as a flat list; repeated ones (set-cookie) are joined the way HTTP allows. */
function incomingHeaders(request: IncomingMessage): Headers {
	const headers = new Headers();
	for (const [name, value] of Object.entries(request.headers)) {
		if (value === undefined) continue;
		headers.set(name, Array.isArray(value) ? value.join(', ') : value);
	}
	return headers;
}

/** Read the body with the same cap the transport applies, so an oversized POST is refused early. */
function readBody(request: IncomingMessage, limit: number): Promise<Uint8Array<ArrayBuffer>> {
	const { promise, resolve, reject } = Promise.withResolvers<Uint8Array<ArrayBuffer>>();
	const chunks: Buffer[] = [];
	let size = 0;
	request.on('data', (chunk: Buffer) => {
		size += chunk.length;
		if (size > limit) {
			reject(new Error('request body too large'));
			request.destroy();
			return;
		}
		chunks.push(chunk);
	});
	// A copy into a plain `ArrayBuffer`, which is what `Request` accepts as a body.
	request.on('end', () => {
		const merged = Buffer.concat(chunks);
		const bytes = new Uint8Array(merged.length);
		bytes.set(merged);
		resolve(bytes);
	});
	request.on('error', reject);
	return promise;
}

export interface RunningServer {
	/** `http://host:port` — the real port, so `port: 0` is usable in tests. */
	url: string;
	/** Outbound extension bridge, shared by every MCP request on this server. */
	bridge: BridgeHub;
	close: () => Promise<void>;
}

/** Start the HTTP endpoint; resolves once the socket is bound. */
export async function startHttpServer(config: ServerConfig, store: CertStore): Promise<RunningServer> {
	const bridge = new BridgeHub(config);
	const handler = createHttpHandler(config, store, bridge);
	const limit = Math.ceil((config.maxBytes * 4) / 3) + 64 * 1024;

	const server = createServer(async (request: IncomingMessage, response: ServerResponse) => {
		try {
			const host = request.headers.host ?? `${config.host}:${config.port}`;
			const body =
				request.method === 'GET' || request.method === 'HEAD' ? undefined : await readBody(request, limit);
			const answer = await handler(
				new Request(`http://${host}${request.url ?? '/'}`, {
					method: request.method,
					headers: incomingHeaders(request),
					body
				})
			);
			response.writeHead(answer.status, Object.fromEntries(answer.headers));
			response.end(Buffer.from(await answer.arrayBuffer()));
		} catch (error) {
			audit(config, { event: 'http', ok: false, error: (error as Error).message });
			response.writeHead(error instanceof Error && error.message === 'request body too large' ? 413 : 500, {
				'content-type': 'application/json'
			});
			response.end(JSON.stringify({ error: 'internal', message: (error as Error).message }));
		}
	});

	const bound = Promise.withResolvers<void>();
	server.once('error', bound.reject);
	bridge.attach(server);
	server.listen(config.port, config.host, bound.resolve);
	await bound.promise;
	const address = server.address();
	const port = typeof address === 'object' && address ? address.port : config.port;
	return {
		url: `http://${config.host}:${port}`,
		bridge,
		close: () => {
			bridge.close();
			const closed = Promise.withResolvers<void>();
			server.close((error) => (error ? closed.reject(error) : closed.resolve()));
			return closed.promise;
		}
	};
}

async function main(): Promise<void> {
	const config = loadConfig();
	if (!config.token) {
		console.error('autofirme-mcp http: AUTOFIRME_MCP_TOKEN is not set — refusing to expose signing without a token');
		process.exit(1);
	}
	const store = await loadStore(config);
	audit(config, {
		event: 'startup',
		ok: true,
		detail: `http transport, ${store.certificates.length} certificate(s), signing_ready=${store.signingReady}`,
		bytes: config.maxBytes
	});
	const running = await startHttpServer(config, store);
	console.error(
		`autofirme-mcp http ready — ${running.url}/mcp, ${store.certificates.length} certificate(s), workspace ${config.workspace}`
	);
}

// Only start when run as a program: the tests import `startHttpServer` for an ephemeral port.
if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
	main().catch((error) => {
		console.error(`autofirme-mcp http failed to start: ${(error as Error).stack ?? String(error)}`);
		process.exit(1);
	});
}
