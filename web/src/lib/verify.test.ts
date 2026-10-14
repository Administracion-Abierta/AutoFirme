import { execFileSync } from 'node:child_process';
import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { describe, expect, it } from 'vitest';
import { detectFormat, verifyArtifact, type CheckStatus, type VerificationReport } from '#lib/verify';

const fixtures = resolve(process.cwd(), 'test/fixtures') + '/';
const read = (name: string) => new Uint8Array(readFileSync(fixtures + name));
const testRoot = readFileSync(fixtures + 'ca/test-root.crt', 'utf8');
const status = (report: VerificationReport, id: string): CheckStatus | undefined =>
	report.checks.find((entry) => entry.id === id)?.status;

describe('format detection', () => {
	it('sniffs CMS, XML and PDF payloads without trusting the file name', () => {
		expect(detectFormat(read('cades-valid.csig'))).toBe('cades');
		expect(detectFormat(read('xades-valid.xsig'))).toBe('xades');
		expect(detectFormat(new TextEncoder().encode('%PDF-1.7\n'))).toBe('pdf');
		expect(detectFormat(new TextEncoder().encode('hola'))).toBe('unknown');
	});

	it('reports an unrecognised artifact instead of pretending it verified', async () => {
		const report = await verifyArtifact(new TextEncoder().encode('not a signature'));
		expect(report.format).toBe('unknown');
		expect(report.ok).toBe(false);
		expect(status(report, 'structure')).toBe('fail');
	});

	it('does not claim to verify PDF/PAdES yet (issue #11)', async () => {
		const report = await verifyArtifact(new TextEncoder().encode('%PDF-1.7\n'));
		expect(report.format).toBe('pdf');
		expect(report.ok).toBe(false);
		expect(status(report, 'unsupported')).toBe('info');
	});
});

describe('CAdES verification', () => {
	it('passes every check for a valid attached signature', async () => {
		const report = await verifyArtifact(read('cades-valid.csig'), { trustedRoots: [testRoot] });

		expect(report.format).toBe('cades');
		expect(report.ok).toBe(true);
		expect(status(report, 'signature')).toBe('pass');
		expect(status(report, 'contentDigest')).toBe('pass');
		expect(status(report, 'certChain')).toBe('pass');
		expect(status(report, 'certValidity')).toBe('pass');
		expect(status(report, 'signingTime')).toBe('pass');
		expect(report.signer?.subject).toContain('AutoFirma Web Test Signer');
		expect(report.signingTime).toMatch(/^\d{4}-\d{2}-\d{2}T/);
	});

	it('flips only the content integrity check for a tampered document', async () => {
		const report = await verifyArtifact(read('cades-tampered.csig'), { trustedRoots: [testRoot] });

		expect(status(report, 'contentDigest')).toBe('fail');
		// The signed attributes are untouched, so the signature itself still verifies.
		expect(status(report, 'signature')).toBe('pass');
		expect(report.ok).toBe(false);
	});

	it('cannot judge the content of a detached signature until the document is supplied', async () => {
		const withoutDocument = await verifyArtifact(read('cades-detached.csig'), { trustedRoots: [testRoot] });
		expect(status(withoutDocument, 'contentDigest')).toBe('info');
		expect(status(withoutDocument, 'signature')).toBe('pass');

		const withDocument = await verifyArtifact(read('cades-detached.csig'), {
			trustedRoots: [testRoot],
			detached: read('document.txt')
		});
		expect(status(withDocument, 'contentDigest')).toBe('pass');
		expect(withDocument.ok).toBe(true);

		const withTamperedDocument = await verifyArtifact(read('cades-detached.csig'), {
			trustedRoots: [testRoot],
			detached: read('document-tampered.txt')
		});
		expect(status(withTamperedDocument, 'contentDigest')).toBe('fail');
	});

	it('rejects a chain that does not end in a trust anchor', async () => {
		const report = await verifyArtifact(read('cades-valid.csig'));

		expect(status(report, 'certChain')).toBe('fail');
		expect(report.checks.find((entry) => entry.id === 'certChain')?.detail).toMatch(
			/not in trust store|issuer certificate not included/
		);
		// Everything that does not depend on the trust store still passes.
		expect(status(report, 'signature')).toBe('pass');
		expect(status(report, 'contentDigest')).toBe('pass');
		expect(report.ok).toBe(false);
	});

	it('fails the signature check when the signature bytes themselves are tampered with', async () => {
		const bytes = read('cades-valid.csig');
		const flipped = bytes.slice();
		flipped[flipped.length - 5] ^= 0x01;
		const report = await verifyArtifact(flipped, { trustedRoots: [testRoot] });

		expect(status(report, 'signature')).toBe('fail');
		// The content is intact, so only the cryptographic check may complain.
		expect(status(report, 'contentDigest')).toBe('pass');
		expect(report.ok).toBe(false);
	});

	it('reports an unreadable CMS structure as a failed check, not a crash', async () => {
		const truncated = read('cades-valid.csig').subarray(0, 40);
		const report = await verifyArtifact(new Uint8Array(truncated));

		expect(report.format).toBe('cades');
		expect(status(report, 'structure')).toBe('fail');
		expect(report.ok).toBe(false);
	});
});

