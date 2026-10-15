# REVERSING-NOTES — AutoFirma v1.9 Linux samples (Refs #2)

Reverse-engineering notes for the AutoFirma 1.9 sample installers distributed for the
*Computer Basics* reverse-engineering task. Scope: the two Linux installers (Debian `.deb`,
Fedora `.rpm`). The Windows Inno Setup installer (`Autofirma64.zip`) is **not** analysed — see
[Limits](#limits-and-unknowns).

## 1. Provenance and inputs

| Input | Path (gitignored, never committed) | Size |
|---|---|---|
| Debian package zip | `original/Autofirma_Linux_Debian.zip` | 64.2 MB |
| Fedora package zip | `original/Autofirma_Linux_Fedora.zip` | 63.5 MB |
| Debian package | `extracted/deb/autofirma_1_9.deb` | 67,275,264 B |
| Fedora package | `extracted/rpm/autofirma-1.9-1.noarch_FEDORA.rpm` | 63.5 MB |

Package metadata (Debian `control`):

```
Package: autofirma        Version: 1.9.0        Architecture: all
Maintainer: Gobierno de Espana <soporte.afirma@correo.gob.es>
Homepage: https://administracionelectronica.gob.es/ctt/clienteafirma
Depends: libnss3-tools    Recommends: openjdk-17-jre
License: GPL-2+ | EUPL-1.1   Installed-Size: 56115
```

## 2. Method (REA staged workflow)

The REA skill (`reverse-engineer-anything`) staged workflow was followed — question checklist,
inventory of the smallest useful overview first, format-aware parsers, distinct
observation/inference/unknown, then evidence-backed ledger (this file).

**The REA MCP server is not registered on this host and no `rea` CLI is installed** (checked:
`which rea` empty, no `~/.rea`). Per the skill's connection guidance, the usable supported work was
continued with the direct static toolchain that the JAR route needs:

- `ar` + `tar`/`cpio`/`rpm2cpio` — package member extraction
- `unzip` — ZIP/JAR member inventory
- `jarsigner`/`keytool` — signature and signer-certificate evidence
- `jadx 1.5.6` — decompilation
- `sha256sum` — artifact identity

Evidence type throughout: **static, from the shipped artifacts**. No runtime observation was made
(AutoFirma was never executed).

## 3. Debian ⇄ Fedora artifact identity (T1)

`rpm2cpio autofirma-1.9-1.noarch_FEDORA.rpm | cpio -idmv` and `ar x` + `tar -xzf data.tar.gz`
produce the same two payload JARs. Byte-identical:

| Artifact | Size (B) | sha256 |
|---|---|---|
| `autofirma.jar` (deb `usr/lib/Autofirma/`, rpm `usr/lib64/autofirma/`) | 49,545,879 | `bd265f7597ee6814634f7b8b247f96eb2a0db61b602a75d37086a4a84c1f1259` |
| `autofirmaConfigurador.jar` (same dirs) | 22,529,020 | `5c2c4668129643304469d4a388e11ff47c020c6b99d954c3be729780e2495c9a` |

**Observation:** Debian and Fedora ship identical code. Differences are packaging only:

| | `.deb` | `.rpm` |
|---|---|---|
| JAR dir | `/usr/lib/Autofirma/` | `/usr/lib64/autofirma/` (case differs) |
| launcher | `/usr/bin/autofirma` **and** `/usr/bin/autofirmacl` | `/usr/bin/autofirma` |
| desktop entry | `/usr/share/applications/afirma.desktop` | — |
| browser pref | `/etc/firefox/pref/Autofirma.js` | `/usr/lib64/firefox/defaults/pref/autofirma.js` |
| licenses/docs | `eupl-1.1.txt`, `gpl-2.0.txt`, `copyright`, `es.gob.afirma.metainfo.xml` | — |

Launcher (verbatim, Debian):

```bash
#!/bin/bash
java -Djdk.tls.maxHandshakeMessageSize=65536 -jar /usr/lib/Autofirma/autofirma.jar "$@"
# autofirmacl adds: -Dafirma_debug_level=OFF
```

Observation: `autofirmacl` is the same JAR with the debug level forced to `OFF`; CLI vs GUI mode is
selected by argument parsing (`CommandLineLauncher`), not by a separate main class.

## 4. `autofirma.jar` inventory (T2)

- **Fat/uber JAR**: 21,247 ZIP entries, 86,699,418 B uncompressed; `es.gob.afirma`
  implementation classes plus shaded third-party code.
- **Multi-release JAR**: `META-INF/versions/{9,11,15,21}` present (1,094 class entries under
  `versions/9` alone) — the same JAR targets JDK 8+ and overrides on newer runtimes.
- **Signed, timestamped**: `META-INF/FIRMA_DE.SF` (3,062,267 B) + `META-INF/FIRMA_DE.RSA`;
  per-entry `SHA-384-Digest` in the manifest (no MD5/SHA-1 legacy digests).

Signer (from `keytool -printcert -jarfile`):

```
Owner:  CN=FIRMA DE CODIGO JAVA SECRETARIA GENERAL DE ADMINISTRACION DIGITAL,
        SERIALNUMBER=S2800568D, OU=SECRETARIA GENERAL DE ADMINISTRACION DIGITAL,
        O=MINISTERIO DE ASUNTOS ECONOMICOS Y TRANSFORMACION DIGITAL, L=MADRID, C=ES
Issuer: OU=AC Componentes Informaticos, O=FNMT-RCM, C=ES
Validity: 2023-05-19 .. 2026-05-19
Timestamp: CN=DigiCert Timestamp 2024 (DigiCert Trusted G4 RSA4096 SHA256 TimeStamping CA)
```

**Unknown/limit:** `jarsigner -verify` reports *"contains entries whose certificate chain is
invalid … PKIX path building failed"* — the FNMT-RCM issuer is not in the local JDK truststore.
So: signature **present and timestamped**; chain **not verifiable offline here**. This is a local
truststore gap, not evidence of tampering (the digests verify against the manifest entries).

### MANIFEST.MF (headers of interest)

```
Manifest-Version: 1.0
Application-Name: Autofirma
Implementation-Title: es.gob.afirma
Implementation-Version: build01
Specification-Title: Autofirma        Specification-Version: 1.8
Specification-Vendor: Gobierno de España
Implementation-Vendor: Gobierno de España
Permissions: all-permissions
Main-Class: es.gob.afirma.standalone.SimpleAfirma
Build-Jdk-Spec: 1.8
Created-By: Maven JAR Plugin 3.3.0
```

### Entry points

| Role | Class |
|---|---|
| `Main-Class` (GUI + CLI dispatch) | `es.gob.afirma.standalone.SimpleAfirma` |
| CLI parsing / commands | `es.gob.afirma.standalone.CommandLineLauncher`, `...CommandLineCommand`, `...CommandLineParameters` |
| Browser-protocol invocation | `es.gob.afirma.standalone.protocol.*` (`ProtocolInvocationLauncher`, 41 classes) |
| Signature viewer | `es.gob.afirma.standalone.VisorFirma` |
| Keystore selection / worker | `es.gob.afirma.standalone.SimpleKeyStoreManager`, `SimpleKeyStoreManagerWorker` |

### `META-INF/services` (13 providers registered)

`java.security.Provider`, `javax.script.ScriptEngineFactory`, `javax.imageio.spi.{ImageReaderSpi,
ImageWriterSpi, ImageInputStreamSpi}`, `javax.xml.stream.{XMLInputFactory, XMLOutputFactory,
XMLEventFactory}`, and six Woodstox/MSV RelaXNG + DTD/W3C/XML schema-validation factories.

## 5. `es.gob.afirma.*` package map (T2)

1,209 `es/gob/**` class entries (899 `es/gob/afirma`, 310 `es/gob/jmulticard`) plus 135
`es/uji/crypto/xades` (bundled `jxades` fork) — 1,344 first-party classes out of 19,056 class
entries total. Largest `es/gob/afirma` subpackages:

| Classes | Package | Role (inferred from names) |
|---|---|---|
| 355 | `es/gob/afirma/standalone` (incl. `ui`, `protocol`, `plugins`) | Swing UI, protocol handler, plugin manager |
| 175 | `es/gob/afirma/signers` | signature formats (CAdES, XAdES, PAdES, OOXML, ODF) |
| 151 | `es/gob/afirma/keystores` | keystore providers/adapters, Mozilla NSS store |
| 134 | `es/gob/afirma/core` | core APIs, `misc`, `ui`, `prefs` |
| 55 | `es/gob/afirma/ui` | shared JSE UI toolkit (certificate selection) |
| 21 | `es/gob/afirma/signvalidation` | signature validation |
| 8 | `es/gob/afirma/massive` | batch/massive signing |

Deeper interest: `standalone/ui` (90), `keystores/jmulticard/ui` (53),
`standalone/ui/preferences` (43), `standalone/protocol` (41), `standalone/ui/pdf` (35),
`keystores` (35), `ui/core/jse/certificateselection` (31), `standalone/ui/restoreconfig` (23),
`standalone/plugins` (23), `keystores/mozilla` (19).

## 6. Bundled third-party libraries (T2)

From `META-INF/maven/**/pom.properties` — 62 published artifacts survive shading: **37 first-party**
`es.gob.afirma*` / `es.uji.crypto.xades` modules and **25 third-party** dependencies.

First-party Maven modules (e.g. `afirma-core`, `afirma-crypto-{cades,xades,padestri-client,
pdf,odf,ooxml,validation,batch-client}`, `afirma-keystores-{mozilla,filters,jmulticard-ui}`,
`afirma-ui-simpleafirma-{plugins,plugins-manager,configurator-common}`, `es.gob.afirma.jmulticard`
and `es.uji.crypto.xades:jxades`).

Third-party dependencies (shaded into the JAR; ~17,712 class entries vs 1,344 first-party; several
are bytecode-relocated):

| Coordinate | Notes |
|---|---|
| `org.bouncycastle:*` (bcprov/bcpkix/bcutil, incl. PQC: Dilithium, SPHINCS+) | largest shaded tree: `org/bouncycastle/{jcajce 1733, pqc 1021, crypto 781, asn1 746, oer 315, math 264, cms 239, cert 158}` classes |
| `org.spongycastle:*` | legacy BouncyCastle fork, same package layout (`jcajce 1096`, `asn1 659`, `crypto 654`, `pqc 333`) |
| `org.apache.pdfbox:{pdfbox,fontbox}` | PDF signing/rendering (780 + 93 cmap resources) |
| `com.lowagie:itext` (`com/aowagie/*`, 425 classes) | legacy PDF library, repackaged namespace |
| `com.sun.jna` + `com.sun.jna.platform` (1401 classes) | native access (Windows COM etc.) |
| `org.bidib…:proxy-vole` | proxy autodiscovery (WPAD/PAC — uses Rhino) |
| `org.javadelight:delight-rhino-sandbox` + `org.mozilla:rhino` (399) | JS engine for PAC scripts |
| `org.apache.santuario:xmlsec`, `org.codehaus.woodstox:stax2-api`, `com.fasterxml.woodstox:woodstox-core`, `net.java.dev.msv:xsdlib`, `com.sun.xml.bind.jaxb:isorelax` | XML-DSig and RelaxNG/DTD validation stack |
| `com.twelvemonkeys.{common,imageio}` (common-image/io/lang, imageio-{core,jpeg,metadata}) | JPEG/ImageIO extensions (417 classes) |
| `org.glassfish:javax.json` + `javax.json:javax.json-api`, `jakarta.xml.bind:jakarta.xml.bind-api` | JSON/JAXB APIs |
| `org.json:json`, `org.ini4j:ini4j`, `commons-codec`, `commons-logging`, `org.slf4j:{api,simple}`, `org.java-websocket:Java-WebSocket`, `nu.xom:jaxen` (168), `com/ctc/wstx` shaded MSV |

Observation: the JAR is a Maven-shaded uber-JAR; several upstreams are relocated
(`com/lowagie` ← iText, `com/ctc/wstx/shaded/msv_core/*`, `delight/rhinosandox/internal`).
**Unknown:** exact upstream versions for every shaded library are only available where
`pom.properties` survived shading; bytecode-relocated copies can differ from the published release.

## 7. Decompilation results (T3, T4)

Commands (reproducible, run from the worktree root):

```bash
# inputs (gitignored)
unzip -o original/Autofirma_Linux_Debian.zip -d extracted/deb
cd extracted/deb && ar x autofirma_1_9.deb && tar -xzf data.tar.gz -C unpack

jadx -r -j 4 -d decompiled            extracted/deb/unpack/usr/lib/Autofirma/autofirma.jar
jadx -r -j 4 -d decompiled-configurador extracted/deb/unpack/usr/lib/Autofirma/autofirmaConfigurador.jar
```

| Output | jadx layout | `*.java` files | Size | Tracked |
|---|---|---|---|---|
| `decompiled/` | `decompiled/sources/**` | 11,487 | 83 MB | no — tag `reference/decompile-1.9` |
| `decompiled-configurador/` | `decompiled-configurador/sources/**` | 4,811 | 32 MB | no — tag `reference/decompile-1.9` |

- `-r` (`--no-res`) was used deliberately: only Java source is the deliverable
  (AGENTS.md: "Decompiled Java is text — small, diffable"); decoding the ~21k resources would
  dump images/PDF fonts/help corpora into git. The `resources/` trees are therefore empty and were
  removed.
- jadx 1.5.6 exits **3** ("finished with errors") though the tree is complete: 19 un-decompilable
  items in `autofirma.jar`, 7 in `autofirmaConfigurador.jar`.
- Coverage: 49 of 11,487 main-jar files (0.4%) contain a jadx fallback marker
  (`Method not decompiled:` / `JADX ERROR: Type inference failed|JadxOverflowException|
  NullPointerException`). Affected modules are concentrated in shaded third-party code
  (`com.aowagie.text.pdf`, `com.ctc.wstx`, `org.bouncycastle.jcajce`, `org.mozilla.javascript`,
  `com.sun.java.help.search`), plus one first-party hit:
  `es/gob/afirma/ui/core/jse/JSEUIManager.java` (`JadxOverflowException in pass:
  RegionMakerVisitor`).
- **Unknown:** jadx output is an approximation of the original source, not the original. Original
  variable names, generics, comments, and lambdas are unrecoverable; compare against the JAR's
  bytecode before relying on a decompiled method.

Both trees were tracked from #2 until #26, which dropped them from the repo (180 MB of text in
every clone) and pinned them on the annotated tag `reference/decompile-1.9` instead. They are
gitignored now; recover them into the worktree with:

