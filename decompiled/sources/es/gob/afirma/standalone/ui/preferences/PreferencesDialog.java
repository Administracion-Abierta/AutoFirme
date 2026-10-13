package es.gob.afirma.standalone.ui.preferences;

import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Frame;
import javax.swing.JDialog;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/PreferencesDialog.class */
public final class PreferencesDialog extends JDialog {
    private static final long serialVersionUID = 1;

    public PreferencesDialog(Frame frame, boolean z, int i) {
        super(frame, z);
        setTitle(SimpleAfirmaMessages.getString("MainMenu.24"));
        setIconImages(DesktopUtil.getIconImages());
        add(new PreferencesPanel(this, i));
        pack();
        setLocationRelativeTo(frame);
        setDefaultCloseOperation(2);
    }

    public PreferencesDialog(Frame frame, boolean z) {
        this(frame, z, 0);
    }

    public static void show(Frame frame, boolean z, int i) {
        new PreferencesDialog(frame, z, i).setVisible(true);
    }

    public static void show(Frame frame, boolean z) {
        new PreferencesDialog(frame, z, 0).setVisible(true);
    }
}
