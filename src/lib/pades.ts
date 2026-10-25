/**
 * PAdES baseline B-B (ETSI EN 319 142-1) signing in the browser.
 *
 * `signPdf` appends a real PDF **incremental update** to the input document — the original bytes
 * are copied verbatim, then new objects (signature dictionary, `AcroForm` `/Sig` field widget, an
 * appearance stream when the signature is visible, re-defined page and `/AcroForm` holder) plus a
 * classic cross-reference section and trailer are appended, exactly like a desktop signer. Nothing
 * in the input is flattened or rewritten in place.
 *
 * The `/Contents` placeholder holds a CMS `SignedData` (RFC 5652): detached, eContentType
 * `id-data`, signed attributes content-type + signing-time + message-digest +
 * signing-certificate-v2 (RFC 5035), signature `sha256WithRSAEncryption` or
 * `ecdsa-with-SHA256/384` over the byte ranges. No timestamp, no DSS/LTV — B-B only.
 *
 * The private key never leaves the client: it is a WebCrypto `CryptoKey` used through
 * `crypto.subtle`.
 */

import { requestTimeStamp, timeStampAttributes } from './timestamp';
import {
	PDFArray,
	PDFDict,
	PDFDocument,
	PDFName,
	PDFNumber,
	PDFRef,
	PDFString,
} from 'pdf-lib';

import {
	certIssuerAndSerial,
	concat,
	content,
	contextTag,
	ecdsaRawToDer,
	int,
	intBytes,
	octet,
	oid,
	readTlv,
	seq,
	setOf,
	time,
	toBytes,
	type Bytes
} from './der';
import {
	buildAppearance,
	mapAutoFirmaRect,
	rectForCorner,
	serializeObject,
	type VisibleSignatureOptions
} from './pades-visible';

const OID = {
	data: '1.2.840.113549.1.7.1',
	signedData: '1.2.840.113549.1.7.2',
	contentType: '1.2.840.113549.1.9.3',
	signingTime: '1.2.840.113549.1.9.5',
	messageDigest: '1.2.840.113549.1.9.4',
	signingCertificateV2: '1.2.840.113549.1.9.16.2.47',
	sha256: '2.16.840.1.101.3.4.2.1',
	sha384: '2.16.840.1.101.3.4.2.2',
	sha256WithRsa: '1.2.840.113549.1.1.11',
	ecdsaWithSha256: '1.2.840.10045.4.3.2',
	ecdsaWithSha384: '1.2.840.10045.4.3.3'
} as const;

const encoder = new TextEncoder();
const decoder = new TextDecoder('latin1');
const DER_NULL: Bytes = new Uint8Array([0x05, 0x00]);

/**
 * Room reserved in `/Contents` (and in the probe that sizes it) for a TSA token. The token only
 * exists once the signature does, and it may not change `/ByteRange` without invalidating itself,
 * so the slot is sized up front and the CMS is zero-padded into it. 8 KiB covers the tokens public
 * TSAs issue with their certificate chain attached.
 */
const TIME_STAMP_ALLOWANCE = 8192;

export interface PadesSignatureOptions {
	/** DER bytes of the signing certificate. */
	certificate: ArrayBuffer | ArrayBufferView;
	/** Extra DER certificates, leaf-to-root, excluding `certificate`. */
	chain?: readonly (ArrayBuffer | ArrayBufferView)[];
	/** `RSASSA-PKCS1-v1_5` or `ECDSA` key from WebCrypto; never leaves the client. */
	privateKey: CryptoKey;
	/** Defaults to now; fixed in tests so the output is byte-reproducible. */
	signingTime?: Date;
	/** `/Name` entry, also the default text of a visible appearance. */
	name?: string;
	reason?: string;
	location?: string;
	contact?: string;
	/** Draw a visible signature block. Omit for an invisible signature. */
	visible?: VisibleSignatureOptions;
	/**
	 * RFC 3161 TSA URL. When set, the signature value is timestamped (PAdES B-T, ETSI EN 319 142-1
	 * §6.4); leave it out for a plain B-B signature. A same-origin proxy may be needed, since most
	 * TSAs send no CORS headers.
	 */
	timeStamp?: string;
}

