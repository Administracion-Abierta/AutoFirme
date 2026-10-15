# AutoFirme Chrome extension implementation plan

> **For agentic workers:** Use subagent-driven-development for the independent build, bridge and UI deliverables; integrate and verify in this session.

**Goal:** Package the complete AutoFirme webapp with a responsive request chooser.
**Architecture:** One Svelte app, two build targets. MV3 bridge routes supported website requests to an extension-owned Svelte chooser using the real CertPicker, certstore and CMS implementation.
**Tech Stack:** Existing Svelte 5/SvelteKit/Vite/TypeScript/pnpm; Chrome MV3.
**Spec:** docs/superpowers/specs/2026-10-15-autofirme-extension-design.md

## Global constraints

- Separate clone: /home/kumibrr/.t3/projects/autofirme, branch feat/chrome-extension.
- Preserve the full real webapp and its normal web build. No hosted iframe and no reduced replacement application.
- Chrome 116+, explicit per-site optional HTTP/HTTPS permissions, no default blanket host grants.
- User explicitly approves every signature. No Bitwarden integration; only a similar chooser interaction.
- Original fallback at most once before signing; never replay after signing begins or on ambiguous disconnect.
- PINs/private keys stay in extension-owned app contexts. Use upstream certstore, not the SuperFirma vault.
- Initial bridge supports selectCertificate and RSA/SHA-256 CAdES implicit/explicit with 4MiB canonical Base64; unsupported calls pass through untouched.
- All implemented upstream features remain available in the main app, including PDF stamps, XML signing, batch, verification, downloads, ES/EN and themes.
- PWA installation/update is web-only; explicit TSA permission for extension timestamps.
- Work on the feature branch; commit but do not push. Independent signature verification required.

## Task 1: MV3 app build

Files: vite.config.ts, package.json/pnpm-lock.yaml if needed, src/app.html,
src/lib/components/PwaControls.svelte, src/lib/timestamp.ts, scripts/build-extension.mjs,
extension/manifest.json, extension/bootstrap.js, build checks.

1. Establish baseline pnpm install, test, check and build; distinguish pre-existing failures.
2. Write build checks rejecting inline executable script/eval, missing local assets,
   PWA registration and missing full-app entry. Run against the initial output and observe failure.
3. Add an extension build mode compiling the actual Svelte app. Use Vite/Svelte entry
   if SvelteKit bootstrap cannot satisfy MV3 CSP. Keep regular web target unchanged.
4. Produce dist/extension/index.html, app JS/CSS/local assets and bundle extension/background.js,
   extension/page.js and extension/content.js. Bundle all code locally. Use an exact
   self-only CSP; preserve stamp/download functionality. Manifest action has no ephemeral
   popup: background opens a compact resizable app window. options_page is index.html.
5. Disable PWA controls/registration only in extension context. Externalize theme bootstrap.
6. Timestamp module checks extension host permission before HTTP; the UI supplies an explicit
   grant button. Web builds keep normal TSA behavior. No generic fetch proxy.
7. Run regular build, extension build and relevant checks; commit owned files only.

## Task 2: Request and site access bridge

