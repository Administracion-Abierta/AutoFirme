/**
 * Revocation checking: OCSP (RFC 6960) with CRL fallback (Refs #136).
 *
 * Browser pages cannot reach OCSP/CRL endpoints (CORS), so network I/O goes
 * through the desktop shell (`fetch_revocation_bytes` in `src-tauri/src/net/ocsp.rs`);
 * the browser build uses plain `fetch` with no user-agent spoofing.
 *
 * Safety rules: only the cert serial plus issuer hashes cross the wire (an OCSP
 * request is hashes + serial by construction; a CRL fetch is a plain download —
 * never the certificate, never keys, never document bytes). Anything unverifiable
 * (unreachable responder, stale response, bad signature) is `unknown`, never `good`.
 * OCSP nonce omitted (ponytail: replay cover only matters for stale responses, which
 * are already rejected on `nextUpdate`; add a nonce when a responder requires it).
 * Response signatures verify against the issuer's own key only; delegated responder
 * certs are ignored (out of scope per #136 — chain validation and the id-kp-OCSPSigning
 * EKU check do not exist here, so trusting an embedded cert would let any responder
 * mint `good` for a revoked serial).
 */

import { TAG, at, children, oid, parse, time, wrap, type Der } from './asn1';
import { algorithmForOid, parseCertificate, verifySignature, type Certificate } from './x509';
import { digestBytes } from './digest';
import type { Bytes } from './types';

export type RevocationStatus = 'good' | 'revoked' | 'unknown';

export interface RevocationResult {
	status: RevocationStatus;
	/** Technical facts: source, times, serial. Not translated. */
	detail: string;
	/** Which source produced this verdict. */
	via: 'ocsp' | 'crl' | 'none';
	/** Responder/CRL `producedAt`/`thisUpdate` — set on definitive verdicts. */
	responderTime?: string;
	/** Freshness horizon the verdict may be cached until (ISO). Absent on `unknown`. */
	validUntil?: string;
}

export type RevocationTransport = (
	url: string,
	body: Bytes | null,
	contentType: string | null
) => Promise<Bytes>;

export interface RevocationOptions {
	now?: Date;
	transport?: RevocationTransport;
}

const OID_AIA = '1.3.6.1.5.5.7.1.1';
const OID_OCSP_ACCESS = '1.3.6.1.5.5.7.48.1';
const OID_CRL_DP = '2.5.29.31';
const OID_BASIC_OCSP = '1.3.6.1.5.5.7.48.1.1';
const OID_SHA1 = '1.3.14.3.2.26';
const TAG_UTF8_GENERAL = 0x86; // uniformResourceIdentifier [6] IMPLICIT
const TAG_CONTEXT_0 = 0xa0;
// CertStatus ::= CHOICE { good [0] IMPLICIT NULL, revoked [1] IMPLICIT RevokedInfo, unknown [2] IMPLICIT UnknownInfo }
// IMPLICIT keeps the underlying type's form: good/unknown are primitive (0x80/0x82), revoked is a SEQUENCE (0xa1).
const TAG_STATUS_GOOD = 0x80;
const TAG_STATUS_REVOKED = 0xa1;
const SKEW_MS = 5 * 60_000;
const DAY_MS = 24 * 3600_000;

function concat(...parts: Bytes[]): Bytes {
	const out = new Uint8Array(parts.reduce((n, p) => n + p.length, 0));
	let at = 0;
	for (const part of parts) {
		out.set(part, at);
		at += part.length;
	}
	return out;
}

function find(nodes: Der[], tag: number): Der | null {
	return nodes.find((node) => node.tag === tag) ?? null;
}

function sameBytes(a: Bytes, b: Bytes): boolean {
	if (a.length !== b.length) return false;
	for (let i = 0; i < a.length; i++) if (a[i] !== b[i]) return false;
	return true;
}

function base128(value: number): number[] {
	if (value === 0) return [0];
	const out: number[] = [];
	let rest = value;
	while (rest > 0) {
		out.unshift(rest & 0x7f);
		rest >>>= 7;
	}
	for (let i = 0; i < out.length - 1; i++) out[i] |= 0x80;
	return out;
}

