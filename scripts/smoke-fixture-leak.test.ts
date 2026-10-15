/**
 * Regression for review finding 3 on #39: the smoke driver never removed the temp directory
 * `createPkcs12Fixtures()` writes, so `autofirme-p12-*` directories accumulated in `$TMPDIR` by
 * the hundreds across runs.
 *
 * The check runs `scripts/smoke-real.test.ts` — the driver that owns the cleanup (`afterAll` +
 * `rmSync`) — as a nested vitest filtered to just that file (its sibling smoke tests spawn
 * processes and must not recurse here), with `SMOKE_GENERATED=1` so the generated keystore case
 * runs. The `autofirme-p12-*` count in `$TMPDIR` must be identical before and after: anything the
 * fixture generator created has to be gone by the time the driver exits.
 */

import { spawnSync } from 'node:child_process';
import { readdirSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { fileURLToPath } from 'node:url';
import { expect, test } from 'vitest';

function fixtureDirs(): number {
	return readdirSync(tmpdir(), { withFileTypes: true }).filter(
		(entry) => entry.isDirectory() && entry.name.startsWith('autofirme-p12-')
	).length;
}

test('the smoke driver removes its generated fixture directory', () => {
	const root = fileURLToPath(new URL('..', import.meta.url));
	const before = fixtureDirs();
	const run = spawnSync(
		'pnpm',
		['exec', 'vitest', 'run', '--config', 'scripts/vitest.smoke.config.ts', 'scripts/smoke-real.test.ts'],
		{ cwd: root, encoding: 'utf8', env: { ...process.env, SMOKE_GENERATED: '1' } }
	);
	expect(run.status, run.stdout.slice(-2000) + run.stderr.slice(-2000)).toBe(0);
	expect(fixtureDirs(), 'no autofirme-p12-* temp dir may survive the run').toBe(before);
});
