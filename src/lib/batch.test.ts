/**
 * Batch queue behaviour, driven by the **real** signing engine (#5 PKCS#12 + #6 CAdES-BES): the
 * queue, the digest, the manifest and the ZIP are exercised with genuine signatures, and every
 * output is cross-checked by `openssl cms -verify` — the same oracle #6 uses — and, for the
 * archive, by Node's independent CRC-32.
 */

import { execFileSync, spawnSync } from 'node:child_process';
import { webcrypto } from 'node:crypto';
import { mkdirSync, readFileSync, rmSync, symlinkSync, writeFileSync } from 'node:fs';
import { join } from 'node:path';
import { crc32 as nodeCrc32 } from 'node:zlib';
import { afterAll, beforeAll, describe, expect, it, vi } from 'vitest';
import { get } from 'svelte/store';
import {
	buildManifest,
	buildZip,
	createBatchQueue,
	formatForName,
	sha256Hex,
	signCadesFromDigest,
	summarizeBatch,
	type BatchSigner,
	type SignedDocument
} from './batch';
import { signCadesDetached } from './cms';
import { loadSignerSession } from './keystore';
import type { SignerCert } from './sign';
import { signingAlgorithmName } from './sign';
import { createCadesFixtures, FIXTURE_PIN, writeFixtureFile, type CadesFixtures } from '../test/fixtures';

// jsdom's `crypto` only has getRandomValues; the queue's SHA-256 needs SubtleCrypto — the same API
// browsers expose.
if (!globalThis.crypto?.subtle) {
	Object.defineProperty(globalThis, 'crypto', { value: webcrypto, configurable: true, writable: true });
}

let fixtures: CadesFixtures;
/** The CA-signed leaf: it owns the private key, so it can sign. */
let certificate: SignerCert;
/** The CA certificate of the same container: in the list, but with no key — the real failure path. */
let keyless: SignerCert;

beforeAll(async () => {
	fixtures = createCadesFixtures();
	const session = await loadSignerSession(fixtures.p12, FIXTURE_PIN);
	certificate = session.signer;
	keyless = session.certificates.find((candidate) => candidate.key === null)!;
}, 60_000);

afterAll(() => rmSync(fixtures.dir, { recursive: true, force: true }));

const file = (name: string, content: string) => new File([content], name, { type: 'text/plain' });

/** The real engine behind the queue: `#lib/cms` over the certificate the container provided. */
type Engine = (file: File, bytes: Uint8Array<ArrayBuffer>) => Promise<SignedDocument>;

const cadesEngine = (cert: SignerCert = certificate): Engine =>
	async (_file, bytes) => ({
		bytes: await signCadesDetached(cert, bytes),
		algorithm: signingAlgorithmName(cert)
	});

/** `null` when openssl accepts the signature, its diagnostics otherwise. */
function verify(signature: Uint8Array, contentPath: string, name: string): string | null {
	const signaturePath = join(fixtures.dir, name);
	writeFileSync(signaturePath, signature);
	try {
		execFileSync(
			'openssl',
			[
				'cms', '-verify', '-inform', 'DER', '-in', signaturePath, '-content', contentPath,
				'-CAfile', fixtures.caPath, '-purpose', 'any', '-cades', '-binary', '-out',
				join(fixtures.dir, 'verified.bin')
			],
			{ stdio: 'pipe' }
		);
		return null;
	} catch (error) {
		return (error as { stderr?: Buffer }).stderr?.toString() ?? String(error);
	}
}

/** Writes the document next to the signatures and returns the path openssl will read. */
function contentPath(name: string, bytes: Uint8Array): string {
	const path = join(fixtures.dir, `${name}.content`);
	writeFileSync(path, bytes);
	return path;
}

