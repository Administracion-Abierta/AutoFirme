package es.gob.afirma.standalone.configurator;

import es.gob.afirma.core.LogManager;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.prefs.KeyStorePreferencesManager;
import es.gob.afirma.standalone.configurator.common.ConfigUpdaterManager;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import es.gob.afirma.standalone.plugins.manager.PluginsManager;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/AutofirmaConfigurator.class */
public class AutofirmaConfigurator implements ConsoleListener {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final File TMP = new File("/var/tmp");
    private static final File TEMP = new File("/var/temp");
    public static final String PARAMETER_INSTALL = "-install";
    public static final String PARAMETER_UNINSTALL = "-uninstall";
    public static final String PARAMETER_KEEP_OPEN = "-keep_open";
    public static final String PARAMETER_HEADLESS = "-headless";
    public static final String PARAMETER_JNLP_INSTANCE = "-jnlp";
    public static final String PARAMETER_FIREFOX_SECURITY_ROOTS = "-firefox_roots";
    public static final String PARAMETER_CERTIFICATE_PATH = "-certificate_path";
    public static final String PARAMETER_KEYSTORE_PATH = "-keystore_path";
    public static final String CONFIG_PATH = "-config_path";
    public static final String UPDATE_CONFIG = "-update_config";
    private static final String PLUGINS_DIRNAME = "plugins";
    private Configurator configurator;
    private final ConfigArgs config;
    private Console mainScreen;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/AutofirmaConfigurator$Operation.class */
    private enum Operation {
        INSTALLATION,
        UNINSTALLATION
    }

