/**
 * XAdES-EPES signatures (enveloped, enveloping and detached), built in the browser.
 *
 * The `ds:Signature` template carries two references — the data object and the
 * `xades:SignedProperties` — both digested over the *in-document* node-sets (canonicalized
 * with Exclusive C14N), and the signature value is a WebCrypto signature over the
 * canonicalized `ds:SignedInfo` (#5's non-extractable key). `KeyInfo/X509Data` carries the
 * DER chain of the PKCS#12 container, and the XAdES signed properties carry the
 * ES/EN-independent metadata: signing time, the certificate digests, and the signature
 * policy (implied by default — see `XadesCommon.policy`).
 *
 * Reference types: `enveloped` inserts the signature as the last child of the document
 * element (`URI=""`); `enveloping` wraps the document in a `ds:Object` inside a standalone
 * signature (`URI="#object"`); `detached` leaves the data outside the signature and hashes
 * its raw octets (`URI` is the data location — a bare filename for detached, an absolute
 * URI for externally-detached). Detached is the shape for non-XML payloads.
 */

import type { Bytes } from './asn1';
import { EXC_C14N, canonicalize } from './c14n';
import { type SignerCert, type SigningAlgorithm, signBytes } from './sign';
import { requestTimeStamp } from './timestamp';

const DS = 'http://www.w3.org/2000/09/xmldsig#';
const XADES = 'http://uri.etsi.org/01903/v1.3.2#';
const SIGNED_PROPERTIES_TYPE = 'http://uri.etsi.org/01903#SignedProperties';

const DIGEST_ALGORITHM: Record<SigningAlgorithm['hash'], string> = {
	'SHA-1': 'http://www.w3.org/2000/09/xmldsig#sha1',
	'SHA-256': 'http://www.w3.org/2001/04/xmlenc#sha256',
	'SHA-384': 'http://www.w3.org/2001/04/xmldsig-more#sha384',
	'SHA-512': 'http://www.w3.org/2001/04/xmlenc#sha512'
};

const SIGNATURE_ALGORITHM: Record<SigningAlgorithm['name'], Partial<Record<SigningAlgorithm['hash'], string>>> = {
	'RSASSA-PKCS1-v1_5': {
		'SHA-1': 'http://www.w3.org/2000/09/xmldsig#rsa-sha1',
		'SHA-256': 'http://www.w3.org/2001/04/xmldsig-more#rsa-sha256',
		'SHA-384': 'http://www.w3.org/2001/04/xmldsig-more#rsa-sha384',
		'SHA-512': 'http://www.w3.org/2001/04/xmldsig-more#rsa-sha512'
	},
	ECDSA: {
		'SHA-256': 'http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256',
		'SHA-384': 'http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384',
		'SHA-512': 'http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512'
	}
};

interface XadesCommon {
	/** Certificates of the PKCS#12 container (#5), the signer first — all go into the KeyInfo. */
	certificates: readonly SignerCert[];
	/** The certificate owning the private key (#5). Defaults to the first chain entry with a key. */
	signer?: SignerCert;
	/** XAdES `SignerRole` claimed roles (FacturaE: emisor/receptor/tercero/…). */
	claimedRoles?: readonly string[];
	/** EPES signature policy. Defaults to `SignaturePolicyImplied` (nothing is invented). */
	policy?: { identifier: string; description?: string; digest?: Bytes; digestAlgorithm?: SigningAlgorithm['hash'] };
	/** XAdES `DataObjectFormat` of the signed document. */
	dataObject?: { description?: string; mimeType?: string };
	/** Overrides the certificate's default hash (legacy portals ask for SHA1withRSA). The XAdES
	 * certificate digests follow it, as AutoFirma does. */
	hash?: SigningAlgorithm['hash'];
	/** Signing time; defaults to now. */
	now?: Date;
	/**
	 * RFC 3161 TSA URL. When set, the canonicalised `ds:SignatureValue` is timestamped and the
	 * token embedded as `SignatureTimeStamp/EncapsulatedTimeStamp` — the EPES becomes XAdES-T.
	 */
	timeStamp?: string;
	/** Deterministic ids — for golden vectors and reproducible output. */
	ids?: Partial<XadesIds>;
}

export type XadesReference =
	/** Enveloped: the signature is inserted into the document (`URI=""`). The default. */
	| { type: 'enveloped' }
	/** Enveloping: the document is wrapped in a `ds:Object` inside a standalone signature. */
	| { type: 'enveloping'; objectId?: string }
	/**
	 * Detached / externally-detached: the signature is standalone and `uri` locates the data
	 * (a bare filename for detached, an absolute URI for externally-detached). The digest
	 * covers the raw octets — no transforms — so any payload works, XML or not.
	 *
	 * The string itself decides how a verifier resolves the data: `xmlsec1` resolves a relative
	 * `uri` against its process cwd (so a bare filename works), while DSS wants an absolute URI.
	 * Either form verifies with whichever verifier understands it.
	 */
	| { type: 'detached'; uri: string; data: string | Bytes };

