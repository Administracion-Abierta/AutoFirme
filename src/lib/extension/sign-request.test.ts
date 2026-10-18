import { afterAll, beforeAll, describe, expect, it, vi } from 'vitest';
import { readFileSync, rmSync } from 'node:fs';
import { createPublicKey, verify } from 'node:crypto';
import { join } from 'node:path';
import { createCadesFixtures, FIXTURE_PIN, type CadesFixtures } from '../../test/fixtures';
import { loadSignerSession } from '../keystore';
import type { SignerSession } from '../sign';
import { objectId, parseDer, seq, tlv, type Bytes } from '../asn1';
import { confirmRequest, certificateProblem, signRequest } from './sign-request';

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
