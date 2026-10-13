package es.gob.afirma.standalone.ui.restoreconfig;

import es.gob.afirma.core.misc.BoundedBufferedReader;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.configurator.common.ConfiguratorUtil;
import es.gob.afirma.standalone.so.macos.ShellScript;
import es.gob.afirma.standalone.so.macos.UnixUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/restoreconfig/RestoreConfigFirefoxMacOS.class */
final class RestoreConfigFirefoxMacOS {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String MACOSX_MOZILLA_PATH = "/Library/Application Support/firefox/profiles.ini";
    private static final String PROFILES_INI_PATH_PREFIX = "Path=";
    private static final String DIR_CERTUTIL = "certutil";
    private static final String CERTUTIL_EXE = "certutil";
    private static final String RESOURCE_CERTUTIL = "/osx/certutil.osx.zip";
    private static final String CUSTOM_PROFILE_PREFERENCES_FILENAME = "user.js";
    private static final String MOZ_PREFERENCE_FILE_HEADER = "// === PROPIEDADES PERSONALIZADAS DE CONFIGURACION ===\r\n";
    private static final String MOZ_PREFERENCE_ENTERPRISE_ROOTS_HEADER = "\r\n// Confianza en los certificados raices del almacen del sistema\r\n";
    private static final String MOZ_PREFERENCE_ENTERPRISE_ROOTS = "security.enterprise_roots.enabled";
    private static final String BREAK_LINE = "\r\n";
    private final List<File> usersDirs;
    private final RestoreConfigPanel restorePanel;
    private List<File> profilesDirs = null;

    public RestoreConfigFirefoxMacOS(List<File> list, RestoreConfigPanel restoreConfigPanel) {
        this.usersDirs = list;
        this.restorePanel = restoreConfigPanel;
    }

    public boolean hasProfiles() {
        return !getMozillaProfiles().isEmpty();
    }

    public void installRootCAMozillaKeyStore(File file, File file2) throws MozillaProfileNotFoundException, IOException, KeyStoreException {
        manageMozillaKeyStore(file, false, file2);
    }

    public void uninstallRootCAMozillaKeyStore(File file, File file2) throws MozillaProfileNotFoundException, IOException, KeyStoreException {
        manageMozillaKeyStore(file, true, null);
    }

    public void reinstallRootCAMozillaKeyStore(File file, File file2) throws MozillaProfileNotFoundException, IOException, KeyStoreException {
        manageMozillaKeyStore(file, true, file2);
    }

    private void manageMozillaKeyStore(File file, boolean z, File file2) throws MozillaProfileNotFoundException, IOException, KeyStoreException {
        List<File> mozillaProfiles = getMozillaProfiles();
        if (mozillaProfiles.isEmpty()) {
            throw new MozillaProfileNotFoundException();
        }
        try {
            executeCertUtil(prepareCertUtil(file), z, file2, mozillaProfiles);
        } catch (Exception e) {
            throw new KeyStoreException("Error al instalar la CA de confianza en el almacen de Firefox", e);
        }
    }

    private void executeCertUtil(File file, boolean z, File file2, List<File> list) throws IOException, KeyStoreException {
        boolean zExecCommandLineCertUtil = false;
        for (File file3 : list) {
            if (file3.isDirectory()) {
                this.restorePanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.36", file3.getName()));
                zExecCommandLineCertUtil = execCommandLineCertUtil(file, file3, z, file2);
                if (zExecCommandLineCertUtil) {
                    this.restorePanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.37", file3.getName()));
                }
            }
        }
        if (zExecCommandLineCertUtil) {
            throw new KeyStoreException("Error en la instalacion del certificado de CA en alguno de los perfiles de usuario de Firefox. Es posible que la aplicacion funcione en su propio perfil.");
        }
    }

    private static boolean execCommandLineCertUtil(File file, File file2, boolean z, File file3) throws IOException {
        String strEscapePath = escapePath(file.getParent());
        RestoreConfigMacOSX.writeScriptFile("export PATH=$PATH:" + strEscapePath, false);
        RestoreConfigMacOSX.writeScriptFile("export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:" + strEscapePath, true);
        if (z) {
            String removeCertCommand = getRemoveCertCommand(file, file2);
            System.out.println(removeCertCommand);
            RestoreConfigMacOSX.writeScriptFile(removeCertCommand, true);
        }
        if (file3 != null) {
            RestoreConfigMacOSX.writeScriptFile(getImportCertCommand(file, file2, file3), true);
        }
        try {
            new ShellScript(new File(RestoreConfigMacOSX.mac_script_path), false).run();
            return false;
        } catch (Exception e) {
            LOGGER.severe("Error en la instalacion del certificado en el almacen de confianza del perfil " + file2.getName() + " de Firefox: " + e);
            return true;
        }
    }

