/**
 * Client-side store for signing identities (issue #29).
 *
 * Two layers over one IndexedDB database:
 * - **identities** — the PKCS#12 container bytes, sealed with PBKDF2(SHA-256) → AES-GCM under the
 *   container's own PIN, the one the user types to load it. That PIN is never persisted: it only
 *   derives the key, losing it means the blob is unreadable, and unlocking asks for it once. Each
 *   saved signature carries its own PIN, so saving several of them never trips over a shared one.
 *
 * The backend is a four-method interface: IndexedDB in the browser, a map in tests — jsdom has no
 * IndexedDB and the project takes no dependency for it.
 */

import type { Bytes } from './asn1';
import { loadSignerSession } from './keystore';
import type { SignerSession } from './sign';

export type StoreErrorCode = 'unavailable' | 'wrong-pin' | 'tampered' | 'quota' | 'missing';

export class StoreError extends Error {
	constructor(
		readonly code: StoreErrorCode,
		message: string
	) {
		super(message);
		this.name = 'StoreError';
	}
}

export type StoreName = 'identities' | 'outputs' | 'meta';
export const STORES: readonly StoreName[] = ['identities', 'outputs', 'meta'];

/** One stored item. `id` also keys the backend record, so the two never disagree. */
export interface StoredRecord {
	id: string;
	value: unknown;
}

export interface StoreBackend {
	put(store: StoreName, record: StoredRecord): Promise<void>;
	get(store: StoreName, id: string): Promise<StoredRecord | null>;
	remove(store: StoreName, id: string): Promise<void>;
	list(store: StoreName): Promise<StoredRecord[]>;
	/** Drops the whole store — only a backend that owns persistent state implements it. */
	reset?(): Promise<void>;
}

/* ------------------------------------------------------------------ sealed blobs */

const KDF_ITERATIONS = 250_000;
const SALT_BYTES = 16;
const IV_BYTES = 12;

export interface SealedBlob {
	salt: Bytes;
	iv: Bytes;
	iterations: number;
	ciphertext: Bytes;
}

async function deriveStoreKey(pin: string, salt: Bytes, iterations: number): Promise<CryptoKey> {
	const material = await crypto.subtle.importKey(
		'raw',
		new TextEncoder().encode(pin),
		'PBKDF2',
		false,
		['deriveKey']
	);
	return crypto.subtle.deriveKey(
		{ name: 'PBKDF2', salt, iterations, hash: 'SHA-256' },
		material,
		{ name: 'AES-GCM', length: 256 },
		false,
		['encrypt', 'decrypt']
	);
}

/** Encrypt `plain` under the PIN. `aad` is authenticated but not encrypted — tampering it fails. */
export async function sealBlob(plain: Bytes, pin: string, aad: string): Promise<SealedBlob> {
	const salt = crypto.getRandomValues(new Uint8Array(SALT_BYTES));
	const iv = crypto.getRandomValues(new Uint8Array(IV_BYTES));
	const key = await deriveStoreKey(pin, salt, KDF_ITERATIONS);
	const ciphertext = await crypto.subtle.encrypt(
		{ name: 'AES-GCM', iv, additionalData: new TextEncoder().encode(aad) },
		key,
		plain
	);
	return { salt, iv, iterations: KDF_ITERATIONS, ciphertext: new Uint8Array(ciphertext) };
}

/** Reverse of `sealBlob`. A wrong PIN and a modified blob both raise `StoreError('tampered')`. */
export async function openBlob(sealed: SealedBlob, pin: string, aad: string): Promise<Bytes> {
	const key = await deriveStoreKey(pin, sealed.salt, sealed.iterations);
	try {
		const plain = await crypto.subtle.decrypt(
			{ name: 'AES-GCM', iv: sealed.iv, additionalData: new TextEncoder().encode(aad) },
			key,
			sealed.ciphertext
		);
		return new Uint8Array(plain);
	} catch {
		throw new StoreError('tampered', 'the stored blob failed authentication');
	}
}

