/**
 * The verify panel's verdict banner (#40): `valid-but-expired` has to read as a warning, not as the
 * red `invalid` card, and the per-check rows have to carry the amber status through.
 */

import { cleanup, render, screen } from '@testing-library/svelte';
import { afterEach, describe, expect, it } from 'vitest';
import { copy } from '#lib/i18n';
import type { Check, VerificationStatus } from '#lib/verify';
import VerifyReport from './VerifyReport.svelte';

afterEach(cleanup);

const check = (status: Check['status']): Check => ({
	id: 'certValidity',
	status,
	detail: 'the signer certificate expired 2025-01-01 — the signature itself still verifies'
});

function reportWith(status: VerificationStatus, checkStatus: Check['status']) {
	return {
		format: 'cades' as const,
		ok: status !== 'invalid',
		status,
		checks: [check(checkStatus)],
		signer: null,
		signingTime: null
	};
}

describe('verification report banner', () => {
	it('warns amber for a valid signature whose certificate expired (#40)', () => {
		render(VerifyReport, {
			props: { copy: copy.es, report: reportWith('valid-but-expired', 'warn'), running: false }
		});

		const banner = screen.getByRole('alert');
		expect(banner.textContent).toContain(copy.es.verifyExpired);
		expect(banner.textContent).not.toContain(copy.es.verifyFail);
		// The amber warning, not the red rejection: the signature itself is sound.
		expect(banner.className).toContain('amber');
		expect(banner.className).not.toContain('destructive');
	});

	it('keeps the red card for a report that actually failed', () => {
		render(VerifyReport, {
			props: { copy: copy.es, report: reportWith('invalid', 'fail'), running: false }
		});

		const banner = screen.getByRole('alert');
		expect(banner.textContent).toContain(copy.es.verifyFail);
		expect(banner.textContent).not.toContain(copy.es.verifyExpired);
	});

	it('leaves a fully valid report on the green card', () => {
		render(VerifyReport, {
			props: { copy: copy.es, report: reportWith('valid', 'pass'), running: false }
		});

		const banner = screen.getByRole('alert');
		expect(banner.textContent).toContain(copy.es.verifyOk);
		expect(banner.textContent).not.toContain(copy.es.verifyExpired);
	});
});
