# Store listing kit (Chrome Web Store / Microsoft Edge Add-ons)

Everything needed to submit the MV3 package produced by `pnpm build:extension` (or
`pnpm package:extension`). Keep this file next to the release: the copy and the
justifications must match the shipped `extension/manifest.json`.

**Distribution blocker:** the upstream revision used as reference did not declare an
application license (`extension/README.md`, `docs/CHROME-EXTENSION-VERIFICATION.md` §6).
Resolve distribution rights before pressing Submit.

## 1. Listing metadata

| Field | Value |
|---|---|
| Name | `AutoFirme` |
| Category | Productivity |
| Language | Spanish (default), English |
| Homepage | https://github.com/Administracion-Abierta/AutoFirme |
| Support | https://github.com/Administracion-Abierta/AutoFirme/issues |
| Privacy policy | `docs/PRIVACY.md` of the published repository (must be a public URL before submitting) |
| Icon | `static/icon-128.png` (also packaged as `icon-128.png`) |
| Screenshots | `docs/images/store/screenshot-inicio.png`, `docs/images/store/screenshot-ajustes.png` (1280×800) |
| Promo tile (optional) | 440×280, not generated yet; take a crop of the header + certificate picker |

Chrome also accepts 640×400 screenshots. Both committed captures are 1280×800 and
were derived from the 1280×900 screenshots in `docs/images/` (`ffmpeg`, scaled and
padded, no UI added).

## 2. Store copy

### Short description (132 characters max)

- ES: `Firma y verifica documentos con tus certificados PKCS#12 sin instalar AutoFirma. Todo se procesa en tu navegador.`
- EN: `Sign and verify documents with your PKCS#12 certificates without installing AutoFirma. Everything runs in your browser.`

### Detailed description — ES

```
AutoFirme firma y verifica documentos electrónicos con tus propios certificados, sin la
aplicación de escritorio y sin subir claves privadas a ningún servidor.

• Importa un PKCS#12 (.p12/.pfx) una vez: queda cifrado con su PIN en el navegador.
• Firma CAdES, XAdES y PAdES, con sellado de tiempo opcional (TSA) y firma visible en PDF.
• Verifica firmas CAdES, XAdES y PAdES y explica cada comprobación.
• Intercepta las llamadas AutoScript de los portafirmas y firma desde la propia web.
• Cola por lotes y descarga en ZIP.
• Interfaz en español e inglés, temas claro y oscuro.

La clave privada nunca sale del navegador. No hay analítica, ni cuentas, ni telemetría.
Solo se realiza una conexión de red cuando tú la provocas: el servidor TSA configurado,
el servidor del portal que ya usa CAdEStri, o el puente MCP opcional.
```

### Detailed description — EN

```
AutoFirme signs and verifies electronic documents with your own certificates, without
the desktop application and without uploading private keys to any server.

• Import a PKCS#12 (.p12/.pfx) once: it stays encrypted under its PIN in the browser.
• Sign CAdES, XAdES and PAdES, with optional trusted timestamping (TSA) and a visible
  PDF signature block.
• Verify CAdES, XAdES and PAdES signatures and see every check explained.
• Intercepts portal AutoScript calls and signs from the website itself.
• Batch queue and ZIP download.
• Spanish and English UI, light and dark themes.

The private key never leaves the browser. No analytics, no accounts, no telemetry. The
only network calls happen when you trigger them: the configured TSA, the portal's own
CAdEStri signing server, or the optional MCP bridge.
```

## 3. Single purpose

- ES: `Firmar y verificar documentos electrónicos con certificados X.509 del usuario y atender las peticiones AutoScript de los portafirmas, sin salir del navegador.`
- EN: `Sign and verify electronic documents with the user's X.509 certificates and serve portal AutoScript requests, without leaving the browser.`

## 4. Permission justifications

Each entry matches a key in `extension/manifest.json`.

| Permission | Why it is required | User-visible behaviour |
|---|---|---|
| `storage` | Keeps sealed PKCS#12 containers, the optional in-memory PIN setting, disabled-site exceptions and the MCP bridge settings. | Identities appear in the certificate picker after a reload. Nothing is stored unencrypted. |
| `scripting` | Registers the isolated and MAIN-world bridge scripts that intercept `AutoScript` calls at `document_start`. | Every HTTP/HTTPS page gets the bridge unless its origin is disabled in Site access. |
| `webNavigation` | Reads the actual frame URL and document identity before signing, so a stale or navigated page cannot authorize a signature. | A request started on an old page fails with a message instead of signing. |
| `host_permissions` (`http://*/*`, `https://*/*`) | Portal interception is the extension's core function and must work on the first call after installation, on any administration domain. | Site access lists every origin and lets the user disable any of them; Chrome's own site-access settings still apply. |
| Optional host grant requested at runtime | Timestamping needs access to the TSA origin the user typed; Chrome prompts for that origin only. | No TSA call is attempted before the grant; the permission panel explains it. |

No `tabs`, `cookies`, `history`, `downloads`, `clipboardWrite`, `nativeMessaging` or
`management` permission is requested. There is no remote code: the CSP is
`script-src 'self'; object-src 'none'` and the artifact checker rejects remote assets,
inline scripts and `eval`.

## 5. Privacy practices checklist (Developer Dashboard)

- Data collection: **none** — no personally identifiable information, health, financial,
  authentication, personal communications, location, web history, user activity or
  website content leaves the device.
- Sale to third parties: **no**.
- Use for creditworthiness/lending: **no**.
- Remote code: **no**.
- Certification: the extension's single purpose matches §3, and all permissions are
  used for that purpose.
- Privacy policy URL: the public URL of `docs/PRIVACY.md`.

## 6. Submission steps

1. `pnpm install --frozen-lockfile && pnpm check && pnpm test && pnpm build:extension`.
2. `pnpm package:extension` → `dist/autofirme-extension-<version>.zip` + `SHA256SUMS`.
3. In the Chrome Web Store Developer Dashboard: upload the ZIP, paste §1–§3, attach the
   icon/screenshots, complete §5, set visibility (public or unlisted).
4. Edge Add-ons accepts the same MV3 ZIP; reuse §2–§5. No Edge-specific manifest change.
5. After approval, record the listing URL in `docs/CHROME-EXTENSION.md`.

The same ZIP is what `pnpm test:extension:browser` and `pnpm demo:registradores` load
unpacked, so the store artifact is the verified one.
