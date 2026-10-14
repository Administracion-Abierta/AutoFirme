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
| `src/lib/components/` | `FileDrop`, `FileCard`, `CertPicker`, `SignButton`, `ResultCard` |
| `src/lib/asn1.ts` | DER reader (PKCS#12 / X.509) |
| `src/lib/pkcs12.ts` | PKCS#12 container: PBES2 decryption, certificate bags |
| `src/lib/x509.ts` | Certificate summary (subject, issuer, validity, public key) |
| `src/lib/keystore.ts` | PIN + container → non-extractable `CryptoKey` + certificate list |
| `src/lib/sign.ts` | Format list and sign-flow types (the seam for #6–#11) |
| `src/lib/c14n.ts` | Canonical XML: C14N 1.0 + Exclusive C14N |
| `src/lib/xades.ts` | XAdES-EPES enveloped signature over an XML document |
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

## XAdES support (no dependencies) — #7

XML documents are signed with an **enveloped XAdES-EPES** signature, entirely in the browser
(`src/lib/c14n.ts` + `src/lib/xades.ts`), and downloaded as a `<name>.xsig` file:

- **Canonicalization** is implemented here from the W3C specs: **C14N 1.0**
  (`REC-xml-c14n-20010315`) and **Exclusive C14N** (`xml-exc-c14n#`), which is what all the
  XMLDSig transforms and the `SignedInfo` canonicalization use.
- Two references: the whole document (`URI=""`, `enveloped-signature` + exc-c14n) and
  `xades:SignedProperties`. Digest and signature hash: SHA-256; `ds:SignatureValue` is a WebCrypto
  signature over the canonicalized `SignedInfo`.
- `ds:KeyInfo/X509Data` embeds the whole certificate chain loaded from the PKCS#12, and
  `xades:SigningCertificate` carries each certificate digest plus its issuer/serial.
- **Signature policy**: EPES wants a `SignaturePolicyIdentifier`; without one supplied by the
  caller the signature declares `<SignaturePolicyImplied/>` (nothing is invented — a real Spanish
  policy URN would have to come from the caller).
- The output is verified by **`xmlsec1`**, an independent XMLDSig implementation, in
  `src/lib/xades.test.ts` (its digests are recomputed by xmlsec1, so it doubles as the
  interoperability check for the canonicalizer). Tests also need `openssl` (fixtures, #5).

```bash
xmlsec1 --verify --enabled-key-data x509 --trusted-pem leaf.pem facturae.xml.xsig  # → status: OK
```

Not in scope: browser-side *verification* of signatures, inclusive-c14n `InclusiveNamespaces`
PrefixList on the transforms (supported by the canonicalizer, unused by the profile), timestamps
and long-term validation (XAdES-T/XL).

## Status

Document selection, PKCS#12 certificate loading (#5) and XAdES-EPES XML signing (#7) work.
Signing engines follow in #6 (CAdES) and #11 (PAdES); CAdES and PAdES buttons are marked
"pendiente" and report that the format is unavailable.
