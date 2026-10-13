package es.gob.afirma.standalone.ui;

import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.awt.GridLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/ClosePanel.class */
public final class ClosePanel extends JPanel {
    private static final long serialVersionUID = -2848106901208270337L;
    private final JCheckBox ask = new JCheckBox(SimpleAfirmaMessages.getString("ClosePanel.0"));

    JCheckBox getAskCheckBox() {
        return this.ask;
    }

    public ClosePanel() {
        setLayout(new GridLayout(0, 1));
        add(new JLabel(SimpleAfirmaMessages.getString("SimpleAfirma.47")));
        this.ask.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_OMIT_ASKONCLOSE));
        this.ask.addItemListener(itemEvent -> {
            PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_GENERAL_OMIT_ASKONCLOSE, getAskCheckBox().isSelected());
        });
        this.ask.setMnemonic('m');
        add(this.ask);
    }
}
