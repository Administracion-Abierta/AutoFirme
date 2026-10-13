package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.RuntimeConfigNeededException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.signers.AOSimpleSignInfo;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.core.util.tree.AOTreeNode;
import es.gob.afirma.signers.cades.AOCAdESSigner;
import es.gob.afirma.signers.cms.AOCMSSigner;
import es.gob.afirma.signers.odf.AOODFSigner;
import es.gob.afirma.signers.ooxml.AOOOXMLSigner;
import es.gob.afirma.signers.pades.AOPDFSigner;
import es.gob.afirma.signers.xades.AOFacturaESigner;
import es.gob.afirma.signers.xades.AOXAdESSigner;
import es.gob.afirma.signers.xmldsig.AOXMLDSigSigner;
import es.gob.afirma.signvalidation.SignValider;
import es.gob.afirma.signvalidation.SignValiderFactory;
import es.gob.afirma.signvalidation.SignValidity;
import es.gob.afirma.standalone.DataAnalizerUtil;
import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.VisorFirma;
import es.gob.afirma.standalone.crypto.CompleteSignInfo;
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/VisorPanel.class */
public final class VisorPanel extends JPanel implements KeyListener, PluginButtonsContainer {
    private static final long serialVersionUID = 8309157734617505338L;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final int DEFAULT_WINDOW_WIDTH = 600;
    private static final int DEFAULT_WINDOW_HEIGHT = 540;
    private final VisorFirma visorFirma;
    JPanel mainPluginsButtonsPanel;
    JPanel pluginButtonsPanel;
    SignDataPanel signDataPanel;
    File signatureFile;

    public VisorPanel(File file, byte[] bArr, VisorFirma visorFirma, boolean z) {
        super(true);
        this.mainPluginsButtonsPanel = null;
        this.pluginButtonsPanel = null;
        this.signDataPanel = null;
        this.signatureFile = null;
        this.visorFirma = visorFirma;
        this.signatureFile = file;
        createUI(file, bArr, z);
    }

