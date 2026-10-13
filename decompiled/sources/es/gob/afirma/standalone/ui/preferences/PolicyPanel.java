package es.gob.afirma.standalone.ui.preferences;

import com.sun.jna.platform.win32.WinNT;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AdESPolicy;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.ietf.jgss.Oid;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/PolicyPanel.class */
final class PolicyPanel extends JPanel implements ItemListener {
    private static final long serialVersionUID = 4804298622744399269L;
    private static final int POLICY_INDEX_NONE = 0;
    private JComboBox<PolicyItem> policiesCombo;
    private final JTextField identifierField;
    private final JTextField hashField;
    private final JComboBox<String> hashAlgorithmField;
    private final JTextField qualifierField;
    private final String signatureFormat;
    private AdESPolicy currentPolicy;
    private final List<PolicyItem> policies;
    final boolean allowNoPolicy;
    final boolean allowCustomPolicy;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String[] POLICY_HASH_ALGORITHMS = {AOSignConstants.DIGEST_ALGORITHM_SHA1, "SHA-512", "SHA-384", "SHA-256"};

    PolicyPanel(String str, List<PolicyItem> list, AdESPolicy adESPolicy, boolean z) {
        this(str, list, adESPolicy, true, true, true, z);
    }

    PolicyPanel(String str, List<PolicyItem> list, AdESPolicy adESPolicy, boolean z, boolean z2, boolean z3, boolean z4) {
        this.identifierField = new JTextField();
        this.hashField = new JTextField();
        this.hashAlgorithmField = new JComboBox<>(POLICY_HASH_ALGORITHMS);
        this.qualifierField = new JTextField();
        this.signatureFormat = str;
        this.allowCustomPolicy = z2;
        this.allowNoPolicy = z;
        AdESPolicy adESPolicy2 = adESPolicy;
        if (adESPolicy != null) {
            Iterator<PolicyItem> it = list.iterator();
            while (it.hasNext()) {
                if (it.next().equals(adESPolicy2)) {
                    adESPolicy2 = null;
                }
            }
        }
        this.policies = new ArrayList();
        if (this.allowNoPolicy) {
            this.policies.add(new PolicyItem(SimpleAfirmaMessages.getString("PreferencesPanel.24"), null));
        }
        Iterator<PolicyItem> it2 = list.iterator();
        while (it2.hasNext()) {
            this.policies.add(it2.next());
        }
        if (this.allowCustomPolicy) {
            this.policies.add(new PolicyItem(SimpleAfirmaMessages.getString("PreferencesPanel.26"), adESPolicy2));
        }
        this.currentPolicy = adESPolicy;
        createUI(str, z3, z4);
    }

    private void createUI(final String str, boolean z, boolean z2) {
        if (AOSignConstants.SIGN_FORMAT_FACTURAE.equals(this.signatureFormat)) {
            setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanel.153")));
        } else {
            setBorder(BorderFactory.createEmptyBorder());
        }
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridy = 0;
        this.policiesCombo = new JComboBox<>();
        this.policiesCombo.setModel(new DefaultComboBoxModel(this.policies.toArray(new PolicyItem[this.policies.size()])));
        Component jLabel = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.23"));
        jLabel.setLabelFor(this.policiesCombo);
        if (AOSignConstants.SIGN_FORMAT_FACTURAE.equals(this.signatureFormat)) {
            Component componentCreateFacturaEPolicyPanel = createFacturaEPolicyPanel(jLabel, this.policiesCombo);
            gridBagConstraints.gridy++;
            add(componentCreateFacturaEPolicyPanel, gridBagConstraints);
        } else {
            gridBagConstraints.gridy++;
            add(jLabel, gridBagConstraints);
            gridBagConstraints.gridy++;
            add(this.policiesCombo, gridBagConstraints);
        }
        this.policiesCombo.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.183"));
        this.policiesCombo.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.47"));
        this.policiesCombo.setEnabled(!z2);
        if (z) {
            boolean z3 = this.policiesCombo.getSelectedIndex() > getNoPolicyIndex();
            boolean z4 = this.policiesCombo.getSelectedIndex() == getCustomPolicyIndex();
            this.identifierField.setEnabled(z3);
            this.identifierField.setEditable(z4);
            this.identifierField.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.54"));
            this.identifierField.addFocusListener(new FocusListener() { // from class: es.gob.afirma.standalone.ui.preferences.PolicyPanel.1
                public void focusLost(FocusEvent focusEvent) {
                    if (!AOSignConstants.SIGN_FORMAT_XADES.equals(str)) {
                        PolicyPanel.this.getIdentifierField().setText(PolicyPanel.this.getIdentifierField().getText().replace("urn:oid:", ""));
                        return;
                    }
                    try {
                        new Oid(PolicyPanel.this.getIdentifierField().getText());
                        PolicyPanel.this.getIdentifierField().setText("urn:oid:" + PolicyPanel.this.getIdentifierField().getText());
                    } catch (Exception e) {
                    }
                }

                public void focusGained(FocusEvent focusEvent) {
                }
            });
            Component jLabel2 = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel." + str + ".27"));
            jLabel2.setLabelFor(this.identifierField);
            gridBagConstraints.gridy++;
            add(jLabel2, gridBagConstraints);
            gridBagConstraints.gridy++;
            add(this.identifierField, gridBagConstraints);
            this.hashField.setEnabled(z3);
            this.hashField.setEditable(z4);
            this.hashField.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.55"));
            Component jLabel3 = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.28"));
            jLabel3.setLabelFor(this.hashField);
            gridBagConstraints.gridy++;
            add(jLabel3, gridBagConstraints);
            gridBagConstraints.gridy++;
            add(this.hashField, gridBagConstraints);
            this.hashAlgorithmField.setEnabled(z4);
            this.hashAlgorithmField.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.50"));
            Component jLabel4 = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.29"));
            jLabel4.setLabelFor(this.hashAlgorithmField);
            gridBagConstraints.gridy++;
            add(jLabel4, gridBagConstraints);
            gridBagConstraints.gridy++;
            add(this.hashAlgorithmField, gridBagConstraints);
            this.qualifierField.setEnabled(z3);
            this.qualifierField.setEditable(z4);
            this.qualifierField.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.56"));
            Component jLabel5 = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.30"));
            jLabel5.setLabelFor(this.qualifierField);
            gridBagConstraints.gridy++;
            add(jLabel5, gridBagConstraints);
            gridBagConstraints.gridy++;
            add(this.qualifierField, gridBagConstraints);
        }
        this.policiesCombo.addItemListener(this);
        if (this.currentPolicy != null) {
            for (int i = 0; i < this.policiesCombo.getItemCount(); i++) {
                if (((PolicyItem) this.policiesCombo.getItemAt(i)).equals(this.currentPolicy)) {
                    this.policiesCombo.setSelectedIndex(i);
                }
            }
        }
        loadPolicy(((PolicyItem) this.policiesCombo.getSelectedItem()).getPolicy());
    }

