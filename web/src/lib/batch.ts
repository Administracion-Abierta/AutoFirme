/**
 * Batch signing queue: N documents, sequential signing, per-document status, ZIP + manifest.
 *
 * Manifest semantics mirror AutoFirma's batch client (read-only reference,
 * `decompiled/sources/es/gob/afirma/signers/batch/client/`):
 * - the definition document is a `signbatch` object whose `singlesigns` array carries one entry per
 *   document with `id`, `datareference`, `format`, `suboperation` (`JSONBatchInfo.java:11-20`);
 * - results are written back into those same entries as `result` (a `BatchDataResult.Result` name)
 *   plus `description` when there is something to say (`JSONBatchInfo.updateResults`);
 * - `BatchDataResult.Result` has NOT_STARTED, DONE_AND_SAVED, DONE_BUT_NOT_SAVED_YET,
 *   DONE_BUT_SAVED_SKIPPED, DONE_BUT_ERROR_SAVING, ERROR_PRE, ERROR_POST, SKIPPED, SAVE_ROLLBACKED.
 *   A purely client-side run can only reach NOT_STARTED, DONE_AND_SAVED and ERROR_PRE; the other
 *   names belong to the server-side save phase this app does not have, so they are not emitted.
 * `BatchSigner.java:205` requires the `signbatch` node to carry `algorithm`; only a format engine
 * knows it, so it is emitted when the engine reports one.
 *
 * The issue also asks for `sha256`, `timestamp` and the output file name per document, which the
 * decompiled manifest has no place for: they are extra fields on the same entries.
 */

import { writable, get, type Readable } from 'svelte/store';
import { SIGN_FORMATS, type SignFormat } from './sign';
import { zipStore } from './zip';

/** `BatchDataResult.Result` names reachable by a client-side run. */
export type BatchResult = 'NOT_STARTED' | 'DONE_AND_SAVED' | 'ERROR_PRE';

export type DocumentStatus = 'pending' | 'signing' | 'signed' | 'error';

export interface BatchDocument {
	/** Manifest `id`; stable while the document stays in the queue. */
	id: string;
	file: File;
	status: DocumentStatus;
	/** Manifest `result`. */
	result: BatchResult;
	/** Manifest `description` — the failure message, empty when there is none. */
	description: string;
	/** Hex SHA-256 of the original document, empty until it has been read. */
	sha256: string;
	/** Name of the signed file inside the ZIP, empty until it is signed. */
	output: string;
	/** ISO-8601 instant the signature was produced, empty until it is signed. */
	timestamp: string;
	/** Signature algorithm reported by the engine, empty until it is signed. */
	algorithm: string;
	/** Signed bytes, kept so the ZIP can be built (and rebuilt) without re-signing. */
	bytes: Uint8Array | null;
}

export interface BatchState {
	documents: BatchDocument[];
	phase: 'idle' | 'running' | 'done';
	format: SignFormat;
	/** Id of the document being signed right now, null otherwise. */
	current: string | null;
}

/** What a signature engine returns for one document. */
export interface SignedDocument {
	bytes: Uint8Array;
	/** Signature algorithm, e.g. `SHA256withRSA` — engines (#6–#11) are the only ones that know it. */
	algorithm?: string;
}

/**
 * Format engine seam. #6 (CAdES), #7 (XAdES) and #11 (PAdES) implement this; the queue never looks
 * inside the bytes. Rejecting marks that document as `error` and the queue carries on.
 */
export type BatchSigner = (
	file: File,
	bytes: Uint8Array,
	format: SignFormat
) => Promise<SignedDocument>;

export interface BatchQueue extends Readable<BatchState> {
	/** Replace the document list (clears every previous result). */
	set(files: File[], format: SignFormat): void;
	/**
	 * Sign every document that is not signed yet, one at a time, in queue order. Resolves with the
	 * final state; a failing document is recorded as `error` and never stops the run.
	 */
	run(sign: BatchSigner): Promise<BatchState>;
	clear(): void;
}

export function sha256Hex(bytes: Uint8Array): Promise<string> {
	const digest = crypto.subtle.digest('SHA-256', bytes as unknown as BufferSource);
	return digest.then((value) =>
		[...new Uint8Array(value)].map((byte) => byte.toString(16).padStart(2, '0')).join('')
	);
}

export const EMPTY_BATCH_STATE: BatchState = {
	documents: [],
	phase: 'idle',
	format: 'cades',
	current: null
};

