// @vitest-environment node
/**
 * 50 MB PAdES sign+verify budget (Refs #94) + WebCrypto probes (Refs #75).
 * Both share `./diagnostics`; neither side reverted.
 */

import { rmSync } from 'node:fs';
import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { PDF_BUDGET, pdfWithContent, runBudget, runWebCryptoProbes } from './diagnostics';
import { runEngineSync, transferList, type EngineRequest, type EngineResult } from './engine';
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


/**
 * Runs the engine across a transfer boundary like the worker does: the clone
 * detaches every transferred buffer, so a length read after the sign step would
 * silently become 0.
 */
async function runThroughTransfer(request: EngineRequest): Promise<EngineResult> {
	const cloned = structuredClone(request, { transfer: transferList(request) }) as EngineRequest;
	const result = await runEngineSync(cloned);
	return structuredClone(result, {
		transfer: result.kind === 'sign-pades' ? [result.signed] : []
	}) as EngineResult;
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
			runThroughTransfer,
			undefined,
			trust
		);
		// Transfer detaches the signed buffer in the verify step; a 0 here means the
		// length was read after the hand-off.
		expect(result.verified).toBe(true);
		expect(result.signedBytes).toBeGreaterThan(PDF_BUDGET.bytes);
		expect(result.pass).toBe(true);
		expect(result.signMs).toBeLessThanOrEqual(PDF_BUDGET.signMs);
		expect(result.verifyMs).toBeLessThanOrEqual(PDF_BUDGET.verifyMs);
	}, 180_000);
});

describe('runWebCryptoProbes', () => {
	it('runs every probe successfully on a WebCrypto host', async () => {
		const results = await runWebCryptoProbes();
		expect(results.map((result) => result.id)).toEqual([
			'secure-context',
			'crypto-subtle',
			'sha256',
			'pkcs8',
			'aes-cbc',
			'rsa-pss'
		]);
		expect(results.filter((result) => !result.ok)).toEqual([]);
	});
});
