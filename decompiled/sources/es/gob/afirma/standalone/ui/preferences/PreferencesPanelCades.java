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
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/PreferencesPanelCades.class */
final class PreferencesPanelCades extends JScrollPane {
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final long serialVersionUID = -2410844527428138817L;
    private static final String SIGN_FORMAT_CADES = "CAdES";
    private static final AdESPolicy POLICY_CADES_AGE_1_9;
    private static final String POLICY_BUNDLE_NAME = "policy";
    private PolicyPanel cadesPolicyDlg;
    private JLabel currentPolicyValue;
    private boolean blocked = true;
    private final JCheckBox signatureMode = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanel.1"));
    private final JRadioButton optionCoSign = new JRadioButton(SimpleAfirmaMessages.getString("PreferencesPanel.168"));
    private final JRadioButton optionCounterSignLeafs = new JRadioButton(SimpleAfirmaMessages.getString("PreferencesPanel.169"));
    private final JRadioButton optionCounterSignTree = new JRadioButton(SimpleAfirmaMessages.getString("PreferencesPanel.170"));

    static {
        ResourceBundle bundle = ResourceBundle.getBundle(POLICY_BUNDLE_NAME, Locale.getDefault());
        POLICY_CADES_AGE_1_9 = new AdESPolicy(bundle.getString("FirmaAGE19.policyIdentifier"), bundle.getString("FirmaAGE19.policyIdentifierHash.CAdES"), AOSignConstants.DIGEST_ALGORITHM_SHA1, bundle.getString("FirmaAGE19.policyQualifier"));
    }

    PreferencesPanelCades(KeyListener keyListener, ModificationListener modificationListener, boolean z) {
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
        loadCadesPolicy();
        this.cadesPolicyDlg.setModificationListener(modificationListener);
        this.cadesPolicyDlg.setKeyListener(keyListener);
        JPanel jPanelCreatePolicyPanel = createPolicyPanel();
        jPanelCreatePolicyPanel.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanel.153")));
        gridBagConstraints.gridy++;
        jPanel.add(jPanelCreatePolicyPanel, gridBagConstraints);
        JPanel jPanel2 = new JPanel(new FlowLayout(3));
        jPanel2.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanel.69")));
        this.signatureMode.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.182"));
        this.signatureMode.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.45"));
        this.signatureMode.addItemListener(modificationListener);
        this.signatureMode.addKeyListener(keyListener);
        this.signatureMode.setEnabled(!isBlocked());
        jPanel2.add(this.signatureMode);
        gridBagConstraints.gridy++;
        jPanel.add(jPanel2, gridBagConstraints);
        JPanel jPanel3 = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = 1;
        gridBagConstraints2.weightx = 1.0d;
        gridBagConstraints2.gridy = 0;
        jPanel3.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanel.167")));
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
        jPanel3.add(this.optionCoSign, gridBagConstraints2);
        gridBagConstraints2.gridy++;
        jPanel3.add(this.optionCounterSignLeafs, gridBagConstraints2);
        gridBagConstraints2.gridy++;
        jPanel3.add(this.optionCounterSignTree, gridBagConstraints2);
        gridBagConstraints.gridy++;
        jPanel.add(jPanel3, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.weighty = 1.0d;
        jPanel.add(new JPanel(), gridBagConstraints);
        JPanel jPanel4 = new JPanel(new FlowLayout(4));
        JButton jButton = new JButton(SimpleAfirmaMessages.getString("PreferencesPanel.147"));
        jButton.setMnemonic('R');
        jButton.addActionListener(actionEvent -> {
            if (AOUIFactory.showConfirmDialog(getParent(), SimpleAfirmaMessages.getString("PreferencesPanel.157"), SimpleAfirmaMessages.getString("PreferencesPanel.139"), 0, 2) == 0) {
                restorePreferences();
            }
        });
        jButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.136"));
        gridBagConstraints.gridy++;
        gridBagConstraints.weighty = 0.0d;
        jPanel4.add(jButton, gridBagConstraints);
        gridBagConstraints.gridy++;
        jPanel.add(jPanel4, gridBagConstraints);
        setViewportView(jPanel);
    }

    private JPanel createPolicyPanel() {
        JLabel jLabel = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.171"));
        this.currentPolicyValue = new JLabel(this.cadesPolicyDlg.getSelectedPolicyName());
        this.currentPolicyValue.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        this.currentPolicyValue.setFocusable(true);
        this.currentPolicyValue.addFocusListener(new FocusListener() { // from class: es.gob.afirma.standalone.ui.preferences.PreferencesPanelCades.1
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
            changeCadesPolicyDlg(getParent());
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

    void checkPreferences() throws AOException {
        loadCadesPolicy();
        AdESPolicy selectedPolicy = this.cadesPolicyDlg.getSelectedPolicy();
        if (selectedPolicy != null) {
            try {
                new Oid(selectedPolicy.getPolicyIdentifier().replace("urn:oid:", ""));
            } catch (GSSException e) {
                throw new AOException("El identificador debe ser un OID", e);
            }
        }
    }

    void savePreferences() {
        String str;
        PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_CADES_IMPLICIT, this.signatureMode.isSelected());
        AdESPolicy selectedPolicy = this.cadesPolicyDlg.getSelectedPolicy();
        if (selectedPolicy != null) {
            PreferencesManager.put(PreferencesManager.PREFERENCE_CADES_POLICY_IDENTIFIER, selectedPolicy.getPolicyIdentifier());
            PreferencesManager.put(PreferencesManager.PREFERENCE_CADES_POLICY_HASH, selectedPolicy.getPolicyIdentifierHash());
            PreferencesManager.put(PreferencesManager.PREFERENCE_CADES_POLICY_HASH_ALGORITHM, selectedPolicy.getPolicyIdentifierHashAlgorithm());
            if (selectedPolicy.getPolicyQualifier() != null) {
                PreferencesManager.put(PreferencesManager.PREFERENCE_CADES_POLICY_QUALIFIER, selectedPolicy.getPolicyQualifier().toString());
            } else {
                PreferencesManager.remove(PreferencesManager.PREFERENCE_CADES_POLICY_QUALIFIER);
            }
        } else {
            PreferencesManager.remove(PreferencesManager.PREFERENCE_CADES_POLICY_IDENTIFIER);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_CADES_POLICY_HASH);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_CADES_POLICY_HASH_ALGORITHM);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_CADES_POLICY_QUALIFIER);
        }
        if (this.optionCoSign.isSelected()) {
            str = "cosign";
        } else if (this.optionCounterSignLeafs.isSelected()) {
            str = PreferencesManager.VALUE_MULTISIGN_COUNTERSIGN_LEAFS;
        } else {
            str = PreferencesManager.VALUE_MULTISIGN_COUNTERSIGN_TREE;
        }
        PreferencesManager.put(PreferencesManager.PREFERENCE_CADES_MULTISIGN, str);
        this.cadesPolicyDlg.saveCurrentPolicy();
    }

