package es.gob.afirma.standalone.protocol;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.misc.protocol.ParameterException;
import es.gob.afirma.core.misc.protocol.ParameterLocalAccessRequestedException;
import es.gob.afirma.core.misc.protocol.ParameterNeedsUpdatedVersionException;
import es.gob.afirma.core.misc.protocol.ProtocolInvocationUriParser;
import es.gob.afirma.core.misc.protocol.ProtocolInvocationUriParserUtil;
import es.gob.afirma.core.misc.protocol.ProtocolVersion;
import es.gob.afirma.core.misc.protocol.UrlParametersForBatch;
import es.gob.afirma.core.misc.protocol.UrlParametersToLoad;
import es.gob.afirma.core.misc.protocol.UrlParametersToSave;
import es.gob.afirma.core.misc.protocol.UrlParametersToSelectCert;
import es.gob.afirma.core.misc.protocol.UrlParametersToSign;
import es.gob.afirma.core.misc.protocol.UrlParametersToSignAndSave;
import es.gob.afirma.signers.batch.client.TriphaseDataParser;
import es.gob.afirma.standalone.JMulticardUtilities;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import es.gob.afirma.standalone.ui.AboutDialog;
import es.gob.afirma.standalone.ui.OSXHandler;
import java.awt.Component;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/ProtocolInvocationLauncher.class */
public final class ProtocolInvocationLauncher {
    private static final String RESULT_OK = "OK";
    private static final int MIN_JAVASCRIPT_VERSION_CODE_NEEDED = 1;
    private static final int DEFAULT_JAVASCRIPT_VERSION_CODE = 1;
    private static final String IDSESSION_PARAM = "idsession";
    private static final String PORTS_PARAM = "ports";
    private static final String PROTOCOL_VERSION_PARAM = "v";
    private static final String JAVASCRIPT_VERSION_CODE_PARAM = "jvc";
    static final String MIN_REQUESTED_VERSION_PARAM = "mcv";
    private static final int DEFAULT_WEBSOCKET_PORT = 63117;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    static final ProtocolVersion MAX_PROTOCOL_VERSION_SUPPORTED = ProtocolVersion.VERSION_4;
    private static KeyStore.PrivateKeyEntry stickyKeyEntry = null;
    private static Thread activeWaitingThread = null;
    private static int requestedProtocolVersion = -1;

    public static KeyStore.PrivateKeyEntry getStickyKeyEntry() {
        return stickyKeyEntry;
    }

    public static void setStickyKeyEntry(KeyStore.PrivateKeyEntry privateKeyEntry) {
        stickyKeyEntry = privateKeyEntry;
    }

    void showAbout(EventObject eventObject) {
        AboutDialog.showAbout(null);
    }

    public static String launch(String str) {
        return launch(str, -1, false);
    }

