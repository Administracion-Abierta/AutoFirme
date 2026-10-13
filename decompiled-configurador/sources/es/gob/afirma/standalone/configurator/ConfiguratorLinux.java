package es.gob.afirma.standalone.configurator;

import es.gob.afirma.core.misc.BoundedBufferedReader;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.standalone.configurator.common.ConfiguratorUtil;
import es.gob.afirma.standalone.plugins.AfirmaPlugin;
import es.gob.afirma.standalone.plugins.manager.PluginsManager;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/ConfiguratorLinux.class */
final class ConfiguratorLinux implements Configurator {
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String ALTERNATIVE_APP_SUBDIR = ".afirma/Autofirma";
    private static final String UNINSTALL_SCRIPT_NAME = "uninstall.sh";
    private static final String INSTALL_SCRIPT_NAME = "script.sh";
    private static final String KS_FILENAME = "autofirma.pfx";
    private static final String FILE_AUTOFIRMA_CERTIFICATE = "Autofirma_ROOT.cer";
    private static final String KS_PASSWORD = "654321";
    private final boolean jnlpInstance;

    public ConfiguratorLinux(boolean z) {
        this.jnlpInstance = z;
    }

    @Override // es.gob.afirma.standalone.configurator.Configurator
    public void configure(Console console) throws GeneralSecurityException, IOException, HeadlessException {
        LOGGER.info(Messages.getString("ConfiguratorLinux.2"));
        File applicationDirectory = getApplicationDirectory(this.jnlpInstance);
        LOGGER.info(Messages.getString("ConfiguratorLinux.3") + applicationDirectory.getAbsolutePath());
        String[] systemUsersHomes = getSystemUsersHomes();
        if (!checkSSLKeyStoreGenerated(applicationDirectory)) {
            LOGGER.info(Messages.getString("ConfiguratorLinux.5"));
            CertUtil.CertPack certPackForLocalhostSsl = CertUtil.getCertPackForLocalhostSsl(ConfiguratorUtil.CERT_ALIAS, KS_PASSWORD);
            LOGGER.info(Messages.getString("ConfiguratorLinux.11"));
            ConfiguratorUtil.installFile(certPackForLocalhostSsl.getPkcs12(), new File(applicationDirectory, KS_FILENAME));
            ConfiguratorUtil.installFile(certPackForLocalhostSsl.getCaCertificate().getEncoded(), new File(applicationDirectory, FILE_AUTOFIRMA_CERTIFICATE));
            try {
                LOGGER.info(Messages.getString("ConfiguratorLinux.13"));
                ConfiguratorFirefoxLinux.createScriptsToSystemKeyStore(applicationDirectory, systemUsersHomes, new File(applicationDirectory, INSTALL_SCRIPT_NAME), new File(applicationDirectory, UNINSTALL_SCRIPT_NAME));
                ConfiguratorFirefoxLinux.createScriptsToMozillaKeyStore(applicationDirectory, systemUsersHomes, new File(applicationDirectory, INSTALL_SCRIPT_NAME), new File(applicationDirectory, UNINSTALL_SCRIPT_NAME));
            } catch (MozillaProfileNotFoundException e) {
                LOGGER.warning(Messages.getString("ConfiguratorLinux.12"));
            }
        } else {
            LOGGER.info(Messages.getString("ConfiguratorLinux.14"));
        }
        LOGGER.info(Messages.getString("ConfiguratorLinux.8"));
        if (isHeadlessJre()) {
            LOGGER.warning("La JVM con la que se ha ejecutado el instalador no soporta interfaces graficas (headless). Esta JVM no es compatible con la interfaz grafica de Autofirma. Instale una JRE completa para poder ejecutarla.");
        }
    }

    private static boolean checkSSLKeyStoreGenerated(File file) {
        return new File(file, KS_FILENAME).exists();
    }

    private static File getApplicationDirectory(boolean z) {
        if (z) {
            try {
                return getIntApplicationDirectory();
            } catch (IOException e) {
                LOGGER.severe("No se encuentra ni ha podido generarse el directorio de aplicacion: " + e);
            }
        }
        return ConfiguratorUtil.getApplicationDirectory();
    }

    private static File getIntApplicationDirectory() throws IOException {
        try {
            String property = System.getProperty("user.home");
            if (property == null) {
                throw new IOException("No se encuentra definido el directorio del usuario");
            }
            File file = new File(property, ALTERNATIVE_APP_SUBDIR);
            if (!file.isDirectory() && !file.mkdirs()) {
                throw new IOException("No ha podido crearse el directorio para los ficheros de aplicacion");
            }
            return file;
        } catch (Exception e) {
            throw new IOException("No se ha podido identificar el directorio del usuario para almacenar los ficheros de instalacion", e);
        }
    }

    @Override // es.gob.afirma.standalone.configurator.Configurator
    public File getAplicationDirectory() {
        return getApplicationDirectory(false);
    }

    @Override // es.gob.afirma.standalone.configurator.Configurator
    public File getAlternativeApplicationDirectory() {
        return new File(System.getProperty("user.home"), ALTERNATIVE_APP_SUBDIR);
    }

