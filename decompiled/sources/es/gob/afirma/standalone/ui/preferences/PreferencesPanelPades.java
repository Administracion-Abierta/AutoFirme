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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/PreferencesPanelPades.class */
final class PreferencesPanelPades extends JScrollPane {
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final long serialVersionUID = 4299378019540627483L;
    private static final AdESPolicy POLICY_PADES_AGE_1_9;
    private static final String POLICY_BUNDLE_NAME = "policy";
    private PolicyPanel padesPolicyDlg;
    private JLabel currentPolicyValue;
    private boolean blocked = true;
    private final JComboBox<Object> padesBasicFormat = new JComboBox<>();
    private final JComboBox<Object> pdfSignCertified = new JComboBox<>();
    private final JTextField padesSignReason = new JTextField();
    private final JTextField padesSignProductionCity = new JTextField();
    private final JTextField padesSignerContact = new JTextField();
    private final JCheckBox visiblePdfSignature = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanel.79"));
    private final JCheckBox obfuscateCertificateInfo = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanel.175"));
    private final JCheckBox visiblePdfStamp = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanel.172"));
    private final JCheckBox checkShadowAttack = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanel.187"));
    private final JCheckBox checkAllowCertifiedPdf = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanel.204"));
    private static final String PADES_FORMAT_BASIC_TEXT;
    private static final String PADES_FORMAT_BES_TEXT;
    private static final String PDF_CERT_TYPE_0_TEXT;
    private static final String PDF_CERT_TYPE_1_TEXT;
    private static final String PDF_CERT_TYPE_2_TEXT;
    private static final String PDF_CERT_TYPE_3_TEXT;
    private static final String SIGN_FORMAT_PADES = "PAdES";

    static {
        ResourceBundle bundle = ResourceBundle.getBundle(POLICY_BUNDLE_NAME, Locale.getDefault());
        POLICY_PADES_AGE_1_9 = new AdESPolicy(bundle.getString("FirmaAGE19.policyIdentifier"), bundle.getString("FirmaAGE19.policyIdentifierHash.PAdES"), AOSignConstants.DIGEST_ALGORITHM_SHA1, bundle.getString("FirmaAGE19.policyQualifier"));
        PADES_FORMAT_BASIC_TEXT = SimpleAfirmaMessages.getString("PreferencesPanel.71");
        PADES_FORMAT_BES_TEXT = SimpleAfirmaMessages.getString("PreferencesPanel.72");
        PDF_CERT_TYPE_0_TEXT = SimpleAfirmaMessages.getString("PreferencesPanel.206");
        PDF_CERT_TYPE_1_TEXT = SimpleAfirmaMessages.getString("PreferencesPanel.207");
        PDF_CERT_TYPE_2_TEXT = SimpleAfirmaMessages.getString("PreferencesPanel.208");
        PDF_CERT_TYPE_3_TEXT = SimpleAfirmaMessages.getString("PreferencesPanel.209");
    }

