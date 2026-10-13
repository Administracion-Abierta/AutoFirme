package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOFormatFileException;
import es.gob.afirma.core.CustomRuntimeConfigNeededException;
import es.gob.afirma.core.RuntimeConfigNeededException;
import es.gob.afirma.core.RuntimePasswordNeededException;
import es.gob.afirma.core.keystores.AOCancelledSMOperationException;
import es.gob.afirma.core.keystores.AuthenticationException;
import es.gob.afirma.core.keystores.CertificateContext;
import es.gob.afirma.core.keystores.KeyStoreManager;
import es.gob.afirma.core.keystores.LockedKeyStoreException;
import es.gob.afirma.core.keystores.PinException;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.core.ui.GenericFileFilter;
import es.gob.afirma.keystores.AOCertificatesNotFoundException;
import es.gob.afirma.keystores.AOKeyStoreDialog;
import es.gob.afirma.keystores.AOKeyStoreManager;
import es.gob.afirma.keystores.CertificateFilter;
import es.gob.afirma.keystores.filters.EncodedCertificateFilter;
import es.gob.afirma.signers.odf.AOODFSigner;
import es.gob.afirma.signers.ooxml.AOOOXMLSigner;
import es.gob.afirma.signers.pades.AOPDFSigner;
import es.gob.afirma.signers.pades.IncorrectPageException;
import es.gob.afirma.signers.pades.InvalidSignaturePositionException;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.signers.xades.AOFacturaESigner;
import es.gob.afirma.signers.xades.AOXAdESSigner;
import es.gob.afirma.signvalidation.SignValidity;
import es.gob.afirma.standalone.CommandLineParameters;
import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.SimpleAfirma;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import es.gob.afirma.standalone.plugins.AfirmaPlugin;
import es.gob.afirma.standalone.plugins.Permission;
import es.gob.afirma.standalone.plugins.PluginControlledException;
import es.gob.afirma.standalone.plugins.manager.PermissionChecker;
import es.gob.afirma.standalone.plugins.manager.PluginException;
import java.awt.Component;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignPanelSignTask.class */
final class SignPanelSignTask extends SwingWorker<Void, Void> {
    private static final String EXTRAPARAM_HEADLESS = "headless";
    private final Component parent;
    private final List<SignOperationConfig> signConfigs;
    private final AOKeyStoreManager ksm;
    private final List<? extends CertificateFilter> certFilters;
    private KeyStore.PrivateKeyEntry selectedPke;
    private final CommonWaitDialog waitDialog;
    private final SignatureExecutor signExecutor;
    private final SignatureResultViewer resultViewer;
    private boolean needRelaunch;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final List<String> invalidPageNumberFilesList = new ArrayList();

    SignPanelSignTask(Component component, List<SignOperationConfig> list, AOKeyStoreManager aOKeyStoreManager, List<? extends CertificateFilter> list2, CommonWaitDialog commonWaitDialog, SignatureExecutor signatureExecutor, SignatureResultViewer signatureResultViewer) {
        this.needRelaunch = false;
        this.parent = component;
        this.signConfigs = list;
        this.ksm = aOKeyStoreManager;
        this.certFilters = list2 != null ? new ArrayList(list2) : null;
        this.selectedPke = null;
        this.waitDialog = commonWaitDialog;
        this.signExecutor = signatureExecutor;
        this.resultViewer = signatureResultViewer;
    }

    SignPanelSignTask(Component component, List<SignOperationConfig> list, KeyStore.PrivateKeyEntry privateKeyEntry, CommonWaitDialog commonWaitDialog, SignatureExecutor signatureExecutor, SignatureResultViewer signatureResultViewer) {
        this.needRelaunch = false;
        this.parent = component;
        this.signConfigs = list;
        this.ksm = null;
        this.certFilters = null;
        this.selectedPke = privateKeyEntry;
        this.waitDialog = commonWaitDialog;
        this.signExecutor = signatureExecutor;
        this.resultViewer = signatureResultViewer;
    }

