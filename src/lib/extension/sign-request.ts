import { oid, parseDer, type Bytes } from '../asn1';
import { signCadesDetached } from '../cms';
import { signPdf } from '../pades';
import type { SignerCert } from '../sign';
import { certificateIdentity } from '../x509';
import { signXades } from '../xades';
import type { ExtensionRequest, Rpc, SignatureResult } from './runtime';
import { signCadesTriPhase } from './triphase';

export const MAX_REQUEST_BYTES = 4 * 1024 * 1024;
export type CertificateProblem = 'expired' | 'notYetValid' | 'privateKey' | 'rsa' | 'invalid' | 'ca' | 'keyUsage';
/** Local rejection before any begin RPC; the waiting request can still be cancelled or retried. */
export class RequestValidationError extends Error {
 constructor(message: string, readonly certificateProblem?: CertificateProblem) {
  super(message);
  this.name = 'RequestValidationError';
 }
}
export function certificateProblem(cert: SignerCert, now = Date.now()): CertificateProblem | null {
 if (!Number.isFinite(cert.notBefore.getTime()) || !Number.isFinite(cert.notAfter.getTime())) return 'invalid';
 if (cert.notAfter.getTime() <= now) return 'expired';
 if (cert.notBefore.getTime() > now) return 'notYetValid';
 if (!cert.key || cert.key.type !== 'private' || !cert.key.usages.includes('sign')) return 'privateKey';
 const keyAlgorithm = cert.key.algorithm as RsaHashedKeyAlgorithm;
 if (cert.algorithm?.name !== 'RSASSA-PKCS1-v1_5' || cert.algorithm.hash !== 'SHA-256' || keyAlgorithm.name !== 'RSASSA-PKCS1-v1_5' || (!Number.isInteger(keyAlgorithm.modulusLength) || keyAlgorithm.modulusLength < 2048)) return 'rsa';
 try {
  certificateIdentity(cert.der);
  const tbs = parseDer(cert.der).children[0];
  const extensions = tbs.children.find((node) => node.tag === 0xa3)?.children[0].children ?? [];
  for (const extension of extensions) {
   const id = oid(extension.children[0]);
   const value = parseDer(extension.children.at(-1)!.value);
   if (id === '2.5.29.19' && value.children[0]?.tag === 0x01 && value.children[0].value[0] !== 0) return 'ca';
   if (id === '2.5.29.15' && (value.tag !== 0x03 || !((value.value[1] ?? 0) & 0xc0))) return 'keyUsage';
  }
 } catch { return 'invalid'; }
 return null;
}
const FORMATS: Record<string, true> = { CAdES: true, PAdES: true, XAdES: true };
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
export async function signRequest(request: ExtensionRequest, cert: SignerCert, chain: readonly SignerCert[]): Promise<SignatureResult> {
 if (request.operation === 'selectCertificate') return { certificate: base64(cert.der) };
 const content = decodeContent(request);
 let signature: Bytes;
 if (request.serverUrl) {
  signature = await signCadesTriPhase(request, cert, chain, content);
 } else if (request.format === 'PAdES') {
  if (!cert.key) throw new Error('The signing certificate has no private key');
  signature = await signPdf(content, {
   certificate: cert.der,
   chain: chain.filter((entry) => entry !== cert).map((entry) => entry.der),
   privateKey: cert.key,
   signingTime: new Date(),
   name: cert.commonName,
  });
 } else if (request.format === 'XAdES') {
  const xml = await signXades({
   xml: content,
   certificates: chain.length ? chain : [cert],
   signer: cert,
   dataObject: { mimeType: 'application/xml' },
   hash: request.algorithm === 'SHA1withRSA' ? 'SHA-1' : undefined,
  });
  signature = new TextEncoder().encode(xml);
 } else {
  signature = await signCadesDetached(cert, content, { attached: request.mode === 'implicit', chain });
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
  const result = await sign(request, cert, chain);
  await send({ type: 'request:complete', id: request.id, result });
 } catch (error) {
  try { await send({ type: 'request:fail', id: request.id }); } catch { /* Worker loss is already terminal; never replay. */ }
  throw error;
 }
}