interface Algorithm {
	/** Parameters for `crypto.subtle.sign`. */
	params: AlgorithmIdentifier | EcdsaParams;
	digestName: 'SHA-256' | 'SHA-384';
	digestOid: string;
	signatureOid: string;
	/** `NULL` for RSA, absent for ECDSA (RFC 4055 §3.1, RFC 5754 §3.1). */
	signatureParams: Bytes;
	/** Upper bound of the DER signature, used to size the `/Contents` placeholder. */
	maxSignatureBytes: number;
	/** ECDSA only: bytes per coordinate, for the raw → DER conversion. */
	coordinateBytes: number;
}

function algorithmFor(key: CryptoKey): Algorithm {
	const keyAlgorithm = key.algorithm as {
		name: string;
		namedCurve?: string;
		modulusLength?: number;
	};
	if (keyAlgorithm.name === 'RSASSA-PKCS1-v1_5') {
		return {
			params: { name: 'RSASSA-PKCS1-v1_5' },
			digestName: 'SHA-256',
			digestOid: OID.sha256,
			signatureOid: OID.sha256WithRsa,
			signatureParams: DER_NULL,
			maxSignatureBytes: Math.ceil((keyAlgorithm.modulusLength ?? 2048) / 8),
			coordinateBytes: 0
		};
	}
	if (keyAlgorithm.name === 'ECDSA') {
		const coordinateBytes =
			keyAlgorithm.namedCurve === 'P-384' ? 48 : keyAlgorithm.namedCurve === 'P-521' ? 66 : 32;
		const sha384 = coordinateBytes >= 48;
		return {
			params: { name: 'ECDSA', hash: sha384 ? 'SHA-384' : 'SHA-256' },
			digestName: sha384 ? 'SHA-384' : 'SHA-256',
			digestOid: sha384 ? OID.sha384 : OID.sha256,
			signatureOid: sha384 ? OID.ecdsaWithSha384 : OID.ecdsaWithSha256,
			signatureParams: new Uint8Array(0),
			// SEQUENCE header plus two INTEGERs, each with a header and a possible leading zero.
			maxSignatureBytes: 2 * coordinateBytes + 8,
			coordinateBytes
		};
	}
	throw new Error(`unsupported signing key algorithm: ${keyAlgorithm.name}`);
}

function subtle(): SubtleCrypto {
	const implementation = globalThis.crypto?.subtle;
	if (!implementation) throw new Error('WebCrypto (crypto.subtle) is unavailable here');
	return implementation;
}

async function digestOf(name: string, data: Bytes): Promise<Bytes> {
	return new Uint8Array(await subtle().digest(name, data));
}

async function signData(key: CryptoKey, algorithm: Algorithm, data: Bytes): Promise<Bytes> {
	const raw = new Uint8Array(await subtle().sign(algorithm.params, key, data));
	return algorithm.coordinateBytes === 0 ? raw : ecdsaRawToDer(raw, algorithm.coordinateBytes);
}

/** Content bytes of a TLV, without its tag/length header. */
function derInner(item: Bytes): Bytes {
	return content(item, readTlv(item));
}

/** A `SEQUENCE` of `length` bytes — the shape a `TimeStampToken` has, for the size probe only. */
function probeToken(length: number): Bytes {
	const body = length - 4; // two-byte tag plus a two-byte long-form length
	return concat([new Uint8Array([0x30, 0x82, body >> 8, body & 0xff]), new Uint8Array(body)]);
}

/** CMS signed attributes: content-type, signing-time, message-digest, signing-certificate-v2. */
function signedAttributesOf(digest: Bytes, signingTime: Date, certificateHash: Bytes): Bytes {
	// SigningCertificateV2 ::= SEQUENCE { certs SEQUENCE OF ESSCertIDv2 } (RFC 5035 §5.4);
	// ESSCertIDv2.hashAlgorithm defaults to SHA-256 and is therefore omitted.
	const signingCertificateV2 = seq(seq(seq(octet(certificateHash))));
	return setOf([
		seq(oid(OID.contentType), setOf([oid(OID.data)])),
		seq(oid(OID.signingTime), setOf([time(signingTime)])),
		seq(oid(OID.messageDigest), setOf([octet(digest)])),
		seq(oid(OID.signingCertificateV2), setOf([signingCertificateV2]))
	]);
}

