# Product

<!-- impeccable:product-schema 1 -->
<!-- Resumen en español de lo que la app ya tiene: docs/LEEME.md. -->

## Platform

web

## Stack

Delegated (established codebase): SvelteKit SPA, Svelte 5, Tailwind v4, TypeScript, shadcn-svelte (bits-ui), static adapter. Signing/verification is pure client-side WebCrypto; static hosting, zero backend.

## Users

Primary: Spanish citizens and small-office clerks who must produce legally valid electronic signatures (CAdES/XAdES/PAdES on X.509 certificates) without installing desktop software. They arrive with a PKCS#12 (.p12/.pfx) certificate file, a set of documents (PDF, Office, XML), and low tolerance for jargon — many use it a few times a year. Secondary: anyone verifying a received signature file (.csig/.xsig/signed PDF) to check authenticity.

## Product Purpose

AutoFirma Web replicates the core of Spain's AutoFirma desktop tool in the browser: load a X.509 identity from a PKCS#12 container, batch-sign documents (CAdES detached, XAdES, PAdES pending), and verify signature artifacts — entirely client-side. Success: a non-expert completes a legally valid signature run without help, and trusts that keys never leave the machine.

## Positioning

Private keys never leave the client: no signing server exists to breach or subpoena. Self-hostable as a static bundle on any web server. The desktop tool's flow, minus the install.

## Operating Context

Used inside browsers (Chrome/Firefox/Safari/Edge, desktop and mobile). Users work with Spanish administration formats (.csig, .xsig, AutoFirma-generated artifacts) and PKCS#12 containers issued by national certifiers (FNMT, Camerfirma). Bilingual ES/EN interface; Spanish is the default and primary audience language.

## Capabilities and Constraints

- Sign formats: CAdES detached, XAdES, PAdES B-B — all implemented and wired into the picker.
- Visible PDF signatures (#45): optional block with an uploaded PNG/JPEG image plus the signer's name
  and date, placed by page and corner; off by default, invisible signatures unchanged.
- Stored identities (#29): save/unlock PKCS#12 containers in IndexedDB; PINs never persist.
- Batch queue with per-document status; ZIP download of signed outputs; downloads vault in IndexedDB.
- Verification of CAdES/XAdES/PDF artifacts, with detached-document re-check.
- Constraint: private keys and PINs stay client-side; no telemetry; no backend.
- Constraint: visual-only UI work must not change component prop interfaces or logic modules (sign/pkcs12/keystore/certs.ts, stores.ts, mcp/).

## Brand Commitments

Name: AutoFirma Web (ES/EN copy everywhere, `src/lib/i18n.ts` is the single copy source). License GPL-compatible (reference: clienteafirma GPL-2+/EUPL-1.1). Visual: shadcn-svelte structure and CSS-variable dark mode per issue #27; screenshots in `work/issue27-ref/` are the operator's structural reference.

## Evidence on Hand

- Operator's reference screenshots: `work/issue27-ref/ref-dark-layout.png` (2/3+1/3 layout, teal accent), `work/issue27-ref/ref-light-states.png` (upload states, progress, error, review).
- Source architecture: `docs/architecture.md`; issue thread #27 with acceptance criteria.
- Working app with real flows: sign, verify, stored identities, downloads.

## Product Principles

1. Trust is the interface: certificate state, signature status, and errors must be unambiguous at a glance.
2. Nothing leaves the machine: privacy is stated in plain words where data touches the UI.
3. Plain verbs over ceremony: controls say what they do ("Firmar", "Descargar"), errors say what happened and how to recover.
4. Bilingual by construction: every string through i18n.ts, ES-first.

## Accessibility & Inclusion

Keyboard-operable throughout; visible focus states; status changes announced (aria-live); ES/EN toggle meets contrast in both themes. Target WCAG 2.1 AA for body text and controls.
