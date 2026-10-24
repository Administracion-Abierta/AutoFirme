/**
 * Certificate picker (#29 + sign-panel rework): the trigger button, the saved-signatures modal
 * (grid of stored identities + "Agregar firma"), the seal-on-add flow inside the modal, the PIN +
 * load flow, the store's save form and the click-to-select behaviour of a saved sign.
 * The vault runs over the in-memory backend; the panel is the real component.
 */

import { rmSync } from 'node:fs';
import { afterAll, afterEach, beforeAll, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor, within } from '@testing-library/svelte';
import type { Bytes } from '#lib/asn1';
import { createVault, memoryBackend, type Vault } from '#lib/certstore';
import { copy } from '#lib/i18n';
import { loadSignerSession } from '#lib/keystore';
import type { SignerCert, SignerSession } from '#lib/sign';
import {
	ASYNC,
	createPkcs12Fixtures,
	FIXTURE_PIN,
	LEAF_COMMON_NAME,
	type Pkcs12Fixtures
} from '../../test/fixtures';
import CertPicker from './CertPicker.svelte';

// Sealing and unlocking run PBKDF2 with 250k iterations; when the suite runs its files in
// parallel they take long enough to trip the 5s default, so the waits below need the headroom.
vi.setConfig({ testTimeout: 20_000 });

let fixtures: Pkcs12Fixtures;
let session: SignerSession;

beforeAll(async () => {
	fixtures = createPkcs12Fixtures();
	session = await loadSignerSession(fixtures.modern, FIXTURE_PIN);
}, 120_000);

afterAll(() => rmSync(fixtures.dir, { recursive: true, force: true }));

afterEach(cleanup);

function mount(vault: Vault, onpick = vi.fn()) {
	const rendered = render(CertPicker, {
		props: {
			copy: copy.es,
			cert: null,
			vault,
			onpick: onpick as (cert: SignerCert | null, chain: readonly SignerCert[]) => void
		}
	});
	return { ...rendered, onpick };
}

async function field(id: string, value: string) {
	await fireEvent.input(document.querySelector<HTMLInputElement>(`#${id}`)!, {
		target: { value }
	});
}

async function openPicker() {
	await fireEvent.click(trigger());
}

/** The trigger button; its label is the picked file's name or the unlocked sign's label. */
function trigger(): HTMLButtonElement {
	return document.querySelector<HTMLButtonElement>('#pkcs12-picker')!;
}

