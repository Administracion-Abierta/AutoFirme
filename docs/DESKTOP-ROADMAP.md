# AutoFirme Desktop — features roadmap

Status: **approved plan, not started**. No code exists yet. This document supersedes
`docs/PIVOT-STUDY.md` §5 (desktop rejected) and the "four shells" model in `README.md`.

## 1. Decisions (locked 2026-10-20)

| # | Decision | Consequence |
|---|---|---|
| 1 | **No web product.** The Cloudflare-hosted SPA stops being a published app. | The Worker deploy is retired in P8. `build/` survives as the extension's options page and as the dev harness. `AGENTS.md`'s "wrangler stays assets-only" rule becomes vestigial and must be replaced. |
| 2 | OS order: **Linux → Windows → macOS**. | v0.1 is Linux-only. Windows is the second target, macOS last (notarization + Developer Program are the long poles). |
| 3 | **Desktop and extension are separate products.** Extension ships standalone: in-extension PKCS#12 signing and its own portal interception, no desktop dependency. The optional bridge between them is a **native messaging host**, never a localhost API. | No `triphase.ts` retarget. The extension keeps its own MCP bridge (agents → browser-confirmed signature). The desktop app gets its own MCP. They share the engine source, not a runtime. |
| 4 | Certificate store: **OS keystore + PKCS#11 authoritative; PKCS#12 import kept; IndexedDB demoted to migration-only.** | One-time migration path; every cert shows its source. Divergence between stores becomes a bug class, not a feature. |
| 5 | **P2P slice 1 = LAN + Tailscale only**, no relays. | Nothing to operate. File-out fallback always present. |
| 6 | **No hosted presence at all.** No docs site, no landing page, no demo host. | Docs live in the repo. Nothing to deploy, nothing to keep green. |
| 7 | **Firefox after Windows.** | Firefox `userScripts` (#18) stays `fleet:hold` until the Windows MSI exists. |
| 8 | **macOS committed from the start, notarization included.** | Apple Developer Program purchase moves into the P7 budget, not "someday". Keychain provider is mandatory, not optional. |
| 9 | **GitHub Releases is the only distribution channel.** | No apt/dnf repo, no Forgejo pages, no third-party store as primary. Packages are attached assets + `SHA256SUMS`, minisign signatures. |

## 2. Product shape

One engine (`src/lib`, 109 files), **two products**, no website:

| Product | Ships as | Owns |
|---|---|---|
| **AutoFirme Desktop** | Tauri v2 app (Linux first) | signing UI, native keys (cards, OS stores, PKCS#11), `afirma://` + local sign server, MCP (stdio + HTTP), P2P receive/send |
| **AutoFirme Extension** | MV3 (Chrome/Edge), Firefox later | portal AutoScript interception, in-extension PKCS#12 signing, its own MCP bridge |

Shared, not coupled: `src/lib` (engine), test fixtures, `docs/`.

Explicitly gone: mobile, zero-install verification, link sharing. A signed file received by
someone without the app cannot be verified by them. **"Verify before open" is therefore the
core UX of every flow**, not a detail.

## 3. Architecture

```
AutoFirme Desktop (Tauri v2)
├─ Webview (WebKitGTK → WebView2 → WKWebView): SvelteKit static build, SPA only
│   └─ engine (src/lib) ── KeyProvider seam ──┬─ p12
│                                             ├─ pkcs11 / smartcard (pcsc + cryptoki)
│                                             ├─ windows-my / keychain / nss
│                                             └─ (future) remote QES
├─ Rust core
│   ├─ key layer (pcsc 2.9, cryptoki/pkcs11 0.5)
│   ├─ local API: axum on 127.0.0.1, TLS, OS-trusted cert
│   ├─ deep link: afirma:// (register only if unclaimed)
│   ├─ native messaging host: dev.eduard3v.autofirme
│   ├─ MCP supervisor: spawns bundled mcp sidecar (stdio | HTTP + bearer)
│   └─ updater (minisign), single-instance, file associations
└─ P2P (later): quinn QUIC, mdns-sd, age envelope, ASiC container
```

The `KeyProvider` seam is the load-bearing refactor; see §5.1. The engine assembles
CAdES/XAdES/PAdES and never learns where the key lives.

## 4. Phases

| Phase | Features | Effort | Exit criteria |
|---|---|---|---|
| **P0** Shell + parity gate | Tauri v2 scaffold, secure-context resolution, 12-item engine smoke, CI `cargo check` + headless smoke, **plus audit fixes §9.1–9.6: delete wrangler/Containerfile/Caddyfile + deploy jobs, pin Node, disable the service worker, add the `mcp` CI job, extend `check-versions`** | 10–14 d | all 12 green on Linux; one signed PDF validates in Acrobat; CI has no deploy job and no untested project |
| **P1** Key layer | `KeyProvider` seam, PKCS#11 modules, OS stores, smartcard autodetect + native PIN, hot-plug | 15–20 d | DNIe 3.x signs CAdES with PIN prompted natively, key non-exportable |
| **P2** Portal + native messaging | `afirma://` registration, local sign server (protocol 3 + 4), native messaging host + manifests, extension optional upgrade | 13–17 d | real sede completes a signature end to end, twice: extension-only, and via desktop |
| **P3** Format parity | OOXML wired, XAdES-T, FacturaE 3.0/3.1/3.2.2, co-sign/counter-sign, ASiC-S, XAdES detached/enveloping, ODF, XMLDSig | 25–30 d | PARITY P0/P1 rows green |
| **P4** Validation depth | TSL/LOTL anchors, OCSP/CRL (native, no CORS), PAdES form-mod + PSA, LTV/DSS, level detection + validity taxonomy | 20–28 d | EU DSS agrees with our verdict on the fixture corpus |
| **P5** MCP in the app | sidecar supervisor, stdio + HTTP, one-click client config, native confirm/PIN for agent requests | 5–8 d | agent signs via `autofirma_sign` with a native consent dialog |
| **P6** P2P slice 1 | ASiC container, QUIC + mDNS + Tailscale, fingerprint pinning, signed receipt via co-sign | 15–25 d | file sent and verified across two machines, receipt counter-signed |
| **P7** Distribution | deb/rpm/AppImage, MSI, dmg; Authenticode; notarization; updater + minisign | 10–14 d | install → sign → auto-update on a clean Linux box |
| **P8** Cut the web | remove Worker deploy + wrangler config, rewrite `AGENTS.md`/`README.md`/`DEPLOY.md`, keep `build/` as extension asset + harness | 3–5 d | CI green with no deploy job; docs describe two products |

Total ≈ **115–160 dev-days** solo. Critical path: P0 → P1 → P2 → P7. P3/P4 interleave.
P5 is small only because the MCP server already exists; do not let it drift into a rewrite.

## 5. Technical implementation notes

### 5.1 `KeyProvider` seam (P1, do before any card code)

Engine today assumes PKCS#12 (`pkcs12.ts` → `CryptoKey` → `subtle.sign`). Replace with:

```ts
interface KeyProvider {
  id: 'p12' | 'pkcs11' | 'smartcard' | 'windows-my' | 'keychain' | 'nss';
  list(): Promise<CertRef[]>;
  sign(ref: CertRef, digest: Uint8Array, alg: SignAlg): Promise<Uint8Array>; // raw signature
}
```

`sign.ts` calls the provider; container assembly is untouched. Card/NSS providers are
implemented in Rust and reached by a single command, so the TS side stays thin.

**Cheapest path to DNIe:** drive `opensc-pkcs11.so` through `cryptoki` instead of
implementing jMulticard's CWA-14890 secure channel. If OpenSC signs, that is ~20 d instead
of ~45 d. Verify this first, in P1, day 1.

### 5.2 `afirma://` + local API (P2)

- `tauri-plugin-deep-link`, scheme `afirma`. Probe for an existing handler and **never steal
  it** (rFirma made the same choice). Offer extension-only mode.
- `axum` on `127.0.0.1` with per-install self-signed cert added to the OS trust store at
  install time (what real AutoFirma does, and why its installer wants admin).
- Speak protocol 3 (legacy socket) **and** protocol 4 (modern sedes). The byte format is the
  contract. Turn `demo/registradores.mjs` into a protocol conformance fixture set.
- Secure-domain allow-list is mandatory: every request needs user confirmation unless the
  origin is allow-listed.

### 5.3 Native messaging host (P2)

- Host name: `dev.eduard3v.autofirme`. Manifests:
  - Linux Chrome/Chromium: `~/.config/google-chrome/NativeMessagingHosts/dev.eduard3v.autofirme.json`
  - Linux Firefox: `~/.mozilla/native-messaging-hosts/...` (same schema, `allowed_extensions`)
  - Windows: registry key `HKCU\Software\Google\Chrome\NativeMessagingHosts\<name>`
  - macOS: `~/Library/Application Support/Google/Chrome/NativeMessagingHosts/...`
- Framing: 4-byte little-endian length prefix + UTF-8 JSON. Max host→extension message 1 MB,
  extension→host 64 MB (Chrome's limits are the binding ones).
- Protocol: `hello` (host version, capabilities), `listCerts`, `signDigest`, `confirm`
  (host asks the GUI, returns user consent). **Digest in, raw signature out** — never a
  document, never a PIN over the wire.
- Extension treats host absence as normal (`curl`-equivalent probe fails → in-extension
  PKCS#12 path). Version skew handled by `hello.capabilities`, not by version compare.
- Firefox needs `allowed_extensions` with the add-on ID and its own signed manifest; budget
  an extra day and treat Firefox as a later deliverable (see issue #18).

### 5.4 MCP inside the app (P5)

| Option | Verdict |
|---|---|
| Rewrite tools in Rust (`rmcp`) | no — engine is TS, would duplicate `cms.ts`/`xades.ts` |
| Headless webview driving the engine | no — fragile |
| **Bundle existing `mcp/` as a supervised sidecar** | yes |

- Build a self-contained binary (Node 22 SEA or `bun build --compile`) from `mcp/src`,
  ship it inside the app bundle, spawn and supervise it.
- Two transports: **stdio** (headless agents) and **HTTP on 127.0.0.1 + bearer** (GUI running,
  so consent and PIN can surface as native dialogs).
- Settings → MCP: toggle, port, token regenerate, copy-paste config for
  `claude_desktop_config.json`, Cursor, hermes.
- Tool surface stays `autofirma_list_certs` / `autofirma_sign` / `autofirma_verify`.
  Keep the honest runtime refusal for `xades`/`pades` until P3 lands.
- The extension keeps its **own** MCP bridge (agent → Chrome confirm). Separate product,
  separate process, no shared state.

### 5.5 P2P (P6)

Trust model: the signature is the security, the transport is not.

- Container: ASiC-S (P3) + optional `age` envelope keyed to the recipient's certificate.
- Transport: `quinn` QUIC over LAN/Tailscale, `mdns-sd` discovery, self-signed cert pinned by
  fingerprint shown as words for out-of-band comparison (TOFU store).
- Receipt: recipient counter-signs the same container (P3 co-sign) → machine-verifiable proof
  of delivery and acceptance. Neither AutoFirma nor rFirma has this.
- Not in slice 1: relays, DHT, resumable multi-GB, mobile peers.

## 6. Possible errors

| # | Failure | Impact | Mitigation |
|---|---|---|---|
| 1 | `isSecureContext === false` under `tauri://localhost` on WebKitGTK → no `crypto.subtle` | engine dead on Linux | Gate A in P0; serve the build from `http://127.0.0.1:<port>` (localhost is a trustworthy origin) |
| 2 | Native messaging host not found (manifest path, wrong name, `path` not absolute) | silent fallback, card signing "missing" | `hello` probe + explicit UI state: "desktop helper not detected", with the expected manifest path shown |
| 3 | Chrome native messaging message size caps (1 MB host→ext, 64 MB ext→host) | big documents fail | never send documents over NM; digest in, signature out |
| 4 | `afirma://` already owned by real AutoFirma | two apps fight for sedes | detect, warn, never steal; extension-only mode remains |
| 5 | Protocol 3 vs 4 byte mismatch | sedes fail silently | fixture conformance tests; raw frame logging in debug builds |
| 6 | `pcscd` not running / Smart Card service stopped | "no certificates" | detect at startup, guided install, plain-language error |
| 7 | DNIe PIN retry exhaustion → blocked card (PUK) | irreversible user harm | show remaining attempts, hard-warn at ≤2, never auto-retry |
| 8 | Rust MCP / stdout pollution breaks stdio framing | agent sees garbage | existing stderr discipline; add a test that greps `console.log` in `mcp/src` |
| 9 | HTTP MCP endpoint reachable by any local page | signatures without consent | bearer + Origin allow-list + per-request confirm |
| 10 | Updater compromise / rollback | RCE class | minisign manifests, version floor, no downgrade |
| 11 | macOS notarization / Windows Authenticode not budgeted | Gatekeeper/SmartScreen blocks installs | budget $99/yr + ~$200–400/yr before P7 |
| 12 | 50 MB PDF stalls WebKitGTK main thread | perceived hang | engine in a Worker from P0, perf assertion in Gate B |
| 13 | Crash mid-write truncates an original | data loss | temp file + atomic rename |
| 14 | Extension and desktop drift (two MCP paths, two cert stores) | confusing behaviour, support load | separate products is a **documented** decision; shared engine source + shared fixtures is the only coupling |
| 15 | No web app means no verification for recipients without the app | adoption ceiling | "verify before open" UX; publish the verifier as a documented desktop feature, not a URL |
| 16 | mDNS blocked on corporate Wi-Fi | P2P fails | manual address + file-out fallback always available |

## 7. Pros / cons

**Pros**

- ~100% of AutoFirma 1.9 + rFirma feature surface becomes reachable (cards, OS stores,
  PKCS#11, `afirma://`, trust-store install, proxy, updater).
- Keys non-exportable *and* card-native: strictly better than the web build.
- No CORS: revocation, TSL and AIA become plain native fetches.
- Real filesystem: large files, directory batches, atomic writes, no upload round-trip.
- Settles the "which version are you on" question forever.
- MCP becomes a product feature with native consent, not a side project.
- Sockets exist, so P2P is possible at all.

**Cons**

- **No mobile.** AutoFirma 1.9 ships Android/iOS; we ship neither.
- **No link, no zero-install.** The cheapest trial path is gone; every user must install first.
- **No web verifier:** a recipient without the app cannot check a signature. This is a real
  product regression, accepted deliberately.
- Three OS × two arch packaging, Authenticode, notarization, apt/dnf repos, enterprise
  blocklists.
- Update lag versus instant web deploys.
- Two local servers (portal + MCP) are permanent attack surface.
- Perpetual maintenance: WebKitGTK regressions, reader quirks, keystore APIs, driver variance.
- Two products to keep coherent with one engine.

Net: right call for professional, high-volume signers (gestoría, registrador, admin staff with
a DNIe). Wrong call for a citizen signing once a year — and decision #1 removes the fallback
for exactly that user.

## 8. Open questions

Resolved 2026-10-20 — see decisions 6–9. Nothing hosted, Firefox after Windows, macOS committed,
GitHub Releases only.

## 9. Issue map (created 2026-10-20)

Epic: **#37**. Every issue carries `fleet:hold`; removing that label is the act of approving it for
execution.

| Phase | Issue | Milestone |
|---|---|---|
| P0 shell + parity gate + audit fixes | #19 | v0.1 |
| P1 native key layer | #20 | v0.1 |
| P2 portal interop + native messaging | #21 | v0.1 |
| P3 format parity | #22 | v0.2 |
| P4 validation depth | #23 | v0.3 |
| P5 MCP inside the app | #24 | v0.2 |
| P6 P2P slice 1 | #25 | v0.4 |
| P7 distribution | #26 | v0.3 |
| P8 docs rewrite | #27 | v0.1 |
| E0 extension v1 hardening | #28 | Extension v1 |
| E1 portal coverage | #29 | Extension v1 |
| E2 Firefox `userScripts` (was #18) | #30 | Extension v1 |
| E3 extension MCP bridge | #31 | Extension v1 |
| E4 optional native-messaging client | #32 | Extension v1 |
| X1 i18n parity | #33 | v0.1 |
| X2 security model doc | #34 | v0.1 |
| X3 test strategy | #35 | v0.1 |
| X4 user/enterprise/migration docs | #36 | v0.3 |


## 10. Stack audit (2026-10-20, before any code)

Findings from reading the tree at `ef5f92f`, plus a real run of the suites: `pnpm check` clean
(0 errors, 0 warnings), vitest 244 passed / 1 skipped, node --test 19 + 8 passed. The stack is
healthy; what follows is drift and plan-breaking coupling, not rot. The first six land in **P0**,
not P8, because they make CI or the desktop build lie.

### Blocker-class

1. **CI deploy job is already broken.** `ci.yml:84-88` runs `pnpm exec wrangler deploy`, but
   `wrangler` appears 0 times in `package.json` (grep count 0). The job cannot succeed as written
   and nobody noticed because it only runs on `main`. Delete it in P0 together with
   `wrangler.jsonc`, `Caddyfile`, and `Containerfile` (all three still tracked in git), and drop
   the `image` + static-tarball steps from `release-develop.yml`. **Order matters:**
   `scripts/release-workflow.test.mjs` asserts those exact jobs exist ("the image job publishes the
   Containerfile to ghcr.io", "develop artifacts job publishes … extension zip and checksums"), so
   the workflow edit and the test rewrite are one commit or CI goes red on purpose.
2. **Four different Node versions in one repo.** `.nvmrc` = `22.22.0`, CI = `24`
   (`ci.yml:41`, `release.yml`, `release-develop.yml`), local = `26.7.0`, `mcp` esbuild
   `--target=node22`. No `engines` field in either `package.json`. Fix: pin one LTS (24) via
   `.nvmrc` + `engines.node` in both packages, and build the MCP sidecar with the same major it
   is tested on. A sidecar built against a version nobody runs is a P5 bug factory.
3. **The service worker must be switched off for desktop.** `src/service-worker.ts` (6.4 KB)
   exists and SvelteKit auto-registers it; `tsconfig.json:4` excludes it from type-checking.
   WebKitGTK/Tauri has no service worker, so registration fails and cached assets can go stale in
   dev. Set `serviceWorker: { register: false }` for the desktop build, hide
   `PwaControls` (`App.svelte:462`), and drop the file once the web build is gone.
4. **`mcp/` is never tested in CI.** Zero `mcp` references in `.github/workflows`, and
   `mcp/node_modules` is absent locally, so `pnpm test` covers none of it. It is a separate pnpm
   project (own lockfile, not a root workspace member). Before P5 turns it into a shipped binary:
   add a CI job (`pnpm -C mcp install --frozen-lockfile && check && test && e2e`), or promote
   `mcp` into the root workspace so one install covers both.
5. **Version single-source doesn't know the desktop app.** `scripts/check-versions.mjs` compares
   only `package.json` ↔ `extension/manifest.json`. Once `src-tauri/tauri.conf.json` and
   `Cargo.toml` exist, drift is silent and release-please won't touch them. Extend the script and
   `release-please-config.json` `extra-files` before the first tag.
6. **Do not extend the reproducibility gate to Rust.** `release.yml` builds twice and diffs
   `SHA256SUMS`. Cargo output is not byte-reproducible by default (build paths, timestamps, build
   id), so adding Tauri artifacts to that check would block every release. Keep the gate
   extension-only and say so in the workflow comment.

### Medium

7. **`pdf-lib@1.17.1` is unmaintained** (last real release 2021) and is the load-bearing
   dependency for PAdES B-LT/LTA and the form-modification checks in P4.4. `@cantoo/pdf-lib` is a
   drop-in fork carrying the PDFBox-tested fixes. Decide before P3; if you keep 1.17.1, budget
   extra days for incremental-update work.
8. **Engine purity is confirmed good.** Every `node:` import and every `Buffer` use in `src/lib`
   lives in `*.test.ts` files only; no `process.env` in the engine. The same sources run unchanged
   in a webview and in the Node sidecar — P0.2 and P5 need no rewrite.
9. **Two browser-only couplings to replace in P0/P1:** anchor-click downloads
   (`App.svelte:311`, `FileCard.svelte:47`) → Tauri `dialog.save` + `fs.writeFile`, keeping the
   anchor as fallback; and `navigator.clipboard` (`App.svelte:154`), which is fine.
10. **`localStorage` holds the MCP bearer token** (`src/lib/mcp.ts:34,51`). In the desktop app the
    MCP direction reverses, but a token still needs a home: OS keyring / `tauri-plugin-stronghold`
    in P5.4, never `localStorage`.
11. **`pin-memory.ts` is sound as written** — outside the extension it is a module-level `Map`,
    so no PIN touches disk. Constraint for P1: card PINs must never pass through this module; they
    go native dialog → PKCS#11 session and stay in Rust.
12. **Extension changes P2 will force:** `nativeMessaging` permission and `externally_connectable`
    (neither present in `extension/manifest.json`), host manifest install paths
    (`~/.config/{google-chrome,chromium}/NativeMessagingHosts/` on Linux,
    `/usr/lib/mozilla/native-messaging-hosts/` + registry key on Windows), and a signed host
    binary on Windows/macOS or the browser refuses to launch it.

### Hygiene

13. Two package managers on disk: gitignored `bun.lock` at root (delete), `mcp/pnpm-lock.yaml`
    inside a non-workspace project (see #4).
14. `vite.config.ts:25` prerender `handleMissingId: ({ id }) => id.startsWith('/') || id === ''`
    silently accepts broken links; tighten to an allowlist when P0.3 lands, or desktop asset-path
    bugs hide behind it.
15. Test oracles (`openssl` >= 3.4, `xmlsec1`, `xmllint`, Info-ZIP) are installed ad hoc in the CI
    container and are undeclared. Put them in the P0.6 builder image so local and CI agree; local
    host currently has OpenSSL 4.0.3 against CI's 3.5.

## 11. Schedule (wave plan, 2026-10-20)

Dates and intra-issue dependencies live in each issue body as a `<!-- bullet-schedule -->` block
(`start`, `end`, `depends-on`), enforced by bullet's launch gate: `start` parks the issue with its
label untouched, `depends-on` blocks until the blocker is **closed** (closing = the human merged the
rolling PR). Milestone `due_on` carries the phase deadline.

| Wave | Start | Issues | Phase | Depends on |
|---|---|---|---|---|
| 0 | 2026-10-20 | #19, #28, #35 | P0 shell, E0 extension hardening, X3 test strategy | — |
| 1 | 2026-11-04 | #20, #22, #27, #33 | P1 native keys, P3 format parity, P8 docs, X1 i18n | #19 |
| 1e | 2026-11-04 / 11-18 | #29, #31 | E1 portal coverage, E3 extension MCP bridge | #28 |
| 2 | 2026-11-24 | #21, #34, #25 | P2 portal + native messaging, X2 security, P6 P2P slice 1 | #20 (#22 for #25) |
| 3 | 2026-12-08 | #23 | P4 validation depth | #22 |
| 3 | 2026-12-11 | #24, #26, #32 | P5 MCP inside app, P7 distribution, E4 native-messaging client | #20, #21 (+#31) |
| 4 | 2026-12-25 | #36, #30 | X4 user/enterprise docs, E2 Firefox `userScripts` | #26 (+#29) |

Window ends: #35 10-24 · #27 11-08 · #33 11-10 · #29 11-17 · #20 11-23 · #31 11-27 · #34 11-30 ·
#22 12-07 · #21 12-10 · #23 01-04 · #24/#32 12-18 · #26 12-24 · #25 01-01 · #36 01-07 · #30 01-08 ·
#19 11-03 · #28 11-03. Milestone deadlines: v0.1 12-10, v0.2 12-18, v0.4 01-01, v0.3 01-07,
Extension v1 01-08.

Dependencies encode what a worktree can actually see, not just narrative order: a dependent coder's
worktree is cut from `develop`, so a blocker sitting on `bullet-develop` is invisible until the human
merges. Dates encode expected order and are advisory (an overdue issue still launches — a slipped
plan must not wedge the fleet).
