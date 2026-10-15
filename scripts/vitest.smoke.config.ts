/**
 * Runner for `scripts/smoke-real.sh`, which drives the E2E sign → verify loop (issue #39).
 *
 * `vite.config.ts` restricts the app suite to `src/**\/*.test.ts`, so the smoke never runs as part of
 * `pnpm test`; this config is what makes it runnable on its own, with Node's `crypto.subtle` instead
 * of the jsdom environment the app tests need.
 */

import { fileURLToPath } from 'node:url';
import { defineConfig } from 'vitest/config';

export default defineConfig({
	root: fileURLToPath(new URL('..', import.meta.url)),
	test: {
		environment: 'node',
		include: ['scripts/*.test.ts'],
		// Serial: smoke-fixture-leak.test.ts counts $TMPDIR autofirme-p12-* dirs before/after its
		// nested run, while the smoke driver in a sibling file creates and removes the same prefix —
		// concurrent files would flake that count.
		fileParallelism: false,
		// A keystore load, an RSA signature and two openssl processes — well under this in practice.
		testTimeout: 60_000
	}
});
