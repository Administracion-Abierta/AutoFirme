/**
 * Snapshot/contract tests for the shadcn-svelte primitives the redesign introduced, exercised
 * through the components that actually use them: the document row (table + badge + button), the
 * drop zone (card), the desktop grid, the help menu with its privacy dialog, the tooltip on the
 * blocked sign button, and the certificate picker's fields (input).
 */
import type { BatchDocument } from '#lib/batch';
import { cleanup, fireEvent, render, screen, within } from '@testing-library/svelte';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { tick } from 'svelte';
import App from '#lib/App.svelte';
import CertPicker from '#lib/components/CertPicker.svelte';
import FileCard from '#lib/components/FileCard.svelte';
import FileDrop from '#lib/components/FileDrop.svelte';
import { createVault, memoryBackend } from '#lib/certstore';
import { copy } from '#lib/i18n';
import { toSelectedFile } from '#lib/sign';

afterEach(cleanup);

const t = copy.es;


describe('shadcn primitives in use', () => {
	it('renders a document as one row whose download arms only when signed', async () => {
		const onremove = vi.fn();
		const file = new File(['%PDF-1.4'], 'contrato.pdf', { type: 'application/pdf' });
		const entry = {
			id: 'd1',
			file,
			status: 'pending',
			format: 'cades',
			result: 'NOT_STARTED',
			description: '',
			sha256: '',
			output: 'contrato.pdf.csig',
			timestamp: '',
			algorithm: '',
			bytes: null
		} satisfies BatchDocument;
		const { container } = render(FileCard, {
			props: { copy: t, file: toSelectedFile(file), document: entry, onremove }
		});

		expect(container.querySelector('[data-slot="table-row"]')).not.toBeNull();
		const remove = screen.getByRole('button', { name: t.remove });
		expect(screen.getByRole('button', { name: t.downloadOne }).hasAttribute('disabled')).toBe(true);
		return fireEvent.click(remove).then(() => {
			expect(onremove).toHaveBeenCalledOnce();
		});
	});

	it('renders the drop zone as a dashed shadcn card with its browse button', () => {
		const onselect = vi.fn();
		const { container } = render(FileDrop, { props: { copy: t, onselect } });

		const card = container.querySelector('[data-slot="card"]') as HTMLElement;
		expect(card.className).toContain('border-dashed');
		expect(card.getAttribute('aria-label')).toBe(t.dropTitle);

		// The whole zone is one button: clicking the surface opens the file dialog.
		const trigger = screen.getByRole('button', { name: t.dropTitle });
		return fireEvent.click(trigger).then(() => {
			expect(container.querySelector('input[type="file"]')).not.toBeNull();
			expect(onselect).not.toHaveBeenCalled();
		});
	});

	it('lays the sign flow out as one column of step cards under a step indicator', async () => {
		const { container } = render(App);
		await tick();

		const panel = container.querySelector('#panel-sign');
		expect(panel?.className).toContain('flex-col');
		// Step 1 first: the certificate card with the Next button.
		expect(panel?.textContent).toContain(t.step1Title);
		expect(screen.getByRole('button', { name: t.next })).toBeTruthy();
		// Step 1 has no drop zone yet — the file input here belongs to the keystore picker.
		const input = container.querySelector<HTMLInputElement>('#pkcs12-file');
		expect(input?.getAttribute('aria-label')).toBe(t.addSign);
		expect(container.querySelector('#panel-sign p.text-lg')).toBeNull();
	});

	it('explains the blocked Next button with a hint and opens the privacy dialog from settings', async () => {
		const { container } = render(App);
		await tick();

		// No certificate loaded yet: the hint states the reason inline and Next stays disabled.
		expect(container.querySelector('#sign-hint')?.textContent).toContain('certificado');
		expect(screen.getByRole('button', { name: t.next }).hasAttribute('disabled')).toBe(true);

		// The privacy note lives in the Help tab.
		await fireEvent.click(screen.getByRole('button', { name: t.settingsLabel }));
		const dialog = await screen.findByRole('dialog');
		await fireEvent.click(within(dialog).getByRole('tab', { name: t.helpMenu }));
		expect(within(dialog).getByText(t.privacyTitle)).toBeTruthy();
		expect(within(dialog).getByText(t.privacyNote)).toBeTruthy();
	});

	it('renders the picker trigger and the hidden file input, with no PIN field on the panel', () => {
		const { container } = render(CertPicker, {
			props: {
				copy: t,
				cert: null,
				vault: createVault(memoryBackend()),
				onpick: vi.fn()
			}
		});

		// The panel is pick-only: the PIN lives in the modal's add flow, which is portaled out of
		// `container`, so nothing input-shaped is left on the panel itself.
		expect(container.querySelectorAll('[data-slot="input"]')).toHaveLength(0);
		// The file input is still there for "Agregar firma", but hidden behind the trigger button.
		const file = container.querySelector<HTMLInputElement>('#pkcs12-file');
		expect(file?.type).toBe('file');
		expect(file?.classList.contains('sr-only')).toBe(true);
		expect(screen.getByRole('button', { name: new RegExp(t.selectSign) })).toBeTruthy();
	});
});
