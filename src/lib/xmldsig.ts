/**
 * Plain XMLDSig signatures (enveloped, enveloping and detached), built in the browser.
 *
 * Same reference shapes as `xades.ts` — enveloped inserts into the document (`URI=""`),
 * enveloping wraps the document in a `ds:Object` (`URI="#object"`), detached hashes the
 * raw external octets (`URI` locates the data) — but with a single `Reference` and no
 * `xades:QualifyingProperties` block, for consumers that reject XAdES.
 */

import type { Bytes } from './asn1';
import { EXC_C14N, canonicalize } from './c14n';
import { type SignerCert, type SigningAlgorithm, signBytes } from './sign';

const DS = 'http://www.w3.org/2000/09/xmldsig#';

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

interface XmldsigCommon {
	/** Certificates of the PKCS#12 container (#5), the signer first — all go into the KeyInfo. */
	certificates: readonly SignerCert[];
	/** The certificate owning the private key (#5). Defaults to the first chain entry with a key. */
	signer?: SignerCert;
	/** Overrides the certificate's default hash (legacy portals ask for SHA1withRSA). */
	hash?: SigningAlgorithm['hash'];
	/** Deterministic ids — for golden vectors and reproducible output. */
	ids?: Partial<XmldsigIds>;
}

export type XmldsigReference =
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
	 */
	| { type: 'detached'; uri: string; data: string | Bytes };

export type XmldsigSignatureOptions = XmldsigCommon &
	(
		| { reference?: { type: 'enveloped' }; xml: string | Bytes }
		| { reference: { type: 'enveloping'; objectId?: string }; xml: string | Bytes }
		| { reference: { type: 'detached'; uri: string; data: string | Bytes }; xml?: string | Bytes }
	);

export interface XmldsigIds {
	signature: string;
	signedInfo: string;
	signatureValue: string;
	keyInfo: string;
	/** Reference to the data object. */
	documentReference: string;
	/** Id of the enveloping `ds:Object` (unused by the enveloped and detached shapes). */
	dataObject: string;
}

const idsFor = (overrides: Partial<XmldsigIds> = {}): XmldsigIds => {
	const random = [...crypto.getRandomValues(new Uint8Array(8))]
		.map((byte) => byte.toString(16).padStart(2, '0'))
		.join('');
	return {
		signature: `Signature-${random}`,
		signedInfo: `SignedInfo-${random}`,
		signatureValue: `SignatureValue-${random}`,
		keyInfo: `KeyInfo-${random}`,
		documentReference: `Reference-${random}`,
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
	if (failure) throw new Error(`XMLDSig: the document is not well-formed XML — ${failure.textContent}`);
	return doc;
}

/** The `Id` attributes are uppercase, so `getElementById` is not usable here without a DTD. */
function byId(root: ParentNode, id: string): Element {
	const found = [...root.querySelectorAll('[Id]')].find((element) => element.getAttribute('Id') === id);
	if (!found) throw new Error(`XMLDSig: no element with Id="${id}"`);
	return found;
}

/**
 * Sign bytes or an XML document with plain XMLDSig, returning the signature as XML text.
 *
 * Enveloped returns the input document with the signature appended; enveloping and detached
 * return a standalone `ds:Signature` document — pair the latter with its data file (the
 * `URI` is resolved relative to the signature, as `xmlsec1` does).
 */
export async function signXmldsig(options: XmldsigSignatureOptions): Promise<string> {
	const signer = options.signer ?? options.certificates.find((certificate) => certificate.key !== null);
	if (!signer?.key || !signer.algorithm) throw new Error('XMLDSig: the signing certificate has no private key');
	const algorithm = signer.algorithm;
	const hash = options.hash ?? algorithm.hash;
	const signatureMethod = SIGNATURE_ALGORITHM[algorithm.name][hash];
	if (!signatureMethod) throw new Error(`XMLDSig: ${algorithm.name} cannot sign with ${hash}`);
	const ids = idsFor(options.ids);
	const chain = options.certificates.length === 0 ? [signer] : options.certificates;
	const reference: XmldsigReference = options.reference ?? { type: 'enveloped' };
	const objectId = reference.type === 'enveloping' ? (reference.objectId ?? ids.dataObject) : ids.dataObject;

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
	</ds:SignedInfo>
	<ds:SignatureValue Id="${ids.signatureValue}"/>
	<ds:KeyInfo Id="${ids.keyInfo}">
		<ds:X509Data>${chain.map((certificate) => `<ds:X509Certificate>${toBase64(certificate.der)}</ds:X509Certificate>`).join('')}</ds:X509Data>
	</ds:KeyInfo>
${reference.type === 'enveloping' ? `\t<ds:Object Id="${objectId}"/>\n` : ''}</ds:Signature>`;

	const setDigest = (doc: Document, referenceId: string, value: Bytes) => {
		const digestValue = byId(doc, referenceId).getElementsByTagNameNS(DS, 'DigestValue')[0];
		digestValue.textContent = toBase64(value);
	};
	const finish = async (doc: Document): Promise<void> => {
		const signedInfo = byId(doc, ids.signedInfo);
		const canonical = canonicalize(signedInfo, { exclusive: true });
		const signatureValue = await signBytes(signer, canonical, hash);
		byId(doc, ids.signatureValue).textContent = toBase64(signatureValue);
	};
	const serialize = (doc: Document): string => {
		const body = [...doc.childNodes].map((child) => new XMLSerializer().serializeToString(child)).join('');
		return `<?xml version="1.0" encoding="UTF-8"?>\n${body}`;
	};

	if (reference.type === 'enveloped') {
		if (options.xml === undefined) throw new Error('XMLDSig: the enveloped shape needs the document in `xml`');
		const doc = parseXml(options.xml);
		if (doc.querySelector('Signature')) throw new Error('XMLDSig: the document is already signed');
		// The document digest predates the insertion — the enveloped-signature transform would
		// remove the signature anyway.
		const documentDigest = await digest(hash, canonicalize(doc, { exclusive: true }));
		doc.documentElement.appendChild(doc.importNode(parseXml(template).documentElement, true));
		setDigest(doc, ids.documentReference, documentDigest);
		await finish(doc);
		return serialize(doc);
	}

	const signatureDoc = parseXml(template);
	if (reference.type === 'enveloping') {
		if (options.xml === undefined) throw new Error('XMLDSig: the enveloping shape needs the document in `xml`');
		const dataDoc = parseXml(options.xml);
		if (dataDoc.querySelector('Signature')) throw new Error('XMLDSig: the document is already signed');
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
