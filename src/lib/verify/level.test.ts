/** Level detection across families (Refs #139): the decision table plus DSS agreement. */

import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { describe, expect, it } from 'vitest';
import { verifyArtifact } from './index';
import { detectLevel, levelFromEvidence, type LevelEvidence } from './level';
import type { Bytes, Check } from './types';

const fixtures = resolve(process.cwd(), 'test/fixtures') + '/';
const evidence = resolve(process.cwd(), 'docs/evidence/issue-125') + '/';
const read = (dir: string, name: string): Bytes => new Uint8Array(readFileSync(dir + name));

const base: LevelEvidence = {
	structureOk: true,
	signatureTimestampVerified: false,
	xLevelAttested: false,
	archivePresent: false
};

describe('levelFromEvidence', () => {
	it('reports B with no timestamp evidence', () => {
		expect(levelFromEvidence(base)).toBe('B');
	});

	it('reports T on a verified signature timestamp', () => {
		expect(levelFromEvidence({ ...base, signatureTimestampVerified: true })).toBe('T');
	});

	it('reports XL on attested X-level validation data', () => {
		expect(levelFromEvidence({ ...base, xLevelAttested: true })).toBe('XL');
	});

	it('never reports LTA from timestamps alone — archive evidence is required', () => {
		expect(levelFromEvidence({ ...base, signatureTimestampVerified: true, xLevelAttested: true })).toBe('XL');
	});

	it('reports LTA only with archive evidence over a verified base', () => {
		expect(levelFromEvidence({ ...base, signatureTimestampVerified: true, archivePresent: true })).toBe('LTA');
		// An archive timestamp without any base timestamp is still only B: nothing attested the signature.
		expect(levelFromEvidence({ ...base, archivePresent: true })).toBe('B');
	});

	it('reports unknown when structure failed, whatever else is claimed', () => {
		expect(levelFromEvidence({ ...base, structureOk: false, signatureTimestampVerified: true })).toBe('unknown');
	});
});

describe('detectLevel on shipped fixtures', () => {
	it('reads B for the BES fixtures (no timestamps anywhere)', () => {
		for (const [name, format] of [
			['cades-valid.csig', 'cades'],
			['xades-valid.xsig', 'xades'],
			['pades-b-b-signed.pdf', 'pdf']
		] as const) {
			const bytes = read(fixtures, name);
			// The level input is the bytes, not the report; the DOM here is the one `verifyXades`
			// itself needs to parse XML (a no-DOM environment fails structure first, never guesses).
			expect(detectLevel(format, bytes, [{ id: 'structure', status: 'pass', detail: '' }])).toBe('B');
		}
	});

	it('agrees with DSS on the #125 oracle fixtures', async () => {
		// DSS: XAdES-BASELINE-B / XAdES-BASELINE-T / CAdES-X (docs/evidence/issue-125/README.md).
		expect((await verifyArtifact(read(evidence, 'xades-bes.xsig'))).level).toBe('B');
		expect((await verifyArtifact(read(evidence, 'xades-t.xsig'))).level).toBe('T');
		for (const name of ['cades-xlong1.p7s', 'cades-xlong2.p7s']) {
			expect((await verifyArtifact(read(evidence, name))).level).toBe('XL');
		}
	});

	it('reports unknown for unrecognised bytes without throwing', () => {
		const bytes = new TextEncoder().encode('not a signature');
		expect(detectLevel('unknown', bytes, [])).toBe('unknown');
		expect(detectLevel('cades', bytes, [])).toBe('unknown');
	});

	it('never claims a level the artifact has no evidence for', () => {
		// A truncated CMS still sniffs as CAdES; its level must be unknown, not B.
		const truncated = read(fixtures, 'cades-valid.csig').subarray(0, 40);
		expect(detectLevel('cades', new Uint8Array(truncated), [])).toBe('unknown');
		// The pre-fix X Long control carries refs DSS could not read as CAdES-X: not XL by refs alone.
		const oldoid = read(evidence, 'cades-xlong2-oldoid.p7s');
		expect(detectLevel('cades', oldoid, [{ id: 'timestamp', status: 'fail', detail: 'mismatch' }])).not.toBe('XL');
	});
});

