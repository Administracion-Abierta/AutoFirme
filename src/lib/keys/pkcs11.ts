/**
 * PKCS#11 key provider (issue #100): any cryptoki module (SafeSign, FNMT, eID middleware)
 * as a key source. The module path is always caller-supplied — never hardcoded — and this
 * file owns no native binding: `CryptokiModule` is the minimal structural seam a native
 * bridge would implement. No bridge is registered in this tree — no Tauri command exposes a
 * module, and the OpenSC probe in #98 is Rust-only — so until one lands, `unavailableModule()`
 * keeps the picker honest instead of pretending hardware exists.
 */

import { oid, parseDer, type Bytes } from '../asn1';
import type { SignerCert } from '../sign';
import { parseCertificate, type Certificate } from '../x509';

/** One PKCS#11 slot: a reader, with or without a token in it. */
export interface Pkcs11Slot {
	/** Slot id as reported by `C_GetSlotList`. */
	id: number;
	description: string;
	/** Null when no token is present — the UI shows the empty reader as such. */
	token: { label: string; manufacturer: string } | null;
}

/** One certificate object found on a token. */
export interface Pkcs11Object {
	/** `CKA_ID` of the key/certificate pair — pairs a cert with its private key. */
	keyId: string;
	/** `CKA_LABEL` — several objects may share it; it is NOT unique. */
	label: string;
	/** Raw X.509 DER (`CKA_VALUE`). */
	der: Bytes;
}

/** Minimal cryptoki surface the provider needs. A native bridge would implement this. */
export interface CryptokiModule {
	listSlots(): Promise<Pkcs11Slot[]>;
	/** `C_FindObjects` for `CKO_CERTIFICATE`. Runs after `login`, which is what unlocks them. */
	listCertificates(slotId: number): Promise<Pkcs11Object[]>;
	/**
	 * Sign with the private key paired to `keyId`. `login` already ran: the bridge holds the
	 * authenticated session so the PIN never crosses this boundary twice.
	 */
	sign(slotId: number, keyId: string, mechanism: string, data: Bytes): Promise<Bytes>;
	/** `C_Login`. Kept separate so a wrong PIN surfaces before any certificate is shown. */
	login(slotId: number, pin: string): Promise<void>;
}

/** Error with a UI-facing code — the picker has copy for each. */
export type Pkcs11ErrorCode = 'unavailable' | 'no-token' | 'wrong-pin' | 'locked' | 'sign-failed';

export class Pkcs11Error extends Error {
	override name = 'Pkcs11Error';
	constructor(
		readonly code: Pkcs11ErrorCode,
		message: string
	) {
		super(message);
	}
}

/** Placeholder bridge: every call reports the module as unavailable. */
export function unavailableModule(modulePath: string): CryptokiModule {
	const fail = (): Promise<never> =>
		Promise.reject(new Pkcs11Error('unavailable', `PKCS#11 module not loaded: ${modulePath}`));
	return { listSlots: fail, listCertificates: () => fail(), sign: () => fail(), login: () => fail() };
}

/**
 * The picker's source badge and the certificate's `source`: `pkcs11:<module file name>`. A path is
 * required and nothing here defaults one — which module to load is the user's choice, and a
 * hardcoded `/usr/lib/opensc-pkcs11.so` would silently load a library the user never picked.
 */
export function pkcs11Source(modulePath: string): string {
	const base = modulePath.split(/[/\\]/).pop() ?? '';
	if (!base) throw new Pkcs11Error('unavailable', `"${modulePath}" is not a module path`);
	return `pkcs11:${base}`;
}

/** True when the string names a shared library an OS loader could open. The picker's own
 * module-path validation: it warns about a wrong pick instead of failing at `C_Load` time. */
export function isModulePath(value: string): boolean {
	return /\.(so(\.\d+)*|dll|dylib)$/i.test(value.trim());
}

