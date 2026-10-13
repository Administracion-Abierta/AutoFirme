package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.signvalidation.SignValidity;
import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirma;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.plugins.OutputData;
import es.gob.afirma.standalone.plugins.PluginAction;
import es.gob.afirma.standalone.plugins.SignatureProcessAction;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignResultListPanel.class */
public final class SignResultListPanel extends JPanel implements PluginButtonsContainer {
    private static final long serialVersionUID = 1896328450345342947L;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final int DEFAULT_WINDOW_WIDTH = 600;
    private static final int DEFAULT_WINDOW_HEIGHT = 540;
    private final SimpleAfirma saf;
    private final JButton returnButton = new JButton();
    JPanel mainPluginsButtonsPanel = null;
    JPanel pluginButtonsPanel = null;
    List<SignOperationConfig> currentSignConfigs;
    X509Certificate currentSigningCert;

    public SignResultListPanel(SimpleAfirma simpleAfirma, List<SignOperationConfig> list, File file, X509Certificate x509Certificate) {
        this.currentSignConfigs = null;
        this.currentSigningCert = null;
        this.saf = simpleAfirma;
        this.currentSignConfigs = list;
        this.currentSigningCert = x509Certificate;
        createUI(list, file, x509Certificate);
    }

    private void createUI(List<SignOperationConfig> list, File file, X509Certificate x509Certificate) {
        setPreferredSize(new Dimension(600, 540));
        SignValidity signValidity = new SignValidity(SignValidity.SIGN_DETAIL_TYPE.OK, null);
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < list.size() && signValidity.getValidity() != SignValidity.SIGN_DETAIL_TYPE.KO; i++) {
            if (list.get(i).getSignatureFile() == null) {
                arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, null));
                break;
            }
        }
        if (arrayList.size() == 0) {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.OK, null));
        }
        Component signResultPanel = new SignResultPanel(arrayList, false, null);
        Component massiveResultProcessPanel = new MassiveResultProcessPanel(list, file, x509Certificate);
        Component jPanel = new JPanel(true);
        jPanel.setLayout(new BoxLayout(jPanel, 1));
        this.returnButton.setText(SimpleAfirmaMessages.getString("SignDetailPanel.0"));
        this.returnButton.setMnemonic('m');
        this.returnButton.setAlignmentX(0.5f);
        jPanel.add(this.returnButton);
        this.returnButton.addActionListener(actionEvent -> {
            goToBack();
        });
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST && Platform.getOS() == Platform.OS.WINDOWS) {
            setBackground(LookAndFeelManager.SECUNDARY_COLOR);
            jPanel.setBackground(LookAndFeelManager.SECUNDARY_COLOR);
        }
        this.mainPluginsButtonsPanel = buildMainPluginsButtonsPanel();
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(11, 11, 11, 11);
        add(signResultPanel, gridBagConstraints);
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridy++;
        gridBagConstraints.insets = new Insets(0, 11, 11, 11);
        add(massiveResultProcessPanel, gridBagConstraints);
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.gridy++;
        add(jPanel, gridBagConstraints);
        gridBagConstraints.gridy++;
        add(this.mainPluginsButtonsPanel, gridBagConstraints);
        refreshPluginButtonsContainer();
        this.returnButton.requestFocusInWindow();
    }

    void goToBack() {
        this.saf.loadMainApp();
    }

    private JPanel buildMainPluginsButtonsPanel() {
        JPanel jPanel = new JPanel(new GridBagLayout());
        this.pluginButtonsPanel = new JPanel(new FlowLayout(0));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridy = 0;
        jPanel.add(new JSeparator(0), gridBagConstraints);
        gridBagConstraints.gridy++;
        jPanel.add(this.pluginButtonsPanel, gridBagConstraints);
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST && Platform.getOS() == Platform.OS.WINDOWS) {
            jPanel.setBackground(LookAndFeelManager.SECUNDARY_COLOR);
            this.pluginButtonsPanel.setBackground(LookAndFeelManager.SECUNDARY_COLOR);
        }
        return jPanel;
    }

    @Override // es.gob.afirma.standalone.ui.PluginButtonsContainer
    public void refreshPluginButtonsContainer() {
        List<PluginGraphicButton> pluginsButtons = PluginsUiComponentsBuilder.getPluginsButtons(PluginIntegrationWindow.MULTI_RESULT);
        for (PluginGraphicButton pluginGraphicButton : pluginsButtons) {
            pluginGraphicButton.getGraphicButton().addActionListener(new PluginButtonActionListener(this, this.currentSigningCert, pluginGraphicButton.getButton().getAction()));
        }
        EventQueue.invokeLater(() -> {
            if (pluginsButtons.isEmpty()) {
                this.mainPluginsButtonsPanel.setVisible(false);
                return;
            }
            this.mainPluginsButtonsPanel.setVisible(false);
            this.pluginButtonsPanel.removeAll();
            Iterator it = pluginsButtons.iterator();
            while (it.hasNext()) {
                this.pluginButtonsPanel.add(((PluginGraphicButton) it.next()).getGraphicButton());
            }
            this.mainPluginsButtonsPanel.setVisible(true);
        });
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignResultListPanel$PluginButtonActionListener.class */
    class PluginButtonActionListener implements ActionListener {
        final SignResultListPanel signResultPanel;
        final X509Certificate cert;
        final PluginAction action;

        public PluginButtonActionListener(SignResultListPanel signResultListPanel, X509Certificate x509Certificate, PluginAction pluginAction) {
            this.signResultPanel = signResultListPanel;
            this.cert = x509Certificate;
            this.action = pluginAction;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            List<SignOperationConfig> list = this.signResultPanel.currentSignConfigs;
            ArrayList arrayList = new ArrayList();
            for (SignOperationConfig signOperationConfig : list) {
                OutputData outputData = new OutputData();
                outputData.setDataFile(signOperationConfig.getSignatureFile());
                outputData.setSignatureFormat(signOperationConfig.getSignatureFormatName());
                arrayList.add(outputData);
            }
            new Thread(() -> {
                if (this.action instanceof SignatureProcessAction) {
                    ((SignatureProcessAction) this.action).processSignatures((OutputData[]) arrayList.toArray(new OutputData[arrayList.size()]), this.cert, SwingUtilities.getWindowAncestor(SignResultListPanel.this));
                } else {
                    this.action.start(SwingUtilities.getWindowAncestor(SignResultListPanel.this));
                }
            }).start();
        }
    }
}
