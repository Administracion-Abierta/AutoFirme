# Deploy (static build, any host)

Static build, zero backend. Signing runs in the browser; the host serves files only.
There is no Worker and no edge code in this repository: `pnpm build` emits `build/` and any
nginx/Caddy/static host serves it. The only container here is `mcp/Containerfile`, for the
optional MCP server.

## What the host must do

- **SPA fallback.** The app has deep entries (`/?entry=sign`, `/?op=sign…`, `/?shared=1`)
  and they boot from `build/404.html`, which is the app shell. Unknown paths must
  resolve to it:

  ```nginx
  root /srv/autofirme/build;
  location / { try_files $uri $uri/ /404.html; }
  ```

- **HTTPS.** Required for the service worker and for the PWA install prompt.
- **Caching.** `static/_headers` (shipped into `build/`) sets immutable caching for
  `/_app/immutable/*` and icons. Do not cache `/`, `service-worker.js` or
  `manifest.webmanifest` beyond revalidation — installs and updates depend on fresh copies.
  Translate the file to the host's own syntax if it is not Cloudflare.

## Releases

- `main` produces a versioned release through Release Please (`release.yml`); its
  `artifacts` job uploads the extension zip, `SHA256SUMS` and the site tarball.
- `develop` publishes a rolling pre-release tagged `dev-<short-sha>` with the extension zip
  and `SHA256SUMS`, and uploads the built `build/` directory as a workflow artifact
  (`release-develop.yml`).
- The desktop shell (`src-tauri/`) is checked by CI but not packaged: the app ships as a
  web app.

## After a deploy

Open the production URL on Chrome Android, confirm the install prompt, share a PDF to the
app, and check the sign flow opens with the file preloaded.

## Limits

- `share_target` POST bodies never reach the host: the service worker intercepts them and
  redirects into the sign flow (see `src/service-worker.ts`).
- Transparent AutoScript interception is extension-only; portals reach the PWA through
  share, file-open, or the deep-link contract (`docs/PWA-SIGNING.md`).