    public static String launch(String str, int i, boolean z) {
        Map<String, String> xml;
        String strEncode;
        String strEncode2;
        String strEncode3;
        String errorMessage;
        String errorMessage2;
        String strEncode4;
        if (Platform.OS.MACOSX.equals(Platform.getOS())) {
            try {
                OSXHandler.setAboutHandler(null, ProtocolInvocationLauncher.class.getDeclaredMethod("showAbout", EventObject.class));
            } catch (Exception e) {
                LOGGER.warning("No ha sido posible establecer el menu 'Acerca de...' de OS X: " + e);
            }
        }
        if (str == null) {
            LOGGER.severe("No se ha proporcionado una URL para la invocacion");
            ProtocolInvocationLauncherErrorManager.showError("SAF_01");
            return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_01");
        }
        if (!str.startsWith("afirma://")) {
            LOGGER.severe("La URL de invocacion no comienza por 'afirma://'");
            ProtocolInvocationLauncherErrorManager.showError("SAF_02");
            return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_02");
        }
        JMulticardUtilities.configureJMulticard(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_ENABLED_JMULTICARD));
        requestedProtocolVersion = i;
        Map<String, String> mapExtractParams = extractParams(str);
        int i2 = 1;
        if (mapExtractParams.containsKey(JAVASCRIPT_VERSION_CODE_PARAM)) {
            try {
                i2 = Integer.parseInt(mapExtractParams.get(JAVASCRIPT_VERSION_CODE_PARAM));
            } catch (Exception e2) {
                i2 = 1;
            }
        }
        if (i2 < 1) {
            JOptionPane.showMessageDialog((Component) null, ProtocolMessages.getString("ProtocolLauncher.51"), ProtocolMessages.getString("ProtocolLauncher.52"), 2);
        }
        if (str.startsWith("afirma://websocket?") || str.startsWith("afirma://websocket/?")) {
            LOGGER.info("Se inicia el modo de comunicacion por websockets: " + str);
            requestedProtocolVersion = getVersion(mapExtractParams);
            ChannelInfo channelInfo = getChannelInfo(mapExtractParams);
            if (channelInfo.getPorts() == null) {
                LOGGER.severe("Usando puerto por defecto para la comunicacion WebSocket");
                channelInfo.setPorts(new int[]{DEFAULT_WEBSOCKET_PORT});
            }
            try {
                AfirmaWebSocketServerManager.startService(channelInfo, requestedProtocolVersion);
                return RESULT_OK;
            } catch (SocketOperationException e3) {
                LOGGER.log(Level.SEVERE, "No se pudo abrir ninguno de los puertos proporcionados", (Throwable) e3);
                ProtocolInvocationLauncherErrorManager.showError("SAF_45", e3);
                forceCloseApplication(0);
                return RESULT_OK;
            } catch (UnsupportedProtocolException e4) {
                LOGGER.severe("La version del protocolo no esta soportada (" + e4.getVersion() + "): " + e4);
                ProtocolInvocationLauncherErrorManager.showError("SAF_21", e4);
                forceCloseApplication(0);
                return RESULT_OK;
            }
        }
        if (str.startsWith("afirma://service?") || str.startsWith("afirma://service/?")) {
            LOGGER.info("Se inicia el modo de comunicacion por sockets: " + str);
            requestedProtocolVersion = getVersion(mapExtractParams);
            ChannelInfo channelInfo2 = getChannelInfo(mapExtractParams);
            if (channelInfo2.getPorts() == null) {
                LOGGER.log(Level.SEVERE, "No se ha proporcionado el listado de puertos para la conexion");
                ProtocolInvocationLauncherErrorManager.showError("SAF_03");
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_03");
            }
            try {
                ServiceInvocationManager.startService(channelInfo2, requestedProtocolVersion);
                return RESULT_OK;
            } catch (UnsupportedProtocolException e5) {
                LOGGER.severe("La version del protocolo no esta soportada (" + e5.getVersion() + "): " + e5);
                String str2 = e5.isNewVersionNeeded() ? "SAF_21" : "SAF_21";
                ProtocolInvocationLauncherErrorManager.showError(str2, e5);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage(str2);
            }
        }
        if (str.startsWith("afirma://batch?") || str.startsWith("afirma://batch/?")) {
            LOGGER.info("Se invoca a la aplicacion para el procesado de un lote de firma");
            try {
                UrlParametersForBatch parametersToBatch = ProtocolInvocationUriParserUtil.getParametersToBatch(mapExtractParams, !z);
                if (requestedProtocolVersion == -1) {
                    requestedProtocolVersion = parseProtocolVersion(parametersToBatch.getMinimumProtocolVersion());
                }
                if (parametersToBatch.getFileId() != null) {
                    try {
                        byte[] dataFromRetrieveServlet = ProtocolInvocationLauncherUtil.getDataFromRetrieveServlet(parametersToBatch);
                        if (parametersToBatch.isJsonBatch()) {
                            xml = TriphaseDataParser.parseParamsListJson(dataFromRetrieveServlet);
                        } else {
                            xml = ProtocolInvocationUriParserUtil.parseXml(dataFromRetrieveServlet);
                        }
                        parametersToBatch = ProtocolInvocationUriParserUtil.getParametersToBatch(xml, !z);
                    } catch (ProtocolInvocationLauncherUtil.DecryptionException e6) {
                        LOGGER.severe("Error al descifrar los datos obtenidos: " + e6);
                        ProtocolInvocationLauncherErrorManager.showError("SAF_15", e6);
                        return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_15");
                    } catch (ProtocolInvocationLauncherUtil.InvalidEncryptedDataLengthException e7) {
                        LOGGER.log(Level.SEVERE, "No se pueden recuperar los datos del servidor: " + e7, (Throwable) e7);
                        ProtocolInvocationLauncherErrorManager.showError("SAF_16", e7);
                        return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_16");
                    }
                }
                if (!z && parametersToBatch.isActiveWaiting()) {
                    requestWait(parametersToBatch.getStorageServletUrl(), parametersToBatch.getId());
                }
                LOGGER.info("Se inicia la operacion de firma de lote. Version de protocolo: " + requestedProtocolVersion);
                try {
                    return ProtocolInvocationLauncherBatch.processBatch(parametersToBatch, requestedProtocolVersion, z);
                } catch (SocketOperationException e8) {
                    LOGGER.severe("Error durante la operacion de firma por lotes: " + e8);
                    if (e8.getErrorCode() == ProtocolInvocationLauncherBatch.getResultCancel()) {
                        strEncode = e8.getErrorCode();
                    } else {
                        strEncode = URLEncoder.encode(ProtocolInvocationLauncherErrorManager.getErrorMessage(e8.getErrorCode()), StandardCharsets.UTF_8.toString());
                    }
                    sendDataToServer(strEncode, parametersToBatch.getStorageServletUrl().toString(), parametersToBatch.getId());
                    return ProtocolInvocationLauncherErrorManager.getErrorMessage(e8.getErrorCode());
                }
            } catch (ParameterException e9) {
                LOGGER.log(Level.SEVERE, "Error en los parametros de firma por lote: " + e9, (Throwable) e9);
                ProtocolInvocationLauncherErrorManager.showErrorDetail("SAF_03", e9);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_03");
            } catch (Exception e10) {
                LOGGER.log(Level.SEVERE, "Error en los parametros de firma por lote: " + e10, (Throwable) e10);
                ProtocolInvocationLauncherErrorManager.showError("SAF_03", e10);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_03");
            }
        }
        if (str.startsWith("afirma://selectcert?") || str.startsWith("afirma://selectcert/?")) {
            LOGGER.info("Se invoca a la aplicacion para la seleccion de un certificado");
            try {
                UrlParametersToSelectCert parametersToSelectCert = ProtocolInvocationUriParserUtil.getParametersToSelectCert(mapExtractParams, !z);
                if (requestedProtocolVersion == -1) {
                    requestedProtocolVersion = parseProtocolVersion(parametersToSelectCert.getMinimumProtocolVersion());
                }
                if (parametersToSelectCert.getFileId() != null) {
                    try {
                        parametersToSelectCert = ProtocolInvocationUriParser.getParametersToSelectCert(ProtocolInvocationLauncherUtil.getDataFromRetrieveServlet(parametersToSelectCert), true);
                    } catch (ProtocolInvocationLauncherUtil.DecryptionException e11) {
                        LOGGER.severe("Error al descifrar: " + e11);
                        ProtocolInvocationLauncherErrorManager.showError("SAF_15", e11);
                        return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_15");
                    } catch (ProtocolInvocationLauncherUtil.InvalidEncryptedDataLengthException e12) {
                        LOGGER.log(Level.SEVERE, "No se pueden recuperar los datos del servidor: " + e12, (Throwable) e12);
                        ProtocolInvocationLauncherErrorManager.showError("SAF_16", e12);
                        return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_16");
                    }
                }
                if (!z && parametersToSelectCert.isActiveWaiting()) {
                    requestWait(parametersToSelectCert.getStorageServletUrl(), parametersToSelectCert.getId());
                }
                LOGGER.info("Se inicia la operacion de seleccion de certificado. Version de protocolo: " + requestedProtocolVersion);
                try {
                    return ProtocolInvocationLauncherSelectCert.processSelectCert(parametersToSelectCert, requestedProtocolVersion, z);
                } catch (AOCancelledOperationException e13) {
                    return ProtocolInvocationLauncherSelectCert.getResultCancel();
                } catch (SocketOperationException e14) {
                    LOGGER.severe("Error durante la operacion de seleccion de certificados: " + e14);
                    if (e14.getErrorCode() == ProtocolInvocationLauncherSelectCert.getResultCancel()) {
                        strEncode2 = e14.getErrorCode();
                    } else {
                        strEncode2 = URLEncoder.encode(ProtocolInvocationLauncherErrorManager.getErrorMessage(e14.getErrorCode()), StandardCharsets.UTF_8.toString());
                    }
                    sendDataToServer(strEncode2, parametersToSelectCert.getStorageServletUrl().toString(), parametersToSelectCert.getId());
                    return ProtocolInvocationLauncherErrorManager.getErrorMessage(e14.getErrorCode());
                }
            } catch (ParameterException e15) {
                LOGGER.log(Level.SEVERE, "Error en los parametros de seleccion de certificados: " + e15, (Throwable) e15);
                ProtocolInvocationLauncherErrorManager.showErrorDetail("SAF_03", e15);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_03");
            } catch (Exception e16) {
                LOGGER.log(Level.SEVERE, "Error en los parametros de seleccion de certificados: " + e16, (Throwable) e16);
                ProtocolInvocationLauncherErrorManager.showError("SAF_03", e16);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_03");
            }
        }
        if (str.startsWith("afirma://save?") || str.startsWith("afirma://save/?")) {
            LOGGER.info("Se invoca a la aplicacion para el guardado de datos");
            try {
                UrlParametersToSave parametersToSave = ProtocolInvocationUriParserUtil.getParametersToSave(mapExtractParams, !z);
                if (requestedProtocolVersion == -1) {
                    requestedProtocolVersion = parseProtocolVersion(parametersToSave.getMinimumProtocolVersion());
                }
                LOGGER.info("Cantidad de datos a guardar: " + (parametersToSave.getData() == null ? 0 : parametersToSave.getData().length));
                if (parametersToSave.getFileId() != null) {
                    try {
                        parametersToSave = ProtocolInvocationUriParser.getParametersToSave(ProtocolInvocationLauncherUtil.getDataFromRetrieveServlet(parametersToSave), true);
                    } catch (ProtocolInvocationLauncherUtil.DecryptionException e17) {
                        LOGGER.severe("Error al descifrar: " + e17);
                        ProtocolInvocationLauncherErrorManager.showError("SAF_15", e17);
                        return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_15");
                    } catch (ProtocolInvocationLauncherUtil.InvalidEncryptedDataLengthException e18) {
                        LOGGER.log(Level.SEVERE, "No se pueden recuperar los datos del servidor: " + e18, (Throwable) e18);
                        ProtocolInvocationLauncherErrorManager.showError("SAF_16", e18);
                        return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_16");
                    }
                }
                if (!z && parametersToSave.isActiveWaiting()) {
                    requestWait(parametersToSave.getStorageServletUrl(), parametersToSave.getId());
                }
                LOGGER.info("Se inicia la operacion de guardado. Version de protocolo: " + requestedProtocolVersion);
                try {
                    return ProtocolInvocationLauncherSave.processSave(parametersToSave, requestedProtocolVersion, z);
                } catch (SocketOperationException e19) {
                    LOGGER.log(Level.SEVERE, "Error en la operacion de guardado: " + e19, (Throwable) e19);
                    if (e19.getErrorCode() == ProtocolInvocationLauncherSave.getResultCancel()) {
                        strEncode3 = e19.getErrorCode();
                    } else {
                        strEncode3 = URLEncoder.encode(ProtocolInvocationLauncherErrorManager.getErrorMessage(e19.getErrorCode()), StandardCharsets.UTF_8.toString());
                    }
                    sendDataToServer(strEncode3, parametersToSave.getStorageServletUrl().toString(), parametersToSave.getId());
                    return ProtocolInvocationLauncherErrorManager.getErrorMessage(e19.getErrorCode());
                }
            } catch (ParameterLocalAccessRequestedException e20) {
                LOGGER.severe("Se ha pedido un acceso a una direccion local (localhost o 127.0.0.1): " + e20);
                ProtocolInvocationLauncherErrorManager.showError("SAF_13", e20);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_13");
            } catch (ParameterNeedsUpdatedVersionException e21) {
                LOGGER.severe("Se necesita una version mas moderna de Autofirma para procesar la peticion: " + e21);
                ProtocolInvocationLauncherErrorManager.showError("SAF_14", e21);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_14");
            } catch (ParameterException e22) {
                LOGGER.log(Level.SEVERE, "Error en los parametros de guardado", (Throwable) e22);
                ProtocolInvocationLauncherErrorManager.showErrorDetail("SAF_03", e22);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_03");
            } catch (Exception e23) {
                LOGGER.log(Level.SEVERE, "Error en los parametros de guardado", (Throwable) e23);
                ProtocolInvocationLauncherErrorManager.showError("SAF_03", e23);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_03");
            }
        }
        if (str.startsWith("afirma://signandsave?") || str.startsWith("afirma://signandsave/?")) {
            LOGGER.info("Se invoca a la aplicacion para la firma/multifirma y el guardado del resultado");
            try {
                UrlParametersToSignAndSave parametersToSignAndSave = ProtocolInvocationUriParserUtil.getParametersToSignAndSave(mapExtractParams, !z);
                if (requestedProtocolVersion == -1) {
                    requestedProtocolVersion = parseProtocolVersion(parametersToSignAndSave.getMinimumProtocolVersion());
                }
                LOGGER.info("Cantidad de datos a firmar y guardar: " + (parametersToSignAndSave.getData() == null ? 0 : parametersToSignAndSave.getData().length));
                if (parametersToSignAndSave.getFileId() != null) {
                    try {
                        parametersToSignAndSave = ProtocolInvocationUriParser.getParametersToSignAndSave(ProtocolInvocationLauncherUtil.getDataFromRetrieveServlet(parametersToSignAndSave), true);
                    } catch (ProtocolInvocationLauncherUtil.DecryptionException e24) {
                        LOGGER.severe("Error al descifrar: " + e24);
                        ProtocolInvocationLauncherErrorManager.showError("SAF_15", e24);
                        return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_15");
                    } catch (ProtocolInvocationLauncherUtil.InvalidEncryptedDataLengthException e25) {
                        LOGGER.log(Level.SEVERE, "No se pueden recuperar los datos del servidor", (Throwable) e25);
                        ProtocolInvocationLauncherErrorManager.showError("SAF_16", e25);
                        return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_16");
                    }
                }
                if (!z && parametersToSignAndSave.isActiveWaiting()) {
                    requestWait(parametersToSignAndSave.getStorageServletUrl(), parametersToSignAndSave.getId());
                }
                LOGGER.info("Se inicia la operacion de firma y guardado. Version de protocolo: " + requestedProtocolVersion);
                try {
                    StringBuilder sbProcessSign = ProtocolInvocationLauncherSignAndSave.processSign(parametersToSignAndSave, requestedProtocolVersion);
                    if (!z) {
                        LOGGER.info("Enviamos el resultado de la operacion de firma y guardado al servidor intermedio");
                        sendDataToServer(sbProcessSign.toString(), parametersToSignAndSave.getStorageServletUrl().toString(), parametersToSignAndSave.getId());
                    }
                    return sbProcessSign.toString();
                } catch (SocketOperationException e26) {
                    LOGGER.severe("Error durante la operacion de firma: " + e26);
                    String errorCode = e26.getErrorCode();
                    if ("CANCEL".equals(errorCode)) {
                        errorMessage = errorCode;
                    } else if (!errorCode.equals(e26.getMessage())) {
                        errorMessage = errorCode + ": " + e26.getMessage();
                    } else {
                        errorMessage = ProtocolInvocationLauncherErrorManager.getErrorMessage(errorCode);
                    }
                    if (!z) {
                        errorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString());
                        sendDataToServer(errorMessage, parametersToSignAndSave.getStorageServletUrl().toString(), parametersToSignAndSave.getId());
                    }
                    return errorMessage;
                }
            } catch (ParameterLocalAccessRequestedException e27) {
                LOGGER.severe("Se ha pedido un acceso a una direccion local (localhost o 127.0.0.1): " + e27);
                ProtocolInvocationLauncherErrorManager.showError("SAF_13", e27);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_13");
            } catch (ParameterNeedsUpdatedVersionException e28) {
                LOGGER.severe("Se necesita una version mas moderna de Autofirma para procesar la peticion: " + e28);
                ProtocolInvocationLauncherErrorManager.showError("SAF_14", e28);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_14");
            } catch (ParameterException e29) {
                LOGGER.log(Level.SEVERE, "Error en los parametros de firma y guardado: " + e29, (Throwable) e29);
                ProtocolInvocationLauncherErrorManager.showErrorDetail("SAF_03", e29);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_03");
            } catch (Exception e30) {
                LOGGER.log(Level.SEVERE, "Error en los parametros de firma y guardado: " + e30, (Throwable) e30);
                ProtocolInvocationLauncherErrorManager.showError("SAF_03", e30);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_03");
            }
        }
        if (str.startsWith("afirma://sign?") || str.startsWith("afirma://sign/?") || str.startsWith("afirma://cosign?") || str.startsWith("afirma://cosign/?") || str.startsWith("afirma://countersign?") || str.startsWith("afirma://countersign/?")) {
            LOGGER.info("Se invoca a la aplicacion para realizar una operacion de firma/multifirma");
            try {
                UrlParametersToSign parametersToSign = ProtocolInvocationUriParserUtil.getParametersToSign(mapExtractParams, !z);
                if (requestedProtocolVersion == -1) {
                    requestedProtocolVersion = parseProtocolVersion(parametersToSign.getMinimumProtocolVersion());
                }
                if (parametersToSign.getFileId() != null) {
                    try {
                        parametersToSign = ProtocolInvocationUriParser.getParametersToSign(ProtocolInvocationLauncherUtil.getDataFromRetrieveServlet(parametersToSign), true);
                    } catch (ProtocolInvocationLauncherUtil.DecryptionException e31) {
                        LOGGER.log(Level.SEVERE, "Error al descifrar", (Throwable) e31);
                        ProtocolInvocationLauncherErrorManager.showError("SAF_15", e31);
                        return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_15");
                    } catch (ProtocolInvocationLauncherUtil.InvalidEncryptedDataLengthException | IOException e32) {
                        LOGGER.log(Level.SEVERE, "No se pueden recuperar los datos del servidor", e32);
                        ProtocolInvocationLauncherErrorManager.showError("SAF_16", e32);
                        return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_16");
                    }
                }
                if (!z && parametersToSign.isActiveWaiting()) {
                    requestWait(parametersToSign.getStorageServletUrl(), parametersToSign.getId());
                }
                LOGGER.info("Se inicia la operacion de firma. Version de protocolo: " + requestedProtocolVersion);
                try {
                    StringBuilder sbProcessSign2 = ProtocolInvocationLauncherSign.processSign(parametersToSign, requestedProtocolVersion, null);
                    if (!z) {
                        LOGGER.info("Enviamos el resultado de la operacion de firma al servidor intermedio");
                        sendDataToServer(sbProcessSign2.toString(), parametersToSign.getStorageServletUrl().toString(), parametersToSign.getId());
                    }
                    return sbProcessSign2.toString();
                } catch (SocketOperationException e33) {
                    LOGGER.severe("Error durante la operacion de firma: " + e33);
                    String errorCode2 = e33.getErrorCode();
                    if ("CANCEL".equals(errorCode2)) {
                        errorMessage2 = errorCode2;
                    } else if (!errorCode2.equals(e33.getMessage())) {
                        errorMessage2 = errorCode2 + ": " + e33.getMessage();
                    } else {
                        errorMessage2 = ProtocolInvocationLauncherErrorManager.getErrorMessage(errorCode2);
                    }
                    if (!z) {
                        errorMessage2 = URLEncoder.encode(errorMessage2, StandardCharsets.UTF_8.toString());
                        sendDataToServer(errorMessage2, parametersToSign.getStorageServletUrl().toString(), parametersToSign.getId());
                    }
                    return errorMessage2;
                }
            } catch (ParameterLocalAccessRequestedException e34) {
                LOGGER.severe("Se ha pedido un acceso a una direccion local (localhost o 127.0.0.1): " + e34);
                ProtocolInvocationLauncherErrorManager.showError("SAF_13", e34);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_13");
            } catch (ParameterNeedsUpdatedVersionException e35) {
                LOGGER.severe("Se necesita una version mas moderna de Autofirma para procesar la peticion: " + e35);
                ProtocolInvocationLauncherErrorManager.showError("SAF_14", e35);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_14");
            } catch (ParameterException e36) {
                LOGGER.log(Level.SEVERE, "Error en los parametros de firma", (Throwable) e36);
                ProtocolInvocationLauncherErrorManager.showErrorDetail("SAF_03", e36);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_03");
            } catch (Exception e37) {
                LOGGER.log(Level.SEVERE, "Error en los parametros de firma", (Throwable) e37);
                ProtocolInvocationLauncherErrorManager.showError("SAF_03", e37);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_03");
            }
        }
        if (str.startsWith("afirma://load?") || str.startsWith("afirma://load/?")) {
            LOGGER.info("Se invoca a la aplicacion para realizar una operacion de carga de uno o varios ficheros");
            try {
                UrlParametersToLoad parametersToLoad = ProtocolInvocationUriParserUtil.getParametersToLoad(mapExtractParams);
                if (requestedProtocolVersion == -1) {
                    requestedProtocolVersion = parseProtocolVersion(parametersToLoad.getMinimumProtocolVersion());
                }
                if (parametersToLoad.getFileId() != null) {
                    try {
                        parametersToLoad = ProtocolInvocationUriParser.getParametersToLoad(ProtocolInvocationLauncherUtil.getDataFromRetrieveServlet(parametersToLoad));
                    } catch (ProtocolInvocationLauncherUtil.DecryptionException e38) {
                        LOGGER.log(Level.SEVERE, "Error al descifrar", (Throwable) e38);
                        ProtocolInvocationLauncherErrorManager.showError("SAF_15", e38);
                        return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_15");
                    } catch (ProtocolInvocationLauncherUtil.InvalidEncryptedDataLengthException | IOException e39) {
                        LOGGER.log(Level.SEVERE, "No se pueden recuperar los datos del servidor", e39);
                        ProtocolInvocationLauncherErrorManager.showError("SAF_16", e39);
                        return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_16");
                    }
                }
                if (!z && parametersToLoad.isActiveWaiting()) {
                    requestWait(parametersToLoad.getStorageServletUrl(), parametersToLoad.getId());
                }
                LOGGER.info("Se inicia la operacion de carga. Version de protocolo: " + requestedProtocolVersion);
                try {
                    return ProtocolInvocationLauncherLoad.processLoad(parametersToLoad, requestedProtocolVersion, z);
                } catch (SocketOperationException e40) {
                    LOGGER.severe("La operacion indicada no esta soportada: " + e40);
                    if (e40.getErrorCode() == ProtocolInvocationLauncherLoad.getResultCancel()) {
                        strEncode4 = e40.getErrorCode();
                    } else {
                        strEncode4 = URLEncoder.encode(ProtocolInvocationLauncherErrorManager.getErrorMessage(e40.getErrorCode()), StandardCharsets.UTF_8.toString());
                    }
                    sendDataToServer(strEncode4, parametersToLoad.getStorageServletUrl().toString(), parametersToLoad.getId());
                    return ProtocolInvocationLauncherErrorManager.getErrorMessage(e40.getErrorCode());
                }
            } catch (ParameterLocalAccessRequestedException e41) {
                LOGGER.severe("Se ha pedido un acceso a una direccion local (localhost o 127.0.0.1): " + e41);
                ProtocolInvocationLauncherErrorManager.showError("SAF_13", e41);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_13");
            } catch (ParameterNeedsUpdatedVersionException e42) {
                LOGGER.severe("Se necesita una version mas moderna de Autofirma para procesar la peticion: " + e42);
                ProtocolInvocationLauncherErrorManager.showError("SAF_14", e42);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_14");
            } catch (ParameterException e43) {
                LOGGER.severe("Error en los parametros de carga: " + e43);
                ProtocolInvocationLauncherErrorManager.showErrorDetail("SAF_03", e43);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_03");
            } catch (Exception e44) {
                LOGGER.severe("Error en los parametros de carga: " + e44);
                ProtocolInvocationLauncherErrorManager.showError("SAF_03", e44);
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_03");
            }
        }
        LOGGER.severe("La operacion indicada en la URL no esta soportada: " + str.substring(0, Math.min(30, str.length())) + "...");
        ProtocolInvocationLauncherErrorManager.showError("SAF_04");
        return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_04");
    }

    private static void requestWait(URL url, String str) {
        try {
            activeWaitingThread = new ActiveWaitingThread(url.toString(), str);
            activeWaitingThread.start();
        } catch (Exception e) {
            LOGGER.warning("Se ha interrumpido la espera activa para la conexion con servidor intermedio: " + e);
        }
    }

    private static void sendDataToServer(String str, String str2, String str3) {
        Thread activeWaitingThread2 = getActiveWaitingThread();
        if (activeWaitingThread2 != null) {
            activeWaitingThread2.interrupt();
        }
        synchronized (IntermediateServerUtil.getUniqueSemaphoreInstance()) {
            try {
                IntermediateServerUtil.sendData(str, str2, str3);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error al enviar los datos al servidor intermedio: " + e, (Throwable) e);
                ProtocolInvocationLauncherErrorManager.showError("SAF_11", e);
            }
        }
    }

    public static Thread getActiveWaitingThread() {
        return activeWaitingThread;
    }

    private static int parseProtocolVersion(String str) {
        int i;
        try {
            i = Integer.parseInt(str);
        } catch (Exception e) {
            i = 1;
        }
        return i;
    }

    private static int getVersion(Map<String, String> map) {
        int i = 1;
        String str = map.get("v");
        if (str != null) {
            try {
                i = Integer.parseInt(str.trim());
            } catch (Exception e) {
                LOGGER.info("El ID de protocolo indicado no es un numero entero (" + str + "): " + e);
            }
        }
        return i;
    }

    private static Map<String, String> extractParams(String str) {
        HashMap map = new HashMap();
        for (String str2 : str.substring(str.indexOf(63) + 1).split("&")) {
            int iIndexOf = str2.indexOf(61);
            if (iIndexOf > 0) {
                try {
                    map.put(str2.substring(0, iIndexOf), URLDecoder.decode(str2.substring(iIndexOf + 1), StandardCharsets.UTF_8.toString()));
                } catch (UnsupportedEncodingException e) {
                    LOGGER.warning("No se pudo decodificar el valor del parametro '" + str2.substring(0, iIndexOf) + "': " + e);
                }
            }
        }
        return map;
    }

    private static ChannelInfo getChannelInfo(Map<String, String> map) {
        int[] iArr = null;
        String str = map.get(PORTS_PARAM);
        if (str != null) {
            String[] strArrSplit = str.split(",");
            iArr = new int[strArrSplit.length];
            for (int i = 0; i < strArrSplit.length; i++) {
                try {
                    iArr[i] = Math.abs(Integer.parseInt(strArrSplit[i]));
                } catch (Exception e) {
                    throw new IllegalArgumentException("El parametro 'ports' de la URI de invocacion contiene valores no numericos: " + e, e);
                }
            }
        }
        String str2 = map.get(IDSESSION_PARAM);
        if (str2 != null && !str2.isEmpty()) {
            LOGGER.info("Se ha recibido un id de sesion: " + str2);
            boolean z = true;
            for (char c : str2.toCharArray()) {
                if (!Character.isLetterOrDigit(c)) {
                    z = false;
                    break;
                }
            }
            if (!z) {
                LOGGER.info("No se ha proporcionado un id de sesion valido");
                str2 = null;
            }
        } else {
            LOGGER.info("No se utilizara id para la sesion");
        }
        return new ChannelInfo(str2, iArr);
    }

    public static void forceCloseApplication(int i) {
        Runtime.getRuntime().halt(i);
    }
}
