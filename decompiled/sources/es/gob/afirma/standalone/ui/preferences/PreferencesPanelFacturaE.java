package es.gob.afirma.standalone.ui.preferences;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AdESPolicy;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/PreferencesPanelFacturaE.class */
final class PreferencesPanelFacturaE extends JScrollPane {
    private static final long serialVersionUID = 4299378019540627483L;
    private static final String FACTURAE_ROL_EMISOR = "Emisor";
    private static final String FACTURAE_ROL_RECEPTOR = "Receptor";
    private static final String FACTURAE_ROL_TERCERO = "Tercero";
    private static final String SIGN_FORMAT_FACTURAE = "FacturaE";
    private static final String POLICY_FACTURAE_31_NAME = "3.1";
    private static final String POLICY_FACTURAE_30_NAME = "3.0";
    private PolicyPanel facturaePolicyPanel;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final AdESPolicy POLICY_FACTURAE_31 = new AdESPolicy("http://www.facturae.es/politica_de_firma_formato_facturae/politica_de_firma_formato_facturae_v3_1.pdf", "Ohixl6upD6av8N7pEvDABhEL6hM=", AOSignConstants.DIGEST_ALGORITHM_SHA1, null);
    private static final AdESPolicy POLICY_FACTURAE_30 = new AdESPolicy("http://www.facturae.es/politica de firma formato facturae/politica de firma formato facturae v3_0.pdf", "xmfh8D/Ec/hHeE1IB4zPd61zHIY=", AOSignConstants.DIGEST_ALGORITHM_SHA1, null);
    private final JTextField facturaeSignatureProductionCity = new JTextField();
    private final JTextField facturaeSignatureProductionProvince = new JTextField();
    private final JTextField facturaeSignatureProductionPostalCode = new JTextField();
    private final JTextField facturaeSignatureProductionCountry = new JTextField();
    private final JComboBox<Object> facturaeRol = new JComboBox<>(new Object[]{FACTURAE_ROL_EMISOR, FACTURAE_ROL_RECEPTOR, FACTURAE_ROL_TERCERO});
    private final JPanel panelPolicies = new JPanel();
    private boolean blocked = true;

    PreferencesPanelFacturaE(KeyListener keyListener, ModificationListener modificationListener, boolean z) {
        setBlocked(z);
        createUI(keyListener, modificationListener);
    }

