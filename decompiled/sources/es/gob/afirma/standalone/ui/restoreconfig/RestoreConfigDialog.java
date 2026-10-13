package es.gob.afirma.standalone.ui.restoreconfig;

import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Dimension;
import java.awt.Frame;
import javax.swing.JDialog;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/restoreconfig/RestoreConfigDialog.class */
public final class RestoreConfigDialog extends JDialog {
    private static final long serialVersionUID = -241482490367263150L;

    public RestoreConfigDialog(Frame frame, boolean z) {
        super(frame, z);
        setTitle(SimpleAfirmaMessages.getString("MainMenu.20"));
        add(new RestoreConfigPanel(this));
        setSize(new Dimension(600, (int) Math.min(550.0d, LookAndFeelManager.getScreenSize().getHeight() * 0.8d)));
        setLocationRelativeTo(frame);
        setDefaultCloseOperation(2);
        getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("RestoreConfigPanel.4"));
    }

    public static void show(Frame frame, boolean z) {
        new RestoreConfigDialog(frame, z).setVisible(true);
    }
}
