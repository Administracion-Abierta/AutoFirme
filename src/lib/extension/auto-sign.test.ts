import { afterAll, beforeAll, describe, expect, it, vi } from 'vitest';
import { rmSync } from 'node:fs';
import { createCadesFixtures, FIXTURE_PIN, type CadesFixtures } from '../../test/fixtures';
import { createVault, memoryBackend, type Vault } from '../certstore';
import { loadSignerSession } from '../keystore';
import { signCadesDetached } from '../cms';
import type { SignerSession } from '../sign';
import { certificateProblem } from './certificate';
import { signRemembered } from './auto-sign';

let fixtures: CadesFixtures;
let vault: Vault;
let identity: string;
let session: SignerSession;

beforeAll(async () => {
	fixtures = createCadesFixtures();
	session = await loadSignerSession(fixtures.p12, FIXTURE_PIN);
	vault = createVault(memoryBackend());
	identity = (await vault.saveIdentity({ label: 'MCP identity', session, container: fixtures.p12, pin: FIXTURE_PIN })).id;
});

afterAll(() => rmSync(fixtures.dir, { recursive: true, force: true }));

const message = (signingTime = new Date()) => ({
	certId: identity,
	data: Buffer.from('MCP document').toString('base64'),
	signingTime: signingTime.toISOString()
});

describe('MCP remembered-PIN signing', () => {
	it('signs the requested identity without a chooser when its PIN is remembered', async () => {
		const at = new Date();
		const pinFor = vi.fn(async () => FIXTURE_PIN);
		const result = await signRemembered(message(at), {
			pinFor,
			vault,
			certificateProblem,
			signCadesDetached
		});
		const expected = await signCadesDetached(session.signer, Buffer.from('MCP document'), {
			attached: false,
			chain: session.certificates,
			signingTime: at
		});
		expect(pinFor).toHaveBeenCalledWith(identity);
		expect(result).toEqual({ signed_b64: Buffer.from(expected).toString('base64') });
	});

	it('returns null without touching the vault when no PIN is remembered', async () => {
		const unlock = vi.spyOn(vault, 'unlockIdentity');
		const result = await signRemembered(message(), { pinFor: async () => null, vault });
		expect(result).toBeNull();
		expect(unlock).not.toHaveBeenCalled();
		unlock.mockRestore();
	});

	it('forgets a stale PIN and falls back to the chooser', async () => {
		const forgetPin = vi.fn(async () => {});
		const result = await signRemembered(message(), { pinFor: async () => '9999', vault, forgetPin });
		expect(result).toBeNull();
		expect(forgetPin).toHaveBeenCalledWith(identity);
	});

	it('never signs silently with an expired certificate', async () => {
		const expired = {
			...vault,
			unlockIdentity: async () => ({ ...session, signer: { ...session.signer, notAfter: new Date(0) } })
		};
		const sign = vi.fn();
		const result = await signRemembered(message(), {
			pinFor: async () => FIXTURE_PIN,
			vault: expired,
			certificateProblem,
			signCadesDetached: sign
		});
		expect(result).toBeNull();
		expect(sign).not.toHaveBeenCalled();
	});
});
