# External validators — Acrobat + EU DSS (Refs #96)

## EU DSS (European Commission demo, demookit)

- UI reachable: `GET .../DSS/webapp-demo/validation` → HTTP 200 (2026-10-21).
- Programmatic validation attempted and **blocked**:
  - `POST .../signature-rest/validation/validateSignature` (documented demo shape)
    → HTTP 403 `Forbidden` (anonymous REST not permitted).
  - The `/signature-rest` page is a browser UI pointing at eIDAS EFDA
    conformance tests, not an open validation endpoint; no anonymous
    multipart/file API is exposed.
- Result: **no DSS verdict obtained.** Disagreement recorded, not a product
  finding: the demo requires an interactive browser session (file upload form),
  which this unattended container cannot drive (no display/browser session in
  scope for this issue). Human follow-up: upload `raw/item-01.cades.p7s` +
  `raw/item-03.doc.bin`, `raw/item-04.xades.xml`, `raw/item-07.pades.pdf` to
  the DSS demo validation page and paste the verdicts here; if any DSS verdict
  disagrees with ours, file a new issue per the #96 scope rule (do not fix here).

## Adobe Acrobat (desktop reader)

- **Not available** in this Linux container (no license, no GUI, no Reader
  install). No verdict obtained.
- Substituted oracle: `pdfsig` (poppler) on `raw/item-07.pades.pdf` reports
  `Signature #1`, signer `CN=Ñandú Prueba`, `SHA-256`,
  `SubFilter ETSI.CAdES.detached`, `Total document signed` — the same fields
  Acrobat's signature panel shows. Full output in `raw/item-09.interop.log`.
- Human follow-up: open `raw/item-07.pades.pdf` in Acrobat on any desktop and
  confirm the signature panel agrees; mismatches → new issue.

## Independent-validator summary (what DID run)

| Artifact | Independent validator | Verdict |
|---|---|---|
| CAdES `item-01.cades.p7s` + doc | `openssl cms -verify` | `Verification successful` |
| XAdES `item-04.xades.xml` | `xmlsec1 verify --trusted-pem` | `OK` |
| PAdES `item-07.pades.pdf` | `openssl cms -verify` on ByteRange CMS | `Verification successful` |
| PAdES `item-07.pades.pdf` | `pdfsig` (poppler) | signer + algo + ranges correct |
