package es.gob.afirma.standalone.ui;

import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Properties;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignatureAttributesDialog.class */
public class SignatureAttributesDialog {
    private static final String PROP_POLICY_ID = "policyIdentifier";
    private static final String PROP_POLICY_HASH = "policyIdentifierHash";
    private static final String PROP_POLICY_HASH_ALGO = "policyIdentifierHashAlgorithm";
    private static final String PROP_POLICY_DESCRIPTION = "policyDescription";
    private static final String PROP_POLICY_QUALIFIER = "policyQualifier";
    private static final String PROP_PLACE_STREET = "signatureProductionStreetAddress";
    private static final String PROP_PLACE_CITY = "signatureProductionCity";
    private static final String PROP_SIGN_REASON = "signReason";
    private static final String PROP_CONTACT_INFO = "signerContact";
    private static final String PROP_PLACE_PROVINCE = "signatureProductionProvince";
    private static final String PROP_PLACE_PC = "signatureProductionPostalCode";
    private static final String PROP_PLACE_COUNTRY = "signatureProductionCountry";
    private static final String PROP_ROLES = "signerClaimedRoles";
    private static final String PROP_PROFILE = "profile";
    private static final String PROFILE_B_LEVEL = "B-B-Level";
    private static final String[] SIGNED_ATTR_FLAGS = {"policyIdentifier", "signatureProductionStreetAddress", "signatureProductionCity", "signatureProductionProvince", "signatureProductionPostalCode", "signatureProductionCountry", "signerClaimedRoles", "signerContact", "signReason"};
    private static String accessibleDescription = "";

    public static JDialog newInstance(Component component, SignOperationConfig signOperationConfig) {
        accessibleDescription = "";
        JOptionPane jOptionPane = new JOptionPane();
        jOptionPane.setMessageType(-1);
        jOptionPane.setMessage(createInfoPanel(signOperationConfig));
        JDialog jDialogCreateDialog = jOptionPane.createDialog(getParentComponent(component), SimpleAfirmaMessages.getString("SignatureAttributesDialog.0"));
        jDialogCreateDialog.setIconImages(DesktopUtil.getIconImages());
        return jDialogCreateDialog;
    }

