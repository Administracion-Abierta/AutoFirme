package es.gob.afirma.keystores.mozilla;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.keystores.KeyStoreUtilities;
import es.gob.afirma.keystores.mozilla.bintutil.MsPortableExecutable;
import es.gob.afirma.keystores.mozilla.bintutil.PEParserException;
import es.gob.afirma.keystores.mozilla.bintutil.PeMachineType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/mozilla/MozillaKeyStoreUtilitiesWindows.class */
final class MozillaKeyStoreUtilitiesWindows {
    private static final String P11_CONFIG_VALID_CHARS = ":\\0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_.~";
    private static final String SOFTOKN3_DLL = "softokn3.dll";
    private static final String MSVCR100_DLL = "msvcr100.dll";
    private static final String MSVCP100_DLL = "msvcp100.dll";
    private static final String MSVCR120_DLL = "msvcr120.dll";
    private static final String MSVCP120_DLL = "msvcp120.dll";
    private static final String MSVCR140_DLL = "vcruntime140.dll";
    private static final String MSVCP140_DLL = "msvcp140.dll";
    private static final String PLC4_DLL = "plc4.dll";
    private static final String PLDS4_DLL = "plds4.dll";
    private static final String NSPR4_DLL = "nspr4.dll";
    private static final String MOZSQLITE3_DLL = "mozsqlite3.dll";
    private static final String MOZCRT19_DLL = "mozcrt19.dll";
    private static final String NSSUTIL3_DLL = "nssutil3.dll";
    private static final String FREEBL3_DLL = "freebl3.dll";
    private static final String NSSDBM3_DLL = "nssdbm3.dll";
    private static final String SQLITE3_DLL = "sqlite3.dll";
    private static final String MOZUTILS_DLL = "mozutils.dll";
    private static final String MOZGLUE_DLL = "mozglue.dll";
    private static final String NSS3_DLL = "nss3.dll";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static String appData = null;

    private MozillaKeyStoreUtilitiesWindows() {
    }

