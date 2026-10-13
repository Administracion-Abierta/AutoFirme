package es.gob.afirma.standalone.ui.preferences;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/PreferencesPanel.class */
final class PreferencesPanel extends JPanel implements KeyListener, DisposableInterface {
    private static final long serialVersionUID = -3168095095548385291L;
    private final JButton applyButton;
    private final ModificationListener modificationListener;
    private final Window window;
    private PreferencesPanelGeneral preferencesPanelGeneral;
    private PreferencesPanelCades preferencesPanelCades;
    private PreferencesPanelPades preferencesPanelPades;
    private PreferencesPanelFacturaE preferencesPanelFacturaE;
    private PreferencesPanelXades preferencesPanelXades;
    private PreferencesPanelKeystores preferencesPanelKeystores;
    private final JTabbedPane tabbedPane;

    Window getParentWindow() {
        return this.window;
    }

    void createUI(int i) {
        boolean z = PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_BLOCKED);
        this.preferencesPanelGeneral = new PreferencesPanelGeneral(this, this.modificationListener, this, this, z);
        this.preferencesPanelCades = new PreferencesPanelCades(this, this.modificationListener, z);
        this.preferencesPanelPades = new PreferencesPanelPades(this, this.modificationListener, z);
        this.preferencesPanelFacturaE = new PreferencesPanelFacturaE(this, this.modificationListener, z);
        this.preferencesPanelXades = new PreferencesPanelXades(this, this.modificationListener, z);
        this.preferencesPanelKeystores = new PreferencesPanelKeystores(this, this.modificationListener, z);
        this.tabbedPane.setPreferredSize(new Dimension(600, (int) Math.min(610.0d, LookAndFeelManager.getScreenSize().getHeight() * 0.8d)));
        this.tabbedPane.addKeyListener(this);
        int tabCount = this.tabbedPane.getTabCount();
        this.tabbedPane.addTab(SimpleAfirmaMessages.getString("PreferencesPanel.2"), (Icon) null, this.preferencesPanelGeneral, SimpleAfirmaMessages.getString("PreferencesPanel.40"));
        this.tabbedPane.setMnemonicAt(tabCount, 71);
        int tabCount2 = this.tabbedPane.getTabCount();
        this.tabbedPane.addTab(SimpleAfirmaMessages.getString("PreferencesPanel.3"), (Icon) null, this.preferencesPanelPades, SimpleAfirmaMessages.getString("PreferencesPanel.41"));
        this.tabbedPane.setMnemonicAt(tabCount2, 70);
        int tabCount3 = this.tabbedPane.getTabCount();
        this.tabbedPane.addTab(SimpleAfirmaMessages.getString("PreferencesPanel.4"), (Icon) null, this.preferencesPanelCades, SimpleAfirmaMessages.getString("PreferencesPanel.42"));
        this.tabbedPane.setMnemonicAt(tabCount3, 69);
        int tabCount4 = this.tabbedPane.getTabCount();
        this.tabbedPane.addTab(SimpleAfirmaMessages.getString("PreferencesPanel.5"), (Icon) null, this.preferencesPanelXades, SimpleAfirmaMessages.getString("PreferencesPanel.43"));
        this.tabbedPane.setMnemonicAt(tabCount4, 88);
        int tabCount5 = this.tabbedPane.getTabCount();
        this.tabbedPane.addTab(SimpleAfirmaMessages.getString("PreferencesPanel.100"), (Icon) null, this.preferencesPanelFacturaE, SimpleAfirmaMessages.getString("PreferencesPanel.101"));
        this.tabbedPane.setMnemonicAt(tabCount5, 76);
        int tabCount6 = this.tabbedPane.getTabCount();
        this.tabbedPane.addTab(SimpleAfirmaMessages.getString("PreferencesPanel.192"), (Icon) null, this.preferencesPanelKeystores, SimpleAfirmaMessages.getString("PreferencesPanel.193"));
        this.tabbedPane.setMnemonicAt(tabCount6, 78);
        this.tabbedPane.setSelectedIndex(i);
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridy = 0;
        add(this.tabbedPane, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.ipady = 11;
        add(createButtonsPanel(), gridBagConstraints);
    }

    boolean savePreferences() {
        if (!checkPreferences()) {
            return false;
        }
        this.preferencesPanelFacturaE.savePreferences();
        this.preferencesPanelGeneral.savePreferences();
        this.preferencesPanelCades.savePreferences();
        this.preferencesPanelPades.savePreferences();
        this.preferencesPanelXades.savePreferences();
        this.preferencesPanelKeystores.savePreferences();
        try {
            PreferencesManager.flush();
            return true;
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").severe("Error al guardar las preferencias de firma: " + e);
            return true;
        }
    }