/** Independent ZIP reader — walks the central directory instead of trusting the writer. */
function readZip(archive: Uint8Array<ArrayBuffer>) {
	const view = new DataView(archive.buffer);
	const end = archive.length - 22;
	expect(view.getUint32(end, true)).toBe(0x06054b50);
	const count = view.getUint16(end + 10, true);
	const entries: { name: string; data: Uint8Array; crc: number }[] = [];
	let cursor = view.getUint32(end + 16, true);
	for (let index = 0; index < count; index += 1) {
		expect(view.getUint32(cursor, true)).toBe(0x02014b50);
		const nameLength = view.getUint16(cursor + 28, true);
		const local = view.getUint32(cursor + 42, true);
		const name = new TextDecoder().decode(archive.subarray(cursor + 46, cursor + 46 + nameLength));
		const localNameLength = view.getUint16(local + 26, true);
		const extraLength = view.getUint16(local + 28, true);
		const size = view.getUint32(local + 18, true);
		const start = local + 30 + localNameLength + extraLength;
		entries.push({
			name,
			data: archive.subarray(start, start + size),
			crc: view.getUint32(local + 14, true)
		});
		cursor += 46 + nameLength;
	}
	return entries;
}

describe('batch queue', () => {
	it('signs a 3-file batch one document at a time and leaves every signature openssl-verifiable', async () => {
		const queue = createBatchQueue();
		queue.set([file('a.txt', 'uno'), file('b.txt', 'dos'), file('c.txt', 'tres')], 'cades');

		const order: string[] = [];
		let running = 0;
		const engine = cadesEngine();
		const sign: BatchSigner = async (document, bytes) => {
			running += 1;
			try {
				// Sequential processing: the queue must never have two signatures in flight.
				expect(running).toBe(1);
				order.push(document.name);
				// The state machine publishes progress before the engine resolves, so the UI can paint.
				expect(get(queue).phase).toBe('running');
				expect(get(queue).documents.find((item) => item.file === document)?.status).toBe('signing');
				return await engine(document, bytes);
			} finally {
				running -= 1;
			}
		};

		const state = await queue.run(sign);

		expect(order).toEqual(['a.txt', 'b.txt', 'c.txt']);
		expect(state.phase).toBe('done');
		expect(state.current).toBeNull();
		expect(state.documents.map((document) => document.status)).toEqual([
			'signed',
			'signed',
			'signed'
		]);
		expect(state.documents.map((document) => document.output)).toEqual([
			'a.txt.csig',
			'b.txt.csig',
			'c.txt.csig'
		]);

		for (const [index, document] of state.documents.entries()) {
			const bytes = new Uint8Array(await document.file.arrayBuffer());
			expect(document.sha256).toBe(await sha256Hex(bytes));
			expect(
				verify(document.bytes!, contentPath(`doc-${index}`, bytes), `doc-${index}.csig`)
			).toBeNull();
		}
	});

	it('keeps going when one document fails, and re-running only retries what is not signed', async () => {
		const queue = createBatchQueue();
		queue.set([file('a.txt', 'uno'), file('b.txt', 'dos'), file('c.txt', 'tres')], 'cades');

		const tried: string[] = [];
		// A certificate that cannot sign: every document it touches fails for real.
		const flaky: BatchSigner = async (document, bytes) => {
			tried.push(document.name);
			if (document.name === 'b.txt') return cadesEngine(keyless)(document, bytes);
			return cadesEngine()(document, bytes);
		};

		const state = await queue.run(flaky);

		expect(tried).toEqual(['a.txt', 'b.txt', 'c.txt']);
		expect(state.documents.map((document) => document.status)).toEqual([
			'signed',
			'error',
			'signed'
		]);
		const failed = state.documents[1];
		expect(failed.description).toMatch(/no private key/);
		expect(failed.result).toBe('ERROR_PRE');
		expect(failed.sha256).toBe(await sha256Hex(new Uint8Array(await failed.file.arrayBuffer())));
		expect(failed.bytes).toBeNull();

		const manifest = buildManifest(state);
		expect(manifest.singlesigns.map((entry) => entry.result)).toEqual([
			'DONE_AND_SAVED',
			'ERROR_PRE',
			'DONE_AND_SAVED'
		]);
		expect(manifest.singlesigns[1].description).toMatch(/no private key/);
		expect(manifest.singlesigns[0]).not.toHaveProperty('description');
		expect(manifest.singlesigns[0]).toHaveProperty('timestamp');
		expect(manifest.singlesigns[1]).not.toHaveProperty('timestamp');
		expect(manifest.signbatch).toEqual({
			format: 'CAdES-BES',
			suboperation: 'sign',
			algorithm: 'SHA256withRSA'
		});

		// Resumable: the second pass signs only what is still missing.
		const resumed: string[] = [];
		const final = await queue.run(async (document, bytes) => {
			resumed.push(document.name);
			return cadesEngine()(document, bytes);
		});

		expect(resumed).toEqual(['b.txt']);
		expect(final.documents.map((document) => document.status)).toEqual([
			'signed',
			'signed',
			'signed'
		]);
		expect(final.documents[1].result).toBe('DONE_AND_SAVED');
		expect(final.documents[1].description).toBe('');
		const retried = final.documents[1];
		expect(
			verify(
				retried.bytes!,
				contentPath('b.txt', new Uint8Array(await retried.file.arrayBuffer())),
				'resumed.csig'
			)
		).toBeNull();
	});

	it('abandons a run whose document list was replaced while a signature was in flight', async () => {
		const queue = createBatchQueue();
		queue.set([file('a.txt', 'uno'), file('b.txt', 'dos'), file('c.txt', 'tres')], 'cades');

		const signed: string[] = [];
		let release = () => {};
		const gate = new Promise<void>((resolve) => (release = resolve));
		const engine: BatchSigner = async (document, bytes) => {
			signed.push(document.name);
			await gate;
			return cadesEngine()(document, bytes);
		};

		const run = queue.run(engine);
		// The user drops another document while `a.txt` is still being signed.
		queue.set([file('nuevo.txt', 'cuatro')], 'cades');
		release();
		const state = await run;

		// The abandoned documents are neither signed nor recorded against the new list's entries.
		expect(signed).toEqual(['a.txt']);
		expect(state.phase).toBe('idle');
		expect(state.current).toBeNull();
		expect(
			state.documents.map((document) => [document.file.name, document.status, document.sha256])
		).toEqual([['nuevo.txt', 'pending', '']]);
	});

	it('stops at the next document when cancelled and resumes there on the following run', async () => {
		const queue = createBatchQueue();
		queue.set([file('a.txt', 'uno'), file('b.txt', 'dos'), file('c.txt', 'tres')], 'cades');

		const signed: string[] = [];
		let release = () => {};
		const gate = new Promise<void>((resolve) => (release = resolve));
		const engine: BatchSigner = async (document, bytes) => {
			signed.push(document.name);
			await gate;
			return cadesEngine()(document, bytes);
		};

		// A no-op while idle: there is no run to stop.
		queue.cancel();
		expect(get(queue).phase).toBe('idle');

		const run = queue.run(engine);
		// The loop starts the first document synchronously, so the cancel lands while `a.txt` is in
		// flight — exactly what the button does.
		expect(get(queue).current).toBe('1');
		queue.cancel();
		release();
		const state = await run;

		// The signature already in flight is kept (WebCrypto cannot be aborted mid-call); the two
		// documents behind it were never started.
		expect(signed).toEqual(['a.txt']);
		expect(state.phase).toBe('cancelled');
		expect(state.current).toBeNull();
		expect(state.documents.map((document) => document.status)).toEqual([
			'signed',
			'pending',
			'pending'
		]);
		expect(state.documents.map((document) => document.result)).toEqual([
			'DONE_AND_SAVED',
			'NOT_STARTED',
			'NOT_STARTED'
		]);
		expect(state.documents[1].sha256).toBe('');
		const kept = state.documents[0];
		expect(
			verify(
				kept.bytes!,
				contentPath('cancelled', new Uint8Array(await kept.file.arrayBuffer())),
				'cancelled.csig'
			)
		).toBeNull();

		// The next run picks up where the cancelled one stopped, and is not cancelled itself.
		const resumed: string[] = [];
		const final = await queue.run(async (document, bytes) => {
			resumed.push(document.name);
			return cadesEngine()(document, bytes);
		});

		expect(resumed).toEqual(['b.txt', 'c.txt']);
		expect(final.phase).toBe('done');
		expect(final.documents.map((document) => document.status)).toEqual([
			'signed',
			'signed',
			'signed'
		]);
	});

	it('packages the signed documents and the manifest into a ZIP with unique names', async () => {
		const queue = createBatchQueue();
		queue.set([file('dup.txt', 'uno'), file('dup.txt', 'dos')], 'cades');
		const state = await queue.run(cadesEngine());

		const archive = buildZip(state);
		const entries = readZip(archive);

		expect(entries.map((entry) => entry.name)).toEqual([
			'dup.txt.csig',
			'dup-2.txt.csig',
			'manifest.json'
		]);
		for (const entry of entries) {
			// Node's CRC-32 is an independent implementation of what the header claims.
			expect(entry.crc).toBe(nodeCrc32(entry.data));
		}
		expect(entries[0].data).toEqual(state.documents[0].bytes);
		expect(entries[1].data).toEqual(state.documents[1].bytes);

		const manifest = JSON.parse(new TextDecoder().decode(entries[2].data));
		expect(manifest.singlesigns.map((entry: { datareference: string }) => entry.datareference)).toEqual([
			'dup.txt',
			'dup.txt'
		]);
		expect(manifest.singlesigns.map((entry: { sha256: string }) => entry.sha256)).toEqual([
			state.documents[0].sha256,
			state.documents[1].sha256
		]);

		// What came out of the archive is a signature openssl accepts over the original document.
		const original = contentPath('dup', new Uint8Array(await state.documents[0].file.arrayBuffer()));
		expect(verify(entries[0].data, original, 'dup.txt.csig')).toBeNull();
	});
});

