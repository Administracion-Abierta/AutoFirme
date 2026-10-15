/**
 * Stored identities and outputs (issue #29). The vault runs over the in-memory backend — its crypto
 * and error mapping are the same code the IndexedDB backend uses, which jsdom cannot provide.
 */

import { rmSync } from 'node:fs';
import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { createVault, memoryBackend, StoreError, type StoreBackend, type Vault } from './certstore';
import { loadSignerSession } from './keystore';
import { Pkcs12Error } from './pkcs12';
import type { SignerSession } from './sign';
import {
	createPkcs12Fixtures,
	FIXTURE_PIN,
	LEAF_COMMON_NAME,
	type Pkcs12Fixtures
} from '../test/fixtures';

/** Each saved signature is sealed with its own PIN; the fixtures' is the one they were made with. */

let fixtures: Pkcs12Fixtures;
let session: SignerSession;

beforeAll(async () => {
	fixtures = createPkcs12Fixtures();
	session = await loadSignerSession(fixtures.modern, FIXTURE_PIN);
}, 120_000);

afterAll(() => rmSync(fixtures.dir, { recursive: true, force: true }));

async function codeOf(run: () => Promise<unknown>): Promise<string> {
	try {
		await run();
	} catch (error) {
		if (error instanceof StoreError) return error.code;
		if (error instanceof Pkcs12Error) return `pkcs12:${error.code}`;
		throw error;
	}
	return 'no error';
}

/** Fresh backend + vault per test; the same backend can host a second vault to mimic a reload. */
function vaultOn(backend: StoreBackend = memoryBackend()): { vault: Vault; backend: StoreBackend } {
	return { vault: createVault(backend), backend };
}

async function stored(vault: Vault, label = 'Trabajo', pin = FIXTURE_PIN) {
	return vault.saveIdentity({ label, session, container: fixtures.modern, pin });
}

