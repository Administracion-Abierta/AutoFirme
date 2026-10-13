package es.gob.afirma.standalone.ui.restoreconfig;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.configurator.common.ConfiguratorUtil;
import es.gob.afirma.standalone.so.macos.ShellScript;
import es.gob.afirma.standalone.so.macos.UnixUtils;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/restoreconfig/RestoreConfigMacOSX.class */
final class RestoreConfigMacOSX implements RestoreConfig {
    private static final String KS_FILENAME = "/autofirma.pfx";
    private static final String SSL_CA_CER_FILENAME = "/Autofirma_ROOT.cer";
    private static final String SSL_CER_FILENAME = "/autofirma.cer";
    private static final String KS_PASSWORD = "654321";
    private static final String CERT_CN = "127.0.0.1";
    private static final String CERT_CN_ROOT = "Autofirma ROOT";
    private static final String FIND_CERT_HASH_PREFIX = "hash:";
    private static final String MAC_SCRIPT_NAME = "/installCerScript";
    private static final String MAC_SCRIPT_EXT = ".sh";
    static final String EXPORT_PATH = "export PATH=$PATH:";
    static final String EXPORT_LIBRARY_LD = "export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:";
    private static final String CHANGE_OWN_COMMAND = "chown %USERNAME% \"%DIR%\"";
    static String mac_script_path;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final byte[] DUMMY = "dummy".getBytes();
    private static List<File> userDirs = null;

    RestoreConfigMacOSX() {
    }

