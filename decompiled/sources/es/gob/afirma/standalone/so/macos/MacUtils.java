package es.gob.afirma.standalone.so.macos;

import es.gob.afirma.core.misc.Platform;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/so/macos/MacUtils.class */
public class MacUtils {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String SCRIPT_NAME = "afirma";
    private static final String SCRIPT_EXT = ".sh";

    public static File newScriptFile() throws IOException {
        return File.createTempFile(SCRIPT_NAME, SCRIPT_EXT);
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x0069 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void writeScriptFile(java.lang.StringBuilder r5, java.io.File r6, boolean r7) throws java.io.IOException {
        /*
            java.util.logging.Logger r0 = es.gob.afirma.standalone.so.macos.MacUtils.LOGGER
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
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.standalone.so.macos.MacUtils.writeScriptFile(java.lang.StringBuilder, java.io.File, boolean):void");
    }

    public static String executeScriptFile(File file, boolean z, boolean z2) throws InterruptedException, IOException {
        String strRun;
        ShellScript shellScript = new ShellScript(file, z2);
        try {
            if (z) {
                LOGGER.info("Se ejecuta con permisos de administrador el script: " + file);
                strRun = shellScript.runAsAdministrator();
            } else {
                LOGGER.info("Se ejecuta el script: " + file);
                strRun = shellScript.run();
            }
            return strRun;
        } catch (IOException e) {
            throw new IOException("Error en la ejecucion del script", e);
        }
    }

    public static void closeMacService(String str) {
        if (str == null) {
            LOGGER.warning("No se ha proporcionado el id del proceso que hay que cerrar");
            return;
        }
        LOGGER.warning("Ejecuto kill");
        try {
            new ShellScript("kill -9 $(ps -ef | grep " + str + " | awk '{print $2}')").run();
        } catch (Exception e) {
            LOGGER.warning("No se ha podido cerrar la aplicacion: " + e);
        }
    }

    public static void focusApplication() {
        if (Platform.OS.MACOSX.equals(Platform.getOS())) {
            try {
                new AppleScript("tell me to activate").run();
            } catch (Exception e) {
                LOGGER.warning("Fallo cogiendo el foco en macOS: " + e);
            }
        }
    }

    public static File getMacApp(File file) {
        File parentFile;
        File parentFile2;
        File parentFile3 = file.getParentFile();
        if (parentFile3 != null && "JAR".equals(parentFile3.getName()) && (parentFile = parentFile3.getParentFile()) != null && "Resources".equals(parentFile.getName()) && (parentFile2 = parentFile.getParentFile()) != null && "Contents".equals(parentFile2.getName())) {
            File file2 = new File(parentFile2, "MacOS");
            if (file2.isDirectory()) {
                File file3 = new File(file2, file.getName().substring(0, file.getName().length() - ".jar".length()));
                if (file3.isFile()) {
                    return file3;
                }
                return null;
            }
            return null;
        }
        return null;
    }
}
