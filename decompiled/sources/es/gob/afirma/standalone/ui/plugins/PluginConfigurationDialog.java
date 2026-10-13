package es.gob.afirma.standalone.ui.plugins;

import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.plugins.AfirmaPlugin;
import es.gob.afirma.standalone.plugins.ConfigurationPanel;
import es.gob.afirma.standalone.plugins.manager.PluginException;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/plugins/PluginConfigurationDialog.class */
public class PluginConfigurationDialog extends JDialog {
    private static final long serialVersionUID = 4377003209574629672L;
    private final ConfigurationPanel configPanel;
    boolean accepted;

    public PluginConfigurationDialog(Window window, AfirmaPlugin afirmaPlugin) throws PluginException {
        super(window);
        try {
            this.configPanel = afirmaPlugin.buildConfigurationPanel();
            this.accepted = false;
            setTitle(afirmaPlugin.getInfo().getName());
            setLayout(new GridBagLayout());
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = 2;
            gridBagConstraints.gridy = 0;
            add(this.configPanel, gridBagConstraints);
            gridBagConstraints.gridy++;
            gridBagConstraints.insets = new Insets(11, 0, 0, 0);
            add(createButtonsPanel(), gridBagConstraints);
            setResizable(true);
            setDefaultCloseOperation(2);
            pack();
            Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
            setLocation(centerPoint.x - (getWidth() / 2), centerPoint.y - (getHeight() / 2));
        } catch (Exception e) {
            throw new PluginException("Error al construir el dialogo de configuracion", e);
        }
    }

    private JPanel createButtonsPanel() {
        JPanel jPanel = new JPanel(new FlowLayout(2));
        JButton jButton = new JButton(SimpleAfirmaMessages.getString("PluginConfigurationDialog.0"));
        jButton.addActionListener(actionEvent -> {
            this.accepted = true;
            dispose();
        });
        JButton jButton2 = new JButton(SimpleAfirmaMessages.getString("PluginConfigurationDialog.1"));
        jButton2.addActionListener(actionEvent2 -> {
            this.accepted = false;
            dispose();
        });
        jPanel.add(jButton);
        jPanel.add(jButton2);
        return jPanel;
    }

    public void init(Properties properties) {
        this.accepted = false;
        this.configPanel.init(properties);
    }

    public Properties recoverConfig() {
        return this.configPanel.getConfiguration();
    }

    public boolean isAccepted() {
        return this.accepted;
    }
}
