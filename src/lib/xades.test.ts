/**
 * XAdES-EPES end-to-end: a Facturae-shaped document is signed with a certificate loaded from a
 * PKCS#12 container (#5), and the `.xsig` is verified by `xmlsec1` — an independent XMLDSig
 * implementation, so it doubles as the check that our canonicalization matches a real one.
 *
 * Requires the `openssl` (fixtures, #5) and `xmlsec1` binaries on `PATH`; neither is a dependency
 * of the app.
 */

import { execFileSync, spawnSync } from 'node:child_process';
import { createHash } from 'node:crypto';
import { basename, join } from 'node:path';
import { rmSync } from 'node:fs';
import { beforeAll, describe, expect, it, vi } from 'vitest';
import {
	FIXTURE_PIN,
	createPkcs12Fixtures,
	createTsaFixtures,
	writeFixtureFile,
	xmlsec1Verify,
	type Pkcs12Fixtures
} from '../test/fixtures';
import { canonicalize } from './c14n';
import { loadSignerSession } from './keystore';
import { type SignerSession } from './sign';
import { signXades, type XadesIds } from './xades';

const DS = 'http://www.w3.org/2000/09/xmldsig#';
const XADES = 'http://uri.etsi.org/01903/v1.3.2#';

const IDS: Partial<XadesIds> = {
	signature: 'Signature-Test',
	signedInfo: 'SignedInfo-Test',
	signatureValue: 'SignatureValue-Test',
	keyInfo: 'KeyInfo-Test',
	signedProperties: 'SignedProperties-Test',
	documentReference: 'Reference-Document',
	propertiesReference: 'Reference-Properties'
};