Files: extension/background.js, page.js, content.js, core/*.js and extension tests.
Reuse reviewed adapter/contract/lifecycle logic from sibling superfirma as appropriate,
rename channels to autofirme and remove vault/helper dependencies. Root adds types/UI.

Interfaces (Chrome runtime.sendMessage, response {ok:true,value} or {ok:false,error}):
- {type:'request:get',id} -> {id,origin,operation,format?,mode?,data?,digest?}.
- {type:'request:begin',id} -> true; transitions waiting -> signing.
- {type:'request:complete',id,result:{certificate,signature?}} -> true; only after begin.
- {type:'request:fail',id} -> true; terminal error, never fallback.
- {type:'request:cancel',id} -> true; terminal cancellation before signing.
- {type:'request:fallback',id} -> true; only before signing.
- {type:'app:open',tab?:boolean} -> true; open full app, preserve pending request.
- {type:'sites:list'} -> {origins:string[],suggestedOrigin:string|null}.
- {type:'sites:set',origin,enabled:boolean} -> true; UI first obtains optional permission.

Page ports use name autofirme-page and request {id,request}, result {id,response}.
Chooser URL index.html?request=<opaque UUID>; background creates popup window
(width480,height720) and records its tab ID before authorizing UI operations.
Main app toolbar window width520,height760. Website requests get at most 8 pending
per document and a five-minute deadline. Worker loss cannot silently replay.

1. Write failing unit tests for validated bridge requests, original callback preservation,
   sender-derived identity, UI authorization, concurrent request isolation, lifecycle,
   stale documents and exactly-once fallback. Run and observe failure.
2. Implement validated contract, request store, adapter and Chrome integration. Keep
   request state independent of Chrome API plumbing for unit testing.
3. Authenticate request UI by exact own index.html URL and the recorded chooser tab ID;
   reject pages/content scripts/other app tabs. Verify document binding and drop late results.
4. Store enabled origins in chrome.storage.local; use only these origins for dynamic
   MAIN/ISOLATED scripts, document_start and all frames. Host permission alone (e.g. TSA)
   must not activate interception. Remove registrations on revoke; check stored grant on
   request reception. No remote source code or native service required.
5. Run tests; commit bridge files only. Share any interface changes with root/UI implementer.

## Task 3: Actual webapp and chooser UI

Files: src/lib/extension/{runtime.ts,ExtensionShell.svelte,RequestChooser.svelte,
SiteAccess.svelte,sign-request.ts}, src/routes/+page.svelte, CertPicker optional props,
small responsive changes, component tests. Do not edit build/manifest/background files.

1. Write failing tests for request confirmation, invalid/expired/RSA-ineligible certificate,
   cancellation/fallback and ensuring certificate selection alone never signs.
2. Route ?request= to RequestChooser only on chrome-extension: origin; otherwise render
   the actual App.svelte. ExtensionShell wraps actual App with site-access/open-tab controls.
3. Reuse real CertPicker, browserVault, language copy and theme tokens. Chooser shows site,
   operation and digest, compact certificate owner/issuer/expiry summary and explicit
   confirmation. Show Import/unlock via existing picker. Add optional autoOpen prop if needed.
4. Request:get loads data. Confirmation first calls request:begin, then local sign-request
   (or returns cert selection), then request:complete; failures call request:fail. No automatic
   completion on certificate selection. Pending UI disabled during signing; errors actionable.
5. sign-request adapts real cms.ts to attached/chain support if needed; use raw issuer/serial
   extraction so legal NumericString certificate names do not break CMS signing. Test with
   OpenSSL. Bound input and reject ineligible certificate before beginning.
6. SiteAccess explicitly requests exact origin permission on click, then sites:set persists
   enabled interception. TSA permission is a separate network-only grant. Provide revoke and
   open-full-tab controls. No prompts or toggles in the ordinary hosted web app.
7. Keep full feature UI accessible at narrow widths (320px and up) with no horizontal overflow.
   Run component tests/type checks; commit owned files only.

## Task 4: Integrated browser verification, docs and package

Root owns integration tests and docs; resolves cross-task issues with focused regression tests.

1. Run all upstream tests, pnpm check, regular build, extension build and artifact CSP checks.
2. Launch disposable Chromium per documented extension-test workflow (required unpacked
   extension flags unavailable in shared preview). Use non-production OpenSSL PKCS#12 fixture.
3. Verify actual full app features/screens, narrow viewport, request chooser, import/unlock,
   both CMS modes with OpenSSL -cades, selectCertificate callback, no implicit signing,
   explicit/dismissal fallback exactly once, cancellation, unsupported PDF pass-through,
   lock/session behavior, stale requests and concurrent chooser binding.
4. Preserve web/PWA checks. Document any upstream failing baseline honestly, and repair issues
   necessary to keep extension safe and functional. Never claim untested platforms/sites.
5. Independent task-scoped spec/code reviews, address important findings, then whole-branch
   review and focused re-verification. Track rulings in execution ledger.
6. Write extension installation/security/compatibility/verification documentation; package
   dist/autofirme-extension.zip with notices and source revision. Verify archive and SHA-256.
7. Commit final changes, leave branch local, report precise test evidence and archive path.
