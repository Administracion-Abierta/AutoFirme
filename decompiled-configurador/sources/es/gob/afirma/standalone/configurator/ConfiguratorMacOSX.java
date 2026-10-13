package es.gob.afirma.standalone.configurator;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.keystores.mozilla.MozillaKeyStoreUtilities;
import es.gob.afirma.keystores.mozilla.MozillaKeyStoreUtilitiesOsX;
import es.gob.afirma.keystores.mozilla.apple.ShellScript;
import es.gob.afirma.standalone.configurator.common.ConfiguratorUtil;
import es.gob.afirma.standalone.plugins.AfirmaPlugin;
import es.gob.afirma.standalone.plugins.manager.PluginsManager;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/ConfiguratorMacOSX.class */
final class ConfiguratorMacOSX implements Configurator {
    private static final String KS_FILENAME = "/autofirma.pfx";
    private static final String SSL_CER_FILENAME = "/autofirma.cer";
    private static final String KS_PASSWORD = "654321";
    private static final String CERT_CN = "127.0.0.1";
    private static final String CERT_CN_ROOT = "Autofirma ROOT";
    private static final String MACOSX_CERTIFICATE = "/Autofirma_ROOT.cer";
    private static final String GET_USERS_COMMAND = "dscacheutil -q user";
    private static final String USER_DIR_LINE_HEADER = "dir: ";
    private static final String USER_DIR_LINE_PREFIX = "dir: /Users/";
    private static final String GET_USER_SCRIPTS_NAME = "scriptGetUsers";
    private static final String SCRIPT_EXT = ".sh";
    private static final String MAC_SCRIPT_NAME = "installCerScript";
    private static final String MAC_SCRIPT_EXT = ".sh";
    private static final String FIND_CERT_HASH_PREFIX = "hash:";
    static File scriptFile;
    private final boolean headless;
    private final boolean firefoxSecurityRoots;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final byte[] DUMMY = "dummy".getBytes();
    private static String[] userDirs = null;

    public ConfiguratorMacOSX(boolean z, boolean z2) {
        this.headless = z;
        this.firefoxSecurityRoots = z2;
    }

    @Override // es.gob.afirma.standalone.configurator.Configurator
    public void configure(Console console) throws GeneralSecurityException, IOException {
        boolean z;
        userDirs = getSystemUsersHomes();
        console.print(Messages.getString("ConfiguratorMacOSX.2"));
        File resourcesDirectory = getResourcesDirectory(true);
        console.print(Messages.getString("ConfiguratorMacOSX.3") + resourcesDirectory.getAbsolutePath());
        configureSSL(resourcesDirectory, console);
        if (this.headless) {
            z = this.firefoxSecurityRoots;
        } else {
            z = JOptionPane.showConfirmDialog(console.getParentComponent(), Messages.getString("ConfiguratorMacOSX.23"), Messages.getString("ConfiguratorMacOSX.24"), 0) == 0;
        }
        if (z) {
            console.print(Messages.getString("ConfiguratorMacOSX.22"));
            try {
                ConfiguratorFirefoxMac.configureUseSystemTrustStore(true, userDirs, console);
            } catch (MozillaProfileNotFoundException e) {
                console.print(Messages.getString("ConfiguratorMacOSX.21") + ": " + e);
            }
        }
        console.print(Messages.getString("ConfiguratorMacOSX.8"));
        LOGGER.info("Finalizado");
    }