function cmsOf(input: {
	certificate: Bytes;
	chain: readonly Bytes[];
	signedAttributes: Bytes;
	signature: Bytes;
	/** RFC 3161 `id-aa-timeStampToken` attribute set; absent in a B-B signature. */
	unsignedAttributes?: Bytes;
	algorithm: Algorithm;
}): Bytes {
	const { issuer, serial } = certIssuerAndSerial(input.certificate);
	const signerInfo = seq(
		int(1),
		seq(issuer, intBytes(serial)),
		seq(oid(input.algorithm.digestOid), DER_NULL),
		contextTag(0, derInner(input.signedAttributes)),
		seq(
			oid(input.algorithm.signatureOid),
			...(input.algorithm.signatureParams.length > 0 ? [input.algorithm.signatureParams] : [])
		),
		octet(input.signature),
		// `unsignedAttrs [1] IMPLICIT SET OF Attribute`, after the signature (RFC 5652 §5.3).
		...(input.unsignedAttributes ? [contextTag(1, derInner(input.unsignedAttributes))] : [])
	);

	const signedData = seq(
		int(1),
		setOf([seq(oid(input.algorithm.digestOid), DER_NULL)]),
		seq(oid(OID.data)),
		contextTag(0, concat([input.certificate, ...input.chain])),
		setOf([signerInfo])
	);

	return seq(oid(OID.signedData), contextTag(0, signedData));
}

class Writer {
	private readonly parts: Bytes[] = [];
	length = 0;

	push(part: string | Bytes): void {
		const bytes = typeof part === 'string' ? encoder.encode(part) : part;
		this.parts.push(bytes);
		this.length += bytes.length;
	}

	bytes(): Bytes {
		return concat(this.parts);
	}
}

function pdfLiteral(value: string): string {
	return value.replace(/\\/g, '\\\\').replace(/\(/g, '\\(').replace(/\)/g, '\\)');
}

/** PDF text string: ASCII literal, else UTF-16BE with BOM as hex (ISO 32000-1 §7.9.4). */
function pdfTextString(value: string): string {
	if (/^[\x00-\x7f]*$/.test(value)) return `(${pdfLiteral(value)})`;
	let hex = 'feff';
	for (let index = 0; index < value.length; index += 1) {
		hex += value.charCodeAt(index).toString(16).padStart(4, '0');
	}
	return `<${hex}>`;
}

function pdfDate(date: Date): string {
	const pad = (value: number, width = 2) => String(value).padStart(width, '0');
	return (
		'D:' +
		date.getUTCFullYear() +
		pad(date.getUTCMonth() + 1) +
		pad(date.getUTCDate()) +
		pad(date.getUTCHours()) +
		pad(date.getUTCMinutes()) +
		pad(date.getUTCSeconds()) +
		'Z'
	);
}

function trailerNumber(pdf: Bytes, key: string): number | null {
	const tail = decoder.decode(pdf.subarray(Math.max(0, pdf.length - 4096)));
	const matches = [...tail.matchAll(new RegExp(`${key}\\s+(\\d+)`, 'g'))];
	const last = matches[matches.length - 1];
	return last ? Number(last[1]) : null;
}

/** A free `/T` name, so a second signature on the same document stays addressable. */
function nextFieldName(acroForm: PDFDict | null, context: PDFDict['context']): string {
	const taken = new Set<string>();
	for (const entry of acroForm?.lookupMaybe(PDFName.of('Fields'), PDFArray)?.asArray() ?? []) {
		if (!(entry instanceof PDFRef)) continue;
		const name = context.lookupMaybe(entry, PDFDict)?.lookupMaybe(PDFName.of('T'), PDFString);
		if (name) taken.add(name.decodeText());
	}
	let index = 1;
	while (taken.has(`Signature${index}`)) index += 1;
	return `Signature${index}`;
}