/** Source of a certificate that came from a PKCS#12 container — the default, and the only one
 * the store itself produces. Token certificates carry `pkcs11:<module>` instead (#100). */
export const PKCS12_SOURCE = 'pkcs12';

/**
 * Badge text for a certificate's source: PKCS#12 containers have none (the file is the badge),
 * token certificates say `pkcs11:<module>`. Unknown values pass through so a future provider
 * only has to set the field.
 */
export function certificateSource(source: string | undefined): string {
	return source ?? PKCS12_SOURCE;
}

/* ------------------------------------------------------------------ records */

export interface IdentityMeta {
	id: string;
	label: string;
	commonName: string;
	/** ISO instant the certificate stops being valid — the picker's expiry column. */
	notAfter: string;
	createdAt: string;
	/** Size of the encrypted container, for the settings list. */
	size: number;
	/** Where the certificate came from: `pkcs12` by default, `pkcs11:<module>` for a token (#100). */
	source: string;
}

interface IdentityRecord extends IdentityMeta {
	sealed: SealedBlob;
}


export interface SaveIdentityInput {
	label: string;
	/** Session the container was just loaded with — supplies the CN and expiry for the list. */
	session: SignerSession;
	/** Raw PKCS#12 bytes, exactly as uploaded. */
	container: Bytes;
	/** The container's own PIN: the blob is sealed under it and unlocked with it. Never stored. */
	pin: string;
	/** Key source badge of the certificate being saved; absent means a PKCS#12 upload. */
	source?: string;
}

export interface Vault {
	/**
	 * False when the browser gave no usable database and the store lives in memory for this visit:
	 * the UI says so instead of letting signatures vanish on the next reload.
	 */
	readonly persistent: boolean;
	listIdentities(): Promise<IdentityMeta[]>;
	saveIdentity(input: SaveIdentityInput): Promise<IdentityMeta>;
	/** Change the user-chosen alias; the sealed container and its PIN are untouched. */
	renameIdentity(id: string, label: string): Promise<IdentityMeta>;
	/** Unseal the container with its PIN and import its key. */
	unlockIdentity(id: string, pin: string): Promise<SignerSession>;
	removeIdentity(id: string): Promise<void>;
	/** Deletes every saved record and reloads the page: the way out of a broken store. */
	reset(): Promise<void>;
}

/* ------------------------------------------------------------------ helpers */

/** Map backend failures onto the typed errors the UI has copy for. */
export function toStoreError(reason: unknown): StoreError {
	if (reason instanceof StoreError) return reason;
	// IndexedDB reports failures as `DOMException`, which does not extend `Error` everywhere —
	// the name is the only portable part of it.
	const failure = reason as { name?: string; message?: string } | null;
	if (failure?.name === 'QuotaExceededError') {
		return new StoreError('quota', 'the browser store is full');
	}
	return new StoreError('unavailable', failure?.message ?? String(reason));
}

/* ------------------------------------------------------------------ vault */

