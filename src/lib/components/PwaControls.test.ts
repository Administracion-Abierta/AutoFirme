/**
 * PWA header controls (#47): the install button driven by `beforeinstallprompt`, and the update
 * button driven by a service worker waiting to take over.
 */

import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen } from '@testing-library/svelte';
import { copy } from '#lib/i18n';
import PwaControls from './PwaControls.svelte';

afterEach(() => {
	cleanup();
	// The container stub used by the update test must not leak into the ones that follow it.
	Reflect.deleteProperty(navigator, 'serviceWorker');
});

describe('pwa controls', () => {
	it('stays silent until the browser offers an install', async () => {
		render(PwaControls, { copy: copy.es });
		expect(screen.queryByRole('button', { name: copy.es.installApp })).toBeNull();
	});

	it('shows the install button, cancels the prompt event and replays it on click', async () => {
		render(PwaControls, { copy: copy.es });

		const prompt = vi.fn(async () => {});
		const event = new Event('beforeinstallprompt', { cancelable: true });
		Object.assign(event, { prompt, userChoice: Promise.resolve({ outcome: 'accepted' }) });
		window.dispatchEvent(event);

		const button = await screen.findByRole('button', { name: copy.es.installApp });
		await fireEvent.click(button);

		expect(event.defaultPrevented).toBe(true);
		expect(prompt).toHaveBeenCalledTimes(1);
		// A deferred prompt is single-use: the button retires with it instead of erroring twice.
		expect(screen.queryByRole('button', { name: copy.es.installApp })).toBeNull();
	});

	it('offers the waiting worker an activation when a new version has been downloaded', async () => {
		const postMessage = vi.fn();
		Object.defineProperty(navigator, 'serviceWorker', {
			configurable: true,
			value: {
				controller: {},
				getRegistration: async () => ({
					waiting: { state: 'installed', postMessage },
					installing: null,
					addEventListener: () => {}
				}),
				addEventListener: () => {},
				removeEventListener: () => {}
			}
		});
		render(PwaControls, { copy: copy.es });

		const button = await screen.findByRole('button', { name: copy.es.updateApp });
		await fireEvent.click(button);

		expect(postMessage).toHaveBeenCalledWith({ type: 'SKIP_WAITING' });
	});
	it('offers no update button when there is no registration', async () => {
		const registration = Promise.resolve(null);
		Object.defineProperty(navigator, 'serviceWorker', {
			configurable: true,
			value: {
				controller: null,
				getRegistration: () => registration,
				addEventListener: () => {},
				removeEventListener: () => {}
			}
		});
		render(PwaControls, { copy: copy.es });

		// The component chains on this exact promise, so resolving it is the awaited signal —
		// no guessed delay.
		await registration;
		expect(screen.queryByRole('button', { name: copy.es.updateApp })).toBeNull();
	});
	/**
	 * The first worker `clients.claim()`s the page, which also fires `controllerchange` —
	 * reloading there would wipe what a new visitor typed seconds after arrival. Only a later
	 * takeover (an update) reloads, and only once.
	 */
	it('reloads when a worker takes over a controlled page, not when the first one claims it', async () => {
		const listeners: Array<[string, () => void]> = [];
		Object.defineProperty(navigator, 'serviceWorker', {
			configurable: true,
			value: {
				controller: null,
				getRegistration: async () => null,
				addEventListener: (type: string, listener: () => void) => listeners.push([type, listener]),
				removeEventListener: () => {}
			}
		});
		const reload = vi.fn();
		vi.stubGlobal('location', { reload });
		try {
			render(PwaControls, { copy: copy.es });
			await vi.waitFor(() => expect(listeners.some(([type]) => type === 'controllerchange')).toBe(true));

			const claim = () =>
				listeners.filter(([type]) => type === 'controllerchange').forEach(([, listener]) => listener());
			claim(); // first install claiming the page: no reload
			expect(reload).not.toHaveBeenCalled();
			claim(); // an update taking over: reload
			expect(reload).toHaveBeenCalledTimes(1);
			claim(); // duplicated event: still one reload
			expect(reload).toHaveBeenCalledTimes(1);
		} finally {
			vi.unstubAllGlobals();
		}
	});
});
