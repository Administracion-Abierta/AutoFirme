package es.gob.afirma.standalone.ui.restoreconfig;

import es.gob.afirma.core.misc.BoundedBufferedReader;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.keystores.mozilla.MozillaKeyStoreUtilities;
import es.gob.afirma.standalone.so.macos.UnixUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.Timer;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/restoreconfig/RestoreConfigFirefox.class */
final class RestoreConfigFirefox {
    private static final int TIMEOUT = 5000;
    private static final int WITHOUT_TIMEOUT = 0;
    private static final String SSL_ROOT_CERTIFICATE_FILENAME = "Autofirma_ROOT.cer";
    static final String DIR_CERTUTIL = "certutil";
    private static final String LINUX_UNINSTALLSCRIPT_NAME = "uninstallRestore-";
    private static final String LINUX_SCRIPT_NAME = "installRestore-";
    private static final String LINUX_MOZILLA_PATH = "/.mozilla/firefox/profiles.ini";
    private static final String UBUNTU_22_MOZILLA_PATH = "/snap/firefox/common/.mozilla/firefox/profiles.ini";
    private static final String LINUX_CHROMIUM_PREFS_PATH = "/.config/chromium/Local State";
    private static final String LINUX_CHROME_PREFS_PATH = "/.config/google-chrome/Local State";
    private static String WINDOWS_MOZILLA_PATH;
    private static String USERS_WINDOWS_PATH;
    static final String CERTUTIL_EXE;
    private static final String FILE_CERTUTIL;
    private static final String RESOURCE_BASE;
    private static String DEFAULT_WINDOWS_USER_NAME;
    private static final String CUSTOM_PROFILE_PREFERENCES_FILENAME = "user.js";
    private static final String MOZ_PREFERENCE_FILE_HEADER = "// === PROPIEDADES PERSONALIZADAS DE CONFIGURACION ===\r\n";
    private static final String MOZ_PREFERENCE_ENTERPRISE_ROOTS_HEADER = "\r\n// Confianza en los certificados raices del almacen del sistema\r\n";
    private static final String MOZ_PREFERENCE_ENTERPRISE_ROOTS = "security.enterprise_roots.enabled";
    private static final String BREAK_LINE = "\r\n";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String NSS_LINUX_CHROME_PATH = "/.pki/nssdb";
    private static final String NSS_LINUX_CHROMIUM_PATH = "/snap/chromium/current/.pki/nssdb";
    private static final String[] NSS_DIR_SUBPATH = {NSS_LINUX_CHROME_PATH, NSS_LINUX_CHROMIUM_PATH};

