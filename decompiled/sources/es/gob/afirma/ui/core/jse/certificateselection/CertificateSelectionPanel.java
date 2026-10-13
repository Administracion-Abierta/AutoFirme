package es.gob.afirma.ui.core.jse.certificateselection;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.keystores.NameCertificateBean;
import es.gob.afirma.core.prefs.KeyStorePreferencesManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/certificateselection/CertificateSelectionPanel.class */
final class CertificateSelectionPanel extends JPanel implements ListSelectionListener {
    private static final long serialVersionUID = 6288294705582545804L;
    private static final int TITLE_FONT_SIZE = 14;
    private static final int TEXT_FONT_SIZE = 12;
    private static final int CERT_LIST_ELEMENT_HEIGHT = 86;
    private static final String PREFERENCE_CERT_VIEW = "certView";
    private JList<CertificateLine> certList;
    JScrollPane sPane;
    private JPanel certListPanel;
    private JPanel textMessagePanel;
    private int selectedIndex = -1;
    private NameCertificateBean[] certificateBeans;
    private final String dialogSubHeadline;
    private CertificateLineView certLineView;
    private static boolean highContrast;
    private static final String VERDANA_FONT_NAME = "Verdana";
    private static final Font TITLE_FONT = new Font(VERDANA_FONT_NAME, 1, 14);
    private static final Font TEXT_FONT = new Font(VERDANA_FONT_NAME, 0, 12);
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    CertificateSelectionPanel(NameCertificateBean[] nameCertificateBeanArr, CertificateSelectionDialog certificateSelectionDialog, String str, String str2, boolean z, boolean z2, int[] iArr) {
        this.certificateBeans = nameCertificateBeanArr == null ? new NameCertificateBean[0] : (NameCertificateBean[]) nameCertificateBeanArr.clone();
        this.dialogSubHeadline = str2;
        this.certLineView = loadPreferredCertificateView();
        createUI(certificateSelectionDialog, str, z, z2, iArr);
    }