function oidValue(arc: string): Bytes {
	const [first, second, ...rest] = arc.split('.').map(Number);
	return new Uint8Array([first * 40 + second, ...rest.flatMap(base128)]);
}

function integerValue(raw: Bytes): Bytes {
	let start = 0;
	while (start < raw.length - 1 && raw[start] === 0 && (raw[start + 1] & 0x80) === 0) start++;
	const body = raw.subarray(start);
	if (body[0] & 0x80) return concat(new Uint8Array([0]), body);
	return body;
}

/** Locations advertised by the certificate itself. Empty when the cert carries none. */
export function extractRevocationUrls(cert: Certificate): { ocsp: string[]; crls: string[] } {
	const ocsp: string[] = [];
	const crls: string[] = [];
	const tbs = at(parse(cert.raw).node, 0);
	const extensions = find(children(tbs), 0xa3);
	if (!extensions) return { ocsp, crls };
	const list = children(at(extensions, 0));
	const text = new TextDecoder();
	for (const extension of list) {
		try {
			const fields = children(extension);
			const id = oid(fields[0]);
			const valueNode = fields[fields.length - 1];
			const value = parse(valueNode.tag === TAG.OCTET_STRING ? valueNode.content : valueNode.raw).node;
			if (id === OID_AIA) {
				for (const description of children(value)) {
					const parts = children(description);
					if (parts.length === 2 && oid(parts[0]) === OID_OCSP_ACCESS && parts[1].tag === TAG_UTF8_GENERAL) {
						ocsp.push(text.decode(parts[1].content));
					}
				}
			} else if (id === OID_CRL_DP) {
				for (const point of children(value)) {
					const naming = find(children(point), TAG_CONTEXT_0);
					const fullName = naming && find(children(naming), TAG_CONTEXT_0);
					for (const name of fullName ? children(fullName) : []) {
						if (name.tag === TAG_UTF8_GENERAL) crls.push(text.decode(name.content));
					}
				}
			}
		} catch {
			// Skip unreadable extensions; absence of a URL just means that source is untried.
		}
	}
	return { ocsp, crls };
}

async function issuerHashes(issuer: Certificate): Promise<{ nameHash: Bytes; keyHash: Bytes }> {
	const spki = parse(issuer.spki).node;
	const keyBytes = children(spki)[1];
	const keyMaterial =
		keyBytes.tag === TAG.BIT_STRING ? keyBytes.content.subarray(1) : keyBytes.content;
	return {
		nameHash: await digestBytes('SHA-1', issuer.subjectDer),
		keyHash: await digestBytes('SHA-1', keyMaterial)
	};
}

/** Minimal `OCSPRequest`: hashes + serial only — never the certificate itself. */
export async function buildOcspRequest(cert: Certificate, issuer: Certificate): Promise<Bytes> {
	const { nameHash, keyHash } = await issuerHashes(issuer);
	const padded = cert.serial.length % 2 === 0 ? cert.serial : `0${cert.serial}`;
	const serialBytes = new Uint8Array([...padded.matchAll(/../g)].map((m) => parseInt(m[0], 16)));
	const certId = wrap(
		TAG.SEQUENCE,
		concat(
			wrap(TAG.SEQUENCE, concat(wrap(TAG.OID, oidValue(OID_SHA1)), wrap(TAG.NULL, new Uint8Array(0)))),
			wrap(TAG.OCTET_STRING, nameHash),
			wrap(TAG.OCTET_STRING, keyHash),
			wrap(TAG.INTEGER, integerValue(serialBytes))
		)
	);
	// OCSPRequest > tbsRequest > requestList > Request > CertID: the per-request
	// SEQUENCE is what openssl emits and strict responders 400 without.
	return wrap(TAG.SEQUENCE, wrap(TAG.SEQUENCE, wrap(TAG.SEQUENCE, wrap(TAG.SEQUENCE, certId))));
}

