package es.gob.afirma.keystores.jmulticard.ui;

import com.sun.jna.platform.win32.WinError;
import es.gob.jmulticard.CancelledOperationException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.security.auth.callback.PasswordCallback;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/jmulticard/ui/UIPasswordCallbackCan.class */
public final class UIPasswordCallbackCan extends PasswordCallback {
    private static final long serialVersionUID = 1719174318602363633L;
    private static final String CAN_EXAMPLE = "/images/can_example.png";
    private final transient String message;
    private final transient Component parent;
    private final transient String title;

    public UIPasswordCallbackCan(String str, Object obj, String str2, String str3) {
        super(str, false);
        this.parent = obj instanceof Component ? (Component) obj : null;
        if (str != null) {
            this.message = str;
        } else {
            this.message = str2;
        }
        this.title = str3;
    }

    @Override // javax.security.auth.callback.PasswordCallback
    public char[] getPassword() {
        final JPasswordField jPasswordField = new JPasswordField(10);
        JLabel jLabel = new JLabel(this.message);
        jLabel.setMinimumSize(new Dimension(jLabel.getFontMetrics(jLabel.getFont()).stringWidth(this.message), jLabel.getSize().height));
        jLabel.setLabelFor(jPasswordField);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.anchor = 10;
        jPanel.add(jLabel, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.gridy++;
        gridBagConstraints.gridy++;
        gridBagConstraints.insets = new Insets(20, 0, 0, 20);
        jPanel.add(jPasswordField, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.gridy++;
        gridBagConstraints.gridy++;
        ImageIcon imageIcon = new ImageIcon(getClass().getResource(CAN_EXAMPLE));
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(WinError.ERROR_BAD_PIPE, 140, 4));
        Color color = Color.LIGHT_GRAY;
        BufferedImage bufferedImage = new BufferedImage(imageIcon.getIconWidth() + 2 + 0, imageIcon.getIconHeight() + 2 + 0, 2);
        Graphics2D graphics2DCreateGraphics = bufferedImage.createGraphics();
        graphics2DCreateGraphics.setColor(color);
        graphics2DCreateGraphics.drawImage(imageIcon.getImage(), 1, 1, (ImageObserver) null);
        graphics2DCreateGraphics.setStroke(new BasicStroke(5.0f));
        graphics2DCreateGraphics.drawRect(0, 0, bufferedImage.getWidth() - 1, bufferedImage.getHeight() - 1);
        graphics2DCreateGraphics.dispose();
        JLabel jLabel2 = new JLabel(new ImageIcon(bufferedImage), 2);
        jLabel2.setVerticalAlignment(1);
        jLabel2.setHorizontalAlignment(0);
        jPanel.add(jLabel2, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.gridy++;
        gridBagConstraints.gridy++;
        JOptionPane jOptionPane = new JOptionPane(jPanel, -1, 2) { // from class: es.gob.afirma.keystores.jmulticard.ui.UIPasswordCallbackCan.1
            private static final long serialVersionUID = -3012522768561175760L;

            public void selectInitialValue() {
                jPasswordField.requestFocusInWindow();
            }
        };
        jOptionPane.setPreferredSize(new Dimension(WinError.ERROR_FAIL_NOACTION_REBOOT, 310));
        JDialog jDialogCreateDialog = jOptionPane.createDialog(this.parent, this.title);
        jDialogCreateDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        jDialogCreateDialog.setVisible(true);
        Object value = jOptionPane.getValue();
        if (value == null) {
            return new char[0];
        }
        if (((Integer) value).intValue() == 0) {
            return jPasswordField.getPassword();
        }
        throw new CancelledOperationException("La insercion de contrasena ha sido cancelada por el usuario");
    }
}
