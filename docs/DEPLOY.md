# Deploy (Cloudflare Pages, git sync)

Static build, zero backend. Signing runs in the browser; Pages serves files only.

## Dashboard setup (once, manual)

1. Workers & Pages → Create → Connect to Git → select `Administracion-Abierta/AutoFirme`.
2. Production branch: `main`. Preview branch: `develop` (automatic preview URLs per push/PR).
3. Build settings — Framework preset `SvelteKit`, or manual:
   - Build command: `pnpm install --frozen-lockfile && pnpm build`
   - Output directory: `build`
   - Node version: `22` (pinned by `.nvmrc`; pnpm 11 comes from `package.json` `packageManager`)
4. No environment variables, no bindings, no Functions. The app needs none.

## Conventions

- `static/_headers` ships immutable caching for `/_app/immutable/*` and icons; do not
  cache `/`, `service-worker.js` or the manifest beyond revalidation — installs and
  updates depend on fresh copies.
- SPA fallback is `build/404.html` (`adapter-static` fallback). Deep entries
  (`/?entry=sign`, `/?op=sign…`, `/?shared=1`) all resolve to it offline/online.
- After deploy: open the production URL on Chrome Android, confirm the install prompt,
  share a PDF to the app, and check the sign flow opens with the file preloaded.

## Limits

- `share_target` POST bodies never reach Pages: the service worker intercepts them and
  redirects into the sign flow (see `src/service-worker.ts`).
- Transparent AutoScript interception is extension-only; portals reach the PWA through
  share, file-open, or the deep-link contract (`docs/PWA-SIGNING.md`).
