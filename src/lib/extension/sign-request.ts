import type { Bytes } from '../asn1';
import { signCadesDetached } from '../cms';
import { signOoxmlPackage } from '../ooxml';
import { signPdf } from '../pades';
import type { SignerCert } from '../sign';
import { signXades } from '../xades';
import { certificateProblem, type CertificateProblem } from './certificate';
import { MAX_REQUEST_BYTES } from './limits';
import type { ExtensionRequest, Rpc, SignatureResult } from './runtime';
import { signCadesTriPhase } from './triphase';

/** Local rejection before any begin RPC; the waiting request can still be cancelled or retried. */
export class RequestValidationError extends Error {
 constructor(message: string, readonly certificateProblem?: CertificateProblem) {
  super(message);
  this.name = 'RequestValidationError';
 }
}
const FORMATS: Record<string, true> = { CAdES: true, PAdES: true, XAdES: true, OOXML: true };
const FONT_COLORS: Record<string, [number, number, number]> = {
	black: [0, 0, 0],
	white: [1, 1, 1],
	gray: [0.5, 0.5, 0.5],
	lightGray: [0.75, 0.75, 0.75],
	darkGray: [0.25, 0.25, 0.25],
	red: [1, 0, 0],
	pink: [1, 0.75, 0.8]
};
const two = (value: number) => String(value).padStart(2, '0');
/** The `$$SIGNDATE$$` subset of Java's SimpleDateFormat used by public portals. */
function formatSignDate(date: Date, pattern?: string): string {
	if (!pattern) return new Intl.DateTimeFormat('es-ES', { dateStyle: 'short', timeStyle: 'medium' }).format(date);
	const names = new Intl.DateTimeFormat('es-ES', { timeZoneName: 'short' }).formatToParts(date);
	const zone = names.find((part) => part.type === 'timeZoneName')?.value ?? '';
	const tokens: Record<string, string> = {
		yyyy: String(date.getFullYear()),
		yy: two(date.getFullYear() % 100),
		MM: two(date.getMonth() + 1),
		dd: two(date.getDate()),
		HH: two(date.getHours()),
		mm: two(date.getMinutes()),
		ss: two(date.getSeconds()),
		z: zone
	};
	return pattern.replace(/yyyy|yy|MM|dd|HH|mm|ss|z/g, (token) => tokens[token]);
}
function layerText(text: string, cert: SignerCert, date: Date, params: Record<string, string>): string[] {
	const serial = cert.serial ? BigInt(`0x${cert.serial}`).toString() : '';
	const expanded = text
		.replaceAll('$$SUBJECTCN$$', cert.commonName)
		.replaceAll('$$SUBJECTDN$$', cert.subject)
		.replaceAll('$$ISSUERCN$$', cert.issuer)
		.replaceAll('$$CERTSERIAL$$', serial)
		.replaceAll('$$REASON$$', params.signReason ?? '')
		.replaceAll('$$LOCATION$$', params.signatureProductionCity ?? '')
		.replaceAll('$$CONTACT$$', params.signerContact ?? '')
		.replace(/\$\$SIGNDATE(?:=([^$]*))?\$\$/g, (_, pattern: string | undefined) => formatSignDate(date, pattern));
	return expanded.split(/\r?\n/);
}
function defaultLines(cert: SignerCert, date: Date, params: Record<string, string>): string[] {
	const lines = [`Firmado por ${cert.commonName}`, `Fecha: ${formatSignDate(date)}`];
	if (params.signReason) lines.push(`Motivo: ${params.signReason}`);
	if (params.signatureProductionCity) lines.push(`Lugar: ${params.signatureProductionCity}`);
	if (params.signerContact) lines.push(`Contacto: ${params.signerContact}`);
	return lines;
}
/** Translate AutoFirma's visible-signature parameters into the local appearance options. */
function visibleSignature(request: ExtensionRequest, cert: SignerCert, date: Date) {
	const params = request.params ?? {};
	if (params.signaturePositionOnPageLowerLeftX === undefined) return undefined;
	const image = params.signatureRubricImage;
	// AutoFirma pages are one-based; absent or -1 means the last page. It ignores layer2Text
	// when a rubric image is present.
	const page = params.signaturePage === undefined || params.signaturePage === '-1' ? -1 : Number(params.signaturePage) - 1;
	const lines = image
		? []
		: params.layer2Text !== undefined
			? layerText(params.layer2Text, cert, date, params)
			: defaultLines(cert, date, params);
	return {
		page,
		autoFirmaRect: [
			Number(params.signaturePositionOnPageLowerLeftX),
			Number(params.signaturePositionOnPageLowerLeftY),
			Number(params.signaturePositionOnPageUpperRightX),
			Number(params.signaturePositionOnPageUpperRightY)
		] as [number, number, number, number],
		lines,
		...(image ? { image: Uint8Array.from(atob(image), (character) => character.charCodeAt(0)) } : {}),
		...(params.layer2FontFamily !== undefined ? { fontFamily: Number(params.layer2FontFamily) as 0 | 1 | 2 } : {}),
		...(params.layer2FontSize !== undefined ? { fontSize: Number(params.layer2FontSize) } : {}),
		...(params.layer2FontStyle !== undefined ? { fontStyle: Number(params.layer2FontStyle) as 0 | 1 | 2 | 3 } : {}),
		...(params.layer2FontColor ? { fontColor: FONT_COLORS[params.layer2FontColor] } : {})
	};
}
function decodeContent(request: ExtensionRequest): Bytes {
 if (request.operation !== 'sign' || !Object.hasOwn(FORMATS, request.format ?? '') || !['explicit', 'implicit'].includes(request.mode ?? '')) throw new Error('Unsupported request');
 const value = request.data;
 if (typeof value !== 'string' || !value.length || value.length > Math.ceil(MAX_REQUEST_BYTES / 3) * 4 || value.length % 4 || !/^[A-Za-z0-9+/]+={0,2}$/.test(value)) throw new Error('Invalid or oversized request data');
 const raw = atob(value);
 if (raw.length > MAX_REQUEST_BYTES) throw new Error('Request too large');
 return Uint8Array.from(raw, (character) => character.charCodeAt(0));
}
export function base64(bytes: Bytes): string {
 let raw = '';
 for (let offset = 0; offset < bytes.length; offset += 8192) raw += String.fromCharCode(...bytes.subarray(offset, offset + 8192));
 return btoa(raw);
}
export async function signRequest(request: ExtensionRequest, cert: SignerCert, chain: readonly SignerCert[], signingTime = new Date()): Promise<SignatureResult> {
 if (request.operation === 'selectCertificate') return { certificate: base64(cert.der) };
 const content = decodeContent(request);
 let signature: Bytes;
 if (request.serverUrl) {
  signature = await signCadesTriPhase(request, cert, chain, content);
 } else if (request.format === 'PAdES') {
  if (!cert.key) throw new Error('The signing certificate has no private key');
  const params = request.params ?? {};
  signature = await signPdf(content, {
   certificate: cert.der,
   chain: chain.filter((entry) => entry !== cert).map((entry) => entry.der),
   privateKey: cert.key,
   signingTime,
   name: cert.commonName,
   reason: params.signReason,
   location: params.signatureProductionCity,
   contact: params.signerContact,
   visible: visibleSignature(request, cert, signingTime),
  });
 } else if (request.format === 'XAdES') {
  const xml = await signXades({
   xml: content,
   certificates: chain.length ? chain : [cert],
   signer: cert,
   dataObject: { mimeType: 'application/xml' },
   hash: request.algorithm === 'SHA1withRSA' ? 'SHA-1' : undefined,
   now: signingTime,
  });
  signature = new TextEncoder().encode(xml);
 } else if (request.format === 'OOXML') {
  // RSA only, like the App dispatcher: signOoxmlPackage rejects non-RSA keys. Refs #127.
  if (!cert.key) throw new Error('The signing certificate has no private key');
  signature = (await signOoxmlPackage(content, {
   key: cert.key,
   certificates: [cert.der, ...chain.filter((entry) => entry !== cert).map((entry) => entry.der)],
   signingTime,
  })) as Bytes;
 } else {
  signature = await signCadesDetached(cert, content, { attached: request.mode === 'implicit', chain, signingTime });
 }
 return { certificate: base64(cert.der), signature: base64(signature) };
}
/** Called only by explicit confirmation. The begin transition prevents unsafe native replay. */
export async function confirmRequest(request: ExtensionRequest, cert: SignerCert, chain: readonly SignerCert[], send: Rpc, sign = signRequest): Promise<void> {
 const problem = certificateProblem(cert);
 if (problem) throw new RequestValidationError(problem, problem);
 if (request.operation !== 'selectCertificate') {
  try { decodeContent(request); }
  catch (reason) { throw new RequestValidationError(reason instanceof Error ? reason.message : String(reason)); }
 }
 await send({ type: 'request:begin', id: request.id });
 try {
  const result = await sign(request, cert, chain, request.signingTime ? new Date(request.signingTime) : undefined);
  await send({ type: 'request:complete', id: request.id, result });
 } catch (error) {
  try { await send({ type: 'request:fail', id: request.id }); } catch { /* Worker loss is already terminal; never replay. */ }
  throw error;
 }
}
