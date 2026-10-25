# P0 gate evidence — 12-item parity run (Refs #96)

Run date: 2026-10-21. Host: Linux container, worktree `issue-96`.
Engine under test: `5f8c6af2` — the last commit touching `src/`, `extension/` or `scripts/`
(`git log -1 --format=%H HEAD -- src extension scripts` returns it; every commit after it on this
branch is docs-only, `git diff 5f8c6af2 HEAD -- src/ extension/ scripts/` is empty). Each run log
records the host, the tool versions and the exit code it was produced with.

This pack is checked mechanically by [`check-evidence.py`](check-evidence.py) (`python3
docs/evidence/check-evidence.py`): every `raw/…` path cited here exists, every artifact under
`raw/` is cited, the 12 rows name real suites, no run log is truncated, and the DSS formats quoted
are the ones DSS returned.

## The 12 items are the ones #19 defines

`src/routes/diagnostics/+page.svelte` (branch `issue/19-p0-tauri-shell-parity-gate-10-`) carries
the canonical list — the `GATE` array, "The 12-item parity gate of #19, each item mapped to the
suite that proves it". The list below is taken from it verbatim, not reconstructed. Issue #96's
parent is #19, so that array is the contract.

| # | Item (as #19 names it) | Proving suite (#19's `proof`) | Run here | Verdict |
|---|---|---|---|---|
| 1 | PKCS#12 import (PBES2 + legacy PBE) | `src/lib/pkcs12.test.ts` | 24 tests | PASS |
| 2 | CAdES-BES | `src/lib/cms.test.ts` | 9 tests | PASS |
| 3 | CAdES-T | `src/lib/timestamp.test.ts` | 6 tests | PASS |
| 4 | XAdES-EPES + c14n | `src/lib/xades.test.ts`, `src/lib/c14n.test.ts` | 11 + 9 tests | PASS |
| 5 | PAdES B-B | `src/lib/pades.test.ts` | 9 tests | PASS |
| 6 | PAdES visible stamp | `src/lib/pades-visible.test.ts` | 6 tests | PASS |
| 7 | PAdES B-T | `src/lib/timestamp.test.ts`, `src/lib/pades.test.ts` | 6 + 9 tests | PASS |
| 8 | Batch + ZIP | `src/lib/batch.test.ts`, `scripts/verify-batch-zip.mjs` | 8 tests + zip verify | PASS |
| 9 | Verify CAdES/XAdES/PAdES incl. byte-range | `src/lib/verify.test.ts` | 23 tests | PASS |
| 10 | IndexedDB cert store | `src/lib/certstore.test.ts` | 9 tests | PASS |
| 11 | i18n ES/EN | `src/lib/App.test.ts`, `src/lib/i18n.ts` | 19 tests | PASS |
| 12 | Extension bridge handshake + portal interception | `extension/tests/*.test.js` | 54 tests | PASS |

Suite totals: `npx vitest run` over the 11 named files → **11 files, 133 tests, 0 fail**;
`node --test extension/tests/*.test.js` → **54 tests, 0 fail**; `scripts/verify-batch-zip.mjs`
→ exit 0 (`unzip -t` + sha256 manifest recompute). Full run logs: `raw/gate-suites.json`,
`raw/gate-suites.log`, `raw/extension-tests.log`, `raw/batchzip.log`.

**Scope of "ran"**: these are the in-repo suites running on the merged tree, i.e. the same
`pnpm test` #96 says "proves them already". What #96 adds is the *independent-validator* leg —
nothing in #19's list is validated by a non-AutoFirme implementation, so the artifacts below are
the cross-check. Two #19 items (3 CAdES-T, 7 PAdES B-T) are proven by `timestamp.test.ts`; that
suite exercises the RFC 3161 request/parse path against a fixture TSA, not a live TSA — recorded
as a limit, not a pass claim beyond what the suite asserts.

## Independent-validator leg (the actual delta of #96)

Signed artifacts produced from repo entry points with the fixture keystore, then re-validated by
implementations that share no code with this repo:

| Artifact | Independent validator | Verdict | Log |
|---|---|---|---|
| `raw/cades.p7s` + `raw/cades.content.bin` | `openssl cms -verify` 4.0.3 | `CMS Verification successful` | `raw/cades.openssl.log` |
| `raw/xades.xml` | `xmlsec1 --verify` 1.3.12 | `Verification status: OK` | `raw/xades.xmlsec1.log` |
| `raw/pades.pdf` (ByteRange CMS) | `openssl cms -verify` | `CMS Verification successful` | `raw/pades.interop.log` |
| `raw/pades.pdf` | `pdfsig` (poppler) | `Signature is Valid`, signer + algo + both ranges read | `raw/pades.interop.log` |
| `raw/pades.pdf` | `qpdf --check` 12.4.2 | `No syntax or stream encoding errors found` (exit 0) | `raw/pades.interop.log` |
| `raw/cades.p7s`, `raw/xades.xml`, `raw/pades.pdf` | **EU DSS demo REST** | `SignatureValid: true`, intact, per family | `raw/dss.*.verdict.json` |

