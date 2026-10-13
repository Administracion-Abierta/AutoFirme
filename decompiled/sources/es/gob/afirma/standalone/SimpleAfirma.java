package es.gob.afirma.standalone;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.LogManager;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.BoundedBufferedReader;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.misc.http.SslSecurityManager;
import es.gob.afirma.core.prefs.KeyStorePreferencesManager;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.keystores.AOKeyStore;
import es.gob.afirma.keystores.AOKeyStoreManager;
import es.gob.afirma.keystores.AOKeyStoreManagerFactory;
import es.gob.afirma.keystores.AOKeystoreAlternativeException;
import es.gob.afirma.signers.cades.AOCAdESSigner;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.signers.pkcs7.ObtainContentSignedData;
import es.gob.afirma.signers.xml.XmlDSigProviderHelper;
import es.gob.afirma.signvalidation.SignValider;
import es.gob.afirma.signvalidation.SignValiderFactory;
import es.gob.afirma.signvalidation.SignValidity;
import es.gob.afirma.signvalidation.ValidateBinarySignature;
import es.gob.afirma.signvalidation.ValidatePdfSignature;
import es.gob.afirma.standalone.configurator.common.ConfigUpdaterManager;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import es.gob.afirma.standalone.plugins.manager.PluginsManager;
import es.gob.afirma.standalone.protocol.ProtocolInvocationLauncher;
import es.gob.afirma.standalone.ui.ClosePanel;
import es.gob.afirma.standalone.ui.DNIeWaitPanel;
import es.gob.afirma.standalone.ui.MainMenu;
import es.gob.afirma.standalone.ui.MainScreen;
import es.gob.afirma.standalone.ui.SignDetailPanel;
import es.gob.afirma.standalone.ui.SignOperationConfig;
import es.gob.afirma.standalone.ui.SignPanel;
import es.gob.afirma.standalone.ui.SignResultListPanel;
import es.gob.afirma.standalone.ui.SignatureResultViewer;
import es.gob.afirma.standalone.updater.Updater;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileLock;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/SimpleAfirma.class */
public final class SimpleAfirma implements PropertyChangeListener, WindowListener, SignatureResultViewer {
    private static final int DEFAULT_WINDOW_WIDTH = 780;
    private static final int DEFAULT_WINDOW_HEIGHT = 580;
    private static final String IP_DISCOVERY_AUTOMATION = "http://checkip.amazonaws.com";
    private static final String SYSTEM_PROPERTY_DEBUG_FILE = "afirma_debug";
    private static final String SYSTEM_PROPERTY_DEBUG_LEVEL = "afirma_debug_level";
    private static final String SYSTEM_PROPERTY_IGNORE_VIRTUAL_READERS = "ignoreVirtualReaders";
    public static final String APPLICATION_HOME;
    private static final String PLUGINS_DIRNAME = "plugins";
    private static final String PROTOCOL_URL_START_LOWER_CASE = "afirma://";
    private static final String WEBSOCKET_REQUEST_PREFIX = "afirma://websocket";
    private static boolean updatesEnabled;
    public static final String AVOID_UPDATE_CHECK = "es.gob.afirma.doNotCheckUpdates";
    public static final String AVOID_UPDATE_CHECK_ENV = "AUTOFIRMA_AVOID_UPDATE_CHECK";
    private static final String[] SUPPORTED_JAVA_VERSIONS;
    public static final boolean DEBUG = false;
    static final Logger LOGGER;
    private Container container;
    private JPanel currentPanel;
    private AOKeyStoreManager ksManager;
    private static final PluginsManager pluginsManager;
    private static Locale[] locales;
    private static String version;
    private final JFrame window = new MainScreen();
    private final MainMenu mainMenu = new MainMenu(this.window, this);

    static {
        try {
            LogManager.install(LogManager.App.AUTOFIRMA);
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").severe("No ha sido posible instalar el gestor de registro: " + e);
        }
        APPLICATION_HOME = Platform.getUserHome() + File.separator + LogManager.SUBDIR + File.separator + "Autofirma";
        updatesEnabled = true;
        SUPPORTED_JAVA_VERSIONS = new String[]{"1.8", "9.0", "10.0", "11.0", "17.0", "21"};
        LOGGER = Logger.getLogger("es.gob.afirma");
        pluginsManager = new PluginsManager(getPluginsDir());
        locales = new Locale[]{Locale.getDefault()};
        version = null;
    }

    public Frame getMainFrame() {
        return this.window;
    }

    public SimpleAfirma() {
        XmlDSigProviderHelper.configureXmlDSigProvider();
        System.setProperty(SYSTEM_PROPERTY_IGNORE_VIRTUAL_READERS, Boolean.TRUE.toString());
    }

    public boolean isKeyStoreReady() {
        return this.ksManager != null;
    }

    synchronized void setKeyStoreManager(AOKeyStoreManager aOKeyStoreManager) {
        if (aOKeyStoreManager != null) {
            LOGGER.info("Establecido KeyStoreManager: " + aOKeyStoreManager);
            this.ksManager = aOKeyStoreManager;
            if (this.currentPanel instanceof SignPanel) {
                ((SignPanel) this.currentPanel).notifyStoreReady();
                return;
            }
            return;
        }
        LOGGER.info("No se ha podido inicializar el almacen por defecto");
        AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.6"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, null);
    }

