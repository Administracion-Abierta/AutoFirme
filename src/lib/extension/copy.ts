export const extensionCopy = {
 es: {
  request:'Solicitud de certificado', sign:'Firmar documento', select:'Compartir certificado público',
  requestedBy:'Sitio solicitante', digest:'Resumen SHA-256', noDigest:'No se solicita firmar un documento.',
  consent:'Selecciona y desbloquea un certificado. Solo el botón de confirmación autoriza esta solicitud.',
  confirm:'Confirmar y firmar', confirmSelect:'Compartir certificado', cancel:'Cancelar', fallback:'Continuar con AutoFirma',
  fullTab:'Abrir aplicación completa', access:'Acceso a sitios', loading:'Cargando solicitud…', signing:'Procesando…',
  done:'Solicitud completada. Puedes cerrar esta ventana.', cancelled:'Solicitud cancelada.', continued:'Solicitud enviada a AutoFirma.',
  failed:'La solicitud ha terminado. Abre la aplicación o inicia una solicitud nueva en el sitio.',
  permissionDenied:'Permiso no concedido. Puedes volver a intentarlo.', origin:'Origen del sitio', enable:'Habilitar sitio', revoke:'Deshabilitar',
  sitesHint:'Solo estos sitios pueden solicitar interceptación de AutoFirma. El permiso se solicita al habilitar cada origen.',
  noSites:'No hay sitios habilitados.', tsa:'Permiso de red para sellado de tiempo', tsaHint:'Autoriza únicamente el origen de tu TSA. Este permiso no habilita solicitudes de firma del sitio.',
  tsaUrl:'URL de TSA', grantTsa:'Autorizar TSA', granted:'Permiso concedido.', close:'Cerrar',
  invalid:'El certificado no es válido.', expired:'El certificado ha caducado.', notYetValid:'El certificado aún no es válido.',
  privateKey:'Desbloquea un certificado con clave privada.', rsa:'Se requiere un certificado RSA de al menos 2048 bits con SHA-256.',
  ca:'Selecciona un certificado de firma personal, no una autoridad certificadora.', keyUsage:'El certificado no permite firmar documentos.'
 },
 en: {
  request:'Certificate request', sign:'Sign document', select:'Share public certificate', requestedBy:'Requesting website', digest:'SHA-256 digest',
  noDigest:'No document signature is requested.', consent:'Select and unlock a certificate. Only the confirmation button authorizes this request.',
  confirm:'Confirm and sign', confirmSelect:'Share certificate', cancel:'Cancel', fallback:'Continue with AutoFirma', fullTab:'Open full application',
  access:'Site access', loading:'Loading request…', signing:'Processing…', done:'Request completed. You can close this window.', cancelled:'Request cancelled.',
  continued:'Request handed to AutoFirma.', failed:'This request has ended. Open the application or start a new request on the website.',
  permissionDenied:'Permission was not granted. You can try again.', origin:'Website origin', enable:'Enable site', revoke:'Disable',
  sitesHint:'Only these websites can request AutoFirma interception. Permission is requested when you enable each origin.', noSites:'No sites enabled.',
  tsa:'Timestamp network permission', tsaHint:'Authorize only your TSA origin. This permission does not enable website signing requests.',
  tsaUrl:'TSA URL', grantTsa:'Authorize TSA', granted:'Permission granted.', close:'Close', invalid:'The certificate is invalid.', expired:'The certificate has expired.',
  notYetValid:'The certificate is not yet valid.', privateKey:'Unlock a certificate with its private key.', rsa:'An RSA certificate of at least 2048 bits with SHA-256 is required.',
  ca:'Choose a personal signing certificate, not a certificate authority.', keyUsage:'The certificate does not permit document signing.'
 }
};
