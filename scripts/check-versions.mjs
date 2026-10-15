// Single source of truth for the product version.
//
//   node scripts/check-versions.mjs            # files agree with each other
//   node scripts/check-versions.mjs v0.1.3     # ...and with a release tag
//
// `package.json` and `extension/manifest.json` both carry the version (Chrome reads the
// manifest one), so a release that bumps only one of them must not ship. release-please
// updates both through the `extra-files` entry in `release-please-config.json`, and this
// script is what makes a drift loud instead of silent.
import { readFileSync } from 'node:fs';

const read = (path) => JSON.parse(readFileSync(new URL(`../${path}`, import.meta.url), 'utf8'));
const { version: appVersion } = read('package.json');
const { version: manifestVersion } = read('extension/manifest.json');

const problems = [];
if (appVersion !== manifestVersion) {
	problems.push(`package.json ${appVersion} != extension/manifest.json ${manifestVersion}`);
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
