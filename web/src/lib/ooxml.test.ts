/**
 * OOXML signing: the docx/xlsx fixtures are generated here, and the produced package is re-read so
 * every reference digest plus the RSA signature is recomputed independently. `openssl` and
 * `xmllint` are used as second opinions when present (certificate identity, raw `SignatureValue`,
 * canonical form).
 */
import { execFileSync } from 'node:child_process';
import { mkdirSync, mkdtempSync, readFileSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { pathToFileURL } from 'node:url';
import { webcrypto } from 'node:crypto';
import { beforeAll, describe, expect, it } from 'vitest';
import { unzipSync, zipSync } from 'fflate';
import { canonicalizeBytes, canonicalizeToString } from './c14n';
import { certificateIdentity, relationshipTransformOctets, signOoxmlPackage } from './ooxml';

if (!globalThis.crypto?.subtle) Object.defineProperty(globalThis, 'crypto', { value: webcrypto });

const DS_NS = 'http://www.w3.org/2000/09/xmldsig#';
const MDSSI_NS = 'http://schemas.openxmlformats.org/package/2006/digital-signature';
const XADES_NS = 'http://uri.etsi.org/01903/v1.3.2#';
const RELATIONSHIPS_NS = 'http://schemas.openxmlformats.org/package/2006/relationships';
const DECODER = new TextDecoder();
const ENCODER = new TextEncoder();
const artifacts = mkdtempSync(join(tmpdir(), 'ooxml-'));

function has(binary: string): boolean {
	try {
		execFileSync(binary, ['--version'], { stdio: 'ignore' });
		return true;
	} catch {
		return false;
	}
}

const hasOpenssl = has('openssl');
const hasXmllint = has('xmllint');
const hasSoffice = has('soffice');

let signingKey: CryptoKey;
let publicKey: CryptoKey;
let certificates: Uint8Array[];
let certPath: string;
let keyPath: string;
let publicPath: string;

function fromBase64(value: string): Uint8Array {
	return Uint8Array.from(atob(value), (char) => char.charCodeAt(0));
}

function toBase64(bytes: Uint8Array): string {
	let binary = '';
	for (let i = 0; i < bytes.length; i += 0x8000) binary += String.fromCharCode(...bytes.subarray(i, i + 0x8000));
	return btoa(binary);
}

function pemToDer(pem: string): Uint8Array {
	return fromBase64(pem.replace(/-----[^-]+-----/g, '').replace(/\s+/g, ''));
}

function parse(xml: Uint8Array | string): Document {
	return new DOMParser().parseFromString(typeof xml === 'string' ? xml : DECODER.decode(xml), 'application/xml');
}

function elements(root: Document | Element, namespace: string, local: string): Element[] {
	return Array.from(root.getElementsByTagNameNS(namespace, local));
}

function elementById(root: Element, id: string): Element | null {
	for (const element of Array.from(root.getElementsByTagName('*'))) if (element.getAttribute('Id') === id) return element;
	return null;
}

function digestValue(element: Element): string {
	return element.getElementsByTagNameNS(DS_NS, 'DigestValue')[0].textContent ?? '';
}

async function sha(algorithm: 'SHA-256' | 'SHA-512', bytes: Uint8Array): Promise<string> {
	return toBase64(new Uint8Array(await crypto.subtle.digest(algorithm, bytes as unknown as ArrayBuffer)));
}

/** Minimal but valid OOXML package: content types, package rels, main part, part rels, docProps. */
function ooxmlFixture(kind: 'docx' | 'xlsx'): Record<string, Uint8Array> {
	const partName = kind === 'docx' ? 'word/document.xml' : 'xl/workbook.xml';
	const partDir = kind === 'docx' ? 'word' : 'xl';
	const relsName = `${partDir}/_rels/${kind === 'docx' ? 'document' : 'workbook'}.xml.rels`;
	const mainContentType =
		kind === 'docx'
			? 'application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml'
			: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml';
	const body =
		kind === 'docx'
			? '<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">' +
				'<w:body><w:p><w:r><w:t>AutoFirma Web</w:t></w:r></w:p></w:body></w:document>'
			: '<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"><sheets>' +
				'<sheet name="Hoja1" sheetId="1"/></sheets></workbook>';
	const xml = (content: string) => ENCODER.encode(content);
	return {
		'[Content_Types].xml': xml(
			'<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">' +
				'<Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>' +
				'<Default Extension="xml" ContentType="application/xml"/>' +
				`<Override PartName="/${partName}" ContentType="${mainContentType}"/>` +
				'<Override PartName="/docProps/core.xml" ContentType="application/vnd.openxmlformats-package.core-properties+xml"/>' +
				'</Types>'
		),
		'_rels/.rels': xml(
			`<Relationships xmlns="${RELATIONSHIPS_NS}">` +
				'<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" ' +
				`Target="${partName}"/>` +
				'<Relationship Id="rId2" Type="http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties" ' +
				'Target="docProps/core.xml"/></Relationships>'
		),
		[partName]: xml(body),
		[relsName]: xml(
			`<Relationships xmlns="${RELATIONSHIPS_NS}">` +
				'<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" ' +
				'Target="styles.xml"/></Relationships>'
		),
		[`${partDir}/styles.xml`]: xml(`<styles xmlns="urn:fixture:${kind}"/>`),
		'docProps/core.xml': xml('<cp:coreProperties xmlns:cp="urn:fixture:core"/>')
	};
}

interface Signed {
	entries: Record<string, Uint8Array>;
	document: Document;
	signature: Element;
	signatureName: string;
}

function readSignedPackage(bytes: Uint8Array): Signed {
	const entries = unzipSync(bytes);
	const signatureName = Object.keys(entries).find((name) => /^_xmlsignatures\/sig-[\w-]+\.xml$/.test(name));
	if (!signatureName) throw new Error('paquete sin parte de firma');
	const document = parse(entries[signatureName]);
	return { entries, document, signature: document.documentElement, signatureName };
}

/** Recompute every manifest reference: raw parts, and relationship transform + c14n octets. */
async function manifestFailures(signed: Signed): Promise<string[]> {
	const failures: string[] = [];
	const manifest = elements(signed.signature, DS_NS, 'Manifest')[0];
	for (const reference of elements(manifest, DS_NS, 'Reference')) {
		const uri = reference.getAttribute('URI') ?? '';
		const name = uri.slice(1).split('?')[0];
		const transform = reference.getElementsByTagNameNS(DS_NS, 'Transform')[0]?.getAttribute('Algorithm');
		const octets =
			transform === 'http://schemas.openxmlformats.org/package/2006/RelationshipTransform'
				? ENCODER.encode(
						relationshipTransformOctets(
							signed.entries[name],
							elements(reference, MDSSI_NS, 'RelationshipReference').map((node) => node.getAttribute('SourceId') ?? '')
						)
					)
				: signed.entries[name];
		if (!octets) failures.push(`${uri}: parte ausente`);
		else if ((await sha('SHA-256', octets)) !== digestValue(reference)) failures.push(`${uri}: digest distinto`);
	}
	return failures;
}

/** Recompute the references to the signature's own objects. */
async function internalFailures(signed: Signed): Promise<string[]> {
	const failures: string[] = [];
	for (const reference of elements(elements(signed.signature, DS_NS, 'SignedInfo')[0], DS_NS, 'Reference')) {
		const uri = reference.getAttribute('URI') ?? '';
		const target = elementById(signed.signature, uri.slice(1));
		if (!target) {
			failures.push(`${uri}: destino ausente`);
			continue;
		}
		const algorithm = (reference.getElementsByTagNameNS(DS_NS, 'DigestMethod')[0].getAttribute('Algorithm') ?? '').endsWith('sha512')
			? 'SHA-512'
			: 'SHA-256';
		if ((await sha(algorithm, canonicalizeBytes(target))) !== digestValue(reference)) failures.push(`${uri}: digest distinto`);
	}
	return failures;
}

async function signatureValueValid(signed: Signed): Promise<boolean> {
	const signedInfo = elements(signed.signature, DS_NS, 'SignedInfo')[0];
	const value = fromBase64(elements(signed.signature, DS_NS, 'SignatureValue')[0].textContent ?? '');
	return crypto.subtle.verify(
		{ name: 'RSASSA-PKCS1-v1_5' },
		publicKey,
		value as unknown as ArrayBuffer,
		canonicalizeBytes(signedInfo) as unknown as ArrayBuffer
	);
}

async function sign(kind: 'docx' | 'xlsx', fixture = ooxmlFixture(kind)): Promise<{ bytes: Uint8Array; signed: Signed; fixture: Record<string, Uint8Array> }> {
	const bytes = await signOoxmlPackage(zipSync(fixture, { level: 6 }), {
		key: signingKey,
		certificates,
		signingTime: new Date('2026-10-14T10:00:00Z')
	});
	writeFileSync(join(artifacts, `signed-${kind}.${kind}`), bytes);
	return { bytes, signed: readSignedPackage(bytes), fixture };
}

beforeAll(async () => {
	keyPath = join(artifacts, 'key.pem');
	certPath = join(artifacts, 'cert.pem');
	publicPath = join(artifacts, 'public.pem');
	execFileSync('openssl', [
		'req', '-x509', '-newkey', 'rsa:2048', '-nodes', '-days', '1', '-sha256',
		'-keyout', keyPath, '-out', certPath,
		'-subj', '/C=ES/O=AutoFirma Web Tests/CN=Firmante de prueba'
	]);
	certificates = [pemToDer(readFileSync(certPath, 'utf8'))];
	signingKey = await crypto.subtle.importKey(
		'pkcs8',
		pemToDer(readFileSync(keyPath, 'utf8')) as unknown as ArrayBuffer,
		{ name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' },
		false,
		['sign']
	);
	execFileSync('openssl', ['rsa', '-in', keyPath, '-pubout', '-out', publicPath]);
	publicKey = await crypto.subtle.importKey(
		'spki',
		pemToDer(readFileSync(publicPath, 'utf8')) as unknown as ArrayBuffer,
		{ name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' },
		false,
		['verify']
	);
});

describe('signOoxmlPackage', () => {
	it.each(['docx', 'xlsx'] as const)('keeps the %s package readable and adds the signature parts', async (kind) => {
		const { signed, fixture } = await sign(kind);

		expect(signed.signatureName).toMatch(/^_xmlsignatures\/sig-[\w-]+\.xml$/);
		expect(signed.entries['_xmlsignatures/origin.sigs']).toHaveLength(0);
		// Everything but the rewritten package relationships and content types must survive intact.
		for (const [name, bytes] of Object.entries(fixture)) {
			if (name === '[Content_Types].xml' || name === '_rels/.rels') continue;
			expect(Array.from(signed.entries[name])).toEqual(Array.from(bytes));
		}
		expect(Array.from(parse(signed.entries['_rels/.rels']).documentElement.children)).toHaveLength(3);

		const originRels = parse(signed.entries['_xmlsignatures/_rels/origin.sigs.rels']).documentElement;
		const signatureRel = Array.from(originRels.children).find((node) =>
			(node.getAttribute('Type') ?? '').endsWith('/digital-signature/signature')
		);
		expect(signatureRel?.getAttribute('Target')).toBe(signed.signatureName.replace('_xmlsignatures/', ''));

		const packageRels = parse(signed.entries['_rels/.rels']).documentElement;
		expect(
			Array.from(packageRels.children).some((node) =>
				(node.getAttribute('Type') ?? '').endsWith('/digital-signature/origin')
			)
		).toBe(true);

		const types = parse(signed.entries['[Content_Types].xml']).documentElement;
		const children = Array.from(types.children);
		expect(children.find((node) => node.getAttribute('PartName') === `/${signed.signatureName}`)?.getAttribute('ContentType')).toBe(
			'application/vnd.openxmlformats-package.digital-signature-xmlsignature+xml'
		);
		expect(children.find((node) => node.getAttribute('Extension') === 'sigs')?.getAttribute('ContentType')).toBe(
			'application/vnd.openxmlformats-package.digital-signature-origin'
		);
	});

	it.each(['docx', 'xlsx'] as const)('covers every signed part of a %s and the digests recompute', async (kind) => {
		const { signed, fixture } = await sign(kind);

		const manifest = elements(signed.signature, DS_NS, 'Manifest')[0];
		const referenced = elements(manifest, DS_NS, 'Reference').map((reference) => (reference.getAttribute('URI') ?? '').slice(1).split('?')[0]);
		expect(new Set(referenced)).toEqual(
			new Set([...Object.keys(fixture).filter((name) => name.endsWith('.rels') || name.startsWith('word') || name.startsWith('xl'))])
		);
		expect(await manifestFailures(signed)).toEqual([]);
		expect(await internalFailures(signed)).toEqual([]);
	});

	it.each(['docx', 'xlsx'] as const)('signs a %s the public key validates', async (kind) => {
		const { signed } = await sign(kind);
		expect(await signatureValueValid(signed)).toBe(true);

		expect(elements(signed.signature, DS_NS, 'X509Certificate')[0].textContent).toBe(toBase64(certificates[0]));
		expect(elements(signed.signature, DS_NS, 'X509SerialNumber')[0].textContent).toBe(certificateIdentity(certificates[0]).serial);
	});

	it('carries XAdES v1.3.2 signed properties with the signing certificate and time', async () => {
		const { signed } = await sign('docx');

		const signedProperties = elements(signed.signature, XADES_NS, 'SignedProperties')[0];
		expect(elements(signedProperties, XADES_NS, 'SigningTime')[0].textContent).toBe('2026-10-14T10:00:00Z');
		expect(elements(signedProperties, XADES_NS, 'SignaturePolicyImplied')).toHaveLength(1);
		expect(elements(signedProperties, DS_NS, 'DigestValue')[0].textContent).toBe(await sha('SHA-256', certificates[0]));
		expect(elements(signedProperties, DS_NS, 'X509IssuerName')[0].textContent).toBe(certificateIdentity(certificates[0]).issuer);

		const mdssi = elements(signed.signature, MDSSI_NS, 'SignatureTime')[0];
		expect(elements(mdssi, MDSSI_NS, 'Format')[0].textContent).toBe('YYYY-MM-DDThh:mm:ssTZD');
		expect(elements(mdssi, MDSSI_NS, 'Value')[0].textContent).toBe('2026-10-14T10:00:00Z');
	});

	it.runIf(hasOpenssl)('matches the certificate identity reported by openssl', async () => {
		const identity = certificateIdentity(certificates[0]);
		const subject = execFileSync('openssl', ['x509', '-in', certPath, '-noout', '-subject', '-nameopt', 'RFC2253'])
			.toString()
			.replace(/^subject=/, '')
			.replace(/,\s*/g, ',')
			.trim();
		const serial = BigInt(`0x${execFileSync('openssl', ['x509', '-in', certPath, '-noout', '-serial']).toString().replace('serial=', '').trim()}`);

		expect(identity.issuer).toBe(subject);
		expect(BigInt(identity.serial)).toBe(serial);
	});

	it.runIf(hasOpenssl)('openSSL validates the raw signature over the canonical SignedInfo', async () => {
		const { signed } = await sign('xlsx');
		const signedInfoPath = join(artifacts, 'signedinfo.bin');
		const signaturePath = join(artifacts, 'signature.bin');
		writeFileSync(signedInfoPath, canonicalizeBytes(elements(signed.signature, DS_NS, 'SignedInfo')[0]));
		writeFileSync(signaturePath, fromBase64(elements(signed.signature, DS_NS, 'SignatureValue')[0].textContent ?? ''));

		const result = execFileSync('openssl', ['dgst', '-sha256', '-verify', publicPath, '-signature', signaturePath, signedInfoPath]).toString();
		expect(result.trim()).toContain('Verified OK');
	});

	it.runIf(hasXmllint)('canonicalizes exactly like xmllint (inclusive and exclusive)', async () => {
		const { signed } = await sign('docx');
		const path = join(artifacts, 'signature.xml');
		writeFileSync(path, DECODER.decode(signed.entries[signed.signatureName]));

		expect(canonicalizeToString(signed.document)).toBe(execFileSync('xmllint', ['--c14n', path], { encoding: 'utf8' }));
		expect(canonicalizeToString(signed.document, true)).toBe(execFileSync('xmllint', ['--exc-c14n', path], { encoding: 'utf8' }));
	});

	it.runIf(hasSoffice)('stays a readable package: LibreOffice converts the signed docx', async () => {
		const { bytes } = await sign('docx');
		const docx = join(artifacts, 'smoke.docx');
		const out = join(artifacts, 'soffice-out');
		mkdirSync(out, { recursive: true });
		writeFileSync(docx, bytes);

		execFileSync(
			'soffice',
			[
				'--headless',
				'--norestore',
				`-env:UserInstallation=${pathToFileURL(join(artifacts, 'soffice-profile')).href}`,
				'--convert-to',
				'pdf',
				docx,
				'--outdir',
				out
			],
			{ timeout: 120_000 }
		);

		const pdf = readFileSync(join(out, 'smoke.pdf'));
		expect(pdf.subarray(0, 5).toString('latin1')).toBe('%PDF-');
		expect(pdf.byteLength).toBeGreaterThan(1000);
	}, 180_000);

	it('refuses a package without content types', async () => {
		await expect(signOoxmlPackage(zipSync({ 'word/document.xml': ENCODER.encode('<w/>') }), { key: signingKey, certificates })).rejects.toThrow();
	});
});
