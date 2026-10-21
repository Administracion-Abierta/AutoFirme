/**
 * Node's ESM resolver is extension-strict; the app's TypeScript is not (Vite resolves the imports,
 * and the tests run through Vite). This hook appends `.ts` / `/index.ts` to extensionless relative
 * specifiers so scripts can import `src/lib/**` directly. Loaded with
 * `node --import ./scripts/ts-hook.mjs` by `pnpm perf` (`scripts/perf-budget.mjs`); nothing else
 * uses it.
 */

import { registerHooks } from 'node:module';

registerHooks({
	resolve(specifier, context, next) {
		if (specifier.startsWith('.') && !/\.[a-z]+$/i.test(specifier)) {
			for (const candidate of [`${specifier}.ts`, `${specifier}/index.ts`]) {
				try {
					return next(candidate, context);
				} catch {
					// try the next candidate
				}
			}
		}
		return next(specifier, context);
	}
});
