# Chrome extension

The Chrome extension renders the AutoFirme Svelte app. It shares the app,
certificate store, signing and verification engines, batch queue and stamp controls
with the desktop product's WebView (and with the local dev harness).

See [CHROME-EXTENSION-DESIGN.md](CHROME-EXTENSION-DESIGN.md) for the implementation
and request protocol, and [CHROME-EXTENSION-VERIFICATION.md](CHROME-EXTENSION-VERIFICATION.md)
for the recorded checks and remaining manual tests. En español: las funciones de la
extensión están en [LEEME.md](LEEME.md) (sección «Extensión Chrome»).

Store submission copy, permission justifications, listing assets and the privacy
statement live in [STORE-LISTING.md](STORE-LISTING.md) and [PRIVACY.md](PRIVACY.md).

## 1. Build and install

Use Node 24 (`.nvmrc`, `engines.node`), pnpm 11.24.0, and Chrome/Chromium 116+. The
extension is **Chrome-only for now**; Firefox support is deferred to
[issue #117](https://git.eduard3v.com/EduarD3V/AutoFirme/issues/117)
(`userScripts` API study, tagged `fleet:hold`).

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

The SvelteKit build (`pnpm build`) still produces `build/`, now the extension's options
page and the local dev harness — no website ships. Extension code is bundled locally;
this is not a hosted iframe. The extension uses Chrome's installation/update system.

## 2. App features

Use the same signing and verification workflows, certificate import and saved
identities, PDF visible image/text stamps, implemented signature formats, batch
queue and ZIP downloads, saved outputs, themes and ES/EN language controls as the
desktop app. Files and keys are processed locally. Optional timestamping contacts the
configured TSA when Chrome grants access to its origin.

Identities are sealed in extension-origin IndexedDB using the app's existing
PIN-protected store. Unlocking uses the certificate container's PIN. The retired
hosted web app and the extension are different origins: storage saved under the old
web app does not migrate automatically. Import certificates into the extension. The extension does
not provide operating-system certificate-store access or a native helper.

### MCP bridge

Settings → Servidor MCP (extension builds only) can connect the extension to a deployed
`autofirma-mcp` HTTP entry. The operator enters the server's `/mcp` address and its
`AUTOFIRME_MCP_TOKEN`, enables the bridge, and the extension signs only with the identity the
agent requested. If that identity has a PIN remembered from the picker's per-identity checkbox,
the service worker signs without a window; otherwise Chrome opens one consent window for the
signature. The private key, PIN and vault never leave Chrome.
The socket is outbound to `WS(S) /bridge` (same token), so no inbound port is opened on
the browser side and stdio MCP servers cannot use it. See `docs/MCP-DESIGN.md`.

## 3. Signing requests from websites

The extension intercepts every HTTP and HTTPS website from installation: the manifest
declares `http://*/*` and `https://*/*` as install-time host permissions, so a site's
`AutoScript` calls open the chooser on the first request with no prior setup. Older
integrations that publish the same API as `MiniApplet` are intercepted too; when a page
assigns the two names the same object it is still wrapped only once.

For a `CAdEStri` call (Aragon's portafirmas), the page's signing server performs the
pre-sign and post-sign phases: the extension sends the selected certificate and the
request data, signs the returned attributes locally with the RSA key, and returns
the PKCS#1 signature to that server. The private key never leaves the browser. The
chooser marks the request as server-backed.

In Site access, enter an origin and select Disable site to exclude it: reload the site
and its calls run the original AutoFirma integration again. Chrome's own site-access
settings can still withhold host access; if Chrome revoked the wildcard grant (or a
specific origin), the panel can request a per-origin grant again. Restricted browser
pages such as chrome:// pages cannot be intercepted. Timestamping needs its own grant
for the configured TSA origin.

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
AutoScript.sign(base64, 'SHA256withRSA', 'PAdES', null, success, error);
AutoScript.sign(base64, 'SHA256withRSA', 'AUTO', null, success, error);
AutoScript.sign(base64, 'SHA1withRSA', 'XAdES', null, success, error);
// The same signatures under the legacy `MiniApplet` alias:
MiniApplet.sign(base64, 'SHA256withRSA', 'CAdES', null, success, error);
MiniApplet.selectCertificate(null, success, error);
AutoScript.sign(base64, 'SHA256withRSA', 'OOXML', null, success, error);
```

Input must be canonical Base64, at most 4 MiB. Certificate selection does not
honor unsupported filters. Supported signing uses currently valid RSA certificates
with at least 2048-bit keys and appropriate document-signing usage. `AUTO` sniffs
the payload: a `%PDF-` header signs as PAdES, XML signs as enveloped XAdES when
the mode is implicit, and everything else signs as CAdES. OOXML packages go through
the explicit `OOXML` format, not `AUTO`.
Legacy portals that still request `SHA1withRSA` are honored only for enveloped
XAdES; CAdES and PAdES stay SHA-256-only.
Explicit XAdES needs a detached signature and continues through the original
implementation. Unsupported methods, formats, algorithms, parameters, sticky
identities and custom stores also continue through it unchanged. This website
contract is narrower than the full app's interactive feature set.

## 4. Security boundaries and limits

Every HTTP/HTTPS website gets MAIN/isolated-world bridge scripts unless its origin is
disabled in AutoFirme, and Chrome still withholds host access where its own settings
say so. Chrome supplies the requesting tab, frame
and document identity. The bridge does not accept an origin
supplied by the website. An opaque ID is bound to the actual chooser
tab. Other app tabs cannot confirm or retrieve that chooser's payload. Private
keys and PINs stay in extension-owned contexts; callbacks receive only the public
certificate and signature. Navigation, stale documents, timeouts and disconnects
cannot authorize replay. Worker restarts fail pending requests safely.

Certificate-chain packaging does not establish trust for an administration. The
extension does not add online revocation checks or OS biometrics. It inherits the
app's format and verification limitations. It has not had an external security
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
pnpm demo:registradores          # live website run, see demo/README.md
```

`pnpm check` runs two TypeScript projects: the app (`tsconfig.json` over
`src`, `test`, root configs) and `tsconfig.extension.json` over `extension/`
and `scripts/`. The extension project keeps `checkJs: true` and `strict` but
sets `noImplicitAny: false` and ships no `@types/chrome`: the MV3 code and
driver scripts are plain JS whose tests hand-write stubs, and typing every
`chrome.*` stub would add noise without catching API misuse. JSDoc casts went
only where real type errors hid (optional regex matches, `Array.at(-1)`,
PDFObject lookups). A new type error in either directory fails `pnpm check`.
To tighten later: flip `noImplicitAny` to `true` (optionally with
`@types/chrome`) once the JSDoc coverage justifies it — the project file is
the single knob.

The extension browser workflow uses a disposable unpacked Chromium profile, local
non-binding website fixture and freshly generated OpenSSL test certificate. It requires OpenSSL,
`unzip` (batch-ZIP check) and Chrome/Chromium. Set `CHROME_PATH` to its executable, or install
Chromium using Playwright's browser installer (the script can discover its Linux
cache: `npx playwright install --with-deps chromium` leaves the binary in
`~/.cache/ms-playwright/chromium-<build>/chrome-linux64/chrome`). Branded Google Chrome builds 137
and newer ignore `--load-extension`; use Chromium or Chrome for Testing, and the script rejects a
branded `CHROME_PATH` before launching. `pnpm package:extension` additionally requires `python3`.
The browser test loads the production manifest with its install-time wildcard
host permissions: it checks that a fresh profile captures the fixture origin's first
call, disables that origin through the Site access panel, verifies the disabled site
falls back to native AutoFirma across a browser restart and signs once the site is
re-enabled. Chrome's actual installation prompts and desktop AutoFirma launch need
separate manual checks.

See the [verification record](CHROME-EXTENSION-VERIFICATION.md) for executed checks
and remaining manual/environment-specific checks, including the full target and
format matrix run against the Colegio de Registradores' own test page
(<https://www.registradores.org/test-autofirma>) in §4.1.
`pnpm package:extension` packages the existing verified build; rebuild first after
making source changes.
