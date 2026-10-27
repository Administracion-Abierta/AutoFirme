# Privacy statement / Declaración de privacidad

Applies to the AutoFirme Chrome/Edge extension and the AutoFirme web app. Last updated
2026-10-21. Contact: the issue tracker of
<https://github.com/Administracion-Abierta/AutoFirme>.

## English

**AutoFirme runs locally.** Signing, certificate parsing, verification, PDF stamping and
batch/ZIP creation happen in the browser. There is no AutoFirme account, backend,
analytics, telemetry or advertising, and no document, key, PIN or certificate is sent to
the project's maintainers.

### What is stored on your device

- Imported PKCS#12 containers, sealed with AES-GCM under your PIN (PBKDF2-SHA256,
  250,000 iterations) in the extension-origin IndexedDB. The PIN itself is never written
  to disk.
- Settings: theme, language, TSA URL, disabled-site exceptions and, if you enable the
  MCP bridge, its endpoint and token.
- The in-memory PIN cache, only when you tick the remember box: it lives in memory for
  the chosen minutes and disappears when the browser closes.

You can delete a saved identity from the certificate picker. When the browser database is
unavailable the picker also offers a full store reset. Uninstalling the extension removes
its storage.

### When the extension uses the network

Only actions you initiate:

1. **Timestamping (TSA).** If you configure a TSA and sign with the timestamp option, the
   extension sends the signature digest to that URL. Chrome asks for access to its origin
   first.
2. **Portal server-backed CAdEStri signing.** Aragon-style portals run the pre-sign and
   post-sign phases on their own server. The extension sends the selected public
   certificate, request data and the local PKCS#1 signature to the server the portal
   named. That server is the portal's, not AutoFirme's.
3. **Optional MCP bridge.** If you enable it, the extension opens an outbound
   WebSocket to the server address and token you entered. It never opens an inbound port.

No other outbound request exists: fonts, styles, scripts and icons are bundled with the
package. Verification performs no revocation lookup (no OCSP/CRL fetch).

### Permissions

`storage`, `scripting` and `webNavigation`, plus HTTP/HTTPS host access so portal
`AutoScript` calls can be intercepted from installation. Site access lets you disable
any origin, and Chrome's own site-access settings still apply. The full justification
table is in [STORE-LISTING.md](STORE-LISTING.md) §4.

### Legal and support scope

The extension provides technical signature generation; it is not a qualified trust
service and produces no legal signature by itself. Certificate trust, revocation and
long-term validation depend on the certificates and on the relying party. No external
security audit has been performed.

## Español

**AutoFirme funciona en local.** La firma, el análisis del certificado, la verificación,
el sellado visible del PDF y la creación de lotes/ZIP ocurren en el navegador. No hay
cuenta, servidor, analítica, telemetría ni publicidad de AutoFirme, y ningún documento,
clave, PIN o certificado se envía a los mantenedores del proyecto.

### Qué se guarda en tu dispositivo

- Los contenedores PKCS#12 importados, cifrados con AES-GCM bajo tu PIN (PBKDF2-SHA256,
  250.000 iteraciones) en IndexedDB del origen de la extensión. El PIN nunca se escribe en
  disco.
- Ajustes: tema, idioma, URL de la TSA, sitios deshabilitados y, si activas el puente MCP,
  su dirección y token.
- La caché de PIN en memoria, solo si marcas la casilla: vive en memoria durante los
  minutos elegidos y desaparece al cerrar el navegador.

Puedes borrar una identidad guardada desde el selector de certificados. Cuando la base de
datos del navegador no está disponible, ese mismo selector ofrece además reiniciar todo el
almacén. Desinstalar la extensión elimina su almacenamiento.

### Cuándo usa la red

Solo acciones que tú inicias:

1. **Sellado de tiempo (TSA).** Si configuras una TSA y firmas con sello de tiempo, la
   extensión envía el resumen de la firma a esa URL. Chrome pide antes permiso para su
   origen.
2. **Firma CAdEStri con servidor del portal.** Los portafirmas tipo Aragón ejecutan las
   fases de prefirma y postfirma en su propio servidor. La extensión envía el certificado
   público elegido, los datos de la solicitud y la firma PKCS#1 local al servidor que
   indica el portal. Ese servidor es del portal, no de AutoFirme.
3. **Puente MCP opcional.** Si lo activas, la extensión abre un WebSocket saliente hacia
   la dirección y el token que introduzcas. Nunca abre un puerto entrante.

No existe ninguna otra petición saliente: las fuentes, estilos, scripts e iconos van
incluidos en el paquete. La verificación no consulta revocación (no hay peticiones
OCSP/CRL).

### Permisos

`storage`, `scripting` y `webNavigation`, además de acceso a hosts HTTP/HTTPS para
interceptar las llamadas `AutoScript` de los portafirmas desde la instalación. El panel de
acceso a sitios permite deshabilitar cualquier origen y los ajustes de sitio de Chrome
siguen aplicándose. La tabla completa de justificaciones está en
[STORE-LISTING.md](STORE-LISTING.md) §4.

### Alcance legal y de soporte

La extensión genera firmas técnicas; no es un servicio de confianza cualificado ni
produce por sí sola una firma legal. La confianza, la revocación y la validación a largo
plazo dependen de los certificados y de quien recibe la firma. No se ha realizado una
auditoría de seguridad externa.
