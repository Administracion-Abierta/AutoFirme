package es.gob.afirma.standalone;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.BoundedBufferedReader;
import es.gob.afirma.core.misc.Platform;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/DesktopUtil.class */
public final class DesktopUtil {
    private static final String REG_CMD = "reg";
    private static final String REG_KEY = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run";
    private static final String REG_VALUE = "Autofirma";
    private static final String REG_VALUE_OPT = "/v";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final Image ICON = Toolkit.getDefaultToolkit().getImage(DesktopUtil.class.getResource("/resources/logo_cliente_24.png"));
    private static List<Image> ICONS = null;

    public static Image getDefaultDialogsIcon() {
        return ICON;
    }

    public static List<Image> getIconImages() {
        if (ICONS == null) {
            ICONS = new ArrayList();
            ICONS.add(Toolkit.getDefaultToolkit().getImage(DesktopUtil.class.getResource("/resources/logo_cliente_16.png")));
            ICONS.add(Toolkit.getDefaultToolkit().getImage(DesktopUtil.class.getResource("/resources/logo_cliente_24.png")));
            ICONS.add(Toolkit.getDefaultToolkit().getImage(DesktopUtil.class.getResource("/resources/logo_cliente_32.png")));
            ICONS.add(Toolkit.getDefaultToolkit().getImage(DesktopUtil.class.getResource("/resources/logo_cliente_48.png")));
            ICONS.add(Toolkit.getDefaultToolkit().getImage(DesktopUtil.class.getResource("/resources/logo_cliente_128.png")));
            ICONS.add(Toolkit.getDefaultToolkit().getImage(DesktopUtil.class.getResource("/resources/logo_cliente_256.png")));
            ICONS.add(Toolkit.getDefaultToolkit().getImage(DesktopUtil.class.getResource("/resources/logo_cliente_512.png")));
        }
        return ICONS;
    }

