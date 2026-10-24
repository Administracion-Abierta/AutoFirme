/**
 * Native-messaging-host consent gate (Refs #121, `docs/SECURITY.md` §3).
 *
 * The host binary is planned work (roadmap §5.3), so this file pins the
 * contract any future implementation must preserve, at the level Chrome
 * enforces it:
 *
 * 1. Framing: 4-byte little-endian length prefix + UTF-8 JSON. Host→extension
 *    frames above 1 MiB are dropped by Chrome before parse — the extension
 *    must do the same and never feed them to `JSON.parse`.
 * 2. Confirm-before-sign: `signDigest` for digest D is refused unless a
 *    `confirm` for exactly D preceded it. Consent for one digest authorises
 *    nothing else.
 */
import test from 'node:test';
import assert from 'node:assert/strict';
/** Chrome's binding host→extension cap (roadmap §5.3, §6 item 3). */
const MAX_HOST_MESSAGE = 1024 * 1024;

/**
 * Minimal model of the documented host protocol: `hello` negotiates,
 * `confirm` records user consent per digest, `signDigest` requires it.
 * Digest in, raw signature out — documents and PINs never cross.
 */
function createHost({ sign = () => 'c2ln' } = {}) {
	const confirmed = new Set();
	return {
		hello(capabilities = ['signDigest']) {
			if (!capabilities.includes('signDigest')) throw Error('host cannot sign');
			return { version: 1, capabilities };
		},
		/** The GUI returns user consent for exactly this digest. */
		confirm(digest) {
			confirmed.add(digest);
			return true;
		},
		signDigest(digest) {
			if (!confirmed.has(digest)) throw Error('signDigest without confirm for this digest');
			confirmed.delete(digest);
			return sign();
		}
	};
}

/** Decode one length-prefixed frame, enforcing the 1 MiB cap before parsing. */
function decodeFrame(bytes) {
	const length = bytes.readUInt32LE(0);
	if (length > MAX_HOST_MESSAGE) throw Error('host message exceeds 1 MiB');
	return JSON.parse(bytes.subarray(4, 4 + length).toString('utf8'));
}

test('oversized host frames are rejected before parse', () => {
	const evil = Buffer.concat([Buffer.from([0xff, 0xff, 0x10, 0x00]), Buffer.from('{"op":"x"}')]);
	assert.throws(() => decodeFrame(evil), /exceeds 1 MiB/);
	const ok = Buffer.concat([
		Buffer.alloc(4),
		Buffer.from(JSON.stringify({ op: 'hello', version: 1 }))
	]);
	ok.writeUInt32LE(Buffer.from(JSON.stringify({ op: 'hello', version: 1 })).length, 0);
	assert.equal(decodeFrame(ok).op, 'hello');
});

test('signDigest without a prior confirm for the same digest is refused', () => {
	const host = createHost();
	host.hello();
	assert.throws(() => host.signDigest('digest-a'), /without confirm/);
});

test('confirm for one digest does not authorise another', () => {
	const host = createHost();
	host.hello();
	host.confirm('digest-a');
	assert.equal(host.signDigest('digest-a'), 'c2ln');
	assert.throws(() => host.signDigest('digest-b'), /without confirm/);
});

test('each confirm is single-use — no replay of one consent into two signatures', () => {
	const host = createHost();
	host.hello();
	host.confirm('digest-a');
	host.signDigest('digest-a');
	assert.throws(() => host.signDigest('digest-a'), /without confirm/);
});