    private static String getRemoveCertCommand(File file, File file2) {
        return String.format("max=$(%1$s -L -d %2$s | grep Autofirma | wc -l);for ((i=0; i<$max; i++));do %1$s -D -d %2$s -n \"%3$s\";done", escapePath(file.getAbsolutePath()), (new File(file2, "pkcs11.txt").exists() ? "sql:" : "") + escapePath(file2.getAbsolutePath()), ConfiguratorUtil.CERT_ALIAS);
    }

    private static String getImportCertCommand(File file, File file2, File file3) {
        return String.format("%s -A -d sql:%s -i %s -n \"%s\" -t \"C,,\"", escapePath(file.getAbsolutePath()), escapePath(file2.getAbsolutePath()), escapePath(file3.getAbsolutePath()), ConfiguratorUtil.CERT_ALIAS);
    }

    private static File prepareCertUtil(File file) throws IOException {
        File file2 = new File(file, "certutil");
        copyCertUtils(file2);
        return getCertUtilFile(file2);
    }

    private static void copyCertUtils(File file) throws IOException {
        if (!file.exists()) {
            uncompressResource(RESOURCE_CERTUTIL, file.getParentFile());
            UnixUtils.addExexPermissionsToAllFilesOnDirectory(file);
        }
    }

    private static void uncompressResource(String str, File file) throws IOException {
        byte[] bArr = new byte[1024];
        ZipInputStream zipInputStream = new ZipInputStream(RestoreConfigFirefoxMacOS.class.getResourceAsStream(str));
        Throwable th = null;
        while (true) {
            try {
                ZipEntry nextEntry = zipInputStream.getNextEntry();
                if (nextEntry != null) {
                    File canonicalFile = new File(file, nextEntry.getName()).getCanonicalFile();
                    if (!isParent(file, canonicalFile)) {
                        zipInputStream.closeEntry();
                        throw new IOException("Se ha encontrado en el archivo comprimido una ruta que apuntaba fuera del directorio de destino");
                    }
                    if (nextEntry.isDirectory()) {
                        canonicalFile.mkdirs();
                    } else {
                        if (!canonicalFile.getParentFile().exists()) {
                            canonicalFile.getParentFile().mkdirs();
                        }
                        FileOutputStream fileOutputStream = new FileOutputStream(canonicalFile);
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
                } else {
                    if (zipInputStream != null) {
                        if (0 != 0) {
                            try {
                                zipInputStream.close();
                                return;
                            } catch (Throwable th7) {
                                th.addSuppressed(th7);
                                return;
                            }
                        }
                        zipInputStream.close();
                        return;
                    }
                    return;
                }
            } catch (Throwable th8) {
                if (zipInputStream != null) {
                    if (0 != 0) {
                        try {
                            zipInputStream.close();
                        } catch (Throwable th9) {
                            th.addSuppressed(th9);
                        }
                    } else {
                        zipInputStream.close();
                    }
                }
                throw th8;
            }
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

    private static File getCertUtilFile(File file) throws IOException {
        File file2 = new File(file, "certutil");
        if (!file2.isFile()) {
            throw new IOException("No se encuentra el ejecutable CertUtil para la instalacion en Firefox");
        }
        if (!file2.canExecute()) {
            UnixUtils.addExexPermissionsToAllFilesOnDirectory(file2.getParentFile());
        }
        if (!file2.canExecute()) {
            throw new IOException("No hay permisos de ejecucion para Mozilla CertUtil");
        }
        return file2;
    }

    private static String escapePath(String str) {
        return str.replace(" ", "\\ ");
    }

    private List<File> getMozillaProfiles() {
        if (this.profilesDirs == null) {
            this.profilesDirs = new ArrayList();
            Iterator<File> it = this.usersDirs.iterator();
            while (it.hasNext()) {
                File file = new File(it.next(), MACOSX_MOZILLA_PATH);
                if (file.isFile()) {
                    this.profilesDirs.addAll(loadProfiles(file));
                }
            }
        }
        return this.profilesDirs;
    }

    /* JADX WARN: Code duplicated, block: B:27:0x00a3  */
    /* JADX WARN: Code duplicated, block: B:32:0x00bc A[Catch: Throwable -> 0x00e5, all -> 0x00ed, Exception -> 0x0113, TryCatch #5 {, blocks: (B:4:0x0013, B:5:0x002d, B:7:0x0038, B:9:0x0042, B:11:0x0061, B:17:0x0077, B:20:0x008b, B:19:0x0081, B:23:0x009b, B:29:0x00a8, B:32:0x00bc, B:31:0x00b2, B:34:0x00c3), top: B:72:0x0013 }] */
    /* JADX WARN: Code duplicated, block: B:66:0x00a8 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Not initialized variable reg: 8, insn: 0x00ef: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r8 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:47:0x00ef */
    /* JADX WARN: Not initialized variable reg: 9, insn: 0x00f3: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r9 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:49:0x00f3 */
    private static List<File> loadProfiles(File file) {
        InputStream inputStream;
        Throwable th;
        ArrayList arrayList = new ArrayList();
        try {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Throwable th2 = null;
                BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(new InputStreamReader(fileInputStream), 256, 2048);
                Throwable th3 = null;
                while (true) {
                    try {
                        try {
                            String line = boundedBufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            if (line.startsWith(PROFILES_INI_PATH_PREFIX)) {
                                File file2 = new File(file.getParentFile(), line.substring(PROFILES_INI_PATH_PREFIX.length()));
                                if (file2.isDirectory()) {
                                    arrayList.add(file2);
                                }
                            }
                        } catch (Throwable th4) {
                            th3 = th4;
                            throw th4;
                        }
                    } catch (Throwable th5) {
                        if (boundedBufferedReader != null) {
                            if (th3 != null) {
                                try {
                                    boundedBufferedReader.close();
                                } catch (Throwable th6) {
                                    th3.addSuppressed(th6);
                                }
                            } else {
                                boundedBufferedReader.close();
                            }
                        }
                        throw th5;
                    }
                    if (boundedBufferedReader != null) {
                        if (th3 != null) {
                            boundedBufferedReader.close();
                        } else {
                            boundedBufferedReader.close();
                        }
                    }
                    throw th5;
                }
                if (boundedBufferedReader != null) {
                    if (0 != 0) {
                        try {
                            boundedBufferedReader.close();
                        } catch (Throwable th7) {
                            th3.addSuppressed(th7);
                        }
                    } else {
                        boundedBufferedReader.close();
                    }
                }
                if (fileInputStream != null) {
                    if (0 != 0) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th8) {
                            th2.addSuppressed(th8);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
            } catch (Throwable th9) {
                if (inputStream != null) {
                    if (th != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable th10) {
                            th.addSuppressed(th10);
                        }
                    } else {
                        inputStream.close();
                    }
                }
                throw th9;
            }
        } catch (Exception e) {
            LOGGER.severe("Error al buscar los directorios de perfiles de Firefox: " + e);
        }
        return arrayList;
    }

    public void configureUseSystemTrustStore(boolean z) throws MozillaProfileNotFoundException, IOException {
        List<File> mozillaProfiles = getMozillaProfiles();
        if (mozillaProfiles.isEmpty()) {
            throw new MozillaProfileNotFoundException();
        }
        for (File file : mozillaProfiles) {
            this.restorePanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.38", file.getName()));
            try {
                configureUseSystemTrustStore(file, z);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "No se pudo editar la confianza de Firefox para el perfil: " + LoggerUtil.getCleanUserHomePath(file.getName()), (Throwable) e);
                this.restorePanel.appendMessage(SimpleAfirmaMessages.getString("RestoreConfigMacOSX.39", file.getName()));
            }
        }
    }

    private static void configureUseSystemTrustStore(File file, boolean z) throws IOException {
        File file2 = new File(file, CUSTOM_PROFILE_PREFERENCES_FILENAME);
        if (!file2.isFile()) {
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            Throwable th = null;
            try {
                fileOutputStream.write(("// === PROPIEDADES PERSONALIZADAS DE CONFIGURACION ===\r\n\r\n// Confianza en los certificados raices del almacen del sistema\r\n" + getUseSystemTrustStoreConfigContent(z)).getBytes(StandardCharsets.UTF_8));
                if (fileOutputStream != null) {
                    if (0 == 0) {
                        fileOutputStream.close();
                        return;
                    }
                    try {
                        fileOutputStream.close();
                        return;
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                        return;
                    }
                }
                return;
            } catch (Throwable th3) {
                if (fileOutputStream != null) {
                    if (0 != 0) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        fileOutputStream.close();
                    }
                }
                throw th3;
            }
        }
        boolean z2 = false;
        StringBuilder sb = new StringBuilder();
        FileInputStream fileInputStream = new FileInputStream(file2);
        Throwable th5 = null;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            Throwable th6 = null;
            try {
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                Throwable th7 = null;
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
                        } catch (Throwable th8) {
                            th7 = th8;
                            throw th8;
                        }
                    } catch (Throwable th9) {
                        if (bufferedReader != null) {
                            if (th7 != null) {
                                try {
                                    bufferedReader.close();
                                } catch (Throwable th10) {
                                    th7.addSuppressed(th10);
                                }
                            } else {
                                bufferedReader.close();
                            }
                        }
                        throw th9;
                    }
                }
                if (bufferedReader != null) {
                    if (0 != 0) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable th11) {
                            th7.addSuppressed(th11);
                        }
                    } else {
                        bufferedReader.close();
                    }
                }
                if (inputStreamReader != null) {
                    if (0 != 0) {
                        try {
                            inputStreamReader.close();
                        } catch (Throwable th12) {
                            th6.addSuppressed(th12);
                        }
                    } else {
                        inputStreamReader.close();
                    }
                }
                if (fileInputStream != null) {
                    if (0 != 0) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th13) {
                            th5.addSuppressed(th13);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
                if (!z2) {
                    sb.append(MOZ_PREFERENCE_ENTERPRISE_ROOTS_HEADER).append(getUseSystemTrustStoreConfigContent(z));
                }
                editFile(file2, sb.toString().getBytes(StandardCharsets.UTF_8));
            } catch (Throwable th14) {
                if (inputStreamReader != null) {
                    if (0 != 0) {
                        try {
                            inputStreamReader.close();
                        } catch (Throwable th15) {
                            th6.addSuppressed(th15);
                        }
                    } else {
                        inputStreamReader.close();
                    }
                }
                throw th14;
            }
        } catch (Throwable th16) {
            if (fileInputStream != null) {
                if (0 != 0) {
                    try {
                        fileInputStream.close();
                    } catch (Throwable th17) {
                        th5.addSuppressed(th17);
                    }
                } else {
                    fileInputStream.close();
                }
            }
            throw th16;
        }
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x004b */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void editFile(java.io.File r4, byte[] r5) throws java.io.IOException {
        /*
            r0 = r4
            boolean r0 = r0.canWrite()
            if (r0 != 0) goto L12
            r0 = r4
            boolean r0 = isMine(r0)
            if (r0 != 0) goto L12
            r0 = r4
            makeFileMine(r0)
        L12:
            java.io.FileOutputStream r0 = new java.io.FileOutputStream
            r1 = r0
            r2 = r4
            r1.<init>(r2)
            r6 = r0
            r0 = 0
            r7 = r0
            r0 = r6
            r1 = r5
            r0.write(r1)     // Catch: java.lang.Throwable -> L43 java.lang.Throwable -> L4b
            r0 = r6
            if (r0 == 0) goto L6e
            r0 = r7
            if (r0 == 0) goto L3c
            r0 = r6
            r0.close()     // Catch: java.lang.Throwable -> L31
            goto L6e
        L31:
            r8 = move-exception
            r0 = r7
            r1 = r8
            r0.addSuppressed(r1)
            goto L6e
        L3c:
            r0 = r6
            r0.close()
            goto L6e
        L43:
            r8 = move-exception
            r0 = r8
            r7 = r0
            r0 = r8
            throw r0     // Catch: java.lang.Throwable -> L4b
        L4b:
            r9 = move-exception
            r0 = r6
            if (r0 == 0) goto L6b
            r0 = r7
            if (r0 == 0) goto L67
            r0 = r6
            r0.close()     // Catch: java.lang.Throwable -> L5c
            goto L6b
        L5c:
            r10 = move-exception
            r0 = r7
            r1 = r10
            r0.addSuppressed(r1)
            goto L6b
        L67:
            r0 = r6
            r0.close()
        L6b:
            r0 = r9
            throw r0
        L6e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.standalone.ui.restoreconfig.RestoreConfigFirefoxMacOS.editFile(java.io.File, byte[]):void");
    }

    private static boolean isMine(File file) {
        try {
            String user = getUser();
            return user == null || user.equals(Files.getOwner(file.toPath(), new LinkOption[0]).getName());
        } catch (Exception e) {
            LOGGER.warning("No se ha podido comprobar el propietario del fichero: " + e);
            return true;
        }
    }

    private static void makeFileMine(File file) {
        try {
            String user = getUser();
            if (user != null) {
                RestoreConfigMacOSX.writeScriptFile(String.format("chown -f %s %s", user, escapePath(file.getAbsolutePath())), false);
                new ShellScript(new File(RestoreConfigMacOSX.mac_script_path), false).runAsAdministrator();
            }
        } catch (Exception e) {
            LOGGER.warning("No se ha podido cambiar el propietario del fichero para obtener el permiso de escritura: " + e);
        }
    }

    private static String getUser() {
        return System.getenv("USER");
    }

    private static String getUseSystemTrustStoreConfigContent(boolean z) {
        return "user_pref(\"security.enterprise_roots.enabled\", " + z + ");\r\n";
    }
}