    void createUI(KeyListener keyListener, ModificationListener modificationListener) {
        getVerticalScrollBar().setUnitIncrement(16);
        JPanel jPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridy = 0;
        loadPreferences();
        this.panelPolicies.setLayout(new GridBagLayout());
        this.panelPolicies.add(this.facturaePolicyPanel, gridBagConstraints);
        gridBagConstraints.gridy++;
        jPanel.add(this.panelPolicies, gridBagConstraints);
        this.facturaePolicyPanel.setModificationListener(modificationListener);
        this.facturaePolicyPanel.setKeyListener(keyListener);
        JPanel jPanel2 = new JPanel();
        jPanel2.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanelFacturaE.4")));
        jPanel2.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = 1;
        gridBagConstraints2.weightx = 1.0d;
        gridBagConstraints2.gridy = 0;
        JLabel jLabel = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.11"));
        jLabel.setLabelFor(this.facturaeSignatureProductionCity);
        gridBagConstraints2.gridy++;
        jPanel2.add(jLabel, gridBagConstraints2);
        this.facturaeSignatureProductionCity.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.181"));
        this.facturaeSignatureProductionCity.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.66"));
        this.facturaeSignatureProductionCity.addKeyListener(modificationListener);
        this.facturaeSignatureProductionCity.addKeyListener(keyListener);
        gridBagConstraints2.gridy++;
        jPanel2.add(this.facturaeSignatureProductionCity, gridBagConstraints2);
        JLabel jLabel2 = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.14"));
        jLabel2.setLabelFor(this.facturaeSignatureProductionProvince);
        gridBagConstraints2.gridy++;
        jPanel2.add(jLabel2, gridBagConstraints2);
        this.facturaeSignatureProductionProvince.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.181"));
        this.facturaeSignatureProductionProvince.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.68"));
        this.facturaeSignatureProductionProvince.addKeyListener(modificationListener);
        this.facturaeSignatureProductionProvince.addKeyListener(keyListener);
        gridBagConstraints2.gridy++;
        jPanel2.add(this.facturaeSignatureProductionProvince, gridBagConstraints2);
        JLabel jLabel3 = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.102"));
        jLabel3.setLabelFor(this.facturaeSignatureProductionPostalCode);
        gridBagConstraints2.gridy++;
        jPanel2.add(jLabel3, gridBagConstraints2);
        this.facturaeSignatureProductionPostalCode.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.181"));
        this.facturaeSignatureProductionPostalCode.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.102"));
        this.facturaeSignatureProductionPostalCode.addKeyListener(modificationListener);
        this.facturaeSignatureProductionPostalCode.addKeyListener(keyListener);
        gridBagConstraints2.gridy++;
        jPanel2.add(this.facturaeSignatureProductionPostalCode, gridBagConstraints2);
        JLabel jLabel4 = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.12"));
        jLabel4.setLabelFor(this.facturaeSignatureProductionCountry);
        gridBagConstraints2.gridy++;
        jPanel2.add(jLabel4, gridBagConstraints2);
        this.facturaeSignatureProductionCountry.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.181"));
        this.facturaeSignatureProductionCountry.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.67"));
        this.facturaeSignatureProductionCountry.addKeyListener(modificationListener);
        this.facturaeSignatureProductionCountry.addKeyListener(keyListener);
        gridBagConstraints2.gridy++;
        jPanel2.add(this.facturaeSignatureProductionCountry, gridBagConstraints2);
        JPanel jPanel3 = new JPanel(new FlowLayout(3));
        jPanel3.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanel.69")));
        JPanel jPanel4 = new JPanel(new GridBagLayout());
        this.facturaeRol.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.183"));
        this.facturaeRol.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.53"));
        this.facturaeRol.addItemListener(modificationListener);
        JLabel jLabel5 = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanelFacturaE.3"));
        jLabel5.setLabelFor(this.facturaeRol);
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.anchor = 21;
        gridBagConstraints3.insets = new Insets(0, 7, 4, 7);
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.gridy = 0;
        jPanel4.add(jLabel5, gridBagConstraints3);
        gridBagConstraints3.gridx++;
        jPanel4.add(this.facturaeRol, gridBagConstraints3);
        jPanel3.add(jPanel4);
        gridBagConstraints.gridy++;
        jPanel.add(jPanel3, gridBagConstraints);
        gridBagConstraints.gridy++;
        jPanel.add(jPanel2, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.weighty = 1.0d;
        jPanel.add(new JPanel(), gridBagConstraints);
        JPanel jPanel5 = new JPanel(new FlowLayout(4));
        JButton jButton = new JButton(SimpleAfirmaMessages.getString("PreferencesPanel.147"));
        jButton.setMnemonic('R');
        jButton.addActionListener(actionEvent -> {
            if (AOUIFactory.showConfirmDialog(getParent(), SimpleAfirmaMessages.getString("PreferencesPanel.158"), SimpleAfirmaMessages.getString("PreferencesPanel.139"), 0, 2) == 0) {
                restorePreferences();
            }
        });
        jButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.136"));
        gridBagConstraints.gridy++;
        gridBagConstraints.weighty = 0.0d;
        jPanel5.add(jButton, gridBagConstraints);
        gridBagConstraints.gridy++;
        jPanel.add(jPanel5, gridBagConstraints);
        setViewportView(jPanel);
    }

    void savePreferences() {
        PreferencesManager.put(PreferencesManager.PREFERENCE_FACTURAE_SIGNER_ROLE, this.facturaeRol.getSelectedItem().toString());
        PreferencesManager.put(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_CITY, this.facturaeSignatureProductionCity.getText());
        PreferencesManager.put(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_PROVINCE, this.facturaeSignatureProductionProvince.getText());
        PreferencesManager.put(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_POSTAL_CODE, this.facturaeSignatureProductionPostalCode.getText());
        PreferencesManager.put(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_COUNTRY, this.facturaeSignatureProductionCountry.getText());
        AdESPolicy selectedPolicy = this.facturaePolicyPanel.getSelectedPolicy();
        if (selectedPolicy != null) {
            if (this.facturaePolicyPanel.getCurrentPolicyItem().toString().equals(SimpleAfirmaMessages.getString("PreferencesPanelFacturaE.1"))) {
                PreferencesManager.put(PreferencesManager.PREFERENCE_FACTURAE_POLICY, POLICY_FACTURAE_30_NAME);
            } else if (this.facturaePolicyPanel.getCurrentPolicyItem().toString().equals(SimpleAfirmaMessages.getString("PreferencesPanelFacturaE.2"))) {
                PreferencesManager.put(PreferencesManager.PREFERENCE_FACTURAE_POLICY, POLICY_FACTURAE_31_NAME);
            }
            PreferencesManager.put(PreferencesManager.PREFERENCE_FACTURAE_POLICY_IDENTIFIER, selectedPolicy.getPolicyIdentifier());
            PreferencesManager.put(PreferencesManager.PREFERENCE_FACTURAE_POLICY_IDENTIFIER_HASH, selectedPolicy.getPolicyIdentifierHash());
            PreferencesManager.put(PreferencesManager.PREFERENCE_FACTURAE_POLICY_IDENTIFIER_HASH_ALGORITHM, selectedPolicy.getPolicyIdentifierHashAlgorithm());
            if (selectedPolicy.getPolicyQualifier() != null) {
                PreferencesManager.put(PreferencesManager.PREFERENCE_FACTURAE_POLICY_QUALIFIER, selectedPolicy.getPolicyQualifier().toString());
            } else {
                PreferencesManager.remove(PreferencesManager.PREFERENCE_FACTURAE_POLICY_QUALIFIER);
            }
        } else {
            PreferencesManager.remove(PreferencesManager.PREFERENCE_FACTURAE_POLICY_IDENTIFIER);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_FACTURAE_POLICY_IDENTIFIER_HASH);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_FACTURAE_POLICY_IDENTIFIER_HASH_ALGORITHM);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_FACTURAE_POLICY_QUALIFIER);
        }
        this.facturaePolicyPanel.saveCurrentPolicy();
    }

    void loadPreferences() {
        this.facturaeRol.setSelectedItem(PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_SIGNER_ROLE));
        this.facturaeSignatureProductionCity.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_CITY));
        this.facturaeSignatureProductionProvince.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_PROVINCE));
        this.facturaeSignatureProductionPostalCode.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_POSTAL_CODE));
        this.facturaeSignatureProductionCountry.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_COUNTRY));
        ArrayList arrayList = new ArrayList();
        arrayList.add(new PolicyItem(SimpleAfirmaMessages.getString("PreferencesPanelFacturaE.2"), POLICY_FACTURAE_31));
        arrayList.add(new PolicyItem(SimpleAfirmaMessages.getString("PreferencesPanelFacturaE.1"), POLICY_FACTURAE_30));
        this.panelPolicies.removeAll();
        this.facturaePolicyPanel = new PolicyPanel("FacturaE", arrayList, getFacturaEPreferedPolicy(), false, false, false, isBlocked());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridy = 0;
        this.panelPolicies.add(this.facturaePolicyPanel, gridBagConstraints);
        revalidate();
        repaint();
    }

    void restorePreferences() {
        PreferencesManager.remove(PreferencesManager.PREFERENCE_FACTURAE_SIGNER_ROLE);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_CITY);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_PROVINCE);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_POSTAL_CODE);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_COUNTRY);
        this.facturaeRol.setSelectedItem(PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_SIGNER_ROLE));
        this.facturaeSignatureProductionCity.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_CITY));
        this.facturaeSignatureProductionProvince.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_PROVINCE));
        this.facturaeSignatureProductionPostalCode.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_POSTAL_CODE));
        this.facturaeSignatureProductionCountry.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_COUNTRY));
        if (!isBlocked()) {
            PreferencesManager.remove(PreferencesManager.PREFERENCE_FACTURAE_POLICY);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_FACTURAE_POLICY_IDENTIFIER);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_FACTURAE_POLICY_IDENTIFIER_HASH);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_FACTURAE_POLICY_IDENTIFIER_HASH_ALGORITHM);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_FACTURAE_POLICY_QUALIFIER);
            ArrayList arrayList = new ArrayList();
            arrayList.add(new PolicyItem(SimpleAfirmaMessages.getString("PreferencesPanelFacturaE.2"), POLICY_FACTURAE_31));
            arrayList.add(new PolicyItem(SimpleAfirmaMessages.getString("PreferencesPanelFacturaE.1"), POLICY_FACTURAE_30));
            this.panelPolicies.removeAll();
            this.facturaePolicyPanel = new PolicyPanel("FacturaE", arrayList, getFacturaEDefaultPolicy(), false, false, false, isBlocked());
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = 1;
            gridBagConstraints.weightx = 1.0d;
            gridBagConstraints.gridy = 0;
            this.panelPolicies.add(this.facturaePolicyPanel, gridBagConstraints);
        }
        revalidate();
        repaint();
    }

    private static AdESPolicy getFacturaEPreferedPolicy() {
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_POLICY);
        if (str.equals(POLICY_FACTURAE_30_NAME)) {
            return POLICY_FACTURAE_30;
        }
        if (str.equals(POLICY_FACTURAE_31_NAME)) {
            return POLICY_FACTURAE_31;
        }
        return null;
    }

    private AdESPolicy getFacturaEDefaultPolicy() {
        AdESPolicy selectedPolicy = null;
        if (isBlocked()) {
            selectedPolicy = this.facturaePolicyPanel.getSelectedPolicy();
        } else {
            String str = PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_POLICY);
            if (str.equals(POLICY_FACTURAE_30_NAME)) {
                selectedPolicy = POLICY_FACTURAE_30;
            } else if (str.equals(POLICY_FACTURAE_31_NAME)) {
                selectedPolicy = POLICY_FACTURAE_31;
            }
        }
        return selectedPolicy;
    }

    void checkPreferences() throws AOException {
        AdESPolicy selectedPolicy = this.facturaePolicyPanel.getSelectedPolicy();
        if (selectedPolicy != null) {
            try {
                new Oid(selectedPolicy.getPolicyIdentifier().replace("urn:oid:", ""));
            } catch (GSSException e) {
                throw new AOException("El identificador debe ser un OID", e);
            }
        }
    }

    public boolean isBlocked() {
        return this.blocked;
    }

    public void setBlocked(boolean z) {
        this.blocked = z;
    }
}
