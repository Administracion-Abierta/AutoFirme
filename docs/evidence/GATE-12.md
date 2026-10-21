# P0 gate evidence — 12-item parity run (Refs #96)

Run date: 2026-10-21. Host: Linux container in this worktree.
Engine: `src/lib/cms.ts` + `src/lib/xades.ts` + `src/lib/pades.ts` + `src/lib/ooxml.ts`
at commit `c0722af5` (plus this evidence-only diff — no `src/` changes).
Keystore: self-signed RSA-2048 fixture generated on the fly by
`src/test/fixtures.ts` (openssl); trust anchor = the fixture leaf passed as
`trustedRoots`, so self-verify items check the chain mechanism, not a public root.
Runner: throwaway script (not committed) driving the app's own sign/verify entry
points; oracles: `openssl cms -verify`, `xmlsec1 verify`, `pdfsig` (poppler).

**No canonical 12-item list exists in the tree** (roadmap, #19, CI, #75 branch
all say "12-item" without enumerating). The mapping below is the reconstruction;
each item is one row, one command-equivalent, one artifact.

| # | Gate item | Verdict | Artifact(s) |
|---|---|---|---|
| 01 | CAdES-BES sign (detached, SHA-256) | PASS (1520 bytes) | `raw/item-01.cades.p7s` |
| 02 | CAdES self-verify (`verifyArtifact`, 7 checks) | PASS (0 fail) | `raw/item-02.cades-report.json` |
| 03 | CAdES interop: `openssl cms -verify` | PASS (`Verification successful`) | `raw/item-03.openssl.log`, `raw/item-03.doc.bin` |
| 04 | XAdES-EPES sign (enveloped) | PASS (5845 chars) | `raw/item-04.xades.xml` |
| 05 | XAdES self-verify (`verifyArtifact`, 8 checks) | PASS (0 fail) | `raw/item-05.xades-report.json` |
| 06 | XAdES interop: `xmlsec1 verify` | PASS (`OK`) | `raw/item-06.xmlsec1.log` |
| 07 | PAdES B-B sign (incremental update over `test/fixtures/pades-source.pdf`) | PASS (6789 bytes) | `raw/item-07.pades.pdf` |
| 08 | PAdES self-verify (`verifyArtifact`, 7 checks) | PASS (0 fail) | `raw/item-08.pades-report.json` |
| 09 | PAdES interop: ByteRange CMS via `openssl cms -verify` + `pdfsig` signer read + prefix-preservation | PASS (openssl OK, prefix intact) | `raw/item-09.interop.log`, `raw/item-09.cms.der`, `raw/item-09.byrange-content.bin` |
| 10 | OOXML sign (signature part added to a minimal docx) | PASS (4656 bytes) | `raw/item-10.ooxml.docx` |
| 11 | Tamper negative: 1-byte flip inside signed data fails in all 3 families | PASS (invalid × 3) | `raw/item-11.negative.json` |
| 12 | Batch smoke: 50 × CAdES sign | PASS (~1.5 ms/file, budget <2 s/file) | `raw/item-12.perf.log` |

Machine-readable: `raw/results.json`. Trust inputs: `raw/leaf.pem`, `raw/chain.pem`.

## Disagreements / limits (recorded, not hidden)

- Item 11, first run: a flip at the buffer midpoint (inside the PAdES `/Contents`
  gap, outside both ByteRange spans) still verified `valid` — correct behavior
  (unsigned gap), wrong test placement. Re-ran with the flip at offset 10
  (inside span 1): all three families `invalid`. Lesson kept in the artifact log.
- `qpdf --check` was tried as a PAdES oracle and rejected: it cannot parse the
  incremental xref the engine appends (`unable to find page tree`), while
  poppler (`pdfsig`) — the same parser family desktop readers use — reads the
  signature, signer CN, hash algo and signed ranges correctly, and `openssl`
  verifies the ByteRange CMS. Oracle mismatch recorded; engine output accepted
  on the poppler+openssl majority.
- Acrobat and EU DSS verdicts: see `external-validators.md` — unreachable
  unattended; independent local oracles substituted, blocker recorded exactly.
