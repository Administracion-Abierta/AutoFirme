package es.gob.afirma.standalone.configurator;

import es.gob.afirma.core.misc.BoundedBufferedReader;
import es.gob.afirma.standalone.configurator.common.ConfiguratorUtil;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/ConfiguratorFirefoxLinux.class */
final class ConfiguratorFirefoxLinux {
    private static final String FILE_AUTOFIRMA_CERTIFICATE = "Autofirma_ROOT.cer";
    private static final String PROFILES_INI_RELATIVE_PATH = ".mozilla/firefox/profiles.ini";
    private static final String PROFILES_INI_RELATIVE_PATH_UBUNTU_22 = "snap/firefox/common/.mozilla/firefox/profiles.ini";
    private static final String PROFILE_INI_PATH_PREFIX = "Path=";
    private static final String CERTUTIL_RESOURCE = "/linux/certutil.linux.zip";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String CERTUTIL_EXE = "certutil";
    private static final String CERTUTIL_RELATIVE_PATH = CERTUTIL_EXE + File.separator + CERTUTIL_EXE;
    private static final String NSS_CHROME_PATH = "/.pki/nssdb";
    private static final String NSS_CHROMIUM_PATH = "/snap/chromium/current/.pki/nssdb";
    private static final String[] NSS_DIR_SUBPATH = {NSS_CHROME_PATH, NSS_CHROMIUM_PATH};

    private ConfiguratorFirefoxLinux() {
    }

