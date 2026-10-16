/**
 * Regression for the #3 review findings: the release pipeline must stay on the
 * `release: published` trigger (finding 1), run the double-build / SHA256SUMS gate
 * inside the publishing `artifacts` job before any upload (finding 3), ship the
 * ghcr.io image only after that gate passes (finding 4), and keep the out-of-scope
 * Chrome Web Store job and build attestation out (findings 5 and 6).
 *
 * Each assertion guards a shipped-scope invariant, not wording: removing or
 * re-placing the gate so it no longer runs before publication fails CI here.
 * Finding 2 (branch based on `develop`) is a git-state fact with no in-repo
 * subject; it was fixed by the merge and cannot pin in a test.
 */

import { test } from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { join, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';

const workflow = readFileSync(
	join(dirname(fileURLToPath(import.meta.url)), '..', '.github', 'workflows', 'release.yml'),
	'utf8'
);

/** Body of one top-level job, from `  <name>:` to the next two-space key. */
function job(name) {
	const start = workflow.indexOf(`\n  ${name}:\n`);
	assert.ok(start >= 0, `job ${name} missing from release.yml`);
	const rest = workflow.slice(start + 1);
	const next = rest.slice(1).search(/^ {2}\S/m);
	return next < 0 ? rest : rest.slice(0, next + 1);
}

test('release runs on the published release, never on a bare tag push (finding 1)', () => {
	// Exact pin of the whole trigger block: `release: published` must be the only
	// trigger. A loose `tags:` check misses e.g. `push: tags: [v*]` added next to
	// `release:` (both live at different indents), which would silently re-enable
	// the obsolete tag pipeline the issue forbids.
	const onStart = workflow.indexOf('\non:\n');
	assert.ok(onStart >= 0, 'on: block missing');
	const onEnd = workflow.indexOf('\npermissions:', onStart);
	assert.ok(onEnd > onStart, 'permissions: must follow on:');
	assert.equal(
		workflow.slice(onStart + 1, onEnd),
		'on:\n  release:\n    types: [published]\n'
	);
});

test('the artifacts job builds twice and diffs SHA256SUMS before uploading (finding 3)', () => {
	const artifacts = job('artifacts');
	assert.match(artifacts, /cp dist\/SHA256SUMS sums-a\.txt/);
	assert.match(artifacts, /rm -rf dist build \.svelte-kit/);
	assert.match(artifacts, /diff sums-a\.txt dist\/SHA256SUMS/);
	const packs = artifacts.match(/pnpm package:extension/g);
	assert.ok(packs && packs.length === 2, `expected exactly 2 package:extension runs, got ${packs?.length}`);
	// The gate is only meaningful before anything reaches the release.
	assert.ok(
		artifacts.indexOf('diff sums-a.txt') < artifacts.indexOf('gh release upload'),
		'reproducibility diff must run before gh release upload'
	);
});

test('the image job publishes the Containerfile to ghcr.io after the gate (finding 4)', () => {
	const image = job('image');
	assert.match(image, /needs: artifacts/);
	assert.match(image, /packages: write/);
	assert.match(image, /registry: ghcr\.io/);
	assert.match(image, /file: Containerfile/);
});

test('out-of-scope chrome-webstore and attestation stay out (findings 5, 6)', () => {
	assert.doesNotMatch(workflow, /chrome/i, 'Chrome Web Store is deferred to its own issue');
	assert.doesNotMatch(workflow, /attest-build-provenance|id-token/, 'not requested in #3');
});
