# Deploy (Cloudflare Pages, git sync)

Static build, zero backend. Signing runs in the browser; Pages serves files only.

## Dashboard setup (once, manual)

1. Workers & Pages → Create → Connect to Git → select `Administracion-Abierta/AutoFirme`.
2. Production branch: `main`. Preview branch: `develop` (automatic preview URLs per push/PR).
3. Build settings — Framework preset `SvelteKit`, or manual:
   - Build command: `pnpm install --frozen-lockfile && pnpm build`
   - Output directory: `build`
   - Deploy command: **empty / none**. Pages serves `build/` directly after building.
   - Node version: `22.22.0` (pinned by `.nvmrc`; pnpm 11 needs Node ≥ 22.13, so never pin below that)
4. No environment variables, no bindings, no Functions. The app needs none.

> NEVER set the deploy command to `npx wrangler deploy`. Wrangler detects SvelteKit and
> tries to convert the project into a Worker (`sv add`, `wrangler.jsonc` pointing at
> `.svelte-kit/cloudflare/_worker.js`), which fails in CI on pnpm ignored-build-scripts
> and is the wrong target anyway: this app is static files, no edge code. If that command
> already ran, check the Cloudflare dashboard for a stray `autofirme` Worker and delete it.

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
