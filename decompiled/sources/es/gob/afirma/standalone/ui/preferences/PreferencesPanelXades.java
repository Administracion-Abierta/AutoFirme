package es.gob.afirma.standalone.ui.preferences;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AdESPolicy;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/PreferencesPanelXades.class */
public final class PreferencesPanelXades extends JScrollPane {
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final long serialVersionUID = 1424468022677956783L;
    private static final String SIGN_FORMAT_XADES = "XAdES";
    public static final AdESPolicy POLICY_XADES_AGE_1_9;
    private static final String POLICY_BUNDLE_NAME = "policy";
    private PolicyPanel xadesPolicyDlg;
    private JLabel currentPolicyValue;
    private final JTextField xadesSignatureProductionCity = new JTextField();
    private final JTextField xadesSignatureProductionProvince = new JTextField();
    private final JTextField xadesSignatureProductionPostalCode = new JTextField();
    private final JTextField xadesSignatureProductionCountry = new JTextField();
    private final JTextField xadesSignerClaimedRole = new JTextField();
    private final JComboBox<Object> xadesSignFormat = new JComboBox<>(new Object[]{AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED, AOSignConstants.SIGN_FORMAT_XADES_ENVELOPING, AOSignConstants.SIGN_FORMAT_XADES_DETACHED});
    private final JRadioButton optionCoSign = new JRadioButton(SimpleAfirmaMessages.getString("PreferencesPanel.168"));
    private final JRadioButton optionCounterSignLeafs = new JRadioButton(SimpleAfirmaMessages.getString("PreferencesPanel.169"));
    private final JRadioButton optionCounterSignTree = new JRadioButton(SimpleAfirmaMessages.getString("PreferencesPanel.170"));
    private final JPanel panelPolicies = new JPanel();
    private boolean blocked = true;

    static {
        ResourceBundle bundle = ResourceBundle.getBundle(POLICY_BUNDLE_NAME, Locale.getDefault());
        POLICY_XADES_AGE_1_9 = new AdESPolicy(bundle.getString("FirmaAGE19.policyIdentifier"), bundle.getString("FirmaAGE19.policyIdentifierHash.XAdES"), AOSignConstants.DIGEST_ALGORITHM_SHA1, bundle.getString("FirmaAGE19.policyQualifier"));
    }

