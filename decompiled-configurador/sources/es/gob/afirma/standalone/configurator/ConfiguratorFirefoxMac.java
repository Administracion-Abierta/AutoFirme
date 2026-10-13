package es.gob.afirma.standalone.configurator;

import es.gob.afirma.core.misc.BoundedBufferedReader;
import es.gob.afirma.standalone.configurator.common.ConfiguratorUtil;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/ConfiguratorFirefoxMac.class */
final class ConfiguratorFirefoxMac {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String FILE_AUTOFIRMA_CERTIFICATE = "Autofirma_ROOT.cer";
    private static final String CERTUTIL_RESOURCE = "/osx/certutil.osx.zip";
    private static final String CERTUTIL_RELATIVE_PATH = "certutil/certutil";
    private static final String PROFILES_INI_RELATIVE_PATH = "/Library/Application Support/firefox/profiles.ini";
    private static final String COMMAND_EXPORT_PATH = "export PATH=$PATH:";
    private static final String COMMAND_EXPORT_LIBRARY_LD = "export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:";
    private static final String CUSTOM_PROFILE_PREFERENCES_FILENAME = "user.js";
    private static final String MOZ_PREFERENCE_FILE_HEADER = "// === PROPIEDADES PERSONALIZADAS DE CONFIGURACION ===\r\n";
    private static final String MOZ_PREFERENCE_ENTERPRISE_ROOTS_HEADER = "\r\n// Confianza en los certificados raices del almacen del sistema\r\n";
    private static final String MOZ_PREFERENCE_ENTERPRISE_ROOTS = "security.enterprise_roots.enabled";
    private static final String BREAK_LINE = "\r\n";

    private ConfiguratorFirefoxMac() {
    }

