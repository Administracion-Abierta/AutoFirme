/**
 * `/diagnostics` budget: a 50 MB PAdES sign+verify must stay inside the declared
 * ceilings (Refs #94). Same ceilings as `scripts/perf-budget.mjs` so CI and the
 * shell smoke agree on what "too slow" means.
 */

import { runEngineSync, type EngineRequest } from './engine';

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

/**
 * Runs the 50 MB sign+verify budget through any engine runner — the worker client
 * in the browser, `runEngineSync` in tests. Progress posts keep the UI alive.
 */
export async function runBudget(
	request: Omit<Extract<EngineRequest, { kind: 'sign-pades' }>, 'pdf'>,
	run: (request: EngineRequest, onProgress?: (phase: string, fraction: number | null) => void) => Promise<unknown>,
	onProgress?: (phase: string, fraction: number | null) => void
): Promise<BudgetResult> {
	const pdf = pdfWithContent(PDF_BUDGET.bytes);
	const pdfCopy = pdf.slice().buffer as ArrayBuffer;
	const signStart = performance.now();
	const signOut = (await run({ ...request, pdf: pdfCopy }, onProgress)) as { signed: ArrayBuffer };
	const signMs = Math.round(performance.now() - signStart);
	onProgress?.('verify', null);
	const verifyStart = performance.now();
	await run({ kind: 'verify', artifact: signOut.signed }, onProgress);
	const verifyMs = Math.round(performance.now() - verifyStart);
	onProgress?.('done', 1);
	return {
		signMs,
		verifyMs,
		signedBytes: signOut.signed.byteLength,
		pass: signMs <= PDF_BUDGET.signMs && verifyMs <= PDF_BUDGET.verifyMs
	};
}
