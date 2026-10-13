package es.gob.jmulticard.jse.smartcardio;

import java.io.File;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/smartcardio/LibJ2PCSCGNULinuxFix.class */
final class LibJ2PCSCGNULinuxFix {
    private static final int PCSC_LIBRARY_VERSION = 1;
    private static final String SMARTCARDIO_LIBRARY_PROPERTY = "sun.security.smartcardio.library";
    private static final String LIBRARY_PATH_PROPERTY = "java.library.path";
    private static final String GNULINUX_OS_PROPERTY_PREFIX = "Linux";
    private static final String PCSC_LIBRARY_NAME = "pcsclite";
    private static final String UBUNTU_MULTILIB_32_SUFFIX = "i386-linux-gnu";
    private static final String UBUNTU_MULTILIB_64_SUFFIX = "x86_64-linux-gnu";
    private static final String JRE_BITNESS_PROPERTY = "os.arch";
    private static final String OS_NAME_PROPERTY = "os.name";
    private static final String JRE_BITNESS_32_VALUE = "i386";
    private static final String JRE_BITNESS_64_VALUE = "amd64";
    private static final String LIBDIR = "/lib/";

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/smartcardio/LibJ2PCSCGNULinuxFix$UbuntuBitness.class */
    private enum UbuntuBitness {
        NA,
        PURE32,
        PURE64,
        MULTILIB
    }

    private LibJ2PCSCGNULinuxFix() {
    }

    static void fixNativeLibrary() {
        File libPcscLite;
        String osName = System.getProperty(OS_NAME_PROPERTY);
        if (osName != null && osName.startsWith(GNULINUX_OS_PROPERTY_PREFIX) && (libPcscLite = findGNULinuxNativeLibrary(PCSC_LIBRARY_NAME, 1)) != null) {
            System.setProperty(SMARTCARDIO_LIBRARY_PROPERTY, libPcscLite.getAbsolutePath());
        }
    }

    private static UbuntuBitness getUbuntuBitness() {
        File multiLibDir32 = new File("/lib/i386-linux-gnu");
        boolean has32 = multiLibDir32.exists() && multiLibDir32.isDirectory();
        File multiLibDir64 = new File("/lib/x86_64-linux-gnu");
        boolean has64 = multiLibDir64.exists() && multiLibDir64.isDirectory();
        if (has32) {
            if (!has64) {
                return UbuntuBitness.PURE32;
            }
            return UbuntuBitness.MULTILIB;
        }
        if (has64) {
            return UbuntuBitness.PURE64;
        }
        return UbuntuBitness.NA;
    }

    private static String extendLibraryPath(String libPath, String extension) {
        return libPath.contains(extension) ? libPath : libPath + ":" + extension;
    }

    private static String addMultiarchPath(String libPath, String suffix) {
        String retval = extendLibraryPath(libPath, LIBDIR + suffix);
        return extendLibraryPath(retval, "/usr/lib/" + suffix);
    }

    private static String fixPathForUbuntuMultiLib(String libraryPath) {
        switch (getUbuntuBitness()) {
            case PURE32:
                return addMultiarchPath(libraryPath, UBUNTU_MULTILIB_32_SUFFIX);
            case PURE64:
                return addMultiarchPath(libraryPath, UBUNTU_MULTILIB_64_SUFFIX);
            case MULTILIB:
                String jvmBinaryArch = System.getProperty(JRE_BITNESS_PROPERTY);
                if (jvmBinaryArch == null) {
                    return libraryPath;
                }
                if (JRE_BITNESS_32_VALUE.equals(jvmBinaryArch)) {
                    return addMultiarchPath(libraryPath, UBUNTU_MULTILIB_32_SUFFIX);
                }
                if (JRE_BITNESS_64_VALUE.equals(jvmBinaryArch)) {
                    return addMultiarchPath(libraryPath, UBUNTU_MULTILIB_64_SUFFIX);
                }
                break;
            default:
                Logger.getLogger(LibJ2PCSCGNULinuxFix.class.getName()).warning("No se ha podido determinar la arquitectura de Ubuntu, no se aplicaran correcciones de directorio de biliotecas");
                break;
        }
        return libraryPath;
    }

    private static File findGNULinuxNativeLibrary(String baseName, int version) {
        String nativeLibraryPaths = System.getProperty(LIBRARY_PATH_PROPERTY);
        if (nativeLibraryPaths == null) {
            return null;
        }
        String nativeLibraryPaths2 = fixPathForUbuntuMultiLib(nativeLibraryPaths);
        String libFileName = System.mapLibraryName(baseName) + "." + version;
        for (String nativeLibraryPath : nativeLibraryPaths2.split(":")) {
            File libraryFile = new File(nativeLibraryPath, libFileName);
            if (libraryFile.exists()) {
                return libraryFile;
            }
        }
        return null;
    }
}
