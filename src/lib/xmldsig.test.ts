/**
 * Plain XMLDSig end-to-end: enveloped, enveloping, detached and externally-detached
 * signatures verified by `xmlsec1` — an independent XMLDSig implementation, so it
 * doubles as the check that our canonicalization matches a real one. No XAdES block.
 *
 * Requires the `openssl` (fixtures, #5) and `xmlsec1` binaries on `PATH`; neither is a
 * dependency of the app.
 */

import { execFileSync, spawnSync } from 'node:child_process';
import { basename, join } from 'node:path';
import { beforeAll, describe, expect, it } from 'vitest';
import {
	FIXTURE_PIN,
	createPkcs12Fixtures,
	writeFixtureFile,
	xmlsec1Verify,
	type Pkcs12Fixtures
} from '../test/fixtures';
import { canonicalize } from './c14n';
import { loadSignerSession } from './keystore';
import { type SignerSession } from './sign';
import { signXmldsig } from './xmldsig';

const DS = 'http://www.w3.org/2000/09/xmldsig#';

const IDS = {
	signature: 'Signature-Test',
	signedInfo: 'SignedInfo-Test',
	signatureValue: 'SignatureValue-Test',
	keyInfo: 'KeyInfo-Test',
	documentReference: 'Reference-Document',
	dataObject: 'Object-Test'
};

const SAMPLE = `<?xml version="1.0" encoding="UTF-8"?>
<order xmlns="http://example.test/orders">
	<id>2026-0001</id>
	<total>100.00</total>
</order>
`;
const encoder = new TextEncoder();

function parse(xml: string): Document {
	return new DOMParser().parseFromString(xml, 'application/xml');
}

function elements(root: Element, namespace: string, localName: string): Element[] {
	return [...root.getElementsByTagNameNS(namespace, localName)];
}

