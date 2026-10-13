package es.gob.afirma.standalone.protocol;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.AOException;
import es.gob.afirma.core.keystores.CertificateContext;
import es.gob.afirma.core.keystores.LockedKeyStoreException;
import es.gob.afirma.core.keystores.PinException;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.misc.http.HttpError;
import es.gob.afirma.core.misc.protocol.ParameterException;
import es.gob.afirma.core.misc.protocol.UrlParametersForBatch;
import es.gob.afirma.core.prefs.KeyStorePreferencesManager;
import es.gob.afirma.keystores.AOCertificatesNotFoundException;
import es.gob.afirma.keystores.AOKeyStore;
import es.gob.afirma.keystores.AOKeyStoreDialog;
import es.gob.afirma.keystores.AOKeyStoreManagerFactory;
import es.gob.afirma.keystores.AggregatedKeyStoreManager;
import es.gob.afirma.keystores.filters.CertFilterManager;
import es.gob.afirma.keystores.filters.EncodedCertificateFilter;
import es.gob.afirma.signers.batch.client.BatchSigner;
import es.gob.afirma.standalone.SimpleAfirma;
import es.gob.afirma.standalone.SimpleKeyStoreManager;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import es.gob.afirma.standalone.crypto.CypherDataManager;
import es.gob.afirma.standalone.so.macos.MacUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.CertificateEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/ProtocolInvocationLauncherBatch.class */
final class ProtocolInvocationLauncherBatch {
    private static final char RESULT_SEPARATOR = '|';
    private static final String RESULT_CANCEL = "CANCEL";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private ProtocolInvocationLauncherBatch() {
    }

