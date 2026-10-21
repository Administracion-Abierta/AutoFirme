/**
 * 50 MB PAdES sign+verify budget (Refs #94). Fails the shell CI job when the
 * engine exceeds the declared ceilings — same ceilings as `scripts/perf-budget.mjs`.
 */

import { rmSync } from 'node:fs';
import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { PDF_BUDGET, pdfWithContent, runBudget } from './diagnostics';
import { runEngineSync } from './engine';
import { loadSignerSession } from './keystore';
import {
	createPkcs12Fixtures,
	FIXTURE_PIN,
	type Pkcs12Fixtures
} from '../test/fixtures';

/** DER → PEM trust anchor for the self-signed fixture leaf. */
function derToPem(der: Uint8Array): string {
	const base64 = Buffer.from(der.buffer, der.byteOffset, der.byteLength).toString('base64');
	return `-----BEGIN CERTIFICATE-----\n${base64.match(/.{1,64}/g)!.join('\n')}\n-----END CERTIFICATE-----\n`;
}

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
		const certificate = signer.der.buffer.slice(
			signer.der.byteOffset,
			signer.der.byteOffset + signer.der.byteLength
		) as ArrayBuffer;
		const trust = [derToPem(signer.der)];
		const result = await runBudget(
			{
				kind: 'sign-pades',
				certificate,
				chain: [],
				privateKey: signer.key,
				signingTime: Date.UTC(2026, 9, 21)
			},
			(request) => runEngineSync(request),
			undefined,
			trust
		);
		expect(result.verified).toBe(true);
		expect(result.pass).toBe(true);
		expect(result.signMs).toBeLessThanOrEqual(PDF_BUDGET.signMs);
		expect(result.verifyMs).toBeLessThanOrEqual(PDF_BUDGET.verifyMs);
	}, 180_000);
});
