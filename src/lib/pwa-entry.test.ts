/** PWA sign entry: deep-link parsing and portal hand-back. Pure functions only. */

import { describe, expect, it } from 'vitest';
import {
	buildDeepLinkReturn,
	encodeBase64,
	parseDeepLink,
	sanitizeReturnUrl
} from './pwa-entry';

describe('parseDeepLink', () => {
	it('ignores non-sign queries', () => {
		expect(parseDeepLink('?entry=sign')).toBeNull();
		expect(parseDeepLink('')).toBeNull();
	});

	it('accepts a well-formed request', () => {
		const parsed = parseDeepLink(
			`?op=sign&format=cades&data_b64=${btoa('hola')}&name=doc.txt&return_url=https%3A%2F%2Fportal.example%2Fback`
		);
		expect(parsed).toMatchObject({
			request: { format: 'cades', name: 'doc.txt', returnUrl: 'https://portal.example/back' }
		});
		if (parsed && 'request' in parsed) {
			expect(Array.from(parsed.request.bytes)).toEqual([104, 111, 108, 97]);
		}
	});

	it('rejects bad format, bad base64, http return and oversize payload', () => {
		expect(parseDeepLink('?op=sign&format=pdf&data_b64=eA')).toMatchObject({
			error: expect.stringContaining('unsupported format')
		});
		expect(parseDeepLink('?op=sign&format=cades&data_b64=!!!')).toMatchObject({
			error: expect.stringContaining('base64')
		});
		expect(
			parseDeepLink('?op=sign&format=cades&data_b64=eA&return_url=http%3A%2F%2Fx.example')
		).toMatchObject({ error: expect.stringContaining('https') });
		const big = btoa('x'.repeat(60 * 1024));
		expect(parseDeepLink(`?op=sign&format=cades&data_b64=${big}`)).toMatchObject({
			error: expect.stringContaining('too large')
		});
	});
});

describe('sanitizeReturnUrl', () => {
	it('allows https only', () => {
		expect(sanitizeReturnUrl('https://portal.example/back')).toBe('https://portal.example/back');
		expect(sanitizeReturnUrl('http://portal.example/back')).toBeNull();
		expect(sanitizeReturnUrl('javascript:alert(1)')).toBeNull();
		expect(sanitizeReturnUrl(null)).toBeNull();
	});
});

describe('deep-link round trip', () => {
	it('carries the signature back as query params', () => {
		const href = buildDeepLinkReturn('https://portal.example/back?ticket=7', {
			format: 'cades',
			signatureB64: encodeBase64(new Uint8Array([1, 2, 3])),
			algorithm: 'SHA256withRSA',
			sha256: 'abc'
		});
		const url = new URL(href);
		expect(url.searchParams.get('ticket')).toBe('7');
		expect(url.searchParams.get('op')).toBe('signed');
		expect(url.searchParams.get('signature_b64')).toBe(encodeBase64(new Uint8Array([1, 2, 3])));
		expect(url.searchParams.get('sha256')).toBe('abc');
	});
});
