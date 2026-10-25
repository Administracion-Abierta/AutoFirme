// Page-specific knowledge for <https://www.registradores.org/test-autofirma>.
//
// The page is a Liferay portlet whose instance id is regenerated per deploy, so every
// selector is matched by suffix instead of by that id. The page builds its payload in the
// click handler, which means the demo has to reproduce the same bytes to verify the
// detached signature afterwards.
import assert from 'node:assert/strict';
import {createHash} from 'node:crypto';

export const REGISTRADORES_URL = 'https://www.registradores.org/test-autofirma';

/**
 * Suffix of each form control's id. The portlet prepends a namespace that embeds a
 * per-deploy random segment (`…_INSTANCE_jmth3XZkBd0N_`), so matching the suffix is the
 * only stable way to reach a control.
 */
const SUFFIX = {
  format: '_fFormat',
  sign: '_botonFirmar',
  nif: '_fNif',
  nombre: '_fNombre',
  signature: '_firmaB64TextArea',
  certificate: '_certData',
  download: '_botonGuardar',
};

/** The `Archivo` target never reaches AutoScript: the page leaves its payload null. */
export const TARGETS = [
  {id: 'targetGeneratedData', name: 'Datos generados'},
  {id: 'targetPdfDocument', name: 'Documento PDF'},
  {id: 'targetFormulario', name: 'Formulario'},
];

export const FORMATS = ['CAdES', 'XAdES', 'PAdES', 'AUTO'];

/** The page signs with detached CAdES and this exact RSA/SHA-256 profile. */
export const MODE = 'mode=explicit';

/** Payload the page generates for `Datos generados` (13 doublings, minus two characters). */
export function generatedPayload() {
  let data = '1234567890';
  for (let i = 0; i < 13; i++) data += data;
  return Buffer.from(data.slice(0, data.length - 2), 'utf8');
}

/** Payload the page generates for `Formulario`: the two fields it serializes. */
export function formPayload({nif, nombre}) {
  return Buffer.from(`fNif=${encodeURIComponent(nif)}&fNombre=${encodeURIComponent(nombre)}`, 'utf8');
}

/** SHA-256 the chooser should display for a payload, as hex. */
export function sha256Hex(payload) {
  return createHash('sha256').update(payload).digest('hex');
}

/** Open the test page, accept its cookie banner and wait until the form is usable. */
export async function openTestPage(page, url = REGISTRADORES_URL) {
  await page.goto(url, {waitUntil: 'domcontentloaded', timeout: 60_000});
  await page.waitForFunction(
    () => Boolean(document.querySelector('[id$="_botonFirmar"]') && document.querySelector('[id$="_fFormat"]')),
    undefined,
    {timeout: 30_000},
  );
  // The Liferay cookie modal overlays the whole form, so it has to go before anything on
  // the page is clickable. Accept through the same control the page renders.
  const accept = page.getByRole('button', {name: /^(Aceptar|Accept)$/}).first();
  await accept.waitFor({state: 'visible', timeout: 10_000}).catch(() => {});
  if (await accept.isVisible().catch(() => false)) {
    await accept.click({timeout: 10_000}).catch(() => {});
    await accept.waitFor({state: 'hidden', timeout: 10_000}).catch(() => {});
  }
  await page.waitForFunction(
    () => {
      const format = document.querySelector('[id$="_fFormat"]');
      const button = document.querySelector('[id$="_botonFirmar"]');
      if (!format || !button) return false;
      const atPoint = document.elementFromPoint(
        format.getBoundingClientRect().left + 4,
        format.getBoundingClientRect().top + 4,
      );
      return Boolean(atPoint && format.contains(atPoint));
    },
    undefined,
    {timeout: 15_000},
  );
  // Liferay binds the portlet's click handler about a second after load. A click before
  // that reaches the DOM but no listener, and nothing on the page reports it, so wait for
  // AutoScript and the handler to be in place rather than probing by clicking: a probe
  // call would start the page's "Cargando" overlay, which never clears without desktop
  // AutoFirma and would block every later click.
  await page.waitForFunction(
    () => {
      const script = /** @type {{AutoScript?: any}} */ (window).AutoScript;
      return typeof script?.sign === 'function' && script?.VERSION !== undefined;
    },
    undefined,
    {timeout: 30_000},
  );
  await page.waitForTimeout(1_500);
}

/**
 * Fill the NIF and name fields the `Formulario` target signs.
 * The page only reveals them once that target is selected, so select it first.
 */
