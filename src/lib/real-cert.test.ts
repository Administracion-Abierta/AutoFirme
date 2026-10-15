/**
 * Real-container smoke test (#40).
 *
 * `work/certs/old-cert.p12` is a legacy FNMT keystore whose certificate expired 2026-03-29. The
 * whole point of AutoFirma's behaviour — and of this issue — is that it still signs, with the
 * expiry said out loud: this file loads the real container, signs with it, checks the signature
 * with openssl as an independent oracle, and asserts our own verifier reports `valid-but-expired`.
 *
 * The container and `.p12pass` are gitignored and absent from a fresh clone, so the suite skips
 * itself when either is missing. `scripts/smoke-real.sh` is the supported way to run it.
 */

import { spawnSync } from 'node:child_process';
import { existsSync, mkdtempSync, readFileSync, rmSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join, resolve } from 'node:path';
import { afterAll, describe, expect, it } from 'vitest';
import { signCadesDetached } from './cms';
import { loadSignerSession } from './keystore';
import { verifyArtifact } from './verify';

const root = process.cwd();
const certPath = resolve(root, process.env.AUTOFIRME_CERT ?? 'work/certs/old-cert.p12');
const pinPath = resolve(root, process.env.AUTOFIRME_PASS ?? '.p12pass');
/** A missing container or PIN is the normal case in a fresh clone — skip, do not fail. */
const available = existsSync(certPath) && existsSync(pinPath);
const pin = available ? readFileSync(pinPath, 'utf8').trim() : '';

describe.skipIf(!available)('real legacy container: expired certificate still signs (#40)', () => {
	const dir = mkdtempSync(join(tmpdir(), 'autofirme-real-'));
	const write = (name: string, bytes: Uint8Array): string => {
		const path = join(dir, name);
		writeFileSync(path, bytes);
		return path;
	};

	afterAll(() => rmSync(dir, { recursive: true, force: true }));

	it('signs with the expired FNMT certificate, and both openssl and our verifier accept it', async () => {
		const session = await loadSignerSession(new Uint8Array(readFileSync(certPath)), pin);
		const { signer } = session;

		expect(signer.expired).toBe(true);
		expect(signer.notYetValid).toBe(false);
		expect(signer.key).not.toBeNull();
		// The warning the UI shows, on stdout: the smoke script's "warning surfaced" evidence.
		console.log(
			`WARNING: ${signer.commonName} expired ${signer.notAfter.toISOString()} — signing anyway`
		);

		const document = new TextEncoder().encode('AutoFirma Web — real-container smoke (#40)\n');
		const signature = await signCadesDetached(signer, document);

		// Independent oracle. `-noverify` skips the chain: the container carries it, the roots are
		// FNMT's, and trusting them is our verifier's job in the assertions below. openssl reports on
		// stderr and exits non-zero when the signature does not hold.
		const signaturePath = write('signature.p7', signature);
		const documentPath = write('document.txt', document);
		const extractedPath = join(dir, 'extracted.bin');
		const oracle = spawnSync(
			'openssl',
			[
				'cms', '-verify', '-binary', '-inform', 'DER', '-in', signaturePath, '-content', documentPath,
				'-noverify', '-out', extractedPath
			],
			{ encoding: 'utf8' }
		);
		console.log(`openssl: ${(oracle.stderr ?? '').trim()}`);
		expect(oracle.status).toBe(0);
		expect(readFileSync(extractedPath, 'utf8')).toBe(new TextDecoder().decode(document));

		// The CAdES builder embeds the signer certificate alone (detached chain, #6), so the issuer
		// has to come from the container itself: its own certificates are handed over as the trust
		// anchors. Whether FNMT's issuance is trustworthy is not what this issue is about — the
		// expiry of the signer is.
		const anchors = session.certificates.map(
			(certificate) =>
				`-----BEGIN CERTIFICATE-----\n${Buffer.from(certificate.der)
					.toString('base64')
					.match(/.{1,64}/g)!
					.join('\n')}\n-----END CERTIFICATE-----\n`
		);
		const report = await verifyArtifact(signature, { detached: document, trustedRoots: anchors });
		expect(report.status).toBe('valid-but-expired');
		expect(report.ok).toBe(true);
		expect(report.checks.find((entry) => entry.id === 'signature')?.status).toBe('pass');
		expect(report.checks.find((entry) => entry.id === 'contentDigest')?.status).toBe('pass');
		expect(report.checks.find((entry) => entry.id === 'certChain')?.status).toBe('pass');
		expect(report.checks.find((entry) => entry.id === 'certValidity')?.status).toBe('warn');
		expect(new Date(report.signer!.notAfter).getTime()).toBe(signer.notAfter.getTime());
		expect(signer.notAfter.getTime()).toBeLessThan(Date.now());
	}, 60_000);
});
