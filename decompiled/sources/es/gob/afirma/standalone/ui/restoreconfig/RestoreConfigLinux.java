package es.gob.afirma.standalone.ui.restoreconfig;

import com.aowagie.text.ElementTags;
import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.BoundedBufferedReader;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.configurator.common.ConfiguratorUtil;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/restoreconfig/RestoreConfigLinux.class */
final class RestoreConfigLinux implements RestoreConfig {
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String KS_FILENAME = "autofirma.pfx";
    private static final String FILE_AUTOFIRMA_CERTIFICATE = "Autofirma_ROOT.cer";
    private static final String KS_PASSWORD = "654321";
    private static final String PROTOCOL_HANDLER_CONFIG_FILE = "autofirma.js";
    private static final String DEFAULT_PROTOCOL_HANDLER_CONFIG_DIR = "/etc/firefox/pref";
    static final String EXPORT_PATH = "export PATH=$PATH:";
    static final String EXPORT_LD_LIBRARY = "export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:";

    RestoreConfigLinux() {
    }

    @Override // es.gob.afirma.standalone.ui.restoreconfig.RestoreConfig
    public void restore(RestoreConfigPanel restoreConfigPanel) {
        File linuxAlternativeAppDir;
        File applicationDirectory = RestoreConfigUtil.getApplicationDirectory();
        restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.3", applicationDirectory.getAbsolutePath()));
        if (Files.isWritable(applicationDirectory.toPath())) {
            linuxAlternativeAppDir = applicationDirectory;
        } else {
            try {
                linuxAlternativeAppDir = getLinuxAlternativeAppDir();
            } catch (IOException e) {
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.1"));
                LOGGER.log(Level.SEVERE, "No se puede utilizar el directorio alternativo de trabajo", (Throwable) e);
                return;
            }
        }
        LOGGER.info("Directorio de trabajo: " + LoggerUtil.getCleanUserHomePath(linuxAlternativeAppDir.getAbsolutePath()));
        List<String> systemUsersHomes = null;
        try {
            systemUsersHomes = getSystemUsersHomes();
        } catch (IOException e2) {
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.6"));
            LOGGER.log(Level.WARNING, "No se puede utilizar el directorio alternativo de trabajo");
        }
        if (systemUsersHomes != null) {
            try {
                restoreSslCertificates(applicationDirectory, linuxAlternativeAppDir, systemUsersHomes, restoreConfigPanel);
            } catch (AOCancelledOperationException e3) {
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.19"));
                return;
            }
        }
        restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.15"));
        try {
            restoreProtocolHandler(linuxAlternativeAppDir);
        } catch (IOException | RuntimeException e4) {
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.16"));
            LOGGER.log(Level.WARNING, "Error en la restauracion del protocolo afirma", e4);
        }
    }

