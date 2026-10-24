/**
 * Signature policy and commitment-type checks (P4.3, Refs #137).
 *
 * Standalone and unwired: the verifiers (`cms.ts`, `xades.ts`, `pades.ts`) do NOT
 * call this module yet — wiring needs a new `CheckId` in `types.ts`, which is
 * outside this issue's owns list. A later issue maps `PolicyFinding` onto checks.
 *
 * Rules, per family:
 * - Policy: identifier + hash checked against {@link BUNDLED_POLICIES}. Tampered
 *   hash → `fail`. Unknown identifier *with* a hash → `warn` recording the
 *   identifier (never fail: the cryptography may still hold). Implied/absent →
 *   `info`. Explicit policy with no hash → `warn` (nothing to check it against).
 *   A matching policy declared outside the signed attributes/properties → `warn`
 *   (recorded, not covered by the signature).
 * - Commitment: must live where the signature covers it (CAdES signedAttrs,
 *   XAdES `SignedSignatureProperties`); an unsigned commitment → `fail`.
 *   Unknown OID/URI → `warn` with the value. Absent → `info`.
 * - FacturaE profile: the policy URI must be the FacturaE one and the claimed
 *   role must be one of {@link FACTURAE_ROLES}; anything else → `warn`.
 */

import { children, fromBase64, hex, octets, oid, type Der } from './asn1';
import { DIGEST_OIDS, DIGEST_URIS } from './digest';
import type { Bytes } from './types';
export type Family = 'cades' | 'xades' | 'pades';
export type PolicyFindingStatus = 'pass' | 'warn' | 'fail' | 'info';

export interface PolicyFinding {
	area: 'policy' | 'commitment';
	status: PolicyFindingStatus;
	/** Technical facts — not translated: OIDs, URIs, digests. */
	detail: string;
	/** The unknown identifier, when the finding is about one. */
	identifier?: string;
}

/** One bundled, checkable signature policy. */
export interface BundledPolicy {
	/** Human label, e.g. `FacturaE 3.1`. */
	label: string;
	/** Policy URI as it appears in `SigPolicyId.Identifier`. */
	uri: string;
	hashAlgorithm: string;
	/** Base64 of the policy document digest, as published with the policy. */
	digestBase64: string;
}

export const FACTURAE_31_URI =
	'http://www.facturae.es/politica_de_firma_formato_facturae/politica_de_firma_formato_facturae_v3_1.pdf';

/**
 * Policies checkable offline. Only FacturaE 3.1 is bundled: URI and SHA-1 digest
 * are the pair AutoFirma itself ships as its `POLICY_FACTURAE_31` constant
 * (`reference/decompile-1.9:decompiled/sources/es/gob/afirma/signers/xades/AOFacturaESigner.java:26`).
 * There is no generally-published eIDAS policy to bundle (qualified-status
 * determination is out of scope), so everything else takes the unknown-policy path.
 */
export const BUNDLED_POLICIES: BundledPolicy[] = [
	{
		label: 'FacturaE 3.1',
		uri: FACTURAE_31_URI,
		hashAlgorithm: 'SHA-1',
		digestBase64: 'Ohixl6upD6av8N7pEvDABhEL6hM='
	}
];
/** XAdES `CommitmentTypeId` fragment names (any `01903` version URI qualifies). */
const XADES_COMMITMENTS: Record<string, true> = {
	ProofOfOrigin: true,
	ProofOfReceipt: true,
	ProofOfDelivery: true,
	ProofOfSender: true,
	ProofOfApproval: true,
	ProofOfCreation: true
};
export const OID_SIG_POLICY = '1.2.840.113549.1.9.16.2.15';
/** `id-aa-ets-commitmentType` — CAdES commitment-type-indication (RFC 5126 §5.11.3). */
export const OID_COMMITMENT = '1.2.840.113549.1.9.16.2.16';

/** `id-ct` commitment identifiers (RFC 5126 §5.11.3, S/MIME `id-ct-*`). */
export const CADES_COMMITMENTS: Record<string, string> = {
	'1.2.840.113549.1.9.16.6.1': 'proofOfOrigin',
	'1.2.840.113549.1.9.16.6.2': 'proofOfReceipt',
	'1.2.840.113549.1.9.16.6.3': 'proofOfDelivery',
	'1.2.840.113549.1.9.16.6.4': 'proofOfSender',
	'1.2.840.113549.1.9.16.6.5': 'proofOfApproval',
	'1.2.840.113549.1.9.16.6.6': 'proofOfCreation'
};
const XADES_NS = 'http://uri.etsi.org/01903';

/** Attribute shape this module needs; structurally compatible with `cms.ts`. */
export interface AttrLike {
	type: string;
	values: Der[];
}

