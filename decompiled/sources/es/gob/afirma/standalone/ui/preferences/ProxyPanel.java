package es.gob.afirma.standalone.ui.preferences;

import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/ProxyPanel.class */
final class ProxyPanel extends JPanel {
    private static final long serialVersionUID = -5919574790093890970L;
    private static final int PREFERRED_WIDTH = 420;
    private static final int PREFERRED_HEIGHT = 210;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private final JTextField hostTF = new JTextField();
    private final JTextField portTF = new JTextField();
    private final JTextField usernameProxy = new JTextField();
    private final JPasswordField passwordProxy = new JPasswordField();
    private final JTextArea excludedUrlsTA = new JTextArea();
    private final JRadioButton noProxyRb = new JRadioButton(SimpleAfirmaMessages.getString("ProxyDialog.22"));
    private final JRadioButton systemProxyRb = new JRadioButton(SimpleAfirmaMessages.getString("ProxyDialog.23"));
    private final JRadioButton manualProxyRb = new JRadioButton(SimpleAfirmaMessages.getString("ProxyDialog.24"));
    private final JButton checkConnectionBtn = new JButton(SimpleAfirmaMessages.getString("ProxyDialog.8"));
    private final JButton detectProxyBtn = new JButton(SimpleAfirmaMessages.getString("ProxyDialog.11"));
    private final ProxyPanelHandler eventsHandler = new ProxyPanelHandler(this);
    private JPanel manualProxyPanel;

    ProxyPanel() {
        createUI();
        this.eventsHandler.registerComponents();
        new Thread(() -> {
            this.eventsHandler.loadViewData();
        }).start();
    }

    private void createUI() {
        setLayout(new GridBagLayout());
        setMinimumSize(new Dimension(PREFERRED_WIDTH, 210));
        getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("ProxyDialog.12"));
        Component jLabel = new JLabel(SimpleAfirmaMessages.getString("ProxyDialog.15"));
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(this.noProxyRb);
        buttonGroup.add(this.systemProxyRb);
        buttonGroup.add(this.manualProxyRb);
        this.manualProxyPanel = createManualProxyPanel();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(jLabel, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.insets = new Insets(10, 0, 0, 0);
        add(this.noProxyRb, gridBagConstraints);
        gridBagConstraints.gridy++;
        add(this.systemProxyRb, gridBagConstraints);
        gridBagConstraints.gridy++;
        add(this.manualProxyRb, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.insets = new Insets(10, 25, 0, 0);
        add(this.manualProxyPanel, gridBagConstraints);
    }

    private JPanel createManualProxyPanel() {
        JPanel jPanel = new JPanel(new GridBagLayout());
        JLabel jLabel = new JLabel(SimpleAfirmaMessages.getString("ProxyDialog.5"));
        jLabel.setLabelFor(this.hostTF);
        JLabel jLabel2 = new JLabel(SimpleAfirmaMessages.getString("ProxyDialog.7"));
        jLabel2.setLabelFor(this.portTF);
        JLabel jLabel3 = new JLabel(SimpleAfirmaMessages.getString("ProxyDialog.16"));
        jLabel3.setLabelFor(this.usernameProxy);
        JLabel jLabel4 = new JLabel(SimpleAfirmaMessages.getString("ProxyDialog.17"));
        jLabel4.setLabelFor(this.passwordProxy);
        JLabel jLabel5 = new JLabel(SimpleAfirmaMessages.getString("ProxyDialog.20"));
        jLabel5.setLabelFor(this.excludedUrlsTA);
        JLabel jLabel6 = new JLabel(SimpleAfirmaMessages.getString("ProxyDialog.21"));
        jLabel6.setEnabled(false);
        JScrollPane jScrollPane = new JScrollPane(this.excludedUrlsTA, 20, 31);
        JPanel jPanel2 = new JPanel(new FlowLayout(3));
        jPanel2.add(this.checkConnectionBtn);
        this.checkConnectionBtn.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("ProxyDialog.6"));
        this.checkConnectionBtn.setMnemonic('V');
        this.detectProxyBtn.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("ProxyDialog.13"));
        this.detectProxyBtn.setMnemonic('A');
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(4, 0, 0, 0);
        jPanel.add(jLabel, gridBagConstraints);
        gridBagConstraints.weightx = 0.8d;
        gridBagConstraints.gridx++;
        jPanel.add(this.hostTF, gridBagConstraints);
        gridBagConstraints.weightx = 0.0d;
        gridBagConstraints.gridx++;
        gridBagConstraints.insets = new Insets(4, 11, 0, 0);
        jPanel.add(jLabel2, gridBagConstraints);
        gridBagConstraints.weightx = 0.2d;
        gridBagConstraints.gridx++;
        gridBagConstraints.insets = new Insets(4, 0, 0, 0);
        jPanel.add(this.portTF, gridBagConstraints);
        gridBagConstraints.weightx = 0.0d;
        gridBagConstraints.gridx++;
        gridBagConstraints.insets = new Insets(4, 11, 0, 0);
        jPanel.add(this.detectProxyBtn, gridBagConstraints);
        gridBagConstraints.weightx = 0.0d;
        gridBagConstraints.gridy++;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(4, 0, 0, 0);
        jPanel.add(jLabel3, gridBagConstraints);
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridx++;
        gridBagConstraints.gridwidth = 4;
        jPanel.add(this.usernameProxy, gridBagConstraints);
        gridBagConstraints.weightx = 0.0d;
        gridBagConstraints.gridy++;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 1;
        jPanel.add(jLabel4, gridBagConstraints);
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridx++;
        gridBagConstraints.gridwidth = 4;
        jPanel.add(this.passwordProxy, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.insets = new Insets(8, 0, 0, 0);
        jPanel.add(jLabel5, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.ipady = 40;
        gridBagConstraints.insets = new Insets(4, 0, 0, 0);
        jPanel.add(jScrollPane, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.ipady = 0;
        jPanel.add(jLabel6, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        jPanel.add(jPanel2, gridBagConstraints);
        return jPanel;
    }

    void saveData() throws ConfigurationException {
        this.eventsHandler.saveViewData();
    }

    JPanel getManualProxyPanel() {
        return this.manualProxyPanel;
    }

    JRadioButton getNoProxyRb() {
        return this.noProxyRb;
    }

    JRadioButton getSystemProxyRb() {
        return this.systemProxyRb;
    }

    JRadioButton getManualProxyRb() {
        return this.manualProxyRb;
    }

    JTextComponent getHostField() {
        return this.hostTF;
    }

    JTextComponent getPortField() {
        return this.portTF;
    }

    JTextComponent getUsernameField() {
        return this.usernameProxy;
    }

    JTextComponent getPasswordField() {
        return this.passwordProxy;
    }

    JTextComponent getExcludedUrlsField() {
        return this.excludedUrlsTA;
    }

    JButton getCheckConnectionButton() {
        return this.checkConnectionBtn;
    }

    JButton getDetectProxyButton() {
        return this.detectProxyBtn;
    }
}
