#!/usr/bin/env node
/**
 * AutoFirma Web as an MCP server, over stdio.
 *
 * An agent that can already read and write files on this machine gets a way to produce a real
 * CAdES signature with the operator's own certificate, without a desktop app and without the
 * private key ever being exportable. Certificates come from `AUTOFIRME_MCP_CERTS` (a directory of
 * `.p12` files) and its PIN from `AUTOFIRME_MCP_PIN`; see `docs/MCP-DESIGN.md`.
 *
 * The tool surface lives in `surface.ts` and is shared with the remote (Streamable HTTP) entry.
 */

import { StdioServerTransport } from '@modelcontextprotocol/sdk/server/stdio.js';
import { audit } from './audit';
import { loadConfig } from './config';
import { buildServer, loadStore } from './surface';

async function main(): Promise<void> {
	const config = loadConfig();
	const store = await loadStore(config);
	audit(config, {
		event: 'startup',
		ok: true,
		detail: `${store.certificates.length} certificate(s), signing_ready=${store.signingReady}`,
		bytes: config.maxBytes
	});

	await buildServer(config, store).connect(new StdioServerTransport());
	// stdout is the MCP channel; anything a human should read goes to stderr.
	console.error(`autofirme-mcp ready — ${store.certificates.length} certificate(s), workspace ${config.workspace}`);
}

main().catch((error) => {
	console.error(`autofirme-mcp failed to start: ${(error as Error).stack ?? String(error)}`);
	process.exit(1);
});
