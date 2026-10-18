import { build } from 'vite';
import { copyFile } from 'node:fs/promises';

// Compile exactly the same route and app components as the web target, without
// SvelteKit's inline bootstrap and web service-worker registration.
await build({ mode: 'extension' });
for (const name of ['background', 'page', 'content']) {
  await build({
    configFile: false,
    publicDir: false,
    build: {
      outDir: 'dist/extension', emptyOutDir: false, target: 'chrome116',
      lib: { entry: `extension/${name}.js`, formats: [name === 'background' ? 'es' : 'iife'], name: `AutoFirme_${name}`, fileName: () => `${name}.js` }
    }
  });
}
await copyFile('extension/manifest.json', 'dist/extension/manifest.json');
// Only assets the extension HTML and manifest actually reference. Copying all of
// static/ leaks the web app's PWA manifest and _headers into the Chrome package.
for (const name of ['favicon.svg', 'icon-192.png', 'icon-512.png', 'theme-bootstrap.js']) {
  await copyFile(`static/${name}`, `dist/extension/${name}`);
}
await import('./check-extension.mjs');
