package es.gob.afirma.standalone.ui;

import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.LookAndFeelManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowListener;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/MainScreen.class */
public final class MainScreen extends JFrame {
    private static final long serialVersionUID = -3288572031446592104L;
    private JScrollPane scrollPane = null;

    public void showMainScreen(WindowListener windowListener, JPanel jPanel, int i, int i2) {
        SwingUtilities.invokeLater(() -> {
            createUI(windowListener, jPanel, i, i2);
        });
    }

    void createUI(WindowListener windowListener, JPanel jPanel, int i, int i2) {
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            setBackground(LookAndFeelManager.DEFAULT_COLOR);
        }
        if (LookAndFeelManager.needMaximizeWindow()) {
            setExtendedState(getExtendedState() | 6);
        }
        setLayout(new BorderLayout());
        setDefaultCloseOperation(0);
        if (windowListener != null) {
            addWindowListener(windowListener);
        }
        this.scrollPane = new JScrollPane();
        this.scrollPane.setPreferredSize(new Dimension(i, i2));
        this.scrollPane.getHorizontalScrollBar().setUnitIncrement(30);
        this.scrollPane.getVerticalScrollBar().setUnitIncrement(30);
        this.scrollPane.setViewportView(jPanel);
        add(this.scrollPane, "Center");
        try {
            setIconImages(DesktopUtil.getIconImages());
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").warning("No se ha podido cargar el icono de la aplicacion: " + e);
        }
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void replaceShowingPanel(JPanel jPanel) {
        if (this.scrollPane != null) {
            this.scrollPane.setViewportView(jPanel);
            pack();
        }
    }
}
