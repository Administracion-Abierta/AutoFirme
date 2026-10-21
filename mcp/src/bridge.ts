/**
 * Outbound bridge for the Chrome extension.
 *
 * The extension holds the operator's saved identities and the PIN prompt, so the MCP server never
 * sees a private key or a PIN. It accepts one authenticated WebSocket at `/bridge` (same bearer
 * token as `/mcp`) and sends jobs over it: list certificates, or ask a human to confirm and sign.
 * The extension opens its own chooser window for every signature and answers with the finished
 * CAdES container, which this server verifies before returning it.
 *
 * RFC 6455 is implemented here instead of pulling a dependency: the framing needed for short JSON
 * messages is small, and the client is a known Chrome runtime, not the open web.
 */

import { createHash, randomUUID, timingSafeEqual } from 'node:crypto';
import type { Duplex } from 'node:stream';
import type { IncomingMessage } from 'node:http';
import { audit } from './audit';
import type { CertSummary } from './certstore';
import type { ServerConfig } from './config';
import { ToolError } from './input';

export const BRIDGE_PATH = '/bridge';
const PROTOCOL = 'autofirme-bridge.v1';
/** JSON jobs carry a base64 document in and signature bytes out; sized to the server's own cap. */
const FRAME_OVERHEAD = 64 * 1024;
const PING_INTERVAL_MS = 25_000;
const CERTS_TIMEOUT_MS = 15_000;
const SIGN_TIMEOUT_MS = 5 * 60_000;

const OP_CONTINUATION = 0x0;
const OP_TEXT = 0x1;
const OP_CLOSE = 0x8;
const OP_PING = 0x9;
const OP_PONG = 0xa;

/** Constant-time token comparison, digesting first so length does not leak either. */
export function tokenMatches(presented: string, expected: string): boolean {
	const digest = (value: string) => createHash('sha256').update(value).digest();
	return timingSafeEqual(digest(presented), digest(expected));
}

function frame(opcode: number, payload: Buffer): Buffer {
	const length = payload.length;
	const header =
		length < 126
			? Buffer.from([0x80 | opcode, length])
			: length < 65_536
				? Buffer.from([0x80 | opcode, 126, length >> 8, length & 0xff])
				: (() => {
						const out = Buffer.alloc(10);
						out[0] = 0x80 | opcode;
						out[1] = 127;
						out.writeBigUInt64BE(BigInt(length), 2);
						return out;
					})();
	return Buffer.concat([header, payload]);
}

function refuse(socket: Duplex, status: number, message: string): void {
	socket.end(`HTTP/1.1 ${status} ${message}\r\nconnection: close\r\ncontent-length: 0\r\n\r\n`);
}

interface Pending {
	resolve(value: unknown): void;
	reject(error: Error): void;
	timer: NodeJS.Timeout;
}

class BridgeClient {
	readonly pending = new Map<string, Pending>();
	alive = true;
	private readonly socket: Duplex;
	private buffer: Buffer = Buffer.alloc(0);
	private fragments: Buffer[] = [];
	private readonly maxFrame: number;

	constructor(socket: Duplex, private readonly hub: BridgeHub) {
		this.socket = socket;
		this.maxFrame = hub.maxFrame;
		socket.on('data', (chunk: Buffer) => this.receive(chunk));
		socket.on('close', () => this.closed());
		socket.on('error', () => this.closed());
	}

	send(message: unknown): void {
		if (this.socket.destroyed || this.socket.writableEnded) return;
		this.socket.write(frame(OP_TEXT, Buffer.from(JSON.stringify(message), 'utf8')));
	}

	ping(): void {
		if (this.socket.destroyed || this.socket.writableEnded) return;
		this.socket.write(frame(OP_PING, Buffer.alloc(0)));
	}

	close(code = 1000): void {
		if (this.socket.destroyed || this.socket.writableEnded) return;
		const payload = Buffer.alloc(2);
		payload.writeUInt16BE(code, 0);
		this.socket.end(frame(OP_CLOSE, payload));
		this.closed();
	}

	private fail(code: number, reason: string): void {
		this.close(code);
		if (!this.socket.destroyed) this.socket.destroy();
		this.hub.drop(this, reason);
	}

	private closed(): void {
		this.hub.drop(this, 'disconnected');
	}

