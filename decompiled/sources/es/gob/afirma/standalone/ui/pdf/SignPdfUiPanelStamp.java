package es.gob.afirma.standalone.ui.pdf;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/SignPdfUiPanelStamp.class */
final class SignPdfUiPanelStamp extends JPanel implements KeyListener, FocusListener, PageLabel.PageLabelListener, ActionListener, DocumentListener {
    private static final long serialVersionUID = -4465164058611491582L;
    private static final int PREFERRED_WIDTH = 470;
    private static final int PREFERRED_HEIGHT = 630;
    private static final int PAGEPANEL_PREFERRED_WIDTH = 466;
    private static final int PAGEPANEL_PREFERRED_HEIGHT = 410;
    final Properties extraParams;
    private final SignPdfUiPanel.SignPdfUiPanelListener listener;
    private JPanel pagePanel;
    private final List<BufferedImage> pdfPages;
    private final List<Dimension> pdfPageSizes;
    private PageLabel pageLabel;
    private JDialog dialogParent;
    private final PdfDocument pdfDocument;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    static final String[] IMAGE_EXT = {"jpg", "jpeg", "png", "gif", "bmp"};
    final JTextField fileText = new JTextField();
    private Properties extraParamsForLocation = null;
    private int currentPage = 1;
    private int currentScale = 100;
    boolean locationSelected = false;
    final JButton okButton = new JButton(SignPdfUiMessages.getString("SignPdfUiStamp.8"));
    private final JTextField posX = new JTextField(4);
    private final JTextField posY = new JTextField(4);
    private final JTextField width = new JTextField(4);
    private final JTextField height = new JTextField(4);
    private final JLabel indexLabel = new JLabel();
    final JButton firstPageButton = new JButton("<<");
    final JButton previousPageButton = new JButton("<");
    final JButton nextPageButton = new JButton(">");
    final JButton lastPageButton = new JButton(">>");
    final JCheckBox allPagesCheckbox = new JCheckBox(SignPdfUiMessages.getString("SignPdfUiStamp.7"));
    private int pressButton = 0;

    private void setProperties(Properties properties) {
        this.extraParamsForLocation = properties != null ? (Properties) properties.clone() : null;
    }

    int getCurrentPage() {
        return this.currentPage;
    }

    SignPdfUiPanel.SignPdfUiPanelListener getListener() {
        return this.listener;
    }

    SignPdfUiPanelStamp(List<BufferedImage> list, List<Dimension> list2, byte[] bArr, SignPdfUiPanel.SignPdfUiPanelListener signPdfUiPanelListener, Properties properties) {
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
        this.extraParams = properties;
        this.pdfDocument = new PdfDocument();
        this.pdfDocument.setBytesPdf(bArr);
        createUI();
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
        pageLabel.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiStamp.5"));
        return pageLabel;
    }

    void createUI() {
        addKeyListener(this);
        getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiStamp.17"));
        setPreferredSize(new Dimension(PREFERRED_WIDTH, 630));
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(10, 10, 0, 10);
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.gridy = 0;
        add(createFilePanel(), gridBagConstraints);
        gridBagConstraints.fill = 1;
        gridBagConstraints.insets = new Insets(5, 10, 10, 10);
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridy++;
        Component jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        TitledBorder titledBorderCreateTitledBorder = BorderFactory.createTitledBorder(SignPdfUiMessages.getString("SignPdfUiStamp.2"));
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
        add(createButtonsPanel(), gridBagConstraints);
        enableButtons();
    }

    private JPanel createPagePanel() {
        this.pagePanel = new JPanel();
        this.pagePanel.setLayout(new GridBagLayout());
        this.pagePanel.setPreferredSize(new Dimension(PAGEPANEL_PREFERRED_WIDTH, PAGEPANEL_PREFERRED_HEIGHT));
        this.pageLabel = createPageLabel(this.pdfPages.get(0), this, this, this.pagePanel, this.pdfPageSizes.get(0));
        this.pagePanel.add(this.pageLabel);
        return this.pagePanel;
    }