    static void installOnMozillaKeyStore(File file, String[] strArr, File file2, Console console) throws MozillaProfileNotFoundException, IOException {
        try {
            File filePrepareCertUtil = prepareCertUtil(file, file2);
            for (File file3 : getMozillaUsersProfiles(strArr)) {
                console.print(Messages.getString("ConfiguratorMacOSX.35", file3.getName()));
                generateInstallScriptWithCheck(file, file3, filePrepareCertUtil, file2);
                try {
                    ConfiguratorMacOSX.executeScriptFile(file2, true, false);
                } catch (Exception e) {
                    console.print(Messages.getString("ConfiguratorMacOSX.36", file3.getName()));
                    LOGGER.log(Level.WARNING, "No se pudo instalar el certificado en el siguiente perfil de Mozilla: " + file3.getName(), (Throwable) e);
                }
            }
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "No se pudo preparar CertUtil para la instalacion del certificado SSL en Mozilla Firefox. Se aborta la operacion", (Throwable) e2);
        }
    }

    static void uninstallFromMozillaKeyStore(File file, String[] strArr, File file2) throws MozillaProfileNotFoundException, IOException {
        try {
            File filePrepareCertUtil = prepareCertUtil(file, file2);
            for (File file3 : getMozillaUsersProfiles(strArr)) {
                generateUninstallScript(file, file3, filePrepareCertUtil, file2);
                try {
                    ConfiguratorMacOSX.executeScriptFile(file2, true, false);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "No se pudo desinstalar el certificado en el siguiente perfil de Mozilla: " + file3.getName(), (Throwable) e);
                }
            }
        } catch (Exception e2) {
            LOGGER.warning("No se pudo preparar CertUtil para la desinstalacion del certificado SSL en Mozilla Firefox. Se aborta la operacion: " + e2);
        }
    }

    private static void generateInstallScriptWithCheck(File file, File file2, File file3, File file4) {
        boolean z = false;
        boolean z2 = false;
        do {
            try {
                generateInstallScript(file, file2, file3, file4);
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

    private static void generateInstallScript(File file, File file2, File file3, File file4) throws IOException {
        if (!file2.isDirectory()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(COMMAND_EXPORT_PATH);
        sb.append(file3.getParentFile().getAbsolutePath());
        ConfiguratorMacUtils.writeScriptFile(sb, file4, false);
        StringBuilder sb2 = new StringBuilder();
        sb2.append(COMMAND_EXPORT_LIBRARY_LD);
        sb2.append(file3.getParentFile().getAbsolutePath());
        ConfiguratorMacUtils.writeScriptFile(sb2, file4, true);
        ConfiguratorMacUtils.writeScriptFile(new StringBuilder().append(escapePath(file3.getAbsolutePath())).append(" -A -d ").append(escapePath((new File(file2, "pkcs11.txt").exists() ? "sql:" : "") + file2.getAbsolutePath())).append(" -i ").append(escapePath(new File(file, FILE_AUTOFIRMA_CERTIFICATE).getAbsolutePath())).append(" -n ").append("\"").append(ConfiguratorUtil.CERT_ALIAS).append("\"").append(" -t  \"C,,\""), file4, true);
    }

    private static void generateUninstallScript(File file, File file2, File file3, File file4) throws IOException {
        if (!file2.isDirectory()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(COMMAND_EXPORT_PATH);
        sb.append(file3.getParentFile().getAbsolutePath());
        ConfiguratorMacUtils.writeScriptFile(sb, file4, false);
        StringBuilder sb2 = new StringBuilder();
        sb2.append(COMMAND_EXPORT_LIBRARY_LD);
        sb2.append(file3.getParentFile().getAbsolutePath());
        ConfiguratorMacUtils.writeScriptFile(sb2, file4, true);
        String str = (new File(file2, "pkcs11.txt").exists() ? "sql:" : "") + file2.getAbsolutePath();
        ConfiguratorMacUtils.writeScriptFile(new StringBuilder().append("max=$(").append(escapePath(file3.getAbsolutePath())).append(" -L -d ").append(escapePath(str)).append(" | grep Autofirma | wc -l);for ((i=0; i<$max; i++));do ").append(escapePath(file3.getAbsolutePath())).append(" -D -d ").append(escapePath(str)).append(" -n \"SocketAutoFirma\";done"), file4, true);
    }

    private static File prepareCertUtil(File file, File file2) throws IOException {
        File file3 = new File(file, CERTUTIL_RELATIVE_PATH);
        if (!file3.isFile()) {
            ConfiguratorUtil.uncompressResource(CERTUTIL_RESOURCE, file);
        }
        if (!file3.isFile()) {
            throw new IOException("No se encuentra CertUtil para la instalacion en Firefox");
        }
        if (!file3.canExecute()) {
            ConfiguratorMacUtils.addExexPermissionsToAllFilesOnDirectory(file3.getParentFile());
        }
        if (!file3.canExecute()) {
            throw new IOException("El ejecutable CertUtil no tiene permisos de ejecucion");
        }
        return file3;
    }

    private static File[] getMozillaUsersProfiles(String[] strArr) throws MozillaProfileNotFoundException {
        List<File> profilesIniFiles = getProfilesIniFiles(strArr);
        if (profilesIniFiles == null) {
            throw new MozillaProfileNotFoundException();
        }
        List<File> profileDirs = getProfileDirs(profilesIniFiles);
        if (profileDirs.isEmpty()) {
            throw new MozillaProfileNotFoundException();
        }
        return (File[]) profileDirs.toArray(new File[profileDirs.size()]);
    }

    private static List<File> getProfilesIniFiles(String[] strArr) {
        ArrayList arrayList = new ArrayList();
        for (String str : strArr) {
            File file = new File(str, PROFILES_INI_RELATIVE_PATH);
            if (file.exists() && file.isFile()) {
                arrayList.add(file);
            }
        }
        return arrayList;
    }

    /* JADX WARN: Code duplicated, block: B:32:0x00eb  */
    /* JADX WARN: Code duplicated, block: B:37:0x0104 A[Catch: Throwable -> 0x0132, all -> 0x013b, Exception -> 0x0166, TryCatch #4 {Throwable -> 0x0132, blocks: (B:7:0x0031, B:8:0x004c, B:10:0x0057, B:12:0x0061, B:14:0x00a1, B:16:0x00a9, B:22:0x00bf, B:25:0x00d3, B:24:0x00c9, B:28:0x00e3, B:34:0x00f0, B:37:0x0104, B:36:0x00fa, B:39:0x010b), top: B:75:0x0031 }] */
    /* JADX WARN: Code duplicated, block: B:81:0x00f0 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private static List<File> getProfileDirs(List<File> list) {
        ArrayList arrayList = new ArrayList();
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
                                        File file2 = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("/") + 1) + line.substring("Path=".length()));
                                        if (file2.exists() && file2.isDirectory()) {
                                            arrayList.add(file2);
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
                LOGGER.warning("Error al buscar los directorios de perfiles de Firefox: " + e);
            }
        }
        return arrayList;
    }

    private static String escapePath(String str) {
        if (str == null) {
            throw new IllegalArgumentException("La ruta a 'escapar' no puede ser nula");
        }
        return str.replace(" ", "\\ ");
    }

    static void configureUseSystemTrustStore(boolean z, String[] strArr, Console console) throws MozillaProfileNotFoundException, IOException {
        File[] mozillaUsersProfiles = getMozillaUsersProfiles(strArr);
        if (mozillaUsersProfiles == null || mozillaUsersProfiles.length == 0) {
            throw new MozillaProfileNotFoundException("No se han encontrado perfiles de Mozilla en el sistema");
        }
        if (z) {
            console.print(Messages.getString("ConfiguratorWindows.19"));
        } else {
            console.print(Messages.getString("ConfiguratorWindows.20"));
        }
        for (File file : mozillaUsersProfiles) {
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
                        if (fileOutputStream2 != null) {
                            if (th19 != null) {
                                try {
                                    fileOutputStream2.close();
                                } catch (Throwable th22) {
                                    th19.addSuppressed(th22);
                                }
                            } else {
                                fileOutputStream2.close();
                            }
                        }
                        throw th21;
                    }
                } catch (Throwable th23) {
                    th19 = th23;
                    throw th23;
                }
            }
        }
    }

    private static String getUseSystemTrustStoreConfigContent(boolean z) {
        return "user_pref(\"security.enterprise_roots.enabled\", " + z + ");" + BREAK_LINE;
    }
}
