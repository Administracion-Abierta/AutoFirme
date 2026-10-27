// Drives the extension's consent UI the way a person would: pick the identity, type its
// PIN, and only then press the confirm button. Every step waits for the UI to say it is
// ready, so the demo fails loudly instead of signing with a half-open dialog.
import assert from 'node:assert/strict';

/** Wait until the chooser offers the saved identity, or open the picker if it is collapsed. */
export async function selectIdentity(chooser, subject) {
  const alreadyVisible = await chooser.locator(`button:has-text("${subject}")`).count();
  if (!alreadyVisible) {
    const add = chooser.locator('button:has-text("Agregar firma")').first();
    if (await add.count()) await add.click();
    await chooser.locator(`button:has-text("${subject}")`).first().waitFor({timeout: 10_000});
  }
  await chooser.locator(`button:has-text("${subject}")`).first().click();
}

/** Unlock the chosen identity with its container PIN. */
export async function unlockIdentity(chooser, pin) {
  const pinField = chooser.locator('input[type="password"]').first();
  await pinField.waitFor({timeout: 10_000});
  await pinField.fill(pin);
  const open = chooser.locator('button:has-text("Abrir")').first();
  await open.waitFor({timeout: 10_000});
  await open.click();
  await chooser.locator('#request-confirm:not([disabled])').waitFor({timeout: 10_000});
}

/**
 * Complete a chooser: select, unlock and confirm. Returns the digest shown to the user.
 * @param {import('playwright').Page} chooser
 * @param {{subject: string, pin: string}} identity
 */
export async function signWithChooser(chooser, {subject, pin}) {
  await selectIdentity(chooser, subject);
  await unlockIdentity(chooser, pin);
  // The digest lives inside a collapsed disclosure; open it so the demo shows the same
  // value a citizen would check before signing.
  await chooser.locator('#request-details > summary').click({timeout: 5_000}).catch(() => {});
  const digest = (await chooser.locator('#request-digest').textContent().catch(() => null))?.trim() ?? null;
  await chooser.click('#request-confirm');
  return {digest};
}

/** Ask for the certificate only, without signing anything. */
export async function shareCertificateWithChooser(chooser, {subject, pin}) {
  await selectIdentity(chooser, subject);
  await unlockIdentity(chooser, pin);
  const share = chooser.locator('button:has-text("Compartir certificado")').first();
  await share.waitFor({timeout: 10_000});
  await share.click();
}

/** Cancel a pending request; the website must see a cancellation, not a signature. */
export async function cancelChooser(chooser) {
  if (chooser.isClosed()) return;
  const cancel = chooser.locator('#request-cancel');
  await cancel.waitFor({timeout: 10_000});
  await cancel.click();
}

/** Hand the request back to the site's own AutoFirma integration. */
export async function fallbackChooser(chooser) {
  if (chooser.isClosed()) throw Error('the chooser window closed before it could fall back');
  const fallback = chooser.locator('#request-fallback');
  await fallback.waitFor({timeout: 10_000});
  await fallback.click();
}

/** Assert the confirm button only becomes enabled once an identity is actually usable. */
export async function assertConfirmationGated(chooser) {
  const confirm = chooser.locator('#request-confirm');
  await confirm.waitFor({timeout: 10_000});
  assert.ok(await confirm.isDisabled(), 'confirm must stay disabled until a certificate is unlocked');
}