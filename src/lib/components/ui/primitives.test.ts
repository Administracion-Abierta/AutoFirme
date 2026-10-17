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
			expect(card.outerHTML).toMatchInlineSnapshot(`"<div data-slot="card" data-size="default" class="ring-foreground/10 text-card-foreground gap-(--card-spacing) overflow-hidden rounded-xl py-(--card-spacing) text-sm [--card-spacing:--spacing(4)] has-data-[slot=card-footer]:pb-0 has-[>img:first-child]:pt-0 data-[size=sm]:[--card-spacing:--spacing(3)] data-[size=sm]:has-data-[slot=card-footer]:pb-0 *:[img:first-child]:rounded-t-xl *:[img:last-child]:rounded-b-xl group/card flex flex-col min-h-44 w-full shrink-0 cursor-pointer border-2 border-dashed bg-card/50 shadow-none ring-0 transition-colors border-border hover:border-primary/60" aria-label="Arrastra aquí los ficheros que quieres firmar" role="button" tabindex="0"><div data-slot="card-content" class="flex flex-col items-center gap-3 overflow-hidden px-6 py-12 text-center"><!----><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-file-plus lucide-icon size-12 transition-colors text-muted-foreground" aria-hidden="true"><!----><path d="M6 22a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h8a2.4 2.4 0 0 1 1.704.706l3.588 3.588A2.4 2.4 0 0 1 20 8v12a2 2 0 0 1-2 2z"></path><!----><path d="M14 2v5a1 1 0 0 0 1 1h5"></path><!----><path d="M9 15h6"></path><!----><path d="M12 18v-6"></path><!----><!----></svg><!----> <p class="text-lg font-medium">Arrastra aquí los ficheros que quieres firmar <span class="text-primary underline decoration-primary/40 underline-offset-4">Seleccionar ficheros</span><!----></p> <p class="text-muted-foreground text-sm">o selecciónalos desde tu equipo</p> <input class="sr-only" type="file" tabindex="-1" aria-hidden="true" multiple=""><!----></div><!----></div>"`);
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

		// The settings dialog carries the privacy note and the language and theme controls.
		await fireEvent.click(screen.getByRole('button', { name: t.settingsLabel }));
		const dialog = await screen.findByRole('dialog');
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
