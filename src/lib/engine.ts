/**
 * Signing engine behind a Web Worker (Refs #94).
 *
 * The 50 MB PAdES sign must not freeze the webview, so `signPdf` / `verifyArtifact`
 * run off the main thread. The protocol below is the only thing crossing the boundary:
 * structured-cloneable `ArrayBuffer`s (plus the `CryptoKey`, which the spec makes
 * cloneable) — never DOM objects, never `Uint8Array` views sharing memory.
 *
 * Tests and non-browser runtimes use {@link runEngineSync}, which runs the same
 * operations in-process; the browser UI uses {@link createEngineClient}.
 */

import { signPdf } from './pades';
import { verifyArtifact } from './verify';
import type { VerificationReport } from './verify';

export interface SignPadesRequest {
	kind: 'sign-pades';
	/** Owns an `ArrayBuffer` so it can ride the transfer list. */
	pdf: ArrayBuffer;
	certificate: ArrayBuffer;
	chain: ArrayBuffer[];
	/** Structured-cloneable; never leaves the client. */
	privateKey: CryptoKey;
	signingTime?: number;
	name?: string;
}

export interface VerifyRequest {
	kind: 'verify';
	artifact: ArrayBuffer;
	/** PEM trust anchors; the fixture leaf is self-signed so tests pass it as its own root. */
	trustedRoots?: string[];
}

export type EngineRequest = SignPadesRequest | VerifyRequest;

export interface EngineProgress {
	id: number;
	phase: string;
	/** 0–1 when known, null for indeterminate steps. */
	fraction: number | null;
}

export type EngineResult =
	| { kind: 'sign-pades'; signed: ArrayBuffer }
	| { kind: 'verify'; report: VerificationReport };

export type EngineResponse =
	| { id: number; ok: true; result: EngineResult }
	| { id: number; ok: false; error: string };

/** Same operations as the worker, in-process — for tests and non-DOM runtimes. */
export async function runEngineSync(request: EngineRequest): Promise<EngineResult> {
	if (request.kind === 'sign-pades') {
		const signed = await signPdf(new Uint8Array(request.pdf), {
			certificate: new Uint8Array(request.certificate),
			chain: request.chain.map((entry) => new Uint8Array(entry)),
			privateKey: request.privateKey,
			signingTime: request.signingTime === undefined ? undefined : new Date(request.signingTime),
			name: request.name
		});
		const pdf = signed as Uint8Array<ArrayBuffer>;
		return { kind: 'sign-pades', signed: pdf.buffer.slice(pdf.byteOffset, pdf.byteOffset + pdf.byteLength) as ArrayBuffer };
	}
	const report = await verifyArtifact(new Uint8Array(request.artifact) as Uint8Array<ArrayBuffer>, {
		trustedRoots: request.trustedRoots
	});
	return { kind: 'verify', report };
}

/** Buffers carried by a request, for the worker `postMessage` transfer list. */
export function transferList(request: EngineRequest): ArrayBuffer[] {
	return request.kind === 'sign-pades'
		? [request.pdf, request.certificate, ...request.chain]
		: [request.artifact];
}

export interface EngineClient {
	/** Runs one request on the worker; progress posts arrive via `onProgress`. */
	run(request: EngineRequest, onProgress?: (phase: string, fraction: number | null) => void): Promise<EngineResult>;
	terminate(): void;
}

/** Browser entry point. Construct inside an event handler, never during prerender. */
export function createEngineClient(): EngineClient {
	const worker = new Worker(new URL('./engine.worker.ts', import.meta.url), { type: 'module' });
	let nextId = 1;
	const pending = new Map<
		number,
		{
			resolve: (result: EngineResult) => void;
			reject: (error: Error) => void;
			onProgress?: (phase: string, fraction: number | null) => void;
		}
	>();
	// A worker that fails to load or dies mid-request never posts a reply; without these the
	// `run` promise would hang forever and the caller's UI would stay stuck in "running".
	const fail = (reason: string): void => {
		const error = new Error(reason);
		for (const slot of pending.values()) slot.reject(error);
		pending.clear();
	};
	worker.onerror = (event) => fail(event.message || 'engine worker failed');
	worker.onmessageerror = () => fail('engine worker sent a message that could not be cloned');
	worker.onmessage = (event: MessageEvent<EngineResponse | EngineProgress>) => {
		const message = event.data;
		if ('phase' in message) {
			pending.get(message.id)?.onProgress?.(message.phase, message.fraction);
			return;
		}
		const slot = pending.get(message.id);
		if (!slot) return;
		pending.delete(message.id);
		if (message.ok) slot.resolve(message.result);
		else slot.reject(new Error(message.error));
	};
	return {
		run(request, onProgress) {
			const id = nextId++;
			return new Promise<EngineResult>((resolve, reject) => {
				pending.set(id, { resolve, reject, onProgress });
				worker.postMessage({ ...request, id }, transferList(request));
			});
		},
		terminate() {
			worker.terminate();
			// A request still in flight will never be answered; reject it instead of leaking a
			// promise that never settles.
			fail('engine worker terminated');
		}
	};
}
