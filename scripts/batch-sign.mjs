/**
 * Massive directory signing + pre-hashed digest mode (Refs #133).
 *
 *   node --import ./scripts/ts-hook.mjs scripts/batch-sign.mjs --p12 <cert.p12> --out <dir> <input-dir> [--format auto|cades|xades|pades]
 *   node --import ./scripts/ts-hook.mjs scripts/batch-sign.mjs digest --p12 <cert.p12> --digest <sha256hex> --name <original> --out <sig.csig>
 *
 * The directory walk is sorted (codepoint order, recursive), one file at a time: read, hash,
 * sign, write, release — peak memory tracks the runtime (and jsdom, for XML batches) plus one
 * file, never the batch. One bad file records ERROR_PRE and the walk continues; exit code is 1
 * when any file failed.
 *
 * The PIN rides in `$P12_PIN` (never in argv, so it stays out of `ps`), like `smoke-real.mjs`.
 * Per-file formats come from `formatForName` unless `--format` overrides; the manifest is built
 * by `buildManifest`, so the CLI cannot drift from the queue's semantics.
 */

import { createHash } from 'node:crypto';
import { mkdirSync, readdirSync, readFileSync, statSync, writeFileSync } from 'node:fs';
import { dirname, join, relative } from 'node:path';

import {
	buildManifest,
	formatForName,
	summarizeBatch,
	signCadesFromDigest
} from '../src/lib/batch.ts';
import { signCadesDetached } from '../src/lib/cms.ts';
import { loadSignerSession } from '../src/lib/keystore.ts';
import { signPdf } from '../src/lib/pades.ts';
import { SIGN_FORMATS, signingAlgorithmName } from '../src/lib/sign.ts';
import { signXades } from '../src/lib/xades.ts';

const args = process.argv.slice(2);
/** @param {string} name @returns {string | null} */
const flag = (name) => {
	const index = args.indexOf(name);
	return index === -1 ? null : (args[index + 1] ?? null);
};
/** @param {number} code @returns {never} */
const usage = (code) => {
	console.error(
		'usage: node --import ./scripts/ts-hook.mjs scripts/batch-sign.mjs --p12 <cert.p12> --out <dir> <input-dir> [--format auto|cades|xades|pades]\n' +
		'       node --import ./scripts/ts-hook.mjs scripts/batch-sign.mjs digest --p12 <cert.p12> --digest <sha256hex> --name <original> --out <sig.csig>'
	);
	process.exit(code);
};

/**
 * Sorted recursive walk: codepoint order at every level, paths relative to `root`. An entry the walk
 * cannot reach — a dangling symlink, an unreadable file or directory — is still returned, so the
 * signing loop records the loss as its row's `ERROR_PRE` instead of the whole batch aborting.
 */
function walk(root) {
	const found = [];
	const visit = (dir) => {
		let entries;
		try {
			entries = readdirSync(dir).sort();
		} catch {
			found.push(relative(root, dir));
			return;
		}
		for (const entry of entries) {
			const path = join(dir, entry);
			let directory = false;
			try {
				directory = statSync(path).isDirectory();
			} catch {
				// Unreachable entry: keep the name so the loop reports it and the batch carries on.
			}
			if (directory) visit(path);
			else found.push(relative(root, path));
		}
	};
	visit(root);
	return found;
}
async function ensureDom() {
	if (globalThis.DOMParser && globalThis.XMLSerializer) return;
	// jsdom is already a devDependency (vitest); the XAdES engine needs a DOMParser, which Node
	// does not ship. Loaded lazily so non-XML batches never pay for it.
	const { JSDOM } = await import('jsdom');
	const dom = new JSDOM('');
	globalThis.DOMParser ??= dom.window.DOMParser;
	globalThis.XMLSerializer ??= dom.window.XMLSerializer;
}

async function signFile(cert, session, bytes, format) {
	const algorithm = signingAlgorithmName(cert);
	if (format === 'cades') {
		return { bytes: await signCadesDetached(cert, bytes), algorithm };
	}
	if (format === 'xades') {
		await ensureDom();
		return {
			bytes: new TextEncoder().encode(
				await signXades({ xml: bytes, certificates: session.certificates, signer: cert })
			),
			algorithm
		};
	}
	return {
		bytes: await signPdf(bytes, {
			certificate: cert.der,
			chain: session.certificates.filter((entry) => entry !== cert).map((entry) => entry.der),
			privateKey: cert.key,
			signingTime: new Date(),
			name: cert.commonName
		}),
		algorithm
	};
}

