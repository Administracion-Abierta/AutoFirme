# EU DSS verdicts — XAdES-T and CAdES-X Long type 1/2 (Refs #125)

The issue's acceptance criterion is "fixtures open in EU DSS with level T / CAdES-XL reported".
This directory holds a live run against the EU DSS demo, so the criterion is checked by DSS
itself rather than by reading its source.

Endpoint, method and body shape are the ones #96 established:

```bash
POST https://ec.europa.eu/digital-building-blocks/DSS/webapp-demo/services/rest/validation/validateSignature
Content-Type: application/json
{"signedDocument":{"bytes":"<base64>","name":"<name>"},"originalDocuments":[…]}   # singular field
```

Run 2026-10-22, DSS demo, HTTP 200 on every request. Signers: `Firma CAdES` (CAdES fixtures) and
`Ñandú Prueba` (XAdES fixtures). Timestamps come from `www.freetsa.org` — the only reachable
RFC 3161 TSA from this container, so the fixtures carry real, externally issued tokens.

| Artifact | DSS `SignatureFormat` | DSS `Indication` / `SubIndication` |
|---|---|---|
| `cades-xlong1.p7s` | **`CAdES-X`** | INDETERMINATE / `NO_CERTIFICATE_CHAIN_FOUND` |
| `cades-xlong2.p7s` | **`CAdES-X`** | INDETERMINATE / `NO_CERTIFICATE_CHAIN_FOUND` |
| `xades-t.xsig` | **`XAdES-BASELINE-T`** | INDETERMINATE / `NO_CERTIFICATE_CHAIN_FOUND` |
| `xades-bes.xsig` (control: no `timeStamp`) | `XAdES-BASELINE-B` | INDETERMINATE / `NO_CERTIFICATE_CHAIN_FOUND` |
| `cades-xlong2-oldoid.p7s` (control: pre-fix OID) | `CAdES-C` | INDETERMINATE / `NO_CERTIFICATE_CHAIN_FOUND` |
| `cades-xlong2-badrefs.p7s` (control: broken imprint) | `CAdES-X` | INDETERMINATE / `NO_CERTIFICATE_CHAIN_FOUND` |

`INDETERMINATE` / `NO_CERTIFICATE_CHAIN_FOUND` on every row is the same trust-anchor statement #96
documented: the fixtures are self-signed and DSS validates against the EU trusted lists. It does not
dispute the structure, the signer, or the signature value.

## What the two controls establish

- **`cades-xlong2-oldoid.p7s`** is byte-identical to `cades-xlong2.p7s` except that the X-level
  timestamp's attribute OID is rewritten from `id-aa-ets-certCRLTimestamp` (…16.2.26) to the
  pre-fix `id-aa-timeStampToken` (…16.2.14) — same DER length, one byte changed. DSS then reports
  `CAdES-C`, i.e. **the OID is exactly what buys the `X`** in DSS's verdict. Before `be72e780` this
  repository emitted …16.2.14, so the pre-fix output provably could never have been read as
  CAdES-X. One byte, two format labels: that is the finding, demonstrated.
- **`cades-xlong2-badrefs.p7s`** keeps the correct OID and flips one byte inside
  `complete-certificate-references`. DSS reports the same structural `CAdES-X`, but its
  `cryptographicVerification` constraint turns to `HASH_FAILURE` and the report carries
  "The time-stamp message imprint is not intact!" — so DSS verifies the imprint too, and a
  structurally valid signature with corrupt X-level validation data does not come out clean.
  Our own verifier agrees and names the failure (see `ours` in `dss.issue-125.verdicts.json`).
- **`xades-bes.xsig`** is the same signer and document without `timeStamp`: DSS says
  `XAdES-BASELINE-B`, so the `-T` on `xades-t.xsig` is attributable to the timestamp we added,
  not to the rest of the signature.

## CAdES-XL, honestly

DSS reports **`CAdES-X`, never `CAdES-XL`**, for both types, and that is expected:
`complete-revocation-references` in these fixtures is an empty `SEQUENCE` (no CRL/OCSP material is
fetched yet — #23's scope, explicitly out of scope for #125). DSS's `CAdES_XL` label needs that
validation material; its `CAdES_X` check only needs exactly one of the two X-level timestamp OIDs,
which is what the fix supplies. The gap is the revocation material, not the attribute or OID
contract — and it stays a gap: nothing here works around it.

## Reproduce

```bash
# from the worktree (Node 26 + the repo's ts-hook so the app's TypeScript imports directly)
node --import ./scripts/ts-hook.mjs scripts/dss-issue-125.mjs
```

`scripts/dss-issue-125.mjs` rebuilds every fixture, posts each one to the DSS demo and rewrites this
directory. The signatures are freshly timestamped on each run, so byte-wise reruns differ by design;
the format labels above are the stable part. It needs outbound HTTPS to the DSS demo and to
`freetsa.org`, and it is deliberately not part of `pnpm test` — a network-dependent oracle cannot
gate a suite.
