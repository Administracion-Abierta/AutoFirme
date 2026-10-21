# demo

Live end-to-end runs against real websites. Nothing here is mocked: the production build of
the extension is loaded into Chrome for Testing, a throwaway certificate signs real documents
on a real administration page, and OpenSSL independently verifies every signature afterwards.

This is a *demo and smoke test*, not part of `pnpm test` — it needs a network connection, a
real browser and roughly five minutes.

## Requirements

| Tool | Why |
|---|---|
| Node 24 | the runner |
| `openssl` on `PATH` | generates the throwaway identity and verifies every signature |
| Chrome for Testing or Chromium | Playwright's download works; branded Google Chrome 137+ ignores `--load-extension` and the runner refuses it up front |

```sh
pnpm install
pnpm build:extension        # the demo loads dist/extension
pnpm demo:chromium          # one-time browser download
pnpm demo:registradores
```

`CHROME_PATH` overrides the browser if you already have one, for example:

```sh
CHROME_PATH=~/.cache/ms-playwright/chromium-1243/chrome-linux64/chrome pnpm demo:registradores
```

## Options

| Flag | Effect |
|---|---|
| `--headed` | shows the browser, so you can watch the chooser windows open and close |
| `--url=<url>` | runs against a copy of the page (a staging deployment, for instance) |

## What `demo:registradores` does

Against <https://www.registradores.org/test-autofirma>, the Colegio de Registradores' own
AutoFirma test page:

1. signs each CAdES target the page offers (81 KB generated text, its sample PDF, the
   NIF-and-name form), checking that the digest the chooser shows matches an independent
   SHA-256 of the payload and that OpenSSL recovers the original bytes;
2. checks the rest of the website contract — certificate selection, cancelling, handing the
   request back to the site's own AutoFirma, and two requests in flight at once;
3. checks that PAdES, XAdES and AUTO pass through untouched, since the extension only claims
   CAdES over RSA/SHA-256;
4. checks that disabling the site in Site access stops interception and re-enabling restores
   it.

Exit code 0 means every signature the extension produced was verified by OpenSSL.

## Safety

The identity is generated per run into a temporary directory: self-issued, valid for two days,
never written to the repository and never sent anywhere. Everything the run produces — keys,
PKCS#12, signatures — stays under the temp directory it prints at the end.

The `Archivo` target is absent on purpose: that page never calls AutoScript with a payload for
it, so there is nothing to sign or verify.

## Layout

```
registradores.mjs    the suite and its reporting
lib/extension.mjs    launches Chrome with the unpacked extension; finds chooser popups
lib/chooser.mjs      drives the consent UI the way a person would
lib/registradores.mjs page-specific selectors and payloads
lib/identity.mjs     throwaway PKCS#12 generation
lib/openssl.mjs      independent signature verification
```