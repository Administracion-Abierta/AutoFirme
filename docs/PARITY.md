# PARITY.md — AutoFirma 1.9 vs AutoFirma Web

Feature-by-feature audit of what the desktop AutoFirma 1.9 does against what this repo actually
implements, with an explicit verdict for anything that cannot exist in a browser.

**Method.** The original side is the decompiled core at git tag `reference/decompile-1.9`
(`git checkout reference/decompile-1.9 -- decompiled decompiled-configurador`; jadx command in
[REVERSING-NOTES.md](REVERSING-NOTES.md) §7). Citations below are `decompiled/sources/<path>:line`
— note the `sources/` segment jadx adds — and were read on the extracted tree, not from memory;
`decompiled-configurador/…` marks the installer/Configurador jar, which has its own
`sources/es/gob/afirma/` root.
The rewrite side is this repo's `src/`, `mcp/`, `test/` and the forge issues (#3–#47); a row
that names an **open** issue is not implemented yet. `[INFERENCE]` marks anything not directly
citable. Line numbers of a jadx tree shift if the tree is regenerated; the class and method names
are the stable part of every citation. Checked mechanically by
[`docs/check-parity-doc.py`](check-parity-doc.py) — every matrix row has five filled cells with a
`full`/`partial`/`missing` status and a `needed`/`browser-moot` verdict, §G gaps are numbered and
prioritized, and (with the decompiled tree restored) every citation resolves, stays in range and
still shows the identifier its row claims.

**Status** = `full` (works for the cases a citizen needs) · `partial` (real code, narrow or
unwired) · `missing` (nothing in the tree).
**Verdict** = `needed` (a web app must do it) · `browser-moot` (impossible or pointless in a
browser — the web-native replacement is named instead). A `missing` + `browser-moot` row is *not*
a gap.

This doc is the audit of issue #44. The gap list at the end is what the operator converts into
issues; nothing here is a promise that a gap will be closed.

---

## A. Signing formats

Original registry: `core/signers/AOSignerFactory.java:31`, a 29-row `{format, class, autoDetect}`
table (constants `:13-30`); `getSigner(byte[])` `:36` content-sniffs in table order,
`getSigner(String)` `:92` is an exact lookup, `getSupportedFormats()` `:114`.

