# Security — loopback surfaces (X2a)

Scope of this file: the three loopback-adjacent signing surfaces — portal sign
server, MCP HTTP endpoint, native messaging host. P2P and key handling belong to
the sibling child, not here. Each surface lists entry points, trust boundary,
worst case, and mitigations, plus the test that proves the consent gate holds.

Status note (2026-10-23): the MCP HTTP endpoint and the extension consent
machinery exist in this tree and are tested. The portal sign server (`axum` on
`127.0.0.1`) and the native messaging host binary are planned work
(`docs/DESKTOP-ROADMAP.md` §§5.2–5.3) — their sections below threat-model the
documented contract, and their tests pin the contract invariants that any future
implementation must preserve.

## 1. Portal sign server (`127.0.0.1`, TLS, per-install cert)

Planned: `axum` on `127.0.0.1` with a per-install self-signed cert added to the
OS trust store at install time; speaks protocol 3 (legacy socket) and protocol 4
(modern sedes); `demo/registradores.mjs` is the fixture set to grow into protocol
3/4 conformance (roadmap §5.2). Today the same portal calls are answered by the
extension bridge (`extension/core/`), the reference implementation of the
consent gate below.

### Entry points

- `afirma://` deep-link intents (protocol 3+4 byte format is the contract).
- TLS loopback socket on `127.0.0.1` (real AutoFirma used port 63117; exact port
  is an implementation detail, the loopback bind is not).
- Portal call shapes the extension already answers: `sign` with `mode=implicit`
  or `mode=explicit` (`extension/core/contract.js`), and the `CAdEStri`
  pre-sign/post-sign variant with a page-supplied `serverUrl`
  (`src/lib/extension/triphase.ts`). The demo driver
  (`demo/registradores.mjs`) exercises plain `AutoScript.sign` with
  `mode=explicit` against the live registradores test page — not the tri-phase pair.

### Trust boundary

Loopback is **not** a security boundary: any local process — and, without an
`Origin` check, any web page that can fetch `https://127.0.0.1:<port>/` — can
reach the socket. The boundary is therefore **per-request user confirmation**,
not "localhost said so". Origin is proven by the consent UI, not by the
transport: the chooser binds each request to the exact tab/frame/document/origin
(`Coordinator.capture` from Chrome sender metadata) and shows the origin plus
the SHA-256 digest before any signing happens.

### Worst case

A compromised local process (or a malicious page, if the server ever accepts
cross-origin fetches) crafts a sign request for a document the user never saw.
Without the gate, one silent call = one legally-weighted signature under the
user's certificate. The blast radius is one signature per call; there is no
lateral movement through this surface beyond what each call authorises.

### Mitigations

- Secure-domain allow-list is mandatory: every request needs user confirmation
  unless the origin is allow-listed (roadmap §5.2).
- Per-install cert in the OS trust store at install time (what real AutoFirma
  does); detect a competing `afirma://` owner, warn, never steal — extension-only
  mode remains.
- Request caps already in the bridge contract and required of the server:
  ≤4 MiB canonical Base64 per document, ≤8 pending per document, ≤16 in flight,
  5-minute deadline (`extension/core/contract.js`, `extension/core/coordinator.js`).
- Stale-document invalidation: navigation, back-forward-cache restore, or worker
  disconnect finishes pending requests as error/fallback, never replays them.

### Consent-gate test

`extension/tests/loopback-consent.test.js` — "result without begin is refused":
`RequestState.finish('result')` from `waiting` returns `false`, so no code path
can produce a signature without passing through `start()` (the confirmation
click) first. The companion cases pin that `fallback` after signing starts is
refused (no double signature via replay) and that a completed request stays
terminal (no second signature from a replayed `begin`). Sender binding is
covered where the sender exists: `bridge.test.js` — "chooser authorization binds
exact own URL, request and recorded tab" — and the end-to-end suite
(`background.test.js`: "begin gates completion…", stale/cached-document
rejections).

