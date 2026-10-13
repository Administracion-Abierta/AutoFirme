package es.gob.afirma.standalone.ui.restoreconfig;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import es.gob.afirma.core.misc.AOFileUtils;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.configurator.common.ConfiguratorUtil;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/restoreconfig/RestoreConfigWindows.class */
final class RestoreConfigWindows implements RestoreConfig {
    private static final String SSL_KEYSTORE_FILENAME = "autofirma.pfx";
    private static final String CA_CERTIFICATE_FILENAME = "Autofirma_ROOT.cer";
    private static final String KS_PASSWORD = "654321";
    private static final String ADMIN_EXECUTOR_BAT = "execute.bat";
    private static final String RESTORE_PROTOCOL_BAT = "afirma_register.bat";
    private static final String REPLACE_EXE_DIR = "$$PATH_DIR_EXE$$";
    private static final String REPLACE_PATH_BAT = "$$PATH_BAT$$";
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    static final String NEWLINE = System.getProperty("line.separator");
    private static final File appDir = RestoreConfigUtil.getApplicationDirectory();

    RestoreConfigWindows() {
    }

    @Override // es.gob.afirma.standalone.ui.restoreconfig.RestoreConfig
    public void restore(RestoreConfigPanel restoreConfigPanel) {
        File windowsAlternativeAppDir;
        boolean z;
        CertificateFile certificateFileLoadRootCertificate;
        LOGGER.info("Ruta de appDir: " + LoggerUtil.getCleanUserHomePath(appDir.getAbsolutePath()));
        restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.3", appDir.getAbsolutePath()));
        boolean zIsSelected = restoreConfigPanel.firefoxIntegrationCb.isSelected();
        if (Files.isWritable(appDir.toPath())) {
            windowsAlternativeAppDir = appDir;
            z = false;
        } else {
            windowsAlternativeAppDir = DesktopUtil.getWindowsAlternativeAppDir();
            z = true;
        }
        LOGGER.info("Ruta de trabajo:  " + LoggerUtil.getCleanUserHomePath(windowsAlternativeAppDir.getAbsolutePath()));
        if (!windowsAlternativeAppDir.exists()) {
            try {
                windowsAlternativeAppDir.mkdirs();
            } catch (Exception e) {
                LOGGER.warning("No se ha podido crear el directorio: " + LoggerUtil.getCleanUserHomePath(windowsAlternativeAppDir.getAbsolutePath()));
            }
        }
        if (isRebuildCertNeeded(appDir)) {
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.38"));
            try {
                certificateFileLoadRootCertificate = rebuildCertificates(restoreConfigPanel, windowsAlternativeAppDir);
            } catch (Exception e2) {
                LOGGER.log(Level.SEVERE, "No se han podido regenerar los certificados necesarios. No se instalaran en los almacenes de confianza", (Throwable) e2);
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.33"));
                certificateFileLoadRootCertificate = null;
            }
        } else {
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.39"));
            try {
                if (!appDir.equals(windowsAlternativeAppDir)) {
                    copyCerts(appDir, windowsAlternativeAppDir);
                }
                certificateFileLoadRootCertificate = loadRootCertificate(windowsAlternativeAppDir);
            } catch (Exception e3) {
                LOGGER.log(Level.WARNING, "No se ha podido cargar el certificado de CA del directorio de instalacion. No se instalara en los almacenes de confianza", (Throwable) e3);
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.4"));
                certificateFileLoadRootCertificate = null;
            }
        }
        if (certificateFileLoadRootCertificate != null) {
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.18"));
            installRootCAWindowsKeystore(restoreConfigPanel, certificateFileLoadRootCertificate);
        }
        if (certificateFileLoadRootCertificate != null) {
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.13"));
            installRootCAMozillaKeystore(restoreConfigPanel, certificateFileLoadRootCertificate, windowsAlternativeAppDir);
        }
        if (!z) {
            File windowsAlternativeAppDir2 = DesktopUtil.getWindowsAlternativeAppDir();
            if (windowsAlternativeAppDir2.exists()) {
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.36"));
                try {
                    copyCerts(windowsAlternativeAppDir, windowsAlternativeAppDir2);
                } catch (IOException e4) {
                    LOGGER.log(Level.WARNING, "No se ha podido copiar el almacen del certificado SSL al directorio alternativo de instalacion", (Throwable) e4);
                    restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.37"));
                }
            }
        }
        restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.24"));
        try {
            restoreProtocolRegistry(appDir.getAbsoluteFile(), windowsAlternativeAppDir.getAbsoluteFile());
        } catch (Exception e5) {
            LOGGER.log(Level.WARNING, "Error restaurando los valores del protocolo 'afirma'", (Throwable) e5);
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.25"));
        }
        if (zIsSelected) {
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.41"));
        } else {
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.42"));
        }
        try {
            RestoreConfigFirefox.configureUseSystemTrustStore(zIsSelected);
        } catch (MozillaProfileNotFoundException e6) {
            LOGGER.info("No se encontraron perfiles de Firefox en los que configurar la confianza en el almacen del sistema");
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.44"));
        } catch (Exception e7) {
            LOGGER.log(Level.WARNING, "Error configurando la confianza de Firefox en el almacen del sistema (activando: " + zIsSelected + ")", (Throwable) e7);
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.43"));
        }
    }

