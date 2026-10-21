# Decisions

## 2026-10-21 — extension/ and mcp/ stay in the monorepo

No split into separate repos.

Why: both source-import the engine (`extension/` mounts `src/routes/+page.svelte`
and `src/lib/extension/*` uses `cms/pades/xades/certstore/keystore/sign/i18n`;
`mcp/src/{tools,certstore}.ts` import `../../src/lib/…`). A split without an
extracted engine package means copy-paste drift within weeks.

Cost of splitting (when revisited): extract engine as versioned package first,
freeze the page↔worker Rpc, the MCP `/mcp` + `/bridge` (`autofirme-bridge.v1`)
contract, and the `autofirma_{list_certs,sign,verify}` tool surface; duplicate
release-please + `check-versions` + CI per repo; cross-repo `depends-on`
invisible to bullet.

Revisit when: store review cadence diverges from web deploy, or the Tauri P5
sidecar needs a versioned `mcp` artifact.
