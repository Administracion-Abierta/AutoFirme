import { describe, expect, it } from 'vitest';
import { zipSync } from 'fflate';
import { suggestSignFormat } from './detect';

const xml = (content: string): Uint8Array => new TextEncoder().encode(content);

function zip(files: Record<string, string>): Uint8Array {
	const entries: Record<string, Uint8Array> = {};
	for (const [name, content] of Object.entries(files)) entries[name] = xml(content);
	return zipSync(entries, { level: 1 });
}

const CONTENT_TYPES = `<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/></Types>`;
const RELS = `<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"/>`;
const ODF_MANIFEST = `<manifest:manifest xmlns:manifest="urn:oasis:names:tc:opendocument:xmlns:manifest:1.0"/>`;

function ooxml(kind: 'word' | 'xl' | 'ppt'): Uint8Array {
	return zip({
		'[Content_Types].xml': CONTENT_TYPES,
		'_rels/.rels': RELS,
		[`${kind}/document.xml`]: `<${kind} xmlns="urn:fixture"/>`
	});
}

function odf(mime: string): Uint8Array {
	return zip({ mimetype: mime, 'META-INF/manifest.xml': ODF_MANIFEST, 'content.xml': '<office/>' });
}

function asic(): Uint8Array {
	return zip({
		mimetype: 'application/vnd.etsi.asic-e+zip',
		'META-INF/manifest.xml': ODF_MANIFEST,
		'META-INF/signatures.p7s': 'SIGNATURE',
		'document.txt': 'hello'
	});
}

describe('suggestSignFormat', () => {
	const cases: Array<[string, string, Uint8Array, string]> = [
		['ooxml docx', 'informe.docx', ooxml('word'), 'ooxml'],
		['ooxml xlsx', 'datos.xlsx', ooxml('xl'), 'ooxml'],
		['ooxml pptx', 'slides.pptx', ooxml('ppt'), 'ooxml'],
		['ooxml dotm by macro ext', 'plantilla.dotm', ooxml('word'), 'ooxml'],
		['odf odt', 'carta.odt', odf('application/vnd.oasis.opendocument.text'), 'odf'],
		['odf ods', 'hoja.ods', odf('application/vnd.oasis.opendocument.spreadsheet'), 'odf'],
		['odf odp', 'slides.odp', odf('application/vnd.oasis.opendocument.presentation'), 'odf'],
		['odf ott template', 'base.ott', odf('application/vnd.oasis.opendocument.text-template'), 'odf'],
		['asic e', 'lote.asice', asic(), 'asic'],
		['asic s', 'lote.asics', asic(), 'asic'],
		['asic bdoc', 'lote.bdoc', asic(), 'asic'],
		['asic by content not ext', 'lote.zip', asic(), 'asic'],
		['pdf bytes', 'cualquiera.bin', xml('%PDF-1.7\n%...'), 'pades'],
		['pdf by magic + ext', 'f.pdf', xml('%PDF-1.7'), 'pades'],
		['pdf fallback ext on text', 'scan.pdf', xml('not really a pdf'), 'pades'],
		['xml decl', 'factura.xml', xml('<?xml version="1.0"?><a/>'), 'xades'],
		['xml bare', 'f.xml', xml('<Signature xmlns="urn:x"/>'), 'xades'],
		['txt default', 'nota.txt', xml('hola mundo'), 'cades'],
		['zip unknown', 'cosas.zip', zip({ 'a.txt': 'hello' }), 'cades'],
		['empty', 'vacio.bin', new Uint8Array(0), 'cades']
	];
	it.each(cases)('%s → %s', (_label, name, bytes, expected) => {
		expect(suggestSignFormat(name, bytes as Uint8Array<ArrayBuffer>)).toBe(expected);
	});
});
