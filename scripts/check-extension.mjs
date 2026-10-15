import assert from 'node:assert/strict';
import { readFile, readdir, stat } from 'node:fs/promises';
import path from 'node:path';

const root = path.resolve(process.argv[2] ?? 'dist/extension');
const read = (name) => readFile(path.join(root, name), 'utf8');
const manifest = JSON.parse(await read('manifest.json'));
assert.equal(manifest.manifest_version, 3);
assert.equal(manifest.minimum_chrome_version, '116');
assert.equal(manifest.content_security_policy.extension_pages, "script-src 'self'; object-src 'none'");
assert.equal(manifest.action.default_popup, undefined, 'signing must survive focus loss');
assert.equal(manifest.options_page, 'index.html');
assert.equal(manifest.host_permissions?.length ?? 0, 0, 'hosts require explicit grants');
const html = await read('index.html');
assert.ok(!/<script\b(?![^>]*\bsrc=)[^>]*>\s*\S/i.test(html), 'inline executable script');
assert.ok(!/\son\w+\s*=/i.test(html), 'inline event handler');
assert.ok(!/manifest\.webmanifest/.test(html), 'PWA manifest in extension');
for (const match of html.matchAll(/(?:src|href)="([^"]+)"/g)) {
  assert.ok(!/^(https?:)?\/\//.test(match[1]), 'remote bootstrap asset');
  await stat(path.resolve(root, match[1].replace(/^\//, '')));
}
async function files(directory) {
  return (await Promise.all((await readdir(directory, { withFileTypes: true })).map(async (entry) => {
    const name = path.join(directory, entry.name);
    return entry.isDirectory() ? files(name) : [name];
  }))).flat();
}
const javascript = await Promise.all((await files(root)).filter((name) => name.endsWith('.js')).map((name) => readFile(name, 'utf8')));
const code = javascript.join('\n');
assert.ok(!/\beval\s*\(|\bnew\s+Function\s*\(/.test(code), 'dynamic code execution');
assert.ok(!/serviceWorker\s*\.\s*register\s*\(/.test(code), 'webapp worker registration');
for (const feature of ['PKCS', 'timestamp', 'CAdES', 'PAdES', 'XAdES']) {
  assert.ok(code.includes(feature), `full application feature absent: ${feature}`);
}
const cssFiles = (await files(root)).filter((name) => name.endsWith('.css'));
const css = (await Promise.all(cssFiles.map((name) => readFile(name, 'utf8')))).join('\n');
assert.ok(css.includes('.flex{') && css.includes('.grid{'), 'shared app Tailwind utilities absent');
for (const cssFile of cssFiles) {
  const sheet = await readFile(cssFile, 'utf8');
  for (const match of sheet.matchAll(/url\(["']?([^"')]+)["']?\)/g)) {
    if (!match[1].startsWith('data:')) await stat(path.resolve(path.dirname(cssFile), match[1]));
  }
}
for (const name of ['background.js', 'page.js', 'content.js']) await stat(path.join(root, name));
console.log('Extension artifact checks passed: full app, local assets, MV3 CSP and no PWA registration.');
