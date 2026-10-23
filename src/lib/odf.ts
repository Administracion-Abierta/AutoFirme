/**
 * ODF (`.odt`/`.ods`/`.odp`) package signatures: the package files are digested
 * (XML parts canonicalized with C14N 1.0, binary parts raw) into a `ds:Signature`
 * stored at `META-INF/documentsignatures.xml`, and `META-INF/manifest.xml` gains
 * the matching file-entry. Layout mirrors
 * `es/gob/afirma/signers/odf/AOODFSigner.java` (tag `reference/decompile-1.9`);
 * the code is new TypeScript.
 *
 * Two deliberate departures from 1.9: SHA-256/`rsa-sha256` instead of SHA-1
 * (deprecated; LibreOffice and DSS accept SHA-256), and an XAdES v1.3.2
 * `QualifyingProperties` object next to 1.9's `dc:date` property so DSS reports
 * XAdES instead of plain XMLDSig.
 */
import { unzipSync, zipSync } from 'fflate';
import type { Zippable } from 'fflate';
import { canonicalizeBytes } from './c14n';
import { certificateIdentity } from './ooxml';

const DS_NS = 'http://www.w3.org/2000/09/xmldsig#';
const XADES_NS = 'http://uri.etsi.org/01903/v1.3.2#';
const MANIFEST_NS = 'urn:oasis:names:tc:opendocument:xmlns:manifest:1.0';
const SIGNATURES_NS = 'urn:oasis:names:tc:opendocument:xmlns:digitalsignature:1.0';
const DC_NS = 'http://purl.org/dc/elements/1.1/';
const C14N = 'http://www.w3.org/TR/2001/REC-xml-c14n-20010315';
const SHA256 = 'http://www.w3.org/2001/04/xmlenc#sha256';
const RSA_SHA256 = 'http://www.w3.org/2001/04/xmldsig-more#rsa-sha256';
const XADES_SIGNED_PROPERTIES_TYPE = 'http://uri.etsi.org/01903#SignedProperties';
const SIGNATURES_PATH = 'META-INF/documentsignatures.xml';
const MANIFEST_PATH = 'META-INF/manifest.xml';
const MIMETYPE_PATH = 'mimetype';
const XML_DECLARATION = '<?xml version="1.0" encoding="UTF-8"?>\n';

/** Manifest entries canonicalized before digesting, like every other XML part. */
const CANONICALIZED_PARTS: Record<string, true> = {
	'content.xml': true,
	'meta.xml': true,
	'styles.xml': true,
	'settings.xml': true,
	[MANIFEST_PATH]: true
};

export interface OdfSignOptions {
	/** RSA signing key (WebCrypto, PKCS#1 v1.5 over SHA-256). */
	key: CryptoKey;
	/** DER certificates, leaf first. */
	certificates: Uint8Array[];
	/** XAdES `SigningTime` / `dc:date`; defaults to now. */
	signingTime?: Date;
}

function parseXml(text: string): Document {
	const doc = new DOMParser().parseFromString(text, 'application/xml');
	if (doc.getElementsByTagName('parsererror').length > 0) throw new Error('ODF: invalid XML part');
	return doc;
}

/**
 * Serialize a parsed part back to UTF-8 with a fixed declaration: the DOM round-trip may not
 * reproduce the original declaration/encoding byte-for-byte. Harmless here because the part is
 * digested over its canonicalized element (C14N drops the declaration) and the signature file
 * itself is never digested as a whole — but the bytes we write, not the bytes we read, are what
 * verifiers see.
 */
function serializeXml(doc: Document): Uint8Array {
	return new TextEncoder().encode(XML_DECLARATION + new XMLSerializer().serializeToString(doc.documentElement));
}

async function digest(bytes: Uint8Array): Promise<string> {
	return toBase64(new Uint8Array(await crypto.subtle.digest('SHA-256', bytes as unknown as ArrayBuffer)));
}

function toBase64(bytes: Uint8Array): string {
	let binary = '';
	for (const byte of bytes) binary += String.fromCharCode(byte);
	return btoa(binary);
}

