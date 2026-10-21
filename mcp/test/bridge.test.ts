/**
 * The extension bridge: one authenticated WebSocket, list/sign jobs, and the MCP tool surface
 * switching to the browser's identities while it is connected.
 *
 * The fake extension signs with the same throwaway fixture key the stdio tests use, so the server's
 * own verification of the returned artifact is exercised for real.
 */

import { connect } from 'node:net';
import { Client } from '@modelcontextprotocol/sdk/client/index.js';
import { StreamableHTTPClientTransport } from '@modelcontextprotocol/sdk/client/streamableHttp.js';
import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { z } from 'zod';
import { signCadesDetached } from '../../src/lib/cms.ts';
import { CertStore } from '../src/certstore';
import { startHttpServer, type RunningServer } from '../src/server-http';
import { storeFixture, type StoreFixture } from './helpers';

const TOKEN = 'bridge-token-0123456789';
const SIGNING_TIME = '2026-10-14T09:00:00.000Z';

let fixture: StoreFixture;
let store: CertStore;
let running: RunningServer;
let client: Client;
let socket: WebSocket;

const ToolAnswer = z.object({
	content: z.array(z.object({ type: z.string(), text: z.string().optional() })),
	isError: z.boolean().optional()
});
const ListAnswer = z.object({
	signing_ready: z.boolean(),
	certificates: z.array(z.object({ cert_id: z.string() }).passthrough())
});
const SignAnswer = z.object({
	detached: z.boolean(),
	cert_id: z.string(),
	common_name: z.string(),
	signed_b64: z.string()
});
const VerifyAnswer = z.object({ supported: z.boolean(), report: z.object({ ok: z.boolean() }).passthrough() });
const BridgeJob = z.object({
	id: z.string(),
	type: z.string(),
	certId: z.string().optional(),
	data: z.string().optional(),
	signingTime: z.string().optional()
});

/** The parsed JSON body of a tool answer, validated at the transport boundary. */
function payload<T extends z.ZodType>(result: unknown, schema: T): z.output<T> {
	const answer = ToolAnswer.parse(result);
	const text = answer.content.find((part) => part.type === 'text')?.text ?? '';
	if (answer.isError) throw new Error(text);
	return schema.parse(JSON.parse(text));
}

/** A browser that answers bridge jobs using the fixture signer. */
async function answer(raw: string): Promise<void> {
	const job = BridgeJob.parse(JSON.parse(raw));
	let value: unknown;
	if (job.type === 'certs') {
		value = { certificates: store.certificates };
	} else if (job.type === 'sign' && job.certId && job.data) {
		const signer = store.signer(job.certId);
		const document = new Uint8Array(Buffer.from(job.data, 'base64'));
		const signature = await signCadesDetached(signer, document, {
			signingTime: new Date(job.signingTime ?? SIGNING_TIME)
		});
		value = { signed_b64: Buffer.from(signature).toString('base64') };
	}
	socket.send(JSON.stringify({ type: 'result', id: job.id, ok: true, value }));
}

beforeAll(async () => {
	fixture = storeFixture();
	store = await CertStore.load(fixture.config);
	running = await startHttpServer({ ...fixture.config, token: TOKEN, port: 0 }, store);
	socket = new WebSocket(`${running.url.replace('http', 'ws')}/bridge?token=${TOKEN}`, 'autofirme-bridge.v1');
	socket.addEventListener('message', (event) => void answer(String(event.data)));
	await new Promise<void>((resolve, reject) => {
		socket.addEventListener('open', () => resolve(), { once: true });
		socket.addEventListener('error', () => reject(new Error('bridge socket failed')), { once: true });
	});
	client = new Client({ name: 'autofirme-bridge-test', version: '0.0.0' });
	await client.connect(
		new StreamableHTTPClientTransport(new URL(`${running.url}/mcp`), {
			requestInit: { headers: { authorization: `Bearer ${TOKEN}` } }
		})
	);
});

afterAll(async () => {
	socket?.close();
	await client?.close();
	await running?.close();
});

describe('extension bridge', () => {
	it('refuses a socket with a wrong token or without the bridge subprotocol', async () => {
		const { hostname, port } = new URL(running.url);
		const status = (token: string, protocol?: string) =>
			new Promise<string>((resolve, reject) => {
				const socket = connect(Number(port), hostname, () => {
					socket.write(
						[
							`GET /bridge?token=${token} HTTP/1.1`,
							`Host: ${hostname}:${port}`,
							'Upgrade: websocket',
							'Connection: Upgrade',
							'Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==',
							'Sec-WebSocket-Version: 13',
							...(protocol ? [`Sec-WebSocket-Protocol: ${protocol}`] : []),
							'',
							''
						].join('\r\n')
					);
				});
				socket.once('data', (data) => {
					resolve(String(data).split('\r\n')[0] ?? '');
					socket.destroy();
				});
				socket.once('error', reject);
			});
		expect(await status('incorrect', 'autofirme-bridge.v1')).toContain('401');
		expect(await status(TOKEN)).toContain('400');
	});

	it('lists the extension identities once connected', async () => {
		const listed = payload(
			await client.callTool({ name: 'autofirma_list_certs', arguments: {} }),
			ListAnswer
		);
		expect(listed.signing_ready).toBe(true);
		expect(listed.certificates.map((cert) => cert.cert_id)).toEqual(
			store.certificates.map((cert) => cert.cert_id)
		);
	});

	it('signs through the extension and verifies the returned CAdES before answering', async () => {
		const cert = store.certificates.find((entry) => entry.signable);
		expect(cert).toBeDefined();
		const signed = payload(
			await client.callTool({
				name: 'autofirma_sign',
				arguments: { path: 'documento.txt', format: 'cades', cert_id: cert!.cert_id, signing_time: SIGNING_TIME }
			}),
			SignAnswer
		);
		expect(signed.detached).toBe(true);
		expect(signed.cert_id).toBe(cert!.cert_id);
		expect(signed.common_name).toBe(cert!.common_name);

		const verified = payload(
			await client.callTool({
				name: 'autofirma_verify',
				arguments: { artifact_b64: signed.signed_b64, detached_path: 'documento.txt' }
			}),
			VerifyAnswer
		);
		expect(verified.supported).toBe(true);
		expect(verified.report.ok).toBe(true);
	});
});
