/**
 * jsdom ships no WebCrypto; borrow Node's implementation so the verifiers under test
 * exercise the real `crypto.subtle` code paths instead of a mock.
 */
import { webcrypto } from 'node:crypto';

if (!globalThis.crypto?.subtle) {
	Object.defineProperty(globalThis, 'crypto', { value: webcrypto, configurable: true });
}
