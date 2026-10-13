package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.misc.Platform;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/ResizingTextPanel.class */
final class ResizingTextPanel extends JPanel {
    private static final long serialVersionUID = -5496697696047898537L;
    private static final int MARGIN = 30;
    private final String text;
    private Font font;

    private void changeInternalFont() {
        FontMetrics fontMetrics;
        FontMetrics fontMetrics2 = getFontMetrics(this.font);
        while (true) {
            fontMetrics = fontMetrics2;
            if (getWidth() <= fontMetrics.stringWidth(this.text) + 90) {
                break;
            }
            this.font = this.font.deriveFont(this.font.getSize() + 1.0f);
            fontMetrics2 = getFontMetrics(this.font);
        }
        while (getWidth() <= fontMetrics.stringWidth(this.text) + 90) {
            this.font = this.font.deriveFont(this.font.getSize() - 1.0f);
            fontMetrics = getFontMetrics(this.font);
        }
    }

    ResizingTextPanel(String str) {
        super(true);
        this.text = str != null ? str : "";
        this.font = getFont();
        if (Platform.OS.LINUX.equals(Platform.getOS())) {
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        changeInternalFont();
        graphics.setFont(this.font);
        graphics.drawString(this.text, 30, getSize().height / 2);
    }
}