    /* JADX INFO: renamed from: doInBackground, reason: merged with bridge method [inline-methods] */
    public Void m756doInBackground() {
        doSignature();
        return null;
    }

    CommonWaitDialog getWaitDialog() {
        return this.waitDialog;
    }

    protected void done() {
        if (this.signExecutor != null) {
            if (this.needRelaunch) {
                this.signExecutor.relaunchTask(this.selectedPke, this.signConfigs);
            } else {
                this.signExecutor.finishTask();
            }
        }
    }

    void doSignature() {
        char[] password;
        List<? extends CertificateFilter> listSingletonList;
        char[] password2;
        this.needRelaunch = false;
        if (this.signConfigs == null || this.signConfigs.isEmpty()) {
            return;
        }
        if (this.selectedPke == null) {
            try {
                this.selectedPke = getPrivateKeyEntry(this.certFilters, false);
            } catch (AOCancelledOperationException e) {
                return;
            } catch (AOCertificatesNotFoundException e2) {
                LOGGER.severe("El almacen no contiene ningun certificado que se pueda usar para firmar: " + e2);
                showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.29"), e2);
                return;
            } catch (Exception e3) {
                LOGGER.severe("Ocurrio un error al extraer la clave privada del certificiado seleccionado: " + e3);
                showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.56"), e3);
                return;
            }
        }
        boolean z = this.signConfigs.size() == 1;
        File fileSelectOutDir = null;
        if (!z) {
            fileSelectOutDir = selectOutDir();
            if (fileSelectOutDir == null) {
                return;
            }
        }
        String absolutePath = "";
        int length = Integer.MAX_VALUE;
        for (SignOperationConfig signOperationConfig : this.signConfigs) {
            if (signOperationConfig.getDataFile().getParentFile() != null && signOperationConfig.getDataFile().getParentFile().getAbsolutePath().length() < length) {
                absolutePath = signOperationConfig.getDataFile().getParentFile().getAbsolutePath();
                length = absolutePath.length();
            }
        }
        byte[] bArrSignData = null;
        for (SignOperationConfig signOperationConfig2 : this.signConfigs) {
            if (signOperationConfig2.getSignValidity() != null && signOperationConfig2.getSignValidity().get(0).getValidity() == SignValidity.SIGN_DETAIL_TYPE.KO && !PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_ALLOW_INVALID_SIGNATURES)) {
                LOGGER.severe("La entrada es una firma invalida. Se omitira");
            } else {
                AOSigner signer = signOperationConfig2.getSigner();
                signOperationConfig2.addExtraParams(System.getProperties());
                if (!z) {
                    signOperationConfig2.addExtraParam("headless", Boolean.TRUE.toString());
                } else if (signOperationConfig2.getSignValidity() != null && signOperationConfig2.getSignValidity().get(0).getValidity() == SignValidity.SIGN_DETAIL_TYPE.PENDING_CONFIRM_BY_USER && signOperationConfig2.getSignValidity().get(0).getErrorException() != null && (signOperationConfig2.getSignValidity().get(0).getErrorException() instanceof RuntimeConfigNeededException)) {
                    RuntimeConfigNeededException runtimeConfigNeededException = (RuntimeConfigNeededException) signOperationConfig2.getSignValidity().get(0).getErrorException();
                    if (runtimeConfigNeededException.getRequestType() == RuntimeConfigNeededException.RequestType.CONFIRM) {
                        if (AOUIFactory.showConfirmDialog(this.parent, SimpleAfirmaMessages.getString(runtimeConfigNeededException.getRequestorText()), SimpleAfirmaMessages.getString("SignPanel.153"), 0, 3) == 0) {
                            if (runtimeConfigNeededException instanceof CustomRuntimeConfigNeededException) {
                                ((CustomRuntimeConfigNeededException) runtimeConfigNeededException).prepareOperationWithConfirmation(signOperationConfig2.getExtraParams());
                            } else {
                                signOperationConfig2.addExtraParam(runtimeConfigNeededException.getParam(), Boolean.TRUE.toString());
                            }
                        } else {
                            return;
                        }
                    } else if (runtimeConfigNeededException.getRequestType() == RuntimeConfigNeededException.RequestType.PASSWORD) {
                        try {
                            password = AOUIFactory.getPassword(SimpleAfirmaMessages.getString(runtimeConfigNeededException.getRequestorText()), this.parent);
                        } catch (AOCancelledOperationException e4) {
                            password = null;
                        }
                        if (password != null) {
                            this.needRelaunch = true;
                            if (runtimeConfigNeededException instanceof RuntimePasswordNeededException) {
                                ((RuntimePasswordNeededException) runtimeConfigNeededException).configure(signOperationConfig2.getExtraParams(), password);
                            } else {
                                signOperationConfig2.addExtraParam(runtimeConfigNeededException.getParam(), new String(password));
                            }
                        } else {
                            return;
                        }
                    } else {
                        LOGGER.severe("No se puede gestionar la solicitud de datos necesaria para completar la firma: " + runtimeConfigNeededException);
                        showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.154", runtimeConfigNeededException.getMessage()), runtimeConfigNeededException);
                    }
                }
                try {
                    byte[] bArrPluginsPreProcess = pluginsPreProcess(loadData(signOperationConfig2.getDataFile()), signOperationConfig2.getSignatureFormatName());
                    String algorithm = this.selectedPke.getPrivateKey().getAlgorithm();
                    String digestAlgorithm = signOperationConfig2.getDigestAlgorithm();
                    if (digestAlgorithm == null) {
                        digestAlgorithm = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_SIGNATURE_ALGORITHM);
                    }
                    try {
                        try {
                            bArrSignData = signData(bArrPluginsPreProcess, signer, signOperationConfig2, AOSignConstants.composeSignatureAlgorithmName(digestAlgorithm, algorithm), this.selectedPke, z, this.parent);
                            bArrSignData = pluginsPostProcess(bArrSignData, signOperationConfig2.getSignatureFormatName(), this.selectedPke.getCertificateChain());
                            if (fileSelectOutDir != null) {
                                String signedName = signOperationConfig2.getSigner().getSignedName(signOperationConfig2.getDataFile().getName(), "_signed");
                                String str = "";
                                if (signOperationConfig2.getDataFile() != null && !absolutePath.isEmpty()) {
                                    str = signOperationConfig2.getDataFile().getParentFile().getAbsolutePath().substring(absolutePath.length()) + File.separator;
                                }
                                try {
                                    signOperationConfig2.setSignatureFile(saveDataToFile(bArrSignData, new File(fileSelectOutDir, str + signedName), PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_MASSIVE_OVERWRITE)));
                                } catch (Exception e5) {
                                    LOGGER.log(Level.WARNING, "Error al guardar una de las firmas generadas", (Throwable) e5);
                                }
                            }
                        } catch (AOCancelledOperationException e6) {
                            return;
                        } catch (AOFormatFileException e7) {
                            LOGGER.warning("La firma o el documento no son aptos para firmar: " + e7);
                            if (z) {
                                showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.154", e7.getMessage()), e7);
                                return;
                            }
                        } catch (RuntimeConfigNeededException e8) {
                            LOGGER.warning("No se puede completar la firma sin intervencion del usuario: " + e8);
                            if (z) {
                                if (e8.getRequestType() == RuntimeConfigNeededException.RequestType.CONFIRM) {
                                    if (AOUIFactory.showConfirmDialog(this.parent, SimpleAfirmaMessages.getString(e8.getRequestorText()), SimpleAfirmaMessages.getString("SignPanel.153"), 0, 3) == 0) {
                                        this.needRelaunch = true;
                                        if (e8 instanceof CustomRuntimeConfigNeededException) {
                                            ((CustomRuntimeConfigNeededException) e8).prepareOperationWithConfirmation(signOperationConfig2.getExtraParams());
                                            return;
                                        } else {
                                            signOperationConfig2.addExtraParam(e8.getParam(), Boolean.TRUE.toString());
                                            return;
                                        }
                                    }
                                    return;
                                }
                                if (e8.getRequestType() == RuntimeConfigNeededException.RequestType.PASSWORD) {
                                    try {
                                        password2 = AOUIFactory.getPassword(SimpleAfirmaMessages.getString(e8.getRequestorText()), this.parent);
                                    } catch (AOCancelledOperationException e9) {
                                        password2 = null;
                                    }
                                    if (password2 != null) {
                                        this.needRelaunch = true;
                                        if (e8 instanceof RuntimePasswordNeededException) {
                                            ((RuntimePasswordNeededException) e8).configure(signOperationConfig2.getExtraParams(), password2);
                                            return;
                                        } else {
                                            signOperationConfig2.addExtraParam(e8.getParam(), new String(password2));
                                            return;
                                        }
                                    }
                                    return;
                                }
                                LOGGER.severe("No se puede gestionar la solicitud de datos necesaria para completar la firma: " + e8);
                                showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.154", e8.getMessage()), e8);
                                return;
                            }
                        } catch (AOCancelledSMOperationException e10) {
                            refreshKeyStore();
                            return;
                        } catch (LockedKeyStoreException e11) {
                            LOGGER.log(Level.SEVERE, "El almacen de claves o o el certificado esta bloqueado", (Throwable) e11);
                            refreshKeyStore();
                            showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.161"), e11);
                            return;
                        } catch (PinException e12) {
                            LOGGER.log(Level.WARNING, "El PIN utilizado es incorrecto", (Throwable) e12);
                            Certificate certificate = this.selectedPke.getCertificate();
                            refreshKeyStore();
                            try {
                                listSingletonList = Collections.singletonList(new EncodedCertificateFilter(Base64.encode(certificate.getEncoded())));
                            } catch (CertificateEncodingException e13) {
                                LOGGER.log(Level.SEVERE, "Error en la codificacion del certificado. No se selecionara automaticamente", (Throwable) e12);
                                listSingletonList = null;
                            }
                            try {
                                this.selectedPke = getPrivateKeyEntry(listSingletonList, true);
                                doSignature();
                                return;
                            } catch (Exception e14) {
                                LOGGER.log(Level.SEVERE, "Ocurrio un error al extraer la clave privada del certificiado seleccionado", (Throwable) e14);
                                showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.56"), e12);
                                return;
                            }
                        } catch (AuthenticationException e15) {
                            LOGGER.log(Level.SEVERE, "Error al firmar por un problema de acceso al almacen o la clave del certificado", (Throwable) e15);
                            showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.162"), e15);
                            return;
                        } catch (SingleSignatureException e16) {
                            LOGGER.warning("Error en una firma del lote, se continua con la siguiente: " + e16);
                        } catch (Exception e17) {
                            LOGGER.log(Level.SEVERE, "Error durante el proceso de firma: " + e17, (Throwable) e17);
                            if (z) {
                                showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.65"), e17);
                                return;
                            }
                        } catch (OutOfMemoryError e18) {
                            LOGGER.severe("Falta de memoria en el proceso de firma: " + e18);
                            if (z) {
                                showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.1"), e18);
                                return;
                            }
                        } catch (Error e19) {
                            LOGGER.log(Level.SEVERE, "Error interno durante el proceso de firma", (Throwable) e19);
                            if (z) {
                                showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.65"), e19);
                                return;
                            }
                        }
                    } catch (Exception e20) {
                        LOGGER.severe("El tipo de clave del certificado no esta soportado: " + e20);
                        showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.123", algorithm), null);
                        return;
                    }
                } catch (Exception e21) {
                    LOGGER.severe("Error cargando el fichero a firmar: " + e21);
                    if (z) {
                        showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.123"), e21);
                        return;
                    }
                }
            }
        }
        pluginsReset();
        if (z) {
            SignOperationConfig signOperationConfig3 = this.signConfigs.get(0);
            try {
                signOperationConfig3.setSignatureFile(saveSignature(bArrSignData, signOperationConfig3.getSigner(), signOperationConfig3.getDataFile(), this.parent));
                this.resultViewer.showResultsInfo(bArrSignData, signOperationConfig3, (X509Certificate) this.selectedPke.getCertificate());
            } catch (AOCancelledOperationException e22) {
                return;
            } catch (Exception e23) {
                LOGGER.log(Level.SEVERE, "No se ha podido guardar el resultado de la firma", (Throwable) e23);
                showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.88"), e23);
                return;
            }
        } else {
            this.resultViewer.showResultsInfo(this.signConfigs, fileSelectOutDir, (X509Certificate) this.selectedPke.getCertificate());
        }
        if (!invalidPageNumberFilesList.isEmpty()) {
            String str2 = "";
            Iterator<String> it = invalidPageNumberFilesList.iterator();
            while (it.hasNext()) {
                str2 = str2 + it.next() + "\n";
            }
            AOUIFactory.showMessageDialog(this, SimpleAfirmaMessages.getString("SignResultPanel.34") + "\n" + str2, SimpleAfirmaMessages.getString("SimpleAfirma.48"), 2);
            invalidPageNumberFilesList.clear();
        }
    }

    private void refreshKeyStore() {
        this.selectedPke = null;
        try {
            this.ksm.refresh();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error al actualizar el almacen de claves", (Throwable) e);
        }
    }

    private static byte[] signData(byte[] bArr, AOSigner aOSigner, SignOperationConfig signOperationConfig, String str, KeyStore.PrivateKeyEntry privateKeyEntry, boolean z, Component component) throws SingleSignatureException, IOException, AOException {
        byte[] bArrSignData;
        SignOperationConfig.CryptoOperation cryptoOperation = signOperationConfig.getCryptoOperation();
        Properties extraParams = signOperationConfig.getExtraParams();
        try {
            switch (cryptoOperation) {
                case COSIGN:
                    bArrSignData = aOSigner.cosign(bArr, str, privateKeyEntry.getPrivateKey(), privateKeyEntry.getCertificateChain(), extraParams);
                    break;
                case COUNTERSIGN_LEAFS:
                    bArrSignData = aOSigner.countersign(bArr, str, CounterSignTarget.LEAFS, null, privateKeyEntry.getPrivateKey(), privateKeyEntry.getCertificateChain(), extraParams);
                    break;
                case COUNTERSIGN_TREE:
                    bArrSignData = aOSigner.countersign(bArr, str, CounterSignTarget.TREE, null, privateKeyEntry.getPrivateKey(), privateKeyEntry.getCertificateChain(), extraParams);
                    break;
                default:
                    bArrSignData = aOSigner.sign(bArr, str, privateKeyEntry.getPrivateKey(), privateKeyEntry.getCertificateChain(), extraParams);
                    break;
            }
        } catch (IncorrectPageException e) {
            LOGGER.warning("El documento no dispone de las paginas donde estampar la firma visible: " + e);
            Properties properties = (Properties) extraParams.clone();
            properties.remove(PdfExtraParams.SIGNATURE_PAGE);
            properties.remove(PdfExtraParams.SIGNATURE_PAGES);
            signOperationConfig.setExtraParams(properties);
            if (!invalidPageNumberFilesList.contains(signOperationConfig.getDataFile().getName())) {
                invalidPageNumberFilesList.add(signOperationConfig.getDataFile().getName());
            }
            bArrSignData = signData(bArr, aOSigner, signOperationConfig, str, privateKeyEntry, z, component);
        } catch (InvalidSignaturePositionException e2) {
            LOGGER.warning("No es posible estampar la firma visible ya que la posicion indicada se encuentra fuera de rango " + e2);
            Properties properties2 = (Properties) extraParams.clone();
            properties2.remove(PdfExtraParams.SIGNATURE_PAGE);
            properties2.remove(PdfExtraParams.SIGNATURE_PAGES);
            properties2.remove(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_LOWER_LEFTX);
            properties2.remove(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_LOWER_LEFTY);
            properties2.remove(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_UPPER_RIGHTX);
            properties2.remove(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_UPPER_RIGHTY);
            signOperationConfig.setExtraParams(properties2);
            if (!invalidPageNumberFilesList.contains(signOperationConfig.getDataFile().getName())) {
                invalidPageNumberFilesList.add(signOperationConfig.getDataFile().getName());
            }
            bArrSignData = signData(bArr, aOSigner, signOperationConfig, str, privateKeyEntry, z, component);
        }
        return bArrSignData;
    }

    private static File saveSignature(byte[] bArr, AOSigner aOSigner, File file, Component component) throws IOException {
        GenericFileFilter genericFileFilter;
        String signedName = aOSigner.getSignedName(file.getName(), "_signed");
        ArrayList arrayList = new ArrayList();
        if (aOSigner instanceof AOPDFSigner) {
            genericFileFilter = new GenericFileFilter(new String[]{"pdf"}, SimpleAfirmaMessages.getString("SignPanel.72"));
        } else if ((aOSigner instanceof AOXAdESSigner) || (aOSigner instanceof AOFacturaESigner)) {
            genericFileFilter = new GenericFileFilter(new String[]{"xsig", "xml"}, SimpleAfirmaMessages.getString("SignPanel.76"));
        } else if (aOSigner instanceof AOOOXMLSigner) {
            if (signedName.toLowerCase().endsWith("docx")) {
                genericFileFilter = new GenericFileFilter(new String[]{"docx"}, SimpleAfirmaMessages.getString("SignPanel.91"));
            } else if (signedName.toLowerCase().endsWith("pptx")) {
                genericFileFilter = new GenericFileFilter(new String[]{"pptx"}, SimpleAfirmaMessages.getString("SignPanel.92"));
            } else if (signedName.toLowerCase().endsWith("xlsx")) {
                genericFileFilter = new GenericFileFilter(new String[]{"xlsx"}, SimpleAfirmaMessages.getString("SignPanel.93"));
            } else {
                genericFileFilter = new GenericFileFilter(new String[]{CommandLineParameters.FORMAT_OOXML}, SimpleAfirmaMessages.getString("SignPanel.94"));
            }
        } else if (aOSigner instanceof AOODFSigner) {
            if (signedName.toLowerCase().endsWith("odt")) {
                genericFileFilter = new GenericFileFilter(new String[]{"odt"}, SimpleAfirmaMessages.getString("SignPanel.96"));
            } else if (signedName.toLowerCase().endsWith("odp")) {
                genericFileFilter = new GenericFileFilter(new String[]{"odp"}, SimpleAfirmaMessages.getString("SignPanel.97"));
            } else if (signedName.toLowerCase().endsWith("ods")) {
                genericFileFilter = new GenericFileFilter(new String[]{"ods"}, SimpleAfirmaMessages.getString("SignPanel.98"));
            } else {
                genericFileFilter = new GenericFileFilter(new String[]{CommandLineParameters.FORMAT_ODF}, SimpleAfirmaMessages.getString("SignPanel.99"));
            }
        } else {
            genericFileFilter = new GenericFileFilter(new String[]{"csig", "p7s"}, SimpleAfirmaMessages.getString("SignPanel.80"));
        }
        arrayList.add(genericFileFilter);
        return AOUIFactory.getSaveDataToFile(bArr, SimpleAfirmaMessages.getString("SignPanel.81"), file.getParent(), signedName, arrayList, genericFileFilter, component);
    }

    private KeyStore.PrivateKeyEntry getPrivateKeyEntry(List<? extends CertificateFilter> list, boolean z) throws NoSuchAlgorithmException, AOCertificatesNotFoundException, KeyStoreException, UnrecoverableEntryException {
        AOKeyStoreDialog aOKeyStoreDialog = new AOKeyStoreDialog(this.ksm, this.parent, true, PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_KEYSTORE_SHOWEXPIREDCERTS), true, list, z);
        aOKeyStoreDialog.show();
        CertificateContext selectedCertificateContext = aOKeyStoreDialog.getSelectedCertificateContext();
        KeyStoreManager keyStoreManager = selectedCertificateContext.getKeyStoreManager();
        keyStoreManager.setParentComponent(this.parent);
        return keyStoreManager.getKeyEntry(selectedCertificateContext.getAlias());
    }

    private static byte[] loadData(File file) throws IOException {
        byte[] bArr = new byte[4096];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileInputStream fileInputStream = new FileInputStream(file);
        Throwable th = null;
        while (true) {
            try {
                try {
                    int i = fileInputStream.read(bArr);
                    if (i <= 0) {
                        break;
                    }
                    byteArrayOutputStream.write(bArr, 0, i);
                } catch (Throwable th2) {
                    if (fileInputStream != null) {
                        if (th != null) {
                            try {
                                fileInputStream.close();
                            } catch (Throwable th3) {
                                th.addSuppressed(th3);
                            }
                        } else {
                            fileInputStream.close();
                        }
                    }
                    throw th2;
                }
            } catch (Throwable th4) {
                th = th4;
                throw th4;
            }
        }
        if (fileInputStream != null) {
            if (0 != 0) {
                try {
                    fileInputStream.close();
                } catch (Throwable th5) {
                    th.addSuppressed(th5);
                }
            } else {
                fileInputStream.close();
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    private File selectOutDir() throws AOCancelledOperationException {
        try {
            File[] loadFiles = AOUIFactory.getLoadFiles(SimpleAfirmaMessages.getString("SignPanelSignTask.3"), this.signConfigs.get(0).getDataFile().getParent(), null, null, null, true, false, DesktopUtil.getDefaultDialogsIcon(), this.parent);
            if (loadFiles != null) {
                return loadFiles[0];
            }
            return null;
        } catch (AOCancelledOperationException e) {
            return null;
        }
    }

    private static File saveDataToFile(byte[] bArr, File file, boolean z) throws IOException {
        File file2 = file;
        if (!z) {
            int i = 1;
            while (file2.isFile()) {
                String name = file.getName();
                int iLastIndexOf = name.lastIndexOf(46);
                file2 = new File(file.getParentFile(), name.substring(0, iLastIndexOf) + '(' + i + ')' + name.substring(iLastIndexOf));
                i++;
            }
        }
        if (!file.getParentFile().isDirectory() && !file.getParentFile().mkdirs()) {
            throw new IOException("No se pudo crear el directorio de salida de la firma");
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file2);
        Throwable th = null;
        try {
            try {
                fileOutputStream.write(bArr, 0, bArr.length);
                if (fileOutputStream != null) {
                    if (0 != 0) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        fileOutputStream.close();
                    }
                }
                return file2;
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (fileOutputStream != null) {
                if (th != null) {
                    try {
                        fileOutputStream.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    fileOutputStream.close();
                }
            }
            throw th4;
        }
    }

    static void showErrorMessage(String str, Throwable th) {
        AOUIFactory.showErrorMessage(str, SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, th);
    }

    static boolean showConfirmRequest(Component component, String str) {
        return AOUIFactory.showConfirmDialog(component, str, SimpleAfirmaMessages.getString("SimpleAfirma.8"), 0, 2) == 0;
    }

    private static byte[] pluginsPreProcess(byte[] bArr, String str) {
        try {
            byte[] bArrPreSignProcess = bArr;
            for (AfirmaPlugin afirmaPlugin : SimpleAfirma.getPluginsManager().getPluginsLoadedList()) {
                if (PermissionChecker.check(afirmaPlugin.getInfo(), Permission.PRESIGN)) {
                    try {
                        bArrPreSignProcess = afirmaPlugin.preSignProcess(bArrPreSignProcess, str);
                    } catch (PluginControlledException e) {
                        LOGGER.log(Level.WARNING, "El plugin " + afirmaPlugin + " lanzo una excepcion controlada", (Throwable) e);
                        AOUIFactory.showErrorMessage(e.getMessage(), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 2, e);
                    } catch (Error | Exception e2) {
                        LOGGER.log(Level.SEVERE, "Ocurrio un error grave al preprocesar los datos con el plugin " + afirmaPlugin + ". Se continuara el proceso con el resto de plugins", e2);
                        AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.111", afirmaPlugin.toString(), e2.getMessage()), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e2);
                    }
                }
            }
            return bArrPreSignProcess;
        } catch (PluginException e3) {
            LOGGER.log(Level.SEVERE, "No se ha podido cargar el listado de plugins instalados", (Throwable) e3);
            AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.113"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 2, e3);
            return bArr;
        }
    }

    private static byte[] pluginsPostProcess(byte[] bArr, String str, Certificate[] certificateArr) {
        try {
            byte[] bArrPostSignProcess = bArr;
            for (AfirmaPlugin afirmaPlugin : SimpleAfirma.getPluginsManager().getPluginsLoadedList()) {
                if (PermissionChecker.check(afirmaPlugin.getInfo(), Permission.POSTSIGN)) {
                    try {
                        bArrPostSignProcess = afirmaPlugin.postSignProcess(bArrPostSignProcess, str, certificateArr);
                    } catch (PluginControlledException e) {
                        LOGGER.log(Level.WARNING, "El plugin " + afirmaPlugin + " lanzo una excepcion controlada", (Throwable) e);
                        AOUIFactory.showErrorMessage(e.getMessage(), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 2, e);
                    } catch (Error | Exception e2) {
                        LOGGER.log(Level.SEVERE, "Ocurrio un error grave al postprocesar la firma con el plugin " + afirmaPlugin + ". Se continuara el proceso con el resto de plugins", e2);
                        AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.112", afirmaPlugin.toString(), e2.getMessage()), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e2);
                    }
                }
            }
            return bArrPostSignProcess;
        } catch (PluginException e3) {
            LOGGER.log(Level.SEVERE, "No se ha podido cargar el listado de plugins instalados", (Throwable) e3);
            AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.114"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 2, e3);
            return bArr;
        }
    }

    private static void pluginsReset() {
        try {
            for (AfirmaPlugin afirmaPlugin : SimpleAfirma.getPluginsManager().getPluginsLoadedList()) {
                try {
                    afirmaPlugin.reset();
                } catch (PluginControlledException e) {
                    LOGGER.log(Level.WARNING, "El plugin " + afirmaPlugin + " lanzo una excepcion controlada", (Throwable) e);
                    AOUIFactory.showErrorMessage(e.getMessage(), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 2, e);
                } catch (Error | Exception e2) {
                    LOGGER.log(Level.SEVERE, "Ocurrio un error grave al reiniciar la firma con el plugin " + afirmaPlugin, e2);
                    AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.115", afirmaPlugin.toString(), e2.getMessage()), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e2);
                }
            }
        } catch (PluginException e3) {
            LOGGER.log(Level.SEVERE, "No se ha podido cargar el listado de plugins instalados", (Throwable) e3);
            AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.114"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 2, e3);
        }
    }
}
