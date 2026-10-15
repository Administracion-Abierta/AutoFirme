import { build } from 'vite';
import { copyFile, rm } from 'node:fs/promises';

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
await rm('dist/extension/manifest.webmanifest', { force: true });
await import('./check-extension.mjs');