export async function fillForm(page, {nif = '12345678Z', nombre = 'Prueba AutoFirme Demo'} = {}) {
  const radio = page.locator('#targetFormulario');
  if (!(await radio.isChecked())) await radio.click();
  await page.fill(`[id$="${SUFFIX.nif}"]`, nif);
  await page.fill(`[id$="${SUFFIX.nombre}"]`, nombre);
  return {nif, nombre};
}

/** Read the current form values, so the payload matches what the page will serialise. */
export async function readForm(page) {
  return page.evaluate(suffix => {
    const input = (/** @type {string} */ name) => /** @type {HTMLInputElement|null} */ (document.querySelector(`[id$="${name}"]`));
    return {nif: input(suffix.nif)?.value ?? '', nombre: input(suffix.nombre)?.value ?? ''};
  }, SUFFIX);
}

/** Select a target radio and a format, the same way the page's own handler reads them. */
export async function selectSigningOptions(page, {target, format}) {
  // The radios carry an inline onclick that sets the page's target flags, and Playwright's
  // check() would click a second time to verify, unselecting the radio again. A single
  // click plus an assertion keeps the page's own state intact.
  const radio = page.locator(`#${target}`);
  if (!(await radio.isChecked())) await radio.click();
  assert.ok(await radio.isChecked(), `page refused target ${target}`);
  await page.selectOption(`[id$="${SUFFIX.format}"]`, format);
  const selected = await page.inputValue(`[id$="${SUFFIX.format}"]`);
  assert.equal(selected, format, `page did not accept format ${format}`);
}

/** Press the page's "Firmar" button. */
export async function pressSign(page) {
  // The page's own wait overlay swallows clicks, and it only clears when the previous
  // call answered. Hide a leftover one from an earlier case rather than hanging on it.
  await page.evaluate(() => {
    const overlay = document.querySelector('.jquery-loading-modal');
    if (overlay) overlay.remove();
    document.querySelectorAll('.jquery-loading-modal__bg').forEach(node => node.remove());
  });
  await page.click(`[id$="${SUFFIX.sign}"]`);
}

/** Read the signature and certificate the page received from AutoScript. */
export async function readResult(page) {
  return page.evaluate(suffix => {
    const field = (/** @type {string} */ name) => /** @type {HTMLInputElement|null} */ (document.querySelector(`[id$="${suffix[name]}"]`));
    return {
      signatureBase64: field('signature')?.value ?? '',
      certificateText: field('certificate')?.value ?? '',
      downloadEnabled: !(field('download')?.disabled ?? true),
    };
  }, SUFFIX);
}

/** Text of the newest visible modal, which is how the page reports success or failure. */
export async function readLatestMessage(page) {
  return page.evaluate(() => {
    const nodes = /** @type {HTMLElement[]} */ ([...document.querySelectorAll('.modal-content, [role="dialog"]')]);
    const visible = nodes
      .filter(node => node.offsetParent !== null)
      .map(node => node.innerText.replace(/\s+/g, ' ').trim());
    return visible.length ? visible[visible.length - 1] : null;
  });
}

/**
 * Wait until the page has stored a signature or has reported a failure.
 * The site's success handler fills the field synchronously, but a failure arrives through
 * a modal that the extension's own callback triggers, so both are awaited here.
 */
export async function waitForOutcome(page, {expectSignature = true, timeout = 30_000} = {}) {
  const deadline = Date.now() + timeout;
  while (Date.now() < deadline) {
    const result = await readResult(page);
    if (expectSignature && result.signatureBase64) return {...result, message: null};
    const message = await readLatestMessage(page);
    if (message && (!expectSignature || /error|no se ha podido|cancel/i.test(message))) return {...result, message};
    await page.waitForTimeout(250);
  }
  const result = await readResult(page);
  return {...result, message: await readLatestMessage(page)};
}

/** Close any modal the page opened, so the next case starts from a clean page. */
export async function dismissMessages(page) {
  await page.evaluate(() => {
    const buttons = /** @type {HTMLElement[]} */ ([...document.querySelectorAll('.modal-header button, .modal-footer button, [aria-label="Close"], .close')]);
    for (const button of buttons) {
      button.click();
    }
  }).catch(() => {});
  await page.waitForTimeout(500);
}

/** Wait until the page shows a modal, or give up and report nothing appeared. */
export async function waitForMessage(page, timeout = 45_000) {
  try {
    await page.waitForFunction(
      () => [...document.querySelectorAll('.modal-content, [role="dialog"]')].some(node => /** @type {HTMLElement} */ (node).offsetParent !== null),
      undefined,
      {timeout},
    );
  } catch {
    return null;
  }
  return readLatestMessage(page);
}