export function createVault(raw: StoreBackend, persistent: boolean | (() => boolean) = true): Vault {
	const stillPersistent = typeof persistent === 'function' ? persistent : () => persistent;
	// Every backend call goes through here, so a full disk or a blocked database reaches the UI as
	// a `StoreError` whatever backend is in use.
	const backend = guarded(raw);
	/** `[autofirme:store]` traces the lifetime of a saved signature, on purpose: a store that
	 * silently loses signatures is the one thing this layer must never do. */
	const log = (message: string, details?: unknown) =>
		console.debug(`[autofirme:store] ${message}`, details ?? '');

	log(`vault ready (${stillPersistent() ? 'persistent' : 'memory only'})`);

	return {
		// A getter: the store can degrade to memory mid-visit, and the UI must see that change.
		get persistent() {
			return stillPersistent();
		},
		async reset() {
			await backend.reset?.();
			location.reload();
		},

		async listIdentities() {
			const records = (await backend.list('identities')) as { value: IdentityRecord }[];
			log(`listed ${records.length} saved signature(s)`);
			return records
				.map(({ value }) => ({
					id: value.id,
					label: value.label,
					commonName: value.commonName,
					notAfter: value.notAfter,
					createdAt: value.createdAt,
					size: value.size,
					// Records written before #100 have no source; they were all PKCS#12 uploads.
					source: certificateSource(value.source)
				}))
				.sort((a, b) => b.createdAt.localeCompare(a.createdAt));
		},

		async saveIdentity(input) {
			const id = crypto.randomUUID();
			const meta: IdentityMeta = {
				id,
				label: input.label.trim() || input.session.signer.commonName,
				commonName: input.session.signer.commonName,
				notAfter: input.session.signer.notAfter.toISOString(),
				createdAt: new Date().toISOString(),
				size: input.container.length,
				source: certificateSource(input.source)
			};
			await backend.put('identities', {
				id,
				value: { ...meta, sealed: await sealBlob(input.container, input.pin, id) }
			});
			log(`saved "${meta.label}" (${meta.commonName}), ${input.container.length} bytes sealed`);
			return meta;
		},

		async renameIdentity(id, label) {
			const record = await backend.get('identities', id);
			if (!record) {
				log(`rename failed: no identity ${id}`);
				throw new StoreError('missing', `no stored identity ${id}`);
			}
			const current = record.value as IdentityRecord;
			const next = label.trim() || current.commonName;
			await backend.put('identities', { id, value: { ...current, label: next } });
			log(`renamed ${current.commonName} to "${next}"`);
			return {
				id: current.id,
				label: next,
				commonName: current.commonName,
				notAfter: current.notAfter,
				createdAt: current.createdAt,
				size: current.size,
				// Preserved through a rename: the alias is the only thing this call may change.
				source: certificateSource(current.source)
			};
		},

		async unlockIdentity(id, pin) {
			const record = await backend.get('identities', id);
			if (!record) {
				log(`unlock failed: no identity ${id}`);
				throw new StoreError('missing', `no stored identity ${id}`);
			}
			const { sealed } = record.value as IdentityRecord;
			// One secret per saved signature: the same PIN unseals the blob and opens the container.
			const container = await openBlob(sealed, pin, id);
			const session = await loadSignerSession(container, pin);
			log(`unlocked ${session.signer.commonName}`);
			return session;
		},

		async removeIdentity(id) {
			await backend.remove('identities', id);
			log(`removed identity ${id}`);
		}
	};
}

/* ------------------------------------------------------------------ backends */

/**
 * How long a backend call may stay silent before it is reported as a failure. A wedged database
 * (an upgrade some other page is holding open, for instance) answers nothing at all: without this
 * the save button would spin forever and the user would only see their signatures disappear on the
 * next reload — the bug this exists to prevent.
 */
const CALL_TIMEOUT_MS = 8_000;

function withTimeout<T>(run: Promise<T>, label: string): Promise<T> {
	const { promise, resolve, reject } = Promise.withResolvers<T>();
	const timer = setTimeout(
		() => reject(new StoreError('unavailable', `${label} did not answer within ${CALL_TIMEOUT_MS} ms`)),
		CALL_TIMEOUT_MS
	);
	run.then(
		(value) => {
			clearTimeout(timer);
			resolve(value);
		},
		(reason) => {
			clearTimeout(timer);
			reject(reason);
		}
	);
	return promise;
}

/** Wrap every backend call so a full disk or a blocked database reaches the UI as a `StoreError`. */
function guarded(backend: StoreBackend): StoreBackend {
	const wrap =
		<T extends unknown[], R>(call: (...args: T) => Promise<R>) =>
		async (...args: T): Promise<R> => {
			try {
				return await withTimeout(call(...args), 'the browser store');
			} catch (reason) {
				throw toStoreError(reason);
			}
		};
	return {
		put: wrap(backend.put.bind(backend)),
		get: wrap(backend.get.bind(backend)),
		remove: wrap(backend.remove.bind(backend)),
		list: wrap(backend.list.bind(backend)),
		// Not wrapped in the call timeout: deleting a database legitimately waits for other tabs.
		reset: backend.reset ? () => backend.reset!() : undefined
	};
}

