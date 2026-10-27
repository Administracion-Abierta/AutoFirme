/**
 * True inside the Tauri webview. The desktop shell ships its assets with the app, so the
 * PWA surface (install prompt, service-worker update prompt) is web-only.
 *
 * Tauri v2 exposes `__TAURI_INTERNALS__` on `window` before the app boots; keying on it keeps
 * the web build free of the `@tauri-apps/api` dependency.
 */
export function isDesktop(): boolean {
	return typeof window !== 'undefined' && '__TAURI_INTERNALS__' in window;
}