| AutoFirma capability | Original evidence (package/class:line) | Ours (file · issue) | Status | Verdict |
|---|---|---|---|---|
| CAdES (BES/EPES), explicit + implicit mode, detached or embedded | `signers/cades/AOCAdESSigner.java:31` sign, `:224` mode; `cades/CAdESExtraParams.java:8` | `src/lib/cms.ts:64 signCadesDetached` | **partial** — detached + explicit only, SHA-256 only, no implicit mode; `src/lib/cms.ts:72` rejects any other hash | needed |
| CAdES-T (TSA `signature-timeStampToken`) | `signers/cades/AOCAdESSigner.java:40,234` `applyTimeStamp`; `signers/tsp/pkcs7/CMSTimestamper.java:108` | `src/lib/cms.ts:24,107` (`requestTimeStamp`, unsigned attribute) | **full** for TSA-present signing (#43) | needed |
| CAdES-ASiC-S (ZIP container, `META-INF/signature.p7s`) | `signers/cades/asic/AOCAdESASiCSSigner.java:27-30`; `core/signers/asic/ASiCUtil.java:16,18` | none (our `src/lib/zip.ts` is a batch ZIP writer only) | **missing** | needed |
| CMS / PKCS#7 generic, explicit + implicit, configurable content OID | `signers/cms/AOCMSSigner.java:35,45,48` | partial via CAdES (`src/lib/cms.ts`) — `id-data` only, no implicit/digest-signing mode | **partial** (`browser-moot` for the exotic content OIDs) | needed |
| XAdES-EPES, Enveloped | `signers/xades/XAdESSigner.java:215`; wrapper `AOXAdESSigner.java:47` | `src/lib/xades.ts:167 signXades` | **full** for enveloped XML (SHA-256/384/512, exclusive C14N, policy optional; implied by default) | needed |
| XAdES Detached / Enveloping / Externally Detached | `signers/xades/XAdESSigner.java:179,137,103` | none — file transform only | **missing** | needed |
| XAdES-T (`SignatureTimeStamp`/`EncapsulatedTimeStamp`) | `signers/xades/XAdESTspUtil.java:53` | none | **missing** | needed (#43) |
| XMLDSig (plain, 4 sub-modes, default Enveloping, SHA-1 default) | `signers/xmldsig/AOXMLDSigSigner.java:124-126,145,189,214` | none standalone (XAdES wraps a `ds:Signature`) | **missing** | needed (low priority — XAdES covers the use case) |
| PAdES B-B, incremental update, `adbe.pkcs7.detached` / `ETSI.CAdES.detached` | `signers/pades/AOPDFSigner.java:118,378`; `pades/PdfUtil.java:129` append mode | `src/lib/pades.ts:293 signPdf`, wired into the picker (`src/lib/sign.ts:20`) | **full** — engine + UI, openssl/pdfsig-checked (#11) | needed |
| PAdES visible signature (image/text, page, position) | `standalone/ui/preferences/PreferencesPanelPades.java:52`; `signers/pades/AOPDFSigner.java` appearance path | `src/lib/pades-visible.ts`, sign panel toggle off-by-default (#45) | **full** | needed |
| PAdES-T / LTV (DSS, `ETSI.RFC3161`, DocTimeStamp) | `signers/pades/PdfTimestamper.java:31,60`; `signvalidation/SignatureFormatDetectorPadesCades.java:27` | `src/lib/pades.ts:19,510,516` (B-T via TSA); LTV DSS/DocTimeStamp still absent | **partial** (B-T full, no LTV) | needed (#43) |
| OOXML (docx/xlsx/pptx) signature part + origin | `signers/ooxml/AOOOXMLSigner.java:188`; `ooxml/OOXMLZipHelper.java:41,232` | `src/lib/ooxml.ts:255 signOoxmlPackage` | **partial** — library written and tested, **not called** from `src/lib/App.svelte`/`src/lib/batch.ts` (`README.md` "Status") | needed |
| ODF (`.odt`/`.ods`) signature via manifest | `signers/odf/AOODFSigner.java:336,366` | none | **missing** | needed (low priority — office formats are rare in Spanish admin flows) |
| FacturaE 3.0/3.1 (XAdES Enveloped forced, FacturaE policy, parameter + claimed-role whitelist) | `signers/xades/AOFacturaESigner.java:26-27` (`POLICY_FACTURAE_30/31`), `:29-49` (`ALLOWED_PARAMS`, `FORMAT=…ENVELOPED`), `:81,85` (`signerClaimedRoles`, default `emisor`), `:89,94` (policy whitelist), `:69` cosign throws | none | **missing** | needed (public-sector invoicing is AutoFirma's flagship use case) |
| Raw PKCS#1 (`NONE`, `.p1`, sign-only) | `core/signers/AOPkcs1Signer.java:21` (`.p1`), `:25` (sign), `:117` (`isSign` always false) | none exposed; `src/lib/sign.ts:77 signBytes` is the internal primitive | **partial** (internal only) | browser-moot as a UI feature — nothing a user picks |
| Format auto-detection on input | `core/signers/AOSignerFactory.java:36,64` | `src/lib/verify/detect.ts:12 detectFormat` (magic bytes) | **partial** — detection exists for *verify*, not for choosing a signer | needed |
| Digest/algo matrix SHA1/256/384/512 × RSA/DSA/ECDSA | `core/signers/AOSignConstants.java:66-81` | WebCrypto only: `src/lib/sign.ts:32 SigningAlgorithm` (RSA + ECDSA, P-256/384/521, no DSA) | **partial** — SHA-1 and DSA deliberately absent | browser-moot for SHA-1/DSA (`MUST NOT` in current guidance); RSA/ECDSA = full |

**Modes and profiles** (original): `explicit`/`implicit` and `advanced`/`baseline`
(`core/signers/AOSignConstants.java:83-85,50-52`); baseline forces `ETSI.CAdES.detached` and
strips a user-supplied subfilter (`signers/pades/AOPDFSigner.java:378`). Ours: explicit/BES/EPES
only — the concept of a signer-chosen mode does not exist in the SPA.

## B. Multi-signature operations

Original `AOSigner` is a composite of `AOSimpleSigner` + `AOCoSigner` + `AOCounterSigner`
(`core/signers/AOSigner.java:10`); co-sign has a detached and an embedded overload
(`core/signers/AOCoSigner.java:11,13`) and counter-sign takes a `CounterSignTarget`
(tree vs leaf, `core/signers/AOCounterSigner.java:11`).

| Capability | Original evidence | Ours | Status | Verdict |
|---|---|---|---|---|
| CAdES co-sign (detached + embedded) | `signers/cades/AOCAdESSigner.java:51,75` → `signers/multi/cades/CAdESCoSigner.java:127` | none — one signer per artifact | **missing** | needed |
| CAdES counter-sign (tree + leaf) | `signers/cades/AOCAdESSigner.java:101` | none | **missing** | needed |
| XAdES co-sign / counter-sign | `signers/xades/AOXAdESSigner.java:222,244` | none (`src/lib/xades.ts` writes a fresh `ds:Signature`) | **missing** | needed |
| PAdES / ODF / OOXML co-sign = re-sign the same document | `signers/pades/AOPDFSigner.java:149`; `signers/odf/AOODFSigner.java:542`; `signers/ooxml/AOOOXMLSigner.java:202` | none wired; `src/lib/pades.ts:293 signPdf` appends one revision, so co-sign would work at the byte level | **missing** (engine-ready, unreachable) | needed |
| Counter-sign of PAdES/ODF/OOXML/FacturaE (throws in the original) | `signers/pades/AOPDFSigner.java:159`; `signers/odf/AOODFSigner.java:552`; `signers/facturae` `AOFacturaESigner.java:74` | n/a | **missing** | browser-moot by construction — the original refuses too |

## C. Verification and validation

Original validators live in `es.gob.afirma.signvalidation` (there is **no** `core/verify`
package): base `signvalidation/SignValider.java:10`, factory by magic bytes
`signvalidation/SignValiderFactory.java:20`, binary path `signvalidation/ValidateBinarySignature.java:99`.

| Capability | Original evidence | Ours | Status | Verdict |
|---|---|---|---|---|
| CAdES/CMS verification (structure, digest, signature, cert validity) | `signvalidation/ValidateBinarySignature.java:99,120,126`; `signers/cades/CAdESValidator.java:186` | `src/lib/verify/cms.ts` `verifyCades` (structure, messageDigest vs content, CMS signature, signer cert, validity window, chain) — independent-validator evidence: openssl `CMS Verification successful` (`evidence/raw/cades.openssl.log`), EU DSS `SignatureValid: true`, `CAdES-BASELINE-B` (`evidence/raw/dss.cades.verdict.json`); see [evidence/GATE-12.md](evidence/GATE-12.md), [external-validators.md](evidence/external-validators.md) | **full** for the checks it names | needed |
| XAdES verification (references, transforms, signature value) | `signvalidation/ValidateXMLSignature.java:51,126`; `standalone/configurator/common/XAdESValidator.java:35` | `src/lib/verify/xades.ts` (references + transforms, C14N, signature, signer cert) — independent-validator evidence: xmlsec1 `Verification status: OK` (`evidence/raw/xades.xmlsec1.log`), EU DSS `SignatureValid: true`, `XAdES-BASELINE-B` (`evidence/raw/dss.xades.verdict.json`); see [evidence/GATE-12.md](evidence/GATE-12.md), [external-validators.md](evidence/external-validators.md) | **full** | needed |
| PAdES verification (byte range, signature, trust) | `signvalidation/ValidatePdfSignature.java:64,71,164` | `src/lib/verify/pades.ts` via the PDF dispatch in `verifyArtifact` (byte-range, digest, chain; no form-modification/shadow-attack depth) — independent-validator evidence: pdfsig + qpdf + openssl agree on ByteRange [0 1590 6120 669] (`evidence/raw/pades.interop.log`), EU DSS `SignatureValid: true`, `PAdES-BES`, same ByteRange (`evidence/raw/dss.pades.verdict.json`); Acrobat still pending — see [evidence/GATE-12.md](evidence/GATE-12.md), [external-validators.md](evidence/external-validators.md) | **partial** (no PSA/modification depth) | needed |
| OOXML/ODF verification | original has **no** validator either (`SignValiderFactory.java:31,35` warn and return `null`) | n/a | **missing** | browser-moot — feature parity means *also* not validating them; ours says so explicitly |
| External-reference policy (`CANT_VALIDATE_EXTERNALLY_DETACHED`, relaxed mode) | `ValidateXMLSignature.java:128`; `SignValider.java:20` | none — unresolved references fail the check | **partial** | needed (low priority) |
| Trust chain to anchors | **absent in the original too**: the validators check `x509Certificate.checkValidity()` only (`ValidateBinarySignature.java:126`); modern validation is delegated to VALIDe (`:143` `SIGN_PROFILE_NOT_CHECKED`) | `src/lib/verify/roots.ts:7` + `buildChain` — chain building against committed anchors, currently **FNMT RACM only** | **partial** | needed (our side is *ahead*; the gap is anchor coverage, not the mechanism) |
| OCSP / CRL revocation checking | none first-party: no `OCSPResponder`/`CertPathValidator` under `es/gob/afirma`; `CRL_PROBLEM` exists as an enum value only (`signvalidation/SignValidity.java`) | none (zero `ocsp`/`crl` hits in `src/`) | **missing** | needed |
| Timestamp-token verification | not implemented; levels are *detected* only (`signvalidation/SignatureFormatDetectorPadesCades.java:164` `resolvePDFFormat`, `:441` `isCAdESLTLevel`) | none | **missing** | needed (#43) |
| Signature-level detection (B/EPES/T/LT/LTA, A/C/X/XL, ASiC-S B/T/LT/LTA) | `signvalidation/ISignatureFormatDetector.java:5-51`; `SignatureFormatDetectorXades.java` | none | **missing** | needed |
| Validity taxonomy (`OK/KO/UNKNOWN/PENDING_CONFIRM`, 24 error codes) | `signvalidation/SignValidity.java:11-48` | `src/lib/verify/types.ts` `CheckStatus` (`pass`/`fail`/`info`) + a fixed set of check ids | **partial** — enough for a citizen report, not machine-mappable to AutoFirma codes | browser-moot (different UI, richer per-check detail) |

## D. Identity and key acquisition

Original: `keystores/AOKeyStore.java:13-31` (18 store types), dispatch
`keystores/AOKeyStoreManagerFactory.java:34`, per-OS default `AOKeyStore.java:100`.

| Store | Original evidence | Ours | Status | Verdict |
|---|---|---|---|---|
| PKCS#12 / PFX, any OS | `keystores/Pkcs12KeyStoreManager.java:14` | `src/lib/pkcs12.ts`, `src/lib/keystore.ts` (PBES2 + legacy PBES1 DES/RC2/RC4 hand-rolled, container MAC verified, key imported **non-extractable**) | **full** | needed |
| JKS / JCEKS / JAVACE | `keystores/JavaKeyStoreManager.java:12`; `AOKeyStoreManager.java:150-153` | none | **missing** | needed (low priority — a leftover from Java tooling) |
| Single cert / PKCS#7 file (`SINGLE`) | `keystores/AOKeyStoreManagerHelperSingle.java:20` | verify accepts a PEM/DER set: `src/lib/verify/roots.ts`, `test/fixtures/ca/` | **partial** (verify side only) | needed (low priority) |
| Windows CAPI `Windows-MY` + address book + intermediate CAs | `keystores/CAPIKeyStoreManager.java:56`; `CAPIUnifiedKeyStoreManager.java:29`; `AOKeyStoreManagerHelperCapiAddressBook.java:19` | none | **missing** | browser-moot — browsers expose no OS key store to the page; replacement: import the `.p12` from the local disk. **Held by the browser vendor, not by us** — Chrome/Firefox ship `webcrypto` without any OS-store bridge; nothing to do until a Web API appears. |
| macOS Keychain | `keystores/AppleKeyStoreManager.java:19,60` | none | **missing** | browser-moot (same reason) |
| Mozilla/NSS profile store (the Linux default) | `keystores/mozilla/NssKeyStoreManager.java:33`; `mozilla/MozillaKeyStoreUtilities{Unix,Windows,OsX}.java` | none | **missing** | browser-moot — the page cannot open the browser's NSS DB; replacement: upload the `.p12` |
| DNIe / CERES / CardOS / SmartCafe smartcards via vendor PKCS#11 | `keystores/SmartCardUnifiedKeyStoreManager.java:86-89`; `mozilla/MozillaKeyStoreUtilities.java:41` | none | **missing** | browser-moot — no PKCS#11 or PC/SC from a page; replacement: `dnie-remote`/`eID` flows, a local bridge app, or the card's PKCS#12 export. Marked *moot for the SPA*, still a real gap for Spanish DNIe users (§H) |
| JMulticard pure-Java card stack (APDU over PC/SC) | `keystores/AOKeyStoreManagerHelperFullJava.java:27`; `jmulticard/**` | n/a — not browser-reachable | **missing** | browser-moot |
| Generic PKCS#11 module picker (`.dll/.so/.dylib`) | `AOKeyStoreManagerHelperPkcs11.java:28`; `AOKeyStoreManagerFactory.java:266` | none | **missing** | browser-moot (WebCrypto cannot load a native module) |
| Several certificates per container, picker, expiry shown | `ui/preferences/PreferencesPanelKeystores.java:57` | `src/lib/components/CertPicker.svelte`, `src/lib/keystore.ts` (`expired`/`notYetValid` per cert) | **full** | needed |
| Storing identities for reuse (new in the web app, no original equivalent) | — (the original re-reads the OS store each run) | `src/lib/certstore.ts` (IndexedDB, identities sealed with PBKDF2→AES-GCM; #29) | **full** | needed — this is the web replacement for the OS keystore |

## E. Batch / massive signing

The original has two unrelated things called "batch":

| Capability | Original evidence | Ours | Status | Verdict |
|---|---|---|---|---|
| Local JSON batch definition (`singlesigns[]`, `suboperation`, per-item `result`) | `standalone/protocol/JSONBatchManager.java:20,22,29`; `standalone/protocol/LocalBatchSigner.java:30` | `src/lib/batch.ts` — sequential queue, per-file status, ZIP + `manifest.json` mirroring those field names | **full** for the client-side subset | needed |
| UI multi-file queue | `standalone/ui/SignPanel.java:684` (`getLoadFiles(…, false, true)` = multi-select), `:780-784` (`SignPanelMultiFilePanel`, `SignPanelMultiFilePanel.java:45,50`) | multi-file queue is **wired**: `src/lib/components/FileDrop.svelte:98` (`multiple`), `src/lib/batch.ts:101 createBatchQueue`, per-file rows and one ZIP in `src/lib/App.svelte`; exercised by `src/lib/App.test.ts:251` | **full** for files | needed |
| Massive operation types `SIGN/COSIGN/COUNTERSIGN_ALL/COUNTERSIGN_LEAFS` over a directory | `massive/MassiveType.java:4`; `massive/DirectorySignatureHelper.java:88,96` (walks `File.listFiles()`) | none — the multi-file *file* queue above exists, whole-directory input and the co/counter-sign operation types do not (follows §B) | **missing** | needed |
| Server-side tri-phase batch (`presigner`/`postsigner` URLs, `certs`, `tridata`) | `core/misc/protocol/UrlParametersForBatch.java:11,12` (`batchpresignerurl`/`batchpostsignerurl`); `signers/batch/client/BatchSigner.java:38,39` (`certs`, `tridata`); `TriphaseDataParser.java:31` | none, by design: there is no server holding keys | **missing** | browser-moot — the whole point of the SPA is that the key stays local; a server phase would be a downgrade |
| Batch ZIP container | no batch-specific ZIP exists in the original (`ASiCUtil.java` is ASiC, not batch) | `src/lib/zip.ts` + `scripts/verify-batch-zip.mjs` | **full** (ours, an addition, not parity) | needed |

## F. Application surface

| Capability | Original evidence | Ours | Status | Verdict |
|---|---|---|---|---|
| `afirma://` protocol for web portals: `sign`/`cosign`/`countersign`, `selectcert`, `load`, `save`, `signandsave`, `batch` | `standalone/protocol/ProtocolInvocationLauncher.java:372,203,437,251,306,151`; params `core/misc/protocol/UrlParameters.java:17` | none | **missing** | browser-moot — a page cannot register an OS URI scheme; replacement: the SPA *is* the portal-side flow (file in, signature out, no native hand-off), and for agents the MCP server (`mcp/src/surface.ts:55,66,95` `autofirma_list_certs`, `autofirma_sign`, `autofirma_verify`; remote transport #46) |
| Local WebSocket/raw-socket server on port 63117 for portal callbacks | `ProtocolInvocationLauncher.java:47,109,132`; `AfirmaWebSocketServerV4.java` | none | **missing** | browser-moot — replaced by direct browser APIs and, where a portal must be scripted, the MCP/HTTP endpoint (#46) |
| CLI (`listaliases`, `sign`, `cosign`, `countersign`, `verify`, `batchsign`; `-store -alias -format -algorithm -config …`) | `standalone/CommandLineCommand.java:5-11`; `standalone/CommandLineLauncher.java:264`; `standalone/CommandLineParameters.java:10-51` | `mcp/` stdio server (sign cades + verify; `mcp/src/tools.ts:19` refuses xades/pades with a reason) | **partial** | needed (#46) — a scriptable surface is what replaces the CLI |
| Main window: open, sign (Ctrl+F), verify (Ctrl+V), bulk results | `standalone/SimpleAfirma.java:607`; `standalone/ui/MainMenu.java:54-167` | `src/routes/+page.svelte` + `src/lib/App.svelte` (drop zone, sign panel, verify panel) | **full** for the core flow | needed |
| Preferences (per-format: visible signature, shadow-attack check, co/counter-sign defaults, policy, hash, proxy, card management) | `standalone/ui/preferences/PreferencesPanel.java:41-66` and its per-format panels | partial: theme switcher (`src/lib/theme.ts`), format picker; no per-format preference tabs | **partial** | needed (low priority — most originals are desktop-only knobs; a web app can hard-code the safe default) |
| Signature viewer window (`VisorFirma`) | `standalone/VisorFirma.java:24`; `standalone/ui/VisorPanel.java:63` | `src/lib/verify/` report rendered by the verify panel in `src/lib/App.svelte` | **full** | needed |
| i18n ES/EN | Java `.properties` bundles | `src/lib/i18n.ts` | **full** | needed |
| OS installers, localhost SSL cert, root-cert injection into OS/browser stores | `decompiled-configurador/sources/es/gob/afirma/standalone/configurator/ConfiguratorLinux.java:27`; `…/ConfiguratorMacOSX.java:244`; `…/ui/restoreconfig/RestoreConfigFirefox*.java` | none — `Containerfile` (caddy) + static build (#12); deploy docs | **missing** | browser-moot — HTTPS comes from the host; no local CA to install; replacement: the own-domain deploy |
| Auto-updater (`updateSite`, per-OS versions) | `standalone/updater/Updater.java:22,126`; `SimpleAfirma.java:662` | none; PWA service worker #47 (open) | **missing** | browser-moot — the browser refreshes the app; replacement: cache-busting via the static host, offline shell via #47 |
| Plugin jars from a `plugins` dir | `standalone/SimpleAfirma.java:92`; `plugins/manager/PluginInfoLoader.java` | none | **missing** | browser-moot — no dynamic native code in a page; replacement: MCP tool servers (#46) |
| File-system dialogs / "reveal output folder" | `AOUIFactory.getLoadFiles`; `MassiveResultProcessPanel.java:93` `Desktop.getDesktop().open` | native file picker + browser downloads (per-row `Descargar` and the batch ZIP) | **full** | browser-moot — the browser sandbox owns file I/O; the web-native equivalent is in place |
| Proxy configuration with encrypted password | `ui/preferences/ProxyPanel.java:23,183` | n/a — the browser owns the proxy | **missing** | browser-moot |
| Look-and-feel / high-contrast / font scaling | `LookAndFeelManager.java`; `MassiveResultProcessPanel.java:74` | Tailwind v4 themes (`src/app.css`, `src/lib/theme.ts`), semantic markup | **partial** | browser-moot — replaced by the browser's own zoom/high-contrast; accessibility is a repo requirement (AGENTS.md), not a parity row |
| Desktop-app packaging, per-OS JRE handling | `Configurator*.java`; `standalone/updater/**` | static SPA + `Containerfile`; desktop install #47 (open) | **partial** | browser-moot |

## G. Gap list — missing **and** needed, prioritized

Each line is issue-sized. `P0` breaks the "drop-in replacement" promise for a normal citizen,
`P1` blocks a common public-administration flow, `P2` is nice-to-have.

**P0**

1. PAdES verification depth: form modification and PSA/shadow-attack checks still absent
   (`signvalidation/ValidatePdfSignature.java:64,71,164`); byte-range, digest and chain already
   ship (`src/lib/verify/pades.ts`). *(New — not covered by an open issue.)*
2. OOXML signing is written and tested but unreachable from the UI (`src/lib/ooxml.ts`,
   `README.md` "Status"); wire it, with verify-side honesty about the missing validator.
3. DNIe / CERES smartcard users have no path: `browser-moot` as a *native* driver; the replacement
   (a local bridge app or an eID/`dnie-remote` flow) needs a product decision. Decision recorded
   2026-10-16: tracked as a possible improvement (`README.md` "Possible improvements"); the
   desktop-app route is discarded for now (`docs/PIVOT-STUDY.md` §5), so a no-key-export PKCS#11
   helper stays out of scope until card support is taken up. *(New.)*

**P1**

4. XAdES-T RFC 3161 timestamp (#43, open) — `signers/tsp/pkcs7/CMSTimestamper.java:108` (CAdES-T and PAdES B-T already ship).
5. FacturaE 3.0/3.1 signing (`signers/xades/AOFacturaESigner.java:26-27,29-49,81,89`).
6. Co-sign and counter-sign for CAdES/XAdES/PAdES (`signers/multi/cades/CAdESCoSigner.java:127`;
   `signers/xades/AOXAdESSigner.java:222,244`). *(New.)*
7. ASiC-S containers, binary and XML (`core/signers/asic/ASiCUtil.java:16-19`). *(New.)*
8. Revocation checking (OCSP/CRL) and timestamp-token validation — the original delegates this to
   VALIDe (`ValidateBinarySignature.java:143` `SIGN_PROFILE_NOT_CHECKED`); we do strictly less.
   *(New.)*
9. Trust-anchor coverage: `src/lib/verify/roots.ts:7` holds FNMT RACM only; a signature from any
   other Spanish CA reports "chain untrusted". *(New.)*
10. XAdES Detached/Enveloping/Externally Detached (`signers/xades/XAdESSigner.java:103,137,179`).
   *(New, low volume.)*

**P2**

11. Signature-level detection and the AutoFirma validity taxonomy
   (`signvalidation/ISignatureFormatDetector.java:5-51`; `SignValidity.java:11-48`) — needed to
   answer "is this an LTA signature?" (`[INFERENCE]` low demand for a citizen UI).
12. ODF signing (`signers/odf/AOODFSigner.java:336,366`). *(New.)*
13. JKS/JCEKS containers and single-cert/PKCS#7 inputs (`keystores/JavaKeyStoreManager.java:12`;
   `AOKeyStoreManagerHelperSingle.java:20`). *(New.)*
14. Massive directory signing with `COSIGN`/`COUNTERSIGN_*` (`massive/MassiveType.java:4`;
   `massive/DirectorySignatureHelper.java:96`) — the whole-directory half of #42, depends on 7.
   *(New.)*
15. MCP server parity with the CLI: XAdES/PAdES refused at runtime (`mcp/src/tools.ts:19`),
   remote transport still open (#46).
16. Plain XMLDSig signing (`signers/xmldsig/AOXMLDSigSigner.java:124`). *(New.)*

**Deliberately not gaps** (browser-moot with the replacement named): smartcard and OS-keystore
access, the `afirma://` URI scheme and its local socket server, OS/browser trust-store
installation, the auto-updater, plugin jars, proxy configuration, JRE packaging, and SHA-1/DSA
signature algorithms. See the *Verdict* column — the replacement is stated per row.

## H. Bottom line

The web app replaces AutoFirma for: **CAdES-BES + CAdES-T** and **XAdES-EPES** signing, **CAdES, XAdES
and PAdES verification** (PAdES without form-modification/shadow-attack depth), **PAdES B-B and B-T
signing with visible signatures**, certificate loading from **PKCS#12**, **batch** signing with a
manifest, ES/EN UI, and self-hosting. The gap between that and "drop-in replacement" is
concentrated in four places: **PAdES depth** (LTV, PSA/shadow checks), **office formats** (OOXML
unwired, ODF absent, FacturaE absent), **long-term validity** (XAdES-T, OCSP/CRL, anchors = FNMT
only), and **multi-signature** (no co-sign/counter-sign). The desktop-specific half of AutoFirma —
smartcards, OS keystores, the `afirma://` hand-off, installers — has no browser equivalent by
construction; recorded product decision: PWA stays the primary surface, desktop discarded for now
(`docs/PIVOT-STUDY.md` §5).
