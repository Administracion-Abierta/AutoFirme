/**
 * Regression guard for the suite-wide async-util allowance (review #43, finding 7 load flake).
 * The flake class: findByRole/waitFor gave up at testing-library's 1s default while a PBKDF2 seal
 * or PKCS#12 unlock outlasted it under the full suite's parallel files. src/test-setup.ts raises
 * that allowance — but only if its `configure` call hits the SAME @testing-library/dom instance
 * the bound queries read from. The pnpm layout keeps several copies of the package on disk and a
 * bare '@testing-library/dom' import silently configured the wrong one (the flake came back at
 * ~1.2s until the import moved to the copy @testing-library/svelte re-exports). `screen` and
 * every bound query take their timeout from the wrapper's `getConfig`, so reading it THROUGH THE
 * WRAPPER is exactly what the waiting code will see — no timer needed.
 */
import { getConfig } from '@testing-library/svelte';
import { expect, it } from 'vitest';

it('the dom instance bound queries use carries the suite allowance', () => {
	expect(getConfig().asyncUtilTimeout).toBeGreaterThanOrEqual(15_000);
});
