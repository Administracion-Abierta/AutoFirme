package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.standalone.DesktopUtil;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/CommonWaitDialog.class */
public final class CommonWaitDialog extends JDialog {
    private static final long serialVersionUID = -6137113502471587689L;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private final JLabel labelProgress;

    public CommonWaitDialog(Frame frame, String str, String str2) {
        super(frame, Dialog.ModalityType.APPLICATION_MODAL);
        this.labelProgress = new JLabel();
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 10;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        jPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.labelProgress.setHorizontalAlignment(0);
        this.labelProgress.setText(str);
        jPanel.add(this.labelProgress, gridBagConstraints);
        JProgressBar jProgressBar = new JProgressBar();
        jProgressBar.setIndeterminate(true);
        if (Platform.OS.MACOSX.equals(Platform.getOS())) {
            try {
                JProgressBar.class.getMethod("putClientProperty", Object.class, Object.class).invoke(jProgressBar, "JProgressBar.style", "circular");
            } catch (Exception e) {
                LOGGER.warning("No se ha podido establecer el estilo OS X en el dialogo de espera: " + e);
            }
        }
        this.labelProgress.setLabelFor(jProgressBar);
        gridBagConstraints.gridy++;
        gridBagConstraints.gridy++;
        jPanel.add(jProgressBar, gridBagConstraints);
        setContentPane(jPanel);
        setDefaultCloseOperation(0);
        try {
            setIconImages(DesktopUtil.getIconImages());
        } catch (Exception e2) {
            LOGGER.warning("No se ha podido cargar el icono del dialogo de espera de firma: " + e2);
        }
        pack();
        Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        setLocation(centerPoint.x - (getWidth() / 2), centerPoint.y - (getHeight() / 2));
        setResizable(false);
    }

    public void setMessage(String str) {
        this.labelProgress.setText(str);
        pack();
        revalidate();
        repaint();
    }

    public String getMessage() {
        return this.labelProgress.getText();
    }
}
