package es.gob.afirma.standalone.protocol;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.keystores.CertificateContext;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.misc.protocol.UrlParametersToSelectCert;
import es.gob.afirma.core.prefs.KeyStorePreferencesManager;
import es.gob.afirma.keystores.AOCertificatesNotFoundException;
import es.gob.afirma.keystores.AOKeyStore;
import es.gob.afirma.keystores.AOKeyStoreDialog;
import es.gob.afirma.keystores.AOKeyStoreManagerFactory;
import es.gob.afirma.keystores.AggregatedKeyStoreManager;
import es.gob.afirma.keystores.CertificateFilter;
import es.gob.afirma.keystores.filters.CertFilterManager;
import es.gob.afirma.standalone.SimpleAfirma;
import es.gob.afirma.standalone.SimpleKeyStoreManager;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import es.gob.afirma.standalone.crypto.CypherDataManager;
import es.gob.afirma.standalone.so.macos.MacUtils;
import java.io.File;
import java.security.KeyStore;
import java.security.cert.CertificateEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/ProtocolInvocationLauncherSelectCert.class */
final class ProtocolInvocationLauncherSelectCert {
    private static final String RESULT_CANCEL = "CANCEL";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private ProtocolInvocationLauncherSelectCert() {
    }

    static String processSelectCert(UrlParametersToSelectCert urlParametersToSelectCert, int i, boolean z) throws SocketOperationException {
        String defaultKeyStoreLib;
        KeyStore.PrivateKeyEntry keyEntry;
        String strCipherData;
        if (urlParametersToSelectCert == null) {
            LOGGER.severe("Las opciones de firma son nulas");
            ProtocolInvocationLauncherErrorManager.showError("SAF_01");
            if (!z) {
                throw new SocketOperationException("SAF_01");
            }
            return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_01");
        }
        if (!ProtocolInvocationLauncher.MAX_PROTOCOL_VERSION_SUPPORTED.support(i)) {
            LOGGER.severe(String.format("Version de protocolo no soportada (%1s). Version actual: %s2. Hay que actualizar la aplicacion.", Integer.valueOf(i), Integer.valueOf(ProtocolInvocationLauncher.MAX_PROTOCOL_VERSION_SUPPORTED.getVersion())));
            ProtocolInvocationLauncherErrorManager.showError("SAF_21");
            if (!z) {
                throw new SocketOperationException("SAF_21");
            }
            return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_21");
        }
        if (urlParametersToSelectCert.getMinimumClientVersion() != null && new Version(urlParametersToSelectCert.getMinimumClientVersion()).greaterThan(SimpleAfirma.getVersion())) {
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
            keyStore = SimpleKeyStoreManager.getKeyStore(urlParametersToSelectCert.getDefaultKeyStore());
        }
        if (keyStore == null) {
            keyStore = AOKeyStore.getDefaultKeyStoreTypeByOs(Platform.getOS());
        }
        if (z2 && (AOKeyStore.PKCS12.equals(keyStore) || AOKeyStore.PKCS11.equals(keyStore))) {
            defaultKeyStoreLib = PreferencesManager.get(PreferencesManager.PREFERENCE_LOCAL_KEYSTORE_PATH);
        } else {
            defaultKeyStoreLib = urlParametersToSelectCert.getDefaultKeyStoreLib();
        }
        CertFilterManager certFilterManager = new CertFilterManager(urlParametersToSelectCert.getExtraParams());
        List<CertificateFilter> filters = certFilterManager.getFilters();
        boolean zIsMandatoryCertificate = certFilterManager.isMandatoryCertificate();
        if (urlParametersToSelectCert.getSticky() && !urlParametersToSelectCert.getResetSticky() && ProtocolInvocationLauncher.getStickyKeyEntry() != null) {
            LOGGER.info("Se usa Sticky Signature y tenemos valor de clave privada");
            keyEntry = ProtocolInvocationLauncher.getStickyKeyEntry();
        } else {
            try {
                AggregatedKeyStoreManager aOKeyStoreManager = AOKeyStoreManagerFactory.getAOKeyStoreManager(keyStore, defaultKeyStoreLib, null, keyStore.getStorePasswordCallback(null), null);
                LOGGER.info("Obtenido gestor de almacenes de claves: " + aOKeyStoreManager);
                try {
                    MacUtils.focusApplication();
                    String name = null;
                    if (defaultKeyStoreLib != null) {
                        name = new File(defaultKeyStoreLib).getName();
                    }
                    AOKeyStoreDialog aOKeyStoreDialog = new AOKeyStoreDialog(aOKeyStoreManager, null, true, true, true, filters, zIsMandatoryCertificate, name);
                    aOKeyStoreDialog.allowOpenExternalStores(certFilterManager.isExternalStoresOpeningAllowed());
                    aOKeyStoreDialog.show();
                    CertificateContext selectedCertificateContext = aOKeyStoreDialog.getSelectedCertificateContext();
                    keyEntry = selectedCertificateContext.getKeyStoreManager().getKeyEntry(selectedCertificateContext.getAlias());
                    if (urlParametersToSelectCert.getSticky()) {
                        ProtocolInvocationLauncher.setStickyKeyEntry(keyEntry);
                    } else {
                        ProtocolInvocationLauncher.setStickyKeyEntry(null);
                    }
                } catch (AOCancelledOperationException e) {
                    LOGGER.severe("Operacion cancelada por el usuario: " + e);
                    if (!z) {
                        throw new SocketOperationException(getResultCancel());
                    }
                    return getResultCancel();
                } catch (AOCertificatesNotFoundException e2) {
                    LOGGER.severe("No hay certificados validos en el almacen: " + e2);
                    ProtocolInvocationLauncherErrorManager.showError("SAF_19", e2);
                    if (!z) {
                        throw new SocketOperationException("SAF_19");
                    }
                    return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_19");
                } catch (Exception e3) {
                    LOGGER.severe("Error al mostrar el dialogo de seleccion de certificados: " + e3);
                    ProtocolInvocationLauncherErrorManager.showError("SAF_08", e3);
                    if (!z) {
                        throw new SocketOperationException("SAF_08");
                    }
                    return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_08");
                }
            } catch (Exception e4) {
                LOGGER.log(Level.SEVERE, "Error obteniendo el AOKeyStoreManager", (Throwable) e4);
                ProtocolInvocationLauncherErrorManager.showError("SAF_08", e4);
                if (!z) {
                    throw new SocketOperationException("SAF_08");
                }
                return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_08");
            }
        }
        try {
            byte[] encoded = keyEntry.getCertificateChain()[0].getEncoded();
            if (urlParametersToSelectCert.getDesKey() != null) {
                try {
                    strCipherData = CypherDataManager.cipherData(encoded, urlParametersToSelectCert.getDesKey());
                } catch (Exception e5) {
                    LOGGER.severe("Error en el cifrado de los datos a enviar: " + e5);
                    ProtocolInvocationLauncherErrorManager.showError("SAF_12", e5);
                    if (!z) {
                        throw new SocketOperationException("SAF_12");
                    }
                    return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_12");
                }
            } else {
                LOGGER.warning("Se omite el cifrado de los datos resultantes por no haberse proporcionado una clave de cifrado");
                strCipherData = Base64.encode(encoded, true);
            }
            if (urlParametersToSelectCert.getStorageServletUrl() != null) {
                Thread activeWaitingThread = ProtocolInvocationLauncher.getActiveWaitingThread();
                if (activeWaitingThread != null) {
                    activeWaitingThread.interrupt();
                }
                synchronized (IntermediateServerUtil.getUniqueSemaphoreInstance()) {
                    try {
                        LOGGER.info("Enviamos el resultado de la operacion de seleccion de certificado al servidor intermedio");
                        IntermediateServerUtil.sendData(strCipherData, urlParametersToSelectCert.getStorageServletUrl().toString(), urlParametersToSelectCert.getId());
                    } catch (Exception e6) {
                        LOGGER.log(Level.SEVERE, "Error al enviar los datos al servidor", (Throwable) e6);
                        ProtocolInvocationLauncherErrorManager.showError("SAF_11", e6);
                        return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_11");
                    }
                }
            } else {
                LOGGER.info("Se omite el envio por red del resultado por no haberse proporcionado una URL de destino");
            }
            return strCipherData;
        } catch (CertificateEncodingException e7) {
            LOGGER.severe("Error en la decodificacion del certificado de firma: " + e7);
            ProtocolInvocationLauncherErrorManager.showError("SAF_18", e7);
            if (!z) {
                throw new SocketOperationException("SAF_18");
            }
            return ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_18");
        }
    }

    public static String getResultCancel() {
        return "CANCEL";
    }
}
