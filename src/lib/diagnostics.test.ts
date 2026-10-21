// @vitest-environment node
import { describe, expect, it } from 'vitest';
import { runWebCryptoProbes } from './diagnostics';

describe('runWebCryptoProbes', () => {
	it('runs every probe successfully on a WebCrypto host', async () => {
		const results = await runWebCryptoProbes();
		expect(results.map((result) => result.id)).toEqual([
			'secure-context',
			'crypto-subtle',
			'sha256',
			'pkcs8',
			'aes-cbc',
			'rsa-pss'
		]);
		expect(results.filter((result) => !result.ok)).toEqual([]);
	});
});