export interface PolicyClaim {
	identifier: string | null;
	/** Digest algorithm as declared (OID or URI); null when not declared. */
	digestAlgorithm: string | null;
	digestValue: Bytes | null;
	/** `SignaturePolicyImplied` / no policy attribute at all. */
	implied: boolean;
	/** Inside signedAttrs (CAdES) or SignedSignatureProperties (XAdES). */
	signed: boolean;
	family: Family;
	/** The attribute/element was present but unreadable. */
	unreadable?: boolean;
}

export interface CommitmentClaim {
	/** OID (CAdES), URI (XAdES) or bare name; null when no commitment is claimed. */
	kind: string | null;
	signed: boolean;
	family: Family;
}

function sameBytes(a: Bytes, b: Bytes): boolean {
	if (a.length !== b.length) return false;
	for (let i = 0; i < a.length; i++) if (a[i] !== b[i]) return false;
	return true;
}

export function assessPolicyClaim(claim: PolicyClaim): PolicyFinding {
	if (claim.implied || claim.identifier === null) {
		return {
			area: 'policy',
			status: 'info',
			detail: claim.unreadable
				? `(${claim.family}) signature-policy attribute present but unreadable — policy not checked`
				: `(${claim.family}) SignaturePolicyImplied — no policy to check`
		};
	}
	const identifier = claim.identifier.trim();
	const bundled = BUNDLED_POLICIES.filter((policy) => policy.uri === identifier);
	if (bundled.length === 0) {
		return {
			area: 'policy',
			status: 'warn',
			detail: `(${claim.family}) unknown signature policy ${identifier}${
				claim.digestValue ? ` with digest ${hex(claim.digestValue).slice(0, 16)}… — recorded, not validated` : ' without a digest — cannot be validated'
			}`,
			identifier
		};
	}
	if (!claim.digestValue) {
		return {
			area: 'policy',
			status: 'warn',
			detail: `(${claim.family}) recognised policy ${bundled[0].label} but no policy hash to check it against`,
			identifier
		};
	}
	for (const policy of bundled) {
		let expected: Bytes;
		try {
			expected = fromBase64(policy.digestBase64);
		} catch {
			continue;
		}
		if (!sameBytes(claim.digestValue, expected)) continue;
		const declared = claim.digestAlgorithm
			? (DIGEST_OIDS[claim.digestAlgorithm]?.hash ?? DIGEST_URIS[claim.digestAlgorithm]?.hash ?? claim.digestAlgorithm)
			: null;
		const published =
			DIGEST_OIDS[policy.hashAlgorithm]?.hash ?? DIGEST_URIS[policy.hashAlgorithm]?.hash ?? policy.hashAlgorithm;
		if (declared !== null && declared !== published) {
			return {
				area: 'policy',
				status: 'warn',
				detail: `(${claim.family}) policy ${policy.label} digest matches but the declared hash algorithm ${claim.digestAlgorithm} is not the published ${policy.hashAlgorithm} — the claim is inconsistent`,
				identifier
			};
		}
	if (claim.signed) {
		return { area: 'policy', status: 'pass', detail: `(${claim.family}) policy ${policy.label} — identifier and hash match`, identifier };
	}
	return {
		area: 'policy',
		status: 'warn',
		detail: `(${claim.family}) policy ${policy.label} hash matches but is declared outside the signed attributes/properties — the signature does not cover it`,
		identifier
	};
	}
	return {
		area: 'policy',
		status: 'fail',
		detail: `(${claim.family}) policy hash ${hex(claim.digestValue).slice(0, 16)}… matches no published digest for ${bundled[0].label} (${identifier}) — the policy reference was tampered with or the wrong document was hashed`,
		identifier
	};
}

function commitmentName(kind: string, family: Family): string | null {
	if (family === 'xades') {
		const fragment = kind.split('#')[1] ?? kind;
		return Object.hasOwn(XADES_COMMITMENTS, fragment) ? fragment : null;
	}
	return Object.hasOwn(CADES_COMMITMENTS, kind) ? CADES_COMMITMENTS[kind] : null;
}

export function assessCommitmentClaim(claim: CommitmentClaim): PolicyFinding {
	if (claim.kind === null) {
		return { area: 'commitment', status: 'info', detail: `(${claim.family}) no commitment-type claimed` };
	}
	if (!claim.signed) {
		return {
			area: 'commitment',
			status: 'fail',
			detail: `(${claim.family}) commitment ${claim.kind} is declared outside the signed attributes/properties — the signature does not cover it`,
			identifier: claim.kind
		};
	}
	const name = commitmentName(claim.kind.trim(), claim.family);
	if (name) {
		return { area: 'commitment', status: 'pass', detail: `(${claim.family}) commitment ${name} (${claim.kind})` };
	}
	return {
		area: 'commitment',
		status: 'warn',
		detail: `(${claim.family}) unknown commitment-type ${claim.kind} — recorded, not validated`,
		identifier: claim.kind
	};
}

