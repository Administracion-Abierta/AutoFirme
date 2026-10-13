package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.RuntimeConfigNeededException;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.AOSignerFactory;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.keystores.CertificateFilter;
import es.gob.afirma.keystores.MultipleCertificateFilter;
import es.gob.afirma.keystores.filters.PseudonymFilter;
import es.gob.afirma.keystores.filters.SkipAuthDNIeFilter;
import es.gob.afirma.keystores.filters.rfc.KeyUsageFilter;
import es.gob.afirma.signers.cades.AOCAdESSigner;
import es.gob.afirma.signers.pades.AOPDFSigner;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.signers.xades.AOXAdESSigner;
import es.gob.afirma.signers.xades.XAdESExtraParams;
import es.gob.afirma.signvalidation.SignValider;
import es.gob.afirma.signvalidation.SignValiderFactory;
import es.gob.afirma.signvalidation.SignValidity;
import es.gob.afirma.standalone.DataAnalizerUtil;
import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirma;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import es.gob.afirma.standalone.plugins.DataProcessAction;
import es.gob.afirma.standalone.plugins.InputData;
import es.gob.afirma.standalone.ui.pdf.VisiblePdfSignatureManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.dnd.DropTarget;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignPanel.class */
public final class SignPanel extends JPanel implements LoadDataFileListener, SignatureExecutor, PluginButtonsContainer {
    private static final long serialVersionUID = -4828575650695534417L;
    private static final int MINIMUM_PANEL_WIDTH = 600;
    private static final int MINIMUM_PANEL_HEIGHT = 520;
    private UpperPanel upperPanel;
    private LowerPanel lowerPanel;
    JPanel mainPluginsButtonsPanel;
    JPanel pluginButtonsPanel;
    private final JFrame window;
    private final JButton selectButton;
    private final SimpleAfirma saf;
    private List<SignOperationConfig> signOperationConfigs;
    private boolean taskIsRunning;
    private CommonWaitDialog signWaitDialog;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static String[][] signersTypeRelation = {new String[]{"es.gob.afirma.signers.pades.AOPDFSigner", SimpleAfirmaMessages.getString("SignPanel.104")}, new String[]{"es.gob.afirma.signers.xades.AOFacturaESigner", SimpleAfirmaMessages.getString("SignPanel.105")}, new String[]{"es.gob.afirma.signers.xades.AOXAdESSigner", SimpleAfirmaMessages.getString("SignPanel.106")}, new String[]{"es.gob.afirma.signers.cades.AOCAdESSigner", SimpleAfirmaMessages.getString("SignPanel.107")}, new String[]{"es.gob.afirma.signers.odf.AOODFSigner", SimpleAfirmaMessages.getString("SignPanel.108")}, new String[]{"es.gob.afirma.signers.ooxml.AOOOXMLSigner", SimpleAfirmaMessages.getString("SignPanel.109")}};

    public SignPanel(JFrame jFrame, SimpleAfirma simpleAfirma) {
        super(true);
        this.mainPluginsButtonsPanel = null;
        this.pluginButtonsPanel = null;
        this.selectButton = new JButton();
        this.taskIsRunning = false;
        this.signWaitDialog = null;
        this.window = jFrame;
        this.saf = simpleAfirma;
        createUI();
    }

