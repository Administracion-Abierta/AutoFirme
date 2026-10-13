package es.gob.afirma.standalone.protocol;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOFormatFileException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.RuntimeConfigNeededException;
import es.gob.afirma.core.RuntimePasswordNeededException;
import es.gob.afirma.core.keystores.CertificateContext;
import es.gob.afirma.core.keystores.LockedKeyStoreException;
import es.gob.afirma.core.keystores.PinException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.misc.protocol.UrlParametersToSignAndSave;
import es.gob.afirma.core.prefs.KeyStorePreferencesManager;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.AOSignerFactory;
import es.gob.afirma.core.signers.AOTriphaseException;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.signers.ExtraParamsProcessor;
import es.gob.afirma.core.signers.OptionalDataInterface;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.core.ui.GenericFileFilter;
import es.gob.afirma.keystores.AOCertificatesNotFoundException;
import es.gob.afirma.keystores.AOKeyStore;
import es.gob.afirma.keystores.AOKeyStoreDialog;
import es.gob.afirma.keystores.AOKeyStoreManagerFactory;
import es.gob.afirma.keystores.AggregatedKeyStoreManager;
import es.gob.afirma.keystores.filters.CertFilterManager;
import es.gob.afirma.keystores.filters.EncodedCertificateFilter;
import es.gob.afirma.signers.cades.CAdESExtraParams;
import es.gob.afirma.signers.pades.AOPDFSigner;
import es.gob.afirma.signers.pades.InvalidPdfException;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.signers.padestri.client.AOPDFTriPhaseSigner;
import es.gob.afirma.signers.pkcs7.ContainsNoDataException;
import es.gob.afirma.signers.xades.EFacturaAlreadySignedException;
import es.gob.afirma.signers.xades.InvalidEFacturaDataException;
import es.gob.afirma.signers.xml.InvalidXMLException;
import es.gob.afirma.signvalidation.InvalidSignatureException;
import es.gob.afirma.signvalidation.SignValider;
import es.gob.afirma.signvalidation.SignValiderFactory;
import es.gob.afirma.signvalidation.SignValidity;
import es.gob.afirma.standalone.CommandLineParameters;
import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.SimpleAfirma;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.SimpleKeyStoreManager;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import es.gob.afirma.standalone.plugins.AfirmaPlugin;
import es.gob.afirma.standalone.plugins.EncryptingException;
import es.gob.afirma.standalone.plugins.Permission;
import es.gob.afirma.standalone.plugins.PluginControlledException;
import es.gob.afirma.standalone.plugins.SignDataProcessor;
import es.gob.afirma.standalone.plugins.SignOperation;
import es.gob.afirma.standalone.plugins.SignResult;
import es.gob.afirma.standalone.plugins.manager.PermissionChecker;
import es.gob.afirma.standalone.plugins.manager.PluginException;
import es.gob.afirma.standalone.so.macos.MacUtils;
import es.gob.afirma.standalone.ui.DataDebugDialog;
import es.gob.afirma.standalone.ui.pdf.SignPdfDialog;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/ProtocolInvocationLauncherSignAndSave.class */
final class ProtocolInvocationLauncherSignAndSave {
    public static final String RESULT_CANCEL = "CANCEL";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    static boolean showRubricIsCanceled = false;

    private ProtocolInvocationLauncherSignAndSave() {
    }

