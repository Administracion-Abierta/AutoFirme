/**
 * `/diagnostics` budget: a 50 MB PAdES sign+verify must stay inside the declared
 * ceilings (Refs #94). Same ceilings as `scripts/perf-budget.mjs` so CI and the
 * shell smoke agree on what "too slow" means.
 */

import { runEngineSync, type EngineRequest, type EngineResult } from './engine';

export const PDF_BUDGET = {
	/** Bytes of the synthetic PDF the budget is asserted against. */
	bytes: 50 * 1024 * 1024,
	signMs: 30_000,
	verifyMs: 30_000
} as const;

export interface BudgetResult {
	signMs: number;
	verifyMs: number;
	signedBytes: number;
	/** Verify step accepted the produced signature — timing alone never passes. */
	verified: boolean;
	pass: boolean;
}

/** Structurally minimal PDF of `size` bytes, zero-filled content stream. */
export function pdfWithContent(size: number): Uint8Array<ArrayBuffer> {
	const content = new Uint8Array(size).fill(0x20);
	const encoder = new TextEncoder();
	const bodies: Uint8Array[] = [
		encoder.encode('<< /Type /Catalog /Pages 2 0 R >>'),
		encoder.encode('<< /Type /Pages /Kids [3 0 R] /Count 1 >>'),
		encoder.encode('<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R /Resources << >> >>'),
		concat(
			encoder.encode(`<< /Length ${content.length} >>\nstream\n`),
			content,
			encoder.encode('\nendstream')
		)
	];
	const header = encoder.encode('%PDF-1.7\n');
	const parts: Uint8Array[] = [header];
	const offsets: number[] = [];
	let length = header.length;
	for (let index = 0; index < bodies.length; index++) {
		offsets.push(length);
		const object = concat(encoder.encode(`${index + 1} 0 obj\n`), bodies[index], encoder.encode('\nendobj\n'));
		parts.push(object);
		length += object.length;
	}
	const xref = encoder.encode(
		`xref\n0 ${bodies.length + 1}\n0000000000 65535 f \n` +
			offsets.map((offset) => `${String(offset).padStart(10, '0')} 00000 n \n`).join('') +
			`trailer\n<< /Size ${bodies.length + 1} /Root 1 0 R >>\nstartxref\n${length}\n%%EOF\n`
	);
	parts.push(xref);
	return concat(...parts);
}

function concat(...chunks: Uint8Array[]): Uint8Array<ArrayBuffer> {
	const total = chunks.reduce((sum, chunk) => sum + chunk.length, 0);
	const out = new Uint8Array(total);
	let at = 0;
	for (const chunk of chunks) {
		out.set(chunk, at);
		at += chunk.length;
	}
	return out;
}

export type EngineRunner = (
	request: EngineRequest,
	onProgress?: (phase: string, fraction: number | null) => void
) => Promise<EngineResult>;

/**
 * Runs the 50 MB sign+verify budget through any engine runner — the worker client
 * in the browser, `runEngineSync` in tests. Progress posts keep the UI alive.
 * `trustedRoots` anchors the verify step (the fixture leaf is self-signed).
 */
export async function runBudget(
	request: Omit<Extract<EngineRequest, { kind: 'sign-pades' }>, 'pdf'>,
	run: EngineRunner = runEngineSync,
	onProgress?: (phase: string, fraction: number | null) => void,
	trustedRoots?: string[]
): Promise<BudgetResult> {
	const pdf = pdfWithContent(PDF_BUDGET.bytes);
	const pdfCopy = pdf.slice().buffer as ArrayBuffer;
	const signStart = performance.now();
	const signOut = await run({ ...request, pdf: pdfCopy }, onProgress);
	if (signOut.kind !== 'sign-pades') throw new Error('engine returned no signature');
	const signMs = Math.round(performance.now() - signStart);
	onProgress?.('verify', null);
	const verifyStart = performance.now();
	const verifyOut = await run({ kind: 'verify', artifact: signOut.signed, trustedRoots }, onProgress);
	const verifyMs = Math.round(performance.now() - verifyStart);
	if (verifyOut.kind !== 'verify') throw new Error('engine returned no verification report');
	onProgress?.('done', 1);
	const verified = verifyOut.report.ok;
	return {
		signMs,
		verifyMs,
		signedBytes: signOut.signed.byteLength,
		verified,
		pass: verified && signMs <= PDF_BUDGET.signMs && verifyMs <= PDF_BUDGET.verifyMs
	};
}
