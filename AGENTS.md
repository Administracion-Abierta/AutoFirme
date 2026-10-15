# AGENTS.md — AutoFirme → AutoFirma Web

Origin: uni task (Computer Basics) — reverse-engineer AutoFirma v1.9 installers,
recover the Java source. **That phase is DONE.** The jadx trees
(`decompiled/`, `decompiled-configurador/`, 11,487 + 4,811 files) left the tracked
tree in #26, where they cost 180 MB of text per clone; they live on the annotated tag
`reference/decompile-1.9` now (`git checkout reference/decompile-1.9 -- decompiled
decompiled-configurador`). The analysis itself is in `docs/`.

## Current goal

Build **AutoFirma Web**: a client-side rendered web app that replicates AutoFirma's
core (sign documents with X.509 certificates, verify signatures) — prettier, online,
self-hostable by anyone.

- **Client-side rendered SPA.** Signing/verification runs in the browser
  (WebCrypto API + WASM where needed). No signing server: private keys never
  leave the client.
- **Self-host first:** static build deployable on any nginx/k3s/static host,
  zero backend required for core flows.
- **Reference, don't port:** AutoFirma 1.9 — https://github.com/ctt-gob-es/clienteafirma
  (GPL-2+/EUPL-1.1), or the tag above for the exact 1.9 sources — is READ-ONLY
  reference for understanding formats and flows. Write new TypeScript from the
  architecture doc (`docs/architecture.md`), not line-by-line translation. Keep the
  new code clean-roomed from it and license the app GPL-compatible.

## Hard rules

1. **NEVER commit anything in `original/`.** Teacher's installers (~270 MB zips:
   EXE/deb/rpm). `.gitignore` excludes it — keep it that way. Never `git add -f`,
   never `git add -A` blindly.
2. Never commit `extracted/` or `work/` (scratch, gitignored). Binaries stay out of
   the tree; reference them by path.
3. Never commit a `decompiled*` tree: the reverse-engineering phase is closed and the
   tag `reference/decompile-1.9` is the record. Re-generate with jadx if ever needed
   (command in `docs/REVERSING-NOTES.md` §7) — don't edit the trees.
4. The app lives at the repo root — SvelteKit, Svelte 5, Tailwind v4, TypeScript.
   Static adapter for deploy; node adapter only if a real backend need appears.
5. Commit but do not push unless the user asks.

## Layout

```
original/            # teacher's installers — INPUT, gitignored, read-only
extracted/           # unpacked installers — scratch, gitignored
work/                # scratch
decompiled*/         # jadx output — untracked reference, gitignored (tag reference/decompile-1.9)
docs/                # architecture.md (signing-core map), REVERSING-NOTES.md
src/, static/        # the product — SvelteKit SPA (Svelte 5 + Tailwind v4)
test/                # fixtures + cross-check scripts (openssl / xmlsec1)
Containerfile        # multi-stage build: node build -> caddy serve (context = repo root)
```

Build/test from the repo root (pnpm is canonical; the lockfile and `Containerfile` use it):
`pnpm install && pnpm test && pnpm build`.

## Agent workflow

- Work on branches; `Refs #N` only in commits (never closing keywords).
- Issues on GitHub (github.com/Administracion-Abierta/AutoFirme) drive the fleet (bullet).
  Big problems get split into ≤~400-line deliverables per issue; PRs to `main` need CI green
  (`checks`, `e2e`, `secret-scan`, `dependency-audit`), 1 review, and `CODEOWNERS` sign-off on
  the crypto paths.
- Crypto code MUST ship with a verification check (e.g. generated signature verified
  by `openssl cms -verify` in a script/test) before the issue can move to review.
- UI: clean, modern, accessible; ES/EN copy. Prettier than AutoFirma is a requirement,
  not a joke.