    static {
        String property;
        if (Platform.getOS() == Platform.OS.WINDOWS) {
            try {
                property = System.getProperty("os.name");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "No se pudo obtener el nombre del sistema operativo", (Throwable) e);
                property = "";
            }
            if (property.contains("XP")) {
                WINDOWS_MOZILLA_PATH = "\\Application Data\\Mozilla\\Firefox\\profiles.ini";
                USERS_WINDOWS_PATH = "C:\\Documents and Settings\\";
            } else {
                WINDOWS_MOZILLA_PATH = "\\AppData\\Roaming\\Mozilla\\Firefox\\profiles.ini";
                try {
                    USERS_WINDOWS_PATH = new File(System.getProperty("user.home")).getParentFile().getAbsolutePath() + File.separator;
                } catch (Exception e2) {
                    LOGGER.warning("No se ha podido identificar el directorio de usuarios: " + e2);
                    USERS_WINDOWS_PATH = "C:/Users/";
                }
            }
        }
        DEFAULT_WINDOWS_USER_NAME = "Default";
        switch (Platform.getOS()) {
            case WINDOWS:
                CERTUTIL_EXE = "certutil.exe";
                FILE_CERTUTIL = "certutil.windows.zip";
                RESOURCE_BASE = "/windows/";
                return;
            case LINUX:
                CERTUTIL_EXE = DIR_CERTUTIL;
                FILE_CERTUTIL = "certutil.linux.zip";
                RESOURCE_BASE = "/linux/";
                return;
            default:
                throw new IllegalStateException("Sistema operativo no soportado: " + Platform.getOS());
        }
    }

    private RestoreConfigFirefox() {
    }

    /* JADX WARN: Not initialized variable reg: 20, insn: 0x031c: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r20 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:55:0x031c */
    /* JADX WARN: Not initialized variable reg: 21, insn: 0x0321: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r21 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:57:0x0321 */
    private static void createScriptsRemoveExecutionWarningInChrome(File file, String str, String str2) {
        FileOutputStream fileOutputStream;
        Throwable th;
        String[] strArr = {"sed", "s/\\\"protocol_handler\\\":{\\\"excluded_schemes\\\":{/\\\"protocol_handler\\\":{\\\"excluded_schemes\\\":{\\\"afirma\\\":false,/g", escapePath(str + str2), ">", escapePath(str + str2) + "1"};
        String[] strArr2 = {"sed", "s/\\\"afirma\\\":false,//g", escapePath(str + str2), ">", escapePath(str + str2) + "1"};
        String[] strArr3 = {"\\cp", escapePath(str + str2) + "1", escapePath(str + str2)};
        try {
            StringBuilder sb = new StringBuilder();
            for (String str3 : strArr) {
                sb.append(str3);
                sb.append(' ');
            }
            StringBuilder sb2 = new StringBuilder();
            for (String str4 : strArr2) {
                sb2.append(str4);
                sb2.append(' ');
            }
            sb2.append("\n");
            sb.append("\n");
            for (String str5 : strArr3) {
                sb.append(str5);
                sb.append(' ');
            }
            for (String str6 : strArr3) {
                sb2.append(str6);
                sb2.append(' ');
            }
            try {
                sb.append("\n");
                sb2.append("\n");
                Random random = new Random();
                String absolutePath = new File(file, LINUX_SCRIPT_NAME + random.nextInt() + ".sh").getAbsolutePath();
                String absolutePath2 = new File(file, LINUX_UNINSTALLSCRIPT_NAME + random.nextInt() + ".sh").getAbsolutePath();
                File file2 = new File(absolutePath);
                File file3 = new File(absolutePath2);
                FileOutputStream fileOutputStream2 = new FileOutputStream(file2, true);
                Throwable th2 = null;
                FileOutputStream fileOutputStream3 = new FileOutputStream(file3, true);
                Throwable th3 = null;
                try {
                    try {
                        fileOutputStream2.write(sb.toString().getBytes());
                        fileOutputStream3.write(sb2.toString().getBytes());
                        if (fileOutputStream3 != null) {
                            if (0 != 0) {
                                try {
                                    fileOutputStream3.close();
                                } catch (Throwable th4) {
                                    th3.addSuppressed(th4);
                                }
                            } else {
                                fileOutputStream3.close();
                            }
                        }
                        if (fileOutputStream2 != null) {
                            if (0 != 0) {
                                try {
                                    fileOutputStream2.close();
                                } catch (Throwable th5) {
                                    th2.addSuppressed(th5);
                                }
                            } else {
                                fileOutputStream2.close();
                            }
                        }
                    } catch (Throwable th6) {
                        th3 = th6;
                        throw th6;
                    }
                } catch (Throwable th7) {
                    if (fileOutputStream3 != null) {
                        if (th3 != null) {
                            try {
                                fileOutputStream3.close();
                            } catch (Throwable th8) {
                                th3.addSuppressed(th8);
                            }
                        } else {
                            fileOutputStream3.close();
                        }
                    }
                    throw th7;
                }
            } catch (Throwable th9) {
                if (fileOutputStream != null) {
                    if (th != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th10) {
                            th.addSuppressed(th10);
                        }
                    } else {
                        fileOutputStream.close();
                    }
                }
                throw th9;
            }
        } catch (Exception e) {
            LOGGER.severe("Excepcion en la creacion del script linux para la modificacion del fichero de protocolos de Google Chrome: " + e);
        }
    }

    static void removeAppExecutionWarningInChrome(File file, List<String> list) {
        for (String str : list) {
            if (Platform.OS.LINUX.equals(Platform.getOS())) {
                File file2 = new File(escapePath(str) + LINUX_CHROME_PREFS_PATH);
                File file3 = new File(escapePath(str) + LINUX_CHROMIUM_PREFS_PATH);
                if (file2.isFile()) {
                    createScriptsRemoveExecutionWarningInChrome(file, str, LINUX_CHROME_PREFS_PATH);
                }
                if (file3.isFile()) {
                    createScriptsRemoveExecutionWarningInChrome(file, str, LINUX_CHROMIUM_PREFS_PATH);
                }
            }
        }
    }

    static void installRootCAChromeKeyStore(File file, File file2, List<String> list) throws IOException {
        if (!Platform.OS.LINUX.equals(Platform.getOS())) {
            return;
        }
        String certUtilPath = getCertUtilPath(file);
        for (String str : list) {
            for (String str2 : NSS_DIR_SUBPATH) {
                if (new File(escapePath(str) + str2).isDirectory()) {
                    execCommandLineCertUtil(file, new String[]{certUtilPath, "-d", "sql:" + escapePath(str) + str2, "-A", "-n", "\"SocketAutoFirma\"", "-i", escapePath(file2.getAbsolutePath()), "-t", "\"TCP,TCP,TCP\""}, true);
                }
            }
        }
    }

    static void installRootCAMozillaKeyStore(File file) throws MozillaProfileNotFoundException, IOException, KeyStoreException {
        installRootCAMozillaKeyStore(file, null);
    }

    static void installRootCAMozillaKeyStore(File file, File file2) throws MozillaProfileNotFoundException, IOException, KeyStoreException {
        ArrayList<File> firefoxProfilesDir = getFirefoxProfilesDir();
        if (firefoxProfilesDir == null || firefoxProfilesDir.isEmpty()) {
            throw new MozillaProfileNotFoundException();
        }
        Iterator<File> it = firefoxProfilesDir.iterator();
        while (it.hasNext()) {
            try {
                importCARootOnFirefoxKeyStore(file, file2, new HashSet(Arrays.asList(it.next().listFiles())));
            } catch (Exception e) {
                LOGGER.warning("No se pudo instalar el certificado de CA en todos los perfiles de Firefox: " + e);
            }
        }
    }

    static void installRootCAMozillaKeyStore(File file, File file2, List<String> list) throws MozillaProfileNotFoundException, KeyStoreException {
        Set<File> profiles = getProfiles(getMozillaUsersProfilesPath(list));
        if (profiles.isEmpty()) {
            throw new MozillaProfileNotFoundException();
        }
        importCARootOnFirefoxKeyStore(file, file2, profiles);
    }

    public static void uninstallRootCAMozillaKeyStore(File file) {
        try {
            executeCertUtilToDelete(file);
        } catch (Exception e) {
            LOGGER.warning("No se pudo desinstalar el certificado SSL raiz del almacen de Mozilla Firefox: " + e);
        }
    }

    static void removeConfigurationFiles(File file) {
        if (!file.exists()) {
            return;
        }
        deleteConfigDir(file);
    }

    private static String escapePath(String str) {
        if (str == null) {
            throw new IllegalArgumentException("La ruta a 'escapar' no puede ser nula");
        }
        if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
            if (str.contains(" ")) {
                return OperatorName.SHOW_TEXT_LINE_AND_SPACE + str + OperatorName.SHOW_TEXT_LINE_AND_SPACE;
            }
            return str;
        }
        return str.replace(" ", "\\ ");
    }

    public static String getCertUtilPath(File file) throws IOException {
        String absolutePath;
        if (Platform.OS.LINUX.equals(Platform.getOS())) {
            absolutePath = CERTUTIL_EXE;
        } else {
            File file2 = new File(file.getAbsolutePath() + File.separator + DIR_CERTUTIL + File.separator + CERTUTIL_EXE);
            if (!file2.exists() || !file2.isFile()) {
                throw new IOException("No se encuentra el ejecutable CertUtil para la instalacion en Firefox");
            }
            if (!file2.canExecute()) {
                throw new IOException("No hay permisos de ejecucion para Mozilla CertUtil");
            }
            absolutePath = file2.getAbsolutePath();
        }
        return absolutePath;
    }

    private static void executeCertUtilToImport(File file, File file2, Set<File> set) throws IOException, KeyStoreException {
        String strEscapePath = escapePath(getCertUtilPath(file));
        boolean zExecCommandLineCertUtil = false;
        File file3 = file2;
        if (file3 == null) {
            file3 = new File(file, SSL_ROOT_CERTIFICATE_FILENAME);
        }
        for (File file4 : set) {
            if (file4.isDirectory()) {
                zExecCommandLineCertUtil = execCommandLineCertUtil(file, new String[]{strEscapePath, "-A", "-d", "sql:" + escapePath(file4.getAbsolutePath()), "-i", escapePath(file3.getAbsolutePath()), "-n", "\"SocketAutoFirma\"", "-t", "\"C,,\""}, false);
            }
        }
        if (zExecCommandLineCertUtil) {
            throw new KeyStoreException("Error en la instalacion del certificado de CA en alguno de los perfiles de usuario de Firefox. Es posible que la aplicacion funcione en su propio perfil.");
        }
    }

    /* JADX WARN: Not initialized variable reg: 17, insn: 0x025a: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r17 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:49:0x025a */
    /* JADX WARN: Not initialized variable reg: 18, insn: 0x025f: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r18 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:51:0x025f */
    private static boolean execCommandLineCertUtil(File file, String[] strArr, boolean z) throws IOException {
        FileOutputStream fileOutputStream;
        Throwable th;
        StringBuilder sb = new StringBuilder();
        for (String str : strArr) {
            sb.append(str);
            sb.append(' ');
        }
        if (!Platform.OS.LINUX.equals(Platform.getOS())) {
            LOGGER.info("Se ejecutara el siguiente comando:\n" + sb.toString());
            try {
                return execCommand(strArr, 5000);
            } catch (Exception e) {
                LOGGER.severe("Excepcion en la ejecucion del script para la instalacion del certificado en el almacen de confianza: " + e);
                return true;
            }
        }
        StringBuilder sb2 = new StringBuilder();
        if (z) {
            sb2.append(strArr[0] + ' ');
            sb2.append("-D ");
            sb2.append("-d ");
            sb2.append(strArr[2] + ' ');
            sb2.append("-n ");
            sb2.append(strArr[5] + ' ');
        } else {
            sb2.append(strArr[0] + ' ');
            sb2.append("-D ");
            sb2.append("-d ");
            sb2.append(strArr[3] + ' ');
            sb2.append("-n ");
            sb2.append(strArr[7] + ' ');
        }
        Random random = new Random();
        String absolutePath = new File(file, LINUX_SCRIPT_NAME + random.nextInt() + ".sh").getAbsolutePath();
        String absolutePath2 = new File(file, LINUX_UNINSTALLSCRIPT_NAME + random.nextInt() + ".sh").getAbsolutePath();
        File file2 = new File(absolutePath);
        File file3 = new File(absolutePath2);
        try {
            try {
                FileOutputStream fileOutputStream2 = new FileOutputStream(file2, true);
                Throwable th2 = null;
                FileOutputStream fileOutputStream3 = new FileOutputStream(file3, true);
                Throwable th3 = null;
                try {
                    fileOutputStream2.write(sb.toString().getBytes());
                    fileOutputStream3.write(sb2.toString().getBytes());
                    if (fileOutputStream3 != null) {
                        if (0 != 0) {
                            try {
                                fileOutputStream3.close();
                            } catch (Throwable th4) {
                                th3.addSuppressed(th4);
                            }
                        } else {
                            fileOutputStream3.close();
                        }
                    }
                    if (fileOutputStream2 != null) {
                        if (0 != 0) {
                            try {
                                fileOutputStream2.close();
                            } catch (Throwable th5) {
                                th2.addSuppressed(th5);
                            }
                        } else {
                            fileOutputStream2.close();
                        }
                    }
                    UnixUtils.addAllPermissionsToFile(file3);
                    UnixUtils.addAllPermissionsToFile(file2);
                    try {
                        execCommand(new String[]{absolutePath2}, 0);
                        try {
                            boolean zExecCommand = execCommand(new String[]{absolutePath}, 0);
                            if (!file3.delete()) {
                                LOGGER.warning("No puedo eliminar el fichero de script: " + file3.getName());
                            }
                            if (!file2.delete()) {
                                LOGGER.warning("No puedo eliminar el fichero de script: " + file2.getName());
                            }
                            return zExecCommand;
                        } catch (Exception e2) {
                            LOGGER.severe("Excepcion en la ejecucion del script para la instalacion del certificado en el almacen de confianza: " + e2);
                            return true;
                        }
                    } catch (Exception e3) {
                        LOGGER.warning("Error en la ejecucion del script para la desinstalacion del certificado del almacen de confianza: " + e3);
                        return true;
                    }
                } catch (Throwable th6) {
                    if (fileOutputStream3 != null) {
                        if (0 != 0) {
                            try {
                                fileOutputStream3.close();
                            } catch (Throwable th7) {
                                th3.addSuppressed(th7);
                            }
                        } else {
                            fileOutputStream3.close();
                        }
                    }
                    throw th6;
                }
            } catch (Throwable th8) {
                if (fileOutputStream != null) {
                    if (th != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th9) {
                            th.addSuppressed(th9);
                        }
                    } else {
                        fileOutputStream.close();
                    }
                }
                throw th8;
            }
        } catch (Exception e4) {
            LOGGER.severe("No se han podido generar los script para la instalacion de los certificados: " + e4);
            return true;
        }
    }

    private static void importCARootOnFirefoxKeyStore(File file, File file2, Set<File> set) throws KeyStoreException {
        try {
            executeCertUtilToImport(file, file2, set);
        } catch (Exception e) {
            LOGGER.warning("No se pudo instalar la CA del certificado SSL para el socket en todos los perfiles de Firefox. Probablemente alguno de los perfiles nunca haya sido iniciado o no se este ejecutando Autofirma como administrador: " + e);
            throw new KeyStoreException("Error al instalar la CA de confianza en el almacen de Firefox", e);
        }
    }

    private static void executeCertUtilToDelete(File file) throws GeneralSecurityException, IOException {
        boolean zExecCommand;
        String certUtilPath = getCertUtilPath(file);
        boolean z = false;
        for (String str : new File(USERS_WINDOWS_PATH).list((file2, str2) -> {
            return new File(file2, str2).isDirectory();
        })) {
            if (!DEFAULT_WINDOWS_USER_NAME.equalsIgnoreCase(str)) {
                String str3 = USERS_WINDOWS_PATH + str + WINDOWS_MOZILLA_PATH;
                if (new File(str3).exists()) {
                    LOGGER.info("Se ha encontrado el perfil de Firefox: " + str3);
                    MozillaKeyStoreUtilities.getMozillaUserProfileDirectoryWindows(str3);
                    String mozillaUserProfileDirectoryWindows = MozillaKeyStoreUtilities.getMozillaUserProfileDirectoryWindows(str3);
                    if (mozillaUserProfileDirectoryWindows == null) {
                        LOGGER.warning("No se ha encontrado un directorio de perfil de un usuario");
                        z = true;
                    } else {
                        for (File file3 : new File(mozillaUserProfileDirectoryWindows).getParentFile().listFiles()) {
                            if (file3.isDirectory()) {
                                try {
                                    zExecCommand = execCommand(new String[]{OperatorName.SHOW_TEXT_LINE_AND_SPACE + certUtilPath + OperatorName.SHOW_TEXT_LINE_AND_SPACE, "-D", "-d", "\"sql:" + file3.getAbsolutePath() + OperatorName.SHOW_TEXT_LINE_AND_SPACE, "-n", "\"SocketAutoFirma\""}, 5000);
                                } catch (Exception e) {
                                    LOGGER.severe("Excepcion en la ejecucion del script para el borrado del certificado en el almacen de confianza de alguno de los perfiles de Firefox: " + e);
                                    zExecCommand = true;
                                }
                                if (zExecCommand) {
                                    z = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (z) {
            throw new KeyStoreException("Error en el borrado del certificado de CA en alguno de los perfiles de usuario de Firefox");
        }
    }

    private static void deleteConfigDir(File file) {
        RestoreConfigUtil.deleteDir(new File(file, DIR_CERTUTIL));
    }

    static void copyConfigurationFiles(File file) throws IOException {
        if (!new File(file, DIR_CERTUTIL).exists()) {
            uncompressResource(RESOURCE_BASE + FILE_CERTUTIL, file);
        }
    }

    private static void uncompressResource(String str, File file) throws IOException {
        byte[] bArr = new byte[1024];
        ZipInputStream zipInputStream = new ZipInputStream(RestoreConfigFirefox.class.getResourceAsStream(str));
        Throwable th = null;
        try {
            if (Platform.OS.LINUX != Platform.getOS()) {
                while (true) {
                    ZipEntry nextEntry = zipInputStream.getNextEntry();
                    if (nextEntry == null) {
                        break;
                    }
                    File file2 = new File(file, nextEntry.getName());
                    if (!isParent(file, file2)) {
                        zipInputStream.closeEntry();
                        throw new IOException("Se ha encontrado en el archivo comprimido una ruta que apuntaba fuera del directorio de destino");
                    }
                    if (nextEntry.isDirectory()) {
                        file2.mkdirs();
                    } else {
                        if (!file2.getParentFile().exists()) {
                            file2.getParentFile().mkdirs();
                        }
                        FileOutputStream fileOutputStream = new FileOutputStream(file2);
                        Throwable th2 = null;
                        while (true) {
                            try {
                                try {
                                    int i = zipInputStream.read(bArr);
                                    if (i <= 0) {
                                        break;
                                    } else {
                                        fileOutputStream.write(bArr, 0, i);
                                    }
                                } catch (Throwable th3) {
                                    th2 = th3;
                                    throw th3;
                                }
                            } catch (Throwable th4) {
                                if (fileOutputStream != null) {
                                    if (th2 != null) {
                                        try {
                                            fileOutputStream.close();
                                        } catch (Throwable th5) {
                                            th2.addSuppressed(th5);
                                        }
                                    } else {
                                        fileOutputStream.close();
                                    }
                                }
                                throw th4;
                            }
                        }
                        fileOutputStream.flush();
                        if (fileOutputStream != null) {
                            if (0 != 0) {
                                try {
                                    fileOutputStream.close();
                                } catch (Throwable th6) {
                                    th2.addSuppressed(th6);
                                }
                            } else {
                                fileOutputStream.close();
                            }
                        }
                    }
                    zipInputStream.closeEntry();
                }
            } else {
                new File(file, DIR_CERTUTIL).mkdirs();
                while (true) {
                    ZipEntry nextEntry2 = zipInputStream.getNextEntry();
                    if (nextEntry2 == null) {
                        break;
                    }
                    File canonicalFile = new File(file, nextEntry2.getName()).getCanonicalFile();
                    if (!isParent(file, canonicalFile)) {
                        zipInputStream.closeEntry();
                        throw new IOException("Se ha encontrado en el archivo comprimido una ruta que apuntaba fuera del directorio de destino");
                    }
                    FileOutputStream fileOutputStream2 = new FileOutputStream(canonicalFile);
                    Throwable th7 = null;
                    while (true) {
                        try {
                            try {
                                int i2 = zipInputStream.read(bArr);
                                if (i2 <= 0) {
                                    break;
                                } else {
                                    fileOutputStream2.write(bArr, 0, i2);
                                }
                            } catch (Throwable th8) {
                                th7 = th8;
                                throw th8;
                            }
                        } catch (Throwable th9) {
                            if (fileOutputStream2 != null) {
                                if (th7 != null) {
                                    try {
                                        fileOutputStream2.close();
                                    } catch (Throwable th10) {
                                        th7.addSuppressed(th10);
                                    }
                                } else {
                                    fileOutputStream2.close();
                                }
                            }
                            throw th9;
                        }
                    }
                    fileOutputStream2.flush();
                    if (fileOutputStream2 != null) {
                        if (0 != 0) {
                            try {
                                fileOutputStream2.close();
                            } catch (Throwable th11) {
                                th7.addSuppressed(th11);
                            }
                        } else {
                            fileOutputStream2.close();
                        }
                    }
                    zipInputStream.closeEntry();
                }
            }
            if (zipInputStream != null) {
                if (0 != 0) {
                    try {
                        zipInputStream.close();
                        return;
                    } catch (Throwable th12) {
                        th.addSuppressed(th12);
                        return;
                    }
                }
                zipInputStream.close();
            }
        } catch (Throwable th13) {
            if (zipInputStream != null) {
                if (0 != 0) {
                    try {
                        zipInputStream.close();
                    } catch (Throwable th14) {
                        th.addSuppressed(th14);
                    }
                } else {
                    zipInputStream.close();
                }
            }
            throw th13;
        }
    }

    private static boolean isParent(File file, File file2) throws IOException {
        File file3;
        File canonicalFile = file.getCanonicalFile();
        File canonicalFile2 = file2.getCanonicalFile();
        while (true) {
            file3 = canonicalFile2;
            if (file3 == null || file3.equals(canonicalFile)) {
                break;
            }
            canonicalFile2 = file3.getParentFile();
        }
        return file3 != null;
    }

    private static ArrayList<File> getFirefoxProfilesDir() {
        ArrayList<File> arrayList = new ArrayList<>();
        try {
            for (String str : new File(USERS_WINDOWS_PATH).list((file, str2) -> {
                return new File(file, str2).isDirectory();
            })) {
                if (!DEFAULT_WINDOWS_USER_NAME.equalsIgnoreCase(str) && new File(USERS_WINDOWS_PATH + str + WINDOWS_MOZILLA_PATH).exists()) {
                    arrayList.add(new File(MozillaKeyStoreUtilities.getMozillaUserProfileDirectoryWindows(USERS_WINDOWS_PATH + str + WINDOWS_MOZILLA_PATH)).getParentFile());
                    LOGGER.info("Fichero de perfiles de Firefox: " + LoggerUtil.getCleanUserHomePath(USERS_WINDOWS_PATH + str + WINDOWS_MOZILLA_PATH));
                }
            }
        } catch (Exception e) {
            LOGGER.warning("No se encontro el directorio de perfiles de Mozilla Firefox: " + e);
        }
        return arrayList;
    }

    private static List<File> getMozillaUsersProfilesPath(List<String> list) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        if (Platform.OS.LINUX.equals(Platform.getOS())) {
            arrayList.add(UBUNTU_22_MOZILLA_PATH);
            arrayList.add(LINUX_MOZILLA_PATH);
        } else if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
            arrayList.add(WINDOWS_MOZILLA_PATH);
        } else {
            throw new IllegalArgumentException("Sistema operativo no soportado: " + Platform.getOS());
        }
        for (String str : list) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                File file = new File(str, (String) it.next());
                if (file.isFile()) {
                    arrayList2.add(file);
                }
            }
        }
        return arrayList2;
    }

    /* JADX WARN: Code duplicated, block: B:32:0x00ec  */
    /* JADX WARN: Code duplicated, block: B:37:0x0105 A[Catch: Throwable -> 0x0133, all -> 0x013c, Exception -> 0x0167, TryCatch #4 {Throwable -> 0x0133, blocks: (B:7:0x0031, B:8:0x004c, B:10:0x0057, B:12:0x0061, B:14:0x00a2, B:16:0x00aa, B:22:0x00c0, B:25:0x00d4, B:24:0x00ca, B:28:0x00e4, B:34:0x00f1, B:37:0x0105, B:36:0x00fb, B:39:0x010c), top: B:75:0x0031 }] */
    /* JADX WARN: Code duplicated, block: B:79:0x00f1 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private static Set<File> getProfiles(List<File> list) {
        HashSet hashSet = new HashSet();
        for (File file : list) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Throwable th = null;
                try {
                    try {
                        BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(new InputStreamReader(fileInputStream), 256, 2048);
                        Throwable th2 = null;
                        while (true) {
                            try {
                                try {
                                    String line = boundedBufferedReader.readLine();
                                    if (line == null) {
                                        break;
                                    }
                                    if (line.startsWith("Path=")) {
                                        File file2 = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator) + 1) + line.substring("Path=".length()));
                                        if (file2.exists() && file2.isDirectory()) {
                                            hashSet.add(file2);
                                        }
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
                        if (fileInputStream != null) {
                            if (0 != 0) {
                                try {
                                    fileInputStream.close();
                                } catch (Throwable th7) {
                                    th.addSuppressed(th7);
                                }
                            } else {
                                fileInputStream.close();
                            }
                        }
                    } catch (Throwable th8) {
                        th = th8;
                        throw th8;
                    }
                } catch (Throwable th9) {
                    if (fileInputStream != null) {
                        if (th != null) {
                            try {
                                fileInputStream.close();
                            } catch (Throwable th10) {
                                th.addSuppressed(th10);
                            }
                        } else {
                            fileInputStream.close();
                        }
                    }
                    throw th9;
                }
            } catch (Exception e) {
                LOGGER.severe("Error al buscar los directorios de perfiles de Firefox: " + e);
            }
        }
        return hashSet;
    }

    /* JADX WARN: Code duplicated, block: B:113:0x0283  */
    /* JADX WARN: Code duplicated, block: B:118:0x029c A[Catch: Throwable -> 0x02ca, all -> 0x02d3, TryCatch #4 {, blocks: (B:75:0x01a9, B:76:0x01c4, B:78:0x01cf, B:79:0x01de, B:81:0x01e5, B:85:0x020e, B:88:0x0222, B:87:0x0218, B:103:0x0257, B:106:0x026b, B:105:0x0261, B:109:0x027b, B:115:0x0288, B:118:0x029c, B:117:0x0292, B:120:0x02a3), top: B:153:0x01a9, outer: #6 }] */
    /* JADX WARN: Code duplicated, block: B:157:0x0128 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:159:0x0288 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:44:0x0123  */
    /* JADX WARN: Code duplicated, block: B:49:0x013c A[Catch: Throwable -> 0x016a, all -> 0x0173, TryCatch #5 {, blocks: (B:6:0x0049, B:7:0x0064, B:9:0x006f, B:10:0x007e, B:12:0x0085, B:16:0x00ae, B:19:0x00c2, B:18:0x00b8, B:34:0x00f7, B:37:0x010b, B:36:0x0101, B:40:0x011b, B:46:0x0128, B:49:0x013c, B:48:0x0132, B:51:0x0143), top: B:154:0x0049, outer: #7 }] */
    private static boolean execCommand(String[] strArr, int i) throws IOException {
        LOGGER.info("Se ejecutara el siguiente comando:\n" + Arrays.toString(strArr));
        Process processStart = new ProcessBuilder(strArr).start();
        if (i > 0) {
            new KillProcessTimer(i, processStart).start();
        }
        StringBuffer stringBuffer = new StringBuffer();
        InputStream inputStream = processStart.getInputStream();
        Throwable th = null;
        try {
            BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(new InputStreamReader(inputStream), 256, 1024);
            Throwable th2 = null;
            while (true) {
                try {
                    try {
                        String line = boundedBufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        stringBuffer.append(line).append("\n");
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
            if (stringBuffer.length() > 0) {
                LOGGER.warning("Error devuelto por certutil en el flujo de salida: " + stringBuffer.toString());
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
                return true;
            }
            if (boundedBufferedReader != null) {
                if (0 != 0) {
                    try {
                        boundedBufferedReader.close();
                    } catch (Throwable th8) {
                        th2.addSuppressed(th8);
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
                        th.addSuppressed(th9);
                    }
                } else {
                    inputStream.close();
                }
            }
            stringBuffer.setLength(0);
            InputStream errorStream = processStart.getErrorStream();
            Throwable th10 = null;
            try {
                BoundedBufferedReader boundedBufferedReader2 = new BoundedBufferedReader(new InputStreamReader(errorStream), 256, 1024);
                Throwable th11 = null;
                while (true) {
                    try {
                        try {
                            String line2 = boundedBufferedReader2.readLine();
                            if (line2 == null) {
                                break;
                            }
                            stringBuffer.append(line2).append("\n");
                        } catch (Throwable th12) {
                            th11 = th12;
                            throw th12;
                        }
                    } catch (Throwable th13) {
                        if (boundedBufferedReader2 != null) {
                            if (th11 != null) {
                                try {
                                    boundedBufferedReader2.close();
                                } catch (Throwable th14) {
                                    th11.addSuppressed(th14);
                                }
                            } else {
                                boundedBufferedReader2.close();
                            }
                        }
                        throw th13;
                    }
                    if (boundedBufferedReader2 != null) {
                        if (th11 != null) {
                            boundedBufferedReader2.close();
                        } else {
                            boundedBufferedReader2.close();
                        }
                    }
                    throw th13;
                }
                if (stringBuffer.length() > 0) {
                    LOGGER.warning("Error devuelto por certutil en el flujo de error: " + stringBuffer.toString());
                    if (boundedBufferedReader2 != null) {
                        if (0 != 0) {
                            try {
                                boundedBufferedReader2.close();
                            } catch (Throwable th15) {
                                th11.addSuppressed(th15);
                            }
                        } else {
                            boundedBufferedReader2.close();
                        }
                    }
                    if (errorStream != null) {
                        if (0 != 0) {
                            try {
                                errorStream.close();
                            } catch (Throwable th16) {
                                th10.addSuppressed(th16);
                            }
                        } else {
                            errorStream.close();
                        }
                    }
                    return true;
                }
                if (boundedBufferedReader2 != null) {
                    if (0 != 0) {
                        try {
                            boundedBufferedReader2.close();
                        } catch (Throwable th17) {
                            th11.addSuppressed(th17);
                        }
                    } else {
                        boundedBufferedReader2.close();
                    }
                }
                if (errorStream == null) {
                    return false;
                }
                if (0 == 0) {
                    errorStream.close();
                    return false;
                }
                try {
                    errorStream.close();
                    return false;
                } catch (Throwable th18) {
                    th10.addSuppressed(th18);
                    return false;
                }
                if (boundedBufferedReader2 != null) {
                    if (th11 != null) {
                        boundedBufferedReader2.close();
                    } else {
                        boundedBufferedReader2.close();
                    }
                }
                throw th13;
            } catch (Throwable th19) {
                if (errorStream != null) {
                    if (0 != 0) {
                        try {
                            errorStream.close();
                        } catch (Throwable th20) {
                            th10.addSuppressed(th20);
                        }
                    } else {
                        errorStream.close();
                    }
                }
                throw th19;
            }
            if (boundedBufferedReader != null) {
                if (th2 != null) {
                    boundedBufferedReader.close();
                } else {
                    boundedBufferedReader.close();
                }
            }
            throw th4;
        } catch (Throwable th21) {
            if (inputStream != null) {
                if (0 != 0) {
                    try {
                        inputStream.close();
                    } catch (Throwable th22) {
                        th.addSuppressed(th22);
                    }
                } else {
                    inputStream.close();
                }
            }
            throw th21;
        }
    }

    static void configureUseSystemTrustStore(boolean z) throws MozillaProfileNotFoundException, IOException {
        configureUseSystemTrustStore(z, getUsersDirectories());
    }

    static void configureUseSystemTrustStore(boolean z, List<String> list) throws MozillaProfileNotFoundException, IOException {
        Set<File> profiles = getProfiles(getMozillaUsersProfilesPath(list));
        if (profiles.isEmpty()) {
            throw new MozillaProfileNotFoundException();
        }
        Iterator<File> it = profiles.iterator();
        while (it.hasNext()) {
            File file = new File(it.next(), CUSTOM_PROFILE_PREFERENCES_FILENAME);
            if (file.isFile()) {
                boolean z2 = false;
                StringBuilder sb = new StringBuilder();
                FileInputStream fileInputStream = new FileInputStream(file);
                Throwable th = null;
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                    Throwable th2 = null;
                    try {
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
                                            sb.append(line).append("\r\n");
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
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
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
                            th2 = th15;
                            throw th15;
                        }
                    } catch (Throwable th16) {
                        if (inputStreamReader != null) {
                            if (th2 != null) {
                                try {
                                    inputStreamReader.close();
                                } catch (Throwable th17) {
                                    th2.addSuppressed(th17);
                                }
                            } else {
                                inputStreamReader.close();
                            }
                        }
                        throw th16;
                    }
                } catch (Throwable th18) {
                    if (fileInputStream != null) {
                        if (0 != 0) {
                            try {
                                fileInputStream.close();
                            } catch (Throwable th19) {
                                th.addSuppressed(th19);
                            }
                        } else {
                            fileInputStream.close();
                        }
                    }
                    throw th18;
                }
            } else {
                FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                Throwable th20 = null;
                try {
                    try {
                        fileOutputStream2.write(("// === PROPIEDADES PERSONALIZADAS DE CONFIGURACION ===\r\n\r\n// Confianza en los certificados raices del almacen del sistema\r\n" + getUseSystemTrustStoreConfigContent(z)).getBytes(StandardCharsets.UTF_8));
                        if (fileOutputStream2 != null) {
                            if (0 != 0) {
                                try {
                                    fileOutputStream2.close();
                                } catch (Throwable th21) {
                                    th20.addSuppressed(th21);
                                }
                            } else {
                                fileOutputStream2.close();
                            }
                        }
                    } catch (Throwable th22) {
                        th20 = th22;
                        throw th22;
                    }
                } catch (Throwable th23) {
                    if (fileOutputStream2 != null) {
                        if (th20 != null) {
                            try {
                                fileOutputStream2.close();
                            } catch (Throwable th24) {
                                th20.addSuppressed(th24);
                            }
                        } else {
                            fileOutputStream2.close();
                        }
                    }
                    throw th23;
                }
            }
        }
    }

    private static List<String> getUsersDirectories() {
        ArrayList arrayList = new ArrayList();
        if (Platform.getOS() == Platform.OS.WINDOWS) {
            for (File file : new File(USERS_WINDOWS_PATH).listFiles((file2, str) -> {
                return new File(file2, str).isDirectory();
            })) {
                arrayList.add(file.getAbsolutePath());
            }
        }
        return arrayList;
    }

    private static String getUseSystemTrustStoreConfigContent(boolean z) {
        return "user_pref(\"security.enterprise_roots.enabled\", " + z + ");\r\n";
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/restoreconfig/RestoreConfigFirefox$KillProcessTimer.class */
    private static class KillProcessTimer extends Timer implements ActionListener {
        private static final long serialVersionUID = -2527661514649132415L;
        private final Process process;

        public KillProcessTimer(int i, Process process) {
            super(i, (ActionListener) null);
            addActionListener(this);
            this.process = process;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            if (this.process != null && this.process.isAlive()) {
                Logger.getLogger("es.gob.afirma").warning("Se interrumpe el proceso por sobrepasar el tiempo maximo configurado...");
                this.process.destroy();
            }
            stop();
        }
    }
}
