/** Digest algorithm identifiers (CAdES uses OIDs, XMLDSig uses URIs) and the WebCrypto digest call. */

import type { Bytes } from './types';

export type HashName = 'SHA-1' | 'SHA-256' | 'SHA-384' | 'SHA-512';

export interface DigestAlgorithm {
	hash: HashName;
	label: string;
}

export const DIGEST_OIDS: Record<string, DigestAlgorithm> = {
	'1.3.14.3.2.26': { hash: 'SHA-1', label: 'SHA-1' },
	'2.16.840.1.101.3.4.2.1': { hash: 'SHA-256', label: 'SHA-256' },
	'2.16.840.1.101.3.4.2.2': { hash: 'SHA-384', label: 'SHA-384' },
	'2.16.840.1.101.3.4.2.3': { hash: 'SHA-512', label: 'SHA-512' }
};

export const DIGEST_URIS: Record<string, DigestAlgorithm> = {
	'http://www.w3.org/2000/09/xmldsig#sha1': { hash: 'SHA-1', label: 'SHA-1' },
	'http://www.w3.org/2001/04/xmlenc#sha256': { hash: 'SHA-256', label: 'SHA-256' },
	'http://www.w3.org/2001/04/xmldsig-more#sha384': { hash: 'SHA-384', label: 'SHA-384' },
	'http://www.w3.org/2001/04/xmlenc#sha512': { hash: 'SHA-512', label: 'SHA-512' }
};

export async function digestBytes(hash: HashName, bytes: Bytes): Promise<Bytes> {
	return new Uint8Array(await crypto.subtle.digest(hash, bytes));
}