describe('XAdES evidence is element-based, not text-based', () => {
	const enc = new TextEncoder();
	const structure: Check[] = [{ id: 'structure', status: 'pass', detail: '' }];
	const bes = readFileSync(evidence + 'xades-bes.xsig', 'utf8');
	const refs =
		'<xades:CompleteCertificateRefs/><xades:CompleteRevocationRefs/><xades:SigAndRefsTimeStamp/>';
	const inject = (markup: string): Bytes => enc.encode(bes.replace('<documento>', '<documento>' + markup));

	it.each([
		['a comment', `<!-- ${refs} -->`],
		['CDATA', `<![CDATA[${refs}]]>`],
		['a processing instruction', `<?x ${refs} ?>`],
		['a foreign namespace', `<x:CompleteCertificateRefs xmlns:x="http://example.com/x"/>`],
		// `startsWith` would accept this: the namespace URI must match, not merely be prefixed by it.
		[
			'a look-alike namespace',
			`<xades:UnsignedProperties xmlns:xades="http://uri.etsi.org/01903evil"><xades:UnsignedSignatureProperties>${refs}</xades:UnsignedSignatureProperties></xades:UnsignedProperties>`
		]
	])('ignores X-level markup hidden in %s', (_where, markup) => {
		expect(detectLevel('xades', inject(markup), structure)).toBe('B');
	});

	it('still reads XL from real XAdES refs elements', () => {
		const real = `<xades:UnsignedProperties xmlns:xades="http://uri.etsi.org/01903"><xades:UnsignedSignatureProperties>${refs}</xades:UnsignedSignatureProperties></xades:UnsignedProperties>`;
		expect(detectLevel('xades', inject(real), structure)).toBe('XL');
		// XAdES 1.3.2's namespace URI carries the version; it is the same namespace.
		const versioned = `<xades:UnsignedProperties xmlns:xades="http://uri.etsi.org/01903/v1.3.2#"><xades:UnsignedSignatureProperties>${refs}</xades:UnsignedSignatureProperties></xades:UnsignedProperties>`;
		expect(detectLevel('xades', inject(versioned), structure)).toBe('XL');
	});
});

describe('PAdES X-level attestation', () => {
	const enc = new TextEncoder();
	const structure: Check[] = [{ id: 'structure', status: 'pass', detail: '' }];
	const timestamped: Check[] = [
		...structure,
		{ id: 'timestamp', status: 'pass', detail: 'covers the signature value' }
	];
	const body = (dictionary: string): Bytes =>
		enc.encode(`%PDF-1.7\n1 0 obj\n<< ${dictionary} >>\nendobj\n%%EOF\n`);

	it('does not read a bare DSS dictionary as XL', () => {
		// LTV data with nothing attesting it is CAdES-C-shaped, not XL (header rule).
		expect(detectLevel('pdf', body('/Type /Catalog /DSS 5 0 R'), structure)).toBe('B');
	});

	it('reads XL only once a timestamp attests the DSS', () => {
		expect(detectLevel('pdf', body('/Type /Catalog /DSS 5 0 R'), timestamped)).toBe('XL');
	});

	it('reads LTA from a real DocTimeStamp dictionary', () => {
		expect(detectLevel('pdf', body('/Type /DocTimeStamp /SubFilter /ETSI.RFC3161'), timestamped)).toBe('LTA');
	});

	it('ignores X-level markup that is not PDF structure', () => {
		// A `%` comment inside an object and bytes appended after the last `%%EOF` are not
		// structure, so neither may promote the level (pre-fix both forged XL/LTA).
		const commented = '%PDF-1.7\n1 0 obj\n<< /Type /Catalog % /DSS 5 0 R /DocTimeStamp\n>>\nendobj\n%%EOF\n';
		// The commented-out markers are gone, leaving the attested signature timestamp: T, not LTA.
		expect(detectLevel('pdf', enc.encode(commented), timestamped)).toBe('T');
		// A DocTimeStamp object after the final `%%EOF` sits outside every revision: no archive evidence.
		const appended = enc.encode(
			'%PDF-1.7\n1 0 obj\n<< /Type /Catalog >>\nendobj\n%%EOF\n2 0 obj\n<< /Type /DocTimeStamp >>\nendobj\n'
		);
		expect(detectLevel('pdf', appended, timestamped)).toBe('T');
	});

	it('ignores X-level markers inside a stream payload', () => {
		// A stream's payload is arbitrary data, not syntax: `stream` ends the dictionary that counts.
		const streamed = enc.encode(
			'%PDF-1.7\n1 0 obj\n<< /Length 42 >>\nstream\n/Type /DocTimeStamp /DSS 5 0 R\nendstream\nendobj\n%%EOF\n'
		);
		expect(detectLevel('pdf', streamed, timestamped)).toBe('T');
	});

	it('ignores object-shaped markers written as PDF data, not syntax', () => {
		// A fake `N G obj` header inside a comment, a literal string or a stream payload is data, not
		// structure: reading it as an object promoted a plain B-B document to LTA.
		const fake = '7 7 obj << /DSS 5 0 R /Type /DocTimeStamp >> endobj';
		expect(
			detectLevel('pdf', enc.encode(`%PDF-1.7\n1 0 obj\n<< /Type /Catalog >>\nendobj\n% ${fake}\n%%EOF\n`), timestamped)
		).toBe('T');
		expect(
			detectLevel('pdf', enc.encode(`%PDF-1.7\n1 0 obj\n<< /Type /Catalog /Keywords (${fake}) >>\nendobj\n%%EOF\n`), timestamped)
		).toBe('T');
		expect(
			detectLevel('pdf', enc.encode(`%PDF-1.7\n1 0 obj\n<< /Length 64 >>\nstream\n${fake}\nendstream\nendobj\n%%EOF\n`), timestamped)
		).toBe('T');
	});
});