	private receive(chunk: Buffer): void {
		this.buffer = this.buffer.length === 0 ? chunk : Buffer.concat([this.buffer, chunk]);
		while (this.buffer.length >= 2) {
			const first = this.buffer[0];
			const second = this.buffer[1];
			const fin = (first & 0x80) !== 0;
			const opcode = first & 0x0f;
			const masked = (second & 0x80) !== 0;
			let length = second & 0x7f;
			let offset = 2;
			if (length === 126) {
				if (this.buffer.length < 4) return;
				length = this.buffer.readUInt16BE(2);
				offset = 4;
			} else if (length === 127) {
				if (this.buffer.length < 10) return;
				const wide = this.buffer.readBigUInt64BE(2);
				if (wide > BigInt(this.maxFrame)) return this.fail(1009, 'frame too large');
				length = Number(wide);
				offset = 10;
			}
			if (!masked) return this.fail(1002, 'unmasked frame');
			if (length > this.maxFrame) return this.fail(1009, 'frame too large');
			if (this.buffer.length < offset + 4 + length) return;
			const mask = this.buffer.subarray(offset, offset + 4);
			const payload = Buffer.alloc(length);
			const body = this.buffer.subarray(offset + 4, offset + 4 + length);
			for (let i = 0; i < length; i += 1) payload[i] = body[i] ^ mask[i & 3];
			this.buffer = this.buffer.subarray(offset + 4 + length);

			if (opcode === OP_CLOSE) return this.fail(1000, 'closed by extension');
			if (opcode === OP_PING) {
				this.socket.write(frame(OP_PONG, payload));
				continue;
			}
			if (opcode === OP_PONG) {
				this.alive = true;
				continue;
			}
			if (opcode === OP_CONTINUATION) {
				this.fragments.push(payload);
				if (this.fragments.reduce((total, part) => total + part.length, 0) > this.maxFrame) {
					return this.fail(1009, 'message too large');
				}
				if (fin) {
					const message = Buffer.concat(this.fragments);
					this.fragments = [];
					this.message(message);
				}
				continue;
			}
			if (opcode !== OP_TEXT) return this.fail(1003, 'binary frames unsupported');
			if (fin) {
				this.message(payload);
			} else {
				this.fragments = [payload];
			}
		}
	}

	private message(payload: Buffer): void {
		let message: unknown;
		try {
			message = JSON.parse(new TextDecoder('utf-8', { fatal: true }).decode(payload));
		} catch {
			return this.fail(1007, 'invalid JSON');
		}
		if (typeof message !== 'object' || message === null) return;
		const event = message as { type?: unknown; id?: unknown; ok?: unknown; value?: unknown; error?: unknown };
		if (event.type !== 'result' || typeof event.id !== 'string') return;
		const pending = this.pending.get(event.id);
		if (!pending) return;
		this.pending.delete(event.id);
		clearTimeout(pending.timer);
		if (event.ok === true) pending.resolve(event.value);
		else pending.reject(new ToolError('bridge_error', typeof event.error === 'string' ? event.error : 'extension refused'));
	}
}

export class BridgeHub {
	private readonly clients = new Set<BridgeClient>();
	private readonly heartbeat: NodeJS.Timeout;
	/** Largest JSON job accepted: the document cap inflated to base64 plus envelope room. */
	readonly maxFrame: number;
	private lastCertificates: CertSummary[] = [];

	constructor(private readonly config: ServerConfig) {
		this.maxFrame = Math.ceil((config.maxBytes * 4) / 3) + FRAME_OVERHEAD;
		this.heartbeat = setInterval(() => {
			for (const client of this.clients) {
				if (!client.alive) {
					client.close(1001);
					continue;
				}
				client.alive = false;
				client.ping();
			}
		}, PING_INTERVAL_MS);
		this.heartbeat.unref();
	}

	get online(): boolean {
		return this.clients.size > 0;
	}

	commonName(certId: string): string | null {
		return this.lastCertificates.find((cert) => cert.cert_id === certId)?.common_name ?? null;
	}

	certificate(certId: string): CertSummary | null {
		return this.lastCertificates.find((cert) => cert.cert_id === certId) ?? null;
	}

