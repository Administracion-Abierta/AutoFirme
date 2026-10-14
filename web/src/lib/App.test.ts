import { rmSync } from 'node:fs';
import { afterAll, afterEach, beforeAll, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/svelte';
import { tick } from 'svelte';
import App from './App.svelte';
import * as keystore from '#lib/keystore';
import { createPkcs12Fixtures, FIXTURE_PIN, LEAF_COMMON_NAME, type Pkcs12Fixtures } from '../test/fixtures';

afterEach(cleanup);

afterEach(() => vi.restoreAllMocks());

let fixtures: Pkcs12Fixtures;

beforeAll(() => {
	fixtures = createPkcs12Fixtures();
}, 60_000);

afterAll(() => rmSync(fixtures.dir, { recursive: true, force: true }));

async function pickDocumentAndKey(container: HTMLElement) {
	await fireEvent.change(fileInput(container), {
		target: { files: [new File(['%PDF-1.4'], 'contrato.pdf', { type: 'application/pdf' })] }
	});
	const keystore = container.querySelector<HTMLInputElement>('#pkcs12-file');
	if (!keystore) throw new Error('keystore input not rendered');
	await fireEvent.change(keystore, {
		target: { files: [new File([fixtures.modern], 'keystore.p12')] }
	});
	await fireEvent.input(container.querySelector<HTMLInputElement>('#pkcs12-pin')!, {
		target: { value: FIXTURE_PIN }
	});
}

function fileInput(container: HTMLElement): HTMLInputElement {
	const input = container.querySelector<HTMLInputElement>('input[type="file"]');
	if (!input) throw new Error('file input not rendered');
	return input;
}

describe('App (sign workflow shell)', () => {
	it('starts on the drop step, with no way to sign yet', () => {
		const { container } = render(App);

		expect(container.textContent).toContain('AutoFirma Web');
		expect(container.textContent).toContain('Arrastra aquí el fichero que quieres firmar');
		expect(fileInput(container)).toBeInstanceOf(HTMLInputElement);
		// Step 2 is unreachable before a document is loaded.
		expect(screen.queryByRole('button', { name: 'Firmar' })).toBeNull();
	});

	it('shows the sign step on file selection, but keeps signing disabled until a cert exists', async () => {
		const { container } = render(App);

		await fireEvent.change(fileInput(container), {
			target: { files: [new File(['%PDF-1.4'], 'contrato.pdf', { type: 'application/pdf' })] }
		});

		expect(container.textContent).toContain('contrato.pdf');
		expect(container.textContent).toContain('8 B');

		const sign = screen.getByRole('button', { name: 'Firmar' });
		expect(sign.hasAttribute('disabled')).toBe(true);
		expect(container.textContent).toContain('Selecciona un certificado para habilitar la firma');

		expect(screen.getByRole('combobox')).toHaveProperty('disabled', true);
		expect(container.textContent).toContain('CAdES-BES');
		expect(container.textContent).toContain('Todavía no se ha firmado ningún documento');

		// Removing the document returns to the drop step.
		await fireEvent.click(screen.getByRole('button', { name: 'Quitar' }));
		expect(screen.queryByRole('button', { name: 'Firmar' })).toBeNull();
	});

	it('switches language', async () => {
		const { container } = render(App);

		await fireEvent.click(screen.getByRole('button', { name: 'EN' }));

		expect(container.textContent).toContain('Drag the file you want to sign here');
		expect(container.textContent).toContain('Choose file');
	});

	it('loads a PKCS#12 file and enables signing with the certificate it holds', async () => {
		const { container } = render(App);

		await pickDocumentAndKey(container);
		await fireEvent.click(screen.getByRole('button', { name: 'Cargar certificados' }));

		await waitFor(() => expect(container.textContent).toContain(LEAF_COMMON_NAME));
		expect(container.textContent).toContain(`CN=${LEAF_COMMON_NAME}, O=AutoFirme, C=ES`);
		// The chain certificate of the file is listed but cannot sign.
		expect(container.textContent).toContain('AutoFirme Test CA (sin clave privada)');
		expect(screen.getByRole('button', { name: 'Firmar' }).hasAttribute('disabled')).toBe(false);
	});

	it('shows the wrong-PIN error and keeps signing disabled', async () => {
		const { container } = render(App);

		await pickDocumentAndKey(container);
		await fireEvent.input(container.querySelector<HTMLInputElement>('#pkcs12-pin')!, {
			target: { value: '0000' }
		});
		await fireEvent.click(screen.getByRole('button', { name: 'Cargar certificados' }));

		await screen.findByRole('alert');
		expect(container.textContent).toContain('PIN incorrecto.');
		expect(screen.getByRole('button', { name: 'Firmar' }).hasAttribute('disabled')).toBe(true);
	});

	it('clears the loaded certificates when another keystore file is picked', async () => {
		const { container } = render(App);

		await pickDocumentAndKey(container);
		await fireEvent.click(screen.getByRole('button', { name: 'Cargar certificados' }));
		await waitFor(() => expect(container.textContent).toContain(LEAF_COMMON_NAME));
		expect(screen.getByRole('button', { name: 'Firmar' }).hasAttribute('disabled')).toBe(false);

		await fireEvent.change(container.querySelector<HTMLInputElement>('#pkcs12-file')!, {
			target: { files: [new File([fixtures.ec], 'other.p12')] }
		});

		expect(container.querySelector<HTMLInputElement>('#cert-picker')?.value).toBe('');
		expect(screen.getByRole('button', { name: 'Firmar' }).hasAttribute('disabled')).toBe(true);
	});

	it('discards a load that finishes after another keystore file is picked', async () => {
		const loadSpy = vi.spyOn(keystore, 'loadSignerSession');
		const { container } = render(App);

		await pickDocumentAndKey(container);

		// Start the load without awaiting it, then supersede the file while it is in flight.
		fireEvent.click(screen.getByRole('button', { name: 'Cargar certificados' }));
		await fireEvent.change(container.querySelector<HTMLInputElement>('#pkcs12-file')!, {
			target: { files: [new File([fixtures.ec], 'other.p12')] }
		});

		// Await the superseded load itself and the render it triggers, so the assertions cannot race it.
		await vi.waitFor(() => expect(loadSpy).toHaveBeenCalledOnce());
		await loadSpy.mock.results[0].value;
		await tick();

		expect(container.querySelector<HTMLSelectElement>('#cert-picker')?.value).toBe('');
		expect(container.textContent).not.toContain(LEAF_COMMON_NAME);
		expect(screen.getByRole('button', { name: 'Firmar' }).hasAttribute('disabled')).toBe(true);
	});

	it('disables signing when the document is removed after a certificate was loaded', async () => {
		const { container } = render(App);

		await pickDocumentAndKey(container);
		await fireEvent.click(screen.getByRole('button', { name: 'Cargar certificados' }));
		await waitFor(() => expect(container.textContent).toContain(LEAF_COMMON_NAME));
		expect(screen.getByRole('button', { name: 'Firmar' }).hasAttribute('disabled')).toBe(false);

		await fireEvent.click(screen.getByRole('button', { name: 'Quitar' }));
		await fireEvent.change(fileInput(container), {
			target: { files: [new File(['%PDF-1.4'], 'otro.pdf', { type: 'application/pdf' })] }
		});

		// The picker starts over empty, so the stale signer must not keep signing alive.
		expect(screen.getByRole('button', { name: 'Firmar' }).hasAttribute('disabled')).toBe(true);
		expect(container.querySelector<HTMLSelectElement>('#cert-picker')?.value).toBe('');
		expect(container.textContent).not.toContain('Emitido por');
	});

});
