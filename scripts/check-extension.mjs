import assert from 'node:assert/strict';
import { readFile, readdir, stat } from 'node:fs/promises';
import path from 'node:path';
import ts from 'typescript';

const root = path.resolve(process.argv[2] ?? 'dist/extension');
const read = (name) => readFile(path.join(root, name), 'utf8');
const manifest = JSON.parse(await read('manifest.json'));
assert.equal(manifest.manifest_version, 3);
assert.equal(manifest.minimum_chrome_version, '116');
assert.equal(manifest.content_security_policy.extension_pages, "script-src 'self'; object-src 'none'");
assert.equal(manifest.action.default_popup, undefined, 'signing must survive focus loss');
assert.equal(manifest.options_page, 'index.html');
assert.deepEqual(manifest.host_permissions, ['http://*/*', 'https://*/*'], 'interception defaults to all websites');
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
// A complete orphaned bundle cannot prove that index.html actually loads the app.
const moduleEntries = [...html.matchAll(/<script\b([^>]*)>/gi)]
  .map((match) => match[1])
  .filter((attributes) => /\btype=["']module["']/i.test(attributes))
  .map((attributes) => /\bsrc=["']([^"']+)["']/i.exec(attributes)?.[1])
  .filter((src) => typeof src === 'string');
assert.ok(moduleEntries.length > 0, 'application module entry absent');
const reachable = new Map();
function localModule(specifier, importer) {
  assert.ok(!/^[a-z][a-z\d+.-]*:|^\/\//i.test(specifier), 'remote application module');
  assert.ok(specifier.startsWith('.') || specifier.startsWith('/'), 'unbundled application module');
  const filename = specifier.startsWith('/')
    ? path.resolve(root, `.${specifier}`)
    : path.resolve(path.dirname(importer), specifier);
  assert.ok(filename.startsWith(`${root}${path.sep}`), 'application module escapes package');
  return filename;
}
async function visitModule(filename) {
  if (reachable.has(filename)) return;
  const source = await readFile(filename, 'utf8');
  reachable.set(filename, source);
  // Parse JavaScript rather than matching import-like strings in comments or data.
  const syntax = ts.createSourceFile(filename, source, ts.ScriptTarget.Latest, false, ts.ScriptKind.JS);
  const imports = [];
  function visit(node) {
    if ((ts.isImportDeclaration(node) || ts.isExportDeclaration(node)) && node.moduleSpecifier) {
      assert.ok(ts.isStringLiteral(node.moduleSpecifier), 'nonliteral module dependency');
      imports.push(node.moduleSpecifier.text);
    } else if (ts.isCallExpression(node) && node.expression.kind === ts.SyntaxKind.ImportKeyword) {
      assert.ok(node.arguments.length > 0 && ts.isStringLiteral(node.arguments[0]), 'nonliteral dynamic module dependency');
      imports.push(node.arguments[0].text);
    }
    ts.forEachChild(node, visit);
  }
  visit(syntax);
  for (const specifier of imports) await visitModule(localModule(specifier, filename));
}
for (const entry of moduleEntries) {
  await visitModule(localModule(entry.startsWith('/') || entry.startsWith('.') ? entry : `./${entry}`, path.join(root, 'index.html')));
}
const applicationCode = [...reachable.values()].join('\n');
for (const feature of ['PKCS', 'timestamp', 'CAdES', 'PAdES', 'XAdES']) {
  assert.ok(applicationCode.includes(feature), `full application feature absent: ${feature}`);
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