### EU DSS verdicts (HTTP 200, 2026-10-21)

Posted each signed document to
`https://ec.europa.eu/digital-building-blocks/DSS/webapp-demo/services/rest/validation/validateSignature`
as `application/json` (`{"signedDocument":{"bytes":<b64>,"name":…},"originalDocuments":[…]}`).

| Document | DSS format | SignatureIntact / SignatureValid | Indication | SubIndication | Scope DSS read |
|---|---|---|---|---|---|
| `cades.p7s` (+ detached `cades.content.bin`) | `CAdES-BASELINE-B` | true / **true** | INDETERMINATE | `NO_CERTIFICATE_CHAIN_FOUND` | Full document |
| `xades.xml` | `XAdES-BASELINE-B` | true / **true** | INDETERMINATE | `NO_CERTIFICATE_CHAIN_FOUND` | Full XML file with transformations |
| `pades.pdf` | `PAdES-BES` | true / **true** | INDETERMINATE | `NO_CERTIFICATE_CHAIN_FOUND` | The document ByteRange : [0, 1590, 6120, 669] |

**Does DSS agree with us? Yes on everything we claim.** For all three families DSS reports the
signature value intact and valid, the digest over the right content, the right signer
(`Ñandú Prueba`, RSA-2048/SHA-256), and — for PAdES — the exact ByteRange our verifier emits.
`INDETERMINATE / NO_CERTIFICATE_CHAIN_FOUND` is **not a disagreement**: the fixture keystore is
self-signed (`CN=Ñandú Prueba`, issuer itself) and DSS validates against the EU trusted lists, so
it has no anchor for it. Our own `certChain` check passes only because the test passes the fixture
leaf as `trustedRoots` — the mechanism is exercised, the anchor is deliberately the fixture. That
is the same limitation #19's items carry ("self-verify items check the chain mechanism, not a
public root"). Error key `BBB_XCV_CCCBB_SIG_ANS` = "certificate chain not trusted"; no
cryptographic error, no `TOTAL_PASSED`-style failure in `ValidationReport`.

Trust model note for a reader of the reports: if the fixture were replaced by a real FNMT/AC
chain, the expected DSS indication moves from INDETERMINATE to PASSED for CAdES/XAdES once the
anchors are published in the LOTL. Recorded, not fixed here (out of scope per #96).

## Disagreements / limits (recorded, not hidden)

- **Acrobat: not obtained.** No Acrobat in this Linux container. Substituted oracle is `pdfsig`
  (poppler) — same parser family desktop readers use — plus DSS, which is an EU reference
  implementation. Human follow-up in `external-validators.md`.
- **`qpdf --check` on the PAdES output: passes (exit 0), no rejection.** An earlier draft of this
  doc claimed qpdf "cannot parse the incremental xref … `unable to find page tree`". That is not
  reproducible: `qpdf --check raw/pades.pdf` (qpdf 12.4.2) exits 0 with "No syntax or stream
  encoding errors found", and `qpdf --show-npages` returns 1. The qpdf leg is therefore a third
  agreeing oracle, not a disagreement. See `raw/pades.interop.log`.
- **#19's list, not a format-matrix list.** An earlier draft invented its own 12 items (3 formats ×
  sign/verify/interop + OOXML + tamper + batch). Those artifacts are kept because they are the
  independent-validator evidence this issue exists for, but they are **not** the gate's 12 items;
  the gate's 12 are the table above. Nothing in the artifact set was dropped.
- **Items 3 and 7 (CAdES-T / PAdES B-T) have no independent-validator artifact.** The timestamp
  token path is only exercised against the fixture TSA in `timestamp.test.ts`; no public TSA was
  used. Difference from a live-TSA run is not observable in this environment; recorded as a limit.
- **Fixture trust anchor.** Loaded from `raw/chain.pem`; `raw/leaf.pem` is a bare public key, not a
  certificate — `xmlsec1 --trusted-pem leaf.pem` fails with `failed to load trusted cert`. Kept as
  a warning for whoever re-runs the command.

## Artifacts

`raw/` — `cades.p7s`, `cades.content.bin`, `cades.selfverify.json`, `cades.openssl.log`,
`xades.xml`, `xades.selfverify.json`, `xades.xmlsec1.log`, `pades.pdf`, `pades.selfverify.json`,
`pades.byrange.cms.der`, `pades.byrange-content.bin`, `pades.interop.log`, `ooxml.docx`,
`tamper.negative.json`, `batch.perf.log`, `dss.{cades,xades,pades}.verdict.json`,
`dss.{cades,xades,pades}.validation-report.xml`, `chain.pem`, `leaf.pem`, `results.json` (the
format-level run, kept for provenance), `gate-suites.json`, `gate-suites.log`,
`extension-tests.log`, `batchzip.log`.

These are the artifacts a human needs to drive Acrobat / DSS by hand — `cades.p7s` +
`cades.content.bin`, `xades.xml`, `pades.pdf`.
