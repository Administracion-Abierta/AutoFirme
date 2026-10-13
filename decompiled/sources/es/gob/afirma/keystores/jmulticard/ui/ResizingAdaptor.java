package es.gob.afirma.keystores.jmulticard.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/jmulticard/ui/ResizingAdaptor.class */
final class ResizingAdaptor extends ComponentAdapter {
    private final transient AbstractJAccessibilityCustomDialog theCustomDialog;

    ResizingAdaptor(AbstractJAccessibilityCustomDialog abstractJAccessibilityCustomDialog) {
        this.theCustomDialog = abstractJAccessibilityCustomDialog;
    }

    public void componentResized(ComponentEvent componentEvent) {
        if (this.theCustomDialog != null) {
            adjustFontSize(this.theCustomDialog.getComponents());
        }
    }

    private void adjustFontSize(Component[] componentArr) {
        float imageResizeFactor;
        String name;
        float fRound;
        float fRound2 = 1.0f;
        if (this.theCustomDialog != null) {
            fRound2 = Math.round((this.theCustomDialog.getSize().getWidth() / ((double) this.theCustomDialog.getInitialWidth())) * (this.theCustomDialog.getSize().getHeight() / ((double) this.theCustomDialog.getInitialHeight())) * ((double) this.theCustomDialog.getMinimumRelation()));
        }
        for (Component component : componentArr) {
            if (isResizable(component)) {
                if (fRound2 > 10.0f) {
                    if (this.theCustomDialog != null) {
                        fRound = Math.round(fRound2 / getResizingFactorCustomDialog());
                    } else {
                        fRound = Math.round(fRound2 / getResizingFactorFileChooser());
                    }
                    component.setFont(component.getFont().deriveFont(getFontSize() + fRound));
                } else {
                    if (component instanceof JComboBox) {
                        component.setPreferredSize(new Dimension(100, 25));
                    }
                    component.setFont(component.getFont().deriveFont(getFontSize()));
                }
            }
            if (component instanceof JPanel) {
                TitledBorder border = ((JPanel) component).getBorder();
                if ((border instanceof TitledBorder) && ((name = component.getName()) == null || !"AccessibilityButtonsPanel".equalsIgnoreCase(name))) {
                    TitledBorder titledBorder = border;
                    float fRound3 = Math.round(fRound2 / getResizingFactorFrame());
                    if (titledBorder.getTitleFont() != null) {
                        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont((getFontSize() - 2.0f) + fRound3));
                    } else {
                        titledBorder.setTitleFont(component.getFont().deriveFont((getFontSize() - 2.0f) + fRound3));
                    }
                }
            }
            if ((component instanceof Container) && !(component instanceof JComboBox)) {
                adjustFontSize(((Container) component).getComponents());
            }
            if (component instanceof IconLabel) {
                IconLabel iconLabel = (IconLabel) component;
                if (iconLabel.getOriginalIcon() != null) {
                    resizeImage(getImageResizeFactor(0.0015d), component, iconLabel.getOriginalDimension().width, iconLabel.getOriginalDimension().height, 4);
                }
            }
            if ((component instanceof JButton) && ((JButton) component).getIcon() != null) {
                if (this.theCustomDialog != null) {
                    imageResizeFactor = getImageResizeFactor(0.0025d);
                } else {
                    imageResizeFactor = getImageResizeFactor(0.0015d);
                }
                resizeImageButton(imageResizeFactor, component);
            }
        }
    }

    private float getImageResizeFactor(double d) {
        float height = 0.0f;
        if (this.theCustomDialog != null) {
            height = (float) (((double) this.theCustomDialog.getHeight()) * d);
        }
        return height;
    }

    private static Image iconToImage(Icon icon) {
        return iconToImage(icon, new Dimension(icon.getIconWidth(), icon.getIconWidth()));
    }

    private static Image iconToImage(Icon icon, Dimension dimension) {
        if (icon instanceof ImageIcon) {
            return ((ImageIcon) icon).getImage();
        }
        BufferedImage bufferedImageCreateCompatibleImage = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(dimension.width, dimension.height);
        Graphics2D graphics2DCreateGraphics = bufferedImageCreateCompatibleImage.createGraphics();
        icon.paintIcon((Component) null, graphics2DCreateGraphics, 0, 0);
        graphics2DCreateGraphics.dispose();
        return bufferedImageCreateCompatibleImage;
    }

    private static void resizeImage(double d, Component component, int i, int i2, int i3) {
        Image imageIconToImage;
        if (component instanceof IconLabel) {
            IconLabel iconLabel = (IconLabel) component;
            imageIconToImage = iconToImage(iconLabel.getOriginalIcon(), iconLabel.getOriginalDimension());
        } else {
            imageIconToImage = iconToImage(((JLabel) component).getIcon());
        }
        ((JLabel) component).setIcon(new ImageIcon(imageIconToImage.getScaledInstance((int) Math.round(((double) (i * i3)) * d), (int) Math.round(((double) (i2 * i3)) * d), 4)));
    }

    private static void resizeImageButton(double d, Component component) {
        ImageIcon imageIcon;
        JButton jButton = (JButton) component;
        if (jButton.getName() != null && "maximizar".equalsIgnoreCase(jButton.getName())) {
            imageIcon = AccesiblityConstants.IMAGEICON_MAXIMIZE;
        } else if (jButton.getName() != null && "restaurar".equalsIgnoreCase(jButton.getName())) {
            imageIcon = AccesiblityConstants.IMAGEICONRESTORE;
        } else {
            imageIcon = new ImageIcon(iconToImage(jButton.getIcon()));
        }
        int iRound = (int) Math.round(50.0d * d);
        int i = iRound < 25 ? 25 : iRound;
        jButton.setIcon(new ImageIcon(imageIcon.getImage().getScaledInstance(i, i, 4)));
        jButton.setPreferredSize(new Dimension(i, i));
    }

    private static float getFontSize() {
        if (GeneralConfig.isBigFontSize()) {
            return 16.0f;
        }
        return 14.0f;
    }

    private static boolean isResizable(Component component) {
        return (((0 != 0 || (component instanceof JButton) || (component instanceof JLabel)) || (component instanceof JTextField)) || (component instanceof JPanel)) || (component instanceof JCheckBox);
    }

    private static float getResizingFactorFrame() {
        return 3.0f;
    }

    private static float getResizingFactorFileChooser() {
        return 3.0f;
    }

    private static float getResizingFactorCustomDialog() {
        return 2.0f;
    }
}
