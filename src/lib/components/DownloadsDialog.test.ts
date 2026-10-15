/**
 * Downloads list (#29): the signed outputs of previous runs, re-downloadable and deletable.
 */

import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/svelte';
import { createVault, memoryBackend, type Vault } from '#lib/certstore';
import { copy } from '#lib/i18n';
import DownloadsDialog from './DownloadsDialog.svelte';

const signed = new TextEncoder().encode('firma de contrato') as Uint8Array<ArrayBuffer>;

afterEach(cleanup);

async function seed(vault: Vault) {
	return vault.saveOutput({
		name: 'contrato.pdf.csig',
		format: 'cades',
		mime: 'application/octet-stream',
		commonName: 'Ñandú Prueba',
		bytes: signed
	});
}

/** jsdom has no `URL.createObjectURL`: capture the Blobs and the download names instead. */
function captureDownloads() {
	const blobs: Blob[] = [];
	const names: string[] = [];
	Object.defineProperty(URL, 'createObjectURL', {
		value: (blob: Blob) => {
			blobs.push(blob);
			return 'blob:test';
		},
		configurable: true,
		writable: true
	});
	Object.defineProperty(URL, 'revokeObjectURL', { value: () => {}, configurable: true, writable: true });
	const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(function (
		this: HTMLAnchorElement
	) {
		names.push(this.download);
	});
	return { blobs, names, restore: () => click.mockRestore() };
}

describe('downloads dialog', () => {
	it('lists a stored signature and re-downloads its exact bytes', async () => {
		const vault = createVault(memoryBackend());
		await seed(vault);
		const downloads = captureDownloads();
		render(DownloadsDialog, { props: { copy: copy.es, vault } });

		await fireEvent.click(screen.getByRole('button', { name: copy.es.downloadsLabel }));
		await waitFor(() => expect(screen.getByText('contrato.pdf.csig')).toBeTruthy());
		await fireEvent.click(screen.getByRole('button', { name: copy.es.downloadsDownload }));

		await waitFor(() => expect(downloads.names).toEqual(['contrato.pdf.csig']));
		// `Buffer` on both sides: the component's blob comes from the jsdom realm, the fixture's
		// TextEncoder from Node's, and `toEqual` compares typed arrays by their constructor.
		expect(Buffer.from(await downloads.blobs[0].arrayBuffer())).toEqual(Buffer.from(signed));
		downloads.restore();
	});

	it('deletes an output from the list', async () => {
		const vault = createVault(memoryBackend());
		await seed(vault);
		render(DownloadsDialog, { props: { copy: copy.es, vault } });

		await fireEvent.click(screen.getByRole('button', { name: copy.es.downloadsLabel }));
		await waitFor(() => expect(screen.getByText('contrato.pdf.csig')).toBeTruthy());
		await fireEvent.click(screen.getByRole('button', { name: copy.es.remove }));

		await waitFor(() => expect(screen.getByText(copy.es.downloadsEmpty)).toBeTruthy());
		expect(await vault.listOutputs()).toEqual([]);
	});

	it('starts empty', async () => {
		render(DownloadsDialog, { props: { copy: copy.es, vault: createVault(memoryBackend()) } });

		await fireEvent.click(screen.getByRole('button', { name: copy.es.downloadsLabel }));

		await waitFor(() => expect(screen.getByText(copy.es.downloadsEmpty)).toBeTruthy());
	});
});
