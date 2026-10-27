# AutoFirme — qué tienen las aplicaciones

Documento en español, conciso y completo, sobre lo que **ya funciona**. La versión inglesa está en
[`README.md`](../README.md). Las decisiones de producto están en
[`DESKTOP-ROADMAP.md`](DESKTOP-ROADMAP.md) §1, que sustituye al análisis de superficies de
[`PIVOT-STUDY.md`](PIVOT-STUDY.md).

## Qué es

Firma y verificación electrónica con certificados X.509, **100 % en el dispositivo**: sin servidor de
firma, sin backend, sin que la clave privada salga del equipo. Clon funcional de AutoFirma 1.9
(reescritura limpia en TypeScript, no traducción del Java). Referencia:
[`architecture.md`](architecture.md), [`PARITY.md`](PARITY.md), [`USE-CASES.md`](USE-CASES.md).

Dos productos sobre un solo motor: la **app de escritorio** (Tauri, en construcción) y la
**extensión de Chrome MV3** ([repositorio propio](https://github.com/Administracion-Abierta/AutoFirme-Extension)),
la superficie que intercepta las llamadas `AutoScript` de las sedes electrónicas.
llamadas `AutoScript` de las sedes electrónicas.

## Firmar

| Formato | Estado | Detalle |
|---|---|---|
| **CAdES-BES** (`.csig`) | funciona, en la interfaz | desattached; modo adjunto solo para la extensión; atributos `contentType`, `messageDigest`, `signingTime`, `signingCertificateV2` |
| **CAdES-T** | funciona | sello RFC 3161 como atributo sin firmado `id-aa-timeStampToken` (#43) |
| **XAdES-EPES** (`.xsig`) | funciona, en la interfaz | XML envolvente, SHA-256/384/512, C14N 1.0 y C14N exclusivo, política EPES |
| **XAdES-T** | no | falta `SignatureTimeStamp` |
| **PAdES B-B** (PDF) | funciona, en la interfaz | actualización incremental real, `adbe.pkcs7.detached` / `ETSI.CAdES.detached` (#11) |
| **PAdES B-T** | funciona | con TSA configurado |
| **Firma visible en PDF** | funciona, en la interfaz | imagen PNG/JPEG (alfa vía `/SMask`), nombre y fecha, por página y esquina o coordenadas; desactivada por defecto (#45) |
| **OOXML** (docx/xlsx/pptx) | funciona, en la interfaz; auto-detectado al soltar el archivo | parte `XAdES v1.3.2` + `origin.sigs`, deja el resto del ZIP idéntico bit a bit (#10, #127). Verificar OOXML no está soportado (igual que 1.9) |
| ODF, FacturaE, ASiC-S, co-firma/contrafirma, Detached/Enveloping | no | ver `PARITY.md` §G |

Claves admitidas: **RSA (RSASSA-PKCS1-v1_5)** y **ECDSA P-256/P-384/P-521**. Nada de SHA-1 para
firmar ni de DSA.

## Certificados e identidad

- Importación de **PKCS#12 / `.p12` / `.pfx`** con PIN, sin dependencias: PBES2 (PBKDF2 + AES-CBC) y
  los PBES1 antiguos (DES, RC2, RC4) escritos a mano porque `crypto.subtle` no los tiene.
- Clave importada como `CryptoKey` **no extraíble**, usos `['sign']`: no se puede volcar a disco.
- **Identidades guardadas** en IndexedDB, selladas con PBKDF2 → AES-GCM bajo el PIN del contenedor;
  el PIN nunca se persiste (#29).
- Anclas de confianza incluidas: raíz **FNMT AC RAIZ FNMT-RCM** (`scripts/fetch-roots.sh` la
  actualiza comprobando asunto y huella).
- **No hay** acceso al almacén del sistema (Windows-MY, Llavero, NSS) ni a certificados de DNIe o
  tarjeta: el navegador no lo expone. Llegan con la app de escritorio (`DESKTOP-ROADMAP.md`).

## Verificar

`Verificar` acepta `.csig`, `.xsig` o PDF y devuelve una tarjeta por comprobación, verde/ámbar/roja:
estructura, integridad del contenido, firma criptográfica, referencias firmadas, certificado firmante,
validez (vencido = **ámbar**, nunca rojo: `valid-but-expired`), cadena de confianza hasta la raíz e
instante de firma. CAdES desattached pide también el documento original; PAdES no lo necesita porque
el `/ByteRange` del propio PDF es el contenido. Verificación cruzada con `openssl cms -verify`,
`pdfsig` y `qpdf` en las pruebas.

Fuera de alcance: OCSP/CRL, **validar** sellos de tiempo recibidos y políticas de certificado.

## Lote y salidas

Cola de lote multiarchivo con estado por documento, ZIP de salida (`unzip -t` en las pruebas) y su
`manifest.json` (#9); bóveda de descargas en IndexedDB; reutilización de identidades guardadas.

## Idiomas, temas, accesibilidad

Copia **español e inglés** (`src/lib/i18n.ts`, español por defecto); cambio de idioma y de pestaña
reflejados en la URL. Modo claro/oscuro antes del primer pintado (`static/theme-bootstrap.js`),
responsive de 320 a 1280 px, navegación por teclado y roles ARIA en pestañas y diálogos.

## Distribución

- **App de escritorio (Tauri):** se instala por sistema operativo desde los assets del release
  (minisign + `SHA256SUMS`); aún no hay binarios publicados. Ver `DEPLOY.md`.
- **Extensión Chrome MV3 (#24):** en su [propio repositorio](https://github.com/Administracion-Abierta/AutoFirme-Extension):
  la interfaz completa más un puente que intercepta `AutoScript`; ZIP por release. Firefox pendiente.

## Privacidad

Sin telemetría, sin analítica, sin backend. Lo único que sale del navegador, si se configura, es el
resumen SHA-256 hacia la TSA: nunca el documento, nunca la clave.

## Comandos

```bash
pnpm install
pnpm dev                  # servidor de desarrollo
pnpm build                # build/ (opciones + arnés local)
pnpm preview              # servir build/
pnpm test                 # vitest + node:test
pnpm check                # svelte-check + tsc
pnpm perf                 # presupuestos de rendimiento (frío, PDF 50 MB, lote de 500)
```

La extensión de Chrome se compila y empaqueta en su propio repositorio
([AutoFirme-Extension](https://github.com/Administracion-Abierta/AutoFirme-Extension)).

## Pruebas y evidencia

325 pruebas automatizadas (vitest + node:test). Toda la cripto se contrasta con herramientas
independientes: `openssl cms -verify`, `openssl ts`, `pdfsig`, `qpdf`, `unzip -t` y recomputación
de resumenes en Python. La extensión se probó contra la página de pruebas del Colegio de
Registradores (<https://www.registradores.org/test-autofirma>); esa verificación y su evidencia
viven en el [repositorio de la extensión](https://github.com/Administracion-Abierta/AutoFirme-Extension)
(`docs/CHROME-EXTENSION-VERIFICATION.md`). Comprobaciones que aún requieren manual: diálogos
nativos de instalación de Chrome, Windows/macOS físicos, TSA pública real y una transacción
administrativa con certificado personal.

## Dónde mirar

| Documento | Contenido |
|---|---|
| [`../README.md`](../README.md) | versión inglesa, mapa de rutas y guías por función |
| [`PIVOT-STUDY.md`](PIVOT-STUDY.md) | superficies y decisiones (histórico, sustituido por `DESKTOP-ROADMAP.md`) |
| [`DESKTOP-ROADMAP.md`](DESKTOP-ROADMAP.md) | decisiones de producto: escritorio + extensión |
| [AutoFirme-Extension/docs](https://github.com/Administracion-Abierta/AutoFirme-Extension/tree/main/docs) | extensión: instalación, contrato de sitio, límites |
| [`PARITY.md`](PARITY.md) | matriz de paridad con AutoFirma 1.9 y lista de huecos |
| [`USE-CASES.md`](USE-CASES.md) | casos de uso de 1.9 y su alcance aquí |
| [`architecture.md`](architecture.md) | mapa del núcleo Java que este proyecto reemplaza |
| [`MCP-DESIGN.md`](MCP-DESIGN.md) | servidor MCP: motor bajo Node, transporte, modelo de amenaza |
| [`REVERSING-NOTES.md`](REVERSING-NOTES.md) | procedencia del estudio de los instaladores 1.9 |