    static StringBuilder processSign(UrlParametersToSignAndSave urlParametersToSignAndSave, int i) throws SocketOperationException {
        if (urlParametersToSignAndSave == null) {
            LOGGER.severe("Las opciones de firma son nulas");
            throw new SocketOperationException("SAF_01");
        }
        if (!ProtocolInvocationLauncher.MAX_PROTOCOL_VERSION_SUPPORTED.support(i)) {
            LOGGER.severe(String.format("Version de protocolo no soportada (%1d). Version actual: %2d. Hay que actualizar la aplicacion.", Integer.valueOf(i), Integer.valueOf(ProtocolInvocationLauncher.MAX_PROTOCOL_VERSION_SUPPORTED.getVersion())));
            throw new SocketOperationException("SAF_21");
        }
        if (urlParametersToSignAndSave.getMinimumClientVersion() != null && new Version(urlParametersToSignAndSave.getMinimumClientVersion()).greaterThan(SimpleAfirma.getVersion())) {
            throw new SocketOperationException("SAF_41");
        }
        urlParametersToSignAndSave.getExtraParams().remove("profile");
        SignOperation signOperation = new SignOperation();
        signOperation.setData(urlParametersToSignAndSave.getData());
        signOperation.setCryptoOperation(SignOperation.Operation.getOperation(urlParametersToSignAndSave.getOperation()));
        signOperation.setAlgorithm(urlParametersToSignAndSave.getSignatureAlgorithm());
        signOperation.setFormat(urlParametersToSignAndSave.getSignatureFormat());
        signOperation.setExtraParams(urlParametersToSignAndSave.getExtraParams());
        signOperation.setAnotherParams(urlParametersToSignAndSave.getAnotherParams());
        SignDataProcessor signDataProcessorSelectProcessor = selectProcessor(i, signOperation);
        signDataProcessorSelectProcessor.setCipherKey(urlParametersToSignAndSave.getDesKey());
        List<SignOperation> listPreProcess = signDataProcessorSelectProcessor.preProcess(signOperation);
        boolean z = listPreProcess.size() > 1;
        ArrayList arrayList = new ArrayList(listPreProcess.size());
        for (int i2 = 0; i2 < listPreProcess.size(); i2++) {
            try {
                arrayList.add(sign(listPreProcess.get(i2), urlParametersToSignAndSave, z, false));
            } catch (SocketOperationException e) {
                LOGGER.log(Level.SEVERE, "Se identifico un error en una operacion de firma", (Throwable) e);
                if (!signDataProcessorSelectProcessor.isErrorsAllowed()) {
                    throw e;
                }
            } catch (VisibleSignatureMandatoryException e2) {
                LOGGER.log(Level.SEVERE, "No se cumplieron los requisitos para firma visible PDF: " + e2);
                throw new SocketOperationException("SAF_43", e2);
            }
        }
        try {
            return signDataProcessorSelectProcessor.postProcess(arrayList, signOperation);
        } catch (EncryptingException e3) {
            LOGGER.log(Level.SEVERE, "Error en el cifrado de los datos a enviar", (Throwable) e3);
            throw new SocketOperationException("SAF_12");
        } catch (Exception e4) {
            LOGGER.log(Level.SEVERE, "Error en el postprocesador de los datos a enviar", (Throwable) e4);
            throw new SocketOperationException("SAF_42");
        }
    }

    private static SignDataProcessor selectProcessor(int i, SignOperation signOperation) {
        List<AfirmaPlugin> pluginsLoadedList;
        SignDataProcessor inlineProcessor;
        try {
            pluginsLoadedList = SimpleAfirma.getPluginsManager().getPluginsLoadedList();
        } catch (PluginException e) {
            LOGGER.log(Level.SEVERE, "No se pudo cargar el listado de plugins", (Throwable) e);
            pluginsLoadedList = null;
        }
        if (pluginsLoadedList != null) {
            for (AfirmaPlugin afirmaPlugin : pluginsLoadedList) {
                try {
                    if (PermissionChecker.check(afirmaPlugin.getInfo(), Permission.INLINE_PROCESS) && (inlineProcessor = afirmaPlugin.getInlineProcessor(i)) != null && inlineProcessor.checkTrigger(signOperation)) {
                        return inlineProcessor;
                    }
                } catch (PluginControlledException e2) {
                    LOGGER.log(Level.WARNING, "Error al evaluar el uso del plugin " + afirmaPlugin.getClass().getName() + " para su uso en linea", (Throwable) e2);
                }
            }
        }
        return new NativeSignDataProcessor(i);
    }