```bash
git checkout reference/decompile-1.9 -- decompiled decompiled-configurador
```

`docs/architecture.md` cites `file:line` against `decompiled/sources/**`; its checker needs the
tree present (see the note in §7 of that doc).

### `autofirmaConfigurador.jar` snapshot

- 7,611 entries, 33,868,314 B uncompressed; `Main-Class:
  es.gob.afirma.standalone.configurator.AutofirmaConfigurator`, spec version 1.1.
- Signed by the same FNMT-RCM `FIRMA DE CODIGO JAVA` certificate; timestamped 2025-05-23.
- Contains a second entry point `AutofirmaConfiguratorSilent` (unattended install mode) and
  per-OS configurators: `ConfiguratorFirefox{Linux,Mac,Windows}`, `CertUtil`.
- Own `es/gob/afirma` tree is 427 classes, plus the same shaded XML/SSC stack and `nu/xom`, `es/uji/crypto`.

## 8. Limits and unknowns

- **Windows EXE not compared.** Per AGENTS.md the `.deb`/`.rpm` JAR already covers the Linux
  deliverable; whether the Windows build ships the same JAR bytes is **unknown**.
- **Signature chain not verified** against a public trust anchor (FNMT-RCM not in the local
  truststore); digest integrity yes, chain trust unconfirmed offline.
