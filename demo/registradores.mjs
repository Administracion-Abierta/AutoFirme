// Live end-to-end demo: the AutoFirme extension signs real documents requested by the
// Colegio de Registradores' own test page, and OpenSSL independently verifies every
// signature afterwards.
//
//   pnpm demo:registradores            # headless
//   pnpm demo:registradores --headed   # watch the chooser windows
//   pnpm demo:registradores --url=...  # a copy of the same page
//
// Nothing here is mocked: it loads the production build of the extension into Chrome for
// Testing, generates a throwaway certificate, and drives the website exactly as a citizen
// would. Exit code 0 means every signature the extension produced was verified by OpenSSL.
import {mkdir, mkdtemp} from 'node:fs/promises';
import {tmpdir} from 'node:os';
import {join} from 'node:path';
import assert from 'node:assert/strict';
import {createTestIdentity} from './lib/identity.mjs';
import {launchWithExtension, waitForChooser} from './lib/extension.mjs';
import {
  assertConfirmationGated, cancelChooser, fallbackChooser, shareCertificateWithChooser, signWithChooser,
} from './lib/chooser.mjs';
import {opensslVersion, verifyCades, verifyPades} from './lib/openssl.mjs';
import {
  REGISTRADORES_URL, fillForm, formPayload, generatedPayload, openTestPage, pressSign,
  readForm, readLatestMessage, readResult, selectSigningOptions, sha256Hex, waitForOutcome,
} from './lib/registradores.mjs';

const argv = process.argv.slice(2);
const headed = argv.includes('--headed');
const url = (argv.find(argument => argument.startsWith('--url=')) ?? '').slice('--url='.length) || REGISTRADORES_URL;

const log = message => process.stdout.write(`${message}\n`);
const heading = title => log(`\n\x1b[1m${title}\x1b[0m`);

let failures = 0;
const results = [];

/** Record one named check; a thrown error fails that check only. */
async function check(name, run) {
  try {
    const detail = await run();
    results.push({name, passed: true});
    log(`  PASS  ${name}${detail ? ` — ${detail}` : ''}`);
  } catch (error) {
    failures++;
    results.push({name, passed: false});
    log(`  FAIL  ${name}\n        ${String(error instanceof Error ? error.message : error).split('\n')[0]}`);
  }
}

/** Toggle a site through the extension's own message, so the worker re-registers scripts. */
async function setSiteEnabled(app, origin, enabled) {
  const reply = await app.evaluate(
    ([siteOrigin, isEnabled]) => new Promise(resolve =>
      // `chrome` exists in every extension page; this project ships no @types/chrome.
      globalThis.chrome.runtime.sendMessage({type: 'sites:set', origin: siteOrigin, enabled: isEnabled}, resolve)),
    [origin, enabled],
  );
  if (!reply?.ok) throw Error(`extension refused the site change: ${reply?.error ?? 'no reply'}`);
}