describe('per-document format', () => {
	it('signs each document with its own format and names the outputs accordingly', async () => {
		const queue = createBatchQueue();
		queue.set([file('a.txt', 'uno'), file('b.txt', 'dos')], 'cades');
		queue.setFormat('2', 'xades');

		const formats: string[] = [];
		const state = await queue.run((_file, _bytes, format) => {
			formats.push(format);
			return Promise.resolve({ bytes: new Uint8Array(0) });
		});

		expect(formats).toEqual(['cades', 'xades']);
		expect(state.documents.map((document) => [document.output, document.format])).toEqual([
			['a.txt.csig', 'cades'],
			['b.txt.xsig', 'xades']
		]);
	});

	it('labels the manifest per entry and joins the batch formats when they differ', async () => {
		const queue = createBatchQueue();
		queue.set([file('a.txt', 'uno'), file('b.txt', 'dos')], 'cades');
		queue.setFormat('2', 'pades');

		const manifest = buildManifest(get(queue));

		expect(manifest.signbatch.format).toBe('CAdES-BES, PAdES-B-B');
		expect(manifest.singlesigns.map((entry) => entry.format)).toEqual([
			'CAdES-BES',
			'PAdES-B-B'
		]);
	});

	it('refuses a format change while the batch is running and keeps the run consistent', async () => {
		const queue = createBatchQueue();
		queue.set([file('a.txt', 'uno'), file('b.txt', 'dos')], 'cades');

		let release = () => {};
		const gate = new Promise<void>((resolve) => (release = resolve));
		let started = false;
		const engine: BatchSigner = async (document, bytes, format) => {
			started = true;
			await gate;
			return cadesEngine()(document, bytes);
		};

		const run = queue.run(engine);
		// The loop awaits the file read before calling the engine, so spin until it is reached.
		await vi.waitFor(() => expect(started).toBe(true));

		queue.setFormat('2', 'pades');
		release();
		const state = await run;

		// The override was a no-op: the run kept the format the batch started with.
		expect(state.documents.map((document) => document.format)).toEqual(['cades', 'cades']);
	});
});

