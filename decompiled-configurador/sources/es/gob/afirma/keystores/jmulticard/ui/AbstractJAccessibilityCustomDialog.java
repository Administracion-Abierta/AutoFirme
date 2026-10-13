package es.gob.afirma.keystores.jmulticard.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/jmulticard/ui/AbstractJAccessibilityCustomDialog.class */
abstract class AbstractJAccessibilityCustomDialog extends JDialog {
    private static final long serialVersionUID = 1;
    private static int actualPositionX = -1;
    private static int actualPositionY = -1;
    private static int actualWidth = -1;
    private static int actualHeight = -1;
    private boolean bigSizeDefault;
    private final transient boolean isInputDialog;

    abstract int getMinimumRelation();

    AbstractJAccessibilityCustomDialog(JDialog jDialog, boolean z, boolean z2) {
        super(jDialog, z);
        this.bigSizeDefault = false;
        this.isInputDialog = z2;
        addComponentListener(new ResizingAdaptor(this));
        setResizable(false);
    }

    AbstractJAccessibilityCustomDialog(JFrame jFrame, boolean z, boolean z2) {
        super(jFrame, z);
        this.bigSizeDefault = false;
        this.isInputDialog = z2;
        addComponentListener(new ResizingAdaptor(this));
        setResizable(false);
    }

    AbstractJAccessibilityCustomDialog(boolean z) {
        this.bigSizeDefault = false;
        this.isInputDialog = z;
        addComponentListener(new ResizingAdaptor(this));
        setResizable(false);
    }

    static int getActualPositionX() {
        return actualPositionX;
    }

    static void setActualPositionX(int i) {
        actualPositionX = i;
    }

    static int getActualPositionY() {
        return actualPositionY;
    }

    static void setActualPositionY(int i) {
        actualPositionY = i;
    }

    static int getActualWidth() {
        return actualWidth;
    }

    static void setActualWidth(int i) {
        actualWidth = i;
    }

    static int getActualHeight() {
        return actualHeight;
    }

    static void setActualHeight(int i) {
        actualHeight = i;
    }

    boolean isBigSizeDefault() {
        return this.bigSizeDefault;
    }

    void setBigSizeDefault(boolean z) {
        this.bigSizeDefault = z;
    }

    int getInitialWidth() {
        if (this.isInputDialog) {
            return 530;
        }
        return 460;
    }

    int getInitialHeight() {
        if (this.isInputDialog) {
            return 210;
        }
        return CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA256;
    }

    int getMaxWidth() {
        if (this.isInputDialog) {
            return 770;
        }
        return 700;
    }

    int getMaxHeight() {
        if (this.isInputDialog) {
            return 299;
        }
        return 305;
    }