/**
 * Sign `pdf` as PAdES B-B. Returns the input bytes followed by an incremental update that carries
 * the signature, so the original document survives untouched (AcroForm not flattened).
 */
export async function signPdf(
	pdf: ArrayBuffer | ArrayBufferView,
	options: PadesSignatureOptions
): Promise<Bytes> {
	const source = toBytes(pdf);
	const certificate = toBytes(options.certificate);
	const chain = (options.chain ?? []).map((entry) => toBytes(entry));
	const signingTime = options.signingTime ?? new Date();
	const algorithm = algorithmFor(options.privateKey);
	const timeStampUrl = options.timeStamp;

	// The trailer of the *input* stays the base of the /Prev chain we append to.
	const previousXrefOffset = trailerNumber(source, 'startxref');
	if (previousXrefOffset === null) {
		throw new Error('input PDF has no startxref: cannot append an incremental update');
	}

	const document = await PDFDocument.load(source, { updateMetadata: false });
	const context = document.context;
	const rootRef = context.trailerInfo.Root;
	if (!(rootRef instanceof PDFRef)) throw new Error('input PDF has no document catalog');
	if (context.trailerInfo.Encrypt) throw new Error('encrypted PDFs cannot be signed');

	const pageIndex = options.visible?.page ?? 0;
	const pages = document.getPages();
	const resolvedPage = pageIndex < 0 ? pages.length + pageIndex : pageIndex;
	// AutoFirma ignores an out-of-range page and stamps the last one.
	const page = pages[
		options.visible?.autoFirmaRect && (resolvedPage < 0 || resolvedPage >= pages.length)
			? pages.length - 1
			: resolvedPage
	];
	if (!page) throw new Error(`input PDF has no page ${pageIndex}`);

	const visible = options.visible;
	// Explicit coordinates win; otherwise the block is placed against the page's chosen corner.
	const rect: [number, number, number, number] = visible
		? (visible.rect ??
			(visible.autoFirmaRect
				? mapAutoFirmaRect(
						page.getMediaBox(),
						page.getRotation().angle,
						visible.autoFirmaRect
					)
				: rectForCorner(page.getSize(), visible.corner ?? 'bottom-right', visible.size)))
		: [0, 0, 0, 0];
	const appearance = visible
		? await buildAppearance(document, rect, {
				lines: visible.lines ?? [options.name ?? 'Firmado'],
				image: visible.image,
				fontFamily: visible.fontFamily,
				fontSize: visible.fontSize,
				fontStyle: visible.fontStyle,
				fontColor: visible.fontColor
			})
		: null;

	// Existing AcroForm state, so the new field becomes reachable and re-signing keeps working.
	const catalog = context.lookup(rootRef, PDFDict);
	const acroEntry = catalog.get(PDFName.of('AcroForm'));
	const acroRef = acroEntry instanceof PDFRef ? acroEntry : null;
	const acroDict = acroRef
		? context.lookup(acroRef, PDFDict)
		: acroEntry instanceof PDFDict
			? acroEntry
			: null;
	const fieldName = nextFieldName(acroDict, context);

	// The `/Contents` placeholder is excluded from `/ByteRange`, so its size never feeds back into
	// the digest: sizing it from a zeroed probe keeps the layout single-pass.
	const certificateHash = await digestOf('SHA-256', certificate);
	const probe = cmsOf({
		certificate,
		chain,
		signedAttributes: signedAttributesOf(
			new Uint8Array(algorithm.digestName === 'SHA-384' ? 48 : 32),
			signingTime,
			certificateHash
		),
		signature: new Uint8Array(algorithm.maxSignatureBytes),
		unsignedAttributes: timeStampUrl
			? timeStampAttributes(probeToken(TIME_STAMP_ALLOWANCE))
			: undefined,
		algorithm
	});
	const contentsHexLength = probe.length * 2;

	const sigRef = context.nextRef();
	const fieldRef = context.nextRef();
	const appearanceRef = appearance ? context.nextRef() : null;
	const fontRef = appearance ? context.nextRef() : null;

	const out = new Writer();
	const base = source.length;
	const offsets = new Map<number, number>();
	let contentsOffset = 0;
	let byteRangeOffset = 0;

	const openObject = (number: number): void => {
		offsets.set(number, base + out.length);
		out.push(`${number} 0 obj\n`);
	};
	const closeObject = (): void => out.push('\nendobj\n');

	out.push('\n');

	openObject(sigRef.objectNumber);
	out.push('<< /Type /Sig /Filter /Adobe.PPKLite /SubFilter /ETSI.CAdES.detached\n');
	byteRangeOffset = base + out.length + '/ByteRange [0 '.length;
	out.push(`/ByteRange [0 ${'0'.repeat(10)} ${'0'.repeat(10)} ${'0'.repeat(10)}]\n`);
	out.push('/Contents ');
	// `/ByteRange` must exclude the delimiters too, so record the position of the '<'.
	contentsOffset = base + out.length;
	out.push(`<${'0'.repeat(contentsHexLength)}>\n`);
	out.push(`/M (${pdfDate(signingTime)})\n`);
	if (options.name) out.push(`/Name ${pdfTextString(options.name)}\n`);
	if (options.reason) out.push(`/Reason ${pdfTextString(options.reason)}\n`);
	if (options.location) out.push(`/Location ${pdfTextString(options.location)}\n`);
	if (options.contact) out.push(`/ContactInfo ${pdfTextString(options.contact)}\n`);
	out.push('>>');
	closeObject();

	openObject(fieldRef.objectNumber);
	out.push('<< /Type /Annot /Subtype /Widget /FT /Sig /F 4\n');
	out.push(`/T (${pdfLiteral(fieldName)})\n`);
	out.push(`/P ${page.ref.toString()}\n`);
	out.push(`/Rect [${rect.join(' ')}]\n`);
	out.push(`/V ${sigRef.toString()}\n`);
	if (appearanceRef) out.push(`/AP << /N ${appearanceRef.toString()} >>\n`);
	out.push('>>');
	closeObject();

	if (appearanceRef && fontRef && appearance) {
		const width = Math.abs(rect[2] - rect[0]);
		const height = Math.abs(rect[3] - rect[1]);
		const xobjects = appearance.imageRef
			? ` /XObject << /Im0 ${appearance.imageRef.toString()} >>`
			: '';
		openObject(appearanceRef.objectNumber);
		out.push(
			`<< /Type /XObject /Subtype /Form /FormType 1 /BBox [0 0 ${width} ${height}] ` +
				`/Resources << /Font << /F1 ${fontRef.toString()} >>${xobjects} >> ` +
				`/Length ${encoder.encode(appearance.content).length} >>\nstream\n`
		);
		out.push(appearance.content);
		out.push('endstream');
		closeObject();

		openObject(fontRef.objectNumber);
		out.push(`<< /Type /Font /Subtype /Type1 /BaseFont /${appearance.fontBase} /Encoding /WinAnsiEncoding >>`);
		closeObject();

		// Image streams (and their /SMask) are pdf-lib's own; they are appended verbatim so the
		// appearance can reference them. Their refs land in the xref like any other object.
		for (const object of appearance.objects) {
			openObject(object.ref.objectNumber);
			out.push(object.bytes);
			closeObject();
		}
	}

	// Page: append the widget to /Annots (re-defined object, same object number).
	const pageNode = PDFDict.fromMapWithContext(new Map(page.node.asMap()), context);
	const annots = PDFArray.withContext(context);
	pageNode.lookupMaybe(PDFName.of('Annots'), PDFArray)?.asArray().forEach((entry) => annots.push(entry));
	annots.push(fieldRef);
	pageNode.set(PDFName.of('Annots'), annots);
	openObject(page.ref.objectNumber);
	out.push(serializeObject(pageNode));
	closeObject();

	// AcroForm: the field must be reachable from the catalog, or no viewer finds the signature.
	const mergedAcroForm = acroDict
		? PDFDict.fromMapWithContext(new Map(acroDict.asMap()), context)
		: PDFDict.withContext(context);
	const fields = PDFArray.withContext(context);
	acroDict?.lookupMaybe(PDFName.of('Fields'), PDFArray)?.asArray().forEach((entry) => fields.push(entry));
	fields.push(fieldRef);
	mergedAcroForm.set(PDFName.of('Fields'), fields);
	mergedAcroForm.set(PDFName.of('SigFlags'), PDFNumber.of(3));

	if (acroRef) {
		openObject(acroRef.objectNumber);
		out.push(serializeObject(mergedAcroForm));
		closeObject();
	} else {
		const mergedCatalog = PDFDict.fromMapWithContext(new Map(catalog.asMap()), context);
		mergedCatalog.set(PDFName.of('AcroForm'), mergedAcroForm);
		openObject(rootRef.objectNumber);
		out.push(serializeObject(mergedCatalog));
		closeObject();
	}

	// Cross-reference section: one subsection per run of consecutive object numbers.
	const xrefOffset = base + out.length;
	out.push('xref\n');
	const numbers = [...offsets.keys()].sort((a, b) => a - b);
	for (let start = 0; start < numbers.length; ) {
		let end = start;
		while (end + 1 < numbers.length && numbers[end + 1] === numbers[end] + 1) end += 1;
		out.push(`${numbers[start]} ${end - start + 1}\n`);
		for (let index = start; index <= end; index += 1) {
			const offset = offsets.get(numbers[index]);
			if (offset === undefined) throw new Error('internal error: missing xref offset');
			out.push(`${String(offset).padStart(10, '0')} 00000 n \n`);
		}
		start = end + 1;
	}
	out.push('trailer\n<<');
	out.push(` /Size ${Math.max(trailerNumber(source, '/Size') ?? 0, context.largestObjectNumber + 1)}`);
	out.push(` /Root ${rootRef.toString()}`);
	if (context.trailerInfo.Info) out.push(` /Info ${context.trailerInfo.Info.toString()}`);
	if (context.trailerInfo.ID) out.push(` /ID ${context.trailerInfo.ID.toString()}`);
	out.push(` /Prev ${previousXrefOffset}`);
	out.push(' >>\nstartxref\n');
	out.push(`${xrefOffset}\n`);
	out.push('%%EOF\n');

	const signed = concat([source, out.bytes()]);

	// `/ByteRange [start1 len1 start2 len2]` covers everything but the `/Contents` hex string and
	// its angle brackets, i.e. `[0, <offset of '<'>, <offset after '>'>, <tail length>]`.
	const contentsEnd = contentsOffset + contentsHexLength + 2;
	[contentsOffset, contentsEnd, signed.length - contentsEnd].forEach((value, index) => {
		if (value > 9_999_999_999) throw new Error('file too large for the 10-digit /ByteRange slots');
		signed.set(encoder.encode(String(value).padStart(10, '0')), byteRangeOffset + index * 11);
	});

	const digest = await digestOf(
		algorithm.digestName,
		concat([signed.subarray(0, contentsOffset), signed.subarray(contentsEnd)])
	);
	const signedAttributes = signedAttributesOf(digest, signingTime, certificateHash);
	const signature = await signData(options.privateKey, algorithm, signedAttributes);
	// The TSA timestamps the signature value — the bytes of the `/Contents` octet string.
	const token = timeStampUrl ? await requestTimeStamp(timeStampUrl, signature) : null;
	const cms = cmsOf({
		certificate,
		chain,
		signedAttributes,
		signature,
		unsignedAttributes: token ? timeStampAttributes(token) : undefined,
		algorithm
	});
	if (cms.length * 2 > contentsHexLength) {
		throw new Error('CMS signature does not fit the reserved /Contents placeholder');
	}

	const padded = [...cms]
		.map((byte) => byte.toString(16).padStart(2, '0'))
		.join('')
		.padEnd(contentsHexLength, '0');
	signed.set(encoder.encode(padded), contentsOffset + 1);

	return signed;
}
