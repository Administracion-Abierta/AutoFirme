package es.gob.afirma.keystores.jmulticard.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Locale;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/jmulticard/ui/AccesibilityUtils.class */
final class AccesibilityUtils {
    private static final boolean HIGH_CONTRAST;

    static boolean isHighContrast() {
        return HIGH_CONTRAST;
    }

    static {
        Object desktopProperty = Toolkit.getDefaultToolkit().getDesktopProperty("win.highContrast.on");
        if (desktopProperty instanceof Boolean) {
            HIGH_CONTRAST = ((Boolean) desktopProperty).booleanValue();
        } else if (System.getProperty("os.name") != null && System.getProperty("os.name").contains("inux")) {
            HIGH_CONTRAST = true;
        } else {
            HIGH_CONTRAST = false;
        }
    }

    private AccesibilityUtils() {
    }

    static void remarcar(JComponent jComponent) {
        if (GeneralConfig.isRemarked()) {
            if (jComponent instanceof JButton) {
                final JButton jButton = (JButton) jComponent;
                jButton.addFocusListener(new FocusListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.AccesibilityUtils.1
                    public void focusLost(FocusEvent focusEvent) {
                        if (jButton.getParent() instanceof JPanel) {
                            jButton.getParent().setBorder(BorderFactory.createEmptyBorder());
                        }
                    }

                    public void focusGained(FocusEvent focusEvent) {
                        if (GeneralConfig.isHighContrast() || AccesibilityUtils.isHighContrast()) {
                            if (jButton.getParent() instanceof JPanel) {
                                jButton.getParent().setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                            }
                        } else if (jButton.getParent() instanceof JPanel) {
                            jButton.getParent().setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                        }
                    }
                });
            }
            if (jComponent instanceof JTextField) {
                final JTextField jTextField = (JTextField) jComponent;
                jTextField.addFocusListener(new FocusListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.AccesibilityUtils.2
                    public void focusLost(FocusEvent focusEvent) {
                        jTextField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                    }

                    public void focusGained(FocusEvent focusEvent) {
                        if (GeneralConfig.isHighContrast() || AccesibilityUtils.isHighContrast()) {
                            jTextField.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                        } else {
                            jTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                        }
                    }
                });
            }
            if (jComponent instanceof JComboBox) {
                final JComboBox jComboBox = (JComboBox) jComponent;
                jComboBox.addFocusListener(new FocusListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.AccesibilityUtils.3
                    public void focusLost(FocusEvent focusEvent) {
                        jComboBox.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                    }

                    public void focusGained(FocusEvent focusEvent) {
                        if (GeneralConfig.isHighContrast() || AccesibilityUtils.isHighContrast()) {
                            jComboBox.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                        } else {
                            jComboBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                        }
                    }
                });
            }
            if (jComponent instanceof JLabel) {
                final JLabel jLabel = (JLabel) jComponent;
                jLabel.addFocusListener(new FocusListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.AccesibilityUtils.4
                    public void focusLost(FocusEvent focusEvent) {
                        jLabel.setBorder(BorderFactory.createEmptyBorder());
                    }

                    public void focusGained(FocusEvent focusEvent) {
                        if (GeneralConfig.isHighContrast() || AccesibilityUtils.isHighContrast()) {
                            jLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                        } else {
                            jLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                        }
                    }
                });
            }
            if (jComponent instanceof JCheckBox) {
                final JCheckBox jCheckBox = (JCheckBox) jComponent;
                jCheckBox.addFocusListener(new FocusListener() { // from class: es.gob.afirma.keystores.jmulticard.ui.AccesibilityUtils.5
                    public void focusLost(FocusEvent focusEvent) {
                        jCheckBox.getParent().setBorder(BorderFactory.createEmptyBorder());
                    }

                    public void focusGained(FocusEvent focusEvent) {
                        if (GeneralConfig.isHighContrast() || AccesibilityUtils.isHighContrast()) {
                            jCheckBox.getParent().setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                        } else {
                            jCheckBox.getParent().setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                        }
                    }
                });
            }
        }
    }

    static void setContrastColor(JComponent jComponent) {
        if (GeneralConfig.isHighContrast()) {
            if ((jComponent instanceof JComboBox) || (jComponent instanceof JPasswordField) || (jComponent instanceof JTextField)) {
                jComponent.setBackground(Color.WHITE);
                return;
            }
            if (jComponent instanceof JCheckBox) {
                jComponent.setForeground(Color.WHITE);
                return;
            }
            if ((jComponent instanceof JPanel) && (jComponent.getBorder() instanceof TitledBorder)) {
                jComponent.getBorder().setTitleColor(Color.WHITE);
            }
            jComponent.setForeground(Color.WHITE);
            jComponent.setBackground(Color.BLACK);
        }
    }

    static void setFontBold(JComponent jComponent) {
        TitledBorder border;
        if (jComponent instanceof JPanel) {
            if ((jComponent.getBorder() instanceof TitledBorder) && (border = jComponent.getBorder()) != null) {
                if (GeneralConfig.isFontBold()) {
                    border.setTitleFont(new Font(jComponent.getFont().getName(), 1, jComponent.getFont().getSize()));
                    return;
                } else {
                    border.setTitleFont(new Font(jComponent.getFont().getName(), 0, jComponent.getFont().getSize()));
                    return;
                }
            }
            return;
        }
        if (GeneralConfig.isFontBold()) {
            jComponent.setFont(new Font(jComponent.getFont().getName(), 1, jComponent.getFont().getSize()));
        } else {
            jComponent.setFont(new Font(jComponent.getFont().getName(), 0, jComponent.getFont().getSize()));
        }
    }

    static String remarkMnemonic(String str, int i) {
        String str2 = str;
        int iIndexOf = str.indexOf(i);
        if (iIndexOf == -1) {
            iIndexOf = str.indexOf(String.valueOf((char) i).toLowerCase(Locale.getDefault()));
        }
        if (iIndexOf != -1) {
            str2 = str.substring(0, iIndexOf) + "<u>" + str.charAt(iIndexOf) + "</u>" + str.substring(iIndexOf + 1);
        }
        return str2;
    }

    static void remarkMnemonic(AbstractButton abstractButton, int i) {
        int iIndexOf = abstractButton.getText().toLowerCase(Locale.getDefault()).indexOf(i);
        if (iIndexOf != -1) {
            abstractButton.setDisplayedMnemonicIndex(iIndexOf);
        }
    }

    static void showToolTip(boolean z, JWindow jWindow, JButton jButton, JLabel jLabel) {
        jLabel.setText(jButton.getToolTipText());
        jWindow.setBackground((Color) UIManager.get("ToolTip.background"));
        jLabel.setBackground((Color) UIManager.get("ToolTip.background"));
        jLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(0, 3, 0, 3)));
        jLabel.setFont((Font) UIManager.get("ToolTip.font"));
        jLabel.setOpaque(true);
        jWindow.add(jLabel);
        Point point = new Point();
        try {
            point = jButton.getLocationOnScreen();
        } catch (IllegalComponentStateException e) {
            Logger.getLogger("es.gob.afirma").warning("Error mostrando el tooltip: " + e);
        }
        int height = 0;
        if (jButton.getSize().getHeight() > 34.0d) {
            height = (int) (jButton.getSize().getHeight() * 0.5d);
        }
        jWindow.setLocation((int) point.getX(), ((int) point.getY()) + 30 + height);
        jWindow.pack();
        jWindow.setVisible(z);
    }
}