describe('directory walk helpers', () => {
	it('picks the format from the file name: PDFs to PAdES, XML to XAdES, the rest to CAdES', () => {
		expect(formatForName('contrato.pdf')).toBe('pades');
		expect(formatForName('CONTRATO.PDF')).toBe('pades');
		expect(formatForName('factura.xml')).toBe('xades');
		expect(formatForName('nota.txt')).toBe('cades');
		expect(formatForName('sin-extension')).toBe('cades');
	});

	it('summarizes a mixed run as one verdict row per document', async () => {
		const queue = createBatchQueue();
		queue.set([file('a.txt', 'uno'), file('b.txt', 'dos')], 'cades');
		const state = await queue.run(async (document, bytes) =>
			document.name === 'b.txt' ? cadesEngine(keyless)(document, bytes) : cadesEngine()(document, bytes)
		);
		const report = summarizeBatch(state);
		expect(report.signed).toBe(1);
		expect(report.failed).toBe(1);
		expect(report.rows.map((row) => [row.name, row.result])).toEqual([
			['a.txt', 'DONE_AND_SAVED'],
			['b.txt', 'ERROR_PRE']
		]);
		expect(report.rows[1].detail).toMatch(/no private key/);
	});

	it('signs 500 files with a per-file verdict table and retries only the failure', async () => {
		const files = Array.from({ length: 500 }, (_, index) =>
			file(`document-${String(index + 1).padStart(3, '0')}.txt`, `document ${index + 1}\n`)
		);
		const queue = createBatchQueue();
		queue.set(files, 'cades');
		// Bulk rows use a stub signer: 500 real signatures prove nothing the 1-3-file
		// openssl tests do not already prove, and would make the suite crypto-bound.
		const stub: Engine = async (document) => ({
			bytes: new TextEncoder().encode(`stub:${document.name}`),
			algorithm: 'stub'
		});
		const tried = new Set<string>();
		const seen: string[] = [];
		const state = await queue.run(async (document, bytes) => {
			seen.push(document.name);
			if (document.name === 'document-250.txt' && !tried.has(document.name)) {
				tried.add(document.name);
				throw new Error('injected failure');
			}
			return stub(document, bytes);
		});
		const report = summarizeBatch(state);
		expect(report.rows).toHaveLength(500);
		expect(report.signed).toBe(499);
		expect(report.failed).toBe(1);
		expect(seen).toHaveLength(500);
		// A rerun retries only the failure: the second run touches one document.
		const second: string[] = [];
		const retried = await queue.run(async (document, bytes) => {
			second.push(document.name);
			return stub(document, bytes);
		});
		expect(summarizeBatch(retried).failed).toBe(0);
		expect(second).toEqual(['document-250.txt']);
	});
});

