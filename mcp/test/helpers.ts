/**
 * Shared fixtures for the MCP tests.
 *
 * PKCS#12 containers, a CA and a document are generated with the `openssl` CLI into a temp
 * directory (the same helper the app's tests use) — nothing binary is committed, and the keys the tests
 * sign with are throwaway.
 */

import { mkdirSync, mkdtempSync, readFileSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { createCadesFixtures, FIXTURE_PIN, type CadesFixtures } from '../../src/test/fixtures';
import { DEFAULT_MAX_BYTES, type ServerConfig } from '../src/config';

export function tempDir(prefix: string): string {
	return mkdtempSync(join(tmpdir(), prefix));
}

/** A config with every limit at its default, so a test only states what it changes. */
export function configFor(workspace: string, overrides: Partial<ServerConfig> = {}): ServerConfig {
	return {
		certDir: null,
		pin: null,
		workspace,
		maxBytes: DEFAULT_MAX_BYTES,
		auditLog: null,
		trustedRoots: null,
		token: null,
		host: '127.0.0.1',
		port: 0,
		...overrides
	};
}

export interface StoreFixture {
	fixtures: CadesFixtures;
	/** Directory holding `prueba.p12`, as `AUTOFIRME_MCP_CERTS` would point at. */
	certDir: string;
	/** `AUTOFIRME_MCP_WORKSPACE`: holds `documento.txt`. */
	workspace: string;
	config: ServerConfig;
}

/** A keystore directory with one CA-signed leaf container, and a workspace with one document. */
export function storeFixture(): StoreFixture {
	const fixtures = createCadesFixtures();
	const root = tempDir('autofirme-mcp-');
	const certDir = join(root, 'certs');
	const workspace = join(root, 'workspace');
	mkdirSync(certDir);
	mkdirSync(workspace);
	writeFileSync(join(certDir, 'prueba.p12'), fixtures.p12);
	writeFileSync(join(workspace, 'documento.txt'), fixtures.content);
	// The fixture CA is private, so the operator has to declare it as a trust anchor — exactly what
	// AUTOFIRME_MCP_TRUSTED_ROOTS does in production.
	return {
		fixtures,
		certDir,
		workspace,
		config: configFor(workspace, {
			certDir,
			pin: FIXTURE_PIN,
			trustedRoots: [readFileSync(fixtures.caPath, 'utf8')]
		})
	};
}
