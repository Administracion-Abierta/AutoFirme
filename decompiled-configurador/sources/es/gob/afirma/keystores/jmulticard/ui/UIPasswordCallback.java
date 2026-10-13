package es.gob.afirma.keystores.jmulticard.ui;

import es.gob.jmulticard.CancelledOperationException;
import java.awt.Component;
import java.awt.Dimension;
import javax.security.auth.callback.PasswordCallback;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/jmulticard/ui/UIPasswordCallback.class */
public final class UIPasswordCallback extends PasswordCallback {
    private static final long serialVersionUID = 1719174318602363633L;
    private static final String DNI_LOGO = "/images/dnie_logo.png";
    private final transient String message;
    private final transient Component parent;
    private final transient String title;

    public UIPasswordCallback(String str, Object obj, String str2, String str3) {
        super(str, false);
        this.parent = obj instanceof Component ? (Component) obj : null;
        if (str != null) {
            this.message = str;
        } else {
            this.message = str2;
        }
        this.title = str3;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: es.gob.jmulticard.CancelledOperationException */
    @Override // javax.security.auth.callback.PasswordCallback
    public char[] getPassword() throws CancelledOperationException {
        final JPasswordField jPasswordField = new JPasswordField(10);
        JLabel jLabel = new JLabel(this.message);
        jLabel.setMinimumSize(new Dimension(jLabel.getFontMetrics(jLabel.getFont()).stringWidth(this.message), jLabel.getSize().height));
        jLabel.setLabelFor(jPasswordField);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, 1));
        jPanel.add(jLabel);
        jPanel.add(jPasswordField);
        JOptionPane jOptionPane = new JOptionPane(jPanel, 3, 2, new ImageIcon(getClass().getResource(DNI_LOGO))) { // from class: es.gob.afirma.keystores.jmulticard.ui.UIPasswordCallback.1
            private static final long serialVersionUID = -3012522768561175760L;

            public void selectInitialValue() {
                jPasswordField.requestFocusInWindow();
            }
        };
        jOptionPane.createDialog(this.parent, this.title).setVisible(true);
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
