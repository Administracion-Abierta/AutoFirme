package es.gob.afirma.standalone.ui;

import com.aowagie.text.pdf.ColumnText;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.AOSignerFactory;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.signers.pades.AOPDFSigner;
import es.gob.afirma.signers.xades.XAdESExtraParams;
import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import es.gob.afirma.standalone.ui.preferences.AgePolicy;
import es.gob.afirma.standalone.ui.preferences.FormatItem;
import es.gob.afirma.standalone.ui.preferences.PreferencesDialog;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.util.Map;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignatureConfigInfoPanel.class */
public class SignatureConfigInfoPanel extends JPanel {
    private static final long serialVersionUID = -2242593726948948111L;
    private String accesibleDescription;
    private JLabel signFormatLabel;
    private JPanel attributesPanel;
    private JPanel signOptionsPanel;
    private SignPanelFilePanel signPanelFile;
    private JLabel noCertPDFAllowLabel;
    private static final String PADES_CERT_TIPE_1 = SimpleAfirmaMessages.getString("PreferencesPanel.207");
    private static final String PADES_CERT_TIPE_2 = SimpleAfirmaMessages.getString("PreferencesPanel.208");
    private static final String PADES_CERT_TIPE_3 = SimpleAfirmaMessages.getString("PreferencesPanel.209");
    private JCheckBox pdfVisible = null;
    private JCheckBox pdfStamp = null;
    private JCheckBox pdfCertifiedSignature = null;
    private final JComboBox<Object> pdfCertifiedSignatureLevel = new JComboBox<>();

    public SignatureConfigInfoPanel(SignOperationConfig signOperationConfig, Color color, SignPanelFilePanel signPanelFilePanel) {
        this.signPanelFile = signPanelFilePanel;
        createUI(signOperationConfig, color);
    }

