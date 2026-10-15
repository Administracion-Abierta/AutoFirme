import adapter from '@sveltejs/adapter-static';
import { sveltekit } from '@sveltejs/kit/vite';
import tailwindcss from '@tailwindcss/vite';
import { defineConfig } from 'vitest/config';

export default defineConfig({
	plugins: [
		tailwindcss(),
		// The sidebar-07 demo renders shadcn's sample data with "#" links; keep the SPA
		// prerender strict for the real app but lenient for that demo page.
		sveltekit({
			adapter: adapter({ fallback: '404.html' }),
			prerender: {
				handleMissingId: ({ id }) => id.startsWith('/') || id === '',
				handleHttpError: ({ path, message }) => {
					if (path.startsWith('/sidebar-07') || path.includes('/avatars/')) return;
					throw new Error(`prerender failed: ${path} ${message}`);
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
});
