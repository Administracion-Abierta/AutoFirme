# MCP server for AutoFirma Web — design study

**Issue:** #28 (stdio, CAdES only) and #46 (remote transport, deployment). **Status:**
recommendation implemented — `mcp/` ships both transports. En español: qué hace el servidor MCP, en [LEEME.md](LEEME.md).

AutoFirma Web's signing engine is a client-side SPA: the private key never leaves the browser and
the CLI equivalent — desktop AutoFirma — is a Java GUI an agent cannot drive. The goal is to let a
coding agent on the same machine sign and verify documents through this engine, with an MCP server
as the surface.

---

## 1. Surface: reuse the TypeScript engine under Node (option **a**)

**Recommendation: (a) run the existing TS engine under Node.** Effort: **~half a day** for the
CAdES slice, which is what `mcp/` implements. Option (b), a Playwright harness, is rejected.

The engine was audited module by module for its platform dependencies:

| Module | Platform needs | Node 22+ |
|---|---|---|
| `asn1.ts`, `der.ts`, `x509.ts` | DER + `Text{En,De}coder` | ✅ |
| `pkcs12.ts` | `crypto.subtle` (PBKDF2, AES-CBC) | ✅ |
| `keystore.ts` | `crypto.subtle.importKey('pkcs8', …, extractable:false)` | ✅ |
| `sign.ts`, `cms.ts` | `crypto.subtle.sign/digest` | ✅ |
| `zip.ts` | `TextEncoder` | ✅ |
| `pades.ts` | `pdf-lib` + `crypto.subtle` | ✅ (pdf-lib is isomorphic) |
| `verify/{asn1,cms,detect,digest,roots,types,x509}.ts` | `crypto.subtle`, `atob`/`btoa` | ✅ |
| `xades.ts`, `ooxml.ts`, `c14n.ts` | `DOMParser`, `XMLSerializer`, DOM traversal | ❌ |
| `verify/{c14n,xades}.ts` | DOM traversal / `DOMParser` | ❌ |
| `batch.ts` | `svelte/store` (UI queue orchestration) | n/a |

Evidence: `grep -rn "DOMParser\|XMLSerializer" src/lib --include=*.ts` matches exactly
`xades.ts`, `ooxml.ts` and `verify/xades.ts` (besides their own tests); the two `c14n.ts` modules
never construct a DOM either — they only traverse the `Node`s they are handed, and
`verify/c14n.ts` reads `document.childNodes` off the tree the verifier parses. Every other file
touches only globals Node has had as first-class citizens since 18 (`crypto`, `atob`, `btoa`,
`TextEncoder`, `TextDecoder`).

The DOM-dependent modules are **not** on the CAdES path. `src/lib/cms.ts` (sign) and
`src/lib/verify/cms.ts` (verify) build and parse the `SignedData` structures by hand over
`crypto.subtle`, so a CAdES server needs **zero engine changes** — which is also why this slice is
low-risk: the browser app keeps working exactly as before, and the Node build is a second consumer
of the same code, not a fork.

Cost of the rejected alternatives:

- **(b) Headless browser (Playwright drives the SPA).** Zero engine changes, but it drags a
  browser runtime and its GPU/font sandbox into a signing daemon, needs the UI's file-picker flow
  scripted (or a new hidden API on the page), breaks on every UI refactor, and is ~10× slower per
  signature. It buys nothing that option (a) does not already have, because the code was written
  DOM-free in the first place.
- **(c) Port the engine to Java/reuse decompiled AutoFirma.** Wastes the clean-room rewrite and
  reintroduces the GPL-derived code the project deliberately keeps out of the app.
- **(d) Run the MCP server inside the browser extension.** Not possible: Manifest V3 has no
  server-socket API. `chrome.sockets.tcpServer` exists only for deprecated Chrome Apps, and a
  service worker or extension page cannot bind a TCP port; it can only make outbound requests.
  `/mcp` therefore needs a process outside Chrome. A native-messaging host can relay requests to
  the extension if the agent must use a key that is unlocked there, but the host, not the CRX,
  owns the listening socket.

**Sequencing note:** the slice deliberately stops at CAdES. XAdES and PAdES are reachable —
PAdES signing is already DOM-free (it needs only `pdf-lib` as a dependency) and XAdES needs a DOM
implementation (`@xmldom/xmldom` is not spec-accurate enough for C14N; jsdom is the realistic
choice, at a large dependency cost). Both are follow-ups, and the server answers `unsupported_format`
rather than pretending.

**Risk of option (a)** — four failure modes, all of them loud:

