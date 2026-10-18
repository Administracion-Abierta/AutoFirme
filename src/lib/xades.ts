/**
 * XAdES-EPES enveloped signature, built in the browser.
 *
 * The document is parsed, the `ds:Signature` template is inserted as the last child of the
 * document element, the two digests are computed over the *in-document* node-sets (canonicalized
 * with Exclusive C14N), and the signature value is a WebCrypto signature over the canonicalized
 * `ds:SignedInfo` (#5's non-extractable key). `KeyInfo/X509Data` carries the DER chain of the
 * PKCS#12 container, and the XAdES signed properties carry the ES/EN-independent metadata:
 * signing time, the certificate digests, and the signature policy (implied by default — see
 * `XadesSignatureOptions.policy`).
 */

import type { Bytes } from './asn1';
import { EXC_C14N, canonicalize } from './c14n';
import { type SignerCert, type SigningAlgorithm, signBytes } from './sign';

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

export interface XadesSignatureOptions {
	/** The XML document to sign, as text or UTF-8 bytes. */
	xml: string | Bytes;
	/** Certificates of the PKCS#12 container (#5), the signer first — all go into the KeyInfo. */
	certificates: readonly SignerCert[];
	/** The certificate owning the private key (#5). Defaults to the first chain entry with a key. */
	signer?: SignerCert;
	/** EPES signature policy. Defaults to `SignaturePolicyImplied` (nothing is invented). */
	policy?: { identifier: string; description?: string; digest?: Bytes };
	/** XAdES `DataObjectFormat` of the signed document. */
	dataObject?: { description?: string; mimeType?: string };
	/** Overrides the certificate's default hash (legacy portals ask for SHA1withRSA). The XAdES
	 * certificate digests follow it, as AutoFirma does. */
	hash?: SigningAlgorithm['hash'];
	/** Signing time; defaults to now. */
	now?: Date;
	/** Deterministic ids — for golden vectors and reproducible output. */
	ids?: Partial<XadesIds>;
}

export interface XadesIds {
	signature: string;
	signedInfo: string;
	signatureValue: string;
	keyInfo: string;
	signedProperties: string;
	/** Reference to the whole document (the enveloped transform target). */
	documentReference: string;
	/** Reference to `xades:SignedProperties`. */
	propertiesReference: string;
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

function policyXml(policy: XadesSignatureOptions['policy']): string {
	if (!policy) return '<xades:SignaturePolicyIdentifier><xades:SignaturePolicyImplied/></xades:SignaturePolicyIdentifier>';
	return `<xades:SignaturePolicyIdentifier>
				<xades:SignaturePolicyId>
					<xades:SigPolicyId>
						<xades:Identifier>${escapeXml(policy.identifier)}</xades:Identifier>
						${policy.description ? `<xades:Description>${escapeXml(policy.description)}</xades:Description>` : ''}
					</xades:SigPolicyId>
					${
						policy.digest
							? `<xades:SigPolicyHash>
						<ds:DigestMethod Algorithm="${DIGEST_ALGORITHM['SHA-256']}"/>
						<ds:DigestValue>${toBase64(policy.digest)}</ds:DigestValue>
					</xades:SigPolicyHash>`
							: ''
					}
				</xades:SignaturePolicyId>
			</xades:SignaturePolicyIdentifier>`;
}

/**
 * Sign an XML document, returning the enveloped signature as XML text (the `.xsig` file).
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

	const doc = parseXml(options.xml);
	if (doc.querySelector('Signature')) throw new Error('XAdES: the document is already signed');

	// Reference #1 — the document itself: PIs/comments above the root included, the ds:Signature
	// not yet inserted (the enveloped-signature transform would remove it anyway).
	const documentDigest = await digest(hash, canonicalize(doc, { exclusive: true }));
	const certificateDigests = await Promise.all(chain.map((certificate) => digest(hash, certificate.der)));
	const signingTime = (options.now ?? new Date()).toISOString().replace(/\.\d{3}Z$/, 'Z');
	const mimeType = options.dataObject?.mimeType ?? 'application/xml';

	const template = `<ds:Signature xmlns:ds="${DS}" Id="${ids.signature}">
	<ds:SignedInfo Id="${ids.signedInfo}">
		<ds:CanonicalizationMethod Algorithm="${EXC_C14N}"/>
		<ds:SignatureMethod Algorithm="${signatureMethod}"/>
		<ds:Reference Id="${ids.documentReference}" URI="">
			<ds:Transforms>
				<ds:Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/>
				<ds:Transform Algorithm="${EXC_C14N}"/>
			</ds:Transforms>
			<ds:DigestMethod Algorithm="${DIGEST_ALGORITHM[hash]}"/>
			<ds:DigestValue/>
		</ds:Reference>
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
	<ds:Object>
		<xades:QualifyingProperties xmlns:xades="${XADES}" Target="#${ids.signature}">
			<xades:SignedProperties Id="${ids.signedProperties}">
				<xades:SignedSignatureProperties>
					<xades:SigningTime>${signingTime}</xades:SigningTime>
					<xades:SigningCertificate>${certificatesXml(chain, certificateDigests, hash)}</xades:SigningCertificate>
					${policyXml(options.policy)}
				</xades:SignedSignatureProperties>
				<xades:SignedDataObjectProperties>
					<xades:DataObjectFormat ObjectReference="#${ids.documentReference}">
						${options.dataObject?.description ? `<xades:Description>${escapeXml(options.dataObject.description)}</xades:Description>` : ''}
						<xades:MimeType>${escapeXml(mimeType)}</xades:MimeType>
					</xades:DataObjectFormat>
				</xades:SignedDataObjectProperties>
			</xades:SignedProperties>
		</xades:QualifyingProperties>
	</ds:Object>
</ds:Signature>`;

	doc.documentElement.appendChild(doc.importNode(parseXml(template).documentElement, true));

	const setDigest = (referenceId: string, value: Bytes) => {
		byId(doc, referenceId).getElementsByTagNameNS(DS, 'DigestValue')[0].textContent = toBase64(value);
	};
	setDigest(ids.documentReference, documentDigest);
	setDigest(
		ids.propertiesReference,
		await digest(hash, canonicalize(byId(doc, ids.signedProperties), { exclusive: true }))
	);

	const signedInfo = byId(doc, ids.signedInfo);
	const signatureValue = await signBytes(signer, canonicalize(signedInfo, { exclusive: true }), hash);
	byId(doc, ids.signatureValue).textContent = toBase64(signatureValue);

	// Serialize every document child — top-level PIs/comments must survive signing.
	const body = [...doc.childNodes].map((child) => new XMLSerializer().serializeToString(child)).join('');
	return `<?xml version="1.0" encoding="UTF-8"?>\n${body}`;
}
