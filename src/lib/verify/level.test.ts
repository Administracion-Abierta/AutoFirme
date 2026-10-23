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
			// No DOM in this environment is fine: the level input is the bytes, not the report.
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

describe('PAdES X-level attestation', () => {
	const enc = new TextEncoder();
	const structure: Check[] = [{ id: 'structure', status: 'pass', detail: '' }];
	const dssPdf = enc.encode('%PDF-1.7\n1 0 obj << /Type /Catalog /DSS 5 0 R >>\n%%EOF\n');

	it('does not read a bare DSS dictionary as XL', () => {
		// LTV data with nothing attesting it is CAdES-C-shaped, not XL (header rule).
		expect(detectLevel('pdf', dssPdf, structure)).toBe('B');
	});

	it('reads XL only once a timestamp attests the DSS', () => {
		const timestamped: Check[] = [
			...structure,
			{ id: 'timestamp', status: 'pass', detail: 'covers the signature value' }
		];
		expect(detectLevel('pdf', dssPdf, timestamped)).toBe('XL');
	});
});
