package es.gob.afirma.standalone.configurator;

import es.gob.afirma.core.misc.Platform;
import java.awt.Color;
import java.awt.Toolkit;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/LookAndFeelManager.class */
final class LookAndFeelManager {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    public static final Color WINDOW_COLOR;
    public static final boolean HIGH_CONTRAST;
    private static final int LARGE_FONT_LIMIT = 12;
    private static final boolean LARGE_FONT;

    static {
        WINDOW_COLOR = UIManager.getColor("window") != null ? new Color(UIManager.getColor("window").getRGB()) : Color.WHITE;
        Object desktopProperty = Toolkit.getDefaultToolkit().getDesktopProperty("win.highContrast.on");
        if (desktopProperty instanceof Boolean) {
            HIGH_CONTRAST = ((Boolean) desktopProperty).booleanValue();
        } else if (Platform.OS.LINUX.equals(Platform.getOS())) {
            HIGH_CONTRAST = true;
        } else {
            HIGH_CONTRAST = false;
        }
        Object desktopProperty2 = Toolkit.getDefaultToolkit().getDesktopProperty("win.defaultGUI.font.height");
        if (desktopProperty2 instanceof Integer) {
            LARGE_FONT = ((Integer) desktopProperty2).intValue() > 12;
        } else if (Platform.OS.LINUX.equals(Platform.getOS())) {
            LARGE_FONT = true;
        } else {
            LARGE_FONT = false;
        }
    }

    private LookAndFeelManager() {
    }

    static void applyLookAndFeel() {
        boolean z = HIGH_CONTRAST || LARGE_FONT;
        if (!z) {
            UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
            UIManager.put("RootPane.background", WINDOW_COLOR);
            UIManager.put("TextPane.background", WINDOW_COLOR);
            UIManager.put("TextArea.background", WINDOW_COLOR);
            UIManager.put("InternalFrameTitlePane.background", WINDOW_COLOR);
            UIManager.put("InternalFrame.background", WINDOW_COLOR);
            UIManager.put("Label.background", WINDOW_COLOR);
            UIManager.put("PopupMenuSeparator.background", WINDOW_COLOR);
        }
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        if (Platform.OS.MACOSX.equals(Platform.getOS())) {
            UIManager.put("OptionPane.background", WINDOW_COLOR);
            UIManager.put("Panel.background", WINDOW_COLOR);
            System.setProperty("apple.awt.brushMetalLook", "true");
            System.setProperty("apple.awt.antialiasing", "true");
            System.setProperty("apple.awt.textantialiasing", "true");
            System.setProperty("apple.awt.rendering", "quality");
            System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
            System.setProperty("apple.awt.graphics.EnableDeferredUpdates", "true");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        } else if (!z) {
            try {
                for (UIManager.LookAndFeelInfo lookAndFeelInfo : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(lookAndFeelInfo.getName())) {
                        UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                        LOGGER.info("Establecido 'Look&Feel' Nimbus");
                        return;
                    }
                }
            } catch (Exception e) {
                LOGGER.warning("No se ha podido establecer el 'Look&Feel' Nimbus: " + e);
            }
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            LOGGER.info("Establecido 'Look&Feel' " + UIManager.getLookAndFeel().getName());
        } catch (Exception e2) {
            LOGGER.warning("No se ha podido establecer ningun 'Look&Feel': " + e2);
        }
    }
}