    private static void restoreSslCertificates(File file, File file2, List<String> list, RestoreConfigPanel restoreConfigPanel) {
        File file3;
        if (!checkSSLKeyStoreGenerated(file)) {
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.5"));
            deleteInstalledCertificates(file);
            try {
                CertUtil.CertPack certPackForLocalhostSsl = CertUtil.getCertPackForLocalhostSsl(ConfiguratorUtil.CERT_ALIAS, KS_PASSWORD);
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.11"));
                try {
                    RestoreConfigUtil.installFile(certPackForLocalhostSsl.getPkcs12(), new File(file2, KS_FILENAME));
                    file3 = new File(file2, FILE_AUTOFIRMA_CERTIFICATE);
                    RestoreConfigUtil.installFile(certPackForLocalhostSsl.getCaCertificate().getEncoded(), file3);
                } catch (IOException e) {
                    restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.4"));
                    LOGGER.log(Level.SEVERE, "Error al copiar los certificados SSL a disco", (Throwable) e);
                    return;
                } catch (Exception e2) {
                    restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.4"));
                    LOGGER.log(Level.SEVERE, "Error al extraer los certificados SSL para copiarlos a disco", (Throwable) e2);
                    return;
                }
            } catch (Exception e3) {
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.2"));
                LOGGER.log(Level.SEVERE, "Error al generar los certificados SSL", (Throwable) e3);
                return;
            }
        } else if (!checkSSLRootCertificateGenerated(file)) {
            try {
                FileInputStream fileInputStream = new FileInputStream(new File(file, KS_FILENAME));
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
                        file3 = new File(file2, FILE_AUTOFIRMA_CERTIFICATE);
                        RestoreConfigUtil.installFile(certificateChain[certificateChain.length - 1].getEncoded(), file3);
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
            } catch (Exception e4) {
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.4"));
                LOGGER.log(Level.SEVERE, "Error al copiar los certificados SSL a disco", (Throwable) e4);
                return;
            }
        } else {
            file3 = new File(file, FILE_AUTOFIRMA_CERTIFICATE);
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.14"));
        }
        try {
            RestoreConfigFirefox.copyConfigurationFiles(file2);
            LOGGER.info("Se va a instalar el certificado CA raiz en Google Chrome");
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.9"));
            closeChrome(restoreConfigPanel);
            try {
                RestoreConfigFirefox.installRootCAChromeKeyStore(file2, file3, list);
            } catch (Exception e5) {
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.10"));
                LOGGER.log(Level.WARNING, "Error al instalar el certificado de confianza en el almacen del sistema: " + e5, (Throwable) e5);
            }
            LOGGER.info("Se va a instalar el certificado CA raiz en Mozilla Firefox");
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.13"));
            if (!closeFirefox(restoreConfigPanel)) {
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigWindows.45"));
            }
            RestoreConfigFirefox.uninstallRootCAMozillaKeyStore(file2);
            try {
                RestoreConfigFirefox.installRootCAMozillaKeyStore(file2, file3, list);
            } catch (MozillaProfileNotFoundException e6) {
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.12"));
                LOGGER.warning("Error al obtener los perfiles de usuario de Mozilla Firefox: " + e6);
            } catch (Exception e7) {
                restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.18"));
                LOGGER.log(Level.WARNING, "Error al instalar el certificado de confianza en el almacen de Firefox: " + e7, (Throwable) e7);
            }
        } catch (IOException e8) {
            restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigLinux.17"));
            LOGGER.log(Level.SEVERE, "No se pudo copiar certUtil al directorio de trabajo. Se omitira la instalacion de los certificados: " + e8, (Throwable) e8);
        }
    }

