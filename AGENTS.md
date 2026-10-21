# AGENTS.md — AutoFirme → desktop app + extension

Origin: uni task (Computer Basics) — reverse-engineer AutoFirma v1.9 installers,
recover the Java source. **That phase is DONE.** The jadx trees
(`decompiled/`, `decompiled-configurador/`, 11,487 + 4,811 files) left the tracked
tree in #26, where they cost 180 MB of text per clone; they live on the annotated tag
`reference/decompile-1.9` now (`git checkout reference/decompile-1.9 -- decompiled
decompiled-configurador`). The analysis itself is in `docs/`.

## Current goal

Two products, one engine. **AutoFirme Desktop** (Tauri v2 app, Linux first) and the
**AutoFirme Extension** (Chrome/Edge MV3, Firefox later) share the signing engine in
`src/lib` (CAdES/XAdES/PAdES over X.509 certificates). No website: the hosted SPA is
retired; `build/` survives only as the extension's options page and the dev harness.
Product decisions are locked in `docs/DESKTOP-ROADMAP.md` §1.

- **Desktop = the full product.** Signing UI, native keys (OS stores, PKCS#11/DNIe),
  `afirma://` links, local sign server, its own MCP sidecar. The `KeyProvider` seam
  lets the engine assemble signatures without ever learning where the key lives.
- **Extension = the portal product.** Ships standalone: in-extension PKCS#12 signing
  plus portal `AutoScript` interception, no desktop dependency. The optional bridge
  between the two is a **native messaging host**, never a localhost API.
- **Reference, don't port:** AutoFirma 1.9 — https://github.com/ctt-gob-es/clienteafirma
  (GPL-2+/EUPL-1.1), or the tag above for the exact 1.9 sources — is READ-ONLY
  reference for understanding formats and flows. Derive new code (TypeScript or Rust)
  from the architecture doc (`docs/architecture.md`), not line-by-line translation,
  and never from decompiled Java. Keep the new code clean-roomed from it and license
  the app GPL-compatible.

## Hard rules

1. **NEVER commit anything in `original/`.** Teacher's installers (~270 MB zips:
   EXE/deb/rpm). `.gitignore` excludes it — keep it that way. Never `git add -f`,
   never `git add -A` blindly.
2. Never commit `extracted/` or `work/` (scratch, gitignored). Binaries stay out of
   the tree; reference them by path.
3. Never commit a `decompiled*` tree: the reverse-engineering phase is closed and the
   tag `reference/decompile-1.9` is the record. Re-generate with jadx if ever needed
   (command in `docs/REVERSING-NOTES.md` §7) — don't edit the trees, and never derive
   new Rust code from them; `docs/architecture.md` is the only source.
4. No new web deploy surface. No wrangler/worker config, no hosting docs, no landing
   page: GitHub Releases is the only distribution channel (`docs/DEPLOY.md`). If a
   deploy scaffold reappears (`adapter-cloudflare`, a Worker entrypoint), revert it.
5. The app lives at the repo root — SvelteKit, Svelte 5, Tailwind v4, TypeScript.
   Static adapter: the build feeds the desktop webview, the extension options page,
   and the dev harness — never a published site.
6. Commit but do not push unless the user asks.

## Layout

```
original/            # teacher's installers — INPUT, gitignored, read-only
extracted/           # unpacked installers — scratch, gitignored
work/                # scratch
decompiled*/         # jadx output — untracked reference, gitignored (tag reference/decompile-1.9)
docs/                # architecture.md (signing-core map), DESKTOP-ROADMAP.md, REVERSING-NOTES.md
src/, static/        # the engine + UI — SvelteKit (Svelte 5 + Tailwind v4), feeds desktop + extension
src-tauri/           # desktop shell (Tauri v2) — Rust core, KeyProvider backends
extension/           # MV3 extension — portal bridge, options page mounts the same UI
mcp/                 # MCP server (stdio + Streamable HTTP)
test/                # fixtures + cross-check scripts (openssl / xmlsec1)
build/               # generated: extension options page + dev harness (`pnpm preview`) — never deployed
```

Build/test from the repo root (pnpm is canonical; the lockfile and `Containerfile` use it):
`pnpm install && pnpm test && pnpm build`.

## Agent workflow

- Branches: `develop` is the integration branch (PRs target it), `main` is what gets
  released; `main` only receives merges from `develop`.
- Work on branches; `Refs #N` only in commits (never closing keywords).
- Issues on GitHub (github.com/Administracion-Abierta/AutoFirme) drive the fleet (bullet).
- Big problems get split into ≤~400-line deliverables per issue; PRs to `main` need CI green
  (`checks`, `e2e`, `secret-scan`, `dependency-audit`), 1 review, and `CODEOWNERS` sign-off on
  the crypto paths.
- Versioning is automatic: conventional commits merged into `main` produce a Release Please
  PR that bumps the single product version in `package.json` **and**
  `extension/manifest.json` (Chrome reads the manifest one) plus `CHANGELOG.md`. Never edit
  those versions by hand; `pnpm check:versions` (in CI) fails when they drift or when a
  release tag does not match.
- Crypto code MUST ship with a verification check (e.g. generated signature verified
  by `openssl cms -verify` in a script/test) before the issue can move to review.
- UI: clean, modern, accessible; ES/EN copy. Prettier than AutoFirma is a requirement,
  not a joke.
