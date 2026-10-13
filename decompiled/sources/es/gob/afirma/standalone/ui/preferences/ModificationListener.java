package es.gob.afirma.standalone.ui.preferences;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/ModificationListener.class */
final class ModificationListener extends KeyAdapter implements ItemListener {
    private final PreferencesPanel prefPanel;

    ModificationListener(PreferencesPanel preferencesPanel) {
        if (preferencesPanel == null) {
            throw new IllegalArgumentException("Se necesita un ModificationListener para indicar que ha habido modificaciones, no puede ser nulo");
        }
        this.prefPanel = preferencesPanel;
    }

    public void keyReleased(KeyEvent keyEvent) {
        this.prefPanel.setModified(true);
    }

    public void itemStateChanged(ItemEvent itemEvent) {
        this.prefPanel.setModified(true);
    }
}
