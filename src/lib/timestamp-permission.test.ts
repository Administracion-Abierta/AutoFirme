import { afterEach, expect, it, vi } from 'vitest';
import { requestTimeStamp } from './timestamp';
afterEach(() => vi.unstubAllGlobals());
it('does not contact the TSA in the extension before its host permission is granted', async () => {
  vi.stubGlobal('location', { protocol: 'chrome-extension:' });
  const contains = vi.fn(async () => false);
  const fetch = vi.fn();
  vi.stubGlobal('chrome', { permissions: { contains } });
  vi.stubGlobal('fetch', fetch);
  await expect(requestTimeStamp('https://tsa.example/path', new Uint8Array([1]))).rejects.toThrow(/permission/i);
  expect(contains).toHaveBeenCalledWith({ origins: ['https://tsa.example/*'] });
  expect(fetch).not.toHaveBeenCalled();
});
it('fails closed if the extension permissions API is unavailable', async () => {
  vi.stubGlobal('location', { protocol: 'chrome-extension:' });
  vi.stubGlobal('chrome', undefined);
  const fetch = vi.fn();
  vi.stubGlobal('fetch', fetch);
  await expect(requestTimeStamp('https://tsa.example/path', new Uint8Array([1]))).rejects.toThrow(/permission/i);
  expect(fetch).not.toHaveBeenCalled();
});
it('permits a granted TSA and lets the normal HTTP response handling run', async () => {
  vi.stubGlobal('location', { protocol: 'chrome-extension:' });
  vi.stubGlobal('chrome', { permissions: { contains: vi.fn(async () => true) } });
  const fetch = vi.fn(async () => ({ ok: false, status: 503 }));
  vi.stubGlobal('fetch', fetch);
  await expect(requestTimeStamp('https://tsa.example/path', new Uint8Array([1]))).rejects.toThrow('HTTP 503');
  expect(fetch).toHaveBeenCalledOnce();
});
it('rejects non-HTTP TSA schemes without fetching', async () => {
  vi.stubGlobal('location', { protocol: 'chrome-extension:' });
  const fetch = vi.fn();
  vi.stubGlobal('fetch', fetch);
  await expect(requestTimeStamp('file:///secret', new Uint8Array([1]))).rejects.toThrow('HTTP(S)');
  expect(fetch).not.toHaveBeenCalled();
});
