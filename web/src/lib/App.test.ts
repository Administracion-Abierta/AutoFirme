import { execFileSync } from 'node:child_process';
import { readFileSync, rmSync } from 'node:fs';
import { join, resolve } from 'node:path';
import { afterAll, afterEach, beforeAll, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/svelte';
import { tick } from 'svelte';
import App from './App.svelte';
import * as keystore from '#lib/keystore';
import {
	createPkcs12Fixtures,
	writeFixtureFile,
	xmlsec1Verify,
	FIXTURE_PIN,
	LEAF_COMMON_NAME,
	type Pkcs12Fixtures
} from '../test/fixtures';

afterEach(() => {
	cleanup();
	vi.restoreAllMocks();
});

let fixtures: Pkcs12Fixtures;

beforeAll(() => {
	fixtures = createPkcs12Fixtures();
}, 60_000);

afterAll(() => rmSync(fixtures.dir, { recursive: true, force: true }));

async function pickDocumentAndKey(container: HTMLElement) {
	await fireEvent.change(fileInput(container), {
		target: { files: [new File(['%PDF-1.4'], 'contrato.pdf', { type: 'application/pdf' })] }
	});
	await loadKey(container);
}

/** Fill the PKCS#12 picker with the fixture container and its PIN. */
async function loadKey(container: HTMLElement) {
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

/** jsdom has no `URL.createObjectURL`: capture the Blobs and the download names instead. */
function captureDownloads() {
	const blobs: Blob[] = [];
	const names: string[] = [];
	// jsdom defines `URL.revokeObjectURL` as a read-only member, so it has to be redefined, not assigned.
	Object.defineProperty(URL, 'createObjectURL', {
		value: (blob: Blob) => {
			blobs.push(blob);
			return 'blob:test';
		},
		configurable: true,
		writable: true
	});
	Object.defineProperty(URL, 'revokeObjectURL', {
		value: () => {},
		configurable: true,
		writable: true
	});
	const click = vi
		.spyOn(HTMLAnchorElement.prototype, 'click')
		.mockImplementation(function (this: HTMLAnchorElement) {
			names.push(this.download);
		});
	return {
		blobs,
		names,
		restore: () => click.mockRestore()
	};
}

describe('App (sign workflow shell)', () => {
	it('starts on the drop step, with no way to sign yet', () => {
		const { container } = render(App);

		expect(container.textContent).toContain('AutoFirma Web');
		expect(container.textContent).toContain('Arrastra aquí los ficheros que quieres firmar');
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
		// The queue is listed with every document still pending.
		expect(container.querySelector('[data-status="pending"]')?.textContent).toContain('contrato.pdf');

		// Removing the document returns to the drop step.
		await fireEvent.click(screen.getByRole('button', { name: 'Quitar' }));
		expect(screen.queryByRole('button', { name: 'Firmar' })).toBeNull();
	});

	it('switches language', async () => {
		const { container } = render(App);

		await fireEvent.click(screen.getByRole('button', { name: 'EN' }));

		expect(container.textContent).toContain('Drag the files you want to sign here');
		expect(container.textContent).toContain('Choose files');
	});

	it('loads a PKCS#12 file and enables signing with the certificate it holds', async () => {
		const { container } = render(App);

		await pickDocumentAndKey(container);
		await fireEvent.click(screen.getByRole('button', { name: 'Cargar certificados' }));

		await waitFor(() => expect(container.textContent).toContain(LEAF_COMMON_NAME));
		expect(container.textContent).toContain(`CN=${LEAF_COMMON_NAME}, O=AutoFirme, C=ES`);
		expect(container.textContent).toContain('Válido desde');
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

	it('signs a 3-file batch with the loaded certificate and hands over one ZIP', async () => {
		const downloaded: string[] = [];
		vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(function (
			this: HTMLAnchorElement
		) {
			downloaded.push(this.download);
		});
		// jsdom has no blob URLs; only their creation matters here.
		Object.defineProperty(URL, 'createObjectURL', { value: () => 'blob:batch', configurable: true });
		Object.defineProperty(URL, 'revokeObjectURL', { value: () => {}, configurable: true });

		const { container } = render(App);
		await fireEvent.change(fileInput(container), {
			target: {
				files: [
					new File(['uno'], 'uno.txt', { type: 'text/plain' }),
					new File(['dos'], 'dos.txt', { type: 'text/plain' }),
					new File(['tres'], 'tres.txt', { type: 'text/plain' })
				]
			}
		});
		await loadKey(container);
		await fireEvent.click(screen.getByRole('button', { name: 'Cargar certificados' }));
		await waitFor(() => expect(container.textContent).toContain(LEAF_COMMON_NAME));

		await fireEvent.click(screen.getByRole('button', { name: 'Firmar' }));

		// Every document ends signed, with its own SHA-256 shown, and the archive becomes available.
		await waitFor(() => expect(container.textContent).toContain('Firmados: 3/3'));
		expect(container.querySelectorAll('[data-status="signed"]')).toHaveLength(3);
		expect(container.querySelector('[data-status="signed"]')?.textContent).toContain('uno.txt.csig');

		const download = screen.getByRole('button', { name: 'Descargar ZIP' });
		expect(download.hasAttribute('disabled')).toBe(false);
		await fireEvent.click(download);
		expect(downloaded).toEqual(['firmas-lote.zip']);
	});

	it('signs an XML document as XAdES-EPES in the queue and ships a .xsig xmlsec1 verifies', async () => {
		const { container } = render(App);

		await fireEvent.change(fileInput(container), {
			target: {
				files: [new File(['<fe:Facturae xmlns:fe="urn:fe"><N>2026-0001</N></fe:Facturae>'], 'facturae.xml', {
					type: 'application/xml'
				})]
			}
		});
		await loadKey(container);
		await fireEvent.click(screen.getByRole('button', { name: 'Cargar certificados' }));
		await waitFor(() => expect(container.textContent).toContain(LEAF_COMMON_NAME));
		await fireEvent.click(screen.getByRole('radio', { name: /XAdES-EPES/ }));

		const downloads = captureDownloads();
		try {
			await fireEvent.click(screen.getByRole('button', { name: 'Firmar' }));
			await waitFor(() => expect(container.textContent).toContain('Firmados: 1/1'));
			await fireEvent.click(screen.getByRole('button', { name: 'Descargar ZIP' }));
		} finally {
			downloads.restore();
		}

		expect(downloads.names).toEqual(['firmas-lote.zip']);
		const archive = writeFixtureFile(
			fixtures.dir,
			'lote.zip',
			new Uint8Array(await downloads.blobs[0].arrayBuffer())
		);
		// Info-ZIP extracts the entry, so the queue's naming is checked by a tool that did not write it.
		const xsig = execFileSync('unzip', ['-p', archive, 'facturae.xml.xsig']);
		expect(xsig.toString()).toContain('<ds:Signature');
		// The leaf fixture is self-signed, so it is its own trust anchor.
		const path = writeFixtureFile(fixtures.dir, 'ui.xsig', new Uint8Array(xsig));
		expect(xmlsec1Verify(path, join(fixtures.dir, 'leaf.pem'))).toMatch(/status: OK/);
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

	it('does not resurrect the signer when the document is removed while a load runs', async () => {
		const original = keystore.loadSignerSession;
		let release!: () => void;
		const gate = new Promise<void>((resolve) => (release = resolve));
		vi.spyOn(keystore, 'loadSignerSession').mockImplementation(async (bytes, pin, options) => {
			await gate;
			return original(bytes, pin, options);
		});
		const { container } = render(App);

		await pickDocumentAndKey(container);
		fireEvent.click(screen.getByRole('button', { name: 'Cargar certificados' }));
		await vi.waitFor(() => expect(vi.mocked(keystore.loadSignerSession)).toHaveBeenCalledOnce());

		// The document is removed while the keystore is still being parsed.
		await fireEvent.click(screen.getByRole('button', { name: 'Quitar' }));
		release();
		await vi.mocked(keystore.loadSignerSession).mock.results[0].value;
		await tick();

		// A new document arrives: nothing was loaded for it, so signing must stay off.
		await fireEvent.change(fileInput(container), {
			target: { files: [new File(['%PDF-1.4'], 'otro.pdf', { type: 'application/pdf' })] }
		});
		expect(screen.getByRole('button', { name: 'Firmar' }).hasAttribute('disabled')).toBe(true);
		expect(container.querySelector<HTMLSelectElement>('#cert-picker')?.value).toBe('');
		expect(container.textContent).not.toContain('Emitido por');
	});
});

describe('verify mode', () => {
	it('renders a per-check validity report for a dropped artifact', async () => {
		const { container } = render(App);
		await fireEvent.click(screen.getByRole('tab', { name: 'Verificar' }));

		expect(container.textContent).toContain('Arrastra aquí el fichero firmado');

		const bytes = readFileSync(resolve(process.cwd(), 'test/fixtures/cades-tampered.csig'));
		await fireEvent.change(fileInput(container), {
			target: { files: [new File([bytes], 'firma.csig')] }
		});

		await screen.findByText(/no supera todas las comprobaciones/);
		expect(container.querySelector('[data-check="contentDigest"]')?.getAttribute('data-status')).toBe('fail');
		expect(container.querySelector('[data-check="signature"]')?.getAttribute('data-status')).toBe('pass');
		expect(container.textContent).toContain('Firmante');
	});

	it('labels the trust-chain check as failed when the root is not bundled, keeping the rest green', async () => {
		const { container } = render(App);
		await fireEvent.click(screen.getByRole('tab', { name: 'Verificar' }));

		const bytes = readFileSync(resolve(process.cwd(), 'test/fixtures/xades-valid.xsig'));
		await fireEvent.change(fileInput(container), {
			target: { files: [new File([bytes], 'firma.xsig')] }
		});

		await screen.findByText(/no supera todas las comprobaciones/);
		expect(container.querySelector('[data-check="signature"]')?.getAttribute('data-status')).toBe('pass');
		expect(container.querySelector('[data-check="reference"]')?.getAttribute('data-status')).toBe('pass');
		expect(container.querySelector('[data-check="certChain"]')?.getAttribute('data-status')).toBe('fail');
		expect(container.textContent).toContain('XAdES');
	});
});
