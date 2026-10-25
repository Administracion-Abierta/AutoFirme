import { execFileSync } from 'node:child_process';
import { readFileSync, rmSync } from 'node:fs';
import { join, resolve } from 'node:path';
import { afterAll, afterEach, beforeAll, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor, within } from '@testing-library/svelte';
import App from './App.svelte';
import { copy } from '#lib/i18n';
import {
	ASYNC,
	cmsTimeStamp,
	createPkcs12Fixtures,
	createTsaFixtures,
	tsVerifyToken,
	writeFixtureFile,
	xmlsec1Verify,
	FIXTURE_PIN,
	LEAF_COMMON_NAME,
	type Pkcs12Fixtures
} from '../test/fixtures';

afterEach(() => {
	cleanup();
	vi.restoreAllMocks();
	// The theme is persisted, so a test that switches it must not colour the next one.
	localStorage.clear();
	document.documentElement.classList.remove('dark');
});

let fixtures: Pkcs12Fixtures;

beforeAll(() => {
	fixtures = createPkcs12Fixtures();
}, 60_000);

afterAll(() => rmSync(fixtures.dir, { recursive: true, force: true }));

async function pickDocumentAndKey(container: HTMLElement, keystore: Uint8Array = fixtures.modern) {
	await loadKey(container, keystore);
	// The flow is now two steps: the certificate on step 1, the documents on step 2.
	await fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));
	await fireEvent.change(fileInput(container), {
		target: { files: [new File(['%PDF-1.4'], 'contrato.pdf', { type: 'application/pdf' })] }
	});
}

