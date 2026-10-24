/**
 * Batch signing queue: N documents, sequential signing, per-document status, ZIP + manifest.
 *
 * Manifest semantics mirror AutoFirma's batch client (read-only reference,
 * `decompiled/sources/es/gob/afirma/signers/batch/client/`, tag `reference/decompile-1.9`):
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
import {
	type Bytes,
	derInteger,
	explicitTagged,
	implicitTagged,
	objectId,
	octetString,
	seq,
	setOf,
	utcTime
} from './asn1';
import { signBytes, type SignerCert } from './sign';
import { zipStore } from './zip';
import { certificateIdentity } from './x509';

/** `BatchDataResult.Result` names reachable by a client-side run. */
export type BatchResult = 'NOT_STARTED' | 'DONE_AND_SAVED' | 'ERROR_PRE';

export type DocumentStatus = 'pending' | 'signing' | 'signed' | 'error';

export interface BatchDocument {
	/** Manifest `id`; stable while the document stays in the queue. */
	id: string;
	file: File;
	/** Format this document signs with — the global choice unless overridden per file. */
	format: SignFormat;
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
	bytes: Uint8Array<ArrayBuffer> | null;
}

export interface BatchState {
	documents: BatchDocument[];
	/** `cancelled` is a run the user stopped; the documents not reached stay `pending`. */
	phase: 'idle' | 'running' | 'done' | 'cancelled';
	/** Format for every document that has no per-document override. */
	format: SignFormat;
	/** Id of the document being signed right now, null otherwise. */
	current: string | null;
}

/** What a signature engine returns for one document. */
export interface SignedDocument {
	bytes: Uint8Array<ArrayBuffer>;
	/** Signature algorithm, e.g. `SHA256withRSA` — engines (#6–#11) are the only ones that know it. */
	algorithm?: string;
}

/**
 * Format engine seam. #6 (CAdES), #7 (XAdES) and #11 (PAdES) implement this; the queue never looks
 * inside the bytes. Rejecting marks that document as `error` and the queue carries on.
 */
export type BatchSigner = (
	file: File,
	bytes: Uint8Array<ArrayBuffer>,
	format: SignFormat
) => Promise<SignedDocument>;

export interface BatchQueue extends Readable<BatchState> {
	/** Replace the document list (clears every previous result). */
	set(files: File[], format: SignFormat): void;
	/** One document's format override; resets that document's result. No-op while running. */
	setFormat(id: string, format: SignFormat): void;
	/**
	 * Sign every document that is not signed yet, one at a time, in queue order. Resolves with the
	 * final state; a failing document is recorded as `error` and never stops the run.
	 */
	run(sign: BatchSigner): Promise<BatchState>;
	/**
	 * Stop the run in flight: no further document is started. The one being signed when this lands
	 * finishes and keeps its result — WebCrypto cannot be aborted mid-call. No-op when not running.
	 */
	cancel(): void;
	clear(): void;
}

