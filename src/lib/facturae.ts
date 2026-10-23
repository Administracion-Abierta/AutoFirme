/**
 * FacturaE presets: Spanish e-invoices signed as enveloped XAdES-EPES with the
 * per-version signature policy and a whitelisted claimed role.
 *
 * Policy values verbatim from `AOFacturaESigner` (clienteafirma, read-only reference):
 * only 3.0 and 3.1 policies were ever published, so 3.2.x signs under the 3.1 policy.
 */

import type { Bytes } from './asn1';
import { signXades, type XadesIds } from './xades';
import type { SignerCert, SigningAlgorithm } from './sign';

export type FacturaeVersion = '3.0' | '3.1' | '3.2' | '3.2.1' | '3.2.2';

export interface FacturaePreset {
	version: FacturaeVersion;
	policyIdentifier: string;
	/** Base64 policy hash, as published. */
	policyHash: string;
	policyHashAlgorithm: SigningAlgorithm['hash'];
}

const POLICY_30 =
	'http://www.facturae.es/politica de firma formato facturae/politica de firma formato facturae v3_0.pdf';
const POLICY_31 =
	'http://www.facturae.es/politica_de_firma_formato_facturae/politica_de_firma_formato_facturae_v3_1.pdf';

/** One preset per invoice version; 3.2.x reuses the 3.1 policy (none was published for 3.2). */
export const FACTURAE_PRESETS: Record<FacturaeVersion, FacturaePreset> = {
	'3.0': { version: '3.0', policyIdentifier: POLICY_30, policyHash: 'xmfh8D/Ec/hHeE1IB4zPd61zHIY=', policyHashAlgorithm: 'SHA-1' },
	'3.1': { version: '3.1', policyIdentifier: POLICY_31, policyHash: 'Ohixl6upD6av8N7pEvDABhEL6hM=', policyHashAlgorithm: 'SHA-1' },
	'3.2': { version: '3.2', policyIdentifier: POLICY_31, policyHash: 'Ohixl6upD6av8N7pEvDABhEL6hM=', policyHashAlgorithm: 'SHA-1' },
	'3.2.1': { version: '3.2.1', policyIdentifier: POLICY_31, policyHash: 'Ohixl6upD6av8N7pEvDABhEL6hM=', policyHashAlgorithm: 'SHA-1' },
	'3.2.2': { version: '3.2.2', policyIdentifier: POLICY_31, policyHash: 'Ohixl6upD6av8N7pEvDABhEL6hM=', policyHashAlgorithm: 'SHA-1' }
};

/** Role whitelist verbatim from the reference (`signerClaimedRoles`, lowercased). */
export const FACTURAE_CLAIMED_ROLES = [
	'emisor',
	'receptor',
	'tercero',
	'supplier',
	'customer',
	'third party'
] as const;

export type FacturaeClaimedRole = (typeof FACTURAE_CLAIMED_ROLES)[number];

function normalizeRole(role: string | undefined): FacturaeClaimedRole {
	const normalized = (role ?? 'emisor').toLowerCase();
	if (!(FACTURAE_CLAIMED_ROLES as readonly string[]).includes(normalized)) {
		throw new Error(`FacturaE: role '${role}' is not valid (emisor, receptor, tercero, …)`);
	}
	return normalized as FacturaeClaimedRole;
}

function policyDigest(base64: string): Bytes {
	const binary = atob(base64);
	const bytes = new Uint8Array(binary.length);
	for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
	return bytes;
}

/**
 * FacturaE-shaped check, per reference `isValidDataFile`: root `Facturae` with
 * `FileHeader`, `Parties` and `Invoices` children. Never throws.
 */
export function isFacturae(invoice: string | Bytes): boolean {
	try {
		const text = typeof invoice === 'string' ? invoice : new TextDecoder().decode(invoice);
		const doc = new DOMParser().parseFromString(text, 'application/xml');
		const root = doc.documentElement;
		if (!root || root.localName !== 'Facturae' || root.getElementsByTagName('parsererror').length > 0) {
			return false;
		}
		const children = new Set(
			[...root.childNodes]
				.filter((node) => node.nodeType === node.ELEMENT_NODE)
				.map((node) => (node as Element).localName)
		);
		return children.has('FileHeader') && children.has('Parties') && children.has('Invoices');
	} catch {
		return false;
	}
}

export interface SignFacturaeOptions {
	/** The unsigned FacturaE invoice, as XML text or UTF-8 bytes. */
	invoice: string | Bytes;
	version: FacturaeVersion;
	certificates: readonly SignerCert[];
	signer?: SignerCert;
	/** Defaults to `emisor`; anything outside the whitelist throws before signing. */
	claimedRole?: string;
	now?: Date;
	ids?: Partial<XadesIds>;
}

/**
 * Sign a FacturaE invoice: forced enveloped XAdES with the version's policy and role.
 * Co-signature is refused by `cosignFacturae`; this function signs a fresh invoice.
 */
export async function signFacturae(options: SignFacturaeOptions): Promise<string> {
	const preset = FACTURAE_PRESETS[options.version];
	if (!preset) throw new Error(`FacturaE: unsupported version '${options.version}'`);
	const role = normalizeRole(options.claimedRole);
	if (!isFacturae(options.invoice)) throw new Error('FacturaE: not a Facturae invoice');
	return signXades({
		xml: options.invoice,
		certificates: options.certificates,
		signer: options.signer,
		claimedRoles: [role],
		policy: {
			identifier: preset.policyIdentifier,
			digest: policyDigest(preset.policyHash),
			digestAlgorithm: preset.policyHashAlgorithm
		},
		now: options.now,
		ids: options.ids
	});
}

/** FacturaE forbids co-signature (reference throws `UnsupportedOperationException`). */
export function cosignFacturae(): never {
	throw new Error('FacturaE: co-signature is not supported for electronic invoices');
}

/** FacturaE forbids counter-signature (reference throws `UnsupportedOperationException`). */
export function countersignFacturae(): never {
	throw new Error('FacturaE: counter-signature is not supported for electronic invoices');
}
