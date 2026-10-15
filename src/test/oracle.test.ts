/**
 * Regression guard for the xmlsec1 oracle verdict parse (review #43, finding 1). The oracle read
 * the tool's bare `OK`/`FAIL` line; xmlsec1 1.3.12 writes `Verification status: OK` instead, so
 * every XAdES test failed on `no verdict` even when the signature was fine. Pin both formats and
 * the FAILED mapping against the tool's actual output shapes — spawnSync mocked (vi.mock hoists
 * above the fixtures import), no binary needed.
 */
import { spawnSync } from 'node:child_process';
import { vi, describe, expect, it } from 'vitest';
import { xmlsec1Verify } from './fixtures';

vi.mock('node:child_process', () => ({ spawnSync: vi.fn(), execFileSync: vi.fn() }));

const mockedSpawn = vi.mocked(spawnSync);

function output(stdout: string) {
	mockedSpawn.mockReturnValue({ status: 0, stdout, stderr: '', pid: 1, signal: null, output: [] });
}

describe('xmlsec1Verify verdict parsing', () => {
	it('reads the 1.2.x bare verdict line', () => {
		output('normalization is NOT required\nOK\n');
		expect(xmlsec1Verify('sig.xsig', 'trusted.pem')).toBe('OK');
	});

	it('reads the 1.3.x "Verification status:" verdict, OK and FAILED alike', () => {
		output('Verification code: 1\nVerification status: OK\n');
		expect(xmlsec1Verify('sig.xsig', 'trusted.pem')).toBe('OK');
		output('Verification status: FAILED\n');
		expect(xmlsec1Verify('sig.xsig', 'trusted.pem')).toBe('FAIL');
	});

	it('fails loudly when the tool printed no verdict at all', () => {
		mockedSpawn.mockReturnValue({
			status: 1,
			stdout: '',
			stderr: 'xmlsec1: error: unknown crypto engine\n',
			pid: 1,
			signal: null,
			output: []
		});
		expect(() => xmlsec1Verify('sig.xsig', 'trusted.pem')).toThrow(/no verdict/);
	});
});
