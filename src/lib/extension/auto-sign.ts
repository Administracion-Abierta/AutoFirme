import type { Bytes } from '../asn1';
import { browserVault, type Vault } from '../certstore';
import { signCadesDetached } from '../cms';
import { forgetPin, rememberedPin } from '../pin-memory';
import type { SignerCert, SignerSession } from '../sign';
import { certificateProblem, type CertificateProblem } from './certificate';
import { MAX_REQUEST_BYTES } from './limits';

export interface RememberedSignJob {
	certId: string;
	data: string;
	signingTime: string;
}

export interface RememberedSignDeps {
	pinFor?: (id: string) => Promise<string | null>;
	vault?: Pick<Vault, 'unlockIdentity'>;
	forgetPin?: (id: string) => Promise<void>;
	certificateProblem?: (cert: SignerCert, now?: number) => CertificateProblem | null;
	signCadesDetached?: typeof signCadesDetached;
}

function bridgeData(value: string): Bytes {
	if (
		typeof value !== 'string' ||
		!value.length ||
		value.length > Math.ceil(MAX_REQUEST_BYTES / 3) * 4 ||
		value.length % 4 ||
		!/^[A-Za-z0-9+/]+={0,2}$/.test(value)
	)
		throw new Error('Invalid or oversized request data');
	const raw = atob(value);
	if (raw.length > MAX_REQUEST_BYTES) throw new Error('Request too large');
	return Uint8Array.from(raw, (character) => character.charCodeAt(0));
}

function base64(bytes: Bytes): string {
	let raw = '';
	for (let offset = 0; offset < bytes.length; offset += 8192)
		raw += String.fromCharCode(...bytes.subarray(offset, offset + 8192));
	return btoa(raw);
}

/**
 * Signs an MCP job from the session PIN memory, with no chooser window and no second prompt.
 * A remembered PIN is the operator's standing consent for that identity: it only enters that
 * memory when the user ticks "remember this PIN" in the chooser, and it never touches disk.
 *
 * Returns null whenever the job must go through the chooser instead: no PIN remembered, the
 * sealed container no longer opens with it, or the certificate is not usable right now.
 */
export async function signRemembered(
	message: RememberedSignJob,
	deps: RememberedSignDeps = {}
): Promise<{ signed_b64: string } | null> {
	const pin = await (deps.pinFor ?? rememberedPin)(message.certId);
	if (!pin) return null;
	const vault = deps.vault ?? browserVault();
	let session: SignerSession;
	try {
		session = await vault.unlockIdentity(message.certId, pin);
	} catch {
		// A stale PIN is useless: drop it so the next call asks again instead of paying PBKDF2 twice.
		try {
			await (deps.forgetPin ?? forgetPin)(message.certId);
		} catch {
			// Memory-only session: there is nothing durable to clean up.
		}
		return null;
	}
	const problem = deps.certificateProblem ?? certificateProblem;
	if (problem(session.signer) !== null) return null;
	const signingTime = new Date(message.signingTime);
	if (Number.isNaN(signingTime.getTime())) throw new Error('Invalid signing time');
	const signature = await (deps.signCadesDetached ?? signCadesDetached)(
		session.signer,
		bridgeData(message.data),
		{ attached: false, chain: session.certificates, signingTime }
	);
	return { signed_b64: base64(signature) };
}
