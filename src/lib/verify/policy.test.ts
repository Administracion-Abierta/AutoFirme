/**
 * P4.3 policy + commitment checks (Refs #137): assessors per acceptance plus the
 * per-family extractors. Pure unit tests — no fixtures, no network, no signing.
 */

import { describe, expect, it } from 'vitest';
import { parse } from './asn1';
import {
	assessCommitmentClaim,
	assessFacturaePolicy,
	assessFacturaeRoles,
	assessPolicyClaim,
	BUNDLED_POLICIES,
	CADES_COMMITMENTS,
	extractCadesClaims,
	extractXadesClaims,
	FACTURAE_31_URI,
	FACTURAE_ROLES,
	OID_COMMITMENT,
	OID_SIG_POLICY
} from './policy';
import type { Bytes } from './types';

const facturae = BUNDLED_POLICIES[0];

function unb64(text: string): Bytes {
	const binary = atob(text);
	const out = new Uint8Array(binary.length);
	for (let i = 0; i < binary.length; i++) out[i] = binary.charCodeAt(i);
	return out;
}

/** Minimal DER builder — just enough for SigPolicyQualifierInfo shapes. */
function tlv(tag: number, content: Bytes): Bytes {
	const out: number[] = [tag];
	if (content.length < 128) out.push(content.length);
	else {
		const lenBytes: number[] = [];
		let n = content.length;
		while (n > 0) {
			lenBytes.unshift(n & 0xff);
			n >>= 8;
		}
		out.push(0x80 | lenBytes.length, ...lenBytes);
	}
	return new Uint8Array([...out, ...content]);
}

function concat(parts: Bytes[]): Bytes {
	const out = new Uint8Array(parts.reduce((n, p) => n + p.length, 0));
	let at = 0;
	for (const part of parts) {
		out.set(part, at);
		at += part.length;
	}
	return out;
}

function oidBytes(dotted: string): Bytes {
	const arcs = dotted.split('.').map(Number);
	const body: number[] = [40 * arcs[0] + arcs[1]];
	for (const arc of arcs.slice(2)) {
		const stack: number[] = [arc & 0x7f];
		let rest = arc >> 7;
		while (rest > 0) {
			stack.unshift((rest & 0x7f) | 0x80);
			rest >>= 7;
		}
		body.push(...stack);
	}
	return tlv(0x06, new Uint8Array(body));
}

function policyDer(uri: string, digest: Bytes): Bytes {
	return tlv(
		0x30,
		concat([
			tlv(0x16, new TextEncoder().encode(uri)),
			tlv(0x30, concat([tlv(0x30, oidBytes('1.3.14.3.2.26')), tlv(0x04, digest)]))
		])
	);
}

/** `CommitmentTypeIndication ::= SEQUENCE { commitmentTypeId OID, qualifiers OPTIONAL }`. */
function commitmentDer(dotted: string, qualifier?: Bytes): Bytes {
	return tlv(0x30, concat(qualifier ? [oidBytes(dotted), tlv(0x30, qualifier)] : [oidBytes(dotted)]));
}

function parsePolicyXml(inner: string, signedProperties = 'SignedSignatureProperties'): Document {
	return new DOMParser().parseFromString(
		`<ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#" xmlns:xades="http://uri.etsi.org/01903/v1.3.2#"><ds:Object><xades:QualifyingProperties><xades:SignedProperties><xades:${signedProperties}>${inner}</xades:${signedProperties}></xades:SignedProperties></xades:QualifyingProperties></ds:Object></ds:Signature>`,
		'application/xml'
	);
}