    protected JPanel createAccessibilityButtonsPanel() {
        JPanel jPanel = new JPanel(new GridBagLayout());
        final JWindow jWindow = new JWindow();
        final JLabel jLabel = new JLabel();
        JPanel jPanel2 = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        JPanel jPanel3 = new JPanel();
        final JButton jButton = new JButton(new ImageIcon(InputPasswordSmartcardDialog.class.getResource("/images/restore.png")));
        jButton.setMnemonic(82);
        jButton.setToolTipText(Messages.getString("Wizard.restaurar.description"));
        jButton.getAccessibleContext().setAccessibleName(jButton.getToolTipText());
        jButton.addFocusListener(new FocusListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.AbstractJAccessibilityCustomDialog.1
            public void focusLost(FocusEvent focusEvent) {
                AccesibilityUtils.showToolTip(false, jWindow, jButton, jLabel);
            }

            public void focusGained(FocusEvent focusEvent) {
                AccesibilityUtils.showToolTip(true, jWindow, jButton, jLabel);
            }
        });
        Dimension dimension = new Dimension(20, 20);
        jButton.setPreferredSize(dimension);
        jButton.addKeyListener(new KeyListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.AbstractJAccessibilityCustomDialog.2
            public void keyTyped(KeyEvent keyEvent) {
            }

            public void keyReleased(KeyEvent keyEvent) {
            }

            public void keyPressed(KeyEvent keyEvent) {
                if (10 == keyEvent.getKeyCode()) {
                    jButton.doClick();
                }
            }
        });
        jButton.setName("restaurar");
        jPanel3.add(jButton);
        AccesibilityUtils.remarcar(jButton);
        jPanel2.add(jPanel3, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        JPanel jPanel4 = new JPanel();
        final JButton jButton2 = new JButton(new ImageIcon(InputPasswordSmartcardDialog.class.getResource("/images/maximize.png")));
        jButton2.setMnemonic(77);
        jButton2.setToolTipText(Messages.getString("Wizard.maximizar.description"));
        jButton2.getAccessibleContext().setAccessibleName(jButton2.getToolTipText());
        jButton2.addKeyListener(new KeyListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.AbstractJAccessibilityCustomDialog.3
            public void keyTyped(KeyEvent keyEvent) {
            }

            public void keyReleased(KeyEvent keyEvent) {
            }

            public void keyPressed(KeyEvent keyEvent) {
                if (10 == keyEvent.getKeyCode()) {
                    jButton2.doClick();
                }
            }
        });
        jButton2.setName("maximizar");
        jButton2.setPreferredSize(dimension);
        AccesibilityUtils.remarcar(jButton2);
        jPanel4.add(jButton2);
        jButton2.addFocusListener(new FocusListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.AbstractJAccessibilityCustomDialog.4
            public void focusLost(FocusEvent focusEvent) {
                AccesibilityUtils.showToolTip(false, jWindow, jButton2, jLabel);
            }

            public void focusGained(FocusEvent focusEvent) {
                AccesibilityUtils.showToolTip(true, jWindow, jButton2, jLabel);
            }
        });
        jPanel2.add(jPanel4, gridBagConstraints);
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = 0;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.weightx = 1.0d;
        gridBagConstraints2.weighty = 1.0d;
        gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints2.anchor = 15;
        jPanel.add(jPanel2, gridBagConstraints2);
        jButton.addActionListener(new ActionListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.AbstractJAccessibilityCustomDialog.5
            public void actionPerformed(ActionEvent actionEvent) {
                AbstractJAccessibilityCustomDialog.this.restaurarActionPerformed(jButton, jButton2);
            }
        });
        jButton2.addActionListener(new ActionListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.AbstractJAccessibilityCustomDialog.6
            public void actionPerformed(ActionEvent actionEvent) {
                AbstractJAccessibilityCustomDialog.this.maximizarActionPerformed(jButton, jButton2);
            }
        });
        if (GeneralConfig.isMaximized()) {
            jButton2.setEnabled(false);
            jButton.setEnabled(true);
        } else {
            jButton2.setEnabled(true);
            jButton.setEnabled(false);
        }
        return jPanel;
    }

    void maximizarActionPerformed(JButton jButton, JButton jButton2) {
        setActualPositionX(getX());
        setActualPositionY(getY());
        setActualWidth(getWidth());
        setActualHeight(getHeight());
        int maxWidth = getMaxWidth();
        int maxHeight = getMaxHeight();
        setBounds(getInitialX(maxWidth), getInitialY(maxHeight), maxWidth, maxHeight);
        jButton2.setEnabled(false);
        jButton.setEnabled(true);
    }

    void restaurarActionPerformed(JButton jButton, JButton jButton2) {
        int initialWidth = getInitialWidth();
        int initialHeight = getInitialHeight();
        if (GeneralConfig.isBigFontSize() || GeneralConfig.isFontBold() || isBigSizeDefault()) {
            initialWidth = 555;
            initialHeight = 250;
        }
        setMinimumSize(new Dimension(initialWidth, initialHeight));
        if (getActualPositionX() != -1 && getActualPositionY() != -1 && getActualWidth() != -1 && getActualHeight() != -1) {
            setBounds(getActualPositionX(), getActualPositionY(), getActualWidth(), getActualHeight());
        } else {
            setBounds(getInitialX(initialWidth), getInitialY(initialHeight), initialWidth, initialHeight);
        }
        jButton2.setEnabled(true);
        jButton.setEnabled(false);
    }

    private static int getInitialX(int i) {
        return (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (i / 2);
    }

    private static int getInitialY(int i) {
        return (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - (i / 2);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/jmulticard/ui/AbstractJAccessibilityCustomDialog$ButtonAbstractAction.class */
    protected static final class ButtonAbstractAction extends AbstractAction {
        private static final long serialVersionUID = 1;
        private final transient JButton button;

        ButtonAbstractAction(JButton jButton) {
            this.button = jButton;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            if (this.button != null) {
                this.button.doClick();
            }
        }
    }
}