    private void createUI(final CertificateSelectionDialog certificateSelectionDialog, String str, boolean z, boolean z2, int[] iArr) {
        URL resource;
        URL resource2;
        URL resource3;
        URL resource4;
        setLayout(new GridBagLayout());
        Color color = Color.BLACK;
        setBackground(color);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.insets = new Insets(13, 15, 8, 15);
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        Component jLabel = new JLabel(str != null ? str : CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.0"));
        jLabel.setFont(TITLE_FONT);
        Object desktopProperty = Toolkit.getDefaultToolkit().getDesktopProperty("win.highContrast.on");
        if (desktopProperty instanceof Boolean) {
            highContrast = ((Boolean) desktopProperty).booleanValue();
        }
        if (!highContrast) {
            try {
                jLabel.setForeground(Color.decode("0x0033BC"));
                color = UIManager.getColor("window") != null ? new Color(UIManager.getColor("window").getRGB()) : Color.WHITE;
            } catch (Throwable th) {
                color = Color.WHITE;
            }
            setBackground(color);
        } else {
            jLabel.setForeground(Color.WHITE);
        }
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        add(jLabel, gridBagConstraints);
        gridBagConstraints.insets = new Insets(13, 0, 8, 5);
        gridBagConstraints.weightx = 0.0d;
        gridBagConstraints.gridx++;
        if (z) {
            if (!highContrast) {
                resource2 = CertificateSelectionPanel.class.getResource("/resources/toolbar/ic_autorenew_black_18dp.png");
            } else {
                resource2 = CertificateSelectionPanel.class.getResource("/resources/toolbar/ic_autorenew_white_18dp.png");
            }
            final Component jButton = new JButton(new ImageIcon(resource2, CertificateSelectionDialogMessages.getString("UtilToolBar.1")));
            jButton.setBorder(BorderFactory.createEmptyBorder());
            jButton.setRolloverEnabled(false);
            jButton.setCursor(Cursor.getPredefinedCursor(12));
            jButton.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("UtilToolBar.1"));
            jButton.setToolTipText(CertificateSelectionDialogMessages.getString("UtilToolBar.1"));
            jButton.addActionListener(new ActionListener() { // from class: es.gob.afirma.ui.core.jse.certificateselection.CertificateSelectionPanel.1
                public void actionPerformed(ActionEvent actionEvent) {
                    UtilActions.doRefresh(certificateSelectionDialog, CertificateSelectionPanel.this);
                }
            });
            jButton.addFocusListener(new FocusListener() { // from class: es.gob.afirma.ui.core.jse.certificateselection.CertificateSelectionPanel.2
                public void focusGained(FocusEvent focusEvent) {
                    if (CertificateSelectionPanel.isHighContrast()) {
                        jButton.setIcon(new ImageIcon(CertificateSelectionPanel.class.getResource("/resources/toolbar/ic_autorenew_black_18dp.png"), CertificateSelectionDialogMessages.getString("UtilToolBar.1")));
                    }
                }

                public void focusLost(FocusEvent focusEvent) {
                    if (CertificateSelectionPanel.isHighContrast()) {
                        jButton.setIcon(new ImageIcon(CertificateSelectionPanel.class.getResource("/resources/toolbar/ic_autorenew_white_18dp.png"), CertificateSelectionDialogMessages.getString("UtilToolBar.1")));
                    }
                }
            });
            add(jButton, gridBagConstraints);
            gridBagConstraints.gridx++;
            if (z2 && iArr != null && iArr.length > 0) {
                JPopupMenu jPopupMenu = new JPopupMenu();
                if (contains(iArr, 1)) {
                    JMenuItem jMenuItem = new JMenuItem(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.35"));
                    jMenuItem.addActionListener(new ChangeKeyStoreActionListener(this, certificateSelectionDialog, 1, jMenuItem.getText(), null));
                    jPopupMenu.add(jMenuItem);
                }
                if (contains(iArr, 2)) {
                    JMenuItem jMenuItem2 = new JMenuItem(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.36"));
                    jMenuItem2.addActionListener(new ChangeKeyStoreActionListener(this, certificateSelectionDialog, 2, jMenuItem2.getText(), null));
                    jPopupMenu.add(jMenuItem2);
                }
                if (contains(iArr, 3)) {
                    JMenuItem jMenuItem3 = new JMenuItem(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.37"));
                    jMenuItem3.addActionListener(new ChangeKeyStoreActionListener(this, certificateSelectionDialog, 3, jMenuItem3.getText(), null));
                    jPopupMenu.add(jMenuItem3);
                }
                if (contains(iArr, 4)) {
                    JMenuItem jMenuItem4 = new JMenuItem(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.38"));
                    jMenuItem4.addActionListener(new ChangeKeyStoreActionListener(this, certificateSelectionDialog, 4, jMenuItem4.getText(), null));
                    jPopupMenu.add(jMenuItem4);
                }
                Map<String, String> userSmartCardsRegistered = KeyStorePreferencesManager.getUserSmartCardsRegistered();
                for (String str2 : userSmartCardsRegistered.keySet()) {
                    String str3 = userSmartCardsRegistered.get(str2);
                    JMenuItem jMenuItem5 = new JMenuItem(str2);
                    jMenuItem5.addActionListener(new ChangeKeyStoreActionListener(this, certificateSelectionDialog, 5, str2, str3));
                    jPopupMenu.add(jMenuItem5);
                }
                Map<String, String> systemSmartCardsRegistered = KeyStorePreferencesManager.getSystemSmartCardsRegistered();
                for (String str4 : systemSmartCardsRegistered.keySet()) {
                    String str5 = systemSmartCardsRegistered.get(str4);
                    JMenuItem jMenuItem6 = new JMenuItem(str4);
                    jMenuItem6.addActionListener(new ChangeKeyStoreActionListener(this, certificateSelectionDialog, 5, str4, str5));
                    jPopupMenu.add(jMenuItem6);
                }
                if (!highContrast) {
                    resource4 = CertificateSelectionPanel.class.getResource("/resources/toolbar/ic_open_in_browser_black_18dp.png");
                } else {
                    resource4 = CertificateSelectionPanel.class.getResource("/resources/toolbar/ic_open_in_browser_white_18dp.png");
                }
                final Component jDropDownButton = new JDropDownButton(new ImageIcon(resource4, CertificateSelectionDialogMessages.getString("UtilToolBar.2")));
                jDropDownButton.setComponentPopupMenu(jPopupMenu);
                jDropDownButton.setRolloverEnabled(false);
                jDropDownButton.setBorder(BorderFactory.createEmptyBorder());
                jDropDownButton.setCursor(Cursor.getPredefinedCursor(12));
                jDropDownButton.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("UtilToolBar.2"));
                jDropDownButton.setToolTipText(CertificateSelectionDialogMessages.getString("UtilToolBar.2"));
                jDropDownButton.addFocusListener(new FocusListener() { // from class: es.gob.afirma.ui.core.jse.certificateselection.CertificateSelectionPanel.3
                    public void focusGained(FocusEvent focusEvent) {
                        if (CertificateSelectionPanel.isHighContrast()) {
                            jDropDownButton.setIcon(new ImageIcon(CertificateSelectionPanel.class.getResource("/resources/toolbar/ic_open_in_browser_black_18dp.png"), CertificateSelectionDialogMessages.getString("UtilToolBar.1")));
                        }
                    }

                    public void focusLost(FocusEvent focusEvent) {
                        if (CertificateSelectionPanel.isHighContrast()) {
                            jDropDownButton.setIcon(new ImageIcon(CertificateSelectionPanel.class.getResource("/resources/toolbar/ic_open_in_browser_white_18dp.png"), CertificateSelectionDialogMessages.getString("UtilToolBar.1")));
                        }
                    }
                });
                add(jDropDownButton, gridBagConstraints);
            }
            gridBagConstraints.insets = new Insets(13, 0, 8, 15);
            gridBagConstraints.gridx++;
            if (!highContrast) {
                resource3 = CertificateSelectionPanel.class.getResource("/resources/toolbar/ic_help_black_18dp.png");
            } else {
                resource3 = CertificateSelectionPanel.class.getResource("/resources/toolbar/ic_help_white_18dp.png");
            }
            final Component jButton2 = new JButton(new ImageIcon(resource3, CertificateSelectionDialogMessages.getString("UtilToolBar.3")));
            jButton2.setBorder(BorderFactory.createEmptyBorder());
            jButton2.setRolloverEnabled(false);
            jButton2.setCursor(Cursor.getPredefinedCursor(12));
            jButton2.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("UtilToolBar.3"));
            jButton2.setToolTipText(CertificateSelectionDialogMessages.getString("UtilToolBar.3"));
            jButton2.addActionListener(new ActionListener() { // from class: es.gob.afirma.ui.core.jse.certificateselection.CertificateSelectionPanel.4
                public void actionPerformed(ActionEvent actionEvent) {
                    UtilActions.doHelp();
                }
            });
            jButton2.addFocusListener(new FocusListener() { // from class: es.gob.afirma.ui.core.jse.certificateselection.CertificateSelectionPanel.5
                public void focusGained(FocusEvent focusEvent) {
                    if (CertificateSelectionPanel.isHighContrast()) {
                        jButton2.setIcon(new ImageIcon(CertificateSelectionPanel.class.getResource("/resources/toolbar/ic_help_black_18dp.png"), CertificateSelectionDialogMessages.getString("UtilToolBar.1")));
                    }
                }

                public void focusLost(FocusEvent focusEvent) {
                    if (CertificateSelectionPanel.isHighContrast()) {
                        jButton2.setIcon(new ImageIcon(CertificateSelectionPanel.class.getResource("/resources/toolbar/ic_help_white_18dp.png"), CertificateSelectionDialogMessages.getString("UtilToolBar.1")));
                    }
                }
            });
            add(jButton2, gridBagConstraints);
        }
        gridBagConstraints.gridx++;
        JPopupMenu jPopupMenu2 = new JPopupMenu();
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButtonMenuItem jRadioButtonMenuItem = new JRadioButtonMenuItem(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.25"));
        jRadioButtonMenuItem.addActionListener(new ChangeViewActionListener(this, certificateSelectionDialog, CertificateLineView.PERSONAL));
        jRadioButtonMenuItem.setSelected(this.certLineView == CertificateLineView.PERSONAL);
        buttonGroup.add(jRadioButtonMenuItem);
        jPopupMenu2.add(jRadioButtonMenuItem);
        JRadioButtonMenuItem jRadioButtonMenuItem2 = new JRadioButtonMenuItem(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.26"));
        jRadioButtonMenuItem2.addActionListener(new ChangeViewActionListener(this, certificateSelectionDialog, CertificateLineView.REPRESENTATIVE));
        jRadioButtonMenuItem2.setSelected(this.certLineView == CertificateLineView.REPRESENTATIVE);
        buttonGroup.add(jRadioButtonMenuItem2);
        jPopupMenu2.add(jRadioButtonMenuItem2);
        JRadioButtonMenuItem jRadioButtonMenuItem3 = new JRadioButtonMenuItem(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.27"));
        jRadioButtonMenuItem3.addActionListener(new ChangeViewActionListener(this, certificateSelectionDialog, CertificateLineView.PSEUDONYM));
        jRadioButtonMenuItem3.setSelected(this.certLineView == CertificateLineView.PSEUDONYM);
        buttonGroup.add(jRadioButtonMenuItem3);
        jPopupMenu2.add(jRadioButtonMenuItem3);
        if (!highContrast) {
            resource = CertificateSelectionPanel.class.getResource("/resources/toolbar/ic_view_arrow_black.png");
        } else {
            resource = CertificateSelectionPanel.class.getResource("/resources/toolbar/ic_view_arrow_white.png");
        }
        final Component jDropDownButton2 = new JDropDownButton(new ImageIcon(resource, CertificateSelectionDialogMessages.getString("UtilToolBar.4")));
        jDropDownButton2.setComponentPopupMenu(jPopupMenu2);
        jDropDownButton2.setRolloverEnabled(false);
        jDropDownButton2.setBorder(BorderFactory.createEmptyBorder());
        jDropDownButton2.setCursor(Cursor.getPredefinedCursor(12));
        jDropDownButton2.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("UtilToolBar.4"));
        jDropDownButton2.setToolTipText(CertificateSelectionDialogMessages.getString("UtilToolBar.4"));
        jDropDownButton2.addFocusListener(new FocusListener() { // from class: es.gob.afirma.ui.core.jse.certificateselection.CertificateSelectionPanel.6
            public void focusGained(FocusEvent focusEvent) {
                if (CertificateSelectionPanel.isHighContrast()) {
                    jDropDownButton2.setIcon(new ImageIcon(CertificateSelectionPanel.class.getResource("/resources/toolbar/ic_view_arrow_black.png"), CertificateSelectionDialogMessages.getString("UtilToolBar.1")));
                }
            }

            public void focusLost(FocusEvent focusEvent) {
                if (CertificateSelectionPanel.isHighContrast()) {
                    jDropDownButton2.setIcon(new ImageIcon(CertificateSelectionPanel.class.getResource("/resources/toolbar/ic_view_arrow_white.png"), CertificateSelectionDialogMessages.getString("UtilToolBar.1")));
                }
            }
        });
        add(jDropDownButton2, gridBagConstraints);
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.insets = new Insets(0, 15, 4, 15);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy++;
        this.textMessagePanel = new JPanel();
        this.textMessagePanel.setLayout(new GridBagLayout());
        this.textMessagePanel.setOpaque(false);
        this.textMessagePanel.setBorder((Border) null);
        add(this.textMessagePanel, gridBagConstraints);
        gridBagConstraints.insets = new Insets(4, 15, 8, 15);
        gridBagConstraints.gridy++;
        add(new JSeparator(), gridBagConstraints);
        gridBagConstraints.insets = new Insets(8, 18, 13, 18);
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridy++;
        this.certListPanel = new JPanel();
        this.certListPanel.setLayout(new GridBagLayout());
        this.certListPanel.setBorder((Border) null);
        this.certList = new JList<>();
        this.certList.setSelectionMode(0);
        this.certList.setCellRenderer(new CertListCellRendered(color, highContrast));
        updateCertListInfo(this.certificateBeans);
        this.certList.addListSelectionListener(this);
        CertLinkMouseListener certLinkMouseListener = new CertLinkMouseListener();
        this.certList.addMouseMotionListener(certLinkMouseListener);
        this.certList.addMouseListener(certLinkMouseListener);
        add(this.certListPanel, gridBagConstraints);
    }

    private static boolean contains(int[] iArr, int i) {
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    void setCertLineView(CertificateLineView certificateLineView) {
        this.certLineView = certificateLineView;
    }

    void refresh(NameCertificateBean[] nameCertificateBeanArr) {
        this.certificateBeans = (NameCertificateBean[]) nameCertificateBeanArr.clone();
        if (this.sPane.getVerticalScrollBar() != null) {
            this.sPane.getVerticalScrollBar().setValue(0);
        }
        updateCertListInfo(nameCertificateBeanArr);
        if (nameCertificateBeanArr.length > 0) {
            this.certList.setSelectedIndex(0);
        }
    }

    private static List<CertificateLine> createCertLines(NameCertificateBean[] nameCertificateBeanArr, CertificateLineView certificateLineView) {
        CertificateLineFactory certificateLineFactoryNewInstance = CertificateLineFactory.newInstance(certificateLineView);
        ArrayList arrayList = new ArrayList();
        for (NameCertificateBean nameCertificateBean : nameCertificateBeanArr) {
            try {
                CertificateLine certificateLineBuildCertificateLine = certificateLineFactoryNewInstance.buildCertificateLine(nameCertificateBean.getName(), nameCertificateBean.getCertificate(), isHighContrast());
                certificateLineBuildCertificateLine.setPreferredSize(new Dimension(0, 86));
                arrayList.add(certificateLineBuildCertificateLine);
            } catch (Exception e) {
            }
        }
        return arrayList;
    }

    void updateCertListInfo() {
        updateCertListInfo(this.certificateBeans);
        if (this.certificateBeans.length > 0) {
            this.certList.setSelectedIndex(0);
            this.sPane.getVerticalScrollBar().setValue(0);
        }
    }

    void updateCertListInfo(NameCertificateBean[] nameCertificateBeanArr) {
        String string;
        List<CertificateLine> listCreateCertLines = createCertLines(nameCertificateBeanArr, this.certLineView);
        this.textMessagePanel.removeAll();
        if (listCreateCertLines.size() <= 1) {
            if (listCreateCertLines.size() == 1) {
                string = this.dialogSubHeadline != null ? this.dialogSubHeadline : CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.1");
            } else {
                string = CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.8");
            }
            JTextPane jTextPane = new JTextPane();
            jTextPane.setOpaque(false);
            jTextPane.setText(string);
            jTextPane.setFont(TEXT_FONT);
            jTextPane.setBorder((Border) null);
            jTextPane.setPreferredSize(new Dimension(370, 40));
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = 1;
            gridBagConstraints.weightx = 1.0d;
            gridBagConstraints.weighty = 1.0d;
            this.textMessagePanel.add(jTextPane, gridBagConstraints);
        }
        this.certList.setListData(listCreateCertLines.toArray(new CertificateLine[listCreateCertLines.size()]));
        this.certList.setVisibleRowCount(Math.max(Math.min(4, listCreateCertLines.size()), 1));
        if (listCreateCertLines.size() > 0) {
            this.certList.setSelectedIndex(0);
            this.selectedIndex = 0;
        } else {
            this.selectedIndex = -1;
        }
        JScrollPane jScrollPane = new JScrollPane(this.certList, 20, 31);
        jScrollPane.setBorder((Border) null);
        jScrollPane.setPreferredSize(new Dimension(500, (86 * this.certList.getVisibleRowCount()) + 3));
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = 1;
        gridBagConstraints2.weightx = 1.0d;
        gridBagConstraints2.weighty = 1.0d;
        if (this.sPane != null) {
            this.certListPanel.remove(this.sPane);
        }
        this.sPane = jScrollPane;
        this.certListPanel.add(this.sPane, gridBagConstraints2);
    }

    void selectCertificateList() {
        this.certList.requestFocusInWindow();
    }

    String getSelectedCertificateAlias() {
        if (this.selectedIndex == -1) {
            return null;
        }
        return this.certificateBeans[this.selectedIndex].getAlias();
    }

    int getShowedCertsCount() {
        if (this.certificateBeans == null) {
            return 0;
        }
        return this.certificateBeans.length;
    }

    public CertificateLineView getCertLineView() {
        return this.certLineView;
    }

    NameCertificateBean[] getCertificateBeans() {
        return this.certificateBeans;
    }

    public static boolean isHighContrast() {
        return highContrast;
    }

    void addCertificateListMouseListener(MouseListener mouseListener) {
        this.certList.addMouseListener(mouseListener);
    }

    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        this.selectedIndex = this.certList.getSelectedIndex();
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/certificateselection/CertificateSelectionPanel$CertListCellRendered.class */
    private static final class CertListCellRendered implements ListCellRenderer<CertificateLine> {
        private final Color bgColor;
        private final boolean highContrast;

        public /* bridge */ /* synthetic */ Component getListCellRendererComponent(JList jList, Object obj, int i, boolean z, boolean z2) {
            return getListCellRendererComponent((JList<? extends CertificateLine>) jList, (CertificateLine) obj, i, z, z2);
        }

        CertListCellRendered(Color color, boolean z) {
            this.bgColor = color;
            this.highContrast = z;
        }

        public Component getListCellRendererComponent(JList<? extends CertificateLine> jList, CertificateLine certificateLine, int i, boolean z, boolean z2) {
            if (z) {
                certificateLine.setBackground(Color.decode("0xD9EAFF"));
                if (CertificateSelectionPanel.isHighContrast()) {
                    if (certificateLine instanceof DefaultCertificateLine) {
                        ((DefaultCertificateLine) certificateLine).getAlias().setForeground(Color.BLACK);
                        ((DefaultCertificateLine) certificateLine).getIssuer().setForeground(Color.BLACK);
                        ((DefaultCertificateLine) certificateLine).getDates().setForeground(Color.BLACK);
                        ((DefaultCertificateLine) certificateLine).getPropertiesLink().setForeground(Color.BLUE);
                    } else if (certificateLine instanceof PseudonymCertificateLine) {
                        ((PseudonymCertificateLine) certificateLine).getPositionLabel().setForeground(Color.BLACK);
                        ((PseudonymCertificateLine) certificateLine).getOrganizationLabel().setForeground(Color.BLACK);
                        ((PseudonymCertificateLine) certificateLine).getDates().setForeground(Color.BLACK);
                        ((PseudonymCertificateLine) certificateLine).getPropertiesLink().setForeground(Color.BLUE);
                    } else if (certificateLine instanceof RepresentativeCertificateLine) {
                        ((RepresentativeCertificateLine) certificateLine).getOrganizationLabel().setForeground(Color.BLACK);
                        ((RepresentativeCertificateLine) certificateLine).getAgentLabel().setForeground(Color.BLACK);
                        ((RepresentativeCertificateLine) certificateLine).getDates().setForeground(Color.BLACK);
                        ((RepresentativeCertificateLine) certificateLine).getPropertiesLink().setForeground(Color.BLUE);
                    }
                }
                certificateLine.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.WHITE, 1), BorderFactory.createLineBorder(Color.decode("0x84ACDD"), 1)));
            } else {
                certificateLine.setBackground(this.bgColor);
                if (CertificateSelectionPanel.isHighContrast()) {
                    if (certificateLine instanceof DefaultCertificateLine) {
                        ((DefaultCertificateLine) certificateLine).getAlias().setForeground(Color.WHITE);
                        ((DefaultCertificateLine) certificateLine).getIssuer().setForeground(Color.WHITE);
                        ((DefaultCertificateLine) certificateLine).getDates().setForeground(Color.WHITE);
                        ((DefaultCertificateLine) certificateLine).getPropertiesLink().setForeground(Color.YELLOW);
                    } else if (certificateLine instanceof PseudonymCertificateLine) {
                        ((PseudonymCertificateLine) certificateLine).getPositionLabel().setForeground(Color.WHITE);
                        ((PseudonymCertificateLine) certificateLine).getOrganizationLabel().setForeground(Color.WHITE);
                        ((PseudonymCertificateLine) certificateLine).getDates().setForeground(Color.WHITE);
                        ((PseudonymCertificateLine) certificateLine).getPropertiesLink().setForeground(Color.YELLOW);
                    } else if (certificateLine instanceof RepresentativeCertificateLine) {
                        ((RepresentativeCertificateLine) certificateLine).getOrganizationLabel().setForeground(Color.WHITE);
                        ((RepresentativeCertificateLine) certificateLine).getAgentLabel().setForeground(Color.WHITE);
                        ((RepresentativeCertificateLine) certificateLine).getDates().setForeground(Color.WHITE);
                        ((RepresentativeCertificateLine) certificateLine).getPropertiesLink().setForeground(Color.YELLOW);
                    }
                }
                certificateLine.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 4));
            }
            return certificateLine;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/certificateselection/CertificateSelectionPanel$CertLinkMouseListener.class */
    private final class CertLinkMouseListener extends MouseAdapter {
        private boolean entered = false;

        CertLinkMouseListener() {
        }

        public void mouseClicked(MouseEvent mouseEvent) {
            JList jList = (JList) mouseEvent.getSource();
            CertificateLine certificateLine = (CertificateLine) jList.getSelectedValue();
            if (certificateLine != null && certificateLine.getCertificateLinkBounds() != null && mouseEvent.getClickCount() == 1 && mouseEvent.getY() < 86 * jList.getModel().getSize() && certificateLine.getCertificateLinkBounds().contains(mouseEvent.getX(), mouseEvent.getY() % 86)) {
                try {
                    CertificateUtils.openCert(CertificateSelectionPanel.this, certificateLine.getCertificate());
                } catch (AOCancelledOperationException e) {
                }
            }
        }

        public void mouseMoved(MouseEvent mouseEvent) {
            JList jList = (JList) mouseEvent.getSource();
            CertificateLine certificateLine = (CertificateLine) jList.getSelectedValue();
            if (certificateLine != null) {
                if (mouseEvent.getY() < 86 * jList.getModel().getSize() && certificateLine.getCertificateLinkBounds().contains(mouseEvent.getX(), mouseEvent.getY() % 86)) {
                    if (!this.entered) {
                        jList.setCursor(Cursor.getPredefinedCursor(12));
                        this.entered = true;
                        return;
                    }
                    return;
                }
                if (this.entered) {
                    jList.setCursor(Cursor.getPredefinedCursor(0));
                    this.entered = false;
                }
            }
        }
    }

    private static CertificateLineView loadPreferredCertificateView() {
        CertificateLineView certificateLineViewValueOf = null;
        try {
            String str = Preferences.userNodeForPackage(CertificateSelectionPanel.class).get(PREFERENCE_CERT_VIEW, null);
            if (str != null) {
                certificateLineViewValueOf = CertificateLineView.valueOf(str);
            }
        } catch (Exception e) {
            certificateLineViewValueOf = null;
            LOGGER.log(Level.WARNING, "No se pudo cargar la vista de certificado de las preferencias del usuario. Se usara la por defecto", (Throwable) e);
        }
        if (certificateLineViewValueOf == null) {
            certificateLineViewValueOf = CertificateLineView.PERSONAL;
        }
        return certificateLineViewValueOf;
    }

    void savePreferredCertificateView() {
        try {
            Preferences preferencesUserNodeForPackage = Preferences.userNodeForPackage(CertificateSelectionPanel.class);
            if (this.certLineView != null) {
                preferencesUserNodeForPackage.put(PREFERENCE_CERT_VIEW, this.certLineView.name());
            } else {
                preferencesUserNodeForPackage.remove(PREFERENCE_CERT_VIEW);
            }
            preferencesUserNodeForPackage.sync();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se pudo guardar la vista utilizada en las preferencias del usuario", (Throwable) e);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/certificateselection/CertificateSelectionPanel$ChangeViewActionListener.class */
    private static class ChangeViewActionListener implements ActionListener {
        private final CertificateSelectionPanel panel;
        private final CertificateSelectionDialog dialog;
        private final CertificateLineView view;

        public ChangeViewActionListener(CertificateSelectionPanel certificateSelectionPanel, CertificateSelectionDialog certificateSelectionDialog, CertificateLineView certificateLineView) {
            this.panel = certificateSelectionPanel;
            this.dialog = certificateSelectionDialog;
            this.view = certificateLineView;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            UtilActions.doChangeView(this.panel.getCertificateBeans(), this.view, this.dialog, this.panel);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/certificateselection/CertificateSelectionPanel$ChangeKeyStoreActionListener.class */
    private static class ChangeKeyStoreActionListener implements ActionListener {
        private final CertificateSelectionPanel panel;
        private final CertificateSelectionDialog dialog;
        private final int keyStoreType;
        private final String keyStoreName;
        private final String ksLibPath;

        public ChangeKeyStoreActionListener(CertificateSelectionPanel certificateSelectionPanel, CertificateSelectionDialog certificateSelectionDialog, int i) {
            this.panel = certificateSelectionPanel;
            this.dialog = certificateSelectionDialog;
            this.keyStoreType = i;
            this.keyStoreName = null;
            this.ksLibPath = null;
        }

        public ChangeKeyStoreActionListener(CertificateSelectionPanel certificateSelectionPanel, CertificateSelectionDialog certificateSelectionDialog, int i, String str, String str2) {
            this.panel = certificateSelectionPanel;
            this.dialog = certificateSelectionDialog;
            this.keyStoreType = i;
            this.keyStoreName = str;
            this.ksLibPath = str2;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            UtilActions.doChangeKeyStore(this.keyStoreType, this.dialog, this.panel, this.keyStoreName, this.ksLibPath);
        }
    }
}
