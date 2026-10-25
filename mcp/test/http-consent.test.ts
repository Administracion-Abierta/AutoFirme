/**
 * MCP consent gate (Refs #121, `docs/SECURITY.md` §2): whoever holds the bearer
 * token can sign, so the token check must run before any signing path — and an
 * unauthenticated call must leave no `sign` event in the audit trail.
 *
 * The existing `http.test.ts` matrix pins the 401 statuses; this file pins the
 * side-effect half: a rejected call signs nothing. Audit lines go to stderr, so
 * the test captures `process.stderr.write` for the duration of each call.
 */

import { beforeAll, describe, expect, it, vi } from 'vitest';
import { CertStore } from '../src/certstore';
import { createHttpHandler } from '../src/http';
import { storeFixture, type StoreFixture } from './helpers';

const TOKEN = 'token-de-prueba-0123456789';

let fixture: StoreFixture;
let store: CertStore;

beforeAll(async () => {
	fixture = storeFixture();
	store = await CertStore.load(fixture.config);
});

/** Collect everything the handler writes to stderr while `run` executes. */
async function captureStderr(run: () => Promise<unknown>): Promise<string> {
	const chunks: string[] = [];
	const original = process.stderr.write.bind(process.stderr);
	vi.spyOn(process.stderr, 'write').mockImplementation(((chunk: unknown) => {
		chunks.push(String(chunk));
		return true;
	}) as typeof process.stderr.write);
	try {
		await run();
	} finally {
		process.stderr.write = original;
		vi.restoreAllMocks();
	}
	return chunks.join('');
}

describe('unauthenticated sign signs nothing', () => {
	const post = (authorization?: string) =>
		createHttpHandler({ ...fixture.config, token: TOKEN }, store)(
			new Request('http://localhost:8790/mcp', {
				method: 'POST',
				headers: authorization ? { authorization } : {}
			})
		);

	for (const header of [undefined, 'Bearer wrong-token', 'Bearer ', `Basic ${TOKEN}`, TOKEN]) {
		it(`rejects ${JSON.stringify(header)} with 401 and no sign audit event`, async () => {
			let status = 0;
			const stderr = await captureStderr(async () => {
				const response = await post(header);
				status = response.status;
			});
			expect(status, `header ${JSON.stringify(header)}`).toBe(401);
			expect(stderr).not.toMatch(/"event":"sign"/);
		});
	}
});
