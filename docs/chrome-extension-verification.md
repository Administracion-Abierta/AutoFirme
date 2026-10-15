# Chrome extension verification record

Environment: Fedora Linux x86-64, Node 24.19.0, pnpm 11.24.0, disposable headless
Chromium profile, OpenSSL and unzip. The extension uses Chrome MV3 (minimum 116).
No personal browser profile, certificate or legally binding document is used.

## Baseline and automated checks

- Unmodified upstream: 197 tests pass, two skipped; type checking clean.
- Complete application after extension integration: 222 tests pass, two skipped;
  `pnpm test --maxWorkers=2`. Existing CMS, XML, PDF/stamp, ZIP, PKCS#12 and app checks
  stay in the suite; new chooser, eligibility, TSA and permission tests are included.
- Type checking: zero errors and warnings.
- Normal static web build and full Svelte app extension build both pass.
- Bridge tests: 25 pass, covering sender-derived identity, permissions separated
  from enabled sites, exact chooser authorization, supported-call validation,
  source callbacks, concurrency, expiry, cached/stale documents, worker/port loss,
  local disconnect cleanup, repeat BFCache restoration, public results, SPA URL
  changes and safe pre-capture fallback after site disable.
- Artifact tests: 15 pass. Checks reject missing entries and orphan app bundles,
  remote/missing assets, inline executable scripts, eval, PWA registration and
  reduced app feature bundles. Reachable module imports are checked.
- `bash test/fixtures/verify-fixtures.sh`: all positive and tampered CAdES/XML and
  canonicalization fixture checks pass.

The initial concurrent validation attempt saturated this 3.6 GiB machine and was
stopped. Above app results come from a fresh stable run with two workers; no
interrupted or timed-out run is represented as passing.

## Real unpacked extension flow

`pnpm test:extension:browser` launches its own disposable Chromium because MV3
installation requires flags unavailable to the collaborative preview. The test
manifest temporarily pregrants only 127.0.0.1; production grants no hosts by default.

Observed passes:

- Actual AutoFirme App.svelte mounts under the extension origin; real CertPicker
  imports an OpenSSL-generated PKCS#12 and seals it using the real identity store.
- Full app stays within 320px, 420px and 1280px viewport widths.
- Actual PDF visible-stamp controls sign a two-document batch. Both signed PDFs are
  stored in the app's outputs, the ZIP downloads and passes `unzip -t`, and the
  actual verification screen reports passing structure and cryptographic checks.
  Both PDF signatures independently pass OpenSSL CMS/CAdES verification against the
  disposable test certificate. The self-issued test certificate is not an FNMT
  trust anchor; no claim that every trust check passes is made.
- Website requests open the real chooser; saved identities require a new unlock.
  Opening/selecting/unlocking alone produces no signature. Explicit confirmation
  returns attached and detached CAdES verified independently by OpenSSL `-cades`.
- Certificate-selection callbacks, explicit fallback, dismissal fallback exactly
  once, cancellation and unsupported PDF pass-through all work.
- Two simultaneous distinct payloads stay isolated: cancelling one leaves the other
  usable, and its signature verifies against only the intended payload. Another
  app tab cannot retrieve the chooser's request.
- Requesting-document navigation removes the stale chooser without replay or a
  late result. Disabling a site restores the original native call on that already
  loaded document, with no chooser and no reload requirement.
- Site-management RPC remains authorized after the app changes its own SPA query;
  chooser RPC remains bound to the exact request URL and actual chooser tab.

The final inline certificate-list rendering rerun also passes all these checks.
Saved choices appear directly beside the website, without a nested chooser modal.
The selected-certificate view stays within 320px and 420px widths. The real
CertPicker and normal full-app modal share the same identity/PIN/import logic.

## Review and remaining checks

Task-scoped spec/quality reviews and fix reviews cover packaging, bridge and UI.
Whole-branch review found one remaining site-disable issue; it was fixed and
rereviewed. Other fixes have regression tests first observed failing: local port
cleanup, BFCache reconnection, reachable app entry, preflight error recovery and
SPA management identity.

Real MV3 worker termination/restart is currently tested through Chrome API fakes,
not by killing the actual browser worker. Chrome's native optional-permission
prompt UI, actual desktop AutoFirma external-protocol launch, real government
sites, and physical Windows/macOS Chrome require manual/environment-specific
checks. Original AutoScript fallback functions in the local fixture count calls;
they deliberately do not launch a native signing app.

The full app's TSA engine has existing OpenSSL timestamp oracles and new
permission-denied/granted tests. A live public TSA was not used for the extension
browser test. No external security audit or production-readiness claim is made.

Packaging records the pinned upstream revision, local source revision, dependency
notices and SHA-256. Upstream has no declared application license at the inspected
revision; this is recorded rather than inventing a license grant.