    void initGUI(File file, SignOperationConfig signOperationConfig) {
        String string = PreferencesManager.get(PreferencesManager.PREFERENCES_LOCALE);
        if (string == null || string.isEmpty()) {
            string = Locale.getDefault().toString();
        }
        setDefaultLocale(buildLocale(string));
        boolean z = file == null && !PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_HIDE_DNIE_START_SCREEN);
        if (z) {
            try {
                List list = TerminalFactory.getDefault().terminals().list();
                if (list.isEmpty()) {
                    z = false;
                } else if (Platform.getOS() == Platform.OS.WINDOWS && list.size() == 1 && ((CardTerminal) list.get(0)).getName().startsWith("Windows Hello")) {
                    z = false;
                }
            } catch (Exception e) {
                LOGGER.info("No se ha podido obtener la lista de lectores de tarjetas del sistema: " + e);
                z = false;
            }
        }
        this.window.setTitle(SimpleAfirmaMessages.getString("SimpleAfirma.10", getVersion()));
        if (z) {
            LOGGER.info("Mostramos pantalla del DNIe");
            this.currentPanel = new DNIeWaitPanel(this);
            ((MainScreen) this.window).showMainScreen(this, this.currentPanel, 780, 580);
            this.container = this.window;
            this.container.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("DNIeWaitPanel.3") + SimpleAfirmaMessages.getString("DNIeWaitPanel.7"));
            return;
        }
        LOGGER.info("Mostramos pantalla principal");
        this.currentPanel = new SignPanel(this.window, this);
        ((MainScreen) this.window).showMainScreen(this, this.currentPanel, 780, 580);
        this.container = this.window;
        loadDefaultKeyStore();
        configureMenuBar();
        if (file != null) {
            loadFileToSign(file, signOperationConfig);
        }
    }

    private void configureMenuBar() {
        if (this.window != null) {
            if (Platform.OS.MACOSX.equals(Platform.getOS())) {
                this.window.getRootPane().putClientProperty("Window.documentFile", (Object) null);
            }
            this.mainMenu.setEnabledOpenCommand(true);
            this.window.setJMenuBar(this.mainMenu);
        }
    }

    private void loadDefaultKeyStore() {
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_KEYSTORE_DEFAULT_STORE);
        if (this.ksManager != null) {
            if (PreferencesManager.VALUE_KEYSTORE_DEFAULT.equals(str)) {
                LOGGER.info("Se omite la carga concurrente de almacen por haberse hecho una precarga previa");
                return;
            }
            this.ksManager = null;
        }
        LOGGER.info("Cargaremos el almacen de claves por defecto");
        this.container.setCursor(new Cursor(3));
        try {
            new SimpleKeyStoreManagerWorker(this, null, false, false).execute();
        } catch (Exception e) {
            LOGGER.severe("No se pudo abrir el almacen por defecto del entorno operativo: " + e);
            AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.42"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e);
        } finally {
            this.container.setCursor(new Cursor(0));
        }
    }

    @Override // java.beans.PropertyChangeListener
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (DNIeWaitPanel.PROP_DNIE_REJECTED.equals(propertyChangeEvent.getPropertyName())) {
            loadDefaultKeyStore();
            loadMainApp();
            return;
        }
        if (DNIeWaitPanel.PROP_HELP_REQUESTED.equals(propertyChangeEvent.getPropertyName())) {
            showHelp("Autofirma.html");
            return;
        }
        if (DNIeWaitPanel.PROP_DNIE_REQUESTED.equals(propertyChangeEvent.getPropertyName())) {
            this.container.setCursor(new Cursor(3));
            try {
                new SimpleKeyStoreManagerWorker(this, null, true, this.ksManager != null).execute();
            } catch (Exception e) {
                LOGGER.severe("Fallo la inicializacion del DNIe, se intentara el almacen por defecto del sistema: " + e);
                loadDefaultKeyStore();
            } finally {
                this.container.setCursor(new Cursor(0));
            }
            loadMainApp();
        }
    }

    public void loadMainApp() {
        this.window.setTitle(SimpleAfirmaMessages.getString("SimpleAfirma.10", getVersion()));
        configureMenuBar();
        SignPanel signPanel = new SignPanel(this.window, this);
        if (this.container instanceof MainScreen) {
            this.container.replaceShowingPanel(signPanel);
        } else {
            this.container.add(signPanel, "Center");
        }
        if (this.currentPanel != null) {
            this.currentPanel.setVisible(false);
            this.currentPanel.setFocusable(false);
            this.currentPanel.setEnabled(false);
        }
        this.currentPanel = signPanel;
        this.container.requestFocus();
        this.container.repaint();
    }

    public void windowClosing(WindowEvent windowEvent) {
        askForClosing();
    }

    public void windowOpened(WindowEvent windowEvent) {
    }

    public void windowClosed(WindowEvent windowEvent) {
    }

    public void windowActivated(WindowEvent windowEvent) {
    }

    public void windowIconified(WindowEvent windowEvent) {
    }

    public void windowDeiconified(WindowEvent windowEvent) {
    }

    public void windowDeactivated(WindowEvent windowEvent) {
    }

    public void closeApplication(int i) {
        if (this.window != null) {
            this.window.dispose();
        }
        System.exit(i);
    }

    public static void forceCloseApplication(int i) {
        Runtime.getRuntime().halt(i);
    }

    public synchronized AOKeyStoreManager getAOKeyStoreManager() {
        String lastSelectedKeystoreLib = null;
        if (this.ksManager == null) {
            try {
                AOKeyStore defaultKeyStoreType = SimpleKeyStoreManager.getDefaultKeyStoreType();
                if (defaultKeyStoreType.equals(AOKeyStore.PKCS12) || defaultKeyStoreType.equals(AOKeyStore.PKCS11)) {
                    lastSelectedKeystoreLib = PreferencesManager.get(PreferencesManager.PREFERENCE_LOCAL_KEYSTORE_PATH);
                }
                this.ksManager = AOKeyStoreManagerFactory.getAOKeyStoreManager(defaultKeyStoreType, lastSelectedKeystoreLib, null, defaultKeyStoreType.getStorePasswordCallback(this), this);
            } catch (AOCancelledOperationException e) {
                AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.56"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, e);
                AOKeyStore defaultKeyStoreTypeByOs = AOKeyStore.getDefaultKeyStoreTypeByOs(Platform.getOS());
                try {
                    this.ksManager = AOKeyStoreManagerFactory.getAOKeyStoreManager(defaultKeyStoreTypeByOs, lastSelectedKeystoreLib, null, defaultKeyStoreTypeByOs.getStorePasswordCallback(this), this);
                } catch (Exception e2) {
                    LOGGER.log(Level.SEVERE, "Error al seleccionar el almacen del sistema", (Throwable) e2);
                    AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.6"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, e2);
                }
            } catch (AOKeystoreAlternativeException e3) {
                LOGGER.log(Level.SEVERE, "Error al seleccionar el tipo de almacen", (Throwable) e3);
                AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.54"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, e3);
                AOKeyStore defaultKeyStoreTypeByOs2 = AOKeyStore.getDefaultKeyStoreTypeByOs(Platform.getOS());
                try {
                    this.ksManager = AOKeyStoreManagerFactory.getAOKeyStoreManager(defaultKeyStoreTypeByOs2, lastSelectedKeystoreLib, null, defaultKeyStoreTypeByOs2.getStorePasswordCallback(this), this);
                } catch (Exception e4) {
                    LOGGER.log(Level.SEVERE, "Error al seleccionar el almacen del sistema", (Throwable) e4);
                    AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.6"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, e4);
                }
            } catch (IOException e5) {
                LOGGER.log(Level.SEVERE, "Error al leer el almacen", (Throwable) e5);
                AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.56"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, e5);
                AOKeyStore defaultKeyStoreTypeByOs3 = AOKeyStore.getDefaultKeyStoreTypeByOs(Platform.getOS());
                try {
                    this.ksManager = AOKeyStoreManagerFactory.getAOKeyStoreManager(defaultKeyStoreTypeByOs3, lastSelectedKeystoreLib, null, defaultKeyStoreTypeByOs3.getStorePasswordCallback(this), this);
                } catch (Exception e6) {
                    LOGGER.log(Level.SEVERE, "Error al seleccionar el almacen del sistema", (Throwable) e5);
                    AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.6"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, e6);
                }
            }
        } else {
            String lastSelectedKeystore = KeyStorePreferencesManager.getLastSelectedKeystore();
            if (lastSelectedKeystore != null && !lastSelectedKeystore.equals(this.ksManager.getType().getName())) {
                try {
                    AOKeyStore lastSelectedKeystore2 = SimpleKeyStoreManager.getLastSelectedKeystore();
                    if (lastSelectedKeystore2.equals(AOKeyStore.PKCS12) || lastSelectedKeystore2.equals(AOKeyStore.PKCS11)) {
                        lastSelectedKeystoreLib = KeyStorePreferencesManager.getLastSelectedKeystoreLib();
                    }
                    this.ksManager = AOKeyStoreManagerFactory.getAOKeyStoreManager(lastSelectedKeystore2, lastSelectedKeystoreLib, null, lastSelectedKeystore2.getStorePasswordCallback(this), this);
                } catch (AOCancelledOperationException e7) {
                    AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.57"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.WARNING_MESSAGE, e7);
                    AOKeyStore defaultKeyStoreTypeByOs4 = AOKeyStore.getDefaultKeyStoreTypeByOs(Platform.getOS());
                    try {
                        this.ksManager = AOKeyStoreManagerFactory.getAOKeyStoreManager(defaultKeyStoreTypeByOs4, lastSelectedKeystoreLib, null, defaultKeyStoreTypeByOs4.getStorePasswordCallback(this), this);
                    } catch (Exception e8) {
                        LOGGER.log(Level.SEVERE, "Error al seleccionar el almacen del sistema", (Throwable) e8);
                        AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.6"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, e8);
                    }
                } catch (AOKeystoreAlternativeException e9) {
                    LOGGER.log(Level.SEVERE, "Error al seleccionar el tipo de almacen", (Throwable) e9);
                    AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.54"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, e9);
                    AOKeyStore defaultKeyStoreTypeByOs5 = AOKeyStore.getDefaultKeyStoreTypeByOs(Platform.getOS());
                    try {
                        this.ksManager = AOKeyStoreManagerFactory.getAOKeyStoreManager(defaultKeyStoreTypeByOs5, lastSelectedKeystoreLib, null, defaultKeyStoreTypeByOs5.getStorePasswordCallback(this), this);
                    } catch (Exception e10) {
                        LOGGER.log(Level.SEVERE, "Error al seleccionar el almacen del sistema", (Throwable) e10);
                        AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.6"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, e10);
                    }
                } catch (IOException e11) {
                    LOGGER.log(Level.SEVERE, "Error al leer el almacen", (Throwable) e11);
                    AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.56"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, e11);
                    AOKeyStore defaultKeyStoreTypeByOs6 = AOKeyStore.getDefaultKeyStoreTypeByOs(Platform.getOS());
                    try {
                        this.ksManager = AOKeyStoreManagerFactory.getAOKeyStoreManager(defaultKeyStoreTypeByOs6, lastSelectedKeystoreLib, null, defaultKeyStoreTypeByOs6.getStorePasswordCallback(this), this);
                    } catch (Exception e12) {
                        LOGGER.log(Level.SEVERE, "Error al seleccionar el almacen del sistema", (Throwable) e11);
                        AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.6"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, e12);
                    }
                }
            }
        }
        return this.ksManager;
    }

    /* JADX WARN: Not initialized variable reg: 16, insn: 0x00ab: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r16 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:23:0x00ab */
    /* JADX WARN: Not initialized variable reg: 17, insn: 0x00b0: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r17 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:25:0x00b0 */
    @Override // es.gob.afirma.standalone.ui.SignatureResultViewer
    public void showResultsInfo(byte[] bArr, SignOperationConfig signOperationConfig, X509Certificate x509Certificate) {
        InputStream inputStream;
        Throwable th;
        Properties properties;
        this.mainMenu.setEnabledSignCommand(false);
        this.mainMenu.setEnabledOpenCommand(false);
        List<SignValidity> arrayList = new ArrayList();
        List<SignValidity> arrayList2 = new ArrayList();
        SignValider signValider = SignValiderFactory.getSignValider(bArr);
        if (signValider != null) {
            try {
                if (signValider instanceof ValidateBinarySignature) {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(signOperationConfig.getDataFile());
                        Throwable th2 = null;
                        AOCAdESSigner aOCAdESSigner = new AOCAdESSigner();
                        byte[] dataFromInputStream = AOUtil.getDataFromInputStream(fileInputStream);
                        if (aOCAdESSigner.isSign(dataFromInputStream)) {
                            arrayList2 = ValidateBinarySignature.validate(bArr, ObtainContentSignedData.obtainData(bArr));
                        } else {
                            arrayList2 = ValidateBinarySignature.validate(bArr, dataFromInputStream);
                        }
                        if (fileInputStream != null) {
                            if (0 != 0) {
                                try {
                                    fileInputStream.close();
                                } catch (Throwable th3) {
                                    th2.addSuppressed(th3);
                                }
                            } else {
                                fileInputStream.close();
                            }
                        }
                    } catch (Throwable th4) {
                        if (inputStream != null) {
                            if (th != null) {
                                try {
                                    inputStream.close();
                                } catch (Throwable th5) {
                                    th.addSuppressed(th5);
                                }
                            } else {
                                inputStream.close();
                            }
                        }
                        throw th4;
                    }
                } else if (signValider instanceof ValidatePdfSignature) {
                    if (signOperationConfig.getExtraParams() != null) {
                        properties = signOperationConfig.getExtraParams();
                    } else {
                        properties = new Properties();
                    }
                    properties.setProperty(PdfExtraParams.CHECK_CERTIFICATES, Boolean.toString(true));
                    arrayList2 = signValider.validate(bArr, properties);
                } else {
                    arrayList2 = signValider.validate(bArr);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "No se ha podido validar el documento correctamente", (Throwable) e);
            }
        }
        SignValidity signValidity = arrayList2.get(0);
        if (signValidity != null && (SignValidity.SIGN_DETAIL_TYPE.KO.equals(signValidity.getValidity()) || SignValidity.SIGN_DETAIL_TYPE.UNKNOWN.equals(signValidity.getValidity()))) {
            arrayList = arrayList2;
        } else {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.GENERATED, null));
        }
        SignDetailPanel signDetailPanel = new SignDetailPanel(this, bArr, signOperationConfig, x509Certificate, arrayList, null);
        if (this.container instanceof MainScreen) {
            this.container.replaceShowingPanel(signDetailPanel);
        } else {
            this.container.add(signDetailPanel, "Center");
        }
        if (this.window != null) {
            this.window.getRootPane().putClientProperty("Window.documentFile", signOperationConfig.getSignatureFile());
            this.window.setTitle(SimpleAfirmaMessages.getString("SimpleAfirma.10", getVersion()) + " - " + signOperationConfig.getSignatureFile().getName());
        }
        if (this.currentPanel != null) {
            this.currentPanel.setVisible(false);
            this.currentPanel.setFocusable(false);
            this.currentPanel.setEnabled(false);
        }
        this.container.repaint();
        this.container.requestFocusInWindow();
        this.currentPanel = signDetailPanel;
        if (this.window != null) {
            this.window.repaint();
        }
    }

    @Override // es.gob.afirma.standalone.ui.SignatureResultViewer
    public void showResultsInfo(List<SignOperationConfig> list, File file, X509Certificate x509Certificate) {
        this.mainMenu.setEnabledSignCommand(false);
        this.mainMenu.setEnabledOpenCommand(false);
        SignResultListPanel signResultListPanel = new SignResultListPanel(this, list, file, x509Certificate);
        if (this.container instanceof MainScreen) {
            this.container.replaceShowingPanel(signResultListPanel);
        } else {
            this.container.add(signResultListPanel, "Center");
        }
        if (this.window != null) {
            this.window.getRootPane().putClientProperty("Window.documentFile", file);
            this.window.setTitle(SimpleAfirmaMessages.getString("SimpleAfirma.10", getVersion()) + " - " + file.getAbsolutePath());
        }
        if (this.currentPanel != null) {
            this.currentPanel.setVisible(false);
            this.currentPanel.setFocusable(false);
            this.currentPanel.setEnabled(false);
        }
        this.container.repaint();
        this.container.requestFocusInWindow();
        this.currentPanel = signResultListPanel;
        if (this.window != null) {
            this.window.repaint();
        }
    }

    private static Locale buildLocale(String str) {
        String[] strArrSplit = str.split("_");
        if (strArrSplit.length == 1) {
            return new Locale(strArrSplit[0]);
        }
        if (strArrSplit.length == 2) {
            return new Locale(strArrSplit[0], strArrSplit[1]);
        }
        return new Locale(strArrSplit[0], strArrSplit[1], strArrSplit[2]);
    }

    public static Locale[] getAvailableLocales() {
        if (locales != null) {
            return (Locale[]) locales.clone();
        }
        return null;
    }

    public static void setDefaultLocale(Locale locale) {
        if (locale != null) {
            Locale.setDefault(locale);
            PreferencesManager.put(PreferencesManager.PREFERENCES_LOCALE, locale.toString());
            SimpleAfirmaMessages.changeLocale();
        }
    }

    public void setSignMenuCommandEnabled(boolean z) {
        if (this.mainMenu != null) {
            this.mainMenu.setEnabledSignCommand(z);
        }
    }

    public void signLoadedFile() {
        if (this.currentPanel instanceof SignPanel) {
            ((SignPanel) this.currentPanel).sign();
        }
    }

    public static void showHelp(String str) {
        File file = new File(APPLICATION_HOME + File.separator + "help");
        if (!HelpResourceManager.isLocalHelpUpdated(file)) {
            try {
                HelpResourceManager.extractHelpResources(file);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "No se han podido copiar los ficheros de ayuda a disco", (Throwable) e);
            }
        }
        String str2 = file + File.separator + "index.html";
        try {
            FileInputStream fileInputStream = new FileInputStream(str2);
            Throwable th = null;
            try {
                try {
                    Desktop.getDesktop().browse(new URI(HelpResourceManager.createHelpFileLauncher(str2 + "?redirectPage=" + str)));
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
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "No se ha podido leer el archivo de ayuda correctamente", (Throwable) e2);
        }
    }

    public void loadFileToSign(File file, SignOperationConfig signOperationConfig) {
        if (this.currentPanel instanceof SignPanel) {
            try {
                ((SignPanel) this.currentPanel).loadFiles(new File[]{file}, signOperationConfig);
            } catch (Exception e) {
                AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.0"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e);
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:111:0x0246 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:65:0x01b1 A[Catch: HeadlessException -> 0x02f5, Exception -> 0x0309, TryCatch #10 {Exception -> 0x0309, HeadlessException -> 0x02f5, blocks: (B:58:0x0164, B:60:0x0169, B:62:0x0178, B:64:0x01aa, B:65:0x01b1, B:67:0x01b7, B:69:0x01c9, B:70:0x01d8, B:72:0x01eb, B:74:0x01f2, B:75:0x0208, B:77:0x0221, B:78:0x023c, B:80:0x0246, B:83:0x0288, B:84:0x0291, B:87:0x02b8, B:86:0x029d, B:82:0x026d, B:88:0x02d1), top: B:116:0x0164 }] */
    /* JADX WARN: Code duplicated, block: B:67:0x01b7 A[Catch: HeadlessException -> 0x02f5, Exception -> 0x0309, TryCatch #10 {Exception -> 0x0309, HeadlessException -> 0x02f5, blocks: (B:58:0x0164, B:60:0x0169, B:62:0x0178, B:64:0x01aa, B:65:0x01b1, B:67:0x01b7, B:69:0x01c9, B:70:0x01d8, B:72:0x01eb, B:74:0x01f2, B:75:0x0208, B:77:0x0221, B:78:0x023c, B:80:0x0246, B:83:0x0288, B:84:0x0291, B:87:0x02b8, B:86:0x029d, B:82:0x026d, B:88:0x02d1), top: B:116:0x0164 }] */
    /* JADX WARN: Code duplicated, block: B:69:0x01c9 A[Catch: HeadlessException -> 0x02f5, Exception -> 0x0309, TryCatch #10 {Exception -> 0x0309, HeadlessException -> 0x02f5, blocks: (B:58:0x0164, B:60:0x0169, B:62:0x0178, B:64:0x01aa, B:65:0x01b1, B:67:0x01b7, B:69:0x01c9, B:70:0x01d8, B:72:0x01eb, B:74:0x01f2, B:75:0x0208, B:77:0x0221, B:78:0x023c, B:80:0x0246, B:83:0x0288, B:84:0x0291, B:87:0x02b8, B:86:0x029d, B:82:0x026d, B:88:0x02d1), top: B:116:0x0164 }] */
    /* JADX WARN: Code duplicated, block: B:88:0x02d1 A[Catch: HeadlessException -> 0x02f5, Exception -> 0x0309, TryCatch #10 {Exception -> 0x0309, HeadlessException -> 0x02f5, blocks: (B:58:0x0164, B:60:0x0169, B:62:0x0178, B:64:0x01aa, B:65:0x01b1, B:67:0x01b7, B:69:0x01c9, B:70:0x01d8, B:72:0x01eb, B:74:0x01f2, B:75:0x0208, B:77:0x0221, B:78:0x023c, B:80:0x0246, B:83:0x0288, B:84:0x0291, B:87:0x02b8, B:86:0x029d, B:82:0x026d, B:88:0x02d1), top: B:116:0x0164 }] */
    public static void main(String[] strArr) {
        SimpleAfirma simpleAfirma;
        Platform.OS os;
        configureLog();
        LookAndFeelManager.applyLookAndFeel();
        try {
            ProxyUtil.setProxySettings();
        } catch (Throwable th) {
            LOGGER.log(Level.SEVERE, "Error al aplicar la configuracion de proxy", th);
            if (isUsingGUI(strArr) && !isHeadlessMode()) {
                AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.11"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.WARNING_MESSAGE, th);
            }
        }
        boolean z = PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_SECURE_CONNECTIONS);
        HttpManager.setSecureConnections(z);
        HttpManager.setSecureDomains(PreferencesManager.get("secureDomainsList"));
        if (z) {
            try {
                SslSecurityManager.configureAfirmaTrustManagers();
                try {
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL("https://www.google.es/").openConnection();
                    httpsURLConnection.connect();
                    httpsURLConnection.disconnect();
                } catch (Exception e) {
                }
            } catch (Exception e2) {
                LOGGER.warning("Error al configurar almacenes de confianza: " + e2);
            }
        }
        if (isUsingCommnadLine(strArr) || isHeadlessMode()) {
            CommandLineLauncher.main(strArr);
            return;
        }
        if (Platform.OS.MACOSX.equals(Platform.getOS())) {
            Image image = Toolkit.getDefaultToolkit().getImage(SimpleAfirma.class.getResource("/resources/logo_cliente_256.png"));
            try {
                settingDockMacIconWithJava8(image);
            } catch (Error | Exception e3) {
                try {
                    settingDockMacIconWithJava9(image);
                } catch (Error | Exception e4) {
                    LOGGER.warning("No ha sido posible establecer el icono del Dock de OS X: " + e3);
                }
            }
            String property = System.getProperty("os.name");
            if (property != null && property.startsWith("Mac OS X") && new File("/System/Library/Frameworks/PCSC.framework/Versions/Current").isDirectory()) {
                System.setProperty("sun.security.smartcardio.library", "/System/Library/Frameworks/PCSC.framework/Versions/Current/PCSC");
            }
        }
        if (updatesEnabled) {
            updatesEnabled = (Boolean.getBoolean(AVOID_UPDATE_CHECK) || Boolean.parseBoolean(System.getenv(AVOID_UPDATE_CHECK_ENV))) ? false : true;
            if (!updatesEnabled) {
                LOGGER.info("Se ha configurado en el sistema que se omita la busqueda de actualizaciones de Autofirma");
            }
        }
        if (updatesEnabled && PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_UPDATECHECK)) {
            Updater.checkForUpdates(null);
        } else {
            LOGGER.info("No se buscaran nuevas versiones de la aplicacion");
        }
        if (strArr == null) {
            if (!isSimpleAfirmaAlreadyRunning()) {
                printSystemInfo();
                LOGGER.info("Apertura como herramienta de escritorio");
                if (ConfigUpdaterManager.needCheckConfigUpdates()) {
                    new Thread(() -> {
                        LOGGER.info("Comprobamos si existe nueva configuracion remota");
                        ConfigUpdaterManager.updatePrefsConfigFile();
                    }).start();
                }
                simpleAfirma = new SimpleAfirma();
                os = Platform.getOS();
                if (Platform.OS.WINDOWS != os) {
                    JMulticardUtilities.configureJMulticard(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_ENABLED_JMULTICARD));
                    LOGGER.info("Se intenta una precarga temprana del almacen de Mozilla");
                    simpleAfirma.setKeyStoreManager(AOKeyStoreManagerFactory.getAOKeyStoreManager(AOKeyStore.MOZ_UNI, null, "AFIRMA-NSS-KEYSTORE", null, null));
                }
                if (Platform.OS.LINUX.equals(os)) {
                    Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                    Field declaredField = defaultToolkit.getClass().getDeclaredField("awtAppClassName");
                    declaredField.setAccessible(true);
                    declaredField.set(defaultToolkit, "Autofirma");
                }
                LOGGER.info("Cargando plugins");
                getPluginsManager().getPluginsLoadedList();
                LOGGER.info("Iniciando entorno grafico");
                simpleAfirma.initGUI(null, null);
                checkJavaVersion(simpleAfirma.getMainFrame());
            } else {
                LOGGER.log(Level.WARNING, "La aplicacion ya se encuentra activa en otra ventana. Se cerrara esta instancia");
                AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.3"), SimpleAfirmaMessages.getString("SimpleAfirma.48"), 2, null);
                forceCloseApplication(0);
            }
        } else {
            try {
                if (strArr.length > 0 && strArr[0].toLowerCase().startsWith(PROTOCOL_URL_START_LOWER_CASE)) {
                    printSystemInfo();
                    LOGGER.info("Invocacion por protocolo con URL:\n" + strArr[0]);
                    ProtocolInvocationLauncher.launch(strArr[0]);
                    if (!strArr[0].startsWith(WEBSOCKET_REQUEST_PREFIX)) {
                        forceCloseApplication(0);
                    }
                } else if (!isSimpleAfirmaAlreadyRunning()) {
                    printSystemInfo();
                    LOGGER.info("Apertura como herramienta de escritorio");
                    if (ConfigUpdaterManager.needCheckConfigUpdates()) {
                        new Thread(() -> {
                            LOGGER.info("Comprobamos si existe nueva configuracion remota");
                            ConfigUpdaterManager.updatePrefsConfigFile();
                        }).start();
                    }
                    simpleAfirma = new SimpleAfirma();
                    os = Platform.getOS();
                    if (Platform.OS.WINDOWS != os && Platform.OS.MACOSX != os) {
                        JMulticardUtilities.configureJMulticard(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_ENABLED_JMULTICARD));
                        LOGGER.info("Se intenta una precarga temprana del almacen de Mozilla");
                        try {
                            simpleAfirma.setKeyStoreManager(AOKeyStoreManagerFactory.getAOKeyStoreManager(AOKeyStore.MOZ_UNI, null, "AFIRMA-NSS-KEYSTORE", null, null));
                        } catch (Exception e5) {
                            LOGGER.severe("Ha fallado la precarga temprana de NSS, se intentara la carga concurrente normal: " + e5);
                        }
                    }
                    if (Platform.OS.LINUX.equals(os)) {
                        try {
                            Toolkit defaultToolkit2 = Toolkit.getDefaultToolkit();
                            Field declaredField2 = defaultToolkit2.getClass().getDeclaredField("awtAppClassName");
                            declaredField2.setAccessible(true);
                            declaredField2.set(defaultToolkit2, "Autofirma");
                        } catch (Exception e6) {
                            LOGGER.warning("No ha sido posible renombrar la ventana AWT para X11: " + e6);
                        }
                    }
                    LOGGER.info("Cargando plugins");
                    try {
                        getPluginsManager().getPluginsLoadedList();
                    } catch (Exception e7) {
                        LOGGER.severe("No se han podido cargar los plugins en la aplicacion");
                        AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.4"), SimpleAfirmaMessages.getString("SimpleAfirma.48"), 2, e7);
                    }
                    LOGGER.info("Iniciando entorno grafico");
                    simpleAfirma.initGUI(null, null);
                    checkJavaVersion(simpleAfirma.getMainFrame());
                } else {
                    LOGGER.log(Level.WARNING, "La aplicacion ya se encuentra activa en otra ventana. Se cerrara esta instancia");
                    AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.3"), SimpleAfirmaMessages.getString("SimpleAfirma.48"), 2, null);
                    forceCloseApplication(0);
                }
            } catch (Exception e8) {
                LOGGER.log(Level.SEVERE, "Error global en la aplicacion: " + e8, (Throwable) e8);
                if (strArr != null && strArr.length > 0 && strArr[0].startsWith(WEBSOCKET_REQUEST_PREFIX)) {
                    forceCloseApplication(-1);
                }
            } catch (HeadlessException e9) {
                LOGGER.log(Level.WARNING, "No se puede crear el entorno grafico. Se tratar la peticion como una llamada por consola");
                CommandLineLauncher.main(strArr);
            }
        }
    }

    private static void settingDockMacIconWithJava8(Image image) throws IllegalAccessException, NoSuchMethodException, ClassNotFoundException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Class<?> cls = Class.forName("com.apple.eawt.Application");
        cls.getMethod("setDockIconImage", Image.class).invoke(cls.getMethod("getApplication", new Class[0]).invoke(null, new Object[0]), image);
    }

    private static void settingDockMacIconWithJava9(Image image) throws IllegalAccessException, NoSuchMethodException, ClassNotFoundException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Class<?> cls = Class.forName("java.awt.Taskbar");
        cls.getMethod("setIconImage", Image.class).invoke(cls.getMethod("getTaskbar", new Class[0]).invoke(null, new Object[0]), image);
    }

    private static boolean isSimpleAfirmaAlreadyRunning() {
        File file = new File(APPLICATION_HOME);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            final File file2 = new File(APPLICATION_HOME + File.separator + ".lock");
            final RandomAccessFile randomAccessFile = new RandomAccessFile(file2, "rw");
            final FileLock fileLockTryLock = randomAccessFile.getChannel().tryLock();
            if (fileLockTryLock != null) {
                Runtime.getRuntime().addShutdownHook(new Thread() { // from class: es.gob.afirma.standalone.SimpleAfirma.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            fileLockTryLock.release();
                            randomAccessFile.close();
                            if (!file2.delete()) {
                                SimpleAfirma.LOGGER.warning("No se ha podido eliminar el bloqueo de instancia");
                            }
                        } catch (Exception e) {
                            SimpleAfirma.LOGGER.warning("No se ha podido eliminar el bloqueo de instancia: " + e);
                        }
                    }
                });
            }
            return fileLockTryLock == null;
        } catch (Exception e) {
            LOGGER.warning("No se ha podido comprobar el bloqueo de instancia");
            return false;
        }
    }

    public boolean askForClosing() {
        if (PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_OMIT_ASKONCLOSE)) {
            closeApplication(0);
            return true;
        }
        ClosePanel closePanel = new ClosePanel();
        closePanel.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("SimpleAfirma.47"));
        if (AOUIFactory.showConfirmDialog(this.container, closePanel, SimpleAfirmaMessages.getString("SimpleAfirma.48"), 0, 2) == 0) {
            closeApplication(0);
            return true;
        }
        return false;
    }

    private static void configureLog() {
        try {
            String property = System.getProperty(SYSTEM_PROPERTY_DEBUG_FILE);
            if (property != null) {
                configureFileLogger(property);
            }
        } catch (Exception e) {
            LOGGER.warning("No se ha podido verificar si se deseaba un log en fichero: " + e);
        }
        try {
            String property2 = System.getProperty(SYSTEM_PROPERTY_DEBUG_LEVEL);
            if (property2 != null) {
                LOGGER.setLevel(Level.parse(property2));
            }
        } catch (Exception e2) {
            LOGGER.warning("No se ha podido verificar si se deseaba modificar el nivel de log: " + e2);
        }
    }

    private static void configureFileLogger(String str) {
        try {
            Formatter formatter = new Formatter() { // from class: es.gob.afirma.standalone.SimpleAfirma.2
                @Override // java.util.logging.Formatter
                public String format(LogRecord logRecord) {
                    return new StringBuffer(Long.toString(logRecord.getMillis())).append(": ").append(logRecord.getLevel().toString()).append(": ").append(logRecord.getMessage()).append("\n").toString();
                }
            };
            FileHandler fileHandler = new FileHandler(str, 10485760, 3);
            fileHandler.setFormatter(formatter);
            LOGGER.addHandler(fileHandler);
        } catch (Exception e) {
            LOGGER.warning("No se pudo configurar el log en fichero: " + e);
        }
    }

    static String getIp() throws IOException {
        BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(new InputStreamReader(new URL(IP_DISCOVERY_AUTOMATION).openStream()), 1, 2048);
        Throwable th = null;
        try {
            String line = boundedBufferedReader.readLine();
            if (boundedBufferedReader != null) {
                if (0 != 0) {
                    try {
                        boundedBufferedReader.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    boundedBufferedReader.close();
                }
            }
            return line;
        } catch (Throwable th3) {
            if (boundedBufferedReader != null) {
                if (0 != 0) {
                    try {
                        boundedBufferedReader.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    boundedBufferedReader.close();
                }
            }
            throw th3;
        }
    }

    public static PluginsManager getPluginsManager() {
        return pluginsManager;
    }

    public JPanel getCurrentPanel() {
        return this.currentPanel;
    }

    public static String getVersion() {
        if (version == null) {
            version = Updater.getCurrentVersionText();
        }
        return version;
    }

    private static void printSystemInfo() {
        LOGGER.info(new StringBuilder(390).append("Autofirma ").append(getVersion()).append("\nResolucion DPI de pantalla: ").append(DesktopUtil.getDPI()).append("\nSistema operativo: ").append(System.getProperty("os.name")).append("\nVersion del SO: ").append(System.getProperty("os.version")).append("\nVersion de Java: ").append(System.getProperty("java.version")).append("\nArquitectura del JRE: ").append(Platform.getJavaArch()).append("\nJava Vendor: ").append(System.getProperty("java.vm.vendor")).append("\nLocalizacion por defecto: ").append(Locale.getDefault()).append("\nTamano actual en memoria: ").append(Runtime.getRuntime().totalMemory() / 1048576).append("MB").append("\nTamano maximo de memoria: ").append(Runtime.getRuntime().maxMemory() / 1048576).append("MB").append("\nMemoria actualmente libre: ").append(Runtime.getRuntime().freeMemory() / 1048576).append("MB").toString());
    }

    private static boolean isUsingCommnadLine(String[] strArr) {
        return (strArr == null || strArr.length <= 0 || strArr[0].toLowerCase().startsWith(PROTOCOL_URL_START_LOWER_CASE)) ? false : true;
    }

    private static boolean isUsingGUI(String[] strArr) {
        return strArr == null || strArr.length == 0;
    }

    private static boolean isHeadlessMode() {
        return Boolean.getBoolean("java.awt.headless");
    }

    public static void setUpdatesEnabled(boolean z) {
        updatesEnabled = z;
    }

    public static boolean isUpdatesEnabled() {
        return updatesEnabled;
    }

    private static boolean isJavaSupported() {
        String property = System.getProperty("java.version");
        for (String str : SUPPORTED_JAVA_VERSIONS) {
            if (property.startsWith(str)) {
                return true;
            }
        }
        return false;
    }

    private static void checkJavaVersion(Component component) {
        boolean zIsJavaSupported = isJavaSupported();
        boolean z = PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_CHECK_JAVA_VERSION);
        if (zIsJavaSupported || !z) {
            if (zIsJavaSupported && !z) {
                PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_GENERAL_CHECK_JAVA_VERSION, true);
                return;
            }
            return;
        }
        String property = System.getProperty("java.version");
        JCheckBox jCheckBox = new JCheckBox(SimpleAfirmaMessages.getString("SimpleAfirma.51"));
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, 1));
        jPanel.add(new JLabel(SimpleAfirmaMessages.getString("SimpleAfirma.49", property)));
        jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        jPanel.add(jCheckBox);
        JOptionPane.showMessageDialog(component, jPanel, SimpleAfirmaMessages.getString("SimpleAfirma.50"), 2);
        if (jCheckBox.isSelected()) {
            PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_GENERAL_CHECK_JAVA_VERSION, false);
        }
    }

    private static File getPluginsDir() {
        File alternativeDirectory = DesktopUtil.getAlternativeDirectory();
        if (alternativeDirectory == null) {
            alternativeDirectory = DesktopUtil.getApplicationDirectory();
        }
        return new File(alternativeDirectory, PLUGINS_DIRNAME);
    }
}
