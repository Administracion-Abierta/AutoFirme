package es.gob.afirma.standalone.ui.plugins;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.plugins.Permission;
import es.gob.afirma.standalone.plugins.PluginInfo;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/plugins/PermissionsDialog.class */
public class PermissionsDialog extends JDialog implements KeyListener {
    private static final long serialVersionUID = -4583047726394153567L;
    private final PluginInfo pluginInfo;
    private boolean accepted;

    PermissionsDialog(PluginInfo pluginInfo, Window window) {
        super(window);
        setTitle(SimpleAfirmaMessages.getString("PermissionsDialog.0"));
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.pluginInfo = pluginInfo;
        setSize(new Dimension(400, (int) Math.min(500.0d, LookAndFeelManager.getScreenSize().getHeight() * 0.8d)));
        setLocationRelativeTo(window);
        setDefaultCloseOperation(2);
        addWindowListener(new WindowAdapter() { // from class: es.gob.afirma.standalone.ui.plugins.PermissionsDialog.1
            public void windowClosing(WindowEvent windowEvent) {
                PermissionsDialog.this.setVisible(false);
                PermissionsDialog.this.dispose();
            }
        });
        createUI();
    }

    void createUI() {
        setIconImages(DesktopUtil.getIconImages());
        getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PermissionsDialog.1"));
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        JLabel jLabel = new JLabel() { // from class: es.gob.afirma.standalone.ui.plugins.PermissionsDialog.2
            public Dimension getPreferredSize() {
                return new Dimension(getParent().getSize().width, super.getPreferredSize().height);
            }
        };
        jLabel.setHorizontalAlignment(10);
        jLabel.setVerticalAlignment(1);
        jLabel.setText(generatePermissionsText(this.pluginInfo.getPermissions()));
        JScrollPane jScrollPane = new JScrollPane(jLabel);
        jScrollPane.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PermissionsDialog.2")));
        jScrollPane.setHorizontalScrollBarPolicy(31);
        jScrollPane.setVerticalScrollBarPolicy(20);
        JButton jButton = new JButton(SimpleAfirmaMessages.getString("PermissionsDialog.3"));
        jButton.addKeyListener(this);
        jButton.addActionListener(actionEvent -> {
            this.accepted = true;
            dispose();
        });
        JButton jButton2 = new JButton(SimpleAfirmaMessages.getString("PermissionsDialog.4"));
        jButton2.addKeyListener(this);
        jButton2.addActionListener(actionEvent2 -> {
            this.accepted = false;
            dispose();
        });
        JPanel jPanel = new JPanel(new FlowLayout(2));
        jPanel.add(jButton);
        jPanel.add(jButton2);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(11, 11, 11, 11);
        contentPane.add(jScrollPane, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.insets = new Insets(0, 11, 11, 11);
        contentPane.add(jPanel, gridBagConstraints);
    }

    private static String generatePermissionsText(Permission[] permissionArr) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        for (Permission permission : permissionArr) {
            sb.append("<b> - ").append(SimpleAfirmaMessages.getString("permission." + permission.toString())).append(":</b> ").append(SimpleAfirmaMessages.getString("permission." + permission.toString() + ".desc")).append("<br>");
        }
        sb.append("</html>");
        return sb.toString();
    }

    public boolean isAccepted() {
        return this.accepted;
    }

    public void keyPressed(KeyEvent keyEvent) {
    }

    public void keyTyped(KeyEvent keyEvent) {
    }

    public void keyReleased(KeyEvent keyEvent) {
        if (keyEvent != null && keyEvent.getKeyCode() == 27 && !Platform.OS.MACOSX.equals(Platform.getOS())) {
            this.accepted = false;
            dispose();
        }
    }
}
