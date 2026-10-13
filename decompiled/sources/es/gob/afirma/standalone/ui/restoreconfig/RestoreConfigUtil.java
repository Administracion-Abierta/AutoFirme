package es.gob.afirma.standalone.ui.restoreconfig;

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

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/restoreconfig/RestoreConfigUtil.class */
final class RestoreConfigUtil {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    static final String CERT_ALIAS_BROWSER = "Autofirma ROOT";

    private RestoreConfigUtil() {
    }

    static void installFile(byte[] bArr, File file) throws IOException {
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

    static void deleteDir(File file) {
        try {
            Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() { // from class: es.gob.afirma.standalone.ui.restoreconfig.RestoreConfigUtil.1
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

    static File getApplicationDirectory() {
        try {
            return new File(RestoreConfigUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        } catch (Exception e) {
            LOGGER.warning("No se pudo localizar el directorio del fichero en ejecucion: " + e);
            return null;
        }
    }
}
