import { afterEach, describe, expect, it, vi } from 'vitest';

afterEach(() => {
	vi.unstubAllGlobals();
	vi.restoreAllMocks();
	vi.doUnmock('@tauri-apps/api/core');
	vi.doUnmock('@tauri-apps/api/event');
});

describe('classifyIncoming', () => {
	it('accepts gated requests and refuses the rest', async () => {
		const { classifyIncoming } = await import('./deeplink');
		const gated = { op: 'sign', origin: 'sede.example.es', allowed: false, params: [] };
		expect(classifyIncoming(gated)).toEqual({ gated });
		const refused = { raw: 'afirma://x', reason: 'bad op' };
		expect(classifyIncoming(refused)).toEqual({ refused });
		expect(classifyIncoming({ op: 'sign' })).toBeNull();
		expect(classifyIncoming(null)).toBeNull();
	});
});

describe('bridge outside the desktop shell', () => {
	it('returns safe defaults and never loads Tauri', async () => {
		vi.stubGlobal('window', {});
		const bridge = await import('./deeplink');
		await expect(bridge.deeplinkStatus()).resolves.toBeNull();
		await expect(bridge.deeplinkList()).resolves.toEqual([]);
		await expect(bridge.deeplinkRemove('sede.example.es')).resolves.toBe(false);
		await expect(
			bridge.deeplinkConfirm({ op: 'sign', origin: null, allowed: false, params: [] }, true, false)
		).resolves.toBe(false);
		const detach = await bridge.listenDeepLinks(() => {});
		expect(detach()).toBeUndefined();
	});
});

describe('bridge on desktop', () => {
	it('invokes commands with exact args', async () => {
		vi.stubGlobal('window', { __TAURI_INTERNALS__: {} });
		const invoke = vi.fn(async () => true);
		vi.doMock('@tauri-apps/api/core', () => ({ invoke }));
		const { deeplinkConfirm } = await import('./deeplink');
		const gated = { op: 'sign', origin: 'sede.example.es', allowed: false, params: [] as [string, string][] };
		await expect(deeplinkConfirm(gated, true, true)).resolves.toBe(true);
		expect(invoke).toHaveBeenCalledWith('deeplink_confirm', { gated, allow: true, remember: true });
	});

	it('forwards only well-formed shell events', async () => {
		vi.stubGlobal('window', { __TAURI_INTERNALS__: {} });
		vi.doMock('@tauri-apps/api/core', () => ({ invoke: vi.fn() }));
		let handler: ((event: { payload: unknown }) => void) | null = null;
		vi.doMock('@tauri-apps/api/event', () => ({
			listen: vi.fn(async (_event: string, fn: (event: { payload: unknown }) => void) => {
				handler = fn;
				return () => {};
			})
		}));
		const { listenDeepLinks } = await import('./deeplink');
		const seen: unknown[] = [];
		await listenDeepLinks((request) => seen.push(request));
		handler!({ payload: { op: 'sign', origin: 'sede.example.es', allowed: true, params: [] } });
		handler!({ payload: { nope: 1 } });
		expect(seen).toHaveLength(1);
	});
});

describe('payloadFromGated', () => {
	const gated = (params: [string, string][], op = 'sign') => ({
		op,
		origin: 'sede.example.es',
		allowed: true,
		params
	});

	it('decodes base64 dat and maps the portal format', async () => {
		const { payloadFromGated } = await import('./deeplink');
		const payload = await payloadFromGated(
			gated([
				['dat', 'cG9ydGFsLWRvYw=='],
				['format', 'CAdES'],
				['filename', '../../etc/passwd']
			])
		);
		expect(payload).not.toBeNull();
		expect(payload!.format).toBe('cades');
		expect(payload!.file.name).toBe('..-..-etc-passwd');
		expect(Array.from(new Uint8Array(await payload!.file.arrayBuffer()))).toEqual(
			Array.from(new TextEncoder().encode('portal-doc'))
		);
	});

	it('inflates gzip payloads', async () => {
		const { payloadFromGated } = await import('./deeplink');
		const payload = await payloadFromGated(
			gated([
				['dat', 'H4sIAAAAAAAAAyvILypJzNFNyU8GAGjUedQKAAAA'],
				['gzip', 'true']
			])
		);
		expect(payload).not.toBeNull();
		expect(payload!.format).toBeNull();
		expect(payload!.file.name).toBe('documento');
		expect(Array.from(new Uint8Array(await payload!.file.arrayBuffer()))).toEqual(
			Array.from(new TextEncoder().encode('portal-doc'))
		);
	});

	it('refuses malformed, absent, or non-signing payloads', async () => {
		const { payloadFromGated } = await import('./deeplink');
		await expect(payloadFromGated(gated([['dat', '@@@not-base64@@@']]))).resolves.toBeNull();
		await expect(payloadFromGated(gated([['dat', 'cG9ydGFsLWRvYw=='], ['gzip', 'true']]))).resolves.toBeNull();
		await expect(payloadFromGated(gated([['fileid', 'abc']]))).resolves.toBeNull();
		await expect(payloadFromGated(gated([['dat', 'cG9ydGFsLWRvYw==']], 'selectcert'))).resolves.toBeNull();
	});
});