/**
 * Cryptoki mechanisms the provider names, by key type. `CKM_ECDSA_SHA256` (0x1044) is NOT used:
 * probed against SoftHSM 2.6 (`pkcs11-tool -M` lists only the raw `CKM_ECDSA` as an ECDSA entry
 * with `sign`), `C_SignInit` with 0x1044 answers `CKR_MECHANISM_INVALID` (0x70) while 0x1041
 * signs. `CKM_SHA256_RSA_PKCS` (0x40) *is* offered there and its signature verified with openssl.
 *
 * The asymmetry is the mechanism's, not ours: the RSA mechanism hashes the input itself, the raw
 * ECDSA one signs whatever bytes it is given, so `sign()` hashes for EC and the bridge stays a
 * plain `C_Sign` — see `signingMechanism`.
 */
export const SIGNING_MECHANISMS = {
	rsa: 'CKM_SHA256_RSA_PKCS',
	/** Raw ECDSA: input is the SHA-256 digest, output is `r || s`. */
	ec: 'CKM_ECDSA'
} as const;

/**
 * What `C_Sign` receives for one certificate: the raw mechanism plus the bytes it binds.
 * RSA mechanisms hash internally; raw `CKM_ECDSA` does not, so the digest is computed here.
 * The app signs SHA-256 throughout (`objectToSignerCert` pins it, `cms.ts` emits that digest
 * OID), so another hash is refused instead of being signed under the wrong one.
 */
export async function signingMechanism(
	algorithm: SignerCert['algorithm'],
	data: Bytes
): Promise<{ mechanism: string; payload: Bytes }> {
	if (algorithm && algorithm.hash !== 'SHA-256')
		throw new Pkcs11Error('sign-failed', `unsupported hash: ${algorithm.hash}`);
	if (algorithm?.name !== 'ECDSA') return { mechanism: SIGNING_MECHANISMS.rsa, payload: data };
	return {
		mechanism: SIGNING_MECHANISMS.ec,
		payload: new Uint8Array(await crypto.subtle.digest('SHA-256', data))
	};
}

export interface Pkcs11ProviderOptions {
	/** Absolute path (or OS store name) of the cryptoki library — always caller-supplied. */
	modulePath: string;
	/** Bridge for that module. Defaults to the honest unavailable stub. */
	bridge?: CryptokiModule;
	/** PIN callback: the provider never stores the PIN, it only forwards it to `C_Login`. */
	login?: (slot: Pkcs11Slot) => Promise<string>;
}

/**
 * A token certificate as the picker sees it: a `SignerCert` whose key never leaves the
 * token (`key` stays null) plus the routing back to its slot object.
 */
export interface Pkcs11SignerCert extends SignerCert {
	/** `pkcs11:<module>` — the picker's source badge. */
	source: string;
	/** How `sign()` routes back to the right slot object. */
	tokenRef: { slotId: number; keyId: string };
}

/**
 * A PKCS#11 module as a key source. Owns the module path + bridge but no global state:
 * create one per configured module, drop it to unload.
 */
export class Pkcs11Provider {
	readonly modulePath: string;
	private readonly bridge: CryptokiModule;
	private readonly loginCallback?: (slot: Pkcs11Slot) => Promise<string>;

	constructor(options: Pkcs11ProviderOptions) {
		if (!options.modulePath) throw new Pkcs11Error('unavailable', 'a PKCS#11 module path is required');
		this.modulePath = options.modulePath;
		this.bridge = options.bridge ?? unavailableModule(options.modulePath);
		this.loginCallback = options.login;
	}

	/** Source badge the picker renders: `pkcs11:<module>` — file name, never the full path. */
	get sourceLabel(): string {
		return pkcs11Source(this.modulePath);
	}

	async slots(): Promise<Pkcs11Slot[]> {
		return this.bridge.listSlots();
	}

	/**
	 * Signable certificates of one slot's token, sign-only filtered. Logs in first when the
	 * token needs it and a PIN is available; without one, tries the public objects only
	 * (some tokens expose the certificates pre-login).
	 */
	async certificates(slotId: number, pin?: string): Promise<Pkcs11SignerCert[]> {
		const slot = (await this.slots()).find((entry) => entry.id === slotId);
		if (!slot) throw new Pkcs11Error('no-token', `slot ${slotId} is not present`);
		if (!slot.token) throw new Pkcs11Error('no-token', `slot ${slotId} has no token inserted`);
		const resolvedPin = pin ?? (await this.loginCallback?.(slot));
		if (resolvedPin !== undefined) await this.bridge.login(slotId, resolvedPin);
		const objects = await this.bridge.listCertificates(slotId);
		return signOnly(objectsToSignerCerts(objects, this.sourceLabel, slotId));
	}