function escapeXml(value: string): string {
	return value.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

/** Digest octets of one package file: canonicalized XML or raw bytes. */
function partOctets(name: string, bytes: Uint8Array): Uint8Array {
	if (!CANONICALIZED_PARTS[name]) return bytes;
	const doc = parseXml(new TextDecoder().decode(bytes));
	return canonicalizeBytes(doc.documentElement);
}

/** Sign an ODF package and return a new package carrying the signature. */
export async function signOdfPackage(packageBytes: Uint8Array, options: OdfSignOptions): Promise<Uint8Array> {
	const { key, certificates } = options;
	if (certificates.length === 0) throw new Error('ODF: no certificates to sign with');
	const entries: Record<string, Uint8Array> = { ...unzipSync(packageBytes) };
	if (!entries[MIMETYPE_PATH]) throw new Error('ODF: package has no mimetype entry');
	if (!entries[MANIFEST_PATH]) throw new Error('ODF: package has no META-INF/manifest.xml');
	const signingTime = (options.signingTime ?? new Date()).toISOString().replace(/\.\d{3}Z$/, 'Z');
	const uuid = crypto.randomUUID();
	const signatureId = `Signature-${uuid}`;
	const signedPropertiesId = `SignedProperties-${uuid}`;
	const keyInfoId = `KeyInfo-${uuid}`;
	const propertyId = `SignatureProperty-${uuid}`;

	// Package references: every file-entry that is not a directory, skipping the
	// signature file itself (not yet written — its digest would be circular).
	const manifest = parseXml(new TextDecoder().decode(entries[MANIFEST_PATH]));
	const fullPaths = Array.from(manifest.getElementsByTagNameNS(MANIFEST_NS, 'file-entry'))
		.map((entry) => entry.getAttribute('manifest:full-path') ?? '')
		.filter((path) => path !== '' && !path.endsWith('/') && path !== SIGNATURES_PATH);
	if (!fullPaths.includes(MIMETYPE_PATH)) {
		const entry = manifest.createElementNS(MANIFEST_NS, 'manifest:file-entry');
		entry.setAttribute('manifest:full-path', MIMETYPE_PATH);
		entry.setAttribute('manifest:media-type', new TextDecoder().decode(entries[MIMETYPE_PATH]));
		manifest.documentElement.appendChild(entry);
		fullPaths.unshift(MIMETYPE_PATH);
	}

	const digestValues: Record<string, string> = {};
	for (const path of fullPaths) {
		if (path === MANIFEST_PATH) continue; // digested after its entries are added below
		const bytes = entries[path];
		if (!bytes) throw new Error(`ODF: manifest lists missing file ${path}`);
		digestValues[path] = await digest(partOctets(path, bytes));
	}
	// The manifest itself is digested after serialization with its updated entries,
	// so record its file-entry additions first, then digest what ships.
	const root = entries[SIGNATURES_PATH]
		? parseXml(new TextDecoder().decode(entries[SIGNATURES_PATH])).documentElement
		: null;
	if (!root) {
		for (const [path, mediaType] of [
			[SIGNATURES_PATH, ''],
			['META-INF/', '']
		] as const) {
			if (
				!Array.from(manifest.getElementsByTagNameNS(MANIFEST_NS, 'file-entry')).some(
					(entry) => entry.getAttribute('manifest:full-path') === path
				)
			) {
				const entry = manifest.createElementNS(MANIFEST_NS, 'manifest:file-entry');
				entry.setAttribute('manifest:full-path', path);
				entry.setAttribute('manifest:media-type', mediaType);
				manifest.documentElement.appendChild(entry);
			}
		}
	}
	entries[MANIFEST_PATH] = serializeXml(manifest);
	if (!fullPaths.includes(MANIFEST_PATH)) fullPaths.push(MANIFEST_PATH);
	digestValues[MANIFEST_PATH] = await digest(partOctets(MANIFEST_PATH, entries[MANIFEST_PATH]));

	const identity = certificateIdentity(certificates[0]);
	const references = fullPaths
		.map((path) => {
			// XML parts are digested canonicalized, so the reference must declare the
			// transform a verifier has to apply; binary parts (mimetype) are raw, no transform.
			const transforms = CANONICALIZED_PARTS[path]
				? `<ds:Transforms><ds:Transform Algorithm="${C14N}"/></ds:Transforms>`
				: '';
			return (
				`<ds:Reference URI="${escapeXml(path.replace(/ /g, '%20'))}">` +
				transforms +
				`<ds:DigestMethod Algorithm="${SHA256}"/>` +
				`<ds:DigestValue>${digestValues[path]}</ds:DigestValue></ds:Reference>`
			);
		})
		.join('');
	// The dc:date property object: 1.9's only `ds:Object`, kept so 1.9-era readers still find the
	// signing date; it is bound by the SignedInfo reference to its Id, so it is covered by the RSA
	// signature. (Whether a given verifier consumes it is not verified here.)
	const dateObject =
		`<ds:Object><ds:SignatureProperties><ds:SignatureProperty Id="${propertyId}" Target="#${signatureId}">` +
		`<dc:date xmlns:dc="${DC_NS}">${signingTime}</dc:date></ds:SignatureProperty></ds:SignatureProperties></ds:Object>`;
	const xadesObject =
		`<ds:Object><xades:QualifyingProperties xmlns:xades="${XADES_NS}" Target="#${signatureId}">` +
		`<xades:SignedProperties Id="${signedPropertiesId}"><xades:SignedSignatureProperties>` +
		`<xades:SigningTime>${signingTime}</xades:SigningTime>` +
		`<xades:SigningCertificate><xades:Cert><xades:CertDigest>` +
		`<ds:DigestMethod Algorithm="${SHA256}"/><ds:DigestValue>${await digest(certificates[0])}</ds:DigestValue>` +
		`</xades:CertDigest><xades:IssuerSerial><ds:X509IssuerName>${escapeXml(identity.issuer)}</ds:X509IssuerName>` +
		`<ds:X509SerialNumber>${identity.serial}</ds:X509SerialNumber></xades:IssuerSerial></xades:Cert></xades:SigningCertificate>` +
		`<xades:SignaturePolicyIdentifier><xades:SignaturePolicyImplied/></xades:SignaturePolicyIdentifier>` +
		`</xades:SignedSignatureProperties></xades:SignedProperties></xades:QualifyingProperties></ds:Object>`;

	const document = parseXml(
		`<ds:Signature xmlns:ds="${DS_NS}" Id="${signatureId}"><ds:SignedInfo>` +
			`<ds:CanonicalizationMethod Algorithm="${C14N}"/>` +
			`<ds:SignatureMethod Algorithm="${RSA_SHA256}"/>` +
			references +
			`<ds:Reference URI="#${propertyId}"><ds:DigestMethod Algorithm="${SHA256}"/><ds:DigestValue/></ds:Reference>` +
			`<ds:Reference URI="#${signedPropertiesId}" Type="${XADES_SIGNED_PROPERTIES_TYPE}">` +
			`<ds:Transforms><ds:Transform Algorithm="${C14N}"/></ds:Transforms>` +
			`<ds:DigestMethod Algorithm="${SHA256}"/><ds:DigestValue/></ds:Reference>` +
			`</ds:SignedInfo><ds:SignatureValue Id="SignatureValue-${uuid}"/>` +
			`<ds:KeyInfo Id="${keyInfoId}"><ds:X509Data>` +
			certificates.map((cert) => `<ds:X509Certificate>${toBase64(cert)}</ds:X509Certificate>`).join('') +
			`</ds:X509Data></ds:KeyInfo>` +
			dateObject +
			xadesObject +
			`</ds:Signature>`
	);
	// Import into the final wrapper first: digests and the RSA signature are taken
	// from the nodes that ship, so verifiers recompute exactly these octets.
	const finalDoc = root ? root.ownerDocument : parseXml(`<document-signatures xmlns="${SIGNATURES_NS}"/>`);
	const signatureRoot = finalDoc.importNode(document.documentElement, true) as Element;
	(root ?? finalDoc.documentElement).appendChild(signatureRoot);

	const signedInfo = signatureRoot.getElementsByTagNameNS(DS_NS, 'SignedInfo')[0];
	for (const reference of Array.from(signedInfo.getElementsByTagNameNS(DS_NS, 'Reference'))) {
		const uri = reference.getAttribute('URI') ?? '';
		if (!uri.startsWith('#')) continue; // package refs carry precomputed digests
		const id = uri.slice(1);
		const target = Array.from(signatureRoot.getElementsByTagName('*')).find(
			(node) => node.getAttribute('Id') === id
		);
		if (!target) throw new Error(`ODF: signature reference without target: ${uri}`);
		reference.getElementsByTagNameNS(DS_NS, 'DigestValue')[0].textContent = await digest(
			canonicalizeBytes(target)
		);
	}
	signatureRoot.getElementsByTagNameNS(DS_NS, 'SignatureValue')[0].textContent = toBase64(
		new Uint8Array(
			await crypto.subtle.sign(
				{ name: 'RSASSA-PKCS1-v1_5' },
				key,
				canonicalizeBytes(signedInfo) as unknown as ArrayBuffer
			)
		)
	);
	entries[SIGNATURES_PATH] = serializeXml(finalDoc);

	// ODF §2: `mimetype` is first and STORED; every other entry may be deflated.
	const ordered: Zippable = {
		[MIMETYPE_PATH]: [entries[MIMETYPE_PATH], { level: 0 }]
	};
	for (const name of Object.keys(entries)) {
		if (name !== MIMETYPE_PATH) ordered[name] = entries[name];
	}
	return zipSync(ordered, { level: 6 });
}
