# Chrome extension design

Status: implemented in extension 0.1.2. This document describes the current code.
Installation and supported calls are in [CHROME-EXTENSION.md](CHROME-EXTENSION.md).
Recorded checks are in [CHROME-EXTENSION-VERIFICATION.md](CHROME-EXTENSION-VERIFICATION.md).
En español: las funciones de la extensión están en [LEEME.md](LEEME.md).

## 1. Shared app, separate build target

The SvelteKit build and the extension render the same `src/routes/+page.svelte` and
`App.svelte`. Both use the existing certificate store, signing and verification
engines, batch queue, PDF stamp controls, saved outputs, settings and translations.
The extension bundles the app locally instead of loading a hosted page or copying
its engines into a second application.

`pnpm build` produces the SvelteKit output that serves as the extension's options
page and the local dev harness. `pnpm build:extension` uses a standalone Vite entry
to produce `dist/extension`. That entry avoids SvelteKit's inline bootstrap and the
app service worker. Chrome installs and updates the extension.

The Manifest V3 policy is `script-src 'self'; object-src 'none'`. Scripts, fonts
and styles ship in the package. The build checker rejects inline executable
scripts, dynamic code execution, remote bootstrap assets, missing app modules
and app service-worker registration.

| Component | Responsibility |
|---|---|
| `extension/manifest.json` | Chrome permissions, worker and app entry |
| `scripts/build-extension.mjs` | Full app and bridge bundles |
| `extension/page.js`, `extension/core/adapter.js` | Wrap supported AutoScript calls in the page's MAIN world |
| `extension/content.js` | Forward requests and results through an isolated-world port |
| `extension/background.js` | Chrome API integration, site access and chooser windows |
| `extension/core/contract.js` | Validate supported requests and public results |
| `extension/core/coordinator.js`, `extension/core/lifecycle.js` | Request identity, ownership and state |
| `src/lib/extension/ExtensionShell.svelte` | Full-app window controls |
| `src/lib/extension/RequestChooser.svelte` | Request review and confirmation |
| `src/lib/extension/SiteAccess.svelte` | Disabled-site list and TSA permission controls |
| `src/lib/extension/sign-request.ts` | Certificate eligibility and local CAdES/PAdES/XAdES signing |

## 2. App windows and certificate storage

The toolbar opens a persistent, resizable 520 by 760 window. A request opens its
own chooser, initially 480 by 520. The chooser resizes to its content, capped by
the available screen height and a 1200px limit. Longer content scrolls. The full
app can also open in a tab. These windows remain
open when focus moves elsewhere during signing or a batch operation.

The chooser renders the existing `CertPicker` inline. Saved choices remain beside
the requesting website. The compact header contains the request title and app
name. The site section keeps
the origin visible and offers a SHA-256 copy button with a tooltip. Request
parameters and the full digest sit in a collapsed disclosure. Selected certificate
details also start collapsed. The selected identity is excluded from saved choices,
and the add button sits beside their heading. Signing actions share a 40px height.
The normal full-app certificate dialog shares the same import, PIN and selection
logic. A resize observer tracks natural content height; DOM
mutations also handle disclosure and PIN/import changes when painting is paused.
Resizing uses the original browser-frame height to avoid viewport feedback loops.

The extension uses the app's PIN-protected identity store in extension-origin
IndexedDB. Storage from the retired hosted web app belongs to a different origin and
does not migrate automatically. Private keys and PINs stay in extension-owned contexts. This build
uses imported PKCS#12 identities and does not access the operating-system store.

## 3. Website access

The manifest declares `http://*/*` and `https://*/*` as install-time
`host_permissions` (Chrome 116+): a fresh install intercepts every HTTP(S) site with
no prompt. The site-access panel is the exception list, not an allow list: users
disable an exact origin there, and the panel can re-request a per-origin grant
(`chrome.permissions.request` on user activation) when Chrome's own site-access
settings withheld it.

The worker stores exceptions in `disabledOrigins`. It registers MAIN and ISOLATED
scripts at `document_start` in all matching frames, excluding disabled origins.
Version 0.1.0's `enabledOrigins` list no longer gates interception.

Unregistering scripts affects future documents. For wrappers already loaded in a
page, the worker checks site access again before capturing a call. A disabled or
ungranted site continues through its original AutoFirma integration. Removing
access terminates pending requests and rejects later completion attempts. A
re-enabled website needs a reload if its bridge was excluded at document load.

