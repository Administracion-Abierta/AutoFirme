package es.gob.afirma.standalone.ui;

import es.gob.afirma.standalone.LookAndFeelManager;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.swing.JLabel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/LabelLinkManager.class */
public final class LabelLinkManager extends KeyAdapter implements FocusListener, MouseListener {
    private final JLabel label;
    private LabelLinkListener labelLinkListener;

    public LabelLinkManager(JLabel jLabel) {
        this.label = jLabel;
        Font font = this.label.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        this.label.setFont(font.deriveFont(attributes));
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            this.label.setForeground(Color.BLUE);
        } else {
            this.label.setForeground(Color.YELLOW);
        }
        this.label.setBackground((Color) null);
        this.label.setCursor(new Cursor(12));
        this.label.setOpaque(true);
        this.label.setFocusable(true);
        this.label.addMouseListener(this);
        this.label.addFocusListener(this);
        this.label.addKeyListener(this);
    }

    public void setLabelLinkListener(LabelLinkListener labelLinkListener) {
        this.labelLinkListener = labelLinkListener;
    }

    public void focusGained(FocusEvent focusEvent) {
        this.label.setOpaque(true);
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            this.label.setForeground(Color.WHITE);
        } else {
            this.label.setForeground(Color.YELLOW);
        }
        this.label.setBackground(Color.blue);
        this.label.repaint();
    }

    public void focusLost(FocusEvent focusEvent) {
        this.label.setOpaque(false);
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            this.label.setForeground(Color.BLUE);
        } else {
            this.label.setForeground(Color.YELLOW);
        }
        this.label.setBackground((Color) null);
        this.label.repaint();
    }

    public void mousePressed(MouseEvent mouseEvent) {
    }

    public void mouseReleased(MouseEvent mouseEvent) {
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        this.label.requestFocus();
        focusGained(null);
        if (this.labelLinkListener != null) {
            this.labelLinkListener.openLink();
        }
    }

    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case 10:
            case 32:
                if (this.labelLinkListener != null) {
                    this.labelLinkListener.openLink();
                }
                break;
        }
    }
}
