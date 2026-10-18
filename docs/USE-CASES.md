# USE-CASES.md — what AutoFirma 1.9 really does, and where AutoFirma Web stands

Feature matrix of the **shipped AutoFirma 1.9** (`autofirma.jar`, the reference implementation) against
this repo. Written for #41. It answers three questions per capability:

1. **Does it exist in AutoFirma 1.9 for a user?** — GUI, CLI, or the `afirma://` browser protocol.
2. **Which Java code implements it?** — a `file:line` citation.
3. **Do we have it?** — status, and the priority of the gap.

`Refs #41`. Complements [architecture.md](architecture.md) (signing-core map, dependency triage) and
[REVERSING-NOTES.md](REVERSING-NOTES.md) (provenance, packaging). Neither is repeated here.

## 0. Method and conventions

Citations are relative to the **decompiled tree of tag `reference/decompile-1.9`**, i.e.
`decompiled/sources/es/gob/afirma/<path>:<line>`. Recover it with:

```bash
git archive reference/decompile-1.9 decompiled | tar -x -C work/   # gitignored scratch
```

Decompilation is lossy (names, lambdas); every row is a *map*, not source. Behaviour that a file does
not literally show is marked `[INFERENCE]` with the search behind it. Upstream source for
cross-reading: <https://github.com/ctt-gob-es/clienteafirma>.

**Status legend**

| Mark | Meaning |
|---|---|
| `DONE` | shipped in this repo, exercised by a test or a cross-check |
| `PARTIAL` | partly there — engine without UI, or UI without the profile/options |
| `MISSING` | not implemented |
| `N/A-BROWSER` | existed only because AutoFirma is a desktop Java app; the SPA replaces the mechanism |
| `OUT-OF-SCOPE` | deliberately out of scope for a browser app (per AGENTS.md "browser replaces them") |

**Priority legend** (for `MISSING`/`PARTIAL` rows only)

| Mark | Meaning |
|---|---|
| `P0` | required for a full replacement of AutoFirma for its real Spanish use cases |
| `P1` | needed for parity; a workaround or a rare case exists |
| `P2` | cosmetic, legacy, or server/JVM-bound |
| `—` | not a gap |

## 1. Entry points

| Channel | 1.9 evidence | What it offers | Our status |
|---|---|---|---|
| Swing GUI | `standalone/SimpleAfirma.java:84,152,607`; `standalone/ui/MainMenu.java:53-160` | Abrir / Firmar (Ctrl-F) / Ver firma (Ctrl-V), Herramientas → restaurar configuración, plugins, preferencias, Ayuda | Workbench SPA (`src/lib/App.svelte`) — `PARTIAL` |
| CLI (`autofirma` / `autofirmacl`) | `standalone/CommandLineLauncher.java:263-288`; `standalone/CommandLineCommand.java:5-10`; `standalone/CommandLineParameters.java:10-27` | Commands `listaliases`, `sign`, `cosign`, `countersign`, `verify`, `batchsign` + `-i -o -alias -filter -store -format -password -algorithm -config -operation -gui -certgui -preurl -posturl -xml -r` | `N/A-BROWSER` (no shell) |
| Browser protocol | `standalone/protocol/ProtocolInvocationLauncher.java:109,132,151,203,251,306,372,437`; params in `core/misc/protocol/UrlParameters.java:18-32` | `afirma://` `websocket`, `service`, `batch`, `selectcert`, `save`, `signandsave`, `sign`/`cosign`/`countersign`, `load` | `N/A-BROWSER` mechanism; the *equivalent* embed API is `MISSING` (§8, P0) |
| Plugins (JVM SPI) | `standalone/plugins/AfirmaPlugin.java:6`; `standalone/plugins/manager/PluginLoader.java:24`; `standalone/plugins/PluginCommand.java:4` | third-party menu actions, CLI commands, sign data processors, custom JARs | `OUT-OF-SCOPE` |
| Configurator (separate JAR) | `standalone/configurator/common/PreferencesManager.java:14`; `standalone/ui/restoreconfig/RestoreConfigLinux.java:25` | registers the protocol handler, installs CA certs into Firefox/Chrome/OS stores, unattended install | `OUT-OF-SCOPE` |
| Per-browser detection (incl. Safari) and per-OS paths | `core/misc/Platform.java:18-23,106-113` | the configurator picks the browser/OS-specific integration to install (Firefox cert DB, Chrome/OS store, macOS dock…) | `OUT-OF-SCOPE` — the SPA is *run by* the browser, so there is nothing to detect or install. In the shipped core this detection has no caller anyway `[INFERENCE: rg "BROWSER\|getBrowser" outside Platform.java → only unrelated third-party hits]` |

