import { execFileSync, spawnSync } from 'node:child_process';
import { mkdtempSync, readFileSync, rmSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join, resolve } from 'node:path';
import { afterAll, describe, expect, it, vi } from 'vitest';
import { explicit, oid, parseDer } from '#lib/asn1';
import { signCadesDetached } from '#lib/cms';
import { loadSignerSession } from '#lib/keystore';
import { requestTimeStamp } from '#lib/timestamp';
import { signXades } from '#lib/xades';
import {
	detectFormat,
	extractPadesSignatures,
	verifyArtifact,
	type CheckStatus,
	type VerificationReport
} from '#lib/verify';
import {
	createCadesFixtures,
	createPkcs12Fixtures,
	createTsaFixtures,
	FIXTURE_PIN,
	type TsaFixtures
} from '../test/fixtures';

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

	it('says a PDF without a signature carries nothing to verify', async () => {
		const report = await verifyArtifact(new TextEncoder().encode('%PDF-1.7\n'));
		expect(report.format).toBe('pdf');
		expect(report.ok).toBe(false);
		expect(status(report, 'structure')).toBe('fail');
	});
});

describe('PAdES verification (PDF, #11)', () => {
	// The golden PDF's signer is self-signed: for this suite it is its own trust anchor.
	const padesRoot = readFileSync(fixtures + 'pades-signer.crt', 'utf8');

	it('verifies the signature embedded in a signed PDF', async () => {
		const report = await verifyArtifact(read('pades-b-b-signed.pdf'), { trustedRoots: [padesRoot] });

		expect(report.format).toBe('pdf');
		expect(report.ok).toBe(true);
		expect(status(report, 'structure')).toBe('pass');
		expect(report.checks.find((entry) => entry.id === 'structure')?.detail).toContain('ByteRange');
		expect(status(report, 'contentDigest')).toBe('pass');
		expect(status(report, 'signature')).toBe('pass');
		expect(status(report, 'signerCert')).toBe('pass');
		expect(status(report, 'certValidity')).toBe('pass');
		expect(status(report, 'certChain')).toBe('pass');
		expect(report.signer?.subject).toContain('Test Signer RSA');
		expect(report.signingTime).toMatch(/^\d{4}-\d{2}-\d{2}T/);
	});

	it('flips the content digest when the signed document is tampered with', async () => {
		const bytes = read('pades-b-b-signed.pdf');
		bytes[5] ^= 0x01; // inside the first range, outside the signature dictionary

		const report = await verifyArtifact(bytes, { trustedRoots: [padesRoot] });

		expect(status(report, 'contentDigest')).toBe('fail');
		// The signed attributes are untouched: only the document digest moved.
		expect(status(report, 'signature')).toBe('pass');
		expect(report.ok).toBe(false);
	});

	it('hands openssl byte ranges it agrees with', () => {
		// The extraction is our own code; openssl is the oracle for the bytes it hands over.
		const [signature] = extractPadesSignatures(read('pades-b-b-signed.pdf'));
		if (!signature) throw new Error('the golden PDF carries no signature');

		const dir = mkdtempSync(join(tmpdir(), 'pades-verify-'));
		try {
			writeFileSync(join(dir, 'cms.der'), signature.cms);
			writeFileSync(join(dir, 'content.bin'), signature.content);
			const result = spawnSync(
				'openssl',
				[
					'cms', '-verify', '-binary', '-inform', 'DER',
					'-in', join(dir, 'cms.der'),
					'-content', join(dir, 'content.bin'),
					'-noverify'
				],
				{ encoding: 'utf8' }
			);
			expect(result.status, result.stderr).toBe(0);
			expect(result.stderr).toMatch(/Verification successful/i);
		} finally {
			rmSync(dir, { recursive: true, force: true });
		}
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

	it('reports an expired signer certificate as a warning, not a failure (#40)', async () => {
		// The fixture cert is valid 2023→2033; verify after its notAfter. #40 settled this: an
		// expired signer still verifies — the cryptography is sound — and the UI warns instead.
		const report = await verifyArtifact(read('cades-valid.csig'), {
			trustedRoots: [testRoot],
			now: new Date('2034-06-01T00:00:00Z')
		});
		expect(report.status).toBe('valid-but-expired');
		expect(status(report, 'certValidity')).toBe('warn');
		expect(report.checks.find((entry) => entry.id === 'certValidity')?.detail).toMatch(/expired/);
		expect(report.ok).toBe(true);
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

	it('turns a malformed SignatureValue base64 into a failed structure check, not a crash', async () => {
		const xml = new TextDecoder().decode(read('xades-valid.xsig'));
		const corrupt = xml.replace(
			/<ds:SignatureValue>[^<]*<\/ds:SignatureValue>/,
			'<ds:SignatureValue>@@not-base64@@</ds:SignatureValue>'
		);
		expect(corrupt).not.toBe(xml);

		const report = await verifyArtifact(new TextEncoder().encode(corrupt));

		expect(report.ok).toBe(false);
		expect(status(report, 'structure')).toBe('fail');
	});

	it('fails the chain check when the signing root is not a trust anchor', async () => {
		const report = await verifyArtifact(read('xades-valid.xsig'));

		expect(status(report, 'certChain')).toBe('fail');
		expect(status(report, 'signature')).toBe('pass');
		expect(report.ok).toBe(false);
	});
});

describe('expired signer certificates (#40)', () => {
	// Generated on the fly: a CA-signed leaf whose window (2024 → 2025) is entirely in the past.
	const cades = createCadesFixtures();
	afterAll(() => rmSync(cades.dir, { recursive: true, force: true }));

	it('signs with an expired certificate and verifies as valid-but-expired, not invalid', async () => {
		const { signer } = await loadSignerSession(cades.expiredP12, FIXTURE_PIN);
		expect(signer.expired).toBe(true);
		const signature = await signCadesDetached(signer, cades.content);

		// No clock override: the report must reach that verdict against the real clock.
		const report = await verifyArtifact(signature, {
			detached: cades.content,
			trustedRoots: [readFileSync(cades.caPath, 'utf8')]
		});

		expect(report.status).toBe('valid-but-expired');
		expect(report.ok).toBe(true); // warning only: the signature itself is sound
		expect(status(report, 'signature')).toBe('pass');
		expect(status(report, 'contentDigest')).toBe('pass');
		expect(status(report, 'certChain')).toBe('pass');
		expect(status(report, 'certValidity')).toBe('warn');
		expect(report.checks.find((entry) => entry.id === 'certValidity')?.detail).toMatch(/expired/);
		expect(new Date(report.signer!.notAfter).toISOString()).toBe(cades.expiredNotAfter);
	});

	it('turns a currently valid CAdES signer into valid-but-expired under a later clock', async () => {
		const bytes = read('cades-valid.csig');
		const now = await verifyArtifact(bytes, { trustedRoots: [testRoot] });
		expect(now.status).toBe('valid');

		const later = await verifyArtifact(bytes, {
			trustedRoots: [testRoot],
			now: new Date(Date.parse(now.signer!.notAfter) + 1000)
		});
		expect(later.status).toBe('valid-but-expired');
		expect(later.ok).toBe(true);
		expect(status(later, 'signature')).toBe('pass');
		expect(status(later, 'certValidity')).toBe('warn');
	});

	it('does the same for XAdES', async () => {
		const bytes = read('xades-valid.xsig');
		const now = await verifyArtifact(bytes, { trustedRoots: [testRoot] });
		expect(now.status).toBe('valid');

		const later = await verifyArtifact(bytes, {
			trustedRoots: [testRoot],
			now: new Date(Date.parse(now.signer!.notAfter) + 1000)
		});
		expect(later.status).toBe('valid-but-expired');
		expect(later.ok).toBe(true);
		expect(status(later, 'signature')).toBe('pass');
		expect(status(later, 'certValidity')).toBe('warn');
	});

	it('still fails a signature whose content was tampered with, expired certificate or not', async () => {
		const report = await verifyArtifact(read('cades-tampered.csig'), {
			trustedRoots: [testRoot],
			now: new Date(Date.parse('2033-06-01T00:00:00Z'))
		});

		expect(status(report, 'contentDigest')).toBe('fail');
		expect(report.status).toBe('invalid');
		expect(report.ok).toBe(false);
		expect(status(report, 'certValidity')).toBe('warn');
	});
});

describe('timestamps, XAdES-T and CAdES-X Long (#125)', () => {
	const cades = createCadesFixtures();
	afterAll(() => rmSync(cades.dir, { recursive: true, force: true }));
	const tsaUrl = 'https://tsa.test/';

	/** Sign through the TSA stub; the stub answers every request with a real openssl token. */
	async function withTsa<T>(run: (tsa: TsaFixtures) => Promise<T>): Promise<T> {
		const tsa = createTsaFixtures();
		vi.stubGlobal(
			'fetch',
			async (_url: string, init: RequestInit) => new Response(tsa.respond(init.body as Uint8Array), { status: 200 })
		);
		try {
			return await run(tsa);
		} finally {
			vi.unstubAllGlobals();
			rmSync(tsa.dir, { recursive: true, force: true });
		}
	}

	const roots = () => ({ detached: cades.content, trustedRoots: [readFileSync(cades.caPath, 'utf8')] });

	it('passes the timestamp check for a CAdES-T signature', async () => {
		await withTsa(async () => {
			const { signer } = await loadSignerSession(cades.p12, FIXTURE_PIN);
			const signature = await signCadesDetached(signer, cades.content, { timeStamp: tsaUrl });
			const report = await verifyArtifact(signature, roots());

			expect(status(report, 'timestamp')).toBe('pass');
			expect(report.checks.find((entry) => entry.id === 'timestamp')?.detail).toMatch(/signature value/);
			expect(report.ok).toBe(true);
		});
	});

	it('fails the timestamp check with a reason when the signature bytes are tampered with', async () => {
		await withTsa(async () => {
			const { signer } = await loadSignerSession(cades.p12, FIXTURE_PIN);
			const signature = await signCadesDetached(signer, cades.content, { timeStamp: tsaUrl });
			// Flip one byte inside the signature OCTET STRING: DER lengths untouched, so the
			// structure still parses and the timestamp check gets to speak for itself.
			const signerInfo = explicit(parseDer(signature).children[1], 0xa0).children[4].children[0];
			const value = signerInfo.children[5].value;
			signature[value.byteOffset - signature.byteOffset] ^= 0x01;

			const report = await verifyArtifact(signature, roots());
			expect(status(report, 'timestamp')).toBe('fail');
			expect(report.checks.find((entry) => entry.id === 'timestamp')?.detail).toMatch(/covers neither/);
			expect(report.ok).toBe(false);
		});
	});

	it('passes both timestamps for CAdES-X Long type 2', async () => {
		await withTsa(async () => {
			const { signer } = await loadSignerSession(cades.p12, FIXTURE_PIN);
			const signature = await signCadesDetached(signer, cades.content, { timeStamp: tsaUrl, xLong: 2 });
			const report = await verifyArtifact(signature, roots());

			const stamps = report.checks.filter((entry) => entry.id === 'timestamp');
			expect(stamps).toHaveLength(2);
			expect(stamps.every((entry) => entry.status === 'pass')).toBe(true);
			expect(stamps.map((entry) => entry.detail).join(' | ')).toMatch(/refs/);
			expect(report.ok).toBe(true);
		});
	});

	it('passes both timestamps for CAdES-X Long type 1', async () => {
		await withTsa(async () => {
			const { signer } = await loadSignerSession(cades.p12, FIXTURE_PIN);
			const signature = await signCadesDetached(signer, cades.content, { timeStamp: tsaUrl, xLong: 1 });
			const report = await verifyArtifact(signature, roots());

			const stamps = report.checks.filter((entry) => entry.id === 'timestamp');
			expect(stamps).toHaveLength(2);
			expect(stamps.every((entry) => entry.status === 'pass')).toBe(true);
			// The escTimeStamp covers signature + CAdES-T token + refs (RFC 5126 §6.3.5).
			expect(stamps.map((entry) => entry.detail).join(' | ')).toMatch(/CAdES-T timestamp and the certificate and revocation refs/);
			expect(report.ok).toBe(true);
		});
	});

	it('fails the CAdES-C timestamp when the refs no longer match its imprint', async () => {
		await withTsa(async () => {
			const { signer } = await loadSignerSession(cades.p12, FIXTURE_PIN);
			const signature = await signCadesDetached(signer, cades.content, { timeStamp: tsaUrl, xLong: 2 });
			// Flip one byte inside the certificate-refs value: the SET still parses, but the bytes the
			// certCRLTimestamp covers are gone, so its check must fail with a reason.
			const signerInfo = explicit(parseDer(signature).children[1], 0xa0).children[4].children[0];
			const refs = signerInfo.children[6].children.find(
				(attribute) => oid(attribute.children[0]) === '1.2.840.113549.1.9.16.2.21'
			)!;
			const value = refs.children[1].children[0].value;
			signature[value.byteOffset - signature.byteOffset + 3] ^= 0x01;

			const report = await verifyArtifact(signature, roots());
			const stamps = report.checks.filter((entry) => entry.id === 'timestamp');
			expect(stamps.some((entry) => entry.status === 'fail' && /covers neither/.test(entry.detail))).toBe(true);
			expect(report.ok).toBe(false);
		});
	});

	it('reports info, never failure, for a BES signature without timestamps', async () => {
		const { signer } = await loadSignerSession(cades.p12, FIXTURE_PIN);
		const report = await verifyArtifact(await signCadesDetached(signer, cades.content), roots());

		expect(status(report, 'timestamp')).toBe('info');
		expect(report.ok).toBe(true);
	});

	it('passes the timestamp check for XAdES-T and reports info without one', async () => {
		await withTsa(async () => {
			const p12 = createPkcs12Fixtures();
			try {
				const session = await loadSignerSession(p12.modern, FIXTURE_PIN);
				const stamped = await signXades({
					xml: '<facturae xmlns:ds="http://www.w3.org/2000/09/xmldsig#"><id>B12345678</id></facturae>',
					certificates: session.certificates,
					signer: session.signer,
					timeStamp: tsaUrl
				});
				const report = await verifyArtifact(new TextEncoder().encode(stamped));
				expect(status(report, 'timestamp')).toBe('pass');

				const plain = await signXades({
					xml: '<facturae xmlns:ds="http://www.w3.org/2000/09/xmldsig#"><id>B12345678</id></facturae>',
					certificates: session.certificates,
					signer: session.signer
				});
				expect(status(await verifyArtifact(new TextEncoder().encode(plain)), 'timestamp')).toBe('info');
			} finally {
				rmSync(p12.dir, { recursive: true, force: true });
			}
		});
	});

	it('rejects a SignatureTimeStamp computed over a different canonicalisation', async () => {
		await withTsa(async (tsa) => {
			const p12 = createPkcs12Fixtures();
			try {
				const session = await loadSignerSession(p12.modern, FIXTURE_PIN);
				const xml = '<facturae xmlns:ds="http://www.w3.org/2000/09/xmldsig#"><id>B12345678</id></facturae>';
				const stamped = await signXades({ xml, certificates: session.certificates, signer: session.signer, timeStamp: tsaUrl });
				// A token over other bytes — e.g. a non-canonical serialisation — smuggled into
				// the EncapsulatedTimeStamp. The XML stays well-formed, so the check must catch it.
				const wrong = Buffer.from(
					await requestTimeStamp(tsaUrl, new TextEncoder().encode('<ds:SignatureValue>other</ds:SignatureValue>'))
				).toString('base64');
				const tampered = stamped.replace(/<xades:EncapsulatedTimeStamp>[^<]*</, `<xades:EncapsulatedTimeStamp>${wrong}<`);
				expect(tampered).not.toBe(stamped);

				const report = await verifyArtifact(new TextEncoder().encode(tampered));
				expect(status(report, 'timestamp')).toBe('fail');
				expect(report.checks.find((entry) => entry.id === 'timestamp')?.detail).toMatch(/covers different bytes/);
				expect(report.ok).toBe(false);
			} finally {
				rmSync(p12.dir, { recursive: true, force: true });
			}
		});
	});
});

describe('fixture cross-check', () => {
	it('is confirmed by openssl and an independent digest recomputation', () => {
		const output = execFileSync('bash', [fixtures + 'verify-fixtures.sh'], { encoding: 'utf8' });
		expect(output).toContain('ok ');
		expect(output).not.toContain('FAIL');
	}, 120_000);
});
