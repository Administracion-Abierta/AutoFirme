// `configure` must come from the SAME @testing-library/dom instance the bound queries use.
// @testing-library/svelte re-exports it from its own store-local dom copy; a bare
// '@testing-library/dom' import here would resolve to a different copy and be ignored.
import { configure } from '@testing-library/svelte';

// Under the full suite's parallel files, async UI waits (findByRole/waitFor) can outlast
// testing-library's 1s default while a PBKDF2 seal or PKCS#12 unlock finishes. The allowance is
// global (each per-file `testTimeout` still bounds the work); assertions are unchanged.
configure({ asyncUtilTimeout: 15_000 });

import { webcrypto } from 'node:crypto';

// jsdom's `crypto` only has getRandomValues; the crypto work here (and in the browser) needs WEB
// SubtleCrypto. Node's implementation is the same API the browsers expose.
if (!globalThis.crypto?.subtle) {
	Object.defineProperty(globalThis, 'crypto', { value: webcrypto, configurable: true, writable: true });
}

// jsdom exposes `<dialog>` and its `open` attribute but none of its methods, so `showModal()` is
// `undefined` and a component that opens a dialog throws before doing anything else. Implement the
// three methods with the platform's own rules (a second `showModal()` on an open dialog throws).
const dialog = globalThis.HTMLDialogElement?.prototype;
if (dialog && !dialog.showModal) {
	Object.defineProperties(dialog, {
		show: {
			value(this: HTMLDialogElement) {
				this.setAttribute('open', '');
			}
		},
		showModal: {
			value(this: HTMLDialogElement) {
				if (this.hasAttribute('open')) {
					throw new DOMException('the dialog is already open', 'InvalidStateError');
				}
				this.setAttribute('open', '');
			}
		},
		close: {
			value(this: HTMLDialogElement, returnValue?: string) {
				if (returnValue !== undefined) this.returnValue = returnValue;
				this.removeAttribute('open');
			}
		}
	});
}
