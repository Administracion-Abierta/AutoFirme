package es.gob.afirma.standalone.configurator.common;

import es.gob.afirma.core.misc.LoggerUtil;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/common/ConfiguratorUtil.class */
public final class ConfiguratorUtil {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    public static final String CERT_ALIAS = "SocketAutoFirma";

    private ConfiguratorUtil() {
    }

    public static void installFile(byte[] bArr, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        Throwable th = null;
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            Throwable th2 = null;
            try {
                try {
                    bufferedOutputStream.write(bArr);
                    if (bufferedOutputStream != null) {
                        if (0 != 0) {
                            try {
                                bufferedOutputStream.close();
                            } catch (Throwable th3) {
                                th2.addSuppressed(th3);
                            }
                        } else {
                            bufferedOutputStream.close();
                        }
                    }
                    if (fileOutputStream != null) {
                        if (0 == 0) {
                            fileOutputStream.close();
                            return;
                        }
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    }
                } catch (Throwable th5) {
                    th2 = th5;
                    throw th5;
                }
            } catch (Throwable th6) {
                if (bufferedOutputStream != null) {
                    if (th2 != null) {
                        try {
                            bufferedOutputStream.close();
                        } catch (Throwable th7) {
                            th2.addSuppressed(th7);
                        }
                    } else {
                        bufferedOutputStream.close();
                    }
                }
                throw th6;
            }
        } catch (Throwable th8) {
            if (fileOutputStream != null) {
                if (0 != 0) {
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
    }

    public static void uncompressResource(String str, File file) throws IOException {
        byte[] bArr = new byte[1024];
        ZipInputStream zipInputStream = new ZipInputStream(ConfiguratorUtil.class.getResourceAsStream(str));
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
                        if (!canonicalFile.exists()) {
                            canonicalFile.mkdirs();
                        }
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
                                    if (fileOutputStream != null) {
                                        if (th2 != null) {
                                            try {
                                                fileOutputStream.close();
                                            } catch (Throwable th4) {
                                                th2.addSuppressed(th4);
                                            }
                                        } else {
                                            fileOutputStream.close();
                                        }
                                    }
                                    throw th3;
                                }
                            } catch (Throwable th5) {
                                th2 = th5;
                                throw th5;
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

    public static void deleteDir(File file) {
        try {
            Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() { // from class: es.gob.afirma.standalone.configurator.common.ConfiguratorUtil.1
                @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
                public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                    Files.delete(path);
                    return FileVisitResult.CONTINUE;
                }

                @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
                public FileVisitResult postVisitDirectory(Path path, IOException iOException) throws IOException {
                    if (iOException != null) {
                        throw iOException;
                    }
                    Files.delete(path);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception e) {
            LOGGER.warning("No se pudo borrar el directorio '" + LoggerUtil.getCleanUserHomePath(file.getAbsolutePath()) + "': " + e);
        }
    }

    public static void printScript(String[] strArr, StringBuilder sb) {
        for (String str : strArr) {
            sb.append(str);
            sb.append(' ');
        }
        sb.append("\n");
    }

    public static void writeScript(StringBuilder sb, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
        Throwable th = null;
        try {
            fileOutputStream.write(sb.toString().getBytes());
            if (fileOutputStream != null) {
                if (0 != 0) {
                    try {
                        fileOutputStream.close();
                        return;
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                        return;
                    }
                }
                fileOutputStream.close();
            }
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

    public static File getApplicationDirectory() {
        try {
            return new File(ConfiguratorUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        } catch (Exception e) {
            LOGGER.warning("No se pudo localizar el directorio del fichero en ejecucion: " + e);
            return null;
        }
    }
}
