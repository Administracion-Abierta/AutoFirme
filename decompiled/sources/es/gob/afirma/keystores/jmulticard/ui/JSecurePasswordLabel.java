package es.gob.afirma.keystores.jmulticard.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.Timer;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/jmulticard/ui/JSecurePasswordLabel.class */
final class JSecurePasswordLabel extends JLabel {
    private static final long serialVersionUID = -4343328489072897605L;
    private final transient int delay = 500;
    final char[] pass;
    private final int maxChars;
    transient int passwordLength;
    private final Timer timer;
    private boolean showCursor;

    char[] getPass() {
        return this.pass;
    }

    Timer getTimer() {
        return this.timer;
    }

    int getMaxChars() {
        return this.maxChars;
    }

    JSecurePasswordLabel(int i) {
        this.maxChars = i;
        this.pass = new char[i];
        clearPassword();
        addKeyListener(new KeyAdapter() { // from class: es.gob.afirma.keystores.jmulticard.ui.JSecurePasswordLabel.1
            public void keyTyped(KeyEvent keyEvent) {
                if (JSecurePasswordLabel.this.getPasswordLength() < JSecurePasswordLabel.this.getMaxChars() && keyEvent.getKeyChar() != '\b' && keyEvent.getKeyChar() != 127 && keyEvent.getKeyChar() != '\n' && keyEvent.getKeyChar() != '\t' && keyEvent.getModifiersEx() != 128 && keyEvent.getModifiersEx() != 512) {
                    char[] pass = JSecurePasswordLabel.this.getPass();
                    JSecurePasswordLabel jSecurePasswordLabel = JSecurePasswordLabel.this;
                    int i2 = jSecurePasswordLabel.passwordLength;
                    jSecurePasswordLabel.passwordLength = i2 + 1;
                    pass[i2] = keyEvent.getKeyChar();
                    keyEvent.setKeyChar((char) 0);
                }
                JSecurePasswordLabel.this.updateText();
            }

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 8 && JSecurePasswordLabel.this.getPasswordLength() > 0) {
                    JSecurePasswordLabel.this.clearPassword(JSecurePasswordLabel.this.getPasswordLength() - 1);
                } else if (keyEvent.getKeyCode() == 127) {
                    JSecurePasswordLabel.this.clearPassword();
                }
                JSecurePasswordLabel.this.updateText();
            }
        });
        addFocusListener(new FocusListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.JSecurePasswordLabel.2
            public void focusGained(FocusEvent focusEvent) {
                JSecurePasswordLabel.this.setBackground(Color.WHITE);
                JSecurePasswordLabel.this.getTimer().start();
            }

            public void focusLost(FocusEvent focusEvent) {
                JSecurePasswordLabel.this.setShowCursor(false);
                JSecurePasswordLabel.this.setBackground(JSecurePasswordLabel.this.getParent().getBackground());
                JSecurePasswordLabel.this.getTimer().stop();
                JSecurePasswordLabel.this.updateText();
            }
        });
        addMouseListener(new MouseAdapter() { // from class: es.gob.afirma.keystores.jmulticard.ui.JSecurePasswordLabel.3
            public void mouseClicked(MouseEvent mouseEvent) {
                JSecurePasswordLabel.this.requestFocus();
            }
        });
        setFocusable(true);
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLoweredBevelBorder());
        setShowCursor(false);
        getClass();
        this.timer = new Timer(500, new ActionListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.JSecurePasswordLabel.4
            public void actionPerformed(ActionEvent actionEvent) {
                if (actionEvent.getSource().equals(JSecurePasswordLabel.this.getTimer()) && JSecurePasswordLabel.this.hasFocus()) {
                    JSecurePasswordLabel.this.setShowCursor(!JSecurePasswordLabel.this.getShowCursor());
                    JSecurePasswordLabel.this.updateText();
                }
            }
        });
        this.timer.stop();
    }

    synchronized void updateText() {
        StringBuilder sb = new StringBuilder(" ");
        for (int i = 0; i < this.passwordLength; i++) {
            sb.append('*');
        }
        sb.append(getShowCursor() ? "|" : " ");
        setText(sb.toString());
    }

    void clearPassword() {
        clearPassword(0);
    }

    void clearPassword(int i) {
        for (int i2 = i; i2 < this.pass.length; i2++) {
            this.pass[i2] = 0;
        }
        this.passwordLength = i;
        updateText();
    }

    char[] getPassword() {
        char[] cArr = new char[this.passwordLength];
        for (int i = 0; i < this.passwordLength; i++) {
            cArr[i] = this.pass[i];
        }
        clearPassword();
        return cArr;
    }

    int getPasswordLength() {
        return this.passwordLength;
    }

    synchronized void setShowCursor(boolean z) {
        this.showCursor = z;
    }

    synchronized boolean getShowCursor() {
        return this.showCursor;
    }
}