describe('assessPolicyClaim', () => {
	it('passes a bundled FacturaE policy whose hash matches', () => {
		const finding = assessPolicyClaim({
			identifier: facturae.uri,
			digestAlgorithm: '1.3.14.3.2.26',
			digestValue: unb64(facturae.digestBase64),
			implied: false,
			signed: true,
			family: 'cades'
		});
		expect(finding.area).toBe('policy');
		expect(finding.status).toBe('pass');
	});

	it('checks the FacturaE 3.1 digest AutoFirma ships, and rejects a near-miss', () => {
		const claim = {
			identifier: FACTURAE_31_URI,
			digestAlgorithm: '1.3.14.3.2.26',
			implied: false,
			signed: true,
			family: 'cades' as const
		};
		const published = unb64('Ohixl6upD6av8N7pEvDABhEL6hM=');
		expect(assessPolicyClaim({ ...claim, digestValue: published }).status).toBe('pass');
		const nearMiss = new Uint8Array(published);
		nearMiss[nearMiss.length - 1] ^= 0xff;
		expect(assessPolicyClaim({ ...claim, digestValue: nearMiss }).status).toBe('fail');
	});

	it('warns when a matching policy sits outside the signed attributes', () => {
		const finding = assessPolicyClaim({
			identifier: FACTURAE_31_URI,
			digestAlgorithm: '1.3.14.3.2.26',
			digestValue: unb64('Ohixl6upD6av8N7pEvDABhEL6hM='),
			implied: false,
			signed: false,
			family: 'cades'
		});
		expect(finding.status).toBe('warn');
		expect(finding.identifier).toBe(FACTURAE_31_URI);
	});

	it('warns when the digest matches but the declared algorithm is not the published one', () => {
		const published = unb64('Ohixl6upD6av8N7pEvDABhEL6hM=');
		const finding = assessPolicyClaim({
			identifier: FACTURAE_31_URI,
			digestAlgorithm: '2.16.840.1.101.3.4.2.1',
			digestValue: published,
			implied: false,
			signed: true,
			family: 'cades'
		});
		expect(finding.status).toBe('warn');
		expect(finding.identifier).toBe(FACTURAE_31_URI);
		// XAdES declares the algorithm as a URI; the same digest stays a pass.
		expect(
			assessPolicyClaim({
				identifier: FACTURAE_31_URI,
				digestAlgorithm: 'http://www.w3.org/2000/09/xmldsig#sha1',
				digestValue: published,
				implied: false,
				signed: true,
				family: 'xades'
			}).status
		).toBe('pass');
	});

	it('fails a bundled policy whose hash was tampered with', () => {
		const finding = assessPolicyClaim({
			identifier: facturae.uri,
			digestAlgorithm: '1.3.14.3.2.26',
			digestValue: new Uint8Array([9, 9, 9]),
			implied: false,
			signed: true,
			family: 'xades'
		});
		expect(finding.status).toBe('fail');
		expect(finding.identifier).toBe(facturae.uri);
	});

	it('warns (never fails) on an unknown-but-hashed policy, recording the OID', () => {
		const finding = assessPolicyClaim({
			identifier: '1.2.3.4.5.6',
			digestAlgorithm: '2.16.840.1.101.3.4.2.1',
			digestValue: new Uint8Array([1, 2, 3, 4]),
			implied: false,
			signed: true,
			family: 'pades'
		});
		expect(finding.status).toBe('warn');
		expect(finding.identifier).toBe('1.2.3.4.5.6');
	});

	it('warns on an unknown policy without a digest and on implied policies (info)', () => {
		const noDigest = assessPolicyClaim({
			identifier: '1.2.3',
			digestAlgorithm: null,
			digestValue: null,
			implied: false,
			signed: true,
			family: 'cades'
		});
		expect(noDigest.status).toBe('warn');
		const implied = assessPolicyClaim({
			identifier: null,
			digestAlgorithm: null,
			digestValue: null,
			implied: true,
			signed: true,
			family: 'xades'
		});
		expect(implied.status).toBe('info');
	});

	it('warns on a recognised policy with no hash, and reports unreadable as info', () => {
		const noHash = assessPolicyClaim({
			identifier: facturae.uri,
			digestAlgorithm: null,
			digestValue: null,
			implied: false,
			signed: true,
			family: 'cades'
		});
		expect(noHash.status).toBe('warn');
		const unreadable = assessPolicyClaim({
			identifier: null,
			digestAlgorithm: null,
			digestValue: null,
			implied: false,
			signed: true,
			family: 'cades',
			unreadable: true
		});
		expect(unreadable.status).toBe('info');
	});
});