    private JPanel createPaginationPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, 3));
        jPanel.setAlignmentY(0.5f);
        JPanel jPanel2 = new JPanel();
        jPanel2.setLayout(new FlowLayout(1));
        JPanel jPanel3 = new JPanel();
        jPanel3.setLayout(new FlowLayout(0));
        this.indexLabel.setText(SignPdfUiMessages.getString("SignPdfUiStamp.6", Integer.toString(getCurrentPage()), Integer.toString(this.pdfPages.size()), Integer.toString(this.currentScale)));
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
        jPanel2.add(this.firstPageButton);
        jPanel2.add(this.previousPageButton);
        jPanel2.add(this.indexLabel);
        jPanel2.add(this.nextPageButton);
        jPanel2.add(this.lastPageButton);
        this.allPagesCheckbox.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == 1) {
                this.firstPageButton.setEnabled(false);
                this.previousPageButton.setEnabled(false);
                this.nextPageButton.setEnabled(false);
                this.lastPageButton.setEnabled(false);
                return;
            }
            enableButtons();
        });
        this.allPagesCheckbox.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiStamp.16"));
        jPanel3.add(this.allPagesCheckbox);
        jPanel.add(jPanel2);
        jPanel.add(jPanel3);
        return jPanel;
    }

    private static JLabel createMessageLabel() {
        JLabel jLabel = new JLabel(SignPdfUiMessages.getString("SignPdfUiStamp.26"));
        jLabel.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiStamp.27"));
        return jLabel;
    }

    private JPanel createCoordenatesPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout(1));
        NaturalNumbersDocFilter naturalNumbersDocFilter = new NaturalNumbersDocFilter();
        JLabel jLabel = new JLabel(SignPdfUiMessages.getString("SignPdfUiStamp.3"));
        jLabel.setLabelFor(this.posX);
        jPanel.add(jLabel);
        this.posX.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiStamp.22"));
        this.posX.addKeyListener(this);
        this.posX.addFocusListener(this);
        PlainDocument document = this.posX.getDocument();
        document.setDocumentFilter(naturalNumbersDocFilter);
        document.addDocumentListener(this);
        jPanel.add(this.posX);
        JLabel jLabel2 = new JLabel(SignPdfUiMessages.getString("SignPdfUiStamp.4"));
        jLabel2.setLabelFor(this.posY);
        jPanel.add(jLabel2);
        this.posY.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiStamp.23"));
        this.posY.addKeyListener(this);
        this.posY.addFocusListener(this);
        PlainDocument document2 = this.posY.getDocument();
        document2.setDocumentFilter(naturalNumbersDocFilter);
        document2.addDocumentListener(this);
        jPanel.add(this.posY);
        JLabel jLabel3 = new JLabel(SignPdfUiMessages.getString("SignPdfUiStamp.20"));
        jLabel3.setLabelFor(this.width);
        jPanel.add(jLabel3);
        this.width.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiStamp.24"));
        this.width.addKeyListener(this);
        this.width.addFocusListener(this);
        PlainDocument document3 = this.width.getDocument();
        document3.setDocumentFilter(naturalNumbersDocFilter);
        document3.addDocumentListener(this);
        jPanel.add(this.width);
        JLabel jLabel4 = new JLabel(SignPdfUiMessages.getString("SignPdfUiStamp.21"));
        jLabel4.setLabelFor(this.height);
        jPanel.add(jLabel4);
        this.height.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiStamp.25"));
        this.height.addKeyListener(this);
        this.height.addFocusListener(this);
        PlainDocument document4 = this.height.getDocument();
        document4.setDocumentFilter(naturalNumbersDocFilter);
        document4.addDocumentListener(this);
        jPanel.add(this.height);
        return jPanel;
    }

    private JPanel createFilePanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        this.fileText.setEditable(false);
        JButton jButton = new JButton(SignPdfUiMessages.getString("SignPdfUiStamp.18"));
        jButton.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiStamp.19"));
        jButton.addActionListener(actionEvent -> {
            InputStream inputStream;
            Throwable th;
            try {
                File[] loadFiles = AOUIFactory.getLoadFiles(SignPdfUiMessages.getString("SignPdfUiPreview.21"), null, null, IMAGE_EXT, SignPdfUiMessages.getString("SignPdfUiPreview.43"), false, false, null, this);
                try {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(loadFiles[0]);
                        Throwable th2 = null;
                        byte[] dataFromInputStream = AOUtil.getDataFromInputStream(fileInputStream);
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataFromInputStream);
                        Throwable th3 = null;
                        try {
                            try {
                                ImageIO.read(byteArrayInputStream);
                                if (byteArrayInputStream != null) {
                                    if (0 != 0) {
                                        try {
                                            byteArrayInputStream.close();
                                        } catch (Throwable th4) {
                                            th3.addSuppressed(th4);
                                        }
                                    } else {
                                        byteArrayInputStream.close();
                                    }
                                }
                                this.extraParams.put("image", Base64.getEncoder().encodeToString(dataFromInputStream));
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
                                this.fileText.setText(loadFiles[0].getAbsolutePath());
                                if (this.locationSelected) {
                                    this.okButton.setEnabled(true);
                                    this.okButton.requestFocusInWindow();
                                } else {
                                    this.okButton.setEnabled(false);
                                }
                            } catch (Throwable th6) {
                                th3 = th6;
                                throw th6;
                            }
                        } catch (Throwable th7) {
                            if (byteArrayInputStream != null) {
                                if (th3 != null) {
                                    try {
                                        byteArrayInputStream.close();
                                    } catch (Throwable th8) {
                                        th3.addSuppressed(th8);
                                    }
                                } else {
                                    byteArrayInputStream.close();
                                }
                            }
                            throw th7;
                        }
                    } catch (Throwable th9) {
                        if (inputStream != null) {
                            if (th != null) {
                                try {
                                    inputStream.close();
                                } catch (Throwable th10) {
                                    th.addSuppressed(th10);
                                }
                            } else {
                                inputStream.close();
                            }
                        }
                        throw th9;
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "No se ha podido cargar la imagen de rubrica", (Throwable) e);
                    AOUIFactory.showErrorMessage(getDialogParent(), SignPdfUiMessages.getString("SignPdfUiPreview.24"), SignPdfUiMessages.getString("SignPdfUiPreview.23"), 0, e);
                }
            } catch (AOCancelledOperationException e2) {
            }
        });
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel.add(jButton, gridBagConstraints);
        gridBagConstraints.gridx++;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        jPanel.add(this.fileText, gridBagConstraints);
        return jPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout(2));
        this.okButton.setEnabled(false);
        this.okButton.setMnemonic('A');
        this.okButton.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiStamp.9"));
        this.okButton.addActionListener(actionEvent -> {
            if (this.allPagesCheckbox.isSelected()) {
                this.extraParams.put(PdfExtraParams.IMAGE_PAGE, "0");
            } else {
                this.extraParams.put(PdfExtraParams.IMAGE_PAGE, Integer.toString(getCurrentPage()));
            }
            this.extraParams.put(PdfExtraParams.IMAGE_POSITION_ON_PAGE_LOWER_LEFTX, this.extraParamsForLocation.getProperty(PdfExtraParams.IMAGE_POSITION_ON_PAGE_LOWER_LEFTX));
            this.extraParams.put(PdfExtraParams.IMAGE_POSITION_ON_PAGE_LOWER_LEFTY, this.extraParamsForLocation.getProperty(PdfExtraParams.IMAGE_POSITION_ON_PAGE_LOWER_LEFTY));
            this.extraParams.put(PdfExtraParams.IMAGE_POSITION_ON_PAGE_UPPER_RIGHTX, this.extraParamsForLocation.getProperty(PdfExtraParams.IMAGE_POSITION_ON_PAGE_UPPER_RIGHTX));
            this.extraParams.put(PdfExtraParams.IMAGE_POSITION_ON_PAGE_UPPER_RIGHTY, this.extraParamsForLocation.getProperty(PdfExtraParams.IMAGE_POSITION_ON_PAGE_UPPER_RIGHTY));
            getListener().nextPanel(this.extraParams, null);
        });
        this.okButton.addKeyListener(this);
        jPanel.add(this.okButton);
        JButton jButton = new JButton(SignPdfUiMessages.getString("SignPdfUiStamp.10"));
        jButton.setMnemonic('C');
        jButton.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiStamp.11"));
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
            if (keyEvent.getKeyCode() == 37 && getCurrentPage() > 1) {
                this.currentPage--;
                changePage();
            } else if (keyEvent.getKeyCode() == 39 && getCurrentPage() < this.pdfPages.size()) {
                this.currentPage++;
                changePage();
            } else if (keyEvent.getKeyCode() == 27) {
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
                        AOUIFactory.showErrorMessage(SignPdfUiMessages.getString("SignPdfUiStamp.15"), SignPdfUiMessages.getString("SignPdfUiStamp.12"), 0, e);
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
                        AOUIFactory.showErrorMessage(SignPdfUiMessages.getString("SignPdfUiStamp.15"), SignPdfUiMessages.getString("SignPdfUiStamp.12"), 0, e2);
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
            }
        }
    }

    private void changePage() {
        enableButtons();
        this.pagePanel.remove(this.pageLabel);
        this.posX.setText("");
        this.posY.setText("");
        this.width.setText("");
        this.height.setText("");
        this.pageLabel = createPageLabel(this.pdfPages.get(getCurrentPage() - 1), this, this, this.pagePanel, this.pdfPageSizes.get(getCurrentPage() - 1));
        this.indexLabel.setText(SignPdfUiMessages.getString("SignPdfUiStamp.6", Integer.toString(getCurrentPage()), Integer.toString(this.pdfPages.size()), Integer.toString(this.currentScale)));
        this.pagePanel.add(this.pageLabel);
        this.pagePanel.repaint();
    }

    void enableButtons() {
        if (this.pdfPages.size() == 1) {
            this.firstPageButton.setEnabled(false);
            this.previousPageButton.setEnabled(false);
            this.nextPageButton.setEnabled(false);
            this.lastPageButton.setEnabled(false);
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
            this.firstPageButton.setEnabled(true);
            this.previousPageButton.setEnabled(true);
            this.nextPageButton.setEnabled(false);
            this.lastPageButton.setEnabled(false);
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
        if (i3 >= 0 && i3 < this.pdfPages.size() && this.pdfPages.get(i3) == null) {
            try {
                this.pdfDocument.loadNewPages(this.pdfPages, getCurrentPage() - 1);
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
        this.locationSelected = rectangle != null;
        this.okButton.setEnabled(this.locationSelected && !this.fileText.getText().isEmpty());
        this.pageLabel.setSelectionBounds(rectangle);
        this.pageLabel.repaint();
    }

    private Properties toPdfPosition(Rectangle rectangle) {
        Dimension dimension = this.pdfPageSizes.get(getCurrentPage() - 1);
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
        properties.put(PdfExtraParams.IMAGE_POSITION_ON_PAGE_LOWER_LEFTX, Integer.toString(Math.round(rectangle.x * this.pageLabel.getScale())));
        properties.put(PdfExtraParams.IMAGE_POSITION_ON_PAGE_LOWER_LEFTY, Integer.toString(Math.round(((this.pageLabel.getHeight() - rectangle.y) - height) * this.pageLabel.getScale())));
        properties.put(PdfExtraParams.IMAGE_POSITION_ON_PAGE_UPPER_RIGHTX, Integer.toString(Math.round((rectangle.x + width) * this.pageLabel.getScale())));
        properties.put(PdfExtraParams.IMAGE_POSITION_ON_PAGE_UPPER_RIGHTY, Integer.toString(Math.round((this.pageLabel.getHeight() - rectangle.y) * this.pageLabel.getScale())));
        return properties;
    }

    public void setDialogParent(JDialog jDialog) {
        this.dialogParent = jDialog;
    }

    JDialog getDialogParent() {
        return this.dialogParent;
    }
}