1. **Build-time coupling to the app.** `mcp/src/*.ts` import the engine as *source*
   (`../../src/lib/…`), so an engine API change or a directory move (the #26 restructure
   already relocated the app once) breaks `pnpm check`/`pnpm build`/`pnpm e2e` — never silently.
   The *runtime* is decoupled: `pnpm build` bundles the engine into `dist/server.js`, which imports
   only `@modelcontextprotocol/sdk`, `zod` and `node:*` (verified — no relative import survives the
   bundle), so a built server keeps running even if `src/` is gone from the deployment.
2. **A DOM creeping onto the CAdES path.** The Node build has no `DOMParser`; `cms.ts`, `pades.ts`
   and the `verify/` modules must stay DOM-free. Today `grep -rn "DOMParser\|XMLSerializer"
   src/lib --include=*.ts` matches only `xades.ts`, `ooxml.ts` and `verify/xades.ts`. A future
   edit that pulls a DOM into `cms.ts` or `verify/cms.ts` fails `pnpm test` (Node environment, no
   jsdom) immediately; in production the tool reports `dom_unavailable` rather than a false result.
3. **Node version floor.** `crypto.subtle`, `atob`/`btoa` and the esbuild `--target=node22` set it;
   Node 18–20 are untested and `mcp/package.json` declares no `engines`.
