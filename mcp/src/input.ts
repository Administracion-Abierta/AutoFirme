/**
 * Where the bytes for a tool call come from.
 *
 * Two channels, exactly one per call: `file_b64` (the agent already holds the document) and
 * `path` (the agent and the server share a machine). `path` is the dangerous one — it is confined
 * to `config.workspace` after `realpath`, so neither `../..` nor a symlink can walk out of it.
 */

import { realpath, stat, readFile } from 'node:fs/promises';
import { isAbsolute, relative, resolve } from 'node:path';
import type { ServerConfig } from './config';

/** Engine byte type: `ArrayBuffer`-backed, so it can be handed straight to `crypto.subtle`. */
export type Bytes = Uint8Array<ArrayBuffer>;

export class ToolError extends Error {
	readonly code: string;

	constructor(code: string, message: string) {
		super(message);
		this.name = 'ToolError';
		this.code = code;
	}
}

export interface Source {
	file_b64?: string;
	path?: string;
}

const BASE64 = /^[A-Za-z0-9+/]*={0,2}$/;
/** A document that cannot possibly fit the cap, judged from its base64 length alone. */
const maxEncodedLength = (maxBytes: number): number => Math.ceil(maxBytes / 3) * 4 + 8;

function tooBig(what: string, size: number, maxBytes: number): ToolError {
	return new ToolError(
		'too_large',
		`${what} is ${size} bytes, above the ${maxBytes} byte limit (AUTOFIRME_MCP_MAX_BYTES)`
	);
}

export function decodeBase64(text: string, maxBytes: number, what = 'document'): Bytes {
	const compact = text.replace(/\s+/g, '');
	if (compact.length > maxEncodedLength(maxBytes)) {
		throw tooBig(what, Math.floor((compact.length * 3) / 4), maxBytes);
	}
	if (compact.length % 4 !== 0 || !BASE64.test(compact)) {
		throw new ToolError('bad_base64', `${what} is not valid base64`);
	}
	// `Buffer` decodes to a pooled allocation; copy into a plain `ArrayBuffer`-backed view so the
	// result is exactly the engine's `Bytes` and carries no pool slack.
	const decoded = new Uint8Array(Buffer.from(compact, 'base64'));
	if (decoded.length > maxBytes) throw tooBig(what, decoded.length, maxBytes);
	return decoded;
}

/** True when `target` is `root` itself or sits underneath it. Both must already be resolved. */
export function isInside(root: string, target: string): boolean {
	if (target === root) return true;
	const rel = relative(root, target);
	return rel !== '' && !rel.startsWith('..') && !isAbsolute(rel);
}

/**
 * Resolve `input.path` under the workspace. Both sides go through `realpath`, so a symlink
 * pointing outside the workspace is rejected by the same check that rejects `../../etc/passwd`.
 */
export async function readPath(config: ServerConfig, input: string): Promise<Bytes> {
	const candidate = isAbsolute(input) ? input : resolve(config.workspace, input);
	let root: string;
	let target: string;
	try {
		[root, target] = await Promise.all([realpath(config.workspace), realpath(candidate)]);
	} catch {
		throw new ToolError('path_not_found', `cannot resolve ${JSON.stringify(input)} under the workspace`);
	}
	if (!isInside(root, target)) {
		throw new ToolError(
			'path_escape',
			`${JSON.stringify(input)} resolves outside the workspace ${JSON.stringify(root)}`
		);
	}
	const info = await stat(target);
	if (!info.isFile()) throw new ToolError('not_a_file', `${JSON.stringify(input)} is not a regular file`);
	if (info.size > config.maxBytes) throw tooBig('document', info.size, config.maxBytes);

	const buffer = await readFile(target);
	if (buffer.length > config.maxBytes) throw tooBig('document', buffer.length, config.maxBytes);
	// `readFile` always returns a fresh, exclusively-owned `ArrayBuffer`, so this is a copy for
	// the type's sake alone; `Bytes` is what `crypto.subtle` accepts without further narrowing.
	return new Uint8Array(buffer);
}

/** Exactly one channel per call — accepting both would make "which bytes won" a guessing game. */
export function pickSource(source: Source, what = 'document'): { kind: 'b64'; value: string } | { kind: 'path'; value: string } {
	const hasB64 = typeof source.file_b64 === 'string' && source.file_b64 !== '';
	const hasPath = typeof source.path === 'string' && source.path !== '';
	if (hasB64 && hasPath) {
		throw new ToolError('ambiguous_source', `${what}: pass either file_b64 or path, not both`);
	}
	if (!hasB64 && !hasPath) {
		throw new ToolError('missing_source', `${what}: pass file_b64 or path`);
	}
	return hasB64 ? { kind: 'b64', value: source.file_b64! } : { kind: 'path', value: source.path! };
}

export async function readSource(config: ServerConfig, source: Source, what = 'document'): Promise<Bytes> {
	const picked = pickSource(source, what);
	return picked.kind === 'b64'
		? decodeBase64(picked.value, config.maxBytes, what)
		: readPath(config, picked.value);
}
