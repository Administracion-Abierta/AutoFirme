package es.gob.afirma.standalone.ui.pdf;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.signers.pades.IncorrectPageException;
import es.gob.afirma.signers.pades.PdfUtil;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirma;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/SignPdfUiPanel.class */
final class SignPdfUiPanel extends JPanel implements PageLabel.PageLabelListener, KeyListener, FocusListener, ActionListener, DocumentListener {
    private static final long serialVersionUID = 8109653789776305491L;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final int PREFERRED_WIDTH = 470;
    private static final int PREFERRED_HEIGHT = 720;
    private static final int PAGEPANEL_PREFERRED_WIDTH = 466;
    private static final int PAGEPANEL_PREFERRED_HEIGHT = 400;
    private final SignPdfUiPanelListener listener;
    private JPanel pagePanel;
    private List<BufferedImage> pdfPages;
    private final boolean isSignPdf;
    private final boolean isMassiveSign;
    private BufferedImage appendPage;
    private final List<Dimension> pdfPageSizes;
    private final Properties initialExtraParams;
    private PageLabel pageLabel;
    private final PdfDocument pdfDocument;
    private Properties extraParamsForLocation = null;
    private int currentPage = 1;
    private int currentScale = 100;
    private final JButton okButton = new JButton(SignPdfUiMessages.getString("SignPdfUiPanel.0"));
    private final JTextField posX = new JTextField(4);
    private final JTextField posY = new JTextField(4);
    private final JTextField width = new JTextField(4);
    private final JTextField height = new JTextField(4);
    private final JLabel indexLabel = new JLabel();
    final JButton firstPageButton = new JButton("<<");
    final JButton previousPageButton = new JButton("<");
    final JButton nextPageButton = new JButton(">");
    final JButton lastPageButton = new JButton(">>");
    final JRadioButton actualPageRadioBtn = new JRadioButton(SignPdfUiMessages.getString("SignPdfUiPanel.23"));
    final JRadioButton allPagesRadioBtn = new JRadioButton(SignPdfUiMessages.getString("SignPdfUiPanel.24"));
    final JRadioButton selectPagesRadioBtn = new JRadioButton(SignPdfUiMessages.getString("SignPdfUiPanel.25"));
    final JTextField selectionPagesRange = new JTextField();
    private int pressButton = 0;

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/SignPdfUiPanel$SignPdfUiPanelListener.class */
    interface SignPdfUiPanelListener {
        void positionSelected(Properties properties);

        void positionCancelled();

        void nextPanel(Properties properties, BufferedImage bufferedImage);
    }

    Properties getExtraParamsForLocation() {
        if (this.extraParamsForLocation != null) {
            return (Properties) this.extraParamsForLocation.clone();
        }
        return null;
    }

    int getCurrentPage() {
        return this.currentPage;
    }

    SignPdfUiPanelListener getListener() {
        return this.listener;
    }

