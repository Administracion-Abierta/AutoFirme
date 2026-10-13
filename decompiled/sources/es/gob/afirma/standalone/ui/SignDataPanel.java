package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.AOSignerFactory;
import es.gob.afirma.core.signers.AOSimpleSignInfo;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.signers.pades.AOPDFSigner;
import es.gob.afirma.standalone.DataAnalizerUtil;
import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.crypto.CertAnalyzer;
import es.gob.afirma.standalone.crypto.CertificateInfo;
import es.gob.afirma.standalone.crypto.CompleteSignInfo;
import es.gob.afirma.standalone.crypto.TimestampsAnalyzer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignDataPanel.class */
final class SignDataPanel extends JPanel {
    private static final long serialVersionUID = 4956746943438652928L;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String FILE_ICON_PDF = "icon_pdf.png";
    private static final String FILE_ICON_SIGN = "icon_sign.png";
    private static final String FILE_ICON_FACTURAE = "icon_facturae.png";
    private static final String FILE_ICON_OOXML_WIN = "icon_office_win.png";
    private static final String FILE_ICON_ODF = "icon_openoffice.png";
    private final JLabel certDescText = new JLabel();
    private final JLabel filePathText = new JLabel();
    private final JLabel certIcon = new JLabel();
    private final JLabel holderDescCertLabel = new JLabel();
    private final JLabel holderCertLabel = new JLabel();
    private final JLabel issuerDescCertLabel = new JLabel();
    private final JLabel issuerCertLabel = new JLabel();
    private final JButton validateCertButton = null;
    private CompleteSignInfo currentSignInfo = null;

    SignDataPanel(File file, byte[] bArr, JComponent jComponent, X509Certificate x509Certificate, KeyListener keyListener, Properties properties) {
        SwingUtilities.invokeLater(() -> {
            createUI(file, bArr, jComponent, x509Certificate, keyListener, properties);
        });
    }