interface ParsedSingle {
	serial: bigint;
	statusTag: number;
	revocationTime: string | null;
	thisUpdate: string;
	nextUpdate: string | null;
}

function parseSingleResponse(node: Der): ParsedSingle {
	const fields = children(node);
	const certId = children(fields[0]);
	const statusNode = fields[1];
	let revocationTime: string | null = null;
	if (statusNode.tag === TAG_STATUS_REVOKED) {
		const revokedInfo = children(statusNode);
		if (revokedInfo.length > 0) revocationTime = time(revokedInfo[0]);
	}
	const nextNode = find(fields.slice(2), TAG_CONTEXT_0);
	return {
		serial:
			(() => {
				const raw = certId[3];
				let result = 0n;
				for (const byte of raw.content) result = (result << 8n) | BigInt(byte);
				return result;
			})(),
		statusTag: statusNode.tag,
		revocationTime,
		thisUpdate: time(fields[2]),
		nextUpdate: nextNode ? time(at(nextNode, 0)) : null
	};
}

function freshness(nowMs: number, thisUpdate: string, nextUpdate: string | null): string | null {
	const thisMs = Date.parse(thisUpdate);
	if (Number.isNaN(thisMs) || nowMs < thisMs - SKEW_MS) return 'response not yet valid';
	if (nextUpdate) {
		const nextMs = Date.parse(nextUpdate);
		if (!Number.isNaN(nextMs) && nowMs > nextMs + SKEW_MS) return 'stale response (past nextUpdate)';
	}
	return null;
}

/** Parse + authenticate a `BasicOCSPResponse`. Never returns `good` on a bad signature. */
export async function parseOcspResponse(
	response: Bytes,
	expectedSerial: bigint,
	issuer: Certificate,
	now: Date = new Date()
): Promise<RevocationResult> {
	try {
		return await parseOcspResponseInner(response, expectedSerial, issuer, now);
	} catch {
		return { status: 'unknown', via: 'ocsp', detail: 'OCSP response undecodable' };
	}
}

async function parseOcspResponseInner(
	response: Bytes,
	expectedSerial: bigint,
	issuer: Certificate,
	now: Date
): Promise<RevocationResult> {
	const top = children(parse(response).node);
	const statusByte = top[0].content[0];
	if (statusByte !== 0) {
		return { status: 'unknown', via: 'ocsp', detail: `OCSP responder error status ${statusByte}` };
	}
	const responseBytes = find(top, TAG_CONTEXT_0);
	if (!responseBytes) return { status: 'unknown', via: 'ocsp', detail: 'OCSP response has no bytes' };
	const inner = children(at(responseBytes, 0));
	if (oid(inner[0]) !== OID_BASIC_OCSP || inner[1].tag !== TAG.OCTET_STRING) {
		return { status: 'unknown', via: 'ocsp', detail: 'not a BasicOCSPResponse' };
	}
	const basic = children(parse(inner[1].content).node);
	const tbs = basic[0];
	const algorithm = algorithmForOid(oid(at(basic[1], 0)));
	const signature = basic[2].tag === TAG.BIT_STRING ? basic[2].content.subarray(1) : basic[2].content;
	// Trust only the issuer's own signature. Embedded responder certs are ignored: chain
	// validation and the id-kp-OCSPSigning EKU check are out of scope (#136), and accepting
	// a self-asserted cert would let any responder mint `good`.
	let signed = false;
	if (algorithm) {
		signed = await verifySignature(issuer, algorithm, tbs.raw, signature).catch(() => false);
	}
	if (!signed) {
		return { status: 'unknown', via: 'ocsp', detail: 'OCSP response signature not verifiable (not trusted)' };
	}
	const data = children(tbs);
	const withoutVersion = data[0].tag === TAG_CONTEXT_0 ? data.slice(1) : data;
	const producedAt = time(withoutVersion[1]);
	const singles = children(withoutVersion[2]);
	const match = singles.map(parseSingleResponse).find((entry) => entry.serial === expectedSerial);
	if (!match) {
		return { status: 'unknown', via: 'ocsp', detail: 'OCSP response covers no entry for this serial' };
	}
	const stale = freshness(now.getTime(), match.thisUpdate, match.nextUpdate);
	if (stale) return { status: 'unknown', via: 'ocsp', detail: `OCSP ${stale}` };
	const validUntil =
		match.nextUpdate ?? new Date(Date.parse(producedAt) + DAY_MS).toISOString();
	if (match.statusTag === TAG_STATUS_GOOD) {
		return { status: 'good', via: 'ocsp', detail: `OCSP good (thisUpdate ${match.thisUpdate})`, responderTime: producedAt, validUntil };
	}
	if (match.statusTag === TAG_STATUS_REVOKED) {
		return {
			status: 'revoked',
			via: 'ocsp',
			detail: `OCSP revoked since ${match.revocationTime ?? 'unknown time'}`,
			responderTime: producedAt,
			validUntil
		};
	}
	return { status: 'unknown', via: 'ocsp', detail: 'OCSP responder has no record for this serial' };
}

