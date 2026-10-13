package es.gob.afirma.standalone.configurator;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.BoundedBufferedReader;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.keystores.mozilla.MozillaKeyStoreUtilities;
import es.gob.afirma.standalone.configurator.common.ConfiguratorUtil;
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
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/ConfiguratorFirefoxWindows.class */
final class ConfiguratorFirefoxWindows {
    private static final String FILE_AUTOFIRMA_CERTIFICATE = "Autofirma_ROOT.cer";
    private static final String CERTUTIL_DIR = "certutil";
    private static final String CERTUTIL_EXE = "certutil.exe";
    private static final String CERTUTIL_RESOURCE = "/windows/certutil.windows.zip";
    private static final String CUSTOM_PROFILE_PREFERENCES_FILENAME = "user.js";
    private static final String MOZ_PREFERENCE_FILE_HEADER = "// === PROPIEDADES PERSONALIZADAS DE CONFIGURACION ===\r\n";
    private static final String MOZ_PREFERENCE_ENTERPRISE_ROOTS_HEADER = "\r\n// Confianza en los certificados raices del almacen del sistema\r\n";
    private static final String MOZ_PREFERENCE_ENTERPRISE_ROOTS = "security.enterprise_roots.enabled";
    private static final String BREAK_LINE = "\r\n";
    private static final String ENV_VARIABLE_ALLUSERSPROFILE = "ALLUSERSPROFILE";
    private static final String ENV_VARIABLE_PUBLIC = "PUBLIC";
    private static final String ENV_VARIABLE_SYSTEMDRIVE = "SystemDrive";
    private static final String CERT_ALIAS = "Autofirma ROOT";
    private static String PROFILES_INI_RELATIVE_PATH;
    private static String USERS_PATH;
    private static String DEFAULT_WINDOWS_USER_NAME = "Default";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    static {
        if (isWindowsXP()) {
            PROFILES_INI_RELATIVE_PATH = "Application Data\\Mozilla\\Firefox\\profiles.ini";
            try {
                USERS_PATH = new File(System.getenv(ENV_VARIABLE_ALLUSERSPROFILE)).getParentFile().getAbsolutePath() + File.separator;
                return;
            } catch (Exception e) {
                LOGGER.warning("No se ha podido identificar el directorio base sobre el que se instalan los perfiles de Mozilla. Se buscara en 'C:/Documents and Settings': " + e);
                USERS_PATH = "C:\\Documents and Settings\\";
                return;
            }
        }
        PROFILES_INI_RELATIVE_PATH = "AppData\\Roaming\\Mozilla\\Firefox\\profiles.ini";
        String str = System.getenv(ENV_VARIABLE_PUBLIC);
        if (str != null) {
            USERS_PATH = new File(str).getParentFile().getAbsolutePath() + File.separator;
            return;
        }
        try {
            USERS_PATH = new File(System.getProperty("user.home")).getParentFile().getAbsolutePath() + File.separator;
        } catch (Exception e2) {
            LOGGER.warning("No se ha podido identificar el directorio de usuarios. Se buscara en 'C:/Users': " + e2);
            USERS_PATH = System.getenv(ENV_VARIABLE_SYSTEMDRIVE) + "\\Users\\";
        }
    }

    private ConfiguratorFirefoxWindows() {
    }

    static void installCACertOnMozillaKeyStores(File file, Console console) throws MozillaProfileNotFoundException, IOException {
        File[] allMozillaProfileDirs = getAllMozillaProfileDirs();
        if (allMozillaProfileDirs == null || allMozillaProfileDirs.length == 0) {
            throw new MozillaProfileNotFoundException("No se han encontrado perfiles de Mozilla en el sistema");
        }
        console.print(Messages.getString("ConfiguratorWindows.4"));
        try {
            copyCertUtilToDisk(file);
        } catch (Exception e) {
            LOGGER.warning("No se pudo copiar certutil para la instalacion del certificado SSL raiz del almacen de Mozilla Firefox: " + e);
        }
        try {
            checkCertUtil(file);
            console.print(Messages.getString("ConfiguratorWindows.9"));
            for (File file2 : allMozillaProfileDirs) {
                importCACertOnMozillaKeyStore(file, file2, console);
            }
        } catch (IOException e2) {
            LOGGER.warning("Se omite la configuracion del certificado SSL en Mozilla Firefox: " + e2);
        }
    }