    void createUI(File file, byte[] bArr, JComponent jComponent, X509Certificate x509Certificate, KeyListener keyListener, Properties properties) {
        String str;
        String string;
        CompleteSignInfo signInfo;
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST && Platform.getOS() == Platform.OS.WINDOWS) {
            setBackground(LookAndFeelManager.DEFAULT_COLOR);
        }
        final JLabel jLabel = new JLabel();
        jLabel.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("SignDataPanel.0"));
        jLabel.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("SignDataPanel.1"));
        jLabel.setBorder(BorderFactory.createEmptyBorder());
        jLabel.setFocusable(false);
        jLabel.setText(file == null ? SimpleAfirmaMessages.getString("SignDataPanel.24") : file.getAbsolutePath());
        jLabel.addMouseListener(new MouseAdapter() { // from class: es.gob.afirma.standalone.ui.SignDataPanel.1
            public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == 3 && mouseEvent.getClickCount() == 1) {
                    new CopyMenuItem(jLabel, SimpleAfirmaMessages.getString("SignDataPanel.30")).show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                }
            }
        });
        this.filePathText.setText(SimpleAfirmaMessages.getString("SignDataPanel.2"));
        this.filePathText.setLabelFor(jLabel);
        Color color = LookAndFeelManager.WINDOWS_HIGH_CONTRAST ? Color.WHITE : Color.GRAY;
        Component jPanel = new JPanel();
        jPanel.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("SignDataPanel.2") + jLabel.getText());
        jPanel.setFocusable(true);
        jPanel.setBorder(BorderFactory.createLineBorder(color));
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        boolean z = false;
        if (jComponent != null) {
            jPanel.add(jComponent, gridBagConstraints);
        } else {
            if (DataAnalizerUtil.isPDF(bArr)) {
                z = true;
                str = FILE_ICON_PDF;
                string = SimpleAfirmaMessages.getString("SignDataPanel.9");
            } else if (DataAnalizerUtil.isFacturae(bArr)) {
                str = FILE_ICON_FACTURAE;
                string = SimpleAfirmaMessages.getString("SignDataPanel.10");
            } else if (DataAnalizerUtil.isOOXML(bArr)) {
                z = true;
                str = FILE_ICON_OOXML_WIN;
                string = SimpleAfirmaMessages.getString("SignDataPanel.40");
            } else if (DataAnalizerUtil.isODF(bArr)) {
                z = true;
                str = FILE_ICON_ODF;
                string = SimpleAfirmaMessages.getString("SignPanel.41");
            } else {
                str = FILE_ICON_SIGN;
                string = SimpleAfirmaMessages.getString("SignDataPanel.31");
            }
            JLabel jLabel2 = new JLabel(new ImageIcon(ImageLoader.loadImage(str)));
            jLabel2.setToolTipText(string);
            jLabel2.setFocusable(false);
            jPanel.add(jLabel2, gridBagConstraints);
        }
        JButton jButton = null;
        if (z && file != null) {
            jButton = new JButton(SimpleAfirmaMessages.getString("SignDataPanel.3"));
            jButton.setPreferredSize(new Dimension(150, 24));
            jButton.setMnemonic('c');
            jButton.setToolTipText(SimpleAfirmaMessages.getString("SignDataPanel.4"));
            jButton.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("SignDataPanel.5"));
            jButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("SignDataPanel.6"));
            jButton.addActionListener(actionEvent -> {
                try {
                    Desktop.getDesktop().open(file);
                } catch (Exception e) {
                    LOGGER.warning("Error abriendo el fichero con el visor por defecto: " + e);
                    AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SignDataPanel.7"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e);
                }
            });
        }
        gridBagConstraints.weightx = 1.0d;
        jPanel.add(jLabel, gridBagConstraints);
        if (jButton != null) {
            gridBagConstraints.weightx = 0.0d;
            gridBagConstraints.ipady = 4;
            jPanel.add(jButton, gridBagConstraints);
        }
        Component jPanel2 = null;
        if (x509Certificate != null) {
            CertificateInfo certInformation = CertAnalyzer.getCertInformation(x509Certificate);
            if (certInformation != null) {
                this.certIcon.setIcon(certInformation.getIcon());
                this.certIcon.setToolTipText(certInformation.getIconTooltip());
                this.certIcon.setFocusable(false);
                this.holderDescCertLabel.setText(SimpleAfirmaMessages.getString("CertificateInfo.1"));
                this.holderCertLabel.setText(certInformation.getHolderName());
                this.holderCertLabel.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("SignDataPanel.46") + SimpleAfirmaMessages.getString("CertificateInfo.1"));
                this.issuerDescCertLabel.setText(SimpleAfirmaMessages.getString("CertificateInfo.2"));
                this.issuerCertLabel.setText("<html><b>" + certInformation.getIssuerName() + "</b></html>");
                new LabelLinkManager(this.holderCertLabel).setLabelLinkListener(new CertInfoLabelLinkImpl(x509Certificate));
            }
            jPanel2 = new JPanel();
            jPanel2.setFocusable(true);
            jPanel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(color), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            jPanel2.setLayout(new GridBagLayout());
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = 1;
            gridBagConstraints2.weightx = 0.0d;
            gridBagConstraints2.weighty = 0.0d;
            gridBagConstraints2.gridheight = 2;
            gridBagConstraints2.insets = new Insets(0, 0, 0, 10);
            jPanel2.add(this.certIcon, gridBagConstraints2);
            gridBagConstraints2.weightx = 0.0d;
            gridBagConstraints2.weighty = 0.0d;
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridheight = 1;
            jPanel2.add(this.holderDescCertLabel, gridBagConstraints2);
            gridBagConstraints2.weightx = 5.0d;
            gridBagConstraints2.gridx = 2;
            gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
            jPanel2.add(this.holderCertLabel, gridBagConstraints2);
            gridBagConstraints2.weightx = 0.0d;
            gridBagConstraints2.weighty = 1.0d;
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = 1;
            gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
            jPanel2.add(this.issuerDescCertLabel, gridBagConstraints2);
            gridBagConstraints2.weightx = 5.0d;
            gridBagConstraints2.gridx = 2;
            jPanel2.add(this.issuerCertLabel, gridBagConstraints2);
            if (this.validateCertButton != null) {
                jPanel2.add(this.validateCertButton);
                jPanel2.add(Box.createRigidArea(new Dimension(5, 0)));
            }
            if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                jPanel2.setBackground(LookAndFeelManager.SECUNDARY_COLOR);
            }
            this.certDescText.setText(SimpleAfirmaMessages.getString("SignDataPanel.21"));
            this.certDescText.setLabelFor(this.holderCertLabel);
            jPanel2.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("SignDataPanel.21") + this.holderDescCertLabel.getText() + this.holderCertLabel.getText() + this.issuerDescCertLabel.getText() + this.issuerCertLabel.getText());
        }
        try {
            signInfo = getSignInfo(bArr, properties);
        } catch (Exception e) {
            LOGGER.severe("Error obteniendo los datos de la firma: " + e);
            signInfo = null;
        }
        this.currentSignInfo = signInfo;
        JTree signDataTree = null;
        String str2 = "";
        if (signInfo != null) {
            signDataTree = getSignDataTree(signInfo, keyListener, this);
            TreeNode treeNode = (TreeNode) signDataTree.getModel().getRoot();
            str2 = str2 + treeNode;
            if (treeNode.getChildCount() >= 0) {
                Enumeration enumerationChildren = treeNode.children();
                while (enumerationChildren.hasMoreElements()) {
                    str2 = str2 + ((TreeNode) enumerationChildren.nextElement()).toString();
                }
            }
        }
        Component jScrollPane = new JScrollPane(signDataTree);
        jScrollPane.setBorder(BorderFactory.createLineBorder(color));
        jScrollPane.setFocusable(true);
        if (Platform.OS.MACOSX.equals(Platform.getOS())) {
            jScrollPane.setVerticalScrollBarPolicy(22);
            jScrollPane.setHorizontalScrollBarPolicy(32);
        } else {
            jScrollPane.setHorizontalScrollBarPolicy(30);
        }
        jScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        jScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        Component jLabel3 = new JLabel(SimpleAfirmaMessages.getString("SignDataPanel.22"));
        jLabel3.setLabelFor(jScrollPane);
        jScrollPane.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("SignDataPanel.22") + str2);
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            jPanel.setBackground(LookAndFeelManager.SECUNDARY_COLOR);
        }
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = 2;
        gridBagConstraints3.weightx = 1.0d;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.insets = new Insets(11, 0, 0, 0);
        add(this.filePathText, gridBagConstraints3);
        gridBagConstraints3.gridy = 1;
        gridBagConstraints3.insets = new Insets(0, 0, 0, 0);
        add(jPanel, gridBagConstraints3);
        gridBagConstraints3.gridy = 2;
        gridBagConstraints3.insets = new Insets(11, 0, 0, 0);
        if (jPanel2 != null) {
            add(this.certDescText, gridBagConstraints3);
            gridBagConstraints3.gridy = 3;
            gridBagConstraints3.insets = new Insets(0, 0, 0, 0);
            add(jPanel2, gridBagConstraints3);
            gridBagConstraints3.gridy = 4;
            gridBagConstraints3.insets = new Insets(11, 0, 0, 0);
        }
        add(jLabel3, gridBagConstraints3);
        gridBagConstraints3.fill = 1;
        gridBagConstraints3.weighty = 1.0d;
        gridBagConstraints3.gridy = 5;
        gridBagConstraints3.insets = new Insets(0, 0, 0, 0);
        add(jScrollPane, gridBagConstraints3);
    }

    public static void openCertificate(X509Certificate x509Certificate) {
        try {
            File fileCreateTempFile = File.createTempFile("afirma", ".cer");
            fileCreateTempFile.deleteOnExit();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileCreateTempFile));
            Throwable th = null;
            try {
                try {
                    bufferedOutputStream.write(x509Certificate.getEncoded());
                    if (bufferedOutputStream != null) {
                        if (0 != 0) {
                            try {
                                bufferedOutputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            bufferedOutputStream.close();
                        }
                    }
                    Desktop.getDesktop().open(fileCreateTempFile);
                } catch (Throwable th3) {
                    if (bufferedOutputStream != null) {
                        if (th != null) {
                            try {
                                bufferedOutputStream.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            bufferedOutputStream.close();
                        }
                    }
                    throw th3;
                }
            } catch (Throwable th5) {
                th = th5;
                throw th5;
            }
        } catch (Exception e) {
            LOGGER.warning("Error abriendo el fichero con el visor por defecto: " + e);
            AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SignDataPanel.23"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e);
        }
    }

    private static CompleteSignInfo getSignInfo(byte[] bArr, Properties properties) throws IOException {
        CompleteSignInfo completeSignInfo = new CompleteSignInfo();
        completeSignInfo.setSignData(bArr);
        AOSigner signer = AOSignerFactory.getSigner(bArr, properties);
        if (signer == null) {
            LOGGER.warning("Formato de firma no reconocido");
            throw new IllegalArgumentException("Formato de firma no reconocido");
        }
        try {
            if (signer instanceof AOPDFSigner) {
                completeSignInfo.setSignInfo(((AOPDFSigner) signer).getSignInfo(bArr, properties));
            } else {
                completeSignInfo.setSignInfo(signer.getSignInfo(bArr));
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al leer la informacion de la firma", (Throwable) e);
        }
        try {
            if (signer instanceof AOPDFSigner) {
                completeSignInfo.setSignsTree(((AOPDFSigner) signer).getSignersStructure(bArr, properties, true));
            } else {
                completeSignInfo.setSignsTree(signer.getSignersStructure(bArr, true));
            }
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "Error al extraer el arbol de firmantes", (Throwable) e2);
            completeSignInfo.setSignsTree(null);
        }
        try {
            if (signer instanceof AOPDFSigner) {
                completeSignInfo.setData(((AOPDFSigner) signer).getData(bArr, properties));
            } else {
                completeSignInfo.setData(signer.getData(bArr));
            }
        } catch (Exception e3) {
            LOGGER.log(Level.WARNING, "Error al extraer los datos firmados", (Throwable) e3);
        }
        try {
            if (signer instanceof AOPDFSigner) {
                completeSignInfo.setTimestampsInfo(TimestampsAnalyzer.getTimestamps(bArr, properties));
            } else {
                completeSignInfo.setTimestampsInfo(TimestampsAnalyzer.getTimestamps(bArr));
            }
        } catch (Exception e4) {
            LOGGER.log(Level.WARNING, "Error al extraer los sellos de tiempo", (Throwable) e4);
        }
        return completeSignInfo;
    }

    private static JTree getSignDataTree(CompleteSignInfo completeSignInfo, KeyListener keyListener, Component component) {
        DefaultMutableTreeNode defaultMutableTreeNode;
        DefaultMutableTreeNode defaultMutableTreeNode2 = new DefaultMutableTreeNode();
        DefaultMutableTreeNode defaultMutableTreeNode3 = new DefaultMutableTreeNode(SimpleAfirmaMessages.getString("SignDataPanel.25"));
        defaultMutableTreeNode3.add(new DefaultMutableTreeNode(completeSignInfo.getSignInfo().getFormat()));
        defaultMutableTreeNode2.add(defaultMutableTreeNode3);
        DefaultMutableTreeNode defaultMutableTreeNode4 = new DefaultMutableTreeNode(SimpleAfirmaMessages.getString("SignDataPanel.26"));
        if (completeSignInfo.getData() == null) {
            defaultMutableTreeNode4.add(new DefaultMutableTreeNode(SimpleAfirmaMessages.getString("SignDataPanel.27")));
        } else {
            defaultMutableTreeNode4.add(new DefaultMutableTreeNode(new ShowFileLinkAction(SimpleAfirmaMessages.getString("SignDataPanel.28"), completeSignInfo.getData(), component)));
        }
        defaultMutableTreeNode2.add(defaultMutableTreeNode4);
        AOTreeModel signsTree = completeSignInfo.getSignsTree();
        if (signsTree != null) {
            defaultMutableTreeNode = new TreeModelManager(signsTree).getSwingTree();
            defaultMutableTreeNode.setUserObject(SimpleAfirmaMessages.getString("SignDataPanel.29"));
            defaultMutableTreeNode2.add(defaultMutableTreeNode);
        } else {
            defaultMutableTreeNode = new DefaultMutableTreeNode(SimpleAfirmaMessages.getString("SignDataPanel.29"));
            defaultMutableTreeNode.add(new DefaultMutableTreeNode(SimpleAfirmaMessages.getString("SignDataPanel.32")));
        }
        defaultMutableTreeNode2.add(defaultMutableTreeNode);
        if (completeSignInfo.getTimestampsInfo() != null && !completeSignInfo.getTimestampsInfo().isEmpty()) {
            DefaultMutableTreeNode swingTree = new TreeModelManager(completeSignInfo.getTimestampsTree()).getSwingTree();
            swingTree.setUserObject(SimpleAfirmaMessages.getString("SignDataPanel.42"));
            defaultMutableTreeNode2.add(swingTree);
        }
        LinksTreeCellRenderer linksTreeCellRenderer = new LinksTreeCellRenderer();
        linksTreeCellRenderer.setLeafIcon(null);
        linksTreeCellRenderer.setClosedIcon((Platform.OS.WINDOWS.equals(Platform.getOS()) || Platform.OS.MACOSX.equals(Platform.getOS())) ? null : UIManager.getDefaults().getIcon("TREE.collapsedIcon"));
        linksTreeCellRenderer.setOpenIcon((Platform.OS.WINDOWS.equals(Platform.getOS()) || Platform.OS.MACOSX.equals(Platform.getOS())) ? null : UIManager.getDefaults().getIcon("TREE.expandedIcon"));
        JTree jTree = new JTree(defaultMutableTreeNode2);
        jTree.setShowsRootHandles(true);
        jTree.setCellRenderer(linksTreeCellRenderer);
        jTree.setRootVisible(false);
        jTree.putClientProperty("JTree.lineStyle", "None");
        jTree.getSelectionModel().setSelectionMode(1);
        TreeFocusManager treeFocusManager = new TreeFocusManager(jTree, obj -> {
            if (obj instanceof AOSimpleSignInfo) {
                openCertificate(((AOSimpleSignInfo) obj).getCerts()[0]);
            } else if (obj instanceof ShowFileLinkAction) {
                ((ShowFileLinkAction) obj).action();
            }
        });
        jTree.addMouseMotionListener(treeFocusManager);
        jTree.addFocusListener(treeFocusManager);
        jTree.addMouseListener(treeFocusManager);
        jTree.addKeyListener(treeFocusManager);
        if (keyListener != null) {
            jTree.addKeyListener(keyListener);
        }
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }
        return jTree;
    }

    public CompleteSignInfo getCurrentSignInfo() {
        return this.currentSignInfo;
    }
}
