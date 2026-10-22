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
| Extension ZIP + `SHA256SUMS` | `pnpm package:extension` | Load unpacked / store install |
| `autofirme-web-<tag>.tar.gz` | release workflow packs `build/` | Extension options page bundle, archived for reproducibility |
| Desktop installer per platform (Linux first) | Tauri bundler (see `docs/DESKTOP-ROADMAP.md` phases) | The full product |
| `SHA256SUMS` over all assets | release workflow | Integrity check before install |
| Minisign signatures | desktop updater path | Trust without a store |

The release workflow (`.github/workflows/release.yml`) builds the extension
package **twice from scratch** and diffs the `SHA256SUMS`: a change that breaks
packaging reproducibility fails the release before upload.

## Versioning

Automatic via Release Please: conventional commits merged into `main` produce a
release PR that bumps the single product version in `package.json` **and**
`extension/manifest.json` (Chrome reads the manifest one) plus `CHANGELOG.md`.
Never edit those versions by hand. `pnpm check:versions` (also a CI gate) fails
when they drift or when a release tag does not match — see
`scripts/check-versions.mjs`.

## Signing keys

- `SHA256SUMS` ships with every release; verify before installing.
- Minisign signatures cover the desktop updater path (roadmap: Apple Developer
  Program purchase rides the P7 budget; Keychain provider mandatory).
- The extension stays **unsigned**: Chrome needs Developer Mode for a manual
  install until a store listing exists.

## Milestones

Release train follows the roadmap phases (`docs/DESKTOP-ROADMAP.md` §4):
Linux desktop first, Windows second, macOS last (notarization is the long pole),
Firefox `userScripts` (#117) after the Windows MSI exists.

## Retired: Cloudflare Worker deploy (P8)

The SPA used to ship as an assets-only Cloudflare Worker (`autofirme`,
`wrangler.jsonc` → `./build`, `404.html` app shell). That deploy is retired:
no website is published anymore. If Worker scaffolding (`wrangler.jsonc`,
`adapter-cloudflare`, a Worker entrypoint) still exists in the tree, it belongs
to code-deletion issue #19, not to this doc. Do not re-add hosting docs.
