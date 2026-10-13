package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.signvalidation.SignValidity;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignPanelMultiFilePanel.class */
final class SignPanelMultiFilePanel extends JPanel implements Scrollable {
    private static final long serialVersionUID = 6644719944157037807L;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private String accessibleDescription;

    SignPanelMultiFilePanel(List<SignOperationConfig> list) {
        super(true);
        this.accessibleDescription = "";
        SwingUtilities.invokeLater(() -> {
            createUI(list);
        });
    }

    void createUI(List<SignOperationConfig> list) {
        setLayout(new GridBagLayout());
        Component fileOperationTitleRenderer = new FileOperationTitleRenderer();
        fileOperationTitleRenderer.setFileNameColumnTitle(SimpleAfirmaMessages.getString("SignDataPanel.43"));
        fileOperationTitleRenderer.setSignatureFormatColumnTitle(SimpleAfirmaMessages.getString("SignDataPanel.44"));
        fileOperationTitleRenderer.setSizeColumnTitle(SimpleAfirmaMessages.getString("SignDataPanel.45"));
        fileOperationTitleRenderer.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LookAndFeelManager.WINDOWS_HIGH_CONTRAST ? Color.white : Color.black));
        SignOperationConfig[] signOperationConfigArr = (SignOperationConfig[]) list.toArray(new SignOperationConfig[list.size()]);
        this.accessibleDescription += SimpleAfirmaMessages.getString("SignDataPanel.47");
        for (SignOperationConfig signOperationConfig : signOperationConfigArr) {
            this.accessibleDescription += SimpleAfirmaMessages.getString("SignDataPanel.43") + signOperationConfig.getDataFile().getName() + SimpleAfirmaMessages.getString("SignDataPanel.44") + signOperationConfig.getSignatureFormatName() + SimpleAfirmaMessages.getString("SignDataPanel.45") + (signOperationConfig.getDataFile().length() / 1024) + "KB";
        }
        final Component jList = new JList(signOperationConfigArr);
        jList.getAccessibleContext().setAccessibleDescription(this.accessibleDescription);
        jList.setCellRenderer(new FileOperationCellRenderer());
        jList.addMouseListener(new MouseListener() { // from class: es.gob.afirma.standalone.ui.SignPanelMultiFilePanel.1
            public void mouseReleased(MouseEvent mouseEvent) {
            }

            public void mousePressed(MouseEvent mouseEvent) {
            }

            public void mouseExited(MouseEvent mouseEvent) {
            }

            public void mouseEntered(MouseEvent mouseEvent) {
            }

            public void mouseClicked(MouseEvent mouseEvent) {
                int iLocationToIndex;
                if (mouseEvent.getSource() instanceof JList) {
                    JList jList2 = (JList) mouseEvent.getSource();
                    if (mouseEvent.getButton() == 3) {
                        int iLocationToIndex2 = jList2.locationToIndex(mouseEvent.getPoint());
                        if (iLocationToIndex2 >= 0) {
                            SignPanelMultiFilePanel.this.new ContextualMenu((SignOperationConfig) jList2.getModel().getElementAt(iLocationToIndex2), (Component) mouseEvent.getSource()).show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                            return;
                        }
                        return;
                    }
                    if (mouseEvent.getButton() == 1 && mouseEvent.getClickCount() == 2 && (iLocationToIndex = jList2.locationToIndex(mouseEvent.getPoint())) >= 0) {
                        SignPanelMultiFilePanel.openDataFileItem((SignOperationConfig) jList2.getModel().getElementAt(iLocationToIndex));
                    }
                }
            }
        });
        jList.addKeyListener(new KeyListener() { // from class: es.gob.afirma.standalone.ui.SignPanelMultiFilePanel.2
            public void keyTyped(KeyEvent keyEvent) {
            }

            public void keyPressed(KeyEvent keyEvent) {
            }

            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 10 && (keyEvent.getSource() instanceof JList)) {
                    JList jList2 = (JList) keyEvent.getSource();
                    int selectedIndex = jList.getSelectedIndex();
                    if (selectedIndex >= 0) {
                        SignPanelMultiFilePanel.openDataFileItem((SignOperationConfig) jList2.getModel().getElementAt(selectedIndex));
                    }
                }
            }
        });
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridy = 0;
        add(fileOperationTitleRenderer, gridBagConstraints);
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridy++;
        add(jList, gridBagConstraints);
    }

    static void openDataFileItem(SignOperationConfig signOperationConfig) {
        if (signOperationConfig.getDataFile() != null) {
            if (signOperationConfig.getSignValidity() != null) {
                new VisorFirma(false, null).initialize(false, signOperationConfig.getDataFile());
            } else {
                SwingUtilities.invokeLater(() -> {
                    try {
                        Desktop.getDesktop().open(signOperationConfig.getDataFile());
                    } catch (IOException e) {
                        LOGGER.log(Level.WARNING, "No se pudo abrir el fichero: " + LoggerUtil.getCleanUserHomePath(signOperationConfig.getDataFile().getAbsolutePath()), (Throwable) e);
                    }
                });
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignPanelMultiFilePanel$FileOperationTitleRenderer.class */
    class FileOperationTitleRenderer extends JPanel {
        private static final long serialVersionUID = -8317461712461916374L;
        private final JLabel sizeLabel;
        private final JLabel fileNameLabel = new JLabel();
        private final JLabel formatNameLabel = new JLabel();

        public FileOperationTitleRenderer() {
            this.formatNameLabel.setPreferredSize(new Dimension(120, 14));
            this.sizeLabel = new JLabel();
            this.sizeLabel.setPreferredSize(new Dimension(60, 14));
            if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST && !Platform.OS.MACOSX.equals(Platform.getOS())) {
                setBackground(LookAndFeelManager.DEFAULT_COLOR);
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
            add(this.formatNameLabel, gridBagConstraints);
            gridBagConstraints.gridx++;
            add(this.sizeLabel, gridBagConstraints);
        }

        void setFileNameColumnTitle(String str) {
            this.fileNameLabel.setText(str);
        }

        void setSignatureFormatColumnTitle(String str) {
            this.formatNameLabel.setText(str);
        }

        void setSizeColumnTitle(String str) {
            this.sizeLabel.setText(str);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignPanelMultiFilePanel$FileOperationCellRenderer.class */
    class FileOperationCellRenderer extends JPanel implements ListCellRenderer<SignOperationConfig> {
        private static final long serialVersionUID = -6210265681086564451L;
        private final JLabel fileNameLabel;
        private final JLabel sizeLabel;
        private final JLabel formatNameLabel;
        private final NumberFormat kbFormatter;
        private final DecimalFormat mbFormatter;
        private final Border focusedBorder;
        private final Border unfocusedBorder;
        private int basePathLength = 0;
        private final Dimension iconDimension = new Dimension(32, 32);
        private final JLabel icon = new JLabel();

        public /* bridge */ /* synthetic */ Component getListCellRendererComponent(JList jList, Object obj, int i, boolean z, boolean z2) {
            return getListCellRendererComponent((JList<? extends SignOperationConfig>) jList, (SignOperationConfig) obj, i, z, z2);
        }

        public FileOperationCellRenderer() {
            this.icon.setPreferredSize(this.iconDimension);
            this.fileNameLabel = new JLabel();
            this.formatNameLabel = new JLabel();
            this.formatNameLabel.setPreferredSize(new Dimension(120, 32));
            this.sizeLabel = new JLabel();
            this.sizeLabel.setPreferredSize(new Dimension(60, 32));
            this.kbFormatter = NumberFormat.getNumberInstance();
            this.mbFormatter = new DecimalFormat("#.#");
            this.mbFormatter.setRoundingMode(RoundingMode.CEILING);
            this.focusedBorder = BorderFactory.createDashedBorder(Color.GRAY);
            this.unfocusedBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
            if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                setBackground(Color.WHITE);
            }
            setLayout(new GridBagLayout());
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = 2;
            gridBagConstraints.insets = new Insets(3, 11, 3, 0);
            gridBagConstraints.gridx = 0;
            add(this.icon, gridBagConstraints);
            gridBagConstraints.gridx++;
            gridBagConstraints.weightx = 1.0d;
            add(this.fileNameLabel, gridBagConstraints);
            gridBagConstraints.weightx = 0.0d;
            gridBagConstraints.gridx++;
            add(this.formatNameLabel, gridBagConstraints);
            gridBagConstraints.gridx++;
            add(this.sizeLabel, gridBagConstraints);
        }

        public Component getListCellRendererComponent(JList<? extends SignOperationConfig> jList, SignOperationConfig signOperationConfig, int i, boolean z, boolean z2) {
            if (this.basePathLength == 0) {
                this.basePathLength = calculateBasePathLength(jList.getModel());
            }
            ScalablePane icon = signOperationConfig.getFileType().getIcon();
            icon.setPreferredSize(this.iconDimension);
            this.icon.setIcon(new ImageIcon(icon.getScaledInstanceToFit(icon.getMaster(), this.iconDimension)));
            if (signOperationConfig.getInvalidSignatureText() == null) {
                this.fileNameLabel.setText(signOperationConfig.getDataFile().getAbsolutePath().substring(this.basePathLength));
                if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                    this.fileNameLabel.setForeground(Color.BLACK);
                }
            } else if (signOperationConfig.getSignValidity() != null && SignValidity.SIGN_DETAIL_TYPE.PENDING_CONFIRM_BY_USER.equals(signOperationConfig.getSignValidity().get(0).getValidity())) {
                this.fileNameLabel.setText((signOperationConfig.getDataFile().getAbsolutePath() + " (" + signOperationConfig.getSignValidity().get(0).validityTypeToString() + ")").substring(this.basePathLength));
                if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                    this.fileNameLabel.setForeground(Color.RED);
                }
            } else {
                this.fileNameLabel.setText((signOperationConfig.getDataFile().getAbsolutePath() + " " + SimpleAfirmaMessages.getString("SignPanel.147")).substring(this.basePathLength));
                if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                    this.fileNameLabel.setForeground(Color.RED);
                }
            }
            this.formatNameLabel.setText(signOperationConfig.getSignatureFormatName());
            this.sizeLabel.setText(calculateSize(signOperationConfig.getDataFile().length()));
            setBorder(z2 ? this.focusedBorder : this.unfocusedBorder);
            return this;
        }

        private int calculateBasePathLength(ListModel<? extends SignOperationConfig> listModel) {
            int length;
            int i = Integer.MAX_VALUE;
            for (int i2 = 0; i2 < listModel.getSize(); i2++) {
                SignOperationConfig signOperationConfig = (SignOperationConfig) listModel.getElementAt(i2);
                if (signOperationConfig.getDataFile() != null && signOperationConfig.getDataFile().getParentFile() != null && (length = signOperationConfig.getDataFile().getParentFile().getAbsolutePath().length()) < i) {
                    i = length;
                }
            }
            if (i > 0) {
                i++;
            }
            return i;
        }

        private String calculateSize(long j) {
            if (j == 0) {
                return "0 KB";
            }
            if (j < 1024) {
                return "1 KB";
            }
            if (j < 1024000) {
                return this.kbFormatter.format(j / 1024) + " KB";
            }
            return this.mbFormatter.format(j / 1048576) + " MB";
        }
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

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignPanelMultiFilePanel$ContextualMenu.class */
    private class ContextualMenu extends JPopupMenu implements ActionListener {
        private static final long serialVersionUID = 4803487514272887434L;
        private final SignOperationConfig config;
        private final Component parent;
        private final JMenuItem openFileItem = new JMenuItem(SimpleAfirmaMessages.getString("SignPanel.51"));
        private final JMenuItem seeAttributesItem;

        public ContextualMenu(SignOperationConfig signOperationConfig, Component component) {
            this.config = signOperationConfig;
            this.parent = component;
            this.openFileItem.setFont(this.openFileItem.getFont().deriveFont(1));
            this.openFileItem.addActionListener(this);
            add(this.openFileItem);
            this.seeAttributesItem = new JMenuItem(SimpleAfirmaMessages.getString("SignPanel.148"));
            this.seeAttributesItem.addActionListener(this);
            add(this.seeAttributesItem);
        }

        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource() == this.openFileItem) {
                SignPanelMultiFilePanel.openDataFileItem(this.config);
            } else if (actionEvent.getSource() == this.seeAttributesItem) {
                JDialog jDialogNewInstance = SignatureAttributesDialog.newInstance(this.parent, this.config);
                jDialogNewInstance.getAccessibleContext().setAccessibleDescription(SignatureAttributesDialog.getAccessibleDescription());
                jDialogNewInstance.setVisible(true);
            }
        }
    }
}
