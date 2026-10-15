/** The PKCS#12 store: what loads, what fails loudly, and what an agent is allowed to see. */

import { writeFileSync } from 'node:fs';
import { join } from 'node:path';
import { describe, expect, it } from 'vitest';
import { CertStore } from '../src/certstore';
import { ToolError } from '../src/input';
import { configFor, storeFixture, tempDir } from './helpers';

describe('CertStore', () => {
	it('loads the leaf container and exposes metadata without key material', async () => {
		const { config } = storeFixture();
		const store = await CertStore.load(config);
		expect(store.errors).toEqual([]);
		expect(store.signingReady).toBe(true);
		// The leaf and the CA of the container, and only the leaf owns the private key.
		expect(store.certificates.length).toBeGreaterThanOrEqual(2);
		expect(store.certificates.filter((cert) => cert.signable)).toHaveLength(1);

		const summary = store.certificates.find((cert) => cert.signable)!;
		expect(Object.keys(summary).sort()).toEqual([
			'cert_id',
			'common_name',
			'container',
			'expired',
			'issuer',
			'not_after',
			'not_before',
			'not_yet_valid',
			'serial',
			'signable',
			'subject'
		]);
		expect(summary.container).toBe('prueba');
		expect(summary.common_name).toBe('Firma CAdES');
		expect(JSON.stringify(store.certificates)).not.toMatch(/PRIVATE KEY|"key"|"der"/);

		// `signer()` is what `sign` needs: the real certificate with its non-extractable key.
		const signer = store.signer(summary.cert_id);
		expect(signer.key).not.toBeNull();
	});

	it('names the known ids when the requested one is wrong', async () => {
		const { config } = storeFixture();
		const store = await CertStore.load(config);
		const known = store.certificates.find((cert) => cert.signable)!.cert_id;
		expect(() => store.signer('nope')).toThrow(ToolError);
		try {
			store.signer('nope');
		} catch (error) {
			expect((error as ToolError).code).toBe('unknown_cert_id');
			expect((error as Error).message).toContain(known);
		}
	});

	it('reports a wrong PIN as an error and stays up', async () => {
		const { config } = storeFixture();
		const store = await CertStore.load({ ...config, pin: '9999' });
		expect(store.signingReady).toBe(false);
		expect(store.errors).toHaveLength(1);
		expect(store.errors[0]).toMatch(/prueba\.p12/);
	});

	it('reports missing configuration instead of throwing', async () => {
		expect((await CertStore.load(configFor(tempDir('autofirme-nocfg-')))).errors[0]).toMatch(
			/AUTOFIRME_MCP_CERTS is not set/
		);
		const noPin = await CertStore.load({
			...configFor(tempDir('autofirme-nopin-')),
			certDir: tempDir('autofirme-certdir-')
		});
		expect(noPin.errors[0]).toMatch(/AUTOFIRME_MCP_PIN is not set/);
	});

	it('skips a container whose name uses the id separator', async () => {
		const { config, certDir } = storeFixture();
		writeFileSync(join(certDir, 'mal#nombre.p12'), new Uint8Array(4));
		const store = await CertStore.load(config);
		expect(store.signingReady).toBe(true);
		expect(store.errors).toHaveLength(1);
		expect(store.errors[0]).toContain('mal#nombre.p12');
	});
});