## 2. What can be signed — formats

| Format | 1.9 implementing class | Registered as | Our engine | Status | Prio |
|---|---|---|---|---|---|
| CAdES (detached CMS) | `signers/cades/AOCAdESSigner.java` | `AOSignConstants.SIGN_FORMAT_CADES` `core/signers/AOSignConstants.java:13` | `src/lib/cms.ts` (#6), with TSA support for CAdES-T (#43) | `DONE` (BES + T) | — |
| CMS/PKCS#7 (no CAdES attrs) | `signers/cms/AOCMSSigner.java` | `core/signers/AOSignConstants.java:10` | — | `MISSING` | P2 |
| Raw PKCS#1 | `core/signers/AOPkcs1Signer.java` | `NONE`/`PKCS1`/`PKCS#1` `core/signers/AOSignConstants.java:15-17` | — | `MISSING` | P2 |
| XAdES (enveloped / enveloping / detached / externally detached) | `signers/xades/AOXAdESSigner.java` | `core/signers/AOSignConstants.java:21-25` | `src/lib/xades.ts` (#7) — **enveloped only**; XAdES-T still needed (#43) | `PARTIAL` (XAdES-T outstanding) | P1 |
| XMLDSig (no XAdES properties) | `signers/xmldsig/AOXMLDSigSigner.java` | `core/signers/AOSignConstants.java:31` | — | `MISSING` | P2 |
| PAdES | `signers/pades/AOPDFSigner.java` | `core/signers/AOSignConstants.java:38` | `src/lib/pades.ts` (#11), wired into the picker (B-B + B-T) plus visible signatures (#45) | `DONE` | — |
| ODF (`.odt/.ods/.odp`) | `signers/odf/AOODFSigner.java` | `core/signers/AOSignConstants.java:34-35` | — | `MISSING` | P0 |
| OOXML (`.docx/.xlsx/.pptx`) | `signers/ooxml/AOOOXMLSigner.java` | `core/signers/AOSignConstants.java:32-33` | `src/lib/ooxml.ts` (#10), not wired | `PARTIAL` | P0 |
| FacturaE | `signers/xades/AOFacturaESigner.java` | `core/signers/AOSignConstants.java:41-43` | — | `MISSING` | P0 |
| ASiC-S (CAdES / XAdES in a ZIP) | `signers/cades/asic/AOCAdESASiCSSigner.java`, `signers/xades/asic/AOXAdESASiCSSigner.java` | `core/signers/AOSignConstants.java:11,19` | — | `MISSING` | P1 |
| Tri-phase variants (`*Tri`) | `signers/*/client/**`, `AOPkcs1TriPhaseSigner` | `core/signers/AOSignConstants.java:14,18,26,39` | `src/lib/extension/triphase.ts` — CAdES only, in the extension; the page's signing server builds the structure and the extension supplies the PKCS#1 signature | `PARTIAL` (CAdES; XAdES/PAdES tri servers still use native AutoFirma) | P1 |

Registry and dispatch: `core/signers/AOSignerFactory.java:31` (format → class table), `:36`
(`getSigner(byte[])`), `:116` (`getSupportedFormats()`). Auto-detection of an already-signed artifact:
`standalone/DataAnalizerUtil.java:45-117`, `core/misc/OfficeAnalizer.java:275,372`.

## 3. Crypto operations

| Operation | 1.9 evidence | Our status | Prio |
|---|---|---|---|
| Sign | `standalone/ui/SignPanelSignTask.java:394` (`aOSigner.sign(...)`, the switch default); CLI `sign` | `DONE` | — |
| **Co-sign** (add a second signer to a signed document) | GUI `standalone/ui/SignPanel.java:396`; CLI `cosign` (`standalone/CommandLineCommand.java:7`); `AOCoSigner` | `MISSING` | **P0** |
| **Counter-sign**, leaf signatures / whole tree | GUI `standalone/ui/SignPanel.java:660` (`getCryptoOperation`, leaf-vs-tree from the `VALUE_MULTISIGN_COUNTERSIGN_LEAFS` preference), `standalone/ui/SignPanelSignTask.java:388,391` (`CounterSignTarget.LEAFS`/`TREE`); CLI `countersign`; `AOCounterSigner` | `MISSING` | **P0** |
| Multi-sign (same certificate, N documents) | CLI `-operation sign` (`standalone/CommandLineParameters.java:43`); GUI multi-file list `standalone/ui/SignPanelMultiFilePanel.java:44`, folder recursion `standalone/ui/SignPanel.java:293` | `PARTIAL` (queue, no folder drop) | P1 |
| Signature algorithm choice (SHA-1/256/384/512, RSA/ECDSA) | `standalone/CommandLineParameters.java:47-51`; pref `PREFERENCE_GENERAL_SIGNATURE_ALGORITHM` | `PARTIAL` (taken from the certificate) | P1 |
| Pre-hashed signing (`precalculatedHashAlgorithm`, `hash/SHA-512` mime) | `massive/DirectorySignatureHelper.java:198,217,219` — **unreferenced from GUI/CLI in 1.9** `[INFERENCE: rg "es\.gob\.afirma\.massive" → only its own package]`. The CLI's hash knobs `-hformat`/`-halgorithm` (`standalone/CommandLineParameters.java:24,25`) are dead parameters: their getter `getHashFormat` (`:344`) has no caller and `-halgorithm` has no getter at all `[INFERENCE: rg "getHashFormat" → single hit, and no "getHashAlgorithm" exists]` — so "generate hash files" is not a 1.9 user feature | `MISSING` | P2 |

## 4. Format-specific features

### 4.1 PDF / PAdES

| Feature | 1.9 evidence | Our status | Prio |
|---|---|---|---|
| Visible signature: page, X/Y, width/height, text layers, font family/size/colour, upload of an image (rubric), rotation | `standalone/ui/pdf/SignPdfUiPanelStamp.java:231,251-281,301,323,401-403` (page/X-Y/W-H, image); `standalone/ui/pdf/SignPdfUiPanelPreview.java:565-574` (text layer, font family/size/style/colour, rotation); prefs `PREFERENCE_PADES_VISIBLE`, `PREFERENCE_PDF_SIGN_LAYER2*`, `PREFERENCE_PDF_SIGN_SIGNATUREROTATION`, `PREFERENCE_PDF_SIGN_IMAGE`; entry dialog `standalone/ui/pdf/SignPdfDialog.java:26` | `DONE` (core) — `src/lib/pades-visible.ts` + sign panel toggle off-by-default (`App.svelte:134-137`) — image (PNG/JPEG, `/SMask`), CN + date lines, per page/corner or coords; P2 (font/rotation depth) |
| Sign an existing **empty signature field** chosen from the form | `standalone/ui/pdf/PdfEmptySignatureFieldsChooserDialog.java:72-76` | `MISSING` | P1 |
| PDF **certification** level (author/…); preference for the default level | `PREFERENCE_PADES_DEFAULT_CERTIFICATION_LEVEL`; `standalone/ui/ExtraParamsHelper.java:176` (`ALLOW_SIGNING_CERTIFIED_PDFS`) | `MISSING` | P1 |
| Co-sign PDFs carrying **unregistered** signatures | `standalone/ui/ExtraParamsHelper.java:180` (`ALLOW_COSIGNING_UNREGISTERED_SIGNATURES`) | `MISSING` | P1 |
| Structured refusals: encrypted PDF, modified form, suspected PSA (shadow attack), unregistered signatures, "is certified" | `signers/pades/common/`: `PdfIsCertifiedException`, `PdfFormModifiedException`, `SuspectedPSAException`, `PdfHasUnregisteredSignaturesException` | `PARTIAL` (encrypted rejected; the rest unreported) | P1 |
| Obfuscate certificate data in the visible stamp | `standalone/ui/ExtraParamsHelper.java:150` (`OBFUSCATE_CERT_DATA`, pref `PREFERENCE_PADES_OBFUSCATE_CERT_INFO`) | `MISSING` | P2 |

### 4.2 XAdES options

| Feature | 1.9 evidence | Our status | Prio |
|---|---|---|---|
| Signature policy (`policyIdentifier`, hash, hash algorithm, description, qualifier) | `standalone/ui/ExtraParamsHelper.java:70-113` (`loadXAdESExtraParams`); prefs `PREFERENCE_XADES_POLICY_*` | `PARTIAL` — `src/lib/xades.ts` emits `SignaturePolicyImplied` when the caller supplies nothing | P1 |
| Claimed roles, production city/province/postal code/country | `standalone/ui/ExtraParamsHelper.java:91,95`; prefs `PREFERENCE_XADES_SIGNATURE_PRODUCTION_*` | `MISSING` | P1 |
| Node to sign (`nodeToSign`), reference digest method, canonicalization algorithm, `canonicalization`/URI dereferencer | `signers/xades/XAdESExtraParams.java:9-49` (arch. §3.2) | `MISSING` | P1 |
| Signed/unsigned CAdES attributes, commitment-type indication | `signers/cms/AOCMSSigner.java:170,174`; `signers/cades/CAdESAttributes.java` | `MISSING` | P1 |
| CAdES `explicit`/`implicit` mode | `standalone/ui/ExtraParamsHelper.java:172` (`CAdESExtraParams.MODE`), pref `PREFERENCE_CADES_IMPLICIT` | `MISSING` | P1 |
| FacturaE profile (policy preloaded, invoice role, production place) | `standalone/ui/ExtraParamsHelper.java:33-68` (`loadFacturaEExtraParams`) | `MISSING` | P0 (§2) |

### 4.3 Timestamps (RFC 3161)

| Feature | 1.9 evidence | Our status | Prio |
|---|---|---|---|
| **Generate** a signature timestamp | `signers/cades/CAdESExtraParams.java:29` (`tsaURL`), `signers/pades/common/PdfExtraParams.java:62`, `signers/pades/PdfTimestamper.java:67,195`, `signers/tsp/pkcs7/CMSTimestamper.java:60,92,104`; applied from the signer, e.g. `signers/cades/AOCAdESSigner.java:234` | `MISSING` | **P0** |
| GUI switch for the TSA | none found — the parameter only arrives via the `properties`/`-config` bag. `[INFERENCE: rg "tsaURL" outside signers/ → no standalone hit]` | `N/A` (no switch to match) | — |
| PDF document timestamp / DSS (`TS_TYPE` 1 vs 2) | `signers/pades/PAdESTriPhaseSigner.java:53` | `MISSING` | P1 (P0 with §4.3 row 1) |
| **Display** existing timestamps in the viewer | `standalone/crypto/TimestampsAnalyzer.java:34,40`; `standalone/ui/SignDataPanel.java:360,393` | `MISSING` | P1 |

### 4.4 Signature parameters shown to the user

| Feature | 1.9 evidence | Our status | Prio |
|---|---|---|---|
| Read-only summary of the parameters applied to a signature (policy, production place, reason, contact, format, algorithm) | `standalone/ui/SignatureAttributesDialog.java:55-98`; `standalone/ui/SignatureConfigInfoPanel.java` | `MISSING` | P1 |
| Sign reason / signer contact (PDF) | `standalone/ui/ExtraParamsHelper.java:136,144`; prefs `PREFERENCE_PADES_SIGN_REASON`, `PREFERENCE_PADES_SIGNER_CONTACT` | `PARTIAL` (`src/lib/pades.ts` accepts `reason`/`location`; no UI) | P1 |
| Change the output format of a queued file | `standalone/ui/ChangeFormatPanel.java:13,32` | `PARTIAL` (`BatchQueue` carries a per-file format field) | P1 |

## 5. Verification and the signature viewer

| Feature | 1.9 evidence | Our status | Prio |
|---|---|---|---|
| Standalone viewer window (`Ver firma`) | `standalone/VisorFirma.java:24`; `standalone/ui/VisorPanel.java:58` | `PARTIAL` (`src/lib/components/VerifyReport.svelte` reports checks) | P1 |
| Signature **tree**: per signer, per timestamp, certificate info | `standalone/ui/VisorPanel.java:90` (`openSign`), `standalone/crypto/CompleteSignInfo.java` | `PARTIAL` (flat list, no signer/timestamp tree) | P1 |
| Format-specific signature details (policy, commitment, data objects, timestamp chains) | `standalone/signdetails/{CAdES,XAdES,PAdES,FacturaE}SignAnalyzer.java`, `standalone/signdetails/SignDetailsFormatter.java:14` | `MISSING` | P1 |
| Validity verdict + machine-readable error codes | `signvalidation/SignValiderFactory.java:20`; `signvalidation/SignValidity.java:19-44` (`CRL_PROBLEM`, `CERTIFICATE_EXPIRED`, `CA_NOT_SUPPORTED`, `MODIFIED_FORM`, `OVERLAPPING_SIGNATURE`, `SUSPECTED_SIGNATURE`, `CERTIFIED_SIGN_REVISION`, …) | `PARTIAL` (structure, integrity, signature, refs, cert validity, chain to the bundled root, signing time — `src/lib/verify/`) | P1 |
| CAdES / XAdES validation | `signvalidation/ValidateBinarySignature.java`, `signvalidation/ValidateXMLSignature.java` | `DONE` | — |
| **PDF / PAdES validation** (byte range, signature, chain) | `signvalidation/ValidatePdfSignature.java`, `signvalidation/PDFSignatureDictionary.java` | `DONE` for structure/digest/chain (`src/lib/verify/pades.ts`); form-modification and shadow-attack depth still `MISSING` (#11) | P0 (depth only) |
| Revocation / trust depth (CRL/OCSP, unsupported CA) | `SignValidity.VALIDITY_ERROR.CRL_PROBLEM` (`signvalidation/SignValidity.java:30`), `CA_NOT_SUPPORTED` `:29` | `MISSING` (no OCSP/CRL, no AIA walk) | **P0** |
| ODF / OOXML validation | 1.9 reports them as `ODF_UNKOWN_VALIDITY` / `OOXML_UNKOWN_VALIDITY` (`signvalidation/SignValidity.java:31-33`) — i.e. **also unsupported in 1.9** | `MISSING` (parity: 1.9 does not validate them either) | P2 |
| Signature policy / profile check | `signvalidation/SignValidity.java:40` (`SIGN_PROFILE_NOT_CHECKED`) | `MISSING` | P1 |
| Detached CAdES needs the original document | `signvalidation/SignValidity.java:22` (`NO_MATCH_DATA`), `:41` (`CANT_VALIDATE_EXTERNALLY_DETACHED`) | `DONE` (the report asks for the document) | — |
| Batch verification | **not a 1.9 feature** — `verify` takes one `-i` file (`standalone/CommandLineLauncher.java:573-606`), the GUI viewer opens one file | `N/A` (no parity to chase) | — |
| Ask the user to confirm a signature whose status needs a human (unknown certificate status) | `SignValidity.SIGN_DETAIL_TYPE.PENDING_CONFIRM_BY_USER` (`signvalidation/SignValidity.java:14`) | `MISSING` | P2 |

## 6. Batch / multiple documents

| Feature | 1.9 evidence | Our status | Prio |
|---|---|---|---|
| GUI multi-file signing, folder recursion, per-file format/operation, overwrite policy | `standalone/ui/SignPanel.java:293,784`, `standalone/ui/SignPanelMultiFilePanel.java:44`; pref `PREFERENCE_GENERAL_MASSIVE_OVERWRITE` | `PARTIAL` — `src/lib/batch.ts` queue + ZIP + `manifest.json` (#9); **no folder drop** (`src/lib/components/FileDrop.svelte` has no directory handling) | P1 |
| Remote batch signing against an @firma server (JSON batch, pre/post URLs) | CLI `batchsign` `standalone/CommandLineLauncher.java:408`; `signers/batch/client/BatchSigner.java:35`; protocol `op=batch` → `standalone/protocol/JSONBatchManager.java:17`, `standalone/protocol/LocalBatchSigner.java:29` | `N/A-BROWSER` for the server side; our ZIP+manifest is the local equivalent (`src/lib/batch.ts`) | — |
| Directory/massive helpers (incl. pre-hashed inputs, `MassiveType` sign/co-sign/counter-sign) | `massive/MassiveType.java:5-8`, `massive/MassiveSignatureHelper.java`, `massive/DirectorySignatureHelper.java` — **unreferenced from the 1.9 GUI/CLI** `[INFERENCE: rg "afirma.massive" → only its own package]` | `MISSING` | P2 |

## 7. Keys and certificates

| Feature | 1.9 evidence | Our status | Prio |
|---|---|---|---|
| PKCS#12 / `.p12` / `.pfx` | `keystores/AOKeyStore.java:15` | `DONE` (#5, `src/lib/pkcs12.ts` + `src/lib/keystore.ts`) | — |
| Legacy PBES1 containers (3DES/RC2) and PKCS#12 MAC verification | `keystores/AOKeyStore.java:15` — JCA `PKCS12`, with the shaded BouncyCastle on the classpath, so legacy PBES1 containers open `[INFERENCE]` | `DONE` (`src/lib/pkcs12.ts:89-95` PBES1 table, `:208` `verifyMac`, primitives in `src/lib/legacy-pbe.ts`, covered by `src/lib/pkcs12.test.ts`) | — |
| JKS / JCEKS / bare certificate file (`SINGLE`) | `keystores/AOKeyStore.java:16,18,20` | `MISSING` | P2 |
| PKCS#11 modules, Mozilla NSS, Windows `Windows-MY`, macOS Keychain | `keystores/AOKeyStore.java:12,13,14,17,19` | `N/A-BROWSER` (no APDU/native store access) | — |
| Smart cards (DNIe, FNMT-CERES, G&D, MoD TEMD) + wait panel | `keystores/AOKeyStore.java:22,25,26,27,28`; `es.gob.jmulticard`; `standalone/ui/DNIeWaitPanel.java:25` | `OUT-OF-SCOPE` (architecture.md §6.2) | — |
| Certificate selection dialog, alias listing, `-alias`/`-filter` | CLI `listaliases` `standalone/CommandLineLauncher.java:835`; `standalone/ui/CertPicker`-equivalent in `ui/core/jse/certificateselection` | `DONE` (`src/lib/components/CertPicker.svelte`) | — |
| Certificate **filters** (sign-only certificates, alias-only, show expired) | `keystores/filters/**`; prefs `PREFERENCE_KEYSTORE_{SIGN_ONLY_CERTS,ALIAS_ONLY_CERTS,SHOWEXPIREDCERTS}` | `MISSING` | P1 |
| Certificate details (holder, issuer, validity, usage, DNIE analyzer) | `standalone/crypto/CertificateInfo.java`, `standalone/crypto/CertAnalyzer.java:11`, `standalone/crypto/DnieCertAnalyzer.java` | `PARTIAL` (`src/lib/x509.ts`: subject, issuer, validity, key) | P2 |
| Import certificates into the trust store / manage trusted CAs | `standalone/ui/preferences/ImportCertificatesDialog.java:38`, `standalone/ui/preferences/TrustedCertificatesPanel.java:29` | `PARTIAL` (one bundled FNMT root, `src/lib/verify/roots/`, refreshable via `scripts/fetch-roots.sh`) | P1 |
| Install CA certificates into the OS/browser stores | `standalone/ui/restoreconfig/CertUtil.java:50`, `standalone/ui/restoreconfig/RestoreConfigFirefox.java` | `N/A-BROWSER` | — |
| Confirmation before signing with the selected certificate | `standalone/ui/ConfirmSignatureDialog.java`; pref `PREFERENCE_GENERAL_CONFIRMTOSIGN` | `PARTIAL` (certificate picker shows the certificate) | P2 |

## 8. Browser protocol (`afirma://`) and web integration

| Operation | 1.9 evidence | Meaning | Our status |
|---|---|---|---|
| `afirma://sign`, `cosign`, `countersign` | `standalone/protocol/ProtocolInvocationLauncher.java:372`; `core/misc/protocol/UrlParametersToSignAndSave.java:25,31` (`cop`, `format`, `algorithm`, …) | a web form hands a document (or a URL/fileId) to AutoFirma, gets the signature back | `N/A-BROWSER` — the SPA **is** the signer; drop the document, sign locally |
| `…/signandsave` + storage servlet | `standalone/protocol/ProtocolInvocationLauncher.java:306`; `UrlParametersToSave`, `UrlParameters.STORAGE_SERVLET_PARAM` | AutoFirma POSTs the result back to the calling server | `MISSING` by design (static, no backend) — a caller-side download is the equivalent (per-row `Descargar`, batch ZIP) |
| `…/load`, `…/save` | `standalone/protocol/ProtocolInvocationLauncher.java:437,251` | pass a file in/out of the local disk | `N/A-BROWSER` (File System Access API / downloads instead) |
| `…/selectcert` | `standalone/protocol/ProtocolInvocationLauncher.java:203` | a page asks the user to choose a certificate | `N/A-BROWSER` (`src/lib/components/CertPicker.svelte`) |
| `…/batch` | `standalone/protocol/ProtocolInvocationLauncher.java:151` | JSON batch queue handed to AutoFirma | `N/A-BROWSER` (our local queue, #9) |
| `afirma://websocket`, `…/service` | `standalone/protocol/ProtocolInvocationLauncher.java:109,132`; `standalone/protocol/AfirmaWebSocketServerV4.java` | local socket/websocket bridge between page and app | `N/A-BROWSER` — **replaced by** the embed API gap below |
| Encrypted/compressed payloads (`key` (DES), `gzip`, base64 `dat`), min client version, active waiting | `core/misc/protocol/UrlParameters.java:18-32,152-196` | transport concerns of the local socket | `N/A-BROWSER` |
| Secure-domain allow-list, blocked lists, TLS-only server URLs | `standalone/ui/preferences/SecureDomainsPanel.java:15`; pref `PREFERENCE_GENERAL_SECURE_DOMAINS_LIST`; `UrlParameters.validateURL` `core/misc/protocol/UrlParameters.java:197` | only listed origins may invoke the local app | `N/A-BROWSER` (same-origin policy is the platform's job) |
| **Embeddable API for a third-party web page** (`postMessage`/iframe/JS module) | — (AutoFirma's answer was the `afirma://` protocol handler) | what a Spanish e-gov form actually needs to replace AutoFirma | `MISSING` (**P0**, §10) |

## 9. Application-level features

| Feature | 1.9 evidence | Our status | Prio |
|---|---|---|---|
| Preferences UI (general, CAdES, XAdES, PAdES, FacturaE, keystores, proxy, smart cards, trusted certs, secure domains) | `standalone/ui/preferences/PreferencesPanel*.java`, `standalone/ui/preferences/ProxyPanel.java:22`, `standalone/ui/preferences/SmartCardPanel.java:17`; keys `standalone/configurator/common/PreferencesManager.java` | `PARTIAL` (theme, language; no preferences surface for formats/policies/keystores) | P1 |
| Default format per file type (`-format auto`) | prefs `PREFERENCE_GENERAL_DEFAULT_FORMAT_{PDF,XML,BIN,OOXML,ODF,FACTURAE}` `standalone/configurator/common/PreferencesManager.java:37-42` | `PARTIAL` (fixed three-format picker) | P1 |
| Language/locale selection | `PREFERENCES_LOCALE` `standalone/configurator/common/PreferencesManager.java:29`; `standalone/SimpleAfirma.java:504` (`buildLocale`) | `DONE` (ES/EN, `src/lib/i18n.ts`) | — |
| Help window, About dialog, version | `SimpleAfirma.showHelp`, `standalone/ui/AboutDialog.java` | `MISSING` | P2 |
| Update check / updater | `standalone/updater/Updater.java:20`; pref `PREFERENCE_GENERAL_UPDATECHECK` | `N/A-BROWSER` (static hosting) | — |
| Proxy configuration | `standalone/ProxyUtil.java`, `standalone/ProxyConfig.java`, `standalone/ui/preferences/ProxyPanel.java:22` | `N/A-BROWSER` (the browser proxies) | — |
| Diagnostic data window, debug logging (`afirma_debug`, `-Dafirma_debug_level`) | `standalone/ui/DataDebugDialog.java`; `SimpleAfirma.configureLog` | `PARTIAL` (technical reasons in the verify report) | P2 |
| VDI/low-resource optimisation, HiDPI | pref `PREFERENCE_GENERAL_VDI_OPTIMIZATION`; `standalone/ui/ScalablePane.java`, `LookAndFeelManager` | `N/A-BROWSER` | — |
| macOS/Windows/Linux OS integration (dock icon, `OSXHandler`, ask-on-close) | `standalone/ui/OSXHandler.java`; pref `PREFERENCE_GENERAL_OMIT_ASKONCLOSE` | `OUT-OF-SCOPE` | — |
| Sign-file data types shown in the UI (icons per type, `FileType`) | `standalone/ui/FileIconProvider.java`, `standalone/ui/FileType.java` | `DONE` (`src/lib/components/FileCard.svelte`) | — |
| User storage of identities and produced signatures | — (desktop filesystem) | `DONE` (IndexedDB with a store PIN, #29, `src/lib/certstore.ts`) | — |

## 10. Gap list, prioritized

**P0 — required for a full replacement**

1. **PAdES verification depth** — §5: form-modification and shadow-attack (PSA) checks; byte range,
   digest and chain already ship (`src/lib/verify/pades.ts`).
2. **Co-signature and counter-signature** (leafs/tree) for CAdES/XAdES/PDF — §3.
3. **Revocation and trust depth** in the verify engine: OCSP/CRL, `CA_NOT_SUPPORTED`
   reporting, trust beyond the bundled FNMT root — §5. CORS is the known constraint.
4. **OOXML in the UI** (engine exists, unused) and **ODF signing** (missing entirely) — §2.
5. **FacturaE profile** (XAdES with policy, role, production place) — §2.
6. **XAdES-T** (generate `SignatureTimeStamp`) and display of received timestamps — §4.3
   (CAdES-T and PAdES B-T generation already ship, #43).
7. **DNIe / smartcard path** — a no-key-export PKCS#11 helper decision (`README.md`
   "Possible improvements"); desktop route discarded (`docs/PIVOT-STUDY.md` §5).
8. **Embeddable API for a calling web page** (`postMessage`/iframe), i.e. what replaces `afirma://sign`
   for forms — §8.

**P1 — parity, with a workaround**

9. Signature profiles and parameters: policy identifiers/hashes/qualifiers for CAdES/XAdES/PAdES,
   claimed roles, production place, reason/contact, CAdES explicit/implicit mode, XAdES modes and
   `nodeToSign` — §4.2, §4.4.
10. Batch parity: folder drop/recursion, per-file format & operation, overwrite policy — §6.
11. Viewer depth: signer/timestamp tree, format-specific signature details, parameter summary,
    profile check — §5, §4.4.
12. Certificate handling: filters (sign-only/alias-only/expired), certificate import into the trust
    store, trust-store management beyond the bundled FNMT root, richer certificate details — §7.
13. ASiC-S containers — §2.
14. Preferences surface for formats/policies/keystores and default format per file type — §9.

**P2 — legacy, JVM- or server-bound, or rarely needed**

15. Generic CMS/PKCS#7, plain XMLDSig, raw PKCS#1 signing — §2.
16. Pre-hashed / directory-massive signing (`massive/` is dead code in 1.9) — §3, §6.
17. JKS/JCEKS/SINGLE stores — §7.
18. ODF/OOXML validation (1.9 does not validate them either), confirm-before-sign, obfuscated stamp,
    help/about, diagnostics — §4.1, §5, §9.

**Not gaps** (the platform replaces them, per AGENTS.md): smart cards and OS/PKCS#11/NSS stores
(§7), the `afirma://` local-socket protocol and its encryption (§8), the configurator/restore-config,
plugins, updater, proxy and VDI preferences, OS integration and per-browser detection (incl. Safari)
(§1, §9), remote batch against an `@firma` server and `signandsave` to a caller server (§6, §8).

## 11. Re-checking this document

Every citation above can be re-verified mechanically:

```bash
git archive reference/decompile-1.9 decompiled | tar -x -C work/    # once
sed -n '5,10p' work/decompiled/sources/es/gob/afirma/standalone/CommandLineCommand.java
rg -n "tsaURL" work/decompiled/sources/es/gob/afirma/signers/       # TSA is extra-param only
rg -l "es\.gob\.afirma\.massive" work/decompiled/sources/           # massive/ has no caller
rg -n "SignValiderFactory.getSignValider" work/decompiled/sources/es/gob/afirma/standalone/
rg -n "BROWSER|getBrowser" work/decompiled/sources/es/gob/afirma/   # only Platform.java (§1)
```

Our side: `README.md` (per-issue status — its "PKCS#12 support" section predates the
PBES1/MAC work in `4c9f6c31` and is stale), `src/lib/sign.ts` (which formats the UI exposes),
`src/lib/verify/index.ts` (which artifacts the verifier accepts).
