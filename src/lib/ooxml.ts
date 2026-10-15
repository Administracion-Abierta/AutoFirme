/**
 * OOXML (docx/xlsx/pptx) digital signatures: ECMA-376 Part 2 §10 package signature part
 * (`_xmlsignatures/sig-<uuid>.xml`) carrying an XAdES v1.3.2 EPES `ds:Signature`, with the MDSSI
 * `SignatureTime` property and an MDSSI `Manifest` of part and relationship references.
 *
 * Reference: `decompiled/sources/es/gob/afirma/signers/ooxml/` (tag `reference/decompile-1.9`).
 * Part names, relationship types,
 * content types and the relationship transform are mirrored; the code is new TypeScript.
 */
import { unzipSync, zipSync } from 'fflate';
import { canonicalizeBytes, canonicalizeToString } from './c14n';

const RELATIONSHIPS_NS = 'http://schemas.openxmlformats.org/package/2006/relationships';
const CONTENT_TYPES_NS = 'http://schemas.openxmlformats.org/package/2006/content-types';
const MDSSI_NS = 'http://schemas.openxmlformats.org/package/2006/digital-signature';
const DS_NS = 'http://www.w3.org/2000/09/xmldsig#';
const XADES_NS = 'http://uri.etsi.org/01903/v1.3.2#';
const C14N = 'http://www.w3.org/TR/2001/REC-xml-c14n-20010315';
const SHA256 = 'http://www.w3.org/2001/04/xmlenc#sha256';
const SHA512 = 'http://www.w3.org/2001/04/xmlenc#sha512';
const XADES_SIGNED_PROPERTIES_TYPE = 'http://uri.etsi.org/01903#SignedProperties';
const SIGNATURE_REL_TYPE = `${RELATIONSHIPS_NS}/digital-signature/signature`;
const ORIGIN_REL_TYPE = `${RELATIONSHIPS_NS}/digital-signature/origin`;
const SIGNATURE_CONTENT_TYPE = 'application/vnd.openxmlformats-package.digital-signature-xmlsignature+xml';
const ORIGIN_CONTENT_TYPE = 'application/vnd.openxmlformats-package.digital-signature-origin';
const PACKAGE_RELATIONSHIPS_CONTENT_TYPE = 'application/vnd.openxmlformats-package.relationships+xml';
const XML_DECLARATION = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>\r\n';
const RELATIONSHIP_TRANSFORM = 'http://schemas.openxmlformats.org/package/2006/RelationshipTransform';
const SIGNATURE_PART_DIR = '_xmlsignatures/';

/** Relationships never covered by the manifest (ECMA-376 §10.1.2.2 volatile properties). */
const EXCLUDED_RELATIONSHIPS: Record<string, true> = {
	'http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties': true,
	'http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties': true,
	[ORIGIN_REL_TYPE]: true,
	'http://schemas.openxmlformats.org/package/2006/relationships/metadata/thumbnail': true,
	'http://schemas.openxmlformats.org/officeDocument/2006/relationships/presProps': true,
	'http://schemas.openxmlformats.org/officeDocument/2006/relationships/viewProps': true
};

/** Document-part directories that carry signed content. */
const CONTENT_DIRS = ['word', 'excel', 'xl', 'powerpoint'];

const OID_NAMES: Record<string, string> = {
	'2.5.4.3': 'CN',
	'2.5.4.4': 'SN',
	'2.5.4.5': 'serialNumber',
	'2.5.4.6': 'C',
	'2.5.4.7': 'L',
	'2.5.4.8': 'ST',
	'2.5.4.9': 'STREET',
	'2.5.4.10': 'O',
	'2.5.4.11': 'OU',
	'2.5.4.12': 'T',
	'2.5.4.17': 'postalCode',
	'2.5.4.42': 'GN',
	'0.9.2342.19200300.100.1.25': 'DC',
	'1.2.840.113549.1.9.1': 'emailAddress'
};

export interface OoxmlSignOptions {
	/** RSA signing key (WebCrypto, PKCS#1 v1.5 over SHA-256). */
	key: CryptoKey;
	/** DER certificates, leaf first. */
	certificates: Uint8Array[];
	/** XAdES `SigningTime`; defaults to now. */
	signingTime?: Date;
}

interface DerNode {
	tag: number;
	content: Uint8Array;
	/** Offset just past this node, for sibling iteration. */
	next: number;
}

