package es.gob.afirma.standalone.ui.pdf;

import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import es.gob.afirma.standalone.ui.EditorFocusManager;
import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/SignPdfUiPanelPreview.class */
final class SignPdfUiPanelPreview extends JPanel implements KeyListener {
    private static final long serialVersionUID = 1848879900511003335L;
    private static final int PREFERRED_WIDTH = 470;
    private static final int PREFERRED_HEIGHT = 720;
    private static final int VIEWLABEL_PREFERRED_WIDTH = 475;
    private static final int VIEWLABEL_PREFERRED_HEIGHT = 200;
    private static final int MAX_TEXT_SIZE = 50;
    private static final int MIN_TEXT_SIZE = 1;
    private static final int STEP_TEXT_SIZE = 1;
    private static final int INIT_TEXT_SIZE = 12;
    private static final char NEWLINE = '\n';
    private static final String SPACE_SEPARATOR = " ";
    private static final String SPLIT_REGEXP = "\\s+";
    private JDialog dialogParent;
    private BufferedImage baseImage;
    private BufferedImage rubricImage;
    private BufferedImage signImage;
    private final SignPdfUiPanel.SignPdfUiPanelListener listener;
    private final Properties prop;
    JLabel viewLabel;
    JTextField rubricImagePath;
    private Font font;
    private int style;
    private JComboBox<FontResource> fontFamilyCombo;
    private JComboBox<RotationAngles> rotateSignature;
    private CustomComboBox colorCombobox;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    static final String[] IMAGE_EXT = {"jpg", "jpeg", "png", "gif", "bmp"};
    private float scale = 1.0f;
    private final JButton browseImageButton = new JButton(SignPdfUiMessages.getString("SignPdfUiPreview.38"));
    final JButton clearImageButton = new JButton(SignPdfUiMessages.getString("SignPdfUiPreview.40"));
    private final JToggleButton boldButton = new JToggleButton("<html><b>N</b></html>");
    private final JToggleButton italicButton = new JToggleButton("<html><i>K</i></html>");
    private final JToggleButton underlineButton = new JToggleButton("<html><u>S</u></html>");
    private final JToggleButton strikethroughButton = new JToggleButton("<html><s>T</s></html>");
    private final JCheckBox saveConfig = new JCheckBox(SignPdfUiMessages.getString("SignPdfUiPreview.33"), true);
    private final JButton okButton = new JButton(SignPdfUiMessages.getString("SignPdfUiPreview.5"));
    SpinnerNumberModel sizeSpinnerModel = new SpinnerNumberModel(12, 1, 50, 1);
    JSpinner fontSizeSpinner = new JSpinner(this.sizeSpinnerModel);
    private final JTextArea signatureText = new JTextArea(4, 60);

    BufferedImage getRubricImage() {
        return this.rubricImage;
    }

    SignPdfUiPanel.SignPdfUiPanelListener getListener() {
        return this.listener;
    }

    JLabel getViewLabel() {
        return this.viewLabel;
    }

    JButton getBrowseImageButton() {
        return this.browseImageButton;
    }

    JButton getClearImageButton() {
        return this.clearImageButton;
    }

    Font getViewFont() {
        return this.font;
    }

    void setViewFont(Font font) {
        this.font = font;
    }

    int getStyle() {
        return this.style;
    }

    void setStyle(int i) {
        this.style = i;
    }

    JToggleButton getBoldButton() {
        return this.boldButton;
    }

    JToggleButton getItalicButton() {
        return this.italicButton;
    }

    JToggleButton getUnderlineButton() {
        return this.underlineButton;
    }

    JToggleButton getStrikethroughButton() {
        return this.strikethroughButton;
    }

    JCheckBox getSaveConfig() {
        return this.saveConfig;
    }

    JButton getOkButton() {
        return this.okButton;
    }

    JComboBox<FontResource> getLetterType() {
        return this.fontFamilyCombo;
    }

    JComboBox<RotationAngles> getRotateSignature() {
        return this.rotateSignature;
    }

    CustomComboBox getColorCombobox() {
        return this.colorCombobox;
    }

    JSpinner getSizeSpinner() {
        return this.fontSizeSpinner;
    }

    int getSelectedSize() {
        return ((Integer) this.fontSizeSpinner.getValue()).intValue();
    }

    void setSelectedSize(int i) {
        this.fontSizeSpinner.setValue(Integer.valueOf(i));
    }

    JTextArea getTextArea() {
        return this.signatureText;
    }

    SignPdfUiPanelPreview(SignPdfUiPanel.SignPdfUiPanelListener signPdfUiPanelListener, Properties properties, BufferedImage bufferedImage) {
        if (signPdfUiPanelListener == null) {
            throw new IllegalArgumentException("La clase a la que notificar la seleccion no puede ser nula");
        }
        this.listener = signPdfUiPanelListener;
        this.prop = properties != null ? (Properties) properties.clone() : null;
        this.style = 0;
        this.baseImage = bufferedImage;
        this.rubricImage = null;
        this.signImage = cloneImage(bufferedImage);
        createUI();
        loadProperties();
    }

    void createUI() {
        addKeyListener(this);
        getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPreview.3"));
        setPreferredSize(new Dimension(PREFERRED_WIDTH, 720));
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(10, 5, 0, 5);
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridy = 0;
        add(createPreviewPanel(), gridBagConstraints);
        gridBagConstraints.gridy++;
        add(createConfigurationPanel(), gridBagConstraints);
        gridBagConstraints.gridy++;
        add(this.saveConfig, gridBagConstraints);
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridy++;
        add(new JPanel(), gridBagConstraints);
        gridBagConstraints.fill = 2;
        gridBagConstraints.gridy++;
        add(createButtonsPanel(), gridBagConstraints);
        showPreview();
        this.fontFamilyCombo.requestFocusInWindow();
    }

