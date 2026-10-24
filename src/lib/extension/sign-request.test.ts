import { afterAll, beforeAll, describe, expect, it, vi } from 'vitest';
import { readFileSync, rmSync } from 'node:fs';
import { createPublicKey, verify } from 'node:crypto';
import { join } from 'node:path';
import { PDFArray, PDFDict, PDFDocument, PDFName, PDFNumber, PDFRef, PDFStream, PDFString } from 'pdf-lib';
import { unzipSync, zipSync } from 'fflate';
import { createCadesFixtures, FIXTURE_PIN, type CadesFixtures } from '../../test/fixtures';
import { loadSignerSession } from '../keystore';
import type { SignerSession } from '../sign';
import { objectId, parseDer, seq, tlv, type Bytes } from '../asn1';
import { certificateProblem } from './certificate';
import { confirmRequest, signRequest } from './sign-request';

let fixtures: CadesFixtures;
let session: SignerSession;
beforeAll(async () => { fixtures = createCadesFixtures(); session = await loadSignerSession(fixtures.p12, FIXTURE_PIN); });
afterAll(() => rmSync(fixtures.dir, { recursive: true, force: true }));
const request = { id: 'request-1', origin: 'https://example.com', operation: 'sign' as const, format: 'CAdES', mode: 'explicit' as const, data: btoa('document') };

describe('website request consent', () => {
 it('checks validity, key presence, RSA and minimum key length before beginning', async () => {
  expect(certificateProblem(session.signer)).toBeNull();
  expect(certificateProblem({ ...session.signer, notAfter: new Date(0) })).toBe('expired');
  expect(certificateProblem({ ...session.signer, notBefore: new Date(Date.now() + 60000) })).toBe('notYetValid');
  expect(certificateProblem({ ...session.signer, key: null })).toBe('privateKey');
  const ec = await loadSignerSession(fixtures.ecP12, FIXTURE_PIN);
  expect(certificateProblem(ec.signer)).toBe('rsa');
  const weak = { ...session.signer, key: { type: 'private', extractable: false, usages: ['sign'], algorithm: { name: 'RSASSA-PKCS1-v1_5', modulusLength: 1024 } } as CryptoKey };
  expect(certificateProblem(weak)).toBe('rsa');
 });
 it('begins before cryptography and completes only once after it', async () => {
  const calls: string[] = [];
  const rpc = async (message: {type: string}) => { calls.push(message.type); return true; };
  await confirmRequest(request, session.signer, session.certificates, rpc, async () => { calls.push('crypto'); return { certificate: 'cert', signature: 'signature' }; });
  expect(calls).toEqual(['request:begin', 'crypto', 'request:complete']);
 });
 it('does not begin with an expired or malformed certificate', async () => {
  const rpc = vi.fn();
  await expect(confirmRequest(request, { ...session.signer, notAfter: new Date(0) }, [], rpc)).rejects.toThrow('expired');
  await expect(confirmRequest(request, { ...session.signer, der: new Uint8Array([0]) }, [], rpc)).rejects.toThrow();
  expect(rpc).not.toHaveBeenCalled();
 });
 it('never signs when begin is rejected, and never requests fallback after crypto failure', async () => {
  const signer = vi.fn(async () => ({ certificate: 'cert' }));
  const denied = vi.fn(async () => { throw new Error('stale request'); });
  await expect(confirmRequest(request, session.signer, [], denied, signer)).rejects.toThrow('stale');
  expect(signer).not.toHaveBeenCalled();
  const messages: string[] = [];
  await expect(confirmRequest(request, session.signer, [], async (m) => { messages.push(m.type); return true; }, async () => { throw new Error('crypto failed'); })).rejects.toThrow('crypto failed');
  expect(messages).toEqual(['request:begin', 'request:fail']);
 });
 it('bounds and validates website input before beginning', async () => {
  const rpc = vi.fn();
  await expect(confirmRequest({ ...request, data: 'not base64?' }, session.signer, [], rpc)).rejects.toThrow();
  expect(rpc).not.toHaveBeenCalled();
 });
 it('returns only public certificate for certificate-selection requests', async () => {
  const result = await signRequest({ id: 'select', origin: request.origin, operation: 'selectCertificate' }, session.signer, session.certificates);
  expect(result).toEqual({ certificate: expect.any(String) });
 });
});

