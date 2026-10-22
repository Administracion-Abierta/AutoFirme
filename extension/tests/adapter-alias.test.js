/**
 * E1 portal coverage (Refs #85): sedes expose the signer as `AutoScript`, and
 * older integrations as a distinct `MiniApplet` global. Both must route
 * through the extension transport; a shared identity must wrap exactly once.
 */
import test from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { installAdapter } from '../core/adapter.js';

function win() {
	const listeners = new Map();
	let sequence = 0;
	const intervals = new Map();
	return {
		location: { origin: 'https://site.test' },
		sent: /** @type {any[]} */ ([]),
		intervals,
		addEventListener(name, fn) {
			const handlers = listeners.get(name) || [];
			handlers.push(fn);
			listeners.set(name, handlers);
		},
		emit(name, event) { for (const fn of [...(listeners.get(name) || [])]) fn(event); },
		postMessage(message, origin) { this.sent.push({ message, origin }); },
		setInterval(fn) { const id = ++sequence; intervals.set(id, fn); return id; },
		clearInterval(id) { intervals.delete(id); },
		setTimeout(fn) { return 1 + (fn && 0); },
		clearTimeout() {},
	};
}
const flush = () => new Promise((resolve) => setImmediate(resolve));

test('a distinct MiniApplet global is intercepted like AutoScript', { timeout: 5_000 }, async () => {
	const window = win();
	let originals = 0;
	window.MiniApplet = { sign(..._args) { void _args; originals++; }, selectCertificate(..._args) { void _args; originals++; } };
	installAdapter(window, async () => ({ kind: 'result', signature: 'c2ln', certificate: 'Y2VydA==' }));
	const seen = await new Promise((resolve, reject) => {
		window.MiniApplet.sign('YQ==', 'SHA256withRSA', 'CAdES', '', (signature, certificate) => resolve({ signature, certificate }), reject);
	});
	assert.deepEqual(seen, { signature: 'c2ln', certificate: 'Y2VydA==' });
	assert.equal(originals, 0, 'the native MiniApplet implementation must not run');
	await new Promise((resolve, reject) => {
		window.MiniApplet.selectCertificate(null, resolve, reject);
	});
});

test('MiniApplet aliasing the same AutoScript object wraps exactly once', async () => {
	const window = win();
	const api = { calls: 0, sign(..._args) { void _args; this.calls++; } };
	window.AutoScript = api;
	window.MiniApplet = api;
	let transports = 0;
	installAdapter(window, async () => { transports++; return { kind: 'fallback' }; });
	api.sign('YQ==', 'SHA256withRSA', 'CAdES', '', () => {}, () => {});
	await flush();
	assert.equal(transports, 1);
	assert.equal(api.calls, 1, 'fallback resumes the original exactly once');
});

test('a late MiniApplet assignment is wrapped by the setter', { timeout: 5_000 }, async () => {
	const window = win();
	installAdapter(window, async () => ({ kind: 'result', signature: 'c2ln', certificate: 'Y2VydA==' }));
	let originals = 0;
	window.MiniApplet = { sign(..._args) { void _args; originals++; } };
	const seen = await new Promise((resolve, reject) => {
		window.MiniApplet.sign('YQ==', 'SHA256withRSA', 'CAdES', '', (signature) => resolve(signature), reject);
	});
	assert.equal(seen, 'c2ln');
	assert.equal(originals, 0);
});

test('a non-configurable global is wrapped in place and the poll catches replacements', { timeout: 5_000 }, async () => {
	const window = win();
	let originals = 0;
	const api = { sign(..._args) { void _args; originals++; } };
	Object.defineProperty(window, 'AutoScript', { value: api, configurable: false, writable: true, enumerable: true });
	installAdapter(window, async () => ({ kind: 'result', signature: 'c2ln', certificate: 'Y2VydA==' }));
	assert.equal(Object.getOwnPropertyDescriptor(window, 'AutoScript')?.set, undefined,
		'a non-configurable global keeps its plain data descriptor');
	const seen = await new Promise((resolve, reject) => {
		window.AutoScript.sign('YQ==', 'SHA256withRSA', 'CAdES', '', (signature) => resolve(signature), reject);
	});
	assert.equal(seen, 'c2ln');
	assert.equal(originals, 0);
	// A page that swaps the whole object is only caught by the polled re-wrap.
	const replacement = { sign(..._args) { void _args; originals++; }, selectCertificate(..._args) { void _args; originals++; } };
	window.AutoScript = replacement;
	for (const tick of [...window.intervals.values()]) tick();
	const late = await new Promise((resolve, reject) => {
		replacement.sign('YQ==', 'SHA256withRSA', 'CAdES', '', (signature) => resolve(signature), reject);
	});
	assert.equal(late, 'c2ln');
	assert.equal(originals, 0);
});
test('the documented interception list names both globals', () => {
	const doc = readFileSync(new URL('../../docs/CHROME-EXTENSION.md', import.meta.url), 'utf8');
	const section = doc.slice(doc.indexOf('Initially intercepted calls are'));
	const fence = /```js\n([\s\S]*?)```/.exec(section);
	assert.ok(fence, 'the interception list fence is present');
	assert.match(fence[1], /AutoScript\.sign\(/);
	assert.match(fence[1], /MiniApplet\.sign\(/,
		'the interception list must document the MiniApplet alias the adapter wraps');
});