    void loadPreferences() {
        this.preferencesPanelGeneral.loadPreferences();
    }

    private boolean checkPreferences() {
        try {
            this.preferencesPanelXades.checkPreferences();
            try {
                this.preferencesPanelPades.checkPreferences();
                try {
                    this.preferencesPanelCades.checkPreferences();
                    return true;
                } catch (Exception e) {
                    AOUIFactory.showErrorMessage("<p>" + SimpleAfirmaMessages.getString("PreferencesPanel.38") + "</p>", SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e);
                    this.tabbedPane.setSelectedIndex(2);
                    return false;
                }
            } catch (Exception e2) {
                AOUIFactory.showErrorMessage("<p>" + SimpleAfirmaMessages.getString("PreferencesPanel.7") + "</p>", SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e2);
                this.tabbedPane.setSelectedIndex(1);
                return false;
            }
        } catch (Exception e3) {
            AOUIFactory.showErrorMessage("<p>" + SimpleAfirmaMessages.getString("PreferencesPanel.6") + "</p>", SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e3);
            this.tabbedPane.setSelectedIndex(3);
            return false;
        }
    }

    private JPanel createButtonsPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout(2));
        JButton jButton = new JButton(SimpleAfirmaMessages.getString("PreferencesPanel.31"));
        jButton.setMnemonic('C');
        jButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.32"));
        jButton.addKeyListener(this);
        jButton.addActionListener(actionEvent -> {
            disposeInterface();
        });
        JButton jButton2 = new JButton(SimpleAfirmaMessages.getString("PreferencesPanel.33"));
        jButton2.setMnemonic('A');
        jButton2.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.34"));
        jButton2.addKeyListener(this);
        jButton2.addActionListener(actionEvent2 -> {
            if (savePreferences()) {
                disposeInterface();
            }
        });
        this.applyButton.setMnemonic('p');
        this.applyButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.35"));
        this.applyButton.addKeyListener(this);
        this.applyButton.addActionListener(actionEvent3 -> {
            if (savePreferences()) {
                setModified(false);
            }
        });
        this.applyButton.setEnabled(false);
        if (Platform.OS.MACOSX.equals(Platform.getOS())) {
            jPanel.add(jButton);
            jPanel.add(this.applyButton);
            jPanel.add(jButton2);
        } else {
            jPanel.add(this.applyButton);
            jPanel.add(jButton2);
            jPanel.add(jButton);
        }
        return jPanel;
    }

    PreferencesPanel(Window window) {
        this(window, 0);
    }

    PreferencesPanel(Window window, int i) {
        this.applyButton = new JButton(SimpleAfirmaMessages.getString("PreferencesPanel.0"));
        this.tabbedPane = new JTabbedPane();
        if (window == null) {
            throw new IllegalArgumentException("Es necesario proporcionar una referencia a la ventana contenedora");
        }
        this.window = window;
        this.modificationListener = new ModificationListener(this);
        createUI(i);
    }

    void setModified(boolean z) {
        this.applyButton.setEnabled(z);
    }

    public void keyPressed(KeyEvent keyEvent) {
    }

    public void keyReleased(KeyEvent keyEvent) {
        if (keyEvent != null && keyEvent.getKeyCode() == 27 && !Platform.OS.MACOSX.equals(Platform.getOS())) {
            disposeInterface();
        }
    }

    public void keyTyped(KeyEvent keyEvent) {
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/PreferencesPanel$ValueTextPair.class */
    static final class ValueTextPair {
        private final String value;
        private final String text;

        public ValueTextPair(String str) {
            this.value = str;
            this.text = str;
        }

        public ValueTextPair(String str, String str2) {
            this.value = str;
            this.text = str2;
        }

        public String getValue() {
            return this.value;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj instanceof ValueTextPair) {
                return this.value.equals(((ValueTextPair) obj).value);
            }
            return this.value.equals(obj.toString());
        }

        public String toString() {
            return this.text;
        }

        public int hashCode() {
            return (5 * this.text.length()) + (7 * this.value.length());
        }
    }

    @Override // es.gob.afirma.standalone.ui.preferences.DisposableInterface
    public void disposeInterface() {
        getParentWindow().dispose();
    }
}