/** Facturae-shaped, with the `ds` prefix already in scope: a realistic prefix-collision hazard. */
const SAMPLE = `<?xml version="1.0" encoding="UTF-8"?>
<fe:Facturae xmlns:fe="http://www.facturae.es/Facturae/2014/v3.2.1/Facturae" xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
	<FileHeader>
		<SchemaVersion>3.2.1</SchemaVersion>
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

const NOW = new Date('2026-10-14T10:00:00Z');
const encoder = new TextEncoder();

function parse(xml: string): Document {
	return new DOMParser().parseFromString(xml, 'application/xml');
}

function elements(root: Element, namespace: string, localName: string): Element[] {
	return [...root.getElementsByTagNameNS(namespace, localName)];
}

describe('signXades', () => {
	let fixtures: Pkcs12Fixtures;
	let session: SignerSession;
	let signed: string;
	let root: Element;

	beforeAll(async () => {
		fixtures = createPkcs12Fixtures();
		session = await loadSignerSession(fixtures.modern, FIXTURE_PIN);
		signed = await signXades({
			xml: SAMPLE,
			certificates: session.certificates,
			signer: session.signer,
			ids: IDS,
			now: NOW
		});
		root = parse(signed).documentElement;
	});

	it('is verified by xmlsec1', () => {
		// #5's leaf fixture is self-signed (the container also carries an unrelated CA cert), so the
		// leaf is its own trust anchor.
		const path = writeFixtureFile(fixtures.dir, 'facturae.xsig', encoder.encode(signed));
		expect(xmlsec1Verify(path, join(fixtures.dir, 'leaf.pem'))).toBe('OK');
	});

	it('fails xmlsec1 verification once the signed document is modified', () => {
		const tampered = signed.replace('B12345678', 'B00000000');
		expect(tampered).not.toBe(signed);
		const path = writeFixtureFile(fixtures.dir, 'tampered.xsig', encoder.encode(tampered));
		expect(xmlsec1Verify(path, join(fixtures.dir, 'leaf.pem'))).toBe('FAIL');
	});

	it('emits the enveloped body without the enveloping-only slot whitespace', () => {
		// The enveloping data slot is interpolated before `<ds:Object>`; empty, it used to leave a
		// stray tab + newline between `ds:KeyInfo` and the QualifyingProperties object.
		expect(signed).toContain('</ds:KeyInfo>\n\t<ds:Object>');
		expect(signed).not.toContain('</ds:KeyInfo>\n\t\n');
	});

	it('signs SHA1withRSA XAdES for legacy portals', async () => {
		const sha1Signed = await signXades({
			xml: SAMPLE,
			certificates: session.certificates,
			signer: session.signer,
			ids: IDS,
			now: NOW,
			hash: 'SHA-1'
		});
		const sha1Root = parse(sha1Signed).documentElement;
		expect(elements(sha1Root, DS, 'SignatureMethod')[0].getAttribute('Algorithm')).toBe(
			'http://www.w3.org/2000/09/xmldsig#rsa-sha1'
		);
		expect(elements(sha1Root, DS, 'DigestMethod').map((method) => method.getAttribute('Algorithm'))).toEqual([
			'http://www.w3.org/2000/09/xmldsig#sha1',
			'http://www.w3.org/2000/09/xmldsig#sha1',
			'http://www.w3.org/2000/09/xmldsig#sha1',
			'http://www.w3.org/2000/09/xmldsig#sha1'
		]);
		const path = writeFixtureFile(fixtures.dir, 'facturae-sha1.xsig', encoder.encode(sha1Signed));
		expect(xmlsec1Verify(path, join(fixtures.dir, 'leaf.pem'))).toBe('OK');

		const signedInfo = canonicalize(elements(sha1Root, DS, 'SignedInfo')[0], { exclusive: true });
		const signature = Buffer.from(elements(sha1Root, DS, 'SignatureValue')[0].textContent!, 'base64');
		const signedInfoPath = writeFixtureFile(fixtures.dir, 'signedinfo-sha1.bin', new Uint8Array(signedInfo));
		const signaturePath = writeFixtureFile(fixtures.dir, 'signature-sha1.bin', new Uint8Array(signature));
		const output = execFileSync(
			'openssl',
			['dgst', '-sha1', '-verify', join(fixtures.dir, 'leaf.pub.pem'), '-signature', signaturePath, signedInfoPath],
			{ encoding: 'utf8' }
		);
		expect(output).toMatch(/Verified OK/);
	});

	it('keeps document-level PIs and comments and xmlsec1 still verifies', async () => {
		const withMisc = SAMPLE.replace(
			'<fe:Facturae',
			'<?xml-stylesheet type="text/xsl" href="facturae.xsl"?>\n<!-- reviewed -->\n<fe:Facturae'
		);
		const miscSigned = await signXades({
			xml: withMisc,
			certificates: session.certificates,
			signer: session.signer,
			ids: IDS,
			now: NOW
		});
		expect(miscSigned).toContain('<?xml-stylesheet type="text/xsl" href="facturae.xsl"?>');
		expect(miscSigned).toContain('<!-- reviewed -->');
		const path = writeFixtureFile(fixtures.dir, 'facturae-misc.xsig', encoder.encode(miscSigned));
		expect(xmlsec1Verify(path, join(fixtures.dir, 'leaf.pem'))).toBe('OK');
	});

	it('carries a WebCrypto signature over the canonical SignedInfo', () => {
		const signedInfo = canonicalize(elements(root, DS, 'SignedInfo')[0], { exclusive: true });
		const signature = Buffer.from(elements(root, DS, 'SignatureValue')[0].textContent!, 'base64');
		const signedInfoPath = writeFixtureFile(fixtures.dir, 'signedinfo.bin', new Uint8Array(signedInfo));
		const signaturePath = writeFixtureFile(fixtures.dir, 'signature.bin', new Uint8Array(signature));
		const output = execFileSync(
			'openssl',
			['dgst', '-sha256', '-verify', join(fixtures.dir, 'leaf.pub.pem'), '-signature', signaturePath, signedInfoPath],
			{ encoding: 'utf8' }
		);
		expect(output).toMatch(/Verified OK/);
	});

	it('envelopes the signature at the document root, XAdES structure in schema order', () => {
		expect(root.lastElementChild!.tagName).toBe('ds:Signature');
		const signature = root.lastElementChild!;
		expect([...signature.children].map((child) => child.tagName)).toEqual([
			'ds:SignedInfo',
			'ds:SignatureValue',
			'ds:KeyInfo',
			'ds:Object'
		]);
		const signatureId = signature.getAttribute('Id');
		expect(signatureId).toBe(IDS.signature);
		expect(elements(root, XADES, 'QualifyingProperties')[0].getAttribute('Target')).toBe(`#${signatureId}`);
		expect(elements(root, XADES, 'SignedProperties')[0].getAttribute('Id')).toBe(IDS.signedProperties);
		expect(elements(root, XADES, 'SignedSignatureProperties')[0].children).toHaveLength(3);
		expect(elements(root, XADES, 'SigningTime')[0].textContent).toBe('2026-10-14T10:00:00Z');
		expect(elements(root, XADES, 'SigningCertificate')).toHaveLength(1);
		expect(elements(root, XADES, 'SignaturePolicyImplied')).toHaveLength(1);
		expect(elements(root, XADES, 'DataObjectFormat')[0].getAttribute('ObjectReference')).toBe(
			`#${IDS.documentReference}`
		);
		expect(elements(root, XADES, 'MimeType')[0].textContent).toBe('application/xml');
	});

	it('references the document (enveloped) and the signed properties', () => {
		const signatureMethod = elements(root, DS, 'SignatureMethod')[0].getAttribute('Algorithm');
		expect(signatureMethod).toBe('http://www.w3.org/2001/04/xmldsig-more#rsa-sha256');
		expect(elements(root, DS, 'CanonicalizationMethod')[0].getAttribute('Algorithm')).toBe(
			'http://www.w3.org/2001/10/xml-exc-c14n#'
		);
		const references = elements(root, DS, 'Reference');
		expect(references[0].getAttribute('URI')).toBe('');
		expect(elements(references[0], DS, 'Transform').map((transform) => transform.getAttribute('Algorithm'))).toEqual([
			'http://www.w3.org/2000/09/xmldsig#enveloped-signature',
			'http://www.w3.org/2001/10/xml-exc-c14n#'
		]);
		expect(references[1].getAttribute('URI')).toBe(`#${IDS.signedProperties}`);
		expect(references[1].getAttribute('Type')).toBe('http://uri.etsi.org/01903#SignedProperties');
		expect(elements(references[1], DS, 'DigestMethod')[0].getAttribute('Algorithm')).toBe(
			'http://www.w3.org/2001/04/xmlenc#sha256'
		);
		// The two references plus the two SigningCertificate cert digests.
		expect(
			elements(root, DS, 'DigestMethod').map((method) => method.getAttribute('Algorithm'))
		).toEqual(Array(4).fill('http://www.w3.org/2001/04/xmlenc#sha256'));
	});

	it('embeds the whole certificate chain from the container in the KeyInfo', () => {
		const embedded = elements(root, DS, 'X509Certificate').map((certificate) => certificate.textContent);
		expect(embedded).toHaveLength(session.certificates.length);
		expect(embedded).toEqual(
			session.certificates.map((certificate) => Buffer.from(certificate.der).toString('base64'))
		);
	});

	it('digests the container certificates and names their issuers in SigningCertificate', () => {
		const certs = elements(root, XADES, 'Cert');
		expect(certs).toHaveLength(session.certificates.length);
		session.certificates.forEach((certificate, index) => {
			const digest = elements(certs[index], DS, 'DigestValue')[0].textContent;
			expect(digest).toBe(createHash('sha256').update(certificate.der).digest('base64'));
			expect(elements(certs[index], DS, 'X509IssuerName')[0].textContent).toBe(
				certificate.issuer.replace(/, /g, ',')
			);
			expect(elements(certs[index], DS, 'X509SerialNumber')[0].textContent).toBe(
				BigInt(`0x${certificate.serial}`).toString()
			);
		});
	});

	it('signs with an EC key too, and xmlsec1 verifies it', async () => {
		const ec = await loadSignerSession(fixtures.ec, FIXTURE_PIN);
		const ecSigned = await signXades({
			xml: SAMPLE,
			certificates: ec.certificates,
			signer: ec.signer,
			ids: IDS,
			now: NOW
		});
		expect(elements(parse(ecSigned).documentElement, DS, 'SignatureMethod')[0].getAttribute('Algorithm')).toBe(
			'http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256'
		);
		const path = writeFixtureFile(fixtures.dir, 'facturae-ec.xsig', encoder.encode(ecSigned));
		// The EC container is self-signed, so the leaf is its own trust anchor.
		expect(xmlsec1Verify(path, join(fixtures.dir, 'ec.pem'))).toBe('OK');
	});
	it('refuses to sign a document that already carries a signature', async () => {
		await expect(
			signXades({ xml: signed, certificates: session.certificates, signer: session.signer })
		).rejects.toThrow(/already signed/);
	});

	it('embeds a SignatureTimeStamp over the canonicalised SignatureValue (XAdES-T)', async () => {
		const tsa = createTsaFixtures();
		try {
			vi.stubGlobal(
				'fetch',
				async (_url: string, init: RequestInit) => new Response(tsa.respond(init.body as Uint8Array), { status: 200 })
			);
			const stamped = await signXades({
				xml: SAMPLE,
				certificates: session.certificates,
				signer: session.signer,
				ids: IDS,
				now: NOW,
				timeStamp: 'https://tsa.test/'
			});
			vi.unstubAllGlobals();

			const stampedRoot = parse(stamped).documentElement;
			const timeStamp = elements(stampedRoot, XADES, 'SignatureTimeStamp')[0];
			expect(timeStamp).toBeDefined();
			expect(elements(timeStamp, DS, 'CanonicalizationMethod')[0].getAttribute('Algorithm')).toBe(
				'http://www.w3.org/2001/10/xml-exc-c14n#'
			);
			// xmlsec1 ignores the unsigned properties and still verifies the core signature.
			const path = writeFixtureFile(fixtures.dir, 'facturae-t.xsig', encoder.encode(stamped));
			expect(xmlsec1Verify(path, join(fixtures.dir, 'leaf.pem'))).toBe('OK');

			// The token imprint is the digest of the canonicalised SignatureValue element.
			const token = Buffer.from(
				elements(timeStamp, XADES, 'EncapsulatedTimeStamp')[0].textContent!,
				'base64'
			);
			const valueBytes = canonicalize(elements(stampedRoot, DS, 'SignatureValue')[0], { exclusive: true });
			const digestHex = createHash('sha256').update(valueBytes).digest('hex');
			const tokenPath = writeFixtureFile(fixtures.dir, 'sig-t.p7', new Uint8Array(token));
			const output = execFileSync(
				'openssl',
				['ts', '-verify', '-in', tokenPath, '-token_in', '-digest', digestHex, '-CAfile', tsa.certPath],
				{ encoding: 'utf8' }
			);
			expect(`${output}`).toMatch(/Verification: OK/);
		} finally {
			vi.unstubAllGlobals();
			rmSync(tsa.dir, { recursive: true, force: true });
		}
	});
});