/** Parse + authenticate a CRL. Revoked only from a fresh, issuer-signed list. */
export async function parseCrl(
	crl: Bytes,
	expectedSerial: bigint,
	issuer: Certificate,
	now: Date = new Date()
): Promise<RevocationResult> {
	try {
		return await parseCrlInner(crl, expectedSerial, issuer, now);
	} catch {
		return { status: 'unknown', via: 'crl', detail: 'CRL undecodable' };
	}
}

async function parseCrlInner(
	crl: Bytes,
	expectedSerial: bigint,
	issuer: Certificate,
	now: Date
): Promise<RevocationResult> {
	const top = children(parse(crl).node);
	const tbs = top[0];
	const algorithm = algorithmForOid(oid(at(top[1], 0)));
	const signature = top[2].tag === TAG.BIT_STRING ? top[2].content.subarray(1) : top[2].content;
	const signed =
		algorithm && (await verifySignature(issuer, algorithm, tbs.raw, signature).catch(() => false));
	if (!signed) {
		return { status: 'unknown', via: 'crl', detail: 'CRL signature not verifiable by issuer' };
	}
	const fields = children(tbs);
	const withoutVersion = fields[0].tag === TAG.INTEGER ? fields.slice(1) : fields;
	const thisUpdate = time(withoutVersion[2]);
	const rest = withoutVersion.slice(3);
	let nextUpdate: string | null = null;
	let revokedList: Der | null = null;
	for (const field of rest) {
		if (field.tag === TAG.SEQUENCE) revokedList = field;
		else if (field.tag === TAG.UTC_TIME || field.tag === TAG.GENERALIZED_TIME) {
			try {
				nextUpdate = time(field);
			} catch {
				// Not a time — must be the revoked list seen as a sequence already.
			}
		}
	}
	const stale = freshness(now.getTime(), thisUpdate, nextUpdate);
	if (stale) return { status: 'unknown', via: 'crl', detail: `CRL ${stale}` };
	const validUntil = nextUpdate ?? new Date(Date.parse(thisUpdate) + DAY_MS).toISOString();
	if (revokedList) {
		for (const entry of children(revokedList)) {
			const parts = children(entry);
			let serial = 0n;
			for (const byte of parts[0].content) serial = (serial << 8n) | BigInt(byte);
			if (serial === expectedSerial) {
				const date = time(parts[1]);
				return {
					status: 'revoked',
					via: 'crl',
					detail: `CRL lists serial as revoked since ${date}`,
					responderTime: thisUpdate,
					validUntil
				};
			}
		}
	}
	return {
		status: 'good',
		via: 'crl',
		detail: `CRL fresh (thisUpdate ${thisUpdate}) and serial not listed`,
		responderTime: thisUpdate,
		validUntil
	};
}

