import { test } from 'node:test';
import assert from 'node:assert/strict';
import { mkdtemp, mkdir, writeFile, rm } from 'node:fs/promises';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { spawnSync } from 'node:child_process';

const manifest = {
  manifest_version: 3, minimum_chrome_version: '116',
  host_permissions: ['http://*/*', 'https://*/*'],
  content_security_policy: { extension_pages: "script-src 'self'; object-src 'none'" },
  action: {}, options_page: 'index.html'
};
const good = {
  'manifest.json': JSON.stringify(manifest),
  'index.html': '<link href="assets/app.css"><script type="module" src="assets/app.js"></script>',
  'assets/app.css': '.flex{display:flex}.grid{display:grid}',
  'assets/app.js': '/* PKCS timestamp CAdES PAdES XAdES */',
  'background.js': '', 'page.js': '', 'content.js': ''
};
for (const entry of [
  ['accepts self-contained app', {}, null],
  ['rejects a manifest without the all-sites host grant', { 'manifest.json': JSON.stringify({ ...manifest, host_permissions: undefined }) }, 'all-sites interception is the install-time default'],
  ['rejects a narrowed all-sites host grant', { 'manifest.json': JSON.stringify({ ...manifest, host_permissions: ['https://*/*'] }) }, 'all-sites interception is the install-time default'],
  ['rejects wildcard hosts as API optional_permissions', { 'manifest.json': JSON.stringify({ ...manifest, optional_permissions: manifest.host_permissions }) }, 'site access is requested as host patterns, not API permissions'],
  ['rejects removed app entry with orphan bundle retained', { 'index.html': '<link href="assets/app.css">' }, 'application module entry absent'],
  ['rejects classic script as app entry', { 'index.html': '<script src="assets/app.js"></script>' }, 'application module entry absent'],
  ['rejects orphan app features when entry is empty', { 'assets/entry.js': '', 'index.html': '<script type="module" src="assets/entry.js"></script>' }, 'full application feature absent'],
  ['accepts features in reachable app import', { 'assets/entry.js': 'import "./app.js";', 'index.html': '<link href="assets/app.css"><script type="module" src="assets/entry.js"></script>' }, null],
  ['accepts features in reachable dynamic import', { 'assets/entry.js': 'import("./app.js");', 'index.html': '<link href="assets/app.css"><script type="module" src="assets/entry.js"></script>' }, null],
  ['accepts features in re-exported chunk', { 'assets/entry.js': 'export * from "./app.js";', 'index.html': '<link href="assets/app.css"><script type="module" src="assets/entry.js"></script>' }, null],
  ['accepts a no-substitution template dynamic import', { 'assets/entry.js': 'import(`./app.js`);', 'index.html': '<link href="assets/app.css"><script type="module" src="assets/entry.js"></script>' }, null],
  ['rejects a computed dynamic import', { 'assets/entry.js': 'import(`./${name}.js`);', 'index.html': '<link href="assets/app.css"><script type="module" src="assets/entry.js"></script>' }, 'nonliteral dynamic module dependency'],
  ['rejects missing reachable chunk', { 'assets/app.js': 'import "./missing.js";' }, 'ENOENT'],
  ['rejects inline bootstrap', { 'index.html': '<script>mountApp()</script>' }, 'inline executable script'],
  ['rejects eval', { 'assets/app.js': 'eval("remote")' }, 'dynamic code execution'],
  ['rejects missing assets', { 'index.html': '<script src="missing.js"></script>' }, 'ENOENT'],
  ['rejects web PWA registration', { 'assets/app.js': 'navigator.serviceWorker.register("service-worker.js")' }, 'webapp worker registration'],
  ['rejects app subset', { 'assets/app.js': '/* CAdES */' }, 'full application feature absent'],
  ['rejects missing shared utility styles', { 'assets/app.css': 'body{}' }, 'shared app Tailwind utilities absent'],
  ['rejects remote assets', { 'index.html': '<script src="https://host/app.js"></script>' }, 'remote bootstrap asset'],
  ['rejects a manifest icon missing from the package', { 'manifest.json': JSON.stringify({ ...manifest, icons: { '128': 'icon-128.png' } }) }, 'ENOENT'],
  ['rejects Chrome-reserved underscore names', { '_headers': '/*\n  X-Frame-Options: DENY\n' }, 'Chrome reserves names starting with "_"']
]) {
  const [name, changes, message] = /** @type {[string, Record<string, string>, (string | null)]} */ (entry);
  test(name, async () => {
    const directory = await mkdtemp(join(tmpdir(), 'autofirme-artifacts-'));
    try {
      await mkdir(join(directory, 'assets'));
      for (const [file, text] of Object.entries({ ...good, ...changes })) await writeFile(join(directory, file), text);
      const result = spawnSync(process.execPath, ['scripts/check-extension.mjs', directory], { encoding: 'utf8' });
      if (message === null) assert.equal(result.status, 0, result.stderr);
      else { assert.notEqual(result.status, 0); assert.ok(result.stderr.includes(message), result.stderr); }
    } finally { await rm(directory, { recursive: true, force: true }); }
  });
}