    /* JADX WARN: Code duplicated, block: B:123:0x0191 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:67:0x018c  */
    /* JADX WARN: Code duplicated, block: B:72:0x01a5 A[Catch: Throwable -> 0x01d3, all -> 0x01dc, Exception -> 0x0207, TryCatch #10 {, blocks: (B:45:0x010e, B:46:0x0129, B:48:0x0133, B:50:0x013f, B:52:0x014a, B:57:0x0160, B:60:0x0174, B:59:0x016a, B:63:0x0184, B:69:0x0191, B:72:0x01a5, B:71:0x019b, B:74:0x01ac), top: B:119:0x010e }] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v10, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r0v5 */
    /* JADX WARN: Type inference failed for: r0v68 */
    /* JADX WARN: Type inference failed for: r0v69, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r0v9, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r11v1, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r11v2 */
    /* JADX WARN: Type inference failed for: r11v3 */
    /* JADX WARN: Type inference failed for: r12v0 */
    /* JADX WARN: Type inference failed for: r12v1 */
    /* JADX WARN: Type inference failed for: r12v10 */
    /* JADX WARN: Type inference failed for: r12v11 */
    /* JADX WARN: Type inference failed for: r12v12 */
    /* JADX WARN: Type inference failed for: r12v13 */
    /* JADX WARN: Type inference failed for: r12v4, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r12v5, types: [int] */
    /* JADX WARN: Type inference failed for: r12v6 */
    /* JADX WARN: Type inference failed for: r12v8 */
    /* JADX WARN: Type inference failed for: r12v9 */
    private static List<String> getSystemUsersHomes() throws IOException {
        ?? th;
        ?? r12;
        boolean z = true;
        try {
            Process processExecuteProcess = executeProcess(ElementTags.ID, "-u");
            processExecuteProcess.waitFor();
            byte[] bArr = new byte[100];
            InputStream inputStream = processExecuteProcess.getInputStream();
            ?? r11 = 0;
            try {
                try {
                    th = inputStream.read(bArr);
                    if (th <= 0 || !"0".equals(new String(bArr, 0, (int) th))) {
                        z = false;
                    }
                    ?? r13 = th;
                    if (inputStream != null) {
                        if (0 != 0) {
                            try {
                                inputStream.close();
                                r13 = th;
                            } catch (Throwable th2) {
                                r11.addSuppressed(th2);
                                r13 = th2;
                            }
                        } else {
                            inputStream.close();
                            r13 = th;
                        }
                    }
                    r12 = r13;
                } catch (Throwable th3) {
                    th = th3;
                    r11 = th;
                    throw th;
                }
            } catch (Throwable th4) {
                if (inputStream != null) {
                    if (r11 != 0) {
                        try {
                            inputStream.close();
                        } catch (Throwable th5) {
                            r11.addSuppressed(th5);
                        }
                    } else {
                        inputStream.close();
                    }
                }
                throw th4;
            }
        } catch (Exception e) {
            LOGGER.warning("No se pudo determinar si el usuario tiene permisos de administracion: " + e);
            r12 = th;
        }
        if (!z) {
            ArrayList arrayList = new ArrayList();
            try {
                arrayList.add(System.getProperty("user.home"));
                return arrayList;
            } catch (Exception e2) {
                throw new IOException("No se pudo identificar el directorio del usuario", e2);
            }
        }
        try {
            try {
                Process processExecuteProcess2 = executeProcess("cut", "-d:", "-f6", "/etc/passwd");
                ArrayList arrayList2 = new ArrayList();
                InputStream inputStream2 = processExecuteProcess2.getInputStream();
                Throwable th6 = null;
                BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(new InputStreamReader(inputStream2), 2048, 2048);
                Throwable th7 = null;
                while (true) {
                    try {
                        try {
                            String line = boundedBufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            if (line.toLowerCase().contains("home/") && !arrayList2.contains(line)) {
                                arrayList2.add(line);
                            }
                        } catch (Throwable th8) {
                            th7 = th8;
                            throw th8;
                        }
                    } catch (Throwable th9) {
                        if (boundedBufferedReader != null) {
                            if (th7 != null) {
                                try {
                                    boundedBufferedReader.close();
                                } catch (Throwable th10) {
                                    th7.addSuppressed(th10);
                                }
                            } else {
                                boundedBufferedReader.close();
                            }
                        }
                        throw th9;
                    }
                    if (boundedBufferedReader != null) {
                        if (th7 != null) {
                            boundedBufferedReader.close();
                        } else {
                            boundedBufferedReader.close();
                        }
                    }
                    throw th9;
                }
                if (boundedBufferedReader != null) {
                    if (0 != 0) {
                        try {
                            boundedBufferedReader.close();
                        } catch (Throwable th11) {
                            th7.addSuppressed(th11);
                        }
                    } else {
                        boundedBufferedReader.close();
                    }
                }
                if (inputStream2 != null) {
                    if (0 != 0) {
                        try {
                            inputStream2.close();
                        } catch (Throwable th12) {
                            th6.addSuppressed(th12);
                        }
                    } else {
                        inputStream2.close();
                    }
                }
                return arrayList2;
            } catch (Throwable th13) {
                if (r12 != 0) {
                    if (th4 != null) {
                        try {
                            r12.close();
                        } catch (Throwable th14) {
                            th4.addSuppressed(th14);
                        }
                    } else {
                        r12.close();
                    }
                }
                throw th13;
            }
        } catch (Exception e3) {
            LOGGER.severe("Error al obtener el listado de directorios de usuarios del sistema: " + e3);
            throw new IOException("No se pudo obtener el listado de directorios de usuarios del sistema", e3);
        }
    }

    private static boolean checkSSLKeyStoreGenerated(File file) {
        return new File(file, KS_FILENAME).exists();
    }

    private static boolean checkSSLRootCertificateGenerated(File file) {
        return new File(file, FILE_AUTOFIRMA_CERTIFICATE).exists();
    }

