package es.gob.afirma.keystores.jmulticard.ui;

import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.JLabel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/jmulticard/ui/IconLabel.class */
final class IconLabel extends JLabel {
    private static final long serialVersionUID = 1;
    private Icon icon = null;
    private Icon originalIcon = null;
    private Dimension originalDimension;

    IconLabel() {
    }

    public Icon getIcon() {
        return this.icon;
    }

    Icon getOriginalIcon() {
        return this.originalIcon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    void setOriginalIcon(Icon icon) {
        this.originalIcon = icon;
    }

    public Dimension getOriginalDimension() {
        return this.originalDimension;
    }

    public void setOriginalDimension(Dimension dimension) {
        this.originalDimension = dimension;
    }
}
