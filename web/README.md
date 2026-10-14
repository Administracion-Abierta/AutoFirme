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
| `src/lib/components/` | `FileDrop`, `FileCard`, `CertPicker`, `BatchQueue`, `ResultCard` |
| `src/lib/sign.ts` | Format list and sign-flow types (the seam for #5–#11) |
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

## Status

Shell only: document selection works, the certificate picker is a placeholder and signing is
disabled until #5 (PKCS#12) lands. Signing engines follow in #6 (CAdES), #7 (XAdES), #11 (PAdES).

The batch layer is in place: N dropped files become a queue signed sequentially with per-document
status and a ZIP + `manifest.json` download. It takes the signature engine as a parameter, so it
runs as soon as #6 hands it one — until then the run button stays disabled and says so.
