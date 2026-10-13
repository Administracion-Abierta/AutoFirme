package es.gob.afirma.keystores.jmulticard.ui;

import es.gob.jmulticard.CancelledOperationException;
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
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import org.spongycastle.crypto.tls.ExtensionType;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/jmulticard/ui/InputPasswordSmartcardDialog.class */
public final class InputPasswordSmartcardDialog extends AbstractJAccessibilityCustomDialog implements ActionListener {
    private static final long serialVersionUID = 1;
    private static final int PIN_MIN_LENGTH = 4;
    private static final int PIN_MAX_LENGTH = 16;
    private static JButton okButton = null;
    private static JButton cancelButton = null;
    private static String cancellText = Messages.getString("PrincipalGUI.cancelar");
    private transient InfoLabel infoLabel;
    private transient JPanel buttonsPanel;
    private transient JPanel mainPanel;
    private transient JSecurePasswordLabel component;
    private transient JCheckBox useCacheCheckBox;
    private final transient IconLabel iconLabel;
    private transient int answer;

    static JButton getOkButton() {
        return okButton;
    }

    static JButton getCancelButton() {
        return cancelButton;
    }

    private InputPasswordSmartcardDialog(JDialog jDialog, boolean z, String str, String str2, String str3, boolean z2, boolean z3) {
        super(jDialog, z, true);
        this.infoLabel = null;
        this.buttonsPanel = null;
        this.mainPanel = null;
        this.component = null;
        this.useCacheCheckBox = null;
        this.iconLabel = new IconLabel();
        initComponents(str, str2, str3, z2, z3);
        setLocationRelativeTo(jDialog);
        pack();
    }

    private InputPasswordSmartcardDialog(Component component, boolean z, String str, String str2, String str3, boolean z2, boolean z3) {
        super(true);
        this.infoLabel = null;
        this.buttonsPanel = null;
        this.mainPanel = null;
        this.component = null;
        this.useCacheCheckBox = null;
        this.iconLabel = new IconLabel();
        setModal(z);
        initComponents(str, str2, str3, z2, z3);
        setLocationRelativeTo(component);
        pack();
    }

    private InputPasswordSmartcardDialog(JFrame jFrame, boolean z, String str, String str2, String str3, boolean z2, boolean z3) {
        super(jFrame, z, true);
        this.infoLabel = null;
        this.buttonsPanel = null;
        this.mainPanel = null;
        this.component = null;
        this.useCacheCheckBox = null;
        this.iconLabel = new IconLabel();
        initComponents(str, str2, str3, z2, z3);
        setLocationRelativeTo(jFrame);
        pack();
    }