describe('format dispatch', () => {
 it('signs PDFs as PAdES and XML as enveloped XAdES', async () => {
  const pdf = readFileSync(join(process.cwd(), 'test/fixtures/pades-source.pdf'));
  const signed = await signRequest({ ...request, format: 'PAdES', data: pdf.toString('base64') }, session.signer, session.certificates);
  expect(Buffer.from(signed.signature!, 'base64').subarray(0, 5).toString()).toBe('%PDF-');

  const xml = '<?xml version="1.0"?><root><value>1</value></root>';
  const enveloped = await signRequest({ ...request, format: 'XAdES', mode: 'implicit', data: Buffer.from(xml).toString('base64') }, session.signer, session.certificates);
  expect(Buffer.from(enveloped.signature!, 'base64').toString()).toContain('<ds:Signature');
 });

 it('keeps the legacy SHA1withRSA XAdES path for old portals', async () => {
  const xml = '<?xml version="1.0"?><root><value>1</value></root>';
  const legacy = await signRequest({ ...request, format: 'XAdES', mode: 'implicit', algorithm: 'SHA1withRSA', data: Buffer.from(xml).toString('base64') }, session.signer, session.certificates);
  const text = Buffer.from(legacy.signature!, 'base64').toString();
  expect(text).toContain('http://www.w3.org/2000/09/xmldsig#rsa-sha1');
  expect(text).toContain('<ds:Signature');
 });

 it('applies AutoFirma’s visible-signature parameters', async () => {
  const pdf = readFileSync(join(process.cwd(), 'test/fixtures/pades-source.pdf'));
  const stamped = await signRequest({
   ...request,
   format: 'PAdES',
   data: pdf.toString('base64'),
   params: {
    visibleSignature: 'want',
    signaturePositionOnPageLowerLeftX: '45',
    signaturePositionOnPageLowerLeftY: '80',
    signaturePositionOnPageUpperRightX: '560',
    signaturePositionOnPageUpperRightY: '140',
    signaturePage: '-1',
    layer2Text: 'Firmado por $$SUBJECTCN$$ el $$SIGNDATE=dd/MM/yyyy (HH:mm:ss)$$',
    layer2FontFamily: '1',
    layer2FontSize: '8',
    layer2FontStyle: '2',
    layer2FontColor: 'gray',
    signReason: 'Conforme',
    signatureProductionCity: 'Madrid',
    signerContact: 'signer@example.test'
   }
  }, session.signer, session.certificates);

  const source = Uint8Array.from(Buffer.from(stamped.signature!, 'base64'));
  const document = await PDFDocument.load(source);
  const acroEntry = document.catalog.get(PDFName.of('AcroForm'));
  const acroForm = acroEntry instanceof PDFRef ? document.context.lookup(acroEntry, PDFDict) : (acroEntry as PDFDict);
  const field = document.context.lookup(acroForm.lookup(PDFName.of('Fields'), PDFArray).get(0) as PDFRef, PDFDict);
  expect(field.lookup(PDFName.of('Rect'), PDFArray).asArray().map((entry) => (entry as PDFNumber).asNumber())).toEqual([45, 80, 560, 140]);
  const signature = document.context.lookup(field.get(PDFName.of('V')) as PDFRef, PDFDict);
  expect(signature.lookup(PDFName.of('Reason'), PDFString).decodeText()).toBe('Conforme');
  expect(signature.lookup(PDFName.of('Location'), PDFString).decodeText()).toBe('Madrid');
  expect(signature.lookup(PDFName.of('ContactInfo'), PDFString).decodeText()).toBe('signer@example.test');
  const appearance = field.lookup(PDFName.of('AP'), PDFDict);
  const form = document.context.lookup(appearance.lookup(PDFName.of('N')) as PDFRef, PDFStream);
  const content = new TextDecoder('latin1').decode(form.getContents());
  expect(content).toContain(`(Firmado por ${session.signer.commonName} el `);
  expect(content).not.toContain('$$');
  expect(content).toContain('BT /F1 8 Tf 0.5 0.5 0.5 rg');
  const font = form.dict.lookup(PDFName.of('Resources'), PDFDict).lookup(PDFName.of('Font'), PDFDict);
  const fontRef = font.get(PDFName.of('F1')) as PDFRef;
  expect(document.context.lookup(fontRef, PDFDict).lookup(PDFName.of('BaseFont'), PDFName).asString()).toBe('/Helvetica-Oblique');
 });

 it('signs an OOXML package as a new ZIP with the signature part inside', async () => {
  const documentXml = new TextEncoder().encode(
   '<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:body/></w:document>'
  );
  const docx = zipSync({
   '[Content_Types].xml': new TextEncoder().encode(
    '<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">' +
     '<Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>' +
     '<Default Extension="xml" ContentType="application/xml"/></Types>'
   ),
   '_rels/.rels': new TextEncoder().encode(
    '<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"/>'
   ),
   'word/document.xml': documentXml
  });
  const signed = await signRequest(
   { ...request, format: 'OOXML', mode: 'implicit', data: Buffer.from(docx).toString('base64') },
   session.signer,
   session.certificates
  );
  const entries = unzipSync(Uint8Array.from(Buffer.from(signed.signature!, 'base64')));
  expect(Object.keys(entries)).toEqual(expect.arrayContaining([
   '[Content_Types].xml', 'word/document.xml', '_xmlsignatures/origin.sigs'
  ]));
  expect(Object.keys(entries).some((name) => /^_xmlsignatures\/sig-[\w-]+\.xml$/.test(name))).toBe(true);
  // The original parts survive untouched: Word only reopens a package it can re-read.
  expect(Buffer.from(entries['word/document.xml'])).toEqual(Buffer.from(documentXml));
 });
});


