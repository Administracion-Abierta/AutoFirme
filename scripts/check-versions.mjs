// Single source of truth for the product version.
//
//   node scripts/check-versions.mjs            # files agree with each other
//   node scripts/check-versions.mjs v0.1.3     # ...and with a release tag
//
// Five files carry the version: `package.json`, `extension/manifest.json` (Chrome reads
// that one), `src-tauri/tauri.conf.json`, `src-tauri/Cargo.toml` and the shell's own entry in
// `src-tauri/Cargo.lock` (CI runs `cargo check --locked`, which fails on a stale lockfile).
// release-please updates all five through the `extra-files` entries in
// `release-please-config.json`; this script is what makes a drift loud instead of silent.
import { readFileSync } from 'node:fs';

const read = (path) => readFileSync(new URL(`../${path}`, import.meta.url), 'utf8');
const readJson = (path) => JSON.parse(read(path));
const { version: appVersion } = readJson('package.json');
const { version: manifestVersion } = readJson('extension/manifest.json');
const { version: tauriVersion } = readJson('src-tauri/tauri.conf.json');

const cargo = read('src-tauri/Cargo.toml');
const packageSection = cargo.split(/^\[[^\]]+\]$/m)[1] ?? '';
const cargoVersion = /^version\s*=\s*"([^"]+)"/m.exec(packageSection)?.[1];

const lock = read('src-tauri/Cargo.lock');
const lockEntry = lock.split(/^\[\[package\]\]$/m).find((entry) => /^name\s*=\s*"autofirme-desktop"$/m.test(entry)) ?? '';
const lockVersion = /^version\s*=\s*"([^"]+)"/m.exec(lockEntry)?.[1];

const problems = [];
for (const [path, version] of [
	['extension/manifest.json', manifestVersion],
	['src-tauri/tauri.conf.json', tauriVersion],
	['src-tauri/Cargo.toml', cargoVersion],
	['src-tauri/Cargo.lock', lockVersion]
]) {
	if (version !== appVersion) problems.push(`package.json ${appVersion} != ${path} ${version ?? '(missing version)'}`);
}
// Chrome rejects anything but 1-4 dot-separated numbers, so a bad bump breaks the load.
if (!/^\d+(\.\d+){0,3}$/.test(manifestVersion)) {
	problems.push(`extension/manifest.json version "${manifestVersion}" is not 1-4 dot-separated numbers`);
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
