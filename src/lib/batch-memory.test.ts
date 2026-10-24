/**
 * Regression for review finding 1 on #133: the batch CLI kept every signed output in
 * `document.bytes`, so peak memory grew with the batch instead of staying at the largest single
 * file. The reviewer fixed the retention (the manifest row no longer carries the bytes) but the
 * issue's own acceptance criterion — "peak memory is the largest single file, never the whole
 * batch" — had no test behind it.
 *
 * The check is a bound relative to the batch, not an absolute byte count: what must hold is that
 * the peak stays far below the total the batch signs, whatever this machine's or runner's
 * baseline. The CLI subprocess samples its own reachable `ArrayBuffer` bytes (`arrayBuffers`) with
 * a forced GC in the sampler, so it measures the live set rather than uncollected garbage, and it
 * reports the peak on exit. Retaining N outputs puts that peak above half the batch; dropping them
 * keeps it at the working set of one file — measured 30 MB against a 128 MB batch for the fixed
 * code and 137 MB for the pre-fix code.
 */

import { spawnSync } from 'node:child_process';
import { mkdirSync, rmSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { afterAll, beforeAll, expect, it } from 'vitest';
import { createCadesFixtures, FIXTURE_PIN, writeFixtureFile, type CadesFixtures } from '../test/fixtures';

// jsdom gives this suite an `http://` base, so `import.meta.url` is not a file URL; vitest runs
// with the repo root as cwd, which is what the CLI subprocess needs anyway.
const ROOT = process.cwd();
const MIB = 1024 * 1024;
const FILE_MIB = 4;
const FILES = 32;
const BATCH_MIB = FILE_MIB * FILES;

/**
 * Runs inside the CLI child: samples reachable `ArrayBuffer` bytes — the memory the CLI would be
 * retaining — with a forced GC so garbage does not mask the live set, and prints the peak on exit.
 * Needs `--expose-gc`.
 *
 * A sampling interval is the only way to observe another process's peak: there is no signal the
 * CLI emits per file, so deterministic time control has no hook here. It is `unref`ed, so it never
 * extends the run — it costs one GC per tick, nothing more — and it is a measurement loop, not a
 * wait for a condition.
 */
const SAMPLER = `data:text/javascript,${encodeURIComponent(`
let peak = 0;
const sample = () => {
	global.gc();
	const kb = Math.round(process.memoryUsage().arrayBuffers / 1024);
	if (kb > peak) peak = kb;
};
const timer = setInterval(sample, 100);
timer.unref();
process.on('exit', () => console.log('PEAK_AB_KB', peak));
`)}`;

let fixtures: CadesFixtures;
let root: string;
let p12Path: string;
let input: string;
let output: string;

/** Sign the whole batch and return the CLI child's peak reachable ArrayBuffer bytes, in KB. */
function peakRetainedKb(): number {
	rmSync(output, { recursive: true, force: true });
	const run = spawnSync(
		process.execPath,
		[
			'--expose-gc',
			'--import', SAMPLER,
			'--import', './scripts/ts-hook.mjs',
			'scripts/batch-sign.mjs',
			'--p12', p12Path,
			'--out', output,
			'--format', 'xades',
			input
		],
		{ cwd: ROOT, encoding: 'utf8', env: { ...process.env, P12_PIN: FIXTURE_PIN } }
	);
	expect(run.status, run.stdout + run.stderr).toBe(0);
	expect(
		run.stdout,
		'every file must actually be signed, or the peak reading proves nothing'
	).toContain(`signed ${FILES}, failed 0`);

	const reported = /PEAK_AB_KB (\d+)/.exec(run.stdout);
	expect(reported, `no PEAK_AB_KB in the child output:\n${run.stdout}`).not.toBeNull();
	return Number(reported![1]);
}

beforeAll(() => {
	fixtures = createCadesFixtures();
	root = join(tmpdir(), `autofirme-batch-memory-${process.pid}`);
	rmSync(root, { recursive: true, force: true });
	input = join(root, 'in');
	output = join(root, 'out');
	mkdirSync(input, { recursive: true });
	p12Path = writeFixtureFile(root, 'cert.p12', fixtures.p12);

	// XAdES output is about the size of its input, so retaining it is visible here; CAdES
	// signature bytes are constant and small, and PAdES cannot take padded input.
	const pad = 'x'.repeat(FILE_MIB * MIB - 128);
	for (let index = 0; index < FILES; index += 1) {
		writeFileSync(
			join(input, `document-${String(index).padStart(3, '0')}.xml`),
			`<?xml version="1.0" encoding="UTF-8"?><root><pad>${pad}</pad></root>`
		);
	}
}, 60_000);

afterAll(() => {
	rmSync(root, { recursive: true, force: true });
	rmSync(fixtures.dir, { recursive: true, force: true });
});

it('does not let the batch CLI retain signed outputs', () => {
	const peakKb = peakRetainedKb();
	const batchKb = BATCH_MIB * 1024;
	expect(
		peakKb,
		`peak reachable memory ${peakKb} KB for a ${BATCH_MIB} MB batch; the CLI is retaining ` +
			`signed outputs again instead of releasing each one after it is written`
	).toBeLessThan(batchKb / 2);
}, 180_000);
