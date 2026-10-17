# PWA signing on Chrome Android

Installable via Chrome Android today: `static/manifest.webmanifest` (name, icons,
`shortcuts`, `share_target`, `file_handlers`), `src/service-worker.ts` (offline shell,
never caches runtime output), install prompt in `PwaControls.svelte`. Served over HTTPS
by Cloudflare Pages (`docs/DEPLOY.md`).

Signing only — no verify surface on any entry point.

## What the platform forbids

A PWA cannot intercept portal calls the way the extension does (host permissions +
content script, `docs/CHROME-EXTENSION.md` §3). No third-party DOM injection, no local
socket server, no `autofirma://` scheme (`protocol_handlers` allow only `web+…` and
safelisted schemes). Transparent AutoScript capture stays extension-only. Every PWA
entry below preloads data; the user still picks a certificate and confirms the sign.

## Entry points

| Entry | Manifest | Handling |
|---|---|---|
| App shortcut “Firmar” | `shortcuts` → `/?entry=sign` | Opens step 2 directly |
| Android share sheet (text/url) | `share_target` GET params | `consumeEntry` reads stash/params, preloads |
| Android share sheet (files) | `share_target` POST `/?shared=1` | SW `stashShare` → IndexedDB → 303 to `/?entry=sign&share=<nonce>` → `takeShare` reads once and deletes |
| OS “open with” (PDF/XML) | `file_handlers` | `launchQueue` consumer appends files |
| Portal link-out | none needed | `/?op=sign&format=cades\|xades\|pades&data_b64=…&name=…&return_url=https://…` |

Deep-link rules (`src/lib/pwa-entry.ts`): `format` must be supported; `data_b64` must be
valid base64 ≤ 48 KiB (larger documents go through share/file); `return_url` must be
HTTPS or it is rejected. The parsed request is shown on step 2 with its destination;
after the user-confirmed run, step 3 offers an explicit “Devolver al portal” button
that navigates to `return_url` with `op=signed&format&signature_b64&algorithm&sha256`.
Same-tab navigation, destination visible before the click — never automatic.

## Security invariants

- Entry preloads data only; cert pick + Firmar click always required.
- Share stash lives in IndexedDB, keyed by a one-time nonce, deleted on read; neither
  inputs nor signatures ever touch a Cache (`cache.put` appears nowhere).
- `sanitizeReturnUrl` rejects non-HTTPS; no silent redirect.

## Manual check (real device)

Install prompt → standalone launch → share PDF → sign flow preloaded → sign with test
cert → ZIP downloads. Deep link: craft a small `op=sign` URL, confirm preload banner,
sign, return to portal, compare `sha256` with an independent hash. Extension remains
the path for portals that will not link out.
