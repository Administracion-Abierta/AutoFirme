import { afterEach, expect, it, vi } from 'vitest';
import { cleanup, render, screen } from '@testing-library/svelte';
import PwaControls from './PwaControls.svelte';
import { copy } from '#lib/i18n';
afterEach(() => { cleanup(); vi.unstubAllGlobals(); Reflect.deleteProperty(navigator, 'serviceWorker'); });
it('does not observe PWA install events or service workers in extension pages', () => {
  vi.stubGlobal('location', { protocol: 'chrome-extension:' });
  const getRegistration = vi.fn();
  Object.defineProperty(navigator, 'serviceWorker', { configurable: true, value: { getRegistration } });
  render(PwaControls, { copy: copy.es });
  window.dispatchEvent(Object.assign(new Event('beforeinstallprompt'), { prompt: vi.fn() }));
  expect(getRegistration).not.toHaveBeenCalled();
  expect(screen.queryByRole('button')).toBeNull();
});