describe('pre-hashed digest input', () => {
	it('signs a digest computed elsewhere; openssl verifies it against the original file', async () => {
		const content = new TextEncoder().encode('documento con digest externo\n');
		const digest = await sha256Hex(content);
		const signature = await signCadesFromDigest(certificate, { digest });
		expect(verify(signature, contentPath('digest-mode', content), 'digest-mode.csig')).toBeNull();
	});

	it('produces the same signature as the content path in #lib/cms, so the two cannot drift', async () => {
		const content = new TextEncoder().encode('mismo documento por las dos vias\n');
		const digest = await sha256Hex(content);
		const signingTime = new Date('2026-10-21T00:00:00Z');

		// `signCadesFromDigest` re-assembles the `SignedData` that `#lib/cms` builds, because
		// `#lib/cms` hashes the content itself and this path only ever has the digest. The two
		// copies must therefore produce the very same signature: this is the guard that makes a
		// change in one of them fail loudly instead of silently producing a different structure.
		const fromDigest = await signCadesFromDigest(certificate, { digest, signingTime });
		const fromContent = await signCadesDetached(certificate, content, { signingTime });

		expect(
			fromDigest,
			'the digest-mode signer has drifted from #lib/cms: the two paths must build the same ' +
				'SignedData, so whatever changed in #lib/cms has to be mirrored here'
		).toEqual(fromContent);
	});

	it('signs a digest with an EC key too (the raw r||s re-encoded as a DER SEQUENCE)', async () => {
		const { signer } = await loadSignerSession(fixtures.ecP12, FIXTURE_PIN);
		const content = new TextEncoder().encode('documento con clave EC\n');
		const digest = await sha256Hex(content);
		const signature = await signCadesFromDigest(signer, { digest });

		expect(signer.algorithm?.name).toBe('ECDSA');
		expect(verify(signature, contentPath('digest-mode-ec', content), 'digest-mode-ec.csig')).toBeNull();
	});

	it('accepts the raw 32-byte digest and rejects a malformed one', async () => {
		const content = new TextEncoder().encode('otro documento\n');
		const hex = await sha256Hex(content);
		const raw = new Uint8Array(32);
		for (let i = 0; i < 32; i += 1) raw[i] = parseInt(hex.slice(i * 2, i * 2 + 2), 16);
		const fromRaw = await signCadesFromDigest(certificate, {
			digest: raw,
			signingTime: new Date('2026-10-21T00:00:00Z')
		});
		const fromHex = await signCadesFromDigest(certificate, {
			digest: hex,
			signingTime: new Date('2026-10-21T00:00:00Z')
		});
		expect(fromRaw).toEqual(fromHex);
		await expect(signCadesFromDigest(certificate, { digest: 'zz' })).rejects.toThrow(
			/64-hex-char/
		);
	});
});