    private static Component createInfoPanel(SignOperationConfig signOperationConfig) {
        JPanel jPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.anchor = 21;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        if (hasAdditionalSignedAttributes(signOperationConfig)) {
            jPanel.add(new JLabel(SimpleAfirmaMessages.getString("SignatureAttributesDialog.1")), gridBagConstraints);
            accessibleDescription += SimpleAfirmaMessages.getString("SignatureAttributesDialog.1");
            Properties extraParams = signOperationConfig.getExtraParams();
            if (extraParams.containsKey("policyIdentifier")) {
                addHeader(jPanel, gridBagConstraints, SimpleAfirmaMessages.getString("SignatureAttributesDialog.2"));
                addData(jPanel, gridBagConstraints, SimpleAfirmaMessages.getString("SignatureAttributesDialog.3"), extraParams.getProperty("policyIdentifier"));
                if (extraParams.containsKey("policyIdentifierHash")) {
                    addData(jPanel, gridBagConstraints, SimpleAfirmaMessages.getString("SignatureAttributesDialog.4"), extraParams.getProperty("policyIdentifierHash"));
                }
                if (extraParams.containsKey("policyIdentifierHashAlgorithm")) {
                    addData(jPanel, gridBagConstraints, SimpleAfirmaMessages.getString("SignatureAttributesDialog.5"), extraParams.getProperty("policyIdentifierHashAlgorithm"));
                }
                if (extraParams.containsKey("policyDescription")) {
                    addData(jPanel, gridBagConstraints, SimpleAfirmaMessages.getString("SignatureAttributesDialog.6"), extraParams.getProperty("policyDescription"));
                }
                if (extraParams.containsKey("policyQualifier")) {
                    addData(jPanel, gridBagConstraints, SimpleAfirmaMessages.getString("SignatureAttributesDialog.7"), extraParams.getProperty("policyQualifier"));
                }
            }
            if (extraParams.containsKey("signatureProductionCity") || extraParams.containsKey("signatureProductionProvince") || extraParams.containsKey("signatureProductionPostalCode") || extraParams.containsKey("signatureProductionCountry") || (PROFILE_B_LEVEL.equalsIgnoreCase(extraParams.getProperty("profile")) && extraParams.containsKey("signatureProductionStreetAddress"))) {
                addHeader(jPanel, gridBagConstraints, SimpleAfirmaMessages.getString("SignatureAttributesDialog.8"));
                if (PROFILE_B_LEVEL.equalsIgnoreCase(extraParams.getProperty("profile")) && extraParams.containsKey("signatureProductionStreetAddress")) {
                    addData(jPanel, gridBagConstraints, SimpleAfirmaMessages.getString("SignatureAttributesDialog.9"), extraParams.getProperty("signatureProductionStreetAddress"));
                }
                if (extraParams.containsKey("signatureProductionCity")) {
                    addData(jPanel, gridBagConstraints, SimpleAfirmaMessages.getString("SignatureAttributesDialog.10"), extraParams.getProperty("signatureProductionCity"));
                }
                if (extraParams.containsKey("signatureProductionProvince")) {
                    addData(jPanel, gridBagConstraints, SimpleAfirmaMessages.getString("SignatureAttributesDialog.11"), extraParams.getProperty("signatureProductionProvince"));
                }
                if (extraParams.containsKey("signatureProductionPostalCode")) {
                    addData(jPanel, gridBagConstraints, SimpleAfirmaMessages.getString("SignatureAttributesDialog.12"), extraParams.getProperty("signatureProductionPostalCode"));
                }
                if (extraParams.containsKey("signatureProductionCountry")) {
                    addData(jPanel, gridBagConstraints, SimpleAfirmaMessages.getString("SignatureAttributesDialog.13"), extraParams.getProperty("signatureProductionCountry"));
                }
            }
            if (extraParams.containsKey("signReason") || extraParams.containsKey("signerContact")) {
                addHeader(jPanel, gridBagConstraints, SimpleAfirmaMessages.getString("SignatureAttributesDialog.19"));
                if (extraParams.containsKey("signReason")) {
                    addData(jPanel, gridBagConstraints, SimpleAfirmaMessages.getString("SignatureAttributesDialog.17"), extraParams.getProperty("signReason"));
                }
                if (extraParams.containsKey("signerContact")) {
                    addData(jPanel, gridBagConstraints, SimpleAfirmaMessages.getString("SignatureAttributesDialog.18"), extraParams.getProperty("signerContact"));
                }
            }
            if (extraParams.containsKey("signerClaimedRoles")) {
                addHeader(jPanel, gridBagConstraints, SimpleAfirmaMessages.getString("SignatureAttributesDialog.14"));
                addData(jPanel, gridBagConstraints, SimpleAfirmaMessages.getString("SignatureAttributesDialog.15"), extraParams.getProperty("signerClaimedRoles"));
            }
        } else {
            jPanel.add(new JLabel(SimpleAfirmaMessages.getString("SignatureAttributesDialog.16")), gridBagConstraints);
            accessibleDescription += SimpleAfirmaMessages.getString("SignatureAttributesDialog.16");
        }
        return jPanel;
    }

    private static boolean hasAdditionalSignedAttributes(SignOperationConfig signOperationConfig) {
        for (String str : SIGNED_ATTR_FLAGS) {
            if (signOperationConfig.getExtraParams().containsKey(str)) {
                return true;
            }
        }
        return false;
    }

    public static void addHeader(Container container, GridBagConstraints gridBagConstraints, String str) {
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy++;
        gridBagConstraints.insets = new Insets(8, 0, 2, 0);
        JLabel jLabel = new JLabel(str);
        jLabel.setFont(jLabel.getFont().deriveFont(1));
        container.add(jLabel, gridBagConstraints);
        accessibleDescription += str;
    }

    public static void addData(Container container, GridBagConstraints gridBagConstraints, String str, String str2) {
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0.0d;
        gridBagConstraints.gridy++;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(2, 15, 0, 0);
        container.add(new JLabel(str), gridBagConstraints);
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridx = 1;
        container.add(new JLabel(str2), gridBagConstraints);
        accessibleDescription += str + " " + str2;
    }

    private static Component getParentComponent(Component component) {
        Component component2;
        Component parent = component;
        while (true) {
            component2 = parent;
            if (component2 == null || component2.getParent() == null || component2 == component2.getParent()) {
                break;
            }
            parent = component2.getParent();
        }
        return component2;
    }

    public static String getAccessibleDescription() {
        return accessibleDescription;
    }
}