async function main() {
  log(`OpenSSL:     ${opensslVersion()}`);
  log(`Page:        ${url}`);
  const scratch = await mkdtemp(join(tmpdir(), 'autofirme-demo-run-'));
  const artifacts = join(scratch, 'openssl');
  await mkdir(artifacts, {recursive: true});

  log('Generating a throwaway signing identity (2 days, self-issued, kept in a temp profile)…');
  const identity = createTestIdentity(scratch);

  const session = await launchWithExtension({identity: identity.pkcs12, pin: identity.pin, subject: identity.subject, headless: !headed});
  log(`Browser:     ${session.chromeFlavour}`);
  log(`Extension:   chrome-extension://${session.extensionId}`);
  const {context} = session;
  // Each case gets its own tab: the page reuses modal DOM across reloads, so a leftover
  // "Error al firmar" from an earlier case can otherwise intercept the next one's clicks.
  let site = await context.newPage();

  /** True while any chooser window is open. */
  const chooserOpen = () => context.pages().some(page => page.url().includes('index.html?request='));

  /** Start each case from a clean tab with no chooser left over from the previous one. */
  const freshTab = async () => {
    const previous = site;
    for (const page of context.pages()) {
      if (page.url().includes('index.html?request=')) await page.close().catch(() => {});
    }
    site = await context.newPage();
    if (!previous.isClosed()) await previous.close().catch(() => {});
    return site;
  };

  /** The sample PDF the page signs for its `Documento PDF` target. */
  const samplePdf = async () => {
    const base64 = await site.evaluate(() => {
      const match = document.documentElement.innerHTML.match(/dataB64 = "(JVBERi0xLjM[^"]+)"/);
      return match ? match[1] : null;
    });
    assert.ok(base64, 'could not read the page sample PDF');
    return Buffer.from(base64, 'base64');
  };

  try {
    heading('CAdES: every target the page offers');
    const signingCases = [
      {name: 'Datos generados (81 KB non-PDF payload)', target: 'targetGeneratedData', payload: generatedPayload},
      {name: 'Documento PDF (the page sample PDF)', target: 'targetPdfDocument', payload: samplePdf},
      {
        name: 'Formulario (NIF + name)',
        target: 'targetFormulario',
        prepare: () => fillForm(site),
        payload: async () => formPayload(await readForm(site)),
      },
    ];

    for (const testCase of signingCases) {
      await check(testCase.name, async () => {
        await openTestPage(await freshTab(), url);
        if (testCase.prepare) await testCase.prepare();
        const payload = await testCase.payload();
        await selectSigningOptions(site, {target: testCase.target, format: 'CAdES'});
        await pressSign(site);

        const chooser = await waitForChooser(context);
        await assertConfirmationGated(chooser);
        const shownOrigin = (await chooser.locator('#request-origin').textContent())?.trim();
        const {digest} = await signWithChooser(chooser, {subject: identity.subject, pin: identity.pin});
        assert.equal(digest, sha256Hex(payload), 'the chooser showed a digest that does not match the payload');

        const outcome = await waitForOutcome(site, {expectSignature: true});
        assert.ok(outcome.signatureBase64, `the page received no signature: ${outcome.message ?? 'nothing'}`);
        assert.ok(outcome.downloadEnabled, 'the page did not enable its download button');

        const verification = verifyCades({
          directory: artifacts,
          signature: Buffer.from(outcome.signatureBase64, 'base64'),
          certificate: identity.certificate,
          detachedContent: payload,
        });
        assert.ok(verification.verified, `OpenSSL rejected the signature: ${verification.error}`);
        assert.deepEqual(verification.content, payload, 'OpenSSL recovered different content');
        return `${payload.length} bytes via ${shownOrigin}, verified by OpenSSL`;
      });
    }

    heading('Website contract');
    await check('AutoScript.selectCertificate returns the public certificate', async () => {
      await openTestPage(await freshTab(), url);
      await site.evaluate(() => {
        const globals = /** @type {{AutoScript?: any, __demoCertificate?: string | null}} */ (window);
        globals.__demoCertificate = null;
        globals.AutoScript?.selectCertificate(null, certificate => {
          globals.__demoCertificate = certificate;
        }, () => {});
      });
      const chooser = await waitForChooser(context);
      await shareCertificateWithChooser(chooser, {subject: identity.subject, pin: identity.pin});
      await site.waitForFunction(
        () => typeof /** @type {{__demoCertificate?: string | null}} */ (window).__demoCertificate === 'string',
        undefined,
        {timeout: 15_000},
      );
      const certificateBase64 = (await site.evaluate(() => /** @type {{__demoCertificate?: string | null}} */ (window).__demoCertificate)) ?? '';
      assert.ok(certificateBase64.length > 100, 'no certificate came back to the page');
      return `${Math.round(certificateBase64.length * 0.75)} bytes of DER certificate delivered`;
    });

    await check('cancelling reports cancellation and signs nothing', async () => {
      await openTestPage(await freshTab(), url);
      await selectSigningOptions(site, {target: 'targetPdfDocument', format: 'CAdES'});
      await pressSign(site);
      const chooser = await waitForChooser(context);
      await chooser.locator('#request-cancel').waitFor({timeout: 15_000});
      await cancelChooser(chooser);
      const outcome = await waitForOutcome(site, {expectSignature: false});
      assert.equal(outcome.signatureBase64, '', 'a cancelled request still produced a signature');
      assert.match(outcome.message ?? '', /cancel/i, `page reported: ${outcome.message}`);
      return outcome.message ?? 'cancelled';
    });

    await check('"Continuar con AutoFirma" resumes the original integration once', async () => {
      await openTestPage(await freshTab(), url);
      await selectSigningOptions(site, {target: 'targetPdfDocument', format: 'CAdES'});
      await pressSign(site);
      const chooser = await waitForChooser(context);
      await fallbackChooser(chooser);
      const outcome = await waitForOutcome(site, {expectSignature: false, timeout: 60_000});
      assert.ok(outcome.message, 'the site never reported the resumed call');
      assert.equal(chooserOpen(), false, 'the chooser stayed open after falling back');
      return outcome.message.slice(0, 80);
    });

    await check('two concurrent requests stay separate', async () => {
      await openTestPage(await freshTab(), url);
      await selectSigningOptions(site, {target: 'targetGeneratedData', format: 'CAdES'});
      await pressSign(site);
      const first = await waitForChooser(context);
      // The page keeps its "Cargando" overlay up while the first request is unanswered,
      // and that overlay eats clicks. Send the second request from the page itself so
      // both are genuinely in flight at the same time.
      await site.evaluate(() => {
        const field = (/** @type {string} */ suffix) => /** @type {HTMLElement|null} */ (document.querySelector(`[id$="${suffix}"]`));
        const pdf = /** @type {HTMLInputElement|null} */ (document.getElementById('targetPdfDocument'));
        if (pdf) {
          pdf.checked = true;
          pdf.dispatchEvent(new Event('click', {bubbles: true}));
        }
        const format = /** @type {HTMLSelectElement|null} */ (field('_fFormat'));
        if (format) format.value = 'CAdES';
        document.querySelector('.jquery-loading-modal')?.remove();
        document.querySelectorAll('.jquery-loading-modal__bg').forEach(node => node.remove());
        field('_botonFirmar')?.click();
      });
      const second = await waitForChooser(context, {known: [first]});
      assert.notEqual(first.url(), second.url(), 'the two requests shared a chooser');
      assert.notEqual(first, second, 'both requests returned the same chooser window');

      const payload = await samplePdf();
      await signWithChooser(second, {subject: identity.subject, pin: identity.pin});
      const outcome = await waitForOutcome(site, {expectSignature: true});
      const verification = verifyCades({
        directory: artifacts,
        signature: Buffer.from(outcome.signatureBase64, 'base64'),
        certificate: identity.certificate,
        detachedContent: payload,
      });
      assert.ok(verification.verified, 'the second signature did not verify against its own payload');
      await cancelChooser(first);
      return 'each chooser handled its own payload';
    });

    heading('PAdES and AUTO');
    for (const format of ['PAdES', 'AUTO']) {
      await check(`${format} signs the page's PDF locally`, async () => {
        await openTestPage(await freshTab(), url);
        const payload = await samplePdf();
        await selectSigningOptions(site, {target: 'targetPdfDocument', format});
        await pressSign(site);
        const chooser = await waitForChooser(context);
        await assertConfirmationGated(chooser);
        await signWithChooser(chooser, {subject: identity.subject, pin: identity.pin});
        const outcome = await waitForOutcome(site, {expectSignature: true});
        assert.ok(outcome.signatureBase64, `${format} produced no signature`);
        const signed = Buffer.from(outcome.signatureBase64, 'base64');
        assert.equal(signed.subarray(0, 5).toString(), '%PDF-', `${format} did not return a PDF`);
        const verification = verifyPades({directory: artifacts, pdf: signed, certificate: identity.certificate});
        assert.ok(verification.verified, `OpenSSL rejected the ${format} signature: ${verification.error}`);
        // The CMS digest covers the signed revision (original bytes plus the incremental update),
        // so only its PDF shape is comparable to the source here.
        assert.equal(verification.content?.subarray(0, 5).toString(), '%PDF-', 'OpenSSL recovered no PDF content');
        assert.ok(signed.length > payload.length, 'the signed PDF carries no incremental update');
        return `${signed.length} bytes signed as PAdES, verified by OpenSSL`;
      });
    }

    await check('AUTO signs a non-PDF payload as CAdES', async () => {
      await openTestPage(await freshTab(), url);
      const payload = generatedPayload();
      await selectSigningOptions(site, {target: 'targetGeneratedData', format: 'AUTO'});
      await pressSign(site);
      const chooser = await waitForChooser(context);
      await signWithChooser(chooser, {subject: identity.subject, pin: identity.pin});
      const outcome = await waitForOutcome(site, {expectSignature: true});
      assert.ok(outcome.signatureBase64, 'AUTO produced no signature');
      const verification = verifyCades({
        directory: artifacts,
        signature: Buffer.from(outcome.signatureBase64, 'base64'),
        certificate: identity.certificate,
        detachedContent: payload,
      });
      assert.ok(verification.verified, `OpenSSL rejected the AUTO signature: ${verification.error}`);
      return 'content sniffing selected CAdES';
    });

    heading('Formats the extension does not claim to handle');
    await check("XAdES explicit passes through to the site's own AutoFirma", async () => {
      await openTestPage(await freshTab(), url);
      await selectSigningOptions(site, {target: 'targetPdfDocument', format: 'XAdES'});
      await pressSign(site);
      await site.waitForTimeout(4_000);
      assert.equal(chooserOpen(), false, 'detached XAdES must not open the extension chooser');
      const outcome = await waitForOutcome(site, {expectSignature: false, timeout: 45_000});
      assert.ok(outcome.message, 'XAdES produced no outcome on the site');
      // With no desktop AutoFirma installed the site reports its own connection error,
      // which is exactly the proof that the original integration ran instead.
      return outcome.message.slice(0, 90);
    });

    heading('Site access');
    await check('a disabled site stops being intercepted', async () => {
      const origin = new URL(url).origin;
      await session.app.bringToFront();
      await setSiteEnabled(session.app, origin, false);
      try {
        await openTestPage(await freshTab(), url);
        await selectSigningOptions(site, {target: 'targetPdfDocument', format: 'CAdES'});
        await pressSign(site);
        await site.waitForTimeout(5_000);
        assert.equal(chooserOpen(), false, 'a disabled site must not open a chooser');
        return 'no chooser after disabling';
      } finally {
        await setSiteEnabled(session.app, origin, true);
      }
    });

    await check('re-enabling restores interception', async () => {
      await openTestPage(await freshTab(), url);
      await selectSigningOptions(site, {target: 'targetPdfDocument', format: 'CAdES'});
      await pressSign(site);
      const chooser = await waitForChooser(context);
      await cancelChooser(chooser);
      return 'chooser opened again';
    });
  } finally {
    await session.close();
  }

  heading('Summary');
  for (const result of results) log(`  ${result.passed ? 'PASS' : 'FAIL'}  ${result.name}`);
  log(`\n${results.length - failures}/${results.length} checks passed.`);
  log(`Signatures, keys and scratch files stay under ${scratch} (nothing was uploaded anywhere).`);
  if (failures) process.exitCode = 1;
}

main().catch(error => {
  process.stderr.write(`\nDemo aborted: ${error.stack ?? error.message}\n`);
  process.exitCode = 1;
});