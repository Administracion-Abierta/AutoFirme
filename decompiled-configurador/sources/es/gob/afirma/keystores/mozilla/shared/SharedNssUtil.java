package es.gob.afirma.keystores.mozilla.shared;

import es.gob.afirma.keystores.mozilla.MozillaKeyStoreUtilities;
import es.gob.afirma.keystores.mozilla.Pkcs11Txt;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/mozilla/shared/SharedNssUtil.class */
public final class SharedNssUtil {
    private static final String NSSDB_PATH_UNIX_USER = System.getProperty("user.home") + "/.pki/nssdb";
    private static final String NSSDB_PATH_UNIX_USER_CHROMIUM = System.getProperty("user.home") + "/snap/chromium/current/.pki/nssdb";
    private static final String NSSDB_PATH_UNIX_GLOBAL = "/etc/pki/nssdb";
    private static final String[] NSSDB_PATHS = {NSSDB_PATH_UNIX_USER, NSSDB_PATH_UNIX_USER_CHROMIUM, NSSDB_PATH_UNIX_GLOBAL};
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private SharedNssUtil() {
    }

    private static boolean isNssProfileDirectory(String str) {
        if (str == null) {
            return false;
        }
        File file = new File(str);
        return file.isDirectory() && file.listFiles(new FileFilter() { // from class: es.gob.afirma.keystores.mozilla.shared.SharedNssUtil.1
            @Override // java.io.FileFilter
            public boolean accept(File file2) {
                String name = file2.getName();
                return name.startsWith("key") && name.endsWith(".db");
            }
        }).length > 0;
    }

    public static String getSharedUserProfileDirectory() throws FileNotFoundException {
        for (String str : NSSDB_PATHS) {
            if (isNssProfileDirectory(str)) {
                LOGGER.info("Detectado directorio del almacen NSS de claves: " + str.replace(System.getProperty("user.home"), "̴"));
                return str;
            }
        }
        throw new FileNotFoundException("No se ha encontrado un directorio de perfil de NSS compartido");
    }

    static Map<String, String> getSharedNssPKCS11Modules(boolean z, boolean z2) {
        if (!z) {
            LOGGER.info("Se ha solicitado incluir los modulos nativos de DNIe");
        }
        try {
            return MozillaKeyStoreUtilities.getPkcs11ModulesFromModuleNames(Pkcs11Txt.getModules(), z2, z);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "No se han podido obtener los modulos externos de Mozilla desde 'pkcs11.txt': " + e, (Throwable) e);
            return new ConcurrentHashMap(0);
        }
    }
}