    /* JADX WARN: Code duplicated, block: B:26:0x00b2  */
    /* JADX WARN: Code duplicated, block: B:31:0x00cb A[Catch: Throwable -> 0x00f9, all -> 0x0102, Exception -> 0x013d, TryCatch #0 {all -> 0x0102, blocks: (B:4:0x0036, B:5:0x0051, B:7:0x005b, B:9:0x0067, B:11:0x0071, B:16:0x0086, B:19:0x009a, B:18:0x0090, B:22:0x00aa, B:28:0x00b7, B:31:0x00cb, B:30:0x00c1, B:33:0x00d2, B:44:0x0101), top: B:61:0x0036, outer: #1 }] */
    /* JADX WARN: Code duplicated, block: B:63:0x00b7 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private static String[] getSystemUsersHomes() throws IOException {
        try {
            Process processStart = new ProcessBuilder("cut", "-d:", "-f6", "/etc/passwd").start();
            ArrayList arrayList = new ArrayList();
            InputStream inputStream = processStart.getInputStream();
            Throwable th = null;
            try {
                BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(new InputStreamReader(inputStream), 2048, 2048);
                Throwable th2 = null;
                while (true) {
                    try {
                        try {
                            String line = boundedBufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            if (line.toLowerCase().contains("home/") && !arrayList.contains(line)) {
                                arrayList.add(line);
                            }
                        } catch (Throwable th3) {
                            th2 = th3;
                            throw th3;
                        }
                    } catch (Throwable th4) {
                        if (boundedBufferedReader != null) {
                            if (th2 != null) {
                                try {
                                    boundedBufferedReader.close();
                                } catch (Throwable th5) {
                                    th2.addSuppressed(th5);
                                }
                            } else {
                                boundedBufferedReader.close();
                            }
                        }
                        throw th4;
                    }
                    if (boundedBufferedReader != null) {
                        if (th2 != null) {
                            boundedBufferedReader.close();
                        } else {
                            boundedBufferedReader.close();
                        }
                    }
                    throw th4;
                }
                if (boundedBufferedReader != null) {
                    if (0 != 0) {
                        try {
                            boundedBufferedReader.close();
                        } catch (Throwable th6) {
                            th2.addSuppressed(th6);
                        }
                    } else {
                        boundedBufferedReader.close();
                    }
                }
                if (inputStream != null) {
                    if (0 != 0) {
                        try {
                            inputStream.close();
                        } catch (Throwable th7) {
                            th.addSuppressed(th7);
                        }
                    } else {
                        inputStream.close();
                    }
                }
                return (String[]) arrayList.toArray(new String[arrayList.size()]);
            } catch (Throwable th8) {
                if (inputStream != null) {
                    if (0 != 0) {
                        try {
                            inputStream.close();
                        } catch (Throwable th9) {
                            th.addSuppressed(th9);
                        }
                    } else {
                        inputStream.close();
                    }
                }
                throw th8;
            }
        } catch (Exception e) {
            LOGGER.severe("Error al obtener el listado de directorios de usuarios del sistema: " + e);
            throw new IOException("No se pudo obtener el listado de directorios de usuarios del sistema", e);
        }
    }

    @Override // es.gob.afirma.standalone.configurator.Configurator
    public void uninstall(Console console, PluginsManager pluginsManager) {
        List<AfirmaPlugin> pluginsLoadedList = null;
        try {
            pluginsLoadedList = pluginsManager.getPluginsLoadedList();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se pudo obtener el listado de plugins de Autofirma", (Throwable) e);
        }
        if (pluginsLoadedList != null && !pluginsLoadedList.isEmpty()) {
            LOGGER.info("Desinstalamos los plugins instalados");
            for (AfirmaPlugin afirmaPlugin : pluginsLoadedList) {
                try {
                    pluginsManager.uninstallPlugin(afirmaPlugin);
                } catch (Exception e2) {
                    LOGGER.log(Level.WARNING, "No se pudo desinstalar el plugin: " + afirmaPlugin.getInfo().getName(), (Throwable) e2);
                }
            }
        }
        File alternativeApplicationDirectory = getAlternativeApplicationDirectory();
        if (alternativeApplicationDirectory.isDirectory()) {
            try {
                Files.walkFileTree(alternativeApplicationDirectory.toPath(), new HashSet(), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() { // from class: es.gob.afirma.standalone.configurator.ConfiguratorLinux.1
                    @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
                    public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) {
                        try {
                            Files.delete(path);
                        } catch (Exception e3) {
                            ConfiguratorLinux.LOGGER.warning("No se pudo eliminar el fichero: " + LoggerUtil.getCleanUserHomePath(path.toAbsolutePath().toString()));
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
                    public FileVisitResult postVisitDirectory(Path path, IOException iOException) {
                        try {
                            Files.delete(path);
                        } catch (IOException e3) {
                            ConfiguratorLinux.LOGGER.warning("No se pudo eliminar el directorio: " + LoggerUtil.getCleanUserHomePath(path.toAbsolutePath().toString()));
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (Exception e3) {
                LOGGER.log(Level.WARNING, "No se ha podido eliminar por completo el directorio alternativo para el certificado SSL", (Throwable) e3);
            }
        }
    }

    private static boolean isHeadlessJre() {
        boolean z = false;
        try {
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        } catch (UnsatisfiedLinkError e) {
            z = true;
        }
        return z;
    }
}