async function main() {
	if (args[0] === 'digest') {
		const p12Path = flag('--p12');
		const digest = flag('--digest');
		const name = flag('--name');
		const outPath = flag('--out');
		if (!p12Path || !digest || !name || !outPath) usage(2);
		const pin = process.env.P12_PIN;
		if (!pin) {
			console.error('P12_PIN is not set');
			process.exit(2);
		}
		const session = await loadSignerSession(new Uint8Array(readFileSync(p12Path)), pin);
		const signature = await signCadesFromDigest(session.signer, { digest });
		mkdirSync(dirname(outPath), { recursive: true });
		writeFileSync(outPath, signature);
		console.log(`${name} DONE_AND_SAVED ${outPath}`);
		console.log(`verify: openssl cms -verify -inform DER -in ${outPath} -content <original> -CAfile <ca.pem> -purpose any -cades -binary`);
		return;
	}

	const p12Path = flag('--p12');
	const outDir = flag('--out');
	/** @type {import('../src/lib/sign.ts').SignFormat | 'auto'} */
	const formatArg = /** @type {*} */ (flag('--format') ?? 'auto');
	const inputDir = args.find((argument) => !argument.startsWith('--') && argument !== args[args.indexOf('--p12') + 1] && argument !== args[args.indexOf('--out') + 1] && argument !== args[args.indexOf('--format') + 1]);
	if (!p12Path || !outDir || !inputDir) usage(2);
	if (formatArg !== 'auto' && !SIGN_FORMATS.some((option) => option.id === formatArg)) usage(2);
	const pin = process.env.P12_PIN;
	if (!pin) {
		console.error('P12_PIN is not set');
		process.exit(2);
	}

	const session = await loadSignerSession(new Uint8Array(readFileSync(p12Path)), pin);
	const cert = session.signer;
	if (!cert.key || !cert.algorithm) throw new Error('signer session has no key/algorithm');
	const extensionOf = (/** @type {import('../src/lib/sign.ts').SignFormat} */ format) => SIGN_FORMATS.find((option) => option.id === format)?.extension ?? '';
	/** @type {import('../src/lib/batch.ts').BatchState} */
	const state = {
		documents: [],
		phase: 'done',
		format: formatArg === 'auto' ? 'cades' : formatArg,
		current: null
	};
	for (const [index, rel] of walk(inputDir).entries()) {
		const format = formatArg === 'auto' ? formatForName(rel) : formatArg;
		/** @type {import('../src/lib/batch.ts').BatchDocument} */
		const document = {
			id: String(index + 1),
			file: new File([], rel),
			format,
			status: 'pending',
			result: 'NOT_STARTED',
			description: '',
			sha256: '',
			output: '',
			timestamp: '',
			algorithm: '',
			bytes: null
		};
		try {
			const bytes = new Uint8Array(readFileSync(join(inputDir, rel)));
			document.sha256 = createHash('sha256').update(bytes).digest('hex');
			const signed = await signFile(cert, session, bytes, format);
			const output = `${rel}${extensionOf(format)}`;
			mkdirSync(dirname(join(outDir, output)), { recursive: true });
			writeFileSync(join(outDir, output), signed.bytes);
			// `bytes` stays null: the signed bytes were written to disk already, and holding
			// them here would keep the whole batch in memory — the one thing this CLI avoids.
			Object.assign(document, {
				status: 'signed',
				result: 'DONE_AND_SAVED',
				output,
				timestamp: new Date().toISOString(),
				algorithm: signed.algorithm
			});
		} catch (reason) {
			Object.assign(document, {
				status: 'error',
				result: 'ERROR_PRE',
				description: reason instanceof Error ? reason.message : String(reason)
			});
		}
		state.documents.push(document);
	}

	const manifest = buildManifest(state);
	mkdirSync(outDir, { recursive: true });
	writeFileSync(join(outDir, 'manifest.json'), `${JSON.stringify(manifest, null, 2)}\n`);

	const report = summarizeBatch(state);
	for (const row of report.rows) {
		console.log(
			`${row.result === 'DONE_AND_SAVED' ? 'ok  ' : 'FAIL'} ${row.name} [${row.format}] ${row.sha256.slice(0, 12)}… ${row.output || row.detail}`
		);
	}
	console.log(`\nsigned ${report.signed}, failed ${report.failed} → ${join(outDir, 'manifest.json')}`);
	if (report.failed > 0) process.exit(1);
}

await main();
