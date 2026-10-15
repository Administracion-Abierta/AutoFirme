/**
 * Regression for review finding 1 on #39: the plan promised a `--generated` / `--no-generated`
 * flag pair and only `--no-generated` existed, so `--generated` died with "unknown argument"
 * (exit 2).
 *
 * These tests drive `scripts/smoke-real.sh` with a fake `pnpm` on PATH that records its
 * arguments and the `SMOKE_GENERATED` value the wrapper exported, so the flag wiring is checked
 * without running the (openssl-heavy) smoke itself. `--cert` always points at a path that never
 * exists, keeping the real keystore out of every case.
 */

import { spawnSync } from 'node:child_process';
import { mkdirSync, mkdtempSync, rmSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { expect, test } from 'vitest';

const WRAPPER = fileURLToPath(new URL('smoke-real.sh', import.meta.url));

/** Run the wrapper with a stub `pnpm` and no inherited keystore selection. */
function runWrapper(args: string[]) {
	const shim = mkdtempSync(join(tmpdir(), 'smoke-shim-'));
	try {
		const bin = join(shim, 'bin');
		mkdirSync(bin);
		writeFileSync(
			join(bin, 'pnpm'),
			'#!/bin/sh\necho "call: $@"\necho "SMOKE_GENERATED=${SMOKE_GENERATED:-unset}"\n',
			{ mode: 0o755 }
		);
		const env: NodeJS.ProcessEnv = { ...process.env, PATH: `${bin}:${process.env.PATH}` };
		delete env.SMOKE_CERT;
		delete env.SMOKE_PIN_FILE;
		delete env.SMOKE_GENERATED;
		return spawnSync('bash', [WRAPPER, '--cert', '/tmp/no-such-keystore.p12', ...args], {
			encoding: 'utf8',
			env
		});
	} finally {
		rmSync(shim, { recursive: true, force: true });
	}
}

test('--generated is accepted and reaches the runner with SMOKE_GENERATED=1', () => {
	const run = runWrapper(['--generated']);
	expect(run.status).toBe(0);
	expect(run.stdout).toContain('call: exec vitest run --config scripts/vitest.smoke.config.ts');
	expect(run.stdout).toContain('SMOKE_GENERATED=1');
});

test('--no-generated is accepted and keeps the generated keystore off', () => {
	const run = runWrapper(['--no-generated']);
	expect(run.status).toBe(0);
	expect(run.stdout).not.toContain('call:');
	expect(run.stdout).toContain('nothing to check');
});

test('--generated --no-generated: the last flag wins', () => {
	const run = runWrapper(['--generated', '--no-generated']);
	expect(run.status).toBe(0);
	expect(run.stdout).not.toContain('call:');
	expect(run.stdout).toContain('nothing to check');
});
