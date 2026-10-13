package es.gob.afirma.standalone.ui;

import es.gob.afirma.standalone.plugins.PluginButton;
import javax.swing.JButton;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/PluginGraphicButton.class */
public class PluginGraphicButton {
    private final JButton graphicButton;
    private final PluginButton button;

    public PluginGraphicButton(PluginButton pluginButton, JButton jButton) {
        this.button = pluginButton;
        this.graphicButton = jButton;
    }

    public JButton getGraphicButton() {
        return this.graphicButton;
    }

    public PluginButton getButton() {
        return this.button;
    }
}
