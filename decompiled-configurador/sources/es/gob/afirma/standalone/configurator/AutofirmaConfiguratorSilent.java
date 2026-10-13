package es.gob.afirma.standalone.configurator;

import es.gob.afirma.core.LogManager;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.prefs.KeyStorePreferencesManager;
import es.gob.afirma.standalone.configurator.common.ConfigUpdaterManager;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import es.gob.afirma.standalone.plugins.manager.PluginsManager;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/AutofirmaConfiguratorSilent.class */
public final class AutofirmaConfiguratorSilent implements ConsoleListener {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final File TMP = new File("/var/tmp");
    private static final File TEMP = new File("/var/temp");
    public static final String PARAMETER_INSTALL = "-install";
    public static final String PARAMETER_UNINSTALL = "-uninstall";
    public static final String PARAMETER_FIREFOX_SECURITY_ROOTS = "-firefox_roots";
    public static final String PARAMETER_CERTIFICATE_PATH = "-certificate_path";
    public static final String PARAMETER_KEYSTORE_PATH = "-keystore_path";
    public static final String CONFIG_PATH = "-config_path";
    public static final String UPDATE_CONFIG = "-update_config";
    private static final String PLUGINS_DIRNAME = "plugins";
    private Configurator configurator;
    private final ConfigArgs config;
    private Console console;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/AutofirmaConfiguratorSilent$Operation.class */
    private enum Operation {
        INSTALLATION,
        UNINSTALLATION
    }

    static {
        try {
            if (Platform.getOS().equals(Platform.OS.MACOSX)) {
                File file = new File(System.getenv("HOME"), "Library/Application Support/Autofirma");
                if ((file.isDirectory() && file.canWrite()) || file.mkdirs()) {
                    LogManager.install(LogManager.App.AUTOFIRMA_CONFIGURATOR, file.getAbsolutePath());
                } else if (TMP.exists() && TMP.isDirectory() && TMP.canWrite()) {
                    LogManager.install(LogManager.App.AUTOFIRMA_CONFIGURATOR, TMP.getAbsolutePath());
                } else if (TEMP.exists() && TEMP.isDirectory() && TEMP.canWrite()) {
                    LogManager.install(LogManager.App.AUTOFIRMA_CONFIGURATOR, TEMP.getAbsolutePath());
                } else {
                    LogManager.install(LogManager.App.AUTOFIRMA_CONFIGURATOR, System.getProperty("java.io.tmpdir"));
                }
            } else if (Platform.getOS().equals(Platform.OS.LINUX)) {
                if (TMP.exists() && TMP.isDirectory() && TMP.canWrite()) {
                    LogManager.install(LogManager.App.AUTOFIRMA_CONFIGURATOR, TMP.getAbsolutePath());
                } else if (TEMP.exists() && TEMP.isDirectory() && TEMP.canWrite()) {
                    LogManager.install(LogManager.App.AUTOFIRMA_CONFIGURATOR, TEMP.getAbsolutePath());
                } else {
                    LogManager.install(LogManager.App.AUTOFIRMA_CONFIGURATOR, System.getProperty("java.io.tmpdir"));
                }
            } else if (Platform.getOS().equals(Platform.OS.WINDOWS)) {
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

    public AutofirmaConfiguratorSilent(String[] strArr) {
        this(new ConfigArgs(strArr));
    }

    public AutofirmaConfiguratorSilent(ConfigArgs configArgs) {
        this.config = configArgs;
        if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
            this.configurator = new ConfiguratorWindows(false, this.config.isFirefoxSecurityRoots(), this.config.getCertificatePath(), this.config.getKeystorePath());
            return;
        }
        if (Platform.OS.LINUX == Platform.getOS()) {
            this.configurator = new ConfiguratorLinux(false);
        } else if (Platform.OS.MACOSX == Platform.getOS()) {
            this.configurator = new ConfiguratorMacOSX(true, this.config.isFirefoxSecurityRoots());
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
        this.console = ConsoleManager.getConsole(this);
        try {
            this.configurator.configure(this.console);
        } catch (ConfigurationException e) {
            LOGGER.log(Level.SEVERE, "Error al generar las claves de cifrado SSL. El configurador se detendra", (Throwable) e);
            throw e;
        } catch (IOException e2) {
            LOGGER.log(Level.SEVERE, "Error al copiar o leer alguno de los ficheros de configuracion. El configurador se detendra", (Throwable) e2);
            throw e2;
        } catch (GeneralSecurityException e3) {
            LOGGER.log(Level.SEVERE, "Error en la importacion de la CA de confianza o la limpieza del almacen", (Throwable) e3);
            throw e3;
        }
    }

    private void uninstall() {
        if (this.configurator == null) {
            LOGGER.warning("No se realizara ninguna accion");
            return;
        }
        this.configurator.uninstall(this.console, new PluginsManager(getPluginsDir(this.configurator)));
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
        if (this.console != null) {
            this.console.dispose();
        }
        System.exit(i);
    }

    @Override // es.gob.afirma.standalone.configurator.ConsoleListener
    public void close() {
        closeApplication(0);
    }

    public static void main(String[] strArr) {
        ConfigArgs configArgs = new ConfigArgs(strArr);
        AutofirmaConfiguratorSilent autofirmaConfiguratorSilent = new AutofirmaConfiguratorSilent(configArgs);
        if (configArgs.isUninstallation()) {
            autofirmaConfiguratorSilent.uninstall();
        } else {
            try {
                autofirmaConfiguratorSilent.configure();
            } catch (Error | Exception e) {
                ConsoleManager.showErrorMessage(Messages.getString("AutofirmaConfigurator.0"), e);
                LOGGER.log(Level.SEVERE, "Error grave durante el proceso de configuracion", e);
                autofirmaConfiguratorSilent.closeApplication(-1);
            } catch (HeadlessException e2) {
                ConsoleManager.showErrorMessage(Messages.getString("AutofirmaConfigurator.7"), e2);
                LOGGER.log(Level.WARNING, "La JVM utilizada no soporta interfaces graficos. No se podra ejecutar la interfaz grafica de Autofirma con ella", e2);
            }
        }
        if (!configArgs.getConfigPath().isEmpty()) {
            LOGGER.info("Se importa la configuracion desde: " + LoggerUtil.getTrimStr(LoggerUtil.getCleanUserHomePath(configArgs.getConfigPath())));
            ConfigUpdaterManager.savePrefsConfigFile(configArgs.getConfigPath(), configArgs.getUpdateConfig());
        }
        autofirmaConfiguratorSilent.closeApplication(0);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/AutofirmaConfiguratorSilent$ConfigArgs.class */
    private static class ConfigArgs {
        private Operation op;
        private boolean firefoxSecurityRoots;
        private String certificatePath;
        private String keystorePath;
        private String configPath;
        private boolean updateConfig;

        public ConfigArgs(String[] strArr) {
            this.op = Operation.INSTALLATION;
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
                        if (this.configPath.toLowerCase(Locale.US).startsWith("https:")) {
                            this.configPath = this.configPath.replace('\\', '/');
                        }
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
