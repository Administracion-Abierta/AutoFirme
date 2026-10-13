package es.gob.afirma.standalone.ui.preferences;

import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/DefaultFormatPanel.class */
class DefaultFormatPanel extends JPanel {
    private static final long serialVersionUID = 4101194604941066022L;
    private static final String XADES = "XAdES";
    private static final String CADES = "CAdES";
    private static final String PADES = "PAdES";
    private static final String OOXML = "OOXML (Office Open XML)";
    private static final String FACTURAE = "FacturaE";
    private static final String ODF = "ODF (Open Document Format)";
    private JComboBox<String> pdfFilesCombo;
    private JComboBox<String> ooxmlFilesCombo;
    private JComboBox<String> facturaeFilesCombo;
    private JComboBox<String> xmlFilesCombo;
    private JComboBox<String> binFilesCombo;
    private JComboBox<String> odfFilesCombo;
    private JButton resetButton;

    public DefaultFormatPanel() {
        super(new GridBagLayout());
        Component jLabel = new JLabel(SimpleAfirmaMessages.getString("DefaultFormatPanel.1"));
        JPanel jPanelCreateFormatsPanel = createFormatsPanel();
        JPanel jPanelCreateButtonsPanel = createButtonsPanel();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridy = 0;
        add(jLabel, gridBagConstraints);
        gridBagConstraints.insets = new Insets(11, 0, 0, 0);
        gridBagConstraints.gridy++;
        add(jPanelCreateFormatsPanel, gridBagConstraints);
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.gridy++;
        add(jPanelCreateButtonsPanel, gridBagConstraints);
    }

    private JPanel createFormatsPanel() {
        this.pdfFilesCombo = new JComboBox<>(new String[]{"PAdES", "CAdES", "XAdES"});
        this.ooxmlFilesCombo = new JComboBox<>(new String[]{"OOXML (Office Open XML)", "CAdES", "XAdES"});
        this.facturaeFilesCombo = new JComboBox<>(new String[]{"FacturaE", "XAdES", "CAdES"});
        this.xmlFilesCombo = new JComboBox<>(new String[]{"XAdES", "CAdES"});
        this.binFilesCombo = new JComboBox<>(new String[]{"CAdES", "XAdES"});
        this.odfFilesCombo = new JComboBox<>(new String[]{"ODF (Open Document Format)", "CAdES", "XAdES"});
        JPanel jPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 7, 0, 7);
        JLabel jLabel = new JLabel(SimpleAfirmaMessages.getString("DefaultFormatPanel.5"));
        jLabel.setLabelFor(this.pdfFilesCombo);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 0.0d;
        jPanel.add(jLabel, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 1.0d;
        jPanel.add(this.pdfFilesCombo, gridBagConstraints);
        gridBagConstraints.gridy++;
        JLabel jLabel2 = new JLabel(SimpleAfirmaMessages.getString("DefaultFormatPanel.6"));
        jLabel2.setLabelFor(this.ooxmlFilesCombo);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 0.0d;
        jPanel.add(jLabel2, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 1.0d;
        jPanel.add(this.ooxmlFilesCombo, gridBagConstraints);
        gridBagConstraints.gridy++;
        JLabel jLabel3 = new JLabel(SimpleAfirmaMessages.getString("DefaultFormatPanel.7"));
        jLabel3.setLabelFor(this.facturaeFilesCombo);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 0.0d;
        jPanel.add(jLabel3, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 1.0d;
        jPanel.add(this.facturaeFilesCombo, gridBagConstraints);
        gridBagConstraints.gridy++;
        JLabel jLabel4 = new JLabel(SimpleAfirmaMessages.getString("DefaultFormatPanel.8"));
        jLabel4.setLabelFor(this.xmlFilesCombo);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 0.0d;
        jPanel.add(jLabel4, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 1.0d;
        jPanel.add(this.xmlFilesCombo, gridBagConstraints);
        gridBagConstraints.gridy++;
        JLabel jLabel5 = new JLabel(SimpleAfirmaMessages.getString("DefaultFormatPanel.9"));
        jLabel5.setLabelFor(this.odfFilesCombo);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 0.0d;
        jPanel.add(jLabel5, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 1.0d;
        jPanel.add(this.odfFilesCombo, gridBagConstraints);
        gridBagConstraints.gridy++;
        JLabel jLabel6 = new JLabel(SimpleAfirmaMessages.getString("DefaultFormatPanel.10"));
        jLabel6.setLabelFor(this.binFilesCombo);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 0.0d;
        jPanel.add(jLabel6, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        jPanel.add(this.binFilesCombo, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.weightx = 1.0d;
        return jPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel jPanel = new JPanel(new FlowLayout(4));
        this.resetButton = new JButton(SimpleAfirmaMessages.getString("DefaultFormatPanel.2"));
        this.resetButton.setMnemonic('r');
        this.resetButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("DefaultFormatPanel.3"));
        this.resetButton.addActionListener(actionEvent -> {
            resetPreferences();
        });
        jPanel.add(this.resetButton);
        return jPanel;
    }

    void setBlocked(boolean z) {
        boolean z2 = !z;
        this.pdfFilesCombo.setEnabled(z2);
        this.ooxmlFilesCombo.setEnabled(z2);
        this.facturaeFilesCombo.setEnabled(z2);
        this.odfFilesCombo.setEnabled(z2);
        this.xmlFilesCombo.setEnabled(z2);
        this.binFilesCombo.setEnabled(z2);
    }

    void loadPreferences() {
        this.pdfFilesCombo.setSelectedItem(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_PDF));
        this.ooxmlFilesCombo.setSelectedItem(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_OOXML));
        this.facturaeFilesCombo.setSelectedItem(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_FACTURAE));
        this.odfFilesCombo.setSelectedItem(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_ODF));
        this.xmlFilesCombo.setSelectedItem(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_XML));
        this.binFilesCombo.setSelectedItem(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_BIN));
    }

    void resetPreferences() {
        this.pdfFilesCombo.setSelectedItem(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_PDF, PreferencesManager.PreferencesSource.DEFAULT));
        this.ooxmlFilesCombo.setSelectedItem(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_OOXML, PreferencesManager.PreferencesSource.DEFAULT));
        this.facturaeFilesCombo.setSelectedItem(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_FACTURAE, PreferencesManager.PreferencesSource.DEFAULT));
        this.odfFilesCombo.setSelectedItem(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_ODF, PreferencesManager.PreferencesSource.DEFAULT));
        this.xmlFilesCombo.setSelectedItem(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_XML, PreferencesManager.PreferencesSource.DEFAULT));
        this.binFilesCombo.setSelectedItem(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_BIN, PreferencesManager.PreferencesSource.DEFAULT));
    }

    void savePreferences() {
        PreferencesManager.put(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_BIN, this.binFilesCombo.getSelectedItem().toString());
        PreferencesManager.put(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_FACTURAE, this.facturaeFilesCombo.getSelectedItem().toString());
        PreferencesManager.put(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_OOXML, this.ooxmlFilesCombo.getSelectedItem().toString());
        PreferencesManager.put(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_PDF, this.pdfFilesCombo.getSelectedItem().toString());
        PreferencesManager.put(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_XML, this.xmlFilesCombo.getSelectedItem().toString());
        PreferencesManager.put(PreferencesManager.PREFERENCE_GENERAL_DEFAULT_FORMAT_ODF, this.odfFilesCombo.getSelectedItem().toString());
    }
}
