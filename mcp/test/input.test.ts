/** The two input channels, the size cap and the workspace jail. */

import { mkdirSync, symlinkSync, writeFileSync } from 'node:fs';
import { join } from 'node:path';
import { describe, expect, it } from 'vitest';
import { decodeBase64, isInside, pickSource, readPath, ToolError } from '../src/input';
import { configFor, tempDir } from './helpers';

/** The `ToolError` code a call rejects with — `failure(...)` reads as the expected code. */
async function failure(run: () => Promise<unknown>): Promise<string> {
	try {
		await run();
	} catch (error) {
		return error instanceof ToolError ? error.code : `not a ToolError: ${String(error)}`;
	}
	return 'no error';
}

describe('decodeBase64', () => {
	it('decodes a plain and a line-wrapped string', () => {
		expect(new TextDecoder().decode(decodeBase64('aGVsbG8=', 64))).toBe('hello');
		expect(new TextDecoder().decode(decodeBase64('aGVs\nbG8=', 64))).toBe('hello');
	});

	it('rejects text that is not base64', () => {
		expect(() => decodeBase64('aGVsbG8', 64)).toThrow(/not valid base64/);
		expect(() => decodeBase64('aGVs*bG8=', 64)).toThrow(/not valid base64/);
	});

	it('rejects on the encoded length before decoding, and on the decoded length after', () => {
		// 40 base64 characters cannot decode to fewer than 30 bytes.
		expect(() => decodeBase64('QUJD'.repeat(10), 4)).toThrow(/above the 4 byte limit/);
		// 8 characters, valid base64, but they decode to 5 bytes.
		expect(() => decodeBase64('QUJDREU=', 4)).toThrow(/above the 4 byte limit/);
	});
});

describe('pickSource', () => {
	it('rejects both channels and neither', async () => {
		expect(await failure(async () => pickSource({ file_b64: 'QQ==', path: 'a' }))).toBe('ambiguous_source');
		expect(await failure(async () => pickSource({}))).toBe('missing_source');
		expect(await failure(async () => pickSource({ file_b64: '', path: '' }))).toBe('missing_source');
	});

	it('returns the channel that was given', () => {
		expect(pickSource({ path: 'a.txt' })).toEqual({ kind: 'path', value: 'a.txt' });
		expect(pickSource({ file_b64: 'QQ==' })).toEqual({ kind: 'b64', value: 'QQ==' });
	});
});

describe('isInside', () => {
	it('accepts the root and its children, rejects prefixes and parents', () => {
		expect(isInside('/a/b', '/a/b')).toBe(true);
		expect(isInside('/a/b', '/a/b/c')).toBe(true);
		expect(isInside('/a/b', '/a/bc')).toBe(false);
		expect(isInside('/a/b', '/a')).toBe(false);
	});
});

describe('readPath', () => {
	it('reads a relative and an absolute path inside the workspace', async () => {
		const workspace = tempDir('autofirme-input-');
		writeFileSync(join(workspace, 'documento.txt'), 'hola');
		const config = configFor(workspace);
		expect(new TextDecoder().decode(await readPath(config, 'documento.txt'))).toBe('hola');
		expect(new TextDecoder().decode(await readPath(config, join(workspace, 'documento.txt')))).toBe('hola');
	});

	it('rejects a parent escape, a symlink escape and an unrelated absolute path', async () => {
		const root = tempDir('autofirme-input-');
		const workspace = join(root, 'workspace');
		mkdirSync(workspace);
		writeFileSync(join(root, 'fuera.txt'), 'secreto');
		symlinkSync(join(root, 'fuera.txt'), join(workspace, 'atajo.txt'));
		const config = configFor(workspace);
		expect(await failure(() => readPath(config, '../fuera.txt'))).toBe('path_escape');
		expect(await failure(() => readPath(config, 'atajo.txt'))).toBe('path_escape');
		expect(await failure(() => readPath(config, join(root, 'fuera.txt')))).toBe('path_escape');
	});

	it('rejects a missing path, a directory and an oversized file', async () => {
		const workspace = tempDir('autofirme-input-');
		mkdirSync(join(workspace, 'carpeta'));
		writeFileSync(join(workspace, 'grande.bin'), new Uint8Array(9));
		expect(await failure(() => readPath(configFor(workspace), 'no-existe.txt'))).toBe('path_not_found');
		expect(await failure(() => readPath(configFor(workspace), 'carpeta'))).toBe('not_a_file');
		expect(await failure(() => readPath(configFor(workspace, { maxBytes: 8 }), 'grande.bin'))).toBe('too_large');
	});
});
