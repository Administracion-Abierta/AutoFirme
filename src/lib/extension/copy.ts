export const extensionCopy = {
 es: {
  requestDetails:'Detalles de la solicitud', certDetails:'Detalles del certificado', copyDigest:'Copiar SHA256', digestCopied:'SHA256 copiado', copyFailed:'No se pudo copiar el SHA256. Puedes consultarlo en los detalles de la solicitud.',
  request:'Solicitud de certificado', sign:'Firmar documento', select:'Compartir certificado público',
  requestedBy:'Sitio solicitante', digest:'Resumen SHA-256', noDigest:'No se solicita firmar un documento.',
  consent:'Selecciona y desbloquea un certificado. Solo el botón de confirmación autoriza esta solicitud.',
  confirm:'Confirmar y firmar', confirmSelect:'Compartir certificado', cancel:'Cancelar', fallback:'Continuar con AutoFirma',
  fullTab:'Abrir aplicación completa', access:'Acceso a sitios', loading:'Cargando solicitud…', signing:'Procesando…',
  done:'Solicitud completada. Puedes cerrar esta ventana.', cancelled:'Solicitud cancelada.', continued:'Solicitud enviada a AutoFirma.',
  failed:'La solicitud ha terminado. Abre la aplicación o inicia una solicitud nueva en el sitio.',
  permissionDenied:'Permiso no concedido. Puedes volver a intentarlo.', origin:'Origen del sitio', disableSite:'Deshabilitar sitio', restore:'Volver a habilitar',
  sitesHint:'AutoFirme no intercepta ningún sitio hasta que lo autorices. Escribe el origen de tu sitio y pulsa Autorizar sitio; recarga el sitio tras conceder el permiso. Cada firma requiere tu confirmación.',
  authorizeSite:'Autorizar sitio', grantedSites:'Sitios autorizados', revokeSite:'Quitar acceso', disabledSites:'Sitios deshabilitados',
  allSites:'Interceptar todos los sitios', allSitesHint:'Concede acceso a todos los sitios HTTP y HTTPS sin autorizarlos uno a uno. Recomendado solo en entornos de confianza. Desactívalo para volver a la autorización por sitio.',
  enableAllSites:'Activar todos los sitios', disableAllSites:'Desactivar todos los sitios',
  tsa:'Permiso de red para sellado de tiempo', tsaHint:'Si Chrome restringe el acceso a sitios, autoriza el origen de tu TSA para usar el sellado de tiempo.',
  tsaUrl:'URL de TSA', grantTsa:'Autorizar TSA', granted:'Permiso concedido.', close:'Cerrar',
  invalid:'El certificado no es válido.', expired:'El certificado ha caducado.', notYetValid:'El certificado aún no es válido.',
  privateKey:'Desbloquea un certificado con clave privada.', rsa:'Se requiere un certificado RSA de al menos 2048 bits con SHA-256.',
  ca:'Selecciona un certificado de firma personal, no una autoridad certificadora.', keyUsage:'El certificado no permite firmar documentos.'
 },
 en: {
  requestDetails:'Request details', certDetails:'Certificate details', copyDigest:'Copy SHA256', digestCopied:'SHA256 copied', copyFailed:'Could not copy SHA256. You can find it in the request details.',
  request:'Certificate request', sign:'Sign document', select:'Share public certificate', requestedBy:'Requesting website', digest:'SHA-256 digest',
  noDigest:'No document signature is requested.', consent:'Select and unlock a certificate. Only the confirmation button authorizes this request.',
  confirm:'Confirm and sign', confirmSelect:'Share certificate', cancel:'Cancel', fallback:'Continue with AutoFirma', fullTab:'Open full application',
  access:'Site access', loading:'Loading request…', signing:'Processing…', done:'Request completed. You can close this window.', cancelled:'Request cancelled.',
  continued:'Request handed to AutoFirma.', failed:'This request has ended. Open the application or start a new request on the website.',
  permissionDenied:'Permission was not granted. You can try again.', origin:'Website origin', disableSite:'Disable site', restore:'Re-enable',
  sitesHint:'AutoFirme does not intercept any website until you authorize it. Enter the website origin and select Authorize site; reload the site after granting. Every signature still requires your confirmation.',
  authorizeSite:'Authorize site', grantedSites:'Authorized sites', revokeSite:'Remove access', disabledSites:'Disabled sites',
  allSites:'Intercept all websites', allSitesHint:'Grants AutoFirme access to every HTTP and HTTPS website without authorizing them one by one. Recommended only in trusted environments. Turn it off to return to per-site authorization.',
  enableAllSites:'Enable all websites', disableAllSites:'Disable all websites',
  tsa:'Timestamp network permission', tsaHint:'If Chrome restricts website access, authorize your TSA origin to use timestamping.',
  tsaUrl:'TSA URL', grantTsa:'Authorize TSA', granted:'Permission granted.', close:'Close', invalid:'The certificate is invalid.', expired:'The certificate has expired.',
  notYetValid:'The certificate is not yet valid.', privateKey:'Unlock a certificate with its private key.', rsa:'An RSA certificate of at least 2048 bits with SHA-256 is required.',
  ca:'Choose a personal signing certificate, not a certificate authority.', keyUsage:'The certificate does not permit document signing.'
 }
};