/** FacturaE profile: the policy must be the FacturaE one. */
export function assessFacturaePolicy(claim: PolicyClaim): PolicyFinding {
	const finding = assessPolicyClaim(claim);
	if (finding.status === 'pass') return finding;
	if (finding.status === 'info') {
		return { area: 'policy', status: 'warn', detail: `(${claim.family}) FacturaE requires an explicit FacturaE policy — none is referenced` };
	}
	if (finding.identifier !== FACTURAE_31_URI) {
		return {
			area: 'policy',
			status: 'warn',
			detail: `(${claim.family}) FacturaE expects the FacturaE 3.1 policy, found ${finding.identifier ?? 'an unreadable reference'}`,
			identifier: finding.identifier
		};
	}
	return finding;
}

/**
 * FacturaE profile: the signer claims one of the two roles per capacity. The set
 * is AutoFirma's own whitelist — `emisor|supplier` (Emisor), `receptor|customer`
 * (Receptor), `tercero|third party` (Tercero)
 * (`reference/decompile-1.9:decompiled/sources/es/gob/afirma/signers/xades/AOFacturaESigner.java:81-82`,
 * mapped at `standalone/signdetails/FacturaESignAnalyzer.java:59-65`).
 */
export const FACTURAE_ROLES: Record<string, string> = {
	emisor: 'Emisor',
	supplier: 'Emisor',
	receptor: 'Receptor',
	customer: 'Receptor',
	tercero: 'Tercero',
	'third party': 'Tercero'
};

export function assessFacturaeRoles(roles: string[], signed: boolean, family: Family): PolicyFinding {
	if (roles.length === 0) {
		return { area: 'commitment', status: 'info', detail: `(${family}) no claimed signer role` };
	}
	if (!signed) {
		return { area: 'commitment', status: 'fail', detail: `(${family}) claimed role(s) ${roles.join(', ')} are declared outside the signed attributes/properties` };
	}
	if (roles.length === 1) {
		const role = roles[0].trim().toLowerCase();
		const capacity = Object.hasOwn(FACTURAE_ROLES, role) ? FACTURAE_ROLES[role] : undefined;
		if (capacity) {
			return { area: 'commitment', status: 'pass', detail: `(${family}) FacturaE claimed role ${role} (${capacity})` };
		}
	}
	return {
		area: 'commitment',
		status: 'warn',
		detail: `(${family}) FacturaE expects one of the roles ${Object.keys(FACTURAE_ROLES).join(', ')}, found ${roles.join(', ')}`
	};
}

/** Text of an IA5String/UTF8String/PrintableString content. */
function derText(node: Der): string {
	return new TextDecoder().decode(node.content);
}

/** `SigPolicyId` CHOICE: OID or URI string. */
function sigPolicyIdentifier(node: Der): string | null {
	try {
		if (node.tag === 0x06) return oid(node);
		if (node.tag === 0x16 || node.tag === 0x0c || node.tag === 0x13) return derText(node);
		return null;
	} catch {
		return null;
	}
}

/** One `attrValues[0]` of `id-aa-ets-sigPolicyId` → claim (null when unreadable). */
function decodeCadesPolicy(value: Der, signed: boolean, family: Family): PolicyClaim {
	const unreadable: PolicyClaim = { identifier: null, digestAlgorithm: null, digestValue: null, implied: false, signed, family, unreadable: true };
	try {
		const fields = children(value);
		const identifier = sigPolicyIdentifier(fields[0]);
		if (!identifier) return unreadable;
		const hashFields = children(fields[1]);
		const digestAlgorithm = oid(children(hashFields[0])[0]);
		const digestValue = octets(hashFields[1]);
		return { identifier, digestAlgorithm, digestValue, implied: false, signed, family };
	} catch {
		return unreadable;
	}
}

/**
 * One `attrValues[0]` of commitment-type-indication → its claim. RFC 5126 §5.11.3
 * puts one `CommitmentTypeIndication` in each attribute value (`commitmentTypeId`
 * first, optional qualifiers after), so the value *is* the indication — AutoFirma
 * emits it that way (`reference/decompile-1.9`:
 * `decompiled/sources/es/gob/afirma/signers/cades/CAdESUtils.java:153`).
 */
function decodeCadesCommitments(value: Der, signed: boolean, family: Family): CommitmentClaim[] {
	try {
		return [{ kind: oid(children(value)[0]), signed, family }];
	} catch {
		return [{ kind: null, signed, family }];
	}
}

/**
 * CAdES claims from already-split attribute lists. PAdES reuses this over the
 * `Contents` CMS — a PAdES signature *is* a CAdES over the ByteRange bytes, so
 * the same attribute OIDs apply; only the `family` label differs.
 */