    static {
        try {
            if (Platform.OS.MACOSX.equals(Platform.getOS())) {
                File file = new File(System.getenv("HOME"), "Library/Application Support/Autofirma");
                if (file.mkdirs()) {
                    ConfiguratorMacUtils.addExexPermissionsToFile(file);
                }
                if (file.isDirectory() && file.canWrite()) {
                    LogManager.install(LogManager.App.AUTOFIRMA_CONFIGURATOR, file.getAbsolutePath());
                } else if (TMP.isDirectory() && TMP.canWrite()) {
                    LogManager.install(LogManager.App.AUTOFIRMA_CONFIGURATOR, TMP.getAbsolutePath());
                } else if (TEMP.isDirectory() && TEMP.canWrite()) {
                    LogManager.install(LogManager.App.AUTOFIRMA_CONFIGURATOR, TEMP.getAbsolutePath());
                } else {
                    LogManager.install(LogManager.App.AUTOFIRMA_CONFIGURATOR, System.getProperty("java.io.tmpdir"));
                }
            } else if (Platform.OS.LINUX.equals(Platform.getOS())) {
                if (TMP.isDirectory() && TMP.canWrite()) {
                    LogManager.install(LogManager.App.AUTOFIRMA_CONFIGURATOR, TMP.getAbsolutePath());
                } else if (TEMP.isDirectory() && TEMP.canWrite()) {
                    LogManager.install(LogManager.App.AUTOFIRMA_CONFIGURATOR, TEMP.getAbsolutePath());
                } else {
                    LogManager.install(LogManager.App.AUTOFIRMA_CONFIGURATOR, System.getProperty("java.io.tmpdir"));
                }
            } else if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
                String str = System.getenv("HOMEDRIVE");
                String str2 = System.getenv("HOMEPATH");
                if (str != null && str2 != null && new File(str + str2).isDirectory()) {
                    LogManager.install(LogManager.App.AUTOFIRMA_CONFIGURATOR, new File(str + str2, LogManager.SUBDIR).getAbsolutePath());
                } else {
                    LogManager.install(LogManager.App.AUTOFIRMA_CONFIGURATOR);
                }
            } else {
                LogManager.install(LogManager.App.AUTOFIRMA_CONFIGURATOR);
            }
        } catch (Exception e) {
            LOGGER.severe("No ha sido posible instalar el gestor de registro: " + e);
        }
    }

    public AutofirmaConfigurator(String[] strArr) {
        this(new ConfigArgs(strArr));
    }

    public AutofirmaConfigurator(ConfigArgs configArgs) {
        this.config = configArgs;
        boolean zIsJnlpInstance = this.config.isJnlpInstance();
        if (zIsJnlpInstance) {
            LOGGER.info("Se configurara la aplicacion en modo JNLP");
        } else {
            LOGGER.info("Se configurara la aplicacion en modo nativo");
        }
        if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
            this.configurator = new ConfiguratorWindows(zIsJnlpInstance, this.config.isFirefoxSecurityRoots(), this.config.getCertificatePath(), this.config.getKeystorePath());
            return;
        }
        if (Platform.OS.LINUX == Platform.getOS()) {
            this.configurator = new ConfiguratorLinux(zIsJnlpInstance);
        } else if (Platform.OS.MACOSX == Platform.getOS()) {
            this.configurator = new ConfiguratorMacOSX(this.config.isHeadless(), this.config.isFirefoxSecurityRoots());
        } else {
            LOGGER.warning("El sistema operativo '" + Platform.getOS() + "' no tiene definida una secuencia de configuracion/desinstalacion");
            this.configurator = null;
        }
    }

    public void configure() throws ConfigurationException, GeneralSecurityException, IOException {
        if (this.configurator == null) {
            LOGGER.warning("No se realizara ninguna accion");
            return;
        }
        this.mainScreen = ConsoleManager.getConsole(this.config.isHeadless() ? null : this);
        this.mainScreen.showConsole();
        try {
            this.configurator.configure(this.mainScreen);
        } catch (ConfigurationException e) {
            LOGGER.log(Level.SEVERE, "Error al generar las claves de cifrado SSL. El configurador se detendra", (Throwable) e);
            this.mainScreen.print(Messages.getString("AutofirmaConfigurator.4"));
            throw e;
        } catch (IOException e2) {
            LOGGER.log(Level.SEVERE, "Error al copiar o leer alguno de los ficheros de configuracion. El configurador se detendra", (Throwable) e2);
            this.mainScreen.print(Messages.getString("AutofirmaConfigurator.3"));
            throw e2;
        } catch (GeneralSecurityException e3) {
            LOGGER.log(Level.SEVERE, "Error en la importacion de la CA de confianza o la limpieza del almacen", (Throwable) e3);
            this.mainScreen.print(Messages.getString("AutofirmaConfigurator.5"));
            throw e3;
        }
    }

    private void uninstall() {
        if (this.configurator == null) {
            LOGGER.warning("No se realizara ninguna accion");
            return;
        }
        this.configurator.uninstall(this.mainScreen, new PluginsManager(getPluginsDir(this.configurator)));
        try {
            PreferencesManager.removeSystemPrefs();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se han podido eliminar las preferencias del sistema de la aplicacion: " + e);
        }
        try {
            KeyStorePreferencesManager.removeSystemPrefs();
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "No se han podido eliminar las preferencias del sistema de los almacenes: " + e2);
        }
    }

    private static File getPluginsDir(Configurator configurator) {
        File alternativeApplicationDirectory = configurator.getAlternativeApplicationDirectory();
        if (alternativeApplicationDirectory == null) {
            alternativeApplicationDirectory = configurator.getAplicationDirectory();
        }
        return new File(alternativeApplicationDirectory, PLUGINS_DIRNAME);
    }

    public void closeApplication(int i) {
        closeApplication(i, false);
    }

    public void closeApplication(int i, boolean z) {
        if (this.mainScreen != null) {
            this.mainScreen.dispose();
        }
        if (!z) {
            System.exit(i);
        }
    }

    @Override // es.gob.afirma.standalone.configurator.ConsoleListener
    public void close() {
        closeApplication(0);
    }

    public static void main(String[] strArr) {
        ConfigArgs configArgs = new ConfigArgs(strArr);
        AutofirmaConfigurator autofirmaConfigurator = new AutofirmaConfigurator(configArgs);
        if (Platform.OS.MACOSX.equals(Platform.getOS())) {
            Image image = Toolkit.getDefaultToolkit().getImage(AutofirmaConfigurator.class.getResource("/logo_cliente_256.png"));
            try {
                settingDockMacIconWithJava8(image);
            } catch (Error | Exception e) {
                try {
                    settingDockMacIconWithJava9(image);
                } catch (Error | Exception e2) {
                    LOGGER.warning("No ha sido posible establecer el icono del Dock de macOS: " + e);
                }
            }
        }
        if (configArgs.isUninstallation()) {
            autofirmaConfigurator.uninstall();
        } else {
            try {
                autofirmaConfigurator.configure();
            } catch (Error | Exception e3) {
                LOGGER.log(Level.SEVERE, "Error grave durante el proceso de configuracion", e3);
                autofirmaConfigurator.closeApplication(-1, configArgs.isNeedKeep());
            }
        }
        if (!configArgs.getConfigPath().isEmpty()) {
            ConfigUpdaterManager.savePrefsConfigFile(configArgs.getConfigPath(), configArgs.getUpdateConfig());
        }
        autofirmaConfigurator.closeApplication(0, configArgs.isNeedKeep());
    }

    private static void settingDockMacIconWithJava8(Image image) throws IllegalAccessException, NoSuchMethodException, ClassNotFoundException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Class<?> cls = Class.forName("com.apple.eawt.Application");
        cls.getMethod("setDockIconImage", Image.class).invoke(cls.getMethod("getApplication", new Class[0]).invoke(null, new Object[0]), image);
    }

    private static void settingDockMacIconWithJava9(Image image) throws IllegalAccessException, NoSuchMethodException, ClassNotFoundException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Class<?> cls = Class.forName("java.awt.Taskbar");
        cls.getMethod("setIconImage", Image.class).invoke(cls.getMethod("getTaskbar", new Class[0]).invoke(null, new Object[0]), image);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/AutofirmaConfigurator$ConfigArgs.class */
    private static class ConfigArgs {
        private Operation op;
        private boolean needKeep;
        private boolean headless;
        private boolean jnlpInstance;
        private boolean firefoxSecurityRoots;
        private String certificatePath;
        private String keystorePath;
        private String configPath;
        private boolean updateConfig;

        public ConfigArgs(String[] strArr) {
            this.op = Operation.INSTALLATION;
            this.needKeep = false;
            this.headless = false;
            this.jnlpInstance = false;
            this.firefoxSecurityRoots = false;
            this.certificatePath = "";
            this.keystorePath = "";
            this.configPath = "";
            this.updateConfig = false;
            if (strArr != null) {
                int i = 0;
                while (i < strArr.length) {
                    String str = strArr[i];
                    if ("-install".equalsIgnoreCase(str)) {
                        this.op = Operation.INSTALLATION;
                    } else if ("-uninstall".equalsIgnoreCase(str)) {
                        this.op = Operation.UNINSTALLATION;
                    } else if (AutofirmaConfigurator.PARAMETER_KEEP_OPEN.equalsIgnoreCase(str)) {
                        this.needKeep = true;
                    } else if (AutofirmaConfigurator.PARAMETER_HEADLESS.equalsIgnoreCase(str)) {
                        this.headless = true;
                    } else if (AutofirmaConfigurator.PARAMETER_JNLP_INSTANCE.equalsIgnoreCase(str)) {
                        this.jnlpInstance = true;
                    } else if ("-firefox_roots".equalsIgnoreCase(str)) {
                        this.firefoxSecurityRoots = true;
                    } else if ("-certificate_path".equalsIgnoreCase(str)) {
                        if (i < strArr.length - 1) {
                            i++;
                            this.certificatePath = strArr[i];
                        }
                    } else if ("-keystore_path".equalsIgnoreCase(str) && i < strArr.length - 1) {
                        i++;
                        this.keystorePath = strArr[i];
                    } else if ("-config_path".equalsIgnoreCase(str) && i < strArr.length - 1) {
                        i++;
                        this.configPath = strArr[i];
                    } else if ("-update_config".equalsIgnoreCase(str)) {
                        this.updateConfig = true;
                    }
                    i++;
                }
            }
        }

        public boolean isUninstallation() {
            return this.op == Operation.UNINSTALLATION;
        }

        public boolean isNeedKeep() {
            return this.needKeep;
        }

        public boolean isHeadless() {
            return this.headless;
        }

        public boolean isJnlpInstance() {
            return this.jnlpInstance;
        }

        public boolean isFirefoxSecurityRoots() {
            return this.firefoxSecurityRoots;
        }

        public String getCertificatePath() {
            return this.certificatePath;
        }

        public String getKeystorePath() {
            return this.keystorePath;
        }

        public String getConfigPath() {
            return this.configPath;
        }

        public boolean getUpdateConfig() {
            return this.updateConfig;
        }
    }
}