describe('assessCommitmentClaim', () => {
	it('passes known commitments per family', () => {
		expect(
			assessCommitmentClaim({ kind: '1.2.840.113549.1.9.16.6.1', signed: true, family: 'cades' }).status
		).toBe('pass');
		expect(
			assessCommitmentClaim({
				kind: 'http://uri.etsi.org/01903/v1.2.2#ProofOfApproval',
				signed: true,
				family: 'xades'
			}).status
		).toBe('pass');
		expect(
			assessCommitmentClaim({ kind: '1.2.840.113549.1.9.16.6.5', signed: true, family: 'pades' }).status
		).toBe('pass');
	});

	it('fails a commitment declared outside the signed properties', () => {
		for (const family of ['cades', 'xades', 'pades'] as const) {
			const kind =
				family === 'xades' ? 'http://uri.etsi.org/01903/v1.3.2#ProofOfOrigin' : '1.2.840.113549.1.9.16.6.1';
			const finding = assessCommitmentClaim({ kind, signed: false, family });
			expect(finding.status).toBe('fail');
			expect(finding.identifier).toBe(kind);
		}
	});

	it('warns on unknown commitment kinds, reports absent as info', () => {
		const unknown = assessCommitmentClaim({ kind: '9.9.9.9', signed: true, family: 'cades' });
		expect(unknown.status).toBe('warn');
		expect(unknown.identifier).toBe('9.9.9.9');
		expect(assessCommitmentClaim({ kind: null, signed: true, family: 'xades' }).status).toBe('info');
	});

	it('never accepts an inherited Object key as a commitment kind', () => {
		for (const kind of ['constructor', '__proto__', 'toString', 'hasOwnProperty']) {
			for (const family of ['cades', 'xades', 'pades'] as const) {
				const declared = family === 'xades' ? `http://uri.etsi.org/01903/v1.3.2#${kind}` : kind;
				const finding = assessCommitmentClaim({ kind: declared, signed: true, family });
				expect(finding.status).toBe('warn');
				expect(finding.identifier).toBe(declared);
			}
		}
	});
});

describe('FacturaE profile', () => {
	const good = {
		identifier: FACTURAE_31_URI,
		digestAlgorithm: '1.3.14.3.2.26',
		digestValue: unb64(facturae.digestBase64),
		implied: false,
		signed: true,
		family: 'xades' as const
	};

	it('passes the FacturaE policy, warns on anything else', () => {
		expect(assessFacturaePolicy(good).status).toBe('pass');
		expect(assessFacturaePolicy({ ...good, implied: true, identifier: null }).status).toBe('warn');
		const other = assessFacturaePolicy({ ...good, identifier: '1.2.3', digestValue: new Uint8Array([1]) });
		expect(other.status).toBe('warn');
		expect(other.identifier).toBe('1.2.3');
		const tampered = assessFacturaePolicy({ ...good, digestValue: new Uint8Array([0]) });
		expect(tampered.status).toBe('fail');
	});

	it('passes only the lone emisor role, warns otherwise, fails unsigned roles', () => {
		expect(assessFacturaeRoles(['emisor'], true, 'xades').status).toBe('pass');
		expect(assessFacturaeRoles([], true, 'xades').status).toBe('info');
		expect(assessFacturaeRoles(['revisor'], true, 'xades').status).toBe('warn');
		expect(assessFacturaeRoles(['emisor', 'revisor'], true, 'xades').status).toBe('warn');
		expect(assessFacturaeRoles(['emisor'], false, 'xades').status).toBe('fail');
	});

	it('accepts every published FacturaE role, in its capacity', () => {
		for (const [role, capacity] of Object.entries(FACTURAE_ROLES)) {
			const finding = assessFacturaeRoles([role], true, 'xades');
			expect(finding.status).toBe('pass');
			expect(finding.detail).toContain(capacity);
		}
		expect(assessFacturaeRoles(['EMISOR'], true, 'xades').status).toBe('pass');
	});

	it('never accepts an inherited Object key as a FacturaE role', () => {
		for (const role of ['constructor', '__proto__', 'toString', 'hasOwnProperty']) {
			const finding = assessFacturaeRoles([role], true, 'xades');
			expect(finding.status).toBe('warn');
			expect(finding.detail).toContain(role);
		}
	});
});