describe('batch CLI directory walk', () => {
	it('records an unreachable entry as ERROR_PRE and keeps signing the rest', () => {
		const root = join(fixtures.dir, 'cli-walk');
		const input = join(root, 'in');
		const output = join(root, 'out');
		mkdirSync(input, { recursive: true });
		writeFixtureFile(input, 'a.txt', new TextEncoder().encode('uno'));
		writeFixtureFile(input, 'z.txt', new TextEncoder().encode('dos'));
		// A dangling symlink: `statSync` fails, so the walk cannot even classify the entry.
		symlinkSync(join(input, 'missing.txt'), join(input, 'dangling.txt'));
		const p12Path = writeFixtureFile(root, 'cert.p12', fixtures.p12);

		const run = spawnSync(
			process.execPath,
			[
				'--import', './scripts/ts-hook.mjs', 'scripts/batch-sign.mjs',
				'--p12', p12Path, '--out', output, input
			],
			{ cwd: process.cwd(), encoding: 'utf8', env: { ...process.env, P12_PIN: FIXTURE_PIN } }
		);

		// The unreachable entry is a failure row, not the end of the batch: both good files signed
		// and the run reports failure through its exit code.
		expect(run.stdout, run.stderr).toContain('signed 2, failed 1');
		expect(run.status).toBe(1);
		const manifest = JSON.parse(readFileSync(join(output, 'manifest.json'), 'utf8'));
		expect(manifest.singlesigns.map((row: { datareference: string; result: string }) => [
			row.datareference,
			row.result
		])).toEqual([
			['a.txt', 'DONE_AND_SAVED'],
			['dangling.txt', 'ERROR_PRE'],
			['z.txt', 'DONE_AND_SAVED']
		]);
	});
});