	/** Sign with the token key paired to `cert`. The cert must come from this provider. */
	async sign(cert: Pkcs11SignerCert, data: Bytes, pin?: string): Promise<Bytes> {
		const { slotId, keyId } = cert.tokenRef ?? {};
		// The source is the only thing tying a certificate to a loaded module, and a slot id has
		// to be a real slot: `-1`/`undefined` would reach `C_Sign` as a garbage session handle.
		if (cert.source !== this.sourceLabel || !Number.isInteger(slotId) || slotId < 0 || !keyId)
			throw new Pkcs11Error('sign-failed', 'certificate does not belong to this module');
		if (pin !== undefined) await this.bridge.login(slotId, pin);
		const { mechanism, payload } = await signingMechanism(cert.algorithm, data);
		return this.bridge.sign(slotId, keyId, mechanism, payload);
	}
}

/* ------------------------------------------------------------------ filters */

/**
 * OIDs that mark a certificate as usable for document signing. RFC 5280 §4.2.1.12 lists the
 * generic purposes; `1.3.6.1.5.5.7.3.36` is RFC 9336 `id-kp-documentSigning`, which a qualified
 * Spanish certificate (FNMT, SafeSign) carries on its own instead of codeSigning.
 */
const SIGNING_EKUS: Record<string, true> = {
	'1.3.6.1.5.5.7.3.3': true, // codeSigning
	'1.3.6.1.5.5.7.3.2': true, // clientAuth
	'1.3.6.1.5.5.7.3.4': true, // emailProtection
	'1.3.6.1.5.5.7.3.36': true, // id-kp-documentSigning (RFC 9336)
	'2.5.29.37.0': true // anyExtendedKeyUsage
};

function extensionValues(der: Bytes): Map<string, Bytes> {
	const found = new Map<string, Bytes>();
	const tbs = parseDer(der).children[0];
	const extensions = tbs.children.find((node) => node.tag === 0xa3)?.children[0]?.children ?? [];
	for (const extension of extensions) {
		// Trailing element: the extnValue, whatever the critical flag says — an unrecognised
		// extension is not "unused" here, the picker only reads the ones it knows.
		found.set(oid(extension.children[0]), extension.children.at(-1)!.value);
	}
	return found;
}

/** What the picker hides: every reason a certificate cannot sign. */
export type CertFilterReason =
	| 'expired'
	| 'not-yet-valid'
	| 'no-key'
	| 'ca'
	| 'key-usage'
	| 'eku'
	/** The DER or one of its extensions could not be read: the picker cannot promise it signs. */
	| 'unreadable';

type FilterableCert = Pick<SignerCert, 'notBefore' | 'notAfter' | 'key' | 'der'> & {
	tokenRef?: { slotId: number; keyId: string };
};

/**
 * Why a certificate cannot sign, or null when it can. Absent extensions mean "no
 * restriction" (RFC 5280: a missing keyUsage imposes nothing). A token reference counts
 * as a key: the private key lives on the token, not in a WebCrypto handle.
 */
