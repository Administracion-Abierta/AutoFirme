# Deploy (Cloudflare Worker `autofirme`, static assets)

Static build, zero backend. Signing runs in the browser; the Worker serves files
only (assets-only Worker, no `main` entrypoint, no edge code).

Live URL: https://autofirme.eduard3v.workers.dev

## How it deploys

- Pushes to `main` run CI (`checks` job) and then the `deploy` job:
  `pnpm install --frozen-lockfile && pnpm build && pnpm exec wrangler deploy`.
- PRs and `develop` never touch the production Worker (`develop` ships the dev
  pre-release instead — see `release-develop.yml`).
- `wrangler.jsonc` is the contract: `name: autofirme`, `assets.directory: ./build`,
  `not_found_handling: 404-page` (deep entries `/?entry=sign`, `/?op=sign…`,
  `/?shared=1` boot from `404.html`, which is the app shell).

## Setup (once, manual)

1. Cloudflare dashboard → My Profile → API Tokens → create a token with
   `Account:Read` + `Workers Scripts:Edit` (narrowest scope that deploys).
2. GitHub repo → Settings → Secrets and variables → Actions → add:
   - `CLOUDFLARE_API_TOKEN` (the token value)
   - `CLOUDFLARE_ACCOUNT_ID` (`d56347ecdd18776f5fae3b468ce5134a`)
3. No environment variables, no bindings, no Functions. The app needs none.

## Conventions

- Do NOT let `wrangler deploy` re-scaffold the project: if it offers `sv add` /
  adapter-cloudflare / a `main` entrypoint, decline — this repo stays on
  `adapter-static` with an assets-only config. If scaffolding ever lands,
  revert it (`git checkout -- package.json pnpm-lock.yaml tsconfig.json vite.config.ts`
  and delete `src/app.d.ts`, `static/.assetsignore`).
- `static/_headers` ships immutable caching for `/_app/immutable/*` and icons; do not
  cache `/`, `service-worker.js` or the manifest beyond revalidation — installs and
  updates depend on fresh copies.
- After deploy: open the production URL on Chrome Android, confirm the install prompt,
  share a PDF to the app, and check the sign flow opens with the file preloaded.

## Limits

- `share_target` POST bodies never reach the Worker runtime: the service worker
  intercepts them and redirects into the sign flow (see `src/service-worker.ts`).
- Transparent AutoScript interception is extension-only; portals reach the PWA through
  share, file-open, or the deep-link contract (`docs/PWA-SIGNING.md`).