    private static void copyCerts(File file, File file2) throws IOException {
        Files.copy(new File(file, SSL_KEYSTORE_FILENAME).toPath(), new File(file2, SSL_KEYSTORE_FILENAME).toPath(), StandardCopyOption.REPLACE_EXISTING);
        AOFileUtils.setAllPermissions(new File(file2, SSL_KEYSTORE_FILENAME));
        Files.copy(new File(file, CA_CERTIFICATE_FILENAME).toPath(), new File(file2, CA_CERTIFICATE_FILENAME).toPath(), StandardCopyOption.REPLACE_EXISTING);
        AOFileUtils.setAllPermissions(new File(file2, CA_CERTIFICATE_FILENAME));
    }

    private static boolean checkSSLKeyStoreGenerated(File file) {
        return new File(file, SSL_KEYSTORE_FILENAME).exists();
    }

    private static boolean checkSSLRootCertificateGenerated(File file) {
        return new File(file, CA_CERTIFICATE_FILENAME).exists();
    }

    /* JADX WARN: Code duplicated, block: B:21:0x00a3  */
    /* JADX WARN: Code duplicated, block: B:26:0x00bc A[Catch: IOException -> 0x00c7, TryCatch #0 {IOException -> 0x00c7, blocks: (B:3:0x0007, B:4:0x004d, B:6:0x0057, B:11:0x0077, B:14:0x008b, B:13:0x0081, B:17:0x009b, B:23:0x00a8, B:26:0x00bc, B:25:0x00b2, B:28:0x00c3), top: B:37:0x0007, inners: #1, #2, #4 }] */
    /* JADX WARN: Code duplicated, block: B:40:0x00a8 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private static Boolean isProcessRunningWindows(String str) {
        String str2 = "";
        Boolean bool = Boolean.FALSE;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ProcessBuilder(System.getenv("windir") + "\\system32\\tasklist.exe").start().getInputStream()));
            Throwable th = null;
            while (true) {
                try {
                    try {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        str2 = str2 + line;
                    } catch (Throwable th2) {
                        if (bufferedReader != null) {
                            if (th != null) {
                                try {
                                    bufferedReader.close();
                                } catch (Throwable th3) {
                                    th.addSuppressed(th3);
                                }
                            } else {
                                bufferedReader.close();
                            }
                        }
                        throw th2;
                    }
                } catch (Throwable th4) {
                    th = th4;
                    throw th4;
                }
                if (bufferedReader != null) {
                    if (th != null) {
                        bufferedReader.close();
                    } else {
                        bufferedReader.close();
                    }
                }
                throw th2;
            }
            if (bufferedReader != null) {
                if (0 != 0) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    bufferedReader.close();
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Ha ocurrido un error al ejecutar el comando " + str + " en Windows. " + e.getMessage());
        }
        if (str2.contains(str)) {
            bool = Boolean.TRUE;
        }
        return bool;
    }

    private static boolean closeFirefox(Component component) {
        int i;
        if (isProcessRunningWindows("firefox.exe").booleanValue()) {
            JOptionPane.showMessageDialog(component, SimpleAfirmaMessages.getString("RestoreApplication.7"), SimpleAfirmaMessages.getString("RestoreApplication.9"), 2);
        }
        int iShowConfirmDialog = 0;
        while (true) {
            i = iShowConfirmDialog;
            if (i != 0 || !isProcessRunningWindows("firefox.exe").booleanValue()) {
                break;
            }
            iShowConfirmDialog = JOptionPane.showConfirmDialog(component, SimpleAfirmaMessages.getString("RestoreApplication.12"), SimpleAfirmaMessages.getString("RestoreApplication.9"), 2, 2);
        }
        return i == 0;
    }

    private static void installRootCAWindowsKeystore(RestoreConfigPanel restoreConfigPanel, CertificateFile certificateFile) {
        try {
            KeyStore keyStore = KeyStore.getInstance("Windows-ROOT");
            keyStore.load(null, null);
            try {
                Certificate certificate = keyStore.getCertificate("Autofirma ROOT");
                if (certificate != null && certificate.equals(certificateFile.getCert())) {
                    LOGGER.info("El certificado raiz ya se encontraba instalado en el almacen del sistema");
                    restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.26"));
                    return;
                }
            } catch (Exception e) {
                LOGGER.warning("No se pudo comprobar si el certificado ya estaba en el almacen: " + e);
            }
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.22"));
            while (keyStore.getCertificate("Autofirma ROOT") != null) {
                try {
                    restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.35"));
                    keyStore.deleteEntry("Autofirma ROOT");
                } catch (KeyStoreException e2) {
                    restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.34"));
                    LOGGER.info("No se ha podido eliminar alguna importacion previa del certificado raiz del almacen de Windows: " + e2.getMessage());
                }
            }
            boolean z = false;
            do {
                try {
                    keyStore.setCertificateEntry("Autofirma ROOT", certificateFile.getCert());
                    z = true;
                } catch (KeyStoreException e3) {
                    LOGGER.log(Level.WARNING, "No se pudo instalar la CA del certificado SSL para el socket en el almacen de Windows", (Throwable) e3);
                    if (JOptionPane.showConfirmDialog((Component) null, SimpleAfirmaMessages.getString("RestoreConfigWindows.0"), SimpleAfirmaMessages.getString("RestoreConfigWindows.1"), 2, 2) == 2) {
                        LOGGER.severe("El usuario cancelo la instalacion del certificado SSL para el socket: " + e3);
                        restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.32"));
                        return;
                    }
                }
            } while (!z);
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e4) {
            LOGGER.severe("No se ha podido cargar el almacen de certificados de confianza de Windows: " + e4);
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.20"));
        }
    }

    private static void installRootCAMozillaKeystore(RestoreConfigPanel restoreConfigPanel, CertificateFile certificateFile, File file) {
        try {
            if (!closeFirefox(restoreConfigPanel)) {
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.45"));
            }
            RestoreConfigFirefox.copyConfigurationFiles(file);
            LOGGER.info("Desinstalamos el certificado raiz del almacen de Firefox");
            RestoreConfigFirefox.uninstallRootCAMozillaKeyStore(file);
            RestoreConfigFirefox.installRootCAMozillaKeyStore(file, certificateFile.getFile());
            RestoreConfigFirefox.removeConfigurationFiles(file);
        } catch (MozillaProfileNotFoundException e) {
            LOGGER.warning("No se ha encontrado el perfil de Mozilla en Windows: " + e);
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.12"));
        } catch (IOException | KeyStoreException e2) {
            LOGGER.log(Level.SEVERE, "Error instalando el certificado raiz: " + e2, e2);
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.31", file.getAbsolutePath()));
        }
    }

    private static void restoreProtocolRegistry(File file, File file2) throws GeneralSecurityException {
        LOGGER.info("Vamos a restaurar el protocolo 'afirma'");
        try {
            if (!Advapi32Util.registryKeyExists(WinReg.HKEY_CLASSES_ROOT, "afirma")) {
                Advapi32Util.registryCreateKey(WinReg.HKEY_CLASSES_ROOT, "afirma");
            }
            Advapi32Util.registrySetStringValue(WinReg.HKEY_CLASSES_ROOT, "afirma", "", "URL:Afirma Protocol");
            Advapi32Util.registrySetStringValue(WinReg.HKEY_CLASSES_ROOT, "afirma", "URL Protocol", "");
            if (!Advapi32Util.registryKeyExists(WinReg.HKEY_CLASSES_ROOT, "afirma\\DefaultIcon")) {
                Advapi32Util.registryCreateKey(WinReg.HKEY_CLASSES_ROOT, "afirma\\DefaultIcon");
            }
            Advapi32Util.registrySetStringValue(WinReg.HKEY_CLASSES_ROOT, "afirma\\DefaultIcon", "", file + "\\ic_firmar.ico");
            if (!Advapi32Util.registryKeyExists(WinReg.HKEY_CLASSES_ROOT, "afirma\\shell\\open\\command")) {
                Advapi32Util.registryCreateKey(WinReg.HKEY_CLASSES_ROOT, "afirma\\shell\\open\\command");
            }
            Advapi32Util.registrySetStringValue(WinReg.HKEY_CLASSES_ROOT, "afirma\\shell\\open\\command", "", file + "\\Autofirma.exe %1");
        } catch (Error | Exception e) {
            LOGGER.warning("No se pudo actualizar el registro con los permisos del usuario, se solicita elevar privilegios: " + e);
            int iRestoreProtocolRegistryByApp = restoreProtocolRegistryByApp(file, file2.getAbsolutePath());
            if (iRestoreProtocolRegistryByApp != 0) {
                throw new GeneralSecurityException("No se pudo registrar el protocolo afirma. Codigo de error: " + iRestoreProtocolRegistryByApp);
            }
        }
        LOGGER.info("Configurado 'afirma' en registro Windows");
    }

    private static CertificateFile rebuildCertificates(RestoreConfigPanel restoreConfigPanel, File file) throws IOException {
        CertificateFile certificateFile;
        if (!checkSSLKeyStoreGenerated(file)) {
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.16"));
            try {
                deleteCertificatesFromDisk(file);
            } catch (IOException e) {
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.17"));
                LOGGER.log(Level.SEVERE, "Error al eliminar los certificados SSL anteriores de disco: " + e);
            }
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.5"));
            try {
                CertUtil.CertPack certPackForLocalhostSsl = CertUtil.getCertPackForLocalhostSsl(ConfiguratorUtil.CERT_ALIAS, KS_PASSWORD);
                certificateFile = new CertificateFile(certPackForLocalhostSsl.getCaCertificate());
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.11"));
                File file2 = new File(file, CA_CERTIFICATE_FILENAME);
                try {
                    RestoreConfigUtil.installFile(certPackForLocalhostSsl.getPkcs12(), new File(file, SSL_KEYSTORE_FILENAME));
                    RestoreConfigUtil.installFile(certPackForLocalhostSsl.getCaCertificate().getEncoded(), file2);
                } catch (Exception e2) {
                    LOGGER.log(Level.SEVERE, "No se ha podido guardar en disco los certificados SSL. Los almacenaremos en un directorio alternativo", (Throwable) e2);
                    File windowsAlternativeAppDir = DesktopUtil.getWindowsAlternativeAppDir();
                    if (!windowsAlternativeAppDir.isDirectory() && !windowsAlternativeAppDir.mkdirs()) {
                        throw new IOException("No se ha podido guardar en disco los certificados SSL. Error al crear el directorio alternativo");
                    }
                    try {
                        file2 = new File(windowsAlternativeAppDir, CA_CERTIFICATE_FILENAME);
                        RestoreConfigUtil.installFile(certPackForLocalhostSsl.getPkcs12(), new File(windowsAlternativeAppDir, SSL_KEYSTORE_FILENAME));
                        RestoreConfigUtil.installFile(certPackForLocalhostSsl.getCaCertificate().getEncoded(), file2);
                    } catch (Exception e3) {
                        LOGGER.log(Level.SEVERE, "No se ha podido guardar en el directorio alternativo los certificados SSL", (Throwable) e2);
                        throw new IOException("Error guardando en el directorio alternativo los certificados SSL", e2);
                    }
                }
                certificateFile.setFile(file2);
            } catch (GeneralSecurityException e4) {
                LOGGER.log(Level.SEVERE, "No se ha podido generar el certificado SSL", (Throwable) e4);
                throw new IOException("No se ha podido generar el certificado SSL", e4);
            }
        } else if (!checkSSLRootCertificateGenerated(file)) {
            try {
                FileInputStream fileInputStream = new FileInputStream(new File(file, SSL_KEYSTORE_FILENAME));
                Throwable th = null;
                try {
                    KeyStore keyStore = KeyStore.getInstance("PKCS12");
                    keyStore.load(fileInputStream, KS_PASSWORD.toCharArray());
                    Certificate[] certificateChain = keyStore.getCertificateChain(ConfiguratorUtil.CERT_ALIAS);
                    certificateFile = new CertificateFile(certificateChain[certificateChain.length - 1]);
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
                    restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.11"));
                    File file3 = new File(file, CA_CERTIFICATE_FILENAME);
                    try {
                        RestoreConfigUtil.installFile(certificateFile.getCert().getEncoded(), file3);
                    } catch (Exception e5) {
                        LOGGER.log(Level.SEVERE, "No se ha podido guardar en disco el certificado raiz SSL. Lo extraemos a un directorio alternativo", (Throwable) e5);
                        File windowsAlternativeAppDir2 = DesktopUtil.getWindowsAlternativeAppDir();
                        if (!windowsAlternativeAppDir2.isDirectory() && !windowsAlternativeAppDir2.mkdirs()) {
                            throw new IOException("No se ha podido guardar en disco el certificado raiz SSL. Error al crear el directorio alternativo");
                        }
                        try {
                            RestoreConfigUtil.installFile(certificateFile.getCert().getEncoded(), windowsAlternativeAppDir2);
                            file3 = new File(windowsAlternativeAppDir2, CA_CERTIFICATE_FILENAME);
                        } catch (Exception e6) {
                            LOGGER.log(Level.SEVERE, "No se ha podido guardar en disco el certificado raiz SSL", (Throwable) e5);
                            throw new IOException("Error guardando en disco el certificado raiz SSL", e5);
                        }
                    }
                    certificateFile.setFile(file3);
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
            } catch (Exception e7) {
                LOGGER.log(Level.SEVERE, "Error al extraer el certificado raiz del PKCS#12", (Throwable) e7);
                throw new IOException("Error al generar el certificado SSL", e7);
            }
        } else {
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.14"));
            File file4 = new File(file, CA_CERTIFICATE_FILENAME);
            certificateFile = new CertificateFile(CertUtil.loadCertificate(file4));
            certificateFile.setFile(file4);
        }
        return certificateFile;
    }

    private static boolean isRebuildCertNeeded(File file) {
        return (new File(file, CA_CERTIFICATE_FILENAME).isFile() && new File(file, SSL_KEYSTORE_FILENAME).isFile()) ? false : true;
    }

    private static CertificateFile loadRootCertificate(File file) throws IOException {
        File file2 = new File(file, CA_CERTIFICATE_FILENAME);
        CertificateFile certificateFile = new CertificateFile(CertUtil.loadCertificate(file2));
        certificateFile.setFile(file2);
        return certificateFile;
    }

    private static void deleteCertificatesFromDisk(File file) throws IOException {
        if (checkSSLKeyStoreGenerated(file)) {
            Files.delete(new File(file, SSL_KEYSTORE_FILENAME).toPath());
        }
        if (checkSSLRootCertificateGenerated(file)) {
            Files.delete(new File(file, CA_CERTIFICATE_FILENAME).toPath());
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Not initialized variable reg: 10, insn: 0x00b5: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r10 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:39:0x00b5 */
    /* JADX WARN: Not initialized variable reg: 11, insn: 0x00b9: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r11 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:41:0x00b9 */
    /* JADX WARN: Type inference failed for: r0v35, types: [java.lang.Throwable] */
    private static int restoreProtocolRegistryByApp(File file, String str) {
        FileOutputStream fileOutputStream;
        Throwable th;
        File file2 = new File(str, RESTORE_PROTOCOL_BAT);
        try {
            try {
                FileOutputStream fileOutputStream2 = new FileOutputStream(file2);
                Throwable th2 = null;
                boolean z = false;
                boolean z2 = false;
                InputStream resourceAsStream = RestoreConfigWindows.class.getResourceAsStream("/windows/afirma_register.bat");
                Throwable th3 = null;
                try {
                    try {
                        fileOutputStream2.write(AOUtil.getDataFromInputStream(resourceAsStream));
                        fileOutputStream2.flush();
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
                        Object obj = resourceAsStream;
                        if (fileOutputStream2 != null) {
                            if (0 != 0) {
                                try {
                                    fileOutputStream2.close();
                                    obj = resourceAsStream;
                                } catch (Throwable th5) {
                                    th2.addSuppressed(th5);
                                    obj = th5;
                                }
                            } else {
                                fileOutputStream2.close();
                                obj = resourceAsStream;
                            }
                        }
                        AOFileUtils.setAllPermissions(file2);
                        File file3 = new File(str, ADMIN_EXECUTOR_BAT);
                        try {
                            try {
                                FileOutputStream fileOutputStream3 = new FileOutputStream(file3);
                                Throwable th6 = null;
                                InputStream resourceAsStream2 = RestoreConfigWindows.class.getResourceAsStream("/windows/execute.bat");
                                Throwable th7 = null;
                                try {
                                    fileOutputStream3.write(new String(AOUtil.getDataFromInputStream(resourceAsStream2)).replace(REPLACE_PATH_BAT, file2.getAbsolutePath().replace("\\", "\\\\")).replace(REPLACE_EXE_DIR, file.getAbsolutePath().replace("\\", "\\\\")).getBytes());
                                    fileOutputStream3.flush();
                                    if (resourceAsStream2 != null) {
                                        if (0 != 0) {
                                            try {
                                                resourceAsStream2.close();
                                            } catch (Throwable th8) {
                                                th7.addSuppressed(th8);
                                            }
                                        } else {
                                            resourceAsStream2.close();
                                        }
                                    }
                                    if (fileOutputStream3 != null) {
                                        if (0 != 0) {
                                            try {
                                                fileOutputStream3.close();
                                            } catch (Throwable th9) {
                                                th6.addSuppressed(th9);
                                            }
                                        } else {
                                            fileOutputStream3.close();
                                        }
                                    }
                                    AOFileUtils.setAllPermissions(file3);
                                    int iWaitFor = -2;
                                    try {
                                        Process processStart = new ProcessBuilder(file3.getAbsolutePath()).start();
                                        iWaitFor = processStart.waitFor();
                                        processStart.destroyForcibly();
                                    } catch (Exception e) {
                                        LOGGER.log(Level.WARNING, "Error durante la ejecucion del proceso de restauracion del protocolo \"afirma\"", (Throwable) e);
                                    }
                                    try {
                                        Thread.sleep(1000L);
                                    } catch (InterruptedException e2) {
                                    }
                                    try {
                                        Files.delete(file3.toPath());
                                        Files.delete(file2.toPath());
                                    } catch (IOException e3) {
                                        LOGGER.log(Level.WARNING, "No se pudo eliminar el ejecutable para el registro del protocolo \"afirma\"", (Throwable) e3);
                                    }
                                    return iWaitFor;
                                } catch (Throwable th10) {
                                    if (resourceAsStream2 != null) {
                                        if (0 != 0) {
                                            try {
                                                resourceAsStream2.close();
                                            } catch (Throwable th11) {
                                                th7.addSuppressed(th11);
                                            }
                                        } else {
                                            resourceAsStream2.close();
                                        }
                                    }
                                    throw th10;
                                }
                            } catch (Throwable th12) {
                                if (0 != 0) {
                                    if (obj != null) {
                                        try {
                                            (z ? 1 : 0).close();
                                        } catch (Throwable th13) {
                                            obj.addSuppressed(th13);
                                        }
                                    } else {
                                        (z2 ? 1 : 0).close();
                                    }
                                }
                                throw th12;
                            }
                        } catch (Exception e4) {
                            LOGGER.log(Level.WARNING, "No se pudo copiar a disco el bat para la ejecucion de la restauracion como administrador. Se abortara su ejecucion", (Throwable) e4);
                            return 2;
                        }
                    } catch (Throwable th14) {
                        th3 = th14;
                        throw th14;
                    }
                } catch (Throwable th15) {
                    if (resourceAsStream != null) {
                        if (th3 != null) {
                            try {
                                resourceAsStream.close();
                            } catch (Throwable th16) {
                                th3.addSuppressed(th16);
                            }
                        } else {
                            resourceAsStream.close();
                        }
                    }
                    throw th15;
                }
            } catch (Throwable th17) {
                if (fileOutputStream != null) {
                    if (th != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th18) {
                            th.addSuppressed(th18);
                        }
                    } else {
                        fileOutputStream.close();
                    }
                }
                throw th17;
            }
        } catch (Exception e5) {
            LOGGER.log(Level.WARNING, "No se pudo copiar a disco el bat de restauracion. Se abortara su ejecucion", (Throwable) e5);
            return 2;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/restoreconfig/RestoreConfigWindows$CertificateFile.class */
    static class CertificateFile {
        private final Certificate cert;
        private File file;

        public CertificateFile(Certificate certificate) {
            this.cert = certificate;
        }

        public File getFile() {
            return this.file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public Certificate getCert() {
            return this.cert;
        }
    }
}
