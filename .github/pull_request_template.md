## Qué cambia

- [ ] Resumen del cambio en 1–3 líneas.

## Evidencia (obligatoria)

Ejecuta desde la raíz del repo y marca lo que corre de verdad — no pongas checks sin salida:

- [ ] `pnpm test` pasa.
- [ ] `pnpm check` pasa (svelte-check + tsc).
- [ ] `pnpm build` pasa (static adapter).
- [ ] Cualquier firma nueva o tocada: verificada con `openssl` (p. ej.
      `openssl cms -verify` / `bash test/fixtures/verify-fixtures.sh`) — ver
      AGENTS.md (el código de firma NO entra a review sin verificación externa).
- [ ] Pasos manuales en navegador (si hay UI): qué ruta, qué se hizo, qué se
      vio. Captura o descripción del resultado.

## Tamaño

- [ ] El entregable cabe en ~400 líneas (límite de AGENTS.md). Si no, dividir
      el issue antes de revisar.

## Metadatos

- [ ] El commit referencia el issue con `Refs #N` (nunca `Closes`/`Fixes`: el
      orquestador cierra el issue).