    PreferencesPanelXades(KeyListener keyListener, ModificationListener modificationListener, boolean z) {
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
        loadXadesPolicy();
        loadPreferences();
        this.xadesPolicyDlg.setModificationListener(modificationListener);
        this.xadesPolicyDlg.setKeyListener(keyListener);
        JPanel jPanelCreatePolicyPanel = createPolicyPanel();
        jPanelCreatePolicyPanel.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanel.153")));
        JPanel jPanel2 = new JPanel(new FlowLayout(3));
        jPanel2.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanel.69")));
        JPanel jPanel3 = new JPanel(new GridBagLayout());
        this.xadesSignFormat.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.183"));
        this.xadesSignFormat.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.53"));
        this.xadesSignFormat.addItemListener(modificationListener);
        this.xadesSignFormat.addKeyListener(keyListener);
        this.xadesSignFormat.setEnabled(!isBlocked());
        JLabel jLabel = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.15"));
        jLabel.addKeyListener(keyListener);
        jLabel.setLabelFor(this.xadesSignFormat);
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.anchor = 21;
        gridBagConstraints2.insets = new Insets(0, 7, 4, 7);
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridx = 0;
        jPanel3.add(jLabel, gridBagConstraints2);
        gridBagConstraints2.gridx = 1;
        jPanel3.add(this.xadesSignFormat, gridBagConstraints2);
        jPanel2.add(jPanel3);
        JPanel jPanel4 = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = 1;
        gridBagConstraints3.weightx = 1.0d;
        gridBagConstraints3.gridy = 0;
        jPanel4.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanel.167")));
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(this.optionCoSign);
        buttonGroup.add(this.optionCounterSignLeafs);
        buttonGroup.add(this.optionCounterSignTree);
        this.optionCoSign.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.184"));
        this.optionCoSign.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.168"));
        this.optionCoSign.setEnabled(!isBlocked());
        this.optionCoSign.addItemListener(modificationListener);
        this.optionCoSign.addKeyListener(keyListener);
        this.optionCounterSignLeafs.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.184"));
        this.optionCounterSignLeafs.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.169"));
        this.optionCounterSignLeafs.setEnabled(!isBlocked());
        this.optionCounterSignLeafs.addItemListener(modificationListener);
        this.optionCounterSignLeafs.addKeyListener(keyListener);
        this.optionCounterSignTree.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.184"));
        this.optionCounterSignTree.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.170"));
        this.optionCounterSignTree.setEnabled(!isBlocked());
        this.optionCounterSignTree.addItemListener(modificationListener);
        this.optionCounterSignTree.addKeyListener(keyListener);
        jPanel4.add(this.optionCoSign, gridBagConstraints3);
        gridBagConstraints3.gridy++;
        jPanel4.add(this.optionCounterSignLeafs, gridBagConstraints3);
        gridBagConstraints3.gridy++;
        jPanel4.add(this.optionCounterSignTree, gridBagConstraints3);
        JPanel jPanel5 = new JPanel();
        jPanel5.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanel.8")));
        jPanel5.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = 1;
        gridBagConstraints4.weightx = 1.0d;
        gridBagConstraints4.gridy = 0;
        JLabel jLabel2 = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.11"));
        jLabel2.setLabelFor(this.xadesSignatureProductionCity);
        gridBagConstraints4.gridy++;
        jPanel5.add(jLabel2, gridBagConstraints4);
        this.xadesSignatureProductionCity.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.181"));
        this.xadesSignatureProductionCity.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.66"));
        this.xadesSignatureProductionCity.addKeyListener(modificationListener);
        this.xadesSignatureProductionCity.addKeyListener(keyListener);
        gridBagConstraints4.gridy++;
        jPanel5.add(this.xadesSignatureProductionCity, gridBagConstraints4);
        JLabel jLabel3 = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.14"));
        jLabel3.setLabelFor(this.xadesSignatureProductionProvince);
        gridBagConstraints4.gridy++;
        jPanel5.add(jLabel3, gridBagConstraints4);
        this.xadesSignatureProductionProvince.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.181"));
        this.xadesSignatureProductionProvince.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.14"));
        this.xadesSignatureProductionProvince.addKeyListener(modificationListener);
        this.xadesSignatureProductionProvince.addKeyListener(keyListener);
        gridBagConstraints4.gridy++;
        jPanel5.add(this.xadesSignatureProductionProvince, gridBagConstraints4);
        JLabel jLabel4 = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.102"));
        jLabel4.setLabelFor(this.xadesSignatureProductionPostalCode);
        gridBagConstraints4.gridy++;
        jPanel5.add(jLabel4, gridBagConstraints4);
        this.xadesSignatureProductionPostalCode.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.181"));
        this.xadesSignatureProductionPostalCode.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.102"));
        this.xadesSignatureProductionPostalCode.addKeyListener(modificationListener);
        this.xadesSignatureProductionPostalCode.addKeyListener(keyListener);
        gridBagConstraints4.gridy++;
        jPanel5.add(this.xadesSignatureProductionPostalCode, gridBagConstraints4);
        JLabel jLabel5 = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.12"));
        jLabel5.setLabelFor(this.xadesSignatureProductionCountry);
        gridBagConstraints4.gridy++;
        jPanel5.add(jLabel5, gridBagConstraints4);
        this.xadesSignatureProductionCountry.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.181"));
        this.xadesSignatureProductionCountry.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.12"));
        this.xadesSignatureProductionCountry.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.67"));
        this.xadesSignatureProductionCountry.addKeyListener(modificationListener);
        this.xadesSignatureProductionCountry.addKeyListener(keyListener);
        gridBagConstraints4.gridy++;
        jPanel5.add(this.xadesSignatureProductionCountry, gridBagConstraints4);
        JLabel jLabel6 = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanelXades.0"));
        jLabel6.setLabelFor(this.xadesSignerClaimedRole);
        gridBagConstraints4.gridy++;
        jPanel5.add(jLabel6, gridBagConstraints4);
        this.xadesSignerClaimedRole.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.181"));
        this.xadesSignerClaimedRole.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanelXades.0"));
        this.xadesSignerClaimedRole.addKeyListener(modificationListener);
        this.xadesSignerClaimedRole.addKeyListener(keyListener);
        gridBagConstraints4.gridy++;
        jPanel5.add(this.xadesSignerClaimedRole, gridBagConstraints4);
        gridBagConstraints.gridy++;
        jPanel.add(jPanelCreatePolicyPanel, gridBagConstraints);
        gridBagConstraints.gridy++;
        jPanel.add(jPanel2, gridBagConstraints);
        gridBagConstraints.gridy++;
        jPanel.add(jPanel4, gridBagConstraints);
        gridBagConstraints.gridy++;
        jPanel.add(jPanel5, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.weighty = 1.0d;
        jPanel.add(new JPanel(), gridBagConstraints);
        JPanel jPanel6 = new JPanel(new FlowLayout(4));
        JButton jButton = new JButton(SimpleAfirmaMessages.getString("PreferencesPanel.147"));
        jButton.setMnemonic('R');
        jButton.addActionListener(actionEvent -> {
            if (AOUIFactory.showConfirmDialog(getParent(), SimpleAfirmaMessages.getString("PreferencesPanel.156"), SimpleAfirmaMessages.getString("PreferencesPanel.139"), 0, 2) == 0) {
                restorePreferences();
            }
        });
        jButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.136"));
        gridBagConstraints.gridy++;
        gridBagConstraints.weighty = 0.0d;
        jPanel6.add(jButton, gridBagConstraints);
        gridBagConstraints.gridy++;
        jPanel.add(jPanel6, gridBagConstraints);
        setViewportView(jPanel);
    }

    private JPanel createPolicyPanel() {
        JLabel jLabel = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.171"));
        this.currentPolicyValue = new JLabel(this.xadesPolicyDlg.getSelectedPolicyName());
        this.currentPolicyValue.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        this.currentPolicyValue.setFocusable(true);
        this.currentPolicyValue.addFocusListener(new FocusListener() { // from class: es.gob.afirma.standalone.ui.preferences.PreferencesPanelXades.1
            public void focusLost(FocusEvent focusEvent) {
                ((JComponent) focusEvent.getSource()).setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            }

            public void focusGained(FocusEvent focusEvent) {
                ((JComponent) focusEvent.getSource()).setBorder(BorderFactory.createLineBorder(Color.black, 1));
            }
        });
        jLabel.setLabelFor(this.currentPolicyValue);
        JButton jButton = new JButton(SimpleAfirmaMessages.getString("PreferencesPanel.150"));
        jButton.setMnemonic('P');
        jButton.addActionListener(actionEvent -> {
            changeXadesPolicyDlg(getParent());
        });
        jButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.151"));
        jButton.setEnabled(!isBlocked());
        JPanel jPanel = new JPanel(new FlowLayout(3));
        JPanel jPanel2 = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 21;
        gridBagConstraints.insets = new Insets(0, 7, 4, 7);
        gridBagConstraints.gridx = 0;
        jPanel2.add(jLabel, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        jPanel2.add(this.currentPolicyValue, gridBagConstraints);
        gridBagConstraints.gridx = 2;
        jPanel2.add(jButton, gridBagConstraints);
        jPanel.add(jPanel2);
        return jPanel;
    }

    void savePreferences() {
        String str;
        PreferencesManager.put(PreferencesManager.PREFERENCE_XADES_SIGN_FORMAT, this.xadesSignFormat.getSelectedItem().toString());
        PreferencesManager.put(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_CITY, this.xadesSignatureProductionCity.getText());
        PreferencesManager.put(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_PROVINCE, this.xadesSignatureProductionProvince.getText());
        PreferencesManager.put(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_POSTAL_CODE, this.xadesSignatureProductionPostalCode.getText());
        PreferencesManager.put(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_COUNTRY, this.xadesSignatureProductionCountry.getText());
        PreferencesManager.put(PreferencesManager.PREFERENCE_XADES_SIGNER_CLAIMED_ROLE, this.xadesSignerClaimedRole.getText());
        AdESPolicy selectedPolicy = this.xadesPolicyDlg.getSelectedPolicy();
        if (selectedPolicy != null) {
            PreferencesManager.put(PreferencesManager.PREFERENCE_XADES_POLICY_IDENTIFIER, selectedPolicy.getPolicyIdentifier());
            PreferencesManager.put(PreferencesManager.PREFERENCE_XADES_POLICY_HASH, selectedPolicy.getPolicyIdentifierHash());
            PreferencesManager.put(PreferencesManager.PREFERENCE_XADES_POLICY_HASH_ALGORITHM, selectedPolicy.getPolicyIdentifierHashAlgorithm());
            if (selectedPolicy.getPolicyQualifier() != null) {
                PreferencesManager.put(PreferencesManager.PREFERENCE_XADES_POLICY_QUALIFIER, selectedPolicy.getPolicyQualifier().toString());
            } else {
                PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_POLICY_QUALIFIER);
            }
        } else {
            PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_POLICY_IDENTIFIER);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_POLICY_HASH);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_POLICY_HASH_ALGORITHM);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_POLICY_QUALIFIER);
        }
        if (this.optionCoSign.isSelected()) {
            str = "cosign";
        } else if (this.optionCounterSignLeafs.isSelected()) {
            str = PreferencesManager.VALUE_MULTISIGN_COUNTERSIGN_LEAFS;
        } else {
            str = PreferencesManager.VALUE_MULTISIGN_COUNTERSIGN_TREE;
        }
        PreferencesManager.put(PreferencesManager.PREFERENCE_XADES_MULTISIGN, str);
        this.xadesPolicyDlg.saveCurrentPolicy();
    }

    void loadPreferences() {
        this.xadesSignatureProductionCity.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_CITY));
        this.xadesSignatureProductionProvince.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_PROVINCE));
        this.xadesSignatureProductionPostalCode.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_POSTAL_CODE));
        this.xadesSignatureProductionCountry.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_COUNTRY));
        this.xadesSignerClaimedRole.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_SIGNER_CLAIMED_ROLE));
        ArrayList arrayList = new ArrayList();
        arrayList.add(new PolicyItem(SimpleAfirmaMessages.getString("PreferencesPanel.73"), POLICY_XADES_AGE_1_9));
        this.panelPolicies.removeAll();
        AdESPolicy xAdESPreferedPolicy = getXAdESPreferedPolicy();
        this.xadesPolicyDlg = new PolicyPanel("XAdES", arrayList, xAdESPreferedPolicy, isBlocked());
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_SIGN_FORMAT);
        if (xAdESPreferedPolicy != null && AgePolicy.isAGEPolicy(xAdESPreferedPolicy.getPolicyIdentifier(), "XAdES")) {
            this.xadesSignFormat.removeAllItems();
            this.xadesSignFormat.addItem(AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED);
            this.xadesSignFormat.addItem(AOSignConstants.SIGN_FORMAT_XADES_DETACHED);
            if (AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED.equals(str) || AOSignConstants.SIGN_FORMAT_XADES_DETACHED.equals(str)) {
                this.xadesSignFormat.setSelectedItem(str);
            }
        } else {
            this.xadesSignFormat.setSelectedItem(str);
        }
        String str2 = PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_MULTISIGN);
        if (str2 != null) {
            this.optionCoSign.setSelected("cosign".equals(str2));
            this.optionCounterSignLeafs.setSelected(PreferencesManager.VALUE_MULTISIGN_COUNTERSIGN_LEAFS.equals(str2));
            this.optionCounterSignTree.setSelected(PreferencesManager.VALUE_MULTISIGN_COUNTERSIGN_TREE.equals(str2));
        }
        revalidate();
        repaint();
    }

    void restorePreferences() {
        PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_CITY);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_PROVINCE);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_POSTAL_CODE);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_COUNTRY);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_SIGNER_CLAIMED_ROLE);
        this.xadesSignatureProductionCity.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_CITY));
        this.xadesSignatureProductionProvince.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_PROVINCE));
        this.xadesSignatureProductionPostalCode.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_POSTAL_CODE));
        this.xadesSignatureProductionCountry.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_COUNTRY));
        this.xadesSignerClaimedRole.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_SIGNER_CLAIMED_ROLE));
        if (!isBlocked()) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new PolicyItem(SimpleAfirmaMessages.getString("PreferencesPanel.73"), POLICY_XADES_AGE_1_9));
            this.xadesPolicyDlg = new PolicyPanel("XAdES", arrayList, restorePolicy(), isBlocked());
            this.currentPolicyValue.setText(this.xadesPolicyDlg.getSelectedPolicyName());
            PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_SIGN_FORMAT);
            String str = PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_SIGN_FORMAT);
            String str2 = PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_POLICY_IDENTIFIER);
            if (AgePolicy.isAGEPolicy(str2, "XAdES")) {
                this.xadesSignFormat.removeAllItems();
                this.xadesSignFormat.addItem(AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED);
                this.xadesSignFormat.addItem(AOSignConstants.SIGN_FORMAT_XADES_DETACHED);
                if (AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED.equals(str)) {
                    this.xadesSignFormat.setSelectedItem(str);
                } else {
                    this.xadesSignFormat.setSelectedItem(AOSignConstants.SIGN_FORMAT_XADES_DETACHED);
                }
            } else {
                this.xadesSignFormat.removeAllItems();
                this.xadesSignFormat.addItem(AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED);
                this.xadesSignFormat.addItem(AOSignConstants.SIGN_FORMAT_XADES_ENVELOPING);
                this.xadesSignFormat.addItem(AOSignConstants.SIGN_FORMAT_XADES_DETACHED);
                this.xadesSignFormat.setSelectedItem(str);
            }
            if (AgePolicy.isAGEPolicy(str2, "XAdES")) {
                this.xadesSignFormat.setSelectedItem(AOSignConstants.SIGN_FORMAT_XADES_DETACHED);
            }
            PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_MULTISIGN);
            String str3 = PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_MULTISIGN);
            this.optionCoSign.setSelected("cosign".equals(str3));
            this.optionCounterSignLeafs.setSelected(PreferencesManager.VALUE_MULTISIGN_COUNTERSIGN_LEAFS.equals(str3));
            this.optionCounterSignTree.setSelected(PreferencesManager.VALUE_MULTISIGN_COUNTERSIGN_TREE.equals(str3));
        }
        revalidate();
        repaint();
    }

    private static AdESPolicy getXAdESPreferedPolicy() {
        AdESPolicy adESPolicy = null;
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_POLICY_IDENTIFIER);
        if (str != null && !str.isEmpty()) {
            try {
                adESPolicy = new AdESPolicy(str, PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_POLICY_HASH), PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_POLICY_HASH_ALGORITHM), PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_POLICY_QUALIFIER));
            } catch (Exception e) {
                LOGGER.severe("Error al recuperar la politica XAdES guardada en preferencias: " + e);
            }
        }
        return adESPolicy;
    }

    private AdESPolicy restorePolicy() {
        AdESPolicy adESPolicy = null;
        loadXadesPolicy();
        if (isBlocked()) {
            adESPolicy = this.xadesPolicyDlg.getSelectedPolicy();
        } else {
            PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_POLICY_IDENTIFIER);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_POLICY_HASH);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_POLICY_HASH_ALGORITHM);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_POLICY_QUALIFIER);
            try {
                String str = PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_POLICY_IDENTIFIER);
                if (str != null && !str.isEmpty()) {
                    adESPolicy = new AdESPolicy(str, PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_POLICY_HASH), PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_POLICY_HASH_ALGORITHM), PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_POLICY_QUALIFIER));
                }
                this.xadesPolicyDlg.loadPolicy(adESPolicy);
            } catch (Exception e) {
                LOGGER.severe("Error al recuperar la politica XAdES guardada en preferencias: " + e);
            }
        }
        return adESPolicy;
    }

    void checkPreferences() throws AOException {
        loadXadesPolicy();
        AdESPolicy selectedPolicy = this.xadesPolicyDlg.getSelectedPolicy();
        if (selectedPolicy != null) {
            try {
                new URI(selectedPolicy.getPolicyIdentifier());
            } catch (URISyntaxException e) {
                throw new AOException("El identificador debe ser una URI", e);
            }
        }
    }

    public boolean isBlocked() {
        return this.blocked;
    }

    public void setBlocked(boolean z) {
        this.blocked = z;
    }

    private void loadXadesPolicy() {
        if (this.xadesPolicyDlg == null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new PolicyItem(SimpleAfirmaMessages.getString("PreferencesPanel.73"), POLICY_XADES_AGE_1_9));
            this.xadesPolicyDlg = new PolicyPanel("XAdES", arrayList, getXAdESPreferedPolicy(), isBlocked());
        }
    }

    public void changeXadesPolicyDlg(Container container) {
        loadXadesPolicy();
        if (AOUIFactory.showConfirmDialog(container, this.xadesPolicyDlg, SimpleAfirmaMessages.getString("PolicyDialog.0"), 2, -1) == 0) {
            try {
                checkPreferences();
                this.currentPolicyValue.setText(this.xadesPolicyDlg.getSelectedPolicyName());
                AdESPolicy selectedPolicy = this.xadesPolicyDlg.getSelectedPolicy();
                if (selectedPolicy != null) {
                    PreferencesManager.put(PreferencesManager.PREFERENCE_XADES_POLICY_IDENTIFIER, selectedPolicy.getPolicyIdentifier());
                    PreferencesManager.put(PreferencesManager.PREFERENCE_XADES_POLICY_HASH, selectedPolicy.getPolicyIdentifierHash());
                    PreferencesManager.put(PreferencesManager.PREFERENCE_XADES_POLICY_HASH_ALGORITHM, selectedPolicy.getPolicyIdentifierHashAlgorithm());
                    if (selectedPolicy.getPolicyQualifier() != null) {
                        PreferencesManager.put(PreferencesManager.PREFERENCE_XADES_POLICY_QUALIFIER, selectedPolicy.getPolicyQualifier().toString());
                    } else {
                        PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_POLICY_QUALIFIER);
                    }
                    if (AgePolicy.isAGEPolicy(selectedPolicy.getPolicyIdentifier(), "XAdES")) {
                        String str = (String) this.xadesSignFormat.getSelectedItem();
                        this.xadesSignFormat.removeAllItems();
                        this.xadesSignFormat.addItem(AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED);
                        this.xadesSignFormat.addItem(AOSignConstants.SIGN_FORMAT_XADES_DETACHED);
                        if (AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED.equals(str) || AOSignConstants.SIGN_FORMAT_XADES_DETACHED.equals(str)) {
                            this.xadesSignFormat.setSelectedItem(str);
                        }
                    } else {
                        String str2 = (String) this.xadesSignFormat.getSelectedItem();
                        this.xadesSignFormat.removeAllItems();
                        this.xadesSignFormat.addItem(AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED);
                        this.xadesSignFormat.addItem(AOSignConstants.SIGN_FORMAT_XADES_ENVELOPING);
                        this.xadesSignFormat.addItem(AOSignConstants.SIGN_FORMAT_XADES_DETACHED);
                        this.xadesSignFormat.setSelectedItem(str2);
                    }
                } else {
                    PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_POLICY_IDENTIFIER);
                    PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_POLICY_HASH);
                    PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_POLICY_HASH_ALGORITHM);
                    PreferencesManager.remove(PreferencesManager.PREFERENCE_XADES_POLICY_QUALIFIER);
                    String str3 = (String) this.xadesSignFormat.getSelectedItem();
                    this.xadesSignFormat.removeAllItems();
                    this.xadesSignFormat.addItem(AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED);
                    this.xadesSignFormat.addItem(AOSignConstants.SIGN_FORMAT_XADES_ENVELOPING);
                    this.xadesSignFormat.addItem(AOSignConstants.SIGN_FORMAT_XADES_DETACHED);
                    this.xadesSignFormat.setSelectedItem(str3);
                }
                this.xadesPolicyDlg.saveCurrentPolicy();
            } catch (Exception e) {
                AOUIFactory.showErrorMessage("<p>" + SimpleAfirmaMessages.getString("PreferencesPanel.6") + "</p>", SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e);
                changeXadesPolicyDlg(container);
            }
        }
        this.xadesPolicyDlg = null;
    }
}
