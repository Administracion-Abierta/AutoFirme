/**
 * RFC 3161 timestamping over HTTP — what turns CAdES-BES/PAdES-B-B into CAdES-T/PAdES-B-T.
 *
 * `requestTimeStamp` posts a `TimeStampReq` to the TSA and hands back the `TimeStampToken` it
 * returns, after checking that the response was granted *and* that the token's `messageImprint`
 * is the digest we asked about. The TSA only ever sees a SHA-256 hash of the signature value:
 * neither the document nor the key leaves the browser.
 *
 * Embedding the token is the caller's job — `timeStampAttributes` builds the
 * `id-aa-timeStampToken` unsigned attribute (`SignerInfo` field `[1]`) both `pades.ts` and
 * `cms.ts` put inside the CMS they already write.
 */

import { content, int, octet, oid, readTlv, seq, set, type Bytes, type Tlv } from './der';

/** `id-aa-timeStampToken` (RFC 3161 §2.4.2). */
export const OID_TIME_STAMP_TOKEN = '1.2.840.113549.1.9.16.2.14';

const OID_SHA256 = '2.16.840.1.101.3.4.2.1';
const QUERY_CONTENT_TYPE = 'application/timestamp-query';
const REPLY_CONTENT_TYPE = 'application/timestamp-reply';

const SEQUENCE = 0x30;
const INTEGER = 0x02;

/** DER `BOOLEAN TRUE`: the token must carry the TSA certificate, or nothing can verify it alone. */
const CERT_REQ = new Uint8Array([0x01, 0x01, 0xff]);

function children(bytes: Bytes, parent: Tlv): Tlv[] {
	const items: Tlv[] = [];
	for (let cursor = parent.contentStart; cursor < parent.end; ) {
		const item = readTlv(bytes, cursor);
		items.push(item);
		cursor = item.end;
	}
	return items;
}

function at(items: Tlv[], index: number, what: string): Tlv {
	const item = items[index];
	if (!item) throw new Error(`the timestamp response has no ${what}`);
	return item;
}

/** RFC 3161 `TimeStampReq`: version 1, SHA-256 message imprint, `certReq` TRUE, no policy. */
export function timeStampRequest(digest: Bytes): Bytes {
	// messageImprint is `SEQUENCE { hashAlgorithm, hashedMessage }`; the SHA-256 parameters are
	// omitted (RFC 5754 §2), and nonce/extensions stay out of a single-shot signing request.
	return seq(int(1), seq(seq(oid(OID_SHA256)), octet(digest)), CERT_REQ);
}

/**
 * The `TimeStampToken` of a `TimeStampResp` (RFC 3161 §2.4.2). Granted (0) and grantedWithMods (1)
 * are the statuses that carry a usable token; everything else is a rejection.
 */
export function parseTimeStampResponse(response: Bytes): Bytes {
	const outer = readTlv(response);
	if (outer.tag !== SEQUENCE) throw new Error('not an RFC 3161 timestamp response');
	const [statusInfo, token] = children(response, outer);
	if (!statusInfo || statusInfo.tag !== SEQUENCE) {
		throw new Error('the timestamp response has no PKIStatusInfo');
	}
	const status = at(children(response, statusInfo), 0, 'PKIStatus');
	if (status.tag !== INTEGER) throw new Error('the timestamp response has no PKIStatus');
	let value = 0;
	for (const byte of content(response, status)) value = value * 256 + byte;
	if (value !== 0 && value !== 1) throw new Error(`the TSA rejected the timestamp (PKIStatus ${value})`);
	if (!token) throw new Error('the TSA granted the timestamp without returning a token');
	return response.subarray(token.start, token.end);
}

/** The SHA-256 `messageImprint.hashedMessage` the token was issued over (RFC 3161 §2.4.2). */
export function timeStampImprint(token: Bytes): Bytes {
	const contentInfo = readTlv(token);
	const signedData = readTlv(token, at(children(token, contentInfo), 1, 'SignedData').contentStart);
	const encapContent = at(children(token, signedData), 2, 'encapContentInfo');
	// encapContentInfo is `SEQUENCE { eContentType, eContent [0] EXPLICIT OCTET STRING }`, and the
	// OCTET STRING content is the DER of TSTInfo.
	const eContent = at(children(token, encapContent), 1, 'eContent');
	const octets = readTlv(token, eContent.contentStart);
	const tstInfo = readTlv(token, octets.contentStart);
	// TSTInfo is `SEQUENCE { version, policy, messageImprint, serialNumber, genTime, ... }`.
	const imprint = at(children(token, tstInfo), 2, 'messageImprint');
	return content(token, at(children(token, imprint), 1, 'hashedMessage'));
}
/**
 * Throw unless `token`'s `messageImprint` is `SHA-256(data)` — the check that turns a decorated
 * blob into proof the TSA saw these exact bytes. Specific messages, so a verifier can quote them
 * as the failure reason instead of inventing one.
 */
export async function verifyTimeStampImprint(token: Bytes, data: Bytes): Promise<void> {
	const expected = new Uint8Array(await crypto.subtle.digest('SHA-256', data));
	let echoed: Bytes;
	try {
		echoed = timeStampImprint(token);
	} catch {
		throw new Error('the timestamp token is unreadable: no RFC 3161 messageImprint found');
	}
	if (echoed.length !== expected.length || echoed.some((byte, index) => byte !== expected[index])) {
		throw new Error('the timestamp token covers different bytes: messageImprint mismatch');
	}
}

/**
 * Ask `url` to timestamp `data` and return its DER `TimeStampToken`. Throws on anything that is
 * not a granted reply whose imprint is `SHA-256(data)` — a token that covers other bytes is as
 * useless as no token at all, and worse if it silently ends up in the signature.
 *
 * `fetch` is read from the global scope on every call, so tests stub it.
 */
export async function requestTimeStamp(url: string, data: Bytes): Promise<Bytes> {
	if (globalThis.location?.protocol === 'chrome-extension:') {
		const tsa = new URL(url);
		if (tsa.protocol !== 'https:' && tsa.protocol !== 'http:') {
			throw new Error('TSA permission requires an HTTP(S) URL');
		}
		const extension = globalThis as typeof globalThis & {
			chrome?: { permissions?: { contains: (permission: { origins: string[] }) => Promise<boolean> } };
		};
		const granted = await extension.chrome?.permissions?.contains({ origins: [`${tsa.origin}/*`] });
		if (!granted) throw new Error(`Grant TSA host permission before timestamping: ${tsa.origin}`);
	}
	const imprint = new Uint8Array(await crypto.subtle.digest('SHA-256', data));
	const response = await globalThis.fetch(url, {
		method: 'POST',
		headers: { 'Content-Type': QUERY_CONTENT_TYPE, Accept: REPLY_CONTENT_TYPE },
		body: timeStampRequest(imprint)
	});
	if (!response.ok) throw new Error(`the TSA at ${url} answered HTTP ${response.status}`);

	const token = parseTimeStampResponse(new Uint8Array(await response.arrayBuffer()));
	const echoed = timeStampImprint(token);
	if (echoed.length !== imprint.length || echoed.some((byte, index) => byte !== imprint[index])) {
		throw new Error(`the TSA at ${url} returned a token for a different digest`);
	}
	return token;
}

/** The `id-aa-timeStampToken` attribute, as the `SET OF` the IMPLICIT `unsignedAttrs [1]` carries. */
export function timeStampAttributes(token: Bytes): Bytes {
	return set(seq(oid(OID_TIME_STAMP_TOKEN), set(token)));
}