## 2. MCP HTTP endpoint (`POST /mcp`, bearer, `Origin`)

Built: `mcp/src/http.ts` + `mcp/src/server-http.ts`. Stateless Streamable HTTP,
one transport per request, default bind `127.0.0.1:8790`, bearer token
`AUTOFIRME_MCP_TOKEN` re-checked per request through a SHA-256 constant-time
compare; no token configured → the entry refuses to start (fail closed, `503`).

### Entry points

- `POST /mcp` — the only routed path; anything else is `404`.
- `Upgrade /bridge` (WebSocket, same token in query string, subprotocol
  `autofirme-bridge.v1`) — the extension bridge; its consent properties are
  pinned in the consent-gate test below, here it is an endpoint that shares the
  token.
- Tools reachable through it: `autofirma_sign`, `autofirma_verify`,
  `autofirma_list_certs` (same registrations as stdio, `mcp/src/surface.ts`).

### Trust boundary

The bearer token **is** the credential and it authorises signing with the loaded
certificate — treat it as the certificate's remote PIN. TLS terminates at the
operator's tunnel (Cloudflare Tunnel in the reference deploy); the token lives
in a k8s Secret, never in a tool argument or a log line. `http.ts` performs **no
`Origin` check**: any local page or process holding the token can call `/mcp`.
That is accepted and documented because the token, not the origin, is the gate —
but it means token secrecy is load-bearing (see worst case).

### Worst case

A leaked token (proxy logs carrying the `/bridge` query string, a committed
config file, a shoulder-surfed settings screen) gives silent, consent-free
signing with the preloaded certificate to anyone who can reach the socket —
including a malicious local page, since there is no `Origin` allow-list. An
agent holding the token can also sign without human consent whenever the bridge
is disconnected or the requested identity has a remembered session PIN. Listing
identities never signs, and verification never needs the key — but `sign` needs
no further proof beyond the token in that configuration.

### Mitigations

- Fail closed: unset token → entry refuses to start; missing/malformed/wrong
  credentials → `401` + `www-authenticate: Bearer` (tested matrix).
- Constant-time compare (SHA-256 digests first) so neither length nor prefix is
  probeable; every request re-checked, no sessions to steal (stateless mode).
- Body cap (`maxBytes` inflated by base64) enforced by adapter *and* transport,
  before parsing; workspace jail (`realpath` both sides) for `path` inputs.
- Audit JSONL per sign/verify/list (digests and ids only, never content/PIN) to
  stderr plus optional mode-0600 file — a leaked-token signing spree leaves a
  timestamped trail.
- Operator duties (documented, not code): TLS via tunnel, token as k8s Secret,
  rotate on suspected exposure (restart revokes the loaded key too), treat
  tunnel/proxy access logs as secret material or scrub `token=`.
- Residual risk, recorded: no `Origin` allow-list on `/mcp` today (roadmap §6
  item 9 names bearer + Origin allow-list + per-request confirm as the target).
  Do not add one casually — MCP clients do not send a stable `Origin`, so a
  naive check breaks legitimate agents while adding little once the token is
  required. The honest fix is per-request native confirmation on the HTTP path
  (roadmap P5), not an `Origin` header check.

### Consent-gate test

`mcp/test/http-consent.test.ts` — "unauthenticated sign signs nothing": the
full matrix (no header, wrong token, non-bearer scheme, empty value) answers
`401` **and** the audit trail contains no `sign` event, proving no signing path
ran before auth. The bridge-consent half is pinned in the extension suite
(`background.test.js`: bridge job with no remembered PIN opens a chooser and
needs `request:begin` + `request:complete`; remembered-PIN auto-sign is limited
to the opted-in `cert_id` in session memory only).

## 3. Native messaging host (`dev.eduard3v.autofirme`)