- **No runtime evidence.** Nothing was executed; all statements about behaviour are static
  observations or clearly-marked inferences from names/strings.
- **Shaded library versions** are only partially recoverable (see §6).
- **Decompilation is lossy** (see §7).
- `decompiled*` trees follow jadx's native `sources/` layout; no flattening was applied so the
  output is exactly reproducible with the recorded command line.

## 9. Finding ledger

| # | Question | Answer | Evidence | Confidence |
|---|---|---|---|---|
| 1 | Do Debian and Fedora ship the same code? | Yes, byte-identical JARs | sha256 of both JARs | observed |
| 2 | What is the main entry point? | `es.gob.afirma.standalone.SimpleAfirma` | `META-INF/MANIFEST.MF` | observed |
| 3 | Is the JAR signed? | Yes, FNMT-RCM code-signing cert + DigiCert timestamp, SHA-384 digests | `keytool -printcert -jarfile`, manifest | observed |
| 4 | Is the signature trustworthy offline? | Unverified — issuer absent from local truststore | `jarsigner -verify` output | observed |
| 5 | How much of it is AutoFirma's own code? | 1,344 `es/gob`+`es/uji` classes vs 17,712 shaded third-party classes | ZIP inventory | observed |
| 6 | Can the source be recovered? | Yes, 11,487 + 4,811 `.java`, 0.4% lossy | jadx run logs, marker scan | observed |
| 7 | Which formats can it sign? | CAdES, XAdES, PAdES, OOXML, ODF (+ validation) | package/class names, Maven module names | inferred |
