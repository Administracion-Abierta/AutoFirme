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
| `src/lib/c14n.ts` | XML Canonicalization 1.0 (inclusive + exclusive), used by the signature engines |
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
pnpm test         # vitest: App smoke test + OOXML signing round-trip
                  #   (`openssl` and `xmllint`, when installed, cross-check the signature and c14n;
                  #    `soffice`, when installed, converts the signed docx to PDF — package smoke)
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

## Status

Shell only: document selection works, the certificate picker is a placeholder and signing is
disabled until #5 (PKCS#12) lands. Signing engines follow in #6 (CAdES), #7 (XAdES), #11 (PAdES).

OOXML signing (#10) exists as a library (`src/lib/ooxml.ts`): it adds
`_xmlsignatures/sig-<uuid>.xml` (XAdES v1.3.2 EPES with MDSSI `SignatureTime`), the
`origin.sigs` origin part and its relationships, and leaves every other part byte-identical.
The UI does not call it yet — that wiring needs the certificate flow from #5.
