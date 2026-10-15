/**
 * End-to-end smoke of the sign → verify loop (issue #39), driven by `scripts/smoke-real.sh`.
 *
 * It runs the app's own code — `loadSignerSession` (PKCS#12 → non-extractable key), `signCadesDetached`
 * (CAdES-BES) and `verifyArtifact` — over a document, for two keystores:
 *
 *   - `SMOKE_CERT` / `SMOKE_PIN_FILE`: a real keystore (the FNMT citizen `.p12` of `work/certs/`),
 *   - `SMOKE_GENERATED=1`: the self-signed container generated on the fly by `src/test/fixtures.ts`.
 *
 * The loop is independent of this repo's writer in two places: `openssl cms -verify` re-checks the
 * produced `.csig` against the document, and `openssl x509` re-reads the signer certificate's subject
 * and validity dates, which are compared with what the report claims. Neither the PIN nor any key
 * material is ever printed. This file lives outside `src/` on purpose: the app suite's `include`
 * (`src/**\/*.test.ts` in `vite.config.ts`) must not pick it up — see `scripts/vitest.smoke.config.ts`.
 */

import { execFileSync, spawnSync } from 'node:child_process';
import { mkdtempSync, readFileSync, rmSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { afterAll, expect, test } from 'vitest';
import type { Bytes } from '../src/lib/asn1';
import { signCadesDetached } from '../src/lib/cms';
import { loadSignerSession } from '../src/lib/keystore';
import type { SignerCert } from '../src/lib/sign';
import { verifyArtifact, type VerificationReport } from '../src/lib/verify';
import { FIXTURE_PIN, createPkcs12Fixtures } from '../src/test/fixtures';

/** The signed document. A newline in it is why every `openssl cms` call below passes `-binary`. */
const DOCUMENT: Bytes = new TextEncoder().encode('AutoFirma Web — documento de prueba E2E\n');

/** Checks a valid signature must produce; `certChain` is reported but asserted separately below. */
const REQUIRED_CHECKS = [
	'structure',
	'contentDigest',
	'signature',
	'signerCert',
	'certValidity',
	'signingTime'
] as const;

interface Keystore {
	name: string;
	/** What is being checked — a path, or the fixture generator. */
	label: string;
	bytes: Bytes;
	pin: string;
}

/** The report's check of that id — looked up by seven assertions and both `certChain` printouts. */
function check(report: VerificationReport, id: string) {
	return report.checks.find((entry) => entry.id === id);
}

/** The generated fixture's temp directory, removed in `afterAll` below. */
let generatedDir: string | undefined;

/** Cases come from the environment, so the wrapper decides what runs and what is skipped. */
function keystores(): Keystore[] {
	const stores: Keystore[] = [];
	const certificatePath = process.env.SMOKE_CERT;
	const pinPath = process.env.SMOKE_PIN_FILE;
	if (certificatePath && pinPath) {
		stores.push({
			name: 'real keystore',
			label: certificatePath,
			bytes: new Uint8Array(readFileSync(certificatePath)),
			pin: readFileSync(pinPath, 'utf8').trim()
		});
	}
	if (process.env.SMOKE_GENERATED === '1') {
		const fixtures = createPkcs12Fixtures();
		generatedDir = fixtures.dir;
		stores.push({
			name: 'generated keystore',
			label: 'self-signed container from src/test/fixtures.ts',
			bytes: fixtures.modern,
			pin: FIXTURE_PIN
		});
	}
	if (stores.length === 0) throw new Error('no keystore selected — run scripts/smoke-real.sh');
	return stores;
}

afterAll(() => {
	if (generatedDir) rmSync(generatedDir, { recursive: true, force: true });
});

for (const keystore of keystores()) {
	test(`signs and verifies with the ${keystore.name} (${keystore.label})`, async () => {
		const session = await loadSignerSession(keystore.bytes, keystore.pin);
		const signer: SignerCert = session.signer;
		const chain = session.certificates.filter((certificate) => certificate !== signer);
		console.log(
			`${keystore.name}: signer ${signer.subject} — serial ${signer.serial}, ` +
				`valid ${signer.notBefore.toISOString()} → ${signer.notAfter.toISOString()}, ` +
				`${chain.length} chain certificate(s) in the container`
		);

		const signature = await signCadesDetached(signer, DOCUMENT);
		const report = await verifyArtifact(signature, { detached: DOCUMENT });

		// What the report must say: every check of the loop is green, the signer identity is the one
		// the container's key belongs to, and the validity window is in the output.
		for (const id of REQUIRED_CHECKS) {
			expect(check(report, id)?.status, `${id}: ${check(report, id)?.detail}`).toBe('pass');
		}
		expect(report.format).toBe('cades');
		expect(report.signer?.subject).toBe(signer.subject);
		expect(report.signer?.issuer).toBe(signer.issuer);
		expect(report.signer?.serial).toBe(signer.serial);
		expect(new Date(report.signer?.notBefore ?? 0).toISOString()).toBe(signer.notBefore.toISOString());
		expect(new Date(report.signer?.notAfter ?? 0).toISOString()).toBe(signer.notAfter.toISOString());
		expect(report.signingTime).not.toBeNull();

		const validity = check(report, 'certValidity')?.detail ?? '';
		expect(validity).toContain(signer.notBefore.toISOString());
		expect(validity).toContain(signer.notAfter.toISOString());

		// `certChain` is reported, never swallowed: signing embeds only the signer certificate (#6),
		// so whether a chain can be built is a fact about the container, not about this loop.
		const chainCheck = check(report, 'certChain');
		console.log(
			`${keystore.name}: ${validity} — chain: ${chainCheck?.status} (${chainCheck?.detail})` +
				`, report.ok=${report.ok}`
		);

		const directory = mkdtempSync(join(tmpdir(), 'autofirme-smoke-'));
		try {
			const documentPath = join(directory, 'documento.txt');
			const signaturePath = join(directory, 'firma.csig');
			const signerPath = join(directory, 'firmante.der');
			writeFileSync(documentPath, DOCUMENT);
			writeFileSync(signaturePath, signature);
			writeFileSync(signerPath, signer.der);

			// Oracle 1: openssl re-reads the .csig we wrote, over the document we wrote — signature over
			// the signed attributes plus the messageDigest of the content. `-noverify` skips only the
			// chain check, which the report's own `certChain` covers against the bundled roots (the
			// container's chain is not embedded — #6); openssl's `-cades` mode requires that very
			// chain validation, so it cannot be combined with `-noverify`. openssl reports on stderr.
			const cms = spawnSync(
				'openssl',
				[
					'cms', '-verify', '-inform', 'DER', '-in', signaturePath,
					'-content', documentPath, '-binary', '-noverify'
				],
				{ encoding: 'utf8' }
			);
			console.log(`${keystore.name}: openssl cms -verify → ${(cms.stdout + cms.stderr).trim()}`);
			expect(cms.status, cms.stderr).toBe(0);
			expect(cms.stdout + cms.stderr).toMatch(/Verification successful/i);

			// Oracle 2: openssl re-reads the certificate — identity and validity dates are not ours.
			const described = execFileSync(
				'openssl',
				['x509', '-inform', 'DER', '-in', signerPath, '-noout', '-subject', '-dates', '-dateopt', 'iso_8601'],
				{ stdio: 'pipe' }
			).toString();
			const [, subject = '', notBefore = '', notAfter = ''] =
				/^subject=(.*)\nnotBefore=(.*)\nnotAfter=(.*)$/m.exec(described) ?? [];
			expect(subject).toContain(`CN=${signer.commonName}`);
			// `openssl -dateopt iso_8601` prints `2026-01-01 00:00:00Z`; the report uses ISO instants.
			expect(new Date(notBefore.replace(' ', 'T')).toISOString()).toBe(signer.notBefore.toISOString());
			expect(new Date(notAfter.replace(' ', 'T')).toISOString()).toBe(signer.notAfter.toISOString());
		} finally {
			rmSync(directory, { recursive: true, force: true });
		}
	}, 60_000);
}
