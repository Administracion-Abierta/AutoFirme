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
| **P0** Shell + parity gate | Tauri v2 scaffold, secure-context resolution, 12-item engine smoke, CI `cargo check` + headless smoke | 8–12 d | all 12 green on Linux; one signed PDF validates in Acrobat |
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

1. Does the project keep any hosted presence at all (docs site, landing page) once the Worker
   is retired? Currently the answer is "nothing", which also removes the demo.
2. Firefox: extension parity before or after Windows support? (issue #18 is `fleet:hold`.)
3. macOS: skip entirely for v1.0 and revisit, or commit to notarization from the start?
4. Who signs releases (GPG/minisign key custody) and where do apt/dnf repos live — Forgejo
   pages, GitHub Releases, or both?
