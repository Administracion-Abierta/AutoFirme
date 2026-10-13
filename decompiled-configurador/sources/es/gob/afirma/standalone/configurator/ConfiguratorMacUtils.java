package es.gob.afirma.standalone.configurator;

import es.gob.afirma.core.misc.LoggerUtil;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/ConfiguratorMacUtils.class */
public class ConfiguratorMacUtils {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    /* JADX WARN: Bottom block not found for handler: all -> 0x0069 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static void writeScriptFile(java.lang.StringBuilder r5, java.io.File r6, boolean r7) throws java.io.IOException {
        /*
            java.util.logging.Logger r0 = es.gob.afirma.standalone.configurator.ConfiguratorMacUtils.LOGGER
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r2 = r1
            r2.<init>()
            java.lang.String r2 = "Se escribira en fichero el siguiente comando:\n"
            java.lang.StringBuilder r1 = r1.append(r2)
            r2 = r5
            java.lang.String r2 = r2.toString()
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.info(r1)
            java.io.FileOutputStream r0 = new java.io.FileOutputStream
            r1 = r0
            r2 = r6
            r3 = r7
            r1.<init>(r2, r3)
            r8 = r0
            r0 = 0
            r9 = r0
            r0 = r8
            r1 = r5
            java.lang.String r1 = r1.toString()     // Catch: java.lang.Throwable -> L60 java.lang.Throwable -> L69
            byte[] r1 = r1.getBytes()     // Catch: java.lang.Throwable -> L60 java.lang.Throwable -> L69
            r0.write(r1)     // Catch: java.lang.Throwable -> L60 java.lang.Throwable -> L69
            r0 = r8
            java.lang.String r1 = "\n"
            byte[] r1 = r1.getBytes()     // Catch: java.lang.Throwable -> L60 java.lang.Throwable -> L69
            r0.write(r1)     // Catch: java.lang.Throwable -> L60 java.lang.Throwable -> L69
            r0 = r8
            if (r0 == 0) goto L8e
            r0 = r9
            if (r0 == 0) goto L59
            r0 = r8
            r0.close()     // Catch: java.lang.Throwable -> L4d
            goto L8e
        L4d:
            r10 = move-exception
            r0 = r9
            r1 = r10
            r0.addSuppressed(r1)
            goto L8e
        L59:
            r0 = r8
            r0.close()
            goto L8e
        L60:
            r10 = move-exception
            r0 = r10
            r9 = r0
            r0 = r10
            throw r0     // Catch: java.lang.Throwable -> L69
        L69:
            r11 = move-exception
            r0 = r8
            if (r0 == 0) goto L8b
            r0 = r9
            if (r0 == 0) goto L87
            r0 = r8
            r0.close()     // Catch: java.lang.Throwable -> L7b
            goto L8b
        L7b:
            r12 = move-exception
            r0 = r9
            r1 = r12
            r0.addSuppressed(r1)
            goto L8b
        L87:
            r0 = r8
            r0.close()
        L8b:
            r0 = r11
            throw r0
        L8e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.standalone.configurator.ConfiguratorMacUtils.writeScriptFile(java.lang.StringBuilder, java.io.File, boolean):void");
    }

    public static void addExexPermissionsToAllFilesOnDirectory(File file) {
        for (File file2 : file.listFiles()) {
            addExexPermissionsToFile(file2);
        }
    }

    public static void addExexPermissionsToFile(File file) {
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
