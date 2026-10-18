import { afterAll, beforeAll, describe, expect, it, vi } from 'vitest';
import { readFileSync, rmSync } from 'node:fs';
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