describe('XAdES verification', () => {
	it('passes every reference and the SignedInfo signature for a valid signature', async () => {
		const report = await verifyArtifact(read('xades-valid.xsig'), { trustedRoots: [testRoot] });

		expect(report.format).toBe('xades');
		expect(status(report, 'signature')).toBe('pass');
		expect(status(report, 'reference')).toBe('pass');
		expect(status(report, 'certChain')).toBe('pass');
		expect(status(report, 'signingTime')).toBe('pass');
		expect(report.ok).toBe(true);
		expect(report.signingTime).toMatch(/^\d{4}-\d{2}-\d{2}T/);
	});

	it('fails the tampered reference while the SignedInfo signature still verifies', async () => {
		const report = await verifyArtifact(read('xades-tampered.xsig'), { trustedRoots: [testRoot] });

		const references = report.checks.filter((entry) => entry.id === 'reference');
		expect(references.length).toBeGreaterThan(1);
		expect(references.some((entry) => entry.status === 'fail')).toBe(true);
		expect(report.checks.filter((entry) => entry.id === 'signature').every((entry) => entry.status === 'pass')).toBe(true);
		expect(report.ok).toBe(false);
	});

	it('fails the signature check when the SignatureValue is tampered with', async () => {
		const xml = new TextDecoder().decode(read('xades-valid.xsig'));
		// First base64 character of the SignatureValue; length preserved, so parsing still succeeds.
		const tampered = xml.replace(/<ds:SignatureValue>([A-Za-z0-9+/=])/, (_, c: string) => `<ds:SignatureValue>${c === 'A' ? 'B' : 'A'}`);
		expect(tampered).not.toBe(xml);

		const report = await verifyArtifact(new TextEncoder().encode(tampered), { trustedRoots: [testRoot] });

		expect(status(report, 'signature')).toBe('fail');
		expect(report.checks.filter((entry) => entry.id === 'reference').every((entry) => entry.status === 'pass')).toBe(true);
		expect(report.ok).toBe(false);
	});

	it('fails the chain check when the signing root is not a trust anchor', async () => {
		const report = await verifyArtifact(read('xades-valid.xsig'));

		expect(status(report, 'certChain')).toBe('fail');
		expect(status(report, 'signature')).toBe('pass');
		expect(report.ok).toBe(false);
	});
});

describe('fixture cross-check', () => {
	it('is confirmed by openssl and an independent digest recomputation', () => {
		const output = execFileSync('bash', [fixtures + 'verify-fixtures.sh'], { encoding: 'utf8' });
		expect(output).toContain('ok ');
		expect(output).not.toContain('FAIL');
	}, 120_000);
});
