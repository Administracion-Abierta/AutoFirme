package es.gob.afirma.keystores.mozilla;

import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.keystores.mozilla.bintutil.ElfParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/mozilla/MozillaKeyStoreUtilitiesUnix.class */
final class MozillaKeyStoreUtilitiesUnix {
    private static final String SOFTOKN3_SO = "libsoftokn3.so";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String[] NSS_PATHS = getNssPaths();
    private static final String[] SQLITE_LIBS = {"mozsqlite3.so", "libmozsqlite3.so", "libsqlite3.so.0", "libnspr4.so"};

    private MozillaKeyStoreUtilitiesUnix() {
    }

    private static String[] getNssPaths() {
        ArrayList arrayList = new ArrayList();
        String javaArch = Platform.getJavaArch();
        String systemLibDir = Platform.getSystemLibDir();
        if ("64".equals(javaArch)) {
            arrayList.add("/usr/lib/x86_64-linux-gnu/nss");
            arrayList.add("/usr/lib/x86_64-linux-gnu");
        } else if ("32".equals(javaArch)) {
            arrayList.add("/usr/lib/i386-linux-gnu/nss");
            arrayList.add("/usr/lib/i386-linux-gnu");
        }
        arrayList.add(systemLibDir + "/nss");
        arrayList.add(systemLibDir);
        arrayList.add(systemLibDir + "/firefox");
        String strSearchLastFirefoxVersion = searchLastFirefoxVersion(systemLibDir);
        if (strSearchLastFirefoxVersion != null) {
            arrayList.add(systemLibDir + "/firefox-" + strSearchLastFirefoxVersion);
        }
        arrayList.add(systemLibDir + "/thunderbird");
        if (isDirectory("/lib" + javaArch)) {
            arrayList.add("/lib" + javaArch);
        } else {
            arrayList.add("/lib");
        }
        arrayList.add("/opt/firefox");
        if (isDirectory("/usr/lib" + javaArch)) {
            arrayList.add("/usr/lib" + javaArch);
        } else {
            arrayList.add("/usr/lib");
        }
        String strSearchLastFirefoxVersion2 = searchLastFirefoxVersion("/opt");
        if (strSearchLastFirefoxVersion2 != null) {
            arrayList.add("/opt/firefox-" + strSearchLastFirefoxVersion2);
        }
        arrayList.add("/opt/fedora-ds/clients/lib");
        arrayList.add("/opt/google/chrome");
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            if (!isDirectory((String) arrayList.get(size))) {
                arrayList.remove(size);
            }
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    static String getNSSLibDirUnix() throws FileNotFoundException {
        String str = null;
        for (String str2 : NSS_PATHS) {
            File file = new File(str2, SOFTOKN3_SO);
            if (file.isFile() && ElfParser.archMatches(file)) {
                str = str2;
                break;
            }
        }
        if (str == null) {
            throw new FileNotFoundException("No se ha podido determinar la localizacion de NSS en UNIX");
        }
        LOGGER.info("Se usara el NSS encontrado en '" + str + OperatorName.SHOW_TEXT_LINE);
        for (String str3 : NSS_PATHS) {
            File file2 = new File(str3);
            for (String str4 : SQLITE_LIBS) {
                File file3 = new File(file2, str4);
                if (file3.exists()) {
                    try {
                        System.load(file3.getAbsolutePath());
                        return str;
                    } catch (Error | Exception e) {
                        LOGGER.warning("Descartamos el NSS situado en '" + LoggerUtil.getCleanUserHomePath(str3) + "' porque no puede cargarse adecuadamente: " + e);
                    }
                }
            }
        }
        throw new FileNotFoundException("No se ha podido determinar la localizacion de NSS en UNIX (sqlite)");
    }

    private static String searchLastFirefoxVersion(String str) {
        Version version = null;
        File file = new File(str);
        if (file.isDirectory()) {
            ArrayList<String> arrayList = new ArrayList();
            for (String str2 : file.list()) {
                if (str2.startsWith("firefox-")) {
                    arrayList.add(str2.replace("firefox-", ""));
                }
            }
            for (String str3 : arrayList) {
                try {
                    Version version2 = new Version(str3);
                    if (version == null || version2.compareTo(version) > 0) {
                        version = version2;
                    }
                } catch (Exception e) {
                    LOGGER.warning("Se encontro un numero de version de Firefox no soportado (" + str3 + "): " + e);
                }
            }
        }
        if (version != null) {
            return version.toString();
        }
        return null;
    }

    static String[] getSoftkn3DependenciesUnix(String str) {
        return new String[]{str + "libnspr4.so", str + "libplds4.so", str + "libplc4.so", str + "libnssutil3.so", str + "libsqlite3.so", str + "libmozsqlite3.so", str + "libsqlite3.so.0"};
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/mozilla/MozillaKeyStoreUtilitiesUnix$Version.class */
    private static class Version implements Comparable<Version> {
        String text;
        int[] versionsParticles;

        public Version(String str) {
            this.text = str;
            String[] strArrSplit = str.split("\\.");
            this.versionsParticles = new int[strArrSplit.length];
            for (int i = 0; i < strArrSplit.length; i++) {
                try {
                    this.versionsParticles[i] = Integer.parseInt(strArrSplit[i]);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Identificador de version no soportado (" + str + "): " + e);
                }
            }
        }

        public int[] getVersionsParticles() {
            if (this.versionsParticles != null) {
                return (int[]) this.versionsParticles.clone();
            }
            return null;
        }

        @Override // java.lang.Comparable
        public int compareTo(Version version) {
            if (version == null) {
                return 1;
            }
            int[] versionsParticles = version.getVersionsParticles();
            int iMax = Math.max(this.versionsParticles.length, versionsParticles.length);
            int i = 0;
            while (i < iMax) {
                int i2 = i < this.versionsParticles.length ? this.versionsParticles[i] : 0;
                int i3 = i < versionsParticles.length ? versionsParticles[i] : 0;
                if (i2 < i3) {
                    return -1;
                }
                if (i2 <= i3) {
                    i++;
                } else {
                    return 1;
                }
            }
            return 0;
        }

        public String toString() {
            return this.text;
        }
    }

    private static boolean isDirectory(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            return Files.isDirectory(new File(str).toPath().toRealPath(new LinkOption[0]), new LinkOption[0]);
        } catch (NoSuchFileException e) {
            return false;
        } catch (IOException e2) {
            LOGGER.warning("No se ha podido comprobar la existencia del directorio '" + LoggerUtil.getCleanUserHomePath(str) + "': " + e2);
            return false;
        }
    }
}
