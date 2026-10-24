# External validators — Acrobat + EU DSS (Refs #96)

Third-party verdicts for one signed document per family. Ours are the `*.selfverify.json` reports
in `raw/`; the question this file answers is whether an implementation that shares no code with
this repo says the same thing.

## EU DSS demo — verdicts obtained

Endpoint: `POST https://ec.europa.eu/digital-building-blocks/DSS/webapp-demo/services/rest/validation/validateSignature`,
`Content-Type: application/json`, body
`{"signedDocument":{"bytes":"<base64>","name":"<name>"}[,"originalDocuments":[…]]}`.

Reachable from this container (the demo's own UI page returns 200; the REST endpoint answers).
Requests ran 2026-10-21, HTTP 200 for all three families. Raw responses, one pair per family, in
`raw/dss.cades.verdict.json` + `raw/dss.cades.validation-report.xml`,
`raw/dss.xades.verdict.json` + `raw/dss.xades.validation-report.xml`,
`raw/dss.pades.verdict.json` + `raw/dss.pades.validation-report.xml`. The `.verdict.json` files are
the parsed summary (method, URL, HTTP status, date, and the format / intact / valid / indication
fields the table below quotes); the `.validation-report.xml` files are the untouched
`validationReportDataHandler` payload DSS returned.

| Document | DSS `SignatureFormat` | `SignatureIntact` | `SignatureValid` | `Indication` / `SubIndication` |
|---|---|---|---|---|
| `raw/cades.p7s` + `raw/cades.content.bin` | `CAdES-BASELINE-B` | true | **true** | INDETERMINATE / `NO_CERTIFICATE_CHAIN_FOUND` |
| `raw/xades.xml` | `XAdES-BASELINE-B` | true | **true** | INDETERMINATE / `NO_CERTIFICATE_CHAIN_FOUND` |
| `raw/pades.pdf` | `PAdES-BES` | true | **true** | INDETERMINATE / `NO_CERTIFICATE_CHAIN_FOUND` |

Signer read back by DSS on all three: `Ñandú Prueba`, RSA-2048, SHA-256 — matching the
`*.selfverify.json` reports. PAdES scope read by DSS: `The document ByteRange : [0, 1590, 6120, 669]`
— byte-identical range to the one our verifier emits and the one `pdfsig` reads.

**Where the verdicts differ and which side is right.** The only difference is cryptographic
indication: DSS says INDETERMINATE (`NO_CERTIFICATE_CHAIN_FOUND`, error key `BBB_XCV_CCCBB_SIG_ANS`),
ours says `status: valid`. **Both are right and they measure different things.** DSS validates
against the EU trusted lists and refuses to build a chain to a root it does not know; our fixture
keystore is self-signed (`CN=Ñandú Prueba`, issuer = itself), and our `certChain` check passes
because the test hands the fixture leaf in as `trustedRoots`. So DSS's INDETERMINATE is a *trust
anchor* statement, not a cryptographic one — it does not dispute the signature value, the digest,
the signer, or the PAdES ranges, all of which it reports as valid. Our report does not claim the
fixture is publicly trusted, so there is no mismatch with our claims; there is a mismatch with
what an unqualified reader might take "valid" to mean, and that is why this row exists.

Reproduce:

```bash
cd docs/evidence/raw
python3 -c "import base64,json;print(json.dumps({'signedDocument':{'bytes':base64.b64encode(open('cades.p7s','rb').read()).decode(),'name':'cades.p7s'},'originalDocuments':[{'bytes':base64.b64encode(open('cades.content.bin','rb').read()).decode(),'name':'cades.content.bin'}]}))" > /tmp/req.json
curl -s -X POST https://ec.europa.eu/digital-building-blocks/DSS/webapp-demo/services/rest/validation/validateSignature \
  -H 'Content-Type: application/json' -H 'Accept: application/json' --data @/tmp/req.json
```

Caveats a re-runner needs: the field is `signedDocument` **singular** (`signedDocuments` is
rejected — `Unrecognized field "signedDocuments"`); `multipart/form-data` to the same path returns
`HTTP 415 Unsupported Media Type`; and a bare `GET` returns `HTTP 405`, which reads like a
forbidden endpoint if you stop there. `POST` with the JSON above is the working shape.

## Adobe Acrobat — no verdict

- **Not available** in this Linux container (no license, no GUI, no Reader install). No verdict
  obtained here; that part of #96's acceptance is unmet and needs a desktop human.
- Substituted oracles for the PAdES artifact: `pdfsig` (poppler 26.10.0) reports `Signature #1`,
  signer `CN=Ñandú Prueba`, `SHA-256`, `SubFilter ETSI.CAdES.detached`, `Signed Ranges
  [0 - 1590], [6120 - 6789]`, `Signature is Valid`; `qpdf --check` (12.4.2) exits 0; DSS wraps it in
  the same `PAdES-BES` verdict as above. Three independent parsers agree.
- Human follow-up: open `docs/evidence/raw/pades.pdf` in Acrobat and confirm the signature panel
  shows a valid signature and "the entire document is signed". Acrobat reports an unknown issuer for
  the self-signed fixture (`Certificate issuer isn't Trusted` — `pdfsig` says the same), which is
  expected, not a defect. A real mismatch → new issue per #96's scope rule.