    static String processBatch(UrlParametersForBatch urlParametersForBatch, int i, boolean z) throws SocketOperationException {
        if (!ProtocolInvocationLauncher.MAX_PROTOCOL_VERSION_SUPPORTED.support(i)) {
            LOGGER.severe(String.format("Version de protocolo no soportada (%1s). Version actual: %s2. Hay que actualizar la aplicacion.", Integer.valueOf(i), Integer.valueOf(ProtocolInvocationLauncher.MAX_PROTOCOL_VERSION_SUPPORTED.getVersion())));
            ProtocolInvocationLauncherErrorManager.showError("SAF_21");
            if (!z) {
                throw new SocketOperationException("SAF_21");
            }
            return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_21");
        }
        if (urlParametersForBatch.getMinimumClientVersion() != null && new Version(urlParametersForBatch.getMinimumClientVersion()).greaterThan(SimpleAfirma.getVersion())) {
            ProtocolInvocationLauncherErrorManager.showError("SAF_41");
            if (!z) {
                throw new SocketOperationException("SAF_41");
            }
            return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_41");
        }
        String lastSelectedKeystore = KeyStorePreferencesManager.getLastSelectedKeystore();
        boolean z2 = PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_USE_DEFAULT_STORE_IN_BROWSER_CALLS);
        AOKeyStore keyStore = null;
        if (lastSelectedKeystore != null && !lastSelectedKeystore.isEmpty()) {
            keyStore = SimpleKeyStoreManager.getLastSelectedKeystore();
        } else if (z2) {
            String str = PreferencesManager.get(PreferencesManager.PREFERENCE_KEYSTORE_DEFAULT_STORE);
            if (!PreferencesManager.VALUE_KEYSTORE_DEFAULT.equals(str)) {
                keyStore = SimpleKeyStoreManager.getKeyStore(str);
            }
        } else {
            keyStore = SimpleKeyStoreManager.getKeyStore(urlParametersForBatch.getDefaultKeyStore());
        }
        if (keyStore == null) {
            keyStore = AOKeyStore.getDefaultKeyStoreTypeByOs(Platform.getOS());
        }
        try {
            SignOperationResult signOperationResultSign = sign(urlParametersForBatch, keyStore, z2, new CertFilterManager(urlParametersForBatch.getExtraParams()));
            StringBuilder sb = new StringBuilder();
            byte[] encoded = null;
            if (urlParametersForBatch.isCertNeeded()) {
                try {
                    encoded = signOperationResultSign.getPke().getCertificate().getEncoded();
                } catch (CertificateEncodingException e) {
                    LOGGER.log(Level.SEVERE, "No se ha podido codificar el certificado de firma para su devolucion", (Throwable) e);
                    ProtocolInvocationLauncherErrorManager.showError("SAF_18", e);
                    if (!z) {
                        throw new SocketOperationException("SAF_18");
                    }
                    return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_18");
                }
            }
            if (urlParametersForBatch.getDesKey() != null) {
                try {
                    sb.append(CypherDataManager.cipherData(signOperationResultSign.getResult(), urlParametersForBatch.getDesKey()));
                    if (encoded != null) {
                        sb.append('|').append(CypherDataManager.cipherData(encoded, urlParametersForBatch.getDesKey()));
                    }
                } catch (Exception e2) {
                    LOGGER.severe("Error en el cifrado de los datos a enviar: " + e2);
                    ProtocolInvocationLauncherErrorManager.showError("SAF_12", e2);
                    if (!z) {
                        throw new SocketOperationException("SAF_12");
                    }
                    return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_12");
                }
            } else {
                LOGGER.warning("Se omite el cifrado de los datos resultantes por no haberse proporcionado una clave de cifrado");
                sb.append(Base64.encode(signOperationResultSign.getResult()));
                if (encoded != null) {
                    sb.append('|').append(Base64.encode(encoded));
                }
            }
            if (urlParametersForBatch.getStorageServletUrl() != null) {
                Thread activeWaitingThread = ProtocolInvocationLauncher.getActiveWaitingThread();
                if (activeWaitingThread != null) {
                    activeWaitingThread.interrupt();
                }
                synchronized (IntermediateServerUtil.getUniqueSemaphoreInstance()) {
                    try {
                        LOGGER.info("Enviamos el resultado de la operacion de firma de lote al servidor intermedio");
                        IntermediateServerUtil.sendData(sb, urlParametersForBatch.getStorageServletUrl().toString(), urlParametersForBatch.getId());
                    } catch (Exception e3) {
                        LOGGER.log(Level.SEVERE, "Error al enviar los datos al servidor", (Throwable) e3);
                        ProtocolInvocationLauncherErrorManager.showError("SAF_11", e3);
                        if (!z) {
                            throw new SocketOperationException("SAF_11");
                        }
                        return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_11");
                    }
                }
            } else {
                LOGGER.info("Se omite el envio por red de los datos resultantes por no haberse proporcionado una URL de destino");
            }
            return sb.toString();
        } catch (AOCancelledOperationException e4) {
            if (!z) {
                throw new SocketOperationException("CANCEL");
            }
            return "CANCEL";
        } catch (SocketOperationException e5) {
            if (!z) {
                throw e5;
            }
            return ProtocolInvocationLauncherErrorManager.getErrorMessage(e5.getErrorCode());
        }
    }

    private static SignOperationResult sign(UrlParametersForBatch urlParametersForBatch, AOKeyStore aOKeyStore, boolean z, CertFilterManager certFilterManager) throws AOCancelledOperationException, SocketOperationException {
        String defaultKeyStoreLib;
        KeyStore.PrivateKeyEntry keyEntry;
        String str;
        List listSingletonList;
        if (urlParametersForBatch.getSticky() && !urlParametersForBatch.getResetSticky() && ProtocolInvocationLauncher.getStickyKeyEntry() != null) {
            LOGGER.info("Se usa Sticky Signature y tenemos valor de clave privada");
            keyEntry = ProtocolInvocationLauncher.getStickyKeyEntry();
        } else {
            if (z && (AOKeyStore.PKCS12.equals(aOKeyStore) || AOKeyStore.PKCS11.equals(aOKeyStore))) {
                defaultKeyStoreLib = PreferencesManager.get(PreferencesManager.PREFERENCE_LOCAL_KEYSTORE_PATH);
            } else {
                defaultKeyStoreLib = urlParametersForBatch.getDefaultKeyStoreLib();
            }
            try {
                AggregatedKeyStoreManager aOKeyStoreManager = AOKeyStoreManagerFactory.getAOKeyStoreManager(aOKeyStore, defaultKeyStoreLib, null, aOKeyStore.getStorePasswordCallback(null), null);
                try {
                    if (Platform.OS.MACOSX.equals(Platform.getOS())) {
                        MacUtils.focusApplication();
                    }
                    String name = null;
                    if (defaultKeyStoreLib != null) {
                        name = new File(defaultKeyStoreLib).getName();
                    }
                    AOKeyStoreDialog aOKeyStoreDialog = new AOKeyStoreDialog(aOKeyStoreManager, null, true, true, true, certFilterManager.getFilters(), certFilterManager.isMandatoryCertificate(), name);
                    aOKeyStoreDialog.allowOpenExternalStores(certFilterManager.isExternalStoresOpeningAllowed());
                    aOKeyStoreDialog.show();
                    CertificateContext selectedCertificateContext = aOKeyStoreDialog.getSelectedCertificateContext();
                    keyEntry = selectedCertificateContext.getKeyStoreManager().getKeyEntry(selectedCertificateContext.getAlias());
                    if (urlParametersForBatch.getSticky()) {
                        ProtocolInvocationLauncher.setStickyKeyEntry(keyEntry);
                    } else {
                        ProtocolInvocationLauncher.setStickyKeyEntry(null);
                    }
                } catch (AOCancelledOperationException e) {
                    LOGGER.info("Operacion cancelada por el usuario: " + e);
                    throw e;
                } catch (AOCertificatesNotFoundException e2) {
                    LOGGER.severe("No hay certificados validos en el almacen: " + e2);
                    ProtocolInvocationLauncherErrorManager.showError("SAF_19", e2);
                    throw new SocketOperationException("SAF_19");
                } catch (Exception e3) {
                    LOGGER.severe("Error al mostrar el dialogo de seleccion de certificados: " + e3);
                    ProtocolInvocationLauncherErrorManager.showError("SAF_08", e3);
                    throw new SocketOperationException("SAF_08");
                }
            } catch (Exception e4) {
                LOGGER.log(Level.SEVERE, "Error obteniendo el AOKeyStoreManager", (Throwable) e4);
                ProtocolInvocationLauncherErrorManager.showError("SAF_08", e4);
                throw new SocketOperationException("SAF_08");
            }
        }
        try {
            return new SignOperationResult(signBatch(urlParametersForBatch, keyEntry), keyEntry);
        } catch (AOCancelledOperationException e5) {
            LOGGER.info("Operacion cancelada por el usuario: " + LoggerUtil.getTrimStr(e5.toString()));
            throw e5;
        } catch (LockedKeyStoreException e6) {
            LOGGER.info("El almacen de claves esta bloqueado: " + LoggerUtil.getTrimStr(e6.toString()));
            ProtocolInvocationLauncher.setStickyKeyEntry(null);
            throw new SocketOperationException("SAF_52");
        } catch (PinException e7) {
            try {
                listSingletonList = Collections.singletonList(new EncodedCertificateFilter(Base64.encode(keyEntry.getCertificate().getEncoded())));
            } catch (Exception e8) {
                listSingletonList = null;
            }
            CertFilterManager certFilterManager2 = new CertFilterManager(listSingletonList, listSingletonList != null, true);
            ProtocolInvocationLauncher.setStickyKeyEntry(null);
            return sign(urlParametersForBatch, aOKeyStore, z, certFilterManager2);
        } catch (AOException e9) {
            LOGGER.info("Error durante la firma del lote: " + e9);
            throw new SocketOperationException("SAF_27");
        } catch (HttpError e10) {
            if (e10.getResponseCode() == 400) {
                str = "SAF_03";
                LOGGER.severe("Error en los parametros enviados al servicio: " + LoggerUtil.getTrimStr(e10.toString()));
            } else if (e10.getResponseCode() / 100 == 4) {
                str = "SAF_26";
                LOGGER.log(Level.SEVERE, "Error en la comunicacion con el servicio de firma de lotes", LoggerUtil.getTrimStr(e10.toString()));
            } else {
                str = "SAF_27";
                LOGGER.log(Level.SEVERE, "Error en el servicio de firma de lotes", (Throwable) e10);
            }
            ProtocolInvocationLauncherErrorManager.showError(str, e10);
            throw new SocketOperationException(str);
        } catch (IllegalArgumentException e11) {
            LOGGER.info("Los parametros de la peticion no eran validos: " + LoggerUtil.getTrimStr(e11.toString()));
            throw new SocketOperationException("SAF_03");
        } catch (CertificateEncodingException e12) {
            LOGGER.info("Error en la codificacion del certificado: " + LoggerUtil.getTrimStr(e12.toString()));
            throw new SocketOperationException("SAF_18");
        } catch (Exception e13) {
            LOGGER.log(Level.SEVERE, "Error en el proceso del lote de firmas", (Throwable) e13);
            String str2 = urlParametersForBatch.isLocalBatchProcess() ? "SAF_20" : "SAF_27";
            ProtocolInvocationLauncherErrorManager.showError(str2, e13);
            throw new SocketOperationException(str2);
        }
    }

    private static byte[] signBatch(UrlParametersForBatch urlParametersForBatch, KeyStore.PrivateKeyEntry privateKeyEntry) throws AOCancelledOperationException, ParameterException, IOException, IllegalArgumentException, AOException, CertificateEncodingException {
        String strSignXML;
        if (urlParametersForBatch.isJsonBatch()) {
            if (urlParametersForBatch.isLocalBatchProcess()) {
                strSignXML = LocalBatchSigner.signLocalBatch(JSONBatchManager.parseBatchConfig(urlParametersForBatch.getData()), privateKeyEntry);
            } else {
                strSignXML = BatchSigner.signJSON(Base64.encode(urlParametersForBatch.getData(), true), urlParametersForBatch.getBatchPresignerUrl(), urlParametersForBatch.getBatchPostSignerUrl(), privateKeyEntry.getCertificateChain(), privateKeyEntry.getPrivateKey(), urlParametersForBatch.getExtraParams());
            }
        } else {
            strSignXML = BatchSigner.signXML(Base64.encode(urlParametersForBatch.getData(), true), urlParametersForBatch.getBatchPresignerUrl(), urlParametersForBatch.getBatchPostSignerUrl(), privateKeyEntry.getCertificateChain(), privateKeyEntry.getPrivateKey(), urlParametersForBatch.getExtraParams());
        }
        return strSignXML.getBytes(StandardCharsets.UTF_8);
    }

    public static String getResultCancel() {
        return "CANCEL";
    }
}