    protected void initComponents(String str, String str2, String str3, boolean z, boolean z2) {
        int maxWidth = getMaxWidth();
        int maxHeight = getMaxHeight();
        setMinimumSize(new Dimension(getInitialWidth(), getInitialHeight()));
        setPreferredSize(new Dimension(getInitialWidth(), getInitialHeight()));
        setMaximumSize(new Dimension(maxWidth, maxHeight));
        if (GeneralConfig.isMaximized()) {
            setBounds(0, 0, maxWidth, maxHeight);
        } else if (GeneralConfig.isBigFontSize() || GeneralConfig.isFontBold()) {
            setMinimumSize(new Dimension(555, 250));
        }
        setTitle(str2);
        setDefaultCloseOperation(2);
        this.answer = 1;
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        this.mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 5, 0, 10);
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.weighty = 1.0d;
        setIconLabel(str3);
        JPanel jPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = 1;
        jPanel.add(this.iconLabel, gridBagConstraints2);
        this.infoLabel = new InfoLabel(str);
        this.infoLabel.setHorizontalAlignment(2);
        this.infoLabel.setVerticalAlignment(0);
        this.mainPanel.add(this.infoLabel, gridBagConstraints);
        this.component = new JSecurePasswordLabel(16);
        gridBagConstraints.gridy++;
        gridBagConstraints.insets = new Insets(2, 5, 2, 10);
        this.mainPanel.add(this.component, gridBagConstraints);
        if (z) {
            this.useCacheCheckBox = new JCheckBox(Messages.getString("InputPasswordSmartcardDialog.useCache"));
            this.useCacheCheckBox.setSelected(z2);
            gridBagConstraints.gridy++;
            this.mainPanel.add(this.useCacheCheckBox, gridBagConstraints);
        }
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
        gridBagConstraints3.gridheight = 2;
        gridBagConstraints3.weighty = 0.35d;
        gridBagConstraints3.weightx = 0.9d;
        contentPane.add(this.mainPanel, gridBagConstraints3);
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.gridy = 1;
        gridBagConstraints3.gridwidth = 2;
        gridBagConstraints3.gridheight = 3;
        gridBagConstraints3.weighty = 0.9d;
        gridBagConstraints3.weightx = 0.0d;
        contentPane.add(jPanel, gridBagConstraints3);
        gridBagConstraints3.gridx = 2;
        gridBagConstraints3.gridy = 3;
        gridBagConstraints3.gridwidth = 6;
        gridBagConstraints3.gridheight = 1;
        gridBagConstraints3.weighty = 0.65d;
        gridBagConstraints3.weightx = 0.0d;
        contentPane.add(this.buttonsPanel, gridBagConstraints3);
        pack();
    }

    protected void setIconLabel(String str) {
        if (str != null) {
            Icon imageIcon = new ImageIcon(InputPasswordSmartcardDialog.class.getResource(str));
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
        okButton.addKeyListener(new KeyListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.InputPasswordSmartcardDialog.1
            public void keyTyped(KeyEvent keyEvent) {
            }

            public void keyReleased(KeyEvent keyEvent) {
            }

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 10) {
                    InputPasswordSmartcardDialog.getOkButton().doClick();
                } else if (keyEvent.getKeyCode() == 27) {
                    InputPasswordSmartcardDialog.getCancelButton().doClick();
                }
            }
        });
        jPanel.add(okButton);
        this.buttonsPanel.add(jPanel, gridBagConstraints);
        okButton.addActionListener(this);
    }

    public static PasswordResult showInputPasswordDialog(Component component, boolean z, String str, int i, String str2, String str3, boolean z2, boolean z3) {
        InputPasswordSmartcardDialog instanceInputPasswordDialog = getInstanceInputPasswordDialog(component, z, str, str2, str3, z2, z3);
        okButton.setEnabled(false);
        instanceInputPasswordDialog.getRootPane().setDefaultButton((JButton) null);
        instanceInputPasswordDialog.component.addKeyListener(new KeyListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.InputPasswordSmartcardDialog.2
            public void keyTyped(KeyEvent keyEvent) {
            }

            public void keyReleased(KeyEvent keyEvent) {
                int passwordLength = InputPasswordSmartcardDialog.this.getComponent().getPasswordLength();
                if (passwordLength >= 4 && passwordLength <= 16) {
                    InputPasswordSmartcardDialog.getOkButton().setEnabled(true);
                    if (keyEvent.getKeyCode() == 10) {
                        InputPasswordSmartcardDialog.getOkButton().doClick();
                        return;
                    }
                    return;
                }
                InputPasswordSmartcardDialog.getOkButton().setEnabled(false);
            }

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 27) {
                    InputPasswordSmartcardDialog.getCancelButton().doClick();
                }
            }
        });
        instanceInputPasswordDialog.component.addAncestorListener(new RequestFocusListener());
        AccesibilityUtils.remarcar(instanceInputPasswordDialog.component);
        AccesibilityUtils.setContrastColor(instanceInputPasswordDialog.component);
        AccesibilityUtils.setFontBold(instanceInputPasswordDialog.component);
        instanceInputPasswordDialog.component.getAccessibleContext().setAccessibleName(str.replace("<br>", "") + "  ALT + " + i + ". ");
        instanceInputPasswordDialog.infoLabel.setLabelFor(instanceInputPasswordDialog.component);
        instanceInputPasswordDialog.infoLabel.setDisplayedMnemonic(i);
        instanceInputPasswordDialog.infoLabel.setText(AccesibilityUtils.remarkMnemonic(instanceInputPasswordDialog.infoLabel.getText(), i));
        if (instanceInputPasswordDialog.useCacheCheckBox != null) {
            instanceInputPasswordDialog.useCacheCheckBox.addKeyListener(new KeyListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.InputPasswordSmartcardDialog.3
                public void keyTyped(KeyEvent keyEvent) {
                }

                public void keyReleased(KeyEvent keyEvent) {
                }

                public void keyPressed(KeyEvent keyEvent) {
                    if (keyEvent.getKeyCode() == 10) {
                        InputPasswordSmartcardDialog.getOkButton().doClick();
                    } else if (keyEvent.getKeyCode() == 27) {
                        InputPasswordSmartcardDialog.getCancelButton().doClick();
                    }
                }
            });
            AccesibilityUtils.remarcar(instanceInputPasswordDialog.useCacheCheckBox);
            AccesibilityUtils.setContrastColor(instanceInputPasswordDialog.useCacheCheckBox);
            AccesibilityUtils.setFontBold(instanceInputPasswordDialog.useCacheCheckBox);
            instanceInputPasswordDialog.useCacheCheckBox.setMnemonic('e');
            AccesibilityUtils.remarkMnemonic((AbstractButton) instanceInputPasswordDialog.useCacheCheckBox, ExtensionType.negotiated_ff_dhe_groups);
        }
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 0, 10);
        cancelButton = instanceInputPasswordDialog.getButton(cancellText, 67);
        JPanel jPanel = new JPanel();
        jPanel.add(cancelButton);
        instanceInputPasswordDialog.buttonsPanel.add(jPanel, gridBagConstraints);
        cancelButton.addActionListener(instanceInputPasswordDialog);
        cancelButton.addKeyListener(new KeyListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.InputPasswordSmartcardDialog.4
            public void keyTyped(KeyEvent keyEvent) {
            }

            public void keyReleased(KeyEvent keyEvent) {
            }

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 10 || keyEvent.getKeyCode() == 27) {
                    InputPasswordSmartcardDialog.getCancelButton().doClick();
                }
            }
        });
        instanceInputPasswordDialog.infoLabel.setHorizontalAlignment(2);
        instanceInputPasswordDialog.component.setVisible(true);
        cancelButton.addActionListener(instanceInputPasswordDialog);
        instanceInputPasswordDialog.pack();
        instanceInputPasswordDialog.setSize(instanceInputPasswordDialog.getInitialWidth() + 1, instanceInputPasswordDialog.getInitialHeight() + 1);
        instanceInputPasswordDialog.setVisible(true);
        if (instanceInputPasswordDialog.getAnswer() == 0) {
            char[] password = instanceInputPasswordDialog.component.getPassword();
            instanceInputPasswordDialog.component.setText("");
            instanceInputPasswordDialog.component.setText(null);
            instanceInputPasswordDialog.component = null;
            instanceInputPasswordDialog.dispose();
            System.gc();
            boolean zIsSelected = false;
            if (instanceInputPasswordDialog.useCacheCheckBox != null) {
                zIsSelected = instanceInputPasswordDialog.useCacheCheckBox.isSelected();
            }
            return new PasswordResult(password, zIsSelected);
        }
        throw new CancelledOperationException("La insercion de contrasena ha sido cancelada por el usuario");
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

    JSecurePasswordLabel getComponent() {
        return this.component;
    }

    static InputPasswordSmartcardDialog getInstanceInputPasswordDialog(Component component, boolean z, String str, String str2, String str3, boolean z2, boolean z3) {
        return new InputPasswordSmartcardDialog(component, z, str, str2, str3, z2, z3);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(okButton)) {
            this.answer = 0;
        } else {
            this.answer = 2;
        }
        setVisible(false);
    }
}
