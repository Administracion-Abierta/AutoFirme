/**
 * Server configuration, read once from the environment.
 *
 * Everything an agent can hand the server is bounded here, so the limits live in one place and
 * the tool layer only has to ask.
 */

import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';

/** 32 MiB: an order of magnitude above any invoice, CV or contract a person signs by hand. */
export const DEFAULT_MAX_BYTES = 32 * 1024 * 1024;

export interface ServerConfig {
	/** Directory of operator-preloaded `.p12` containers. `null` → signing is unavailable. */
	certDir: string | null;
	/** PIN for every container in `certDir`. Never logged, never echoed back to the agent. */
	pin: string | null;
	/** `path` inputs are resolved against this root and may not escape it. */
	workspace: string;
	/** Hard cap on any document, signed artifact or detached content the server will read. */
	maxBytes: number;
	/** Optional append-only JSONL audit log. Audit events always reach stderr regardless. */
	auditLog: string | null;
	/** Operator-supplied PEM trust anchors. `null` → the engine's bundled roots (FNMT). */
	trustedRoots: string[] | null;
	/** Bearer token for the remote transport. `null` → the HTTP entry refuses to start. */
	token: string | null;
	/** Interface the HTTP entry binds. Defaults to loopback: the tunnel terminates TLS in front. */
	host: string;
	port: number;
}

function positiveInt(raw: string | undefined, fallback: number, name: string): number {
	if (raw === undefined || raw === '') return fallback;
	const value = Number(raw);
	if (!Number.isSafeInteger(value) || value <= 0) {
		throw new Error(`${name} must be a positive integer, got ${JSON.stringify(raw)}`);
	}
	return value;
}

export function loadConfig(env: NodeJS.ProcessEnv = process.env): ServerConfig {
	const certDir = env.AUTOFIRME_MCP_CERTS?.trim();
	const rootsPath = env.AUTOFIRME_MCP_TRUSTED_ROOTS?.trim();
	return {
		certDir: certDir ? resolve(certDir) : null,
		pin: env.AUTOFIRME_MCP_PIN ?? null,
		workspace: resolve(env.AUTOFIRME_MCP_WORKSPACE?.trim() || process.cwd()),
		maxBytes: positiveInt(env.AUTOFIRME_MCP_MAX_BYTES, DEFAULT_MAX_BYTES, 'AUTOFIRME_MCP_MAX_BYTES'),
		auditLog: env.AUTOFIRME_MCP_AUDIT_LOG ? resolve(env.AUTOFIRME_MCP_AUDIT_LOG) : null,
		// Read once, at startup: a missing anchor file is a configuration error, not a verify result.
		trustedRoots: rootsPath ? [readFileSync(resolve(rootsPath), 'utf8')] : null,
		token: env.AUTOFIRME_MCP_TOKEN || null,
		host: env.AUTOFIRME_MCP_HOST?.trim() || '127.0.0.1',
		port: positiveInt(env.AUTOFIRME_MCP_PORT, 8790, 'AUTOFIRME_MCP_PORT')
	};
}