describe('extractCadesClaims', () => {
	it('reads policy + commitment from signedAttrs and flags unsigned ones', () => {
		const signed = [
			{ type: OID_SIG_POLICY, values: [parse(policyDer(FACTURAE_31_URI, unb64(facturae.digestBase64))).node] },
			{ type: OID_COMMITMENT, values: [parse(commitmentDer('1.2.840.113549.1.9.16.6.6')).node] }
		];
		const unsigned = [
			{ type: OID_COMMITMENT, values: [parse(commitmentDer('1.2.840.113549.1.9.16.6.1')).node] }
		];
		const { policies, commitments } = extractCadesClaims(signed, unsigned, 'cades');
		expect(policies).toHaveLength(1);
		expect(policies[0].identifier).toBe(FACTURAE_31_URI);
		expect(policies[0].signed).toBe(true);
		expect(Array.from(policies[0].digestValue!)).toEqual(Array.from(unb64(facturae.digestBase64)));
		expect(assessPolicyClaim(policies[0]).status).toBe('pass');
		expect(commitments).toHaveLength(2);
		expect(assessCommitmentClaim(commitments[0]).status).toBe('pass');
		expect(assessCommitmentClaim(commitments[1]).status).toBe('fail');
		expect(commitments[1].signed).toBe(false);
	});

	it('reads a real attribute value, where the value is the indication itself', () => {
		const withQualifier = commitmentDer(
			'1.2.840.113549.1.9.16.6.3',
			tlv(0x30, tlv(0x06, new Uint8Array([0x2a, 0x03, 0x04])))
		);
		const { commitments } = extractCadesClaims(
			[
				{ type: OID_COMMITMENT, values: [parse(commitmentDer('1.2.840.113549.1.9.16.6.1')).node] },
				{ type: OID_COMMITMENT, values: [parse(withQualifier).node] }
			],
			[],
			'cades'
		);
		expect(commitments.map((claim) => claim.kind)).toEqual([
			'1.2.840.113549.1.9.16.6.1',
			'1.2.840.113549.1.9.16.6.3'
		]);
		expect(assessCommitmentClaim(commitments[0]).status).toBe('pass');
	});

	it('labels PAdES claims as pades — same OIDs, different family', () => {
		const { policies } = extractCadesClaims(
			[{ type: OID_SIG_POLICY, values: [parse(policyDer('1.9.9', new Uint8Array([7]))).node] }],
			[],
			'pades'
		);
		expect(policies[0].family).toBe('pades');
		const finding = assessPolicyClaim(policies[0]);
		expect(finding.status).toBe('warn');
		expect(finding.identifier).toBe('1.9.9');
	});

	it('ignores unrelated attributes and survives garbage values', () => {
		const { policies, commitments } = extractCadesClaims(
			[
				{ type: '1.2.840.113549.1.9.4', values: [parse(policyDer(FACTURAE_31_URI, unb64(facturae.digestBase64))).node] },
				{ type: OID_SIG_POLICY, values: [parse(tlv(0x30, new Uint8Array([1, 2, 3]))).node] }
			],
			[],
			'cades'
		);
		expect(commitments).toHaveLength(0);
		expect(policies).toHaveLength(1);
		expect(policies[0].unreadable).toBe(true);
		expect(assessPolicyClaim(policies[0]).status).toBe('info');
		expect(Object.keys(CADES_COMMITMENTS)).toHaveLength(6);
	});
});