    PreferencesPanelPades(KeyListener keyListener, ModificationListener modificationListener, boolean z) {
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
        this.padesBasicFormat.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.183"));
        this.padesBasicFormat.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.70"));
        this.pdfSignCertified.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.183"));
        this.pdfSignCertified.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.204"));
        DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel(new Object[]{new PreferencesPanel.ValueTextPair(AOSignConstants.PADES_SUBFILTER_BES, PADES_FORMAT_BES_TEXT), new PreferencesPanel.ValueTextPair(AOSignConstants.PADES_SUBFILTER_BASIC, PADES_FORMAT_BASIC_TEXT)});
        DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel(new Object[]{new PreferencesPanel.ValueTextPair("0", PDF_CERT_TYPE_0_TEXT), new PreferencesPanel.ValueTextPair("1", PDF_CERT_TYPE_1_TEXT), new PreferencesPanel.ValueTextPair("2", PDF_CERT_TYPE_2_TEXT), new PreferencesPanel.ValueTextPair("3", PDF_CERT_TYPE_3_TEXT)});
        this.padesBasicFormat.setModel(defaultComboBoxModel);
        this.padesBasicFormat.addItemListener(modificationListener);
        this.padesBasicFormat.addKeyListener(keyListener);
        this.padesBasicFormat.setEnabled(!isBlocked());
        this.pdfSignCertified.setModel(defaultComboBoxModel2);
        this.pdfSignCertified.addItemListener(modificationListener);
        this.pdfSignCertified.addKeyListener(keyListener);
        this.pdfSignCertified.setEnabled(isBlocked());
        this.visiblePdfSignature.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.182"));
        this.visiblePdfSignature.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.79"));
        this.visiblePdfSignature.addItemListener(modificationListener);
        this.visiblePdfSignature.addKeyListener(keyListener);
        this.obfuscateCertificateInfo.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.182"));
        this.obfuscateCertificateInfo.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.175"));
        this.obfuscateCertificateInfo.addItemListener(modificationListener);
        this.obfuscateCertificateInfo.addKeyListener(keyListener);
        this.visiblePdfStamp.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.182"));
        this.visiblePdfStamp.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.172"));
        this.visiblePdfStamp.addItemListener(modificationListener);
        this.visiblePdfStamp.addKeyListener(keyListener);
        this.checkShadowAttack.setEnabled(!this.blocked);
        this.checkShadowAttack.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.188"));
        this.checkShadowAttack.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.187"));
        this.checkShadowAttack.addActionListener(new ActionListener() { // from class: es.gob.afirma.standalone.ui.preferences.PreferencesPanelPades.1
            public void actionPerformed(ActionEvent actionEvent) {
                if (((JCheckBox) actionEvent.getSource()).isSelected()) {
                    AOUIFactory.showMessageDialog(this, SimpleAfirmaMessages.getString("PreferencesPanel.189"), SimpleAfirmaMessages.getString("SimpleAfirma.48"), AOUIFactory.WARNING_MESSAGE);
                }
            }
        });
        this.checkShadowAttack.addItemListener(modificationListener);
        this.checkShadowAttack.addKeyListener(keyListener);
        this.checkAllowCertifiedPdf.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.204"));
        this.checkAllowCertifiedPdf.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.205"));
        this.checkAllowCertifiedPdf.addActionListener(new ActionListener() { // from class: es.gob.afirma.standalone.ui.preferences.PreferencesPanelPades.2
            public void actionPerformed(ActionEvent actionEvent) {
                PreferencesPanelPades.this.pdfSignCertified.setEnabled(((JCheckBox) actionEvent.getSource()).isSelected());
            }
        });
        this.checkAllowCertifiedPdf.addItemListener(modificationListener);
        this.checkAllowCertifiedPdf.addKeyListener(keyListener);
        loadPreferences();
        loadPadesPolicy();
        if (this.padesPolicyDlg.getSelectedPolicy() != null) {
            this.padesBasicFormat.removeAllItems();
            this.padesBasicFormat.addItem(new PreferencesPanel.ValueTextPair(AOSignConstants.PADES_SUBFILTER_BES, PADES_FORMAT_BES_TEXT));
            this.padesBasicFormat.addItem(new PreferencesPanel.ValueTextPair(AOSignConstants.PADES_SUBFILTER_BASIC, PADES_FORMAT_BASIC_TEXT));
            this.padesBasicFormat.setSelectedIndex(0);
            this.padesBasicFormat.setEnabled(false);
        }
        this.padesPolicyDlg.setModificationListener(modificationListener);
        this.padesPolicyDlg.setKeyListener(keyListener);
        JPanel jPanelCreatePolicyPanel = createPolicyPanel();
        jPanelCreatePolicyPanel.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanel.153")));
        gridBagConstraints.gridy++;
        jPanel.add(jPanelCreatePolicyPanel, gridBagConstraints);
        JPanel jPanelCreateSignatureOptionsPanel = createSignatureOptionsPanel();
        jPanelCreateSignatureOptionsPanel.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanel.69")));
        gridBagConstraints.gridy++;
        jPanel.add(jPanelCreateSignatureOptionsPanel, gridBagConstraints);
        JPanel jPanel2 = new JPanel();
        jPanel2.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanel.19")));
        jPanel2.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = 1;
        gridBagConstraints2.weightx = 1.0d;
        gridBagConstraints2.gridy = 0;
        JLabel jLabel = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.20"));
        jLabel.setLabelFor(this.padesSignReason);
        jPanel2.add(jLabel, gridBagConstraints2);
        gridBagConstraints2.gridy++;
        this.padesSignReason.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.181"));
        this.padesSignReason.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.63"));
        this.padesSignReason.addKeyListener(modificationListener);
        this.padesSignReason.addKeyListener(keyListener);
        jPanel2.add(this.padesSignReason, gridBagConstraints2);
        gridBagConstraints2.gridy++;
        JLabel jLabel2 = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.21"));
        jLabel2.setLabelFor(this.padesSignProductionCity);
        jPanel2.add(jLabel2, gridBagConstraints2);
        gridBagConstraints2.gridy++;
        this.padesSignProductionCity.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.181"));
        this.padesSignProductionCity.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.64"));
        this.padesSignProductionCity.addKeyListener(modificationListener);
        this.padesSignProductionCity.addKeyListener(keyListener);
        jPanel2.add(this.padesSignProductionCity, gridBagConstraints2);
        gridBagConstraints2.gridy++;
        JLabel jLabel3 = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.22"));
        jLabel3.setLabelFor(this.padesSignerContact);
        jPanel2.add(jLabel3, gridBagConstraints2);
        gridBagConstraints2.gridy++;
        this.padesSignerContact.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.181"));
        this.padesSignerContact.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.65"));
        this.padesSignerContact.addKeyListener(modificationListener);
        this.padesSignerContact.addKeyListener(keyListener);
        jPanel2.add(this.padesSignerContact, gridBagConstraints2);
        gridBagConstraints2.gridy++;
        gridBagConstraints2.weighty = 1.0d;
        jPanel2.add(new JPanel(), gridBagConstraints2);
        gridBagConstraints.gridy++;
        jPanel.add(jPanel2, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.weighty = 1.0d;
        jPanel.add(new JPanel(), gridBagConstraints);
        JPanel jPanel3 = new JPanel(new FlowLayout(4));
        JButton jButton = new JButton(SimpleAfirmaMessages.getString("PreferencesPanel.147"));
        jButton.setMnemonic('R');
        jButton.addActionListener(actionEvent -> {
            if (AOUIFactory.showConfirmDialog(getParent(), SimpleAfirmaMessages.getString("PreferencesPanel.155"), SimpleAfirmaMessages.getString("PreferencesPanel.139"), 0, 2) == 0) {
                restorePreferences();
            }
        });
        jButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.136"));
        gridBagConstraints.gridy++;
        gridBagConstraints.weighty = 0.0d;
        jPanel3.add(jButton, gridBagConstraints);
        gridBagConstraints.gridy++;
        jPanel.add(jPanel3, gridBagConstraints);
        setViewportView(jPanel);
    }

    private JPanel createPolicyPanel() {
        JLabel jLabel = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.171"));
        this.currentPolicyValue = new JLabel(this.padesPolicyDlg.getSelectedPolicyName());
        this.currentPolicyValue.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        this.currentPolicyValue.setFocusable(true);
        this.currentPolicyValue.addFocusListener(new FocusListener() { // from class: es.gob.afirma.standalone.ui.preferences.PreferencesPanelPades.3
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
            changePadesPolicyDlg(getParent());
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

    private JPanel createSignatureOptionsPanel() {
        JPanel jPanel = new JPanel(new GridBagLayout());
        JLabel jLabel = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.115"));
        jLabel.setLabelFor(this.padesBasicFormat);
        JLabel jLabel2 = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.203"));
        jLabel2.setLabelFor(this.pdfSignCertified);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 21;
        gridBagConstraints.insets = new Insets(0, 7, 4, 7);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel.add(jLabel, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        jPanel.add(this.padesBasicFormat, gridBagConstraints);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy++;
        gridBagConstraints.gridwidth = 2;
        jPanel.add(this.visiblePdfSignature, gridBagConstraints);
        gridBagConstraints.gridy++;
        jPanel.add(this.obfuscateCertificateInfo, gridBagConstraints);
        gridBagConstraints.gridy++;
        jPanel.add(this.visiblePdfStamp, gridBagConstraints);
        gridBagConstraints.gridy++;
        jPanel.add(this.checkShadowAttack, gridBagConstraints);
        gridBagConstraints.gridy++;
        JPanel jPanel2 = new JPanel(new FlowLayout(0));
        jPanel2.add(this.checkAllowCertifiedPdf);
        jPanel2.add(jLabel2);
        jPanel2.add(this.pdfSignCertified);
        gridBagConstraints.insets = new Insets(-5, 2, 0, 0);
        gridBagConstraints.gridx = 0;
        jPanel.add(jPanel2, gridBagConstraints);
        JPanel jPanel3 = new JPanel(new FlowLayout(0));
        jPanel3.add(jPanel);
        return jPanel3;
    }

    void checkPreferences() throws AOException {
        loadPadesPolicy();
        AdESPolicy selectedPolicy = this.padesPolicyDlg.getSelectedPolicy();
        if (selectedPolicy != null) {
            try {
                new Oid(selectedPolicy.getPolicyIdentifier().replace("urn:oid:", ""));
            } catch (GSSException e) {
                throw new AOException("El identificador debe ser un OID", e);
            }
        }
    }

    void savePreferences() {
        PreferencesManager.put(PreferencesManager.PREFERENCE_PADES_VISIBLE, Boolean.toString(this.visiblePdfSignature.isSelected()));
        PreferencesManager.put(PreferencesManager.PREFERENCE_PADES_OBFUSCATE_CERT_INFO, Boolean.toString(this.obfuscateCertificateInfo.isSelected()));
        PreferencesManager.put(PreferencesManager.PREFERENCE_PADES_STAMP, Boolean.toString(this.visiblePdfStamp.isSelected()));
        PreferencesManager.put("allowShadowAttack", Boolean.toString(this.checkShadowAttack.isSelected()));
        PreferencesManager.put(PreferencesManager.PREFERENCE_PADES_CHECK_ALLOW_CERTIFIED_PDF, Boolean.toString(this.checkAllowCertifiedPdf.isSelected()));
        PreferencesManager.put(PreferencesManager.PREFERENCE_PADES_SIGNER_CONTACT, this.padesSignerContact.getText());
        PreferencesManager.put(PreferencesManager.PREFERENCE_PADES_SIGN_PRODUCTION_CITY, this.padesSignProductionCity.getText());
        PreferencesManager.put(PreferencesManager.PREFERENCE_PADES_SIGN_REASON, this.padesSignReason.getText());
        PreferencesManager.put(PreferencesManager.PREFERENCE_PADES_FORMAT, ((PreferencesPanel.ValueTextPair) this.padesBasicFormat.getModel().getElementAt(this.padesBasicFormat.getSelectedIndex())).getValue());
        PreferencesManager.put(PreferencesManager.PREFERENCE_PADES_DEFAULT_CERTIFICATION_LEVEL, ((PreferencesPanel.ValueTextPair) this.pdfSignCertified.getModel().getElementAt(this.pdfSignCertified.getSelectedIndex())).getValue());
        AdESPolicy selectedPolicy = this.padesPolicyDlg.getSelectedPolicy();
        if (selectedPolicy != null) {
            PreferencesManager.put(PreferencesManager.PREFERENCE_PADES_POLICY_IDENTIFIER, selectedPolicy.getPolicyIdentifier());
            PreferencesManager.put(PreferencesManager.PREFERENCE_PADES_POLICY_HASH, selectedPolicy.getPolicyIdentifierHash());
            PreferencesManager.put(PreferencesManager.PREFERENCE_PADES_POLICY_HASH_ALGORITHM, selectedPolicy.getPolicyIdentifierHashAlgorithm());
            if (selectedPolicy.getPolicyQualifier() != null) {
                PreferencesManager.put(PreferencesManager.PREFERENCE_PADES_POLICY_QUALIFIER, selectedPolicy.getPolicyQualifier().toString());
            } else {
                PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_POLICY_QUALIFIER);
            }
        } else {
            PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_POLICY_IDENTIFIER);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_POLICY_HASH);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_POLICY_HASH_ALGORITHM);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_POLICY_QUALIFIER);
        }
        this.padesPolicyDlg.saveCurrentPolicy();
    }