export type XadesSignatureOptions = XadesCommon &
	(
		| { reference?: { type: 'enveloped' }; xml: string | Bytes }
		| { reference: { type: 'enveloping'; objectId?: string }; xml: string | Bytes }
		| { reference: { type: 'detached'; uri: string; data: string | Bytes }; xml?: string | Bytes }
	);

export interface XadesIds {
	signature: string;
	signedInfo: string;
	signatureValue: string;
	keyInfo: string;
	signedProperties: string;
	/** Reference to the data object (the enveloped document, the enveloping object, the detached bytes). */
	documentReference: string;
	/** Reference to `xades:SignedProperties`. */
	propertiesReference: string;
	/** Id of the enveloping `ds:Object` (unused by the enveloped and detached shapes). */
	dataObject: string;
}

const idsFor = (overrides: Partial<XadesIds> = {}): XadesIds => {
	const random = [...crypto.getRandomValues(new Uint8Array(8))]
		.map((byte) => byte.toString(16).padStart(2, '0'))
		.join('');
	return {
		signature: `Signature-${random}`,
		signedInfo: `SignedInfo-${random}`,
		signatureValue: `SignatureValue-${random}`,
		keyInfo: `KeyInfo-${random}`,
		signedProperties: `SignedProperties-${random}`,
		documentReference: `Reference-${random}`,
		propertiesReference: `PropertiesReference-${random}`,
		dataObject: `Object-${random}`,
		...overrides
	};
};

