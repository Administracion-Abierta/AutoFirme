import adapter from '@sveltejs/adapter-static';
import { fileURLToPath } from 'node:url';
import { svelte } from '@sveltejs/vite-plugin-svelte';
import { sveltekit } from '@sveltejs/kit/vite';
import tailwindcss from '@tailwindcss/vite';
import { defineConfig } from 'vitest/config';

/**
 * Fragment ids the prerenderer may skip. Empty = `<a href="#">` self-links. Anything
 * else is a broken anchor and must fail the build (it used to be every id starting
 * with `/`).
 */
const MISSING_ID_ALLOWLIST: Record<string, true> = { '': true };

export default defineConfig(({ mode }) => ({
	plugins: [
		tailwindcss(),
		sveltekit({
			adapter: adapter({ fallback: '404.html' }),
			// A service worker cannot register under a custom scheme, and the desktop shell
			// has no offline need (the assets ship with the app), so the tauri build omits it.
			// `pnpm build:tauri`: the shell serves the same static build from `tauri://localhost`.
			...(mode === 'tauri' ? { serviceWorker: { register: false } } : {}),
			prerender: {
				handleMissingId: ({ id, path }) => {
					if (id in MISSING_ID_ALLOWLIST) return;
					throw new Error(`missing fragment id "${id}" in ${path}`);
				}
			}
		})
	],
	// Vitest resolves the browser build of Svelte so components can actually mount.
	resolve: process.env.VITEST ? { conditions: ['browser'] } : {},
	test: {
		environment: 'jsdom',
		setupFiles: ['src/test-setup.ts'],
		include: ['src/**/*.test.ts'],
		// The crypto here (PBKDF2 with 250k iterations for sealing, WebCrypto key imports) takes
		// seconds when the files run in parallel, which trips the 5s default on a loaded machine.
		// Assertions stay as they are; only the allowance is explicit.
		testTimeout: 20_000,
		hookTimeout: 120_000
	}
}));
