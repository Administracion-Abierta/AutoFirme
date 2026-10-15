# AutoFirma Web

Client-side-rendered SPA: sign and verify documents with X.509 certificates, entirely in the
browser. No backend, no key material leaving the client.

The reference implementation is [AutoFirma](https://github.com/ctt-gob-es/clienteafirma)
(`ctt-gob-es/clienteafirma`, the Spanish government's signing client, GPL-2+/EUPL-1.1): this
project re-thinks its signing core (`afirma-core`, the `AOSigner` family) as a browser-native
TypeScript app. See [docs/architecture.md](docs/architecture.md) for the map of the Java signing
core this rewrite follows, and [AGENTS.md](AGENTS.md) for the repo rules.

The app lives at the repo root; from the root:

```bash
pnpm install
pnpm test
pnpm build
```

## Stack

- SvelteKit 3 (static adapter) + Svelte 5 runes + Vite + TypeScript
- Tailwind CSS v4 (`@tailwindcss/vite`)
- shadcn-svelte components (bits-ui) on the same tokens; dark mode is the `.dark` class on `<html>`,
  driven by the system preference from an inline script in `src/app.html` (#27)
- pnpm, Node 22 for builds
- Vitest + `@testing-library/svelte` + jsdom for the smoke test

| Path | Contents |
|---|---|
| `src/routes/` | Pages — `/` is the sign flow |
| `src/lib/App.svelte` | Sign workflow shell (2/3 drop + file list, 1/3 sticky actions on ≥1024px; stacked below) and verify workflow |
| `src/lib/components/` | `FileDrop`, `FileCard`, `CertPicker`, `BatchQueue`, `VerifyReport` |
| `src/lib/components/ui/` | shadcn-svelte primitives (button, card, table, badge, input, dropdown-menu, dialog, tooltip) + their snapshot test (#27) |
| `src/lib/utils.ts` | `cn()` class merger used by the primitives |
| `components.json` | shadcn-svelte CLI config (`#lib` aliases, neutral base, `src/app.css`) |
| `src/lib/asn1.ts` | DER reader and writer (PKCS#12 / X.509 / CMS) |
| `src/lib/pkcs12.ts` | PKCS#12 container: PBES2 decryption, certificate bags |
| `src/lib/x509.ts` | Certificate summary (subject, issuer, validity, public key) |
| `src/lib/keystore.ts` | PIN + container → non-extractable `CryptoKey` + certificate list |
| `src/lib/cms.ts` | CAdES-BES detached `SignedData` (WebCrypto digest + hand-built DER) |
| `src/lib/sign.ts` | Format list and sign-flow types (the seam for #6–#11) |
| `src/lib/c14n.ts` | Canonical XML: C14N 1.0 + Exclusive C14N |
| `src/lib/xades.ts` | XAdES-EPES enveloped signature over an XML document |
| `src/lib/pades.ts` | PAdES B-B signing: CMS `SignedData` over a real PDF incremental update |
| `src/lib/pades-visible.ts` | Visible signature block: corner/page placement, image embedding, block text |
| `src/lib/der.ts` | Minimal DER writer/reader for the PAdES CMS structures |
| `src/lib/batch.ts` | Batch queue state machine, manifest and ZIP assembly (engine-agnostic) |
| `src/lib/zip.ts` | ZIP writer (STORE entries, CRC-32) — no dependency, verified by `unzip -t` |
| `src/lib/verify/` | Verification engine: DER/X.509/CMS, canonicalization, XMLDSig/XAdES, PDF/PAdES (`pades.ts`) |
| `scripts/fetch-roots.sh` | Refreshes the bundled FNMT trust anchor from fnmt.es (checks subject + fingerprint) |
| `scripts/smoke-real.sh` | Signs with a real keystore (`work/certs/*.p12`, gitignored) and verifies the result (#40) |
| `test/fixtures/` | Valid/tampered CAdES + XAdES samples, their generator and cross-check script |
| `src/lib/ooxml.ts` | OOXML (docx/xlsx/pptx) signing: XAdES v1.3.2 signature part, zip surgery via `fflate` |
| `src/lib/i18n.ts` | ES/EN copy |
| `Caddyfile` | Static hosting with SPA fallback (used by the root `Containerfile`) |
| `src/service-worker.ts` | App-shell precache, offline fallback and update handoff (PWA, #47) |
| `static/manifest.webmanifest`, `static/icon-*.{svg,png}` | Install manifest and icon set (#47) |
| `src/lib/components/PwaControls.svelte` | Header `Install app` / `Update version` buttons (#47) |

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
bash scripts/smoke-real.sh   # real expired-cert smoke test (skips without work/certs/*.p12)
```

## Verify flow (issue #8)

`Verificar` mode drops a `/.csig` (CAdES), `/.xsig` (XAdES) or PDF and reports one card per check,
green/red with the technical reason:

| Check | What it proves |
|---|---|
| Structure | the artifact parses as CMS `SignedData` / `ds:Signature`; a PDF's `/ByteRange` + `/Contents` delimit a PAdES signature |
| Content integrity | `messageDigest` signed attribute vs the digest of the embedded content (1) |
| Cryptographic signature | the signature over the DER re-encoded `signedAttrs` / over `c14n(ds:SignedInfo)` |
| Signed references | every `ds:Reference` transform chain (`enveloped-signature`, inclusive/exclusive C14N) reproduces its `DigestValue` |
| Signer certificate | the signer cert of the `SignerInfo`/`KeyInfo` |
| Certificate validity | the signer cert's validity window. Outside it the check is **amber, never red**: an expired signer makes the report `valid-but-expired` — the signature is still valid (#40) |
| Trust chain | issuer links verify by signature up to a bundled root (FNMT AC RAIZ FNMT-RCM) |
| Signing time | `signingTime` attribute / `xades:SigningTime` |

(1) detached CAdES cannot be checked alone: drop the original document when the report asks for it.
A detached PAdES needs no second file — the PDF's own `/ByteRange` spans are the content.

The overall verdict is `valid` (every check passed), `valid-but-expired` (only the signer's validity
window is over — a warning) or `invalid` (a check failed). `valid-but-expired` is what AutoFirma does
with an expired certificate, and it is what `scripts/smoke-real.sh` asserts against the real legacy
FNMT keystore in `work/certs/old-cert.p12`.

All crypto runs in the browser through `crypto.subtle` — certificates are parsed from DER by hand
(no new dependency), the trust store is the PEM under `src/lib/verify/roots/`, and unsupported
algorithms or transforms are reported as a red check with a reason instead of a silent pass.
Revocation (OCSP/CRL), *validating* a timestamp of an incoming signature and certificate policies
are out of scope. Timestamps can be **added** when signing, though — see the TSA section below.

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

## Installable app (PWA) — #47

`pnpm build` emits `build/manifest.webmanifest` and `build/service-worker.js`, and the prerendered
HTML registers the worker — all of it SvelteKit's own service-worker support (no plugin, no
dependency). The worker precaches the shell (`$app/manifest`'s built + static + prerendered files),
answers an offline navigation with the cached shell — signing still works with no network, because
nothing but the documents themselves ever leaves the browser — and never writes a runtime response
to a cache (`GET` + same-origin only, no `cache.put`), so a produced signature cannot be cached.

Chrome/Edge show **Install app** in the header when the browser fires `beforeinstallprompt`; the
button replays that prompt. A new deploy leaves a worker *waiting*, which shows **Update version**;
the click posts `SKIP_WAITING` and the page reloads once the new worker takes control.

Self-hosting notes:

- **HTTPS is required** for the service worker (and therefore for install) — the only exception is
  `http://localhost`. Behind a Cloudflare Tunnel the site is already HTTPS, so there is nothing to
  configure; on bare nginx/k3s, terminate TLS first.
- `.webmanifest` needs `application/manifest+json`. Caddy's Go mime table has no entry for it, so
  the `Caddyfile` sets it explicitly; on nginx add
  `types { application/manifest+json webmanifest; }`.
- Precached paths go through `$app/paths`, so a deployment under a subpath precaches `/subpath/…`.
- `vite dev` does not build the worker; check PWA behaviour with `pnpm build && pnpm preview`.
- The icons are generated from `static/icon.svg` / `static/icon-maskable.svg` (the seal from
  `App.svelte`, full-bleed teal `#008d80`, mark inside the maskable safe circle). Regenerate with:

  ```bash
  rsvg-convert -w 192 -h 192 static/icon.svg -o static/icon-192.png
  rsvg-convert -w 512 -h 512 static/icon.svg -o static/icon-512.png
  rsvg-convert -w 512 -h 512 static/icon-maskable.svg -o static/icon-maskable-512.png
  ```

## PKCS#12 support (no dependencies)

The container is parsed in the browser — no library, no server:

- **PBES2** (PBKDF2 + AES-CBC, what OpenSSL 3+/4 and Java 9+ write) and **legacy PBES1**
  (SHA-1 KDF + 3DES/RC2 — `src/lib/legacy-pbe.ts`, what the FNMT and Windows exporters write).
  A scheme outside those two is rejected with an actionable message instead of a half-parsed key.
- **BER, not just DER**: the envelopes the Windows/CryptoAPI exporter writes carry indefinite
  lengths and split every long string into chunks (`24 80 … 00 00`), including the implicit `[0]`
  of `encryptedContent`. The reader accepts both encodings.
- The private key is imported with `extractable: false` and `usages: ['sign']`, so its material
  cannot be read back out of the browser.
- The certificate that owns the key is identified by comparing public-key material (RSA modulus
  and exponent; EC public point) against each certificate of the container.
- The PKCS#12 MAC (RFC 7292 B.3) is verified when `MacData` is present: a mismatch is reported as a
  wrong PIN, and an unsupported MAC digest leaves the MAC unchecked rather than assumed.

## End-to-end smoke with a real keystore — #39

`scripts/smoke-real.sh` drives the whole loop — read the container, pick the signer, sign a document
with CAdES-BES, verify the produced `.csig` — through the app's own modules, for a **real keystore**
and for a **generated** one:

```bash
pnpm install                                   # the loop runs through src/lib, resolved by vitest
bash scripts/smoke-real.sh                     # real keystore + generated fixture
bash scripts/smoke-real.sh --no-generated      # only the real one
bash scripts/smoke-real.sh --cert work/certs/current-cert.p12 --pin-file .p12pass
```

- The real keystore and its PIN stay in gitignored files (`work/certs/*.p12`, `.p12pass`), are read
  at runtime and are never printed. A missing keystore or PIN file is skipped with a notice and
  **exit 0**, so the script is safe for anyone without a real certificate.
- Per keystore it asserts: `structure`, `contentDigest`, `signature`, `signerCert`, `certValidity`
  and `signingTime` are `pass`; the reported subject, issuer, serial and validity window equal the
  loaded certificate's; and the validity dates appear in the report output.
- Two oracles that are not this repository's code: `openssl cms -verify` over the produced `.csig`
  and the document, and `openssl x509 -subject -dates` over the signer certificate.
- The generated keystore is the self-signed RSA container of `src/test/fixtures.ts` (temporary,
  never committed). Its leaf is self-signed and its CA is not a bundled root, so `certChain: fail`
  is expected there — the signature checks are what it demonstrates.
- The report's `certChain` line is printed as it comes. Signing embeds only the signer certificate
  (#6), so even with the real FNMT keystore — whose container carries `AC FNMT Usuarios` — it reads
  `issuer certificate not included`, and verifying *your own* fresh `.csig` in the app shows a red
  *Trust chain* card. Embedding the chain is a separate change.
- `--cert work/certs/old-cert.p12` is expected to fail `certValidity`: that keystore expired
  2026-03-29 (issue #40 owns the expired-certificate path).

### The same loop in the UI (manual evidence)

The app opens in Spanish; the English labels are in brackets. Either keystore runs through it — the
real one (`work/certs/current-cert.p12`, PIN from `.p12pass`) or the generated fixture (PIN `1234`,
which this throwaway command materializes and prints the path of):

```bash
node --input-type=module -e "const { createPkcs12Fixtures } = await import('./src/test/fixtures.ts'); console.log(createPkcs12Fixtures().dir + '/modern.p12')"
```

1. `pnpm dev` and open `/`; the app starts in **Firmar** [Sign]. Open it from `localhost` or over
   https: away from a secure context the browser withholds WebCrypto entirely, and the app says
   so (banner + error) instead of pretending the file is broken.
2. In *2. Configura la firma* [2. Set up the signature], press **Agregar firma** [Add signature]
   and choose the `.p12`; type its PIN in **PIN de la firma** [PIN of the signature] and press
   **Guardar** [Save]: the container is sealed into this browser (IndexedDB), selected, and the
   modal closes on it. The file-shaped card fills in with the file name and the signer's `CN`:
   the certificate owning the private key is the one selected, and it is the only one that can
   sign (an expired one is flagged in red and still signs). Switching between **Firmar** and
   **Verificar** keeps that selection — the card just goes back to reading *Seleccionar firma*.
3. The card (**Seleccionar firma** [Select signature]) reopens *Firmas guardadas* [Saved
   signatures]: every sign ever added is a cell there, and the PIN is never stored — only the
   sealed container. **Agregar firma** inside the modal adds further `.p12` files the same way;
   a new one joins the grid instead of replacing the previous. Clicking a saved sign selects it
   and closes the modal; the first click of the visit asks that sign's PIN (the key cannot be
   used before it is unsealed), later clicks do not.
4. Drop the document in *1. Elige los documentos* [1. Choose the documents] and press **Firmar**
   [Sign]; the row turns to **Firmada** [Signed] and its **Descargar** [Download] saves the `.csig`
   (the same file is listed in the **Descargas** [Downloads] dialog, *Firmas generadas* [Produced
   signatures], with the signer and the file's SHA-256).
5. Switch to **Verificar** [Verify] and drop the `.csig`; a detached CAdES then asks for the
   original file under *Documento original* [Original document]. The report shows one card per check
   — *Estructura de la firma*, *Integridad del contenido*, *Firma criptográfica*, *Certificado del
   firmante*, *Vigencia del certificado*, *Cadena de confianza*, *Fecha de firma* — plus the signer
   identity (subject, issuer, serial) and its validity window.

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

Not in scope: inclusive-c14n `InclusiveNamespaces` PrefixList on the transforms (supported by the
canonicalizer, unused by the profile), the Detached/Enveloping variants, timestamps and long-term
validation (XAdES-T/XL).

## Batch signing queue — #9

N documents can be dropped at once; they become a queue (`src/lib/batch.ts`) signed **one at a
time** with the chosen certificate, so the UI thread stays free between files and nothing is
uploaded. Each document carries its own status (`pending` / `signing` / `signed` / `error`):

- **Resumable**: a document that fails is recorded as `error` with the engine's message and the
  run continues; the next run only retries what is not signed yet.
- **Cancellable**: while a run is in flight the primary action becomes *Cancel*, which stops the
  batch at the next document (`phase: 'cancelled'`); the one already in flight finishes and keeps
  its signature — WebCrypto cannot be aborted mid-call — and the next run resumes from there.
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

## PAdES B-B (`src/lib/pades.ts`) — #11

`signPdf(pdfBytes, options)` returns the signed document as the original bytes **plus an appended
incremental update** — the input is never rewritten or flattened:

- a `/Sig` field widget is added to the page `/Annots` and to the `/AcroForm` `/Fields`;
- its `/V` dictionary carries `/SubFilter /ETSI.CAdES.detached`, a `/ByteRange` covering everything
  except the `/Contents` hex string, and `/Contents` holding a CMS `SignedData` (detached, signed
  attributes content-type + signing-time + message-digest + signing-certificate-v2);
- RSA PKCS#1 v1.5 (`sha256WithRSAEncryption`) and ECDSA P-256/P-384 (`ecdsa-with-SHA256/384`) keys
  from `crypto.subtle`.

A **visible** signature (#45, `src/lib/pades-visible.ts`) adds an appearance stream inside the same
incremental update — so the drawn bytes are covered by `/ByteRange`, and the block can never
invalidate the signature:

- placed by page and corner (24 pt margin, 200×70 pt by default) or by explicit coordinates;
- the block carries an uploaded **PNG or JPEG** signature image (alpha honoured through `/SMask`),
  plus text lines: the signer's CN and the signing date, wrapped rather than cut.
- the sign panel exposes it as an off-by-default toggle with image upload, page number and corner
  picker; anything that is not a PNG/JPEG is rejected before the run starts.

Scope: PAdES baseline **B-B**, or **B-T** with `options.timeStamp` (RFC 3161, see below). Encrypted
inputs are rejected. The private key stays a WebCrypto `CryptoKey` in the browser.

Verification (`src/lib/pades.test.ts`) extracts the CMS and the byte-range content back out of the
produced PDF and verifies them with **openssl**, i.e. not with the signing code:

```bash
pnpm test        # golden fixture + RSA/ECDSA/visible signature, verified by `openssl cms -verify`
```

The signed output is also accepted by third-party tools (run against the committed fixture and
against freshly signed RSA, ECDSA P-256/P-384 and visible variants):

```bash
pdfsig -nocert test/fixtures/pades-b-b-signed.pdf   # "Total document signed" + "Signature is Valid."
qpdf --check test/fixtures/pades-b-b-signed.pdf     # exit 0, "No syntax or stream encoding errors"
pdftoppm -r 108 -png test/fixtures/pades-b-b-signed.pdf page   # page content still renders
```

`test/fixtures/` holds a one-page source PDF, a self-signed **test-only** RSA and EC key pair, and
the committed signed golden PDF. Regenerate the golden file after an intentional format change with
`PADES_WRITE_FIXTURE=1 pnpm test` (RSA PKCS#1 v1.5 plus a fixed signing time make the output
byte-reproducible).

## Real-certificate smoke test (`scripts/smoke-real.sh`) — #35

End-to-end proof with a **real** certificate, outside the test suite (`pnpm test` only ever uses
the throwaway fixture PKI in `test/fixtures/`):

```bash
bash scripts/smoke-real.sh [input.pdf]     # signs + verifies; exits 0 on success
```

It reads the PIN at runtime from `.p12pass` (repo root, `chmod 600`, **gitignored — never commit
it**), loads `work/certs/old-cert.p12` through the shipped PKCS#12 reader and PAdES signer, writes
the signed PDF to `work/smoke-real-signed.pdf` (both `work/` paths are gitignored) and then
verifies the signature twice:

- the app's own verifier (`src/lib/verify`) over the CMS read back out of the produced PDF;
- `openssl cms -verify` on the same CMS and byte-range content, as an independent oracle.

The real container is PBES1 (`pbeWithSHA1And3-KeyTripleDES-CBC`, SHA-1 MAC), which WebCrypto cannot
decrypt; the script re-wraps it as PBES2/AES-256 with `openssl pkcs12` **into a temp dir**
(`mktemp -d`, removed on exit, temp PEM encrypted with the same PIN) and signs with that. Needs
OpenSSL 3+ locally. Missing `.p12pass` or container ⇒ skip notice, exit 0, so CI stays green.

An expired signer certificate is reported (`certValidity`) but not fatal: the smoke test asserts the
cryptographic checks (`structure`, `contentDigest`, `signature`) plus the openssl cross-check.

## Trusted timestamps (RFC 3161) — #43

`src/lib/timestamp.ts` builds a `TimeStampReq`, POSTs it to the TSA
(`Content-Type: application/timestamp-query`) and checks the reply before anything is embedded: the
`PKIStatus` must be granted, and the token's `messageImprint` must be the SHA-256 of the bytes we
asked about. Pass `{ timeStamp: '<tsa url>' }` to `signCadesDetached` (`src/lib/cms.ts`) or to
`signPdf` (`src/lib/pades.ts`) — or type the address in the sign panel, which is empty (no
timestamp) by default — and the token is added as the `id-aa-timeStampToken` **unsigned** attribute:
CAdES-T, or PAdES B-T for a PDF. The signed attributes are untouched, so the signature value is the
same one a B-B/BES signature would carry.

The imprint is the signature value, never the document (ETSI EN 319 122-1 §5.4): the TSA learns a
SHA-256 digest and nothing else — no document, no key, no certificate.

**CORS / self-hosting.** Most public TSAs (the ACCV one AutoFirma ships with included) send no CORS
headers, so a browser cannot POST to them directly. Nothing else is required for the core feature;
for a deployment that wants a specific TSA, put a same-origin path in front of it and type that path
in the panel. With Caddy, in the `Caddyfile` next to the app's `file_server`:

```
handle_path /tsa/* {
	reverse_proxy http://tss.accv.es:8318
}
```

nginx equivalent:

```
location /tsa/ {
	proxy_pass http://tss.accv.es:8318/;
	proxy_set_header Content-Type application/timestamp-query;
}
```

Tests never reach a TSA: `fetch` is stubbed and a throwaway `openssl ts` instance (fixture
certificate + config) answers with a real `TimeStampResp`, whose token and embedding are then
verified with `openssl ts -verify`.

## Status

Feature-by-feature comparison against the original desktop AutoFirma 1.9 — what is implemented,
what is deliberately impossible in a browser and what is still a gap — is in
[docs/PARITY.md](docs/PARITY.md).

Document selection, PKCS#12 certificate loading (#5), CAdES-BES signing (#6) and XAdES-EPES XML
signing (#7) work, and both formats run through the batch queue (#9): drop N files, sign them
sequentially and download one ZIP with the signed documents and `manifest.json`. The `.csig`
embeds only the signer certificate, not its chain.

PAdES B-B (#11) runs in the app too, verified against `openssl`, `pdfsig` and `qpdf`: pick PAdES-B-B,
optionally switch on the visible block (#45: signature image, page and corner), and the signed PDF
lands in the same batch queue and ZIP as the XML formats. Signatures can carry a trusted RFC 3161
timestamp (CAdES-T, PAdES B-T) when a TSA address is given (#43).

OOXML signing (#10) exists as a library (`src/lib/ooxml.ts`): it adds
`_xmlsignatures/sig-<uuid>.xml` (XAdES v1.3.2 EPES with MDSSI `SignatureTime`), the
`origin.sigs` origin part and its relationships, and leaves every other part byte-identical.
The UI does not call it yet — that wiring needs the certificate flow from #5.
