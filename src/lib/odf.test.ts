/**
 * ODF signing: a minimal ODT is built here, signed, and the package re-read so
 * every digest, the RSA signature, the manifest entry and the ZIP layout are
 * asserted — plus `xmlsec1` (the XMLDSig engine LibreOffice links) over the
 * unpacked package, `openssl dgst -verify` (independent RSA), an xmllint C14N
 * cross-check and a LibreOffice convert round-trip where the binaries exist.
 */
import { execFileSync } from 'node:child_process';
import { mkdtempSync, mkdirSync, readFileSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { dirname, join } from 'node:path';
import { pathToFileURL } from 'node:url';
import { webcrypto } from 'node:crypto';
import { beforeAll, describe, expect, it } from 'vitest';
import { unzipSync, zipSync } from 'fflate';
import { canonicalizeBytes } from './c14n';
import { certificateIdentity } from './ooxml';
import { xmlsec1Verify } from '../test/fixtures';
import { signOdfPackage } from './odf';

if (!globalThis.crypto?.subtle) Object.defineProperty(globalThis, 'crypto', { value: webcrypto });

const DS_NS = 'http://www.w3.org/2000/09/xmldsig#';
const MANIFEST_NS = 'urn:oasis:names:tc:opendocument:xmlns:manifest:1.0';
const DECODER = new TextDecoder();
const artifacts = mkdtempSync(join(tmpdir(), 'odf-'));

function has(binary: string): boolean {
	try {
		execFileSync(binary, binary === 'soffice' || binary === 'xmllint' ? ['--version'] : ['version'], { stdio: 'ignore' });
		return true;
	} catch {
		return false;
	}
}

const hasOpenssl = has('openssl');
const hasXmllint = has('xmllint');
const hasSoffice = has('soffice');

/**
 * Unpack a signed package into a directory, as an ODF consumer sees it: xmlsec1 and LibreOffice
 * both resolve the signature's package-relative URIs against the unpacked layout.
 */
function writePackage(bytes: Uint8Array, root: string): void {
	for (const [name, content] of Object.entries(unzipSync(bytes))) {
		const path = join(root, name);
		mkdirSync(dirname(path), { recursive: true });
		writeFileSync(path, content);
	}
}

let signingKey: CryptoKey;
let publicKey: CryptoKey;
let certificates: Uint8Array[];
let certPath: string;
let keyPath: string;
let publicPath: string;

function fromBase64(value: string): Uint8Array {
	return Uint8Array.from(atob(value), (char) => char.charCodeAt(0));
}

function toBase64(bytes: Uint8Array): string {
	let binary = '';
	for (const byte of bytes) binary += String.fromCharCode(byte);
	return btoa(binary);
}

function pemToDer(pem: string): Uint8Array {
	return fromBase64(pem.replace(/-----[^-]+-----/g, '').replace(/\s+/g, ''));
}

function parse(xml: Uint8Array | string): Document {
	return new DOMParser().parseFromString(typeof xml === 'string' ? xml : DECODER.decode(xml), 'application/xml');
}

function elements(root: Document | Element, namespace: string, local: string): Element[] {
	return Array.from(root.getElementsByTagNameNS(namespace, local));
}

const MANIFEST_XML = `<?xml version="1.0" encoding="UTF-8"?>
<manifest:manifest xmlns:manifest="${MANIFEST_NS}" manifest:version="1.2">
 <manifest:file-entry manifest:full-path="/" manifest:version="1.2" manifest:media-type="application/vnd.oasis.opendocument.text"/>
 <manifest:file-entry manifest:full-path="content.xml" manifest:media-type="text/xml"/>
 <manifest:file-entry manifest:full-path="styles.xml" manifest:media-type="text/xml"/>
 <manifest:file-entry manifest:full-path="meta.xml" manifest:media-type="text/xml"/>
</manifest:manifest>`;

const CONTENT_XML = `<?xml version="1.0" encoding="UTF-8"?>
<office:document-content xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" office:version="1.2">
<office:body><office:text><text:p>Hola</text:p></office:text></office:body>
</office:document-content>`;

const STYLES_XML = `<?xml version="1.0" encoding="UTF-8"?>
<office:document-styles xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" office:version="1.2"/>`;

const META_XML = `<?xml version="1.0" encoding="UTF-8"?>
<office:document-meta xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" xmlns:dc="http://purl.org/dc/elements/1.1/" office:version="1.2">
<office:meta><dc:title>Prueba</dc:title></office:meta>
</office:document-meta>`;

/** Minimal but valid ODT: mimetype + content/styles/meta + manifest. */
function odfFixture(): Record<string, Uint8Array> {
	const encode = (text: string) => new TextEncoder().encode(text);
	return {
		mimetype: encode('application/vnd.oasis.opendocument.text'),
		'content.xml': encode(CONTENT_XML),
		'styles.xml': encode(STYLES_XML),
		'meta.xml': encode(META_XML),
		'META-INF/manifest.xml': encode(MANIFEST_XML)
	};
}

interface Signed {
	bytes: Uint8Array;
	entries: Record<string, Uint8Array>;
	signatures: Document;
	manifest: Document;
}

function readSigned(bytes: Uint8Array): Signed {
	const entries = unzipSync(bytes);
	const signatures = parse(entries['META-INF/documentsignatures.xml']);
	const manifest = parse(entries['META-INF/manifest.xml']);
	return { bytes, entries, signatures, manifest };
}

async function sign(fixture = odfFixture()): Promise<{ bytes: Uint8Array; signed: Signed }> {
	const bytes = await signOdfPackage(zipSync(fixture, { level: 6 }), {
		key: signingKey,
		certificates,
		signingTime: new Date('2026-10-14T10:00:00Z')
	});
	writeFileSync(join(artifacts, 'signed.odt'), bytes);
	return { bytes, signed: readSigned(bytes) };
}

beforeAll(async () => {
	keyPath = join(artifacts, 'key.pem');
	certPath = join(artifacts, 'cert.pem');
	publicPath = join(artifacts, 'public.pem');
	execFileSync('openssl', [
		'req', '-x509', '-newkey', 'rsa:2048', '-nodes', '-days', '1', '-sha256',
		'-keyout', keyPath, '-out', certPath,
		'-subj', '/C=ES/O=AutoFirma Web Tests/CN=Firmante de prueba'
	]);
	certificates = [pemToDer(readFileSync(certPath, 'utf8'))];
	signingKey = await crypto.subtle.importKey(
		'pkcs8',
		pemToDer(readFileSync(keyPath, 'utf8')) as unknown as ArrayBuffer,
		{ name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' },
		false,
		['sign']
	);
	execFileSync('openssl', ['rsa', '-in', keyPath, '-pubout', '-out', publicPath]);
	publicKey = await crypto.subtle.importKey(
		'spki',
		pemToDer(readFileSync(publicPath, 'utf8')) as unknown as ArrayBuffer,
		{ name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' },
		false,
		['verify']
	);
}, 60_000);

describe('signOdfPackage', () => {
	it('keeps mimetype first and STORED, and points the manifest at documentsignatures.xml', async () => {
		const { bytes, signed } = await sign();
		const view = new DataView(bytes.buffer, bytes.byteOffset, bytes.byteLength);
		expect(view.getUint32(0, true)).toBe(0x04034b50);
		expect(view.getUint16(8, true)).toBe(0); // STORE
		const nameLength = view.getUint16(26, true);
		expect(DECODER.decode(bytes.subarray(30, 30 + nameLength))).toBe('mimetype');

		const fileEntries = Array.from(signed.manifest.getElementsByTagNameNS(MANIFEST_NS, 'file-entry'));
		const signatureEntry = fileEntries.find((entry) => entry.getAttribute('manifest:full-path') === 'META-INF/documentsignatures.xml');
		expect(signatureEntry).toBeDefined();
		expect(signed.entries['META-INF/documentsignatures.xml'].length).toBeGreaterThan(0);
		// Package files other than manifest + signature survive intact.
		for (const name of ['mimetype', 'content.xml', 'styles.xml', 'meta.xml']) {
			expect(Array.from(signed.entries[name])).toEqual(Array.from(odfFixture()[name]));
		}
	});

	it('digests every package file and verifies the RSA signature', async () => {
		const { signed } = await sign();
		const signature = elements(signed.signatures, DS_NS, 'Signature')[0];
		const references = elements(signature, DS_NS, 'Reference');
		const packageRefs = references.filter(
			(ref) => !(ref.getAttribute('URI') ?? '').startsWith('#')
		);
		expect(packageRefs.length).toBeGreaterThanOrEqual(5); // mimetype + 3 xml + manifest
		for (const ref of packageRefs) {
			const path = decodeURIComponent(ref.getAttribute('URI') ?? '');
			expect(signed.entries[path]).toBeDefined();
			const expected = toBase64(
				new Uint8Array(await crypto.subtle.digest('SHA-256', signed.entries[path] as unknown as ArrayBuffer))
			);
			const c14nParts = ['content.xml', 'meta.xml', 'styles.xml', 'settings.xml', 'META-INF/manifest.xml'];
			if (c14nParts.includes(path)) {
				const canonical = canonicalizeBytes(parse(signed.entries[path]).documentElement);
				const c14nDigest = toBase64(new Uint8Array(await crypto.subtle.digest('SHA-256', canonical as unknown as ArrayBuffer)));
				expect(ref.getElementsByTagNameNS(DS_NS, 'DigestValue')[0].textContent).toBe(c14nDigest);
				// The digest is over canonicalized octets, so the reference must say so:
				// a verifier that applies no transform computes a different digest.
				expect(
					ref
						.getElementsByTagNameNS(DS_NS, 'Transform')[0]
						?.getAttribute('Algorithm')
				).toBe('http://www.w3.org/TR/2001/REC-xml-c14n-20010315');
				expect(expected).not.toBe(c14nDigest); // canonicalization is not the identity
			} else {
				expect(ref.getElementsByTagNameNS(DS_NS, 'DigestValue')[0].textContent).toBe(expected);
			}
		}
		const signedInfo = elements(signature, DS_NS, 'SignedInfo')[0];
		// Nonstandard URIs here are rejected outright by external verifiers (DSS).
		expect(
			elements(signedInfo, DS_NS, 'SignatureMethod')[0].getAttribute('Algorithm')
		).toBe('http://www.w3.org/2001/04/xmldsig-more#rsa-sha256');
		const signatureValue = fromBase64(
			elements(signature, DS_NS, 'SignatureValue')[0].textContent ?? ''
		);
		expect(
			await crypto.subtle.verify(
				{ name: 'RSASSA-PKCS1-v1_5' },
				publicKey,
				signatureValue as unknown as ArrayBuffer,
				canonicalizeBytes(signedInfo) as unknown as ArrayBuffer
			)
		).toBe(true);
	});

	it('carries XAdES signed properties with the signing certificate and time', async () => {
		const { signed } = await sign();
		const XADES_NS = 'http://uri.etsi.org/01903/v1.3.2#';
		const properties = elements(signed.signatures, XADES_NS, 'SignedProperties')[0];
		expect(elements(properties, XADES_NS, 'SigningTime')[0].textContent).toBe('2026-10-14T10:00:00Z');
		expect(elements(properties, XADES_NS, 'SignaturePolicyImplied')).toHaveLength(1);
		expect(elements(properties, DS_NS, 'DigestValue')[0].textContent).toBe(
			toBase64(new Uint8Array(await crypto.subtle.digest('SHA-256', certificates[0] as unknown as ArrayBuffer)))
		);
		expect(elements(properties, DS_NS, 'X509IssuerName')[0].textContent).toBe(
			certificateIdentity(certificates[0]).issuer
		);
	});

	it.runIf(hasOpenssl)('openSSL validates the raw signature over the canonical SignedInfo', async () => {
		const { signed } = await sign();
		const signature = elements(signed.signatures, DS_NS, 'Signature')[0];
		const signedInfoPath = join(artifacts, 'signedinfo.bin');
		const signaturePath = join(artifacts, 'signature.bin');
		writeFileSync(signedInfoPath, canonicalizeBytes(elements(signature, DS_NS, 'SignedInfo')[0]));
		writeFileSync(signaturePath, fromBase64(elements(signature, DS_NS, 'SignatureValue')[0].textContent ?? ''));

		const result = execFileSync('openssl', ['dgst', '-sha256', '-verify', publicPath, '-signature', signaturePath, signedInfoPath]).toString();
		expect(result.trim()).toContain('Verified OK');
	});

	it.runIf(hasXmllint)('canonicalizes exactly like xmllint', async () => {
		const { signed } = await sign();
		const path = join(artifacts, 'content.xml');
		writeFileSync(path, signed.entries['content.xml']);
		expect(DECODER.decode(canonicalizeBytes(parse(signed.entries['content.xml']).documentElement))).toBe(
			execFileSync('xmllint', ['--c14n', path], { encoding: 'utf8' })
		);
	});

	it.runIf(has('xmlsec1'))(
		'is verified by xmlsec1 — the XMLDSig engine LibreOffice links — and fails once tampered',
		async () => {
			const { bytes } = await sign();
			const root = join(artifacts, 'xmlsec1');
			writePackage(bytes, root);

			// xmlsec1 resolves the package-relative reference URIs against its cwd, so it has to
			// run from the unpacked package root; the signature file is passed by path.
			const signatures = join(root, 'META-INF', 'documentsignatures.xml');
			expect(xmlsec1Verify(signatures, certPath, root)).toBe('OK');

			// Negative control: the packager's own digest is what the reference checks, so editing
			// a digested part must flip the verdict. Without this the test would pass on a
			// verifier that never read the references.
			const contentPath = join(root, 'content.xml');
			writeFileSync(contentPath, DECODER.decode(readFileSync(contentPath)).replace('Hola', 'Hola!'));
			expect(xmlsec1Verify(signatures, certPath, root)).toBe('FAIL');
		}
	);

	it.runIf(hasSoffice)('stays a readable package: LibreOffice converts the signed odt', async () => {
		const { bytes } = await sign();
		const odt = join(artifacts, 'smoke.odt');
		const out = join(artifacts, 'soffice-out');
		mkdirSync(out, { recursive: true });
		writeFileSync(odt, bytes);

		execFileSync(
			'soffice',
			[
				'--headless',
				'--norestore',
				`-env:UserInstallation=${pathToFileURL(join(artifacts, 'soffice-profile')).href}`,
				'--convert-to',
				'pdf',
				odt,
				'--outdir',
				out
			],
			{ timeout: 120_000 }
		);

		const pdf = readFileSync(join(out, 'smoke.pdf'));
		expect(pdf.subarray(0, 5).toString('latin1')).toBe('%PDF-');
		expect(pdf.byteLength).toBeGreaterThan(1000);
	}, 180_000);

	it('appends a second signature without invalidating the first (co-sign)', async () => {
		const first = await sign();
		const second = await signOdfPackage(first.bytes, {
			key: signingKey,
			certificates,
			signingTime: new Date('2026-10-15T10:00:00Z')
		});
		const signed = readSigned(second);
		const signatures = elements(signed.signatures, DS_NS, 'Signature');
		expect(signatures).toHaveLength(2);
		for (const signature of signatures) {
			const signedInfo = elements(signature, DS_NS, 'SignedInfo')[0];
			const signatureValue = fromBase64(elements(signature, DS_NS, 'SignatureValue')[0].textContent ?? '');
			expect(
				await crypto.subtle.verify(
					{ name: 'RSASSA-PKCS1-v1_5' },
					publicKey,
					signatureValue as unknown as ArrayBuffer,
					canonicalizeBytes(signedInfo) as unknown as ArrayBuffer
				)
			).toBe(true);
		}
	});

	it('refuses a package without manifest', async () => {
		const fixture = odfFixture();
		delete fixture['META-INF/manifest.xml'];
		await expect(
			signOdfPackage(zipSync(fixture, { level: 6 }), { key: signingKey, certificates })
		).rejects.toThrow(/manifest/);
	});
});