    private static void configureSSL(File file, Console console) throws GeneralSecurityException, IOException {
        boolean z;
        console.print(Messages.getString("ConfiguratorMacOSX.5"));
        try {
            scriptFile = File.createTempFile(MAC_SCRIPT_NAME, ".sh");
            ConfiguratorMacUtils.addExexPermissionsToFile(scriptFile);
            ConfiguratorMacUtils.addExexPermissionsToAllFilesOnDirectory(file);
            CertUtil.CertPack certPackForLocalhostSsl = CertUtil.getCertPackForLocalhostSsl(ConfiguratorUtil.CERT_ALIAS, KS_PASSWORD);
            console.print(Messages.getString("ConfiguratorMacOSX.11"));
            File file2 = new File(file, MACOSX_CERTIFICATE);
            ConfiguratorUtil.installFile(certPackForLocalhostSsl.getCaCertificate().getEncoded(), file2);
            ConfiguratorUtil.installFile(certPackForLocalhostSsl.getPkcs12(), new File(file, KS_FILENAME));
            File file3 = new File(file, SSL_CER_FILENAME);
            ConfiguratorUtil.installFile(certPackForLocalhostSsl.getSslCertificate().getEncoded(), file3);
            closeFirefox();
            byte[] keyChainPhrase = getKeyChainPhrase(console, false);
            console.print(Messages.getString("ConfiguratorMacOSX.15"));
            do {
                z = true;
                try {
                    uninstallProcess(file, keyChainPhrase);
                } catch (InvalidPasswordException e) {
                    keyChainPhrase = getKeyChainPhrase(console, true);
                    z = false;
                }
            } while (!z);
            JOptionPane.showMessageDialog(console.getParentComponent(), new JLabel("<html>" + Messages.getString("ConfiguratorMacOSX.20") + "</html>"));
            console.print(Messages.getString("ConfiguratorMacOSX.6"));
            try {
                installTrustedCertsInAppleKeyChain(file2, file3, keyChainPhrase, console);
            } catch (Exception e2) {
                console.print(Messages.getString("ConfiguratorMacOSX.34"));
                AOUIFactory.showErrorMessage(Messages.getString("ConfiguratorMacOSX.25"), Messages.getString("ConfiguratorMacOSX.26"), 0, e2);
            }
            console.print(Messages.getString("ConfiguratorMacOSX.13"));
            try {
                ConfiguratorFirefoxMac.installOnMozillaKeyStore(file, getSystemUsersHomes(), scriptFile, console);
                LOGGER.info("Configuracion de NSS");
                MozillaKeyStoreUtilitiesOsX.configureMacNSS(MozillaKeyStoreUtilities.getSystemNSSLibDir());
                executeScriptFile(scriptFile, true, true);
            } catch (AOException e3) {
                LOGGER.severe("La configuracion de NSS para Mac OS X ha fallado: " + e3);
            } catch (MozillaProfileNotFoundException e4) {
                LOGGER.severe("Perfil de Mozilla no encontrado: " + e4);
                console.print(Messages.getString("ConfiguratorMacOSX.12"));
            } catch (Exception e5) {
                LOGGER.log(Level.WARNING, "Error en la importacion del certificado de confianza en el almacen de Firefox: " + e5, (Throwable) e5);
            }
        } catch (Exception e6) {
            console.print(Messages.getString("ConfiguratorMacOSX.18"));
            LOGGER.severe("Error creando script temporal: " + e6);
            throw new IOException("Error creando script temporal", e6);
        }
    }

    private static byte[] getKeyChainPhrase(Console console, boolean z) {
        String string;
        byte[] bytes = null;
        boolean z2 = false;
        boolean z3 = false;
        do {
            if (!z && bytes == null) {
                bytes = DUMMY;
            } else {
                if (z2) {
                    string = Messages.getString("ConfiguratorMacOSX.28");
                } else {
                    string = Messages.getString("ConfiguratorMacOSX.27");
                }
                try {
                    bytes = new String(AOUIFactory.getPassword(string, console.getParentComponent())).getBytes(StandardCharsets.UTF_8);
                } catch (AOCancelledOperationException e) {
                    LOGGER.info("Se cancelo el dialogo de entrada de contrasena: " + e);
                    if (AOUIFactory.showConfirmDialog(console.getParentComponent(), Messages.getString("ConfiguratorMacOSX.29"), Messages.getString("ConfiguratorMacOSX.30"), 0, 2) == 0) {
                        console.print(Messages.getString("ConfiguratorMacOSX.31"));
                        throw new AOCancelledOperationException("El usuario cancelo la insercion de la contrasena del llavero");
                    }
                }
            }
            z2 = false;
            try {
                unlockAppleKeyChain(bytes);
                z3 = true;
            } catch (SecurityException e2) {
                LOGGER.log(Level.WARNING, "Error de contrasena al desbloquear el Llavero", (Throwable) e2);
                if (!Arrays.equals(DUMMY, bytes)) {
                    z2 = true;
                }
            } catch (Exception e3) {
                LOGGER.log(Level.SEVERE, "No se pudo abrir el Llavero", (Throwable) e3);
                return DUMMY;
            }
        } while (!z3);
        return bytes;
    }

