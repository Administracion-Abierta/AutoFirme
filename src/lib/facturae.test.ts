/**
 * FacturaE presets (#131): one invoice per version signs with the right policy and role,
 * verified by `xmlsec1`; wrong roles rejected before signing; co/counter-sign refused.
 *
 * Requires `openssl` and `xmlsec1` on `PATH`; neither is a dependency of the app.
 */

import { join } from 'node:path';
import { beforeAll, describe, expect, it } from 'vitest';
import {
	FIXTURE_PIN,
	createPkcs12Fixtures,
	writeFixtureFile,
	xmlsec1Verify,
	type Pkcs12Fixtures
} from '../test/fixtures';
import { loadSignerSession } from './keystore';
import type { SignerSession } from './sign';
import {
	cosignFacturae,
	countersignFacturae,
	FACTURAE_PRESETS,
	isFacturae,
	signFacturae,
	type FacturaeVersion
} from './facturae';
import type { XadesIds } from './xades';

const IDS: Partial<XadesIds> = {
	signature: 'Signature-FacturaE',
	signedInfo: 'SignedInfo-FacturaE',
	signatureValue: 'SignatureValue-FacturaE',
	keyInfo: 'KeyInfo-FacturaE',
	signedProperties: 'SignedProperties-FacturaE',
	documentReference: 'Reference-FacturaE',
	propertiesReference: 'Reference-Properties-FacturaE'
};

const SCHEMA_BY_VERSION: Record<FacturaeVersion, string> = {
	'3.0': 'http://www.facturae.es/Facturae/2009/v3.0/Facturae',
	'3.1': 'http://www.facturae.es/Facturae/2012/v3.1/Facturae',
	'3.2': 'http://www.facturae.org/Facturae/2014/v3.2/Facturae',
	'3.2.1': 'http://www.facturae.es/Facturae/2014/v3.2.1/Facturae',
	'3.2.2': 'http://www.facturae.org/Facturae/2014/v3.2.2/Facturae'
};

const invoice = (version: FacturaeVersion): string => `<?xml version="1.0" encoding="UTF-8"?>
<fe:Facturae xmlns:fe="${SCHEMA_BY_VERSION[version]}">
	<FileHeader>
		<SchemaVersion>${version}</SchemaVersion>
		<Modality>I</Modality>
	</FileHeader>
	<Parties>
		<SellerParty>
			<TaxIdentification>
				<PersonTypeCode>J</PersonTypeCode>
				<ResidenceTypeCode>R</ResidenceTypeCode>
				<TaxIdentificationNumber>B12345678</TaxIdentificationNumber>
			</TaxIdentification>
			<LegalEntity>
				<CorporateName>AutoFirme Prueba S.L.</CorporateName>
			</LegalEntity>
		</SellerParty>
	</Parties>
	<Invoices>
		<Invoice>
			<InvoiceHeader>
				<InvoiceNumber>2026-0001</InvoiceNumber>
			</InvoiceHeader>
			<InvoiceTotals>
				<TotalGrossAmount>100.00</TotalGrossAmount>
			</InvoiceTotals>
		</Invoice>
	</Invoices>
</fe:Facturae>
`;

const NOW = new Date('2026-10-26T10:00:00Z');
const encoder = new TextEncoder();
const XADES = 'http://uri.etsi.org/01903/v1.3.2#';
const SHA1 = 'http://www.w3.org/2000/09/xmldsig#sha1';

const parse = (xml: string): Document => new DOMParser().parseFromString(xml, 'application/xml');

describe('facturae presets', () => {
	let fixtures: Pkcs12Fixtures;
	let session: SignerSession;

	beforeAll(async () => {
		fixtures = createPkcs12Fixtures();
		session = await loadSignerSession(fixtures.modern, FIXTURE_PIN);
	});

	for (const version of Object.keys(FACTURAE_PRESETS) as FacturaeVersion[]) {
		it(`3.x — ${version} signs the invoice and xmlsec1 verifies`, async () => {
			const signed = await signFacturae({
				invoice: invoice(version),
				version,
				certificates: session.certificates,
				signer: session.signer,
				now: NOW,
				ids: IDS
			});
			const root = parse(signed).documentElement;
			// Version's policy identifier + SHA-1 digest of it.
			const preset = FACTURAE_PRESETS[version];
			const identifiers = [...root.getElementsByTagNameNS(XADES, 'Identifier')].map((node) => node.textContent);
			expect(identifiers).toContain(preset.policyIdentifier);
			const hash = root.getElementsByTagNameNS(XADES, 'SigPolicyHash')[0];
			expect(hash.getElementsByTagNameNS('*', 'DigestMethod')[0].getAttribute('Algorithm')).toBe(SHA1);
			// Default claimed role is emisor.
			const roles = [...root.getElementsByTagNameNS(XADES, 'ClaimedRole')].map((node) => node.textContent);
			expect(roles).toEqual(['emisor']);
			const path = writeFixtureFile(fixtures.dir, `facturae-${version}.xsig`, encoder.encode(signed));
			expect(xmlsec1Verify(path, join(fixtures.dir, 'leaf.pem'))).toBe('OK');
			// XAdES 1.3.2 `SignedSignaturePropertiesType` is a sequence: policy before role.
			const policyNode = root.getElementsByTagNameNS(XADES, 'SignaturePolicyIdentifier')[0];
			const roleNode = root.getElementsByTagNameNS(XADES, 'SignerRole')[0];
			expect(policyNode.compareDocumentPosition(roleNode) & Node.DOCUMENT_POSITION_FOLLOWING).toBeTruthy();
		});
	}

	it('carries a non-default whitelisted role', async () => {
		const signed = await signFacturae({
			invoice: invoice('3.2.2'),
			version: '3.2.2',
			certificates: session.certificates,
			signer: session.signer,
			claimedRole: 'supplier',
			now: NOW,
			ids: IDS
		});
		const roles = [...parse(signed).documentElement.getElementsByTagNameNS(XADES, 'ClaimedRole')].map(
			(node) => node.textContent
		);
		expect(roles).toEqual(['supplier']);
	});

	it('rejects a wrong claimed role before signing', async () => {
		await expect(
			signFacturae({
				invoice: invoice('3.2.2'),
				version: '3.2.2',
				certificates: session.certificates,
				signer: session.signer,
				claimedRole: 'auditor',
				now: NOW,
				ids: IDS
			})
		).rejects.toThrow("role 'auditor' is not valid");
	});

	it('refuses to sign a non-Facturae document', async () => {
		await expect(
			signFacturae({
				invoice: '<hello>not an invoice</hello>',
				version: '3.2.2',
				certificates: session.certificates,
				signer: session.signer,
				now: NOW,
				ids: IDS
			})
		).rejects.toThrow('not a Facturae invoice');
	});
});

describe('facturae guards', () => {
	it('isFacturae accepts a Facturae root and rejects the rest', () => {
		expect(isFacturae(invoice('3.2.2'))).toBe(true);
		expect(isFacturae('<hello/>')).toBe(false);
		expect(isFacturae('not xml at all')).toBe(false);
		expect(isFacturae(new TextEncoder().encode(invoice('3.0')))).toBe(true);
	});

	it('refuses co-signature and counter-signature', () => {
		expect(cosignFacturae).toThrow('co-signature is not supported');
		expect(countersignFacturae).toThrow('counter-signature is not supported');
	});
});
