/**
 * Theme preference (light / dark / system). shadcn-svelte's dark mode is a class on <html>: the
 * boot script in app.html paints it before the first frame, and this module is what the switcher
 * uses from then on. The same key is read by both.
 */

export type Theme = 'light' | 'dark' | 'system';

export const THEME_KEY = 'autofirme-theme';

/** The stored preference, or 'system' when there is none (including during prerender). */
export function readTheme(): Theme {
	try {
		const stored = localStorage.getItem(THEME_KEY);
		return stored === 'light' || stored === 'dark' ? stored : 'system';
	} catch {
		return 'system';
	}
}

/**
 * Paints `mode` onto <html> and stores it. Following the system preference means listening to the
 * media query, so the returned cleanup removes that listener.
 */
export function applyTheme(mode: Theme): () => void {
	try {
		localStorage.setItem(THEME_KEY, mode);
	} catch {
		// Prerender or a storage-denied browser: the page still gets painted below.
	}
	const media = typeof matchMedia === 'function' ? matchMedia('(prefers-color-scheme: dark)') : null;
	const paint = () => {
		const dark = mode === 'dark' || (mode === 'system' && media?.matches === true);
		document.documentElement.classList.toggle('dark', dark);
	};
	paint();
	if (mode !== 'system' || !media) return () => {};
	media.addEventListener('change', paint);
	return () => media.removeEventListener('change', paint);
}