/** Desktop shell when present (no CORS wall), plain fetch in the browser build. */
async function defaultTransport(url: string, body: Bytes | null, contentType: string | null): Promise<Bytes> {
	if (!/^https?:\/\//i.test(url)) throw new Error('refused non-http(s) URL');
	if (typeof window !== 'undefined' && '__TAURI_INTERNALS__' in window) {
		// Exception to static-import rule: `@tauri-apps/api` does not exist in the browser
		// build (same lazy pattern as `src/lib/save-file.ts`); static import would drag it in.
		const { invoke } = await import('@tauri-apps/api/core');
		const reply = await invoke<number[]>('fetch_revocation_bytes', {
			url,
			body: body ? Array.from(body) : null,
			contentType
		});
		return new Uint8Array(reply);
	}
	const reply = await fetch(url, {
		method: body ? 'POST' : 'GET',
		headers: contentType ? { 'Content-Type': contentType } : {},
		body: body ? (body as BodyInit) : null
	});
	if (!reply.ok) throw new Error(`HTTP ${reply.status}`);
	return new Uint8Array(await reply.arrayBuffer());
}

const cache = new Map<string, { result: RevocationResult; expiresAt: number }>();

/** Test seam: drop cached verdicts. */
export function clearRevocationCache(): void {
	cache.clear();
}

/**
 * OCSP first, CRL fallback. Offline/unreachable responders yield `unknown` —
 * revocation is never guessed `good`.
 */
export async function checkRevocation(
	certDer: Bytes,
	issuerDer: Bytes,
	options: RevocationOptions = {}
): Promise<RevocationResult> {
	const now = options.now ?? new Date();
	const transport = options.transport ?? defaultTransport;
	let cert: Certificate;
	let issuer: Certificate;
	try {
		cert = parseCertificate(certDer);
		issuer = parseCertificate(issuerDer);
	} catch {
		return { status: 'unknown', via: 'none', detail: 'certificate DER unparseable' };
	}
	if (sameBytes(cert.subjectDer, cert.issuerDer)) {
		return { status: 'unknown', via: 'none', detail: 'self-signed certificate: revocation not applicable' };
	}
	const serial = BigInt(cert.serial === '' ? '0' : `0x${cert.serial}`);
	const { keyHash } = await issuerHashes(issuer);
	const cacheKey = `${Array.from(keyHash, (b) => b.toString(16).padStart(2, '0')).join('')}:${serial.toString(16)}`;
	const hit = cache.get(cacheKey);
	if (hit && hit.expiresAt > now.getTime()) return hit.result;
	const urls = extractRevocationUrls(cert);
	const attempts: string[] = [];
	let request: Bytes | null = null;
	for (const url of urls.ocsp) {
		try {
			request ??= await buildOcspRequest(cert, issuer);
		} catch {
			attempts.push(`OCSP ${url}: request build failed`);
			continue;
		}
		try {
			const raw = await transport(url, request, 'application/ocsp-request');
			const result = await parseOcspResponse(raw, serial, issuer, now);
			if (result.status !== 'unknown') {
				cache.set(cacheKey, { result, expiresAt: Date.parse(result.validUntil ?? '') || now.getTime() + DAY_MS });
				return result;
			}
			attempts.push(`OCSP ${url}: ${result.detail}`);
		} catch (error) {
			attempts.push(`OCSP ${url}: unreachable (${(error as Error).message})`);
		}
	}
	for (const url of urls.crls) {
		try {
			const raw = await transport(url, null, null);
			const result = await parseCrl(raw, serial, issuer, now);
			if (result.status !== 'unknown') {
				cache.set(cacheKey, { result, expiresAt: Date.parse(result.validUntil ?? '') || now.getTime() + DAY_MS });
				return result;
			}
			attempts.push(`CRL ${url}: ${result.detail}`);
		} catch (error) {
			attempts.push(`CRL ${url}: unreachable (${(error as Error).message})`);
		}
	}
	const offline = attempts.length > 0 && attempts.every((attempt) => attempt.includes('unreachable'));
	const detail =
		attempts.length > 0
			? `revocation unknown${offline ? ' (offline: all responders unreachable)' : ''}: ${attempts.join('; ')}`
			: 'revocation unknown: certificate carries no OCSP or CRL location';
	return { status: 'unknown', via: 'none', detail };
}