    public SignatureConfigInfoPanel() {
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignatureConfigInfoPanel$ValueTextPair.class */
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

    /* JADX INFO: Access modifiers changed from: private */
    public SignatureConfigInfoPanel createUI(SignOperationConfig signOperationConfig, Color color) {
        removeAll();
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            setBackground(color);
        }
        setLayout(new GridBagLayout());
        setAlignmentY(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(2, 0, 2, 0);
        gridBagConstraints.anchor = 17;
        this.signFormatLabel = new JLabel(SimpleAfirmaMessages.getString("SignPanel.103", signOperationConfig.getSignatureFormatName()));
        this.accesibleDescription += SimpleAfirmaMessages.getString("SignPanel.103", signOperationConfig.getSignatureFormatName());
        Component jLabel = new JLabel(SimpleAfirmaMessages.getString("SignPanel.143"));
        this.accesibleDescription += SimpleAfirmaMessages.getString("SignPanel.143");
        this.attributesPanel = createAttributesPanel(signOperationConfig);
        this.signOptionsPanel = createOptionsPanel(signOperationConfig, color);
        add(createFormatOptionsPanel(signOperationConfig, color), gridBagConstraints);
        gridBagConstraints.gridy++;
        if (this.attributesPanel != null) {
            if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                this.attributesPanel.setBackground(color);
            }
            add(jLabel, gridBagConstraints);
            gridBagConstraints.gridy++;
            add(this.attributesPanel, gridBagConstraints);
            gridBagConstraints.gridy++;
        }
        if (this.signOptionsPanel != null) {
            if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                this.signOptionsPanel.setBackground(color);
            }
            add(this.signOptionsPanel, gridBagConstraints);
            gridBagConstraints.gridy++;
        }
        return this;
    }

    private JPanel createAttributesPanel(SignOperationConfig signOperationConfig) {
        String string;
        String property = null;
        String property2 = null;
        boolean z = false;
        if (signOperationConfig.getExtraParams() != null) {
            Properties extraParams = signOperationConfig.getExtraParams();
            property = extraParams.getProperty("policyIdentifier");
            property2 = extraParams.getProperty("signerClaimedRoles");
            z = extraParams.containsKey(XAdESExtraParams.SIGNATURE_PRODUCCTION_STREET_ADDRESS) || extraParams.containsKey("signatureProductionCity") || extraParams.containsKey(XAdESExtraParams.SIGNATURE_PRODUCTION_PROVINCE) || extraParams.containsKey("signatureProductionPostalCode") || extraParams.containsKey("signatureProductionCountry");
        }
        if (property == null && property2 == null && !z) {
            return null;
        }
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, 1));
        if (property != null) {
            if (AgePolicy.isAGEPolicy18(property)) {
                string = SimpleAfirmaMessages.getString("PreferencesPanel.25");
            } else if (AgePolicy.isAGEPolicy19(property)) {
                string = SimpleAfirmaMessages.getString("PreferencesPanel.73");
            } else {
                string = SimpleAfirmaMessages.getString("PreferencesPanel.26");
            }
            this.accesibleDescription += string;
            jPanel.add(new JLabel(" - " + string));
            jPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        }
        if (z) {
            String string2 = SimpleAfirmaMessages.getString("SignPanel.149");
            this.accesibleDescription += string2;
            jPanel.add(new JLabel(" - " + string2));
            jPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        }
        if (property2 != null) {
            jPanel.add(new JLabel(" - " + SimpleAfirmaMessages.getString("SignPanel.144", property2)));
            this.accesibleDescription += SimpleAfirmaMessages.getString("SignPanel.144", property2);
            jPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        }
        jPanel.add(createAttributesHiperlink(SimpleAfirmaMessages.getString("SignPanel.146"), signOperationConfig));
        this.accesibleDescription += SimpleAfirmaMessages.getString("SignPanel.146");
        return jPanel;
    }

    private JLabel createAttributesHiperlink(String str, SignOperationConfig signOperationConfig) {
        JLabel jLabel = new JLabel(" - " + str);
        jLabel.setFocusable(true);
        jLabel.setForeground(LookAndFeelManager.WINDOWS_HIGH_CONTRAST ? Color.yellow : Color.blue);
        Font font = jLabel.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        jLabel.setFont(font.deriveFont(attributes));
        jLabel.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("SignDataPanel.46") + " " + str);
        this.accesibleDescription += SimpleAfirmaMessages.getString("SignDataPanel.46") + " " + str;
        SignatureAttributesListener signatureAttributesListener = new SignatureAttributesListener(signOperationConfig);
        jLabel.addMouseListener(signatureAttributesListener);
        jLabel.addFocusListener(signatureAttributesListener);
        jLabel.addKeyListener(signatureAttributesListener);
        return jLabel;
    }

    private JLabel createChangeFormatHiperlink(String str, SignOperationConfig signOperationConfig, Color color) {
        JLabel jLabel = new JLabel(str);
        jLabel.setFocusable(true);
        jLabel.setForeground(LookAndFeelManager.WINDOWS_HIGH_CONTRAST ? Color.yellow : Color.blue);
        Font font = jLabel.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        jLabel.setFont(font.deriveFont(attributes));
        jLabel.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("SignDataPanel.46") + " " + str);
        this.accesibleDescription += SimpleAfirmaMessages.getString("SignDataPanel.46") + " " + str;
        ChangeFormatListener changeFormatListener = new ChangeFormatListener(this, signOperationConfig, color, this.signPanelFile);
        jLabel.addMouseListener(changeFormatListener);
        jLabel.addFocusListener(changeFormatListener);
        jLabel.addKeyListener(changeFormatListener);
        return jLabel;
    }

    private JPanel createOptionsPanel(SignOperationConfig signOperationConfig, Color color) {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, 1));
        AOSigner signer = signOperationConfig.getSigner();
        SignOperationConfig.CryptoOperation cryptoOperation = signOperationConfig.getCryptoOperation();
        FileType fileType = signOperationConfig.getFileType();
        DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel(new Object[]{new ValueTextPair("1", PADES_CERT_TIPE_1), new ValueTextPair("2", PADES_CERT_TIPE_2), new ValueTextPair("3", PADES_CERT_TIPE_3)});
        if (signer instanceof AOPDFSigner) {
            this.pdfVisible = new JCheckBox(SimpleAfirmaMessages.getString("SignPanel.44"), PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_PADES_VISIBLE));
            this.accesibleDescription += SimpleAfirmaMessages.getString("SignPanel.44");
            if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                this.pdfVisible.setBackground(color);
            }
            this.pdfVisible.setAlignmentX(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
            jPanel.add(this.pdfVisible);
            jPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            this.pdfStamp = new JCheckBox(SimpleAfirmaMessages.getString("SignPanel.120"), PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_PADES_STAMP));
            this.accesibleDescription += SimpleAfirmaMessages.getString("SignPanel.120");
            if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                this.pdfStamp.setBackground(color);
            }
            jPanel.add(this.pdfStamp);
            jPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            if (cryptoOperation == SignOperationConfig.CryptoOperation.COSIGN) {
                this.pdfStamp.setSelected(false);
                this.pdfStamp.setEnabled(false);
                jPanel.add(new JLabel(SimpleAfirmaMessages.getString("SignPanel.121")));
                this.accesibleDescription += SimpleAfirmaMessages.getString("SignPanel.121");
                jPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            }
            this.pdfCertifiedSignature = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanel.210"));
            this.accesibleDescription += SimpleAfirmaMessages.getString("PreferencesPanel.205");
            if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                this.pdfCertifiedSignature.setBackground(color);
            }
            this.pdfCertifiedSignature.addActionListener(new ActionListener() { // from class: es.gob.afirma.standalone.ui.SignatureConfigInfoPanel.1
                public void actionPerformed(ActionEvent actionEvent) {
                    SignatureConfigInfoPanel.this.pdfCertifiedSignatureLevel.setEnabled(((JCheckBox) actionEvent.getSource()).isSelected());
                }
            });
            this.pdfCertifiedSignatureLevel.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.183"));
            this.pdfCertifiedSignatureLevel.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.210"));
            this.pdfCertifiedSignatureLevel.setModel(defaultComboBoxModel);
            this.pdfCertifiedSignatureLevel.setAlignmentX(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
            boolean z = PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_PADES_CHECK_ALLOW_CERTIFIED_PDF);
            boolean z2 = signOperationConfig.getSignValidity() != null;
            this.pdfCertifiedSignature.setVisible(z);
            this.pdfCertifiedSignatureLevel.setVisible(z);
            if (z) {
                String str = PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_DEFAULT_CERTIFICATION_LEVEL);
                if (z2) {
                    this.pdfCertifiedSignature.setSelected(false);
                    this.pdfCertifiedSignature.setEnabled(false);
                    this.pdfCertifiedSignatureLevel.setEnabled(false);
                } else if ("0".equals(str)) {
                    this.pdfCertifiedSignature.setSelected(false);
                    this.pdfCertifiedSignatureLevel.setEnabled(false);
                } else {
                    this.pdfCertifiedSignature.setSelected(true);
                    this.pdfCertifiedSignatureLevel.setEnabled(true);
                }
                ComboBoxModel model = this.pdfCertifiedSignatureLevel.getModel();
                for (int i = 0; i < model.getSize(); i++) {
                    if (model.getElementAt(i).equals(str)) {
                        this.pdfCertifiedSignatureLevel.setSelectedIndex(i);
                        break;
                    }
                }
                if (this.pdfCertifiedSignatureLevel.getSelectedIndex() == -1) {
                    this.pdfCertifiedSignatureLevel.setSelectedIndex(0);
                }
                jPanel.add(this.pdfCertifiedSignature);
                jPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                jPanel.add(this.pdfCertifiedSignatureLevel);
                jPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                if (z2) {
                    this.noCertPDFAllowLabel = new JLabel(SimpleAfirmaMessages.getString("SignPanel.158"));
                    this.accesibleDescription += SimpleAfirmaMessages.getString("SignPanel.158");
                    jPanel.add(this.noCertPDFAllowLabel);
                    jPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                }
            }
        }
        if (fileType == FileType.SIGN_CADES || fileType == FileType.SIGN_XADES) {
            JButton jButton = new JButton(SimpleAfirmaMessages.getString("SignPanel.119"));
            this.accesibleDescription += SimpleAfirmaMessages.getString("SignPanel.119");
            jPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            jButton.setMnemonic('a');
            jButton.addActionListener(actionEvent -> {
                if (fileType == FileType.SIGN_CADES) {
                    PreferencesDialog.show(null, true, 2);
                } else {
                    PreferencesDialog.show(null, true, 3);
                }
            });
            jPanel.add(jButton);
        }
        return jPanel;
    }

    private JPanel createFormatOptionsPanel(SignOperationConfig signOperationConfig, Color color) {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, 0));
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            jPanel.setBackground(color);
        }
        jPanel.add(this.signFormatLabel);
        jPanel.add(Box.createRigidArea(new Dimension(4, 0)));
        if (SignOperationConfig.CryptoOperation.SIGN.equals(signOperationConfig.getCryptoOperation())) {
            jPanel.add(createChangeFormatHiperlink(SimpleAfirmaMessages.getString("SignPanel.157"), signOperationConfig, color));
            jPanel.add(Box.createRigidArea(new Dimension(4, 0)));
        }
        return jPanel;
    }

    public boolean isPdfVisibleSignatureSelected() {
        return this.pdfVisible != null && this.pdfVisible.isSelected();
    }

    public boolean isPdfStampSignatureSelected() {
        return this.pdfStamp != null && this.pdfStamp.isSelected();
    }

    public String getPdfSignatureCertificationLevel() {
        ValueTextPair valueTextPair;
        String value = null;
        if (this.pdfCertifiedSignature != null && this.pdfCertifiedSignature.isSelected() && this.pdfCertifiedSignatureLevel != null && (valueTextPair = (ValueTextPair) this.pdfCertifiedSignatureLevel.getSelectedItem()) != null) {
            value = valueTextPair.getValue();
        }
        return value;
    }

    public String getAccesibleDescription() {
        return this.accesibleDescription;
    }

    public JLabel getSignFormatLabel() {
        return this.signFormatLabel;
    }

    public JPanel getAttributesPanel() {
        return this.attributesPanel;
    }

    public JPanel getSignOptionsPanel() {
        return this.signOptionsPanel;
    }

    public void setAttributesPanel(JPanel jPanel) {
        this.attributesPanel = jPanel;
    }

    public void setSignOptionsPanel(JPanel jPanel) {
        this.signOptionsPanel = jPanel;
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignatureConfigInfoPanel$SignatureAttributesListener.class */
    static class SignatureAttributesListener implements MouseListener, FocusListener, KeyListener {
        private final SignOperationConfig signConfig;

        public SignatureAttributesListener(SignOperationConfig signOperationConfig) {
            this.signConfig = signOperationConfig;
        }

        public void mouseReleased(MouseEvent mouseEvent) {
        }

        public void mousePressed(MouseEvent mouseEvent) {
        }

        public void mouseExited(MouseEvent mouseEvent) {
            mouseEvent.getComponent().setCursor(Cursor.getPredefinedCursor(0));
        }

        public void mouseEntered(MouseEvent mouseEvent) {
            mouseEvent.getComponent().setCursor(Cursor.getPredefinedCursor(12));
        }

        public void mouseClicked(MouseEvent mouseEvent) {
            executeAction((Component) mouseEvent.getSource(), this.signConfig);
        }

        public void focusGained(FocusEvent focusEvent) {
            ((JComponent) focusEvent.getSource()).setBorder(BorderFactory.createDashedBorder((Paint) null, 1.0f, 1.0f));
        }

        public void focusLost(FocusEvent focusEvent) {
            ((JComponent) focusEvent.getSource()).setBorder((Border) null);
        }

        private static void executeAction(Component component, SignOperationConfig signOperationConfig) {
            JDialog jDialogNewInstance = SignatureAttributesDialog.newInstance(component, signOperationConfig);
            jDialogNewInstance.getAccessibleContext().setAccessibleDescription(SignatureAttributesDialog.getAccessibleDescription());
            jDialogNewInstance.setVisible(true);
        }

        public void keyTyped(KeyEvent keyEvent) {
        }

        public void keyPressed(KeyEvent keyEvent) {
        }

        public void keyReleased(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == 10 || keyEvent.getKeyCode() == 32) {
                executeAction((Component) keyEvent.getSource(), this.signConfig);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignatureConfigInfoPanel$ChangeFormatListener.class */
    class ChangeFormatListener implements MouseListener, FocusListener, KeyListener {
        private final SignOperationConfig signConfig;
        private final SignatureConfigInfoPanel panel;
        private final SignPanelFilePanel signPanel;
        private final Color bgColor;

        public ChangeFormatListener(SignatureConfigInfoPanel signatureConfigInfoPanel, SignOperationConfig signOperationConfig, Color color, SignPanelFilePanel signPanelFilePanel) {
            this.panel = signatureConfigInfoPanel;
            this.signConfig = signOperationConfig;
            this.bgColor = color;
            this.signPanel = signPanelFilePanel;
        }

        public void mouseReleased(MouseEvent mouseEvent) {
        }

        public void mousePressed(MouseEvent mouseEvent) {
        }

        public void mouseExited(MouseEvent mouseEvent) {
            mouseEvent.getComponent().setCursor(Cursor.getPredefinedCursor(0));
        }

        public void mouseEntered(MouseEvent mouseEvent) {
            mouseEvent.getComponent().setCursor(Cursor.getPredefinedCursor(12));
        }

        public void mouseClicked(MouseEvent mouseEvent) {
            executeAction(this.signConfig);
        }

        public void focusGained(FocusEvent focusEvent) {
            ((JComponent) focusEvent.getSource()).setBorder(BorderFactory.createDashedBorder((Paint) null, 1.0f, 1.0f));
        }

        public void focusLost(FocusEvent focusEvent) {
            ((JComponent) focusEvent.getSource()).setBorder((Border) null);
        }

        private void executeAction(SignOperationConfig signOperationConfig) {
            ChangeFormatPanel changeFormatPanel = new ChangeFormatPanel(signOperationConfig);
            this.panel.getAccessibleContext().setAccessibleDescription(ChangeFormatPanel.getAccessibleDescription());
            if (AOUIFactory.showConfirmDialog(null, changeFormatPanel, SimpleAfirmaMessages.getString("ChangeFormatDialog.0"), AOUIFactory.OK_CANCEL_OPTION, AOUIFactory.PLAIN_MESSAGE) == AOUIFactory.YES_OPTION) {
                AOSigner signer = AOSignerFactory.getSigner(((FormatItem) changeFormatPanel.getUsedCombo().getSelectedItem()).getName());
                this.signConfig.setSigner(signer);
                this.signConfig.setExtraParams(ExtraParamsHelper.loadExtraParamsForSigner(signer));
                this.signConfig.setSignatureFormatName(SignPanel.getSignatureName(signOperationConfig.getSigner()));
                this.signPanel.setConfigInfoPanel(SignatureConfigInfoPanel.this.createUI(this.signConfig, this.bgColor));
                this.signPanel.updateUI();
            }
        }

        public void keyTyped(KeyEvent keyEvent) {
        }

        public void keyPressed(KeyEvent keyEvent) {
        }

        public void keyReleased(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == 10 || keyEvent.getKeyCode() == 32) {
                executeAction(this.signConfig);
            }
        }
    }
}
