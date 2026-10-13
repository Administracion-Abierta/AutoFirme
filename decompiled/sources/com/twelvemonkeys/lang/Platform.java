package com.twelvemonkeys.lang;

import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/lang/Platform.class */
public final class Platform {
    final OperatingSystem os;
    final String version;
    final Architecture architecture;
    private static final Platform INSTANCE = new Platform();

    private Platform() {
        this(System.getProperties());
    }

    Platform(Properties properties) {
        this.os = normalizeOperatingSystem(properties.getProperty("os.name"));
        this.version = properties.getProperty("os.version");
        this.architecture = normalizeArchitecture(this.os, properties.getProperty("os.arch"));
    }

    static OperatingSystem normalizeOperatingSystem(String str) {
        if (str == null) {
            throw new IllegalStateException("System property \"os.name\" == null");
        }
        String lowerCase = str.toLowerCase();
        if (lowerCase.startsWith("windows")) {
            return OperatingSystem.Windows;
        }
        if (lowerCase.startsWith("linux")) {
            return OperatingSystem.Linux;
        }
        if (lowerCase.startsWith("mac os") || lowerCase.startsWith("darwin")) {
            return OperatingSystem.MacOS;
        }
        if (lowerCase.startsWith("solaris") || lowerCase.startsWith("sunos")) {
            return OperatingSystem.Solaris;
        }
        return OperatingSystem.Unknown;
    }

    static Architecture normalizeArchitecture(OperatingSystem operatingSystem, String str) {
        if (str == null) {
            throw new IllegalStateException("System property \"os.arch\" == null");
        }
        String lowerCase = str.toLowerCase();
        if (operatingSystem == OperatingSystem.Windows && (lowerCase.startsWith("x86") || lowerCase.startsWith("i386"))) {
            return Architecture.X86;
        }
        if (operatingSystem == OperatingSystem.Linux) {
            if (lowerCase.startsWith("x86") || lowerCase.startsWith("i386")) {
                return Architecture.I386;
            }
            if (lowerCase.startsWith("i686")) {
                return Architecture.I686;
            }
            if (lowerCase.startsWith("power") || lowerCase.startsWith("ppc")) {
                return Architecture.PPC;
            }
        } else if (operatingSystem == OperatingSystem.MacOS) {
            if (lowerCase.startsWith("power") || lowerCase.startsWith("ppc")) {
                return Architecture.PPC;
            }
            if (lowerCase.startsWith("x86")) {
                return Architecture.X86;
            }
            if (lowerCase.startsWith("i386")) {
                return Architecture.X86;
            }
        } else if (operatingSystem == OperatingSystem.Solaris) {
            if (lowerCase.startsWith("sparc")) {
                return Architecture.SPARC;
            }
            if (lowerCase.startsWith("x86")) {
                return Architecture.X86;
            }
        }
        return Architecture.Unknown;
    }

    public static Platform get() {
        return INSTANCE;
    }

    public OperatingSystem getOS() {
        return this.os;
    }

    public String getVersion() {
        return this.version;
    }

    public Architecture getArchitecture() {
        return this.architecture;
    }

    public static OperatingSystem os() {
        return INSTANCE.os;
    }

    public static String version() {
        return INSTANCE.version;
    }

    public static Architecture arch() {
        return INSTANCE.architecture;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/lang/Platform$Architecture.class */
    public enum Architecture {
        X86("x86"),
        I386("i386"),
        I686("i686"),
        PPC("ppc"),
        SPARC("sparc"),
        Unknown(System.getProperty("os.arch"));

        final String name;

        Architecture(String str) {
            this.name = str;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.name;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/lang/Platform$OperatingSystem.class */
    public enum OperatingSystem {
        Windows("Windows", "win"),
        Linux("Linux", "lnx"),
        Solaris("Solaris", "sun"),
        MacOS("Mac OS", "osx"),
        Unknown(System.getProperty("os.name"), null);

        final String id;
        final String name;

        OperatingSystem(String str, String str2) {
            this.name = str;
            this.id = str2 != null ? str2 : str.toLowerCase();
        }

        public String getName() {
            return this.name;
        }

        public String id() {
            return this.id;
        }

        @Override // java.lang.Enum
        public String toString() {
            return String.format("%s (%s)", this.id, this.name);
        }
    }
}