function escapeXml(value: string): string {
	return value
		.replace(/&/g, '&amp;')
		.replace(/</g, '&lt;')
		.replace(/>/g, '&gt;')
		.replace(/"/g, '&quot;');
}

function toBase64(bytes: Bytes): string {
	let binary = '';
	for (const byte of bytes) binary += String.fromCharCode(byte);
	return btoa(binary);
}

async function digest(algorithm: SigningAlgorithm['hash'], bytes: Bytes): Promise<Bytes> {
	return new Uint8Array(await crypto.subtle.digest(algorithm, bytes));
}

function parseXml(xml: string | Bytes): Document {
	const text = typeof xml === 'string' ? xml : new TextDecoder().decode(xml);
	const doc = new DOMParser().parseFromString(text, 'application/xml');
	const failure = doc.getElementsByTagName('parsererror')[0];
	if (failure) throw new Error(`XAdES: the document is not well-formed XML — ${failure.textContent}`);
	return doc;
}

/** The `Id` attributes are uppercase, so `getElementById` is not usable here without a DTD. */
function byId(root: ParentNode, id: string): Element {
	const found = [...root.querySelectorAll('[Id]')].find((element) => element.getAttribute('Id') === id);
	if (!found) throw new Error(`XAdES: no element with Id="${id}"`);
	return found;
}

/** RFC 4514 (`CN=x, O=y`) as the RFC 2253 form XAdES uses in `ds:X509IssuerName`. */
function issuerName(issuer: string): string {
	return issuer.replace(/, /g, ',');
}

function hexToDecimal(hex: string): string {
	return BigInt(`0x${hex}`).toString();
}

function certificatesXml(
	certificates: readonly SignerCert[],
	digests: readonly Bytes[],
	hash: SigningAlgorithm['hash']
): string {
	return certificates
		.map(
			(certificate, index) => `<xades:Cert>
					<xades:CertDigest>
						<ds:DigestMethod Algorithm="${DIGEST_ALGORITHM[hash]}"/>
						<ds:DigestValue>${toBase64(digests[index])}</ds:DigestValue>
					</xades:CertDigest>
					<xades:IssuerSerial>
						<ds:X509IssuerName>${escapeXml(issuerName(certificate.issuer))}</ds:X509IssuerName>
						<ds:X509SerialNumber>${hexToDecimal(certificate.serial)}</ds:X509SerialNumber>
					</xades:IssuerSerial>
				</xades:Cert>`
		)
		.join('');
}

function signerRoleXml(claimedRoles: readonly string[] | undefined): string {
	if (!claimedRoles || claimedRoles.length === 0) return '';
	const roles = claimedRoles.map((role) => `<xades:ClaimedRole>${escapeXml(role)}</xades:ClaimedRole>`).join('');
	return `<xades:SignerRole><xades:ClaimedRoles>${roles}</xades:ClaimedRoles></xades:SignerRole>`;
}

function policyXml(policy: XadesSignatureOptions['policy']): string {
	if (!policy) return '<xades:SignaturePolicyIdentifier><xades:SignaturePolicyImplied/></xades:SignaturePolicyIdentifier>';
	const digestAlgorithm = DIGEST_ALGORITHM[policy.digestAlgorithm ?? 'SHA-256'];
	return `<xades:SignaturePolicyIdentifier>
				<xades:SignaturePolicyId>
					<xades:SigPolicyId>
						<xades:Identifier>${escapeXml(policy.identifier)}</xades:Identifier>
						${policy.description ? `<xades:Description>${escapeXml(policy.description)}</xades:Description>` : ''}
					</xades:SigPolicyId>
					${
						policy.digest
							? `<xades:SigPolicyHash>
						<ds:DigestMethod Algorithm="${digestAlgorithm}"/>
						<ds:DigestValue>${toBase64(policy.digest)}</ds:DigestValue>
					</xades:SigPolicyHash>`
							: ''
					}
				</xades:SignaturePolicyId>
			</xades:SignaturePolicyIdentifier>`;
}

/**
 * Sign bytes or an XML document, returning the signature as XML text.
 *
 * Enveloped returns the input document with the signature appended (the `.xsig` file);
 * enveloping and detached return a standalone `ds:Signature` document — pair the latter
 * with its data file (the `URI` is resolved relative to the signature, as `xmlsec1` does).
 */
export async function signXades(options: XadesSignatureOptions): Promise<string> {
	const signer = options.signer ?? options.certificates.find((certificate) => certificate.key !== null);
	if (!signer?.key || !signer.algorithm) throw new Error('XAdES: the signing certificate has no private key');
	const algorithm = signer.algorithm;
	const hash = options.hash ?? algorithm.hash;
	const signatureMethod = SIGNATURE_ALGORITHM[algorithm.name][hash];
	if (!signatureMethod) throw new Error(`XAdES: ${algorithm.name} cannot sign with ${hash}`);
	const ids = idsFor(options.ids);
	const chain = options.certificates.length === 0 ? [signer] : options.certificates;
	const reference: XadesReference = options.reference ?? { type: 'enveloped' };
	const objectId = reference.type === 'enveloping' ? (reference.objectId ?? ids.dataObject) : ids.dataObject;

	const certificateDigests = await Promise.all(chain.map((certificate) => digest(hash, certificate.der)));
	const signingTime = (options.now ?? new Date()).toISOString().replace(/\.\d{3}Z$/, 'Z');
	const mimeType =
		options.dataObject?.mimeType ?? (reference.type === 'detached' ? 'application/octet-stream' : 'application/xml');

	// The data reference: enveloped signs the whole document minus the signature, enveloping
	// signs the `ds:Object` wrapper, detached signs the raw external octets (no transforms).
	const dataReference =
		reference.type === 'enveloped'
			? `<ds:Reference Id="${ids.documentReference}" URI="">
			<ds:Transforms>
				<ds:Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/>
				<ds:Transform Algorithm="${EXC_C14N}"/>
			</ds:Transforms>
			<ds:DigestMethod Algorithm="${DIGEST_ALGORITHM[hash]}"/>
			<ds:DigestValue/>
		</ds:Reference>`
			: reference.type === 'enveloping'
				? `<ds:Reference Id="${ids.documentReference}" URI="#${objectId}">
			<ds:Transforms>
				<ds:Transform Algorithm="${EXC_C14N}"/>
			</ds:Transforms>
			<ds:DigestMethod Algorithm="${DIGEST_ALGORITHM[hash]}"/>
			<ds:DigestValue/>
		</ds:Reference>`
				: `<ds:Reference Id="${ids.documentReference}" URI="${escapeXml(reference.uri)}">
			<ds:DigestMethod Algorithm="${DIGEST_ALGORITHM[hash]}"/>
			<ds:DigestValue/>
		</ds:Reference>`;

	const template = `<ds:Signature xmlns:ds="${DS}" Id="${ids.signature}">
	<ds:SignedInfo Id="${ids.signedInfo}">
		<ds:CanonicalizationMethod Algorithm="${EXC_C14N}"/>
		<ds:SignatureMethod Algorithm="${signatureMethod}"/>
		${dataReference}
		<ds:Reference Id="${ids.propertiesReference}" Type="${SIGNED_PROPERTIES_TYPE}" URI="#${ids.signedProperties}">
			<ds:Transforms>
				<ds:Transform Algorithm="${EXC_C14N}"/>
			</ds:Transforms>
			<ds:DigestMethod Algorithm="${DIGEST_ALGORITHM[hash]}"/>
			<ds:DigestValue/>
		</ds:Reference>
	</ds:SignedInfo>
	<ds:SignatureValue Id="${ids.signatureValue}"/>
	<ds:KeyInfo Id="${ids.keyInfo}">
		<ds:X509Data>${chain.map((certificate) => `<ds:X509Certificate>${toBase64(certificate.der)}</ds:X509Certificate>`).join('')}</ds:X509Data>
	</ds:KeyInfo>
	${reference.type === 'enveloping' ? `<ds:Object Id="${objectId}"/>\n\t` : ''}<ds:Object>
		<xades:QualifyingProperties xmlns:xades="${XADES}" Target="#${ids.signature}">
			<xades:SignedProperties Id="${ids.signedProperties}">
				<xades:SignedSignatureProperties>
					<xades:SigningTime>${signingTime}</xades:SigningTime>
					<xades:SigningCertificate>${certificatesXml(chain, certificateDigests, hash)}</xades:SigningCertificate>
					${policyXml(options.policy)}
					${signerRoleXml(options.claimedRoles)}
				</xades:SignedSignatureProperties>
				<xades:SignedDataObjectProperties>
					<xades:DataObjectFormat ObjectReference="#${ids.documentReference}">
						${options.dataObject?.description ? `<xades:Description>${escapeXml(options.dataObject.description)}</xades:Description>` : ''}
						<xades:MimeType>${escapeXml(mimeType)}</xades:MimeType>
					</xades:DataObjectFormat>
				</xades:SignedDataObjectProperties>
			</xades:SignedProperties>
			${options.timeStamp ? `<xades:UnsignedProperties><xades:UnsignedSignatureProperties><xades:SignatureTimeStamp><ds:CanonicalizationMethod Algorithm="${EXC_C14N}"/><xades:EncapsulatedTimeStamp/></xades:SignatureTimeStamp></xades:UnsignedSignatureProperties></xades:UnsignedProperties>` : ''}
		</xades:QualifyingProperties>
	</ds:Object>
</ds:Signature>`;

	const setDigest = (doc: Document, referenceId: string, value: Bytes) => {
		const digestValue = byId(doc, referenceId).getElementsByTagNameNS(DS, 'DigestValue')[0];
		digestValue.textContent = toBase64(value);
	};
	const finish = async (doc: Document): Promise<void> => {
		setDigest(
			doc,
			ids.propertiesReference,
			await digest(hash, canonicalize(byId(doc, ids.signedProperties), { exclusive: true }))
		);
		const signedInfo = byId(doc, ids.signedInfo);
		const canonical = canonicalize(signedInfo, { exclusive: true });
		const signatureValue = await signBytes(signer, canonical, hash);
		byId(doc, ids.signatureValue).textContent = toBase64(signatureValue);
		if (options.timeStamp) {
			// XAdES-T: the TSA timestamps the canonicalised SignatureValue element, never the
			// document (ETSI EN 319 132-1 §5.4). The imprint algorithm is fixed by the token (SHA-256).
			const signatureValueBytes = canonicalize(byId(doc, ids.signatureValue), { exclusive: true });
			const token = await requestTimeStamp(options.timeStamp, signatureValueBytes);
			byId(doc, ids.signature).getElementsByTagNameNS(XADES, 'EncapsulatedTimeStamp')[0].textContent =
				toBase64(token);
		}
	};
	const serialize = (doc: Document): string => {
		const body = [...doc.childNodes].map((child) => new XMLSerializer().serializeToString(child)).join('');
		return `<?xml version="1.0" encoding="UTF-8"?>\n${body}`;
	};

	if (reference.type === 'enveloped') {
		if (options.xml === undefined) throw new Error('XAdES: the enveloped shape needs the document in `xml`');
		const doc = parseXml(options.xml);
		if (doc.querySelector('Signature')) throw new Error('XAdES: the document is already signed');
		// The document digest predates the insertion — the enveloped-signature transform would
		// remove the signature anyway. PIs/comments above the root are part of the digest.
		const documentDigest = await digest(hash, canonicalize(doc, { exclusive: true }));
		doc.documentElement.appendChild(doc.importNode(parseXml(template).documentElement, true));
		setDigest(doc, ids.documentReference, documentDigest);
		await finish(doc);
		// Serialize every document child — top-level PIs/comments must survive signing.
		return serialize(doc);
	}

	const signatureDoc = parseXml(template);
	if (reference.type === 'enveloping') {
		if (options.xml === undefined) throw new Error('XAdES: the enveloping shape needs the document in `xml`');
		const dataDoc = parseXml(options.xml);
		if (dataDoc.querySelector('Signature')) throw new Error('XAdES: the document is already signed');
		byId(signatureDoc, objectId).appendChild(signatureDoc.importNode(dataDoc.documentElement, true));
		// Digest the wrapper as the verifier sees it — with the data already inside.
		setDigest(signatureDoc, ids.documentReference, await digest(hash, canonicalize(byId(signatureDoc, objectId), { exclusive: true })));
	} else {
		const data = typeof reference.data === 'string' ? new TextEncoder().encode(reference.data) : reference.data;
		setDigest(signatureDoc, ids.documentReference, await digest(hash, data));
	}
	await finish(signatureDoc);
	return serialize(signatureDoc);
}