	attach(server: { on(event: 'upgrade', listener: (request: IncomingMessage, socket: Duplex, head: Buffer) => void): unknown }): void {
		server.on('upgrade', (request, socket, head) => this.upgrade(request, socket, head));
	}

	private upgrade(request: IncomingMessage, socket: Duplex, head: Buffer): void {
		const url = new URL(request.url ?? '/', 'http://bridge.invalid');
		if (!url.pathname.endsWith(BRIDGE_PATH)) return refuse(socket, 404, 'Not Found');
		if (!this.config.token) return refuse(socket, 503, 'Service Unavailable');
		if (!tokenMatches(url.searchParams.get('token') ?? '', this.config.token)) {
			audit(this.config, { event: 'bridge', ok: false, error: 'unauthorized' });
			return refuse(socket, 401, 'Unauthorized');
		}
		const key = request.headers['sec-websocket-key'];
		const offered = new Set(
			(request.headers['sec-websocket-protocol'] ?? '')
				.split(',')
				.map((value) => value.trim())
				.filter(Boolean)
		);
		if (
			typeof key !== 'string' ||
			request.headers.upgrade?.toLowerCase() !== 'websocket' ||
			!offered.has(PROTOCOL)
		) {
			return refuse(socket, 400, 'Bad Request');
		}
		const accept = createHash('sha1').update(`${key}258EAFA5-E914-47DA-95CA-C5AB0DC85B11`).digest('base64');
		socket.write(
			[
				'HTTP/1.1 101 Switching Protocols',
				'upgrade: websocket',
				'connection: Upgrade',
				`sec-websocket-accept: ${accept}`,
				`sec-websocket-protocol: ${PROTOCOL}`,
				'',
				''
			].join('\r\n')
		);
		const client = new BridgeClient(socket, this);
		this.clients.add(client);
		if (head.length > 0) socket.emit('data', head);
		audit(this.config, { event: 'bridge', ok: true, detail: 'extension connected' });
	}

	/** Called by a client when its socket ends, so pending jobs fail instead of hanging. */
	drop(client: BridgeClient, reason: string): void {
		if (!this.clients.delete(client)) return;
		for (const [id, pending] of client.pending) {
			clearTimeout(pending.timer);
			pending.reject(new ToolError('bridge_disconnected', `extension ${reason} while job ${id} was pending`));
		}
		client.pending.clear();
		audit(this.config, { event: 'bridge', ok: false, error: reason });
	}

	private async request(payload: Record<string, unknown>, timeoutMs: number): Promise<unknown> {
		const client = [...this.clients].sort((a, b) => a.pending.size - b.pending.size)[0];
		if (!client) throw new ToolError('bridge_unavailable', 'the AutoFirme extension is not connected');
		const id = randomUUID();
		const { promise, resolve, reject } = Promise.withResolvers<unknown>();
		const timer = setTimeout(() => {
			client.pending.delete(id);
			reject(new ToolError('bridge_timeout', 'the extension did not answer in time'));
		}, timeoutMs);
		timer.unref();
		client.pending.set(id, { resolve, reject, timer });
		client.send({ ...payload, id, protocol: PROTOCOL });
		return promise;
	}

	async listCertificates(): Promise<CertSummary[]> {
		const value = await this.request({ type: 'certs' }, CERTS_TIMEOUT_MS);
		const certificates = (value as { certificates?: unknown } | null)?.certificates;
		if (!Array.isArray(certificates)) {
			throw new ToolError('bridge_error', 'the extension returned no certificate list');
		}
		this.lastCertificates = certificates as CertSummary[];
		return this.lastCertificates;
	}

	async sign(request: {
		format: string;
		certId: string;
		data: string;
		signingTime: string;
	}): Promise<string> {
		const value = await this.request(
			{ type: 'sign', format: request.format, certId: request.certId, data: request.data, signingTime: request.signingTime, mode: 'explicit' },
			SIGN_TIMEOUT_MS
		);
		const { signed_b64 } = (value ?? {}) as { signed_b64?: unknown };
		if (typeof signed_b64 !== 'string') {
			throw new ToolError('bridge_error', 'the extension returned no signed artifact');
		}
		return signed_b64;
	}

	close(): void {
		clearInterval(this.heartbeat);
		for (const client of this.clients) client.close(1001);
		this.clients.clear();
	}
}