    static void uninstallRootCAMozillaKeyStore(File file, Console console, int i) {
        if (i <= 0) {
            return;
        }
        File[] allMozillaProfileDirs = getAllMozillaProfileDirs();
        if (allMozillaProfileDirs == null || allMozillaProfileDirs.length == 0) {
            LOGGER.info("No se han encontrado perfiles de Mozilla en el sistema de los cuales desinstalar");
            return;
        }
        try {
            copyCertUtilToDisk(file);
        } catch (Exception e) {
            LOGGER.warning("No se pudo copiar certutil para la desinstalacion del certificado SSL raiz del almacen de Mozilla Firefox: " + e);
        }
        try {
            checkCertUtil(file);
        } catch (IOException e2) {
            LOGGER.warning("No se pudo encontrar certutil en disco o verificar su funcionamiento: " + e2);
        }
        for (File file2 : allMozillaProfileDirs) {
            for (int i2 = 0; i2 < i; i2++) {
                try {
                    uninstallCACertFromMozillaKeyStore(file, file2, console);
                } catch (Exception e3) {
                    LOGGER.log(Level.WARNING, "No se pudo desinstalar o no se encontro el certificado SSL raiz del almacen de Mozilla Firefox", (Throwable) e3);
                }
            }
        }
    }

    static void uninstallRootCAMozillaKeyStore(File file, Console console) {
        uninstallRootCAMozillaKeyStore(file, console, 1);
    }

    private static void checkCertUtil(File file) throws IOException {
        File file2 = new File(file, CERTUTIL_DIR + File.separator + CERTUTIL_EXE);
        if (!file2.isFile()) {
            throw new IOException("No se encuentra el ejecutable CertUtil");
        }
        if (!file2.canExecute()) {
            throw new IOException("CertUtil no tiene permisos de ejecucion");
        }
    }

