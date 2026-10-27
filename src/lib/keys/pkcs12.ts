import type { SignerSession } from '../sign';
import { softwareSignDigest, type KeyProvider } from './provider';

/**
 * PKCS#12 provider: the container backend of the seam (Refs #97).
 *
 * Pure adapter over an already-loaded `SignerSession` — no parsing and no key
 * import here. Signing goes through the shared software backend, whose body is
 * the one `signBytes` always used, so output bytes are unchanged.
 */
export function createPkcs12Provider(session: SignerSession): KeyProvider {
	return {
		metadata: { kind: 'pkcs12', id: 'pkcs12', label: 'PKCS#12 file' },
		async list() {
			return session.certificates;
		},
		async getCert(id: string) {
			return session.certificates.find((cert) => cert.id === id) ?? null;
		},
		signDigest: softwareSignDigest
	};
}
