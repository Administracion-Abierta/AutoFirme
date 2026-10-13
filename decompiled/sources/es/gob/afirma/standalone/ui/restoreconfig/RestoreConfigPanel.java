package es.gob.afirma.standalone.ui.restoreconfig;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/restoreconfig/RestoreConfigPanel.class */
public final class RestoreConfigPanel extends JPanel implements KeyListener, DisposableInterface {
    private static final long serialVersionUID = 5353477830742383848L;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    static String newline = System.getProperty("line.separator");
    private final Window window;
    JTextArea taskOutput;
    JCheckBox firefoxIntegrationCb;

    RestoreConfigPanel(Window window) {
        this.window = window;
        createUI();
    }

    private void createUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        Component jLabel = new JLabel(SimpleAfirmaMessages.getString("RestoreConfigPanel.4"));
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(15, 15, 11, 15);
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridy++;
        add(jLabel, gridBagConstraints);
        this.taskOutput = new JTextArea();
        this.taskOutput.setMargin(new Insets(5, 5, 5, 5));
        this.taskOutput.setEditable(false);
        this.taskOutput.setLineWrap(true);
        Component jScrollPane = new JScrollPane(this.taskOutput, 20, 31);
        this.taskOutput.addKeyListener(this);
        gridBagConstraints.fill = 1;
        gridBagConstraints.insets = new Insets(0, 15, 0, 15);
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridy++;
        add(jScrollPane, gridBagConstraints);
        this.firefoxIntegrationCb = new JCheckBox(SimpleAfirmaMessages.getString("RestoreConfigPanel.7"));
        this.firefoxIntegrationCb.setToolTipText(SimpleAfirmaMessages.getString("RestoreConfigPanel.8"));
        this.firefoxIntegrationCb.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("RestoreConfigPanel.9"));
        this.firefoxIntegrationCb.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("RestoreConfigPanel.8"));
        if (Platform.getOS() == Platform.OS.WINDOWS || Platform.getOS() == Platform.OS.MACOSX) {
            gridBagConstraints.fill = 2;
            gridBagConstraints.insets = new Insets(7, 15, 0, 15);
            gridBagConstraints.gridy++;
            gridBagConstraints.weighty = 0.0d;
            add(this.firefoxIntegrationCb, gridBagConstraints);
        }
        JPanel jPanelCreateButtonsPanel = createButtonsPanel();
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(7, 11, 0, 11);
        gridBagConstraints.gridy++;
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.ipady = 11;
        add(jPanelCreateButtonsPanel, gridBagConstraints);
    }

    private JPanel createButtonsPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout(4));
        JButton jButton = new JButton(SimpleAfirmaMessages.getString("RestoreConfigPanel.5"));
        jButton.setMnemonic('C');
        jButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("RestoreConfigPanel.6"));
        jButton.addKeyListener(this);
        jButton.addActionListener(actionEvent -> {
            disposeInterface();
        });
        JButton jButton2 = new JButton(SimpleAfirmaMessages.getString("RestoreConfigPanel.1"));
        jButton2.setMnemonic('R');
        jButton2.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("RestoreConfigPanel.2"));
        jButton2.addKeyListener(this);
        jButton2.addActionListener(actionEvent2 -> {
            this.taskOutput.setText((String) null);
            jButton2.setEnabled(false);
            new Thread(() -> {
                new RestoreConfigManager().restoreConfig(this);
            }).start();
            jButton2.setEnabled(true);
        });
        if (Platform.OS.MACOSX.equals(Platform.getOS())) {
            jPanel.add(jButton);
            jPanel.add(jButton2);
        } else {
            jPanel.add(jButton2);
            jPanel.add(jButton);
        }
        return jPanel;
    }

    Window getParentWindow() {
        return this.window;
    }

    @Override // es.gob.afirma.standalone.ui.restoreconfig.DisposableInterface
    public void disposeInterface() {
        getParentWindow().dispose();
    }

    public void keyPressed(KeyEvent keyEvent) {
    }

    public void keyReleased(KeyEvent keyEvent) {
        if (keyEvent != null && keyEvent.getKeyCode() == 27 && !Platform.OS.MACOSX.equals(Platform.getOS())) {
            disposeInterface();
        }
    }

    public void keyTyped(KeyEvent keyEvent) {
    }

    public void appendMessage(String str) {
        SwingUtilities.invokeLater(() -> {
            this.taskOutput.setText(this.taskOutput.getText() + str + newline);
            this.taskOutput.setCaretPosition(this.taskOutput.getDocument().getLength());
        });
    }
}