    static String cleanMozillaUserProfileDirectoryWindows(String str) {
        String windowsShortName = KeyStoreUtilities.getWindowsShortName(str);
        if (windowsShortName != null) {
            return windowsShortName.replace('\\', '/');
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v19 */
    /* JADX WARN: Type inference failed for: r0v21 */
    /* JADX WARN: Type inference failed for: r0v22, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r0v23, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r0v24, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r10v1 */
    /* JADX WARN: Type inference failed for: r10v5 */
    /* JADX WARN: Type inference failed for: r10v6 */
    /* JADX WARN: Type inference failed for: r11v10 */
    /* JADX WARN: Type inference failed for: r11v2 */
    /* JADX WARN: Type inference failed for: r11v9 */
    static String getSystemNSSLibDirWindows() throws IOException {
        File file;
        String windowsShortName = KeyStoreUtilities.getWindowsShortName(MozillaKeyStoreUtilities.getNssPathFromCompatibilityFile());
        if (windowsShortName == null) {
            throw new FileNotFoundException("No se encuentra el directorio de NSS en Windows");
        }
        boolean z = false;
        char[] charArray = windowsShortName.toCharArray();
        int length = charArray.length;
        int i = 0;
        while (i < length) {
            if (P11_CONFIG_VALID_CHARS.indexOf(charArray[i == true ? 1 : 0]) != -1) {
                i++;
            } else {
                z = true;
                break;
            }
        }
        ?? r10 = length;
        ?? r11 = i;
        if (z) {
            LOGGER.info("La ruta hacia las bibliotecas NSS contiene carcateres ilegales, se copiaran a un temporal: " + windowsShortName);
            try {
                File file2 = new File(new File(Platform.getSystemLibDir()).getParent(), "Temp");
                if (file2.isDirectory() && file2.canWrite() && file2.canRead()) {
                    file = Files.createTempDirectory(Paths.get(file2.getCanonicalPath(), new String[0]), "nss", new FileAttribute[0]).toFile();
                } else {
                    file = Files.createTempDirectory("nss", new FileAttribute[0]).toFile();
                }
                Files.copy(Paths.get(SOFTOKN3_DLL, new String[0]), Paths.get(file.getCanonicalPath(), new String[0]), StandardCopyOption.REPLACE_EXISTING);
                String[] softkn3DependenciesWindows = getSoftkn3DependenciesWindows("");
                for (String str : softkn3DependenciesWindows) {
                    Files.copy(Paths.get(str, new String[0]), Paths.get(file.getCanonicalPath(), new String[0]), StandardCopyOption.REPLACE_EXISTING);
                }
                if (new File(file, SOFTOKN3_DLL).isFile()) {
                    windowsShortName = file.getCanonicalPath();
                    r10 = file2;
                    r11 = softkn3DependenciesWindows;
                } else {
                    LOGGER.warning("No se ha podido duplicar NSS encontrado en '" + LoggerUtil.getCleanUserHomePath(windowsShortName) + "' a un directorio temporalse intentara utilizar el NSS de la aplicacion '" + BundledNssHelper.getBundledNssDirectory());
                    return BundledNssHelper.getBundledNssDirectory();
                }
            } catch (Exception e) {
                LOGGER.warning("No se ha podido duplicar NSS en un directorio temporal, si esta version de JRE esta afectada por el error 6581254 de Java es posible que no pueda cargarse. Se intentara utilziar el NSS de la aplicacion '" + BundledNssHelper.getBundledNssDirectory() + "': " + e);
                return BundledNssHelper.getBundledNssDirectory();
            }
        }
        if (windowsShortName != null) {
            File file3 = new File(windowsShortName, SOFTOKN3_DLL);
            if (!file3.isFile()) {
                throw new FileNotFoundException("No se ha encontrado un NSS en Windows para el directorio " + windowsShortName);
            }
            if (!file3.canRead()) {
                throw new FileNotFoundException("No se tiene permiso para leer NSS en Windows para el directorio " + windowsShortName);
            }
            try {
                try {
                    FileInputStream fileInputStream = new FileInputStream(file3);
                    Throwable th = null;
                    PeMachineType peMachineType = new MsPortableExecutable(AOUtil.getDataFromInputStream(fileInputStream)).getPeMachineType();
                    String javaArch = Platform.getJavaArch();
                    if ((!peMachineType.equals(PeMachineType.INTEL_386) || !"32".equals(javaArch)) && (!peMachineType.equals(PeMachineType.X64) || !"64".equals(javaArch))) {
                        LOGGER.info("Se usara un NSS local por ser este Java de " + javaArch + " bits y el NSS de sistema para la arquitectura " + peMachineType);
                        String bundledNssDirectory = BundledNssHelper.getBundledNssDirectory();
                        if (fileInputStream != null) {
                            if (0 != 0) {
                                try {
                                    fileInputStream.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                fileInputStream.close();
                            }
                        }
                        return bundledNssDirectory;
                    }
                    LOGGER.info("Arquitectura del NSS encontrado: " + peMachineType);
                    if (fileInputStream != null) {
                        if (0 != 0) {
                            try {
                                fileInputStream.close();
                            } catch (Throwable th3) {
                                th.addSuppressed(th3);
                            }
                        } else {
                            fileInputStream.close();
                        }
                    }
                    return windowsShortName;
                } catch (Throwable th4) {
                    if (r10 != 0) {
                        if (r11 != 0) {
                            try {
                                r10.close();
                            } catch (Throwable th5) {
                                r11.addSuppressed(th5);
                            }
                        } else {
                            r10.close();
                        }
                    }
                    throw th4;
                }
            } catch (PEParserException e2) {
                LOGGER.warning("No se ha podido analizar la arquitectura del NSS encontrado: " + e2);
            }
            LOGGER.warning("No se ha podido analizar la arquitectura del NSS encontrado: " + e2);
            return windowsShortName;
        }
        throw new FileNotFoundException("No se ha encontrado un NSS compatible en Windows");
    }

    static String[] getSoftkn3DependenciesWindows(String str) {
        return new String[]{str + MSVCR100_DLL, str + MSVCP100_DLL, str + MSVCR120_DLL, str + MSVCP120_DLL, str + MSVCR140_DLL, str + MSVCP140_DLL, str + MOZGLUE_DLL, str + NSS3_DLL, str + MOZUTILS_DLL, str + MOZCRT19_DLL, str + NSPR4_DLL, str + PLDS4_DLL, str + PLC4_DLL, str + NSSUTIL3_DLL, str + MOZSQLITE3_DLL, str + SQLITE3_DLL, str + NSSDBM3_DLL, str + FREEBL3_DLL};
    }

    static String getWindowsAppDataDir() {
        String str;
        if (appData == null && (str = System.getenv("AppData")) != null) {
            LOGGER.info("Se ha comprobado la situacion del directorio 'AppData' de Windows a traves de la variable de entorno");
            appData = str;
        }
        if (appData != null) {
            return appData;
        }
        String str2 = "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming";
        if (new File(str2).isDirectory()) {
            appData = str2;
            LOGGER.info("Se ha comprobado la situacion del directorio 'AppData' de Windows manualmente");
            return appData;
        }
        appData = null;
        throw new IllegalStateException("No se ha podido determinar la situacion del directorio 'AppData' de Windows");
    }
}
