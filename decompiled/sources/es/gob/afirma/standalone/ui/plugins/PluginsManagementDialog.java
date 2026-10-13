package es.gob.afirma.standalone.ui.plugins;

import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Dimension;
import java.awt.Frame;
import javax.swing.JDialog;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/plugins/PluginsManagementDialog.class */
public final class PluginsManagementDialog extends JDialog {
    private static final long serialVersionUID = -2374733164838573024L;

    private PluginsManagementDialog(Frame frame, boolean z) {
        super(frame, z);
        setTitle(SimpleAfirmaMessages.getString("MainMenu.37"));
        add(new PluginsManagementPanel(this));
        setSize(new Dimension(600, (int) Math.min(550.0d, LookAndFeelManager.getScreenSize().getHeight() * 0.8d)));
        setLocationRelativeTo(frame);
        setDefaultCloseOperation(2);
    }

    public static void show(Frame frame, boolean z) {
        new PluginsManagementDialog(frame, z).setVisible(true);
    }
}