    void loadPreferences() {
        this.signatureMode.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_CADES_IMPLICIT));
        ArrayList arrayList = new ArrayList();
        arrayList.add(new PolicyItem(SimpleAfirmaMessages.getString("PreferencesPanel.73"), POLICY_CADES_AGE_1_9));
        this.cadesPolicyDlg = new PolicyPanel("CAdES", arrayList, getCadesPreferedPolicy(), isBlocked());
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_CADES_MULTISIGN);
        if (str != null) {
            this.optionCoSign.setSelected("cosign".equals(str));
            this.optionCounterSignLeafs.setSelected(PreferencesManager.VALUE_MULTISIGN_COUNTERSIGN_LEAFS.equals(str));
            this.optionCounterSignTree.setSelected(PreferencesManager.VALUE_MULTISIGN_COUNTERSIGN_TREE.equals(str));
        }
        revalidate();
        repaint();
    }

    void restorePreferences() {
        if (!isBlocked()) {
            PreferencesManager.remove(PreferencesManager.PREFERENCE_CADES_IMPLICIT);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_CADES_MULTISIGN);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_CADES_POLICY_IDENTIFIER);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_CADES_POLICY_HASH);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_CADES_POLICY_HASH_ALGORITHM);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_CADES_POLICY_QUALIFIER);
            this.signatureMode.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_CADES_IMPLICIT));
            ArrayList arrayList = new ArrayList();
            arrayList.add(new PolicyItem(SimpleAfirmaMessages.getString("PreferencesPanel.73"), POLICY_CADES_AGE_1_9));
            this.cadesPolicyDlg = new PolicyPanel("CAdES", arrayList, getCadesDefaultPolicy(), isBlocked());
            this.currentPolicyValue.setText(this.cadesPolicyDlg.getSelectedPolicyName());
            String str = PreferencesManager.get(PreferencesManager.PREFERENCE_CADES_MULTISIGN);
            this.optionCoSign.setSelected("cosign".equals(str));
            this.optionCounterSignLeafs.setSelected(PreferencesManager.VALUE_MULTISIGN_COUNTERSIGN_LEAFS.equals(str));
            this.optionCounterSignTree.setSelected(PreferencesManager.VALUE_MULTISIGN_COUNTERSIGN_TREE.equals(str));
            revalidate();
            repaint();
        }
    }

    private static AdESPolicy getCadesPreferedPolicy() {
        AdESPolicy adESPolicy = null;
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_CADES_POLICY_IDENTIFIER);
        if (str != null && !str.isEmpty()) {
            try {
                adESPolicy = new AdESPolicy(str, PreferencesManager.get(PreferencesManager.PREFERENCE_CADES_POLICY_HASH), PreferencesManager.get(PreferencesManager.PREFERENCE_CADES_POLICY_HASH_ALGORITHM), PreferencesManager.get(PreferencesManager.PREFERENCE_CADES_POLICY_QUALIFIER));
            } catch (Exception e) {
                LOGGER.severe("Error al recuperar la politica CAdES guardada en preferencias: " + e);
            }
        }
        return adESPolicy;
    }

    private AdESPolicy getCadesDefaultPolicy() {
        AdESPolicy adESPolicy = null;
        loadCadesPolicy();
        if (isBlocked()) {
            adESPolicy = this.cadesPolicyDlg.getSelectedPolicy();
        } else {
            try {
                String str = PreferencesManager.get(PreferencesManager.PREFERENCE_CADES_POLICY_IDENTIFIER);
                if (str != null && !str.isEmpty()) {
                    adESPolicy = new AdESPolicy(str, PreferencesManager.get(PreferencesManager.PREFERENCE_CADES_POLICY_HASH), PreferencesManager.get(PreferencesManager.PREFERENCE_CADES_POLICY_HASH_ALGORITHM), PreferencesManager.get(PreferencesManager.PREFERENCE_CADES_POLICY_QUALIFIER));
                }
                this.cadesPolicyDlg.loadPolicy(adESPolicy);
            } catch (Exception e) {
                LOGGER.severe("Error al recuperar la politica CAdES guardada en preferencias: " + e);
            }
        }
        return adESPolicy;
    }

    private void loadCadesPolicy() {
        if (this.cadesPolicyDlg == null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new PolicyItem(SimpleAfirmaMessages.getString("PreferencesPanel.73"), POLICY_CADES_AGE_1_9));
            this.cadesPolicyDlg = new PolicyPanel("CAdES", arrayList, getCadesPreferedPolicy(), isBlocked());
        }
    }

    public void changeCadesPolicyDlg(Container container) {
        loadCadesPolicy();
        if (AOUIFactory.showConfirmDialog(container, this.cadesPolicyDlg, SimpleAfirmaMessages.getString("PolicyDialog.0"), 2, -1) == 0) {
            try {
                checkPreferences();
                this.currentPolicyValue.setText(this.cadesPolicyDlg.getSelectedPolicyName());
                AdESPolicy selectedPolicy = this.cadesPolicyDlg.getSelectedPolicy();
                if (selectedPolicy != null) {
                    PreferencesManager.put(PreferencesManager.PREFERENCE_CADES_POLICY_IDENTIFIER, selectedPolicy.getPolicyIdentifier());
                    PreferencesManager.put(PreferencesManager.PREFERENCE_CADES_POLICY_HASH, selectedPolicy.getPolicyIdentifierHash());
                    PreferencesManager.put(PreferencesManager.PREFERENCE_CADES_POLICY_HASH_ALGORITHM, selectedPolicy.getPolicyIdentifierHashAlgorithm());
                    if (selectedPolicy.getPolicyQualifier() != null) {
                        PreferencesManager.put(PreferencesManager.PREFERENCE_CADES_POLICY_QUALIFIER, selectedPolicy.getPolicyQualifier().toString());
                    } else {
                        PreferencesManager.remove(PreferencesManager.PREFERENCE_CADES_POLICY_QUALIFIER);
                    }
                } else {
                    PreferencesManager.remove(PreferencesManager.PREFERENCE_CADES_POLICY_IDENTIFIER);
                    PreferencesManager.remove(PreferencesManager.PREFERENCE_CADES_POLICY_HASH);
                    PreferencesManager.remove(PreferencesManager.PREFERENCE_CADES_POLICY_HASH_ALGORITHM);
                    PreferencesManager.remove(PreferencesManager.PREFERENCE_CADES_POLICY_QUALIFIER);
                }
                this.cadesPolicyDlg.saveCurrentPolicy();
            } catch (Exception e) {
                AOUIFactory.showErrorMessage("<p>" + SimpleAfirmaMessages.getString("PreferencesPanel.38") + "</p>", SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e);
                changeCadesPolicyDlg(container);
            }
        }
        this.cadesPolicyDlg = null;
    }

    public boolean isBlocked() {
        return this.blocked;
    }

    public void setBlocked(boolean z) {
        this.blocked = z;
    }
}
