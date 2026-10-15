# AutoFirme webapp Chrome extension

## User requirements

Work in this separate AutoFirme clone, not the SuperFirma implementation. Package
the actual AutoFirme webapp as a responsive Chrome extension, retaining its features
and components. Incoming website requests open a certificate chooser with the
interaction style of Bitwarden's passkey selector. There is no Bitwarden integration.

## Approach

Add a Manifest V3 build target to the existing Svelte app. The normal web build
remains usable. Both builds render the same App.svelte and use the same certificate
store, signing engines, verification engines, batch queue, stamp configuration,
settings, ES/EN translations, theme and downloads. Do not copy a subset of engines
into another application or wrap the hosted site in an iframe.

Alternative approaches considered: a separate extension UI duplicating AutoFirme
would diverge and omit features; a hosted-site wrapper would depend on remote code
and an external origin. Bundle the actual local app instead.

The toolbar opens a compact, resizable extension window, with a full-tab option for
large documents and stamp controls. Responsive layouts support narrow widths and
keyboard navigation. Use a persistent window rather than an action popup that is
destroyed when the user clicks elsewhere during a signing or batch operation.

## Full app and browser packaging

Retain signing and verification, all implemented formats, PDF visible stamps,
batch ZIPs, stored certificates, stored outputs, theme and language settings.
Use AutoFirme's existing PIN-protected identity store; no separate SuperFirma vault.
Sessions and private keys remain in extension-owned app contexts. Existing hosted
webapp storage is a separate origin and is not automatically imported.

Bundle scripts, styles and fonts locally. Adapt HTML bootstrap, asset paths and
SvelteKit output to extension CSP without unsafe-eval or inline executable scripts.
The extension is installed and updated by Chrome; PWA install/update controls and
webapp service-worker registration belong only to the normal web build. Timestamp
requests remain explicit opt-in functionality, with permission requested for the
configured TSA rather than blanket network access.

## Incoming request flow

Register page and isolated-world adapters only for origins the user enables.
Derive requesting origin/tab/frame/document from Chrome metadata, bind a fresh
request ID to that document, and retain the original AutoScript call for fallback.

Open a dedicated extension-owned chooser window using AutoFirme's CertPicker and
identity store. Show the requesting website, operation, format and document digest.
Use compact certificate rows/cards with owner, issuer and expiry, a clear selected
state, and explicit confirmation. Allow import/unlock using the real app's existing
certificate workflow. Include a way to open the complete app without resolving the
pending request. Manage concurrent requests separately.

Only confirmation signs. Return the public certificate and signature through the
original callback contract. Never expose PINs, private keys or generic vault APIs to
website messages. Cancel reports cancellation. Continue with AutoFirma, or closing
the chooser before signing, resumes the original call once. Closing/disconnecting
after signing starts never automatically replays the call. Document navigation,
service-worker loss and expired requests reject/drop stale results.

Keep the previously tested first-release interception contract: certificate
selection and RSA/SHA-256 CAdES with explicit or implicit content. Unsupported
formats/options use original AutoFirma untouched. All other AutoFirme features,
including PDF stamps and XML signing, remain available in the full extension app;
this does not promise arbitrary AutoScript parameters are supported. Extend the
website contract only with explicit mappings and independent verification.

System-store signing is not supplied by the browser-only upstream webapp. Native
helper integration would be a separate, explicitly documented extension feature;
do not label stored PKCS#12 identities as OS certificates.

## Validation and deliverables

Run upstream pnpm tests, type checks and web build, plus extension build. Verify
feature availability and responsive behavior in the bundled app. Test real MV3
installation in a disposable Chrome profile, identity import/unlock, website
requests, chooser consent, cancellation, exactly-once fallback, unsupported calls,
concurrency and stale-document rejection. Verify generated signatures independently
with OpenSSL; retain upstream format cross-checks. Build a ZIP and record upstream
revision, changes, verification limits and license status. Do not push or publish.

Upstream at inspection: d6dfa434d3be2aa2ed53b3956f7d10c3ab8c1035. No declared
license was found at this revision; record that fact without inventing a grant.