    private JPanel createPreviewPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        jPanel.setBorder(BorderFactory.createTitledBorder(SignPdfUiMessages.getString("SignPdfUiPreview.0")));
        jPanel.setDropTarget(buildDropPerformance(jPanel));
        if (this.prop.getProperty(PdfExtraParams.SIGNATURE_PAGE) != null && this.prop.getProperty(PdfExtraParams.SIGNATURE_PAGE).equals("append")) {
            createEmptyImage();
        }
        this.viewLabel = new JLabel();
        this.viewLabel.setPreferredSize(new Dimension(VIEWLABEL_PREFERRED_WIDTH, 200));
        this.viewLabel.setHorizontalAlignment(0);
        if (this.baseImage.getHeight() > 200 || this.baseImage.getWidth() > VIEWLABEL_PREFERRED_WIDTH) {
            resizeBaseImageToPreferredSize(this.viewLabel);
        }
        compoundSignImage();
        this.viewLabel.setToolTipText(SignPdfUiMessages.getString("SignPdfUiPreview.26"));
        this.viewLabel.addMouseListener(new MouseAdapter() { // from class: es.gob.afirma.standalone.ui.pdf.SignPdfUiPanelPreview.1
            public void mouseClicked(MouseEvent mouseEvent) {
                if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                    if (SignPdfUiPanelPreview.this.getRubricImage() != null) {
                        SignPdfUiPanelPreview.this.new RemoveImPopUpMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                        return;
                    } else {
                        SignPdfUiPanelPreview.this.new AddImPopUpMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                        return;
                    }
                }
                SignPdfUiPanelPreview.this.selectRubricImage();
            }
        });
        this.viewLabel.setCursor(new Cursor(12));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.gridy = 0;
        jPanel.add(this.viewLabel, gridBagConstraints);
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridy++;
        jPanel.add(createPreviewHintLabel(), gridBagConstraints);
        return jPanel;
    }

    private JPanel createConfigurationPanel() {
        JPanel jPanel = new JPanel(new GridBagLayout());
        jPanel.setBorder(BorderFactory.createTitledBorder(SignPdfUiMessages.getString("SignPdfUiPreview.1")));
        JPanel jPanelCreateBrowseImagePanel = createBrowseImagePanel();
        JLabel jLabel = new JLabel(SignPdfUiMessages.getString("SignPdfUiPreview.36"));
        jLabel.setLabelFor(this.signatureText);
        this.signatureText.setEditable(true);
        HashSet hashSet = new HashSet(this.signatureText.getFocusTraversalKeys(0));
        hashSet.add(AWTKeyStroke.getAWTKeyStroke(9, 0));
        this.signatureText.setFocusTraversalKeys(0, hashSet);
        HashSet hashSet2 = new HashSet(this.signatureText.getFocusTraversalKeys(1));
        hashSet2.add(AWTKeyStroke.getAWTKeyStroke(9, 64));
        this.signatureText.setFocusTraversalKeys(1, hashSet2);
        this.signatureText.setBackground(Color.WHITE);
        this.signatureText.setText(SignPdfUiMessages.getString("SignPdfUiPreview.25"));
        this.signatureText.setLineWrap(true);
        this.signatureText.setWrapStyleWord(true);
        this.signatureText.addKeyListener(this);
        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setMinimumSize(new Dimension(400, 90));
        jScrollPane.setViewportView(this.signatureText);
        jScrollPane.setHorizontalScrollBarPolicy(31);
        this.signatureText.getDocument().addDocumentListener(new DocumentListener() { // from class: es.gob.afirma.standalone.ui.pdf.SignPdfUiPanelPreview.2
            public void removeUpdate(DocumentEvent documentEvent) {
                SignPdfUiPanelPreview.this.showPreview();
            }

            public void insertUpdate(DocumentEvent documentEvent) {
                SignPdfUiPanelPreview.this.showPreview();
            }

            public void changedUpdate(DocumentEvent documentEvent) {
                SignPdfUiPanelPreview.this.showPreview();
            }
        });
        JPanel jPanel2 = new JPanel(new GridBagLayout());
        this.fontFamilyCombo = new JComboBox<>(FontResource.getAllFontresources());
        setViewFont(new Font(((FontResource) this.fontFamilyCombo.getSelectedItem()).getFont().getFontName(), getStyle(), getSelectedSize()));
        this.fontFamilyCombo.setSelectedItem(FontResource.COURIER);
        this.fontFamilyCombo.setRenderer(new ComboRenderer());
        this.fontFamilyCombo.addKeyListener(this);
        this.fontFamilyCombo.setToolTipText(SignPdfUiMessages.getString("SignPdfUiPreview.20"));
        this.fontFamilyCombo.getAccessibleContext().setAccessibleName(SignPdfUiMessages.getString("SignPdfUiPreview.20"));
        this.fontSizeSpinner.getEditor().getComponent(0).getFormatter().setAllowsInvalid(false);
        this.fontSizeSpinner.addChangeListener(changeEvent -> {
            setViewFont(getViewFont().deriveFont(getStyle(), getSelectedSize()));
            showPreview();
        });
        this.fontSizeSpinner.getEditor().getComponent(0).addKeyListener(this);
        this.fontSizeSpinner.getAccessibleContext().setAccessibleName("SignPdfUiPreview.18");
        this.fontSizeSpinner.setToolTipText(SignPdfUiMessages.getString("SignPdfUiPreview.18"));
        this.rotateSignature = new JComboBox<>(RotationAngles.values());
        this.rotateSignature.addKeyListener(this);
        this.rotateSignature.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == 1) {
                compoundSignImage();
                showPreview();
            }
        });
        this.rotateSignature.getAccessibleContext().setAccessibleName("SignPdfUiPreview.42");
        this.rotateSignature.setToolTipText(SignPdfUiMessages.getString("SignPdfUiPreview.42"));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        jPanel2.add(this.fontFamilyCombo, gridBagConstraints);
        gridBagConstraints.weightx = 0.0d;
        gridBagConstraints.gridx++;
        jPanel2.add(this.fontSizeSpinner, gridBagConstraints);
        gridBagConstraints.gridx++;
        jPanel2.add(this.rotateSignature, gridBagConstraints);
        JPanel jPanel3 = new JPanel(new GridBagLayout());
        this.boldButton.setToolTipText(SignPdfUiMessages.getString("SignPdfUiPreview.14"));
        this.boldButton.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPreview.10"));
        this.boldButton.addActionListener(actionEvent -> {
            if (getBoldButton().isSelected()) {
                if (getUnderlineButton().isSelected()) {
                    getUnderlineButton().doClick();
                }
                if (getStrikethroughButton().isSelected()) {
                    getStrikethroughButton().doClick();
                }
                addStyle(1);
                setViewFont(getViewFont().deriveFont(getStyle()));
                showPreview();
                return;
            }
            deleteStyle(1);
            setViewFont(getViewFont().deriveFont(getStyle()));
            showPreview();
        });
        this.boldButton.addKeyListener(this);
        this.italicButton.setToolTipText(SignPdfUiMessages.getString("SignPdfUiPreview.15"));
        this.italicButton.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPreview.11"));
        this.italicButton.addActionListener(actionEvent2 -> {
            if (getItalicButton().isSelected()) {
                if (getUnderlineButton().isSelected()) {
                    getUnderlineButton().doClick();
                }
                if (getStrikethroughButton().isSelected()) {
                    getStrikethroughButton().doClick();
                }
                addStyle(2);
                setViewFont(getViewFont().deriveFont(getStyle()));
                showPreview();
                return;
            }
            deleteStyle(2);
            setViewFont(getViewFont().deriveFont(getStyle()));
            showPreview();
        });
        this.italicButton.addKeyListener(this);
        this.underlineButton.setToolTipText(SignPdfUiMessages.getString("SignPdfUiPreview.16"));
        this.underlineButton.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPreview.12"));
        this.underlineButton.addActionListener(actionEvent3 -> {
            if (getUnderlineButton().isSelected()) {
                if (getBoldButton().isSelected()) {
                    getBoldButton().doClick();
                }
                if (getItalicButton().isSelected()) {
                    getItalicButton().doClick();
                }
                if (getStrikethroughButton().isSelected()) {
                    getStrikethroughButton().doClick();
                }
                HashMap map = new HashMap();
                map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                setViewFont(getViewFont().deriveFont(map));
                showPreview();
                return;
            }
            setViewFont(new Font(getViewFont().getFontName(), getStyle(), getSelectedSize()));
            showPreview();
        });
        this.underlineButton.addKeyListener(this);
        this.strikethroughButton.setToolTipText(SignPdfUiMessages.getString("SignPdfUiPreview.17"));
        this.strikethroughButton.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPreview.13"));
        this.strikethroughButton.addActionListener(actionEvent4 -> {
            if (getStrikethroughButton().isSelected()) {
                if (getBoldButton().isSelected()) {
                    getBoldButton().doClick();
                }
                if (getItalicButton().isSelected()) {
                    getItalicButton().doClick();
                }
                if (getUnderlineButton().isSelected()) {
                    getUnderlineButton().doClick();
                }
                HashMap map = new HashMap();
                map.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                setViewFont(getViewFont().deriveFont(map));
                showPreview();
                return;
            }
            setViewFont(new Font(getViewFont().getFontName(), getStyle(), getSelectedSize()));
            showPreview();
        });
        this.strikethroughButton.addKeyListener(this);
        this.colorCombobox = new CustomComboBox(ColorResource.getAllColorResources());
        this.colorCombobox.getColorList().addKeyListener(this);
        this.colorCombobox.getColorList().setToolTipText(SignPdfUiMessages.getString("SignPdfUiPreview.19"));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0d;
        jPanel3.add(this.boldButton, gridBagConstraints);
        gridBagConstraints.gridx++;
        jPanel3.add(this.italicButton, gridBagConstraints);
        gridBagConstraints.gridx++;
        jPanel3.add(this.underlineButton, gridBagConstraints);
        gridBagConstraints.gridx++;
        jPanel3.add(this.strikethroughButton, gridBagConstraints);
        gridBagConstraints.gridx++;
        jPanel3.add(this.colorCombobox, gridBagConstraints);
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.weightx = 1.0d;
        gridBagConstraints2.insets = new Insets(4, 0, 0, 0);
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.anchor = 17;
        gridBagConstraints2.fill = 2;
        jPanel.add(jPanelCreateBrowseImagePanel, gridBagConstraints2);
        gridBagConstraints2.gridy++;
        jPanel.add(jLabel, gridBagConstraints2);
        gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints2.gridy++;
        jPanel.add(jScrollPane, gridBagConstraints2);
        gridBagConstraints2.insets = new Insets(4, 0, 0, 0);
        gridBagConstraints2.gridy++;
        jPanel.add(jPanel2, gridBagConstraints2);
        gridBagConstraints2.gridy++;
        jPanel.add(jPanel3, gridBagConstraints2);
        return jPanel;
    }

    private JPanel createBrowseImagePanel() {
        JPanel jPanel = new JPanel(new GridBagLayout());
        JLabel jLabel = new JLabel(SignPdfUiMessages.getString("SignPdfUiPreview.37"));
        this.rubricImagePath = new JTextField();
        this.rubricImagePath.setEditable(false);
        this.browseImageButton.getAccessibleContext().setAccessibleName(SignPdfUiMessages.getString("SignPdfUiPreview.39"));
        this.browseImageButton.addActionListener(actionEvent -> {
            selectRubricImage();
        });
        this.clearImageButton.getAccessibleContext().setAccessibleName(SignPdfUiMessages.getString("SignPdfUiPreview.41"));
        this.clearImageButton.addActionListener(actionEvent2 -> {
            clearRubricImage();
            showPreview();
        });
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        jPanel.add(jLabel, gridBagConstraints);
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        jPanel.add(this.rubricImagePath, gridBagConstraints);
        gridBagConstraints.weightx = 0.0d;
        gridBagConstraints.gridx = 1;
        jPanel.add(this.browseImageButton, gridBagConstraints);
        gridBagConstraints.gridx = 2;
        jPanel.add(this.clearImageButton, gridBagConstraints);
        return jPanel;
    }

    private JComponent createPreviewHintLabel() {
        JEditorPane jEditorPane = new JEditorPane();
        Color background = jEditorPane.getBackground();
        UIDefaults uIDefaults = new UIDefaults();
        uIDefaults.put("EditorPane[Enabled].backgroundPainter", background);
        jEditorPane.putClientProperty("Nimbus.Overrides", uIDefaults);
        jEditorPane.putClientProperty("Nimbus.Overrides.InheritDefaults", true);
        jEditorPane.setBackground(background);
        jEditorPane.setContentType("text/html");
        jEditorPane.setText(SignPdfUiMessages.getString("SignPdfUiPreview.4"));
        EditorFocusManager editorFocusManager = new EditorFocusManager(jEditorPane, (hyperlinkEvent, i) -> {
            try {
                if (hyperlinkEvent.getURL() != null) {
                    Desktop.getDesktop().browse(hyperlinkEvent.getURL().toURI());
                }
            } catch (Exception e) {
                LOGGER.severe("No ha podido abrirse el navegador de sistema: " + e);
                AOUIFactory.showErrorMessage(getDialogParent(), SimpleAfirmaMessages.getString("SignResultPanel.0") + hyperlinkEvent.getURL(), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e);
            }
        });
        jEditorPane.addHyperlinkListener(editorFocusManager);
        jEditorPane.addFocusListener(editorFocusManager);
        jEditorPane.addKeyListener(editorFocusManager);
        jEditorPane.setEditable(false);
        return jEditorPane;
    }

    private JPanel createButtonsPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout(2));
        JButton jButton = new JButton(SignPdfUiMessages.getString("SignPdfUiPreview.31"));
        jButton.setMnemonic('R');
        jButton.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPreview.7"));
        jButton.addActionListener(actionEvent -> {
            loadDefaultProperties();
        });
        jButton.addKeyListener(this);
        this.okButton.setMnemonic('A');
        this.okButton.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPanel.2"));
        this.okButton.addActionListener(actionEvent2 -> {
            String strTrim = getTextArea().getText().trim();
            this.prop.put(PdfExtraParams.LAYER2_TEXT, strTrim);
            if (!strTrim.isEmpty()) {
                this.prop.put(PdfExtraParams.LAYER2_FONTFAMILY, ((FontResource) getLetterType().getSelectedItem()).getPdfFontIndex());
                this.prop.put(PdfExtraParams.LAYER2_FONTSIZE, Integer.toString(getSelectedSize()));
                this.prop.put(PdfExtraParams.LAYER2_FONTSTYLE, Integer.toString(getStyleIndex()));
                this.prop.put(PdfExtraParams.LAYER2_FONTCOLOR, getColorCombobox().getSelectedItem().getPdfColorKey());
            }
            int degrees = ((RotationAngles) getRotateSignature().getSelectedItem()).getDegrees();
            if (degrees != 0) {
                this.prop.put(PdfExtraParams.SIGNATURE_ROTATION, Integer.toString(degrees));
            }
            if (!this.rubricImagePath.getText().isEmpty()) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(new File(this.rubricImagePath.getText()));
                    Throwable th = null;
                    try {
                        byte[] dataFromInputStream = AOUtil.getDataFromInputStream(fileInputStream);
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataFromInputStream);
                        Throwable th2 = null;
                        try {
                            try {
                                this.rubricImage = ImageIO.read(byteArrayInputStream);
                                if (byteArrayInputStream != null) {
                                    if (0 != 0) {
                                        try {
                                            byteArrayInputStream.close();
                                        } catch (Throwable th3) {
                                            th2.addSuppressed(th3);
                                        }
                                    } else {
                                        byteArrayInputStream.close();
                                    }
                                }
                                this.prop.put(PdfExtraParams.SIGNATURE_RUBRIC_IMAGE, Base64.getEncoder().encodeToString(dataFromInputStream));
                                if (fileInputStream != null) {
                                    if (0 != 0) {
                                        try {
                                            fileInputStream.close();
                                        } catch (Throwable th4) {
                                            th.addSuppressed(th4);
                                        }
                                    } else {
                                        fileInputStream.close();
                                    }
                                }
                            } catch (Throwable th5) {
                                th2 = th5;
                                throw th5;
                            }
                        } catch (Throwable th6) {
                            if (byteArrayInputStream != null) {
                                if (th2 != null) {
                                    try {
                                        byteArrayInputStream.close();
                                    } catch (Throwable th7) {
                                        th2.addSuppressed(th7);
                                    }
                                } else {
                                    byteArrayInputStream.close();
                                }
                            }
                            throw th6;
                        }
                    } catch (Throwable th8) {
                        if (fileInputStream != null) {
                            if (0 != 0) {
                                try {
                                    fileInputStream.close();
                                } catch (Throwable th9) {
                                    th.addSuppressed(th9);
                                }
                            } else {
                                fileInputStream.close();
                            }
                        }
                        throw th8;
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "No se ha podido cargar la imagen de rubrica", (Throwable) e);
                    this.rubricImage = null;
                    AOUIFactory.showErrorMessage(this.dialogParent, SignPdfUiMessages.getString("SignPdfUiPreview.24"), SignPdfUiMessages.getString("SignPdfUiPreview.23"), 0, e);
                    return;
                }
            }
            if (this.saveConfig.isSelected()) {
                saveProperties(this.prop);
            }
            getListener().nextPanel(this.prop, null);
        });
        this.okButton.addKeyListener(this);
        jPanel.add(this.okButton);
        JButton jButton2 = new JButton(SignPdfUiMessages.getString("SignPdfUiPreview.6"));
        jButton2.setMnemonic('C');
        jButton2.getAccessibleContext().setAccessibleDescription(SignPdfUiMessages.getString("SignPdfUiPreview.7"));
        jButton2.addActionListener(actionEvent3 -> {
            getListener().positionCancelled();
        });
        jButton2.addKeyListener(this);
        if (Platform.OS.MACOSX.equals(Platform.getOS())) {
            jPanel.add(jButton);
            jPanel.add(jButton2);
            jPanel.add(this.okButton);
        } else {
            jPanel.add(jButton);
            jPanel.add(this.okButton);
            jPanel.add(jButton2);
        }
        return jPanel;
    }

    void selectRubricImage() {
        String text = null;
        String name = null;
        try {
            text = this.rubricImagePath.getText();
            if (text != null && !text.isEmpty()) {
                File file = new File(text);
                if (file.isFile()) {
                    text = file.getParent();
                    name = file.getName();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se tiene acceso a la ruta definida para la imagen de firma", (Throwable) e);
        }
        try {
            loadRubricFile(new File(AOUIFactory.getLoadFiles(SignPdfUiMessages.getString("SignPdfUiPreview.21"), text, name, IMAGE_EXT, SignPdfUiMessages.getString("SignPdfUiPreview.22"), false, false, null, this)[0].getAbsolutePath()));
        } catch (AOCancelledOperationException e2) {
        } catch (Exception e3) {
            LOGGER.severe("No ha sido posible cargar la imagen: " + e3);
            AOUIFactory.showErrorMessage(this.dialogParent, SignPdfUiMessages.getString("SignPdfUiPreview.24"), SignPdfUiMessages.getString("SignPdfUiPreview.23"), 0, e3);
        }
    }

    private void loadProperties() {
        getTextArea().setText(PreferencesManager.get(PreferencesManager.PREFERENCE_PDF_SIGN_LAYER2TEXT));
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_PDF_SIGN_LAYER2FONTFAMILY);
        int i = 0;
        FontResource[] allFontresources = FontResource.getAllFontresources();
        for (int i2 = 0; i2 < allFontresources.length; i2++) {
            if (allFontresources[i2].getPdfFontIndex().equals(str)) {
                i = i2;
            }
        }
        getLetterType().setSelectedIndex(i);
        setSelectedSize(Integer.parseInt(PreferencesManager.get(PreferencesManager.PREFERENCE_PDF_SIGN_LAYER2FONTSIZE)));
        int i3 = Integer.parseInt(PreferencesManager.get(PreferencesManager.PREFERENCE_PDF_SIGN_LAYER2FONTSTYLE));
        if (i3 == 8) {
            getStrikethroughButton().doClick();
        } else if (i3 == 4) {
            getUnderlineButton().doClick();
        } else if (i3 == 3) {
            getItalicButton().doClick();
            getBoldButton().doClick();
        } else if (i3 == 2) {
            getItalicButton().doClick();
        } else if (i3 == 1) {
            getBoldButton().doClick();
        }
        String str2 = PreferencesManager.get(PreferencesManager.PREFERENCE_PDF_SIGN_LAYER2FONTCOLOR);
        int i4 = 0;
        ColorResource[] allColorResources = ColorResource.getAllColorResources();
        for (int i5 = 0; i5 < allColorResources.length; i5++) {
            if (allColorResources[i5].getPdfColorKey().equals(str2)) {
                i4 = i5;
            }
        }
        getColorCombobox().setSelectedItem(allColorResources[i4]);
        getColorCombobox().getColorList().setSelectedIndex(i4);
        Font font = allFontresources[i].getFont();
        Map attributes = getViewFont().getAttributes();
        attributes.put(TextAttribute.FAMILY, font.getFontName());
        setViewFont(getViewFont().deriveFont(attributes));
        getRotateSignature().setSelectedItem(RotationAngles.parse(Integer.parseInt(PreferencesManager.get(PreferencesManager.PREFERENCE_PDF_SIGN_SIGNATUREROTATION))));
        String str3 = PreferencesManager.get(PreferencesManager.PREFERENCE_PDF_SIGN_IMAGE);
        if (str3 != null && !str3.isEmpty()) {
            try {
                loadRubricImage(str3);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "No se ha podido cargar la imagen almacenada en las preferencias", (Throwable) e);
            }
        }
        showPreview();
    }

    private void loadDefaultProperties() {
        getTextArea().setText(PreferencesManager.get(PreferencesManager.PREFERENCE_PDF_SIGN_LAYER2TEXT, PreferencesManager.PreferencesSource.DEFAULT));
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_PDF_SIGN_LAYER2FONTFAMILY, PreferencesManager.PreferencesSource.DEFAULT);
        int i = 0;
        FontResource[] allFontresources = FontResource.getAllFontresources();
        for (int i2 = 0; i2 < allFontresources.length; i2++) {
            if (allFontresources[i2].getPdfFontIndex().equals(str)) {
                i = i2;
            }
        }
        getLetterType().setSelectedIndex(i);
        setSelectedSize(Integer.parseInt(PreferencesManager.get(PreferencesManager.PREFERENCE_PDF_SIGN_LAYER2FONTSIZE, PreferencesManager.PreferencesSource.DEFAULT)));
        int i3 = Integer.parseInt(PreferencesManager.get(PreferencesManager.PREFERENCE_PDF_SIGN_LAYER2FONTSTYLE, PreferencesManager.PreferencesSource.DEFAULT));
        switch (i3) {
            case 1:
                getBoldButton().doClick();
                break;
            case 2:
                getItalicButton().doClick();
                break;
            case 3:
                getItalicButton().doClick();
                getBoldButton().doClick();
                break;
            case 4:
                getUnderlineButton().doClick();
                break;
            case 5:
            case 6:
            case 7:
            default:
                LOGGER.warning("Estilo de texto desconocido: " + i3);
                break;
            case 8:
                getStrikethroughButton().doClick();
                break;
        }
        String str2 = PreferencesManager.get(PreferencesManager.PREFERENCE_PDF_SIGN_LAYER2FONTCOLOR, PreferencesManager.PreferencesSource.DEFAULT);
        int i4 = 0;
        ColorResource[] allColorResources = ColorResource.getAllColorResources();
        for (int i5 = 0; i5 < allColorResources.length; i5++) {
            if (allColorResources[i5].getPdfColorKey().equals(str2)) {
                i4 = i5;
            }
        }
        getColorCombobox().setSelectedItem(allColorResources[i4]);
        getColorCombobox().getColorList().setSelectedIndex(i4);
        Font font = allFontresources[i].getFont();
        Map attributes = getViewFont().getAttributes();
        attributes.put(TextAttribute.FAMILY, font.getFontName());
        setViewFont(getViewFont().deriveFont(attributes));
        getRotateSignature().setSelectedItem(RotationAngles.parse(Integer.parseInt(PreferencesManager.get(PreferencesManager.PREFERENCE_PDF_SIGN_SIGNATUREROTATION, PreferencesManager.PreferencesSource.DEFAULT))));
        String str3 = PreferencesManager.get(PreferencesManager.PREFERENCE_PDF_SIGN_IMAGE, PreferencesManager.PreferencesSource.DEFAULT);
        if (str3 != null && !str3.isEmpty()) {
            try {
                loadRubricImage(str3);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "No se ha podido cargar la imagen almacenada en las preferencias", (Throwable) e);
            }
        }
        showPreview();
    }

    private void saveProperties(Properties properties) {
        String property = properties.getProperty(PdfExtraParams.LAYER2_TEXT, "");
        PreferencesManager.put(PreferencesManager.PREFERENCE_PDF_SIGN_LAYER2TEXT, property);
        if (property != null && !property.isEmpty()) {
            PreferencesManager.put(PreferencesManager.PREFERENCE_PDF_SIGN_LAYER2FONTFAMILY, properties.getProperty(PdfExtraParams.LAYER2_FONTFAMILY));
            PreferencesManager.put(PreferencesManager.PREFERENCE_PDF_SIGN_LAYER2FONTSIZE, properties.getProperty(PdfExtraParams.LAYER2_FONTSIZE));
            PreferencesManager.put(PreferencesManager.PREFERENCE_PDF_SIGN_LAYER2FONTSTYLE, properties.getProperty(PdfExtraParams.LAYER2_FONTSTYLE));
            PreferencesManager.put(PreferencesManager.PREFERENCE_PDF_SIGN_LAYER2FONTCOLOR, properties.getProperty(PdfExtraParams.LAYER2_FONTCOLOR));
        }
        if (properties.getProperty(PdfExtraParams.SIGNATURE_ROTATION) != null) {
            PreferencesManager.put(PreferencesManager.PREFERENCE_PDF_SIGN_SIGNATUREROTATION, properties.getProperty(PdfExtraParams.SIGNATURE_ROTATION));
        }
        if (properties.getProperty(PdfExtraParams.SIGNATURE_RUBRIC_IMAGE) != null && this.rubricImagePath.getText() != null) {
            PreferencesManager.put(PreferencesManager.PREFERENCE_PDF_SIGN_IMAGE, this.rubricImagePath.getText());
        } else {
            PreferencesManager.remove(PreferencesManager.PREFERENCE_PDF_SIGN_IMAGE);
        }
        try {
            PreferencesManager.flush();
        } catch (Exception e) {
            LOGGER.severe("Error al guardar las preferencias de firma visible PDF: " + e);
        }
    }

    void loadRubricFile(File file) throws IOException {
        setCursor(new Cursor(3));
        String string = null;
        if (!file.exists()) {
            string = SimpleAfirmaMessages.getString("SignPanel.3");
        } else if (file.isDirectory()) {
            string = SimpleAfirmaMessages.getString("SignPanel.21");
        } else if (!file.canRead()) {
            string = SimpleAfirmaMessages.getString("SignPanel.7");
        } else if (file.length() < 1) {
            string = SimpleAfirmaMessages.getString("SignPanel.5");
        } else if (!isValidImage(file.getName())) {
            string = SimpleAfirmaMessages.getString("SignPanel.101");
        }
        if (string != null) {
            AOUIFactory.showErrorMessage(this.dialogParent, string, SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, null);
            setCursor(new Cursor(0));
            return;
        }
        loadRubricImage(file.getAbsolutePath());
        showPreview();
        setCursor(new Cursor(0));
        if (checkRubricTransparency()) {
            AOUIFactory.showMessageDialog(this, SignPdfUiMessages.getString("SignPdfDialog.9"), SignPdfUiMessages.getString("SignPdfDialog.8"), 2, null);
        }
    }

    private static boolean isValidImage(String str) {
        boolean z = false;
        for (String str2 : IMAGE_EXT) {
            if (str.endsWith(str2)) {
                z = true;
            }
        }
        return z;
    }

    private void createEmptyImage() {
        BufferedImage bufferedImage = new BufferedImage(this.baseImage.getWidth(), this.baseImage.getHeight(), 1);
        Graphics2D graphics2DCreateGraphics = bufferedImage.createGraphics();
        graphics2DCreateGraphics.setPaint(Color.WHITE);
        graphics2DCreateGraphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        graphics2DCreateGraphics.dispose();
        this.baseImage = bufferedImage;
    }

    void loadRubricImage(String str) throws IOException, IllegalArgumentException {
        BufferedImage bufferedImage = ImageIO.read(new File(str));
        if (bufferedImage == null) {
            throw new IllegalArgumentException("La ruta proporcionada no se corresponde con la de un fichero de imagen soportada");
        }
        this.rubricImage = bufferedImage;
        this.rubricImagePath.setText(str);
        compoundSignImage();
    }

    void compoundSignImage() {
        BufferedImage bufferedImageCloneImage = cloneImage(this.baseImage);
        if (this.rubricImage != null) {
            BufferedImage bufferedImageRotateImage = rotateImage(this.rubricImage, (RotationAngles) this.rotateSignature.getSelectedItem());
            float fMin = Math.min(this.baseImage.getWidth() / bufferedImageRotateImage.getWidth(), this.baseImage.getHeight() / bufferedImageRotateImage.getHeight());
            float width = bufferedImageRotateImage.getWidth() * fMin;
            float height = bufferedImageRotateImage.getHeight() * fMin;
            float width2 = (this.baseImage.getWidth() - width) / 2.0f;
            float height2 = (this.baseImage.getHeight() - height) / 2.0f;
            Image scaledInstance = bufferedImageRotateImage.getScaledInstance(Math.round(width), Math.round(height), 4);
            Graphics2D graphics2DCreateGraphics = bufferedImageCloneImage.createGraphics();
            graphics2DCreateGraphics.drawImage(scaledInstance, Math.round(width2), Math.round(height2), (ImageObserver) null);
            graphics2DCreateGraphics.dispose();
        }
        this.signImage = bufferedImageCloneImage;
    }

    private static BufferedImage rotateImage(BufferedImage bufferedImage, RotationAngles rotationAngles) {
        int width;
        int height;
        double radians;
        switch (rotationAngles) {
            case DEGREES_90:
                width = bufferedImage.getHeight();
                height = bufferedImage.getWidth();
                radians = Math.toRadians(270.0d);
                break;
            case DEGREES_180:
                width = bufferedImage.getWidth();
                height = bufferedImage.getHeight();
                radians = Math.toRadians(180.0d);
                break;
            case DEGREES_270:
                width = bufferedImage.getHeight();
                height = bufferedImage.getWidth();
                radians = Math.toRadians(90.0d);
                break;
            default:
                width = bufferedImage.getWidth();
                height = bufferedImage.getHeight();
                radians = 0.0d;
                break;
        }
        BufferedImage bufferedImage2 = new BufferedImage(width, height, bufferedImage.getType());
        Graphics2D graphics2DCreateGraphics = bufferedImage2.createGraphics();
        graphics2DCreateGraphics.rotate(radians, width / 2, height / 2);
        graphics2DCreateGraphics.drawImage(bufferedImage, (width - bufferedImage.getWidth()) / 2, (height - bufferedImage.getHeight()) / 2, (ImageObserver) null);
        graphics2DCreateGraphics.dispose();
        return bufferedImage2;
    }

    void clearRubricImage() {
        this.rubricImage = null;
        this.rubricImagePath.setText((String) null);
        compoundSignImage();
    }

    public static String breakLines(String str, double d, FontMetrics fontMetrics) {
        String[] strArrSplit = str.split(SPLIT_REGEXP);
        StringBuilder sb = new StringBuilder(str.length());
        int iStringWidth = 0;
        for (int i = 0; i < strArrSplit.length; i++) {
            String str2 = strArrSplit[i];
            if (iStringWidth + fontMetrics.stringWidth(SPACE_SEPARATOR + str2) > d) {
                if (i > 0) {
                    sb.append('\n');
                }
                iStringWidth = 0;
            }
            if (i < strArrSplit.length - 1 && iStringWidth + fontMetrics.stringWidth(str2 + SPACE_SEPARATOR + strArrSplit[i + 1]) <= d) {
                str2 = str2 + SPACE_SEPARATOR;
            }
            sb.append(str2);
            iStringWidth += fontMetrics.stringWidth(str2);
        }
        return sb.toString();
    }

    void showPreview() {
        BufferedImage bufferedImage = new BufferedImage(this.signImage.getWidth(), this.signImage.getHeight(), this.signImage.getType());
        Graphics2D graphics2DCreateGraphics = bufferedImage.createGraphics();
        graphics2DCreateGraphics.drawImage(this.signImage, 0, 0, (ImageObserver) null);
        graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2DCreateGraphics.setColor(this.colorCombobox.getSelectedItem().getColor());
        graphics2DCreateGraphics.setFont(getViewFont().deriveFont(getStyle(), Math.max(1, Math.round(getViewFont().getSize() / this.scale) - 3)));
        printTextOverImage(graphics2DCreateGraphics, this.signImage.getWidth(), this.signImage.getHeight(), getTextArea().getText(), (RotationAngles) this.rotateSignature.getSelectedItem());
        graphics2DCreateGraphics.dispose();
        getViewLabel().setIcon(new ImageIcon(bufferedImage));
    }

    private static void printTextOverImage(Graphics2D graphics2D, int i, int i2, String str, RotationAngles rotationAngles) {
        int i3;
        int i4;
        int i5;
        AffineTransform transform = graphics2D.getTransform();
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        int height = fontMetrics.getHeight();
        switch (rotationAngles) {
            case DEGREES_90:
                i3 = i2;
                i4 = (-i2) + 4;
                i5 = 0;
                graphics2D.rotate(-1.5707963267948966d);
                break;
            case DEGREES_180:
                i3 = i;
                i4 = (-i) + 4;
                i5 = -i2;
                graphics2D.rotate(3.141592653589793d);
                break;
            case DEGREES_270:
                i3 = i2;
                i4 = 4;
                i5 = -i;
                graphics2D.rotate(1.5707963267948966d);
                break;
            default:
                i3 = i;
                i4 = 4;
                i5 = 0;
                break;
        }
        for (String str2 : str.split("\n")) {
            if (fontMetrics.stringWidth(str2) > i3) {
                for (String str3 : breakLines(str2, i3, fontMetrics).split("\n")) {
                    int i6 = i5 + height;
                    i5 = i6;
                    graphics2D.drawString(str3, i4, i6);
                }
            } else {
                int i7 = i5 + height;
                i5 = i7;
                graphics2D.drawString(str2, i4, i7);
            }
        }
        graphics2D.setTransform(transform);
    }

    private void resizeBaseImageToPreferredSize(JComponent jComponent) {
        int width = this.baseImage.getWidth();
        int height = this.baseImage.getHeight();
        Dimension preferredSize = jComponent.getPreferredSize();
        while (true) {
            if (width > preferredSize.width || height > preferredSize.height) {
                width = (int) Math.round(((double) width) * 0.9d);
                height = (int) Math.round(((double) height) * 0.9d);
            } else {
                float width2 = width / this.baseImage.getWidth();
                int iRound = Math.round(this.baseImage.getWidth() * width2);
                int iRound2 = Math.round(this.baseImage.getHeight() * width2);
                this.scale = this.baseImage.getWidth() / iRound;
                Image scaledInstance = this.baseImage.getScaledInstance(iRound, iRound2, 4);
                BufferedImage bufferedImage = new BufferedImage(iRound, iRound2, this.baseImage.getType());
                Graphics2D graphics2DCreateGraphics = bufferedImage.createGraphics();
                graphics2DCreateGraphics.drawImage(scaledInstance, 0, 0, (ImageObserver) null);
                graphics2DCreateGraphics.dispose();
                this.baseImage = bufferedImage;
                return;
            }
        }
    }

    private static BufferedImage cloneImage(BufferedImage bufferedImage) {
        BufferedImage bufferedImage2 = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
        Graphics2D graphics2DCreateGraphics = bufferedImage2.createGraphics();
        graphics2DCreateGraphics.drawImage(bufferedImage, 0, 0, (ImageObserver) null);
        graphics2DCreateGraphics.dispose();
        return bufferedImage2;
    }

    public boolean checkRubricTransparency() {
        boolean z = false;
        if (this.rubricImage != null && (containsAlphaChannel(this.rubricImage) || containsTransparency(this.rubricImage))) {
            z = true;
        }
        return z;
    }

    private static boolean containsAlphaChannel(BufferedImage bufferedImage) {
        return bufferedImage.getColorModel().hasAlpha();
    }

    private static boolean containsTransparency(BufferedImage bufferedImage) {
        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int i2 = 0; i2 < bufferedImage.getWidth(); i2++) {
                if (isTransparent(bufferedImage, i2, i)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isTransparent(BufferedImage bufferedImage, int i, int i2) {
        return (bufferedImage.getRGB(i, i2) >> 24) == 0;
    }

    int getStyleIndex() {
        if (getStrikethroughButton().isSelected()) {
            return 8;
        }
        if (getUnderlineButton().isSelected()) {
            return 4;
        }
        if (getBoldButton().isSelected() && getItalicButton().isSelected()) {
            return 3;
        }
        if (getItalicButton().isSelected()) {
            return 2;
        }
        if (getBoldButton().isSelected()) {
            return 1;
        }
        return 0;
    }

    void addStyle(int i) {
        setStyle(getStyle() + i);
    }

    void deleteStyle(int i) {
        setStyle(getStyle() - i);
    }

    public void keyTyped(KeyEvent keyEvent) {
    }

    public void keyReleased(KeyEvent keyEvent) {
    }

    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent != null) {
            if (keyEvent.getKeyCode() == 27) {
                getListener().positionCancelled();
                return;
            }
            if (keyEvent.getKeyCode() == 78 && keyEvent.isControlDown()) {
                this.boldButton.doClick();
                return;
            }
            if (keyEvent.getKeyCode() == 75 && keyEvent.isControlDown()) {
                this.italicButton.doClick();
                return;
            }
            if (keyEvent.getKeyCode() == 83 && keyEvent.isControlDown()) {
                this.underlineButton.doClick();
                return;
            }
            if (keyEvent.getKeyCode() == 84 && keyEvent.isControlDown()) {
                this.strikethroughButton.doClick();
            } else if (keyEvent.getKeyCode() == 80 && keyEvent.isControlDown()) {
                SwingUtilities.invokeLater(() -> {
                    getViewLabel().dispatchEvent(new MouseEvent(this, 500, 1L, 1, 0, 0, 1, false));
                });
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/SignPdfUiPanelPreview$CustomComboBox.class */
    private final class CustomComboBox extends JPanel {
        private static final long serialVersionUID = 6905237438341447834L;
        private ColorResource color;
        final ColorResource[] colorStrings;
        private final JComboBox<Integer> colorList;
        ImageIcon[] images;

        JComboBox<Integer> getColorList() {
            return this.colorList;
        }

        ColorResource getSelectedItem() {
            return this.color;
        }

        void setSelectedItem(ColorResource colorResource) {
            this.color = colorResource;
        }

        public CustomComboBox(ColorResource[] colorResourceArr) {
            super(new BorderLayout());
            this.color = ColorResource.BLACK;
            this.colorStrings = colorResourceArr;
            this.images = new ImageIcon[this.colorStrings.length];
            Integer[] numArr = new Integer[this.colorStrings.length];
            for (int i = 0; i < this.colorStrings.length; i++) {
                numArr[i] = Integer.valueOf(i);
                this.images[i] = new ImageIcon(this.colorStrings[i].getImage());
                if (this.images[i] != null) {
                    this.images[i].setDescription(this.colorStrings[i].toString());
                }
            }
            this.colorList = new JComboBox<>(numArr);
            ComboBoxRenderer comboBoxRenderer = new ComboBoxRenderer();
            comboBoxRenderer.setPreferredSize(new Dimension(60, 20));
            this.colorList.setRenderer(comboBoxRenderer);
            this.colorList.setMaximumRowCount(3);
            add(this.colorList, "First");
        }

        /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/SignPdfUiPanelPreview$CustomComboBox$ComboBoxRenderer.class */
        private final class ComboBoxRenderer extends JLabel implements ListCellRenderer<Object> {
            private static final long serialVersionUID = -9197195477672319113L;
            private Font uhOhFont;

            public ComboBoxRenderer() {
                setOpaque(true);
                setHorizontalAlignment(2);
                setVerticalAlignment(0);
            }

            public Component getListCellRendererComponent(JList<?> jList, Object obj, int i, boolean z, boolean z2) {
                int iIntValue = ((Integer) obj).intValue();
                Icon icon = CustomComboBox.this.images[iIntValue];
                String string = CustomComboBox.this.colorStrings[iIntValue].toString();
                if (z) {
                    CustomComboBox.this.setSelectedItem(CustomComboBox.this.colorStrings[iIntValue]);
                    SignPdfUiPanelPreview.this.showPreview();
                    setBackground(jList.getSelectionBackground());
                    setForeground(jList.getSelectionForeground());
                } else {
                    setBackground(jList.getBackground());
                    setForeground(jList.getForeground());
                }
                setBorder(new EmptyBorder(1, 3, 1, 1));
                setIcon(icon);
                if (icon != null) {
                    setText(string);
                    setFont(jList.getFont());
                } else {
                    setUhOhText(string + " (no image available)", jList.getFont());
                }
                return this;
            }

            protected void setUhOhText(String str, Font font) {
                if (this.uhOhFont == null) {
                    this.uhOhFont = font.deriveFont(0);
                }
                setFont(this.uhOhFont);
                setText(str);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/SignPdfUiPanelPreview$ComboRenderer.class */
    private final class ComboRenderer extends BasicComboBoxRenderer {
        private static final long serialVersionUID = 3706919693882358747L;
        private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
        private int row;

        ComboRenderer() {
        }

        public Component getListCellRendererComponent(JList jList, Object obj, int i, boolean z, boolean z2) {
            super.getListCellRendererComponent(jList, obj, i, z, z2);
            JLabel listCellRendererComponent = this.defaultRenderer.getListCellRendererComponent(jList, obj, this.row, z, z2);
            Font font = ((FontResource) obj).getFont();
            Font font2 = new Font(font.getFontName(), 0, 14);
            Map attributes = SignPdfUiPanelPreview.this.getViewFont().getAttributes();
            attributes.put(TextAttribute.FAMILY, font.getFontName());
            attributes.put(TextAttribute.SIZE, Integer.valueOf(SignPdfUiPanelPreview.this.getSelectedSize()));
            Font font3 = new Font(attributes);
            SignPdfUiPanelPreview.this.setViewFont(SignPdfUiPanelPreview.this.getViewFont().deriveFont(attributes));
            SignPdfUiPanelPreview.this.getLetterType().setFont(font2);
            listCellRendererComponent.setFont(font2);
            listCellRendererComponent.setText(obj.toString());
            if (z) {
                SignPdfUiPanelPreview.this.setViewFont(font3);
                SignPdfUiPanelPreview.this.showPreview();
            }
            return listCellRendererComponent;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/SignPdfUiPanelPreview$RemoveImPopUpMenu.class */
    class RemoveImPopUpMenu extends JPopupMenu {
        private static final long serialVersionUID = 8491839704433747625L;
        JMenuItem removeImageItem = new JMenuItem(SignPdfUiMessages.getString("SignPdfUiPreview.27"));

        public RemoveImPopUpMenu() {
            this.removeImageItem.addActionListener(actionEvent -> {
                SignPdfUiPanelPreview.this.clearRubricImage();
                SignPdfUiPanelPreview.this.showPreview();
            });
            add(this.removeImageItem);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/SignPdfUiPanelPreview$AddImPopUpMenu.class */
    class AddImPopUpMenu extends JPopupMenu {
        private static final long serialVersionUID = 8491839704433747625L;
        JMenuItem addImageItem = new JMenuItem(SignPdfUiMessages.getString("SignPdfUiPreview.28"));

        public AddImPopUpMenu() {
            this.addImageItem.addActionListener(actionEvent -> {
                SwingUtilities.invokeLater(() -> {
                    SignPdfUiPanelPreview.this.getViewLabel().dispatchEvent(new MouseEvent(SignPdfUiPanelPreview.this, 500, 1L, 1, 0, 0, 1, false));
                });
            });
            add(this.addImageItem);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/SignPdfUiPanelPreview$RotationAngles.class */
    private enum RotationAngles {
        DEGREES_0(0, "RotationModel.0"),
        DEGREES_90(90, "RotationModel.1"),
        DEGREES_180(180, "RotationModel.2"),
        DEGREES_270(TIFF.TAG_IMAGE_DESCRIPTION, "RotationModel.3");

        private final int degrees;
        private final String text;

        RotationAngles(int i, String str) {
            this.degrees = i;
            this.text = str;
        }

        public int getDegrees() {
            return this.degrees;
        }

        public String getText() {
            return SignPdfUiMessages.getString(this.text);
        }

        @Override // java.lang.Enum
        public String toString() {
            return getText();
        }

        public static RotationAngles parse(int i) {
            for (RotationAngles rotationAngles : values()) {
                if (rotationAngles.getDegrees() == i) {
                    return rotationAngles;
                }
            }
            return null;
        }
    }

    public void setDialogParent(JDialog jDialog) {
        this.dialogParent = jDialog;
    }

    JDialog getDialogParent() {
        return this.dialogParent;
    }

    private DropTarget buildDropPerformance(Component component) {
        return new DropTarget(component, 1, new DropTargetListener() { // from class: es.gob.afirma.standalone.ui.pdf.SignPdfUiPanelPreview.3
            public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent) {
            }

            public void dragEnter(DropTargetDragEvent dropTargetDragEvent) {
            }

            public void dragOver(DropTargetDragEvent dropTargetDragEvent) {
            }

            public void dragExit(DropTargetEvent dropTargetEvent) {
            }

            public void drop(DropTargetDropEvent dropTargetDropEvent) {
                Transferable transferable = dropTargetDropEvent.getTransferable();
                dropTargetDropEvent.acceptDrop(1);
                try {
                    Object transferData = transferable.getTransferData(DataFlavor.javaFileListFlavor);
                    if (transferData instanceof List) {
                        dropTargetDropEvent.getDropTargetContext().dropComplete(true);
                        List list = (List) transferData;
                        if (list.isEmpty()) {
                            dropTargetDropEvent.dropComplete(false);
                            return;
                        }
                        if (list.size() > 1) {
                            AOUIFactory.showErrorMessage(SignPdfUiPanelPreview.this.getDialogParent(), SimpleAfirmaMessages.getString("SignPanel.18"), SimpleAfirmaMessages.getString("SimpleAfirma.48"), 2, null);
                        }
                        File file = null;
                        String string = list.get(0).toString();
                        if (string.startsWith("http://") || string.startsWith("https://") || string.startsWith("ftp://")) {
                            AOUIFactory.showErrorMessage(SignPdfUiPanelPreview.this.getDialogParent(), SimpleAfirmaMessages.getString("SignPanel.24"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, null);
                            dropTargetDropEvent.dropComplete(false);
                            return;
                        }
                        if (string.startsWith("file://")) {
                            try {
                                file = new File(new URI(string));
                            } catch (Exception e) {
                                SignPdfUiPanelPreview.LOGGER.warning("Ha fallado la operacion de arrastrar y soltar al obtener la ruta del fichero arrastrado: " + e);
                                dropTargetDropEvent.dropComplete(false);
                                return;
                            }
                        }
                        try {
                            SignPdfUiPanelPreview.this.loadRubricFile(file != null ? file : new File(string));
                        } catch (Exception e2) {
                            SignPdfUiPanelPreview.LOGGER.warning("Ha fallado la operacion de arrastrar y soltar al cargar el fichero arrastrado: " + e2);
                            dropTargetDropEvent.dropComplete(false);
                        }
                    }
                } catch (Exception e3) {
                    SignPdfUiPanelPreview.LOGGER.warning("Ha fallado la operacion de arrastrar y soltar: " + e3);
                    dropTargetDropEvent.dropComplete(false);
                }
            }
        }, true);
    }
}
