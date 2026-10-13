package es.gob.afirma.standalone.so.macos;

import es.gob.afirma.core.misc.LoggerUtil;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/so/macos/UnixUtils.class */
public class UnixUtils {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    public static void addExexPermissionsToAllFilesOnDirectory(File file) {
        for (File file2 : file.listFiles()) {
            addAllPermissionsToFile(file2);
        }
    }

    public static void addAllPermissionsToFile(File file) {
        HashSet hashSet = new HashSet();
        hashSet.add(PosixFilePermission.OWNER_EXECUTE);
        hashSet.add(PosixFilePermission.GROUP_EXECUTE);
        hashSet.add(PosixFilePermission.OTHERS_EXECUTE);
        hashSet.add(PosixFilePermission.OWNER_READ);
        hashSet.add(PosixFilePermission.GROUP_READ);
        hashSet.add(PosixFilePermission.OTHERS_READ);
        hashSet.add(PosixFilePermission.OWNER_WRITE);
        hashSet.add(PosixFilePermission.GROUP_WRITE);
        hashSet.add(PosixFilePermission.OTHERS_WRITE);
        try {
            Files.setPosixFilePermissions(Paths.get(file.getAbsolutePath(), new String[0]), hashSet);
        } catch (Exception e) {
            LOGGER.warning("No se ha podido dar permiso de ejecucion a '" + LoggerUtil.getCleanUserHomePath(file.getAbsolutePath()) + "': " + e);
        }
    }
}
