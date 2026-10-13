package es.gob.afirma.keystores.mozilla;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.BoundedBufferedReader;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.keystores.KeyStoreUtilities;
import es.gob.afirma.keystores.mozilla.shared.SharedNssUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.security.Provider;
import java.security.Security;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/mozilla/MozillaKeyStoreUtilities.class */
public final class MozillaKeyStoreUtilities {
    private static final String SUN_PKCS11_PROVIDER_CLASSNAME = "sun.security.pkcs11.SunPKCS11";
    private static final String LIB_NSPR4_SO = "/lib/libnspr4.so";
    private static final String SOFTOKN3_SO = "libsoftokn3.so";
    private static final String SOFTOKN3_DLL = "softokn3.dll";
    private static final String PKCS11TXT_FILENAME = "pkcs11.txt";
    private static final String AFIRMA_NSS_HOME_ENV = "AFIRMA_NSS_HOME_ENV";
    private static final String AFIRMA_NSS_PROFILES_INI = "AFIRMA_NSS_PROFILES_INI";
    private static final String USE_ENV_VARS = "es.gob.afirma.keystores.mozilla.UseEnvironmentVariables";
    public static final String ENABLE_NSS_WRITE = "es.gob.afirma.keystores.mozilla.EnableNssWrite";
    private static final String[] DNI_P11_NAMES = {"libopensc-dnie.dylib", "libpkcs11-dnie.so", "usrpkcs11.dll", "dnie_p11_priv.dll", "dnie_p11_pub.dll", "opensc-pkcs11.dll", "DNIE_P11.dll", "TIF_P11.dll"};
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static String nssLibDir = null;

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/mozilla/MozillaKeyStoreUtilities$KnownModule.class */
    private enum KnownModule {
        ATOS_CARDOS("Atos CardOS (preinstalado)", "siecap11.dll", Platform.OS.WINDOWS, false),
        FNMT_64("FNMT-RCM Modulo PKCS#11 64bits", "FNMT_P11_x64.dll", Platform.OS.WINDOWS, true),
        FNMT_32("FNMT-RCM Modulo PKCS#11 32bits", "FNMT_P11.dll", Platform.OS.WINDOWS, true);

        private String description;
        private String lib;
        private Platform.OS os;
        private boolean forcedLoad;

        KnownModule(String str, String str2, Platform.OS os, boolean z) {
            this.description = str;
            this.lib = str2;
            this.forcedLoad = z;
            this.os = os;
        }

        String getDescription() {
            return this.description;
        }

        String getLib() {
            return this.lib;
        }

        boolean isForcedLoad() {
            return this.forcedLoad;
        }

        public Platform.OS getOs() {
            return this.os;
        }
    }

    private MozillaKeyStoreUtilities() {
    }

    public static String createPKCS11NSSConfig(String str, String str2) {
        String str3;
        if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
            str3 = SOFTOKN3_DLL;
        } else if (Platform.OS.MACOSX.equals(Platform.getOS())) {
            str3 = "libsoftokn3.dylib";
        } else {
            str3 = SOFTOKN3_SO;
        }
        StringBuilder sb = new StringBuilder("name=NSSCrypto-AFirma\r\n");
        sb.append("library=").append(str2).append(File.separator).append(str3).append("\n").append("attributes=compatibility\n").append("slot=2\n").append("showInfo=false\n").append("allowSingleThreadedModules=true\n").append("nssArgs=\"").append("configdir='").append(str).append("' ").append("certPrefix='' ").append("keyPrefix='' ").append(Boolean.getBoolean(ENABLE_NSS_WRITE) ? "" : "flags='readOnly'").append(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
        return sb.toString();
    }

