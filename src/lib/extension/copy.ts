import type { Lang } from '#lib/i18n';
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
  sitesHint:'AutoFirme detecta solicitudes en todos los sitios HTTP y HTTPS de forma predeterminada. Cada firma requiere tu confirmación. Puedes deshabilitar sitios aquí. Recarga el sitio tras volver a habilitarlo. Chrome puede limitar el acceso en sus ajustes.',
  noSites:'No hay sitios deshabilitados.', tsa:'Permiso de red para sellado de tiempo', tsaHint:'Si Chrome restringe el acceso a sitios, autoriza el origen de tu TSA para usar el sellado de tiempo.',
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
  sitesHint:'AutoFirme detects requests on all HTTP and HTTPS websites by default. Every signature requires your confirmation. You can disable sites here. Reload a site after re-enabling it. Chrome can restrict access in its settings.', noSites:'No disabled sites.',
  tsa:'Timestamp network permission', tsaHint:'If Chrome restricts website access, authorize your TSA origin to use timestamping.',
  tsaUrl:'TSA URL', grantTsa:'Authorize TSA', granted:'Permission granted.', close:'Close', invalid:'The certificate is invalid.', expired:'The certificate has expired.',
  notYetValid:'The certificate is not yet valid.', privateKey:'Unlock a certificate with its private key.', rsa:'An RSA certificate of at least 2048 bits with SHA-256 is required.',
  ca:'Choose a personal signing certificate, not a certificate authority.', keyUsage:'The certificate does not permit document signing.'
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
