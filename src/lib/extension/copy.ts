import type { Lang } from '#lib/i18n';
export const extensionCopy = {
 es: {
  requestDetails:'Detalles de la solicitud', certDetails:'Detalles del certificado', copyDigest:'Copiar SHA256', digestCopied:'SHA256 copiado', copyFailed:'No se pudo copiar el SHA256. Puedes consultarlo en los detalles de la solicitud.',
  request:'Solicitud de certificado', sign:'Firmar documento', select:'Compartir certificado público', server:'servidor',
  requestedBy:'Sitio solicitante', digest:'Resumen SHA-256', noDigest:'No se solicita firmar un documento.',
  consent:'Selecciona y desbloquea un certificado. Solo el botón de confirmación autoriza esta solicitud.',
  confirm:'Confirmar y firmar', confirmSelect:'Compartir certificado', cancel:'Cancelar', fallback:'Continuar con AutoFirma',
  fullTab:'Abrir aplicación completa', access:'Acceso a sitios', loading:'Cargando solicitud…', signing:'Procesando…',
  done:'Solicitud completada. Puedes cerrar esta ventana.', cancelled:'Solicitud cancelada.', continued:'Solicitud enviada a AutoFirma.',
  failed:'La solicitud ha terminado. Abre la aplicación o inicia una solicitud nueva en el sitio.',
  permissionDenied:'Permiso no concedido. Puedes volver a intentarlo.', origin:'Origen del sitio', disableSite:'Deshabilitar sitio', restore:'Volver a habilitar',
  disabledSites:'Sitios deshabilitados',
  grantOrigin:'Origen a conceder',
  sitesHint:'AutoFirme intercepta todos los sitios HTTP y HTTPS. Escribe el origen del sitio que quieras excluir y pulsa Deshabilitar sitio; recarga el sitio tras deshabilitarlo. Cada firma requiere tu confirmación.',
  reenableHint:'Si Chrome retiró el acceso a un origen, vuelve a concederlo aquí antes de firmar en ese sitio.',
  grantSite:'Conceder acceso',
  interceptionFailed:'Chrome no ha podido activar la interceptación de sitios. Recarga la extensión y vuelve a intentarlo.',
  tsa:'Permiso de red para sellado de tiempo', tsaHint:'Si Chrome restringe el acceso a sitios, autoriza el origen de tu TSA para usar el sellado de tiempo.',
  tsaUrl:'URL de TSA', grantTsa:'Autorizar TSA', granted:'Permiso concedido.', close:'Cerrar',
  invalid:'El certificado no es válido.', expired:'El certificado ha caducado.', notYetValid:'El certificado aún no es válido.',
  privateKey:'Desbloquea un certificado con clave privada.', rsa:'Se requiere un certificado RSA de al menos 2048 bits con SHA-256.',
  ca:'Selecciona un certificado de firma personal, no una autoridad certificadora.', keyUsage:'El certificado no permite firmar documentos.',
  mcpOrigin:'Agente MCP',
  mcpWrongCertificate:'El agente MCP solicitó otra identidad. Vuelve a seleccionar la identidad indicada.'
 },
 en: {
  requestDetails:'Request details', certDetails:'Certificate details', copyDigest:'Copy SHA256', digestCopied:'SHA256 copied', copyFailed:'Could not copy SHA256. You can find it in the request details.',
  request:'Certificate request', sign:'Sign document', select:'Share public certificate', server:'server', requestedBy:'Requesting website', digest:'SHA-256 digest',
  noDigest:'No document signature is requested.', consent:'Select and unlock a certificate. Only the confirmation button authorizes this request.',
  confirm:'Confirm and sign', confirmSelect:'Share certificate', cancel:'Cancel', fallback:'Continue with AutoFirma', fullTab:'Open full application',
  access:'Site access', loading:'Loading request…', signing:'Processing…', done:'Request completed. You can close this window.', cancelled:'Request cancelled.',
  continued:'Request handed to AutoFirma.', failed:'This request has ended. Open the application or start a new request on the website.',
  permissionDenied:'Permission was not granted. You can try again.', origin:'Website origin', disableSite:'Disable site', restore:'Re-enable',
  disabledSites:'Disabled sites',
  grantOrigin:'Origin to grant',
  sitesHint:'AutoFirme intercepts every HTTP and HTTPS website. Enter the origin of a site you want to exclude and select Disable site; reload the site after disabling it. Every signature still requires your confirmation.',
  reenableHint:'If Chrome revoked access to an origin, grant it again here before signing on that site.',
  grantSite:'Grant access',
  interceptionFailed:'Chrome could not enable website interception. Reload the extension and try again.',
  tsa:'Timestamp network permission', tsaHint:'If Chrome restricts website access, authorize your TSA origin to use timestamping.',
  tsaUrl:'TSA URL', grantTsa:'Authorize TSA', granted:'Permission granted.', close:'Close', invalid:'The certificate is invalid.', expired:'The certificate has expired.',
  notYetValid:'The certificate is not yet valid.', privateKey:'Unlock a certificate with its private key.', rsa:'An RSA certificate of at least 2048 bits with SHA-256 is required.',
  ca:'Choose a personal signing certificate, not a certificate authority.', keyUsage:'The certificate does not permit document signing.',
  mcpOrigin:'MCP agent',
  mcpWrongCertificate:'The MCP agent requested a different identity. Select the requested identity again.'
 }
};
// Worker and runtime errors arrive as fixed English sentences; translate the known ones and pass anything else through.
const errorMessages: Record<string, [string, string]> = {
 'The requesting document has changed': ['El documento de la página solicitante ha cambiado.', 'The requesting document has changed.'],
 'Request state does not permit this action': ['El estado de la solicitud no permite esta acción.', 'Request state does not permit this action.'],
 'Website permission was removed or the document has changed': ['Se retiró el permiso del sitio o el documento ha cambiado.', 'Website permission was removed or the document has changed.'],
 'Use an HTTP or HTTPS origin': ['Usa un origen HTTP o HTTPS.', 'Use an HTTP or HTTPS origin.']
};
export function errorCopy(lang: Lang, message: string): string {
 const pair = errorMessages[message];
 return pair ? pair[lang === 'es' ? 0 : 1] : message;
}
