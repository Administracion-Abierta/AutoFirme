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

function addDocument(container: HTMLElement, name = 'contrato.pdf') {
	const input = container.querySelector<HTMLInputElement>('input[type="file"]');
	if (!input) throw new Error('drop zone input not rendered');
	return fireEvent.change(input, {
		target: { files: [new File(['%PDF-1.4'], name, { type: 'application/pdf' })] }
	});
}

describe('shadcn primitives in use', () => {
	it('renders a document as one row whose download arms only when signed', async () => {
		const onremove = vi.fn();
		const file = new File(['%PDF-1.4'], 'contrato.pdf', { type: 'application/pdf' });
		const entry = {
			id: 'd1',
			file,
			status: 'pending',
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
			expect(card.outerHTML).toMatchInlineSnapshot(`"<div data-slot="card" data-size="default" class="ring-foreground/10 text-card-foreground gap-(--card-spacing) overflow-hidden rounded-xl py-(--card-spacing) text-sm [--card-spacing:--spacing(4)] has-data-[slot=card-footer]:pb-0 has-[>img:first-child]:pt-0 data-[size=sm]:[--card-spacing:--spacing(3)] data-[size=sm]:has-data-[slot=card-footer]:pb-0 *:[img:first-child]:rounded-t-xl *:[img:last-child]:rounded-b-xl group/card flex flex-col min-h-44 w-full shrink-0 cursor-pointer border-2 border-dashed bg-card/50 shadow-none ring-0 transition-colors border-border hover:border-primary/60" aria-label="Arrastra aquí los ficheros que quieres firmar" role="button" tabindex="0"><div data-slot="card-content" class="flex flex-col items-center gap-3 overflow-hidden px-6 py-12 text-center"><svg aria-hidden="true" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="size-12 transition-colors text-muted-foreground"><path d="M14 3v4a1 1 0 0 0 1 1h4" stroke-linecap="round" stroke-linejoin="round"></path><path d="M5 8V5a2 2 0 0 1 2-2h7l5 5v11a2 2 0 0 1-2 2h-3" stroke-linecap="round" stroke-linejoin="round"></path><path d="M9 21H5a2 2 0 0 1-2-2v-4m6 0h.01M12 15h.01M9 18h.01M12 18h.01" stroke-linecap="round"></path><path d="M12 15v-3m0 0-1.5 1.5M12 12l1.5 1.5" stroke-linecap="round" stroke-linejoin="round"></path><!----></svg> <p class="text-lg font-medium">Arrastra aquí los ficheros que quieres firmar <span class="text-primary underline decoration-primary/40 underline-offset-4">Seleccionar ficheros</span><!----></p> <p class="text-muted-foreground text-sm">o selecciónalos desde tu equipo</p> <input class="sr-only" type="file" tabindex="-1" aria-hidden="true" multiple=""><!----></div><!----></div>"`);
		});
	});

	it('lays the sign flow out as a 2/3 documents column beside the config card, queue full width', async () => {
		const { container } = render(App);
		await addDocument(container);
		await tick();

		const grid = container.querySelector('#panel-sign');
		expect(grid?.className).toContain('lg:grid-cols-3');
		// Desktop: the panels share one fr row; the queue sits under them at its own height.
		expect(grid?.className).toContain('lg:grid-rows-[minmax(0,1fr)_auto]');

		const [documents, config, queue] = Array.from(grid!.children) as HTMLElement[];
		// All three panels are shadcn Cards; documents first (left), config right, queue bottom.
		expect(documents?.getAttribute('data-slot')).toBe('card');
		expect(config?.getAttribute('data-slot')).toBe('card');
		// Documents take two of the three columns; the config card keeps the remaining third.
		expect(documents?.className).toContain('lg:col-span-2');
		expect(config?.className).not.toContain('lg:col-span');
		// The queue spans all three columns at the bottom.
		expect(queue?.className).toContain('lg:col-span-3');

		// The format chooser is a button opening the selection modal.
		const formatTrigger = screen.getByRole('button', { name: /CAdES-BES/ });
		expect(formatTrigger.getAttribute('aria-haspopup')).toBe('dialog');
	});

	it('explains a blocked sign button with a tooltip and opens the privacy dialog from the help menu', async () => {
		const { container } = render(App);
		await addDocument(container);
		await tick();

		// No certificate loaded yet: the disabled button carries the reason on hover.
		expect(container.querySelector('[data-slot="tooltip-trigger"]')).not.toBeNull();

		await fireEvent.click(screen.getByRole('button', { name: t.helpMenu }));
		const privacy = await screen.findByRole('menuitem', { name: t.privacyTitle });
		await fireEvent.click(privacy);

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
