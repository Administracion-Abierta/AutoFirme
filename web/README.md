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
| `src/lib/App.svelte` | Sign workflow shell (steps 1–2) |
| `src/lib/components/` | `FileDrop`, `FileCard`, `CertPicker`, `BatchQueue` |
| `src/lib/asn1.ts` | DER reader and writer (PKCS#12 / X.509 / CMS) |
| `src/lib/pkcs12.ts` | PKCS#12 container: PBES2 decryption, certificate bags |
| `src/lib/x509.ts` | Certificate summary (subject, issuer, validity, public key) |
| `src/lib/keystore.ts` | PIN + container → non-extractable `CryptoKey` + certificate list |
| `src/lib/cms.ts` | CAdES-BES detached `SignedData` (WebCrypto digest + hand-built DER) |
| `src/lib/sign.ts` | Format list and sign-flow types |
| `src/lib/batch.ts` | Batch queue state machine, manifest and ZIP assembly (engine-agnostic) |
| `src/lib/zip.ts` | ZIP writer (STORE entries, CRC-32) — no dependency, verified by `unzip -t` |
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
pnpm verify:zip   # cross-check a batch ZIP with unzip -t and sha256sum
```

## Container

Build context is the repo root:

```bash
docker build -f Containerfile -t autofirme-web .
docker run --rm -p 8080:80 autofirme-web   # http://localhost:8080/
```

Multi-stage: `node:22-alpine` builds the static site, `caddy:2-alpine` serves it with the SPA
fallback (`try_files {path} {path}/ /404.html`).

## PKCS#12 support (no dependencies)

The container is parsed in the browser — no library, no server:

- **PBES2 only**: PBKDF2 (HMAC-SHA1/SHA256) + AES-128/192/256-CBC, which is what OpenSSL 3+/4 and
  Java 9+ write. **Legacy PBES1 containers (3DES/RC2) are rejected** with an actionable message:
  WebCrypto has no DES/RC2 primitive. Re-export as modern PKCS#12 (or use an OS/hardware keystore).
- The private key is imported with `extractable: false` and `usages: ['sign']`, so its material
  cannot be read back out of the browser.
- The certificate that owns the key is identified by comparing public-key material (RSA modulus
  and exponent; EC public point) against each certificate of the container.
- **The PKCS#12 MAC is not verified** (the RFC 7292 B.2 KDF is not implemented). A wrong PIN is
  still detected, by the failure to decrypt the key bag. Follow-up issue material.

## CAdES-BES signatures

`src/lib/cms.ts` builds a **detached CAdES-BES** (`SignedData`, RFC 5652) whose four signed
attributes match the reference implementation: `contentType`, `messageDigest`, `signingTime` and
`signingCertificateV2` (ESS, RFC 5035). SHA-256 only; RSA (`rsaEncryption`) and EC
(`ecdsa-with-SHA256`, with WebCrypto's raw `r || s` re-encoded as a DER `SEQUENCE`) keys. The
document is **never** embedded — the `.csig` carries its digest, so the original file must be
kept next to it. The signature covers the whole `SET OF` signed attributes, and the digest and
signature come from `crypto.subtle`.

Cross-check (the oracle is OpenSSL, not our own code):

```bash
openssl cms -verify -inform DER -in contrato.pdf.csig -content contrato.pdf \
  -CAfile ca.pem -purpose any -binary
```

`-binary` matters: without it `openssl cms` applies S/MIME line-ending canonicalisation and
every signature of a file containing a newline would be reported as a content failure.
`src/lib/cms.test.ts` runs that command (plus `-cades` to force the ESS check) over freshly
generated RSA and EC containers, and compares the attribute set with the committed vector in
`src/test/fixtures/cades/` — a `.csig` generated once by `openssl cms -sign -cades -md sha256
-binary` (its signing key is not committed).

## Status

N documents can be dropped at once: they become a signing queue processed sequentially with a
per-document status (`pending` / `signing` / `signed` / `error`), and a run ends in one ZIP
download holding every signed document plus `manifest.json`. PKCS#12 loading (#5) and CAdES-BES
signing (#6) are wired in, and the `.csig` embeds only the signer certificate, not its chain.
XAdES (#7) and PAdES (#11) follow, so their formats stay disabled. A document that fails is
recorded as `error` with the reason and never stops the batch.