    static void createScriptsToSystemKeyStore(File file, String[] strArr, File file2, File file3) throws IOException {
        String absolutePath;
        LOGGER.info("Comprobamos que se encuentre certutil en el sistema");
        try {
            checkCertUtil(CERTUTIL_EXE);
            absolutePath = CERTUTIL_EXE;
        } catch (Exception e) {
            absolutePath = new File(file, CERTUTIL_RELATIVE_PATH).getAbsolutePath();
            LOGGER.info("No se ha encontrado certutil en el sistema. Se extrae la version interna del configurador al directorio de instalacion: " + absolutePath);
            ConfiguratorUtil.uncompressResource(CERTUTIL_RESOURCE, file);
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (String str : strArr) {
            for (String str2 : NSS_DIR_SUBPATH) {
                String str3 = str + str2;
                if (new File(str3).isDirectory()) {
                    String strEscapePath = escapePath("sql:" + str3);
                    arrayList.add(new String[]{absolutePath, "-d", strEscapePath, "-A", "-n", "\"SocketAutoFirma\"", "-i", escapePath(new File(file, FILE_AUTOFIRMA_CERTIFICATE).getAbsolutePath()), "-t", "\"TCP,TCP,TCP\""});
                    arrayList2.add(new String[]{absolutePath, "-D", "-d", strEscapePath, "-n", "\"SocketAutoFirma\""});
                }
            }
        }
        createScript(arrayList, file2);
        createScript(arrayList2, file3);
    }

    static void createScriptsToMozillaKeyStore(File file, String[] strArr, File file2, File file3) throws MozillaProfileNotFoundException, IOException {
        String absolutePath;
        try {
            checkCertUtil(CERTUTIL_EXE);
            absolutePath = CERTUTIL_EXE;
        } catch (Exception e) {
            try {
                ConfiguratorUtil.uncompressResource(CERTUTIL_RESOURCE, file);
                absolutePath = new File(file, CERTUTIL_RELATIVE_PATH).getAbsolutePath();
            } catch (Exception e2) {
                LOGGER.warning("No se ha podido descomprimir CertUtil. Se omite la configuracion del certificado SSL en Mozilla Firefox: " + e2);
                return;
            }
        }
        List<File> profiles = getProfiles(strArr);
        if (profiles == null || profiles.isEmpty()) {
            LOGGER.info("No se encuentran fichero de perfil de Mozilla, por lo que no se instalaran certificados");
            throw new MozillaProfileNotFoundException();
        }
        writeScriptsToMozillaKeyStoreWithCheck(absolutePath, file, profiles, file2, file3);
    }

    private static void writeScriptsToMozillaKeyStoreWithCheck(String str, File file, List<File> list, File file2, File file3) {
        boolean z = false;
        boolean z2 = false;
        do {
            try {
                writeScriptsToMozillaKeyStore(str, file, list, file2, file3);
                z = true;
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "No se pudo crear el script de instalacion del certificado SSL para el socket en el almacen de Firefox: " + e, (Throwable) e);
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

    private static void writeScriptsToMozillaKeyStore(String str, File file, List<File> list, File file2, File file3) throws GeneralSecurityException, IOException {
        for (File file4 : list) {
            if (file4.isDirectory()) {
                String strEscapePath = escapePath((new File(file4, "pkcs11.txt").exists() ? "sql:" : "") + file4.getAbsolutePath());
                createScript(new String[]{str, "-A", "-d", strEscapePath, "-i", escapePath(new File(file, FILE_AUTOFIRMA_CERTIFICATE).getAbsolutePath()), "-n", "\"SocketAutoFirma\"", "-t", "\"C,,\""}, file2);
                createScript(new String[]{str, "-D", "-d", strEscapePath, "-n", "\"SocketAutoFirma\""}, file3);
            }
        }
    }

    private static void createScript(String[] strArr, File file) throws IOException {
        createScript((List<String[]>) Collections.singletonList(strArr), file);
    }

    private static void createScript(List<String[]> list, File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        Iterator<String[]> it = list.iterator();
        while (it.hasNext()) {
            ConfiguratorUtil.printScript(it.next(), sb);
        }
        ConfiguratorUtil.writeScript(sb, file);
    }

    /* JADX WARN: Code duplicated, block: B:36:0x00f7  */
    /* JADX WARN: Code duplicated, block: B:41:0x0110 A[Catch: Throwable -> 0x013e, all -> 0x0147, Exception -> 0x0172, TryCatch #3 {Throwable -> 0x013e, blocks: (B:13:0x0045, B:14:0x0060, B:16:0x006b, B:18:0x0075, B:20:0x00b5, B:26:0x00cb, B:29:0x00df, B:28:0x00d5, B:32:0x00ef, B:38:0x00fc, B:41:0x0110, B:40:0x0106, B:43:0x0117), top: B:78:0x0045 }] */
    /* JADX WARN: Code duplicated, block: B:83:0x00fc A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private static List<File> getProfiles(String[] strArr) {
        List<File> mozillaProfilesIniPaths = getMozillaProfilesIniPaths(strArr);
        if (mozillaProfilesIniPaths == null || mozillaProfilesIniPaths.isEmpty()) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (File file : mozillaProfilesIniPaths) {
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
                                    if (line.startsWith(PROFILE_INI_PATH_PREFIX)) {
                                        File file2 = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("/") + 1) + line.substring(PROFILE_INI_PATH_PREFIX.length()));
                                        if (file2.isDirectory()) {
                                            arrayList.add(file2);
                                        }
                                    }
                                } catch (Throwable th3) {
                                    if (boundedBufferedReader != null) {
                                        if (th2 != null) {
                                            try {
                                                boundedBufferedReader.close();
                                            } catch (Throwable th4) {
                                                th2.addSuppressed(th4);
                                            }
                                        } else {
                                            boundedBufferedReader.close();
                                        }
                                    }
                                    throw th3;
                                }
                            } catch (Throwable th5) {
                                th2 = th5;
                                throw th5;
                            }
                            if (boundedBufferedReader != null) {
                                if (th2 != null) {
                                    boundedBufferedReader.close();
                                } else {
                                    boundedBufferedReader.close();
                                }
                            }
                            throw th3;
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
                LOGGER.warning("Error al buscar los directorios de perfiles de Firefox: " + e);
            }
        }
        return arrayList;
    }

    private static List<File> getMozillaProfilesIniPaths(String[] strArr) {
        ArrayList arrayList = new ArrayList();
        for (String str : strArr) {
            File file = new File(str, PROFILES_INI_RELATIVE_PATH_UBUNTU_22);
            if (file.isFile()) {
                arrayList.add(file);
            } else {
                File file2 = new File(str, PROFILES_INI_RELATIVE_PATH);
                if (file2.isFile()) {
                    arrayList.add(file2);
                }
            }
        }
        return arrayList;
    }

    private static void checkCertUtil(String str) throws IOException {
        new ProcessBuilder(str).start();
    }

    private static String escapePath(String str) {
        if (str == null) {
            throw new IllegalArgumentException("La ruta a 'escapar' no puede ser nula");
        }
        return str.replace(" ", "\\ ");
    }
}
