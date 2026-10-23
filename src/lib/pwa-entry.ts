import type { SignFormat } from './sign';

/**
 * PWA sign entry points (Chrome Android install): share_target, file_handlers, shortcuts
 * and the portal deep-link contract. Signing only — no verify surface.
 *
 * Everything here preloads data; nothing here signs. The user still picks a certificate
 * and confirms inside the app. A portal cannot silently trigger a signature.
 */

const FORMATS: readonly SignFormat[] = ['cades', 'xades', 'pades', 'ooxml'];

/** What a portal may ask for through the deep-link contract. */
export interface DeepLinkRequest {
	format: SignFormat;
	/** Raw document bytes, decoded from `data_b64`. */
	bytes: Uint8Array;
	/** Suggested file name for the payload. */
	name: string;
	/** Where to send the signed result back. HTTPS + user confirmation required. */
	returnUrl: string | null;
}

/** Result the app hands back to the portal after an explicit user-confirmed sign. */
export interface DeepLinkResult {
	format: SignFormat;
	/** Base64 of the produced signature bytes. */
	signatureB64: string;
	algorithm: string;
	sha256: string;
}

const MAX_DEEP_LINK_BYTES = 48 * 1024;

function isFormat(value: string | null): value is SignFormat {
	return value !== null && (FORMATS as readonly string[]).includes(value);
}

function decodeBase64(value: string): Uint8Array | null {
	try {
		const binary = atob(value);
		const bytes = new Uint8Array(binary.length);
		for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
		return bytes;
	} catch {
		return null;
	}
}

export function encodeBase64(bytes: Uint8Array): string {
	let binary = '';
	for (const byte of bytes) binary += String.fromCharCode(byte);
	return btoa(binary);
}

/**
 * HTTPS only, and same-document navigation goes through the caller rather than a
 * redirect — this keeps the destination visible and user-confirmed at the call site.
 */
export function sanitizeReturnUrl(value: string | null): string | null {
	if (!value) return null;
	try {
		const url = new URL(value, location.origin);
		if (url.protocol !== 'https:') return null;
		return url.href;
	} catch {
		return null;
	}
}

/**
 * Parse `?op=sign&format=cades&data_b64=…&name=…&return_url=…`. Returns null when the
 * query is not a sign request at all, and describes the fault when it is malformed.
 */
export function parseDeepLink(
	search: string
): { request: DeepLinkRequest } | { error: string } | null {
	const params = new URLSearchParams(search);
	if (params.get('op') !== 'sign') return null;
	const format = params.get('format');
	if (!isFormat(format)) return { error: `unsupported format: ${format ?? '(missing)'}` };
	const raw = params.get('data_b64');
	if (!raw) return { error: 'missing data_b64' };
	const bytes = decodeBase64(raw.replaceAll(/[\s]/g, ''));
	if (!bytes || bytes.length === 0) return { error: 'data_b64 is not valid base64' };
	if (bytes.length > MAX_DEEP_LINK_BYTES) {
		return { error: `payload too large for a link (${bytes.length} B); share the file instead` };
	}
	const returnUrl = sanitizeReturnUrl(params.get('return_url'));
	if (params.get('return_url') && !returnUrl) return { error: 'return_url must be https' };
	return {
		request: {
			format,
			bytes,
			name: params.get('name')?.slice(0, 128) || 'documento',
			returnUrl
		}
	};
}

/**
 * Append the signed result to the portal's return URL as query params. The caller
 * navigates there only after the user confirms the hand-back.
 */
export function buildDeepLinkReturn(returnUrl: string, result: DeepLinkResult): string {
	const url = new URL(returnUrl);
	url.searchParams.set('op', 'signed');
	url.searchParams.set('format', result.format);
	url.searchParams.set('signature_b64', result.signatureB64);
	url.searchParams.set('algorithm', result.algorithm);
	url.searchParams.set('sha256', result.sha256);
	return url.href;
}

/** A share is files, text, or both — mirrors the SW stash. Read once, then gone. */
export interface SharePayload {
	files: File[];
	text: string;
}

/** The SW stash is page-visible through the same DB; read it once and delete. */
export async function takeShare(nonce: string): Promise<SharePayload> {
	const { promise, resolve, reject } = Promise.withResolvers<SharePayload>();
	const open = indexedDB.open('autofirme-shares', 1);
	open.onupgradeneeded = () => open.result.createObjectStore('files');
	open.onerror = () => reject(open.error);
	open.onsuccess = () => {
		const db = open.result;
		const tx = db.transaction('files', 'readwrite');
		const store = tx.objectStore('files');
		const get = store.get(nonce);
		let payload: SharePayload = { files: [], text: '' };
		get.onsuccess = () => {
			const raw = get.result as SharePayload | File[] | undefined;
			payload = Array.isArray(raw) ? { files: raw, text: '' } : (raw ?? payload);
			store.delete(nonce);
		};
		tx.oncomplete = () => {
			db.close();
			resolve(payload);
		};
		tx.onerror = () => {
			db.close();
			reject(tx.error);
		};
	};
	return promise;
}
