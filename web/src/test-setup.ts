import { webcrypto } from 'node:crypto';

// jsdom's `crypto` only has getRandomValues; the crypto work here (and in the browser) needs WEB
// SubtleCrypto. Node's implementation is the same API the browsers expose.
if (!globalThis.crypto?.subtle) {
	Object.defineProperty(globalThis, 'crypto', { value: webcrypto, configurable: true, writable: true });
}
