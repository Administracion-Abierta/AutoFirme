package es.gob.afirma.ui.core.jse.certificateselection;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/certificateselection/CertificateSelectionDispatcherListener.class */
final class CertificateSelectionDispatcherListener implements KeyEventDispatcher {
    private final Component parent;
    private final CertificateSelectionDialog selectionDialog;

    CertificateSelectionDispatcherListener(Component component, CertificateSelectionDialog certificateSelectionDialog) {
        this.parent = component;
        this.selectionDialog = certificateSelectionDialog;
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getID() == 402) {
            if (112 == keyEvent.getKeyCode()) {
                UtilActions.doHelp();
                return false;
            }
            if (116 == keyEvent.getKeyCode()) {
                UtilActions.doRefresh(this.selectionDialog, this.parent);
                return false;
            }
            return false;
        }
        return false;
    }
}