export function createBatchQueue(): BatchQueue {
	const store = writable<BatchState>(EMPTY_BATCH_STATE);

	function patch(id: string, changes: Partial<BatchDocument>) {
		store.update((state) => ({
			...state,
			documents: state.documents.map((document) =>
				document.id === id ? { ...document, ...changes } : document
			)
		}));
	}

	return {
		subscribe: store.subscribe,
		set(files, format) {
			store.set({
				documents: files.map((file, index) => ({
					id: String(index + 1),
					file,
					status: 'pending' as const,
					result: 'NOT_STARTED' as const,
					description: '',
					sha256: '',
					output: '',
					timestamp: '',
					algorithm: '',
					bytes: null
				})),
				phase: 'idle',
				format,
				current: null
			});
		},
		clear() {
			store.set({ documents: [], phase: 'idle', format: get(store).format, current: null });
		},
		async run(sign) {
			const start = get(store);
			if (start.phase === 'running') throw new Error('the batch is already running');
			if (start.documents.length === 0) return start;
			const format = start.format;
			const extension = SIGN_FORMATS.find((option) => option.id === format)?.extension ?? '';

			store.update((state) => ({ ...state, phase: 'running', current: null }));

			// Sequential by construction: one await at a time, so the UI thread is free between files
			// and an early failure cannot skip later documents.
			for (const document of start.documents) {
				if (document.status === 'signed') continue;
				patch(document.id, { status: 'signing', description: '' });
				store.update((state) => ({ ...state, current: document.id }));
				let digest = document.sha256;
				try {
					const bytes = new Uint8Array(await document.file.arrayBuffer());
					digest = await sha256Hex(bytes);
					const signed = await sign(document.file, bytes, format);
					patch(document.id, {
						status: 'signed',
						result: 'DONE_AND_SAVED',
						sha256: digest,
						bytes: signed.bytes,
						output: `${document.file.name}${extension}`,
						timestamp: new Date().toISOString(),
						algorithm: signed.algorithm ?? ''
					});
				} catch (reason) {
					patch(document.id, {
						status: 'error',
						result: 'ERROR_PRE',
						description: reason instanceof Error ? reason.message : String(reason),
						sha256: digest
					});
				}
			}

			store.update((state) => ({ ...state, phase: 'done', current: null }));
			return get(store);
		}
	};
}

export interface BatchManifestEntry {
	id: string;
	datareference: string;
	sha256: string;
	format: string;
	suboperation: string;
	result: BatchResult;
	description?: string;
	timestamp?: string;
	output?: string;
}

export interface BatchManifest {
	signbatch: { format: string; suboperation: string; algorithm?: string };
	singlesigns: BatchManifestEntry[];
}

export function buildManifest(state: BatchState): BatchManifest {
	const label = SIGN_FORMATS.find((option) => option.id === state.format)?.label ?? state.format;
	const algorithm = state.documents.find((document) => document.algorithm)?.algorithm;
	return {
		signbatch: {
			format: label,
			suboperation: 'sign',
			...(algorithm ? { algorithm } : {})
		},
		singlesigns: state.documents.map((document) => ({
			id: document.id,
			datareference: document.file.name,
			sha256: document.sha256,
			format: label,
			suboperation: 'sign',
			result: document.result,
			...(document.description ? { description: document.description } : {}),
			...(document.status === 'signed'
				? { timestamp: document.timestamp, output: document.output }
				: {})
		}))
	};
}

/** Unique `name` inside the archive: `dup.txt` becomes `dup-2.txt`, `dup-3.txt`, … */
function unique(name: string, taken: Set<string>): string {
	if (!taken.has(name)) {
		taken.add(name);
		return name;
	}
	const dot = name.lastIndexOf('.');
	const stem = dot > 0 ? name.slice(0, dot) : name;
	const extension = dot > 0 ? name.slice(dot) : '';
	for (let index = 2; ; index += 1) {
		const candidate = `${stem}-${index}${extension}`;
		if (!taken.has(candidate)) {
			taken.add(candidate);
			return candidate;
		}
	}
}

export const MANIFEST_NAME = 'manifest.json';

/** Signed documents plus the manifest, as one ZIP archive. Output names are de-duplicated. */
export function buildZip(state: BatchState): Uint8Array<ArrayBuffer> {
	const taken = new Set<string>([MANIFEST_NAME]);
	const entries = state.documents
		.filter((document) => document.status === 'signed' && document.bytes !== null)
		.map((document) => ({
			name: unique(document.output, taken),
			data: document.bytes as Uint8Array
		}));
	entries.push({
		name: MANIFEST_NAME,
		data: new TextEncoder().encode(`${JSON.stringify(buildManifest(state), null, 2)}\n`)
	});
	return zipStore(entries);
}
