package es.gob.afirma.standalone;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import es.gob.afirma.standalone.ui.MainScreen;
import es.gob.afirma.standalone.ui.VisorPanel;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Locale;
import javax.swing.JApplet;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/VisorFirma.class */
public class VisorFirma extends JApplet implements WindowListener {
    private static final long serialVersionUID = 7060676034863587322L;
    private static final int DEFAULT_WINDOW_WIDTH = 780;
    private static final int DEFAULT_WINDOW_HEIGHT = 650;
    private Window window;
    private Container container = null;
    private JPanel currentPanel;
    private final Object parentComponent;
    private final boolean standalone;
    private File signFile;

    public VisorFirma(boolean z, Object obj) {
        this.standalone = z;
        LookAndFeelManager.applyLookAndFeel();
        this.parentComponent = obj;
    }

    public void initialize(boolean z, File file) {
        JDialog jDialog;
        if (file != null) {
            this.signFile = file;
        }
        String string = PreferencesManager.get(PreferencesManager.PREFERENCES_LOCALE);
        if (string == null || string.isEmpty()) {
            string = Locale.getDefault().toString();
        }
        setDefaultLocale(buildLocale(string));
        if (z) {
            this.container = this;
            return;
        }
        this.currentPanel = new VisorPanel(this.signFile, null, this, this.standalone);
        if (this.parentComponent == null) {
            MainScreen mainScreen = new MainScreen();
            mainScreen.showMainScreen(this, this.currentPanel, 780, 650);
            this.container = mainScreen;
        } else {
            if (this.parentComponent instanceof Frame) {
                jDialog = new JDialog((Frame) this.parentComponent);
            } else if (this.parentComponent instanceof Window) {
                jDialog = new JDialog((Window) this.parentComponent);
            } else if (this.parentComponent instanceof Dialog) {
                jDialog = new JDialog((Dialog) this.parentComponent);
            } else {
                jDialog = new JDialog();
            }
            jDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            jDialog.setSize(780, 650);
            Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
            jDialog.setLocation(centerPoint.x - 390, centerPoint.y - 390);
            jDialog.add(this.currentPanel);
            this.container = jDialog;
        }
        if (this.window != null) {
            this.window.dispose();
        }
        this.window = this.container;
        if (this.window instanceof JFrame) {
            this.window.getRootPane().putClientProperty("Window.documentFile", this.signFile);
            this.window.setTitle(SimpleAfirmaMessages.getString("VisorFirma.0") + (this.signFile != null ? " - " + this.signFile.getAbsolutePath() : ""));
            if (LookAndFeelManager.needMaximizeWindow()) {
                this.window.setExtendedState(this.window.getExtendedState() | 6);
                return;
            }
            return;
        }
        if (this.window instanceof JDialog) {
            this.window.getRootPane().putClientProperty("Window.documentFile", this.signFile);
            this.window.setTitle(SimpleAfirmaMessages.getString("VisorFirma.0") + (this.signFile != null ? " - " + this.signFile.getAbsolutePath() : ""));
            this.window.setVisible(true);
        }
    }

    public Container getParentComponent() {
        return this.container;
    }

    private static Locale buildLocale(String str) {
        String[] strArrSplit = str.split("_");
        if (strArrSplit.length == 1) {
            return new Locale(strArrSplit[0]);
        }
        if (strArrSplit.length == 2) {
            return new Locale(strArrSplit[0], strArrSplit[1]);
        }
        return new Locale(strArrSplit[0], strArrSplit[1], strArrSplit[2]);
    }

    public static void setDefaultLocale(Locale locale) {
        if (locale != null) {
            Locale.setDefault(locale);
            PreferencesManager.put(PreferencesManager.PREFERENCES_LOCALE, locale.toString());
            SimpleAfirmaMessages.changeLocale();
        }
    }

    public void windowClosing(WindowEvent windowEvent) {
        closeApplication(0);
    }

    public void windowOpened(WindowEvent windowEvent) {
    }

    public void windowClosed(WindowEvent windowEvent) {
    }

    public void windowIconified(WindowEvent windowEvent) {
    }

    public void windowDeiconified(WindowEvent windowEvent) {
    }

    public void windowActivated(WindowEvent windowEvent) {
    }

    public void windowDeactivated(WindowEvent windowEvent) {
    }

    public void closeApplication(int i) {
        if (this.window != null) {
            this.window.dispose();
        }
        if (this.standalone) {
            System.exit(i);
        }
    }

    public void loadNewSign() {
        try {
            File file = AOUIFactory.getLoadFiles(SimpleAfirmaMessages.getString("VisorFirma.1"), null, null, null, null, false, false, DesktopUtil.getDefaultDialogsIcon(), this.window)[0];
            if (file == null) {
                return;
            }
            initialize(equals(this.container), file);
            repaint();
        } catch (AOCancelledOperationException e) {
        }
    }
}
