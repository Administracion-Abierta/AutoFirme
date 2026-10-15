/**
 * The xmlsec1 oracle maps the tool's prose to a verdict. Review #46 found the mapping inverted for
 * the pre-1.3 wordings: a `FAILED` verdict captured by the `status: FAIL` / bare `FAIL` patterns
 * returned `'OK'` — a verification failure reported as success. These tests stub `spawnSync` so the
 * matrix is pinned independently of the xmlsec1 build installed on the machine.
 */
import { describe, expect, it, vi } from 'vitest';
import { xmlsec1Verify } from './fixtures';

// `vi.mock` is hoisted above the imports; the factory only runs when `node:child_process` is first
// required, so reading `stubbedOutput` lazily inside the stub is safe.
const stubbedOutput = { value: '' };

vi.mock('node:child_process', async (importOriginal) => {
	const actual = await importOriginal<typeof import('node:child_process')>();
	return {
		...actual,
		spawnSync: vi.fn(() => ({ status: 0, stdout: stubbedOutput.value, stderr: '' }))
	};
});

function oracleWith(output: string): 'OK' | 'FAIL' {
	stubbedOutput.value = output;
	return xmlsec1Verify('/dev/null', '/dev/null');
}

describe('xmlsec1Verify verdict mapping', () => {
	it('reads the 1.3.x status line', () => {
		expect(oracleWith('Verification status: OK\n')).toBe('OK');
		expect(oracleWith('Verification status: FAILED\n')).toBe('FAIL');
	});

	it('reads the status: prefix wording', () => {
		expect(oracleWith('status: OK\n')).toBe('OK');
		expect(oracleWith('status: FAIL\n')).toBe('FAIL');
	});

	it('reads the bare verdict line — FAILED must never map to OK', () => {
		expect(oracleWith('OK\n')).toBe('OK');
		expect(oracleWith('FAIL\n')).toBe('FAIL');
		expect(oracleWith('verification failed: reference resolved to different datasets\nFAIL\n')).toBe('FAIL');
	});

	it('errors instead of guessing when there is no verdict', () => {
		stubbedOutput.value = 'xmlsec1: error: libxml\n';
		expect(() => xmlsec1Verify('/dev/null', '/dev/null')).toThrow(/no verdict/);
	});
});