/** In-memory backend — tests, and any environment without IndexedDB. */
export function memoryBackend(): StoreBackend {
	const stores = new Map<StoreName, Map<string, StoredRecord>>(
		STORES.map((name) => [name, new Map()])
	);
	const store = (name: StoreName) => stores.get(name) as Map<string, StoredRecord>;
	return {
		async put(name, record) {
			store(name).set(record.id, record);
		},
		async get(name, id) {
			return store(name).get(id) ?? null;
		},
		async remove(name, id) {
			store(name).delete(id);
		},
		async list(name) {
			return [...store(name).values()];
		}
	};
}

function request<T>(source: IDBRequest<T>): Promise<T> {
	const { promise, resolve, reject } = Promise.withResolvers<T>();
	source.onsuccess = () => resolve(source.result);
	source.onerror = () => reject(source.error ?? new Error('IndexedDB request failed'));
	return promise;
}

const DB_NAME = 'autofirme-web';
/** v3: the outputs store (produced signatures) went with the downloads feature; upgrade deletes it. */
const DB_VERSION = 3;
/** An open that answers nothing this long is wedged: report it rather than wait forever. */
const OPEN_TIMEOUT_MS = 2_500;

/**
 * IndexedDB backend: one object store per `StoreName`, records keyed by `id`. Structured clone
 * keeps `Uint8Array` and `ArrayBuffer` fields intact, so sealed blobs round-trip untouched.
 */
export function indexedDbBackend(): StoreBackend {
	if (typeof indexedDB === 'undefined') {
		throw new StoreError('unavailable', 'this environment has no IndexedDB');
	}
	let opening: Promise<IDBDatabase> | null = null;

	/** One `indexedDB.open` call, reported as a failure when it is blocked or never answers. */
	const connect = (): Promise<IDBDatabase> =>
		new Promise((resolve, reject) => {
			const request = indexedDB.open(DB_NAME, DB_VERSION);
			const timer = setTimeout(
				() => reject(new StoreError('unavailable', `${DB_NAME} did not open within ${OPEN_TIMEOUT_MS} ms`)),
				OPEN_TIMEOUT_MS
			);
			const settle = <T>(run: (value: T) => void) => (value: T) => {
				clearTimeout(timer);
				run(value);
			};
			const done = settle(resolve);
			const failed = settle(reject);

			request.onupgradeneeded = (event) => {
				const db = request.result;
				for (const name of STORES) {
					if (!db.objectStoreNames.contains(name)) db.createObjectStore(name, { keyPath: 'id' });
				}
				if (db.objectStoreNames.contains('outputs')) {
					// v3: produced signatures were only ever read by the downloads dialog, which is gone.
					db.deleteObjectStore('outputs');
				}
				if (event.oldVersion > 0 && event.oldVersion < 2) {
					// v1 sealed every identity under one store-wide PIN; those records cannot be read
					// under the per-signature scheme, so they go rather than linger as junk.
					const versionChange = request.transaction;
					if (versionChange) {
						for (const name of ['identities', 'meta'] as const) {
							versionChange.objectStore(name).clear();
						}
					}
					console.warn(
						`[autofirme:store] upgraded ${DB_NAME} v${event.oldVersion} → v${DB_VERSION}: ` +
							'identities saved by the old one-PIN scheme were removed'
					);
				}
			};
			request.onsuccess = () => {
				const db = request.result;
				// Another tab wants to upgrade: letting go is what keeps the database usable, and a
				// connection left open here is exactly how a store ends up answering nothing.
				db.onversionchange = () => {
					console.debug(`[autofirme:store] closing our connection: another tab is upgrading`);
					db.close();
					opening = null;
				};
				console.debug(`[autofirme:store] connected to ${DB_NAME} v${db.version}`);
				done(db);
			};
			request.onerror = () => failed(toStoreError(request.error));
			request.onblocked = () =>
				failed(
					new StoreError(
						'unavailable',
						`${DB_NAME} is still open in another tab; close it and try again`
					)
				);
		});

	/** Throw the database away and let the next visit create it fresh — the user asks for this. */
	const discard = (): Promise<void> =>
		new Promise((resolve) => {
			console.error(`[autofirme:store] discarding ${DB_NAME} on request`);
			const request = indexedDB.deleteDatabase(DB_NAME);
			const timer = setTimeout(() => {
				console.error(`[autofirme:store] deleting ${DB_NAME} did not answer`);
				resolve();
			}, OPEN_TIMEOUT_MS);
			const settle = () => {
				clearTimeout(timer);
				resolve();
			};
			request.onsuccess = settle;
			request.onerror = settle;
			request.onblocked = () => {
				console.error(
					`[autofirme:store] ${DB_NAME} is still open in another tab; close it to reset the store`
				);
				settle();
			};
		});

	/**
	 * A database that answers nothing is left alone: deleting it here would throw away signatures
	 * the user may still recover in another browser state, so the store degrades to memory instead
	 * (see `selfHealing`) and the UI offers an explicit reset.
	 */
	const open = (): Promise<IDBDatabase> => {
		if (!opening) {
			opening = connect().catch((reason: unknown) => {
				opening = null;
				throw reason;
			});
		}
		return opening;
	};
	const tx = async (name: StoreName, mode: IDBTransactionMode) =>
		(await open()).transaction(name, mode).objectStore(name);

	// Leaving a connection open when the document goes away (a reload, or the back/forward cache)
	// is what blocks the next document's upgrade — and a blocked upgrade answers nothing at all.
	globalThis.addEventListener?.('pagehide', () => {
		void opening?.then((db) => db.close()).catch(() => {});
	});

	return {
		reset: discard,
		async put(name, record) {
			await request((await tx(name, 'readwrite')).put(record));
		},
		async get(name, id) {
			return ((await request((await tx(name, 'readonly')).get(id))) as StoredRecord) ?? null;
		},
		async remove(name, id) {
			await request((await tx(name, 'readwrite')).delete(id));
		},
		async list(name) {
			return (await request((await tx(name, 'readonly')).getAll())) as StoredRecord[];
		}
	};
}