export function certFilterReason(cert: FilterableCert, now = Date.now()): CertFilterReason | null {
	if (!Number.isFinite(cert.notBefore.getTime()) || !Number.isFinite(cert.notAfter.getTime()))
		return 'unreadable';
	if (cert.notAfter.getTime() <= now) return 'expired';
	if (cert.notBefore.getTime() > now) return 'not-yet-valid';
	if (!cert.key && !cert.tokenRef) return 'no-key';
	let extensions: Map<string, Bytes>;
	try {
		extensions = extensionValues(cert.der);
	} catch {
		return 'unreadable';
	}
	const basic = extensions.get('2.5.29.19');
	if (basic) {
		try {
			const ca = parseDer(basic).children[0];
			if (ca?.tag === 0x01 && ca.value[0] !== 0) return 'ca';
		} catch {
			return 'unreadable';
		}
	}
	const usage = extensions.get('2.5.29.15');
	if (usage) {
		try {
			const bits = parseDer(usage);
			// BIT STRING: first content octet is the unused-bits count, then the bits.
			// digitalSignature(0) | nonRepudiation/contentCommitment(1).
			if (((bits.value[1] ?? 0) & 0xc0) === 0) return 'key-usage';
		} catch {
			return 'key-usage';
		}
	}
	const eku = extensions.get('2.5.29.37');
	if (eku) {
		try {
			const purposes = parseDer(eku).children.map((node) => oid(node));
			// An EKU extension with no listed purpose permits nothing.
			if (!purposes.some((purpose) => SIGNING_EKUS[purpose] === true)) return 'eku';
		} catch {
			return 'eku';
		}
	}
	return null;
}

/** Keep only the certificates that can sign, newest expiry first. */
export function signOnly<T extends FilterableCert>(certs: readonly T[], now = Date.now()): T[] {
	return certs
		.filter((cert) => certFilterReason(cert, now) === null)
		.sort((a, b) => b.notAfter.getTime() - a.notAfter.getTime());
}

/* ------------------------------------------------------------------ objects */

/**
 * Token objects → picker certificates. Several objects may share one `CKA_LABEL` (a key and
 * its cert, or re-issued certs under one alias): collapse to one entry per label, keeping
 * the newest, so the picker never shows the same alias twice. Unlabelled objects keep their
 * own row — collapsing them under one empty key would hide real certificates. Objects whose
 * DER does not parse are dropped — a broken token object must not break the whole listing.
 */
export function objectsToSignerCerts(
	objects: readonly Pkcs11Object[],
	source: string,
	slotId: number
): Pkcs11SignerCert[] {
	const byLabel = new Map<string, Pkcs11SignerCert>();
	objects.forEach((object, position) => {
		const cert = objectToSignerCert(object, source, slotId, position);
		if (!cert) return;
		// Unlabelled objects get a key of their own: one empty label would swallow the rest.
		const alias = object.label || `${position}:`;
		const previous = byLabel.get(alias);
		if (!previous || cert.notAfter.getTime() > previous.notAfter.getTime()) byLabel.set(alias, cert);
	});
	return [...byLabel.values()].sort((a, b) => b.notAfter.getTime() - a.notAfter.getTime());
}

/**
 * One token object → one picker certificate, or null when it cannot be one: DER that is not a
 * certificate, or a key kind the provider has no mechanism for (DSA, Ed25519, …) — offering
 * that certificate would only fail at `C_SignInit` after the user picked it. A broken object is
 * skipped by the caller: one bad blob must not hide the whole token.
 */
export function objectToSignerCert(
	object: Pkcs11Object,
	source: string,
	slotId: number,
	position = 0
): Pkcs11SignerCert | null {
	let parsed: Certificate;
	try {
		parsed = parseCertificate(object.der);
	} catch {
		return null;
	}
	if (parsed.publicKey.kind === 'other') return null;
	const now = Date.now();
	return {
		// Position-prefixed like the PKCS#12 path: serials are unique per issuer only.
		id: `${position}:${parsed.serial}`,
		commonName: object.label || parsed.commonName,
		subject: parsed.subject,
		issuer: parsed.issuer,
		serial: parsed.serial,
		notBefore: parsed.notBefore,
		notAfter: parsed.notAfter,
		expired: parsed.notAfter.getTime() < now,
		notYetValid: parsed.notBefore.getTime() > now,
		der: object.der,
		// The private key never leaves the token: no WebCrypto handle. `tokenRef` is
		// how `sign()` routes back to the right slot object.
		key: null,
		// The token's own public key names the algorithm — only the two kinds the chosen
		// mechanisms sign are left; SHA-256 is what those mechanisms bind, so no WebCrypto
		// import happens here.
		algorithm: { name: parsed.publicKey.kind === 'ec' ? 'ECDSA' : 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' },
		source,
		tokenRef: { slotId, keyId: object.keyId }
	};
}
