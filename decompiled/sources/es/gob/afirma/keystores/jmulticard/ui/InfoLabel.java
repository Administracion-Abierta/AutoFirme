package es.gob.afirma.keystores.jmulticard.ui;

import javax.swing.JLabel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/jmulticard/ui/InfoLabel.class */
final class InfoLabel extends JLabel {
    private static final long serialVersionUID = 1;

    InfoLabel(String str) {
        super("<HTML>" + str + "</HTML>");
        setOpaque(false);
        AccesibilityUtils.remarcar(this);
        AccesibilityUtils.setContrastColor(this);
        AccesibilityUtils.setFontBold(this);
    }

    InfoLabel(String str, boolean z) {
        super("<HTML>" + str + "</HTML>");
        setFocusable(true);
        setOpaque(z);
        AccesibilityUtils.remarcar(this);
        AccesibilityUtils.setContrastColor(this);
        AccesibilityUtils.setFontBold(this);
    }
}
