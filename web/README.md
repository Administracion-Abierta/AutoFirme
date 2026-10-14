# AutoFirma Web — `web/`

Client-side-rendered SPA: sign and verify documents with X.509 certificates, entirely in the
browser. No backend, no key material leaving the client.

## Stack

- SvelteKit 3 (static adapter) + Svelte 5 runes + Vite + TypeScript
- Tailwind CSS v4 (`@tailwindcss/vite`)
- pnpm, Node 22 for builds
- Vitest + `@testing-library/svelte` + jsdom for the smoke test

| Path | Contents |
|---|---|
| `src/routes/` | Pages — `/` is the sign flow |
| `src/lib/App.svelte` | Sign workflow shell (steps 1–2) and verify workflow |
| `src/lib/components/` | `FileDrop`, `FileCard`, `CertPicker`, `SignButton`, `ResultCard`, `VerifyReport` |
| `src/lib/sign.ts` | Format list and sign-flow types (the seam for #5–#11) |
| `src/lib/verify/` | Verification engine: DER/X.509/CMS, canonicalization, XMLDSig/XAdES |
| `scripts/fetch-roots.sh` | Refreshes the bundled FNMT trust anchor from fnmt.es (checks subject + fingerprint) |
| `test/fixtures/` | Valid/tampered CAdES + XAdES samples, their generator and cross-check script |
| `src/lib/i18n.ts` | ES/EN copy |
| `Caddyfile` | Static hosting with SPA fallback (used by the root `Containerfile`) |

`#lib/*` subpath imports (`package.json` `imports`) map to `src/lib/*`.

## Commands

```bash
pnpm install
pnpm dev          # vite dev server
pnpm build        # static output in build/
pnpm preview      # serve build/ locally
pnpm test         # vitest smoke test (mounts App)
pnpm check        # svelte-check + tsc
```

## Verify flow (issue #8)

`Verificar` mode drops a `/.csig` (CAdES), `/.xsig` (XAdES) or PDF and reports one card per check,
green/red with the technical reason:

| Check | What it proves |
|---|---|
| Structure | the artifact parses as CMS `SignedData` / `ds:Signature` (PDF/PAdES is flagged as #11) |
| Content integrity | `messageDigest` signed attribute vs the digest of the embedded content (1) |
| Cryptographic signature | the signature over the DER re-encoded `signedAttrs` / over `c14n(ds:SignedInfo)` |
| Signed references | every `ds:Reference` transform chain (`enveloped-signature`, inclusive/exclusive C14N) reproduces its `DigestValue` |
| Signer certificate | the signer cert of the `SignerInfo`/`KeyInfo` |
| Certificate validity | the signer cert is inside its validity window |
| Trust chain | issuer links verify by signature up to a bundled root (FNMT AC RAIZ FNMT-RCM) |
| Signing time | `signingTime` attribute / `xades:SigningTime` |

(1) detached CAdES cannot be checked alone: drop the original document when the report asks for it.

All crypto runs in the browser through `crypto.subtle` — certificates are parsed from DER by hand
(no new dependency), the trust store is the PEM under `src/lib/verify/roots/`, and unsupported
algorithms or transforms are reported as a red check with a reason instead of a silent pass.
Revocation (OCSP/CRL), timestamps (TSA) and certificate policies are out of scope.

Fixtures: `bash test/fixtures/generate.sh` rebuilds them (throwaway test PKI, keys gitignored),
`bash test/fixtures/verify-fixtures.sh` cross-checks them with `openssl cms -verify` and an
independent python digest recomputation. `pnpm test` runs that cross-check as part of the suite.

## Container

Build context is the repo root:

```bash
docker build -f Containerfile -t autofirme-web .
docker run --rm -p 8080:80 autofirme-web   # http://localhost:8080/
```

Multi-stage: `node:22-alpine` builds the static site, `caddy:2-alpine` serves it with the SPA
fallback (`try_files {path} {path}/ /404.html`).

## Status

Shell only: document selection works, the certificate picker is a placeholder and signing is
disabled until #5 (PKCS#12) lands. Signing engines follow in #6 (CAdES), #7 (XAdES), #11 (PAdES).
