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
| `src/lib/sign.ts` | Format list and sign-flow types (the seam for #5–#11) |
| `src/lib/pades.ts` | PAdES B-B signing: CMS `SignedData` + PDF byte-range incremental update |
| `src/lib/der.ts` | Minimal DER writer/reader for the CMS structures |
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

## PAdES B-B (`src/lib/pades.ts`)

`signPdf(pdfBytes, options)` returns the signed document as the original bytes **plus an appended
incremental update** — the input is never rewritten or flattened:

- a `/Sig` field widget is added to the page `/Annots` and to the `/AcroForm` `/Fields`;
- its `/V` dictionary carries `/SubFilter /ETSI.CAdES.detached`, a `/ByteRange` covering everything
  except the `/Contents` hex string, and `/Contents` holding a CMS `SignedData` (detached, signed
  attributes content-type + signing-time + message-digest + signing-certificate-v2);
- RSA PKCS#1 v1.5 (`sha256WithRSAEncryption`) and ECDSA P-256/P-384 (`ecdsa-with-SHA256/384`) keys
  from `crypto.subtle`; a visible signature also gets an appearance stream.

Scope: PAdES baseline **B-B** only — no timestamp, no DSS/LTV (those are later issues). Encrypted
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

## Status

Shell + PAdES: document selection works, the PAdES engine signs PDFs, and the certificate picker is
still a placeholder — signing from the UI is enabled once #5 (PKCS#12) lands. Remaining formats:
#6 (CAdES), #7 (XAdES).
