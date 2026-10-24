/**
 * The MCP tool surface, once — shared by the stdio entry (`server.ts`) and the remote one
 * (`server-http.ts`).
 *
 * Both transports must expose exactly the same three tools over the same engine; keeping the
 * registration in one place is what makes that true. The transport only decides how a request
 * arrives (a pipe, or an HTTP POST).
 */

import { McpServer } from '@modelcontextprotocol/sdk/server/mcp.js';
import { z } from 'zod';
import { audit } from './audit';
import type { BridgeHub } from './bridge';
import { CertStore } from './certstore';
import type { ServerConfig } from './config';
import { ToolError } from './input';
import { listBridgeCerts, listCerts, signDocument, verifyDocument } from './tools';

/** A tool failure carries its code; an unexpected one is reported as `internal` but still logged. */
function failure(error: unknown): { content: { type: 'text'; text: string }[]; isError: true } {
	const code = error instanceof ToolError ? error.code : 'internal';
	const message = error instanceof Error ? error.message : String(error);
	return {
		content: [{ type: 'text', text: JSON.stringify({ error: code, message }) }],
		isError: true
	};
}

const json = (value: unknown): { content: { type: 'text'; text: string }[] } => ({
	content: [{ type: 'text', text: JSON.stringify(value, null, 2) }]
});

/** `file_b64` and `path` are both optional here; the tool layer rejects "neither" and "both". */
const SOURCE_FIELDS = {
	file_b64: z.string().optional().describe('Document contents, base64. Exactly one of file_b64/path.'),
	path: z.string().optional().describe('Document path, resolved inside AUTOFIRME_MCP_WORKSPACE.')
};

/** The same two channels for `verify`, but the payload is a signature rather than a document. */
const ARTIFACT_FIELDS = {
	artifact_b64: z.string().optional().describe('Signed artifact, base64. Exactly one of artifact_b64/path.'),
	path: z.string().optional().describe('Signed artifact path, resolved inside AUTOFIRME_MCP_WORKSPACE.')
};

/** Load the operator keystore and record what happened, so a missing PIN is visible in the trail. */
export async function loadStore(config: ServerConfig): Promise<CertStore> {
	const store = await CertStore.load(config);
	for (const problem of store.errors) audit(config, { event: 'certstore', ok: false, error: problem });
	return store;
}

/** An MCP server with the three AutoFirma tools registered, transport-independent. */
export function buildServer(config: ServerConfig, store: CertStore, bridge?: BridgeHub): McpServer {
	const server = new McpServer({ name: 'autofirme', version: '0.0.0' });

	server.registerTool(
		'autofirma_list_certs',
		{
			title: 'List signing certificates',
			description:
				'Certificates available to sign, with their validity window. No key material is ever returned. Use the signable certificate\'s cert_id with autofirma_sign. When the AutoFirme extension bridge is connected these are the browser\'s saved identities.',
			inputSchema: {}
		},
		async () => {
			try {
				return json(bridge?.online ? await listBridgeCerts(config, bridge) : listCerts(config, store));
			} catch (error) {
				return failure(error);
			}
		}
	);

	server.registerTool(
		'autofirma_sign',
		{
			title: 'Sign a document',
		description:
			'Produce a signature with a loaded certificate: CAdES-BES is DETACHED (keep the document to verify); OOXML/XAdES/PAdES refuse honestly in this DOM-less build. When the extension bridge is connected, the operator confirms the signature in Chrome and no server-side certificate or PIN is needed.',
		inputSchema: {
			...SOURCE_FIELDS,
			format: z.enum(['cades', 'xades', 'pades', 'ooxml']).describe('Only cades signs in this build; the rest refuse with unsupported_format.'),
			cert_id: z.string().describe('cert_id from autofirma_list_certs; must be a signable certificate.'),
			signing_time: z.string().optional().describe('ISO 8601 signing time; defaults to now.')
		}
		},
		async (args) => {
			try {
				return json(await signDocument(config, store, args, bridge));
			} catch (error) {
				audit(config, {
					event: 'sign',
					ok: false,
					certId: args.cert_id,
					error: error instanceof ToolError ? error.code : 'internal',
					detail: (error as Error).message
				});
				return failure(error);
			}
		}
	);

	server.registerTool(
		'autofirma_verify',
		{
			title: 'Verify a signed artifact',
			description:
				'Check a CAdES artifact: structure, signature, content digest, signer certificate, validity window, chain to a trust anchor and signing time. Pass the original document as detached_b64/detached_path for detached signatures.',
			inputSchema: {
				...ARTIFACT_FIELDS,
				detached_b64: z.string().optional().describe('Original document, base64, for a detached signature.'),
				detached_path: z.string().optional().describe('Original document path, inside the workspace.')
			}
		},
		async (args) => {
			try {
				return json(await verifyDocument(config, args));
			} catch (error) {
				audit(config, {
					event: 'verify',
					ok: false,
					error: error instanceof ToolError ? error.code : 'internal',
					detail: (error as Error).message
				});
				return failure(error);
			}
		}
	);

	return server;
}
