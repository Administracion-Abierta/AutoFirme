/**
 * The three tools against the real engine: a signature the server makes must verify, and every
 * refusal must be a typed `ToolError` rather than a wrong answer.
 */

import { readFileSync, writeFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { join } from 'node:path';
import { beforeAll, describe, expect, it } from 'vitest';
import { CertStore } from '../src/certstore';
import { ToolError } from '../src/input';
import { listCerts, signDocument, verifyDocument } from '../src/tools';
import { storeFixture, tempDir, type StoreFixture } from './helpers';

const SIGNING_TIME = '2026-10-14T09:00:00.000Z';

let fixture: StoreFixture;
let store: CertStore;
/** The signable certificate's id, and the document as the tests hand it over. */
let certId: string;

beforeAll(async () => {
	fixture = storeFixture();
	store = await CertStore.load(fixture.config);
	certId = store.certificates.find((cert) => cert.signable)!.cert_id;
	// One file just outside the workspace, for the escape test.
	writeFileSync(join(fixture.workspace, '..', 'fuera.txt'), 'secreto');
});

async function rejectionCode(run: () => Promise<unknown>): Promise<string> {
	try {
		await run();
	} catch (error) {
		return error instanceof ToolError ? error.code : `not a ToolError: ${String(error)}`;
	}
	return 'no error';
}

const b64 = (bytes: Uint8Array): string => Buffer.from(bytes).toString('base64');

describe('autofirma_sign + autofirma_verify', () => {
	it('signs a document and the artifact verifies against it', async () => {
		const signed = await signDocument(fixture.config, store, {
			path: 'documento.txt',
			format: 'cades',
			cert_id: certId,
			signing_time: SIGNING_TIME
		});
		expect(signed.detached).toBe(true);
		expect(signed.common_name).toBe('Firma CAdES');
		expect(signed.cert_expired).toBe(false);
		expect(signed.signing_time).toBe(SIGNING_TIME);
		expect(signed.bytes).toBe(Buffer.from(signed.signed_b64, 'base64').length);

		const verified = await verifyDocument(fixture.config, {
			artifact_b64: signed.signed_b64,
			detached_b64: b64(fixture.fixtures.content)
		});
		expect(verified.supported).toBe(true);
		expect(verified.report.format).toBe('cades');
		expect(verified.report.ok).toBe(true);
		expect(verified.report.signer?.subject).toContain('Firma CAdES');
		expect(verified.document_sha256).toBe(signed.document_sha256);
	});

	it('is deterministic for a given signing time, over either input channel', async () => {
		const input = { format: 'cades' as const, cert_id: certId, signing_time: SIGNING_TIME };
		const fromPath = await signDocument(fixture.config, store, { ...input, path: 'documento.txt' });
		const fromB64 = await signDocument(fixture.config, store, {
			...input,
			file_b64: b64(fixture.fixtures.content)
		});
		expect(fromB64.sha256).toBe(fromPath.sha256);
	});

	it('fails a tampered document instead of reporting ok', async () => {
		const signed = await signDocument(fixture.config, store, {
			path: 'documento.txt',
			format: 'cades',
			cert_id: certId,
			signing_time: SIGNING_TIME
		});
		const tampered = new TextEncoder().encode('documento de prueba (alterado)\n');
		const verified = await verifyDocument(fixture.config, {
			artifact_b64: signed.signed_b64,
			detached_b64: b64(tampered)
		});
		expect(verified.report.ok).toBe(false);
		expect(verified.report.checks.some((check) => check.status === 'fail')).toBe(true);
	});

	it('refuses the formats this slice cannot produce', async () => {
		for (const format of ['xades', 'pades'] as const) {
			expect(
				await rejectionCode(() =>
					signDocument(fixture.config, store, { path: 'documento.txt', format, cert_id: certId })
				)
			).toBe('unsupported_format');
		}
	});

	it('refuses a bad signing time, an unknown certificate and an escaping path', async () => {
		expect(
			await rejectionCode(() =>
				signDocument(fixture.config, store, {
					path: 'documento.txt',
					format: 'cades',
					cert_id: certId,
					signing_time: 'ayer'
				})
			)
		).toBe('bad_signing_time');
		expect(
			await rejectionCode(() =>
				signDocument(fixture.config, store, { path: 'documento.txt', format: 'cades', cert_id: 'nope' })
			)
		).toBe('unknown_cert_id');
		expect(
			await rejectionCode(() =>
				signDocument(fixture.config, store, { path: '../fuera.txt', format: 'cades', cert_id: certId })
			)
		).toBe('path_escape');
	});

	it('reports XAdES as unsupported rather than verifying it wrongly', async () => {
		const xades = readFileSync(
			fileURLToPath(new URL('../../test/fixtures/xades-valid.xsig', import.meta.url))
		);
		const verified = await verifyDocument(fixture.config, { artifact_b64: b64(xades) });
		expect(verified.supported).toBe(false);
		expect(verified.report.ok).toBe(false);
		expect(verified.report.checks[0].id).toBe('unsupported');
	});

	it('reports an unrecognised artifact as a red structure check', async () => {
		const verified = await verifyDocument(fixture.config, {
			artifact_b64: b64(new TextEncoder().encode('no soy una firma'))
		});
		expect(verified.report.format).toBe('unknown');
		expect(verified.report.ok).toBe(false);
	});
});

describe('autofirma_list_certs', () => {
	it('lists certificates and configuration problems without key material', () => {
		const result = listCerts(fixture.config, store);
		expect(result.signing_ready).toBe(true);
		expect(result.errors).toEqual([]);
		const serialised = JSON.stringify(result);
		expect(serialised).toContain('Firma CAdES');
		expect(serialised).not.toMatch(/PRIVATE KEY|"key"|"der"/);
	});
});

describe('audit', () => {
	it('appends one JSON line per signature, with the document digest and no content', async () => {
		const auditLog = join(tempDir('autofirme-audit-'), 'audit.jsonl');
		const config = { ...fixture.config, auditLog };
		await signDocument(config, store, {
			path: 'documento.txt',
			format: 'cades',
			cert_id: certId,
			signing_time: SIGNING_TIME
		});
		const events = readFileSync(auditLog, 'utf8').trim().split('\n').map((line) => JSON.parse(line));
		const sign = events.find((event) => event.event === 'sign' && event.ok);
		expect(sign).toBeDefined();
		expect(sign.certId).toBe(certId);
		expect(sign.documentSha256).toMatch(/^[0-9a-f]{64}$/);
		expect(readFileSync(auditLog, 'utf8')).not.toContain('documento de prueba');
	});

	it('records a refused signature as a failure', async () => {
		const auditLog = join(tempDir('autofirme-audit-'), 'audit.jsonl');
		const config = { ...fixture.config, auditLog };
		writeFileSync(join(config.workspace, 'documento2.txt'), 'otro');
		await rejectionCode(() =>
			signDocument(config, store, { path: 'documento2.txt', format: 'xades', cert_id: certId })
		);
		const events = readFileSync(auditLog, 'utf8').trim().split('\n').map((line) => JSON.parse(line));
		expect(events.some((event) => event.event === 'sign' && event.ok === false && event.error === 'unsupported_format')).toBe(
			true
		);
	});
});
