package es.gob.afirma.core.misc;

import java.io.File;
import java.util.Locale;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/misc/Platform.class */
public final class Platform {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static OS os = null;
    private static String javaArch = null;
    private static String javaHome = null;
    private static String javaLibraryPath = null;
    private static String userHome = null;

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/misc/Platform$BROWSER.class */
    public enum BROWSER {
        INTERNET_EXPLORER,
        FIREFOX,
        CHROME,
        SAFARI,
        OPERA,
        OTHER
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/misc/Platform$MACHINE.class */
    public enum MACHINE {
        X86,
        ARM32,
        MIPS32,
        AMD64,
        ARM64,
        MIPS64,
        OTHER
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/misc/Platform$OS.class */
    public enum OS {
        WINDOWS,
        LINUX,
        SOLARIS,
        MACOSX,
        ANDROID,
        OTHER
    }

    private Platform() {
    }

    public static MACHINE getMachineType() {
        String property = System.getProperty("os.arch");
        if (property == null) {
            LOGGER.warning("No se ha podido determinar el la arquitectura del procesador");
            return MACHINE.OTHER;
        }
        if (property.equals("aarch64") || property.startsWith("arm64")) {
            return MACHINE.ARM64;
        }
        if (property.equals("mips64")) {
            return MACHINE.MIPS64;
        }
        if (property.equals("mips")) {
            return MACHINE.MIPS32;
        }
        if (property.equals("x86_64") || property.startsWith("amd64")) {
            return MACHINE.AMD64;
        }
        if (property.equals("x86")) {
            return MACHINE.X86;
        }
        LOGGER.warning("No se ha podido determinar el la arquitectura del procesador: " + property);
        return MACHINE.OTHER;
    }

    private static OS recoverOsName() {
        String property = System.getProperty("os.name");
        if (property.contains("indows")) {
            return OS.WINDOWS;
        }
        if (property.contains("inux")) {
            if ("Dalvik".equals(System.getProperty("java.vm.name"))) {
                return OS.ANDROID;
            }
            return OS.LINUX;
        }
        if (property.contains("SunOS") || property.contains("olaris")) {
            return OS.SOLARIS;
        }
        if (property.startsWith("Mac OS X")) {
            return OS.MACOSX;
        }
        LOGGER.warning("No se ha podido determinar el sistema operativo");
        return OS.OTHER;
    }

    public static BROWSER getBrowser(String str) {
        if (str == null) {
            return BROWSER.OTHER;
        }
        if (str.toLowerCase(Locale.US).contains("msie")) {
            return BROWSER.INTERNET_EXPLORER;
        }
        if (str.toLowerCase(Locale.US).contains("firefox")) {
            return BROWSER.FIREFOX;
        }
        if (str.toLowerCase(Locale.US).contains("chrome")) {
            return BROWSER.CHROME;
        }
        if (str.toLowerCase(Locale.US).contains("safari")) {
            return BROWSER.SAFARI;
        }
        if (str.toLowerCase(Locale.US).contains("opera")) {
            return BROWSER.OPERA;
        }
        return BROWSER.OTHER;
    }

    public static OS getOS() {
        if (os == null) {
            os = recoverOsName();
        }
        return os;
    }

    public static String getJavaArch() {
        if (javaArch == null) {
            try {
                javaArch = System.getProperty("sun.arch.data.model");
                if (javaArch == null) {
                    javaArch = System.getProperty("com.ibm.vm.bitmode");
                }
            } catch (Exception e) {
                LOGGER.severe("No se ha podido determinar si la JVM es de 32 o 64 bits, se asume que es 64: " + e);
                return "64";
            }
        }
        return javaArch;
    }

    public static String getJavaHome() {
        if (javaHome == null) {
            javaHome = recoverJavaHome();
        }
        return javaHome;
    }

    public static String getJavaLibraryPath() {
        if (javaLibraryPath == null) {
            javaLibraryPath = System.getProperty("java.library.path");
        }
        return javaLibraryPath;
    }

    public static String getUserHome() {
        if (userHome == null) {
            userHome = System.getProperty("user.home");
        }
        return userHome;
    }

    public static boolean isUnixSystem(OS os2) {
        return os2 == OS.LINUX || os2 == OS.MACOSX || os2 == OS.SOLARIS;
    }

    private static String recoverJavaHome() {
        String property = null;
        try {
            property = System.getProperty("jnlpx.home");
        } catch (Exception e) {
            LOGGER.warning("No se ha podido leer la variable 'jnlpx.home': " + e);
        }
        if (property != null) {
            return property.substring(0, property.lastIndexOf(File.separator));
        }
        try {
            return System.getProperty("java.home");
        } catch (Exception e2) {
            LOGGER.warning("No se ha podido identificar el directorio de Java: " + e2);
            return null;
        }
    }

    public static String getSystemRoot() {
        if (!getOS().equals(OS.WINDOWS)) {
            return File.separator;
        }
        String property = System.getProperty("SystemRoot");
        if (property == null && new File("C:\\WINDOWS\\system32").isDirectory()) {
            return "C:\\WINDOWS";
        }
        if (property == null) {
            LOGGER.warning("No se ha encontrado el directorio ra&iacute;z del sistema, se devolver&aacute;: " + File.separator);
            return File.separator;
        }
        return property;
    }

    public static String getSystemLibDir() {
        if (getOS().equals(OS.WINDOWS)) {
            String systemRoot = getSystemRoot();
            if (systemRoot == null) {
                LOGGER.warning("No se ha podido determinar el directorio de Windows accediendo al registro, se usara 'C:\\WINDOWS\\'");
                systemRoot = "c:\\windows\\";
            }
            if (!systemRoot.endsWith("\\")) {
                systemRoot = systemRoot + "\\";
            }
            if ("32".equals(getJavaArch())) {
                File file = new File(systemRoot + "SysWOW64");
                if (file.isDirectory()) {
                    return file.getAbsolutePath();
                }
            }
            return systemRoot + "System32";
        }
        String str = "/usr/lib" + getJavaArch();
        if (new File(str).exists()) {
            return str;
        }
        return "/usr/lib";
    }
}
