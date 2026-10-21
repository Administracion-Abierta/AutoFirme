import { afterEach, describe, expect, it, vi } from 'vitest';

const BYTES = new Uint8Array([1, 2, 3]);

function stubAnchor() {
	const click = vi.fn();
	const link = { click, href: '', download: '' } as unknown as HTMLAnchorElement;
	vi.spyOn(document, 'createElement').mockReturnValue(link);
	vi.spyOn(URL, 'createObjectURL').mockReturnValue('blob:x');
	vi.spyOn(URL, 'revokeObjectURL').mockImplementation(() => {});
	return { click };
}

afterEach(() => {
	vi.unstubAllGlobals();
	vi.restoreAllMocks();
	vi.doUnmock('@tauri-apps/api/core');
});

describe('saveBytes in the browser', () => {
	it('downloads via anchor and never touches Tauri', async () => {
		const { click } = stubAnchor();
		const { saveBytes } = await import('./save-file');
		await expect(saveBytes('a.zip', BYTES as Uint8Array<ArrayBuffer>, 'application/zip')).resolves
			.toBeNull();
		expect(click).toHaveBeenCalledOnce();
	});
});

describe('saveBytes on desktop', () => {
	it('invokes save_file with exact bytes and returns the path', async () => {
		vi.stubGlobal('window', { __TAURI_INTERNALS__: {} });
		const invoke = vi.fn(async () => '/tmp/a.zip');
		vi.doMock('@tauri-apps/api/core', () => ({ invoke }));
		const { saveBytes } = await import('./save-file');
		await expect(saveBytes('a.zip', BYTES as Uint8Array<ArrayBuffer>, 'application/zip')).resolves
			.toBe('/tmp/a.zip');
		expect(invoke).toHaveBeenCalledWith('save_file', {
			suggestedName: 'a.zip',
			bytes: [1, 2, 3]
		});
	});

	it('surfaces a cancel as null without throwing', async () => {
		vi.stubGlobal('window', { __TAURI_INTERNALS__: {} });
		vi.doMock('@tauri-apps/api/core', () => ({ invoke: vi.fn(async () => null) }));
		const { saveBytes } = await import('./save-file');
		await expect(saveBytes('a.zip', BYTES as Uint8Array<ArrayBuffer>, 'application/zip')).resolves
			.toBeNull();
	});
});
