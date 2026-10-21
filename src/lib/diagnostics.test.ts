/**
 * 50 MB PAdES sign+verify budget (Refs #94). Fails the shell CI job when the
 * engine exceeds the declared ceilings — same ceilings as `scripts/perf-budget.mjs`.
 */

import { rmSync } from 'node:fs';
import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { PDF_BUDGET, pdfWithContent, runBudget } from './diagnostics';
import { runEngineSync } from './engine';
import { loadSignerSession } from './keystore';
import { verifyPades } from './verify/pades';
import {
	createPkcs12Fixtures,
	FIXTURE_PIN,
	type Pkcs12Fixtures
} from '../test/fixtures';

let fixtures: Pkcs12Fixtures;

beforeAll(() => {
	fixtures = createPkcs12Fixtures();
}, 60_000);

afterAll(() => {
	rmSync(fixtures.dir, { recursive: true, force: true });
});

describe('50 MB PAdES budget', () => {
	it('builds a 50 MB input', () => {
		const pdf = pdfWithContent(PDF_BUDGET.bytes);
		expect(pdf.byteLength).toBeGreaterThanOrEqual(PDF_BUDGET.bytes);
	}, 120_000);

	it('signs and verifies inside the budget', async () => {
		const { signer } = await loadSignerSession(fixtures.modern, FIXTURE_PIN);
		if (!signer.key) throw new Error('fixture signer has no private key');
		const result = await runBudget(
			{
				kind: 'sign-pades',
				certificate: signer.der.buffer.slice(
					signer.der.byteOffset,
					signer.der.byteOffset + signer.der.byteLength
				) as ArrayBuffer,
				chain: [],
				privateKey: signer.key,
				signingTime: Date.UTC(2026, 9, 21)
			},
			(request) => runEngineSync(request)
		);
		expect(result.pass).toBe(true);
		expect(result.signMs).toBeLessThanOrEqual(PDF_BUDGET.signMs);
		expect(result.verifyMs).toBeLessThanOrEqual(PDF_BUDGET.verifyMs);
	}, 120_000);

	it('round-trips through the worker path', async () => {
		const { signer } = await loadSignerSession(fixtures.modern, FIXTURE_PIN);
		if (!signer.key) throw new Error('fixture signer has no private key');
		const pdf = pdfWithContent(1024 * 1024).slice().buffer as ArrayBuffer;
		const out = await runEngineSync({
			kind: 'sign-pades',
			pdf,
			certificate: signer.der.buffer.slice(
				signer.der.byteOffset,
				signer.der.byteOffset + signer.der.byteLength
			) as ArrayBuffer,
			chain: [],
			privateKey: signer.key,
			signingTime: Date.UTC(2026, 9, 21)
		});
		if (out.kind !== 'sign-pades') throw new Error('expected a signature');
		const report = await verifyPades(new Uint8Array(out.signed) as Uint8Array<ArrayBuffer>);
		expect(report.ok).toBe(true);
	});
});