    @Override // es.gob.afirma.standalone.ui.restoreconfig.RestoreConfig
    public void restore(RestoreConfigPanel restoreConfigPanel) {
        userDirs = Arrays.asList(getUserHome());
        boolean zIsSelected = restoreConfigPanel.firefoxIntegrationCb.isSelected();
        File macOsXAlternativeAppDir = DesktopUtil.getMacOsXAlternativeAppDir();
        restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.3", macOsXAlternativeAppDir.getAbsolutePath()));
        if (!macOsXAlternativeAppDir.isDirectory()) {
            try {
                Files.createDirectories(macOsXAlternativeAppDir.toPath(), PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwxr-x")));
            } catch (Exception e) {
                macOsXAlternativeAppDir.mkdirs();
            }
            if (!macOsXAlternativeAppDir.isDirectory()) {
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.1"));
                LOGGER.severe("No se puede crear el directorio '" + LoggerUtil.getCleanUserHomePath(macOsXAlternativeAppDir.getAbsolutePath()) + "' se aborta la restauracion");
                return;
            }
        }
        try {
            prepareScript();
            changeDirectoryProperty(macOsXAlternativeAppDir);
            CertFilesInfo certFilesInfoPrepareSslCertificates = prepareSslCertificates(macOsXAlternativeAppDir, restoreConfigPanel);
            if (certFilesInfoPrepareSslCertificates != null) {
                restoreSslCertificatesInKeyChain(macOsXAlternativeAppDir, certFilesInfoPrepareSslCertificates, restoreConfigPanel);
            }
            RestoreConfigFirefoxMacOS restoreConfigFirefoxMacOS = new RestoreConfigFirefoxMacOS(userDirs, restoreConfigPanel);
            if (restoreConfigFirefoxMacOS.hasProfiles()) {
                configureFirefoxTrustStore(restoreConfigFirefoxMacOS, zIsSelected, restoreConfigPanel);
                if (certFilesInfoPrepareSslCertificates != null) {
                    restoreSslCertificatesInFirefox(restoreConfigFirefoxMacOS, macOsXAlternativeAppDir, certFilesInfoPrepareSslCertificates, restoreConfigPanel);
                }
            }
            try {
                removeScript();
            } catch (Exception e2) {
                LOGGER.warning("No se pudo eliminar el script temporal" + e2);
            }
        } catch (Exception e3) {
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.15"));
            LOGGER.log(Level.SEVERE, "Error creando script temporal. Se aborta la operacion", (Throwable) e3);
        }
    }

    private static void restoreSslCertificatesInFirefox(RestoreConfigFirefoxMacOS restoreConfigFirefoxMacOS, File file, CertFilesInfo certFilesInfo, RestoreConfigPanel restoreConfigPanel) {
        if (!closeFirefox(restoreConfigPanel)) {
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.45"));
        }
        try {
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.13"));
            restoreConfigFirefoxMacOS.reinstallRootCAMozillaKeyStore(file, certFilesInfo.getSslRootFile());
        } catch (MozillaProfileNotFoundException e) {
            LOGGER.log(Level.WARNING, "No se ha encontrado el perfil de Mozilla en macOS", (Throwable) e);
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.12"));
        } catch (IOException e2) {
            LOGGER.log(Level.WARNING, "No se pudo configurar certutil para la instalacion en el almacen", (Throwable) e2);
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.35"));
        } catch (Exception e3) {
            LOGGER.log(Level.WARNING, "Error al instalar los certificados en el almacen de confianza de Firefox", (Throwable) e3);
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.19"));
        }
    }

    private static void prepareScript() throws IOException {
        mac_script_path = File.createTempFile(MAC_SCRIPT_NAME, MAC_SCRIPT_EXT).getAbsolutePath();
        UnixUtils.addAllPermissionsToFile(new File(mac_script_path));
    }

    private static void removeScript() throws IOException {
        Files.delete(new File(mac_script_path).toPath());
    }

    private static void configureFirefoxTrustStore(RestoreConfigFirefoxMacOS restoreConfigFirefoxMacOS, boolean z, RestoreConfigPanel restoreConfigPanel) {
        restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString(z ? "RestoreConfigMacOSX.21" : "RestoreConfigMacOSX.22"));
        try {
            restoreConfigFirefoxMacOS.configureUseSystemTrustStore(z);
        } catch (MozillaProfileNotFoundException e) {
            LOGGER.info("No se encontraron perfiles de Firefox en los que configurar la confianza en el almacen del sistema");
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.24"));
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "Error configurando la confianza de Firefox en el almacen del sistema (activando: " + z + ")", (Throwable) e2);
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.23"));
        }
    }

    private static void changeDirectoryProperty(File file) {
        try {
            writeScriptFile(mac_script_path, new StringBuilder(CHANGE_OWN_COMMAND.replace("%DIR%", file.getAbsolutePath()).replace("%USERNAME%", System.getenv("USER"))), true);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se ha podido agregar al script el comando para el cambio de propiedad de: " + LoggerUtil.getCleanUserHomePath(file.getAbsolutePath()), (Throwable) e);
        }
        try {
            executeScript(mac_script_path, true, false);
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "No se ha podido cambiar la propiedad del directorio de Autofirma", (Throwable) e2);
        }
    }

    private static CertFilesInfo prepareSslCertificates(File file, RestoreConfigPanel restoreConfigPanel) {
        CertFilesInfo certFilesInfoBuildCertFiles = buildCertFiles(file);
        if (!certFilesInfoBuildCertFiles.getSslKeyStoreFile().exists()) {
            try {
                generateSslCerts(file, certFilesInfoBuildCertFiles, restoreConfigPanel);
            } catch (IOException e) {
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.7"));
                LOGGER.log(Level.WARNING, "Error al copiar a disco los certificados SSL. Se aborta la operacion", (Throwable) e);
                return null;
            } catch (GeneralSecurityException e2) {
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.9"));
                LOGGER.log(Level.WARNING, "Error al generar los certificados SSL. Se aborta la operacion", (Throwable) e2);
                return null;
            }
        } else {
            try {
                exportSslCerts(certFilesInfoBuildCertFiles);
            } catch (Exception e3) {
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.4"));
                LOGGER.log(Level.WARNING, "Error al copiar los certificados SSL a disco. Se aborta la operacion", (Throwable) e3);
                return null;
            }
        }
        return certFilesInfoBuildCertFiles;
    }

    private static CertFilesInfo buildCertFiles(File file) {
        CertFilesInfo certFilesInfo = new CertFilesInfo();
        certFilesInfo.setSslRootFile(new File(file, SSL_CA_CER_FILENAME));
        certFilesInfo.setSslCertFile(new File(file, SSL_CER_FILENAME));
        certFilesInfo.setSslKeyStoreFile(new File(file, KS_FILENAME));
        return certFilesInfo;
    }

    private static void restoreSslCertificatesInKeyChain(File file, CertFilesInfo certFilesInfo, RestoreConfigPanel restoreConfigPanel) {
        boolean z;
        byte[] keyChainPhrase = getKeyChainPhrase(restoreConfigPanel, false);
        LOGGER.info("Desinstalacion de versiones anteriores del certificado raiz del Llavero del sistema");
        do {
            z = true;
            try {
                uninstallRootCAMacOSXKeyStore(keyChainPhrase);
            } catch (InvalidPasswordException e) {
                keyChainPhrase = getKeyChainPhrase(restoreConfigPanel, true);
                z = false;
            }
        } while (!z);
        restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.6"));
        try {
            installTrustedCertsInAppleKeyChain(certFilesInfo.getSslRootFile(), certFilesInfo.getSslCertFile(), keyChainPhrase, restoreConfigPanel);
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "Error en la importacion del certificado de confianza en el llavero del sistema operativo", (Throwable) e2);
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.20"));
            AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.27", certFilesInfo.getSslRootFile().getAbsolutePath(), certFilesInfo.getSslCertFile().getAbsolutePath()), SimpleAfirmaMessages.getString("RestoreConfigMacOSX.28"), 0, e2);
        }
    }

    private static byte[] getKeyChainPhrase(RestoreConfigPanel restoreConfigPanel, boolean z) {
        String string;
        byte[] bytes = null;
        boolean z2 = false;
        boolean z3 = false;
        do {
            if (!z && bytes == null) {
                bytes = DUMMY;
            } else {
                if (z2) {
                    string = SimpleAfirmaMessages.getString("RestoreConfigMacOSX.30");
                } else {
                    string = SimpleAfirmaMessages.getString("RestoreConfigMacOSX.29");
                }
                try {
                    bytes = new String(AOUIFactory.getPassword(string, restoreConfigPanel)).getBytes(StandardCharsets.UTF_8);
                } catch (AOCancelledOperationException e) {
                    LOGGER.info("Se cancelo el dialogo de entrada de contrasena: " + e);
                    if (AOUIFactory.showConfirmDialog(restoreConfigPanel, SimpleAfirmaMessages.getString("RestoreConfigMacOSX.31"), SimpleAfirmaMessages.getString("RestoreConfigMacOSX.32"), 0, 2) == 0) {
                        restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.33"));
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

    private static void generateSslCerts(File file, CertFilesInfo certFilesInfo, RestoreConfigPanel restoreConfigPanel) throws GeneralSecurityException, IOException {
        restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.5"));
        CertUtil.CertPack certPackForLocalhostSsl = CertUtil.getCertPackForLocalhostSsl(ConfiguratorUtil.CERT_ALIAS, KS_PASSWORD);
        deleteFile(certFilesInfo.getSslRootFile());
        RestoreConfigUtil.installFile(certPackForLocalhostSsl.getCaCertificate().getEncoded(), certFilesInfo.getSslRootFile());
        deleteFile(certFilesInfo.getSslKeyStoreFile());
        RestoreConfigUtil.installFile(certPackForLocalhostSsl.getPkcs12(), certFilesInfo.getSslKeyStoreFile());
        deleteFile(certFilesInfo.getSslCertFile());
        RestoreConfigUtil.installFile(certPackForLocalhostSsl.getSslCertificate().getEncoded(), certFilesInfo.getSslCertFile());
    }

    private static void exportSslCerts(CertFilesInfo certFilesInfo) throws GeneralSecurityException, IOException {
        FileInputStream fileInputStream = new FileInputStream(certFilesInfo.getSslKeyStoreFile());
        Throwable th = null;
        try {
            try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(fileInputStream, KS_PASSWORD.toCharArray());
                Certificate[] certificateChain = keyStore.getCertificateChain(keyStore.aliases().nextElement());
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
                deleteFile(certFilesInfo.getSslRootFile());
                RestoreConfigUtil.installFile(certificateChain[certificateChain.length - 1].getEncoded(), certFilesInfo.getSslRootFile());
                deleteFile(certFilesInfo.getSslCertFile());
                RestoreConfigUtil.installFile(certificateChain[0].getEncoded(), certFilesInfo.getSslCertFile());
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
    }

    private static File getUserHome() {
        return new File(System.getenv("HOME"));
    }

    private static void installTrustedCertsInAppleKeyChain(File file, File file2, byte[] bArr, RestoreConfigPanel restoreConfigPanel) throws InterruptedException, IOException, SecurityException, KeyChainException {
        installTrustedCertInAppleKeyChain(file, bArr, true);
        restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.25"));
        installTrustedCertInAppleKeyChain(file2, bArr, false);
        restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.26"));
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x00cb */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void installTrustedCertInAppleKeyChain(java.io.File r5, byte[] r6, boolean r7) throws java.lang.InterruptedException, java.io.IOException, java.lang.SecurityException, es.gob.afirma.standalone.ui.restoreconfig.KeyChainException {
        /*
            Method dump skipped, instruction units count: 478
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.standalone.ui.restoreconfig.RestoreConfigMacOSX.installTrustedCertInAppleKeyChain(java.io.File, byte[], boolean):void");
    }

    public static Object executeScript(String str, boolean z, boolean z2) throws InterruptedException, IOException {
        String strRun;
        LOGGER.info("Se ejecuta el fichero: " + LoggerUtil.getCleanUserHomePath(str));
        ShellScript shellScript = new ShellScript(new File(str), z2);
        if (z) {
            strRun = shellScript.runAsAdministrator();
        } else {
            strRun = shellScript.run();
        }
        return strRun;
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

    private static void uninstallTrustedCertFromAppleKeyChain(String str, byte[] bArr) throws InterruptedException, IOException, SecurityException, KeyChainException {
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
                                            LOGGER.severe("Salida de error: " + str3);
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

    private static void uninstallTrustedCertFromAppleKeyChainByHash(String str) throws InterruptedException, IOException, SecurityException, KeyChainException {
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

    private static void deleteFile(File file) throws IOException {
        if (file.exists() && !file.delete()) {
            throw new IOException("No puedo eliminar " + file.getName());
        }
    }

    static void writeScriptFile(String str, StringBuilder sb, boolean z) throws IOException {
        writeScriptFile(str, sb.toString(), z);
    }

    static void writeScriptFile(String str, boolean z) throws IOException {
        writeScriptFile(mac_script_path, str, z);
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x0088 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static void writeScriptFile(java.lang.String r5, java.lang.String r6, boolean r7) throws java.io.IOException {
        /*
            java.util.logging.Logger r0 = es.gob.afirma.standalone.ui.restoreconfig.RestoreConfigMacOSX.LOGGER
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r2 = r1
            r2.<init>()
            java.lang.String r2 = "Se escribira en el fichero ("
            java.lang.StringBuilder r1 = r1.append(r2)
            r2 = r5
            java.lang.String r2 = es.gob.afirma.core.misc.LoggerUtil.getCleanUserHomePath(r2)
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = ") el siguiente comando:\n"
            java.lang.StringBuilder r1 = r1.append(r2)
            r2 = r6
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.info(r1)
            java.io.File r0 = new java.io.File
            r1 = r0
            r2 = r5
            r1.<init>(r2)
            r8 = r0
            java.io.FileOutputStream r0 = new java.io.FileOutputStream
            r1 = r0
            r2 = r8
            r3 = r7
            r1.<init>(r2, r3)
            r9 = r0
            r0 = 0
            r10 = r0
            r0 = r9
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L7f java.lang.Throwable -> L88
            r2 = r1
            r2.<init>()     // Catch: java.lang.Throwable -> L7f java.lang.Throwable -> L88
            r2 = r6
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> L7f java.lang.Throwable -> L88
            java.lang.String r2 = "\n"
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> L7f java.lang.Throwable -> L88
            java.lang.String r1 = r1.toString()     // Catch: java.lang.Throwable -> L7f java.lang.Throwable -> L88
            byte[] r1 = r1.getBytes()     // Catch: java.lang.Throwable -> L7f java.lang.Throwable -> L88
            r0.write(r1)     // Catch: java.lang.Throwable -> L7f java.lang.Throwable -> L88
            r0 = r9
            if (r0 == 0) goto Lb0
            r0 = r10
            if (r0 == 0) goto L77
            r0 = r9
            r0.close()     // Catch: java.lang.Throwable -> L6b
            goto Lb0
        L6b:
            r11 = move-exception
            r0 = r10
            r1 = r11
            r0.addSuppressed(r1)
            goto Lb0
        L77:
            r0 = r9
            r0.close()
            goto Lb0
        L7f:
            r11 = move-exception
            r0 = r11
            r10 = r0
            r0 = r11
            throw r0     // Catch: java.lang.Throwable -> L88
        L88:
            r12 = move-exception
            r0 = r9
            if (r0 == 0) goto Lad
            r0 = r10
            if (r0 == 0) goto La8
            r0 = r9
            r0.close()     // Catch: java.lang.Throwable -> L9c
            goto Lad
        L9c:
            r13 = move-exception
            r0 = r10
            r1 = r13
            r0.addSuppressed(r1)
            goto Lad
        La8:
            r0 = r9
            r0.close()
        Lad:
            r0 = r12
            throw r0
        Lb0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.standalone.ui.restoreconfig.RestoreConfigMacOSX.writeScriptFile(java.lang.String, java.lang.String, boolean):void");
    }

    private static boolean closeFirefox(Component component) {
        int i;
        if (isFirefoxOpen()) {
            JOptionPane.showMessageDialog(component, SimpleAfirmaMessages.getString("RestoreApplication.7"), SimpleAfirmaMessages.getString("RestoreApplication.9"), 2);
        }
        int iShowConfirmDialog = 0;
        while (true) {
            i = iShowConfirmDialog;
            if (i != 0 || !isFirefoxOpen()) {
                break;
            }
            iShowConfirmDialog = JOptionPane.showConfirmDialog(component, SimpleAfirmaMessages.getString("RestoreApplication.12"), SimpleAfirmaMessages.getString("RestoreApplication.9"), 2, 2);
        }
        return i == 0;
    }

    private static boolean isFirefoxOpen() {
        try {
            return checkProcess("Firefox.app", "FirefoxNightly.app", "FirefoxDeveloperEdition.app");
        } catch (IOException e) {
            LOGGER.warning("No se pudo completar la deteccion del proceso de Firefox. Se considerara que no esta en ejecucion: " + e);
            return false;
        }
    }

    /* JADX WARN: Code duplicated, block: B:45:0x00ef  */
    /* JADX WARN: Code duplicated, block: B:50:0x0108 A[Catch: Throwable -> 0x0133, all -> 0x013c, TryCatch #8 {, blocks: (B:3:0x0025, B:4:0x0039, B:6:0x0044, B:9:0x0056, B:15:0x0074, B:18:0x0088, B:17:0x007e, B:29:0x00b0, B:35:0x00c3, B:38:0x00d7, B:37:0x00cd, B:41:0x00e7, B:47:0x00f4, B:50:0x0108, B:49:0x00fe, B:52:0x010f), top: B:92:0x0025, outer: #9 }] */
    /* JADX WARN: Code duplicated, block: B:81:0x00f4 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private static boolean checkProcess(String... strArr) throws IOException {
        InputStream inputStream = new ProcessBuilder("ps", "aux").start().getInputStream();
        Throwable th = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            Throwable th2 = null;
            while (true) {
                try {
                    try {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            if (bufferedReader != null) {
                                if (0 != 0) {
                                    try {
                                        bufferedReader.close();
                                    } catch (Throwable th3) {
                                        th2.addSuppressed(th3);
                                    }
                                } else {
                                    bufferedReader.close();
                                }
                            }
                            if (inputStream == null) {
                                return false;
                            }
                            if (0 == 0) {
                                inputStream.close();
                                return false;
                            }
                            try {
                                inputStream.close();
                                return false;
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                                return false;
                            }
                        }
                        for (String str : strArr) {
                            if (line.contains(str)) {
                                if (bufferedReader != null) {
                                    if (0 != 0) {
                                        try {
                                            bufferedReader.close();
                                        } catch (Throwable th5) {
                                            th2.addSuppressed(th5);
                                        }
                                    } else {
                                        bufferedReader.close();
                                    }
                                }
                                if (inputStream != null) {
                                    if (0 != 0) {
                                        try {
                                            inputStream.close();
                                        } catch (Throwable th6) {
                                            th.addSuppressed(th6);
                                        }
                                    } else {
                                        inputStream.close();
                                    }
                                }
                                return true;
                            }
                        }
                    } catch (Throwable th7) {
                        th2 = th7;
                        throw th7;
                    }
                } catch (Throwable th8) {
                    if (bufferedReader != null) {
                        if (th2 != null) {
                            try {
                                bufferedReader.close();
                            } catch (Throwable th9) {
                                th2.addSuppressed(th9);
                            }
                        } else {
                            bufferedReader.close();
                        }
                    }
                    throw th8;
                }
                if (bufferedReader != null) {
                    if (th2 != null) {
                        bufferedReader.close();
                    } else {
                        bufferedReader.close();
                    }
                }
                throw th8;
            }
        } catch (Throwable th10) {
            if (inputStream != null) {
                if (0 != 0) {
                    try {
                        inputStream.close();
                    } catch (Throwable th11) {
                        th.addSuppressed(th11);
                    }
                } else {
                    inputStream.close();
                }
            }
            throw th10;
        }
    }

    /* JADX WARN: Not initialized variable reg: 10, insn: 0x00b1: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r10 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:24:0x00b1 */
    /* JADX WARN: Not initialized variable reg: 11, insn: 0x00b6: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r11 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:26:0x00b6 */
    public static String exportResource(String str, String str2, String str3) throws IOException {
        InputStream inputStream;
        Throwable th;
        FileOutputStream fileOutputStream = new FileOutputStream(str3 + str2);
        Throwable th2 = null;
        try {
            try {
                try {
                    InputStream resourceAsStream = RestoreConfigMacOSX.class.getResourceAsStream(str + str2);
                    Throwable th3 = null;
                    try {
                        if (resourceAsStream == null) {
                            throw new IOException("No ha podido obtenerse el recurso \"" + str2 + "\" del JAR.");
                        }
                        byte[] bArr = new byte[4096];
                        while (true) {
                            int i = resourceAsStream.read(bArr);
                            if (i <= 0) {
                                break;
                            }
                            fileOutputStream.write(bArr, 0, i);
                        }
                        if (resourceAsStream != null) {
                            if (0 != 0) {
                                try {
                                    resourceAsStream.close();
                                } catch (Throwable th4) {
                                    th3.addSuppressed(th4);
                                }
                            } else {
                                resourceAsStream.close();
                            }
                        }
                        if (fileOutputStream != null) {
                            if (0 != 0) {
                                try {
                                    fileOutputStream.close();
                                } catch (Throwable th5) {
                                    th2.addSuppressed(th5);
                                }
                            } else {
                                fileOutputStream.close();
                            }
                        }
                        return str3 + str2;
                    } catch (Throwable th6) {
                        throw th6;
                    }
                } catch (Throwable th7) {
                    throw th7;
                }
            } catch (Throwable th8) {
                if (inputStream != null) {
                    if (th != null) {
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
        } catch (Throwable th10) {
            if (fileOutputStream != null) {
                if (0 != 0) {
                    try {
                        fileOutputStream.close();
                    } catch (Throwable th11) {
                        th2.addSuppressed(th11);
                    }
                } else {
                    fileOutputStream.close();
                }
            }
            throw th10;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/restoreconfig/RestoreConfigMacOSX$InvalidPasswordException.class */
    static class InvalidPasswordException extends SecurityException {
        private static final long serialVersionUID = -8210913902255499160L;

        InvalidPasswordException() {
        }
    }
}
