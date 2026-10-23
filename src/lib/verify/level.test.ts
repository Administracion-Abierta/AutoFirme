/** Level detection across families (Refs #139): the decision table plus DSS agreement. */

import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { describe, expect, it } from 'vitest';
import { verifyArtifact } from './index';
import { detectLevel, levelFromEvidence, type LevelEvidence } from './level';
import type { Bytes } from './types';

const fixtures = resolve(process.cwd(), 'test/fixtures') + '/';
const evidence = resolve(process.cwd(), 'docs/evidence/issue-125') + '/';
const read = (dir: string, name: string): Bytes => new Uint8Array(readFileSync(dir + name));

const base: LevelEvidence = {
	structureOk: true,
	signatureTimestampVerified: false,
	xRefsPresent: false,
	xTimestampVerified: false,
	archivePresent: false
};

describe('levelFromEvidence', () => {
	it('reports B with no timestamp evidence', () => {
		expect(levelFromEvidence(base)).toBe('B');
	});

	it('reports T on a verified signature timestamp', () => {
		expect(levelFromEvidence({ ...base, signatureTimestampVerified: true })).toBe('T');
	});

	it('reports XL when X-level refs ride a verified timestamp', () => {
		expect(
			levelFromEvidence({ ...base, signatureTimestampVerified: true, xRefsPresent: true })
		).toBe('XL');
	});

	it('never reports LTA from timestamps alone — archive evidence is required', () => {
		expect(
			levelFromEvidence({
				...base,
				signatureTimestampVerified: true,
				xRefsPresent: true,
				xTimestampVerified: true
			})
		).toBe('XL');
	});

	it('reports LTA only with archive evidence over a verified base', () => {
		expect(
			levelFromEvidence({ ...base, signatureTimestampVerified: true, archivePresent: true })
		).toBe('LTA');
		expect(levelFromEvidence({ ...base, archivePresent: true })).toBe('B');
	});

	it('reports unknown when structure failed, whatever else is claimed', () => {
		expect(levelFromEvidence({ ...base, structureOk: false, signatureTimestampVerified: true })).toBe(
			'unknown'
		);
	});
});

describe('detectLevel on shipped fixtures', () => {
	it('reads B for the BES fixtures (no timestamps anywhere)', async () => {
		for (const [dir, name, format] of [
			[fixtures, 'cades-valid.csig', 'cades'],
			[fixtures, 'xades-valid.xsig', 'xades'],
			[fixtures, 'pades-b-b-signed.pdf', 'pdf']
		] as const) {
			const report = await verifyArtifact(read(dir, name));
			expect(report.level).toBe('B');
			expect(detectLevel(format, read(dir, name), report.checks)).toBe('B');
		}
	});

	it('agrees with DSS on the #125 oracle fixtures', async () => {
		// DSS: XAdES-BASELINE-B / XAdES-BASELINE-T / CAdES-X (§"EU DSS verdicts", #125).
		const bes = await verifyArtifact(read(evidence, 'xades-bes.xsig'));
		expect(bes.level).toBe('B');
		const t = await verifyArtifact(read(evidence, 'xades-t.xsig'));
		expect(t.level).toBe('T');
		for (const name of ['cades-xlong1.p7s', 'cades-xlong2.p7s']) {
			const report = await verifyArtifact(read(evidence, name));
			expect(report.level).toBe('XL');
		}
	});

	it('reports unknown for unrecognised bytes without throwing', () => {
		const bytes = new TextEncoder().encode('not a signature');
		expect(detectLevel('unknown', bytes, [])).toBe('unknown');
		expect(detectLevel('cades', bytes, [])).toBe('unknown');
	});
});