export function sha256Hex(bytes: Uint8Array<ArrayBuffer>): Promise<string> {
	const digest = crypto.subtle.digest('SHA-256', bytes);
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
	/**
	 * Bumped every time the document list is replaced. A run in flight compares it before every write
	 * and gives up once it changed, so dropping or removing files mid-run can neither sign the
	 * discarded documents nor record their results against the new list's entries.
	 */
	let generation = 0;
	/** Set by `cancel()`, read by the loop before every document, cleared by the next `run()`. */
	let cancelled = false;

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
			generation += 1;
			store.set({
				documents: files.map((file, index) => ({
					id: String(index + 1),
					file,
					format,
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
		/** Re-sign one document with a different format: clears its result, keeps the rest. */
		setFormat(id: string, format: SignFormat) {
			if (get(store).phase === 'running') return;
			generation += 1;
			store.update((state) => ({
				...state,
				documents: state.documents.map((document) =>
					document.id === id
						? { ...document, format, status: 'pending' as const, result: 'NOT_STARTED' as const }
						: document
				)
			}));
		},
		clear() {
			generation += 1;
			store.set({ documents: [], phase: 'idle', format: get(store).format, current: null });
		},
		cancel() {
			// Only a run in flight can be cancelled; the flag a pending cancel left behind is cleared
			// by the next `run()`.
			if (get(store).phase === 'running') cancelled = true;
		},
		async run(sign) {
			const start = get(store);
			if (start.phase === 'running') throw new Error('the batch is already running');
			if (start.documents.length === 0) return start;
			cancelled = false;
			const extensionOf = (id: SignFormat): string =>
				SIGN_FORMATS.find((option) => option.id === id)?.extension ?? '';
			const taken = new Set(
				start.documents
					.filter((document) => document.status === 'signed')
					.map((document) => document.output)
			);
			const run = generation;

			store.update((state) => ({ ...state, phase: 'running', current: null }));

			// Sequential by construction: one await at a time, so the UI thread is free between files
			// and an early failure cannot skip later documents.
			for (const document of start.documents) {
				// The list was replaced while this run was in flight: the queue no longer describes
				// these documents, so stop rather than sign files nobody is waiting for.
				if (generation !== run) return get(store);
				// Cancelled: the document already in flight has finished above and keeps its result,
				// so stop before starting another one.
				if (cancelled) break;
				if (document.status === 'signed') continue;
				patch(document.id, { status: 'signing', description: '' });
				store.update((state) => ({ ...state, current: document.id }));
				let digest = document.sha256;
				try {
					const bytes = new Uint8Array(await document.file.arrayBuffer());
					digest = await sha256Hex(bytes);
					const signed = await sign(document.file, bytes, document.format);
					if (generation !== run) return get(store);
					patch(document.id, {
						status: 'signed',
						result: 'DONE_AND_SAVED',
						sha256: digest,
						bytes: signed.bytes,
						output: signedOutputName(
							document.file.name,
							extensionOf(document.format),
							taken
						),
						timestamp: new Date().toISOString(),
						algorithm: signed.algorithm ?? ''
					});
				} catch (reason) {
					if (generation !== run) return get(store);
					patch(document.id, {
						status: 'error',
						result: 'ERROR_PRE',
						description: reason instanceof Error ? reason.message : String(reason),
						sha256: digest
					});
				}
			}

			store.update((state) => ({
				...state,
				phase: cancelled ? 'cancelled' : 'done',
				current: null
			}));
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
	const labelOf = (id: SignFormat): string =>
		SIGN_FORMATS.find((option) => option.id === id)?.label ?? id;
	const algorithm = state.documents.find((document) => document.algorithm)?.algorithm;
	const formats = [...new Set(state.documents.map((document) => document.format))];
	return {
		signbatch: {
			// A mixed batch lists every format used; a single-format batch keeps its one label.
			format: formats.map(labelOf).join(', '),
			suboperation: 'sign',
			...(algorithm ? { algorithm } : {})
		},
		singlesigns: state.documents.map((document) => ({
			id: document.id,
			datareference: document.file.name,
			sha256: document.sha256,
			format: labelOf(document.format),
			suboperation: 'sign',
			result: document.result,
			...(document.description ? { description: document.description } : {}),
			...(document.status === 'signed'
				? { timestamp: document.timestamp, output: document.output }
				: {})
		}))
	};
}

/**
 * Name of the signed file: the document name plus the format extension, numbered when two documents
 * share a name (`dup.txt` → `dup-2.txt.csig`). `taken` holds every output name already in use.
 */
function signedOutputName(fileName: string, extension: string, taken: Set<string>): string {
	const dot = fileName.lastIndexOf('.');
	const stem = dot > 0 ? fileName.slice(0, dot) : fileName;
	const suffix = dot > 0 ? fileName.slice(dot) : '';
	for (let index = 1; ; index += 1) {
		const candidate = `${index === 1 ? stem : `${stem}-${index}`}${suffix}${extension}`;
		if (!taken.has(candidate)) {
			taken.add(candidate);
			return candidate;
		}
	}
}

export const MANIFEST_NAME = 'manifest.json';

/** Signed documents plus the manifest, as one ZIP archive. Output names are de-duplicated. */
export function buildZip(state: BatchState): Uint8Array<ArrayBuffer> {
	// Output names are already unique (the run assigns them), so only the manifest is appended.
	const entries = state.documents
		.filter((document) => document.status === 'signed' && document.bytes !== null)
		.map((document) => ({ name: document.output, data: document.bytes as Uint8Array<ArrayBuffer> }));
	entries.push({
		name: MANIFEST_NAME,
		data: new TextEncoder().encode(`${JSON.stringify(buildManifest(state), null, 2)}\n`)
	});
	return zipStore(entries);
}

/** Per-file format for a directory walk: PDFs go to PAdES, XML to XAdES, the rest to CAdES. */
export function formatForName(name: string): SignFormat {
	const dot = name.lastIndexOf('.');
	const extension = dot >= 0 ? name.slice(dot).toLowerCase() : '';
	if (extension === '.pdf') return 'pades';
	if (extension === '.xml') return 'xades';
	return 'cades';
}

/**
 * Pre-hashed digest input: the SHA-256 of a document computed elsewhere (streamed off disk,
 * produced by a portal), plus the metadata the signature binds. The document bytes are never
 * needed — only their 32-byte digest enters the CMS `messageDigest` attribute.
 */
export interface DigestInput {
	/** SHA-256 of the document, hex or raw. */
	digest: string | Bytes;
	/** Fixed so a test can reproduce the signature; defaults to now. */
	signingTime?: Date;
}

const OID_SIGNED_DATA = '1.2.840.113549.1.7.2';
const OID_DATA = '1.2.840.113549.1.7.1';
const OID_CONTENT_TYPE = '1.2.840.113549.1.9.3';
const OID_MESSAGE_DIGEST = '1.2.840.113549.1.9.4';
const OID_SIGNING_TIME = '1.2.840.113549.1.9.5';
const OID_SIGNING_CERTIFICATE_V2 = '1.2.840.113549.1.9.16.2.47';
const OID_SHA256 = '2.16.840.1.101.3.4.2.1';
const OID_RSA_ENCRYPTION = '1.2.840.113549.1.1.1';
const OID_ECDSA_WITH_SHA256 = '1.2.840.10045.4.3.2';
const NULL_PARAMS = new Uint8Array([0x05, 0x00]);

/** WebCrypto returns ECDSA as `r || s`; CMS wants a DER `SEQUENCE { r, s }`. */
function ecdsaSignature(raw: Bytes): Bytes {
	const half = raw.length / 2;
	return seq(derInteger(raw.subarray(0, half)), derInteger(raw.subarray(half)));
}

function digestBytes(digest: string | Bytes): Bytes {
	if (typeof digest !== 'string') {
		if (digest.length !== 32) throw new Error(`expected a 32-byte SHA-256 digest, got ${digest.length}`);
		return digest;
	}
	const hex = digest.startsWith('0x') ? digest.slice(2) : digest;
	if (!/^[0-9a-fA-F]{64}$/.test(hex)) throw new Error('expected a 64-hex-char SHA-256 digest');
	const out = new Uint8Array(32);
	for (let i = 0; i < 32; i += 1) out[i] = parseInt(hex.slice(i * 2, i * 2 + 2), 16);
	return out as Bytes;
}

/**
 * CAdES-BES detached signature over a precomputed SHA-256 digest — the same `SignedData` shape
 * `#lib/cms` builds, but the `messageDigest` attribute carries the supplied digest instead of a
 * hash computed here, so the document bytes never enter memory. Verifies with
 * `openssl cms -verify -content <original>`: the attribute commits to the document the digest
 * was computed over. XAdES/PAdES have no digest mode — both embed or transform the bytes.
 */
export async function signCadesFromDigest(
	cert: SignerCert,
	input: DigestInput
): Promise<Bytes> {
	if (!cert.key || !cert.algorithm) {
		throw new Error(`certificate ${cert.commonName} has no private key`);
	}
	if (cert.algorithm.hash !== 'SHA-256') {
		throw new Error(`CAdES-BES is SHA-256 here, but the key is bound to ${cert.algorithm.hash}`);
	}
	const messageDigest = digestBytes(input.digest);
	const identity = certificateIdentity(cert.der);
	const ecdsa = cert.algorithm.name === 'ECDSA';
	const certHash = new Uint8Array(await crypto.subtle.digest('SHA-256', cert.der));
	const issuerSerial = seq(seq(explicitTagged(4, identity.issuerDer)), identity.serialDer);
	const signingCertificateV2 = seq(seq(seq(octetString(certHash), issuerSerial)));
	const signedAttrs = setOf(
		seq(objectId(OID_CONTENT_TYPE), setOf(objectId(OID_DATA))),
		seq(objectId(OID_MESSAGE_DIGEST), setOf(octetString(messageDigest))),
		seq(objectId(OID_SIGNING_TIME), setOf(utcTime(input.signingTime ?? new Date()))),
		seq(objectId(OID_SIGNING_CERTIFICATE_V2), setOf(signingCertificateV2))
	);
	const raw = await signBytes(cert, signedAttrs);
	const signature = ecdsa ? ecdsaSignature(raw) : raw;
	const signerInfos = setOf(
		seq(
			derInteger(1),
			seq(identity.issuerDer, identity.serialDer),
			seq(objectId(OID_SHA256)),
			implicitTagged(0, signedAttrs),
			ecdsa ? seq(objectId(OID_ECDSA_WITH_SHA256)) : seq(objectId(OID_RSA_ENCRYPTION), NULL_PARAMS),
			octetString(signature)
		)
	);
	const signedData = seq(
		derInteger(1),
		setOf(seq(objectId(OID_SHA256))),
		seq(objectId(OID_DATA)),
		implicitTagged(0, setOf(cert.der)),
		signerInfos
	);
	return seq(objectId(OID_SIGNED_DATA), explicitTagged(0, signedData));
}

/** One row of the batch summary report: per-file verdict, stable in queue order. */
export interface BatchVerdict {
	id: string;
	name: string;
	format: string;
	result: BatchResult;
	sha256: string;
	output: string;
	detail: string;
}

/** Summary report over a finished run: counts plus one verdict row per document. */
export function summarizeBatch(state: BatchState): { signed: number; failed: number; rows: BatchVerdict[] } {
	const labelOf = (id: SignFormat): string =>
		SIGN_FORMATS.find((option) => option.id === id)?.label ?? id;
	let signed = 0;
	const rows = state.documents.map((document) => {
		if (document.status === 'signed') signed += 1;
		return {
			id: document.id,
			name: document.file.name,
			format: labelOf(document.format),
			result: document.result,
			sha256: document.sha256,
			output: document.output,
			detail: document.description
		};
	});
	return { signed, failed: rows.length - signed, rows };
}
