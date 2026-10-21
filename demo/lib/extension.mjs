// Launches Chrome for Testing with the unpacked AutoFirme extension and hands back
// Playwright contexts plus the extension's stable ID.
//
// Two things make this fiddly, and both are handled here:
//   - MV3 extensions need a persistent profile and `--load-extension`. Branded Google
//     Chrome 137+ ignores that flag, so the launch refuses it rather than failing later
//     with a confusing "no chooser ever opened" symptom.
//   - the extension's ID is derived from its unpacked path, so the demo must compute it
//     the same way Chrome does before it can open `chrome-extension://…/index.html`.
import {createHash} from 'node:crypto';
import {access, cp, mkdir, mkdtemp, readFile, rm} from 'node:fs/promises';
import {existsSync} from 'node:fs';
import {spawnSync} from 'node:child_process';
import {tmpdir} from 'node:os';
import {join, resolve} from 'node:path';
import {chromium} from 'playwright';

/** Chrome derives an unpacked extension's ID from a SHA-256 of its absolute path. */
export function extensionIdFor(extensionPath) {
  const digest = createHash('sha256').update(extensionPath).digest('hex').slice(0, 32);
  return [...digest].map(character => String.fromCharCode(97 + parseInt(character, 16))).join('');
}

async function findChromium() {
  if (process.env.CHROME_PATH) return process.env.CHROME_PATH;
  // Playwright's own download is the default; `pnpm demo:registradores` prints the
  // command to install it when it is missing.
  try {
    const executable = chromium.executablePath();
    if (executable && existsSync(executable)) return executable;
  } catch {} // Not downloaded yet: fall through to the error below.
  throw Error('Chromium is missing. Run: pnpm exec playwright install chromium (or set CHROME_PATH).');
}

async function assertLoadsExtensions(executable) {
  const version = spawnSync(executable, ['--version'], {encoding: 'utf8'});
  const flavour = `${version.stdout ?? ''}${version.stderr ?? ''}`.trim();
  if (/^Google Chrome\b/.test(flavour) && !/for Testing/.test(flavour)) {
    throw Error(`${flavour} ignores --load-extension since 137. Use Chrome for Testing or Chromium, e.g. CHROME_PATH=~/.cache/ms-playwright/chromium-<build>/chrome-linux64/chrome.`);
  }
  return flavour;
}

/**
 * Start a browser with the extension loaded and the demo identity already imported.
 *
 * @param {object} options
 * @param {string} options.identity PKCS#12 path produced by `createTestIdentity`.
 * @param {string} options.pin PIN of that container.
 * @param {string} options.subject Certificate subject, matched in the picker UI.
 * @param {boolean} [options.headless] Defaults to headless; `--headed` overrides.
 * @param {string} [options.extensionDir] Built extension directory; defaults to `dist/extension`.
 * @returns {Promise<object>} handle with `context`, `app`, `extensionId`, `chromeFlavour` and `close()`.
 */
export async function launchWithExtension({identity, pin, subject, headless = true, extensionDir = resolve('dist/extension')}) {
  const built = resolve(extensionDir);
  await access(join(built, 'manifest.json')).catch(() => {
    throw Error(`No built extension at ${built}. Run: pnpm build:extension`);
  });
  const manifest = JSON.parse(await readFile(join(built, 'manifest.json'), 'utf8'));
  if (manifest.manifest_version !== 3) throw Error('Expected a Manifest V3 extension');
  if (!manifest.host_permissions?.includes('https://*/*')) throw Error('Extension is missing all-sites interception');

  const executable = await findChromium();
  const chromeFlavour = await assertLoadsExtensions(executable);

  const scratch = await mkdtemp(join(tmpdir(), 'autofirme-demo-'));
  const extension = join(scratch, 'extension');
  await cp(built, extension, {recursive: true});
  const profileDir = join(scratch, 'profile');
  await mkdir(profileDir, {recursive: true});

  const browser = await chromium.launchPersistentContext(profileDir, {
    executablePath: executable,
    headless,
    args: [
      `--disable-extensions-except=${extension}`,
      `--load-extension=${extension}`,
      '--no-proxy-server',
      '--no-first-run',
    ],
  });
  const extensionId = extensionIdFor(extension);
  const appURL = `chrome-extension://${extensionId}/index.html`;
  const app = browser.pages()[0] ?? await browser.newPage();
  await app.goto(appURL, {waitUntil: 'domcontentloaded'});
  await app.waitForSelector('h1', {timeout: 15_000});

  if (identity) await importIdentity({app, identity, pin, subject});

  return {
    browser,
    context: browser,
    app,
    extensionId,
    appURL,
    chromeFlavour,
    scratch,
    async close() {
      await browser.close();
      await rm(scratch, {recursive: true, force: true});
    },
  };
}

/** Import the PKCS#12 identity through the real app UI, then select it as the choice. */
async function importIdentity({app, identity, pin, subject}) {
  await app.click('#pkcs12-picker');
  await app.setInputFiles('#pkcs12-file', identity);
  await app.waitForSelector('#add-pin', {timeout: 15_000});
  await app.fill('#add-pin', pin);
  await app.press('#add-pin', 'Enter');
  await app.waitForFunction(
    name => document.querySelector('#pkcs12-picker')?.textContent?.includes(name) && !document.querySelector('[role=dialog][data-state=open]'),
    subject,
    {timeout: 15_000},
  );
}

/** The chooser popup Chrome opens for a pending website request. */
export function isChooserPage(page) {
  return page.url().includes('index.html?request=');
}

/**
 * Wait for a *new* chooser popup and return it.
 *
 * Chrome opens each chooser as its own window, so with several requests in flight the
 * already-open ones would otherwise satisfy the wait. Pass the popups already known about
 * and only a genuinely new window is returned.
 *
 * @param {import('playwright').BrowserContext} context
 * @param {{known?: Array<import('playwright').Page>, timeout?: number}} [options]
 */
export async function waitForChooser(context, {known = [], timeout = 25_000} = {}) {
  const seen = new Set(known);
  const deadline = Date.now() + timeout;
  while (Date.now() < deadline) {
    for (const candidate of context.pages()) {
      if (isChooserPage(candidate) && !seen.has(candidate)) return candidate;
    }
    const remaining = Math.max(0, deadline - Date.now());
    await Promise.race([
      new Promise(resolve => setTimeout(resolve, 100)),
      context.waitForEvent('page', {timeout: Math.min(500, remaining)}).catch(() => {}),
    ]);
  }
  throw Error('No new AutoFirme chooser window appeared');
}