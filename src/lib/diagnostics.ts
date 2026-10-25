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
	// `signed` is transferred to the verify call, so its length must be read first.
	const signedBytes = signOut.signed.byteLength;
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
		signedBytes,
		verified,
		pass: verified && signMs <= PDF_BUDGET.signMs && verifyMs <= PDF_BUDGET.verifyMs
};
}
/**
 * Runtime probes for the load-bearing question of #19: does WebCrypto work inside the
 * desktop webview (`tauri://localhost` on WebKitGTK)? The diagnostics page renders the
 * answer in-app; the run recorded below is what answered the question. CI only asserts
 * the webview boots (`SHELL_SMOKE Finished` in the `shell` job), not the probe values.
 *
 * ANSWERED 2026-10-21 on `ubuntu:24.04`, `webkit2gtk-4.1`, `tauri 2.12.4`, production
 * mode (`--features tauri/custom-protocol`), driven headlessly under `xvfb-run`:
 * `isSecureContext=true`, origin `tauri://localhost`, SHA-256("abc") matches the NIST
 * vector, `importKey(pkcs8)` round-trips a 1218-byte RSA key, AES-CBC and RSA-PSS both
 * succeed. The `axum` fallback is not needed.
 *
 * Each probe is something the signing engine really does: SHA-256 digests, PKCS#8 key
 * import, AES-CBC (PKCS#12 PBE output) and RSA-PSS (signature algorithm).
 */

export type ProbeId =
	| 'secure-context'
	| 'crypto-subtle'
	| 'sha256'
	| 'pkcs8'
	| 'aes-cbc'
	| 'rsa-pss';

export interface ProbeResult {
	id: ProbeId;
	ok: boolean;
	detail: string;
}

const hex = (bytes: ArrayBuffer): string =>
	Array.from(new Uint8Array(bytes), (byte) => byte.toString(16).padStart(2, '0')).join('');

/** `abc` is the NIST vector; a wrong digest means the webview has a broken crypto backend. */
const SHA256_ABC = 'ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad';

async function check(id: ProbeId, run: () => Promise<string>): Promise<ProbeResult> {
	try {
		return { id, ok: true, detail: await run() };
	} catch (error) {
		return { id, ok: false, detail: error instanceof Error ? error.message : String(error) };
	}
}

export async function runWebCryptoProbes(): Promise<ProbeResult[]> {
	const secure = typeof window === 'undefined' ? null : window.isSecureContext;
	const results: ProbeResult[] = [
		{
			id: 'secure-context',
			ok: secure !== false,
			detail: secure === null ? 'no window (non-browser host)' : `isSecureContext=${secure}`
		}
	];

	results.push(
		await check('crypto-subtle', async () => {
			if (typeof crypto?.subtle?.digest !== 'function') throw new Error('crypto.subtle.digest missing');
			return `subtle present (${globalThis.isSecureContext === undefined ? 'node' : 'webview'})`;
		})
	);

	results.push(
		await check('sha256', async () => {
			const digest = hex(await crypto.subtle.digest('SHA-256', new TextEncoder().encode('abc')));
			if (digest !== SHA256_ABC) throw new Error(`wrong SHA-256 for "abc": ${digest}`);
			return `SHA-256("abc") = ${digest.slice(0, 16)}…`;
		})
	);

	results.push(
		await check('pkcs8', async () => {
			const pair = await crypto.subtle.generateKey(
				{ name: 'RSA-PSS', modulusLength: 2048, publicExponent: new Uint8Array([1, 0, 1]), hash: 'SHA-256' },
				true,
				['sign', 'verify']
			);
			const pkcs8 = await crypto.subtle.exportKey('pkcs8', pair.privateKey);
			const imported = await crypto.subtle.importKey('pkcs8', pkcs8, { name: 'RSA-PSS', hash: 'SHA-256' }, false, [
				'sign'
			]);
			return `importKey(pkcs8) -> ${imported.algorithm.name} ${pkcs8.byteLength} bytes`;
		})
	);

	results.push(
		await check('aes-cbc', async () => {
			const key = await crypto.subtle.generateKey({ name: 'AES-CBC', length: 256 }, false, ['encrypt', 'decrypt']);
			const iv = crypto.getRandomValues(new Uint8Array(16));
			const plain = new TextEncoder().encode('autofirme aes-cbc probe');
			const sealed = await crypto.subtle.encrypt({ name: 'AES-CBC', iv }, key, plain);
			const opened = new Uint8Array(await crypto.subtle.decrypt({ name: 'AES-CBC', iv }, key, sealed));
			if (new TextDecoder().decode(opened) !== 'autofirme aes-cbc probe') {
				throw new Error('AES-CBC round trip produced different bytes');
			}
			return `AES-CBC 256 round trip ok (${sealed.byteLength} bytes)`;
		})
	);

	results.push(
		await check('rsa-pss', async () => {
			const pair = await crypto.subtle.generateKey(
				{ name: 'RSA-PSS', modulusLength: 2048, publicExponent: new Uint8Array([1, 0, 1]), hash: 'SHA-256' },
				true,
				['sign', 'verify']
			);
			const message = new TextEncoder().encode('autofirme rsa-pss probe');
			const signature = await crypto.subtle.sign({ name: 'RSA-PSS', saltLength: 32 }, pair.privateKey, message);
			const good = await crypto.subtle.verify({ name: 'RSA-PSS', saltLength: 32 }, pair.publicKey, signature, message);
			message[0] ^= 0xff;
			const tampered = await crypto.subtle.verify({ name: 'RSA-PSS', saltLength: 32 }, pair.publicKey, signature, message);
			if (!good || tampered) throw new Error(`verify=${good} on original, ${tampered} on tampered`);
			return `RSA-PSS sign/verify ok, tampered rejected (${signature.byteLength} bytes)`;
		})
	);

	return results;
}