    SignPdfUiPanel(boolean z, boolean z2, List<BufferedImage> list, List<Dimension> list2, byte[] bArr, Properties properties, SignPdfUiPanelListener signPdfUiPanelListener) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("La lista de paginas no puede ser nula ni vacia");
        }
        if (list2 == null || list.size() != list2.size()) {
            throw new IllegalArgumentException("Las dimensiones de las paginas tienen que corresponderse con la lista de paginas proporcionada");
        }
        if (signPdfUiPanelListener == null) {
            throw new IllegalArgumentException("La clase a la que notificar la seleccion no puede ser nula");
        }
        this.pdfPages = list;
        this.pdfPageSizes = list2;
        this.listener = signPdfUiPanelListener;
        this.isSignPdf = z;
        this.isMassiveSign = z2;
        this.initialExtraParams = properties == null ? new Properties() : properties;
        this.pdfDocument = new PdfDocument();
        this.pdfDocument.setBytesPdf(bArr);
        createUI();
    }

    public void setPdfPages(List<BufferedImage> list) {
        this.pdfPages = list;
    }

    public List<BufferedImage> getPdfPages() {
        return this.pdfPages;
    }

    private void setProperties(Properties properties) {
        this.extraParamsForLocation = properties != null ? (Properties) properties.clone() : null;
    }

    private PageLabel createPageLabel(BufferedImage bufferedImage, PageLabel.PageLabelListener pageLabelListener, KeyListener keyListener, Component component, Dimension dimension) {
        float f;
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        Dimension preferredSize = component.getPreferredSize();
        while (true) {
            if (width <= preferredSize.width && height <= preferredSize.height) {
                break;
            }
            width = (int) Math.round(((double) width) * 0.9d);
            height = (int) Math.round(((double) height) * 0.9d);
        }
        if (bufferedImage.getWidth() <= bufferedImage.getHeight() && dimension.width <= dimension.height) {
            f = dimension.height / height;
        } else {
            f = dimension.width / width;
        }
        float width2 = width / bufferedImage.getWidth();
        this.currentScale = Math.round(100.0f * width2);
        int iRound = Math.round(bufferedImage.getWidth() * width2);
        int iRound2 = Math.round(bufferedImage.getHeight() * width2);
        PageLabel pageLabel = new PageLabel(bufferedImage.getScaledInstance(iRound, iRound2, 4), iRound, iRound2, pageLabelListener, f);
        pageLabel.addKeyListener(keyListener);
        pageLabel.setFocusable(false);
        pageLabel.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPanel.6"));
        return pageLabel;
    }

    void createUI() {
        addKeyListener(this);
        getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPanel.1"));
        setPreferredSize(new Dimension(PREFERRED_WIDTH, 720));
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridy = 0;
        Component jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        TitledBorder titledBorderCreateTitledBorder = BorderFactory.createTitledBorder(SignPdfUiMessages.getString("SignPdfUiPanel.7"));
        titledBorderCreateTitledBorder.setTitleFont(getFont().deriveFont(0));
        jPanel.setBorder(titledBorderCreateTitledBorder);
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = 2;
        gridBagConstraints2.anchor = 19;
        gridBagConstraints2.weightx = 1.0d;
        gridBagConstraints2.weighty = 1.0d;
        gridBagConstraints2.gridy = 0;
        jPanel.add(createMessageLabel(), gridBagConstraints2);
        gridBagConstraints2.gridy++;
        jPanel.add(createCoordenatesPanel(), gridBagConstraints2);
        gridBagConstraints2.gridy++;
        gridBagConstraints2.ipady = 3;
        gridBagConstraints2.fill = 1;
        jPanel.add(createPagePanel(), gridBagConstraints2);
        gridBagConstraints2.ipady = 3;
        gridBagConstraints2.gridy++;
        gridBagConstraints2.anchor = 20;
        gridBagConstraints2.fill = 2;
        jPanel.add(createPaginationPanel(), gridBagConstraints2);
        add(jPanel, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.weighty = 0.0d;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = 1;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
        Component jPanel2 = new JPanel();
        jPanel2.setLayout(new GridBagLayout());
        TitledBorder titledBorderCreateTitledBorder2 = BorderFactory.createTitledBorder(SignPdfUiMessages.getString("SignPdfUiPanel.22"));
        titledBorderCreateTitledBorder2.setTitleFont(getFont().deriveFont(0));
        jPanel2.setBorder(titledBorderCreateTitledBorder2);
        ButtonGroup buttonGroup = new ButtonGroup();
        this.actualPageRadioBtn.addActionListener(actionEvent -> {
            this.selectionPagesRange.setEnabled(false);
        });
        this.allPagesRadioBtn.addActionListener(actionEvent2 -> {
            this.selectionPagesRange.setEnabled(false);
        });
        this.selectPagesRadioBtn.addActionListener(actionEvent3 -> {
            this.selectionPagesRange.setEnabled(true);
        });
        this.actualPageRadioBtn.setSelected(true);
        this.selectionPagesRange.setEnabled(false);
        buttonGroup.add(this.actualPageRadioBtn);
        buttonGroup.add(this.allPagesRadioBtn);
        buttonGroup.add(this.selectPagesRadioBtn);
        jPanel2.add(this.actualPageRadioBtn, gridBagConstraints3);
        gridBagConstraints3.gridy++;
        jPanel2.add(this.allPagesRadioBtn, gridBagConstraints3);
        gridBagConstraints3.gridy++;
        jPanel2.add(this.selectPagesRadioBtn, gridBagConstraints3);
        gridBagConstraints3.gridx++;
        gridBagConstraints3.weightx = 1.0d;
        gridBagConstraints3.insets = new Insets(0, 5, 0, 0);
        jPanel2.add(this.selectionPagesRange, gridBagConstraints3);
        gridBagConstraints3.gridx++;
        gridBagConstraints3.weightx = 0.0d;
        gridBagConstraints3.ipadx = 6;
        gridBagConstraints3.ipady = 6;
        jPanel2.add(createHelpButton(), gridBagConstraints3);
        add(jPanel2, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.weighty = 0.0d;
        add(createButtonsPanel(), gridBagConstraints);
        enableButtons();
    }

    private JPanel createPagePanel() {
        this.pagePanel = new JPanel();
        this.pagePanel.setLayout(new GridBagLayout());
        this.pagePanel.setPreferredSize(new Dimension(PAGEPANEL_PREFERRED_WIDTH, 400));
        this.pageLabel = createPageLabel(this.pdfPages.get(0), this, this, this.pagePanel, this.pdfPageSizes.get(0));
        this.pagePanel.add(this.pageLabel);
        return this.pagePanel;
    }

    private JPanel createPaginationPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout(1, 0, 0));
        this.indexLabel.setText(SignPdfUiMessages.getString("SignPdfUiPanel.5", Integer.toString(getCurrentPage()), Integer.toString(this.pdfPages.size()), Integer.toString(this.currentScale)));
        this.indexLabel.setHorizontalAlignment(0);
        this.indexLabel.setFocusable(false);
        this.indexLabel.addKeyListener(this);
        this.firstPageButton.addActionListener(this);
        this.firstPageButton.addKeyListener(this);
        this.previousPageButton.addActionListener(this);
        this.previousPageButton.addKeyListener(this);
        this.nextPageButton.addActionListener(this);
        this.nextPageButton.addKeyListener(this);
        this.lastPageButton.addActionListener(this);
        this.lastPageButton.addKeyListener(this);
        jPanel.add(this.firstPageButton);
        jPanel.add(this.previousPageButton);
        jPanel.add(this.indexLabel);
        jPanel.add(this.nextPageButton);
        jPanel.add(this.lastPageButton);
        return jPanel;
    }

    private JLabel createMessageLabel() {
        JLabel jLabel;
        new JLabel();
        if (!this.isMassiveSign) {
            jLabel = new JLabel(SignPdfUiMessages.getString("SignPdfUiPanel.14"));
            jLabel.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPanel.15"));
        } else {
            jLabel = new JLabel(SignPdfUiMessages.getString("SignPdfUiPanel.20"));
            jLabel.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPanel.21"));
        }
        return jLabel;
    }

    private JPanel createCoordenatesPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout(1));
        NaturalNumbersDocFilter naturalNumbersDocFilter = new NaturalNumbersDocFilter();
        JLabel jLabel = new JLabel(SignPdfUiMessages.getString("SignPdfUiPanel.8"));
        jLabel.setLabelFor(this.posX);
        jPanel.add(jLabel);
        this.posX.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPanel.16"));
        this.posX.addKeyListener(this);
        this.posX.addFocusListener(this);
        PlainDocument document = this.posX.getDocument();
        document.setDocumentFilter(naturalNumbersDocFilter);
        document.addDocumentListener(this);
        jPanel.add(this.posX);
        JLabel jLabel2 = new JLabel(SignPdfUiMessages.getString("SignPdfUiPanel.9"));
        jLabel2.setLabelFor(this.posY);
        jPanel.add(jLabel2);
        this.posY.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPanel.17"));
        this.posY.addKeyListener(this);
        this.posY.addFocusListener(this);
        PlainDocument document2 = this.posY.getDocument();
        document2.setDocumentFilter(naturalNumbersDocFilter);
        document2.addDocumentListener(this);
        jPanel.add(this.posY);
        JLabel jLabel3 = new JLabel(SignPdfUiMessages.getString("SignPdfUiPanel.12"));
        jLabel3.setLabelFor(this.width);
        jPanel.add(jLabel3);
        this.width.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPanel.18"));
        this.width.addKeyListener(this);
        this.width.addFocusListener(this);
        PlainDocument document3 = this.width.getDocument();
        document3.setDocumentFilter(naturalNumbersDocFilter);
        document3.addDocumentListener(this);
        jPanel.add(this.width);
        JLabel jLabel4 = new JLabel(SignPdfUiMessages.getString("SignPdfUiPanel.13"));
        jLabel4.setLabelFor(this.height);
        jPanel.add(jLabel4);
        this.height.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPanel.19"));
        this.height.addKeyListener(this);
        this.height.addFocusListener(this);
        PlainDocument document4 = this.height.getDocument();
        document4.setDocumentFilter(naturalNumbersDocFilter);
        document4.addDocumentListener(this);
        jPanel.add(this.height);
        return jPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout(2));
        this.okButton.setEnabled(false);
        this.okButton.setMnemonic('A');
        this.okButton.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPanel.2"));
        this.okButton.addActionListener(actionEvent -> {
            Properties properties = this.initialExtraParams != null ? this.initialExtraParams : new Properties();
            if (this.allPagesRadioBtn.isSelected()) {
                properties.put(PdfExtraParams.SIGNATURE_PAGES, PdfExtraParams.PAGES_TO_CHECK_PSA_VALUE_ALL);
            } else if (this.selectPagesRadioBtn.isSelected()) {
                if (!PdfUtil.checkPagesRangeInputFormat(this.selectionPagesRange.getText())) {
                    AOUIFactory.showMessageDialog(this, SignPdfUiMessages.getString("SignPdfUiPanel.26"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0);
                    return;
                }
                properties.put(PdfExtraParams.SIGNATURE_PAGES, this.selectionPagesRange.getText());
            } else {
                properties.put(PdfExtraParams.SIGNATURE_PAGES, Integer.toString(getCurrentPage()));
            }
            if (getCurrentPage() > getPdfPages().size()) {
                properties.put(PdfExtraParams.SIGNATURE_PAGES, ((String) properties.get(PdfExtraParams.SIGNATURE_PAGES)) + ",append");
            }
            properties.putAll(getExtraParamsForLocation());
            BufferedImage fragmentImage = getFragmentImage(properties);
            if (fragmentImage != null) {
                getListener().nextPanel(properties, fragmentImage);
            }
        });
        this.okButton.addKeyListener(this);
        jPanel.add(this.okButton);
        JButton jButton = new JButton(SignPdfUiMessages.getString("SignPdfUiPanel.3"));
        jButton.setMnemonic('C');
        jButton.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPanel.4"));
        jButton.addActionListener(actionEvent2 -> {
            getListener().positionCancelled();
        });
        jButton.addKeyListener(this);
        if (Platform.OS.MACOSX.equals(Platform.getOS())) {
            jPanel.add(jButton);
            jPanel.add(this.okButton);
        } else {
            jPanel.add(this.okButton);
            jPanel.add(jButton);
        }
        return jPanel;
    }

    private static JButton createHelpButton() {
        URL resource;
        if (LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            resource = SignPdfUiPanel.class.getResource("/resources/toolbar/ic_help_white_18dp.png");
        } else {
            resource = SignPdfUiPanel.class.getResource("/resources/toolbar/ic_help_black_18dp.png");
        }
        final JButton jButton = new JButton(new ImageIcon(resource, SignPdfUiMessages.getString("SignPdfUiPanel.27")));
        jButton.setBorder(BorderFactory.createEmptyBorder());
        jButton.setRolloverEnabled(false);
        jButton.setCursor(Cursor.getPredefinedCursor(12));
        jButton.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPanel.27"));
        jButton.addActionListener(actionEvent -> {
            SimpleAfirma.showHelp("pgs/VentanaPdfVisible.html");
        });
        jButton.addFocusListener(new FocusListener() { // from class: es.gob.afirma.standalone.ui.pdf.SignPdfUiPanel.1
            public void focusGained(FocusEvent focusEvent) {
                if (LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                    jButton.setIcon(new ImageIcon(SignPdfUiPanel.class.getResource("/resources/toolbar/ic_help_black_18dp.png"), SignPdfUiMessages.getString("SignPdfUiPanel.27")));
                }
            }

            public void focusLost(FocusEvent focusEvent) {
                if (LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                    jButton.setIcon(new ImageIcon(SignPdfUiPanel.class.getResource("/resources/toolbar/ic_help_white_18dp.png"), SignPdfUiMessages.getString("SignPdfUiPanel.27")));
                }
            }
        });
        return jButton;
    }

    BufferedImage getFragmentImage(Properties properties) {
        BufferedImage bufferedImage;
        int iIntValue = 1;
        BufferedImage bufferedImage2 = null;
        String[] strArrSplit = properties.getProperty(PdfExtraParams.SIGNATURE_PAGES).split(",");
        if (strArrSplit.length > 0) {
            for (String str : strArrSplit) {
                if ("append".equals(str)) {
                    bufferedImage = this.appendPage;
                } else if (PdfExtraParams.PAGES_TO_CHECK_PSA_VALUE_ALL.equals(str)) {
                    bufferedImage = this.pdfPages.get(0);
                } else {
                    ArrayList arrayList = new ArrayList();
                    try {
                        PdfUtil.getPagesRange(str, this.pdfPages.size(), arrayList);
                        if (arrayList.isEmpty()) {
                            throw new IncorrectPageException("Numero de pagina no valido");
                        }
                        iIntValue = ((Integer) arrayList.get(0)).intValue();
                        bufferedImage2 = this.pdfPages.get(iIntValue - 1);
                        if (bufferedImage2 != null) {
                            break;
                        }
                        try {
                            preLoadNecessaryPage(iIntValue);
                            bufferedImage2 = this.pdfPages.get(iIntValue - 1);
                            break;
                        } catch (IOException e) {
                            AOUIFactory.showMessageDialog(this, SignPdfUiMessages.getString("SignPdfUiPanel.26"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0);
                            return null;
                        }
                    } catch (IncorrectPageException e2) {
                        iIntValue = 1;
                        bufferedImage2 = this.pdfPages.get(0);
                    }
                }
                bufferedImage2 = bufferedImage;
            }
        }
        int width = (int) this.pdfPageSizes.get(iIntValue - 1).getWidth();
        int height = (int) this.pdfPageSizes.get(iIntValue - 1).getHeight();
        BufferedImage bufferedImage3 = new BufferedImage(width, height, this.pdfPages.get(iIntValue - 1).getType());
        Graphics2D graphics2DCreateGraphics = bufferedImage3.createGraphics();
        graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2DCreateGraphics.drawImage(bufferedImage2, 0, 0, width, height, (ImageObserver) null);
        graphics2DCreateGraphics.dispose();
        int iMax = Math.max(0, Integer.parseInt(properties.getProperty(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_UPPER_RIGHTX)) - 4);
        int iMax2 = Math.max(0, Integer.parseInt(properties.getProperty(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_UPPER_RIGHTY)));
        int iMax3 = Math.max(0, Integer.parseInt(properties.getProperty(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_LOWER_LEFTX)));
        return bufferedImage3.getSubimage(iMax3, Math.max(0, height - iMax2), Math.max(iMax - iMax3, 1), Math.max(iMax2 - Math.max(0, Integer.parseInt(properties.getProperty(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_LOWER_LEFTY))), 1));
    }

    @Override // es.gob.afirma.standalone.ui.pdf.PageLabel.PageLabelListener
    public void setX(String str) {
        this.posX.setText(str);
    }

    @Override // es.gob.afirma.standalone.ui.pdf.PageLabel.PageLabelListener
    public void setY(String str) {
        this.posY.setText(str);
    }

    @Override // es.gob.afirma.standalone.ui.pdf.PageLabel.PageLabelListener
    public void setWidth(String str) {
        this.width.setText(str);
    }

    @Override // es.gob.afirma.standalone.ui.pdf.PageLabel.PageLabelListener
    public void setHeight(String str) {
        this.height.setText(str);
    }

    public void keyTyped(KeyEvent keyEvent) {
    }

    public void keyReleased(KeyEvent keyEvent) {
    }

    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent != null) {
            if (!(keyEvent.getComponent() instanceof JTextComponent)) {
                if (keyEvent.getKeyCode() == 37 && getCurrentPage() > 1) {
                    this.currentPage--;
                    changePage();
                } else if (keyEvent.getKeyCode() == 39 && getCurrentPage() < this.pdfPages.size()) {
                    this.currentPage++;
                    changePage();
                } else if (keyEvent.getKeyCode() == 39 && getCurrentPage() == this.pdfPages.size() && !this.isSignPdf) {
                    this.currentPage++;
                    appendPage();
                }
            }
            if (keyEvent.getKeyCode() == 27) {
                this.currentPage++;
                getListener().positionCancelled();
            }
        }
    }

    public void focusGained(FocusEvent focusEvent) {
        if (focusEvent.getSource() instanceof JTextComponent) {
            ((JTextComponent) focusEvent.getSource()).selectAll();
        }
    }

    public void focusLost(FocusEvent focusEvent) {
        if (focusEvent.getSource() instanceof JTextComponent) {
            ((JTextComponent) focusEvent.getSource()).select(0, 0);
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent != null) {
            if (actionEvent.getSource() == this.firstPageButton && getCurrentPage() > 1) {
                this.currentPage = 1;
                changePage();
                return;
            }
            if (actionEvent.getSource() == this.previousPageButton && getCurrentPage() > 1) {
                this.currentPage--;
                if (this.pressButton > 0) {
                    try {
                        preLoadImages(getCurrentPage() + 1, this.currentPage);
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Error durante la carga de las miniaturas anteriores: " + e, (Throwable) e);
                        this.currentPage++;
                        AOUIFactory.showErrorMessage(SignPdfUiMessages.getString("SignPdfDialog.5"), SignPdfUiMessages.getString("SignPdfDialog.1"), 0, e);
                    }
                }
                if (this.pdfPages.get(getCurrentPage() - 1) != null) {
                    changePage();
                    this.pressButton++;
                    return;
                }
                return;
            }
            if (actionEvent.getSource() == this.nextPageButton && getCurrentPage() < this.pdfPages.size()) {
                this.currentPage++;
                if (this.pressButton > 0) {
                    try {
                        preLoadImages(getCurrentPage() - 1, this.currentPage);
                    } catch (IOException e2) {
                        LOGGER.log(Level.SEVERE, "Error durante la carga de las miniaturas siguientes: " + e2, (Throwable) e2);
                        this.currentPage--;
                        AOUIFactory.showErrorMessage(SignPdfUiMessages.getString("SignPdfDialog.5"), SignPdfUiMessages.getString("SignPdfDialog.1"), 0, e2);
                    }
                }
                if (this.pdfPages.get(getCurrentPage() - 1) != null) {
                    changePage();
                    this.pressButton++;
                    return;
                }
                return;
            }
            if (actionEvent.getSource() == this.lastPageButton && getCurrentPage() < this.pdfPages.size()) {
                this.currentPage = this.pdfPages.size();
                changePage();
            } else if (actionEvent.getSource() == this.nextPageButton && getCurrentPage() == this.pdfPages.size() && !this.isSignPdf) {
                this.currentPage++;
                appendPage();
            }
        }
    }

    private void appendPage() {
        if (0 == AOUIFactory.showConfirmDialog(this, SignPdfUiMessages.getString("SignPdfUiPanel.11"), SignPdfUiMessages.getString("SignPdfUiPanel.10"), AOUIFactory.YES_NO_OPTION, AOUIFactory.WARNING_MESSAGE)) {
            BufferedImage bufferedImage = new BufferedImage((int) this.pdfPageSizes.get(0).getWidth(), (int) this.pdfPageSizes.get(0).getHeight(), this.pdfPages.get(0).getType());
            Graphics2D graphics2DCreateGraphics = bufferedImage.createGraphics();
            graphics2DCreateGraphics.setPaint(Color.WHITE);
            graphics2DCreateGraphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
            graphics2DCreateGraphics.dispose();
            this.pagePanel.remove(this.pageLabel);
            this.posX.setText("");
            this.posY.setText("");
            this.width.setText("");
            this.height.setText("");
            this.pageLabel = createPageLabel(bufferedImage, this, this, this.pagePanel, this.pdfPageSizes.get(0));
            this.indexLabel.setText(SignPdfUiMessages.getString("SignPdfUiPanel.5", Integer.toString(getCurrentPage()), Integer.toString(this.pdfPages.size()), Integer.toString(this.currentScale)));
            this.nextPageButton.setEnabled(false);
            this.previousPageButton.setEnabled(true);
            this.appendPage = bufferedImage;
            this.pagePanel.add(this.pageLabel);
            this.pagePanel.repaint();
            return;
        }
        this.currentPage--;
    }

    private void changePage() {
        enableButtons();
        this.pagePanel.remove(this.pageLabel);
        this.posX.setText("");
        this.posY.setText("");
        this.width.setText("");
        this.height.setText("");
        this.pageLabel = createPageLabel(this.pdfPages.get(getCurrentPage() - 1), this, this, this.pagePanel, this.pdfPageSizes.get(getCurrentPage() - 1));
        this.indexLabel.setText(SignPdfUiMessages.getString("SignPdfUiPanel.5", Integer.toString(getCurrentPage()), Integer.toString(this.pdfPages.size()), Integer.toString(this.currentScale)));
        this.pagePanel.add(this.pageLabel);
        this.pagePanel.repaint();
    }

    private void enableButtons() {
        if (this.pdfPages.size() == 1) {
            this.lastPageButton.setEnabled(false);
            this.firstPageButton.setEnabled(false);
            if (this.isSignPdf) {
                this.nextPageButton.setEnabled(false);
            } else {
                this.nextPageButton.setEnabled(true);
            }
            this.previousPageButton.setEnabled(false);
            return;
        }
        if (getCurrentPage() == 1) {
            this.firstPageButton.setEnabled(false);
            this.previousPageButton.setEnabled(false);
            this.nextPageButton.setEnabled(true);
            this.lastPageButton.setEnabled(true);
            return;
        }
        if (getCurrentPage() == this.pdfPages.size()) {
            this.lastPageButton.setEnabled(false);
            this.firstPageButton.setEnabled(true);
            if (this.isSignPdf) {
                this.nextPageButton.setEnabled(false);
            } else {
                this.nextPageButton.setEnabled(true);
            }
            this.previousPageButton.setEnabled(true);
            return;
        }
        this.firstPageButton.setEnabled(true);
        this.previousPageButton.setEnabled(true);
        this.nextPageButton.setEnabled(true);
        this.lastPageButton.setEnabled(true);
    }

    private void preLoadImages(int i, int i2) throws IOException {
        int i3;
        if (i > i2) {
            i3 = i2 - 2;
        } else {
            i3 = i2 + 1;
        }
        if (i3 < 0 || i3 >= this.pdfPages.size()) {
            return;
        }
        preLoadNecessaryPage(i3);
    }

    private void preLoadNecessaryPage(int i) throws IOException {
        if (this.pdfPages.get(i) == null) {
            try {
                this.pdfDocument.loadNewPages(this.pdfPages, i - 1);
            } catch (Exception | OutOfMemoryError e) {
                throw new IOException("No se ha podido cargar la previsualizacion de la pagina", e);
            }
        }
    }

    public void changedUpdate(DocumentEvent documentEvent) {
        updateArea();
    }

    public void insertUpdate(DocumentEvent documentEvent) {
        updateArea();
    }

    public void removeUpdate(DocumentEvent documentEvent) {
        updateArea();
    }

    private void updateArea() {
        float scale = this.pageLabel.getScale();
        Rectangle rectangle = null;
        int i = (int) ((this.posX.getText().isEmpty() ? 0 : Integer.parseInt(this.posX.getText())) * scale);
        int i2 = (int) ((this.posY.getText().isEmpty() ? 0 : Integer.parseInt(this.posY.getText())) * scale);
        int i3 = (int) ((this.width.getText().isEmpty() ? 0 : Integer.parseInt(this.width.getText())) * scale);
        int i4 = (int) ((this.height.getText().isEmpty() ? 0 : Integer.parseInt(this.height.getText())) * scale);
        if (i3 > 0 && i4 > 0) {
            rectangle = new Rectangle(i, i2, i3, i4);
            setProperties(toPdfPosition(rectangle));
        } else {
            setProperties(null);
        }
        this.okButton.setEnabled(rectangle != null);
        this.pageLabel.setSelectionBounds(rectangle);
        this.pageLabel.repaint();
    }

    private Properties toPdfPosition(Rectangle rectangle) {
        Dimension dimension;
        if (getCurrentPage() == 1) {
            dimension = this.pdfPageSizes.get(getCurrentPage() - 1);
        } else {
            dimension = this.pdfPageSizes.get(getCurrentPage() - 2);
        }
        if (rectangle.x > dimension.width || rectangle.y > dimension.height) {
            return null;
        }
        if (rectangle.x + rectangle.width > dimension.width) {
            rectangle.width = dimension.width - rectangle.x;
        }
        if (rectangle.y + rectangle.height > dimension.height) {
            rectangle.height = dimension.height - rectangle.y;
        }
        int height = rectangle.height + rectangle.y > this.pageLabel.getHeight() ? this.pageLabel.getHeight() - rectangle.y : rectangle.height;
        int width = rectangle.width + rectangle.x > this.pageLabel.getWidth() ? this.pageLabel.getWidth() - rectangle.x : rectangle.width;
        Properties properties = new Properties();
        properties.put(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_LOWER_LEFTX, Integer.toString(Math.round(rectangle.x * this.pageLabel.getScale())));
        properties.put(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_LOWER_LEFTY, Integer.toString(Math.round(((this.pageLabel.getHeight() - rectangle.y) - height) * this.pageLabel.getScale())));
        properties.put(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_UPPER_RIGHTX, Integer.toString(Math.round((rectangle.x + width) * this.pageLabel.getScale())));
        properties.put(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_UPPER_RIGHTY, Integer.toString(Math.round((this.pageLabel.getHeight() - rectangle.y) * this.pageLabel.getScale())));
        return properties;
    }
}
