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
import { mkdtempSync, readFileSync, rmSync, writeFileSync } from 'node:fs';
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
import { describe, expect, it, vi } from 'vitest';

import { concat, type Bytes } from './der';
import { signPdf, type PadesSignatureOptions } from './pades';
import { cmsTimeStamp, createTsaFixtures, tsVerifyToken } from '../test/fixtures';

// jsdom's `crypto` has no `subtle`; the engine needs real WebCrypto.
if (!globalThis.crypto?.subtle) {
	Object.defineProperty(globalThis, 'crypto', { value: webcrypto, configurable: true });
}

// Vite rewrites `import.meta.url`, so resolve the fixtures from the vitest root (repo root).
const FIXTURES = join(process.cwd(), 'test', 'fixtures');
const SIGNING_TIME = new Date(Date.UTC(2026, 0, 2, 3, 4, 5));
const GOLDEN = 'pades-b-b-signed.pdf';

/** Synthetic 24×12 RGBA "signature image"; the alpha channel exercises pdf-lib's `/SMask` object. */
const SIGNATURE_IMAGE = new Uint8Array(
	Buffer.from(
		'iVBORw0KGgoAAAANSUhEUgAAABgAAAAMCAYAAAB4MH11AAABXklEQVR42hXNER+nMAAA0Ph4HI/jP8fjeBwP7xeP4zgex+M47mvcveDxG4bh7/CHwEhkGv7++zGTWMisFDYqOwcnjYvOzcPLMAQBgZHIFATMJBYyK4WNys7BSeOic/Pwhi+IAgIjkSkKmEksZFYKG5Wdg5PGRefm4Y1f8BMQGIlMPwEziYXMSmGjsnNw0rjo3Dy8vy9IAgIjkSkJmEksZFYKG5Wdg5PGRefm4U1fkAUERiJTFjCTWMisFDYqOwcnjYvOzcObv6AICIxEpiJgJrGQWSlsVHYOThoXnZuHt3xBFRAYiUxVwExiIbNS2KjsHJw0Ljo3D2/9gkNAYCQyHQJmEguZlcJGZefgpHHRuXl4jy9oAgIjkakJmEksZFYKG5Wdg5PGRefm4W1f0AUERiJTFzCTWMisFDYqOwcnjYvOzcPbv+AREBiJTI+AmcRCZqWwUdk5OGlcdG4eXv4D+N9wW2VveV4AAAAASUVORK5CYII=',
		'base64'
	)
);

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

	it('draws AutoFirma’s visible box on the requested page and keeps the signature valid', async () => {
		const signed = await signSource({
			contact: 'signer@example.test',
			visible: {
				page: -1,
				autoFirmaRect: [45, 80, 560, 140],
				lines: ['Firmado por Test Signer RSA el 02/01/2026 (03:04:05)'],
				fontFamily: 1,
				fontSize: 8,
				fontStyle: 2,
				fontColor: [0.5, 0.5, 0.5]
			}
		});
		const extracted = await extractSignature(signed);

		expect(
			extracted.field
				.lookup(PDFName.of('Rect'), PDFArray)
				.asArray()
				.map((entry) => (entry as PDFNumber).asNumber())
		).toEqual([45, 80, 560, 140]);
		expect(
			extracted.signature.lookup(PDFName.of('ContactInfo'), PDFString).decodeText()
		).toBe('signer@example.test');

		const appearance = extracted.field.lookup(PDFName.of('AP'), PDFDict);
		const form = extracted.document.context.lookup(appearance.lookup(PDFName.of('N')) as PDFRef, PDFStream);
		const content = new TextDecoder('latin1').decode(form.getContents());
		expect(content).toContain('BT /F1 8 Tf 0.5 0.5 0.5 rg');
		const font = form.dict.lookup(PDFName.of('Resources'), PDFDict).lookup(PDFName.of('Font'), PDFDict);
		const fontRef = font.get(PDFName.of('F1')) as PDFRef;
		expect(
			extracted.document.context.lookup(fontRef, PDFDict).lookup(PDFName.of('BaseFont'), PDFName).asString()
		).toBe('/Helvetica-Oblique');
		expect(opensslVerify(trimTrailingZeros(extracted.contents), extracted.content)).toMatch(
			/Verification successful/i
		);
	});

	it('draws an image plus signer text in a corner block, and still verifies', async () => {
		const signed = await signSource({
			visible: {
				corner: 'bottom-right',
				image: SIGNATURE_IMAGE,
				lines: ['Test Signer RSA', 'Firmado el 2026-01-02 03:04', 'Conformidad']
			}
		});
		const extracted = await extractSignature(signed);

		// Placement: A4 page, 24 pt margin, 200×70 block, bottom-right corner.
		expect(
			extracted.field
				.lookup(PDFName.of('Rect'), PDFArray)
				.asArray()
				.map((entry) => (entry as PDFNumber).asNumber())
		).toEqual([371.28, 24, 571.28, 94]);

		const appearance = extracted.field.lookup(PDFName.of('AP'), PDFDict);
		const form = extracted.document.context.lookup(
			appearance.lookup(PDFName.of('N')) as PDFRef,
			PDFStream
		);
		const content = new TextDecoder('latin1').decode(form.getContents());
		expect(content).toContain('/Im0 Do');
		for (const line of ['Test Signer RSA', 'Firmado el 2026-01-02', '03:04', 'Conformidad']) {
			expect(content).toContain(`(${line}) Tj`);
		}
		// The date line does not fit beside the image, so it wraps whole instead of being cut.
		expect(content).not.toContain('(03:0) Tj');

		// The image travels as a resource of the form, at the PNG's own pixel size…
		const resources = form.dict.lookup(PDFName.of('Resources'), PDFDict);
		const imageRef = resources.lookup(PDFName.of('XObject'), PDFDict).get(PDFName.of('Im0'));
		expect(imageRef).toBeInstanceOf(PDFRef);
		const image = extracted.document.context.lookup(imageRef as PDFRef, PDFStream);
		expect(image.dict.lookup(PDFName.of('Subtype'), PDFName).asString()).toBe('/Image');
		expect(image.dict.lookup(PDFName.of('Width'), PDFNumber).asNumber()).toBe(24);
		expect(image.dict.lookup(PDFName.of('Height'), PDFNumber).asNumber()).toBe(12);
		// …with its alpha channel written into the same update.
		expect(image.dict.get(PDFName.of('SMask'))).toBeInstanceOf(PDFRef);

		// The drawn block is inside `/ByteRange`: it is the bytes the digest covers, not a post-pass.
		expect(new TextDecoder('latin1').decode(extracted.content)).toContain('/Im0 Do');

		expect(opensslVerify(trimTrailingZeros(extracted.contents), extracted.content)).toMatch(
			/Verification successful/i
		);
	});

	it('embeds an RFC 3161 timestamp that covers the signature value (PAdES B-T)', async () => {
		const tsa = createTsaFixtures();
		try {
			vi.stubGlobal('fetch', async (_url: string, init: RequestInit) => {
				const query = init.body;
				if (!(query instanceof Uint8Array)) throw new Error('the timestamp request has no DER body');
				return new Response(tsa.respond(query), { status: 200 });
			});

			const signed = await signSource({ timeStamp: 'https://tsa.test/' });
			vi.unstubAllGlobals();

			const extracted = await extractSignature(signed);
			const cms = trimTrailingZeros(extracted.contents);
			// openssl accepts the CMS with its unsigned timestamp attribute, over the same byte range.
			expect(opensslVerify(cms, extracted.content)).toMatch(/Verification successful/i);

			// And the token inside it covers exactly the signature value openssl just accepted.
			const { signature, token } = cmsTimeStamp(cms);
			expect(tsVerifyToken(tsa.dir, tsa.certPath, token, signature)).toMatch(/Verification: OK/);
		} finally {
			vi.unstubAllGlobals();
			rmSync(tsa.dir, { recursive: true, force: true });
		}
		});

	it('keeps accented signer text readable (UTF-16 dict strings, WinAnsi appearance)', async () => {
		const signed = await signSource({
			name: 'José García',
			reason: 'Revisión: no conforme',
			location: 'A Coruña',
			visible: { rect: [60, 60, 260, 120] }
		});
		const extracted = await extractSignature(signed);

		for (const [key, expected] of [
			['Name', 'José García'],
			['Reason', 'Revisión: no conforme'],
			['Location', 'A Coruña']
		] as const) {
			const value = extracted.signature.lookup(PDFName.of(key));
			if (!(value instanceof PDFString || value instanceof PDFHexString)) {
				throw new Error(`/${key} is not a string`);
			}
			expect(value.decodeText()).toBe(expected);
		}

		// The signature dictionary itself must stay pure ASCII: UTF-16BE+BOM hex, never raw UTF-8.
		const raw = new TextDecoder('latin1').decode(signed);
		const dictionary = raw.slice(raw.indexOf('/Type /Sig'), raw.indexOf('endobj', raw.indexOf('/Type /Sig')));
		expect(dictionary).not.toMatch(/[\u0080-\uffff]/);
		expect(dictionary).toMatch(/\/Name <feff004a006f007300e9/);
		expect(dictionary).toMatch(/\/Reason <feff/);
		expect(dictionary).toMatch(/\/Location <feff/);

		const appearance = extracted.field.lookup(PDFName.of('AP'), PDFDict);
		const form = extracted.document.context.lookup(appearance.lookup(PDFName.of('N')) as PDFRef, PDFStream);
		const contentStream = new TextDecoder('latin1').decode(form.getContents());
		expect(contentStream).toContain('(Jos\\351 Garc\\355a)');
		expect(contentStream).not.toContain('Ã');
	});
});
