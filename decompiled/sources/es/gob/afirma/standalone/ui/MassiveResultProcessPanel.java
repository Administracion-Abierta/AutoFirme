package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.VisorFirma;
import es.gob.afirma.standalone.crypto.CertAnalyzer;
import es.gob.afirma.standalone.crypto.CertificateInfo;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.security.cert.X509Certificate;
import java.text.NumberFormat;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/MassiveResultProcessPanel.class */
final class MassiveResultProcessPanel extends JPanel {
    private static final long serialVersionUID = -1721238760620367554L;
    private final JLabel certDescText = new JLabel();
    private final JLabel dirPathText = new JLabel();
    private final JLabel certIcon = new JLabel();
    private final JLabel holderDescCertLabel = new JLabel();
    private final JLabel holderCertLabel = new JLabel();
    private final JLabel issuerDescCertLabel = new JLabel();
    private final JLabel issuerCertLabel = new JLabel();
    private final JButton validateCertButton = null;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    MassiveResultProcessPanel(List<SignOperationConfig> list, File file, X509Certificate x509Certificate) {
        SwingUtilities.invokeLater(() -> {
            createUI(list, file, x509Certificate);
        });
    }

    void createUI(List<SignOperationConfig> list, File file, X509Certificate x509Certificate) {
        final JLabel jLabel = new JLabel();
        jLabel.setBorder(BorderFactory.createEmptyBorder());
        jLabel.setFocusable(false);
        jLabel.setText(file.getAbsolutePath());
        jLabel.addMouseListener(new MouseAdapter() { // from class: es.gob.afirma.standalone.ui.MassiveResultProcessPanel.1
            public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == 3 && mouseEvent.getClickCount() == 1) {
                    new CopyMenuItem(jLabel, SimpleAfirmaMessages.getString("MassiveResultProcessPanel.2")).show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                }
            }
        });
        this.dirPathText.setText(SimpleAfirmaMessages.getString("MassiveResultProcessPanel.3"));
        this.dirPathText.setLabelFor(jLabel);
        Color color = LookAndFeelManager.WINDOWS_HIGH_CONTRAST ? Color.WHITE : Color.GRAY;
        Component jPanel = new JPanel();
        jPanel.setBorder(BorderFactory.createLineBorder(color));
        jPanel.setLayout(new BoxLayout(jPanel, 0));
        jPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        jPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        jPanel.setFocusable(true);
        jPanel.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("MassiveResultProcessPanel.3") + file.getAbsolutePath());
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            jPanel.setBackground(LookAndFeelManager.SECUNDARY_COLOR);
        }
        JButton jButton = new JButton(SimpleAfirmaMessages.getString("MassiveResultProcessPanel.4"));
        jButton.setPreferredSize(new Dimension(150, 24));
        jButton.setMnemonic('b');
        jButton.setToolTipText(SimpleAfirmaMessages.getString("MassiveResultProcessPanel.5"));
        jButton.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("MassiveResultProcessPanel.6"));
        jButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("MassiveResultProcessPanel.7"));
        jButton.addActionListener(actionEvent -> {
            try {
                Desktop.getDesktop().open(file);
            } catch (Exception e) {
                LOGGER.warning("Error abriendo el directorio con las firmas generadas: " + e);
                AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("MassiveResultProcessPanel.8"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, null);
            }
        });
        jPanel.add(Box.createRigidArea(new Dimension(11, 0)));
        jPanel.add(jLabel);
        jPanel.add(Box.createHorizontalGlue());
        jPanel.add(Box.createRigidArea(new Dimension(11, 0)));
        jPanel.add(jButton);
        jPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        Component jPanel2 = null;
        if (x509Certificate != null) {
            CertificateInfo certInformation = CertAnalyzer.getCertInformation(x509Certificate);
            jPanel2 = new JPanel();
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
                jPanel2.setFocusable(true);
                jPanel2.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("SignDataPanel.21") + SimpleAfirmaMessages.getString("CertificateInfo.1") + certInformation.getHolderName() + SimpleAfirmaMessages.getString("CertificateInfo.2") + certInformation.getIssuerName());
            }
            jPanel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(color), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            jPanel2.setLayout(new GridBagLayout());
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = 1;
            gridBagConstraints.weightx = 0.0d;
            gridBagConstraints.weighty = 0.0d;
            gridBagConstraints.gridheight = 2;
            gridBagConstraints.insets = new Insets(0, 0, 0, 10);
            jPanel2.add(this.certIcon, gridBagConstraints);
            gridBagConstraints.weightx = 0.0d;
            gridBagConstraints.weighty = 0.0d;
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridheight = 1;
            jPanel2.add(this.holderDescCertLabel, gridBagConstraints);
            gridBagConstraints.weightx = 5.0d;
            gridBagConstraints.gridx = 2;
            gridBagConstraints.insets = new Insets(0, 0, 0, 0);
            jPanel2.add(this.holderCertLabel, gridBagConstraints);
            gridBagConstraints.weightx = 0.0d;
            gridBagConstraints.weighty = 1.0d;
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.insets = new Insets(0, 0, 0, 0);
            jPanel2.add(this.issuerDescCertLabel, gridBagConstraints);
            gridBagConstraints.weightx = 5.0d;
            gridBagConstraints.gridx = 2;
            jPanel2.add(this.issuerCertLabel, gridBagConstraints);
            if (this.validateCertButton != null) {
                jPanel2.add(this.validateCertButton);
                jPanel2.add(Box.createRigidArea(new Dimension(5, 0)));
            }
            if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                jPanel2.setBackground(LookAndFeelManager.SECUNDARY_COLOR);
            }
            this.certDescText.setText(SimpleAfirmaMessages.getString("SignDataPanel.21"));
            this.certDescText.setLabelFor(this.holderCertLabel);
        }
        SignatureResultTitleRenderer signatureResultTitleRenderer = new SignatureResultTitleRenderer();
        signatureResultTitleRenderer.setFileNameColumnTitle(SimpleAfirmaMessages.getString("MassiveResultProcessPanel.9"));
        signatureResultTitleRenderer.setSizeColumnTitle(SimpleAfirmaMessages.getString("MassiveResultProcessPanel.10"));
        signatureResultTitleRenderer.setResultColumnTitle(SimpleAfirmaMessages.getString("MassiveResultProcessPanel.11"));
        signatureResultTitleRenderer.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, color));
        JScrollPane jScrollPane = new JScrollPane(getSignResultList(list, this));
        jScrollPane.setBorder(BorderFactory.createEmptyBorder());
        String string = SimpleAfirmaMessages.getString("MassiveResultProcessPanel.12");
        for (SignOperationConfig signOperationConfig : list) {
            String str = string + SimpleAfirmaMessages.getString("MassiveResultProcessPanel.9") + signOperationConfig.getDataFile().getName() + SimpleAfirmaMessages.getString("MassiveResultProcessPanel.11");
            if (signOperationConfig.getSignatureFile() != null) {
                string = str + SimpleAfirmaMessages.getString("MassiveResultProcessPanel.13") + SimpleAfirmaMessages.getString("MassiveResultProcessPanel.10") + (signOperationConfig.getSignatureFile().length() / 1024) + "KB";
            } else {
                string = str + SimpleAfirmaMessages.getString("MassiveResultProcessPanel.14");
            }
        }
        jScrollPane.setFocusable(true);
        jScrollPane.getAccessibleContext().setAccessibleDescription(string);
        if (Platform.OS.MACOSX.equals(Platform.getOS())) {
            jScrollPane.setVerticalScrollBarPolicy(22);
            jScrollPane.setHorizontalScrollBarPolicy(32);
        } else {
            jScrollPane.setHorizontalScrollBarPolicy(30);
        }
        Component jPanel3 = new JPanel(new GridBagLayout());
        jPanel3.setBorder(BorderFactory.createLineBorder(color));
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = 1;
        gridBagConstraints2.weightx = 1.0d;
        gridBagConstraints2.gridy = 0;
        jPanel3.add(signatureResultTitleRenderer, gridBagConstraints2);
        gridBagConstraints2.weighty = 1.0d;
        gridBagConstraints2.gridy++;
        jPanel3.add(jScrollPane, gridBagConstraints2);
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST && Platform.getOS() == Platform.OS.WINDOWS) {
            setBackground(LookAndFeelManager.SECUNDARY_COLOR);
        }
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = 2;
        gridBagConstraints3.weightx = 1.0d;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.insets = new Insets(11, 0, 0, 0);
        add(this.dirPathText, gridBagConstraints3);
        gridBagConstraints3.gridy++;
        gridBagConstraints3.insets = new Insets(0, 0, 0, 0);
        add(jPanel, gridBagConstraints3);
        gridBagConstraints3.gridy++;
        gridBagConstraints3.insets = new Insets(11, 0, 0, 0);
        if (jPanel2 != null) {
            add(this.certDescText, gridBagConstraints3);
            gridBagConstraints3.gridy++;
            gridBagConstraints3.insets = new Insets(0, 0, 0, 0);
            add(jPanel2, gridBagConstraints3);
            gridBagConstraints3.gridy++;
            gridBagConstraints3.insets = new Insets(11, 0, 0, 0);
        }
        gridBagConstraints3.fill = 1;
        gridBagConstraints3.weighty = 1.0d;
        gridBagConstraints3.gridy++;
        gridBagConstraints3.insets = new Insets(11, 0, 0, 0);
        add(jPanel3, gridBagConstraints3);
    }

    private static Component getSignResultList(List<SignOperationConfig> list, final Component component) {
        final JList jList = new JList(list.toArray(new SignOperationConfig[list.size()]));
        jList.setCellRenderer(new SignatureResultCellRenderer());
        jList.addMouseListener(new MouseListener() { // from class: es.gob.afirma.standalone.ui.MassiveResultProcessPanel.2
            public void mouseReleased(MouseEvent mouseEvent) {
            }

            public void mousePressed(MouseEvent mouseEvent) {
            }

            public void mouseExited(MouseEvent mouseEvent) {
            }

            public void mouseEntered(MouseEvent mouseEvent) {
            }

            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2 && (mouseEvent.getSource() instanceof JList)) {
                    JList jList2 = (JList) mouseEvent.getSource();
                    MassiveResultProcessPanel.openSignatureItem(jList2, jList2.locationToIndex(mouseEvent.getPoint()), component);
                }
            }
        });
        jList.addKeyListener(new KeyListener() { // from class: es.gob.afirma.standalone.ui.MassiveResultProcessPanel.3
            public void keyTyped(KeyEvent keyEvent) {
            }

            public void keyPressed(KeyEvent keyEvent) {
            }

            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 10) {
                    MassiveResultProcessPanel.openSignatureItem((JList) keyEvent.getSource(), jList.getSelectedIndex(), component);
                }
            }
        });
        return jList;
    }

    static void openSignatureItem(JList<SignOperationConfig> jList, int i, Component component) {
        if (i >= 0) {
            SignOperationConfig signOperationConfig = (SignOperationConfig) jList.getModel().getElementAt(i);
            if (signOperationConfig.getSignatureFile() != null) {
                SwingUtilities.invokeLater(() -> {
                    new VisorFirma(false, component).initialize(false, signOperationConfig.getSignatureFile());
                });
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/MassiveResultProcessPanel$SignatureResultTitleRenderer.class */
    private static class SignatureResultTitleRenderer extends JPanel {
        private static final long serialVersionUID = 6994842579055905859L;
        private final JLabel fileNameLabel = new JLabel();
        private final JLabel sizeLabel = new JLabel();
        private final JLabel resultLabel;

        public SignatureResultTitleRenderer() {
            this.sizeLabel.setPreferredSize(new Dimension(60, 14));
            this.resultLabel = new JLabel();
            this.resultLabel.setPreferredSize(new Dimension(52, 14));
            if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                setBackground(LookAndFeelManager.SECUNDARY_COLOR);
            }
            setLayout(new GridBagLayout());
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = 2;
            gridBagConstraints.insets = new Insets(3, 11, 3, 0);
            gridBagConstraints.gridx = 0;
            gridBagConstraints.weightx = 1.0d;
            add(this.fileNameLabel, gridBagConstraints);
            gridBagConstraints.weightx = 0.0d;
            gridBagConstraints.gridx++;
            add(this.sizeLabel, gridBagConstraints);
            gridBagConstraints.gridx++;
            gridBagConstraints.insets = new Insets(0, 5, 0, 5);
            add(this.resultLabel, gridBagConstraints);
        }

        void setFileNameColumnTitle(String str) {
            this.fileNameLabel.setText(str);
        }

        void setSizeColumnTitle(String str) {
            this.sizeLabel.setText(str);
        }

        void setResultColumnTitle(String str) {
            this.resultLabel.setText(str);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/MassiveResultProcessPanel$SignatureResultCellRenderer.class */
    private static class SignatureResultCellRenderer extends JPanel implements ListCellRenderer<SignOperationConfig> {
        private static final long serialVersionUID = -8817021555833690354L;
        private final JLabel fileNameLabel;
        private final JLabel sizeLabel;
        private final JLabel resultIcon;
        private final NumberFormat formatter;
        private final Border focusedBorder;
        private final Border unfocusedBorder;
        private ImageIcon okIcon = null;
        private ImageIcon koIcon = null;
        private int basePathLength = 0;
        private final Dimension iconDimension = new Dimension(32, 32);
        private final JLabel iconLabel = new JLabel();

        public /* bridge */ /* synthetic */ Component getListCellRendererComponent(JList jList, Object obj, int i, boolean z, boolean z2) {
            return getListCellRendererComponent((JList<? extends SignOperationConfig>) jList, (SignOperationConfig) obj, i, z, z2);
        }

        public SignatureResultCellRenderer() {
            this.iconLabel.setPreferredSize(this.iconDimension);
            this.fileNameLabel = new JLabel();
            this.sizeLabel = new JLabel();
            this.sizeLabel.setPreferredSize(new Dimension(60, 32));
            this.resultIcon = new JLabel();
            this.resultIcon.setPreferredSize(this.iconDimension);
            this.formatter = NumberFormat.getNumberInstance();
            this.focusedBorder = BorderFactory.createDashedBorder(LookAndFeelManager.WINDOWS_HIGH_CONTRAST ? Color.WHITE : Color.GRAY);
            this.unfocusedBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
            if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                setBackground(LookAndFeelManager.SECUNDARY_COLOR);
            }
            setLayout(new GridBagLayout());
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = 2;
            gridBagConstraints.insets = new Insets(3, 11, 3, 0);
            gridBagConstraints.gridx = 0;
            add(this.iconLabel, gridBagConstraints);
            gridBagConstraints.gridx++;
            gridBagConstraints.weightx = 1.0d;
            add(this.fileNameLabel, gridBagConstraints);
            gridBagConstraints.weightx = 0.0d;
            gridBagConstraints.gridx++;
            add(this.sizeLabel, gridBagConstraints);
            gridBagConstraints.gridx++;
            gridBagConstraints.insets = new Insets(0, 15, 0, 15);
            add(this.resultIcon, gridBagConstraints);
        }

        public Component getListCellRendererComponent(JList<? extends SignOperationConfig> jList, SignOperationConfig signOperationConfig, int i, boolean z, boolean z2) {
            if (this.basePathLength == 0) {
                this.basePathLength = calculateBasePathLength(jList.getModel());
            }
            ScalablePane icon = signOperationConfig.getFileType().getIcon();
            icon.setPreferredSize(this.iconDimension);
            this.iconLabel.setIcon(new ImageIcon(icon.getScaledInstanceToFit(icon.getMaster(), this.iconDimension)));
            if (signOperationConfig.getSignatureFile() != null) {
                this.fileNameLabel.setText(signOperationConfig.getSignatureFile().getAbsolutePath().substring(this.basePathLength));
                this.sizeLabel.setText(calculateSize(signOperationConfig.getSignatureFile().length()));
                this.resultIcon.setIcon(getResultIcon(true));
            } else {
                this.fileNameLabel.setText(signOperationConfig.getDataFile().getAbsolutePath());
                this.sizeLabel.setText(calculateSize(0L));
                this.resultIcon.setIcon(getResultIcon(false));
            }
            setBorder(z2 ? this.focusedBorder : this.unfocusedBorder);
            return this;
        }

        private static int calculateBasePathLength(ListModel<? extends SignOperationConfig> listModel) {
            int length;
            int i = Integer.MAX_VALUE;
            for (int i2 = 0; i2 < listModel.getSize(); i2++) {
                SignOperationConfig signOperationConfig = (SignOperationConfig) listModel.getElementAt(i2);
                if (signOperationConfig.getSignatureFile() != null && signOperationConfig.getSignatureFile().getParentFile() != null && (length = signOperationConfig.getSignatureFile().getParentFile().getAbsolutePath().length()) < i) {
                    i = length;
                }
            }
            if (i > 0) {
                i++;
            }
            return i;
        }

        private ImageIcon getResultIcon(boolean z) {
            ImageIcon imageIcon;
            if (z) {
                if (this.okIcon == null) {
                    this.okIcon = buildIcon(z);
                }
                imageIcon = this.okIcon;
            } else {
                if (this.koIcon == null) {
                    this.koIcon = buildIcon(z);
                }
                imageIcon = this.koIcon;
            }
            return imageIcon;
        }

        private static ImageIcon buildIcon(boolean z) {
            return ImageLoader.loadIcon(z ? "ok_icon_large.png" : "ko_icon_large.png", 1);
        }

        private String calculateSize(long j) {
            if (j == 0) {
                return "0 KB";
            }
            if (j < 1024) {
                return "1 KB";
            }
            return this.formatter.format(j / 1024) + " KB";
        }
    }
}