    private static void importCACertOnMozillaKeyStore(File file, File file2, Console console) {
        boolean z = false;
        boolean z2 = false;
        do {
            try {
                executeCertUtilToImport(file, file2, console);
                z = true;
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "No se pudo instalar la CA del certificado SSL para el socket en el almacen de Firefox: " + e, (Throwable) e);
                if (JOptionPane.showConfirmDialog((Component) null, Messages.getString("ConfiguratorWindows.10"), Messages.getString("ConfiguratorWindows.1"), 2, 2) == 2) {
                    z2 = true;
                    LOGGER.warning("El usuario cancelo la instalacion del certificado SSL para el socket en Firefox: " + e);
                }
            }
            if (z) {
                return;
            }
        } while (!z2);
    }

    private static void executeCertUtilToImport(File file, File file2, Console console) throws GeneralSecurityException, IOException {
        File file3 = new File(file, CERTUTIL_DIR + File.separator + CERTUTIL_EXE);
        boolean zExists = new File(file2, "pkcs11.txt").exists();
        String[] strArr = new String[10];
        strArr[0] = escapePath(file3.getAbsolutePath());
        strArr[1] = "-A";
        strArr[2] = "-d";
        strArr[3] = escapePath((zExists ? "sql:" : "") + file2.getAbsolutePath());
        strArr[4] = "-i";
        strArr[5] = escapePath(new File(file, FILE_AUTOFIRMA_CERTIFICATE).getAbsolutePath());
        strArr[6] = "-n";
        strArr[7] = "\"SocketAutoFirma\"";
        strArr[8] = "-t";
        strArr[9] = "\"C,,\"";
        try {
            execCertUtilCommandLine(file, strArr, console);
        } catch (AOCancelledOperationException e) {
            throw new KeyStoreException("Se cancelo la insercion del Error en la instalacion del certificado de CA en el perfil " + file2 + " de Firefox. Es probable que la aplicacion no funcione en este perfil.", e);
        } catch (Exception e2) {
            throw new KeyStoreException("Error en la instalacion del certificado de CA en el perfil " + file2 + " de Firefox. Es posible que la aplicacion funcione en su propio perfil. Si desea que la aplicacion se  ejecute correctamente en todos los perfiles, desinstalela y vuelvala a instalar.", e2);
        }
    }

    private static void uninstallCACertFromMozillaKeyStore(File file, File file2, Console console) throws GeneralSecurityException, IOException {
        File file3 = new File(file, CERTUTIL_DIR + File.separator + CERTUTIL_EXE);
        String str = (new File(file2, "pkcs11.txt").exists() ? "sql:" : "") + file2.getAbsolutePath();
        try {
            deleteCertificate(ConfiguratorUtil.CERT_ALIAS, file3, file, str, console);
        } catch (Exception e) {
            LOGGER.warning("No se encontro o no se pudo borrar el certificado 'SocketAutoFirma' de CA en el perfil de Firefox del usuario '" + file2.getAbsolutePath() + "'. Se buscara con el alias " + CERT_ALIAS);
            try {
                deleteCertificate(CERT_ALIAS, file3, file, str, console);
            } catch (Exception e2) {
                throw new KeyStoreException("No se encontro o no se pudo borrar el certificado de CA en el perfil de usuario de Firefox " + file2.getAbsolutePath(), e2);
            }
        }
    }

    private static void deleteCertificate(String str, File file, File file2, String str2, Console console) throws IOException {
        execCertUtilCommandLine(file2, new String[]{escapePath(file.getAbsolutePath()), "-D", "-d", escapePath(str2), "-n", "\"" + str + "\""}, console);
    }

    /* JADX WARN: Code duplicated, block: B:104:0x0243 A[Catch: Throwable -> 0x0271, all -> 0x027a, TryCatch #14 {, blocks: (B:80:0x01b2, B:81:0x01cd, B:83:0x01d8, B:84:0x01f3, B:89:0x01fe, B:92:0x0212, B:91:0x0208, B:95:0x0222, B:101:0x022f, B:104:0x0243, B:103:0x0239, B:106:0x024a), top: B:153:0x01b2, outer: #16 }] */
    /* JADX WARN: Code duplicated, block: B:157:0x022f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:99:0x022a  */
    /* JADX WARN: Not initialized variable reg: 13, insn: 0x012c: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r13 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:47:0x012c */
    /* JADX WARN: Not initialized variable reg: 14, insn: 0x0131: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r14 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:49:0x0131 */
    private static void execCertUtilCommandLine(File file, String[] strArr, Console console) throws IOException {
        BufferedReader bufferedReader;
        Throwable th;
        LOGGER.info("Se ejecutara el siguiente comando:\n" + printCommand(strArr));
        Process processStart = new ProcessBuilder(strArr).start();
        OutputStream outputStream = processStart.getOutputStream();
        Throwable th2 = null;
        if (outputStream != null) {
            if (0 != 0) {
                try {
                    outputStream.close();
                } catch (Throwable th3) {
                    th2.addSuppressed(th3);
                }
            } else {
                outputStream.close();
            }
        }
        InputStream inputStream = processStart.getInputStream();
        Throwable th4 = null;
        try {
            try {
                try {
                    BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(new InputStreamReader(inputStream), 256, 1024);
                    Throwable th5 = null;
                    String line = boundedBufferedReader.readLine();
                    if (line != null) {
                        if (!line.startsWith("Enter Password")) {
                            throw new IOException("Error durante la ejecucion de certutil (out): " + line);
                        }
                        processStart.destroyForcibly();
                        execCertUtilCommandLineWithPassword(file, strArr, console);
                        if (boundedBufferedReader != null) {
                            if (0 != 0) {
                                try {
                                    boundedBufferedReader.close();
                                } catch (Throwable th6) {
                                    th5.addSuppressed(th6);
                                }
                            } else {
                                boundedBufferedReader.close();
                            }
                        }
                        if (inputStream != null) {
                            if (0 == 0) {
                                inputStream.close();
                                return;
                            }
                            try {
                                inputStream.close();
                                return;
                            } catch (Throwable th7) {
                                th4.addSuppressed(th7);
                                return;
                            }
                        }
                        return;
                    }
                    if (boundedBufferedReader != null) {
                        if (0 != 0) {
                            try {
                                boundedBufferedReader.close();
                            } catch (Throwable th8) {
                                th5.addSuppressed(th8);
                            }
                        } else {
                            boundedBufferedReader.close();
                        }
                    }
                    if (inputStream != null) {
                        if (0 != 0) {
                            try {
                                inputStream.close();
                            } catch (Throwable th9) {
                                th4.addSuppressed(th9);
                            }
                        } else {
                            inputStream.close();
                        }
                    }
                    InputStream errorStream = processStart.getErrorStream();
                    Throwable th10 = null;
                    try {
                        BoundedBufferedReader boundedBufferedReader2 = new BoundedBufferedReader(new InputStreamReader(errorStream), 256, 1024);
                        Throwable th11 = null;
                        try {
                            try {
                                String line2 = boundedBufferedReader2.readLine();
                                if (line2 != null) {
                                    throw new IOException("Error durante la ejecucion de certutil (err): " + line2);
                                }
                                if (boundedBufferedReader2 != null) {
                                    if (0 != 0) {
                                        try {
                                            boundedBufferedReader2.close();
                                        } catch (Throwable th12) {
                                            th11.addSuppressed(th12);
                                        }
                                    } else {
                                        boundedBufferedReader2.close();
                                    }
                                }
                                if (errorStream != null) {
                                    if (0 == 0) {
                                        errorStream.close();
                                        return;
                                    }
                                    try {
                                        errorStream.close();
                                        return;
                                    } catch (Throwable th13) {
                                        th10.addSuppressed(th13);
                                        return;
                                    }
                                }
                                return;
                            } catch (Throwable th14) {
                                th11 = th14;
                                throw th14;
                            }
                        } catch (Throwable th15) {
                            if (boundedBufferedReader2 != null) {
                                if (th11 != null) {
                                    try {
                                        boundedBufferedReader2.close();
                                    } catch (Throwable th16) {
                                        th11.addSuppressed(th16);
                                    }
                                } else {
                                    boundedBufferedReader2.close();
                                }
                            }
                            throw th15;
                        }
                        if (boundedBufferedReader2 != null) {
                            if (th11 != null) {
                                boundedBufferedReader2.close();
                            } else {
                                boundedBufferedReader2.close();
                            }
                        }
                        throw th15;
                    } catch (Throwable th17) {
                        if (errorStream != null) {
                            if (0 != 0) {
                                try {
                                    errorStream.close();
                                } catch (Throwable th18) {
                                    th10.addSuppressed(th18);
                                }
                            } else {
                                errorStream.close();
                            }
                        }
                        throw th17;
                    }
                } catch (Throwable th19) {
                    if (inputStream != null) {
                        if (0 != 0) {
                            try {
                                inputStream.close();
                            } catch (Throwable th20) {
                                th4.addSuppressed(th20);
                            }
                        } else {
                            inputStream.close();
                        }
                    }
                    throw th19;
                }
            } catch (Throwable th21) {
                if (bufferedReader != null) {
                    if (th != null) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable th22) {
                            th.addSuppressed(th22);
                        }
                    } else {
                        bufferedReader.close();
                    }
                }
                throw th21;
            }
        } catch (Throwable th23) {
            throw th23;
        }
    }

    /* JADX WARN: Code duplicated, block: B:131:0x0107 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:133:0x0206 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:35:0x0102  */
    /* JADX WARN: Code duplicated, block: B:40:0x011b A[Catch: Throwable -> 0x0149, all -> 0x0152, TryCatch #4 {, blocks: (B:16:0x0085, B:17:0x00a0, B:19:0x00ab, B:20:0x00cb, B:25:0x00d6, B:28:0x00ea, B:27:0x00e0, B:31:0x00fa, B:37:0x0107, B:40:0x011b, B:39:0x0111, B:42:0x0122), top: B:125:0x0085, outer: #8 }] */
    /* JADX WARN: Code duplicated, block: B:85:0x0201  */
    /* JADX WARN: Code duplicated, block: B:90:0x021a A[Catch: Throwable -> 0x0248, all -> 0x0251, TryCatch #5 {, blocks: (B:66:0x0184, B:67:0x019f, B:69:0x01aa, B:70:0x01ca, B:75:0x01d5, B:78:0x01e9, B:77:0x01df, B:81:0x01f9, B:87:0x0206, B:90:0x021a, B:89:0x0210, B:92:0x0221), top: B:126:0x0184, outer: #6 }] */
    private static void execCertUtilCommandLineWithPassword(File file, String[] strArr, Console console) throws IOException {
        char[] cArrShowPasswordDialog;
        while (true) {
            cArrShowPasswordDialog = showPasswordDialog(console.getParentComponent());
            if (cArrShowPasswordDialog != null && cArrShowPasswordDialog.length != 0) {
                break;
            }
        }
        File fileWritePasswordToTempFile = writePasswordToTempFile(cArrShowPasswordDialog, file);
        ArrayList arrayList = new ArrayList(Arrays.asList(strArr));
        arrayList.add(1, "-f");
        arrayList.add(2, fileWritePasswordToTempFile.getAbsolutePath());
        Process processStart = new ProcessBuilder(arrayList).start();
        OutputStream outputStream = processStart.getOutputStream();
        Throwable th = null;
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
        InputStream inputStream = processStart.getInputStream();
        Throwable th3 = null;
        try {
            BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(new InputStreamReader(inputStream), 256, 1024);
            Throwable th4 = null;
            try {
                try {
                    String line = boundedBufferedReader.readLine();
                    if (line != null) {
                        deleteFileSilently(fileWritePasswordToTempFile);
                        throw new IOException("Error durante la ejecucion de certutil (out): " + line);
                    }
                    if (boundedBufferedReader != null) {
                        if (0 != 0) {
                            try {
                                boundedBufferedReader.close();
                            } catch (Throwable th5) {
                                th4.addSuppressed(th5);
                            }
                        } else {
                            boundedBufferedReader.close();
                        }
                    }
                    if (inputStream != null) {
                        if (0 != 0) {
                            try {
                                inputStream.close();
                            } catch (Throwable th6) {
                                th3.addSuppressed(th6);
                            }
                        } else {
                            inputStream.close();
                        }
                    }
                    InputStream errorStream = processStart.getErrorStream();
                    Throwable th7 = null;
                    try {
                        BoundedBufferedReader boundedBufferedReader2 = new BoundedBufferedReader(new InputStreamReader(errorStream), 256, 1024);
                        Throwable th8 = null;
                        try {
                            try {
                                String line2 = boundedBufferedReader2.readLine();
                                if (line2 != null) {
                                    deleteFileSilently(fileWritePasswordToTempFile);
                                    throw new IOException("Error durante la ejecucion de certutil (err): " + line2);
                                }
                                if (boundedBufferedReader2 != null) {
                                    if (0 != 0) {
                                        try {
                                            boundedBufferedReader2.close();
                                        } catch (Throwable th9) {
                                            th8.addSuppressed(th9);
                                        }
                                    } else {
                                        boundedBufferedReader2.close();
                                    }
                                }
                                if (errorStream != null) {
                                    if (0 != 0) {
                                        try {
                                            errorStream.close();
                                        } catch (Throwable th10) {
                                            th7.addSuppressed(th10);
                                        }
                                    } else {
                                        errorStream.close();
                                    }
                                }
                                deleteFileSilently(fileWritePasswordToTempFile);
                                return;
                            } catch (Throwable th11) {
                                th8 = th11;
                                throw th11;
                            }
                        } catch (Throwable th12) {
                            if (boundedBufferedReader2 != null) {
                                if (th8 != null) {
                                    try {
                                        boundedBufferedReader2.close();
                                    } catch (Throwable th13) {
                                        th8.addSuppressed(th13);
                                    }
                                } else {
                                    boundedBufferedReader2.close();
                                }
                            }
                            throw th12;
                        }
                        if (boundedBufferedReader2 != null) {
                            if (th8 != null) {
                                boundedBufferedReader2.close();
                            } else {
                                boundedBufferedReader2.close();
                            }
                        }
                        throw th12;
                    } catch (Throwable th14) {
                        if (errorStream != null) {
                            if (0 != 0) {
                                try {
                                    errorStream.close();
                                } catch (Throwable th15) {
                                    th7.addSuppressed(th15);
                                }
                            } else {
                                errorStream.close();
                            }
                        }
                        throw th14;
                    }
                } catch (Throwable th16) {
                    th4 = th16;
                    throw th16;
                }
            } catch (Throwable th17) {
                if (boundedBufferedReader != null) {
                    if (th4 != null) {
                        try {
                            boundedBufferedReader.close();
                        } catch (Throwable th18) {
                            th4.addSuppressed(th18);
                        }
                    } else {
                        boundedBufferedReader.close();
                    }
                }
                throw th17;
            }
            if (boundedBufferedReader != null) {
                if (th4 != null) {
                    boundedBufferedReader.close();
                } else {
                    boundedBufferedReader.close();
                }
            }
            throw th17;
        } catch (Throwable th19) {
            if (inputStream != null) {
                if (0 != 0) {
                    try {
                        inputStream.close();
                    } catch (Throwable th20) {
                        th3.addSuppressed(th20);
                    }
                } else {
                    inputStream.close();
                }
            }
            throw th19;
        }
    }

    private static char[] showPasswordDialog(Component component) throws AOCancelledOperationException {
        return AOUIFactory.getPassword(Messages.getString("ConfiguratorWindows.18"), null, null, false, component);
    }

    private static File writePasswordToTempFile(char[] cArr, File file) throws IOException {
        File file2;
        int i = 0;
        while (true) {
            file2 = new File(file, "p" + i);
            if (!file2.isFile()) {
                try {
                    break;
                } catch (Exception e) {
                    if (file2.exists()) {
                        deleteFileSilently(file2);
                    }
                    throw new IOException("No se pudo preparar la contrasena maestra para pasarsela a CertUtil", e);
                }
            }
            i++;
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file2);
        Throwable th = null;
        try {
            try {
                fileOutputStream.write(new String(cArr).getBytes(StandardCharsets.UTF_8));
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

    private static void deleteFileSilently(File file) {
        try {
            Files.delete(file.toPath());
        } catch (Exception e) {
        }
    }

    private static void copyCertUtilToDisk(File file) throws IOException {
        if (!new File(file, CERTUTIL_DIR).exists()) {
            ConfiguratorUtil.uncompressResource(CERTUTIL_RESOURCE, file);
        }
    }

    private static File[] getAllMozillaProfileDirs() {
        ArrayList arrayList = new ArrayList();
        for (File file : getUserDirs()) {
            if (new File(file, PROFILES_INI_RELATIVE_PATH).exists()) {
                LOGGER.info("Se usa el perfil de Firefox: " + new File(file, PROFILES_INI_RELATIVE_PATH).getAbsolutePath());
                try {
                    String mozillaUserProfileDirectoryWindows = MozillaKeyStoreUtilities.getMozillaUserProfileDirectoryWindows(new File(file, PROFILES_INI_RELATIVE_PATH).getAbsolutePath());
                    if (mozillaUserProfileDirectoryWindows != null) {
                        arrayList.add(new File(mozillaUserProfileDirectoryWindows));
                    }
                } catch (Exception e) {
                    LOGGER.warning("No se pudieron recuperar los directorios de perfil de Mozilla para el usuario " + file.getName() + ": " + e);
                }
            }
        }
        return (File[]) arrayList.toArray(new File[arrayList.size()]);
    }

    private static File[] getUserDirs() {
        ArrayList arrayList = new ArrayList();
        for (File file : new File(USERS_PATH).listFiles()) {
            if (file.isDirectory() && !DEFAULT_WINDOWS_USER_NAME.equalsIgnoreCase(file.getName())) {
                arrayList.add(file);
            }
        }
        return (File[]) arrayList.toArray(new File[arrayList.size()]);
    }

    private static boolean isWindowsXP() {
        return System.getProperty("os.name") != null && System.getProperty("os.name").contains("XP");
    }

    private static String escapePath(String str) {
        if (str == null) {
            throw new IllegalArgumentException("La ruta a 'escapar' no puede ser nula");
        }
        if (str.contains(" ")) {
            return "\"" + str + "\"";
        }
        return str;
    }

    private static String printCommand(String[] strArr) {
        StringBuilder sb = new StringBuilder();
        for (String str : strArr) {
            sb.append(str);
            sb.append(' ');
        }
        return sb.toString();
    }

    static void configureUseSystemTrustStore(boolean z, Console console) throws MozillaProfileNotFoundException, IOException {
        File[] allMozillaProfileDirs = getAllMozillaProfileDirs();
        if (allMozillaProfileDirs == null || allMozillaProfileDirs.length == 0) {
            throw new MozillaProfileNotFoundException("No se han encontrado perfiles de Mozilla en el sistema");
        }
        if (z) {
            console.print(Messages.getString("ConfiguratorWindows.19"));
        } else {
            console.print(Messages.getString("ConfiguratorWindows.20"));
        }
        for (File file : allMozillaProfileDirs) {
            File file2 = new File(file, CUSTOM_PROFILE_PREFERENCES_FILENAME);
            if (file2.isFile()) {
                boolean z2 = false;
                StringBuilder sb = new StringBuilder();
                FileInputStream fileInputStream = new FileInputStream(file2);
                Throwable th = null;
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                    Throwable th2 = null;
                    try {
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        Throwable th3 = null;
                        while (true) {
                            try {
                                try {
                                    String line = bufferedReader.readLine();
                                    if (line == null) {
                                        break;
                                    }
                                    if (line.contains(MOZ_PREFERENCE_ENTERPRISE_ROOTS)) {
                                        z2 = true;
                                        sb.append(getUseSystemTrustStoreConfigContent(z));
                                    } else {
                                        sb.append(line).append(BREAK_LINE);
                                    }
                                } catch (Throwable th4) {
                                    th3 = th4;
                                    throw th4;
                                }
                            } catch (Throwable th5) {
                                if (bufferedReader != null) {
                                    if (th3 != null) {
                                        try {
                                            bufferedReader.close();
                                        } catch (Throwable th6) {
                                            th3.addSuppressed(th6);
                                        }
                                    } else {
                                        bufferedReader.close();
                                    }
                                }
                                throw th5;
                            }
                        }
                        if (bufferedReader != null) {
                            if (0 != 0) {
                                try {
                                    bufferedReader.close();
                                } catch (Throwable th7) {
                                    th3.addSuppressed(th7);
                                }
                            } else {
                                bufferedReader.close();
                            }
                        }
                        if (inputStreamReader != null) {
                            if (0 != 0) {
                                try {
                                    inputStreamReader.close();
                                } catch (Throwable th8) {
                                    th2.addSuppressed(th8);
                                }
                            } else {
                                inputStreamReader.close();
                            }
                        }
                        if (fileInputStream != null) {
                            if (0 != 0) {
                                try {
                                    fileInputStream.close();
                                } catch (Throwable th9) {
                                    th.addSuppressed(th9);
                                }
                            } else {
                                fileInputStream.close();
                            }
                        }
                        if (!z2) {
                            sb.append(MOZ_PREFERENCE_ENTERPRISE_ROOTS_HEADER).append(getUseSystemTrustStoreConfigContent(z));
                        }
                        FileOutputStream fileOutputStream = new FileOutputStream(file2);
                        Throwable th10 = null;
                        try {
                            try {
                                fileOutputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));
                                if (fileOutputStream != null) {
                                    if (0 != 0) {
                                        try {
                                            fileOutputStream.close();
                                        } catch (Throwable th11) {
                                            th10.addSuppressed(th11);
                                        }
                                    } else {
                                        fileOutputStream.close();
                                    }
                                }
                            } catch (Throwable th12) {
                                th10 = th12;
                                throw th12;
                            }
                        } catch (Throwable th13) {
                            if (fileOutputStream != null) {
                                if (th10 != null) {
                                    try {
                                        fileOutputStream.close();
                                    } catch (Throwable th14) {
                                        th10.addSuppressed(th14);
                                    }
                                } else {
                                    fileOutputStream.close();
                                }
                            }
                            throw th13;
                        }
                    } catch (Throwable th15) {
                        if (inputStreamReader != null) {
                            if (0 != 0) {
                                try {
                                    inputStreamReader.close();
                                } catch (Throwable th16) {
                                    th2.addSuppressed(th16);
                                }
                            } else {
                                inputStreamReader.close();
                            }
                        }
                        throw th15;
                    }
                } catch (Throwable th17) {
                    if (fileInputStream != null) {
                        if (0 != 0) {
                            try {
                                fileInputStream.close();
                            } catch (Throwable th18) {
                                th.addSuppressed(th18);
                            }
                        } else {
                            fileInputStream.close();
                        }
                    }
                    throw th17;
                }
            } else {
                FileOutputStream fileOutputStream2 = new FileOutputStream(file2);
                Throwable th19 = null;
                try {
                    try {
                        fileOutputStream2.write(("// === PROPIEDADES PERSONALIZADAS DE CONFIGURACION ===\r\n\r\n// Confianza en los certificados raices del almacen del sistema\r\n" + getUseSystemTrustStoreConfigContent(z)).getBytes(StandardCharsets.UTF_8));
                        if (fileOutputStream2 != null) {
                            if (0 != 0) {
                                try {
                                    fileOutputStream2.close();
                                } catch (Throwable th20) {
                                    th19.addSuppressed(th20);
                                }
                            } else {
                                fileOutputStream2.close();
                            }
                        }
                    } catch (Throwable th21) {
                        th19 = th21;
                        throw th21;
                    }
                } catch (Throwable th22) {
                    if (fileOutputStream2 != null) {
                        if (th19 != null) {
                            try {
                                fileOutputStream2.close();
                            } catch (Throwable th23) {
                                th19.addSuppressed(th23);
                            }
                        } else {
                            fileOutputStream2.close();
                        }
                    }
                    throw th22;
                }
            }
        }
    }

    private static String getUseSystemTrustStoreConfigContent(boolean z) {
        return "user_pref(\"security.enterprise_roots.enabled\", " + z + ");" + BREAK_LINE;
    }
}
