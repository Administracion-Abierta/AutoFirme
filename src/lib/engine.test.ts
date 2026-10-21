/**
 * Worker client failure paths (Refs #94). `run()` must settle even when the worker never
 * answers: a load error, an uncloneable reply or `terminate()` mid-flight would otherwise
 * leave the UI stuck on "running" forever.
 */

import { afterEach, expect, it, vi } from 'vitest';
import { createEngineClient, type EngineRequest } from './engine';

/** Minimal stand-in for the browser `Worker`: records handlers, never replies. */
class FakeWorker {
	static last: FakeWorker | null = null;
	onmessage: ((event: MessageEvent) => void) | null = null;
	onerror: ((event: ErrorEvent) => void) | null = null;
	onmessageerror: ((event: MessageEvent) => void) | null = null;
	posted: unknown[] = [];
	terminated = false;
	constructor() {
		FakeWorker.last = this;
	}
	postMessage(message: unknown): void {
		this.posted.push(message);
	}
	terminate(): void {
		this.terminated = true;
	}
}

const request: EngineRequest = { kind: 'verify', artifact: new ArrayBuffer(8) };

afterEach(() => {
	vi.unstubAllGlobals();
	FakeWorker.last = null;
});

it('rejects an in-flight run when the worker fails to load', async () => {
	vi.stubGlobal('Worker', FakeWorker);
	const client = createEngineClient();
	const run = client.run(request);
	FakeWorker.last!.onerror!(new ErrorEvent('error', { message: 'boom' }));
	await expect(run).rejects.toThrow('boom');
});

it('rejects an in-flight run when the worker is terminated', async () => {
	vi.stubGlobal('Worker', FakeWorker);
	const client = createEngineClient();
	const run = client.run(request);
	client.terminate();
	await expect(run).rejects.toThrow('terminated');
	expect(FakeWorker.last!.terminated).toBe(true);
});
