// Static SPA: the shell is prerendered, every other route is served the fallback page.
// `ssr = false` keeps the app client-rendered so the same build runs under `tauri://localhost`
// as on the web (no server runtime, no `ssr` assumptions in the engine).
export const ssr = false;
export const prerender = true;
export const trailingSlash = 'never';
