package es.gob.afirma.keystores.mozilla;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.keystores.mozilla.apple.ShellScript;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/mozilla/MozillaKeyStoreUtilitiesOsX.class */
public final class MozillaKeyStoreUtilitiesOsX {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String LIBRARY_FOLDER = "/usr/local/lib";

    private MozillaKeyStoreUtilitiesOsX() {
    }

    /* JADX WARN: Code duplicated, block: B:22:0x00fe  */
    /* JADX WARN: Code duplicated, block: B:26:0x0121  */
    /* JADX WARN: Code duplicated, block: B:28:0x0138  */
    /* JADX WARN: Code duplicated, block: B:48:0x0170 A[SYNTHETIC] */
    public static void configureMacNSS(String str) throws AOException {
        String[] strArr;
        StringBuilder sb;
        int i;
        if (!Platform.OS.MACOSX.equals(Platform.getOS())) {
            return;
        }
        if (str == null) {
            LOGGER.severe("El directorio de NSS para configurar proporcionado es nulo, no se realizara ninguna accion");
            return;
        }
        String str2 = str.endsWith(File.separator) ? str : str + File.separator;
        try {
            System.load(str2 + "libmozglue.dylib");
            System.load(str2 + "libnss3.dylib");
            System.load(str2 + "libsoftokn3.dylib");
        } catch (Exception e) {
            LOGGER.warning("No se puede realizar una carga directa de NSS, se crearan enlaces simbolicos: " + e);
            strArr = new String[]{"libmozglue.dylib", "libmozutils.dylib", "libnspr4.dylib", "libplds4.dylib", "libplc4.dylib", "libnssutil3.dylib", "libmozsqlite3.dylib", "libnss3.dylib"};
            sb = new StringBuilder();
            if (!new File(LIBRARY_FOLDER).exists()) {
                sb.append("mkdir ").append(LIBRARY_FOLDER).append("; ");
            }
            for (String str3 : strArr) {
                if (new File(str2, str3).exists()) {
                    sb.append("ln -s ");
                    sb.append(str2);
                    sb.append(str3);
                    sb.append(" ");
                    sb.append(LIBRARY_FOLDER);
                    sb.append(File.separator);
                    sb.append(str3);
                    sb.append("; ");
                }
            }
            try {
                new ShellScript(sb.toString()).runAsAdministrator();
            } catch (Exception e2) {
                LOGGER.log(Level.SEVERE, "No se han podido crear los enlaces simbolicos para NSS: " + e2, (Throwable) e2);
                LOGGER.severe("Fallo en el script: " + sb.toString());
            }
            try {
                System.load(str2 + "libsoftokn3.dylib");
            } catch (Exception e3) {
                throw new AOException("La configuracion de NSS para Mac OS X ha fallado por motivos de seguridad: " + e3);
            } catch (UnsatisfiedLinkError e4) {
                throw new AOException("La configuracion de NSS para Mac OS X ha fallado: " + e4);
            }
        } catch (UnsatisfiedLinkError e5) {
            LOGGER.warning("No se puede realizar una carga directa de NSS, se crearan enlaces simbolicos: " + e5);
            strArr = new String[]{"libmozglue.dylib", "libmozutils.dylib", "libnspr4.dylib", "libplds4.dylib", "libplc4.dylib", "libnssutil3.dylib", "libmozsqlite3.dylib", "libnss3.dylib"};
            sb = new StringBuilder();
            if (!new File(LIBRARY_FOLDER).exists()) {
                sb.append("mkdir ").append(LIBRARY_FOLDER).append("; ");
            }
            while (i < r0) {
                if (new File(str2, str3).exists()) {
                    sb.append("ln -s ");
                    sb.append(str2);
                    sb.append(str3);
                    sb.append(" ");
                    sb.append(LIBRARY_FOLDER);
                    sb.append(File.separator);
                    sb.append(str3);
                    sb.append("; ");
                }
            }
            new ShellScript(sb.toString()).runAsAdministrator();
            System.load(str2 + "libsoftokn3.dylib");
        }
    }

    static String getSystemNSSLibDirMacOsX() throws FileNotFoundException {
        String str = null;
        for (String str2 : new String[]{"/Applications/Firefox.app/Contents/MacOS", "/lib", "/usr/lib", "/usr/lib/nss", "/Applications/Minefield.app/Contents/MacOS"}) {
            if (new File(str2, "libsoftokn3.dylib").exists()) {
                str = str2;
                break;
            }
        }
        if (str == null) {
            throw new FileNotFoundException("No se ha podido determinar la localizacion de NSS en Mac OS X");
        }
        return str;
    }
}