describe('signXmldsig', () => {
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

	it('signs enveloped: single reference, no XAdES block, xmlsec1 verifies it', async () => {
		const signed = await signXmldsig({
			xml: SAMPLE,
			certificates: session.certificates,
			signer: session.signer,
			ids: IDS
		});
		const root = parse(signed).documentElement;
		const references = elements(root, DS, 'Reference');
		expect(references).toHaveLength(1);
		expect(references[0].getAttribute('URI')).toBe('');
		expect(elements(references[0], DS, 'Transform').map((transform) => transform.getAttribute('Algorithm'))).toEqual([
			'http://www.w3.org/2000/09/xmldsig#enveloped-signature',
			'http://www.w3.org/2001/10/xml-exc-c14n#'
		]);
		expect(root.getElementsByTagNameNS('http://uri.etsi.org/01903/v1.3.2#', 'QualifyingProperties')).toHaveLength(0);
		// The enveloping-only `ds:Object` slot must leave no whitespace when empty.
		expect(signed).toContain('</ds:KeyInfo>\n</ds:Signature>');

		const path = writeFixtureFile(fixtures.dir, 'plain-enveloped.xml', encoder.encode(signed));
		expect(xmlsec1Verify(path, join(fixtures.dir, 'leaf.pem'))).toBe('OK');
		verifySignedInfo(signed, 'plain-enveloped');
	});

	it('signs enveloping with URI="#object" and xmlsec1 verifies it', async () => {
		const signed = await signXmldsig({
			xml: SAMPLE,
			certificates: session.certificates,
			signer: session.signer,
			ids: IDS,
			reference: { type: 'enveloping', objectId: 'Object-Test' }
		});
		const root = parse(signed).documentElement;
		expect(root.tagName).toBe('ds:Signature');
		const references = elements(root, DS, 'Reference');
		expect(references).toHaveLength(1);
		expect(references[0].getAttribute('URI')).toBe('#Object-Test');
		const path = writeFixtureFile(fixtures.dir, 'plain-enveloping.xml', encoder.encode(signed));
		expect(xmlsec1Verify(path, join(fixtures.dir, 'leaf.pem'))).toBe('OK');
		verifySignedInfo(signed, 'plain-enveloping');
	});

	it('signs detached against a sibling file and xmlsec1 verifies it', async () => {
		writeFixtureFile(fixtures.dir, 'data.xml', encoder.encode(SAMPLE));
		const signed = await signXmldsig({
			certificates: session.certificates,
			signer: session.signer,
			ids: IDS,
			reference: { type: 'detached', uri: 'data.xml', data: encoder.encode(SAMPLE) }
		});
		const root = parse(signed).documentElement;
		const references = elements(root, DS, 'Reference');
		expect(references).toHaveLength(1);
		expect(references[0].getAttribute('URI')).toBe('data.xml');
		expect(elements(references[0], DS, 'Transform')).toHaveLength(0);
		const path = writeFixtureFile(fixtures.dir, 'plain-detached.xml', encoder.encode(signed));
		expect(xmlsec1VerifyFromDir(path, join(fixtures.dir, 'leaf.pem'))).toBe('OK');
		verifySignedInfo(signed, 'plain-detached');
	});

	it('signs externally-detached with an absolute URI and xmlsec1 verifies it', async () => {
		const uri = `file://${join(fixtures.dir, 'external-data.xml')}`;
		writeFixtureFile(fixtures.dir, 'external-data.xml', encoder.encode(SAMPLE));
		const signed = await signXmldsig({
			certificates: session.certificates,
			signer: session.signer,
			ids: IDS,
			reference: { type: 'detached', uri, data: encoder.encode(SAMPLE) }
		});
		expect(elements(parse(signed).documentElement, DS, 'Reference')[0].getAttribute('URI')).toBe(uri);
		const path = writeFixtureFile(fixtures.dir, 'plain-external.xml', encoder.encode(signed));
		expect(xmlsec1Verify(path, join(fixtures.dir, 'leaf.pem'))).toBe('OK');
		verifySignedInfo(signed, 'plain-externally-detached');
	});

	it('signs a non-XML payload detached and xmlsec1 verifies it', async () => {
		writeFixtureFile(fixtures.dir, 'payload.bin', new Uint8Array([0, 1, 2, 255, 65, 66, 67]));
		const signed = await signXmldsig({
			certificates: session.certificates,
			signer: session.signer,
			ids: IDS,
			reference: { type: 'detached', uri: 'payload.bin', data: new Uint8Array([0, 1, 2, 255, 65, 66, 67]) }
		});
		const path = writeFixtureFile(fixtures.dir, 'plain-binary.xml', encoder.encode(signed));
		expect(xmlsec1VerifyFromDir(path, join(fixtures.dir, 'leaf.pem'))).toBe('OK');
		verifySignedInfo(signed, 'plain-binary');
	});

	it('fails detached verification once the external bytes change', async () => {
		writeFixtureFile(fixtures.dir, 'mutable-data.xml', encoder.encode(SAMPLE));
		const signed = await signXmldsig({
			certificates: session.certificates,
			signer: session.signer,
			ids: IDS,
			reference: { type: 'detached', uri: 'mutable-data.xml', data: encoder.encode(SAMPLE) }
		});
		const path = writeFixtureFile(fixtures.dir, 'plain-mutable.xml', encoder.encode(signed));
		expect(xmlsec1VerifyFromDir(path, join(fixtures.dir, 'leaf.pem'))).toBe('OK');
		writeFixtureFile(fixtures.dir, 'mutable-data.xml', encoder.encode(SAMPLE.replace('100.00', '999.99')));
		expect(xmlsec1VerifyFromDir(path, join(fixtures.dir, 'leaf.pem'))).toBe('FAIL');
	});

	it('uses the same SignedInfo canonicalisation across all reference types', async () => {
		const shapes = [
			await signXmldsig({ xml: SAMPLE, certificates: session.certificates, signer: session.signer, ids: IDS }),
			await signXmldsig({
				xml: SAMPLE,
				certificates: session.certificates,
				signer: session.signer,
				ids: IDS,
				reference: { type: 'enveloping', objectId: 'Object-Test' }
			})
		];
		for (const signed of shapes) {
			const c14n = canonicalize(elements(parse(signed).documentElement, DS, 'SignedInfo')[0], { exclusive: true });
			expect(new TextDecoder().decode(c14n)).toContain('http://www.w3.org/2001/10/xml-exc-c14n#');
			verifySignedInfo(signed, `plain-shared-${shapes.indexOf(signed)}`);
		}
	});
	it('signs SHA-512 with the xmldsig-more RSA URI and xmlsec1 verifies it', async () => {
		const signed = await signXmldsig({
			xml: SAMPLE,
			certificates: session.certificates,
			signer: session.signer,
			ids: IDS,
			hash: 'SHA-512'
		});
		const root = parse(signed).documentElement;
		expect(elements(root, DS, 'SignatureMethod')[0].getAttribute('Algorithm')).toBe(
			'http://www.w3.org/2001/04/xmldsig-more#rsa-sha512'
		);
		const path = writeFixtureFile(fixtures.dir, 'plain-sha512.xml', encoder.encode(signed));
		expect(xmlsec1Verify(path, join(fixtures.dir, 'leaf.pem'))).toBe('OK');
	});
});