    void loadPreferences() {
        this.padesSignReason.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_SIGN_REASON));
        this.padesSignProductionCity.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_SIGN_PRODUCTION_CITY));
        this.padesSignerContact.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_SIGNER_CONTACT));
        this.visiblePdfSignature.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_PADES_VISIBLE));
        this.obfuscateCertificateInfo.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_PADES_OBFUSCATE_CERT_INFO));
        this.visiblePdfStamp.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_PADES_STAMP));
        this.checkShadowAttack.setSelected(PreferencesManager.getBoolean("allowShadowAttack"));
        this.checkAllowCertifiedPdf.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_PADES_CHECK_ALLOW_CERTIFIED_PDF));
        ComboBoxModel model = this.padesBasicFormat.getModel();
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_FORMAT);
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).equals(str)) {
                this.padesBasicFormat.setSelectedIndex(i);
                break;
            }
        }
        if (this.checkAllowCertifiedPdf.isSelected()) {
            this.pdfSignCertified.setEnabled(true);
            ComboBoxModel model2 = this.pdfSignCertified.getModel();
            String str2 = PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_DEFAULT_CERTIFICATION_LEVEL);
            for (int i2 = 0; i2 < model2.getSize(); i2++) {
                if (model2.getElementAt(i2).equals(str2)) {
                    this.pdfSignCertified.setSelectedIndex(i2);
                    break;
                }
            }
        }
        if (this.pdfSignCertified.getSelectedIndex() == -1 && this.pdfSignCertified.getItemCount() > 0) {
            this.pdfSignCertified.setSelectedIndex(0);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(new PolicyItem(SimpleAfirmaMessages.getString("PreferencesPanel.73"), POLICY_PADES_AGE_1_9));
        this.padesPolicyDlg = new PolicyPanel("PAdES", arrayList, getPadesPreferedPolicy(), isBlocked());
        revalidate();
        repaint();
    }

    void restorePreferences() {
        PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_SIGN_REASON);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_SIGN_PRODUCTION_CITY);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_SIGNER_CONTACT);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_VISIBLE);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_OBFUSCATE_CERT_INFO);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_STAMP);
        this.padesSignReason.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_SIGN_REASON));
        this.padesSignProductionCity.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_SIGN_PRODUCTION_CITY));
        this.padesSignerContact.setText(PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_SIGNER_CONTACT));
        this.visiblePdfSignature.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_PADES_VISIBLE));
        this.obfuscateCertificateInfo.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_PADES_OBFUSCATE_CERT_INFO));
        this.visiblePdfStamp.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_PADES_STAMP));
        if (!isBlocked()) {
            PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_FORMAT);
            String str = PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_FORMAT);
            ComboBoxModel model = this.padesBasicFormat.getModel();
            for (int i = 0; i < model.getSize(); i++) {
                if (model.getElementAt(i).equals(str)) {
                    this.padesBasicFormat.setSelectedIndex(i);
                    break;
                }
            }
            if (this.padesBasicFormat.getSelectedIndex() == -1 && this.padesBasicFormat.getItemCount() > 0) {
                this.padesBasicFormat.setSelectedIndex(0);
            }
            PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_POLICY_IDENTIFIER);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_POLICY_HASH);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_POLICY_HASH_ALGORITHM);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_POLICY_QUALIFIER);
            ArrayList arrayList = new ArrayList();
            arrayList.add(new PolicyItem(SimpleAfirmaMessages.getString("PreferencesPanel.73"), POLICY_PADES_AGE_1_9));
            this.padesPolicyDlg = new PolicyPanel("PAdES", arrayList, getPadesDefaultPolicy(), isBlocked());
            this.currentPolicyValue.setText(this.padesPolicyDlg.getSelectedPolicyName());
            PreferencesManager.remove("allowShadowAttack");
            this.checkShadowAttack.setSelected(PreferencesManager.getBoolean("allowShadowAttack"));
            PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_CHECK_ALLOW_CERTIFIED_PDF);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_DEFAULT_CERTIFICATION_LEVEL);
            this.checkAllowCertifiedPdf.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_PADES_CHECK_ALLOW_CERTIFIED_PDF));
            this.pdfSignCertified.setEnabled(this.checkAllowCertifiedPdf.isSelected());
            String str2 = PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_DEFAULT_CERTIFICATION_LEVEL);
            ComboBoxModel model2 = this.pdfSignCertified.getModel();
            for (int i2 = 0; i2 < model2.getSize(); i2++) {
                if (model2.getElementAt(i2).equals(str2)) {
                    this.pdfSignCertified.setSelectedIndex(i2);
                    break;
                }
            }
            if (this.pdfSignCertified.getSelectedIndex() == -1 && this.pdfSignCertified.getItemCount() > 0) {
                this.pdfSignCertified.setSelectedIndex(0);
            }
        }
        revalidate();
        repaint();
    }

    private static AdESPolicy getPadesPreferedPolicy() {
        AdESPolicy adESPolicy = null;
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_POLICY_IDENTIFIER);
        if (str != null && !str.isEmpty()) {
            try {
                adESPolicy = new AdESPolicy(str, PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_POLICY_HASH), PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_POLICY_HASH_ALGORITHM), PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_POLICY_QUALIFIER));
            } catch (Exception e) {
                LOGGER.severe("Error al recuperar la politica PAdES guardada en preferencias: " + e);
            }
        }
        return adESPolicy;
    }

    private AdESPolicy getPadesDefaultPolicy() {
        AdESPolicy adESPolicy = null;
        loadPadesPolicy();
        if (isBlocked()) {
            adESPolicy = this.padesPolicyDlg.getSelectedPolicy();
        } else {
            try {
                String str = PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_POLICY_IDENTIFIER);
                if (str != null && !str.isEmpty()) {
                    adESPolicy = new AdESPolicy(str, PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_POLICY_HASH), PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_POLICY_HASH_ALGORITHM), PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_POLICY_QUALIFIER));
                }
                this.padesPolicyDlg.loadPolicy(adESPolicy);
            } catch (Exception e) {
                LOGGER.severe("Error al recuperar la politica PAdES guardada en preferencias: " + e);
            }
        }
        return adESPolicy;
    }

    public boolean isBlocked() {
        return this.blocked;
    }

    public void setBlocked(boolean z) {
        this.blocked = z;
    }

    private void loadPadesPolicy() {
        if (this.padesPolicyDlg == null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new PolicyItem(SimpleAfirmaMessages.getString("PreferencesPanel.73"), POLICY_PADES_AGE_1_9));
            this.padesPolicyDlg = new PolicyPanel("PAdES", arrayList, getPadesPreferedPolicy(), isBlocked());
        }
    }

    public void changePadesPolicyDlg(Container container) {
        loadPadesPolicy();
        if (AOUIFactory.showConfirmDialog(container, this.padesPolicyDlg, SimpleAfirmaMessages.getString("PolicyDialog.0"), 2, -1) == 0) {
            try {
                checkPreferences();
                this.currentPolicyValue.setText(this.padesPolicyDlg.getSelectedPolicyName());
                AdESPolicy selectedPolicy = this.padesPolicyDlg.getSelectedPolicy();
                if (selectedPolicy != null) {
                    PreferencesManager.put(PreferencesManager.PREFERENCE_PADES_POLICY_IDENTIFIER, selectedPolicy.getPolicyIdentifier());
                    PreferencesManager.put(PreferencesManager.PREFERENCE_PADES_POLICY_HASH, selectedPolicy.getPolicyIdentifierHash());
                    PreferencesManager.put(PreferencesManager.PREFERENCE_PADES_POLICY_HASH_ALGORITHM, selectedPolicy.getPolicyIdentifierHashAlgorithm());
                    if (selectedPolicy.getPolicyQualifier() != null) {
                        PreferencesManager.put(PreferencesManager.PREFERENCE_PADES_POLICY_QUALIFIER, selectedPolicy.getPolicyQualifier().toString());
                    } else {
                        PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_POLICY_QUALIFIER);
                    }
                    this.padesBasicFormat.removeAllItems();
                    this.padesBasicFormat.addItem(new PreferencesPanel.ValueTextPair(AOSignConstants.PADES_SUBFILTER_BES, PADES_FORMAT_BES_TEXT));
                    this.padesBasicFormat.addItem(new PreferencesPanel.ValueTextPair(AOSignConstants.PADES_SUBFILTER_BASIC, PADES_FORMAT_BASIC_TEXT));
                    this.padesBasicFormat.setSelectedIndex(0);
                    this.padesBasicFormat.setEnabled(false);
                } else {
                    PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_POLICY_IDENTIFIER);
                    PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_POLICY_HASH);
                    PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_POLICY_HASH_ALGORITHM);
                    PreferencesManager.remove(PreferencesManager.PREFERENCE_PADES_POLICY_QUALIFIER);
                    this.padesBasicFormat.setEnabled(true);
                }
                this.padesPolicyDlg.saveCurrentPolicy();
            } catch (Exception e) {
                AOUIFactory.showErrorMessage("<p>" + SimpleAfirmaMessages.getString("PreferencesPanel.7") + "</p>", SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e);
                changePadesPolicyDlg(container);
            }
        }
        this.padesPolicyDlg = null;
    }
}