    /* JADX WARN: Code duplicated, block: B:29:0x00a1  */
    /* JADX WARN: Code duplicated, block: B:34:0x00ba A[Catch: Throwable -> 0x00e3, all -> 0x00eb, TryCatch #5 {, blocks: (B:7:0x0028, B:8:0x0042, B:12:0x0051, B:14:0x005b, B:19:0x0075, B:22:0x0089, B:21:0x007f, B:25:0x0099, B:31:0x00a6, B:34:0x00ba, B:33:0x00b0, B:36:0x00c1), top: B:75:0x0028, outer: #7 }] */
    /* JADX WARN: Code duplicated, block: B:69:0x00a6 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    static String getNssPathFromCompatibilityFile() throws IOException {
        File file = new File(getMozillaUserProfileDirectory(), "compatibility.ini");
        String strTrim = null;
        if (file.exists() && file.canRead()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            Throwable th = null;
            try {
                BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(new InputStreamReader(fileInputStream), 512, 4096);
                Throwable th2 = null;
                while (true) {
                    try {
                        try {
                            String line = boundedBufferedReader.readLine();
                            if (line == null || strTrim != null) {
                                break;
                            }
                            if (line.startsWith("LastPlatformDir=")) {
                                strTrim = line.replace("LastPlatformDir=", "").trim();
                            }
                        } catch (Throwable th3) {
                            th2 = th3;
                            throw th3;
                        }
                    } catch (Throwable th4) {
                        if (boundedBufferedReader != null) {
                            if (th2 != null) {
                                try {
                                    boundedBufferedReader.close();
                                } catch (Throwable th5) {
                                    th2.addSuppressed(th5);
                                }
                            } else {
                                boundedBufferedReader.close();
                            }
                        }
                        throw th4;
                    }
                    if (boundedBufferedReader != null) {
                        if (th2 != null) {
                            boundedBufferedReader.close();
                        } else {
                            boundedBufferedReader.close();
                        }
                    }
                    throw th4;
                }
                if (boundedBufferedReader != null) {
                    if (0 != 0) {
                        try {
                            boundedBufferedReader.close();
                        } catch (Throwable th6) {
                            th2.addSuppressed(th6);
                        }
                    } else {
                        boundedBufferedReader.close();
                    }
                }
                if (fileInputStream != null) {
                    if (0 != 0) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th7) {
                            th.addSuppressed(th7);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
            } catch (Throwable th8) {
                if (fileInputStream != null) {
                    if (0 != 0) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th9) {
                            th.addSuppressed(th9);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
                throw th8;
            }
        }
        if (strTrim == null) {
            throw new FileNotFoundException("No se ha podido determinar el directorio de NSS en Windows a partir de 'compatibility.ini' de Firefox");
        }
        return strTrim;
    }

    public static String getSystemNSSLibDir() throws IOException {
        if (nssLibDir != null) {
            return nssLibDir;
        }
        if (Boolean.getBoolean(USE_ENV_VARS)) {
            try {
                nssLibDir = System.getenv(AFIRMA_NSS_HOME_ENV);
            } catch (Exception e) {
                LOGGER.warning("No se tiene acceso a la variable de entorno 'AFIRMA_NSS_HOME_ENV': " + e);
            }
            if (nssLibDir != null) {
                File file = new File(nssLibDir);
                if (file.isDirectory() && file.canRead()) {
                    LOGGER.info("Directorio de NSS determinado a partir de la variable de entorno 'AFIRMA_NSS_HOME_ENV'");
                } else {
                    LOGGER.warning("La variable de entorno 'AFIRMA_NSS_HOME_ENV' apunta a un directorio que no existe o sobre el que no se tienen permisos de lectura, se ignorara");
                    nssLibDir = null;
                }
            }
        }
        if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
            nssLibDir = MozillaKeyStoreUtilitiesWindows.getSystemNSSLibDirWindows();
        } else if (Platform.OS.LINUX.equals(Platform.getOS()) || Platform.OS.SOLARIS.equals(Platform.getOS())) {
            nssLibDir = MozillaKeyStoreUtilitiesUnix.getNSSLibDirUnix();
        } else if (Platform.OS.MACOSX.equals(Platform.getOS())) {
            nssLibDir = MozillaKeyStoreUtilitiesOsX.getSystemNSSLibDirMacOsX();
        }
        if (nssLibDir == null) {
            throw new FileNotFoundException("No se han encontrado bibliotecas NSS instaladas en su sistema operativo");
        }
        return nssLibDir;
    }

    static Map<String, String> getMozillaPKCS11Modules(boolean z, boolean z2) {
        if (!z) {
            LOGGER.info("Se incluiran los modulos nativos de DNIe/CERES si se encuentran configurados");
        } else {
            LOGGER.info("Se excluiran los modulos nativos de DNIe/CERES en favor del controlador 100% Java");
        }
        try {
            String mozillaUserProfileDirectory = getMozillaUserProfileDirectory();
            File file = new File(mozillaUserProfileDirectory, PKCS11TXT_FILENAME);
            if ("sql".equals(System.getenv("NSS_DEFAULT_DB_TYPE")) || file.exists()) {
                try {
                    List<AOSecMod.ModuleName> modules = Pkcs11Txt.getModules(file);
                    LOGGER.info("Obtenidos los modulos externos de Mozilla desde 'pkcs11.txt'");
                    return getPkcs11ModulesFromModuleNames(modules, z2, z);
                } catch (IOException e) {
                    LOGGER.severe("No se han podido obtener los modulos externos de Mozilla desde 'pkcs11.txt': " + e);
                }
            }
            try {
                List<AOSecMod.ModuleName> modules2 = AOSecMod.getModules(mozillaUserProfileDirectory);
                LOGGER.info("Obtenidos los modulos externos de Mozilla desde 'secmod.db'");
                return getPkcs11ModulesFromModuleNames(modules2, z2, z);
            } catch (Exception e2) {
                LOGGER.severe("No se han podido obtener los modulos externos de Mozilla desde 'secmod.db': " + e2);
                return new ConcurrentHashMap(0);
            }
        } catch (IOException e3) {
            LOGGER.severe("No se ha podido obtener el directorio de perfil de Mozilla para leer la lista de modulos PKCS#11: " + e3);
            return new ConcurrentHashMap(0);
        }
    }

    public static Map<String, String> getPkcs11ModulesFromModuleNames(List<AOSecMod.ModuleName> list, boolean z, boolean z2) {
        if (list == null) {
            return new ConcurrentHashMap(0);
        }
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        for (AOSecMod.ModuleName moduleName : list) {
            String lib = moduleName.getLib();
            if (!z2 || !isDniePkcs11Library(lib)) {
                concurrentHashMap.put(moduleName.getDescription(), lib);
            }
        }
        ConcurrentHashMap concurrentHashMap2 = new ConcurrentHashMap(concurrentHashMap);
        if (z) {
            for (KnownModule knownModule : KnownModule.values()) {
                if (Platform.getOS().equals(knownModule.getOs()) && ((!z2 || !isDniePkcs11Library(knownModule.getLib())) && !isModuleIncluded(concurrentHashMap2, knownModule.getLib()))) {
                    String str = getWindowsSystemDirWithFinalSlash() + knownModule.getLib();
                    if (knownModule.isForcedLoad() || new File(str).exists()) {
                        concurrentHashMap.put(knownModule.getDescription(), str);
                    }
                }
            }
        }
        return purgeStoresTable(concurrentHashMap);
    }

    public static void loadNSSDependencies(String str) {
        String[] softkn3Dependencies;
        if (Platform.OS.LINUX.equals(Platform.getOS()) && "32".equals(Platform.getJavaArch()) && new File("/usr/lib/libsoftokn3.so").exists() && new File(LIB_NSPR4_SO).exists()) {
            softkn3Dependencies = new String[]{"/lib/libmozglue.so", "/usr/lib/libmozglue.so", LIB_NSPR4_SO, "/lib/libplds4.so", "/usr/lib/libplds4.so", "/lib/libplc4.so", "/usr/lib/libplc4.so", "/lib/libnssutil3.so", "/usr/lib/libnssutil3.so", "/lib/libsqlite3.so", "/usr/lib/libsqlite3.so", "/lib/libmozsqlite3.so", "/usr/lib/libmozsqlite3.so"};
            LOGGER.info("Detectada configuracion de NSS mixta entre '/usr/lib' y '/lib'");
        } else {
            softkn3Dependencies = getSoftkn3Dependencies(str + (str.endsWith(File.separator) ? "" : File.separator));
        }
        for (String str2 : softkn3Dependencies) {
            if (new File(str2).exists()) {
                try {
                    System.load(str2);
                } catch (Error | Exception e) {
                    LOGGER.log(Level.WARNING, "Error al cargar la biblioteca " + LoggerUtil.getCleanUserHomePath(str2) + " para el acceso al almacen de claves de Mozilla: " + e, e);
                }
            }
        }
    }

    private static String[] getSoftkn3Dependencies(String str) {
        if (str == null) {
            return new String[0];
        }
        if (Platform.OS.MACOSX.equals(Platform.getOS())) {
            return new String[0];
        }
        String str2 = !str.endsWith(File.separator) ? str + File.separator : str;
        if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
            return MozillaKeyStoreUtilitiesWindows.getSoftkn3DependenciesWindows(str2);
        }
        if (Platform.OS.LINUX.equals(Platform.getOS()) || Platform.OS.SOLARIS.equals(Platform.getOS())) {
            return MozillaKeyStoreUtilitiesUnix.getSoftkn3DependenciesUnix(str2);
        }
        LOGGER.warning("Plataforma no soportada para la precarga de las bibliotecas NSS: " + Platform.getOS());
        return new String[0];
    }

    private static String getProfilesIniPath() {
        String strReplace = null;
        if (Boolean.getBoolean(USE_ENV_VARS) || Boolean.parseBoolean(System.getenv(USE_ENV_VARS))) {
            try {
                strReplace = System.getenv(AFIRMA_NSS_PROFILES_INI);
                if (strReplace == null) {
                    strReplace = System.getProperty(AFIRMA_NSS_PROFILES_INI);
                }
            } catch (Exception e) {
                LOGGER.warning("No se tiene acceso a la variable de entorno 'AFIRMA_NSS_PROFILES_INI': " + e);
            }
            if (strReplace != null) {
                int iIndexOf = strReplace.toUpperCase().indexOf("%APPDATA%");
                if (iIndexOf != -1) {
                    strReplace = strReplace.replace(strReplace.substring(iIndexOf, iIndexOf + "%APPDATA%".length()), MozillaKeyStoreUtilitiesWindows.getWindowsAppDataDir());
                }
                File file = new File(strReplace);
                if (file.isFile() && file.canRead()) {
                    LOGGER.info("Fichero de perfiles de Firefox determinado a partir de la variable de entorno 'AFIRMA_NSS_PROFILES_INI'");
                    return strReplace;
                }
                LOGGER.warning("La variable de entorno 'AFIRMA_NSS_PROFILES_INI' apunta a un fichero que no existe o sobre el que no se tienen permisos de lectura, se ignorara: " + strReplace);
            }
        }
        if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
            return MozillaKeyStoreUtilitiesWindows.getWindowsAppDataDir() + "\\Mozilla\\Firefox\\profiles.ini";
        }
        if (Platform.OS.MACOSX.equals(Platform.getOS())) {
            return Platform.getUserHome() + "/Library/Application Support/Firefox/profiles.ini";
        }
        if (new File(Platform.getUserHome() + "/snap/firefox/common/.mozilla/firefox/profiles.ini").isFile()) {
            return Platform.getUserHome() + "/snap/firefox/common/.mozilla/firefox/profiles.ini";
        }
        return Platform.getUserHome() + "/.mozilla/firefox/profiles.ini";
    }

    public static String getMozillaUserProfileDirectory() throws IOException {
        if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
            return getMozillaUserProfileDirectoryWindows(getProfilesIniPath());
        }
        try {
            return NSPreferences.getFireFoxUserProfileDirectory(new File(getProfilesIniPath()));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "No ha podido determinarse el perfil de usuario de Mozilla, se intentara usar el global: " + e, (Throwable) e);
            return SharedNssUtil.getSharedUserProfileDirectory();
        }
    }

    public static String getMozillaUserProfileDirectoryWindows(String str) throws IOException {
        String fireFoxUserProfileDirectory = NSPreferences.getFireFoxUserProfileDirectory(new File(str));
        if (fireFoxUserProfileDirectory == null) {
            throw new IOException("No se ha encontrado el directorio de perfil de Mozilla");
        }
        return MozillaKeyStoreUtilitiesWindows.cleanMozillaUserProfileDirectoryWindows(fireFoxUserProfileDirectory);
    }

    private static Provider loadNssJava9(String str, String str2) throws IOException, AOException {
        Provider provider;
        Provider provider2 = Security.getProvider("SunPKCS11");
        File fileCreateTempFile = File.createTempFile("pkcs11_nss_", ".cfg");
        FileOutputStream fileOutputStream = new FileOutputStream(fileCreateTempFile);
        Throwable th = null;
        try {
            try {
                fileOutputStream.write(str2.getBytes());
                if (fileOutputStream != null) {
                    if (0 != 0) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        fileOutputStream.close();
                    }
                }
                try {
                    provider = (Provider) Provider.class.getMethod("configure", String.class).invoke(provider2, fileCreateTempFile.getAbsolutePath());
                } catch (Error | Exception e) {
                    LOGGER.warning("NSS no se ha podido iniciar sin precargar sus dependencias: " + e);
                    if (Platform.OS.MACOSX.equals(Platform.getOS())) {
                        MozillaKeyStoreUtilitiesOsX.configureMacNSS(str);
                    } else {
                        loadNSSDependencies(str);
                    }
                    try {
                        provider = (Provider) Provider.class.getMethod("configure", String.class).invoke(provider2, fileCreateTempFile.getAbsolutePath());
                    } catch (Exception e2) {
                        LOGGER.info("No se ha podido cargar NSS en modo SQLite (con prefijo 'sql:/'), se intentara en modo Berkeley (sin prefijo 'sql:/'): " + e2);
                        FileOutputStream fileOutputStream2 = new FileOutputStream(fileCreateTempFile);
                        Throwable th3 = null;
                        try {
                            try {
                                fileOutputStream2.write(str2.replace("sql:/", "").getBytes());
                                if (fileOutputStream2 != null) {
                                    if (0 != 0) {
                                        try {
                                            fileOutputStream2.close();
                                        } catch (Throwable th4) {
                                            th3.addSuppressed(th4);
                                        }
                                    } else {
                                        fileOutputStream2.close();
                                    }
                                }
                                try {
                                    provider = (Provider) Provider.class.getMethod("configure", String.class).invoke(provider2, fileCreateTempFile.getAbsolutePath());
                                } catch (Exception e3) {
                                    throw new AOException("Ocurrio un error al configurar el proveedor de acceso a NSS", e3);
                                }
                            } catch (Throwable th5) {
                                th3 = th5;
                                throw th5;
                            }
                        } catch (Throwable th6) {
                            if (fileOutputStream2 != null) {
                                if (th3 != null) {
                                    try {
                                        fileOutputStream2.close();
                                    } catch (Throwable th7) {
                                        th3.addSuppressed(th7);
                                    }
                                } else {
                                    fileOutputStream2.close();
                                }
                            }
                            throw th6;
                        }
                    }
                }
                if (!fileCreateTempFile.delete()) {
                    LOGGER.warning("No se ha podido eliminar el fichero '" + LoggerUtil.getCleanUserHomePath(fileCreateTempFile.getAbsolutePath()) + OperatorName.SHOW_TEXT_LINE);
                }
                return provider;
            } catch (Throwable th8) {
                th = th8;
                throw th8;
            }
        } catch (Throwable th9) {
            if (fileOutputStream != null) {
                if (th != null) {
                    try {
                        fileOutputStream.close();
                    } catch (Throwable th10) {
                        th.addSuppressed(th10);
                    }
                } else {
                    fileOutputStream.close();
                }
            }
            throw th9;
        }
    }

    private static Provider loadNssJava8(String str, String str2) throws IllegalAccessException, NoSuchMethodException, InstantiationException, SecurityException, ClassNotFoundException, IllegalArgumentException, AOException, InvocationTargetException {
        try {
            return (Provider) Class.forName(SUN_PKCS11_PROVIDER_CLASSNAME).getConstructor(InputStream.class).newInstance(new ByteArrayInputStream(str2.getBytes()));
        } catch (Exception e) {
            LOGGER.info("NSS necesita una precarga o tratamiento de sus dependencias: " + e);
            if (Platform.OS.MACOSX.equals(Platform.getOS())) {
                MozillaKeyStoreUtilitiesOsX.configureMacNSS(str);
            } else {
                loadNSSDependencies(str);
            }
            try {
                return (Provider) Class.forName(SUN_PKCS11_PROVIDER_CLASSNAME).getConstructor(InputStream.class).newInstance(new ByteArrayInputStream(str2.getBytes()));
            } catch (Exception e2) {
                LOGGER.warning("Ha fallado el segundo intento de carga de NSS: " + e2);
                return (Provider) Class.forName(SUN_PKCS11_PROVIDER_CLASSNAME).getConstructor(InputStream.class).newInstance(new ByteArrayInputStream(str2.replace("sql:/", "").getBytes()));
            }
        }
    }

    static Provider loadNSS(boolean z) throws IllegalAccessException, NoSuchMethodException, InstantiationException, IOException, ClassNotFoundException, SecurityException, IllegalArgumentException, AOException, InvocationTargetException {
        String mozillaUserProfileDirectory;
        Provider providerLoadNssJava8;
        String systemNSSLibDir = getSystemNSSLibDir();
        LOGGER.info("Directorio de bibliotecas NSS: " + systemNSSLibDir);
        if (z) {
            mozillaUserProfileDirectory = SharedNssUtil.getSharedUserProfileDirectory();
        } else {
            mozillaUserProfileDirectory = getMozillaUserProfileDirectory();
        }
        String str = mozillaUserProfileDirectory;
        try {
            if ("sql".equals(System.getenv("NSS_DEFAULT_DB_TYPE")) || new File(str, PKCS11TXT_FILENAME).exists()) {
                str = "sql:/" + str;
            }
        } catch (Exception e) {
            LOGGER.warning("No se pudo comprobar si el almacen de claves debia cargase como base de datos: " + e);
        }
        String strCreatePKCS11NSSConfig = createPKCS11NSSConfig(str, systemNSSLibDir);
        LOGGER.info("Configuracion de NSS para SunPKCS11:\n" + strCreatePKCS11NSSConfig.replace(Platform.getUserHome(), "USERHOME"));
        if (AOUtil.isJava9orNewer()) {
            providerLoadNssJava8 = loadNssJava9(systemNSSLibDir, strCreatePKCS11NSSConfig);
        } else {
            providerLoadNssJava8 = loadNssJava8(systemNSSLibDir, strCreatePKCS11NSSConfig);
        }
        Provider provider = providerLoadNssJava8;
        Security.addProvider(provider);
        LOGGER.info("Anadido proveedor PKCS#11 de NSS " + (z ? "del sistema" : "de Mozilla") + ": " + provider.getName());
        return provider;
    }

    private static boolean isDniePkcs11Library(String str) {
        if (str == null || Boolean.getBoolean(KeyStoreUtilities.DISABLE_DNIE_NATIVE_DRIVER)) {
            return false;
        }
        for (String str2 : DNI_P11_NAMES) {
            if (str.toLowerCase().endsWith(str2.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private static String getWindowsSystemDirWithFinalSlash() {
        if (!Platform.OS.WINDOWS.equals(Platform.getOS())) {
            return "";
        }
        return Platform.getSystemLibDir() + "\\";
    }

    private static boolean isModuleIncluded(Map<String, String> map, String str) {
        if (map == null || str == null) {
            throw new IllegalArgumentException("Ni la lista de almacenes ni el modulo a comprobar pueden ser nulos");
        }
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            if (map.get(it.next()).toLowerCase().endsWith(str.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private static Map<String, String> purgeStoresTable(Map<String, String> map) {
        if (map == null) {
            return new ConcurrentHashMap(0);
        }
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        HashSet hashSet = new HashSet();
        for (String str : (String[]) map.keySet().toArray(new String[0])) {
            String lowerCase = map.get(str);
            if (lowerCase.toLowerCase().endsWith(".dll")) {
                lowerCase = lowerCase.toLowerCase();
            }
            if (!hashSet.contains(lowerCase) && !lowerCase.toLowerCase().contains("nssckbi")) {
                concurrentHashMap.put(str, map.get(str));
                hashSet.add(lowerCase);
            } else {
                LOGGER.warning("Se eliminara el modulo '" + str + "' porque ya existe uno con la misma biblioteca o es un modulo de certificados raiz: " + map.get(str));
            }
        }
        return concurrentHashMap;
    }
}
