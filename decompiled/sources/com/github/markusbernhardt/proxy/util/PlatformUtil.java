package com.github.markusbernhardt.proxy.util;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/util/PlatformUtil.class */
public class PlatformUtil {
    public static final String OVERRIDE_HOME_DIR = "com.btr.proxy.user.home";
    public static final String XML_FEATURE_SECURE_PROCESSING = "proxy-vole.xml.factory.feature.secure.processing";

    /* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/util/PlatformUtil$Browser.class */
    public enum Browser {
        IE,
        FIREFOX
    }

    /* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/util/PlatformUtil$Desktop.class */
    public enum Desktop {
        WIN,
        KDE,
        GNOME,
        MAC_OS,
        OTHER
    }

    /* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/util/PlatformUtil$Platform.class */
    public enum Platform {
        WIN,
        LINUX,
        MAC_OS,
        SOLARIS,
        OTHER
    }

    public static Platform getCurrentPlattform() {
        String osName = System.getProperty("os.name");
        Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detecting platform. Name is: {}", osName);
        if (osName.toLowerCase().contains("windows")) {
            Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Windows platform: {}", osName);
            return Platform.WIN;
        }
        if (osName.toLowerCase().contains("linux")) {
            Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Linux platform: {}", osName);
            return Platform.LINUX;
        }
        if (osName.startsWith("Mac OS")) {
            Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Mac OS platform: {}", osName);
            return Platform.MAC_OS;
        }
        if (osName.startsWith("SunOS")) {
            Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Solaris platform: {}", osName);
            return Platform.SOLARIS;
        }
        return Platform.OTHER;
    }

    public static Browser getDefaultBrowser() {
        if (getCurrentPlattform() == Platform.WIN) {
            Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Browser is InternetExplorer", new Object[0]);
            return Browser.IE;
        }
        Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Browser Firefox. Fallback?", new Object[0]);
        return Browser.FIREFOX;
    }

    public static Desktop getCurrentDesktop() {
        Platform platf = getCurrentPlattform();
        if (platf == Platform.WIN) {
            Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Windows desktop", new Object[0]);
            return Desktop.WIN;
        }
        if (platf == Platform.MAC_OS) {
            Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Mac OS desktop", new Object[0]);
            return Desktop.MAC_OS;
        }
        if (platf == Platform.LINUX || platf == Platform.SOLARIS || platf == Platform.OTHER) {
            if (isKDE()) {
                Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected KDE desktop", new Object[0]);
                return Desktop.KDE;
            }
            if (isGnome()) {
                Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Gnome desktop", new Object[0]);
                return Desktop.GNOME;
            }
        }
        Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Unknown desktop", new Object[0]);
        return Desktop.OTHER;
    }

    private static boolean isGnome() {
        return System.getenv("GNOME_DESKTOP_SESSION_ID") != null;
    }

    private static boolean isKDE() {
        return System.getenv("KDE_SESSION_VERSION") != null;
    }

    public static String getUserHomeDir() {
        String result = System.getProperty("user.home");
        String override = System.getProperty(OVERRIDE_HOME_DIR);
        if (override != null && override.trim().length() > 0) {
            result = override;
        }
        return result;
    }
}