/**
 * Keeps the store usable when the browser database stops answering (a wedged upgrade, a quota, a
 * privacy mode that blocks it): every call falls back to memory for the rest of the visit, so the
 * user can still save and sign — and `persistent` turns false, which the modal says out loud.
 */
function selfHealing(
	primary: StoreBackend,
	fallback: StoreBackend,
	onDegrade: (reason: unknown) => void
): StoreBackend {
	let degraded = false;
	async function run<R>(call: (backend: StoreBackend) => Promise<R>): Promise<R> {
		if (!degraded) {
			try {
				return await call(primary);
			} catch (reason) {
				if (!(reason instanceof StoreError) || reason.code !== 'unavailable') throw reason;
				degraded = true;
				onDegrade(reason);
			}
		}
		return call(fallback);
	}
	return {
		put: (name, record) => run((backend) => backend.put(name, record)),
		get: (name, id) => run((backend) => backend.get(name, id)),
		remove: (name, id) => run((backend) => backend.remove(name, id)),
		list: (name) => run((backend) => backend.list(name)),
		// Always the real backend: resetting means discarding the database, not the memory copy.
		reset: primary.reset ? () => primary.reset!() : undefined
	};
}

/**
 * Browser vault. Falls back to memory when IndexedDB is missing (prerender/SSR, strict privacy
 * modes) so the app keeps working, at the cost of the store not surviving the reload.
 */
export function browserVault(): Vault {
	if (typeof indexedDB === 'undefined') {
		console.error('[autofirme:store] no IndexedDB: saved signatures will not survive a reload');
		return createVault(memoryBackend(), false);
	}
	let degraded = false;
	const backend = selfHealing(indexedDbBackend(), memoryBackend(), (reason) => {
		degraded = true;
		console.error(
			'[autofirme:store] the browser database stopped answering; keeping signatures in memory ' +
				'for this visit only. Reload the page, and close any other tab of the app.',
			reason
		);
	});
	return createVault(backend, () => !degraded);
}
