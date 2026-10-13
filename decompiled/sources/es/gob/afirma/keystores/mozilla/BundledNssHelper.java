package es.gob.afirma.keystores.mozilla;

import es.gob.afirma.core.LogManager;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.keystores.KeyStoreUtilities;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/mozilla/BundledNssHelper.class */
final class BundledNssHelper {
    private static final int BUFFER_SIZE = 2048;
    public static final String AFIRMA_NSS_HOME = Platform.getUserHome() + File.separator + LogManager.SUBDIR + File.separator + "nss" + File.separator + Platform.getJavaArch();

    BundledNssHelper() {
    }

    static String getBundledNssDirectory() throws IOException {
        File nssDirFile = getNssDirFile();
        uncompressZip(getNssZipResourceName(), nssDirFile);
        if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
            return KeyStoreUtilities.getWindowsShortName(nssDirFile.getAbsolutePath());
        }
        String absolutePath = nssDirFile.getAbsolutePath();
        Logger.getLogger("es.gob.afirma").info("Se ha encontrado un NSS incorporado en: " + absolutePath.replace(System.getProperty("user.home"), "USERHOME"));
        return absolutePath;
    }

    private static String getNssZipResourceName() {
        return "/nss/" + Platform.getOS().toString() + "/nss" + Platform.getJavaArch() + ".zip";
    }

    private static File getNssDirFile() throws IOException {
        File file = new File(AFIRMA_NSS_HOME);
        if (!file.exists() && !file.mkdirs()) {
            throw new IOException("No se ha podido crear el directorio de NSS: " + AFIRMA_NSS_HOME);
        }
        if (!file.isDirectory()) {
            throw new IOException("No se ha podido crear el directorio de NSS por encontrarse un fichero con el mismo nombre: " + AFIRMA_NSS_HOME);
        }
        if (!file.canWrite() || !file.canRead()) {
            throw new IOException("No se tienen permisos sobre el directorio de NSS: " + AFIRMA_NSS_HOME);
        }
        return file;
    }

    private static void uncompressZip(String str, File file) throws IOException {
        byte[] bArr = new byte[2048];
        InputStream resourceAsStream = BundledNssHelper.class.getResourceAsStream(str);
        Throwable th = null;
        try {
            ZipInputStream zipInputStream = new ZipInputStream(resourceAsStream);
            Throwable th2 = null;
            while (true) {
                try {
                    ZipEntry nextEntry = zipInputStream.getNextEntry();
                    if (nextEntry == null) {
                        if (zipInputStream != null) {
                            if (0 != 0) {
                                try {
                                    zipInputStream.close();
                                } catch (Throwable th3) {
                                    th2.addSuppressed(th3);
                                }
                            } else {
                                zipInputStream.close();
                            }
                        }
                        if (resourceAsStream != null) {
                            if (0 == 0) {
                                resourceAsStream.close();
                                return;
                            }
                            try {
                                resourceAsStream.close();
                                return;
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                                return;
                            }
                        }
                        return;
                    }
                    File canonicalFile = new File(file, nextEntry.getName()).getCanonicalFile();
                    if (!isParent(file, canonicalFile)) {
                        zipInputStream.closeEntry();
                        throw new IOException("Se ha encontrado en el archivo comprimido una ruta que apuntaba fuera del directorio de destino");
                    }
                    if (nextEntry.isDirectory()) {
                        canonicalFile.mkdirs();
                    } else {
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(canonicalFile);
                            Throwable th5 = null;
                            while (true) {
                                try {
                                    try {
                                        int i = zipInputStream.read(bArr);
                                        if (i <= 0) {
                                            break;
                                        } else {
                                            fileOutputStream.write(bArr, 0, i);
                                        }
                                    } catch (Throwable th6) {
                                        th5 = th6;
                                        throw th6;
                                    }
                                } catch (Throwable th7) {
                                    if (fileOutputStream != null) {
                                        if (th5 != null) {
                                            try {
                                                fileOutputStream.close();
                                            } catch (Throwable th8) {
                                                th5.addSuppressed(th8);
                                            }
                                        } else {
                                            fileOutputStream.close();
                                        }
                                    }
                                    throw th7;
                                }
                            }
                            fileOutputStream.flush();
                            if (fileOutputStream != null) {
                                if (0 != 0) {
                                    try {
                                        fileOutputStream.close();
                                    } catch (Throwable th9) {
                                        th5.addSuppressed(th9);
                                    }
                                } else {
                                    fileOutputStream.close();
                                }
                            }
                        } catch (Exception e) {
                            if (!new File(file, nextEntry.getName()).exists()) {
                                zipInputStream.closeEntry();
                                throw new IOException("No se pudo descomprimir una de las dependencias para la carga de NSS", e);
                            }
                        }
                    }
                    zipInputStream.closeEntry();
                } catch (Throwable th10) {
                    if (zipInputStream != null) {
                        if (0 != 0) {
                            try {
                                zipInputStream.close();
                            } catch (Throwable th11) {
                                th2.addSuppressed(th11);
                            }
                        } else {
                            zipInputStream.close();
                        }
                    }
                    throw th10;
                }
            }
        } catch (Throwable th12) {
            if (resourceAsStream != null) {
                if (0 != 0) {
                    try {
                        resourceAsStream.close();
                    } catch (Throwable th13) {
                        th.addSuppressed(th13);
                    }
                } else {
                    resourceAsStream.close();
                }
            }
            throw th12;
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
}