/** The picker's only load path now: pick the file in the modal, type the PIN, seal it. */
async function loadKey(container: HTMLElement, bytes: Uint8Array = fixtures.modern) {
	await fireEvent.click(container.querySelector<HTMLElement>('#pkcs12-picker')!);
	await fireEvent.change(document.querySelector<HTMLInputElement>('#pkcs12-file')!, {
		target: { files: [new File([bytes.slice()], 'keystore.p12')] }
	});
	await fireEvent.input(document.querySelector<HTMLInputElement>('#add-pin')!, {
		target: { value: FIXTURE_PIN }
	});
	await fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));
	// Sealing runs PBKDF2 with 250k iterations, which outlasts the default wait when the suite
	// runs its files in parallel; a successful seal closes the modal on the selected sign.
	await waitFor(() => expect(screen.queryByRole('dialog')).toBeNull(), { timeout: 25_000 });
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
	// The store tests below seal and unlock with PBKDF2 (250k iterations); under the full suite's
	// parallel files that outlasts the default 5s, so allow the operations to finish.
	vi.setConfig({ testTimeout: 30_000 });

	it('starts on the certificate step, with the next button disabled', () => {
		const { container } = render(App);

		expect(container.textContent).toContain('AutoFirma Web');
		expect(container.textContent).toContain('Configura la firma');
		// Step 1 gates on the signature alone: no certificate, no way forward.
		const next = screen.getByRole('button', { name: 'Siguiente' });
		expect(next.hasAttribute('disabled')).toBe(true);
		expect(container.textContent).toContain('Selecciona un certificado para habilitar la firma');
	});

	it('advances to documents once a certificate is loaded, and Firmar gates on files', async () => {
		const { container } = render(App);

		await loadKey(container);
		await waitFor(() => expect(container.textContent).toContain(LEAF_COMMON_NAME), ASYNC);

		await fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));

		expect(container.textContent).toContain('Elige los documentos');
		expect(container.textContent).toContain('CAdES-BES');
		// The sign button needs documents even with a certificate at hand.
		expect(screen.getByRole('button', { name: 'Firmar' }).hasAttribute('disabled')).toBe(true);

		await fireEvent.change(fileInput(container), {
			target: { files: [new File(['%PDF-1.4'], 'contrato.pdf', { type: 'application/pdf' })] }
		});
		expect(container.textContent).toContain('contrato.pdf');
		expect(screen.getByRole('button', { name: 'Firmar' }).hasAttribute('disabled')).toBe(false);

		// Removing the document empties the batch again and the row goes with it.
		await fireEvent.click(screen.getByRole('button', { name: 'Quitar' }));
		expect(container.querySelector('[data-status="pending"]')).toBeNull();
		expect(screen.getByRole('button', { name: 'Firmar' }).hasAttribute('disabled')).toBe(true);
	});

	it('switches language from the settings modal', async () => {
		const { container } = render(App);

		await fireEvent.click(screen.getByRole('button', { name: 'Ajustes y ayuda' }));
		await fireEvent.click(await screen.findByRole('button', { name: 'EN' }));

		expect(container.textContent).toContain('Configure the signature');
		expect(container.textContent).toContain('Choose a certificate to enable signing');
	});

	it('switches the theme from the settings modal, remembering it for the next visit', async () => {
		render(App);

		await fireEvent.click(screen.getByRole('button', { name: 'Ajustes y ayuda' }));
		// The switcher is one group of three buttons inside the modal.
		const group = await screen.findByRole('group', { name: 'Tema' });
		const pick = (name: string) => within(group).getByRole('button', { name });

		await fireEvent.click(pick('Oscuro'));
		expect(document.documentElement.classList.contains('dark')).toBe(true);
		expect(localStorage.getItem('autofirme-theme')).toBe('dark');

		await fireEvent.click(pick('Claro'));
		expect(document.documentElement.classList.contains('dark')).toBe(false);
		expect(localStorage.getItem('autofirme-theme')).toBe('light');

		// "Sistema" hands the decision back to the platform preference.
		await fireEvent.click(pick('Sistema'));
		expect(localStorage.getItem('autofirme-theme')).toBe('system');
	});

	it('step 2: the big drop zone gives way to a row-sized add tile once a document is there, with no header row or download button', async () => {
		const { container } = render(App);
		await loadKey(container);
		await waitFor(() => expect(container.textContent).toContain(LEAF_COMMON_NAME), ASYNC);
		await fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));

		expect(container.textContent).toContain('Arrastra aquí los ficheros');
		await fireEvent.change(fileInput(container), {
			target: { files: [new File(['%PDF-1.4'], 'contrato.pdf', { type: 'application/pdf' })] }
		});
		// With a file in place the big zone is gone; the row-sized add tile stays first in the list.
		expect(container.querySelector('[data-slot="card"][class*="min-h-44"]')).toBeNull();
		expect(container.querySelector('tbody tr')?.textContent).toContain('Seleccionar ficheros');
		expect(container.querySelector('thead')).toBeNull();
		expect(screen.queryByRole('button', { name: 'Descargar' })).toBeNull();
		expect(screen.getByRole('button', { name: 'Quitar' }).querySelector('svg')).not.toBeNull();
	});

	it('step 2: a file dragged over the page raises the full-screen drop overlay until it leaves', async () => {
		const { container } = render(App);
		await loadKey(container);
		await waitFor(() => expect(container.textContent).toContain(LEAF_COMMON_NAME), ASYNC);
		await fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));

		const dragEvent = (type: string) => {
			const event = new Event(type, { bubbles: true, cancelable: true });
			Object.defineProperty(event, 'dataTransfer', { value: { types: ['Files'], files: [] } });
			return event;
		};
		window.dispatchEvent(dragEvent('dragenter'));
		await waitFor(() => expect(screen.getByRole('status').textContent).toContain('Suelta los ficheros aquí'), ASYNC);
		window.dispatchEvent(dragEvent('dragleave'));
		await waitFor(() => expect(screen.queryByRole('status')).toBeNull(), ASYNC);
	});

	it('step 2: the file list scrolls vertically only, and shows an edge fade on each side that has hidden content', async () => {
		const { container } = render(App);
		await loadKey(container);
		await waitFor(() => expect(container.textContent).toContain(LEAF_COMMON_NAME), ASYNC);
		await fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));
		await fireEvent.change(fileInput(container), {
			target: { files: [new File(['%PDF-1.4'], 'contrato.pdf', { type: 'application/pdf' })] }
		});

		const list = container.querySelector('.overflow-x-hidden.overflow-y-auto') as HTMLElement;
		expect(list.className).toContain('overflow-x-hidden');
		// jsdom has no layout: give the list a 300px viewport over 600px of content, scrolled to the start.
		Object.defineProperties(list, {
			scrollWidth: { value: 600, configurable: true },
			clientWidth: { value: 300, configurable: true },
			scrollLeft: { value: 0, writable: true, configurable: true }
		});
		await fireEvent.scroll(list);
		await waitFor(() => expect(container.querySelector('.from-background.bg-gradient-to-l')).not.toBeNull(), ASYNC);
		expect(container.querySelector('.from-background.bg-gradient-to-r')).toBeNull();

		// Scrolled to the far end: only the left fade remains.
		Object.defineProperty(list, 'scrollLeft', { value: 300, configurable: true });
		await fireEvent.scroll(list);
		await waitFor(() => expect(container.querySelector('.from-background.bg-gradient-to-r')).not.toBeNull(), ASYNC);
		expect(container.querySelector('.from-background.bg-gradient-to-l')).toBeNull();
	});

	it('loads a PKCS#12 file and enables the next step with the certificate it holds', async () => {
		const { container } = render(App);

		await loadKey(container);

		await waitFor(() => expect(container.textContent).toContain(LEAF_COMMON_NAME), ASYNC);
		// The trigger names the file and shows which certificate will sign.
		expect(screen.getByRole('button', { name: /keystore\.p12/ })).toBeTruthy();
		expect(container.textContent).toContain(LEAF_COMMON_NAME);
		expect(screen.getByRole('button', { name: 'Siguiente' }).hasAttribute('disabled')).toBe(false);
		// And it carries the sign's summary: issuer DN and validity window under the icon.
		const trigger = container.querySelector<HTMLButtonElement>('#pkcs12-picker')!;
		expect(trigger.textContent).toContain(`${copy.es.signIssuer}: CN=`);
		expect(trigger.textContent).toContain('O=AutoFirme');
		expect(trigger.textContent).toContain(copy.es.signValidity);
	});

	it('shows the wrong-PIN error from the modal add and keeps the next step locked', async () => {
		const { container } = render(App);

		// The add flow lives in the picker's modal: wrong PIN there must not load anything.
		await fireEvent.click(container.querySelector<HTMLElement>('#pkcs12-picker')!);
		await fireEvent.change(document.querySelector<HTMLInputElement>('#pkcs12-file')!, {
			target: { files: [new File([fixtures.modern], 'keystore.p12')] }
		});
		await fireEvent.input(document.querySelector<HTMLInputElement>('#add-pin')!, {
			target: { value: '0000' }
		});
		await fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

		// The modal stays open on failure; its alert carries the message. The vault runs in memory
		// here (jsdom has no IndexedDB), so the ephemeral-store notice sits in the dialog too —
		// wait for the PIN error to join it.
		await waitFor(
			() =>
				expect(
					screen
						.getAllByRole('alert')
						.map((entry) => entry.textContent)
						.join(' ')
				).toContain('PIN incorrecto.'),
			ASYNC
		);
		expect(screen.getByRole('button', { name: 'Siguiente' }).hasAttribute('disabled')).toBe(true);
	});

	it('says the page is insecure when the browser withholds WebCrypto', () => {
		const descriptor = Object.getOwnPropertyDescriptor(globalThis.crypto, 'subtle');
		Object.defineProperty(globalThis.crypto, 'subtle', { value: undefined, configurable: true });
		try {
			const { container } = render(App);
			expect(container.textContent).toContain(copy.es.insecureContext);
		} finally {
			if (descriptor) Object.defineProperty(globalThis.crypto, 'subtle', descriptor);
			else Reflect.deleteProperty(globalThis.crypto, 'subtle');
		}
	});

	it('warns that the picked certificate has expired and still enables signing (#40)', async () => {
		const { container } = render(App);

		await loadKey(container, fixtures.expired);

		// The amber banner (shadcn `Alert`, role=alert) says the expiry out loud; it lives on
		// step 1 with the picker, before any document is chosen.
		const banner = await screen.findByRole('alert');
		expect(banner.textContent).toContain('caducado');
		expect(banner.textContent).toContain(String(new Date(fixtures.expiredNotAfter).getFullYear()));

		// …and the certificate still signs: AutoFirma's behaviour, and the point of #40.
		await fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));
		await fireEvent.change(fileInput(container), {
			target: { files: [new File(['%PDF-1.4'], 'contrato.pdf', { type: 'application/pdf' })] }
		});
		expect(screen.getByRole('button', { name: 'Firmar' }).hasAttribute('disabled')).toBe(false);
	});

	it('seals the added sign into the store and selects it again from the modal', async () => {
		const { container } = render(App);

		// The add flow seals the container into the store as it loads it (label = file name) and
		// closes the modal on the sign it selected — all on step 1, where the picker lives.
		await loadKey(container);
		await waitFor(() => expect(container.textContent).toContain(LEAF_COMMON_NAME), ASYNC);

		// Clear the session the file gave, as an empty form would, then swap in the stored one.
		await fireEvent.change(container.querySelector<HTMLInputElement>('#pkcs12-file')!, {
			target: { files: [] }
		});
		expect(screen.getByRole('button', { name: 'Siguiente' }).hasAttribute('disabled')).toBe(true);

		// The stored sign is a cell of the picker's modal (the grid is the store's own listing).
		// It was unsealed this visit, so one click selects it again and closes the modal; a sign
		// from a previous visit asks for its PIN first — that path lives in CertPicker.test.
		await fireEvent.click(screen.getByRole('button', { name: /Seleccionar firma/ }));
		await fireEvent.click(await screen.findByRole('button', { name: /keystore\.p12/ }, ASYNC));
		await waitFor(() => expect(screen.queryByRole('dialog')).toBeNull(), ASYNC);

		const trigger = container.querySelector<HTMLButtonElement>('#pkcs12-picker')!;
		expect(trigger.textContent).toContain('keystore.p12');
		expect(trigger.textContent).toContain(LEAF_COMMON_NAME);
		expect(screen.getByRole('button', { name: 'Siguiente' }).hasAttribute('disabled')).toBe(false);

		// With the sign back in hand the flow continues: documents, then Firmar enables.
		await fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));
		await fireEvent.change(fileInput(container), {
			target: { files: [new File(['%PDF-1.4'], 'contrato.pdf', { type: 'application/pdf' })] }
		});
		expect(screen.getByRole('button', { name: 'Firmar' }).hasAttribute('disabled')).toBe(false);
	});

	it('clears the loaded certificate when another keystore file is picked', async () => {
		const { container } = render(App);

		await loadKey(container);
		await waitFor(() => expect(container.textContent).toContain(LEAF_COMMON_NAME), ASYNC);

		// A different keystore file invalidates the loaded signer while the picker is still up.
		await fireEvent.change(container.querySelector<HTMLInputElement>('#pkcs12-file')!, {
			target: { files: [new File([fixtures.ec], 'other.p12')] }
		});

		// The trigger now names the new file only, and step 1 gates on a signer again.
		expect(screen.getByRole('button', { name: /other\.p12/ })).toBeTruthy();
		expect(container.textContent).not.toContain(LEAF_COMMON_NAME);
		expect(screen.getByRole('button', { name: 'Siguiente' }).hasAttribute('disabled')).toBe(true);
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
		await loadKey(container);
		await waitFor(() => expect(container.textContent).toContain(LEAF_COMMON_NAME), ASYNC);
		await fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));
		await fireEvent.change(fileInput(container), {
			target: {
				files: [
					new File(['uno'], 'uno.txt', { type: 'text/plain' }),
					new File(['dos'], 'dos.txt', { type: 'text/plain' }),
					new File(['tres'], 'tres.txt', { type: 'text/plain' })
				]
			}
		});

		await fireEvent.click(screen.getByRole('button', { name: 'Firmar' }));

		// Every document ends signed and the per-file download arms with the signature's name.
		await waitFor(() => expect(container.textContent).toContain('Firmados: 3/3'), ASYNC);
		expect(container.querySelectorAll('[data-status="signed"]')).toHaveLength(3);
		const firstRow = container.querySelector('[data-status="signed"]');
		expect(firstRow?.textContent).toContain('uno.txt');
		expect(
			[...(firstRow?.querySelectorAll('button') ?? [])].some(
				(button) => button.textContent?.includes('Descargar') && !button.hasAttribute('disabled')
			)
		).toBe(true);

		const download = screen.getByRole('button', { name: 'Descargar ZIP' });
		expect(download.hasAttribute('disabled')).toBe(false);
		await fireEvent.click(download);
		expect(downloaded).toEqual(['firmas-lote.zip']);
	});

	it('signs an XML document as XAdES-EPES in the queue and ships a .xsig xmlsec1 verifies', async () => {
		const { container } = render(App);

		await loadKey(container);
		await waitFor(() => expect(container.textContent).toContain(LEAF_COMMON_NAME), ASYNC);
		await fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));
		await fireEvent.change(fileInput(container), {
			target: {
				files: [new File(['<fe:Facturae xmlns:fe="urn:fe"><N>2026-0001</N></fe:Facturae>'], 'facturae.xml', {
					type: 'application/xml'
				})]
			}
		});
		// The format chooser is a button labeled with the current format; it opens the modal.
		await fireEvent.click(screen.getByRole('button', { name: /CAdES-BES/ }));
		await fireEvent.click(screen.getByRole('radio', { name: /XAdES-EPES/ }));

		const downloads = captureDownloads();
		try {
			await fireEvent.click(screen.getByRole('button', { name: 'Firmar' }));
			await waitFor(() => expect(container.textContent).toContain('Firmados: 1/1'), ASYNC);
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
		expect(xmlsec1Verify(path, join(fixtures.dir, 'leaf.pem'))).toBe('OK');
	});

	it('timestamps the signature when a TSA address is typed in the panel (CAdES-T)', async () => {
		const tsa = createTsaFixtures();
		const { container } = render(App);
		try {
			vi.stubGlobal('fetch', async (_url: string, init: RequestInit) => {
				const query = init.body;
				if (!(query instanceof Uint8Array)) throw new Error('the timestamp request has no DER body');
				return new Response(tsa.respond(query), { status: 200 });
			});

			await loadKey(container);
			await fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));
			await fireEvent.change(fileInput(container), {
				target: { files: [new File(['contenido\n'], 'contrato.txt', { type: 'text/plain' })] }
			});

			// The TSA field starts empty (no timestamp) and takes the address the user types.
			const tsaField = container.querySelector<HTMLInputElement>('#tsa-url');
			expect(tsaField?.value).toBe('');
			await fireEvent.input(tsaField!, { target: { value: 'https://tsa.test/' } });

			const downloads = captureDownloads();
			try {
				await fireEvent.click(screen.getByRole('button', { name: 'Firmar' }));
				await waitFor(() => expect(container.textContent).toContain('Firmados: 1/1'), {
					timeout: 25_000
				});
				await fireEvent.click(screen.getByRole('button', { name: 'Descargar ZIP' }));
			} finally {
				downloads.restore();
			}

			const archive = writeFixtureFile(
				fixtures.dir,
				'tsa-lote.zip',
				new Uint8Array(await downloads.blobs[0].arrayBuffer())
			);
			const csig = new Uint8Array(execFileSync('unzip', ['-p', archive, 'contrato.txt.csig']));
			const { signature, token } = cmsTimeStamp(csig);
			expect(tsVerifyToken(tsa.dir, tsa.certPath, token, signature)).toMatch(/Verification: OK/);
		} finally {
			vi.unstubAllGlobals();
			rmSync(tsa.dir, { recursive: true, force: true });
		}
	});

	it('keeps the picked signature but disables signing when the document list is emptied', async () => {
		const { container } = render(App);

		await loadKey(container);
		await waitFor(() => expect(container.textContent).toContain(LEAF_COMMON_NAME), ASYNC);
		await fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));
		await fireEvent.change(fileInput(container), {
			target: { files: [new File(['%PDF-1.4'], 'contrato.pdf', { type: 'application/pdf' })] }
		});
		expect(screen.getByRole('button', { name: 'Firmar' }).hasAttribute('disabled')).toBe(false);

		await fireEvent.click(screen.getByRole('button', { name: 'Quitar' }));

		// Removing the document must not throw the signature away — configuring the signature and
		// choosing its documents can happen in either order — but nothing signable is left.
		expect(screen.getByRole('button', { name: 'Firmar' }).hasAttribute('disabled')).toBe(true);

		await fireEvent.change(fileInput(container), {
			target: { files: [new File(['%PDF-1.4'], 'otro.pdf', { type: 'application/pdf' })] }
		});
		expect(screen.getByRole('button', { name: 'Firmar' }).hasAttribute('disabled')).toBe(false);
	});

	it('keeps a signature picked before any document exists', async () => {
		const { container } = render(App);

		// The signature lives on step 1 and the documents on step 2: load it, move on, drop files.
		await loadKey(container);
		await waitFor(() => expect(container.textContent).toContain(LEAF_COMMON_NAME), ASYNC);
		await fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));
		expect(screen.getByRole('button', { name: 'Firmar' }).hasAttribute('disabled')).toBe(true);

		await fireEvent.change(fileInput(container), {
			target: { files: [new File(['%PDF-1.4'], 'contrato.pdf', { type: 'application/pdf' })] }
		});
		// The picker's certificate summary is step-1 UI; what matters here is that moving on
		// never discarded the picked key: Firmar enables as soon as a document lands.
		expect(screen.getByRole('button', { name: 'Firmar' }).hasAttribute('disabled')).toBe(false);
	});

});

describe('MCP endpoint settings', () => {
	it('stores the endpoint and token typed into the settings modal', async () => {
		render(App);
		await fireEvent.click(screen.getByRole('button', { name: 'Ajustes y ayuda' }));
		await fireEvent.click(await screen.findByRole('tab', { name: 'MCP' }));

		const url = await screen.findByLabelText('Dirección del endpoint');
		await fireEvent.input(url, { target: { value: 'https://mcp.example.com/mcp' } });
		await fireEvent.input(screen.getByLabelText('Token de acceso'), { target: { value: 'secreto' } });

		expect(JSON.parse(localStorage.getItem('autofirme-mcp')!)).toEqual({
			url: 'https://mcp.example.com/mcp',
			token: 'secreto',
			enabled: false
		});
		await screen.findByText(/solo lo usa la extensión/);
	});
});