Timestamping remains an app setting. The timestamp engine checks Chrome's host
permission before contacting the configured TSA. The site-access panel can request
that permission when Chrome has withheld it. It does not provide a generic fetch
proxy.

## 4. Supported requests and app messages

The bridge accepts certificate selection and RSA/SHA-256 signing as CAdES
(implicit or explicit), PAdES and enveloped XAdES, plus `AUTO` content sniffing, up
to 4 MiB of canonical Base64. Legacy `SHA1withRSA` calls are accepted only for
enveloped XAdES (CORES' compatibility test, for one); the SHA-1 key view is still
non-extractable and every other format remains SHA-256-only. It also accepts
`CAdEStri` from Aragon's portafirmas: the page supplies `serverUrl`, so the
extension performs the pre-sign and post-sign calls while the private key stays
local; RSA/SHA-256 and RSA/SHA-512 are supported.
Explicit XAdES stays outside the contract because the engine only emits enveloped
signatures. Unsupported methods, filters, sticky identities, other `*Tri` formats
and custom stores use the original AutoFirma implementation. PAdES requests may
carry AutoFirma's visible-stamp bag (`signaturePage`, `signaturePositionOnPage*`,
`signatureRubricImage`, `layer2*`, reason/city/contact); it is mapped to the local
appearance, while a non-zero `signatureRotation` falls back to native. The full app
retains its broader interactive format support.

Page ports use the name `autofirme-page`. A request carries `{id, request}`; a
reply carries `{id, response}`. The background uses Chrome's sender metadata to
bind the request to its tab, frame, document and origin. It allows at most eight
pending requests per document and at most sixteen in flight across the whole
extension; a request beyond that global cap is answered `fallback` immediately and
never opens a chooser window. Each accepted request gets a five-minute deadline.

Extension UI messages use `chrome.runtime.sendMessage`. Replies are
`{ok: true, value}` or `{ok: false, error}`.

| Message | Result or effect |
|---|---|
| `request:get`, `id` | Request origin, operation, data, format, mode and digest |
| `request:resize`, `id`, `height` | Resize the bound chooser only, within 320 to 1200px, without changing signing state |
| `request:begin`, `id` | Transition from waiting to signing after confirmation |
| `request:complete`, `id`, `result` | Return the public certificate and optional signature after signing begins |
| `request:fail`, `id` | End with an error |
| `request:cancel`, `id` | Cancel a waiting request |
| `request:fallback`, `id` | Resume the original call before signing begins |
| `app:open`, optional `tab` | Open the full app without resolving the request |
| `sites:list` | Return `disabledOrigins`, `suggestedOrigin`, `grantedOrigins` and `allSites` |
| `sites:set`, `origin`, `enabled` | Remove or add a disabled-origin exception |

The chooser URL is `index.html?request=<UUID>`. The worker records its actual tab
ID and accepts request actions only from that tab at the exact request URL. Other
app tabs cannot retrieve or complete the request. Main-app management messages
allow the app's own SPA query changes while still checking the extension origin,
index page and tab URL.

## 5. Confirmation and fallback

Selecting or unlocking a certificate never signs. The confirmation button checks
certificate eligibility and calls `request:begin` before the local signing engine.
The certificate must be valid, RSA with at least 2048 bits, eligible for document
signing and not a CA certificate. Results contain only the public certificate and
signature.

| Event | Outcome |
|---|---|
| Confirm | Begin local signing, then return the result through the original callback |
| Cancel while waiting | Invoke the original cancellation callback |
| Continue with AutoFirma while waiting | Invoke the original call once |
| Close the chooser while waiting | Invoke the original call once |
| Close or lose the chooser after signing begins | End without replaying the original call |
| Navigate away, expire or lose the worker connection | Reject or drop stale results without replay |

The coordinator keeps concurrent requests separate. It checks the current frame
and document before returning data or accepting an action. Cached documents cannot
authorize signing. Restoring a page from the back-forward cache reconnects it for
new requests without replaying old ones.

## 6. Verification and limits

The unit suites exercise request validation, sender identity, chooser ownership,
consent, fallback, revocation and document lifecycle. Artifact tests inspect the
built module graph and CSP constraints. The Chromium workflow loads an unpacked
production build in a disposable profile and checks the actual app and chooser.
OpenSSL independently verifies generated CAdES and PDF signatures.

See [CHROME-EXTENSION-VERIFICATION.md](CHROME-EXTENSION-VERIFICATION.md) for the
recorded results and checks that still need a physical browser or desktop app.
The extension inherits the app's format and trust-verification limits. It does
not add online revocation checks or OS biometrics.