    private static void unlockAppleKeyChain(byte[] bArr) throws InterruptedException, IOException, SecurityException {
        ArrayList arrayList = new ArrayList();
        arrayList.add("security");
        arrayList.add("unlock-keychain");
        arrayList.add("-p");
        arrayList.add(new String(bArr, StandardCharsets.UTF_8));
        Process processStart = new ProcessBuilder(arrayList).start();
        if (processStart.waitFor() != 0) {
            byte[] dataFromInputStream = null;
            try {
                InputStream errorStream = processStart.getErrorStream();
                Throwable th = null;
                try {
                    dataFromInputStream = AOUtil.getDataFromInputStream(errorStream);
                    if (errorStream != null) {
                        if (0 != 0) {
                            try {
                                errorStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            errorStream.close();
                        }
                    }
                } catch (Throwable th3) {
                    if (errorStream != null) {
                        if (0 != 0) {
                            try {
                                errorStream.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            errorStream.close();
                        }
                    }
                    throw th3;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "No se pudo leer la salida de error del proceso de desbloqueo del Llavero", (Throwable) e);
            }
            if (dataFromInputStream != null) {
                String str = new String(dataFromInputStream);
                if (str.startsWith("Password:")) {
                    str = str.substring("Password:".length());
                }
                LOGGER.severe("Salida de error: " + str);
                if (str.toLowerCase().contains("password")) {
                    throw new SecurityException("Contrasena incorrecta");
                }
                throw new IOException("Error al desbloquear el llavero");
            }
        }
    }

    private static void installTrustedCertsInAppleKeyChain(File file, File file2, byte[] bArr, Console console) throws InterruptedException, KeyChainException, IOException {
        installTrustedCertInAppleKeyChain(file, bArr, true);
        console.print(Messages.getString("ConfiguratorMacOSX.32"));
        installTrustedCertInAppleKeyChain(file2, bArr, false);
        console.print(Messages.getString("ConfiguratorMacOSX.33"));
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x00cb */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void installTrustedCertInAppleKeyChain(java.io.File r5, byte[] r6, boolean r7) throws java.lang.InterruptedException, es.gob.afirma.standalone.configurator.KeyChainException, java.io.IOException, java.lang.SecurityException {
        /*
            Method dump skipped, instruction units count: 500
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.standalone.configurator.ConfiguratorMacOSX.installTrustedCertInAppleKeyChain(java.io.File, byte[], boolean):void");
    }

    @Override // es.gob.afirma.standalone.configurator.Configurator
    public void uninstall(Console console, PluginsManager pluginsManager) {
        boolean z;
        LOGGER.info("Desinstalacion del certificado raiz de los almacenes de MacOSX");
        try {
            File resourcesDirectory = getResourcesDirectory(true);
            byte[] keyChainPhrase = getKeyChainPhrase(console, false);
            do {
                z = true;
                try {
                    uninstallProcess(resourcesDirectory, keyChainPhrase);
                } catch (InvalidPasswordException e) {
                    keyChainPhrase = getKeyChainPhrase(console, true);
                    z = false;
                }
            } while (!z);
            List<AfirmaPlugin> pluginsLoadedList = null;
            try {
                pluginsLoadedList = pluginsManager.getPluginsLoadedList();
            } catch (Exception e2) {
                LOGGER.log(Level.WARNING, "No se pudo obtener el listado de plugins de Autofirma", (Throwable) e2);
            }
            if (pluginsLoadedList != null && !pluginsLoadedList.isEmpty()) {
                LOGGER.info("Desinstalamos los plugins instalados");
                for (AfirmaPlugin afirmaPlugin : pluginsLoadedList) {
                    try {
                        pluginsManager.uninstallPlugin(afirmaPlugin);
                    } catch (Exception e3) {
                        LOGGER.log(Level.WARNING, "No se pudo desinstalar el plugin: " + afirmaPlugin.getInfo().getName(), (Throwable) e3);
                    }
                }
            }
            File alternativeApplicationDirectory = getAlternativeApplicationDirectory();
            if (alternativeApplicationDirectory.isDirectory()) {
                try {
                    Files.walkFileTree(alternativeApplicationDirectory.toPath(), new HashSet(), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() { // from class: es.gob.afirma.standalone.configurator.ConfiguratorMacOSX.1
                        @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
                        public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) {
                            try {
                                Files.delete(path);
                            } catch (Exception e4) {
                                ConfiguratorMacOSX.LOGGER.warning("No se pudo eliminar el fichero '" + LoggerUtil.getCleanUserHomePath(path.toAbsolutePath().toString()) + "': " + e4);
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
                        public FileVisitResult postVisitDirectory(Path path, IOException iOException) {
                            try {
                                Files.delete(path);
                            } catch (IOException e4) {
                                ConfiguratorMacOSX.LOGGER.warning("No se pudo eliminar el directorio '" + LoggerUtil.getCleanUserHomePath(path.toAbsolutePath().toString()) + "': " + e4);
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (Exception e4) {
                    LOGGER.log(Level.WARNING, "No se ha podido eliminar por completo el directorio alternativo para el certificado SSL", (Throwable) e4);
                }
            }
        } catch (IOException e5) {
            LOGGER.log(Level.SEVERE, "No se pudo obtener el directorio de recursos de la aplicacion", (Throwable) e5);
        }
    }

    private static void uninstallProcess(File file, byte[] bArr) throws InvalidPasswordException {
        uninstallRootCAMacOSXKeyStore(bArr);
        String[] systemUsersHomes = getSystemUsersHomes();
        if (systemUsersHomes != null) {
            try {
                ConfiguratorFirefoxMac.uninstallFromMozillaKeyStore(file, systemUsersHomes, scriptFile);
                return;
            } catch (MozillaProfileNotFoundException e) {
                LOGGER.info("No se han encontrado perfiles de Mozilla de los que desinstalar: " + e);
                return;
            } catch (Exception e2) {
                LOGGER.log(Level.SEVERE, "Se ha producido un error durante la desinstalacion: " + e2, (Throwable) e2);
                return;
            }
        }
        LOGGER.info("No se han encontrado directorios de usuario y se cancela la carga de perfiles de firefox de los que deinstalar certificados");
    }

    private static void uninstallRootCAMacOSXKeyStore(byte[] bArr) throws InvalidPasswordException {
        LOGGER.info("Desinstalamos los anteriores certificados del almacen");
        try {
            uninstallTrustedCertFromAppleKeyChain(CERT_CN, bArr);
        } catch (InvalidPasswordException e) {
            throw e;
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "No se pudo eliminar el anterior certificado 127.0.0.1 del Llavero", (Throwable) e2);
        }
        try {
            uninstallTrustedCertFromAppleKeyChain(CERT_CN_ROOT, bArr);
        } catch (InvalidPasswordException e3) {
            throw e3;
        } catch (Exception e4) {
            LOGGER.log(Level.WARNING, "No se pudo eliminar el anterior certificado Autofirma ROOT del Llavero", (Throwable) e4);
        }
    }

    private static void uninstallTrustedCertFromAppleKeyChain(String str, byte[] bArr) throws InterruptedException, KeyChainException, IOException, SecurityException {
        boolean z;
        String str2;
        int iIndexOf;
        int iIndexOf2;
        do {
            ArrayList arrayList = new ArrayList();
            arrayList.add("sudo");
            arrayList.add("-S");
            arrayList.add("security");
            arrayList.add("find-certificate");
            arrayList.add("-c");
            arrayList.add(str);
            arrayList.add("-Z");
            Process processStart = new ProcessBuilder(arrayList).start();
            OutputStream outputStream = processStart.getOutputStream();
            Throwable th = null;
            try {
                try {
                    outputStream.write(bArr);
                    outputStream.flush();
                    if (outputStream != null) {
                        if (0 != 0) {
                            try {
                                outputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            outputStream.close();
                        }
                    }
                    String strTrim = null;
                    if (processStart.waitFor() == 0) {
                        z = true;
                        try {
                            InputStream inputStream = processStart.getInputStream();
                            Throwable th3 = null;
                            try {
                                try {
                                    byte[] dataFromInputStream = AOUtil.getDataFromInputStream(inputStream);
                                    if (inputStream != null) {
                                        if (0 != 0) {
                                            try {
                                                inputStream.close();
                                            } catch (Throwable th4) {
                                                th3.addSuppressed(th4);
                                            }
                                        } else {
                                            inputStream.close();
                                        }
                                    }
                                    if (dataFromInputStream != null && (iIndexOf = (str2 = new String(dataFromInputStream)).indexOf(FIND_CERT_HASH_PREFIX)) > -1 && (iIndexOf2 = str2.indexOf("\n", iIndexOf)) > -1) {
                                        strTrim = str2.substring(iIndexOf + FIND_CERT_HASH_PREFIX.length(), iIndexOf2).trim();
                                    }
                                } catch (Throwable th5) {
                                    th3 = th5;
                                    throw th5;
                                }
                            } catch (Throwable th6) {
                                if (inputStream != null) {
                                    if (th3 != null) {
                                        try {
                                            inputStream.close();
                                        } catch (Throwable th7) {
                                            th3.addSuppressed(th7);
                                        }
                                    } else {
                                        inputStream.close();
                                    }
                                }
                                throw th6;
                            }
                        } catch (Exception e) {
                            throw new SecurityException("Error al leer la salida del comando de busqueda de certificados en el Llavero", e);
                        }
                    } else {
                        z = false;
                        byte[] dataFromInputStream2 = null;
                        try {
                            InputStream errorStream = processStart.getErrorStream();
                            Throwable th8 = null;
                            try {
                                try {
                                    dataFromInputStream2 = AOUtil.getDataFromInputStream(errorStream);
                                    if (errorStream != null) {
                                        if (0 != 0) {
                                            try {
                                                errorStream.close();
                                            } catch (Throwable th9) {
                                                th8.addSuppressed(th9);
                                            }
                                        } else {
                                            errorStream.close();
                                        }
                                    }
                                    if (dataFromInputStream2 != null) {
                                        String str3 = new String(dataFromInputStream2);
                                        if (str3.contains("SecKeychainSearchCopyNext")) {
                                            z = false;
                                        } else {
                                            if (str3.startsWith("Password:")) {
                                                str3 = str3.substring("Password:".length());
                                            }
                                            if (str3.toLowerCase().contains("password")) {
                                                throw new InvalidPasswordException();
                                            }
                                            LOGGER.info("Salida de error: " + str3);
                                        }
                                    }
                                } catch (Throwable th10) {
                                    th8 = th10;
                                    throw th10;
                                }
                            } catch (Throwable th11) {
                                if (errorStream != null) {
                                    if (th8 != null) {
                                        try {
                                            errorStream.close();
                                        } catch (Throwable th12) {
                                            th8.addSuppressed(th12);
                                        }
                                    } else {
                                        errorStream.close();
                                    }
                                }
                                throw th11;
                            }
                        } catch (Exception e2) {
                            LOGGER.log(Level.WARNING, "No se pudo leer la salida de error del proceso de desinstalacion del certificado en el llavero", (Throwable) e2);
                        }
                    }
                    if (z && strTrim != null) {
                        uninstallTrustedCertFromAppleKeyChainByHash(strTrim);
                    }
                } catch (Throwable th13) {
                    th = th13;
                    throw th13;
                }
            } catch (Throwable th14) {
                if (outputStream != null) {
                    if (th != null) {
                        try {
                            outputStream.close();
                        } catch (Throwable th15) {
                            th.addSuppressed(th15);
                        }
                    } else {
                        outputStream.close();
                    }
                }
                throw th14;
            }
        } while (z);
    }

    private static void uninstallTrustedCertFromAppleKeyChainByHash(String str) throws InterruptedException, KeyChainException, IOException, SecurityException {
        ArrayList arrayList = new ArrayList();
        arrayList.add("sudo");
        arrayList.add("-S");
        arrayList.add("security");
        arrayList.add("delete-certificate");
        arrayList.add("-Z");
        arrayList.add(str);
        arrayList.add("-t");
        Process processStart = new ProcessBuilder(arrayList).start();
        if (processStart.waitFor() != 0) {
            byte[] dataFromInputStream = null;
            try {
                InputStream errorStream = processStart.getErrorStream();
                Throwable th = null;
                try {
                    dataFromInputStream = AOUtil.getDataFromInputStream(errorStream);
                    if (errorStream != null) {
                        if (0 != 0) {
                            try {
                                errorStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            errorStream.close();
                        }
                    }
                } catch (Throwable th3) {
                    if (errorStream != null) {
                        if (0 != 0) {
                            try {
                                errorStream.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            errorStream.close();
                        }
                    }
                    throw th3;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "No se pudo leer la salida de error del proceso de desinstalacion del certificado en el llavero", (Throwable) e);
            }
            if (dataFromInputStream != null) {
                String str2 = new String(dataFromInputStream);
                if (str2.startsWith("Password:")) {
                    str2 = str2.substring("Password:".length());
                }
                LOGGER.severe("Salida de error: " + str2);
                if (str2.toLowerCase().contains("password")) {
                    throw new SecurityException("Contrasena incorrecta");
                }
                throw new KeyChainException("Error al desinstalar el certificado con el hash " + str + " del llavero de macOS");
            }
        }
    }

    private static File getResourcesDirectory() {
        return new File(System.getenv("HOME"), "Library/Application Support/Autofirma");
    }

    private static File getResourcesDirectory(boolean z) throws IOException {
        File resourcesDirectory = getResourcesDirectory();
        if (z && !resourcesDirectory.isDirectory()) {
            if (resourcesDirectory.mkdirs()) {
                ConfiguratorMacUtils.addExexPermissionsToFile(resourcesDirectory);
            }
            if (!resourcesDirectory.isDirectory()) {
                throw new IOException("No se ha podido generar el directorio de recursos de la aplicacion: " + resourcesDirectory.getAbsolutePath());
            }
        }
        return resourcesDirectory;
    }

    public static String executeScriptFile(File file, boolean z, boolean z2) throws InterruptedException, IOException {
        String strRun;
        LOGGER.info("Se ejecuta el script");
        ShellScript shellScript = new ShellScript(file, z2);
        try {
            if (z) {
                strRun = shellScript.runAsAdministrator();
            } else {
                strRun = shellScript.run();
            }
            return strRun;
        } catch (IOException e) {
            throw new IOException("Error en la ejecucion del script via AppleScript: " + e, e);
        }
    }

    private static void closeFirefox() {
        while (isFirefoxOpen()) {
            JOptionPane.showMessageDialog((Component) null, Messages.getString("ConfiguratorMacOSX.17"), Messages.getString("ConfiguratorMacOSX.16"), 2);
        }
    }

    /* JADX WARN: Not initialized variable reg: 10, insn: 0x012a: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r10 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:64:0x012a */
    /* JADX WARN: Not initialized variable reg: 11, insn: 0x012e: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r11 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:66:0x012e */
    private static boolean isFirefoxOpen() {
        InputStream inputStream;
        Throwable th;
        String line;
        try {
            try {
                InputStream inputStream2 = new ProcessBuilder("ps", "aux").start().getInputStream();
                Throwable th2 = null;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream2));
                Throwable th3 = null;
                do {
                    try {
                        try {
                            line = bufferedReader.readLine();
                            if (line == null) {
                                if (bufferedReader != null) {
                                    if (0 != 0) {
                                        try {
                                            bufferedReader.close();
                                        } catch (Throwable th4) {
                                            th3.addSuppressed(th4);
                                        }
                                    } else {
                                        bufferedReader.close();
                                    }
                                }
                                if (inputStream2 != null) {
                                    if (0 != 0) {
                                        try {
                                            inputStream2.close();
                                        } catch (Throwable th5) {
                                            th2.addSuppressed(th5);
                                        }
                                    } else {
                                        inputStream2.close();
                                    }
                                }
                                return false;
                            }
                            if (line.contains("Firefox.app") || line.contains("FirefoxNightly.app")) {
                                break;
                            }
                        } catch (Throwable th6) {
                            th3 = th6;
                            throw th6;
                        }
                    } catch (Throwable th7) {
                        if (bufferedReader != null) {
                            if (th3 != null) {
                                try {
                                    bufferedReader.close();
                                } catch (Throwable th8) {
                                    th3.addSuppressed(th8);
                                }
                            } else {
                                bufferedReader.close();
                            }
                        }
                        throw th7;
                    }
                    LOGGER.warning("No se pudo completar la deteccion del proceso de Firefox. Se considerara que no esta en ejecucion: " + e);
                    return false;
                } while (!line.contains("FirefoxDeveloperEdition.app"));
                if (bufferedReader != null) {
                    if (0 != 0) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable th9) {
                            th3.addSuppressed(th9);
                        }
                    } else {
                        bufferedReader.close();
                    }
                }
                if (inputStream2 != null) {
                    if (0 != 0) {
                        try {
                            inputStream2.close();
                        } catch (Throwable th10) {
                            th2.addSuppressed(th10);
                        }
                    } else {
                        inputStream2.close();
                    }
                }
                return true;
            } catch (Throwable th11) {
                if (inputStream != null) {
                    if (th != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable th12) {
                            th.addSuppressed(th12);
                        }
                    } else {
                        inputStream.close();
                    }
                }
                throw th11;
            }
        } catch (IOException e) {
            LOGGER.warning("No se pudo completar la deteccion del proceso de Firefox. Se considerara que no esta en ejecucion: " + e);
            return false;
        }
    }

    /* JADX WARN: Code duplicated, block: B:101:0x00b0 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:28:0x00ab  */
    /* JADX WARN: Code duplicated, block: B:33:0x00c4 A[Catch: Throwable -> 0x00f2, all -> 0x00fb, Throwable -> 0x0146, all -> 0x014f, Exception -> 0x018c, TryCatch #7 {all -> 0x014f, blocks: (B:7:0x002f, B:8:0x003c, B:9:0x004a, B:11:0x0055, B:13:0x0060, B:18:0x007f, B:21:0x0093, B:20:0x0089, B:40:0x00d6, B:43:0x00ea, B:42:0x00e0, B:24:0x00a3, B:30:0x00b0, B:33:0x00c4, B:32:0x00ba, B:35:0x00cb, B:46:0x00fa, B:52:0x0107, B:55:0x011b, B:54:0x0111, B:57:0x0122, B:68:0x014e), top: B:88:0x000a, outer: #2 }] */
    /* JADX WARN: Not initialized variable reg: 10, insn: 0x00fd: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r10 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:48:0x00fd */
    /* JADX WARN: Not initialized variable reg: 11, insn: 0x0102: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r11 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:50:0x0102 */
    /* JADX WARN: Not initialized variable reg: 8, insn: 0x0151: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r8 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:70:0x0151 */
    /* JADX WARN: Not initialized variable reg: 9, insn: 0x0155: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r9 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:72:0x0155 */
    static String[] getSystemUsersHomes() {
        InputStream inputStream;
        Throwable th;
        InputStreamReader inputStreamReader;
        Throwable th2;
        if (userDirs != null) {
            return userDirs;
        }
        try {
            try {
                String strExecuteScriptFile = executeScriptFile(createGetUsersScript(), false, true);
                HashSet hashSet = new HashSet();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(strExecuteScriptFile.toString().getBytes());
                Throwable th3 = null;
                try {
                    try {
                        InputStreamReader inputStreamReader2 = new InputStreamReader(byteArrayInputStream);
                        Throwable th4 = null;
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader2);
                        Throwable th5 = null;
                        while (true) {
                            try {
                                try {
                                    String line = bufferedReader.readLine();
                                    if (line == null) {
                                        break;
                                    }
                                    if (line.startsWith(USER_DIR_LINE_PREFIX)) {
                                        hashSet.add(line.substring(USER_DIR_LINE_HEADER.length()));
                                    }
                                } catch (Throwable th6) {
                                    th5 = th6;
                                    throw th6;
                                }
                            } catch (Throwable th7) {
                                if (bufferedReader != null) {
                                    if (th5 != null) {
                                        try {
                                            bufferedReader.close();
                                        } catch (Throwable th8) {
                                            th5.addSuppressed(th8);
                                        }
                                    } else {
                                        bufferedReader.close();
                                    }
                                }
                                throw th7;
                            }
                            if (bufferedReader != null) {
                                if (th5 != null) {
                                    bufferedReader.close();
                                } else {
                                    bufferedReader.close();
                                }
                            }
                            throw th7;
                        }
                        if (bufferedReader != null) {
                            if (0 != 0) {
                                try {
                                    bufferedReader.close();
                                } catch (Throwable th9) {
                                    th5.addSuppressed(th9);
                                }
                            } else {
                                bufferedReader.close();
                            }
                        }
                        if (inputStreamReader2 != null) {
                            if (0 != 0) {
                                try {
                                    inputStreamReader2.close();
                                } catch (Throwable th10) {
                                    th4.addSuppressed(th10);
                                }
                            } else {
                                inputStreamReader2.close();
                            }
                        }
                        if (byteArrayInputStream != null) {
                            if (0 != 0) {
                                try {
                                    byteArrayInputStream.close();
                                } catch (Throwable th11) {
                                    th3.addSuppressed(th11);
                                }
                            } else {
                                byteArrayInputStream.close();
                            }
                        }
                        userDirs = (String[]) hashSet.toArray(new String[hashSet.size()]);
                        return userDirs;
                    } catch (Throwable th12) {
                        throw th12;
                    }
                } catch (Throwable th13) {
                    if (inputStreamReader != null) {
                        if (th2 != null) {
                            try {
                                inputStreamReader.close();
                            } catch (Throwable th14) {
                                th2.addSuppressed(th14);
                            }
                        } else {
                            inputStreamReader.close();
                        }
                    }
                    throw th13;
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al obtener el listado de usuarios del sistema", (Throwable) e);
                userDirs = null;
            }
        } catch (Throwable th15) {
            if (inputStream != null) {
                if (th != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable th16) {
                        th.addSuppressed(th16);
                    }
                } else {
                    inputStream.close();
                }
            }
            throw th15;
        }
    }

    private static File createGetUsersScript() throws IOException {
        StringBuilder sb = new StringBuilder(GET_USERS_COMMAND);
        File fileCreateTempFile = File.createTempFile(GET_USER_SCRIPTS_NAME, ".sh");
        try {
            ConfiguratorMacUtils.writeScriptFile(sb, fileCreateTempFile, true);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Ha ocurrido un error al escribir en disco el script de obtencion de usuarios: " + e, (Throwable) e);
        }
        ConfiguratorMacUtils.addExexPermissionsToFile(fileCreateTempFile);
        return fileCreateTempFile;
    }

    @Override // es.gob.afirma.standalone.configurator.Configurator
    public File getAplicationDirectory() {
        try {
            return new File(ConfiguratorMacOSX.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        } catch (URISyntaxException e) {
            LOGGER.log(Level.WARNING, "No se pudo identificar el directorio de instalacion de la aplicacion (en el que se encuentra el configurador)", (Throwable) e);
            return null;
        }
    }

    @Override // es.gob.afirma.standalone.configurator.Configurator
    public File getAlternativeApplicationDirectory() {
        return getResourcesDirectory();
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/ConfiguratorMacOSX$InvalidPasswordException.class */
    static class InvalidPasswordException extends SecurityException {
        private static final long serialVersionUID = -9058805499745499488L;

        InvalidPasswordException() {
        }
    }
}
