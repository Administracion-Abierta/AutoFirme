/**
 * Portal protocol conformance: the Colegio de Registradores test page is the reference client for
 * AutoFirma's `AutoScript`. These fixtures are built with the same payload functions the live demo
 * uses (`demo/lib/registradores.mjs`), so a change to either the page contract or the adapter shows
 * up here without a browser.
 */
import { readFileSync } from 'node:fs';
import test from 'node:test';
import assert from 'node:assert/strict';
import { parseCall, validateRequest } from '../core/contract.js';
import { wrapAutoScript } from '../core/adapter.js';
import {
	FORMATS,
	MODE,
	formPayload,
	generatedPayload,
	sha256Hex,
} from '../../demo/lib/registradores.mjs';

const success = () => {};
const failure = () => {};
const sign = (data, format = 'CAdES', params = MODE) =>
	parseCall('sign', [data.toString('base64'), 'SHA256withRSA', format, params, success, failure]);

test('Datos generados signs the page payload as explicit detached CAdES', () => {
	const payload = generatedPayload();
	const request = sign(payload);

	assert.equal(request?.operation, 'sign');
	assert.equal(request?.format, 'CAdES');
	assert.equal(request?.mode, 'explicit');
	assert.equal(request?.algorithm, 'SHA256withRSA');
	assert.deepEqual(Buffer.from(request?.data ?? '', 'base64'), payload);
	assert.equal(sha256Hex(Buffer.from(request?.data ?? '', 'base64')), sha256Hex(payload));
	// 10 chars doubled 13 times, then the page trims 2: the live page's payload is this size.
	assert.equal(payload.length, 81_918);
});

test('Formulario signs the exact bytes the page serialises', () => {
	const payload = formPayload({ nif: '12345678Z', nombre: 'Prueba AutoFirme Demo' });
	const request = sign(payload);

	assert.equal(request?.format, 'CAdES');
	assert.equal(request?.mode, 'explicit');
	assert.equal(Buffer.from(request?.data ?? '', 'base64').toString('utf8'),
		'fNif=12345678Z&fNombre=Prueba%20AutoFirme%20Demo');
});

test('every format the page offers maps to the documented contract', () => {
	const xml = Buffer.from('<Factura/>');
	const pdf = Buffer.from('%PDF-1.7\n');
	const data = generatedPayload();

	assert.equal(sign(data, 'CAdES', MODE)?.format, 'CAdES');
	assert.equal(sign(data, 'AUTO', MODE)?.format, 'CAdES');
	assert.equal(sign(xml, 'XAdES', MODE), null, 'detached XAdES is handed to desktop AutoFirma');
	assert.equal(sign(xml, 'XAdES', 'mode=implicit')?.format, 'XAdES');
	assert.equal(sign(pdf, 'PAdES', MODE)?.format, 'PAdES');
	assert.equal(sign(pdf, 'AUTO', MODE)?.format, 'PAdES');
	assert.deepEqual(FORMATS, ['CAdES', 'XAdES', 'PAdES', 'AUTO']);
});

test('AUTO with PAdES stamp parameters keeps the full stamp bag across the worker boundary', () => {
	const pdf = Buffer.from('%PDF-1.7\n');
	const stamp = [
		'visibleSignature=want',
		'signaturePositionOnPageLowerLeftX=45',
		'signaturePositionOnPageLowerLeftY=80',
		'signaturePositionOnPageUpperRightX=560',
		'signaturePositionOnPageUpperRightY=140',
		'signaturePage=-1',
		'layer2Text=Firmado por $$SUBJECTCN$$',
		'signReason=Conforme',
	].join('\n');
	const request = sign(pdf, 'AUTO', stamp);
	assert.equal(request?.format, 'PAdES');
	// The worker re-validates the forwarded request; the round-trip must be lossless.
	assert.deepEqual(validateRequest({ ...request }), request);
	const plain = sign(pdf, 'PAdES', stamp);
	assert.deepEqual(request?.params, plain?.params, 'AUTO must not drop stamp parameters');
});

test('AUTO with stamp parameters on a non-PDF payload falls back to desktop AutoFirma', () => {
	const data = generatedPayload();
	const stamp = 'visibleSignature=want\nsignaturePage=-1';
	assert.equal(sign(data, 'AUTO', stamp), null, 'stamp keys are meaningless without a PDF');
});

// OOXML rides AutoScript like the desktop app's `AOOOXMLSigner` entry in `AOSignerFactory`.
test('an OOXML package signs through the contract, stamped parameters refused', () => {
	const docx = Buffer.from('PK\x03\x04[Content_Types].xml');
	const request = sign(docx, 'OOXML', 'mode=implicit');

	assert.deepEqual(request, {
		operation: 'sign',
		data: docx.toString('base64'),
		algorithm: 'SHA256withRSA',
		format: 'OOXML',
		mode: 'implicit'
	});
	// Visible-signature parameters are PAdES-only in the contract, as in AutoFirma.
	assert.equal(sign(docx, 'OOXML', 'mode=implicit\nvisibleSignature=want'), null);
});

test('selectCertificate replays the page call shape', () => {
	const request = parseCall('selectCertificate', [null, success, failure]);
	assert.deepEqual(request, { operation: 'selectCertificate' });
	// The page passes null as the first argument; an API key list is not supported.
	assert.equal(parseCall('selectCertificate', ['key', success, failure]), null);
});

test('the adapter routes a registradores sign call through the transport', async () => {
	const data = generatedPayload();
	const seen = [];
	const api = { sign: (...args) => seen.push(args) };
	wrapAutoScript(api, async (request) => {
		assert.deepEqual(request, sign(data));
		return { kind: 'result', certificate: 'Y2VydA==', signature: 'c2ln' };
	});

	const result = await new Promise((resolve, reject) => {
		api.sign(data.toString('base64'), 'SHA256withRSA', 'CAdES', MODE,
			(signature, certificate) => resolve({ signature, certificate }), reject);
	});
	assert.deepEqual(result, { signature: 'c2ln', certificate: 'Y2VydA==' });
	assert.equal(seen.length, 0, 'the original AutoFirma implementation must not run');
});

test('an unsupported page call falls through to desktop AutoFirma unchanged', () => {
	const calls = [];
	const api = { sign: (...args) => calls.push(args) };
	wrapAutoScript(api, async () => {
		throw new Error('transport must not be called for an unsupported request');
	});

	const args = [Buffer.from('<Factura/>').toString('base64'), 'SHA256withRSA', 'XAdES', MODE,
		success, failure];
	api.sign(...args);
	assert.deepEqual(calls, [args]);
});

// Protocol 4 in-page (Refs #85): the adapter is method-level interception, so a portal that
// used AutoFirma's localhost socket / `afirma://` scheme needs no native helper here — and
// none may be declared, or the in-page guarantee silently stops holding.
test('protocol-4 portals stay in-page: no native helper is declared', () => {
	const manifest = JSON.parse(readFileSync(new URL('../manifest.json', import.meta.url), 'utf8'));
	assert.equal(manifest.permissions.includes('nativeMessaging'), false,
		'a native messaging host would put protocol-4 portals back on the desktop helper');
});