4. **A live signing key in a long-lived process** — the accepted residual risk of any local signer,
   and the reason stdio stays the transport a same-machine agent should use (see §5, "Server
   compromise" and the remote-mode rows added by #46).

## 2. Certificates: operator-preloaded PKCS#12, PIN from the environment

**Recommendation: the operator preloads `.p12` containers into a directory and supplies one PIN at
startup.** Pass-through per call is rejected.

- `AUTOFIRME_MCP_CERTS` — directory of `*.p12` files. PKCS#12 is already encrypted at rest
  (PBES2 + MAC), which is what makes this "encrypted at rest" without inventing a second layer.
- `AUTOFIRME_MCP_PIN` — the PIN for every container. Read once at startup, never logged, never
  returned by any tool.
- On load, `loadSignerSession` imports the private key with `extractable: false` and key usage
  limited to `sign`. The resulting `CryptoKey` cannot be exported by any code — browser or Node.
- **Key lifecycle:** plaintext key material exists only inside the WebCrypto implementation's
  memory for the lifetime of the process (the parsed PKCS#8 bytes are released when
  `loadSignerSession` returns). Nothing is written to disk, no cache, no session file. Restarting
  the server is the revocation mechanism; so is the OS process boundary.
- `autofirma_list_certs` returns DN, serial, validity window and a `signable` flag — never key
  material, never the container's bytes.

**Rejected — pass-through PIN per call:** every tool call would carry the secret into the model
conversation, the client's logs, and any MCP proxy in between. It also makes the agent responsible
for choosing a certificate on every call, which is exactly the decision a human should make once,
at setup.

**Rejected — OS keystore / PKCS#11 / browser NSS store:** the right long-term answer for a desktop
user, but it needs a native dependency and platform-specific code, and it does not help the primary
consumer here (a headless agent in a homelab). Out of scope.

## 3. Transport: stdio for the local agent, Streamable HTTP for remote ones

**stdio stays the default.** It needs no port, no TLS, no authentication story and no network
exposure; the OS process boundary is the access control. `mcp/src/server.ts` is unchanged by #46.

**Streamable HTTP (spec 2025-03-26) is the remote transport**, `POST /mcp`, bearer-authenticated
(`src/http.ts`, Node adapter in `src/server-http.ts`). Both entries register the *same* tools: the
registrations live in `src/surface.ts` and each transport only decides how a request arrives, so a
remote agent sees the identical three tools, schema and error codes.

Decisions inside the remote transport:

- **Stateless** (`sessionIdGenerator: undefined`, JSON responses). No session id, no in-memory
  session table, nothing to resume or leak across callers, and a replica restart costs no session.
  The price is that a client cannot use server→client notifications; nothing in this tool surface
  emits them.
- **Bearer token, re-checked per request** (`AUTOFIRME_MCP_TOKEN`), compared through SHA-256
  digests so neither the length nor an early mismatch can be probed from the outside. No token
  configured → the HTTP entry refuses to start (fail closed) and the handler answers `503`.
- **One transport and one `McpServer` per request**, closed in a `finally`. Registration is cheap;
  isolation is worth more.
- **Body cap** = `AUTOFIRME_MCP_MAX_BYTES` inflated by the base64 encoding, applied both by the
  Node adapter and by the transport, so an oversized POST is refused before it is parsed.

### Why not Cloudflare Workers (the issue's preferred target)

Rejected for this slice, for three reasons that are costs rather than taste:

1. **The keystore needs a filesystem.** `src/certstore.ts` reads a directory of `.p12` containers.
   On Workers the key would arrive as base64 in an env var/secret — a new cert-source path, and
   exactly the "key material as a platform string" shape §2 rejects.
2. **The audit trail needs to append.** `src/audit.ts` writes JSONL; Workers has no filesystem.
   Dropping the durable audit sink of a legally-weighted action to gain a free tier is a bad trade.
3. **workerd bundling is a new build surface** for a process that must hold a live signing key
   anyway, while the homelab already exposes services through the existing Cloudflare Tunnel
   (`Containerfile`, `Caddyfile`), so deployment is one manifest (`mcp/deploy/k3s.yaml`).

The port stays cheap: the handler is a Web-standard `(Request) => Response`, so a Worker entry would
import `createHttpHandler` and add the cert/audit sources it lacks — see follow-up 4.

### Deployment shape

`mcp/Containerfile` (build context = repo root; the engine is compiled in from `src/`) → image;
`mcp/deploy/k3s.yaml` (Deployment + Service + Secret + PVC for the containers) → workload behind the
operator's cloudflared ingress, which terminates TLS. Deploy is operator-run; the commands are in
`mcp/README.md`.

## 4. Tool surface

Three tools, as proposed:

| Tool | Input | Output |
|---|---|---|
| `autofirma_sign` | `format` (`cades`\|`xades`\|`pades`), `cert_id`, one of `file_b64`/`path`, optional `signing_time` | `signed_b64` (DER CAdES), `bytes`, `sha256`, `document_sha256`, `signing_time`, `detached: true`, `cert_expired` |
| `autofirma_verify` | exactly one of `artifact_b64`/`path`, optional `detached_b64`/`detached_path` | `{ report, supported, document_sha256 }` where `report` is the engine's own `VerificationReport` (structure, content digest, signature, signer cert, validity, chain, signing time) |
| `autofirma_list_certs` | — | `{ certificates, errors, signing_ready }` |

Two deliberate shape decisions:

- **`detached: true` is in the sign result.** `cms.ts` produces a *detached* CAdES-BES: the artifact
  alone does not carry the document. An agent that drops the original loses the ability to verify,
  so the response says so explicitly and `autofirma_verify` takes the document back in.
- **`supported` is in the verify result.** When a format is recognised but this build cannot check
  it, the tool reports `supported: false` with an `unsupported` check rather than a misleading `ok`.
- **Trust anchors belong to the operator, not the agent.** `autofirma_verify` builds the chain
  against `AUTOFIRME_MCP_TRUSTED_ROOTS` (a PEM bundle) when the operator set it, and against the
  engine's bundled FNMT roots otherwise. An agent cannot supply its own anchors — that would make
  the chain check report whatever the caller wanted. A private CA therefore shows
  `certChain: fail` until the operator declares it, which is the honest answer.

## 5. Threat model

| Threat | Mitigation |
|---|---|
| **Path escape** — `path` reaching `/etc/shadow`, `~/.ssh/id_rsa`, or the keystore itself | `path` is resolved against `AUTOFIRME_MCP_WORKSPACE`, then **both** root and target go through `realpath`, and the resolved target must be the root or under it. `../..` and symlinks pointing out are rejected by the same check (`path_escape`). Absolute paths are allowed only if they land inside the workspace. |
| **Unbounded sizes** — a gigabyte document, or a base64 string that decodes to one | `AUTOFIRME_MCP_MAX_BYTES` (default 32 MiB) is enforced three times: on the base64 length *before* decoding, on the file size from `stat` before reading, and on the actual buffers. In remote mode the HTTP adapter and the transport also cap the POST body at the same limit inflated by base64. |
| **Ambiguous input** | Exactly one of `file_b64`/`path` per call — passing both is `ambiguous_source`, not a silent pick. |
| **Key handling** | Non-extractable `CryptoKey`, `sign`-only usage; PIN from the environment only; no key material in any tool response; the store is in-process memory only. |
| **Audit** | Every `sign`, `verify`, `list_certs`, startup and keystore failure emits a JSON line with timestamp, tool, `cert_id`, format, document SHA-256 and byte count — identifiers and digests, never content, never the PIN. Always on stderr; additionally appended to `AUTOFIRME_MCP_AUDIT_LOG` (mode 0600) when set. |
| **Expired certificate** | Signing is allowed (a timestamped workflow may legitimately need it) but the result carries `cert_expired: true` and the audit line records it. |
| **Server compromise** | The process holds live signing keys while running. Accepted: it is the operator's own machine and their own key, and the alternative (a remote key service) is strictly worse. Documented, and the reason stdio stays the local default. |
| **Stolen bearer token** (remote mode) | The token is the only credential and it authorises signing with the loaded certificate. TLS terminates at the operator's tunnel; the token lives in a k8s Secret, never in a tool argument or a log line; rotating it is a secret update plus a pod restart — the same restart that revokes the loaded key. Constant-time comparison, and an unset token means the HTTP entry refuses to start rather than serving unauthenticated. |
| **Unauthenticated network scan** (remote mode) | Every request re-checks the bearer header; missing, malformed and wrong credentials all answer `401` with `www-authenticate: Bearer`. Only `POST /mcp` is routed; anything else is `404`. |
| **Session hijacking** (remote mode) | There is no session: stateless mode issues no session id, keeps no server-side state, and builds (and closes) one transport per request, so there is nothing to steal, resume or leak between callers. |

## Configuration summary

| Variable | Meaning | Default |
|---|---|---|
| `AUTOFIRME_MCP_CERTS` | directory of `*.p12` containers | unset → signing unavailable |
| `AUTOFIRME_MCP_PIN` | PIN for those containers | unset → keystore stays closed |
| `AUTOFIRME_MCP_WORKSPACE` | root that `path` inputs may not escape | `process.cwd()` |
| `AUTOFIRME_MCP_MAX_BYTES` | cap on any document/artifact | 33554432 (32 MiB) |
| `AUTOFIRME_MCP_AUDIT_LOG` | extra JSONL audit file | unset → stderr only |
| `AUTOFIRME_MCP_TRUSTED_ROOTS` | PEM bundle of extra trust anchors for verification | unset → engine's bundled roots (FNMT) |
| `AUTOFIRME_MCP_TOKEN` | bearer token for the remote transport | unset → the HTTP entry refuses to start |
| `AUTOFIRME_MCP_HOST` | interface the HTTP entry binds | `127.0.0.1` |
| `AUTOFIRME_MCP_PORT` | port the HTTP entry binds | `8790` |

Client wiring (Claude Desktop / Claude Code shape):

```json
{
  "mcpServers": {
    "autofirme": {
      "command": "node",
      "args": ["/path/to/repo/mcp/dist/server.js"],
      "env": {
        "AUTOFIRME_MCP_CERTS": "/home/me/.autofirme/certs",
        "AUTOFIRME_MCP_PIN": "…",
        "AUTOFIRME_MCP_WORKSPACE": "/home/me/work"
      }
    }
  }
}
```

## 6. What was actually verified

- **`pnpm test` (vitest, `mcp/test/`)** — the workspace jail (`../`, a symlink out, an unrelated
  absolute path), the size cap on both channels, the "exactly one of `file_b64`/`path`" rule,
  keystore loading (wrong PIN, missing configuration, a name that collides with the id separator),
  a signature that verifies, a tampered document that does not, the `unsupported_format` refusals,
  and the audit line. The PKCS#12 containers are generated with `openssl` at test time, so no key
  material is committed.
- **`pnpm e2e`** — a real MCP client over stdio signs a document, and `openssl cms -verify`
  accepts the result and recovers exactly the bytes that were signed; the same signature against a
  tampered document is rejected by both openssl and the server's own `autofirma_verify`. It also
  checks that no tool response contains key material and that the audit log holds digests, not
  content.
- **`pnpm test` in `mcp/` (vitest), remote transport** — the handler's routing and credential
  matrix (`404` off-path, `503` without a configured token, `401` for missing, wrong, empty and
  non-bearer credentials), and, over a real socket with a real MCP client
  (`StreamableHTTPClientTransport`), the identical tool list, a signature produced over HTTP that
  `openssl cms -verify` accepts against the signer CA, and `401` for a wrong token.
- **Live smoke of the built entry** — `node dist/server-http.js` answers `401` unauthenticated,
  completes `initialize` with the bearer token, and `404`s every other path.
- **No engine change was needed** for the CAdES path: `src/lib/` is consumed read-only, which is
  the strongest evidence for option (a) — the same code that runs in the browser runs under Node.

## Follow-ups this study does not implement

1. **XAdES** — needs a DOM implementation. The app's engine already signs and verifies XAdES in
   the browser; a Node build would have to run the same code under jsdom, with the C14N cost that
   implies.
2. **PAdES** — cheapest win: signing needs only `pdf-lib`, and the engine's PDF verify path does not
   exist yet in either runtime.
3. **Multiple PINs** — one container, one PIN, is assumed today.
4. **Cloudflare Workers entry** — the Web-standard handler in `src/http.ts` is the port; what
   Workers still needs is an fs-free certificate source and an audit sink that is not a file (see
   §3, "Why not Cloudflare Workers").
5. **Progress notifications for large batches** — one document per call today.
