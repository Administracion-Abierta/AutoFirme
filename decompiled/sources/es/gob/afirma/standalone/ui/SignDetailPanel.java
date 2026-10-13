package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.signers.AOSimpleSignInfo;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.core.util.tree.AOTreeNode;
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
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignDetailPanel.class */
public final class SignDetailPanel extends JPanel implements PluginButtonsContainer {
    private static final long serialVersionUID = 7567869419737753210L;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final int DEFAULT_WINDOW_WIDTH = 600;
    private static final int DEFAULT_WINDOW_HEIGHT = 500;
    private final SimpleAfirma saf;
    private final JButton returnButton = new JButton();
    JPanel mainPluginsButtonsPanel = null;
    JPanel pluginButtonsPanel = null;
    SignDataPanel componentPanel;
    SignOperationConfig signConfig;
    private X509Certificate signingCert;

    public SignDetailPanel(SimpleAfirma simpleAfirma, byte[] bArr, SignOperationConfig signOperationConfig, X509Certificate x509Certificate, List<SignValidity> list, JComponent jComponent) {
        this.signConfig = null;
        this.signingCert = null;
        this.saf = simpleAfirma;
        this.signConfig = signOperationConfig;
        this.signingCert = x509Certificate;
        createUI(bArr, signOperationConfig, x509Certificate, list, jComponent);
    }

    /* JADX WARN: Not initialized variable reg: 17, insn: 0x0109: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r17 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:54:0x0109 */
    /* JADX WARN: Not initialized variable reg: 18, insn: 0x010e: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r18 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:56:0x010e */
    private void createUI(byte[] bArr, SignOperationConfig signOperationConfig, X509Certificate x509Certificate, List<SignValidity> list, JComponent jComponent) {
        InputStream inputStream;
        Throwable th;
        setPreferredSize(new Dimension(600, 500));
        byte[] dataFromInputStream = bArr != null ? (byte[]) bArr.clone() : null;
        if (dataFromInputStream == null && signOperationConfig != null && signOperationConfig.getSignatureFile() != null) {
            if (!signOperationConfig.getSignatureFile().exists()) {
                LOGGER.severe("La ruta de firma proporcionada no corresponde a ningun fichero");
            } else if (signOperationConfig.getSignatureFile().canRead()) {
                try {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(signOperationConfig.getSignatureFile());
                        Throwable th2 = null;
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                        Throwable th3 = null;
                        try {
                            dataFromInputStream = AOUtil.getDataFromInputStream(bufferedInputStream);
                            if (bufferedInputStream != null) {
                                if (0 != 0) {
                                    try {
                                        bufferedInputStream.close();
                                    } catch (Throwable th4) {
                                        th3.addSuppressed(th4);
                                    }
                                } else {
                                    bufferedInputStream.close();
                                }
                            }
                            if (fileInputStream != null) {
                                if (0 != 0) {
                                    try {
                                        fileInputStream.close();
                                    } catch (Throwable th5) {
                                        th2.addSuppressed(th5);
                                    }
                                } else {
                                    fileInputStream.close();
                                }
                            }
                        } catch (Throwable th6) {
                            if (bufferedInputStream != null) {
                                if (0 != 0) {
                                    try {
                                        bufferedInputStream.close();
                                    } catch (Throwable th7) {
                                        th3.addSuppressed(th7);
                                    }
                                } else {
                                    bufferedInputStream.close();
                                }
                            }
                            throw th6;
                        }
                    } catch (Throwable th8) {
                        if (inputStream != null) {
                            if (th != null) {
                                try {
                                    inputStream.close();
                                } catch (Throwable th9) {
                                    th.addSuppressed(th9);
                                }
                            } else {
                                inputStream.close();
                            }
                        }
                        throw th8;
                    }
                } catch (IOException e) {
                    LOGGER.severe("No se ha podido leer el fichero de firma: " + e);
                }
            } else {
                LOGGER.severe("No se tienen permisos de lectura del fichero indicado");
            }
        }
        Component signResultPanel = new SignResultPanel(list, true, null, dataFromInputStream);
        this.componentPanel = new SignDataPanel(signOperationConfig.getSignatureFile(), dataFromInputStream, jComponent, x509Certificate, null, signOperationConfig.getExtraParams());
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
            setBackground(LookAndFeelManager.DEFAULT_COLOR);
            jPanel.setBackground(LookAndFeelManager.DEFAULT_COLOR);
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
        add(this.componentPanel, gridBagConstraints);
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
            jPanel.setBackground(LookAndFeelManager.DEFAULT_COLOR);
            this.pluginButtonsPanel.setBackground(LookAndFeelManager.DEFAULT_COLOR);
        }
        return jPanel;
    }

    @Override // es.gob.afirma.standalone.ui.PluginButtonsContainer
    public void refreshPluginButtonsContainer() {
        List<PluginGraphicButton> pluginsButtons = PluginsUiComponentsBuilder.getPluginsButtons(PluginIntegrationWindow.SINGLE_RESULT);
        for (PluginGraphicButton pluginGraphicButton : pluginsButtons) {
            pluginGraphicButton.getGraphicButton().addActionListener(new PluginButtonActionListener(this, this.signingCert, pluginGraphicButton.getButton().getAction()));
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

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignDetailPanel$PluginButtonActionListener.class */
    class PluginButtonActionListener implements ActionListener {
        final SignDetailPanel signDetailPanel;
        final X509Certificate cert;
        final PluginAction action;

        public PluginButtonActionListener(SignDetailPanel signDetailPanel, X509Certificate x509Certificate, PluginAction pluginAction) {
            this.signDetailPanel = signDetailPanel;
            this.cert = x509Certificate;
            this.action = pluginAction;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            OutputData outputData = new OutputData();
            outputData.setDataFile(this.signDetailPanel.signConfig.getSignatureFile());
            outputData.setSignatureFormat(this.signDetailPanel.signConfig.getSignatureFormatName());
            HashMap map = new HashMap();
            AOTreeModel signsTree = this.signDetailPanel.componentPanel.getCurrentSignInfo().getSignsTree();
            for (int i = 0; i < AOTreeModel.getChildCount(signsTree.getRoot()); i++) {
                readCertsFromBranch((AOTreeNode) AOTreeModel.getChild(signsTree.getRoot(), i), map);
            }
            outputData.setCerts((X509Certificate[]) map.values().toArray(new X509Certificate[map.size()]));
            new Thread(() -> {
                if (this.action instanceof SignatureProcessAction) {
                    ((SignatureProcessAction) this.action).processSignatures(new OutputData[]{outputData}, this.cert, SwingUtilities.getWindowAncestor(SignDetailPanel.this));
                } else {
                    this.action.start(SwingUtilities.getWindowAncestor(SignDetailPanel.this));
                }
            }).start();
        }

        private void readCertsFromBranch(AOTreeNode aOTreeNode, Map<BigInteger, X509Certificate> map) {
            AOSimpleSignInfo aOSimpleSignInfo = (AOSimpleSignInfo) aOTreeNode.getUserObject();
            if (aOSimpleSignInfo.getCerts() != null && aOSimpleSignInfo.getCerts().length > 0 && aOSimpleSignInfo.getCerts()[0] != null) {
                map.put(aOSimpleSignInfo.getCerts()[0].getSerialNumber(), aOSimpleSignInfo.getCerts()[0]);
            }
            for (int i = 0; i < AOTreeModel.getChildCount(aOTreeNode); i++) {
                readCertsFromBranch((AOTreeNode) AOTreeModel.getChild(aOTreeNode, i), map);
            }
        }
    }
}