Planned contract (roadmap §5.3): host `dev.eduard3v.autofirme` with per-OS
manifests; framing 4-byte little-endian length + UTF-8 JSON; Chrome's caps are
binding — max 1 MB host→extension, 64 MB extension→host; protocol
`hello` (version + capabilities) / `listCerts` / `signDigest` / `confirm` (host
asks the GUI, returns user consent). **Digest in, raw signature out** — never a
document, never a PIN over the wire. Extension treats host absence as normal
(probe fails → in-extension PKCS#12 path); version skew handled by
`hello.capabilities`, not version compare.

### Entry points

- The extension may ask for exactly: `hello`, `listCerts`, `signDigest`,
  `confirm`. Nothing else is a valid host call.
- Manifest paths (Linux Chrome `~/.config/.../NativeMessagingHosts/`,
  Firefox `allowed_extensions`, Windows registry, macOS Application Support) —
  each is a trust root: whoever can write the manifest path can point the
  extension at a rogue host binary.

### Trust boundary

Two boundaries. **Towards the extension:** only the browser-launched host
(manifest `path`, absolute) is trusted; the extension never connects to a
self-advertised socket. **Towards the user:** `signDigest` must never run
without a preceding `confirm` for the same digest — the host asks the GUI, the
GUI returns consent, then and only then does the key operate. The digest binds
the confirmation: consent for digest D authorises exactly D.

### Worst case

A rogue host binary (manifest path writable by malware, or a trojaned updater)
sees every digest the extension sends and returns signatures over digests the
user never approved — or, worse, exfiltrates digests that reveal *what* is being
signed. Since the channel carries digests (SHA-256 of the document), a passive
rogue host learns document hashes; an active one forges the `confirm` answer to
itself and signs silently. Size-limit violations are the shabby cousin: a host
sending >1 MB frames gets its messages dropped by Chrome, which is a DoS, not a
forgery — but an extension that *accepted* oversized frames would risk parser
memory abuse.

### Mitigations

- Digest-only channel: documents never cross NM, so a compromised host learns
  hashes, not contents; PINs never cross either (native prompt at the host).
- `confirm`-before-`signDigest`, same-digest binding; no batch pre-confirmation.
- Framing caps enforced on receipt (drop >1 MB host→ext before parse).
- `hello.capabilities` negotiation so a stale host cannot be tricked into
  unknown ops; host absence = normal fallback, never an error that scares the
  user into disabling security.
- Manifest-path integrity is an installer/OS duty (signed host binary on
  Windows/macOS or the browser refuses to launch it).
- DNIe note: PIN retry exhaustion blocks the card — show remaining attempts,
  hard-warn at ≤2, never auto-retry (roadmap §6 item 7).

### Consent-gate test

`extension/tests/native-host-consent.test.js` — contract-level, no binary:
(1) oversized host→extension frame (>1 MiB length prefix) is rejected before
parse; (2) a `signDigest` model that skips `confirm` (or confirms a different
digest) refuses to sign — the confirm-before-sign, same-digest state machine
that any future host implementation must preserve. Documents (>digest size)
over the channel are rejected by construction.

## Residual risks (accepted, all three surfaces)

| # | Risk | Why accepted | Revisit when |
|---|---|---|---|
| 1 | Loopback reachable by any local process | OS process boundary is the access control; per-request confirmation is the real gate | per-request native confirm lands (P5) |
| 2 | MCP token secrecy is load-bearing; no `Origin` check | token-as-PIN is the documented model; naive `Origin` checks break MCP clients | P5 confirm-on-HTTP, or clients send stable `Origin` |
| 3 | Remembered session PIN allows silent bridge signing | explicit per-identity opt-in, session memory only, cleared on browser close | usage audit per auto-sign |
| 4 | NM manifest path writable by local malware | installer/OS duty; out of the extension's control | signed-host enforcement per OS |
| 5 | `CAdEStri` pre/post-sign round-trips to portal servers | page supplies `serverUrl`; extension performs the calls with the key local | portal-TLS pinning review |

Refs #121
