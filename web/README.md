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
| `src/lib/components/` | `FileDrop`, `FileCard`, `CertPicker`, `BatchQueue`, `VerifyReport` |
| `src/lib/asn1.ts` | DER reader and writer (PKCS#12 / X.509 / CMS) |
| `src/lib/pkcs12.ts` | PKCS#12 container: PBES2 decryption, certificate bags |
| `src/lib/x509.ts` | Certificate summary (subject, issuer, validity, public key) |
| `src/lib/keystore.ts` | PIN + container → non-extractable `CryptoKey` + certificate list |
| `src/lib/cms.ts` | CAdES-BES detached `SignedData` (WebCrypto digest + hand-built DER) |
| `src/lib/sign.ts` | Format list and sign-flow types (the seam for #6–#11) |
| `src/lib/c14n.ts` | Canonical XML: C14N 1.0 + Exclusive C14N |
| `src/lib/xades.ts` | XAdES-EPES enveloped signature over an XML document |
| `src/lib/batch.ts` | Batch queue state machine, manifest and ZIP assembly (engine-agnostic) |
| `src/lib/zip.ts` | ZIP writer (STORE entries, CRC-32) — no dependency, verified by `unzip -t` |
| `src/lib/verify/` | Verification engine: DER/X.509/CMS, canonicalization, XMLDSig/XAdES |
| `scripts/fetch-roots.sh` | Refreshes the bundled FNMT trust anchor from fnmt.es (checks subject + fingerprint) |
| `test/fixtures/` | Valid/tampered CAdES + XAdES samples, their generator and cross-check script |
| `src/lib/ooxml.ts` | OOXML (docx/xlsx/pptx) signing: XAdES v1.3.2 signature part, zip surgery via `fflate` |
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

## CAdES-BES signatures — #6

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

## XAdES support (no dependencies) — #7

XML documents are signed with an **enveloped XAdES-EPES** signature, entirely in the browser
(`src/lib/c14n.ts` + `src/lib/xades.ts`), and shipped as a `<name>.xsig` entry of the batch ZIP
(#9):

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

## Batch signing queue — #9

N documents can be dropped at once; they become a queue (`src/lib/batch.ts`) signed **one at a
time** with the chosen certificate, so the UI thread stays free between files and nothing is
uploaded. Each document carries its own status (`pending` / `signing` / `signed` / `error`):

- **Resumable**: a document that fails is recorded as `error` with the engine's message and the
  run continues; the next run only retries what is not signed yet.
- Replacing the document list mid-run abandons that run instead of writing results against the
  new list.
- A run ends in one ZIP (`src/lib/zip.ts`, STORE entries, hand-written CRC-32) with every signed
  document plus `manifest.json`: `signbatch` (`format`, `suboperation`, `algorithm`) and one
  `singlesigns` entry per document with `id`, `datareference`, `format`, `suboperation`, `result`
  (`BatchDataResult.Result` names: `NOT_STARTED` / `DONE_AND_SAVED` / `ERROR_PRE`), `description`
  on failure, plus `sha256`, `timestamp` and `output` as the issue asks.

Cross-checks: `src/lib/batch.test.ts` verifies each queued `.csig` with `openssl cms -verify` and
reads the archive back with an independent CRC-32 (`node:zlib`); `pnpm verify:zip` extracts a
generated archive with Info-ZIP and re-hashes its documents.

## Status

Document selection, PKCS#12 certificate loading (#5), CAdES-BES signing (#6) and XAdES-EPES XML
signing (#7) work, and both formats run through the batch queue (#9): drop N files, sign them
sequentially and download one ZIP with the signed documents and `manifest.json`. PAdES follows in
#11 and its format is marked "pendiente"; the `.csig` embeds only the signer certificate, not its
chain.

OOXML signing (#10) exists as a library (`src/lib/ooxml.ts`): it adds
`_xmlsignatures/sig-<uuid>.xml` (XAdES v1.3.2 EPES with MDSSI `SignatureTime`), the
`origin.sigs` origin part and its relationships, and leaves every other part byte-identical.
The UI does not call it yet — that wiring needs the certificate flow from #5.
