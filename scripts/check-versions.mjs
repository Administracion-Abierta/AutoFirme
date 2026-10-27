// Single source of truth for the product version.
//
//   node scripts/check-versions.mjs            # files agree with each other
//   node scripts/check-versions.mjs v0.1.3     # ...and with a release tag
//
// Four files carry the version: `package.json`, `src-tauri/tauri.conf.json`,
// `src-tauri/Cargo.toml` and the shell's own entry in `src-tauri/Cargo.lock` (CI runs
// `cargo check --locked`, which fails on a stale lockfile). release-please updates
// all four through the `extra-files` entries in `release-please-config.json`; this
// script is what makes a drift loud instead of silent.

import { readFileSync } from 'node:fs';
const read = (path) => readFileSync(new URL(`../${path}`, import.meta.url), 'utf8');
const readJson = (path) => JSON.parse(read(path));
const { version: appVersion } = readJson('package.json');
const { version: tauriVersion } = readJson('src-tauri/tauri.conf.json');

const cargo = read('src-tauri/Cargo.toml');
const packageSection = cargo.split(/^\[[^\]]+\]$/m)[1] ?? '';
const cargoVersion = /^version\s*=\s*"([^"]+)"/m.exec(packageSection)?.[1];

const lock = read('src-tauri/Cargo.lock');
const lockEntry = lock.split(/^\[\[package\]\]$/m).find((entry) => /^name\s*=\s*"autofirme-desktop"$/m.test(entry)) ?? '';
const lockVersion = /^version\s*=\s*"([^"]+)"/m.exec(lockEntry)?.[1];

const problems = [];
for (const [path, version] of [
	['src-tauri/tauri.conf.json', tauriVersion],
	['src-tauri/Cargo.toml', cargoVersion],
	['src-tauri/Cargo.lock', lockVersion]
]) {
	if (version !== appVersion) problems.push(`package.json ${appVersion} != ${path} ${version ?? '(missing version)'}`);
}

const tag = process.argv.slice(2).find((argument) => argument.startsWith('v'));
if (tag && tag !== `v${appVersion}`) {
	problems.push(`release tag ${tag} != v${appVersion}`);
}

if (problems.length) {
	console.error(`version mismatch:\n- ${problems.join('\n- ')}`);
	process.exit(1);
}
console.log(`versions agree: ${appVersion}${tag ? ` (tag ${tag})` : ''}`);