describe('stored identities', () => {
	it('encrypts the container and unlocks it again with its own PIN', async () => {
		const { vault, backend } = vaultOn();
		const meta = await stored(vault);

		const record = (await backend.get('identities', meta.id)) as {
			value: { sealed: { ciphertext: Uint8Array } };
		};
		// The container itself must not be readable in the store.
		expect(record.value.sealed.ciphertext.subarray(0, 16)).not.toEqual(fixtures.modern.subarray(0, 16));

		const unlocked = await vault.unlockIdentity(meta.id, FIXTURE_PIN);
		expect(unlocked.signer.commonName).toBe(LEAF_COMMON_NAME);
		expect(unlocked.certificates).toHaveLength(session.certificates.length);
	});

	it('lists the stored identity with its label, common name and expiry', async () => {
		const { vault } = vaultOn();
		const meta = await stored(vault, 'Firma del despacho');

		expect(await vault.listIdentities()).toEqual([meta]);
		expect(meta.label).toBe('Firma del despacho');
		expect(meta.commonName).toBe(LEAF_COMMON_NAME);
		expect(new Date(meta.notAfter).getTime()).toBe(session.signer.notAfter.getTime());
		expect(meta.size).toBe(fixtures.modern.length);
	});

	it('survives a reload: a new vault over the same backend still unlocks', async () => {
		const backend = memoryBackend();
		const meta = await stored(createVault(backend));

		const reopened = createVault(backend);
		expect((await reopened.listIdentities()).map((entry) => entry.id)).toEqual([meta.id]);
		expect((await reopened.unlockIdentity(meta.id, FIXTURE_PIN)).signer.commonName).toBe(
			LEAF_COMMON_NAME
		);
	});

	it('keeps several signatures, each with its own PIN', async () => {
		const { vault } = vaultOn();
		const first = await stored(vault, 'Primera', FIXTURE_PIN);
		// A different PIN used to be refused outright, because the first save fixed the store's PIN;
		// now every record carries its own and neither can lock the other out.
		const second = await stored(vault, 'Segunda', 'otro-pin');

		expect((await vault.listIdentities()).map((entry) => entry.label).sort()).toEqual([
			'Primera',
			'Segunda'
		]);
		expect((await vault.unlockIdentity(first.id, FIXTURE_PIN)).signer.commonName).toBe(
			LEAF_COMMON_NAME
		);
		// The second record opens with its own PIN, but the container inside still wants the PIN it
		// was exported with — the two are the same secret by construction, so a mismatch stops here.
		expect(await codeOf(() => vault.unlockIdentity(second.id, 'otro-pin'))).toBe('pkcs12:wrong-pin');
		expect(await codeOf(() => vault.unlockIdentity(first.id, 'otro-pin'))).toBe('tampered');
	});

	it('rejects a tampered ciphertext instead of returning garbage', async () => {
		const { vault, backend } = vaultOn();
		const meta = await stored(vault);

		const record = (await backend.get('identities', meta.id)) as {
			id: string;
			value: { sealed: { ciphertext: Uint8Array } };
		};
		record.value.sealed.ciphertext[8] ^= 0xff;
		await backend.put('identities', record);

		expect(await codeOf(() => vault.unlockIdentity(meta.id, FIXTURE_PIN))).toBe('tampered');
	});

	it('rejects a wrong PIN without touching the other signatures', async () => {
		const { vault } = vaultOn();
		const meta = await stored(vault);

		expect(await codeOf(() => vault.unlockIdentity(meta.id, 'no-es-este'))).toBe('tampered');
		expect((await vault.unlockIdentity(meta.id, FIXTURE_PIN)).signer.commonName).toBe(
			LEAF_COMMON_NAME
		);
	});

	it('binds the ciphertext to its record id', async () => {
		const { vault, backend } = vaultOn();
		const meta = await stored(vault);

		const record = (await backend.get('identities', meta.id)) as { id: string; value: unknown };
		await backend.remove('identities', meta.id);
		await backend.put('identities', { id: 'otro-id', value: record.value });

		expect(await codeOf(() => vault.unlockIdentity('otro-id', FIXTURE_PIN))).toBe('tampered');
	});

	it('forgets an identity, leaving nothing to unlock', async () => {
		const { vault, backend } = vaultOn();
		const meta = await stored(vault);

		await vault.removeIdentity(meta.id);

		expect(await vault.listIdentities()).toEqual([]);
		expect(await backend.get('identities', meta.id)).toBeNull();
		expect(await codeOf(() => vault.unlockIdentity(meta.id, FIXTURE_PIN))).toBe('missing');
	});

	it('maps a full store onto the quota error', async () => {
		const base = memoryBackend();
		const full: StoreBackend = {
			...base,
			async put() {
				throw new DOMException('the quota has been exceeded', 'QuotaExceededError');
			}
		};

		expect(await codeOf(() => stored(createVault(full)))).toBe('quota');
	});
});

describe('stored outputs', () => {
	const signed = new TextEncoder().encode('firma de contrato') as Uint8Array<ArrayBuffer>;

	it('stores the signed bytes with their digest and gives them back unchanged', async () => {
		const { vault } = vaultOn();
		const meta = await vault.saveOutput({
			name: 'contrato.pdf.csig',
			format: 'cades',
			mime: 'application/octet-stream',
			commonName: LEAF_COMMON_NAME,
			bytes: signed
		});

		expect(await vault.listOutputs()).toEqual([meta]);
		expect(meta.name).toBe('contrato.pdf.csig');
		expect(meta.format).toBe('cades');
		expect(meta.commonName).toBe(LEAF_COMMON_NAME);
		expect(meta.sha256).toMatch(/^[0-9a-f]{64}$/);
		expect(await vault.readOutput(meta.id)).toEqual(signed);
	});

	it('deletes an output', async () => {
		const { vault } = vaultOn();
		const meta = await vault.saveOutput({
			name: 'contrato.pdf.csig',
			format: 'cades',
			mime: 'application/octet-stream',
			commonName: LEAF_COMMON_NAME,
			bytes: signed
		});

		await vault.removeOutput(meta.id);

		expect(await vault.listOutputs()).toEqual([]);
		expect(await vault.readOutput(meta.id)).toBeNull();
	});
});