    private void createUI(File file, byte[] bArr, boolean z) {
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST && Platform.getOS() == Platform.OS.WINDOWS) {
            setBackground(LookAndFeelManager.DEFAULT_COLOR);
        }
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setPreferredSize(new Dimension(600, 540));
        openSign(file, bArr, z);
    }

    private void openSign(File file, byte[] bArr, boolean z) {
        if (file == null && bArr == null) {
            LOGGER.warning("Se ha intentado abrir una firma nula");
            return;
        }
        byte[] dataFromInputStream = bArr != null ? (byte[]) bArr.clone() : null;
        if (dataFromInputStream == null && file != null) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Throwable th = null;
                try {
                    try {
                        dataFromInputStream = AOUtil.getDataFromInputStream(fileInputStream);
                        if (fileInputStream != null) {
                            if (0 != 0) {
                                try {
                                    fileInputStream.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                fileInputStream.close();
                            }
                        }
                    } catch (Throwable th3) {
                        if (fileInputStream != null) {
                            if (th != null) {
                                try {
                                    fileInputStream.close();
                                } catch (Throwable th4) {
                                    th.addSuppressed(th4);
                                }
                            } else {
                                fileInputStream.close();
                            }
                        }
                        throw th3;
                    }
                } catch (Throwable th5) {
                    th = th5;
                    throw th5;
                }
            } catch (Exception e) {
                LOGGER.warning("No se ha podido cargar el fichero de firma: " + e);
            }
        }
        List<SignValidity> arrayList = new ArrayList();
        if (dataFromInputStream != null) {
            try {
                arrayList = validateSign(dataFromInputStream);
            } catch (Exception e2) {
                LOGGER.log(Level.WARNING, "No se ha podido comprobar la validez de la firma: " + e2, (Throwable) e2);
                arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, null));
            }
        } else {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.UNKNOWN, null));
        }
        X509Certificate certificate = getCertificate(dataFromInputStream);
        Component signResultPanel = new SignResultPanel(arrayList, true, this, dataFromInputStream);
        this.signDataPanel = new SignDataPanel(file, dataFromInputStream, null, certificate, this, null);
        Component jPanel = new JPanel(true);
        jPanel.setLayout(new FlowLayout(4));
        if (z) {
            JButton jButton = new JButton(SimpleAfirmaMessages.getString("VisorPanel.1"));
            jButton.setMnemonic('V');
            jPanel.add(jButton);
            jButton.addKeyListener(this);
            jButton.addActionListener(actionEvent -> {
                if (getVisorFirma() != null) {
                    getVisorFirma().loadNewSign();
                }
            });
        }
        JButton jButton2 = new JButton(SimpleAfirmaMessages.getString("VisorPanel.0"));
        jButton2.setMnemonic('C');
        jButton2.addKeyListener(this);
        jButton2.addActionListener(actionEvent2 -> {
            if (getVisorFirma() != null) {
                getVisorFirma().closeApplication(0);
            }
        });
        jPanel.add(jButton2);
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST && Platform.getOS() == Platform.OS.WINDOWS) {
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
        add(this.signDataPanel, gridBagConstraints);
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.gridy++;
        add(jPanel, gridBagConstraints);
        gridBagConstraints.gridy++;
        add(this.mainPluginsButtonsPanel, gridBagConstraints);
        refreshPluginButtonsContainer();
        repaint();
    }

    VisorFirma getVisorFirma() {
        return this.visorFirma;
    }

    private static X509Certificate getCertificate(byte[] bArr) {
        AOTreeModel signersStructure;
        AOSimpleSignInfo aOSimpleSignInfo;
        X509Certificate[] certs;
        if (bArr == null) {
            return null;
        }
        try {
            if (new AOFacturaESigner().isSign(bArr)) {
                signersStructure = new AOFacturaESigner().getSignersStructure(bArr, true);
            } else if (new AOXAdESSigner().isSign(bArr)) {
                signersStructure = new AOXAdESSigner().getSignersStructure(bArr, true);
            } else if (new AOXMLDSigSigner().isSign(bArr)) {
                signersStructure = new AOXMLDSigSigner().getSignersStructure(bArr, true);
            } else if (new AOPDFSigner().isSign(bArr)) {
                signersStructure = new AOPDFSigner().getSignersStructure(bArr, true);
            } else if (new AOCAdESSigner().isSign(bArr)) {
                signersStructure = new AOCAdESSigner().getSignersStructure(bArr, true);
            } else if (new AOCMSSigner().isSign(bArr)) {
                signersStructure = new AOCMSSigner().getSignersStructure(bArr, true);
            } else if (new AOOOXMLSigner().isSign(bArr)) {
                signersStructure = new AOOOXMLSigner().getSignersStructure(bArr, true);
            } else if (new AOODFSigner().isSign(bArr)) {
                signersStructure = new AOODFSigner().getSignersStructure(bArr, true);
            } else {
                return null;
            }
            if (signersStructure == null || AOTreeModel.getChildCount(signersStructure.getRoot()) != 1 || (aOSimpleSignInfo = (AOSimpleSignInfo) ((AOTreeNode) AOTreeModel.getChild(signersStructure.getRoot(), 0)).getUserObject()) == null || (certs = aOSimpleSignInfo.getCerts()) == null || certs.length < 1) {
                return null;
            }
            return certs[0];
        } catch (Exception e) {
            LOGGER.warning("No se ha podido obtener el certificado de la firma: " + e);
            return null;
        }
    }

    public static List<SignValidity> validateSign(byte[] bArr) throws IOException {
        List<SignValidity> arrayList = new ArrayList();
        SignValider signValider = SignValiderFactory.getSignValider(bArr);
        if (signValider != null) {
            try {
                arrayList = signValider.validate(bArr);
            } catch (RuntimeConfigNeededException e) {
                throw new IOException("Error en la validacion de la firma", e);
            }
        } else if (DataAnalizerUtil.isSignedODF(bArr)) {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.UNKNOWN, SignValidity.VALIDITY_ERROR.ODF_UNKOWN_VALIDITY));
        } else if (DataAnalizerUtil.isSignedOOXML(bArr)) {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.UNKNOWN, SignValidity.VALIDITY_ERROR.OOXML_UNKOWN_VALIDITY));
        }
        if (arrayList.size() == 0) {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.UNKOWN_SIGNATURE_FORMAT));
        }
        return arrayList;
    }

    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 27 && this.visorFirma != null) {
            this.visorFirma.closeApplication(0);
        }
    }

    public void keyReleased(KeyEvent keyEvent) {
    }

    public void keyTyped(KeyEvent keyEvent) {
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
        List<PluginGraphicButton> pluginsButtons = PluginsUiComponentsBuilder.getPluginsButtons(PluginIntegrationWindow.VISOR);
        for (PluginGraphicButton pluginGraphicButton : pluginsButtons) {
            pluginGraphicButton.getGraphicButton().addActionListener(new PluginButtonActionListener(this.signDataPanel, this.signatureFile, pluginGraphicButton.getButton().getAction()));
        }
        EventQueue.invokeLater(() -> {
            if (pluginsButtons == null || pluginsButtons.isEmpty()) {
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

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/VisorPanel$PluginButtonActionListener.class */
    class PluginButtonActionListener implements ActionListener {
        final SignDataPanel panel;
        final File signFile;
        final PluginAction action;

        public PluginButtonActionListener(SignDataPanel signDataPanel, File file, PluginAction pluginAction) {
            this.panel = signDataPanel;
            this.signFile = file;
            this.action = pluginAction;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            CompleteSignInfo currentSignInfo = this.panel.getCurrentSignInfo();
            OutputData outputData = new OutputData();
            outputData.setDataFile(this.signFile);
            outputData.setSignatureFormat(currentSignInfo.getSignInfo().getFormat());
            HashMap map = new HashMap();
            AOTreeModel signsTree = currentSignInfo.getSignsTree();
            for (int i = 0; i < AOTreeModel.getChildCount(signsTree.getRoot()); i++) {
                readCertsFromBranch((AOTreeNode) AOTreeModel.getChild(signsTree.getRoot(), i), map);
            }
            outputData.setCerts((X509Certificate[]) map.values().toArray(new X509Certificate[map.size()]));
            new Thread(() -> {
                if (this.action instanceof SignatureProcessAction) {
                    ((SignatureProcessAction) this.action).processSignatures(new OutputData[]{outputData}, null, SwingUtilities.getWindowAncestor(VisorPanel.this));
                } else {
                    this.action.start(SwingUtilities.getWindowAncestor(VisorPanel.this));
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
