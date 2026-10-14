import { webcrypto } from 'node:crypto';
import { crc32 as nodeCrc32 } from 'node:zlib';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { cleanup, fireEvent, render, screen } from '@testing-library/svelte';
import { get } from 'svelte/store';
import BatchQueue from './components/BatchQueue.svelte';
import { buildManifest, buildZip, createBatchQueue, sha256Hex, type BatchSigner } from './batch';
import { copy } from './i18n';

// jsdom's `crypto` only has getRandomValues; the queue's SHA-256 (and the engine the test drives
// the queue with) need SubtleCrypto — the same API browsers expose.
if (!globalThis.crypto?.subtle) {
	Object.defineProperty(globalThis, 'crypto', { value: webcrypto, configurable: true, writable: true });
}

afterEach(cleanup);

const file = (name: string, content: string) => new File([content], name, { type: 'text/plain' });

/** A real format engine stand-in: signs the document bytes with an ephemeral ECDSA key. */
async function engine() {
	const key = await crypto.subtle.generateKey({ name: 'ECDSA', namedCurve: 'P-256' }, false, [
		'sign',
		'verify'
	]);
	return {
		algorithm: 'SHA256withECDSA',
		sign: async (_file: File, bytes: Uint8Array<ArrayBuffer>) =>
			new Uint8Array(await crypto.subtle.sign({ name: 'ECDSA', hash: 'SHA-256' }, key.privateKey, bytes)),
		verify: async (bytes: Uint8Array<ArrayBuffer>, signature: Uint8Array<ArrayBuffer>) =>
			crypto.subtle.verify({ name: 'ECDSA', hash: 'SHA-256' }, key.publicKey, signature, bytes)
	};
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
	it('signs a 3-file batch one document at a time and leaves every signature verifiable', async () => {
		const queue = createBatchQueue();
		queue.set([file('a.txt', 'uno'), file('b.txt', 'dos'), file('c.txt', 'tres')], 'cades');
		const real = await engine();

		const order: string[] = [];
		let running = 0;
		const sign: BatchSigner = async (document, bytes) => {
			running += 1;
			try {
				// Sequential processing: the queue must never have two signatures in flight.
				expect(running).toBe(1);
				order.push(document.name);
				// The state machine publishes progress before the engine resolves, so the UI can paint.
				expect(get(queue).phase).toBe('running');
				expect(get(queue).documents.find((item) => item.file === document)?.status).toBe('signing');
				return { bytes: await real.sign(document, bytes), algorithm: real.algorithm };
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

		for (const document of state.documents) {
			const bytes = new Uint8Array(await document.file.arrayBuffer());
			expect(document.sha256).toBe(await sha256Hex(bytes));
			expect(await real.verify(bytes, document.bytes as Uint8Array<ArrayBuffer>)).toBe(true);
		}
	});

	it('keeps going when one document fails, and re-running only retries what is not signed', async () => {
		const queue = createBatchQueue();
		queue.set([file('a.txt', 'uno'), file('b.txt', 'dos'), file('c.txt', 'tres')], 'cades');
		const real = await engine();

		const tried: string[] = [];
		const flaky: BatchSigner = async (document, bytes) => {
			tried.push(document.name);
			if (document.name === 'b.txt') throw new Error('certificado revocado');
			return { bytes: await real.sign(document, bytes), algorithm: real.algorithm };
		};

		const state = await queue.run(flaky);

		expect(tried).toEqual(['a.txt', 'b.txt', 'c.txt']);
		expect(state.documents.map((document) => document.status)).toEqual([
			'signed',
			'error',
			'signed'
		]);
		const failed = state.documents[1];
		expect(failed.description).toBe('certificado revocado');
		expect(failed.result).toBe('ERROR_PRE');
		expect(failed.sha256).toBe(await sha256Hex(new Uint8Array(await failed.file.arrayBuffer())));
		expect(failed.bytes).toBeNull();

		const manifest = buildManifest(state);
		expect(manifest.singlesigns.map((entry) => entry.result)).toEqual([
			'DONE_AND_SAVED',
			'ERROR_PRE',
			'DONE_AND_SAVED'
		]);
		expect(manifest.singlesigns[1].description).toBe('certificado revocado');
		expect(manifest.singlesigns[0]).not.toHaveProperty('description');
		expect(manifest.singlesigns[0]).toHaveProperty('timestamp');
		expect(manifest.singlesigns[1]).not.toHaveProperty('timestamp');
		expect(manifest.signbatch).toEqual({
			format: 'CAdES-BES',
			suboperation: 'sign',
			algorithm: 'SHA256withECDSA'
		});

		// Resumable: the second pass signs only what is still missing.
		const resumed: string[] = [];
		const retry: BatchSigner = async (document, bytes) => {
			resumed.push(document.name);
			return { bytes: await real.sign(document, bytes), algorithm: real.algorithm };
		};

		const final = await queue.run(retry);

		expect(resumed).toEqual(['b.txt']);
		expect(final.documents.map((document) => document.status)).toEqual([
			'signed',
			'signed',
			'signed'
		]);
		expect(final.documents[1].result).toBe('DONE_AND_SAVED');
		expect(final.documents[1].description).toBe('');
	});

	it('packages the signed documents and the manifest into a ZIP with unique names', async () => {
		const queue = createBatchQueue();
		queue.set([file('dup.txt', 'uno'), file('dup.txt', 'dos')], 'cades');
		const real = await engine();
		const state = await queue.run(async (document, bytes) => ({
			bytes: await real.sign(document, bytes),
			algorithm: real.algorithm
		}));

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
	});
});

describe('BatchQueue UI', () => {
	let downloaded: Blob | null = null;

	beforeEach(() => {
		downloaded = null;
		// jsdom has no object URLs (and cannot follow a download link), so the blob the component
		// hands to the browser is captured here instead.
		URL.createObjectURL = (blob: Blob | MediaSource) => {
			downloaded = blob as Blob;
			return 'blob:preview';
		};
		URL.revokeObjectURL = () => {};
		HTMLAnchorElement.prototype.click = () => {};
	});

	it('shows per-document status while the batch runs and downloads the ZIP', async () => {
		const queue = createBatchQueue();
		queue.set([file('a.txt', 'uno'), file('b.txt', 'dos'), file('c.txt', 'tres')], 'cades');
		const real = await engine();

		const { container } = render(BatchQueue, {
			copy: copy.es,
			queue,
			hint: '',
			signer: async (document, bytes) => ({
				bytes: await real.sign(document, bytes),
				algorithm: real.algorithm
			})
		});

		expect(container.querySelectorAll('[data-status="pending"]')).toHaveLength(3);
		expect(container.textContent).toContain('Firmados: 0/3');

		await fireEvent.click(screen.getByRole('button', { name: 'Firmar' }));

		await screen.findByText('Firmados: 3/3');
		expect(container.querySelectorAll('[data-status="signed"]')).toHaveLength(3);
		expect(container.textContent).toContain('a.txt.csig');

		await fireEvent.click(screen.getByRole('button', { name: 'Descargar ZIP' }));

		const archive = new Uint8Array(await downloaded!.arrayBuffer());
		expect(readZip(archive).map((entry) => entry.name)).toEqual([
			'a.txt.csig',
			'b.txt.csig',
			'c.txt.csig',
			'manifest.json'
		]);
	});
});