function readDer(bytes: Uint8Array, offset: number): DerNode {
	const tag = bytes[offset];
	let length = bytes[offset + 1];
	let cursor = offset + 2;
	if (length & 0x80) {
		const count = length & 0x7f;
		length = 0;
		for (let i = 0; i < count; i += 1) length = length * 256 + bytes[cursor + i];
		cursor += count;
	}
	return { tag, content: bytes.subarray(cursor, cursor + length), next: cursor + length };
}

function derChildren(bytes: Uint8Array): DerNode[] {
	const out: DerNode[] = [];
	let cursor = 0;
	while (cursor < bytes.length) {
		const node = readDer(bytes, cursor);
		out.push(node);
		cursor = node.next;
	}
	return out;
}

function derOid(bytes: Uint8Array): string {
	const parts = [Math.floor(bytes[0] / 40), bytes[0] % 40];
	let value = 0;
	for (const byte of bytes.subarray(1)) {
		value = value * 128 + (byte & 0x7f);
		if ((byte & 0x80) === 0) {
			parts.push(value);
			value = 0;
		}
	}
	return parts.join('.');
}

function decodeDirectoryString(bytes: Uint8Array): string {
	if (bytes.length >= 2 && bytes[0] === 0x1e) {
		let out = '';
		for (let i = 2; i + 1 < bytes.length; i += 2) out += String.fromCharCode((bytes[i] << 8) | bytes[i + 1]);
		return out;
	}
	return new TextDecoder().decode(bytes);
}

