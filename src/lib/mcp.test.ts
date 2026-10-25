/** The MCP endpoint preference: stored like the theme, and never a hard failure when storage is off. */

import { afterEach, describe, expect, it, vi } from 'vitest';
import { MCP_KEY, readMcp, saveMcp } from './mcp';

afterEach(() => {
	localStorage.clear();
	vi.restoreAllMocks();
});

describe('MCP endpoint preference', () => {
	it('round-trips the endpoint and the token', () => {
		saveMcp({ url: 'https://mcp.example.com/mcp', token: 'secreto', enabled: true });
		expect(localStorage.getItem(MCP_KEY)).toBe('{"url":"https://mcp.example.com/mcp","token":"secreto","enabled":true}');
		expect(readMcp()).toEqual({ url: 'https://mcp.example.com/mcp', token: 'secreto', enabled: true });
	});

	it('reads empty fields when nothing is stored or the stored value is not what it expects', () => {
		expect(readMcp()).toEqual({ url: '', token: '', enabled: false });
		for (const stored of ['no es json', '"una cadena"', '{}', '{"url":42}']) {
			localStorage.setItem(MCP_KEY, stored);
			expect(readMcp(), stored).toEqual({ url: '', token: '', enabled: false });
		}
	});

	it('does not throw when the browser denies storage', () => {
		vi.spyOn(Storage.prototype, 'setItem').mockImplementation(() => {
			throw new Error('storage denied');
		});
		vi.spyOn(Storage.prototype, 'getItem').mockImplementation(() => {
			throw new Error('storage denied');
		});
		expect(() => saveMcp({ url: 'https://x/mcp', token: 't', enabled: false })).not.toThrow();
		expect(readMcp()).toEqual({ url: '', token: '', enabled: false });
	});
});
