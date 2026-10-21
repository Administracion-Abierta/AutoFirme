# architecture.md — AutoFirma signing core (reference for the web rewrite)

Reference map of the signing core of **AutoFirma 1.9** (upstream `afirma-core`), written for the
AutoFirma Web rewrite. It was built from a jadx 1.5.6 decompilation of the 1.9 `autofirma.jar`;
that tree left the repo in #26 — recover it with
`git checkout reference/decompile-1.9 -- decompiled`. Every row is anchored to a `file:line` of
that tree or marked `[INFERENCE]`. Decompilation is lossy (names, generics, lambdas) — treat this
as a map, not source. Resumen en español de lo que la app ya tiene: [LEEME.md](LEEME.md).

Upstream source, for cross-reading:
https://github.com/ctt-gob-es/clienteafirma (`afirma-core/src/main/java/es/gob/afirma/core/...`).
Provenance, packaging, and the full shaded-dependency inventory are in
[REVERSING-NOTES.md](REVERSING-NOTES.md); this file does not repeat them.

`Refs #3`. Supersedes nothing; the `decompiled/` tree of tag `reference/decompile-1.9` and the
upstream project are the ground truth.

## 1. The signer API surface

`AOSigner` is a marker: it only aggregates three interfaces, so every signer must implement the
whole contract (`core/signers/AOSigner.java:10`).

| Interface | Method | Line |
|---|---|---|
| `AOSimpleSigner` | `byte[] sign(byte[] data, String algo, PrivateKey key, Certificate[] chain, Properties extraParams)` | `AOSimpleSigner.java:11` |
| `AOCoSigner` | `cosign(data, sign, algo, key, chain, extraParams)` · `cosign(sign, algo, key, chain, extraParams)` | `AOCoSigner.java:11,13` |
| `AOCounterSigner` | `countersign(sign, algo, CounterSignTarget target, Object[] nodes, key, chain, extraParams)` | `AOCounterSigner.java:11` |

Everything a signer needs is passed in: `data`, algorithm name, `java.security.PrivateKey`, cert
chain, and a `Properties` bag of extra params. No signer ever touches a keystore — the keystore
layer (§4) produces the `PrivateKey`, the signer consumes it. **That split is the migration seam.**

Dispatch is by format string through a class registry (`core/signers/AOSignerFactory.java:36`
`getSigner(byte[])`, `:92` `getSigner(String)`, `:114` `getSupportedFormats()`):

