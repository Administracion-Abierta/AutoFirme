/**
 * Regression for the release pipeline after #19 (A1/A1b) and the GitHub
 * re-platform (#198): releases build and publish on GitHub Actions; Forgejo
 * stays CI-only and must not publish (publish jobs are gated on
 * github.server_url). Each assertion below guards a shipped-scope invariant,
 * not wording.
 */

import { test } from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { join, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = join(dirname(fileURLToPath(import.meta.url)), '..');
const read = (path) => readFileSync(join(root, path), 'utf8');
const workflow = read('.github/workflows/release.yml');
const devWorkflow = read('.github/workflows/release-develop.yml');
const ciWorkflow = read('.github/workflows/ci.yml');
const rpWorkflow = read('.github/workflows/release-please.yml');
const pkg = JSON.parse(read('package.json'));

/** Body of one top-level job in a workflow file. */
function job(source, name) {
	const start = source.indexOf(`\n  ${name}:\n`);
	assert.ok(start >= 0, `job ${name} missing`);
	const rest = source.slice(start + 1);
	const next = rest.slice(1).search(/^ {2}\S/m);
	return next < 0 ? rest : rest.slice(0, next + 1);
}

test('release runs on the published release, never on a bare tag push', () => {
	const onStart = workflow.indexOf('\non:\n');
	assert.ok(onStart >= 0, 'on: block missing');
	const onEnd = workflow.indexOf('\npermissions:', onStart);
	assert.ok(onEnd > onStart, 'permissions: must follow on:');
	assert.equal(workflow.slice(onStart + 1, onEnd), 'on:\n  release:\n    types: [published]\n');
});

test('the artifacts job builds twice and diffs SHA256SUMS before uploading', () => {
	const artifacts = job(workflow, 'artifacts');
	assert.match(artifacts, /cp dist\/SHA256SUMS sums-a\.txt/);
	assert.match(artifacts, /rm -rf dist build \.svelte-kit/);
	assert.match(artifacts, /diff sums-a\.txt dist\/SHA256SUMS/);
	const packs = artifacts.match(/pnpm package:extension/g);
	assert.ok(packs && packs.length === 2, `expected exactly 2 package:extension runs, got ${packs?.length}`);
	assert.ok(
		artifacts.indexOf('diff sums-a.txt') < artifacts.indexOf('scripts/release.sh'),
		'reproducibility diff must run before the release upload'
	);
});

test('the ghcr image job is gone (A1)', () => {
	for (const source of [workflow, devWorkflow]) {
		assert.doesNotMatch(source, /^\s+image:/m, 'image job must not come back');
		assert.doesNotMatch(source, /ghcr\.io/);
		assert.doesNotMatch(source, /packages: write/);
		assert.doesNotMatch(source, /Containerfile/);
	}
});

test('the Cloudflare deploy job is gone (A1)', () => {
	assert.doesNotMatch(ciWorkflow, /^\s+deploy:/m);
	assert.doesNotMatch(ciWorkflow, /wrangler/);
	if (pkg.devDependencies?.wrangler ?? pkg.dependencies?.wrangler) {
		assert.fail('wrangler is installed again: reinstate a deploy job or drop the dependency');
	}
});

test('the develop static tarball is gone (A1)', () => {
	assert.doesNotMatch(devWorkflow, /autofirme-web-dev\.tar\.gz/);
});

test('out-of-scope chrome-webstore and attestation stay out', () => {
	assert.doesNotMatch(workflow, /chrome/i, 'Chrome Web Store is deferred to its own issue');
	assert.doesNotMatch(workflow, /attest-build-provenance|id-token/, 'not requested in #3');
});

test('the linux-packages job collects bundles into dist before uploading (review round 1)', () => {
	const linux = job(workflow, 'linux-packages');
	// AppImage is back: GitHub caps release assets at 2 GiB, Forgejo's ~100 MiB
	// cap 413'd the attach (#190). GitHub is now the only publish host.
	for (const dir of ['deb', 'rpm', 'appimage']) {
		assert.match(linux, new RegExp(`cp src-tauri/target/release/bundle/${dir}/\\*\\.\\S+ dist/`));
	}
	assert.match(linux, /sha256sum \*\.deb \*\.rpm \*\.AppImage > SHA256SUMS\.linux/);
	assert.ok(
		linux.indexOf('sha256sum') < linux.indexOf('scripts/release.sh'),
		'packages must be collected and checksummed before the release upload'
	);
});

test('release uploads go through the host-detecting release.sh helper', () => {
	for (const j of ['artifacts', 'linux-packages']) {
		assert.match(job(workflow, j), /scripts\/release\.sh/);
	}
	assert.match(job(devWorkflow, 'artifacts'), /scripts\/release\.sh/);
	assert.doesNotMatch(workflow, /forgejo-release\.sh/);
});

test('Forgejo never publishes: publish jobs are gated on the GitHub server URL', () => {
	// Both hosts share the workflow tree; the gate keeps the Forgejo side silent
	// (no release-please PRs, no dev pre-releases). release.yml is inherently
	// host-local: `on: release: published` fires only where the release lives.
	assert.match(rpWorkflow, /startsWith\(github\.server_url, 'https:\/\/github\.com'\)/);
	assert.match(job(devWorkflow, 'artifacts'), /startsWith\(github\.server_url, 'https:\/\/github\.com'\)/);
});

test('no workflow references a Forgejo-prefixed action URL or the Forgejo API', () => {
	// On GitHub these fail resolution ("Unable to resolve action
	// https:/github.com"); on Forgejo DEFAULT_ACTIONS_URL resolves plain refs.
	for (const source of [workflow, devWorkflow, ciWorkflow, rpWorkflow]) {
		assert.doesNotMatch(source, /uses: https?:\/\//, 'fully qualified action URL must go');
	}
	assert.doesNotMatch(rpWorkflow, /github-api-url/, 'release-please must use the GitHub API');
});

test('workflows pin upload-artifact v4 (v3 is force-failed on GitHub)', () => {
	for (const source of [workflow, devWorkflow, ciWorkflow]) {
		assert.doesNotMatch(source, /upload-artifact@v3/);
	}
	const uses = [workflow, devWorkflow, ciWorkflow].join('\n').match(/upload-artifact@\S+/g) ?? [];
	assert.ok(uses.length >= 4 && uses.every((u) => u === 'upload-artifact@v4'), uses.join(' '));
});

test('every upload-artifact step is skipped on non-GitHub hosts (v4 = GHESNotSupportedError there)', () => {
	const sources = [workflow, devWorkflow, ciWorkflow];
	const blocks = sources.flatMap((s) => s.match(/- uses: actions\/upload-artifact@\S+[^-]*?if: startsWith\(github\.server_url, 'https:\/\/github\.com'\)/gs) ?? []);
	assert.ok(blocks.length >= 5, `found ${blocks.length} upload blocks`);
	for (const block of blocks) {
		assert.match(block, /if: startsWith\(github\.server_url, 'https:\/\/github\.com'\)/, block);
	}
});

test('the tauri shell job only runs on GitHub (dtolnay/* is not on the Forgejo mirror)', () => {
	const job = ciWorkflow.match(/^  shell:\n(?:  .*\n|\n)*/m)?.[0] ?? '';
	assert.match(job, /if: startsWith\(github\.server_url, 'https:\/\/github\.com'\)/);
});

test('the trixie container pins the release jobs (review round 1)', () => {
	const linux = job(workflow, 'linux-packages');
	// node:24-trixie = debian:trixie + node; keeps the OpenSSL 3.5 floor and lets the
	// node-based actions/* steps run (plain trixie has no node).
	assert.match(linux, /container: node:24-trixie/);
	// The helper needs only curl + python3, both preinstalled in the image, so
	// the apt list must NOT grow them.
	const lines = linux.split('\n');
	const start = lines.findIndex((line) => line.includes('apt-get install'));
	assert.ok(start >= 0, 'toolchain apt-get install step missing');
	let list = '';
	for (let i = start; i < lines.length; i++) {
		list += ` ${lines[i]}`;
		if (!lines[i].trimEnd().endsWith('\\')) break;
	}
	const pkgs = list.replace(/\\/g, ' ').split(/\s+/).filter(Boolean);
	assert.ok(!pkgs.includes('gh'), 'gh is not in the build container; release.sh uses curl');
});

// The develop pre-release channel: every push to develop ships code + extension zip
// as a marked-unstable dev-<sha> pre-release (GitHub only).

test('develop release triggers only on develop pushes and manual dispatch', () => {
	const onEnd = devWorkflow.indexOf('\npermissions:');
	assert.ok(onEnd > 0, 'permissions must follow on:');
	assert.equal(
		devWorkflow.slice(0, onEnd),
		`name: Develop release\n\n# Every push to develop ships a pre-release tagged dev-<short-sha>: the rolling\n# test channel for the extension zip and the static site. Production releases\n# keep coming from release-please on main (release.yml). Publish step is\n# GitHub-only: both hosts share this tree and the Forgejo side must stay quiet.\non:\n  push:\n    branches: [develop]\n  workflow_dispatch:\n`
	);
});

test('develop artifacts job publishes the pre-release with code, extension zip and checksums', () => {
	const artifacts = job(devWorkflow, 'artifacts');
	assert.match(artifacts, /pnpm build\n/);
	assert.match(artifacts, /pnpm package:extension/);
	// The tag is commit-pinned so a release always maps to exactly one tree.
	assert.match(artifacts, /TAG="dev-\$\{GITHUB_SHA::7\}"/);
	// The helper derives tag/commit from the env GITHUB_TOKEN context: --sha pins the
	// release to the exact tree, --prerelease keeps it off the stable channel.
	assert.match(artifacts, /--sha "\$GITHUB_SHA"/);
	assert.match(artifacts, /--prerelease/);
	assert.match(artifacts, /--title "\[Development build\] \$TAG/);
	assert.match(artifacts, /--notes-file release-notes\.md/);
	assert.match(artifacts, /dist\/autofirme-extension-\*\.zip dist\/SHA256SUMS/);
});

test('develop releases are labelled as development builds', () => {
	const artifacts = job(devWorkflow, 'artifacts');
	assert.match(artifacts, /NOTES: \|-/);
	assert.match(artifacts, /not for production use/);
	// The label is a user-facing promise; the notes must also state the channel.
	assert.match(artifacts, /Rolling pre-release/);
});
