# AutoFirme extension build

Run `pnpm install`, `pnpm check` and `pnpm build:extension` from the repository root. Load
`dist/extension` with **Load unpacked** at `chrome://extensions` in developer mode
(Chrome 116 or later). `pnpm check:extension` repeats the artifact checks; run
`pnpm test:extension:unit` for the website bridge tests.

En español: las funciones de la extensión están en [../docs/LEEME.md](../docs/LEEME.md).

**Chrome only for now.** Firefox support is deferred and studied in
[issue #117](https://git.eduard3v.com/EduarD3V/AutoFirme/issues/117): the
MAIN-world bridge needs the FF 128+ `userScripts` API instead of Chrome's
`world: 'MAIN'` content scripts.

The extension mounts the same `src/routes/+page.svelte` and `App.svelte` as the
desktop product, including its signing, verification, batch queue, PDF stamps, certificate
store and settings. `pnpm build` produces the extension's options page and the local dev
harness; no website ships.
The standalone extension target avoids SvelteKit's inline bootstrap and never
registers the app service worker. Scripts, fonts and styles are bundled locally.

The toolbar opens a persistent, resizable app window. The extension intercepts every
HTTP(S) website from installation: disable individual origins in Site access (reload
them after disabling) if you want a site to keep its original AutoFirma integration.
Every signature requires explicit confirmation.
TSA access requires permission for the configured HTTP(S) TSA origin; timestamping checks that grant
before sending the digest. Imported identities belong to the extension origin;
identities imported under the retired hosted web app are not automatically copied.

Do not publish this package until its license is resolved: upstream revision
`d6dfa434d3be2aa2ed53b3956f7d10c3ab8c1035` did not declare an application license.

See [the extension guide](../docs/CHROME-EXTENSION.md),
[design](../docs/CHROME-EXTENSION-DESIGN.md) and
[verification results](../docs/CHROME-EXTENSION-VERIFICATION.md). Store copy,
permission justifications and assets are in
[the listing kit](../docs/STORE-LISTING.md); the privacy statement is
[PRIVACY.md](../docs/PRIVACY.md).
