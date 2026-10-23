# EU DSS oracle (#140)

`oracle.mjs` posts the signature fixtures in `test/fixtures/` — the hand-picked `CORPUS` list in that
file, not a directory walk — to the EU DSS demo and compares the verdict against our own
`src/lib/verify`, field by field. Any difference that `allowlist.json` does not
pin fails the run; `.github/workflows/verify.yml` runs it on every push to `main`/`develop`.

`--self-check` also guards the corpus itself, offline: every `*.csig`/`*.xsig` in `test/fixtures/`
must be posted, and every file `CORPUS` names must exist. A new signature fixture, or a rename,
fails the push even when the endpoint is down — which is the only way the "every fixture" claim
stays true.

DSS ships no CLI artifact on Maven Central and publishes no Docker image (checked: the
`eu.europa.ec.joinup.sd-dss` Maven group has no CLI or standalone module, and hub/ghcr/quay have no
official DSS image — building one from `esig/dss-demonstrations` needs Maven and a Tomcat WAR). The
demo REST endpoint is the independent validator: it is the same one #96 and #125 used.

```bash
node --import ./scripts/ts-hook.mjs fixtures/dss/oracle.mjs             # live comparison
node --import ./scripts/ts-hook.mjs fixtures/dss/oracle.mjs --record    # print observed pairs
node --import ./scripts/ts-hook.mjs fixtures/dss/oracle.mjs --self-check # offline gate check
```

## Compared fields

`format`, `verdict`, `signedBy` (signer CN), `signingTime`. Two normalisations keep the comparison
meaningful:

- **format** — DSS says `CMS-NOT-ETSI` / `XML-NOT-ETSI` / `PAdES-BES`, we say `cades` / `xades` /
  `pdf`. Different vocabularies for the same families, so both collapse to the family.
- **verdict** — DSS `TOTAL_PASSED` / `TOTAL_FAILED` / `INDETERMINATE` → `valid` / `invalid` /
  `indeterminate`; ours `ok` → `valid` / `invalid`. Three-valued so DSS's own `INDETERMINATE`
  never masquerades as agreement.

DSS-only fields (`SubIndication`, `SignatureLevel`, `CertificateChain`) are printed on mismatch
for triage and never compared.

## Why `INDETERMINATE`

Every valid fixture is pinned as `INDETERMINATE`, not `TOTAL_PASSED`: the fixtures chain to the
throwaway test root in `test/fixtures/ca`, and DSS validates against the EU trusted lists, which
have never heard of it. DSS reports no cryptographic error — the gap is the trust anchor we
deliberately control. Same statement #96 and `docs/evidence/issue-125/README.md` record.

## Allowlist

See `allowlist.json`. An entry is not a skip: both sides must still differ, and each must sit on
the exact pair pinned there. A pin whose values move (because we changed a verdict, or DSS did)
fails the run, so the allowlist cannot rot silently. `--self-check` proves both failure paths
offline on every push.

The endpoint being unreachable is a no-op, not a failure — a network oracle cannot gate a suite.
