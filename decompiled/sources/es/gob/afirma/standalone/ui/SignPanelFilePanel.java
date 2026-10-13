package es.gob.afirma.standalone.ui;

import com.aowagie.text.pdf.ColumnText;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.VisorFirma;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignPanelFilePanel.class */
final class SignPanelFilePanel extends JPanel implements Scrollable {
    private static final long serialVersionUID = -8648491975442788750L;
    private SignatureConfigInfoPanel configInfoPanel;
    private String accesibleDescription;

    SignPanelFilePanel(SignOperationConfig signOperationConfig) {
        super(true);
        SwingUtilities.invokeLater(() -> {
            createUI(signOperationConfig);
        });
    }

    void createUI(SignOperationConfig signOperationConfig) {
        FileType fileType = signOperationConfig.getFileType();
        File dataFile = signOperationConfig.getDataFile();
        setLayout(new GridBagLayout());
        setAlignmentY(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        Color color = null;
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            color = Color.WHITE;
            setBackground(color);
        }
        setFocusable(true);
        JPanel jPanelCreateDetailsPanel = createDetailsPanel(signOperationConfig, color);
        Component jButton = null;
        String extension = getExtension(dataFile);
        if (extension == null || (!isExecutable(extension) && !isLink(extension))) {
            jButton = new JButton(SimpleAfirmaMessages.getString("SignPanel.51"));
            jButton.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("SignPanel.52"));
            this.accesibleDescription += SimpleAfirmaMessages.getString("SignPanel.52");
            jButton.setMnemonic('c');
            jButton.addActionListener(actionEvent -> {
                if (dataFile.getName().endsWith(".csig") || dataFile.getName().endsWith(".xsig")) {
                    new VisorFirma(false, null).initialize(false, dataFile);
                    return;
                }
                try {
                    Desktop.getDesktop().open(dataFile);
                } catch (IOException e) {
                    Logger.getLogger("es.gob.afirma").warning("Error abriendo el fichero: " + e);
                    AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SignPanel.53"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, null);
                }
            });
        }
        getAccessibleContext().setAccessibleDescription(this.accesibleDescription);
        Component jPanel = new JPanel();
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            jPanel.setBackground(color);
        }
        jPanel.setLayout(new BoxLayout(jPanel, 1));
        jPanel.setAlignmentX(1.0f);
        if (jButton != null) {
            jPanel.add(jButton);
        }
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 0.0d;
        gridBagConstraints.insets = new Insets(11, 0, 11, 11);
        gridBagConstraints.anchor = 18;
        Component icon = fileType.getIcon();
        if (icon != null) {
            icon.setMinimumSize(new Dimension(128, 128));
            add(icon, gridBagConstraints);
        }
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.ipadx = 0;
        gridBagConstraints.ipady = 0;
        gridBagConstraints.insets = new Insets(11, 0, 11, 5);
        gridBagConstraints.anchor = 11;
        add(jPanelCreateDetailsPanel, gridBagConstraints);
        gridBagConstraints.weightx = 0.0d;
        gridBagConstraints.gridx = 2;
        gridBagConstraints.ipadx = 0;
        gridBagConstraints.ipady = 0;
        gridBagConstraints.insets = new Insets(11, 6, 11, 11);
        gridBagConstraints.anchor = 12;
        add(jPanel, gridBagConstraints);
        gridBagConstraints.fill = 3;
        gridBagConstraints.anchor = 15;
        gridBagConstraints.weighty = 1.0d;
        Component jPanel2 = new JPanel();
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            jPanel2.setBackground(color);
        }
        add(jPanel2, gridBagConstraints);
    }

    private static boolean isExecutable(String str) {
        return DataFileAnalizer.isExecutableExtension(str);
    }

    private static boolean isLink(String str) {
        return "LNK".equals(str.toUpperCase());
    }

    private JPanel createDetailsPanel(SignOperationConfig signOperationConfig, Color color) {
        File dataFile = signOperationConfig.getDataFile();
        JLabel jLabel = new JLabel(dataFile.getAbsolutePath());
        this.accesibleDescription += dataFile.getAbsolutePath();
        jLabel.setFont(jLabel.getFont().deriveFont(1, jLabel.getFont().getSize() + 3.0f));
        JLabel jLabel2 = new JLabel(SimpleAfirmaMessages.getString("SignPanel.145"));
        this.accesibleDescription += SimpleAfirmaMessages.getString("SignPanel.145");
        jLabel2.setFont(jLabel2.getFont().deriveFont(1));
        DocumentInfoPanel documentInfoPanel = new DocumentInfoPanel(signOperationConfig, color);
        this.accesibleDescription += documentInfoPanel.getAccesibleDescription();
        JLabel jLabel3 = null;
        ValidationInfoPanel validationInfoPanel = null;
        if (signOperationConfig.getSignValidity() != null) {
            jLabel3 = new JLabel(SimpleAfirmaMessages.getString("SignPanel.155"));
            this.accesibleDescription += SimpleAfirmaMessages.getString("SignPanel.155");
            jLabel3.setFont(jLabel3.getFont().deriveFont(1));
            validationInfoPanel = new ValidationInfoPanel(signOperationConfig, color);
            this.accesibleDescription += validationInfoPanel.getAccesibleDescription();
        }
        JLabel jLabel4 = new JLabel(SimpleAfirmaMessages.getString("SignPanel.142"));
        this.accesibleDescription += SimpleAfirmaMessages.getString("SignPanel.142");
        jLabel4.setFont(jLabel4.getFont().deriveFont(1));
        this.configInfoPanel = new SignatureConfigInfoPanel(signOperationConfig, color, this);
        this.accesibleDescription += this.configInfoPanel.getAccesibleDescription();
        JPanel jPanel = new JPanel(new GridBagLayout());
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            jPanel.setBackground(color);
        }
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.anchor = 18;
        gridBagConstraints.gridy = 0;
        jPanel.add(jLabel, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.insets = new Insets(11, 0, 0, 0);
        jPanel.add(jLabel2, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.insets = new Insets(4, 11, 0, 0);
        gridBagConstraints.fill = 0;
        jPanel.add(documentInfoPanel, gridBagConstraints);
        gridBagConstraints.gridy++;
        if (signOperationConfig.getSignValidity() != null) {
            gridBagConstraints.insets = new Insets(11, 0, 0, 0);
            jPanel.add(jLabel3, gridBagConstraints);
            gridBagConstraints.gridy++;
            gridBagConstraints.insets = new Insets(4, 11, 0, 0);
            jPanel.add(validationInfoPanel, gridBagConstraints);
            gridBagConstraints.gridy++;
        }
        gridBagConstraints.insets = new Insets(11, 0, 0, 0);
        jPanel.add(jLabel4, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.insets = new Insets(4, 11, 0, 0);
        jPanel.add(this.configInfoPanel, gridBagConstraints);
        return jPanel;
    }

    private static String getExtension(File file) {
        String strSubstring = null;
        String name = file.getName();
        int iLastIndexOf = name.lastIndexOf(46);
        if (iLastIndexOf > 0 && iLastIndexOf < name.length() - 1) {
            strSubstring = name.substring(iLastIndexOf + 1);
        }
        return strSubstring;
    }

    boolean isVisibleSignature() {
        return this.configInfoPanel.isPdfVisibleSignatureSelected();
    }

    boolean isVisibleStamp() {
        return this.configInfoPanel.isPdfStampSignatureSelected();
    }

    String getCertificationLevel() {
        return this.configInfoPanel.getPdfSignatureCertificationLevel();
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle rectangle, int i, int i2) {
        return 30;
    }

    public int getScrollableBlockIncrement(Rectangle rectangle, int i, int i2) {
        return 30;
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public SignatureConfigInfoPanel getConfigInfoPanel() {
        return this.configInfoPanel;
    }

    public void setConfigInfoPanel(SignatureConfigInfoPanel signatureConfigInfoPanel) {
        this.configInfoPanel = signatureConfigInfoPanel;
    }
}