| Format string (`AOSignConstants`) | Implementing class | Line |
|---|---|---|
| `CAdES` | `signers.cades.AOCAdESSigner` | `AOSignerFactory.java:13` |
| `CAdEStri` | `signers.cadestri.client.AOCAdESTriPhaseSigner` | `:14` |
| `CAdES-ASiC-S` / `-tri` | `signers.cades.asic.AOCAdESASiCSSigner` / `cadestri.client.asic.*` | `:15,16` |
| `CMS/PKCS#7` | `signers.cms.AOCMSSigner` | `:17` |
| `XAdES` / `XAdEStri` | `signers.xades.AOXAdESSigner` / `xadestri.client.AOXAdESTriPhaseSigner` | `:20,21` |
| `XAdES-ASiC-S` / `-tri` | `signers.xades.asic.AOXAdESASiCSSigner` / `xadestri.client.asic.*` | `:22,23` |
| `FacturaE` / `FacturaEtri` | `signers.xades.AOFacturaESigner` / `xadestri.client.AOFacturaETriPhaseSigner` | `:18,19` |
| `XMLDSig` | `signers.xmldsig.AOXMLDSigSigner` | `:24` |
| `PAdES` / `PAdEStri` | `signers.pades.AOPDFSigner` / `padestri.client.AOPDFTriPhaseSigner` | `:25,26` |
| `ODF` | `signers.odf.AOODFSigner` | `:27` |
| `OOXML` | `signers.ooxml.AOOOXMLSigner` | `:28` |
| `NONE` (raw PKCS#1) | `core.signers.AOPkcs1Signer` | `:29` |

Format names, digest names (`SHA1/256/384/512`), JCA algorithm names
(`SHA512withRSA` = default, `NONEwithRSA`, `SHA256withECDSA`, …) and signing modes
(`explicit`/`implicit`) are all constants in `core/signers/AOSignConstants.java` (`:9-43`,
`:66-82`, `:83-84`); `getDigestAlgorithmName`/`composeSignatureAlgorithmName` (`:90`, `:120`)
normalise user input (OIDs, `SHA-256`, `http://…xmldsig#sha1`) to JCA names.

**Rewrite note:** this registry is the natural shape for the SPA's `signers/<format>/` modules —
one entry point per format, same signature.

## 2. Tri-phase signing — the hinge of the whole rewrite

The plain path signs in-process: `AOPkcs1Signer.sign()` builds a JCA `Signature` and calls
`initSign(privateKey)` (`core/signers/AOPkcs1Signer.java:37-40`). For cards, remote signers and
HSMs the code splits signing into three phases so the private key never has to leave its holder:

| Phase | Producer | What crosses the boundary |
|---|---|---|
| `preSign` | format signer | DER bytes to be signed (CAdES: encoded SignedAttrs) |
| `sign` | caller / card / server | raw PKCS#1 signature over those bytes |
| `postSign` | format signer | final container (CMS / PDF / XML) |

Evidence: `GenCAdESEPESSignedData.generateSignedData` does exactly
`preSign(...) → new AOPkcs1Signer().sign(preSignBytes, …) → postSign(...)`
(`signers/cades/GenCAdESEPESSignedData.java:14-21`); the two halves are
`CAdESTriPhaseSigner.preSign` (`:39`) and `postSign` (`:54`). PAdES mirrors it
(`signers/pades/PAdESTriPhaseSigner.java:29,46`) reusing the CAdES parameters
(`:10-11`) — **PAdES is CAdES + a PDF `/ByteRange` carrier.**

State between phases travels as a serialised `TriphaseData` (format + list of
`TriSign{id, signatureId, dict}` entries, `core/signers/TriphaseData.java:22,27,141`), driven by
`TriphaseDataSigner.doSign` (`:19`). `AOPkcs1TriPhaseSigner` (`core/signers/AOPkcs1TriPhaseSigner.java:22`)
is the generic driver; its constants (`:24-37`) reveal the legacy deployment it was built for —
a **remote signing server**: `serverUrl`, `op=pre|post`, `cop=sign`, `doc`, `algo`, `format`,
`cert`, `session`. `TriphaseUtil.prepareCertChainParam` (`TriphaseUtil.java:16`) serialises the
chain for that hop.

`AOPkcs1TriPhaseSigner` (class `Signature`) → `client` packages
(`signers/*/client/`) are HTTP clients of "Cliente @firma": `cadestri/client/PreSigner.java`,
`PostSigner.java`, `xadestri/client/*`, `padestri/client/{AOPDFTriPhaseSigner,PDFTriPhaseSignerUtil}.java`.

Two format-level wrinkles the rewrite must keep:

- **ECDSA encoding.** Cards return raw `r‖s`; XML/CMS need DER `SEQUENCE{INTEGER,INTEGER}`.
  `Pkcs1Utils.encodeSignature/decodeSignature` convert both ways
  (`core/signers/Pkcs1Utils.java:15,32`). WebCrypto's `subtle.sign` returns **raw `r‖s`** too —
  same conversion is needed client-side.
- **Algorithm policy.** After signing, `AOPkcs1Signer` re-verifies the returned PKCS#1 against
  the signer certificate (`:44-52`), and maps provider failures onto app exceptions:
  `es.gob.jmulticard.{CancelledOperationException, jse.provider.SignatureAuthException}` →
  `AOCancelledSMOperationException` / `LockedKeyStoreException` / `AuthenticationException` /
  `PinException` (`:60-71`) — i.e. the on-card-signing provider (§4) plugs in *under* the same
  interface.

**Rewrite note:** tri-phase is not legacy baggage to delete — it is the correct browser design.
Phase 2 is `crypto.subtle.sign`; phases 1 and 3 are pure byte manipulation and belong in
JS/WASM. The remote-server leg is the only part that becomes dead.

## 3. Per-format flows

### 3.1 CAdES / CMS (ASN.1)

| Item | Value |
|---|---|
| Entry | `signers/cades/AOCAdESSigner.java:31,51,75,101` (sign/cosign×2/countersign) |
| Call chain | `sign()` → build `CAdESParameters` → `GenCAdESEPESSignedData.generateSignedData` (`:14`) → `CAdESTriPhaseSigner.preSign` (`:39`) → `AOPkcs1Signer.sign` → `CAdESTriPhaseSigner.postSign` (`:54`) |
| Output | ASN.1 DER `ContentInfo`/`SignedData` (detached-by-default CMS bytes), no base64 at this layer |
| ASN.1 toolkit | **SpongyCastle** (`org.spongycastle.asn1.*`) — hand-assembled `SignedData`/`SignerInfo` (`signers/cades/CAdESTriPhaseSigner.java:15-32`); generic PKCS#7 building in `signers/cms/GenSignedData.java:19-38` |
| Extras | signed/unsigned attributes `AOCMSSigner.addSignedAttribute/addUnsignedAttribute` (`signers/cms/AOCMSSigner.java:170,174`); CAdES-EPES profile/commitment-type in `CAdESAttributes.java`, `CommitmentTypeIndicationsHelper.java` |
| Timestamp | `AOCAdESSigner.applyTimeStamp` (`:234`) → `signers/tsp/pkcs7/*` (TSA over HTTP/ASN.1) |
| Verify path | `signers/pkcs7/ObtainContentSignedData.java`, `ReadNodesTree.java`, `SigUtils.java`, `cades/CAdESValidator.java` |
| Container | ASiC-S variant wraps the CMS in a ZIP: `signers/cades/asic/AOCAdESASiCSSigner.java` |

`AOCMSSigner` is the same machinery without CAdES attributes; it is the generic PKCS#7 signer.

### 3.2 XAdES / XML-DSig

| Item | Value |
|---|---|
| Entry | `signers/xades/AOXAdESSigner.java:47,222,244` (sign/cosign/countersign) |
| Inner worker | `signers/xades/XAdESSigner.java:83` — `sign(data, algo, key, chain, props, URIDereferencer)` |
| XAdES model | bundled **jXAdES fork** `es.uji.crypto.xades.jxades.security.xml.XAdES.*` (`XAdESSigner.java:24-28`) |
| Core XML-DSig | JSR-105 `javax.xml.crypto.dsig` (`XMLSignatureFactory`, `DOMSignContext`) in `AOXMLAdvancedSignature`, which `extends XMLAdvancedSignature` (jXAdES) (`xades/AOXMLAdvancedSignature.java:46`, imports `:33-41`) |
| Legacy XML-DSig | Apache Santuario referenced for constants (`org.apache.xml.security.utils.Constants`, `AOXAdESSigner.java:26`) |
| Modes | enveloped / enveloping / detached / externally-detached, detected statically (`AOXAdESSigner.java:52-92`) |
| Profile knob | `XAdESExtraParams` (`signers/xades/XAdESExtraParams.java:9-49`): `profile`, `format`, `policyIdentifier(+Hash/Algorithm/Description/Qualifier)`, `signerClaimedRoles`, production city/province, `nodeToSign`, `referencesDigestMethod`, `canonicalizationAlgorithm` |
| Timestamp | `XAdESTspUtil.java`; counter-sign `XAdESCounterSigner.java`, co-sign `XAdESCoSigner.java` |
| FacturaE | `AOFacturaESigner.java` — XAdES with the FacturaE policy preloaded |
| Container | `signers/xades/asic/AOXAdESASiCSSigner.java` (+ `UriAndDataDereferencer`) |

### 3.3 PAdES

| Item | Value |
|---|---|
| Entry | `signers/pades/AOPDFSigner.java` |
| PDF toolkit | **repackaged iText** as `com.aowagie.text.pdf.*` (`PdfReader`, `PdfStamper`, `PdfPKCS7`, `AcroFields`, `PdfSignatureAppearance`) — `AOPDFSigner.java:3-8`, `pades/{PdfUtil,PdfSessionManager,PdfTriPhaseSession}.java` |
| Also on classpath | BouncyCastle JCE provider (`org.bouncycastle.jce.provider.BouncyCastleProvider`, `AOPDFSigner.java:39`) |
| Call chain | `sign()` → optional `PdfPreProcessor` (`:3-11`, visible-signature graphics/text) → `PdfTriPhaseSession` (`:3`, `PdfSignatureAppearance`) → CAdES bytes → iText inserts signature dict + `/ByteRange` |
| Tri-phase | `PAdESTriPhaseSigner.preSign/postSign` (`:29,46`) returning `PdfSignResult` (`signers/pades/PdfSignResult.java`); client leg `padestri/client/AOPDFTriPhaseSigner.java` |
| Timestamp | `PdfTimestamper.java` (DSS/DocTimeStamp) |
| Guards | typed refusals in `pades/common/`: `PdfIsCertifiedException`, `PdfFormModifiedException`, `SuspectedPSAException`, `PdfHasUnregisteredSignaturesException` |
| Validate | `signvalidation/ValidatePdfSignature.java`, `PDFSignatureDictionary.java` |

### 3.4 ODF (`AOODFSigner`, `signers/odf/AOODFSigner.java`, 893 lines)

ODF is a ZIP; the signer signs the package and stores XML-DSig/XAdES in
`META-INF/documentsignatures.xml`. It reuses the XAdES machinery (`XAdESExtraParams`,
`AOODFSigner.java:22`) and adds ODF-specific XML handling (`OdfXmlUtil.java`). Woodstox leaks in
via `com.ctc.wstx.cfg.XmlConsts` (`:4`) and iText's XML tags via
`com.aowagie.text.ElementTags` (`:3`) — both are incidental reuse, not ODF requirements.
No tri-phase variant is registered (§1 table).

### 3.5 OOXML (`AOOOXMLSigner` + `AOOXMLXAdESSigner`, `signers/ooxml/`)

OOXML is an OPC (ZIP) package; signatures go to `_xmlsignatures/*.xml` with relationship parts.
`AOOOXMLSigner.java:15` delegates to `AOXAdESSigner`; the OPC plumbing is
`OOXMLZipHelper`, `ContentTypeManager`, `RelationshipsParser`, and a JSR-105 relationship
provider (`signers/ooxml/relprovider/OOXMLProvider.java`, imported at `AOOOXMLSigner.java:14`)
plus `OOXMLURIDereferencer`.
No tri-phase variant is registered.

### 3.6 Validation

`signvalidation/` gives format detection + verification independent of signing:
`SignatureFormatDetector{PadesCades,Xades}.java`, `ValidateBinarySignature`, `ValidatePdfSignature`,
`ValidateXMLSignature`, orchestrated by `SignValiderFactory`/`SignValider`. The web rewrite needs
this whole package (or a JS equivalent) for the "verify" flow.

## 4. Keystore layer (`es/gob/afirma/keystores/`)

`AOKeyStore` enumerates every store the app can open (`keystores/AOKeyStore.java:11-31`). The
middle string is the JCA `KeyStore` type or an internal marker; the last two columns are the
cert-store and key-store `PasswordCallback`s.

| Enum | JCA type | Backing mechanism | OS |
|---|---|---|---|
| `PKCS12` | `PKCS12` | `.p12`/`.pfx` file, password | any |
| `JAVA` / `JAVACE` | `JKS` / `CaseExactJKS` | Java keystore file | any |
| `JCEKS` | `JCEKS` | Java keystore file | any |
| `SINGLE` | `PKCS7` | bare X.509 cert, no key | any |
| `WINDOWS` | `Windows-MY` | Windows cert store (SunMSCAPI) | Windows |
| `WINADDRESSBOOK` / `WINCA` | `Windows-ADDRESSBOOK` / `Windows-ROOT` | other-people / CA stores | Windows |
| `APPLE` | `KeychainStore` | macOS Keychain | macOS |
| `SHARED_NSS` / `MOZ_UNI` | `PKCS11` | Mozilla NSS profile (`keystores/mozilla/`) | any |
| `PKCS11` | `PKCS11` | any PKCS#11 module | any |
| `KNOWN_SMARTCARDS` | `SMARTCARDS` (internal marker) | known cards via `SmartCardUnifiedKeyStoreManager` | any |
| `TEMD` | `PKCS11` | MoD card | any |
| `CERES` / `CERES_430` | `CERES`/`CERES430` | FNMT-RCM CERES card via jmulticard | any |
| `DNIEJAVA` | `DNIeWaitPanel.PROP_DNIE_REQUESTED` | DNIe / FNMT-TIF via jmulticard | any |
| `SMARTCAFE` | `GDSCPKCS15` | G&D SmartCafe PKCS#15 applet | any |
| `OTHER` | — | catch-all | — |

Managers (one line each):

| Class | Role |
|---|---|
| `AOKeyStoreManager` (304 L) | abstract store: `load()`, aliases, `getCertificate`, `getKeyEntry`, filter hook |
| `AOKeyStoreManagerFactory` (408 L) | picks the right manager from `AOKeyStore` + `Platform` |
| `JavaKeyStoreManager` / `Pkcs12KeyStoreManager` / `FileKeyStoreManager` | file-backed stores (JKS / PKCS#12 / generic file) |
| `AppleKeyStoreManager`, `CAPIKeyStoreManager`, `CAPIUnifiedKeyStoreManager` | OS stores (Keychain, Windows CAPI) |
| `SmartCardUnifiedKeyStoreManager` | cards exposed through jmulticard's JCA provider |
| `AggregatedKeyStoreManager` | unions several stores into one alias space |
| `AOKeyStoreManagerHelper{FullJava,Java,Single,Pkcs11,CapiAddressBook,Apple}` | per-type crypto glue (`getKeyEntry` implementations) |
| `filters/` (`CertificateFilter`, `MultipleCertificateFilter`, `filters/rfc/SearchFilter`) | restrict which certs are offered |
| `callbacks/` (`NullPasswordCallback`, `CachePasswordCallback`, `entry`, …)/ `FirstEmptyThenPinUiPasswordCallback` | password/PIN acquisition, UI vs cached |

**Key access pattern:** the manager returns certs and a `java.security.PrivateKey`. For cards that
`PrivateKey` is a *reference*: jmulticard registers a JCA `Provider` whose `SignatureSpi`
implementation performs the APDU exchange — `DnieProvider extends Provider`
(`jmulticard/jse/provider/DnieProvider.java:13`) registers
`Signature.SHA1withRSA…SHA512withRSA → DnieSignatureImpl$Sha1…$Sha512` (`:58-61`) with
`DniePrivateKey` as the supported key class; `DnieSignatureImpl extends SignatureSpi`
(`jmulticard/jse/provider/DnieSignatureImpl.java:21`) hashes locally then signs on-card.

**Smart-card stack (no browser path — see §6):** `es.gob.jmulticard` (310 classes) is a from-scratch
card stack:

| Layer | Package | Notes |
|---|---|---|
| Transport | `jmulticard/jse/smartcardio/SmartcardIoConnection.java:18-27` | `javax.smartcardio` = **PC/SC** |
| PC/SC fixups | `jmulticard/jse/smartcardio/LibJ2PCSCGNULinuxFix.java` | JNA workaround for `libj2pcsc` on GNU/Linux |
| APDU | `jmulticard/connection/{ApduConnection,ApduEncrypter*,CipheredApdu}.java` | ISO 7816 (4/8-byte APDUs) |
| Secure channel | `jmulticard/connection/cwa14890/`, `card/cwa14890/` | CWA-14890 (DNIe/CERES), DES/AES MAC |
| Card models | `card/dnie/` (`Dnie3r2`, `Dnie3r1`, `Dnie`, `DnieNfc`), `card/fnmt/ceres/`, `card/gide/`, `card/icao/` | per-card applets |
| JCA bridge | `jmulticard/jse/provider/` (`DnieProvider`, `DnieKeyStoreImpl`, `DniePrivateKey`, `DnieSignatureImpl`, `ceres/`, `rsacipher/`) | card as a `KeyStore`/`Signature` provider |

Cert types named in code, therefore: **DNIe 3.x** (users/pin variants), **FNMT-RCM CERES (incl.
4.30+)**, **FNMT-TIF**, **MoD TEMD/G&D SmartCafe**, plus generic PKCS#11 cards. "Cert type" is not
a keystore concept — it is a *card applet* behind PC/SC; issuance/trust level comes from the cert
itself (`CertificateFilter`/`SearchFilter`).

## 5. Dependency triage

Full shaded inventory: REVERSING-NOTES.md §6. Classification for the rewrite:

| Dependency | Verdict | Why |
|---|---|---|
| **SpongyCastle** (`org.spongycastle.asn1.*`) | **signing-critical** | CAdES/CMS `SignedData` assembly (§3.1) |
| **BouncyCastle** (`org.bouncycastle.*`, incl. `jcajce`) | **signing-critical** | padding/encryption, PDF crypto path, PQC extras |
| **jXAdES fork** (`es.uji.crypto.xades`) | **signing-critical** | XAdES property model (§3.2) |
| **JSR-105 + Santuario** (`javax.xml.crypto`, `org.apache.xml.security`) | **signing-critical** | XML-DSig transforms/C14N |
| **repackaged iText** (`com.aowagie.text.pdf.*`) | **signing-critical** | PAdES byte-range/PDF writing (§3.3) — note it is *also* an image/PDF toolkit generally, but only PDF signing is used here |
| **Woodstox / stax2** (`com.ctc.wstx`) | **mixed** | StAX writer/parser used for XAdES/ODF XML I/O; pervasive but replaceable by the browser's own `DOMParser`/`XMLSerializer` |
| `org.apache.santuario:xmlsec`, MSV/RelaxNG, `isorelax`, Woodstox DTD/W3C validation factories | validator-side only | only needed if signature *validation* against schemas is kept |
| `es.gob.jmulticard` (+ `javax.smartcardio`) | **dead for browsers** | PC/SC, native (§4) |
| `com.sun.jna` / `jna.platform` | **dead for browsers** | native access (PC/SC fixups, Windows COM, Apple) |
| Apache PDFBox (`org.apache.pdfbox`) | **not used in the signing core** | appears only as `…contentstream.operator.OperatorName` imports in unrelated classes (e.g. `XAdESSigner.java:62`, `keystores/KeyStoreUtilities.java:32`) — a jadx constant-resolution artifact, not real usage `[INFERENCE]` |
| Swing UI (`standalone/ui/**`, `es/gob/afirma/ui`), `standalone/protocol/**` (WebSocket/`afirma://`) | **dead** | replaced by the SPA |
| Rhino + `proxy-vole`, twelve-monkeys ImageIO, `ini4j`, `Java-WebSocket` | **dead** | Rhino only reachable via iText AcroForm JS / MSV validation / PAC autodiscovery — the signing path never runs it; the rest are image IO, prefs, the local socket server |
| `massive/` (batch/directory signing) | out of scope | batch mode has no browser equivalent; revisit later |

## 6. Browser-migration notes

### 6.1 Crypto mapping

| Signing-core primitive | Browser target | Notes |
|---|---|---|
| `AOPkcs1Signer` (`Signature.initSign`), RSA PKCS#1 | `crypto.subtle.sign('RSASSA-PKCS1-v1_5', …)` | same operation as `SHA512withRSA`; `NONEwithRSA` = sign over pre-hashed input |
| ECDSA (`SHA256withECDSA`) | `crypto.subtle.sign('ECDSA', {name:'SHA-256'}…)` | **returns raw `r‖s`** — apply `Pkcs1Utils`-style DER encoding (`core/signers/Pkcs1Utils.java:15`) |
| Digests | `crypto.subtle.digest('SHA-256'…)` | SHA-1 accepted for legacy only (`isSHA1SignatureAlgorithm`, `AOSignConstants.java:145`); the default is `SHA512withRSA` (`:82`) |
| ASN.1 DER encode/decode | **WASM or JS** | no WebCrypto ASN.1; needed for CAdES/PDF and cert parsing |
| CMS/CAdES `SignedData` assembly | **JS/WASM** | pure byte structures; wasm-`asn1js`-class work, no crypto in it |
| XML-DSig + C14N + XAdES model | **JS/WASM** | `xmldsigjs`-class implementation; C14N is the hard part |
| PAdES (`/ByteRange`, incremental update, DSS) | **JS** | `pdf-lib`-class library could replace iText for basic PAdES; byte-range hashing must be exact |
| Signature **validation** (`signvalidation/`) | JS/WASM re-implementation | cert-chain + revocation checks also need OCSP/CRL fetch (CORS!) |

### 6.2 Keystore strategy

**PC/SC smart cards have no browser path.** No browser exposes APDU access; WebUSB/WebNFC/Web
Smart Card (`navigator.smartCard`, not shipped) are not viable for signing today. The DNIe/CERES
experience must be reproduced with the alternatives below.

| Today (Java) | Web replacement | Trade-off |
|---|---|---|
| `PKCS12`/`JAVA`/`JCEKS` file stores | **file upload + user password**, parse PKCS#12 in JS, import key into WebCrypto | needs `PKCS12` ASN.1 parser (small, WASM-friendly); key becomes non-extractable after import |
| `Windows-MY` / `APPLE` OS stores | **OS-certificate bridge, not available in-browser** — fall back to PKCS#12 upload, or a local helper app if ever needed | loses "cert already installed" convenience |
| NSS/Firefox store | dead (browser-internal) | users export a `.p12` |
| `PKCS11`/`DNIEJAVA`/`CERES*`/`KNOWN_SMARTCARDS`/`TEMD` | **out of scope**; document as unsupported, or offer a native helper (out-of-band, no key export) | largest functional gap vs AutoFirma |
| `SINGLE` (cert only) | trivial: parse PEM/DER cert | used for verify-only flows |

WebCrypto cannot sign with a certificate whose private key lives elsewhere — so the browser
keystore design is: **import the PKCS#12 key into `CryptoKey` (non-extractable), keep certs as
`X509Certificate`-equivalent JS objects, and sign via `subtle.sign`.** That is the modern
equivalent of `AOPkcs1Signer`.

### 6.3 What disappears

`standalone/protocol/**` (41 classes: `ProtocolInvocationLauncher*`, `AfirmaWebSocketServerV4`,
`ServiceInvocationManager`, `CommandProcessorThread`, `NativeSignDataProcessor`) plus
`SimpleAfirma.main` (`standalone/SimpleAfirma.java:607`) exist only to bridge a webpage to a local
Java app over a local socket / `afirma://` handler. In an SPA the page *is* the signer: URI parsing
(`core/misc/protocol/{ProtocolInvocationUriParserUtil,UrlParameters}.java`) survives as a pure
function, everything else is deleted. Likewise `signers/*/client/**` (the remote-signing clients)
collapse into `crypto.subtle` calls.

## 7. Limits of this map

- Static only: nothing was executed; behaviour is read from the decompiled tree (tag
  `reference/decompile-1.9`), which is lossy
  (jadx names, inlined constants — see the PDFBox artifact in §5).
- `AOPDFSigner`'s full call chain (JADX output is large) is summarised, not line-traced; the
  entry points and libraries are verified.
- Which exact PAdES profile (`PAdES-B-B`/`-B-T`) `AOPDFSigner` targets was not traced to a
  constant; `[INFERENCE]` baseline B-T given `PdfTimestamper`.
- Deeper: `standalone/ui/preferences/**` (43 classes) and `standalone/plugins/**` govern
  behaviour/config that the web rewrite will redesign rather than port.
- Citations are machine-checked: `python3 docs/check-architecture-doc.py` resolves every
  `file:line` citation against the decompiled tree and fails if a range is out of bounds or does
  not contain the identifiers the sentence names. Since #26 the tree is untracked, so on a fresh
  clone the checker prints an explicit `SKIP rules A/B` and still checks the `§N` cross-references;
  check out the tag first (above) to get the full check.
