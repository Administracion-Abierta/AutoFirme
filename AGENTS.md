# AGENTS.md — AutoFirme → AutoFirma Web

Origin: uni task (Computer Basics) — reverse-engineer AutoFirma v1.9 installers,
recover the Java source. **That phase is DONE** (11,487 files under `decompiled/`).

## Current goal

Build **AutoFirma Web**: a client-side rendered web app that replicates AutoFirma's
core (sign documents with X.509 certificates, verify signatures) — prettier, online,
self-hostable by anyone.

- **Client-side rendered SPA.** Signing/verification runs in the browser
  (WebCrypto API + WASM where needed). No signing server: private keys never
  leave the client.
- **Self-host first:** static build deployable on any nginx/k3s/static host,
  zero backend required for core flows.
- **Reference, don't port:** `decompiled/` is READ-ONLY reference for understanding
  formats and flows. Write new TypeScript from the architecture doc (`docs/architecture.md`),
  not line-by-line translation. AutoFirma is GPL — keep the new code clean-roomed
  from it and license the app GPL-compatible.

## Hard rules

1. **NEVER commit anything in `original/`.** Teacher's installers (~270 MB zips:
   EXE/deb/rpm). `.gitignore` excludes it — keep it that way. Never `git add -f`,
   never `git add -A` blindly.
2. Never commit `extracted/` or `work/` (scratch, gitignored). Binaries stay out of
   the tree; reference them by path.
3. `decompiled/**/*.java` IS tracked (work product / reference). Don't edit files there.
4. New app lives in `webapp/` — SvelteKit, Svelte 5, Tailwind v4, TypeScript.
   Static adapter for deploy; node adapter only if a real backend need appears.
5. Commit but do not push unless the user asks.

## Layout

```
original/            # teacher's installers — INPUT, gitignored, read-only
extracted/           # unpacked installers — scratch, gitignored
decompiled/          # jadx output — TRACKED, read-only reference
docs/                # architecture.md (signing-core map), REVERSING-NOTES.md
webapp/              # the product — SvelteKit SPA (Svelte 5 + Tailwind v4)
```

## Agent workflow

- Work on branches; `Refs #N` only in commits (never closing keywords).
- Issues on Forgejo (git.eduard3v.com/EduarD3V/AutoFirme) drive the fleet (bullet).
  Big problems get split into ≤~400-line deliverables per issue.
- Crypto code MUST ship with a verification check (e.g. generated signature verified
  by `openssl cms -verify` in a script/test) before the issue can move to review.
- UI: clean, modern, accessible; ES/EN copy. Prettier than AutoFirma is a requirement,
  not a joke.
