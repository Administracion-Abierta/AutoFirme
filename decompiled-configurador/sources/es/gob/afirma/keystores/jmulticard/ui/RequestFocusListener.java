package es.gob.afirma.keystores.jmulticard.ui;

import javax.swing.JComponent;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/jmulticard/ui/RequestFocusListener.class */
final class RequestFocusListener implements AncestorListener {
    private final transient boolean removeListener;

    RequestFocusListener() {
        this(true);
    }

    RequestFocusListener(boolean z) {
        this.removeListener = z;
    }

    public void ancestorAdded(AncestorEvent ancestorEvent) {
        JComponent component = ancestorEvent.getComponent();
        component.requestFocusInWindow();
        if (this.removeListener) {
            component.removeAncestorListener(this);
        }
    }

    public void ancestorMoved(AncestorEvent ancestorEvent) {
    }

    public void ancestorRemoved(AncestorEvent ancestorEvent) {
    }
}