    public static boolean getAutoStartEnabled() {
        if (!Platform.OS.WINDOWS.equals(Platform.getOS())) {
            return false;
        }
        try {
            for (String str : new String(AOUtil.getDataFromInputStream(new ProcessBuilder(REG_CMD, "QUERY", REG_KEY, REG_VALUE_OPT, REG_VALUE).start().getInputStream())).trim().split(" ")) {
                if (REG_VALUE.equals(str)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            LOGGER.severe("No se ha podido leer el registro de Windows para determinar el autoarranque de Autofirma: " + e);
            return false;
        }
    }

    public static void setAutoStartEnabled(boolean z) throws IOException {
        if (!Platform.OS.WINDOWS.equals(Platform.getOS())) {
            return;
        }
        if (z) {
            enableAutoStart();
        } else {
            disableAutoStart();
        }
    }

    private static void enableAutoStart() throws IOException {
        if (getAutoStartEnabled()) {
            return;
        }
        Process processStart = new ProcessBuilder(REG_CMD, "ADD", REG_KEY, REG_VALUE_OPT, REG_VALUE, "/t", "REG_SZ", "/d", "c:\\windows\\system32\\winver.exe").start();
        if (!getAutoStartEnabled()) {
            throw new IOException("No se ha podido habilitar el autoarranque de Autofirma: " + new String(AOUtil.getDataFromInputStream(processStart.getErrorStream())));
        }
    }

    private static void disableAutoStart() throws IOException {
        if (!getAutoStartEnabled()) {
            return;
        }
        Process processStart = new ProcessBuilder(REG_CMD, "DELETE", REG_KEY, REG_VALUE_OPT, REG_VALUE, "/f").start();
        if (getAutoStartEnabled()) {
            throw new IOException("No se ha podido deshabilitar el autoarranque de Autofirma: " + new String(AOUtil.getDataFromInputStream(processStart.getErrorStream())));
        }
    }

    public static File getApplicationDirectory() {
        if (isJnlpDeployment()) {
            return getJNLPApplicationDirectory();
        }
        try {
            return new File(DesktopUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        } catch (URISyntaxException e) {
            LOGGER.warning("No se pudo localizar el directorio del fichero en ejecucion: " + e);
            return null;
        }
    }

    public static String getApplicationFilename() {
        String name;
        String strDecode;
        String path = CommandLineLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (path != null && !path.isEmpty()) {
            try {
                strDecode = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                strDecode = path;
            }
            name = new File(strDecode).getName();
        } else {
            name = REG_VALUE;
        }
        return name;
    }

    public static File getJNLPApplicationDirectory() {
        if (Platform.getOS() == Platform.OS.WINDOWS) {
            File windowsAlternativeAppDir = getWindowsAlternativeAppDir();
            if (windowsAlternativeAppDir.isDirectory() || windowsAlternativeAppDir.mkdirs()) {
                return windowsAlternativeAppDir;
            }
        } else if (Platform.getOS() == Platform.OS.MACOSX) {
            File macOsXAlternativeAppDir = getMacOsXAlternativeAppDir();
            if (macOsXAlternativeAppDir.isDirectory() || macOsXAlternativeAppDir.mkdirs()) {
                return macOsXAlternativeAppDir;
            }
        }
        return new File(System.getProperty("java.io.tmpdir"));
    }

    public static File getWindowsAlternativeAppDir() {
        return new File(System.getenv("ALLUSERSPROFILE"), REG_VALUE);
    }

    public static File getLinuxAlternativeAppDir() {
        return new File(System.getProperty("user.home"), ".afirma/Autofirma");
    }

    public static File getMacOsXAlternativeAppDir() {
        return new File(System.getenv("HOME"), "Library/Application Support/Autofirma");
    }

    public static File getAlternativeDirectory() {
        File macOsXAlternativeAppDir = null;
        if (Platform.getOS() == Platform.OS.WINDOWS) {
            macOsXAlternativeAppDir = getWindowsAlternativeAppDir();
        } else if (Platform.getOS() == Platform.OS.LINUX) {
            macOsXAlternativeAppDir = getLinuxAlternativeAppDir();
        } else if (Platform.getOS() == Platform.OS.MACOSX) {
            macOsXAlternativeAppDir = getMacOsXAlternativeAppDir();
        }
        return macOsXAlternativeAppDir;
    }

    private static boolean isJnlpDeployment() {
        try {
            Class.forName("javax.jnlp.ServiceManager").getMethod("lookup", String.class).invoke(null, "javax.jnlp.ExtendedService");
            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    public static int getDPI() {
        if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
            try {
                Process processStart = new ProcessBuilder("wmic", "desktopmonitor", "get", "PixelsPerXLogicalInch").start();
                processStart.waitFor();
                BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(new InputStreamReader(processStart.getInputStream()));
                Throwable th = null;
                int i = 0;
                while (true) {
                    try {
                        try {
                            String line = boundedBufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            try {
                                i = Integer.parseInt(line.trim());
                                break;
                            } catch (Exception e) {
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            throw th2;
                        }
                    } catch (Throwable th3) {
                        if (boundedBufferedReader != null) {
                            if (th != null) {
                                try {
                                    boundedBufferedReader.close();
                                } catch (Throwable th4) {
                                    th.addSuppressed(th4);
                                }
                            } else {
                                boundedBufferedReader.close();
                            }
                        }
                        throw th3;
                    }
                }
                int i2 = i;
                if (boundedBufferedReader != null) {
                    if (0 != 0) {
                        try {
                            boundedBufferedReader.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        boundedBufferedReader.close();
                    }
                }
                return i2;
            } catch (Exception e2) {
                LOGGER.log(Level.SEVERE, "Error obteniendo DPI: " + e2);
                return 0;
            }
        }
        return 0;
    }

    public static int getDisplaysNumber() throws HeadlessException {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
    }

    public static File getCanonicalFile(File file) {
        try {
            return file.getCanonicalFile();
        } catch (IOException e) {
            LOGGER.severe("No se ha podido obtener el fichero canonico: " + e);
            return file;
        }
    }
}
