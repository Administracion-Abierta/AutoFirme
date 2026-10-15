/**
 * The operator's certificate store: a directory of `.p12` containers, one PIN, loaded once.
 *
 * The container is encrypted at rest by PKCS#12 itself — that is the whole persistence story. On
 * load the private key goes into a **non-extractable** WebCrypto handle usable only for signing and
 * lives in this process's memory until it exits; nothing is ever written back to disk.
 */

import { readFile, readdir } from 'node:fs/promises';
import { basename, join } from 'node:path';
import { loadSignerSession } from '../../src/lib/keystore.ts';
import type { SignerCert, SignerSession } from '../../src/lib/sign.ts';
import type { ServerConfig } from './config';
import { ToolError } from './input';

/** What an agent is allowed to know about a certificate: everything except the key. */
export interface CertSummary {
	/** `container#position:serial` — stable for a given set of files, and the id `sign` takes. */
	cert_id: string;
	common_name: string;
	subject: string;
	issuer: string;
	serial: string;
	not_before: string;
	not_after: string;
	expired: boolean;
	not_yet_valid: boolean;
	/** True for the one certificate of the container that owns the private key. */
	signable: boolean;
	/** File the certificate came from, for the operator's benefit. */
	container: string;
}

/** `#` separates container from certificate id, so it cannot appear in a file name. */
const SEPARATOR = '#';

export class CertStore {
	private constructor(
		private readonly entries: Map<string, SignerCert>,
		readonly certificates: CertSummary[],
		/** Containers that failed to load, with the reason — surfaced by `list_certs`. */
		readonly errors: string[]
	) {}

	static empty(errors: string[] = []): CertStore {
		return new CertStore(new Map(), [], errors);
	}

	/** Never throws: a bad PIN on one container must not take the whole server down. */
	static async load(config: ServerConfig): Promise<CertStore> {
		if (!config.certDir) {
			return CertStore.empty(['AUTOFIRME_MCP_CERTS is not set — no certificate is loaded']);
		}
		if (config.pin === null) {
			return CertStore.empty(['AUTOFIRME_MCP_PIN is not set — the keystore cannot be opened']);
		}

		let files: string[];
		try {
			files = (await readdir(config.certDir)).filter((name) => name.endsWith('.p12')).sort();
		} catch (error) {
			return CertStore.empty([`cannot read ${config.certDir}: ${(error as Error).message}`]);
		}

		const entries = new Map<string, SignerCert>();
		const certificates: CertSummary[] = [];
		const errors: string[] = [];
		for (const file of files) {
			const container = basename(file, '.p12');
			if (container.includes(SEPARATOR)) {
				errors.push(`${file}: file name may not contain ${JSON.stringify(SEPARATOR)}`);
				continue;
			}
			try {
				const session: SignerSession = await loadSignerSession(
					new Uint8Array(await readFile(join(config.certDir, file))),
					config.pin
				);
				for (const cert of session.certificates) {
					certificates.push({
						cert_id: `${container}${SEPARATOR}${cert.id}`,
						common_name: cert.commonName,
						subject: cert.subject,
						issuer: cert.issuer,
						serial: cert.serial,
						not_before: cert.notBefore.toISOString(),
						not_after: cert.notAfter.toISOString(),
						expired: cert.expired,
						not_yet_valid: cert.notYetValid,
						signable: cert.key !== null,
						container
					});
					if (cert.key) entries.set(`${container}${SEPARATOR}${cert.id}`, cert);
				}
			} catch (error) {
				errors.push(`${file}: ${(error as Error).message}`);
			}
		}
		return new CertStore(entries, certificates, errors);
	}

	get signingReady(): boolean {
		return this.entries.size > 0;
	}

	/** The certificate behind `cert_id`, or a `ToolError` naming what is wrong with the id. */
	signer(certId: string): SignerCert {
		const cert = this.entries.get(certId);
		if (cert) return cert;
		const known = [...this.entries.keys()];
		throw new ToolError(
			'unknown_cert_id',
			known.length
				? `unknown cert_id ${JSON.stringify(certId)}; known: ${known.map((id) => JSON.stringify(id)).join(', ')}`
				: `unknown cert_id ${JSON.stringify(certId)}; no signable certificate is loaded`
		);
	}
}
