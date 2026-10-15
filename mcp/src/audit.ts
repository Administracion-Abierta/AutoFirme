/**
 * Audit trail for every signature this server produces.
 *
 * A signature is an act with legal weight, so who signed what, with which certificate and when
 * has to leave a trace even when the agent that asked for it is gone. Events carry identifiers
 * and digests only — never a PIN, never key material, never document contents.
 */

import { appendFileSync } from 'node:fs';
import type { ServerConfig } from './config';

export interface AuditEvent {
	/** `sign` | `verify` | `list_certs`. */
	event: string;
	ok: boolean;
	/** Certificate identifier, when one was used. Never the PIN. */
	certId?: string;
	format?: string;
	/** SHA-256 of the document, hex — identifies the payload without keeping it. */
	documentSha256?: string;
	bytes?: number;
	/** Failure code from a `ToolError`, or the error message for anything unexpected. */
	error?: string;
	detail?: string;
}

/** One JSON line per event: to stderr (always, advisory) and to the audit file (when configured). */
export function audit(config: ServerConfig, event: AuditEvent): void {
	const line = JSON.stringify({ ts: new Date().toISOString(), ...event });
	process.stderr.write(`${line}\n`);
	if (!config.auditLog) return;
	try {
		appendFileSync(config.auditLog, `${line}\n`, { mode: 0o600 });
	} catch (error) {
		// A broken audit file must not turn into a failed signature, but it must be loud.
		process.stderr.write(
			`${JSON.stringify({
				ts: new Date().toISOString(),
				event: 'audit_write_failed',
				ok: false,
				error: (error as Error).message
			})}\n`
		);
	}
}
