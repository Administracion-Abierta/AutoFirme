/** Environment → config, including the one value an operator can get wrong. */

import { writeFileSync } from 'node:fs';
import { join, resolve } from 'node:path';
import { describe, expect, it } from 'vitest';
import { DEFAULT_MAX_BYTES, loadConfig } from '../src/config';
import { tempDir } from './helpers';

describe('loadConfig', () => {
	it('defaults the workspace to the process and leaves signing unconfigured', () => {
		const config = loadConfig({});
		expect(config.certDir).toBeNull();
		expect(config.pin).toBeNull();
		expect(config.workspace).toBe(resolve(process.cwd()));
		expect(config.maxBytes).toBe(DEFAULT_MAX_BYTES);
		expect(config.auditLog).toBeNull();
		expect(config.trustedRoots).toBeNull();
	});

	it('resolves the paths it is given', () => {
		const config = loadConfig({
			AUTOFIRME_MCP_CERTS: './certs',
			AUTOFIRME_MCP_PIN: '1234',
			AUTOFIRME_MCP_WORKSPACE: './work',
			AUTOFIRME_MCP_MAX_BYTES: '1024',
			AUTOFIRME_MCP_AUDIT_LOG: './audit.jsonl'
		});
		expect(config.certDir).toBe(resolve('certs'));
		expect(config.pin).toBe('1234');
		expect(config.workspace).toBe(resolve('work'));
		expect(config.maxBytes).toBe(1024);
		expect(config.auditLog).toBe(resolve('audit.jsonl'));
	});

	it('treats a blank certificate directory as unset, keeping a blank PIN as given', () => {
		const config = loadConfig({ AUTOFIRME_MCP_CERTS: '  ', AUTOFIRME_MCP_PIN: '' });
		expect(config.certDir).toBeNull();
		expect(config.pin).toBe('');
	});

	it('refuses a byte limit that is not a positive integer', () => {
		expect(() => loadConfig({ AUTOFIRME_MCP_MAX_BYTES: '0' })).toThrow(/positive integer/);
		expect(() => loadConfig({ AUTOFIRME_MCP_MAX_BYTES: 'mucho' })).toThrow(/positive integer/);
	});

	it('loads the operator trust anchors at startup', () => {
		const pem = join(tempDir('autofirme-roots-'), 'roots.pem');
		writeFileSync(pem, '-----BEGIN CERTIFICATE-----\nMIIB\n-----END CERTIFICATE-----\n');
		expect(loadConfig({ AUTOFIRME_MCP_TRUSTED_ROOTS: pem }).trustedRoots).toEqual([
			'-----BEGIN CERTIFICATE-----\nMIIB\n-----END CERTIFICATE-----\n'
		]);
		// A missing bundle is a configuration error, not a silent fallback to the public roots.
		expect(() => loadConfig({ AUTOFIRME_MCP_TRUSTED_ROOTS: join(pem, 'nope.pem') })).toThrow();
	});
});
