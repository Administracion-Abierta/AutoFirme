import { afterEach, describe, expect, it } from 'vitest';
import { cleanup, fireEvent, render, screen } from '@testing-library/svelte';
import App from './App.svelte';

afterEach(cleanup);

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
});