export function extractCadesClaims(
	signedAttrs: AttrLike[],
	unsignedAttrs: AttrLike[],
	family: 'cades' | 'pades'
): { policies: PolicyClaim[]; commitments: CommitmentClaim[] } {
	const policies: PolicyClaim[] = [];
	const commitments: CommitmentClaim[] = [];
	for (const [attrs, signed] of [
		[signedAttrs, true],
		[unsignedAttrs, false]
	] as const) {
		for (const attr of attrs) {
			if (attr.type === OID_SIG_POLICY && attr.values[0]) {
				policies.push(decodeCadesPolicy(attr.values[0], signed, family));
			} else if (attr.type === OID_COMMITMENT && attr.values[0]) {
				commitments.push(...decodeCadesCommitments(attr.values[0], signed, family));
			}
		}
	}
	return { policies, commitments };
}

function xadesChildren(element: Element, localName: string): Element[] {
	return Array.from(element.children).filter(
		(child) => child.localName === localName && (child.namespaceURI ?? '').startsWith(XADES_NS)
	);
}

function xadesFirst(element: Element, localName: string): Element | null {
	return xadesChildren(element, localName)[0] ?? null;
}

function xadesDescendants(root: Element, localName: string): Element[] {
	return Array.from(root.getElementsByTagName('*')).filter(
		(element) => element.localName === localName && (element.namespaceURI ?? '').startsWith(XADES_NS)
	);
}

/**
 * Covered by the signature when the ancestor chain reaches `xades:SignedProperties`.
 * Both of its children count: `SignedSignatureProperties` (policy, signer role) and
 * `SignedDataObjectProperties`, where a real `CommitmentTypeIndication` lives.
 * Anything under `UnsignedProperties` (or outside `QualifyingProperties`) does not.
 */
function isSignedXades(element: Element): boolean {
	let node: Element | null = element;
	while (node) {
		if (node.localName === 'SignedProperties' && (node.namespaceURI ?? '').startsWith(XADES_NS)) return true;
		node = node.parentElement;
	}
	return false;
}

function textOf(element: Element | null | undefined): string | null {
	const text = element?.textContent?.trim();
	return text ? text : null;
}

/**
 * XAdES claims from a parsed `ds:Signature` document. Location decides `signed`:
 * only claims inside `xades:SignedProperties` are covered by the signature.
 */
export function extractXadesClaims(document: Document, family: Family = 'xades'): {
	policies: PolicyClaim[];
	commitments: CommitmentClaim[];
	roles: { values: string[]; signed: boolean };
} {
	const policies: PolicyClaim[] = [];
	const commitments: CommitmentClaim[] = [];
	let roles: { values: string[]; signed: boolean } = { values: [], signed: false };

	for (const identifier of xadesDescendants(document.documentElement, 'SignaturePolicyIdentifier')) {
		const signed = isSignedXades(identifier);
		if (xadesFirst(identifier, 'SignaturePolicyImplied')) {
			policies.push({ identifier: null, digestAlgorithm: null, digestValue: null, implied: true, signed, family });
			continue;
		}
		const policyId = xadesFirst(identifier, 'SignaturePolicyId');
		const sigPolicyId = policyId ? xadesFirst(policyId, 'SigPolicyId') : null;
		const id = textOf(sigPolicyId ? xadesFirst(sigPolicyId, 'Identifier') : null);
		const hash = policyId ? xadesFirst(policyId, 'SigPolicyHash') : null;
		const digestAlgorithm = hash?.getElementsByTagNameNS('*', 'DigestMethod')[0]?.getAttribute('Algorithm') ?? null;
		let digestValue: Bytes | null = null;
		const digestText = textOf(hash?.getElementsByTagNameNS('*', 'DigestValue')[0]);
		if (digestText) {
			try {
				digestValue = fromBase64(digestText);
			} catch {
				digestValue = null;
			}
		}
		policies.push({ identifier: id, digestAlgorithm, digestValue, implied: false, signed, family });
	}

	for (const indication of xadesDescendants(document.documentElement, 'CommitmentTypeIndication')) {
		const signed = isSignedXades(indication);
		const id = textOf(xadesFirst(xadesFirst(indication, 'CommitmentTypeId') ?? indication, 'Identifier'));
		commitments.push({ kind: id, signed, family });
	}

	const claimedRoles = xadesDescendants(document.documentElement, 'ClaimedRoles')[0];
	if (claimedRoles) {
		roles = {
			values: xadesChildren(claimedRoles, 'ClaimedRole').flatMap((role) => {
				const text = textOf(role);
				return text ? [text] : [];
			}),
			signed: isSignedXades(claimedRoles)
		};
	}
	return { policies, commitments, roles };
}
