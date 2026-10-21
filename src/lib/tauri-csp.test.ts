import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { describe, expect, it } from 'vitest';

/**
 * The shell's CSP is the only thing that decides whether a TSA the user typed in
 * (`src/lib/timestamp.ts:requestTimeStamp`) can be reached from the webview. Tauri has no
 * runtime surface to test here, so the config itself is the contract.
 */
const config: { app: { security: { csp: string } } } = JSON.parse(
	readFileSync(resolve(process.cwd(), 'src-tauri/tauri.conf.json'), 'utf8')
);

function sources(directive: string): string[] {
	const policy = config.app.security.csp;
	const found = policy
		.split(';')
		.map((part) => part.trim().split(/\s+/))
		.find(([name]) => name === directive);
	return found?.slice(1) ?? [];
}

describe('shell CSP connect-src', () => {
	it('permits the HTTP(S) TSA origins the timestamp client accepts', () => {
		expect(sources('connect-src')).toEqual(expect.arrayContaining(['https:', 'http:']));
	});

	it('keeps the IPC transport the Tauri commands need', () => {
		expect(sources('connect-src')).toEqual(expect.arrayContaining(['ipc:', 'http://ipc.localhost']));
	});
});