it('completes a three-phase CAdES signature with the server-provided pre-sign', async () => {
 const content = new TextEncoder().encode('123e4567-e89b-12d3-a456-426614174000');
 const pre = Uint8Array.from({ length: 64 }, (_, index) => (index * 37) & 0xff);
 const final = Uint8Array.from([0xde, 0xad, 0xbe, 0xef]);
 const urlSafe = (bytes: Uint8Array) => Buffer.from(bytes).toString('base64').replace(/\+/g, '-').replace(/\//g, '_');
 const fromUrlSafe = (value: string) => Buffer.from(value.replace(/-/g, '+').replace(/_/g, '/'), 'base64');
 const reply = (body: string) => ({ ok: true, status: 200, arrayBuffer: async () => new TextEncoder().encode(body).buffer });
 let preCalled = false;
 const fetchMock = vi.fn(async (input: string | URL | Request, init?: RequestInit) => {
  const url = new URL(String(input));
  expect(init?.method).toBe('POST');
  expect(url.searchParams.get('cop')).toBe('sign');
  expect(url.searchParams.get('format')).toBe('CAdES');
  expect(url.searchParams.get('algo')).toBe('SHA512withRSA');
  expect(url.searchParams.get('cert')?.split(',')[0]).toBe(urlSafe(session.signer.der));
  expect(fromUrlSafe(url.searchParams.get('params')!).toString()).toBe('precalculatedHashAlgorithm=SHA-512\n');
  if (url.searchParams.get('op') === 'pre') {
   expect(url.searchParams.get('doc')).toBe(urlSafe(content));
   preCalled = true;
   const xml = `<xml><firmas format="CAdES"><firma Id="1"><param n="PRE">${Buffer.from(pre).toString('base64')}</param><param n="NEED_PRE">false</param></firma></firmas></xml>`;
   return reply(Buffer.from(xml).toString('base64'));
  }
  const xml = fromUrlSafe(url.searchParams.get('session')!).toString();
  const pk1 = /<param n="PK1">([^<]+)<\/param>/.exec(xml)?.[1];
  expect(pk1).toBeTruthy();
  expect(verify('sha512', pre, createPublicKey(readFileSync(fixtures.leafPath)), fromUrlSafe(pk1!))).toBe(true);
  expect(xml).not.toContain('n="PRE"');
  expect(xml).toContain('<firmas format="CAdES">');
  return reply('OK NEWID=' + Buffer.from(final).toString('base64url'));
 });
 vi.stubGlobal('fetch', fetchMock);
 try {
  const result = await signRequest({
   id: 'tri', origin: request.origin, operation: 'sign', format: 'CAdES', mode: 'implicit',
   algorithm: 'SHA512withRSA', serverUrl: 'https://sign.test/api',
   params: { precalculatedHashAlgorithm: 'SHA-512' }, data: Buffer.from(content).toString('base64')
  }, session.signer, session.certificates);
  expect(preCalled).toBe(true);
  expect(fetchMock).toHaveBeenCalledTimes(2);
  expect(Buffer.from(result.signature!, 'base64')).toEqual(Buffer.from(final));
 } finally { vi.unstubAllGlobals(); }
});

it('rejects CA certificates and keys restricted to other uses', () => {
 const withExtension = (id: string, value: Bytes) => {
  const original = parseDer(session.signer.der);
  const fields = original.children[0].children.filter((node) => node.tag !== 0xa3).map((node) => node.raw);
  const der = seq(seq(...fields, tlv(0xa3, seq(seq(objectId(id), tlv(0x04, value))))), ...original.children.slice(1).map((node) => node.raw));
  return { ...session.signer, der };
 };
 expect(certificateProblem(withExtension('2.5.29.19', seq(tlv(0x01, new Uint8Array([255])))))) .toBe('ca');
 expect(certificateProblem(withExtension('2.5.29.15', tlv(0x03, new Uint8Array([5, 0x20]))))).toBe('keyUsage');
 expect(certificateProblem(withExtension('2.5.29.15', tlv(0x03, new Uint8Array([7, 0x80]))))).toBeNull();
});
