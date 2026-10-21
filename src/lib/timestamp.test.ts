/**
 * RFC 3161 timestamp client tests.
 *
 * There is no TSA in the room: `fetch` is stubbed to hand the client's own `TimeStampReq` to a
 * throwaway `openssl ts` instance, and openssl is then asked to verify the token the client
 * extracted — the independent oracle for both directions.
 */

import { spawnSync } from 'node:child_process';
import { webcrypto } from 'node:crypto';
import { mkdtempSync, rmSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { afterAll, afterEach, beforeAll, describe, expect, it, vi } from 'vitest';

import { int, seq, type Bytes } from './der';
import { createTsaFixtures, type TsaFixtures } from '../test/fixtures';
import {
	OID_TIME_STAMP_TOKEN,
	parseTimeStampResponse,
	requestTimeStamp,
	timeStampAttributes,
	timeStampRequest,
	verifyTimeStampImprint
} from './timestamp';

// jsdom's `crypto` has no `subtle`; the client needs real WebCrypto.
if (!globalThis.crypto?.subtle) {
	Object.defineProperty(globalThis, 'crypto', { value: webcrypto, configurable: true });
}

const TSA_URL = 'https://tsa.test/timestamp';
const SIGNATURE = new TextEncoder().encode('the signature value being timestamped\n');

let tsa: TsaFixtures;
let dir: string;

beforeAll(() => {
	tsa = createTsaFixtures();
	dir = mkdtempSync(join(tmpdir(), 'autofirme-ts-'));
}, 60_000);

afterAll(() => {
	rmSync(tsa.dir, { recursive: true, force: true });
	rmSync(dir, { recursive: true, force: true });
});

afterEach(() => vi.unstubAllGlobals());

function write(name: string, bytes: Bytes): string {
	const path = join(dir, name);
	writeFileSync(path, bytes);
	return path;
}

/** Run `openssl ts`/`openssl asn1parse`; its prose is the verdict, so a non-zero exit fails. */
function openssl(args: string[]): string {
	const result = spawnSync('openssl', args, { encoding: 'utf8' });
	if (result.status !== 0) throw new Error(`openssl failed: ${args.join(' ')}\n${result.stderr}`);
	return `${result.stdout}${result.stderr}`;
}

/** A TSA that answers whatever `fetch` posts to it, signed by the fixture certificate. */
function stubTsa(reply?: (query: Bytes) => Bytes): void {
	vi.stubGlobal('fetch', async (url: string, init: RequestInit) => {
		expect(url).toBe(TSA_URL);
		expect(new Headers(init.headers).get('content-type')).toBe('application/timestamp-query');
		if (!reply) return new Response(null, { status: 503 });
		return new Response(reply(toBytes(init.body)), { status: 200 });
	});
}

function toBytes(body: unknown): Bytes {
	if (!(body instanceof Uint8Array)) throw new Error('the request has no DER body');
	return new Uint8Array(body);
}

describe('RFC 3161 timestamp client', () => {
	it('builds a TimeStampReq that openssl ts parses', () => {
		// openssl's own sha256 of the same data, so the imprint in the request is checked, not just
		// its shape.
		const digest = new Uint8Array(32).fill(0x2c);
		const text = openssl(['ts', '-query', '-in', write('req.der', timeStampRequest(digest)), '-text']);
		expect(text).toMatch(/Hash Algorithm: sha256/);
		expect(text).toMatch(/Certificate required: yes/);
		expect(text).toMatch(/2c 2c 2c 2c 2c 2c 2c 2c/);
	});

	it('requests a token and gets one openssl verifies against our digest', async () => {
		stubTsa(tsa.respond);

		const token = await requestTimeStamp(TSA_URL, SIGNATURE);

		const digestHex = Buffer.from(new Uint8Array(await webcrypto.subtle.digest('SHA-256', SIGNATURE))).toString(
			'hex'
		);
		expect(
			openssl([
				'ts', '-verify', '-in', write('token.p7', token), '-token_in',
				'-digest', digestHex, '-CAfile', tsa.certPath
			])
		).toMatch(/Verification: OK/);
	});

	it('wraps the token in the id-aa-timeStampToken unsigned attribute', () => {
		const token = tsa.respond(timeStampRequest(new Uint8Array(32).fill(9)));
		const attributes = timeStampAttributes(token);
		const parsed = openssl(['asn1parse', '-inform', 'DER', '-in', write('attrs.der', attributes)]);

		// openssl names 1.2.840.113549.1.9.16.2.14 `id-smime-aa-timeStampToken`.
		expect(OID_TIME_STAMP_TOKEN).toBe('1.2.840.113549.1.9.16.2.14');
		expect(parsed).toContain('OBJECT            :id-smime-aa-timeStampToken');
		expect(attributes[0]).toBe(0x31); // SET OF Attribute
	});

	it('rejects a token issued for a different digest', async () => {
		// The TSA answers with a well-formed token, but over a digest we never asked about.
		stubTsa(() => tsa.respond(timeStampRequest(new Uint8Array(32).fill(0x11))));

		await expect(requestTimeStamp(TSA_URL, SIGNATURE)).rejects.toThrow(/different digest/);
	});

	it('reports a TSA that is down', async () => {
		stubTsa();
		await expect(requestTimeStamp(TSA_URL, SIGNATURE)).rejects.toThrow(/answered HTTP 503/);
	});

	it('reports a rejection and a token-less grant', async () => {
		stubTsa(() => seq(seq(int(2))));
		await expect(requestTimeStamp(TSA_URL, SIGNATURE)).rejects.toThrow(/PKIStatus 2/);

		stubTsa(() => seq(seq(int(0))));
		await expect(requestTimeStamp(TSA_URL, SIGNATURE)).rejects.toThrow(/without returning a token/);
	});

	it('verifyTimeStampImprint accepts the token for its own bytes, rejects anything else', async () => {
		const imprint = new Uint8Array(await webcrypto.subtle.digest('SHA-256', SIGNATURE));
		const token = parseTimeStampResponse(tsa.respond(timeStampRequest(imprint)));

		await expect(verifyTimeStampImprint(token, SIGNATURE)).resolves.toBeUndefined();
		await expect(verifyTimeStampImprint(token, new TextEncoder().encode('other bytes'))).rejects.toThrow(
			/covers different bytes/
		);
		await expect(verifyTimeStampImprint(new Uint8Array([0x30, 0x00]), SIGNATURE)).rejects.toThrow(
			/unreadable/
		);
	});
});