    private static void deleteInstalledCertificates(File file) {
        if (checkSSLKeyStoreGenerated(file) && !new File(file, KS_FILENAME).delete()) {
            LOGGER.warning("No se ha podido eliminar de disco el almacen de claves SSL de Autofirma");
        }
        if (checkSSLRootCertificateGenerated(file) && !new File(file, FILE_AUTOFIRMA_CERTIFICATE).delete()) {
            LOGGER.warning("No se ha podido eliminar el certificado de disco raiz SSL de Autofirma");
        }
    }

    private static boolean closeFirefox(Component component) {
        int i;
        if (isProcessRunningLinux("/usr/lib/firefox/firefox").booleanValue()) {
            JOptionPane.showMessageDialog(component, SimpleAfirmaMessages.getString("RestoreApplication.7"), SimpleAfirmaMessages.getString("RestoreApplication.9"), 2);
        }
        int iShowConfirmDialog = 0;
        while (true) {
            i = iShowConfirmDialog;
            if (i != 0 || !isProcessRunningLinux("/usr/lib/firefox/firefox").booleanValue()) {
                break;
            }
            iShowConfirmDialog = JOptionPane.showConfirmDialog(component, SimpleAfirmaMessages.getString("RestoreApplication.12"), SimpleAfirmaMessages.getString("RestoreApplication.9"), 2, 2);
        }
        return i == 0;
    }

    private static void closeChrome(Component component) {
        if (isProcessRunningLinux("/opt/google/chrome/chrome").booleanValue()) {
            JOptionPane.showMessageDialog(component, SimpleAfirmaMessages.getString("RestoreApplication.8"), SimpleAfirmaMessages.getString("RestoreApplication.9"), 2);
        }
        int iShowConfirmDialog = 0;
        while (iShowConfirmDialog == 0 && isProcessRunningLinux("/opt/google/chrome/chrome").booleanValue()) {
            iShowConfirmDialog = JOptionPane.showConfirmDialog(component, SimpleAfirmaMessages.getString("RestoreApplication.11"), SimpleAfirmaMessages.getString("RestoreApplication.9"), 2);
        }
    }

    /* JADX WARN: Code duplicated, block: B:21:0x0091  */
    /* JADX WARN: Code duplicated, block: B:26:0x00aa A[Catch: IOException -> 0x00b5, TryCatch #1 {IOException -> 0x00b5, blocks: (B:3:0x0007, B:4:0x003b, B:6:0x0045, B:11:0x0065, B:14:0x0079, B:13:0x006f, B:17:0x0089, B:23:0x0096, B:26:0x00aa, B:25:0x00a0, B:28:0x00b1), top: B:39:0x0007, inners: #0, #2, #4 }] */
    /* JADX WARN: Code duplicated, block: B:40:0x0096 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private static Boolean isProcessRunningLinux(String str) {
        String str2 = "";
        Boolean bool = Boolean.FALSE;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(executeProcess("/bin/bash", "-c", "ps -aux").getInputStream()));
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
            LOGGER.severe("Error al detectar si el proceso " + str + " esta activo: " + e.getMessage());
        }
        if (str2.contains(str)) {
            bool = Boolean.TRUE;
        }
        return bool;
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x0205 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void restoreProtocolHandler(java.io.File r6) throws java.io.IOException {
        /*
            Method dump skipped, instruction units count: 748
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.standalone.ui.restoreconfig.RestoreConfigLinux.restoreProtocolHandler(java.io.File):void");
    }

    static File getLinuxAlternativeAppDir() throws IOException {
        try {
            String property = System.getProperty("user.home");
            if (property == null) {
                throw new IOException("No se encuentra definido el directorio del usuario");
            }
            File file = new File(property, ".afirma/Autofirma");
            if (!file.isDirectory() && !file.mkdirs()) {
                throw new IOException("No ha podido crearse el directorio para los ficheros de aplicacion");
            }
            return file;
        } catch (Exception e) {
            throw new IOException("No se ha podido identificar el directorio del usuario para almacenar los ficheros de instalacion", e);
        }
    }

    static Process executeProcess(String... strArr) throws IOException {
        if (LOGGER.isLoggable(Level.INFO)) {
            StringBuilder sb = new StringBuilder();
            for (String str : strArr) {
                sb.append(str).append(" ");
            }
            LOGGER.info("Ejecutamos el comando:\n" + sb.toString());
        }
        return new ProcessBuilder(strArr).start();
    }
}
