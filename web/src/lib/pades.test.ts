/**
 * PAdES B-B golden tests.
 *
 * The signature is verified by an **independent** implementation: `openssl cms -verify` reads the
 * `/Contents` CMS and the `/ByteRange` content extracted back out of the produced PDF. No test
 * here re-uses the signing code path to check itself.
 *
 * Regenerate the committed golden fixture with `PADES_WRITE_FIXTURE=1 pnpm test` after an
 * intentional format change.
 */

import { spawnSync } from 'node:child_process';
import { webcrypto } from 'node:crypto';
import { mkdtempSync, readFileSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import {
	PDFArray,
	PDFDict,
	PDFDocument,
	PDFHexString,
	PDFName,
	PDFNumber,
	PDFRef,
	PDFStream,
	PDFString
} from 'pdf-lib';
import { describe, expect, it } from 'vitest';

import { concat, type Bytes } from './der';
import { signPdf, type PadesSignatureOptions } from './pades';

// jsdom's `crypto` has no `subtle`; the engine needs real WebCrypto.
if (!globalThis.crypto?.subtle) {
	Object.defineProperty(globalThis, 'crypto', { value: webcrypto, configurable: true });
}

// Vite rewrites `import.meta.url`, so resolve the fixtures from the vitest root (`web/`).
const FIXTURES = join(process.cwd(), 'test', 'fixtures');
const SIGNING_TIME = new Date(Date.UTC(2026, 0, 2, 3, 4, 5));
const GOLDEN = 'pades-b-b-signed.pdf';

function fixture(name: string): Uint8Array<ArrayBuffer> {
	return new Uint8Array(readFileSync(join(FIXTURES, name)));
}

function pemToDer(pem: Uint8Array): Uint8Array<ArrayBuffer> {
	const text = new TextDecoder().decode(pem);
	return new Uint8Array(Buffer.from(text.replace(/-----[^-]+-----/g, '').replace(/\s+/g, ''), 'base64'));
}

function importRsaKey(): Promise<CryptoKey> {
	// Node's WebCrypto types are not the DOM ones; the algorithm is identical.
	return webcrypto.subtle.importKey(
		'pkcs8',
		pemToDer(fixture('pades-signer.key')),
		{ name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' },
		false,
		['sign']
	) as Promise<CryptoKey>;
}

function importEcKey(): Promise<CryptoKey> {
	return webcrypto.subtle.importKey(
		'pkcs8',
		pemToDer(fixture('pades-signer-ec.key')),
		{ name: 'ECDSA', namedCurve: 'P-256' },
		false,
		['sign']
	) as Promise<CryptoKey>;
}

async function signSource(overrides: Partial<PadesSignatureOptions> = {}): Promise<Uint8Array<ArrayBuffer>> {
	return signPdf(fixture('pades-source.pdf'), {
		certificate: pemToDer(fixture('pades-signer.crt')),
		privateKey: await importRsaKey(),
		signingTime: SIGNING_TIME,
		name: 'Test Signer RSA',
		reason: 'Conformidad',
		location: 'Madrid',
		...overrides
	});
}

interface ExtractedSignature {
	document: PDFDocument;
	field: PDFDict;
	signature: PDFDict;
	byteRange: number[];
	contents: Uint8Array;
	/** The bytes the digest covers, i.e. everything outside `/Contents`. */
	content: Uint8Array;
}

/** Re-read the signature through pdf-lib, i.e. the way a viewer would. */
async function extractSignature(pdf: Bytes): Promise<ExtractedSignature> {
	const document = await PDFDocument.load(pdf);
	const acroEntry = document.catalog.get(PDFName.of('AcroForm'));
	const acroForm = acroEntry instanceof PDFRef ? document.context.lookup(acroEntry, PDFDict) : (acroEntry as PDFDict);
	const fields = acroForm.lookup(PDFName.of('Fields'), PDFArray);
	const field = document.context.lookup(fields.get(0) as PDFRef, PDFDict);
	const signature = document.context.lookup(field.get(PDFName.of('V')) as PDFRef, PDFDict);
	const byteRange = signature
		.lookup(PDFName.of('ByteRange'), PDFArray)
		.asArray()
		.map((entry) => (entry as PDFNumber).asNumber());
	const contents = signature.lookup(PDFName.of('Contents'), PDFHexString).asBytes();
	return {
		document,
		field,
		signature,
		byteRange,
		contents,
		content: concat([
			pdf.subarray(byteRange[0], byteRange[0] + byteRange[1]),
			pdf.subarray(byteRange[2], byteRange[2] + byteRange[3])
		])
	};
}

/** Independent verification: openssl parses the CMS and checks it against the byte-range bytes. */
function opensslVerify(cms: Uint8Array, content: Uint8Array): string {
	const dir = mkdtempSync(join(tmpdir(), 'pades-'));
	const cmsPath = join(dir, 'signature.cms');
	const contentPath = join(dir, 'content.bin');
	writeFileSync(cmsPath, cms);
	writeFileSync(contentPath, content);
	const result = spawnSync(
		'openssl',
		['cms', '-verify', '-binary', '-inform', 'DER', '-in', cmsPath, '-content', contentPath, '-noverify'],
		{ encoding: 'utf8' }
	);
	expect(result.status, result.stderr).toBe(0);
	return result.stderr;
}

/** `/Contents` is zero-padded; openssl stops at the end of the DER structure. */
function trimTrailingZeros(bytes: Uint8Array): Uint8Array {
	let end = bytes.length;
	while (end > 0 && bytes[end - 1] === 0) end -= 1;
	return bytes.subarray(0, end);
}

describe('PAdES B-B signing', () => {
	it('appends an incremental update that openssl can verify', async () => {
		const source = fixture('pades-source.pdf');
		const signed = await signSource();

		// Incremental update: the original document survives byte for byte.
		expect(signed.subarray(0, source.length)).toEqual(source);
		expect(signed.length).toBeGreaterThan(source.length);

		const extracted = await extractSignature(signed);
		expect(extracted.document.getPageCount()).toBe(1);
		expect(
			extracted.signature.lookup(PDFName.of('SubFilter'), PDFName).asString()
		).toBe('/ETSI.CAdES.detached');
		expect(extracted.field.lookup(PDFName.of('T'), PDFString).decodeText()).toBe('Signature1');
		expect(extracted.field.has(PDFName.of('AP'))).toBe(false);
		expect(extracted.byteRange[0]).toBe(0);

		expect(opensslVerify(trimTrailingZeros(extracted.contents), extracted.content)).toMatch(
			/Verification successful/i
		);
	});

	it('matches the committed golden fixture byte for byte', async () => {
		const signed = await signSource();
		if (process.env.PADES_WRITE_FIXTURE === '1') {
			writeFileSync(join(FIXTURES, GOLDEN), signed);
		}
		expect(signed).toEqual(fixture(GOLDEN));
	});

	it('verifies the committed golden fixture standalone', async () => {
		const golden = fixture(GOLDEN);
		const extracted = await extractSignature(golden);

		expect(golden.subarray(0, fixture('pades-source.pdf').length)).toEqual(fixture('pades-source.pdf'));
		expect(opensslVerify(trimTrailingZeros(extracted.contents), extracted.content)).toMatch(
			/Verification successful/i
		);
	});

	it('signs with an ECDSA P-256 key', async () => {
		const signed = await signSource({
			certificate: pemToDer(fixture('pades-signer-ec.crt')),
			privateKey: await importEcKey(),
			name: 'Test Signer EC'
		});

		const extracted = await extractSignature(signed);
		expect(
			extracted.signature.lookup(PDFName.of('SubFilter'), PDFName).asString()
		).toBe('/ETSI.CAdES.detached');
		expect(opensslVerify(trimTrailingZeros(extracted.contents), extracted.content)).toMatch(
			/Verification successful/i
		);
	});

	it('draws an appearance stream for a visible signature', async () => {
		const signed = await signSource({ visible: { rect: [60, 60, 260, 120] } });
		const extracted = await extractSignature(signed);

		expect(extracted.field.lookup(PDFName.of('Rect'), PDFArray).size()).toBe(4);
		const appearance = extracted.field.lookup(PDFName.of('AP'), PDFDict);
		const form = extracted.document.context.lookup(appearance.lookup(PDFName.of('N')) as PDFRef, PDFStream);
		expect(form.dict.lookup(PDFName.of('BBox'), PDFArray).asArray().map((n) => (n as PDFNumber).asNumber())).toEqual([
			0, 0, 200, 60
		]);
		expect(opensslVerify(trimTrailingZeros(extracted.contents), extracted.content)).toMatch(
			/Verification successful/i
		);
	});
});