describe('extractXadesClaims', () => {
	it('reads an explicit policy, commitment and claimed role from signed properties', () => {
		const document = parsePolicyXml(`
			<xades:SignaturePolicyIdentifier><xades:SignaturePolicyId>
				<xades:SigPolicyId><xades:Identifier>1.2.3.4</xades:Identifier></xades:SigPolicyId>
				<xades:SigPolicyHash><ds:DigestMethod Algorithm="http://www.w3.org/2001/04/xmlenc#sha256"/><ds:DigestValue>AAE=</ds:DigestValue></xades:SigPolicyHash>
			</xades:SignaturePolicyId></xades:SignaturePolicyIdentifier>
			<xades:CommitmentTypeIndication><xades:CommitmentTypeId><xades:Identifier>http://uri.etsi.org/01903/v1.3.2#ProofOfOrigin</xades:Identifier></xades:CommitmentTypeId></xades:CommitmentTypeIndication>
			<xades:SignerRole><xades:ClaimedRoles><xades:ClaimedRole>emisor</xades:ClaimedRole></xades:ClaimedRoles></xades:SignerRole>`);
		const { policies, commitments, roles } = extractXadesClaims(document);
		expect(policies).toHaveLength(1);
		expect(policies[0].identifier).toBe('1.2.3.4');
		expect(policies[0].signed).toBe(true);
		expect(assessPolicyClaim(policies[0]).status).toBe('warn');
		expect(assessPolicyClaim(policies[0]).identifier).toBe('1.2.3.4');
		expect(commitments).toHaveLength(1);
		expect(assessCommitmentClaim(commitments[0]).status).toBe('pass');
		expect(roles.values).toEqual(['emisor']);
		expect(roles.signed).toBe(true);
	});

	it('reads the FacturaE hash the XAdES profile puts in the ds namespace, and fails a tampered one', () => {
		const document = parsePolicyXml(`
			<xades:SignaturePolicyIdentifier><xades:SignaturePolicyId>
				<xades:SigPolicyId><xades:Identifier>${FACTURAE_31_URI}</xades:Identifier></xades:SigPolicyId>
				<xades:SigPolicyHash><ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/><ds:DigestValue>${facturae.digestBase64}</ds:DigestValue></xades:SigPolicyHash>
			</xades:SignaturePolicyId></xades:SignaturePolicyIdentifier>`);
		const { policies } = extractXadesClaims(document);
		expect(Array.from(policies[0].digestValue!)).toEqual(Array.from(unb64(facturae.digestBase64)));
		expect(assessPolicyClaim(policies[0]).status).toBe('pass');

		const tampered = parsePolicyXml(`
			<xades:SignaturePolicyIdentifier><xades:SignaturePolicyId>
				<xades:SigPolicyId><xades:Identifier>${FACTURAE_31_URI}</xades:Identifier></xades:SigPolicyId>
				<xades:SigPolicyHash><ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/><ds:DigestValue>AAAAAAAAAAAAAAAAAAAAAAAAAAA=</ds:DigestValue></xades:SigPolicyHash>
			</xades:SignaturePolicyId></xades:SignaturePolicyIdentifier>`);
		expect(assessPolicyClaim(extractXadesClaims(tampered).policies[0]).status).toBe('fail');
	});

	it('marks implied policies info and flags unsigned commitments', () => {
		const document = parsePolicyXml(
			'<xades:SignaturePolicyIdentifier><xades:SignaturePolicyImplied/></xades:SignaturePolicyIdentifier>'
		);
		const { policies } = extractXadesClaims(document);
		expect(assessPolicyClaim(policies[0]).status).toBe('info');
		const unsigned = new DOMParser().parseFromString(
			`<ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#" xmlns:xades="http://uri.etsi.org/01903/v1.3.2#"><ds:Object><xades:QualifyingProperties><xades:UnsignedProperties><xades:UnsignedSignatureProperties><xades:CommitmentTypeIndication><xades:CommitmentTypeId><xades:Identifier>http://uri.etsi.org/01903/v1.3.2#ProofOfReceipt</xades:Identifier></xades:CommitmentTypeId></xades:CommitmentTypeIndication></xades:UnsignedSignatureProperties></xades:UnsignedProperties></xades:QualifyingProperties></ds:Object></ds:Signature>`,
			'application/xml'
		);
		const { commitments } = extractXadesClaims(unsigned);
		expect(commitments).toHaveLength(1);
		expect(commitments[0].signed).toBe(false);
		expect(assessCommitmentClaim(commitments[0]).status).toBe('fail');
	});

	it('treats a commitment under SignedDataObjectProperties as signed', () => {
		const document = parsePolicyXml(
			'<xades:CommitmentTypeIndication><xades:CommitmentTypeId><xades:Identifier>http://uri.etsi.org/01903/v1.3.2#ProofOfOrigin</xades:Identifier></xades:CommitmentTypeId></xades:CommitmentTypeIndication>',
			'SignedDataObjectProperties'
		);
		const { commitments } = extractXadesClaims(document);
		expect(commitments).toHaveLength(1);
		expect(commitments[0].signed).toBe(true);
		expect(assessCommitmentClaim(commitments[0]).status).toBe('pass');
	});
});
