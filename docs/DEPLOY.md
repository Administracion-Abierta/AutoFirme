# Release & distribution (GitHub Releases is the only channel)

No hosted product, no deploy. Versions ship as release assets; milestones and
`release-please` own the numbering. This file describes the release story —
for the retired Worker deploy, see the note at the bottom.

## Channel

**GitHub Releases is the only distribution channel.** No apt/dnf repo, no docs
site, no demo host, no third-party store as primary (locked in
`docs/DESKTOP-ROADMAP.md` §1, decision 9).

## What a release contains

| Asset | Produce | Use |
|---|---|---|
| `autofirme-web-<tag>.tar.gz` | release workflow packs `build/` | Static site, archived for reproducibility |
| Desktop installer per platform (Linux first) | Tauri bundler (see `docs/DESKTOP-ROADMAP.md` phases) | The full product |
| `SHA256SUMS` over all assets | release workflow | Integrity check before install |
| Minisign signatures | desktop updater path | Trust without a store |

The release workflow (`.github/workflows/release.yml`) packs the site tarball
from `build/`. The browser extension is built and released from its own repo
(github.com/Administracion-Abierta/AutoFirme-Extension), which packages it
**twice from scratch** and diffs the `SHA256SUMS`: a change that breaks
packaging reproducibility fails the release before upload.

## Versioning

Automatic via Release Please: conventional commits merged into `main` produce a
release PR that bumps the product version in `package.json`,
`src-tauri/tauri.conf.json`, `src-tauri/Cargo.toml` and the shell's entry in
`src-tauri/Cargo.lock`, plus `CHANGELOG.md`. Never edit those versions by hand.
`pnpm check:versions` (also a CI gate) fails when they drift or when a release
tag does not match — see `scripts/check-versions.mjs`. The extension repo
versions its own `package.json` + `extension/manifest.json` the same way.

## Signing keys

- `SHA256SUMS` ships with every release; verify before installing.
- Minisign signatures cover the desktop updater path (roadmap: Apple Developer
  Program purchase rides the P7 budget; Keychain provider mandatory).
- The extension stays **unsigned**: Chrome needs Developer Mode for a manual
  install until a store listing exists (see the extension repo).

## Milestones

Release train follows the roadmap phases (`docs/DESKTOP-ROADMAP.md` §4):
Linux desktop first, Windows second, macOS last (notarization is the long pole),
Firefox `userScripts` (#117) after the Windows MSI exists.

## Retired: Cloudflare Worker deploy (P8)

The SPA used to ship as an assets-only Cloudflare Worker (`autofirme`,
`wrangler.jsonc` → `./build`, `404.html` app shell). That deploy is retired, the
scaffolding is gone (`wrangler.jsonc`, `Caddyfile`, `Containerfile` were deleted
by #19): no website is published anymore. Do not re-add hosting config or
hosting docs (`AGENTS.md` rule 4).
