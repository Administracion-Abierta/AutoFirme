/**
 * Loopback consent gate (Refs #121, `docs/SECURITY.md` §1): the portal sign
 * server — and the extension bridge that fronts it today — must never produce
 * a signature without passing through the confirmation click first.
 *
 * The gate is a two-line invariant in `core/lifecycle.js`: `finish('result')`
 * from `waiting` returns `false`, so `request:complete` before `request:begin`
 * is rejected and no code path can emit `{kind:'result'}` without `start()`.
 * These tests pin that invariant plus the two adjacent no-replay rules, so any
 * future sign-server implementation that reuses this state machine inherits
 * the proof.
 */
import test from 'node:test';
import assert from 'node:assert/strict';
import { RequestState } from '../core/lifecycle.js';

test('a result without prior confirmation is refused — the consent gate cannot be bypassed', () => {
	const state = new RequestState();
	assert.equal(state.finish('result'), false, 'signing without begin must fail');
	assert.equal(state.state, 'waiting', 'the refused request stays waiting, not half-signed');
});

test('fallback after signing starts is refused — no double signature via replay', () => {
	const state = new RequestState();
	assert.equal(state.start(), true);
	assert.equal(state.finish('fallback'), false, 'a signing request must not resume the original call');
});

test('confirmation is single-use — a second begin cannot re-arm a finished request', () => {
	const state = new RequestState();
	assert.equal(state.start(), true);
	assert.equal(state.finish('result'), true);
	assert.equal(state.start(), false, 'a completed request stays terminal');
	assert.equal(state.finish('result'), false);
});