    private static JPanel createFacturaEPolicyPanel(Component component, Component component2) {
        JPanel jPanel = new JPanel(new FlowLayout(0));
        JPanel jPanel2 = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 21;
        gridBagConstraints.insets = new Insets(0, 7, 4, 7);
        gridBagConstraints.gridx = 0;
        jPanel2.add(component, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        jPanel2.add(component2, gridBagConstraints);
        jPanel.add(jPanel2);
        return jPanel;
    }

    private int getCustomPolicyIndex() {
        if (!this.allowCustomPolicy) {
            return WinNT.MAXLONG;
        }
        return this.policiesCombo.getItemCount() - 1;
    }

    private int getNoPolicyIndex() {
        if (!this.allowNoPolicy) {
            return -1;
        }
        return 0;
    }

    void setModificationListener(ModificationListener modificationListener) {
        this.policiesCombo.addItemListener(modificationListener);
        this.identifierField.addKeyListener(modificationListener);
        this.hashField.addKeyListener(modificationListener);
        this.hashAlgorithmField.addItemListener(modificationListener);
        this.qualifierField.addKeyListener(modificationListener);
    }

    void setKeyListener(KeyListener keyListener) {
        this.identifierField.addKeyListener(keyListener);
        this.hashField.addKeyListener(keyListener);
        this.hashAlgorithmField.addKeyListener(keyListener);
        this.qualifierField.addKeyListener(keyListener);
    }

    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent != null && itemEvent.getStateChange() == 2) {
            return;
        }
        boolean z = this.policiesCombo.getSelectedIndex() > getNoPolicyIndex();
        boolean z2 = this.policiesCombo.getSelectedIndex() == getCustomPolicyIndex();
        this.identifierField.setEnabled(z);
        this.hashField.setEnabled(z);
        this.qualifierField.setEnabled(z);
        this.identifierField.setEditable(z2);
        this.hashField.setEditable(z2);
        this.qualifierField.setEditable(z2);
        this.hashAlgorithmField.setEnabled(z2);
        loadPolicy(((PolicyItem) this.policiesCombo.getSelectedItem()).getPolicy());
    }

    void loadPolicy(AdESPolicy adESPolicy) {
        if (adESPolicy != null) {
            this.identifierField.setText(adESPolicy.getPolicyIdentifier());
            this.hashField.setText(adESPolicy.getPolicyIdentifierHash());
            this.hashAlgorithmField.setSelectedItem(adESPolicy.getPolicyIdentifierHashAlgorithm());
            this.qualifierField.setText(adESPolicy.getPolicyQualifier() != null ? adESPolicy.getPolicyQualifier().toString() : "");
            return;
        }
        this.identifierField.setText("");
        this.hashField.setText("");
        this.qualifierField.setText("");
    }

    void saveCurrentPolicy() {
        AdESPolicy selectedPolicy = getSelectedPolicy();
        if (this.policiesCombo.getSelectedIndex() == getCustomPolicyIndex()) {
            ((PolicyItem) this.policiesCombo.getSelectedItem()).setPolicy(selectedPolicy);
        } else if (this.policiesCombo.getItemAt(getCustomPolicyIndex()) != null) {
            ((PolicyItem) this.policiesCombo.getItemAt(getCustomPolicyIndex())).setPolicy(null);
        }
        this.currentPolicy = selectedPolicy;
    }

    AdESPolicy getSelectedPolicy() {
        if (this.policiesCombo.getSelectedIndex() <= getNoPolicyIndex()) {
            return null;
        }
        return new AdESPolicy(this.identifierField.getText(), this.hashField.getText(), this.hashAlgorithmField.getSelectedItem().toString(), this.qualifierField.getText());
    }

    public String getSelectedPolicyName() {
        return ((PolicyItem) this.policiesCombo.getSelectedItem()).getName();
    }

    public PolicyItem getCurrentPolicyItem() {
        return (PolicyItem) this.policiesCombo.getSelectedItem();
    }

    JTextField getIdentifierField() {
        return this.identifierField;
    }
}