    /* JADX WARN: Not initialized variable reg: 30, insn: 0x02af: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r30 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:99:0x02af */
    /* JADX WARN: Not initialized variable reg: 31, insn: 0x02b4: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r31 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:101:0x02b4 */
    private static SignResult sign(SignOperation signOperation, UrlParametersToSignAndSave urlParametersToSignAndSave, boolean z, boolean z2) throws SocketOperationException, VisibleSignatureMandatoryException {
        char[] password;
        InputStream inputStream;
        Throwable th;
        byte[] data = signOperation.getData();
        String format = signOperation.getFormat();
        String algorithm = signOperation.getAlgorithm();
        Properties extraParams = signOperation.getExtraParams();
        if (extraParams == null) {
            extraParams = new Properties();
        }
        SignOperation.Operation cryptoOperation = signOperation.getCryptoOperation();
        AOSigner signer = null;
        if (!"auto".equalsIgnoreCase(format)) {
            signer = AOSignerFactory.getSigner(format);
            if (signer == null) {
                LOGGER.severe("No hay un firmador configurado para el formato: " + format);
                throw new SocketOperationException("SAF_06");
            }
        }
        String lastSelectedKeystore = KeyStorePreferencesManager.getLastSelectedKeystore();
        boolean z3 = PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_USE_DEFAULT_STORE_IN_BROWSER_CALLS);
        AOKeyStore keyStore = null;
        if (lastSelectedKeystore != null && !lastSelectedKeystore.isEmpty()) {
            keyStore = SimpleKeyStoreManager.getLastSelectedKeystore();
        } else if (z3) {
            String str = PreferencesManager.get(PreferencesManager.PREFERENCE_KEYSTORE_DEFAULT_STORE);
            if (!PreferencesManager.VALUE_KEYSTORE_DEFAULT.equals(str)) {
                keyStore = SimpleKeyStoreManager.getKeyStore(str);
            }
        } else {
            keyStore = SimpleKeyStoreManager.getKeyStore(urlParametersToSignAndSave.getDefaultKeyStore());
        }
        if (keyStore == null) {
            keyStore = AOKeyStore.getDefaultKeyStoreTypeByOs(Platform.getOS());
        }
        boolean zNeedData = false;
        if (data == null) {
            zNeedData = signer instanceof OptionalDataInterface ? ((OptionalDataInterface) signer).needData(signOperation.getCryptoOperation().toString(), extraParams) : true;
        }
        if (!zNeedData) {
            try {
                if (Boolean.getBoolean("AFIRMA_SHOW_DATA_TO_SIGN")) {
                    DataDebugDialog dataDebugDialog = new DataDebugDialog(data);
                    data = dataDebugDialog.getData();
                    dataDebugDialog.dispose();
                }
            } catch (Exception e) {
                LOGGER.warning("No se pueden mostrar los datos a firmar: " + e);
            }
        }
        String name = null;
        if (zNeedData) {
            String string = SignOperation.Operation.SIGN == cryptoOperation ? ProtocolMessages.getString("ProtocolLauncher.25") : ProtocolMessages.getString("ProtocolLauncher.26");
            String property = extraParams.getProperty("filenameExts");
            String str2 = extraParams.getProperty("filenameDescription", ProtocolMessages.getString("ProtocolLauncher.32")) + (property == null ? " (*.*)" : String.format(" (*.%1s)", property.replace(",", ",*.")));
            try {
                if (Platform.OS.MACOSX.equals(Platform.getOS())) {
                    MacUtils.focusApplication();
                }
                File file = AOUIFactory.getLoadFiles(string, extraParams.getProperty("filenameCurrentDir"), extraParams.getProperty("filenameActualName"), property != null ? property.split(",") : null, str2, false, false, DesktopUtil.getDefaultDialogsIcon(), null)[0];
                name = file.getName();
                try {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        Throwable th2 = null;
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                        Throwable th3 = null;
                        try {
                            try {
                                data = AOUtil.getDataFromInputStream(bufferedInputStream);
                                if (bufferedInputStream != null) {
                                    if (0 != 0) {
                                        try {
                                            bufferedInputStream.close();
                                        } catch (Throwable th4) {
                                            th3.addSuppressed(th4);
                                        }
                                    } else {
                                        bufferedInputStream.close();
                                    }
                                }
                                if (fileInputStream != null) {
                                    if (0 != 0) {
                                        try {
                                            fileInputStream.close();
                                        } catch (Throwable th5) {
                                            th2.addSuppressed(th5);
                                        }
                                    } else {
                                        fileInputStream.close();
                                    }
                                }
                                if (data == null) {
                                    throw new IOException("La lectura de datos para firmar ha devuelto un nulo");
                                }
                            } catch (Throwable th6) {
                                th3 = th6;
                                throw th6;
                            }
                        } catch (Throwable th7) {
                            if (bufferedInputStream != null) {
                                if (th3 != null) {
                                    try {
                                        bufferedInputStream.close();
                                    } catch (Throwable th8) {
                                        th3.addSuppressed(th8);
                                    }
                                } else {
                                    bufferedInputStream.close();
                                }
                            }
                            throw th7;
                        }
                    } catch (Throwable th9) {
                        if (inputStream != null) {
                            if (th != null) {
                                try {
                                    inputStream.close();
                                } catch (Throwable th10) {
                                    th.addSuppressed(th10);
                                }
                            } else {
                                inputStream.close();
                            }
                        }
                        throw th9;
                    }
                } catch (Exception e2) {
                    LOGGER.severe("Error en la lectura de los datos a firmar: " + e2);
                    throw new SocketOperationException("SAF_00");
                }
            } catch (AOCancelledOperationException e3) {
                LOGGER.info("Carga de datos de firma cancelada por el usuario: " + e3);
                throw new SocketOperationException("CANCEL");
            }
        }
        if (signer == null) {
            format = ProtocolInvocationLauncherUtil.identifyFormatFromData(data, cryptoOperation);
            if (format == null) {
                LOGGER.severe("Los datos no se corresponden con una firma electronica o no se pudieron analizar");
                throw new SocketOperationException("SAF_17");
            }
            signer = AOSignerFactory.getSigner(format);
        }
        if (cryptoOperation == SignOperation.Operation.SIGN && isXadesExplicitConfigurated(format, extraParams) && !format.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_XADES_TRI)) {
            LOGGER.warning("Se ha pedido una firma XAdES explicita, este formato dejara de soportarse en proximas versiones");
            try {
                data = MessageDigest.getInstance(AOSignConstants.DIGEST_ALGORITHM_SHA1).digest(data);
                extraParams.setProperty("mimeType", "hash/sha1");
            } catch (Exception e4) {
                LOGGER.warning("Error al generar la huella digital de los datos para firmar como 'XAdES explicit', se realizara una firma XAdES corriente: " + e4);
            }
        }
        if (data != null && Boolean.parseBoolean(extraParams.getProperty("checkSignatures"))) {
            SignValider signValider = SignValiderFactory.getSignValider(signer);
            SignValidity signValidity = null;
            if (signValider != null) {
                signValider.setRelaxed(true);
                if ((signer instanceof AOPDFSigner) || (signer instanceof AOPDFTriPhaseSigner)) {
                    configurePdfSignature(extraParams);
                }
                do {
                    try {
                        signValidity = signValider.validate(data, extraParams).get(0);
                    } catch (RuntimeConfigNeededException e5) {
                        LOGGER.warning("La validacion de la firma ha revelado una situacion que requiere la intervencion del usuario: " + e5);
                        if (Boolean.parseBoolean(extraParams.getProperty("headless"))) {
                            LOGGER.severe("No se pueden mostrar dialogos para pedir datos del usuario. Se abortara la operacion");
                            throw new SocketOperationException("SAF_50", e5.getMessage(), e5);
                        }
                        if (e5.getRequestType() == RuntimeConfigNeededException.RequestType.CONFIRM) {
                            if (AOUIFactory.showConfirmDialog(null, SimpleAfirmaMessages.getString(e5.getRequestorText()), SimpleAfirmaMessages.getString("SignPanelSignTask.4"), 0, 3) != 0) {
                                LOGGER.log(Level.SEVERE, "El usuario ha cancelado la operacion despues de la advertencia: " + SimpleAfirmaMessages.getString(e5.getRequestorText()));
                                throw new SocketOperationException("CANCEL");
                            }
                            extraParams.setProperty(e5.getParam(), Boolean.TRUE.toString());
                        } else {
                            if (e5.getRequestType() != RuntimeConfigNeededException.RequestType.PASSWORD) {
                                LOGGER.severe("No se puede gestionar la solicitud de datos necesaria para completar la firma");
                                throw new SocketOperationException(e5.getRequestorText(), e5.getMessage(), e5);
                            }
                            try {
                                password = AOUIFactory.getPassword(SimpleAfirmaMessages.getString(e5.getRequestorText()), null);
                            } catch (AOCancelledOperationException e6) {
                                password = null;
                            }
                            if (password != null) {
                                if (e5 instanceof RuntimePasswordNeededException) {
                                    ((RuntimePasswordNeededException) e5).configure(extraParams, password);
                                } else {
                                    extraParams.setProperty(e5.getParam(), new String(password));
                                }
                            }
                        }
                    } catch (IOException e7) {
                        LOGGER.severe("Error al identificar la validez de la firma: " + e7);
                        signValidity = new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.UNKOWN_ERROR);
                    }
                } while (signValidity == null);
                if (signValidity.getValidity() == SignValidity.SIGN_DETAIL_TYPE.KO && (cryptoOperation != SignOperation.Operation.SIGN || signValidity.getError() != SignValidity.VALIDITY_ERROR.NO_SIGN)) {
                    LOGGER.severe("La firma indicada no es valida: " + signValidity);
                    throw new SocketOperationException("SAF_39");
                }
            }
        }
        try {
            Properties propertiesExpandProperties = ExtraParamsProcessor.expandProperties(extraParams, data, format);
            CertFilterManager certFilterManager = new CertFilterManager(propertiesExpandProperties);
            try {
                if (isRubricPositionRequired(format, propertiesExpandProperties)) {
                    showRubricPositionDialog(data, propertiesExpandProperties, z);
                    checkShowRubricDialogIsCalceled(propertiesExpandProperties);
                }
                KeyStore.PrivateKeyEntry stickyKeyEntry = null;
                String defaultKeyStoreLib = null;
                if (!urlParametersToSignAndSave.getSticky() || urlParametersToSignAndSave.getResetSticky() || ProtocolInvocationLauncher.getStickyKeyEntry() == null) {
                    defaultKeyStoreLib = (z3 && (AOKeyStore.PKCS12.equals(keyStore) || AOKeyStore.PKCS11.equals(keyStore))) ? PreferencesManager.get(PreferencesManager.PREFERENCE_LOCAL_KEYSTORE_PATH) : urlParametersToSignAndSave.getDefaultKeyStoreLib();
                } else {
                    stickyKeyEntry = ProtocolInvocationLauncher.getStickyKeyEntry();
                }
                SignOperationResult signOperationResultSelectCertAndSign = selectCertAndSign(stickyKeyEntry, keyStore, defaultKeyStoreLib, certFilterManager, urlParametersToSignAndSave.getSticky(), data, algorithm, signer, cryptoOperation, propertiesExpandProperties);
                byte[] result = signOperationResultSelectCertAndSign.getResult();
                KeyStore.PrivateKeyEntry pke = signOperationResultSelectCertAndSign.getPke();
                if (urlParametersToSignAndSave.getSticky()) {
                    ProtocolInvocationLauncher.setStickyKeyEntry(pke);
                } else {
                    ProtocolInvocationLauncher.setStickyKeyEntry(null);
                }
                String property2 = urlParametersToSignAndSave.getExtraParams().getProperty("filenameSaveExts");
                try {
                    AOUIFactory.getSaveDataToFile(result, ProtocolMessages.getString("ProtocolLauncher.31"), urlParametersToSignAndSave.getExtraParams().getProperty("filenameSaveCurrentDir"), getFilename(urlParametersToSignAndSave, name, signer), Collections.singletonList(new GenericFileFilter(property2 != null ? property2.split(",") : null, urlParametersToSignAndSave.getExtraParams().getProperty("filenameSaveDescription", ProtocolMessages.getString("ProtocolLauncher.30")) + (property2 == null ? " (*.*)" : String.format(" (*.%1s)", property2.replace(",", ",*."))))), null);
                    try {
                        byte[] encoded = pke.getCertificateChain()[0].getEncoded();
                        SignResult signResult = new SignResult();
                        signResult.setSignature(result);
                        signResult.setCertificate(encoded);
                        Properties properties = null;
                        if (name != null) {
                            properties = new Properties();
                            properties.setProperty("filename", name);
                        }
                        signResult.setDataFilename(properties);
                        return signResult;
                    } catch (CertificateEncodingException e8) {
                        LOGGER.severe("Error en la decodificacion del certificado de firma: " + e8);
                        throw new SocketOperationException("SAF_18");
                    }
                } catch (AOCancelledOperationException e9) {
                    LOGGER.severe("Operacion cancelada por el usuario: " + e9);
                    throw new SocketOperationException("CANCEL");
                } catch (Exception e10) {
                    LOGGER.severe("Error en el guardado de datos: " + e10);
                    throw new SocketOperationException("SAF_05");
                }
            } catch (AOCancelledOperationException e11) {
                LOGGER.info("El usuario ha cancelado el proceso de firma.");
                throw new VisibleSignatureMandatoryException("Es obligatorio mostrar la firma en el documento PDF");
            }
        } catch (ExtraParamsProcessor.IncompatiblePolicyException e12) {
            LOGGER.info("Se ha indicado una politica no compatible: " + e12);
            throw new SocketOperationException("SAF_23", e12);
        }
    }

    private static SignOperationResult selectCertAndSign(KeyStore.PrivateKeyEntry privateKeyEntry, AOKeyStore aOKeyStore, String str, CertFilterManager certFilterManager, boolean z, byte[] bArr, String str2, AOSigner aOSigner, SignOperation.Operation operation, Properties properties) throws SocketOperationException {
        List listSingletonList;
        KeyStore.PrivateKeyEntry keyEntry = privateKeyEntry;
        if (privateKeyEntry == null) {
            PasswordCallback storePasswordCallback = aOKeyStore.getStorePasswordCallback(null);
            LOGGER.info("Obtenido gestor de almacenes de claves: " + aOKeyStore);
            try {
                AggregatedKeyStoreManager aOKeyStoreManager = AOKeyStoreManagerFactory.getAOKeyStoreManager(aOKeyStore, str, null, storePasswordCallback, null);
                LOGGER.info("Cargando dialogo de seleccion de certificados...");
                try {
                    MacUtils.focusApplication();
                    String name = null;
                    if (str != null) {
                        name = new File(str).getName();
                    }
                    AOKeyStoreDialog aOKeyStoreDialog = new AOKeyStoreDialog(aOKeyStoreManager, null, true, true, true, certFilterManager.getFilters(), certFilterManager.isMandatoryCertificate(), name);
                    aOKeyStoreDialog.allowOpenExternalStores(certFilterManager.isExternalStoresOpeningAllowed());
                    aOKeyStoreDialog.show();
                    CertificateContext selectedCertificateContext = aOKeyStoreDialog.getSelectedCertificateContext();
                    keyEntry = selectedCertificateContext.getKeyStoreManager().getKeyEntry(selectedCertificateContext.getAlias());
                } catch (AOCancelledOperationException e) {
                    LOGGER.severe("Operacion cancelada por el usuario: " + e);
                    throw new SocketOperationException("CANCEL");
                } catch (AOCertificatesNotFoundException e2) {
                    LOGGER.severe("No hay certificados validos en el almacen: " + e2);
                    throw new SocketOperationException("SAF_19");
                } catch (Exception e3) {
                    LOGGER.severe("Error al mostrar el dialogo de seleccion de certificados: " + e3);
                    throw new SocketOperationException("SAF_08", e3);
                }
            } catch (Exception e4) {
                LOGGER.log(Level.SEVERE, "Error obteniendo el AOKeyStoreManager", (Throwable) e4);
                throw new SocketOperationException("SAF_08", e4);
            }
        }
        try {
            String strComposeSignatureAlgorithmName = AOSignConstants.composeSignatureAlgorithmName(str2, keyEntry.getPrivateKey().getAlgorithm());
            ProtocolInvocationLauncher.setStickyKeyEntry(z ? keyEntry : null);
            try {
                return new SignOperationResult(executeSign(aOSigner, operation, bArr, strComposeSignatureAlgorithmName, keyEntry, properties), keyEntry);
            } catch (LockedKeyStoreException e5) {
                LOGGER.log(Level.SEVERE, "El almacen de claves esta bloqueado", (Throwable) e5);
                ProtocolInvocationLauncher.setStickyKeyEntry(null);
                throw new SocketOperationException("SAF_52", e5);
            } catch (PinException e6) {
                LOGGER.warning("PIN invalido. Reintentamos la operacion: " + e6);
                try {
                    listSingletonList = Collections.singletonList(new EncodedCertificateFilter(Base64.encode(keyEntry.getCertificate().getEncoded())));
                } catch (Exception e7) {
                    listSingletonList = null;
                }
                return selectCertAndSign(null, aOKeyStore, str, new CertFilterManager(listSingletonList, listSingletonList != null, true), z, bArr, strComposeSignatureAlgorithmName, aOSigner, operation, properties);
            }
        } catch (Exception e8) {
            throw new SocketOperationException("SAF_51", e8);
        }
    }

    private static byte[] executeSign(AOSigner aOSigner, SignOperation.Operation operation, byte[] bArr, String str, KeyStore.PrivateKeyEntry privateKeyEntry, Properties properties) throws PinException, SocketOperationException, LockedKeyStoreException {
        char[] password;
        byte[] bArrCountersign;
        try {
            try {
                switch (operation) {
                    case SIGN:
                        bArrCountersign = aOSigner.sign(bArr, str, privateKeyEntry.getPrivateKey(), privateKeyEntry.getCertificateChain(), properties);
                        break;
                    case COSIGN:
                        bArrCountersign = aOSigner.cosign(bArr, str, privateKeyEntry.getPrivateKey(), privateKeyEntry.getCertificateChain(), properties);
                        break;
                    case COUNTERSIGN:
                        bArrCountersign = aOSigner.countersign(bArr, str, "tree".equalsIgnoreCase(properties.getProperty("target")) ? CounterSignTarget.TREE : CounterSignTarget.LEAFS, null, privateKeyEntry.getPrivateKey(), privateKeyEntry.getCertificateChain(), properties);
                        break;
                    default:
                        LOGGER.severe("Error al realizar la operacion firma");
                        throw new SocketOperationException("SAF_04");
                }
                return bArrCountersign;
            } catch (AOTriphaseException e) {
                throw ProtocolInvocationLauncherUtil.getInternalException(e);
            }
        } catch (AOCancelledOperationException e2) {
            LOGGER.log(Level.SEVERE, "Operacion cancelada por el usuario", (Throwable) e2);
            throw new SocketOperationException("CANCEL");
        } catch (AOFormatFileException e3) {
            LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e3);
            throw new SocketOperationException("SAF_30");
        } catch (AOInvalidFormatException e4) {
            LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e4);
            throw new SocketOperationException("SAF_31");
        } catch (RuntimeConfigNeededException e5) {
            LOGGER.warning("No se puede completar la firma sin intervencion del usuario: " + e5);
            if (Boolean.parseBoolean(properties.getProperty("headless"))) {
                LOGGER.severe("No se pueden mostrar dialogos para pedir datos del usuario. Se abortara la operacion");
                throw new SocketOperationException("SAF_50", e5.getMessage(), e5);
            }
            if (e5.getRequestType() == RuntimeConfigNeededException.RequestType.CONFIRM) {
                if (AOUIFactory.showConfirmDialog(null, SimpleAfirmaMessages.getString(e5.getRequestorText()), SimpleAfirmaMessages.getString("SignPanelSignTask.4"), 0, 3) == 0) {
                    properties.setProperty(e5.getParam(), Boolean.TRUE.toString());
                    return executeSign(aOSigner, operation, bArr, str, privateKeyEntry, properties);
                }
            } else if (e5.getRequestType() == RuntimeConfigNeededException.RequestType.PASSWORD) {
                try {
                    password = AOUIFactory.getPassword(SimpleAfirmaMessages.getString(e5.getRequestorText()), null);
                } catch (AOCancelledOperationException e6) {
                    password = null;
                }
                if (password != null) {
                    if (e5 instanceof RuntimePasswordNeededException) {
                        ((RuntimePasswordNeededException) e5).configure(properties, password);
                    } else {
                        properties.setProperty(e5.getParam(), new String(password));
                    }
                    return executeSign(aOSigner, operation, bArr, str, privateKeyEntry, properties);
                }
            } else {
                LOGGER.severe("No se puede gestionar la solicitud de datos necesaria para completar la firma");
                throw new SocketOperationException(e5.getRequestorText(), e5.getMessage(), e5);
            }
            LOGGER.severe("Operacion cancelada por el usuario: " + e5);
            throw new SocketOperationException("CANCEL");
        } catch (LockedKeyStoreException | PinException | SocketOperationException e7) {
            throw e7;
        } catch (AOTriphaseException e8) {
            LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e8);
            throw new SocketOperationException("SAF_40", e8);
        } catch (InvalidPdfException e9) {
            LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e9);
            throw new SocketOperationException("SAF_28");
        } catch (ContainsNoDataException e10) {
            LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e10);
            throw new SocketOperationException("SAF_44");
        } catch (EFacturaAlreadySignedException e11) {
            LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e11);
            throw new SocketOperationException("SAF_32");
        } catch (InvalidEFacturaDataException e12) {
            LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e12);
            throw new SocketOperationException("SAF_38");
        } catch (InvalidXMLException e13) {
            LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e13);
            throw new SocketOperationException("SAF_29");
        } catch (InvalidSignatureException e14) {
            LOGGER.log(Level.SEVERE, "La firma de entrada no es valida", (Throwable) e14);
            throw new SocketOperationException("SAF_39");
        } catch (AOException e15) {
            LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e15);
            throw new SocketOperationException("SAF_09", e15);
        } catch (IllegalArgumentException e16) {
            LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e16);
            throw new SocketOperationException("SAF_03", e16);
        } catch (UnsupportedOperationException e17) {
            LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e17);
            throw new SocketOperationException("SAF_04");
        } catch (Exception e18) {
            LOGGER.log(Level.SEVERE, "Error al realizar la operacion de firma", (Throwable) e18);
            throw new SocketOperationException("SAF_09");
        }
    }

    private static void configurePdfSignature(Properties properties) {
        if (!Boolean.parseBoolean(properties.getProperty("allowShadowAttack")) && !properties.containsKey(PdfExtraParams.PAGES_TO_CHECK_PSA)) {
            properties.setProperty(PdfExtraParams.PAGES_TO_CHECK_PSA, "10");
        }
    }

    @Deprecated
    private static boolean isXadesExplicitConfigurated(String str, Properties properties) {
        return str != null && str.toLowerCase().startsWith(CommandLineParameters.FORMAT_XADES) && properties != null && "explicit".equalsIgnoreCase(properties.getProperty(CAdESExtraParams.MODE));
    }

    private static boolean isRubricPositionRequired(String str, Properties properties) {
        if (!Arrays.asList(AOSignConstants.SIGN_FORMAT_PDF, AOSignConstants.SIGN_FORMAT_PDF_TRI, "PAdES", AOSignConstants.SIGN_FORMAT_PADES_TRI, AOSignConstants.PADES_SUBFILTER_BES, AOSignConstants.PADES_SUBFILTER_BASIC).contains(str) || !properties.containsKey(PdfExtraParams.VISIBLE_SIGNATURE)) {
            return false;
        }
        String string = properties.get(PdfExtraParams.VISIBLE_SIGNATURE).toString();
        if (!PdfExtraParams.VISIBLE_SIGNATURE_VALUE_WANT.equalsIgnoreCase(string) && !PdfExtraParams.VISIBLE_SIGNATURE_VALUE_OPTIONAL.equalsIgnoreCase(string)) {
            return false;
        }
        return true;
    }

    private static void showRubricPositionDialog(byte[] bArr, Properties properties, boolean z) {
        SignPdfDialog.getVisibleSignatureDialog(new AOPDFSigner().isSign(bArr), z, bArr, null, true, isCustomAppearance(properties), false, new SignPdfListener(properties)).setVisible(true);
    }

    private static void checkShowRubricDialogIsCalceled(Properties properties) {
        if (showRubricIsCanceled) {
            boolean z = PdfExtraParams.VISIBLE_SIGNATURE_VALUE_WANT.equalsIgnoreCase(properties.get(PdfExtraParams.VISIBLE_SIGNATURE) != null ? properties.get(PdfExtraParams.VISIBLE_SIGNATURE).toString() : null);
            boolean z2 = properties.containsKey(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_LOWER_LEFTX) && properties.containsKey(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_LOWER_LEFTY) && properties.containsKey(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_UPPER_RIGHTX) && properties.containsKey(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_UPPER_RIGHTY) && (properties.containsKey(PdfExtraParams.SIGNATURE_PAGE) || properties.containsKey(PdfExtraParams.SIGNATURE_PAGES));
            if (z && !z2) {
                throw new AOCancelledOperationException("Incluir la firma visible en el documento es obligatoria.");
            }
        }
    }

    private static boolean isCustomAppearance(Properties properties) {
        boolean z = false;
        if (PdfExtraParams.VISIBLE_APPEARANCE_VALUE_CUSTOM.equalsIgnoreCase(properties.getProperty(PdfExtraParams.VISIBLE_APPEARANCE))) {
            z = true;
        }
        return z;
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/ProtocolInvocationLauncherSignAndSave$SignPdfListener.class */
    static class SignPdfListener implements SignPdfDialog.SignPdfDialogListener {
        Properties extraParams;

        public SignPdfListener(Properties properties) {
            this.extraParams = properties;
        }

        @Override // es.gob.afirma.standalone.ui.pdf.SignPdfDialog.SignPdfDialogListener
        public void propertiesCreated(Properties properties) {
            if (properties.isEmpty()) {
                ProtocolInvocationLauncherSign.showRubricIsCanceled = true;
            } else {
                updateOptions(this.extraParams, properties);
            }
        }

        static void updateOptions(Properties properties, Properties properties2) {
            properties.setProperty(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_LOWER_LEFTX, properties2.getProperty(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_LOWER_LEFTX));
            properties.setProperty(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_LOWER_LEFTY, properties2.getProperty(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_LOWER_LEFTY));
            properties.setProperty(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_UPPER_RIGHTX, properties2.getProperty(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_UPPER_RIGHTX));
            properties.setProperty(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_UPPER_RIGHTY, properties2.getProperty(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_UPPER_RIGHTY));
            properties.setProperty(PdfExtraParams.SIGNATURE_PAGE, properties2.getProperty(PdfExtraParams.SIGNATURE_PAGE));
            if (properties2.getProperty(PdfExtraParams.LAYER2_TEXT) != null) {
                properties.setProperty(PdfExtraParams.LAYER2_TEXT, properties2.getProperty(PdfExtraParams.LAYER2_TEXT));
            }
            if (properties2.getProperty(PdfExtraParams.LAYER2_FONTFAMILY) != null) {
                properties.setProperty(PdfExtraParams.LAYER2_FONTFAMILY, properties2.getProperty(PdfExtraParams.LAYER2_FONTFAMILY));
            }
            if (properties2.getProperty(PdfExtraParams.LAYER2_FONTSIZE) != null) {
                properties.setProperty(PdfExtraParams.LAYER2_FONTSIZE, properties2.getProperty(PdfExtraParams.LAYER2_FONTSIZE));
            }
            if (properties2.getProperty(PdfExtraParams.SIGNATURE_ROTATION) != null) {
                properties.setProperty(PdfExtraParams.SIGNATURE_ROTATION, properties2.getProperty(PdfExtraParams.SIGNATURE_ROTATION));
            }
            if (properties2.getProperty(PdfExtraParams.LAYER2_FONTSTYLE) != null) {
                properties.setProperty(PdfExtraParams.LAYER2_FONTSTYLE, properties2.getProperty(PdfExtraParams.LAYER2_FONTSTYLE));
            }
            if (properties2.getProperty(PdfExtraParams.LAYER2_FONTCOLOR) != null) {
                properties.setProperty(PdfExtraParams.LAYER2_FONTCOLOR, properties2.getProperty(PdfExtraParams.LAYER2_FONTCOLOR));
            }
            if (properties2.getProperty(PdfExtraParams.SIGNATURE_RUBRIC_IMAGE) != null) {
                properties.setProperty(PdfExtraParams.SIGNATURE_RUBRIC_IMAGE, properties2.getProperty(PdfExtraParams.SIGNATURE_RUBRIC_IMAGE));
            }
        }
    }

    private static String getFilename(UrlParametersToSignAndSave urlParametersToSignAndSave, String str, AOSigner aOSigner) {
        String string;
        if (urlParametersToSignAndSave.getFileName() != null) {
            return urlParametersToSignAndSave.getFileName();
        }
        if (str != null) {
            int iLastIndexOf = str.lastIndexOf(46);
            if (iLastIndexOf > 0) {
                string = str.substring(0, iLastIndexOf);
            } else {
                string = str;
            }
        } else {
            string = ProtocolMessages.getString("ProtocolLauncher.30");
        }
        return aOSigner.getSignedName(string, null);
    }
}
