/**
 * Performance budgets for the surfaces that grow with input size (Refs #35).
 *
 *   pnpm perf
 *
 * Measures a cold import of the signing core, a PAdES signature over a structurally minimal
 * 50 MB PDF (the byte range covers all of it), and a 500-file CAdES batch through the real queue.
 * Budgets are absolute ceilings roughly 10× the observed times on a developer machine, so runner
 * noise cannot flip them while a real regression (quadratic scan, per-file key import) still does.
 *
 * Node's type stripping plus `scripts/ts-hook.mjs` let this import the app's TypeScript directly.
 */
import { rmSync } from 'node:fs';

const BUDGET_MS = {
	coldStart: 3_000,
	pdf50mb: 30_000,
	batch500: 60_000,
};
const MIB = 1024 * 1024;

function pdfWithContent(size) {
	const content = Buffer.alloc(size, 0x20);
	const bodies = [
		'<< /Type /Catalog /Pages 2 0 R >>',
		'<< /Type /Pages /Kids [3 0 R] /Count 1 >>',
		'<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R /Resources << >> >>',
		Buffer.concat([
			Buffer.from(`<< /Length ${content.length} >>\nstream\n`),
			content,
			Buffer.from('\nendstream'),
		]),
	];
	const parts = [Buffer.from('%PDF-1.7\n')];
	const offsets = [];
	let length = parts[0].length;
	for (const [index, body] of bodies.entries()) {
		offsets.push(length);
		const object = Buffer.concat([
			Buffer.from(`${index + 1} 0 obj\n`),
			Buffer.isBuffer(body) ? body : Buffer.from(body),
			Buffer.from('\nendobj\n'),
		]);
		parts.push(object);
		length += object.length;
	}
	const xref = [
		`xref\n0 ${bodies.length + 1}\n0000000000 65535 f \n`,
		...offsets.map((offset) => `${String(offset).padStart(10, '0')} 00000 n \n`),
		`trailer\n<< /Size ${bodies.length + 1} /Root 1 0 R >>\nstartxref\n${length}\n%%EOF\n`,
	];
	parts.push(Buffer.from(xref.join('')));
	return Buffer.concat(parts);
}

async function timed(label, budget, run) {
	const started = performance.now();
	const detail = await run();
	const elapsed = Math.round(performance.now() - started);
	const verdict = elapsed <= budget ? 'ok  ' : 'FAIL';
	console.log(`${verdict} ${label.padEnd(24)} ${String(elapsed).padStart(6)} ms  (budget ${budget} ms)${detail ? `  ${detail}` : ''}`);
	return elapsed <= budget;
}

const started = performance.now();
const [{ signPdf }, { loadSignerSession }, { signCadesDetached }, { createBatchQueue }, fixtures] =
	await Promise.all([
		import('../src/lib/pades.ts'),
		import('../src/lib/keystore.ts'),
		import('../src/lib/cms.ts'),
		import('../src/lib/batch.ts'),
		import('../src/test/fixtures.ts'),
	]);
const coldStart = Math.round(performance.now() - started);

const generated = fixtures.createPkcs12Fixtures();
try {
	const session = await loadSignerSession(generated.modern, fixtures.FIXTURE_PIN);
	const signer = session.signer;
	if (!signer.key) throw new Error('fixture signer has no private key');
	const privateKey = signer.key;

	let ok = coldStart <= BUDGET_MS.coldStart;
	console.log(`${ok ? 'ok  ' : 'FAIL'} ${'cold start'.padEnd(24)} ${String(coldStart).padStart(6)} ms  (budget ${BUDGET_MS.coldStart} ms)  signing core imported`);

	const pdf = pdfWithContent(50 * MIB);
	ok = (await timed('50 MB PDF sign', BUDGET_MS.pdf50mb, async () => {
		const signed = await signPdf(pdf, {
			certificate: signer.der,
			privateKey,
			signingTime: new Date('2026-10-21T00:00:00Z'),
		});
		return `${pdf.length} B input → ${signed.length} B signed`;
	})) && ok;

	const files = Array.from({ length: 500 }, (_, index) =>
		new File([`document ${index + 1}\n`], `document-${index + 1}.txt`, { type: 'text/plain' }));
	const queue = createBatchQueue();
	queue.set(files, 'cades');
	ok = (await timed('500-file batch', BUDGET_MS.batch500, async () => {
		const state = await queue.run(async (_file, bytes) => ({
			bytes: await signCadesDetached(signer, bytes),
			algorithm: 'SHA256withRSA',
		}));
		const signed = state.documents.filter((document) => document.status === 'signed').length;
		if (signed !== files.length) throw new Error(`only ${signed}/${files.length} documents signed`);
		return `${signed} documents`;
	})) && ok;

	if (!ok) {
		console.error('perf: budget exceeded');
		process.exitCode = 1;
	}
} finally {
	rmSync(generated.dir, { recursive: true, force: true });
}
