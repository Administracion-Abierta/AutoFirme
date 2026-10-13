package com.sun.jna;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/Platform.class */
public final class Platform {
    public static final int UNSPECIFIED = -1;
    public static final int MAC = 0;
    public static final int LINUX = 1;
    public static final int WINDOWS = 2;
    public static final int SOLARIS = 3;
    public static final int FREEBSD = 4;
    public static final int OPENBSD = 5;
    public static final int WINDOWSCE = 6;
    public static final int AIX = 7;
    public static final int ANDROID = 8;
    public static final int GNU = 9;
    public static final int KFREEBSD = 10;
    public static final int NETBSD = 11;
    public static final boolean RO_FIELDS;
    public static final boolean HAS_BUFFERS;
    public static final boolean HAS_AWT;
    public static final boolean HAS_JAWT;
    public static final String MATH_LIBRARY_NAME;
    public static final String C_LIBRARY_NAME;
    public static final boolean HAS_DLL_CALLBACKS;
    public static final String RESOURCE_PREFIX;
    private static final int osType;
    public static final String ARCH;

    static {
        String str;
        String str2;
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Linux")) {
            if ("dalvik".equals(System.getProperty("java.vm.name").toLowerCase())) {
                osType = 8;
                System.setProperty("jna.nounpack", "true");
            } else {
                osType = 1;
            }
        } else if (osName.startsWith("AIX")) {
            osType = 7;
        } else if (osName.startsWith("Mac") || osName.startsWith("Darwin")) {
            osType = 0;
        } else if (osName.startsWith("Windows CE")) {
            osType = 6;
        } else if (osName.startsWith("Windows")) {
            osType = 2;
        } else if (osName.startsWith("Solaris") || osName.startsWith("SunOS")) {
            osType = 3;
        } else if (osName.startsWith("FreeBSD")) {
            osType = 4;
        } else if (osName.startsWith("OpenBSD")) {
            osType = 5;
        } else if (osName.equalsIgnoreCase("gnu")) {
            osType = 9;
        } else if (osName.equalsIgnoreCase("gnu/kfreebsd")) {
            osType = 10;
        } else if (osName.equalsIgnoreCase("netbsd")) {
            osType = 11;
        } else {
            osType = -1;
        }
        boolean hasBuffers = false;
        try {
            Class.forName("java.nio.Buffer");
            hasBuffers = true;
        } catch (ClassNotFoundException e) {
        }
        HAS_AWT = (osType == 6 || osType == 8 || osType == 7) ? false : true;
        HAS_JAWT = HAS_AWT && osType != 0;
        HAS_BUFFERS = hasBuffers;
        RO_FIELDS = osType != 6;
        if (osType == 2) {
            str = "msvcrt";
        } else {
            str = osType == 6 ? "coredll" : "c";
        }
        C_LIBRARY_NAME = str;
        if (osType == 2) {
            str2 = "msvcrt";
        } else {
            str2 = osType == 6 ? "coredll" : OperatorName.MOVE_TO;
        }
        MATH_LIBRARY_NAME = str2;
        ARCH = getCanonicalArchitecture(System.getProperty("os.arch"), osType);
        HAS_DLL_CALLBACKS = osType == 2 && !ARCH.startsWith("aarch");
        RESOURCE_PREFIX = getNativeLibraryResourcePrefix();
    }

    private Platform() {
    }

    public static final int getOSType() {
        return osType;
    }

    public static final boolean isMac() {
        return osType == 0;
    }

    public static final boolean isAndroid() {
        return osType == 8;
    }

    public static final boolean isLinux() {
        return osType == 1;
    }

    public static final boolean isAIX() {
        return osType == 7;
    }

    public static final boolean isWindowsCE() {
        return osType == 6;
    }

    public static final boolean isWindows() {
        return osType == 2 || osType == 6;
    }

    public static final boolean isSolaris() {
        return osType == 3;
    }

    public static final boolean isFreeBSD() {
        return osType == 4;
    }

    public static final boolean isOpenBSD() {
        return osType == 5;
    }

    public static final boolean isNetBSD() {
        return osType == 11;
    }

    public static final boolean isGNU() {
        return osType == 9;
    }

    public static final boolean iskFreeBSD() {
        return osType == 10;
    }

    public static final boolean isX11() {
        return (isWindows() || isMac()) ? false : true;
    }

    public static final boolean hasRuntimeExec() {
        if (isWindowsCE() && "J9".equals(System.getProperty("java.vm.name"))) {
            return false;
        }
        return true;
    }

    public static final boolean is64Bit() {
        String model = System.getProperty("sun.arch.data.model", System.getProperty("com.ibm.vm.bitmode"));
        if (model != null) {
            return "64".equals(model);
        }
        return "x86-64".equals(ARCH) || "ia64".equals(ARCH) || "ppc64".equals(ARCH) || "ppc64le".equals(ARCH) || "sparcv9".equals(ARCH) || "mips64".equals(ARCH) || "mips64el".equals(ARCH) || "loongarch64".equals(ARCH) || "amd64".equals(ARCH) || "aarch64".equals(ARCH) || Native.POINTER_SIZE == 8;
    }

    public static final boolean isIntel() {
        if (ARCH.startsWith("x86")) {
            return true;
        }
        return false;
    }

    public static final boolean isPPC() {
        if (ARCH.startsWith("ppc")) {
            return true;
        }
        return false;
    }

    public static final boolean isARM() {
        return ARCH.startsWith("arm") || ARCH.startsWith("aarch");
    }

    public static final boolean isSPARC() {
        return ARCH.startsWith("sparc");
    }

    public static final boolean isMIPS() {
        if (ARCH.equals("mips") || ARCH.equals("mips64") || ARCH.equals("mipsel") || ARCH.equals("mips64el")) {
            return true;
        }
        return false;
    }

    public static final boolean isLoongArch() {
        return ARCH.startsWith("loongarch");
    }

    static String getCanonicalArchitecture(String arch, int platform) {
        String arch2 = arch.toLowerCase().trim();
        if ("powerpc".equals(arch2)) {
            arch2 = "ppc";
        } else if ("powerpc64".equals(arch2)) {
            arch2 = "ppc64";
        } else if ("i386".equals(arch2) || "i686".equals(arch2)) {
            arch2 = "x86";
        } else if ("x86_64".equals(arch2) || "amd64".equals(arch2)) {
            arch2 = "x86-64";
        } else if ("zarch_64".equals(arch2)) {
            arch2 = "s390x";
        }
        if ("ppc64".equals(arch2) && "little".equals(System.getProperty("sun.cpu.endian"))) {
            arch2 = "ppc64le";
        }
        if ("arm".equals(arch2) && platform == 1 && isSoftFloat()) {
            arch2 = "armel";
        }
        return arch2;
    }

    static boolean isSoftFloat() {
        try {
            File self = new File("/proc/self/exe");
            if (self.exists()) {
                ELFAnalyser ahfd = ELFAnalyser.analyse(self.getCanonicalPath());
                return !ahfd.isArmHardFloat();
            }
            return false;
        } catch (IOException ex) {
            Logger.getLogger(Platform.class.getName()).log(Level.INFO, "Failed to read '/proc/self/exe' or the target binary.", (Throwable) ex);
            return false;
        } catch (SecurityException ex2) {
            Logger.getLogger(Platform.class.getName()).log(Level.INFO, "SecurityException while analysing '/proc/self/exe' or the target binary.", (Throwable) ex2);
            return false;
        }
    }

    static String getNativeLibraryResourcePrefix() {
        String prefix = System.getProperty("jna.prefix");
        if (prefix != null) {
            return prefix;
        }
        return getNativeLibraryResourcePrefix(getOSType(), System.getProperty("os.arch"), System.getProperty("os.name"));
    }

    static String getNativeLibraryResourcePrefix(int osType2, String arch, String name) {
        String osPrefix;
        String arch2 = getCanonicalArchitecture(arch, osType2);
        switch (osType2) {
            case 0:
                osPrefix = "darwin-" + arch2;
                break;
            case 1:
                osPrefix = "linux-" + arch2;
                break;
            case 2:
                osPrefix = "win32-" + arch2;
                break;
            case 3:
                osPrefix = "sunos-" + arch2;
                break;
            case 4:
                osPrefix = "freebsd-" + arch2;
                break;
            case 5:
                osPrefix = "openbsd-" + arch2;
                break;
            case 6:
                osPrefix = "w32ce-" + arch2;
                break;
            case 7:
            case 9:
            default:
                String osPrefix2 = name.toLowerCase();
                int space = osPrefix2.indexOf(" ");
                if (space != -1) {
                    osPrefix2 = osPrefix2.substring(0, space);
                }
                osPrefix = osPrefix2 + "-" + arch2;
                break;
            case 8:
                if (arch2.startsWith("arm")) {
                    arch2 = "arm";
                }
                osPrefix = "android-" + arch2;
                break;
            case 10:
                osPrefix = "kfreebsd-" + arch2;
                break;
            case 11:
                osPrefix = "netbsd-" + arch2;
                break;
        }
        return osPrefix;
    }
}
