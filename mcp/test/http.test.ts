/**
 * The remote transport: the same three tools over Streamable HTTP, behind a bearer token.
 *
 * The cheap checks run against the fetch handler directly; the money path — a real MCP client over
 * a real socket producing a signature that `openssl cms -verify` accepts — runs against the Node
 * entry, because that is what the deployment actually starts.
 */

import { execFileSync } from 'node:child_process';
import { readFileSync, writeFileSync } from 'node:fs';
import { join } from 'node:path';
import { Client } from '@modelcontextprotocol/sdk/client/index.js';
import { StreamableHTTPClientTransport } from '@modelcontextprotocol/sdk/client/streamableHttp.js';
import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { CertStore } from '../src/certstore';
import { createHttpHandler } from '../src/http';
import { startHttpServer, type RunningServer } from '../src/server-http';
import { storeFixture, tempDir, type StoreFixture } from './helpers';

const TOKEN = 'token-de-prueba-0123456789';
const SIGNING_TIME = '2026-10-14T09:00:00.000Z';

let fixture: StoreFixture;
let store: CertStore;

beforeAll(async () => {
	fixture = storeFixture();
	store = await CertStore.load(fixture.config);
});

/** The text payload of a tool answer, with an `isError` answer turned into a throw. */
function payload(result: unknown): unknown {
	const answer = result as { content?: { type: string; text?: string }[]; isError?: boolean };
	const text = answer.content?.find((part) => part.type === 'text')?.text ?? '';
	if (answer.isError) throw new Error(text);
	return JSON.parse(text);
}

describe('createHttpHandler', () => {
	const handler = (overrides: Partial<typeof fixture.config> = {}) =>
		createHttpHandler({ ...fixture.config, token: TOKEN, ...overrides }, store);

	it('serves only the /mcp path', async () => {
		const response = await handler()(new Request('http://localhost:8790/', { method: 'POST' }));
		expect(response.status).toBe(404);
		expect(await response.json()).toMatchObject({ error: 'not_found' });
	});

	it('refuses to answer at all without a configured token', async () => {
		const response = await handler({ token: null })(new Request('http://localhost:8790/mcp', { method: 'POST' }));
		expect(response.status).toBe(503);
		expect(response.headers.get('www-authenticate')).toBeNull();
	});

	it('rejects a missing, wrong or non-bearer credential', async () => {
		const post = (authorization?: string) =>
			handler()(
				new Request('http://localhost:8790/mcp', {
					method: 'POST',
					headers: authorization ? { authorization } : {}
				})
			);
		for (const header of [undefined, 'Bearer token-de-prueba', 'Bearer ', `Basic ${TOKEN}`, TOKEN]) {
			const response = await post(header);
			expect(response.status, `header ${JSON.stringify(header)}`).toBe(401);
			expect(response.headers.get('www-authenticate')).toBe('Bearer');
		}
	});
});

describe('startHttpServer', () => {
	let running: RunningServer;
	let client: Client;

	beforeAll(async () => {
		running = await startHttpServer({ ...fixture.config, token: TOKEN, port: 0 }, store);
		client = new Client({ name: 'autofirme-mcp-http-test', version: '0.0.0' });
		await client.connect(
			new StreamableHTTPClientTransport(new URL(`${running.url}/mcp`), {
				requestInit: { headers: { authorization: `Bearer ${TOKEN}` } }
			})
		);
	});

	afterAll(async () => {
		await client.close();
		await running.close();
	});

	it('exposes the same tool surface as stdio', async () => {
		const names = (await client.listTools()).tools.map((tool) => tool.name).sort();
		expect(names).toEqual(['autofirma_list_certs', 'autofirma_sign', 'autofirma_verify']);
	});

	it('produces a signature over HTTP that openssl accepts against the signer CA', async () => {
		const certs = payload(await client.callTool({ name: 'autofirma_list_certs', arguments: {} })) as {
			signing_ready: boolean;
			certificates: { cert_id: string; signable: boolean }[];
		};
		expect(certs.signing_ready).toBe(true);
		expect(JSON.stringify(certs)).not.toContain('PRIVATE KEY');

		const signed = payload(
			await client.callTool({
				name: 'autofirma_sign',
				arguments: {
					path: 'documento.txt',
					format: 'cades',
					cert_id: certs.certificates.find((cert) => cert.signable)!.cert_id,
					signing_time: SIGNING_TIME
				}
			})
		) as { signed_b64: string; detached: boolean; document_sha256: string };
		expect(signed.detached).toBe(true);

		const dir = tempDir('autofirme-mcp-http-');
		const artifact = join(dir, 'firma.csig');
		const recovered = join(dir, 'recuperado.txt');
		writeFileSync(artifact, Buffer.from(signed.signed_b64, 'base64'));
		execFileSync(
			'openssl',
			[
				'cms', '-verify', '-binary', '-inform', 'DER',
				'-in', artifact,
				'-content', join(fixture.workspace, 'documento.txt'),
				'-CAfile', fixture.fixtures.caPath,
				'-out', recovered
			],
			{ stdio: 'pipe' }
		);
		expect(readFileSync(recovered)).toEqual(Buffer.from(fixture.fixtures.content));
	});

	it('answers 401 over the socket when the token is wrong', async () => {
		const response = await fetch(`${running.url}/mcp`, {
			method: 'POST',
			headers: { authorization: 'Bearer nope', 'content-type': 'application/json' },
			body: JSON.stringify({ jsonrpc: '2.0', id: 1, method: 'initialize', params: {} })
		});
		expect(response.status).toBe(401);
	});
});
