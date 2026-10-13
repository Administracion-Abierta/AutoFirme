package es.gob.afirma.keystores.jmulticard.ui;

import com.sun.jna.platform.win32.WinError;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/jmulticard/ui/ConfirmSmartcardDialog.class */
public final class ConfirmSmartcardDialog extends AbstractJAccessibilityCustomDialog implements ActionListener {
    private static final long serialVersionUID = 1;
    private transient InfoLabel infoLabel;
    private transient JPanel buttonsPanel;
    private final transient IconLabel iconLabel;
    private transient JButton noButton;
    private transient int answer;
    private static JButton okButton = null;
    private static JButton cancelButton = null;
    private static String cancellText = Messages.getString("PrincipalGUI.cancelar");

    static JButton getOkButton() {
        return okButton;
    }

    JButton getNoButton() {
        return this.noButton;
    }

    static JButton getCancelButton() {
        return cancelButton;
    }

    protected ConfirmSmartcardDialog(JDialog jDialog, boolean z, String str, String str2, String str3) {
        super(jDialog, z, false);
        this.infoLabel = null;
        this.buttonsPanel = null;
        this.iconLabel = new IconLabel();
        this.noButton = null;
        initComponents(str, str2, str3);
        setLocationRelativeTo(jDialog);
        pack();
    }

    protected ConfirmSmartcardDialog(Component component, boolean z, String str, String str2, String str3) {
        super(false);
        this.infoLabel = null;
        this.buttonsPanel = null;
        this.iconLabel = new IconLabel();
        this.noButton = null;
        setModal(z);
        initComponents(str, str2, str3);
        setLocationRelativeTo(component);
        pack();
    }

    private ConfirmSmartcardDialog(JFrame jFrame, boolean z, String str, String str2, String str3) {
        super(jFrame, z, false);
        this.infoLabel = null;
        this.buttonsPanel = null;
        this.iconLabel = new IconLabel();
        this.noButton = null;
        initComponents(str, str2, str3);
        setLocationRelativeTo(jFrame);
        pack();
    }

