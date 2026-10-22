# autofirme-mcp

An [MCP](https://modelcontextprotocol.io) server that exposes AutoFirme's signing engine:
CAdES-BES signing with an operator-supplied certificate, and verification. It speaks **stdio** for
an agent on the same machine and **Streamable HTTP** (spec 2025-03-26) for remote ones. The design
study behind it — alternatives, threat model, key lifecycle — is `../docs/MCP-DESIGN.md`.

The private key never leaves the process: it is imported as a non-extractable WebCrypto handle and
nothing is written to disk. Certificates come from `.p12` containers the operator preloads.

## Build and test

```bash
pnpm install
pnpm build       # esbuild bundles → dist/server.js (stdio) + dist/server-http.js (remote)
pnpm test        # vitest (generates throwaway PKCS#12 fixtures with openssl)
pnpm check       # tsc --noEmit
pnpm e2e         # builds, then signs through the stdio server and verifies with openssl cms -verify
```

Requires Node ≥ 22 and `openssl` on `PATH` (tests and the e2e check only).

## Configuration

| Variable | Meaning | Default |
|---|---|---|
| `AUTOFIRME_MCP_CERTS` | directory of `*.p12` containers | unset → signing unavailable |
| `AUTOFIRME_MCP_PIN` | PIN for those containers | unset → keystore stays closed |
| `AUTOFIRME_MCP_WORKSPACE` | root that `path` inputs may not escape | `process.cwd()` |
| `AUTOFIRME_MCP_MAX_BYTES` | cap on any document/artifact | 33554432 (32 MiB) |
| `AUTOFIRME_MCP_AUDIT_LOG` | extra JSONL audit file | unset → stderr only |
| `AUTOFIRME_MCP_TRUSTED_ROOTS` | PEM bundle of extra trust anchors for `autofirma_verify` | unset → engine's bundled roots (FNMT) |
| `AUTOFIRME_MCP_TOKEN` | bearer token for the remote transport | unset → the HTTP entry refuses to start |
| `AUTOFIRME_MCP_HOST` | interface the HTTP entry binds | `127.0.0.1` |
| `AUTOFIRME_MCP_PORT` | port the HTTP entry binds | `8790` |

Wire it into an MCP client (Claude Desktop / Claude Code shape):

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

## Remote transport (Streamable HTTP)

```bash
AUTOFIRME_MCP_TOKEN=$(openssl rand -hex 32) \
AUTOFIRME_MCP_CERTS=/etc/autofirme/certs AUTOFIRME_MCP_PIN=… \
AUTOFIRME_MCP_WORKSPACE=/work \
node dist/server-http.js          # or: pnpm start:http
```

`POST /mcp` is the whole surface, `Authorization: Bearer <token>` on every request; anything else is
`404`, a wrong or missing token is `401`, and no token configured is `503`. The handler is a
Web-standard `fetch` function (`src/http.ts`) and `src/server-http.ts` is only the Node adapter, so
this is the file a Cloudflare Worker or Deno port would reuse.

**Stateless**: no session id, no server-side session table. One POST is one complete MCP exchange.

### Extension bridge (browser-held identities)

If the operator keeps identities in the AutoFirme Chrome extension, no `.p12` directory or
`AUTOFIRME_MCP_PIN` is needed. In the extension's MCP settings enter this server's `/mcp` URL and
`AUTOFIRME_MCP_TOKEN`, then enable **MCP bridge**. The extension connects outbound to
`WS(S) /bridge` (same host, same token), lists its identities to `autofirma_list_certs`, and opens
a Chrome consent window for every `autofirma_sign`. The server receives only the public
certificate and the finished CAdES, which it verifies before answering.

The bridge lives on this HTTP entry; the stdio server has no listening socket and cannot use it.
The token travels in the WebSocket query string because browsers cannot set an `Authorization`
header on `WebSocket` — keep tunnel/proxy access logs private. Signing still needs the operator's
click, but a stolen token can list identities and open consent windows.

Client wiring (remote shape):

```json
{
  "mcpServers": {
    "autofirme": {
      "url": "https://mcp.example.com/mcp",
      "headers": { "Authorization": "Bearer <token>" }
    }
  }
}
```

Deploy (operator-run; the built image serves only plain HTTP — the tunnel terminates TLS):

```bash
# 1. image, from the REPO ROOT (mcp/ compiles the engine from src/)
podman build -f mcp/Containerfile -t registry.example/autofirme-mcp:0.1 .
podman push registry.example/autofirme-mcp:0.1

# 2. secrets, then the workload (mcp/deploy/k3s.yaml)
kubectl -n autofirme create secret generic autofirme-mcp \
  --from-literal=AUTOFIRME_MCP_TOKEN="$(openssl rand -hex 32)" \
  --from-literal=AUTOFIRME_MCP_PIN='<pkcs12 pin>'
kubectl -n autofirme apply -f mcp/deploy/k3s.yaml

# 3. route it through the existing tunnel: cloudflared ingress
#    - hostname: mcp.example.com
#      service: http://autofirme-mcp.autofirme.svc:8790
```

The signing key travels with the pod, not with the tunnel: whoever holds the token can sign with the
loaded certificate. Rotate the token by deleting the secret value and restarting the pod — the same
restart that revokes the loaded key. Audit lines go to stderr, i.e. `kubectl logs deploy/autofirme-mcp`.

## Tools

| Tool | Input | Output |
|---|---|---|
| `autofirma_sign` | `format` (`cades`; `xades`/`pades` answer `unsupported_format`), `cert_id`, exactly one of `file_b64`/`path`, optional `signing_time` | `signed_b64` (detached DER CAdES), digests, `cert_expired` |
| `autofirma_verify` | exactly one of `artifact_b64`/`path`, optional `detached_b64`/`detached_path` | the engine's `VerificationReport`, plus `supported` |
| `autofirma_list_certs` | — | certificate metadata (DN, serial, validity, `signable`) — never key material |

`cades` is the whole slice: `xades` and `pades` answer `unsupported_format`, because XAdES needs a
DOM the Node build does not have. Signatures are **detached** — keep the document, verification
needs it back.

Failures come back as `isError` with a JSON body `{ "error": "<code>", "message": "…" }`; codes
include `path_escape`, `too_large`, `ambiguous_source`, `unknown_cert_id`, `unsupported_format`.

## Key lifecycle

The `.p12` container is encrypted at rest (PKCS#12/PBES2 + MAC) and is the only persistence there
is. On load the private key becomes a non-extractable, `sign`-only `CryptoKey` that exists in
process memory until exit; restarting the server is the revocation mechanism. Every `sign`,
`verify` and `list_certs` call emits one JSON audit line (timestamp, tool, `cert_id`, document
SHA-256, byte count — never content, never the PIN) to stderr and, when configured, to
`AUTOFIRME_MCP_AUDIT_LOG`.
