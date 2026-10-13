package es.gob.afirma.standalone.ui.plugins;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirma;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.plugins.AfirmaPlugin;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/plugins/PluginsManagementPanel.class */
public final class PluginsManagementPanel extends JPanel {
    private static final long serialVersionUID = 8384222173067817402L;
    private final Window window;
    private JList<AfirmaPlugin> pluginsList;
    private JButton addButton;
    private JButton removeButton;
    private JLabel pluginInfoPane;
    private JButton configButton;
    private JButton closeButton;
    final PluginsManagementHandler eventsHandler = new PluginsManagementHandler(this, SimpleAfirma.getPluginsManager());

    PluginsManagementPanel(Window window) {
        this.window = window;
        createUI();
        this.eventsHandler.registerComponents();
        new Thread(() -> {
            this.eventsHandler.loadViewData();
        }).start();
    }

    private void createUI() {
        setLayout(new GridBagLayout());
        JPanel jPanelCreatePluginsListPanel = createPluginsListPanel();
        jPanelCreatePluginsListPanel.setMinimumSize(new Dimension(250, 500));
        jPanelCreatePluginsListPanel.setPreferredSize(new Dimension(250, 500));
        JPanel jPanelCreatePluginInfoPanel = createPluginInfoPanel();
        JPanel jPanelCreateButtonsPanel = createButtonsPanel();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 3;
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(11, 11, 0, 0);
        add(jPanelCreatePluginsListPanel, gridBagConstraints);
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridx++;
        gridBagConstraints.insets = new Insets(11, 11, 0, 11);
        add(jPanelCreatePluginInfoPanel, gridBagConstraints);
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy++;
        gridBagConstraints.ipady = 11;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        add(jPanelCreateButtonsPanel, gridBagConstraints);
    }

    private JPanel createPluginsListPanel() {
        JPanel jPanel = new JPanel(new GridBagLayout());
        jPanel.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PluginsManagementPanel.0")));
        this.pluginsList = new JList<>(new DefaultListModel());
        this.pluginsList.setSelectionMode(1);
        JScrollPane jScrollPane = new JScrollPane(this.pluginsList);
        if (Platform.getOS() == Platform.OS.MACOSX) {
            jScrollPane.setHorizontalScrollBarPolicy(32);
            jScrollPane.setVerticalScrollBarPolicy(22);
        } else {
            jScrollPane.setHorizontalScrollBarPolicy(30);
        }
        this.addButton = new JButton(SimpleAfirmaMessages.getString("PluginsManagementPanel.1"));
        this.addButton.setMnemonic('A');
        this.addButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PluginsManagementPanel.7"));
        this.removeButton = new JButton(SimpleAfirmaMessages.getString("PluginsManagementPanel.2"));
        this.removeButton.setMnemonic('E');
        this.removeButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PluginsManagementPanel.8"));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel.add(jScrollPane, gridBagConstraints);
        gridBagConstraints.weightx = 0.5d;
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy++;
        gridBagConstraints.insets = new Insets(6, 0, 0, 0);
        jPanel.add(this.addButton, gridBagConstraints);
        gridBagConstraints.gridx++;
        gridBagConstraints.insets = new Insets(6, 6, 0, 0);
        jPanel.add(this.removeButton, gridBagConstraints);
        return jPanel;
    }

    private JPanel createPluginInfoPanel() {
        JPanel jPanel = new JPanel(new GridBagLayout());
        jPanel.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PluginsManagementPanel.3")));
        JScrollPane jScrollPane = new JScrollPane(createScrollableTextPanel());
        jScrollPane.setBorder(BorderFactory.createLineBorder(LookAndFeelManager.DEFAULT_COLOR, 1));
        jScrollPane.setFocusable(true);
        jScrollPane.addFocusListener(new FocusListener() { // from class: es.gob.afirma.standalone.ui.plugins.PluginsManagementPanel.1
            public void focusLost(FocusEvent focusEvent) {
                ((JComponent) focusEvent.getSource()).setBorder(BorderFactory.createLineBorder(LookAndFeelManager.DEFAULT_COLOR, 1));
            }

            public void focusGained(FocusEvent focusEvent) {
                ((JComponent) focusEvent.getSource()).setBorder(BorderFactory.createLineBorder(Color.black, 1));
            }
        });
        jScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        JPanel jPanel2 = new JPanel(new FlowLayout(4));
        this.configButton = new JButton(SimpleAfirmaMessages.getString("PluginsManagementPanel.4"));
        this.configButton.setVisible(false);
        jPanel2.add(this.configButton);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridy = 0;
        jPanel.add(jScrollPane, gridBagConstraints);
        gridBagConstraints.weightx = 0.0d;
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.gridy++;
        jPanel.add(jPanel2, gridBagConstraints);
        return jPanel;
    }

    private JComponent createScrollableTextPanel() {
        this.pluginInfoPane = new JLabel() { // from class: es.gob.afirma.standalone.ui.plugins.PluginsManagementPanel.2
            public Dimension getPreferredSize() {
                return new Dimension(getParent().getSize().width, super.getPreferredSize().height);
            }
        };
        this.pluginInfoPane.setFocusable(false);
        JPanel jPanel = new JPanel() { // from class: es.gob.afirma.standalone.ui.plugins.PluginsManagementPanel.3
            public Dimension getPreferredSize() {
                return new Dimension(getParent().getSize().width, super.getPreferredSize().height);
            }
        };
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 3;
        gridBagConstraints.gridy = 0;
        jPanel.add(this.pluginInfoPane, gridBagConstraints);
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridy++;
        jPanel.add(new JPanel(), gridBagConstraints);
        return jPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout(4));
        this.closeButton = new JButton(SimpleAfirmaMessages.getString("PluginsManagementPanel.5"));
        this.closeButton.setMnemonic('C');
        this.closeButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PluginsManagementPanel.6"));
        jPanel.add(this.closeButton);
        return jPanel;
    }

    Window getParentWindow() {
        return this.window;
    }

    JList<AfirmaPlugin> getPluginsList() {
        return this.pluginsList;
    }

    JButton getAddButton() {
        return this.addButton;
    }

    JButton getRemoveButton() {
        return this.removeButton;
    }

    JLabel getPluginInfoPane() {
        return this.pluginInfoPane;
    }

    JButton getConfigButton() {
        return this.configButton;
    }

    JButton getCloseButton() {
        return this.closeButton;
    }
}
