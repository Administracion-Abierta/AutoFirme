package es.gob.afirma.standalone;

import es.gob.afirma.core.misc.Platform;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/LookAndFeelManager.class */
public final class LookAndFeelManager {
    public static final Color DEFAULT_COLOR;
    public static final Color SECUNDARY_COLOR;
    public static final boolean WINDOWS_HIGH_CONTRAST;
    public static final boolean GNOME_DARK_MODE;
    private static final int LARGE_FONT_LIMIT = 13;
    private static final boolean LARGE_FONT;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    public static final Color TRANSPARENT_COLOR = new Color(255, 255, 255, 0);
    private static Dimension screenSize = null;
    private static boolean applied = false;

    static {
        Color color;
        try {
            color = UIManager.getColor("window") != null ? new Color(UIManager.getColor("window").getRGB()) : new Color(238, 238, 238);
        } catch (Throwable th) {
            color = new Color(238, 238, 238);
        }
        DEFAULT_COLOR = color;
        Object desktopProperty = Toolkit.getDefaultToolkit().getDesktopProperty("win.highContrast.on");
        if (desktopProperty instanceof Boolean) {
            WINDOWS_HIGH_CONTRAST = ((Boolean) desktopProperty).booleanValue();
        } else {
            WINDOWS_HIGH_CONTRAST = false;
        }
        SECUNDARY_COLOR = WINDOWS_HIGH_CONTRAST ? Color.BLACK : Color.WHITE;
        Object desktopProperty2 = Toolkit.getDefaultToolkit().getDesktopProperty("win.defaultGUI.font.height");
        if (desktopProperty2 instanceof Integer) {
            LARGE_FONT = ((Integer) desktopProperty2).intValue() > 13;
        } else if (Platform.OS.LINUX.equals(Platform.getOS())) {
            LARGE_FONT = true;
        } else {
            LARGE_FONT = false;
        }
        if (Platform.getOS() == Platform.OS.LINUX) {
            GNOME_DARK_MODE = isGnomeDarkMode();
        } else {
            GNOME_DARK_MODE = false;
        }
    }

    private LookAndFeelManager() {
    }

    public static void applyLookAndFeel() {
        applyLookAndFeel(false);
    }

    public static void applyLookAndFeel(boolean z) {
        if (applied && !z) {
            return;
        }
        boolean z2 = WINDOWS_HIGH_CONTRAST || LARGE_FONT || GNOME_DARK_MODE;
        if (!z2) {
            UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
            UIManager.put("RootPane.background", DEFAULT_COLOR);
            UIManager.put("TextPane.background", DEFAULT_COLOR);
            UIManager.put("TextArea.background", DEFAULT_COLOR);
            UIManager.put("InternalFrameTitlePane.background", DEFAULT_COLOR);
            UIManager.put("InternalFrame.background", DEFAULT_COLOR);
            UIManager.put("Label.background", DEFAULT_COLOR);
            UIManager.put("PopupMenuSeparator.background", DEFAULT_COLOR);
        }
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        if (Platform.OS.MACOSX.equals(Platform.getOS())) {
            UIManager.put("OptionPane.background", DEFAULT_COLOR);
            UIManager.put("Panel.background", DEFAULT_COLOR);
            System.setProperty("apple.awt.brushMetalLook", "true");
            System.setProperty("apple.awt.antialiasing", "true");
            System.setProperty("apple.awt.textantialiasing", "true");
            System.setProperty("apple.awt.rendering", "quality");
            System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
            System.setProperty("apple.awt.graphics.EnableDeferredUpdates", "true");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        } else if (Platform.OS.WINDOWS.equals(Platform.getOS()) && HDPIManager.isHDPIDevice()) {
            setLookAndFeel("Metal");
        } else if (z2) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                LOGGER.warning("No se ha podido establecer ningun 'Look&Feel': " + e);
            }
        } else {
            setLookAndFeel("Nimbus");
        }
        applied = true;
    }

    private static void setLookAndFeel(String str) {
        try {
            for (UIManager.LookAndFeelInfo lookAndFeelInfo : UIManager.getInstalledLookAndFeels()) {
                if (str.equals(lookAndFeelInfo.getName())) {
                    UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                    return;
                }
            }
        } catch (Exception e) {
            LOGGER.warning("No se ha podido establecer el 'Look&Feel' " + str + ": " + e);
        }
    }

    public static Dimension getScreenSize() {
        if (screenSize == null) {
            screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        }
        return screenSize;
    }

    public static boolean needMaximizeWindow() {
        return getScreenSize().height <= 600;
    }

    private static boolean isGnomeDarkMode() {
        return (queryResultContains("echo $XDG_CURRENT_DESKTOP", "gnome") || queryResultContains("echo $XDG_DATA_DIRS | grep -Eo 'gnome'", "gnome") || queryResultContains("ps -e | grep -E -i \"gnome\"", "gnome")) && queryResultContains("gsettings get org.gnome.desktop.interface gtk-theme", "dark");
    }

    private static boolean queryResultContains(String str, String str2) {
        return query(str).toLowerCase().contains(str2);
    }

    private static String query(String str) {
        try {
            Process processExec = Runtime.getRuntime().exec(str);
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(processExec.getInputStream()));
            Throwable th = null;
            while (true) {
                try {
                    try {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        if (sb.length() != 0) {
                            sb.append('\n');
                        }
                        sb.append(line);
                    } catch (Throwable th2) {
                        th = th2;
                        throw th2;
                    }
                } catch (Throwable th3) {
                    if (bufferedReader != null) {
                        if (th != null) {
                            try {
                                bufferedReader.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            bufferedReader.close();
                        }
                    }
                    throw th3;
                }
            }
            if (bufferedReader != null) {
                if (0 != 0) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    bufferedReader.close();
                }
            }
            return sb.toString();
        } catch (IOException e) {
            return "";
        }
    }
}
