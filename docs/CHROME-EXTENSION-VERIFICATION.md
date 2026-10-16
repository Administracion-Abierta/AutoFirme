# Chrome extension verification

Recorded for extension 0.1.2 on 2026-10-15. These are the results of the implementation
checks, not a claim that every platform or administration workflow is supported.
Commands and installation instructions are in [CHROME-EXTENSION.md](CHROME-EXTENSION.md).
The implementation is described in [CHROME-EXTENSION-DESIGN.md](CHROME-EXTENSION-DESIGN.md).

## 1. Environment

Fedora Linux x86-64, Node 24.19.0, pnpm 11.24.0, headless Chromium (Chrome for Testing flavour:
branded Google Chrome 137+ ignores `--load-extension`), OpenSSL, `unzip` and
`python3` (`pnpm package:extension`). The extension requires Chrome or Chromium 116+. Browser tests use a
disposable profile, an OpenSSL-generated PKCS#12 and local test documents.

## 2. Automated checks

| Command | Recorded result |
|---|---|
| `pnpm test --maxWorkers=2` | 226 passed, 2 skipped |
| `pnpm check` | 0 errors, 0 warnings |
| `pnpm build` | Passed, with the existing bundle-size warning |
| `pnpm build:extension` | Passed, with the existing bundle-size warning |
| `pnpm test:extension:unit` | 27 passed |
| `pnpm test:extension:artifacts` | 15 passed |
| `pnpm test:extension:browser` | Passed |

The unmodified upstream baseline had 197 passing tests and two skipped. The suite
includes the existing CMS, XML, PDF, stamp, ZIP and PKCS#12 checks. The extension
adds chooser, certificate eligibility, TSA permission and site-access coverage.
The compact chooser tests also cover collapsed details, SHA-256 clipboard success
and failure, exclusion of the selected identity, and resizing when painting is
paused. A separate bridge test checks that only the bound chooser can resize its
window and that resizing never authorizes a signature.

The bridge tests cover default capture without a site entry, independence from
the old enabled-site list, persisted disabled origins, host revocation, sender
identity, exact chooser ownership, callbacks, concurrent requests, expiry, stale
and cached documents, worker/port loss, repeated back-forward cache restoration,
SPA URL changes and fallback from an already-loaded disabled site.

The artifact tests reject missing entries, orphan app bundles, remote or missing
assets, inline executable scripts, dynamic code execution, PWA registration and
reduced feature bundles. They follow reachable module imports.

`bash test/fixtures/verify-fixtures.sh` also passed the positive and tampered
CAdES, XML and canonicalization checks during integration. Application runs with
`--maxWorkers=2` keep memory use bounded. Version 0.1.1 also passed a plain
`pnpm test` run. The final tooltip cleanup passed all 12 focused chooser tests,
type checking, both builds and the full Chromium workflow.

## 3. Unpacked Chromium extension

`scripts/test-extension-browser.mjs` launches a disposable Chromium profile with
the flags needed to load an unpacked extension. It uses the production all-sites
manifest without modification. Fresh storage contains no enabled-site entry, and
the test does not call `sites:set` before the first website request.

Observed results:

- The actual `App.svelte` mounts under the extension origin. `CertPicker` imports
  the generated PKCS#12 and seals it in the existing identity store.
- The full app fits 320px, 420px and 1280px viewport widths. The chooser fits
  320px and 420px in ES and EN, including expanded certificate details. The header
  stays below 70px, the site section below 55px, the three signing actions are
  40px high, and the add button remains to the right of the saved-choice heading.
- A real pointer hover shows the SHA-256 tooltip, and a real click writes to the
  clipboard without an added clipboard permission. The selected certificate is
  absent from saved choices. A closed tooltip is removed immediately so its old
  position cannot cause horizontal overflow after a viewport change.
- The native chooser window fits its content, grows when request details expand
  and shrinks when they close. Height is capped by the available screen height;
  overflow scrolls. DOM observation also handles updates when painting is paused.
- The PDF stamp controls sign a two-document batch. The app saves both outputs,
  downloads a ZIP that passes `unzip -t`, and reports passing PDF structure and
  signature checks. OpenSSL verifies both signatures against the test certificate.
- Opening the chooser, selecting a certificate and unlocking it do not sign.
  Confirmation returns attached and detached CAdES signatures that OpenSSL verifies
  with `-cades`.
- Certificate selection, explicit fallback, dismissal fallback exactly once,
  cancellation and unsupported PDF pass-through work.
- Two distinct concurrent payloads remain separate. Cancelling one leaves the
  other usable, and its signature verifies against the intended payload. A second
  app tab cannot retrieve the chooser's request.
- Navigation removes stale choosers without replay or late results. Disabling a
  site restores its original call on the loaded document without opening a chooser.
- Main-app management works after SPA query changes. Chooser actions still require
  the exact request URL and actual chooser tab.

The test certificate is self-issued, not an FNMT trust anchor. Successful signature
verification does not imply that all certificate-trust checks pass.

## 4. DGSFP website check

The "Firmar" action at
<https://www.sededgsfp.gob.es/es/Paginas/TestAutofirma.aspx> opened the origin-bound
chooser in a separate live-site check. The page's `MiniApplet` alias refers to
`AutoScript`, so the existing adapter captures its CAdES request. The automated
check cancelled the chooser without signing or launching desktop AutoFirma.
The user also confirmed capture after enabling and reloading the site in 0.1.0.
The 0.1.1 Chromium fixture test checks the new all-sites default separately.

## 5. Remaining manual checks

| Check | Limit of the current evidence |
|---|---|
| Chrome installation/update permission dialogs | The unpacked test does not automate the native permission prompt |
| Actual desktop AutoFirma launch | Local fallback functions count calls; they do not launch the desktop app |
| Physical Windows and macOS Chrome | Automated browser checks ran on Linux |
| MV3 worker process termination/restart | Chrome API fakes cover worker loss; the browser test does not kill the worker |
| Live public TSA | Permission tests and timestamp oracles pass; no live TSA was used in the extension browser test |
| Administration signing transaction | DGSFP capture was checked; no personal certificate or legally binding signature was submitted |

After an update, reload the extension and already-open websites. Chrome may require
accepting the expanded website-access permission. No external security audit has
been performed.

## 6. Package provenance

`pnpm package:extension` checks the built output and creates a ZIP with dependency
notices, the pinned upstream revision, the local source revision and a SHA-256
checksum. It packages an existing build, so rebuild after source changes.

The inspected upstream revision is
`d6dfa434d3be2aa2ed53b3956f7d10c3ab8c1035`. It had no declared application license;
clarify distribution rights before publishing. The extension package contains no
original installers or decompiled Java source.