describe('signXades reference types', () => {
	let fixtures: Pkcs12Fixtures;
	let session: SignerSession;

	beforeAll(async () => {
		fixtures = createPkcs12Fixtures();
		session = await loadSignerSession(fixtures.modern, FIXTURE_PIN);
	});

	function verifySignedInfo(signed: string, name: string): void {
		const root = parse(signed).documentElement;
		const signedInfo = canonicalize(elements(root, DS, 'SignedInfo')[0], { exclusive: true });
		const signature = Buffer.from(elements(root, DS, 'SignatureValue')[0].textContent!, 'base64');
		const signedInfoPath = writeFixtureFile(fixtures.dir, `${name}.signedinfo`, new Uint8Array(signedInfo));
		const signaturePath = writeFixtureFile(fixtures.dir, `${name}.sig`, new Uint8Array(signature));
		const output = execFileSync(
			'openssl',
			['dgst', '-sha256', '-verify', join(fixtures.dir, 'leaf.pub.pem'), '-signature', signaturePath, signedInfoPath],
			{ encoding: 'utf8' }
		);
		expect(output).toMatch(/Verified OK/);
	}

	function xmlsec1VerifyFromDir(signedPath: string, trustAnchor: string): 'OK' | 'FAIL' {
		// xmlsec1 resolves a relative Reference URI against the process cwd, not the
		// signature path — so detached signatures verify from their own directory.
		const { status, stdout, stderr } = spawnSync(
			'xmlsec1',
			['--verify', '--enabled-key-data', 'x509', '--trusted-pem', trustAnchor, basename(signedPath)],
			{ cwd: fixtures.dir, encoding: 'utf8' }
		);
		const output = `${stdout}${stderr}`;
		const verdict =
			/^(?:Verification )?status:\s*(OK|FAIL|FAILED)/m.exec(output)?.[1] ?? /^(OK|FAIL)$/m.exec(output)?.[1];
		if (!verdict) throw new Error(`xmlsec1 printed no verdict (exit ${status}): ${output.trim()}`);
		return verdict === 'OK' ? 'OK' : 'FAIL';
	}

	it('signs enveloping with URI="#object" and xmlsec1 verifies it', async () => {
		const signed = await signXades({
			xml: SAMPLE,
			certificates: session.certificates,
			signer: session.signer,
			ids: { ...IDS, dataObject: 'Object-Test' },
			now: NOW,
			reference: { type: 'enveloping', objectId: 'Object-Test' }
		});
		const root = parse(signed).documentElement;
		expect(root.tagName).toBe('ds:Signature');
		const references = elements(root, DS, 'Reference');
		expect(references).toHaveLength(2);
		expect(references[0].getAttribute('URI')).toBe('#Object-Test');
		expect(elements(references[0], DS, 'Transform').map((transform) => transform.getAttribute('Algorithm'))).toEqual([
			'http://www.w3.org/2001/10/xml-exc-c14n#'
		]);
		expect(byId(root, 'Object-Test').firstElementChild?.tagName).toBe('fe:Facturae');
		expect(elements(root, DS, 'X509Certificate')).toHaveLength(session.certificates.length);
		const path = writeFixtureFile(fixtures.dir, 'facturae-enveloping.xsig', encoder.encode(signed));
		expect(xmlsec1Verify(path, join(fixtures.dir, 'leaf.pem'))).toBe('OK');
		verifySignedInfo(signed, 'enveloping');
	});

	it('signs detached against a sibling file and xmlsec1 verifies it', async () => {
		writeFixtureFile(fixtures.dir, 'invoice.xml', encoder.encode(SAMPLE));
		const signed = await signXades({
			certificates: session.certificates,
			signer: session.signer,
			ids: IDS,
			now: NOW,
			reference: { type: 'detached', uri: 'invoice.xml', data: encoder.encode(SAMPLE) }
		});
		const root = parse(signed).documentElement;
		expect(root.tagName).toBe('ds:Signature');
		const references = elements(root, DS, 'Reference');
		expect(references).toHaveLength(2);
		expect(references[0].getAttribute('URI')).toBe('invoice.xml');
		expect(elements(references[0], DS, 'Transform')).toHaveLength(0);
		const path = writeFixtureFile(fixtures.dir, 'facturae-detached.xsig', encoder.encode(signed));
		expect(xmlsec1VerifyFromDir(path, join(fixtures.dir, 'leaf.pem'))).toBe('OK');
		verifySignedInfo(signed, 'detached');
	});

	it('signs externally-detached with an absolute URI and xmlsec1 verifies it', async () => {
		const uri = `file://${join(fixtures.dir, 'external.xml')}`;
		writeFixtureFile(fixtures.dir, 'external.xml', encoder.encode(SAMPLE));
		const signed = await signXades({
			certificates: session.certificates,
			signer: session.signer,
			ids: IDS,
			now: NOW,
			reference: { type: 'detached', uri, data: encoder.encode(SAMPLE) }
		});
		const root = parse(signed).documentElement;
		expect(elements(root, DS, 'Reference')[0].getAttribute('URI')).toBe(uri);
		const path = writeFixtureFile(fixtures.dir, 'facturae-external.xsig', encoder.encode(signed));
		expect(xmlsec1Verify(path, join(fixtures.dir, 'leaf.pem'))).toBe('OK');
		verifySignedInfo(signed, 'externally-detached');
	});

	it('fails detached verification once the external bytes change', async () => {
		writeFixtureFile(fixtures.dir, 'mutable.xml', encoder.encode(SAMPLE));
		const signed = await signXades({
			certificates: session.certificates,
			signer: session.signer,
			ids: IDS,
			now: NOW,
			reference: { type: 'detached', uri: 'mutable.xml', data: encoder.encode(SAMPLE) }
		});
		const path = writeFixtureFile(fixtures.dir, 'facturae-mutable.xsig', encoder.encode(signed));
		expect(xmlsec1VerifyFromDir(path, join(fixtures.dir, 'leaf.pem'))).toBe('OK');
		writeFixtureFile(fixtures.dir, 'mutable.xml', encoder.encode(SAMPLE.replace('100.00', '999.99')));
		expect(xmlsec1VerifyFromDir(path, join(fixtures.dir, 'leaf.pem'))).toBe('FAIL');
	});

	function byId(root: Element, id: string): Element {
		const found = [...root.querySelectorAll('[Id]')].find((element) => element.getAttribute('Id') === id);
		if (!found) throw new Error(`no element with Id="${id}"`);
		return found;
	}
});
