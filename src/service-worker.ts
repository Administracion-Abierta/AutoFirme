/**
 * App-shell service worker (#47). SvelteKit builds this file into `service-worker.js` at the site
 * root and registers it from the page boot script; `$app/manifest` hands over the exact lists of
 * built files, static files and prerendered pages, so the precache cannot drift from the build.
 *
 * Three rules this file exists to keep:
 *  - the app shell works offline (every route is client-rendered and all crypto is local);
 *  - nothing produced at runtime is ever cached: no `cache.put` anywhere, only GET same-origin
 *    requests are touched, so a produced signature can never end up in a cache;
 *  - a `share_target` POST (Web Share from Android) is stashed in IndexedDB and answered with a
 *    redirect into the sign flow — a static host cannot receive multipart bodies itself.
 */
import { assets, immutable, prerendered } from '$app/manifest';
import { version } from '$app/env';
import { asset, resolve } from '$app/paths';
import { self } from '$app/service-worker';

/** One cache per build: a new deploy activates a fresh cache and the old ones are dropped. */
const CACHE = `autofirme-shell-${version}`;

/**
 * Everything the shell needs, as fetchable URLs. `$app/manifest` paths are relative to the base
 * path, so they go through `$app/paths` — a subpath deployment then precaches `/autofirma/…`.
 */
const SHELL = [
	...prerendered.map(({ path }) => resolve(path)),
	...assets.map(({ path }) => asset(path)),
	// Built files are already listed relative to the base and are meant for `cache.add` as they are.
	...immutable.map(({ path }) => path)
];

/** `''` is how `$app/manifest` writes the prerendered root: the offline fallback for any path. */
const HOME = resolve(prerendered.find(({ path }) => path === '')?.path ?? '');

self.addEventListener('install', (event) => {
	// No `skipWaiting()` on purpose: the waiting worker is what the update prompt offers, and
	// taking over a running signing session unannounced would be rude.
	event.waitUntil(
		(async () => {
			const cache = await caches.open(CACHE);
			// File by file: one missing entry (a stale prerender list, say) must not abort the whole
			// install and leave the app with no worker at all.
			await Promise.all(
				SHELL.map(async (path) => {
					try {
						await cache.add(path);
					} catch (reason) {
						console.warn('[autofirme:sw] precache miss', path, reason);
					}
				})
			);
		})()
	);
});

self.addEventListener('activate', (event) => {
	event.waitUntil(
		(async () => {
			const keys = await caches.keys();
			await Promise.all(
				keys
					.filter((key) => key.startsWith('autofirme-shell-') && key !== CACHE)
					.map((key) => caches.delete(key))
			);
			await self.clients.claim();
		})()
	);
});

self.addEventListener('message', (event) => {
	// The header's update button: activate now, then the page reloads on `controllerchange`.
	if (event.data?.type === 'SKIP_WAITING') self.skipWaiting();
});

/** Pending inbound shares, keyed by a one-time nonce in the redirect URL. */

/** A share is files, text, or both — stashed outside every Cache, never cached. */
export interface SharePayload {
	files: File[];
	text: string;
}

/** Stash one share: signatures and inputs must never end up in a cache. */
function stashShare(payload: SharePayload): Promise<string> {
	const { promise, resolve, reject } = Promise.withResolvers<string>();
	const nonce = crypto.randomUUID();
	// `pwa-entry.ts` reads the stash back from the page through the same database.
	const open = indexedDB.open('autofirme-shares', 1);
	open.onupgradeneeded = () => open.result.createObjectStore('files');
	open.onerror = () => reject(open.error);
	open.onsuccess = () => {
		const db = open.result;
		const tx = db.transaction('files', 'readwrite');
		tx.objectStore('files').put(payload, nonce);
		tx.oncomplete = () => {
			db.close();
			resolve(nonce);
		};
		tx.onerror = () => {
			db.close();
			reject(tx.error);
		};
	};
	return promise;
}

self.addEventListener('fetch', (event) => {
	const { request } = event;
	// Web Share POST: the static host cannot receive multipart bodies, so the worker stashes
	// the files in IndexedDB and redirects into the sign flow with a one-time nonce.
	if (request.method === 'POST' && new URL(request.url).origin === self.location.origin) {
		const url = new URL(request.url);
		if (url.searchParams.has('shared')) {
			event.respondWith(
				(async () => {
					const home = new URL('/?entry=sign', self.location.origin).href;
					try {
						const form = await request.formData();
						const incoming = form
							.getAll('documents')
							.filter((entry): entry is File => entry instanceof File)
							.filter((file) => file.size > 0);
						const text = [form.get('title'), form.get('text'), form.get('url')]
							.filter((entry): entry is string => typeof entry === 'string')
							.map((entry) => entry.trim())
							.filter((entry) => entry.length > 0)
							.join('\n');
						if (incoming.length === 0 && text.length === 0) return Response.redirect(home, 303);
						const nonce = await stashShare({ files: incoming, text });
						return Response.redirect(`${home}&share=${nonce}`, 303);
					} catch {
						return Response.redirect(home, 303);
					}
				})()
			);
			return;
		}
	}
	// Signing never goes through the network, and the Blob URLs a signature is downloaded from are
	// not fetch events; this is still the guard that keeps non-GET requests out of the cache API.
	if (request.method !== 'GET') return;
	if (new URL(request.url).origin !== self.location.origin) return;

	if (request.mode === 'navigate') {
		// Network first: a fresh deploy is picked up immediately and the cached copy is the offline
		// answer. Offline, any path falls back to the shell, which then routes on the client.
		event.respondWith(
			(async () => {
				try {
					return await fetch(request);
				} catch {
					const cache = await caches.open(CACHE);
					return (await cache.match(request)) ?? (await cache.match(HOME)) ?? Response.error();
				}
			})()
		);
		return;
	}

	// Cache first for the frozen build (hashed asset names, so a hit is always current), then the
	// network — whose response is returned but never stored.
	event.respondWith(
		(async () => {
			const cache = await caches.open(CACHE);
			return (await cache.match(request)) ?? fetch(request);
		})()
	);
});