function escapeXml(value: string): string {
	return value.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

function escapeRfc4514(value: string, leading = false): string {
	const escaped = value.replace(/([",+<>;\\])/g, '\\$1');
	return leading && escaped.startsWith('#') ? `\\${escaped}` : escaped;
}

/**
 * `X509IssuerName` / `X509SerialNumber` (XAdES `CertID.IssuerSerial`) from a DER certificate.
 * Only the TBSCertificate prefix is interpreted: version, serial, signature algorithm, issuer.
 */
export function certificateIdentity(der: Uint8Array): { issuer: string; serial: string } {
	const tbs = derChildren(readDer(der, 0).content)[0];
	const fields = derChildren(tbs.content);
	let index = fields[0].tag === 0xa0 ? 1 : 0;
	const serial = fields[index];
	const issuer = derChildren(fields[index + 2].content)
		.map((rdn) =>
			derChildren(rdn.content)
				.map((attribute) => {
					const parts = derChildren(attribute.content);
					const oid = derOid(parts[0].content);
					return `${OID_NAMES[oid] ?? oid}=${escapeRfc4514(decodeDirectoryString(parts[1].content), true)}`;
				})
				.join('+')
		)
		.reverse()
		.join(',');
	let serialNumber = 0n;
	for (const byte of serial.content) serialNumber = serialNumber * 256n + BigInt(byte);
	return { issuer, serial: serialNumber.toString() };
}

function toBase64(bytes: Uint8Array): string {
	let binary = '';
	for (let i = 0; i < bytes.length; i += 0x8000) binary += String.fromCharCode(...bytes.subarray(i, i + 0x8000));
	return btoa(binary);
}

function parseXml(text: string): Document {
	const doc = new DOMParser().parseFromString(text, 'application/xml');
	if (doc.querySelector('parsererror')) throw new Error('documento XML invalido');
	return doc;
}

function serializeXml(doc: Document): Uint8Array {
	return new TextEncoder().encode(XML_DECLARATION + new XMLSerializer().serializeToString(doc.documentElement));
}

async function digest(algorithm: 'SHA-256' | 'SHA-512', bytes: Uint8Array): Promise<string> {
	return toBase64(new Uint8Array(await crypto.subtle.digest(algorithm, bytes as unknown as ArrayBuffer)));
}

function elementById(root: Element, id: string): Element | null {
	for (const element of Array.from(root.getElementsByTagName('*'))) {
		if (element.getAttribute('Id') === id) return element;
	}
	return null;
}

/** Part content type, resolved through `[Content_Types].xml` defaults and overrides. */
function contentTypes(doc: Document): (partName: string) => string | undefined {
	const defaults: Record<string, string> = {};
	const overrides: Record<string, string> = {};
	for (const node of Array.from(doc.documentElement.children)) {
		const value = node.getAttribute('ContentType') ?? '';
		if (node.tagName === 'Default') defaults[(node.getAttribute('Extension') ?? '').toLowerCase()] = value;
		if (node.tagName === 'Override') overrides[node.getAttribute('PartName') ?? ''] = value;
	}
	return (partName) => overrides[`/${partName}`] ?? defaults[partName.split('.').pop()?.toLowerCase() ?? ''];
}

function relationshipElements(relsXml: Uint8Array): Element[] {
	return Array.from(parseXml(new TextDecoder().decode(relsXml)).documentElement.children).filter(
		(node) => node.tagName === 'Relationship'
	) as Element[];
}

/** Relationship ids covered by the manifest: every relationship whose type is not excluded. */
function signedRelationshipIds(relsXml: Uint8Array): string[] {
	return relationshipElements(relsXml)
		.filter((node) => !EXCLUDED_RELATIONSHIPS[node.getAttribute('Type') ?? ''])
		.map((node) => node.getAttribute('Id') ?? '');
}

/**
 * MDSSI relationship transform (ECMA-376 §10.1.2.2): keep the relationships listed as
 * `SourceId`, add `TargetMode="Internal"` where missing, sort by `Id`.
 */
export function relationshipTransformOctets(relsXml: Uint8Array, sourceIds: string[]): string {
	const kept = relationshipElements(relsXml)
		.map((node) => ({ id: node.getAttribute('Id') ?? '', node }))
		.filter(({ id }) => sourceIds.includes(id))
		.sort((a, b) => (a.id < b.id ? -1 : a.id > b.id ? 1 : 0));
	const doc = parseXml(`<Relationships xmlns="${RELATIONSHIPS_NS}"/>`);
	for (const { node } of kept) {
		const clone = doc.importNode(node, true) as Element;
		if (!clone.hasAttribute('TargetMode')) clone.setAttribute('TargetMode', 'Internal');
		doc.documentElement.appendChild(clone);
	}
	return canonicalizeToString(doc.documentElement);
}

function manifestReferences(entries: Record<string, Uint8Array>, types: (partName: string) => string | undefined, digestValues: Record<string, string>): string {
	const references: string[] = [];
	for (const name of Object.keys(entries).sort()) {
		if (!name.endsWith('.rels') || name.startsWith(SIGNATURE_PART_DIR)) continue;
		references.push(
			`<ds:Reference URI="/${name}?ContentType=${PACKAGE_RELATIONSHIPS_CONTENT_TYPE}"><ds:Transforms>` +
				`<ds:Transform Algorithm="${RELATIONSHIP_TRANSFORM}">` +
				signedRelationshipIds(entries[name])
					.map((id) => `<mdssi:RelationshipReference xmlns:mdssi="${MDSSI_NS}" SourceId="${id}"/>`)
					.join('') +
				`</ds:Transform><ds:Transform Algorithm="${C14N}"/></ds:Transforms>` +
				`<ds:DigestMethod Algorithm="${SHA256}"/><ds:DigestValue>${digestValues[name]}</ds:DigestValue></ds:Reference>`
		);
	}
	for (const name of Object.keys(entries).sort()) {
		if (name.endsWith('.rels')) continue; // already covered by the relationship references above
		const contentType = CONTENT_DIRS.some((dir) => name.startsWith(dir)) ? types(name) : undefined;
		if (!contentType) continue;
		references.push(
			`<ds:Reference URI="/${name}?ContentType=${contentType}">` +
				`<ds:DigestMethod Algorithm="${SHA256}"/><ds:DigestValue>${digestValues[name]}</ds:DigestValue></ds:Reference>`
		);
	}
	return references.join('');
}

/** Sign an OOXML package and return a new package carrying the signature part. */
export async function signOoxmlPackage(packageBytes: Uint8Array, options: OoxmlSignOptions): Promise<Uint8Array> {
	const entries: Record<string, Uint8Array> = { ...unzipSync(packageBytes) };
	const uuid = crypto.randomUUID();
	const signatureName = `_xmlsignatures/sig-${uuid}.xml`;
	const signatureId = `Signature-${uuid}`;
	const signedPropertiesId = `SignedProperties-${uuid}`;
	const keyInfoId = `KeyInfo-${uuid}`;
	const signingTime = (options.signingTime ?? new Date()).toISOString().replace(/\.\d{3}Z$/, 'Z');

	const contentTypesDoc = parseXml(new TextDecoder().decode(entries['[Content_Types].xml']));
	if (!Array.from(contentTypesDoc.documentElement.children).some((node) => node.getAttribute('Extension') === 'sigs')) {
		const defaults = contentTypesDoc.createElementNS(CONTENT_TYPES_NS, 'Default');
		defaults.setAttribute('Extension', 'sigs');
		defaults.setAttribute('ContentType', ORIGIN_CONTENT_TYPE);
		contentTypesDoc.documentElement.appendChild(defaults);
	}
	const override = contentTypesDoc.createElementNS(CONTENT_TYPES_NS, 'Override');
	override.setAttribute('PartName', `/${signatureName}`);
	override.setAttribute('ContentType', SIGNATURE_CONTENT_TYPE);
	contentTypesDoc.documentElement.appendChild(override);
	entries['[Content_Types].xml'] = serializeXml(contentTypesDoc);

	const relsDoc = parseXml(new TextDecoder().decode(entries['_rels/.rels']));
	if (!Array.from(relsDoc.documentElement.children).some((node) => node.getAttribute('Type') === ORIGIN_REL_TYPE)) {
		const origin = relsDoc.createElementNS(RELATIONSHIPS_NS, 'Relationship');
		origin.setAttribute('Id', `rel-id-${crypto.randomUUID()}`);
		origin.setAttribute('Type', ORIGIN_REL_TYPE);
		origin.setAttribute('Target', '_xmlsignatures/origin.sigs');
		relsDoc.documentElement.appendChild(origin);
	}
	entries['_rels/.rels'] = serializeXml(relsDoc);

	const originRelsName = '_xmlsignatures/_rels/origin.sigs.rels';
	const originRelsDoc = entries[originRelsName]
		? parseXml(new TextDecoder().decode(entries[originRelsName]))
		: parseXml(`<Relationships xmlns="${RELATIONSHIPS_NS}"/>`);
	const signatureRel = originRelsDoc.createElementNS(RELATIONSHIPS_NS, 'Relationship');
	signatureRel.setAttribute('Id', `rel-${crypto.randomUUID()}`);
	signatureRel.setAttribute('Type', SIGNATURE_REL_TYPE);
	signatureRel.setAttribute('Target', signatureName.replace('_xmlsignatures/', ''));
	originRelsDoc.documentElement.appendChild(signatureRel);
	entries[originRelsName] = serializeXml(originRelsDoc);
	if (!entries['_xmlsignatures/origin.sigs']) entries['_xmlsignatures/origin.sigs'] = new Uint8Array(0);

	// Manifest digests: relationship octets are transformed + canonicalized first, parts are raw.
	const types = contentTypes(parseXml(new TextDecoder().decode(entries['[Content_Types].xml'])));
	const digestValues: Record<string, string> = {};
	for (const name of Object.keys(entries)) {
		if (name.endsWith('.rels')) {
			const octets = new TextEncoder().encode(relationshipTransformOctets(entries[name], signedRelationshipIds(entries[name])));
			digestValues[name] = await digest('SHA-256', octets);
		} else if (CONTENT_DIRS.some((dir) => name.startsWith(dir))) {
			digestValues[name] = await digest('SHA-256', entries[name]);
		}
	}

	const identity = certificateIdentity(options.certificates[0]);
	const xadesObject = parseXml(
		`<ds:Object xmlns:ds="${DS_NS}" Id="QualifyingProperties-${uuid}">` +
			`<xades:QualifyingProperties xmlns:xades="${XADES_NS}" Target="#${signatureId}">` +
			`<xades:SignedProperties Id="${signedPropertiesId}"><xades:SignedSignatureProperties>` +
			`<xades:SigningTime>${signingTime}</xades:SigningTime>` +
			`<xades:SigningCertificate><xades:Cert><xades:CertDigest>` +
			`<ds:DigestMethod Algorithm="${SHA256}"/><ds:DigestValue>${await digest('SHA-256', options.certificates[0])}</ds:DigestValue>` +
			`</xades:CertDigest><xades:IssuerSerial><ds:X509IssuerName>${escapeXml(identity.issuer)}</ds:X509IssuerName>` +
			`<ds:X509SerialNumber>${identity.serial}</ds:X509SerialNumber></xades:IssuerSerial></xades:Cert></xades:SigningCertificate>` +
			`<xades:SignaturePolicyIdentifier><xades:SignaturePolicyImplied/></xades:SignaturePolicyIdentifier>` +
			`</xades:SignedSignatureProperties></xades:SignedProperties></xades:QualifyingProperties></ds:Object>`
	);
	// X509Data only: a `ds:KeyValue` would force the signing key to be extractable to read its
	// public modulus. Verifiers take the key from the certificate anyway.
	const keyInfoDoc = parseXml(
		`<ds:KeyInfo xmlns:ds="${DS_NS}" Id="${keyInfoId}"><ds:X509Data>` +
			options.certificates.map((cert) => `<ds:X509Certificate>${toBase64(cert)}</ds:X509Certificate>`).join('') +
			`</ds:X509Data></ds:KeyInfo>`
	);
	const packageObject = parseXml(
		`<ds:Object xmlns:ds="${DS_NS}" Id="idPackageObject"><ds:Manifest Id="Manifest-${uuid}">` +
			manifestReferences(entries, types, digestValues) +
			`</ds:Manifest><ds:SignatureProperties Id="SignatureProperties-${uuid}">` +
			`<ds:SignatureProperty Id="idSignatureTime" Target="#${signatureId}">` +
			`<mdssi:SignatureTime xmlns:mdssi="${MDSSI_NS}"><mdssi:Format>YYYY-MM-DDThh:mm:ssTZD</mdssi:Format>` +
			`<mdssi:Value>${signingTime}</mdssi:Value></mdssi:SignatureTime></ds:SignatureProperty></ds:SignatureProperties></ds:Object>`
	);

	const document = parseXml(
		`<ds:Signature xmlns:ds="${DS_NS}" Id="${signatureId}"><ds:SignedInfo>` +
			`<ds:CanonicalizationMethod Algorithm="${C14N}"/>` +
			`<ds:SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"/>` +
			`<ds:Reference Id="Reference-${uuid}" URI="#idPackageObject">` +
			`<ds:DigestMethod Algorithm="${SHA512}"/><ds:DigestValue/></ds:Reference>` +
			`<ds:Reference Id="Reference-${uuid}-sp" URI="#${signedPropertiesId}" Type="${XADES_SIGNED_PROPERTIES_TYPE}">` +
			`<ds:Transforms><ds:Transform Algorithm="${C14N}"/></ds:Transforms>` +
			`<ds:DigestMethod Algorithm="${SHA256}"/><ds:DigestValue/></ds:Reference>` +
			`<ds:Reference Id="Reference-${uuid}-ki" URI="#${keyInfoId}">` +
			`<ds:DigestMethod Algorithm="${SHA256}"/><ds:DigestValue/></ds:Reference>` +
			`</ds:SignedInfo><ds:SignatureValue Id="SignatureValue-${uuid}"/></ds:Signature>`
	);
	const root = document.documentElement;
	for (const part of [keyInfoDoc, packageObject, xadesObject]) root.appendChild(document.importNode(part.documentElement, true));

	// Digests are taken from the nodes that ship, so verifiers recompute exactly these octets.
	const signedInfo = root.getElementsByTagNameNS(DS_NS, 'SignedInfo')[0];
	for (const reference of Array.from(signedInfo.getElementsByTagNameNS(DS_NS, 'Reference'))) {
		const target = elementById(root, (reference.getAttribute('URI') ?? '#').slice(1));
		if (!target) throw new Error(`referencia sin destino: ${reference.getAttribute('URI')}`);
		const algorithm = (reference.getElementsByTagNameNS(DS_NS, 'DigestMethod')[0].getAttribute('Algorithm') ?? SHA256).endsWith('sha512')
			? 'SHA-512'
			: 'SHA-256';
		reference.getElementsByTagNameNS(DS_NS, 'DigestValue')[0].textContent = await digest(algorithm, canonicalizeBytes(target));
	}
	root.getElementsByTagNameNS(DS_NS, 'SignatureValue')[0].textContent = toBase64(
		new Uint8Array(await crypto.subtle.sign({ name: 'RSASSA-PKCS1-v1_5' }, options.key, canonicalizeBytes(signedInfo) as unknown as ArrayBuffer))
	);

	entries[signatureName] = serializeXml(document);
	return zipSync(entries, { level: 6 });
}