    private void createUI() {
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            setBackground(LookAndFeelManager.DEFAULT_COLOR);
        }
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(600, 520));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        this.upperPanel = new UpperPanel(this);
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridy = 0;
        add(this.upperPanel, gridBagConstraints);
        this.lowerPanel = new LowerPanel(this);
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridy++;
        add(this.lowerPanel, gridBagConstraints);
        this.mainPluginsButtonsPanel = buildMainPluginsButtonsPanel();
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.gridy++;
        add(this.mainPluginsButtonsPanel, gridBagConstraints);
        refreshPluginButtonsContainer();
        setVisible(true);
    }

    JFrame getWindow() {
        return this.window;
    }

    SimpleAfirma getSimpleAfirma() {
        return this.saf;
    }

    JButton getSelectButton() {
        return this.selectButton;
    }

    List<SignOperationConfig> getSignOperationConfigs() {
        return this.signOperationConfigs;
    }

    public void sign() {
        boolean zIsVisibleSignature;
        boolean zIsVisibleStamp;
        if (this.signOperationConfigs.size() == 1 && this.signOperationConfigs.get(0).getSignValidity() != null && this.signOperationConfigs.get(0).getSignValidity().get(0).getValidity() == SignValidity.SIGN_DETAIL_TYPE.KO && !PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_ALLOW_INVALID_SIGNATURES)) {
            AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.9"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, null);
            return;
        }
        this.signWaitDialog = new CommonWaitDialog(getSimpleAfirma().getMainFrame(), SimpleAfirmaMessages.getString("SignPanel.48"), SimpleAfirmaMessages.getString("SignPanel.50"));
        if (PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_CONFIRMTOSIGN)) {
            ConfirmSignatureDialog confirmSignatureDialog = new ConfirmSignatureDialog(this, this.signOperationConfigs.size());
            confirmSignatureDialog.setVisible(true);
            if (confirmSignatureDialog.getResult() == null) {
                return;
            }
            if (confirmSignatureDialog.getResult().booleanValue()) {
                PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_GENERAL_CONFIRMTOSIGN, false);
                try {
                    PreferencesManager.flush();
                } catch (BackingStoreException e) {
                    LOGGER.warning("No se pudo guardar la preferencia del usuario: confirmToSign");
                }
            }
        }
        setMultisignOpFromPreferences(this.signOperationConfigs);
        if (checkSignPDFOperationSign(this.signOperationConfigs)) {
            this.signWaitDialog.setMessage(SimpleAfirmaMessages.getString("SignPanelSignTask.0"));
            String certificationLevel = null;
            if (this.lowerPanel.getFilePanel() instanceof SignPanelFilePanel) {
                zIsVisibleSignature = ((SignPanelFilePanel) this.lowerPanel.getFilePanel()).isVisibleSignature();
                zIsVisibleStamp = ((SignPanelFilePanel) this.lowerPanel.getFilePanel()).isVisibleStamp();
                certificationLevel = ((SignPanelFilePanel) this.lowerPanel.getFilePanel()).getCertificationLevel();
            } else {
                zIsVisibleSignature = PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_PADES_VISIBLE);
                zIsVisibleStamp = false;
                if (PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_PADES_VISIBLE)) {
                    certificationLevel = PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_DEFAULT_CERTIFICATION_LEVEL);
                }
            }
            if (certificationLevel != null && !certificationLevel.equals("0")) {
                Iterator<SignOperationConfig> it = this.signOperationConfigs.iterator();
                while (it.hasNext()) {
                    it.next().addExtraParam(PdfExtraParams.CERTIFICATION_LEVEL, certificationLevel);
                }
            }
            ArrayList arrayList = new ArrayList(this.signOperationConfigs.size());
            Iterator<SignOperationConfig> it2 = this.signOperationConfigs.iterator();
            while (it2.hasNext()) {
                arrayList.add(it2.next().m750clone());
            }
            try {
                VisiblePdfSignatureManager.getVisibleSignatureParams(arrayList, this, zIsVisibleSignature, zIsVisibleStamp, getWindow());
                return;
            } catch (AOCancelledOperationException e2) {
                this.signWaitDialog.dispose();
                return;
            } catch (Exception e3) {
                LOGGER.log(Level.WARNING, "No se pudo crear la firma visible PDF, se creara una firma invisible", (Throwable) e3);
                initSignTask(arrayList);
                return;
            }
        }
        initSignTask(this.signOperationConfigs);
    }

    @Override // es.gob.afirma.standalone.ui.SignatureExecutor
    public void initSignTask(List<SignOperationConfig> list) {
        setCursor(new Cursor(3));
        this.taskIsRunning = true;
        new SignPanelSignTask(this, list, this.saf.getAOKeyStoreManager(), getCertFilters(), this.signWaitDialog, this, this.saf).execute();
        if (this.taskIsRunning) {
            this.signWaitDialog.setVisible(true);
        }
    }

    @Override // es.gob.afirma.standalone.ui.SignatureExecutor
    public void relaunchTask(KeyStore.PrivateKeyEntry privateKeyEntry, List<SignOperationConfig> list) {
        new SignPanelSignTask(this, list, privateKeyEntry, this.signWaitDialog, this, this.saf).execute();
    }

    @Override // es.gob.afirma.standalone.ui.SignatureExecutor
    public void finishTask() {
        this.taskIsRunning = false;
        this.signWaitDialog.dispose();
        setCursor(new Cursor(0));
    }

    public void notifyStoreReady() {
        if (this.signOperationConfigs != null && !this.signOperationConfigs.isEmpty()) {
            this.saf.setSignMenuCommandEnabled(true);
            this.lowerPanel.updateSignButtonState(true);
        }
    }

    static List<? extends CertificateFilter> getCertFilters() {
        ArrayList arrayList = new ArrayList();
        if (PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_KEYSTORE_SIGN_ONLY_CERTS)) {
            arrayList.add(new KeyUsageFilter(KeyUsageFilter.SIGN_CERT_USAGE));
        }
        if (PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_KEYSTORE_SKIP_AUTH_CERT_DNIE)) {
            arrayList.add(new SkipAuthDNIeFilter());
        }
        if (PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_KEYSTORE_ALIAS_ONLY_CERTS)) {
            arrayList.add(new PseudonymFilter(PseudonymFilter.VALUE_ONLY));
        }
        if (arrayList.size() > 1) {
            return Arrays.asList(new MultipleCertificateFilter((CertificateFilter[]) arrayList.toArray(new CertificateFilter[0])));
        }
        if (arrayList.size() == 1) {
            return arrayList;
        }
        return null;
    }

    @Override // es.gob.afirma.standalone.ui.LoadDataFileListener
    public void loadFiles(File[] fileArr, SignOperationConfig signOperationConfig) {
        setCursor(new Cursor(3));
        File[] fileArrFilterFiles = filterFiles(fileArr);
        if (fileArrFilterFiles == null || fileArrFilterFiles.length == 0) {
            LOGGER.warning("No se ha cargado ningun fichero valido");
            AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.12"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, null);
        } else {
            ArrayList arrayList = new ArrayList();
            for (File file : fileArrFilterFiles) {
                if (signOperationConfig != null) {
                    try {
                        arrayList.add(prepareSignConfig(file, signOperationConfig));
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, String.format("Error al cargar el fichero %s, se salta al siguiente", LoggerUtil.getCleanUserHomePath(file.getAbsolutePath())), (Throwable) e);
                    }
                } else {
                    arrayList.add(prepareSignConfig(file));
                }
            }
            this.lowerPanel.loadDataInfo(arrayList);
            this.lowerPanel.updateSignButtonState(this.saf.isKeyStoreReady());
            this.lowerPanel.requestFocusInWindow();
            this.signOperationConfigs = arrayList;
        }
        setCursor(new Cursor(0));
    }

    private static File[] filterFiles(File[] fileArr) {
        if (fileArr == null || fileArr.length == 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (File file : fileArr) {
            arrayList2.add(file);
        }
        for (int i = 0; i < arrayList2.size(); i++) {
            File file2 = (File) arrayList2.get(i);
            if (file2.exists() && file2.canRead()) {
                if (file2.isFile()) {
                    arrayList.add(file2);
                } else {
                    for (File file3 : file2.listFiles()) {
                        arrayList2.add(file3);
                    }
                }
            }
        }
        return (File[]) arrayList.toArray(new File[arrayList.size()]);
    }

    private static SignOperationConfig prepareSignConfig(File file) throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            Throwable th = null;
            try {
                try {
                    byte[] allBytes = Files.readAllBytes(file.toPath());
                    if (fileInputStream != null) {
                        if (0 != 0) {
                            try {
                                fileInputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            fileInputStream.close();
                        }
                    }
                    SignOperationConfig signOperationConfig = new SignOperationConfig();
                    signOperationConfig.setDataFile(file);
                    configureDataSigner(signOperationConfig, allBytes);
                    return signOperationConfig;
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (fileInputStream != null) {
                    if (th != null) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
                throw th4;
            }
        } catch (Exception e) {
            throw new IOException("No se ha podido leer el fichero", e);
        } catch (OutOfMemoryError e2) {
            throw new IOException("No hay memoria suficiente para leer el fichero", e2);
        }
    }

    private static SignOperationConfig prepareSignConfig(File file, SignOperationConfig signOperationConfig) throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            Throwable th = null;
            try {
                byte[] allBytes = Files.readAllBytes(file.toPath());
                if (fileInputStream != null) {
                    if (0 != 0) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
                SignOperationConfig signOperationConfig2 = new SignOperationConfig();
                signOperationConfig2.setDataFile(file);
                configureDataSigner(signOperationConfig2, signOperationConfig, allBytes);
                return signOperationConfig2;
            } catch (Throwable th3) {
                if (fileInputStream != null) {
                    if (0 != 0) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
                throw th3;
            }
        } catch (Exception e) {
            throw new IOException("No se ha podido leer el fichero", e);
        } catch (OutOfMemoryError e2) {
            throw new IOException("No hay memoria suficiente para leer el fichero", e2);
        }
    }

    private static void configureDataSigner(SignOperationConfig signOperationConfig, byte[] bArr) throws IOException {
        if (DataAnalizerUtil.isPDF(bArr)) {
            signOperationConfig.setFileType(FileType.PDF);
            signOperationConfig.setSigner(AOSignerFactory.getSigner(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_PDF)));
            if ((signOperationConfig.getSigner() instanceof AOPDFSigner) && signOperationConfig.getSigner().isSign(bArr)) {
                signOperationConfig.setCryptoOperation(SignOperationConfig.CryptoOperation.COSIGN);
            }
        } else if (DataAnalizerUtil.isFacturae(bArr)) {
            signOperationConfig.setFileType(FileType.FACTURAE);
            signOperationConfig.setSigner(AOSignerFactory.getSigner(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_FACTURAE)));
        } else if (DataAnalizerUtil.isOOXML(bArr)) {
            signOperationConfig.setFileType(FileType.OOXML);
            signOperationConfig.setSigner(AOSignerFactory.getSigner(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_OOXML)));
        } else if (DataAnalizerUtil.isODF(bArr)) {
            signOperationConfig.setFileType(FileType.ODF);
            signOperationConfig.setSigner(AOSignerFactory.getSigner(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_ODF)));
        } else {
            signOperationConfig.setSigner(AOSignerFactory.getSigner(bArr));
            if (signOperationConfig.getSigner() != null) {
                if (signOperationConfig.getSigner() instanceof AOXAdESSigner) {
                    signOperationConfig.setFileType(FileType.SIGN_XADES);
                } else {
                    signOperationConfig.setFileType(FileType.SIGN_CADES);
                }
                signOperationConfig.setCryptoOperation(SignOperationConfig.CryptoOperation.COSIGN);
            } else if (DataAnalizerUtil.isXML(bArr)) {
                signOperationConfig.setFileType(FileType.XML);
                signOperationConfig.setSigner(AOSignerFactory.getSigner(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_XML)));
            } else {
                signOperationConfig.setFileType(FileType.BINARY);
                signOperationConfig.setSigner(AOSignerFactory.getSigner(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_BIN)));
            }
        }
        if (signOperationConfig.getSigner().isSign(bArr, signOperationConfig.getExtraParams())) {
            setValidationInfo(bArr, signOperationConfig);
        }
        signOperationConfig.setSignatureFormatName(getSignatureName(signOperationConfig.getSigner()));
        signOperationConfig.setExtraParams(ExtraParamsHelper.loadExtraParamsForSigner(signOperationConfig.getSigner()));
        signOperationConfig.getExtraParams().put(XAdESExtraParams.CONFIRM_DIFFERENT_PROFILE, Boolean.TRUE);
    }

    private static void configureDataSigner(SignOperationConfig signOperationConfig, SignOperationConfig signOperationConfig2, byte[] bArr) throws IOException {
        String str;
        if (DataAnalizerUtil.isPDF(bArr)) {
            signOperationConfig.setFileType(FileType.PDF);
            str = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_PDF);
        } else if (DataAnalizerUtil.isFacturae(bArr)) {
            signOperationConfig.setFileType(FileType.FACTURAE);
            str = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_FACTURAE);
        } else if (DataAnalizerUtil.isOOXML(bArr)) {
            signOperationConfig.setFileType(FileType.OOXML);
            str = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_OOXML);
        } else if (DataAnalizerUtil.isODF(bArr)) {
            signOperationConfig.setFileType(FileType.ODF);
            str = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_ODF);
        } else if (DataAnalizerUtil.isXML(bArr)) {
            signOperationConfig.setFileType(FileType.XML);
            str = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_XML);
        } else {
            signOperationConfig.setFileType(FileType.BINARY);
            str = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_BIN);
        }
        if (signOperationConfig2.getSigner() != null) {
            signOperationConfig.setSigner(signOperationConfig2.getSigner());
        } else {
            signOperationConfig.setSigner(AOSignerFactory.getSigner(str));
        }
        if (signOperationConfig.getSigner().isSign(bArr, signOperationConfig2.getExtraParams())) {
            if (signOperationConfig.getSigner() instanceof AOCAdESSigner) {
                signOperationConfig.setFileType(FileType.SIGN_CADES);
            } else if (signOperationConfig.getSigner() instanceof AOXAdESSigner) {
                signOperationConfig.setFileType(FileType.SIGN_XADES);
            }
            setValidationInfo(bArr, signOperationConfig);
        }
        signOperationConfig.setSignatureFormatName(getSignatureName(signOperationConfig.getSigner()));
        signOperationConfig.setCryptoOperation(signOperationConfig2.getCryptoOperation());
        signOperationConfig.setDigestAlgorithm(signOperationConfig2.getDigestAlgorithm());
        Properties properties = signOperationConfig.getExtraParams() != null ? (Properties) signOperationConfig.getExtraParams().clone() : new Properties();
        properties.put(XAdESExtraParams.CONFIRM_DIFFERENT_PROFILE, Boolean.TRUE);
        signOperationConfig.setExtraParams(properties);
    }

    private static void setValidationInfo(byte[] bArr, SignOperationConfig signOperationConfig) {
        SignValider signValider = SignValiderFactory.getSignValider(signOperationConfig.getSigner());
        if (signValider != null) {
            Properties properties = new Properties();
            if (!PreferencesManager.getBoolean("allowShadowAttack")) {
                properties.put("allowShadowAttack", Boolean.TRUE.toString());
            }
            properties.put(PdfExtraParams.CHECK_CERTIFICATES, Boolean.TRUE.toString());
            properties.put(PdfExtraParams.PAGES_TO_CHECK_PSA, PdfExtraParams.PAGES_TO_CHECK_PSA_VALUE_ALL);
            String strBuildErrorText = null;
            List<SignValidity> arrayList = new ArrayList();
            try {
                arrayList = signValider.validate(bArr, properties);
                SignValidity signValidity = arrayList.get(0);
                if (signValidity.getValidity() == SignValidity.SIGN_DETAIL_TYPE.KO || signValidity.getValidity() == SignValidity.SIGN_DETAIL_TYPE.UNKNOWN) {
                    strBuildErrorText = buildErrorText(signValidity.getValidity(), signValidity.getError());
                }
            } catch (RuntimeConfigNeededException e) {
                arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.PENDING_CONFIRM_BY_USER, SignValidity.VALIDITY_ERROR.SUSPECTED_SIGNATURE, e));
                strBuildErrorText = e.getMessage();
            } catch (Exception e2) {
                SignValidity signValidity2 = new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.CORRUPTED_SIGN, e2);
                arrayList.add(signValidity2);
                strBuildErrorText = buildErrorText(signValidity2.getValidity(), signValidity2.getError());
            }
            signOperationConfig.setSignValidity(arrayList);
            if (strBuildErrorText != null) {
                signOperationConfig.setInvalidSignatureText(strBuildErrorText);
            }
        }
    }

    private static String buildErrorText(SignValidity.SIGN_DETAIL_TYPE sign_detail_type, SignValidity.VALIDITY_ERROR validity_error) {
        String string;
        if (sign_detail_type == SignValidity.SIGN_DETAIL_TYPE.UNKNOWN) {
            string = SimpleAfirmaMessages.getString("SignPanel.141");
        } else {
            string = SimpleAfirmaMessages.getString("SignPanel.140");
        }
        switch (validity_error) {
            case NO_DATA:
                return string + ": " + SimpleAfirmaMessages.getString("SignPanel.125");
            case CORRUPTED_SIGN:
                return string + ": " + SimpleAfirmaMessages.getString("SignPanel.126");
            case NO_MATCH_DATA:
                return string + ": " + SimpleAfirmaMessages.getString("SignPanel.127");
            case NO_SIGN:
                return string + ": " + SimpleAfirmaMessages.getString("SignPanel.128");
            case CERTIFICATE_PROBLEM:
                return string + ": " + SimpleAfirmaMessages.getString("SignPanel.129");
            case CERTIFICATE_EXPIRED:
                return string + ": " + SimpleAfirmaMessages.getString("SignPanel.130");
            case CERTIFICATE_NOT_VALID_YET:
                return string + ": " + SimpleAfirmaMessages.getString("SignPanel.131");
            case ALGORITHM_NOT_SUPPORTED:
                return string + ": " + SimpleAfirmaMessages.getString("SignPanel.132");
            case CA_NOT_SUPPORTED:
                return string + ": " + SimpleAfirmaMessages.getString("SignPanel.133");
            case CRL_PROBLEM:
                return string + ": " + SimpleAfirmaMessages.getString("SignPanel.134");
            case PDF_UNKOWN_VALIDITY:
                return string + ": " + SimpleAfirmaMessages.getString("SignPanel.135");
            case OOXML_UNKOWN_VALIDITY:
                return string + ": " + SimpleAfirmaMessages.getString("SignPanel.136");
            case ODF_UNKOWN_VALIDITY:
                return string + ": " + SimpleAfirmaMessages.getString("SignPanel.137");
            case UNKOWN_ERROR:
                return string + ": " + SimpleAfirmaMessages.getString("SignPanel.138");
            case UNKOWN_SIGNATURE_FORMAT:
                return string + ": " + SimpleAfirmaMessages.getString("SignPanel.139");
            case MODIFIED_DOCUMENT:
                return SimpleAfirmaMessages.getString("SignPanel.151");
            case OVERLAPPING_SIGNATURE:
                return SimpleAfirmaMessages.getString("SignPanel.152");
            case CERTIFIED_SIGN_REVISION:
                return string + ": " + SimpleAfirmaMessages.getString("SignPanel.159");
            default:
                return string;
        }
    }

    public static String getSignatureName(AOSigner aOSigner) {
        for (String[] strArr : signersTypeRelation) {
            try {
                if (Class.forName(strArr[0]).isInstance(aOSigner)) {
                    return strArr[1];
                }
            } catch (Exception e) {
            }
        }
        return SimpleAfirmaMessages.getString("SignPanel.110");
    }

    private JPanel buildMainPluginsButtonsPanel() {
        JPanel jPanel = new JPanel(new GridBagLayout());
        this.pluginButtonsPanel = new JPanel(new FlowLayout(0));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridy = 0;
        jPanel.add(new JSeparator(0), gridBagConstraints);
        gridBagConstraints.gridy++;
        jPanel.add(this.pluginButtonsPanel, gridBagConstraints);
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            jPanel.setBackground(LookAndFeelManager.DEFAULT_COLOR);
            this.pluginButtonsPanel.setBackground(LookAndFeelManager.DEFAULT_COLOR);
        }
        return jPanel;
    }

    @Override // es.gob.afirma.standalone.ui.PluginButtonsContainer
    public void refreshPluginButtonsContainer() {
        List<PluginGraphicButton> pluginsButtons = PluginsUiComponentsBuilder.getPluginsButtons(PluginIntegrationWindow.INPUT_DATA);
        for (PluginGraphicButton pluginGraphicButton : pluginsButtons) {
            pluginGraphicButton.getGraphicButton().addActionListener(actionEvent -> {
                try {
                    ArrayList arrayList = new ArrayList();
                    List<SignOperationConfig> signOperationConfigs = getSignOperationConfigs();
                    if (signOperationConfigs != null) {
                        for (SignOperationConfig signOperationConfig : signOperationConfigs) {
                            InputData inputData = new InputData();
                            inputData.setDataFile(signOperationConfig.getDataFile());
                            inputData.setSignatureFormat(signOperationConfig.getSignatureFormatName());
                            arrayList.add(inputData);
                        }
                    }
                    new Thread(() -> {
                        if (pluginGraphicButton.getButton().getAction() instanceof DataProcessAction) {
                            ((DataProcessAction) pluginGraphicButton.getButton().getAction()).processData((InputData[]) arrayList.toArray(new InputData[arrayList.size()]), SwingUtilities.getWindowAncestor(this));
                        } else {
                            pluginGraphicButton.getButton().getAction().start(SwingUtilities.getWindowAncestor(this));
                        }
                    }).start();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "La accion del boton devolvio un error", (Throwable) e);
                }
            });
        }
        EventQueue.invokeLater(() -> {
            if (pluginsButtons.isEmpty()) {
                this.mainPluginsButtonsPanel.setVisible(false);
                return;
            }
            this.mainPluginsButtonsPanel.setVisible(false);
            this.pluginButtonsPanel.removeAll();
            Iterator it = pluginsButtons.iterator();
            while (it.hasNext()) {
                this.pluginButtonsPanel.add(((PluginGraphicButton) it.next()).getGraphicButton());
            }
            this.mainPluginsButtonsPanel.setVisible(true);
        });
    }

    private static boolean checkSignPDFOperationSign(List<SignOperationConfig> list) {
        Iterator<SignOperationConfig> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().getSigner() instanceof AOPDFSigner) {
                return true;
            }
        }
        return false;
    }

    private static void setMultisignOpFromPreferences(List<SignOperationConfig> list) {
        SignOperationConfig.CryptoOperation cryptoOperation = null;
        SignOperationConfig.CryptoOperation cryptoOperation2 = null;
        for (SignOperationConfig signOperationConfig : list) {
            if (signOperationConfig.getFileType() == FileType.SIGN_CADES) {
                if (cryptoOperation == null) {
                    cryptoOperation = getCryptoOperation(PreferencesManager.PREFERENCE_CADES_MULTISIGN);
                }
                signOperationConfig.setCryptoOperation(cryptoOperation);
            } else if (signOperationConfig.getFileType() == FileType.SIGN_XADES) {
                if (cryptoOperation2 == null) {
                    cryptoOperation2 = getCryptoOperation(PreferencesManager.PREFERENCE_XADES_MULTISIGN);
                }
                signOperationConfig.setCryptoOperation(cryptoOperation2);
            }
        }
    }

    private static SignOperationConfig.CryptoOperation getCryptoOperation(String str) {
        String str2 = PreferencesManager.get(str);
        if ("cosign".equals(str2)) {
            return SignOperationConfig.CryptoOperation.COSIGN;
        }
        return PreferencesManager.VALUE_MULTISIGN_COUNTERSIGN_LEAFS.equals(str2) ? SignOperationConfig.CryptoOperation.COUNTERSIGN_LEAFS : SignOperationConfig.CryptoOperation.COUNTERSIGN_TREE;
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignPanel$UpperPanel.class */
    private final class UpperPanel extends JPanel {
        private static final long serialVersionUID = 533243192995645135L;
        private final LoadDataFileListener loadDataListener;

        UpperPanel(LoadDataFileListener loadDataFileListener) {
            super(true);
            this.loadDataListener = loadDataFileListener;
            createUI();
        }

        private void createUI() {
            setLayout(new BorderLayout(5, 5));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            SignPanel.this.getSelectButton().setText(SimpleAfirmaMessages.getString("SignPanel.32"));
            SignPanel.this.getSelectButton().setMnemonic('S');
            SignPanel.this.getSelectButton().getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("SignPanel.33"));
            SignPanel.this.getSelectButton().getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("SignPanel.34"));
            SignPanel.this.getSelectButton().requestFocusInWindow();
            SignPanel.this.getSelectButton().addActionListener(actionEvent -> {
                try {
                    this.loadDataListener.loadFiles(AOUIFactory.getLoadFiles(SimpleAfirmaMessages.getString("SignPanel.35"), null, null, null, null, false, true, DesktopUtil.getDefaultDialogsIcon(), this), null);
                } catch (AOCancelledOperationException e) {
                }
            });
            JLabel jLabel = new JLabel(SimpleAfirmaMessages.getString("SignPanel.14"));
            jLabel.setFocusable(false);
            jLabel.setFont(jLabel.getFont().deriveFont(0, 26.0f));
            jLabel.setLabelFor(SignPanel.this.getSelectButton());
            add(jLabel, "First");
            String string = SimpleAfirmaMessages.getString("SignPanel.40");
            JLabel jLabel2 = new JLabel(string);
            jLabel2.setLabelFor(SignPanel.this.getSelectButton());
            jLabel2.setFocusable(false);
            getAccessibleContext().setAccessibleDescription(string);
            JPanel jPanel = new JPanel(new BorderLayout());
            jPanel.add(jLabel2, "First");
            add(jPanel, "Center");
            JPanel jPanel2 = new JPanel(new FlowLayout(0), true);
            jPanel2.add(SignPanel.this.getSelectButton());
            add(jPanel2, "Last");
            if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                setBackground(LookAndFeelManager.DEFAULT_COLOR);
                jPanel.setBackground(LookAndFeelManager.DEFAULT_COLOR);
                jPanel2.setBackground(LookAndFeelManager.DEFAULT_COLOR);
                jLabel.setForeground(new Color(3399));
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignPanel$LowerPanel.class */
    private final class LowerPanel extends JPanel {
        private static final long serialVersionUID = 533243192995645135L;
        private final LoadDataFileListener loadDataListener;
        private JScrollPane filePanel;
        private final JButton signButton;
        private DropTarget dropTarget;

        LowerPanel(LoadDataFileListener loadDataFileListener) {
            super(true);
            this.dropTarget = null;
            this.loadDataListener = loadDataFileListener;
            this.signButton = new JButton();
            SwingUtilities.invokeLater(() -> {
                createUI();
            });
        }

        void createUI() {
            setLayout(new BorderLayout(5, 5));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            Color color = Color.WHITE;
            if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST && !Platform.OS.MACOSX.equals(Platform.getOS())) {
                color = LookAndFeelManager.DEFAULT_COLOR;
            }
            ResizingTextPanel resizingTextPanel = new ResizingTextPanel(SimpleAfirmaMessages.getString("SignPanel.41"));
            resizingTextPanel.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("SignPanel.42"));
            resizingTextPanel.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("SignPanel.43"));
            resizingTextPanel.setToolTipText(SimpleAfirmaMessages.getString("SignPanel.42"));
            resizingTextPanel.setFocusable(false);
            this.dropTarget = new DropTarget(resizingTextPanel, 1, new DropDataFileListener(this.loadDataListener), true);
            this.filePanel = new JScrollPane();
            this.filePanel.setBackground(color);
            this.filePanel.getViewport().setBackground(color);
            this.filePanel.setBorder(BorderFactory.createLineBorder(LookAndFeelManager.WINDOWS_HIGH_CONTRAST ? Color.WHITE : Color.GRAY));
            this.filePanel.getHorizontalScrollBar().setUnitIncrement(30);
            this.filePanel.getVerticalScrollBar().setUnitIncrement(30);
            if (Platform.OS.MACOSX.equals(Platform.getOS())) {
                this.filePanel.setVerticalScrollBarPolicy(22);
                this.filePanel.setHorizontalScrollBarPolicy(32);
            } else {
                this.filePanel.setVerticalScrollBarPolicy(20);
                this.filePanel.setHorizontalScrollBarPolicy(30);
            }
            this.filePanel.getVerticalScrollBar().setUnitIncrement(16);
            this.filePanel.getHorizontalScrollBar().setUnitIncrement(16);
            this.filePanel.getViewport().setDropTarget(this.dropTarget);
            this.filePanel.setViewportView(resizingTextPanel);
            add(this.filePanel, "Center");
            JPanel jPanel = new JPanel(true);
            this.signButton.setMargin(new Insets(0, 30, 0, 30));
            this.signButton.setText(SimpleAfirmaMessages.getString("SignPanel.45"));
            this.signButton.setMnemonic('F');
            this.signButton.setEnabled(false);
            jPanel.add(this.signButton);
            this.signButton.addActionListener(actionEvent -> {
                SignPanel.this.sign();
            });
            if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                setBackground(LookAndFeelManager.DEFAULT_COLOR);
                resizingTextPanel.setBackground(Color.DARK_GRAY);
                resizingTextPanel.setForeground(Color.LIGHT_GRAY);
                jPanel.setBackground(LookAndFeelManager.DEFAULT_COLOR);
            }
            add(jPanel, "Last");
        }

        public void loadDataInfo(List<SignOperationConfig> list) {
            if (list.size() == 1) {
                this.filePanel.setViewportView(new SignPanelFilePanel(list.get(0)));
            } else if (list.size() > 1) {
                this.filePanel.setViewportView(new SignPanelMultiFilePanel(list));
            }
            add(this.filePanel);
            revalidate();
        }

        public void updateSignButtonState(boolean z) {
            if (z) {
                this.signButton.setIcon((Icon) null);
                this.signButton.setText(SimpleAfirmaMessages.getString("SignPanel.45"));
                this.signButton.setToolTipText((String) null);
                this.signButton.requestFocusInWindow();
            } else {
                this.signButton.setText("");
                this.signButton.setIcon(new ImageIcon(getClass().getResource("/resources/progress.gif")));
                this.signButton.setToolTipText(SimpleAfirmaMessages.getString("SignPanel.13"));
            }
            this.signButton.setEnabled(z);
        }

        JPanel getFilePanel() {
            return this.filePanel.getViewport().getView();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignPanel$OnlyFileFilter.class */
    static class OnlyFileFilter implements FileFilter {
        OnlyFileFilter() {
        }

        @Override // java.io.FileFilter
        public boolean accept(File file) {
            return file.isFile() && file.canRead();
        }
    }
}
