# Chrome extension

The Chrome extension renders the AutoFirme Svelte webapp. It shares the app,
certificate store, signing and verification engines, batch queue and stamp controls
with the ordinary web build.

See [CHROME-EXTENSION-DESIGN.md](CHROME-EXTENSION-DESIGN.md) for the implementation
and request protocol, and [CHROME-EXTENSION-VERIFICATION.md](CHROME-EXTENSION-VERIFICATION.md)
for the recorded checks and remaining manual tests.

## 1. Build and install

Use Node 22+ (Node 24 verified), pnpm 11.24.0, and Chrome/Chromium 116+:

```sh
pnpm install --frozen-lockfile
pnpm check
pnpm build:extension
```

Disable other extensions that intercept AutoFirma calls before testing this one.

In `chrome://extensions`, enable Developer mode and choose Load unpacked, then
select `dist/extension`. Pin AutoFirme to the toolbar. Clicking it opens the
responsive full app in a separate resizable window that remains open when focus
moves to another window. The app also offers an Open in tab action.

The standalone web build remains `pnpm build`, producing `build/`. Extension code
is bundled locally; this is not a hosted iframe. The extension uses Chrome's
installation/update system, so PWA install/update buttons are web-only.

## 2. App features

Use the same signing and verification workflows, certificate import and saved
identities, PDF visible image/text stamps, implemented signature formats, batch
queue and ZIP downloads, saved outputs, themes and ES/EN language controls as the
webapp. Files and keys are processed locally. Optional timestamping contacts the
configured TSA when Chrome grants access to its origin.

Identities are sealed in extension-origin IndexedDB using the app's existing
PIN-protected store. Unlocking uses the certificate container's PIN. The hosted
webapp and extension are different origins: existing webapp storage does not
migrate automatically. Import certificates into the extension. The extension does
not provide operating-system certificate-store access or a native helper.

## 3. Signing requests from websites

Interception is enabled on all HTTP and HTTPS websites by default. Chrome requests
website access when the extension is installed or updated. Reload already-open
websites after installing or updating. You no longer need to add individual sites.

Site access lists disabled origins: enter a website to disable interception there,
or re-enable a listed website and reload it. Existing enabled-site lists from
0.1.0 do not restrict the new default. Chrome's own site-access settings can still
withhold host access; AutoFirme respects those restrictions. Restricted browser
pages such as chrome:// pages cannot be intercepted.

A supported AutoScript request opens a compact certificate chooser inspired by
Bitwarden's passkey-selection interaction. It uses AutoFirme's real certificate
picker and saved identities; there is no Bitwarden integration. Review the
requesting origin, operation and digest, choose or import/unlock a certificate,
and explicitly confirm. Choosing a certificate alone never signs.

The request title shares the header with AutoFirme. Request and certificate details
start collapsed; expand them to review the format, full digest, subject, issuer and
validity. The copy button beside the website copies the full SHA-256. The selected
certificate leaves the saved-choices list. The chooser adjusts its height when
content changes and scrolls when it reaches the available screen height.

Continue with AutoFirma resumes the website's original desktop integration.
Closing the chooser before signing has the same effect, once. Cancel reports
cancellation. Once signing starts, closing or losing the chooser cannot trigger
an automatic second signature through AutoFirma. An ambiguous failure requires
checking the website result before starting another request.

Initially intercepted calls are:

```js
AutoScript.selectCertificate(null, success, error);
AutoScript.sign(base64, 'SHA256withRSA', 'CAdES', null, success, error);
AutoScript.sign(base64, 'SHA256withRSA', 'CAdES', 'mode=implicit', success, error);
AutoScript.sign(base64, 'SHA256withRSA', 'CAdES', 'mode=explicit', success, error);
```

Input must be canonical Base64, at most 4 MiB. Certificate selection does not
honor unsupported filters. Supported signing uses currently valid RSA certificates
with at least 2048-bit keys and appropriate document-signing usage. Unsupported
methods, formats, algorithms, parameters, sticky identities and custom stores
continue through the original implementation unchanged. This website contract is
narrower than the full app's interactive feature set.

## 4. Security boundaries and limits

HTTP/HTTPS websites with Chrome host access get MAIN/isolated-world bridge scripts,
except origins disabled in AutoFirme. Chrome supplies the requesting tab, frame
and document identity. The bridge does not accept an origin
supplied by the website. An opaque ID is bound to the actual chooser
tab. Other app tabs cannot confirm or retrieve that chooser's payload. Private
keys and PINs stay in extension-owned contexts; callbacks receive only the public
certificate and signature. Navigation, stale documents, timeouts and disconnects
cannot authorize replay. Worker restarts fail pending requests safely.

Certificate-chain packaging does not establish trust for an administration. The
extension does not add online revocation checks or OS biometrics. It inherits the
webapp's format and verification limitations. It has not had an external security
audit; real administration sites and physical Windows/macOS Chrome still require
compatibility testing. Browser external-protocol rules may affect actual AutoFirma
launch during fallback.

The upstream revision is `d6dfa434d3be2aa2ed53b3956f7d10c3ab8c1035`. No declared
license was found in that revision; clarify distribution rights before publishing.
No original installers or decompiled Java trees are included in this extension.

## 5. Verification commands

```sh
pnpm test --maxWorkers=2
pnpm check
pnpm build
pnpm test:extension:unit
pnpm build:extension
pnpm check:extension
pnpm test:extension:artifacts
pnpm test:extension:browser
pnpm package:extension
bash test/fixtures/verify-fixtures.sh
```

The extension browser workflow uses a disposable unpacked Chromium profile, local
non-binding website fixture and freshly generated OpenSSL test certificate. It requires OpenSSL,
`unzip` (batch-ZIP check) and Chrome/Chromium. Set `CHROME_PATH` to its executable, or install
Chromium using Playwright's browser installer (the script can discover its Linux
cache: `npx playwright install --with-deps chromium` leaves the binary in
`~/.cache/ms-playwright/chromium-<build>/chrome-linux64/chrome`). Branded Google Chrome builds 137
and newer ignore `--load-extension`; use Chromium or Chrome for Testing, and the script rejects a
branded `CHROME_PATH` before launching. `pnpm package:extension` additionally requires `python3`.
The browser test loads the production all-sites manifest and checks the
first request with fresh storage, without enabling the fixture origin. Chrome's
native grant prompts and actual desktop AutoFirma launch need separate manual
checks.

See the [verification record](CHROME-EXTENSION-VERIFICATION.md) for executed checks
and remaining manual/environment-specific checks. `pnpm package:extension` packages
the existing verified build; rebuild first after making source changes.