    protected void initComponents(String str, String str2, String str3) {
        int maxWidth = getMaxWidth();
        int maxHeight = getMaxHeight();
        setMinimumSize(new Dimension(getInitialWidth(), getInitialHeight()));
        setPreferredSize(new Dimension(getInitialWidth(), getInitialHeight()));
        setMaximumSize(new Dimension(maxWidth, maxHeight));
        if (GeneralConfig.isMaximized()) {
            setBounds(0, 0, maxWidth, maxHeight);
        } else if (GeneralConfig.isBigFontSize() || GeneralConfig.isFontBold()) {
            setMinimumSize(new Dimension(WinError.ERROR_CANT_TERMINATE_SELF, 250));
        }
        setTitle(str2);
        setDefaultCloseOperation(2);
        this.answer = 1;
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        JPanel jPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 10, 0, 10);
        setIconLabel(str3);
        JPanel jPanel2 = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = 1;
        jPanel2.add(this.iconLabel, gridBagConstraints2);
        gridBagConstraints.insets = new Insets(10, 5, 0, 10);
        gridBagConstraints.gridx++;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.weighty = 1.0d;
        this.infoLabel = new InfoLabel(str, false);
        this.infoLabel.setHorizontalAlignment(0);
        this.infoLabel.addAncestorListener(new RequestFocusListener());
        this.infoLabel.setVerticalAlignment(0);
        jPanel.add(this.infoLabel, gridBagConstraints);
        createMainButtonsPanel();
        JPanel jPanelCreateAccessibilityButtonsPanel = createAccessibilityButtonsPanel();
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = 1;
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.gridwidth = 2;
        gridBagConstraints3.gridheight = 1;
        gridBagConstraints3.weighty = 0.0d;
        gridBagConstraints3.weightx = 0.1d;
        contentPane.add(jPanelCreateAccessibilityButtonsPanel, gridBagConstraints3);
        gridBagConstraints3.gridx = 2;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.gridwidth = 6;
        gridBagConstraints3.gridheight = 3;
        gridBagConstraints3.weighty = 0.65d;
        gridBagConstraints3.weightx = 0.9d;
        contentPane.add(jPanel, gridBagConstraints3);
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.gridy = 1;
        gridBagConstraints3.gridwidth = 2;
        gridBagConstraints3.gridheight = 3;
        gridBagConstraints3.weighty = 0.9d;
        gridBagConstraints3.weightx = 0.0d;
        contentPane.add(jPanel2, gridBagConstraints3);
        gridBagConstraints3.gridx = 2;
        gridBagConstraints3.gridy = 3;
        gridBagConstraints3.gridwidth = 6;
        gridBagConstraints3.gridheight = 1;
        gridBagConstraints3.weighty = 0.35d;
        gridBagConstraints3.weightx = 0.0d;
        contentPane.add(this.buttonsPanel, gridBagConstraints3);
        pack();
    }

    protected void setIconLabel(String str) {
        if (str != null) {
            Icon imageIcon = new ImageIcon(ConfirmSmartcardDialog.class.getResource(str));
            Dimension dimension = new Dimension(100, (int) ((100.0f / imageIcon.getIconWidth()) * imageIcon.getIconHeight()));
            this.iconLabel.setOriginalIcon(imageIcon);
            this.iconLabel.setOriginalDimension(dimension);
            this.iconLabel.setIcon(new ImageIcon(imageIcon.getImage().getScaledInstance(dimension.width, dimension.height, 4)));
        }
    }

    void createMainButtonsPanel() {
        this.buttonsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        gridBagConstraints.anchor = 10;
        JPanel jPanel = new JPanel();
        okButton = getButton(Messages.getString("PrincipalGUI.aceptar"), 65);
        okButton.addKeyListener(new KeyListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.ConfirmSmartcardDialog.1
            public void keyTyped(KeyEvent keyEvent) {
            }

            public void keyReleased(KeyEvent keyEvent) {
            }

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 10) {
                    ConfirmSmartcardDialog.getOkButton().doClick();
                }
            }
        });
        jPanel.add(okButton);
        this.buttonsPanel.add(jPanel, gridBagConstraints);
        okButton.addActionListener(this);
    }

    public static int showConfirmDialog(Component component, boolean z, String str, String str2, int i, String str3) {
        ConfirmSmartcardDialog instanceCustomDialog = getInstanceCustomDialog(component, z, str, str2, str3);
        okButton.setEnabled(true);
        instanceCustomDialog.getRootPane().setDefaultButton((JButton) null);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 10);
        if (i != 0) {
            throw new UnsupportedOperationException("Solo se soportan dialogos de tipo Si/No");
        }
        okButton.setText(Messages.getString("CustomDialog.confirmDialog.yes"));
        okButton.setMnemonic(83);
        instanceCustomDialog.noButton = instanceCustomDialog.getButton(Messages.getString("CustomDialog.confirmDialog.no"), 78);
        instanceCustomDialog.noButton.addKeyListener(new KeyListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.ConfirmSmartcardDialog.2
            public void keyTyped(KeyEvent keyEvent) {
            }

            public void keyReleased(KeyEvent keyEvent) {
            }

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 10) {
                    ConfirmSmartcardDialog.this.getNoButton().doClick();
                }
            }
        });
        JPanel jPanel = new JPanel();
        jPanel.add(instanceCustomDialog.noButton);
        instanceCustomDialog.buttonsPanel.add(jPanel, gridBagConstraints);
        instanceCustomDialog.noButton.addActionListener(instanceCustomDialog);
        instanceCustomDialog.infoLabel.setHorizontalAlignment(0);
        instanceCustomDialog.pack();
        instanceCustomDialog.setSize(instanceCustomDialog.getInitialWidth() + 1, instanceCustomDialog.getInitialHeight() + 1);
        instanceCustomDialog.setVisible(true);
        return instanceCustomDialog.getAnswer();
    }

    private JButton getButton(String str, int i) {
        JButton jButton = new JButton(str);
        jButton.setMnemonic(i);
        AccesibilityUtils.remarcar(jButton);
        AccesibilityUtils.setContrastColor(jButton);
        AccesibilityUtils.setFontBold(jButton);
        if (str.equalsIgnoreCase(cancellText)) {
            getRootPane().getInputMap(2).put(KeyStroke.getKeyStroke(27, 0), "cancel");
            getRootPane().getActionMap().put("cancel", new AbstractJAccessibilityCustomDialog.ButtonAbstractAction(getCancelButton()));
        }
        return jButton;
    }

    @Override // es.gob.afirma.keystores.jmulticard.ui.AbstractJAccessibilityCustomDialog
    public int getMinimumRelation() {
        return 7;
    }

    private int getAnswer() {
        return this.answer;
    }

    static ConfirmSmartcardDialog getInstanceCustomDialog(Component component, boolean z, String str, String str2, String str3) {
        ConfirmSmartcardDialog confirmSmartcardDialog;
        if (component instanceof JDialog) {
            confirmSmartcardDialog = new ConfirmSmartcardDialog((JDialog) component, z, str, str2, str3);
        } else if (component instanceof JFrame) {
            confirmSmartcardDialog = new ConfirmSmartcardDialog((JFrame) component, z, str, str2, str3);
        } else {
            confirmSmartcardDialog = new ConfirmSmartcardDialog(component, z, str, str2, str3);
        }
        return confirmSmartcardDialog;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(okButton)) {
            this.answer = 0;
        } else if (actionEvent.getSource().equals(this.noButton)) {
            this.answer = 1;
        } else {
            this.answer = 2;
        }
        setVisible(false);
    }
}
