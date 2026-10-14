import adapter from '@sveltejs/adapter-static';
import { sveltekit } from '@sveltejs/kit/vite';
import tailwindcss from '@tailwindcss/vite';
import { defineConfig } from 'vitest/config';

export default defineConfig({
	plugins: [
		tailwindcss(),
		// Static SPA: `/` is prerendered; unknown routes get the shell via 404.html.
		sveltekit({ adapter: adapter({ fallback: '404.html' }) })
	],
	// Vitest resolves the browser build of Svelte so components can actually mount.
	resolve: process.env.VITEST ? { conditions: ['browser'] } : {},
	test: {
		environment: 'jsdom',
		setupFiles: ['./test/setup.ts'],
		include: ['src/**/*.test.ts']
	}
});