describe('certificate picker', () => {
	it('names the trigger button after the picked file and lets the modal add one', async () => {
		mount(createVault(memoryBackend()));
		const input = document.querySelector<HTMLInputElement>('#pkcs12-file')!;
		expect(trigger().textContent).toContain(copy.es.selectSign);

		// "Agregar firma" is how the modal reaches the hidden file input.
		const click = vi.spyOn(input, 'click');
		await openPicker();
		await fireEvent.click(screen.getByRole('button', { name: new RegExp(copy.es.addSign) }));
		expect(click).toHaveBeenCalled();

		await fireEvent.change(input, {
			target: { files: [new File([fixtures.modern], 'despacho.p12')] }
		});
		expect(trigger().textContent).toContain('despacho.p12');
	});

	it('lists what a previous visit stored and hands the unlocked session to the picker', async () => {
		const vault = createVault(memoryBackend());
		await vault.saveIdentity({
			label: 'Trabajo',
			session,
			container: fixtures.modern,
			pin: FIXTURE_PIN
		});
		const { onpick } = mount(vault);

		await openPicker();
		await waitFor(() => expect(screen.getByRole('button', { name: /Trabajo/ })).toBeTruthy(), ASYNC);
		await fireEvent.click(screen.getByRole('button', { name: /Trabajo/ }));

		// One PIN per saved signature: it unseals the record and opens the container inside it. The
		// click alone asked for that PIN — the grid steps aside for the prompt ("Agregar firma" and
		// the other cells go with it).
		expect(screen.queryByRole('button', { name: new RegExp(copy.es.addSign) })).toBeNull();
		await field('store-pin', FIXTURE_PIN);
		await fireEvent.click(screen.getByRole('button', { name: copy.es.storeOpen }));

		await waitFor(() => expect(onpick).toHaveBeenCalled(), { timeout: 15_000 });
		const [cert, chain] = onpick.mock.calls[0] as [SignerCert, SignerCert[]];
		expect(cert.commonName).toBe(LEAF_COMMON_NAME);
		expect(cert.key).not.toBeNull();
		expect(chain).toHaveLength(session.certificates.length);
		// The trigger button now carries the saved sign's name; the certificate line comes from the
		// app's own `cert` prop, so it is asserted in the App-level test.
		expect(trigger().textContent).toContain('Trabajo');
		// Selecting a signature is the whole point of the click: nothing is left to confirm.
		expect(screen.queryByRole('button', { name: copy.es.storeOpen })).toBeNull();
	});

	it('selects the signature already in use and closes without asking for its PIN again', async () => {
		const vault = createVault(memoryBackend());
		await vault.saveIdentity({
			label: 'Trabajo',
			session,
			container: fixtures.modern,
			pin: FIXTURE_PIN
		});
		const { onpick } = mount(vault);

		await openPicker();
		await fireEvent.click(await screen.findByRole('button', { name: /Trabajo/ }));
		await field('store-pin', FIXTURE_PIN);
		await fireEvent.click(screen.getByRole('button', { name: copy.es.storeOpen }));
		await waitFor(() => expect(onpick).toHaveBeenCalledOnce(), { timeout: 15_000 });

		// Back to the grid: the sign in use is marked, and clicking it selects it without a PIN. The
		// trigger carries the same label by now, so the click is scoped to the modal.
		await openPicker();
		const modal = await screen.findByRole('dialog');
		await fireEvent.click(within(modal).getByRole('button', { name: /Trabajo/ }));
		expect(within(modal).queryByText(copy.es.storePin)).toBeNull();
		expect(within(modal).queryByRole('button', { name: copy.es.storeOpen })).toBeNull();
	});

	// Compact mode has no dialog focus trap: the pressed row unmounts when the PIN form opens and
	// the applied sign leaves the list, so focus must follow into the nodes that survive each step.
	it('keeps compact keyboard focus on the PIN form and the applied selection', async () => {
		const vault = createVault(memoryBackend());
		await vault.saveIdentity({
			label: 'Trabajo',
			session,
			container: fixtures.modern,
			pin: FIXTURE_PIN
		});
		const onpick = vi.fn();
		const { rerender } = render(CertPicker, {
			props: { copy: copy.es, cert: null, vault, compact: true, onpick }
		});

		await waitFor(() => expect(screen.getByRole('button', { name: /Trabajo/ })).toBeTruthy(), ASYNC);
		await fireEvent.click(screen.getByRole('button', { name: /Trabajo/ }));
		await waitFor(() => expect(document.activeElement?.id).toBe('store-pin'));

		await field('store-pin', FIXTURE_PIN);
		await fireEvent.click(screen.getByRole('button', { name: copy.es.storeOpen }));
		await waitFor(() => expect(onpick).toHaveBeenCalled(), { timeout: 15_000 });
		await rerender({ cert: onpick.mock.calls[0][0] });
		await waitFor(() => expect(document.activeElement?.id).toBe('pkcs12-selection'));
	});

	it('says the saved signature could not be opened with that PIN', async () => {
		const vault = createVault(memoryBackend());
		await vault.saveIdentity({
			label: 'Trabajo',
			session,
			container: fixtures.modern,
			pin: FIXTURE_PIN
		});
		mount(vault);

		await openPicker();
		await waitFor(() => expect(screen.getByRole('button', { name: /Trabajo/ })).toBeTruthy(), ASYNC);
		await fireEvent.click(screen.getByRole('button', { name: /Trabajo/ }));
		await field('store-pin', 'no-es-este');

		await fireEvent.click(screen.getByRole('button', { name: copy.es.storeOpen }));

		await waitFor(
			() => expect(screen.getByRole('alert').textContent).toBe(copy.es.storeErrors.tampered),
			ASYNC
		);
	});

	it('names the withheld WebCrypto for what it is when the page is insecure', async () => {
		// http away from localhost: the browser hides crypto.subtle and every load dies with a
		// TypeError — the message must say so instead of blaming the file.
		const descriptor = Object.getOwnPropertyDescriptor(globalThis.crypto, 'subtle');
		Object.defineProperty(globalThis.crypto, 'subtle', { value: undefined, configurable: true });
		try {
			mount(createVault(memoryBackend()));
			await openPicker();
			await fireEvent.change(document.querySelector<HTMLInputElement>('#pkcs12-file')!, {
				target: { files: [new File([fixtures.modern], 'keystore.p12')] }
			});
			await field('add-pin', FIXTURE_PIN);
			await fireEvent.click(screen.getByRole('button', { name: copy.es.storeSave }));
			await waitFor(
				() => expect(screen.getByRole('alert').textContent).toBe(copy.es.insecureContext),
				ASYNC
			);
		} finally {
			if (descriptor) Object.defineProperty(globalThis.crypto, 'subtle', descriptor);
			else Reflect.deleteProperty(globalThis.crypto, 'subtle');
		}
	});

	it('says so when the store cannot keep anything, and offers to reset it', async () => {
		// A vault that is not persistent is what the picker sees when the browser database refused
		// to open: the notice has to appear, or the user finds out only after losing the signatures.
		mount(createVault(memoryBackend(), false));

		await openPicker();
		const modal = await screen.findByRole('dialog');
		await waitFor(() => expect(within(modal).getByRole('alert')).toBeTruthy());
		expect(within(modal).getByRole('alert').textContent).toBe(copy.es.storeEphemeral);
		expect(within(modal).getByRole('button', { name: copy.es.storeReset })).toBeTruthy();
	});

	it('forgets a stored identity', async () => {
		const vault = createVault(memoryBackend());
		await vault.saveIdentity({
			label: 'Trabajo',
			session,
			container: fixtures.modern,
			pin: FIXTURE_PIN
		});
		mount(vault);

		await openPicker();
		await waitFor(() => expect(screen.getByRole('button', { name: /Trabajo/ })).toBeTruthy(), ASYNC);
		await fireEvent.click(screen.getByRole('button', { name: copy.es.remove }));

		await waitFor(() => expect(screen.getByText(copy.es.storeEmpty)).toBeTruthy());
		expect(await vault.listIdentities()).toEqual([]);
	});

	it('renames a stored signature from the modal', async () => {
		const vault = createVault(memoryBackend());
		await vault.saveIdentity({
			label: 'despacho.p12',
			session,
			container: fixtures.modern,
			pin: FIXTURE_PIN
		});
		mount(vault);

		await openPicker();
		const modal = await screen.findByRole('dialog');
		await waitFor(
			() => expect(within(modal).getByRole('button', { name: /despacho\.p12/ })).toBeTruthy(),
			ASYNC
		);

		await fireEvent.click(within(modal).getByRole('button', { name: copy.es.storeRename }));
		await fireEvent.input(within(modal).getByLabelText(copy.es.storeAlias), {
			target: { value: 'Firma personal' }
		});
		await fireEvent.click(within(modal).getByRole('button', { name: copy.es.storeSave }));

		await waitFor(
			() => expect(within(modal).getByRole('button', { name: /Firma personal/ })).toBeTruthy(),
			ASYNC
		);
		expect((await vault.listIdentities())[0].label).toBe('Firma personal');
	});

	it('seals every sign added from the modal and keeps them all in the store', async () => {
		const vault = createVault(memoryBackend());
		mount(vault);

		await openPicker();
		const add = async (bytes: Bytes, name: string) => {
			await fireEvent.click(screen.getByRole('button', { name: new RegExp(copy.es.addSign) }));
			await fireEvent.change(document.querySelector<HTMLInputElement>('#pkcs12-file')!, {
				target: { files: [new File([bytes], name)] }
			});
			await field('add-pin', FIXTURE_PIN);
			await fireEvent.click(screen.getByRole('button', { name: copy.es.storeSave }));
		};

		// A successful seal selects the sign and closes the modal; the grid waits behind it.
		await add(fixtures.modern, 'primera.p12');
		await waitFor(() => expect(screen.queryByRole('dialog')).toBeNull(), ASYNC);
		await openPicker();
		let modal = await screen.findByRole('dialog');
		// The saved cell is the button the grid renders once the record exists — waiting for it
		// also waits out the seal.
		await waitFor(
			() => expect(within(modal).getByRole('button', { name: /primera\.p12/ })).toBeTruthy(),
			ASYNC
		);

		await add(fixtures.expired, 'segunda.p12');
		await waitFor(() => expect(screen.queryByRole('dialog')).toBeNull(), ASYNC);
		await openPicker();
		modal = await screen.findByRole('dialog');
		await waitFor(
			() => expect(within(modal).getByRole('button', { name: /segunda\.p12/ })).toBeTruthy(),
			ASYNC
		);

		// The second add joined the grid instead of replacing the first, and both were sealed:
		// a label per upload, in the store, outliving this visit.
		expect(within(modal).getByText('primera.p12')).toBeTruthy();
		const stored = await vault.listIdentities();
		expect(stored.map((entry) => entry.label).sort()).toEqual(['primera.p12', 'segunda.p12']);
		expect(stored.map((entry) => entry.commonName).sort()).toEqual(
			[LEAF_COMMON_NAME, 'Caducada Prueba'].sort()
		);
		// The sign just added is the one in use.
		expect(trigger().textContent).toContain('segunda.p12');
	});

	it('selects a sign already opened this visit with one click and closes the modal', async () => {
		const vault = createVault(memoryBackend());
		await vault.saveIdentity({
			label: 'Trabajo',
			session,
			container: fixtures.modern,
			pin: FIXTURE_PIN
		});
		const { onpick } = mount(vault);

		await openPicker();
		await fireEvent.click(await screen.findByRole('button', { name: /Trabajo/ }));
		await field('store-pin', FIXTURE_PIN);
		await fireEvent.click(screen.getByRole('button', { name: copy.es.storeOpen }));
		await waitFor(() => expect(onpick).toHaveBeenCalledOnce(), { timeout: 15_000 });

		// Unlocked once, never again this visit: the click picks it outright and the modal is gone.
		await openPicker();
		const modal = await screen.findByRole('dialog');
		await fireEvent.click(within(modal).getByRole('button', { name: /Trabajo/ }));
		await waitFor(() => expect(screen.queryByRole('dialog')).toBeNull());
		await waitFor(() => expect(onpick).toHaveBeenCalledTimes(2));
		expect(within(modal).queryByText(copy.es.storePin)).toBeNull();
	});
});
