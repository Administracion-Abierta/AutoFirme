package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/DNIeWaitPanel.class */
public final class DNIeWaitPanel extends JPanel implements KeyListener {
    private static final long serialVersionUID = -8543615798397861866L;
    public static final String PROP_HELP_REQUESTED = "F1";
    public static final String PROP_DNIE_REQUESTED = "DNI";
    public static final String PROP_DNIE_REJECTED = "NoDNI";
    private static final int DEFAULT_WINDOW_WIDTH = 600;
    private static final int DEFAULT_WINDOW_HEIGHT = 420;

    public DNIeWaitPanel(PropertyChangeListener propertyChangeListener) {
        super(true);
        createUI(propertyChangeListener);
    }

    private void createUI(PropertyChangeListener propertyChangeListener) {
        Component scalablePane;
        addPropertyChangeListener(propertyChangeListener);
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(600, DEFAULT_WINDOW_HEIGHT));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        Component jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        JButton jButton = new JButton(SimpleAfirmaMessages.getString("DNIeWaitPanel.4"));
        jButton.addActionListener(actionEvent -> {
            firePropertyChange(PROP_DNIE_REQUESTED, false, true);
        });
        jButton.setMnemonic('C');
        jButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString(SimpleAfirmaMessages.getString("DNIeWaitPanel.5")));
        jButton.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString(SimpleAfirmaMessages.getString("DNIeWaitPanel.6")));
        jButton.requestFocus();
        jButton.addKeyListener(this);
        jPanel.add(jButton);
        JButton jButton2 = new JButton(SimpleAfirmaMessages.getString("DNIeWaitPanel.0"));
        jButton2.addActionListener(actionEvent2 -> {
            firePropertyChange(PROP_DNIE_REJECTED, false, true);
        });
        jButton2.setMnemonic('u');
        jButton2.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("DNIeWaitPanel.1"));
        jButton2.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("DNIeWaitPanel.2"));
        jButton2.addKeyListener(this);
        jPanel.add(jButton2);
        Component resizingTextPanel = new ResizingTextPanel(SimpleAfirmaMessages.getString("DNIeWaitPanel.3"));
        Component resizingTextPanel2 = new ResizingTextPanel(SimpleAfirmaMessages.getString("DNIeWaitPanel.7"));
        resizingTextPanel.setFocusable(false);
        resizingTextPanel2.setFocusable(false);
        try {
            InputStream resourceAsStream = getClass().getResourceAsStream("/resources/lectordnie.png");
            Throwable th = null;
            try {
                BufferedImage bufferedImage = ImageIO.read(resourceAsStream);
                if (resourceAsStream != null) {
                    if (0 != 0) {
                        try {
                            resourceAsStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        resourceAsStream.close();
                    }
                }
                scalablePane = new ScalablePane(bufferedImage, true);
                scalablePane.setBackground(new Color(255, 255, 255, 0));
                scalablePane.setFocusable(false);
            } catch (Throwable th3) {
                if (resourceAsStream != null) {
                    if (0 != 0) {
                        try {
                            resourceAsStream.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        resourceAsStream.close();
                    }
                }
                throw th3;
            }
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").warning("No se ha podido cargar la imagen explicativa de insercion de DNIe, esta no se mostrara: " + e);
            scalablePane = null;
        }
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            setBackground(LookAndFeelManager.SECUNDARY_COLOR);
            jPanel.setBackground(LookAndFeelManager.SECUNDARY_COLOR);
            resizingTextPanel.setBackground(LookAndFeelManager.SECUNDARY_COLOR);
            resizingTextPanel2.setBackground(LookAndFeelManager.SECUNDARY_COLOR);
        }
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        if (scalablePane != null) {
            add(scalablePane, gridBagConstraints);
        }
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 50;
        add(resizingTextPanel, gridBagConstraints);
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipady = 30;
        add(resizingTextPanel2, gridBagConstraints);
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.insets = new Insets(20, 0, 0, 0);
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipady = 0;
        add(jPanel, gridBagConstraints);
        gridBagConstraints.gridy = 4;
        Component jCheckBox = new JCheckBox(SimpleAfirmaMessages.getString("DNIeWaitPanel.8"), PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_HIDE_DNIE_START_SCREEN));
        jCheckBox.addKeyListener(this);
        jCheckBox.addChangeListener(changeEvent -> {
            PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_GENERAL_HIDE_DNIE_START_SCREEN, jCheckBox.isSelected());
        });
        if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            jCheckBox.setBackground(LookAndFeelManager.SECUNDARY_COLOR);
        }
        add(jCheckBox, gridBagConstraints);
    }

    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent != null && keyEvent.getKeyCode() == 27) {
            firePropertyChange(PROP_DNIE_REJECTED, false, true);
        } else if (keyEvent != null && keyEvent.getKeyCode() == 112 && !Platform.OS.MACOSX.equals(Platform.getOS())) {
            firePropertyChange(PROP_HELP_REQUESTED, false, true);
        }
    }

    public void keyReleased(KeyEvent keyEvent) {
    }

    public void keyTyped(KeyEvent keyEvent) {
    }
}
