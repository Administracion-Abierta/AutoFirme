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

// Official XSD targetNamespaces: `facturae.gob.es/formato/versiones-anteriores` (3.0, 3.1) and
// `facturae.gob.es/formato/ultima-version` (3.2, 3.2.1, 3.2.2).
const SCHEMA_BY_VERSION: Record<FacturaeVersion, string> = {
	'3.0': 'http://www.facturae.es/Facturae/2007/v3.0/Facturae',
	'3.1': 'http://www.facturae.es/Facturae/2007/v3.1/Facturae',
	'3.2': 'http://www.facturae.es/Facturae/2009/v3.2/Facturae',
	'3.2.1': 'http://www.facturae.es/Facturae/2014/v3.2.1/Facturae',
	'3.2.2': 'http://www.facturae.gob.es/formato/Versiones/Facturaev3_2_2.xml'
};

// `TotalCost`/`GrossAmount` are `DoubleSixDecimalType` in 3.2, `DoubleTwoDecimalType` in
// 3.0/3.1 and up to eight decimals in 3.2.1/3.2.2 — one fixture that validates everywhere.
// The whole file validates against the official XSDs for every version (see the issue report).
const lineAmount = (version: FacturaeVersion): string => (version === '3.2' ? '100.000000' : '100.00');

const invoice = (version: FacturaeVersion): string => `<?xml version="1.0" encoding="UTF-8"?>
<fe:Facturae xmlns:fe="${SCHEMA_BY_VERSION[version]}">
	<FileHeader>
		<SchemaVersion>${version}</SchemaVersion>
		<Modality>I</Modality>
		<InvoiceIssuerType>EM</InvoiceIssuerType>
		<Batch>
			<BatchIdentifier>B123456782026-0001</BatchIdentifier>
			<InvoicesCount>1</InvoicesCount>
			<TotalInvoicesAmount><TotalAmount>121.00</TotalAmount></TotalInvoicesAmount>
			<TotalOutstandingAmount><TotalAmount>121.00</TotalAmount></TotalOutstandingAmount>
			<TotalExecutableAmount><TotalAmount>121.00</TotalAmount></TotalExecutableAmount>
			<InvoiceCurrencyCode>EUR</InvoiceCurrencyCode>
		</Batch>
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
				<AddressInSpain>
					<Address>Calle Ejemplo 1</Address>
					<PostCode>28001</PostCode>
					<Town>Madrid</Town>
					<Province>Madrid</Province>
					<CountryCode>ESP</CountryCode>
				</AddressInSpain>
			</LegalEntity>
		</SellerParty>
		<BuyerParty>
			<TaxIdentification>
				<PersonTypeCode>J</PersonTypeCode>
				<ResidenceTypeCode>R</ResidenceTypeCode>
				<TaxIdentificationNumber>A87654321</TaxIdentificationNumber>
			</TaxIdentification>
			<LegalEntity>
				<CorporateName>Cliente Ejemplo S.A.</CorporateName>
				<AddressInSpain>
					<Address>Calle Ejemplo 2</Address>
					<PostCode>08001</PostCode>
					<Town>Barcelona</Town>
					<Province>Barcelona</Province>
					<CountryCode>ESP</CountryCode>
				</AddressInSpain>
			</LegalEntity>
		</BuyerParty>
	</Parties>
	<Invoices>
		<Invoice>
			<InvoiceHeader>
				<InvoiceNumber>2026-0001</InvoiceNumber>
				<InvoiceDocumentType>FC</InvoiceDocumentType>
				<InvoiceClass>OO</InvoiceClass>
			</InvoiceHeader>
			<InvoiceIssueData>
				<IssueDate>2026-10-24</IssueDate>
				<InvoiceCurrencyCode>EUR</InvoiceCurrencyCode>
				<TaxCurrencyCode>EUR</TaxCurrencyCode>
				<LanguageName>es</LanguageName>
			</InvoiceIssueData>
			<TaxesOutputs>
				<Tax>
					<TaxTypeCode>01</TaxTypeCode>
					<TaxRate>21.00</TaxRate>
					<TaxableBase><TotalAmount>100.00</TotalAmount></TaxableBase>
					<TaxAmount><TotalAmount>21.00</TotalAmount></TaxAmount>
				</Tax>
			</TaxesOutputs>
			<InvoiceTotals>
				<TotalGrossAmount>100.00</TotalGrossAmount>
				<TotalGrossAmountBeforeTaxes>100.00</TotalGrossAmountBeforeTaxes>
				<TotalTaxOutputs>21.00</TotalTaxOutputs>
				<TotalTaxesWithheld>0.00</TotalTaxesWithheld>
				<InvoiceTotal>121.00</InvoiceTotal>
				<TotalOutstandingAmount>121.00</TotalOutstandingAmount>
				<TotalExecutableAmount>121.00</TotalExecutableAmount>
			</InvoiceTotals>
			<Items>
				<InvoiceLine>
					<ItemDescription>Servicio de ejemplo</ItemDescription>
					<Quantity>1.00</Quantity>
					<UnitOfMeasure>01</UnitOfMeasure>
					<UnitPriceWithoutTax>100.000000</UnitPriceWithoutTax>
					<TotalCost>${lineAmount(version)}</TotalCost>
					<GrossAmount>${lineAmount(version)}</GrossAmount>
					<TaxesOutputs>
						<Tax>
							<TaxTypeCode>01</TaxTypeCode>
							<TaxRate>21.00</TaxRate>
							<TaxableBase><TotalAmount>100.00</TotalAmount></TaxableBase>
							<TaxAmount><TotalAmount>21.00</TotalAmount></TaxAmount>
						</Tax>
					</TaxesOutputs>
				</InvoiceLine>
			</Items>
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
			// The invoice's own namespace survives signing (nothing rewrites it).
			expect(root.namespaceURI).toBe(SCHEMA_BY_VERSION[version]);
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
