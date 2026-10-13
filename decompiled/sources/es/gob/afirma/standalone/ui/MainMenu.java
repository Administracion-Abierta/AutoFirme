package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.SimpleAfirma;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.VisorFirma;
import es.gob.afirma.standalone.plugins.GenericMenuOption;
import es.gob.afirma.standalone.plugins.manager.PluginException;
import es.gob.afirma.standalone.plugins.manager.PluginLoader;
import es.gob.afirma.standalone.ui.plugins.PluginsManagementDialog;
import es.gob.afirma.standalone.ui.preferences.PreferencesDialog;
import es.gob.afirma.standalone.ui.restoreconfig.RestoreConfigDialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.File;
import java.util.EventObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/MainMenu.class */
public final class MainMenu extends JMenuBar {
    private static final long serialVersionUID = -8361808353554036015L;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private final JMenuItem firmarMenuItem = new JMenuItem();
    private final JMenuItem abrirMenuItem = new JMenuItem();
    private final JFrame parent;
    private final SimpleAfirma saf;

    public JFrame getParentComponent() {
        return this.parent;
    }

    SimpleAfirma getSimpleAfirma() {
        return this.saf;
    }

    public MainMenu(JFrame jFrame, SimpleAfirma simpleAfirma) {
        this.saf = simpleAfirma;
        this.parent = jFrame;
        createUI();
    }

    private void createUI() {
        boolean zEquals = Platform.OS.MACOSX.equals(Platform.getOS());
        JMenu jMenu = new JMenu();
        jMenu.setText(SimpleAfirmaMessages.getString("MainMenu.0"));
        jMenu.setMnemonic(18);
        jMenu.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("MainMenu.1"));
        jMenu.setEnabled(true);
        this.abrirMenuItem.setText(SimpleAfirmaMessages.getString("MainMenu.2"));
        this.abrirMenuItem.setAccelerator(KeyStroke.getKeyStroke(65, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        this.abrirMenuItem.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("MainMenu.3"));
        this.abrirMenuItem.addActionListener(actionEvent -> {
            try {
                getSimpleAfirma().loadFileToSign(AOUIFactory.getLoadFiles(SimpleAfirmaMessages.getString("MainMenu.4"), null, null, null, null, false, true, DesktopUtil.getDefaultDialogsIcon(), this)[0], null);
            } catch (AOCancelledOperationException e) {
            }
        });
        jMenu.add(this.abrirMenuItem);
        this.firmarMenuItem.setText(SimpleAfirmaMessages.getString("MainMenu.5"));
        this.firmarMenuItem.setAccelerator(KeyStroke.getKeyStroke(70, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        this.firmarMenuItem.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("MainMenu.6"));
        this.firmarMenuItem.setEnabled(false);
        this.firmarMenuItem.addActionListener(actionEvent2 -> {
            getSimpleAfirma().signLoadedFile();
        });
        jMenu.add(this.firmarMenuItem);
        JMenuItem jMenuItem = new JMenuItem(SimpleAfirmaMessages.getString("MainMenu.34"));
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(86, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        jMenuItem.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("MainMenu.34"));
        jMenuItem.setMnemonic(86);
        jMenuItem.addActionListener(actionEvent3 -> {
            viewSignature(this);
        });
        jMenu.add(jMenuItem);
        JMenu jMenu2 = new JMenu(SimpleAfirmaMessages.getString("MainMenu.32"));
        jMenu2.setMnemonic(82);
        jMenu2.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("MainMenu.33"));
        jMenu2.setEnabled(true);
        if (!zEquals) {
            jMenu.addSeparator();
            JMenuItem jMenuItem2 = new JMenuItem(SimpleAfirmaMessages.getString("MainMenu.7"));
            jMenuItem2.setAccelerator(KeyStroke.getKeyStroke(115, 8));
            jMenuItem2.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("MainMenu.8"));
            jMenuItem2.addActionListener(actionEvent4 -> {
                exitApplication();
            });
            jMenuItem2.setMnemonic(76);
            jMenu.add(jMenuItem2);
        }
        add(jMenu);
        JMenuItem jMenuItem3 = new JMenuItem(SimpleAfirmaMessages.getString("MainMenu.20"));
        jMenuItem3.setAccelerator(KeyStroke.getKeyStroke(82, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        jMenuItem3.setMnemonic(82);
        jMenuItem3.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("MainMenu.20"));
        jMenuItem3.addActionListener(actionEvent5 -> {
            showRestoreConfig();
        });
        jMenu2.add(jMenuItem3);
        JMenuItem jMenuItem4 = new JMenuItem(SimpleAfirmaMessages.getString("MainMenu.37"));
        jMenuItem4.setAccelerator(KeyStroke.getKeyStroke(71, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        jMenuItem4.setMnemonic(71);
        jMenuItem4.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("MainMenu.37"));
        jMenuItem4.addActionListener(actionEvent6 -> {
            showPlugingManagement();
        });
        jMenu2.add(jMenuItem4);
        add(jMenu2);
        if (!zEquals) {
            JMenuItem jMenuItem5 = new JMenuItem(SimpleAfirmaMessages.getString("MainMenu.12"));
            jMenuItem5.setAccelerator(KeyStroke.getKeyStroke(80, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            jMenuItem5.setMnemonic(80);
            jMenuItem5.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("MainMenu.16"));
            jMenuItem5.addActionListener(actionEvent7 -> {
                showPreferences();
            });
            jMenu2.addSeparator();
            jMenu2.add(jMenuItem5);
        } else {
            try {
                OSXHandler.setPreferencesHandler(this, getClass().getDeclaredMethod("showPreferences", EventObject.class));
            } catch (Error | Exception e) {
                Logger.getLogger("es.gob.afirma").warning("No ha sido posible establecer el menu de preferencias de OS X: " + e);
            }
        }
        List<GenericMenuOption> pluginsMenus = PluginsUiComponentsBuilder.getPluginsMenus();
        if (pluginsMenus != null && !pluginsMenus.isEmpty()) {
            for (GenericMenuOption genericMenuOption : pluginsMenus) {
                JMenu jMenu3 = new JMenu(genericMenuOption.getTitle());
                addSubMenus(jMenu3, genericMenuOption, getParentComponent());
                add(jMenu3);
            }
        }
        add(Box.createHorizontalGlue());
        JMenu jMenu4 = new JMenu(SimpleAfirmaMessages.getString("MainMenu.9"));
        jMenu4.setMnemonic(89);
        jMenu4.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("MainMenu.10"));
        JMenuItem jMenuItem6 = new JMenuItem();
        jMenuItem6.setText(SimpleAfirmaMessages.getString("MainMenu.11"));
        jMenuItem6.setAccelerator(KeyStroke.getKeyStroke(DNIeWaitPanel.PROP_HELP_REQUESTED));
        jMenuItem6.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("MainMenu.13"));
        jMenuItem6.addActionListener(actionEvent8 -> {
            SimpleAfirma.showHelp("Autofirma.html");
        });
        jMenu4.add(jMenuItem6);
        if (!zEquals) {
            jMenu4.addSeparator();
            JMenuItem jMenuItem7 = new JMenuItem(SimpleAfirmaMessages.getString("MainMenu.15"));
            jMenuItem7.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("MainMenu.17"));
            jMenuItem7.setAccelerator(KeyStroke.getKeyStroke(67, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            jMenuItem7.addActionListener(actionEvent9 -> {
                AboutDialog.showAbout(getParentComponent() == null ? this : getParentComponent());
            });
            jMenuItem7.setMnemonic(67);
            jMenu4.add(jMenuItem7);
        }
        add(jMenu4);
        if (!zEquals) {
            this.abrirMenuItem.setMnemonic(66);
            jMenuItem6.setMnemonic(85);
            this.firmarMenuItem.setMnemonic(70);
            return;
        }
        try {
            OSXHandler.setAboutHandler(this, getClass().getDeclaredMethod("showAbout", EventObject.class));
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "Error al abrir la ayuda", (Throwable) e2);
        }
        try {
            OSXHandler.setQuitHandler(this, getClass().getDeclaredMethod("exitApplication", EventObject.class, Object.class));
        } catch (Exception e3) {
            LOGGER.log(Level.WARNING, "Error al cerrar la aplicacion", (Throwable) e3);
        }
    }

    public static void addSubMenus(JMenu jMenu, GenericMenuOption genericMenuOption, JFrame jFrame) {
        for (GenericMenuOption genericMenuOption2 : genericMenuOption.getMenus()) {
            JMenu jMenu2 = new JMenu(genericMenuOption2.getTitle());
            if (genericMenuOption2.getMenus() != null && !genericMenuOption2.getMenus().isEmpty()) {
                addSubMenus(jMenu2, genericMenuOption2, jFrame);
            }
            if (genericMenuOption2.getMenus() == null || genericMenuOption2.getMenus().isEmpty()) {
                JMenuItem jMenuItem = new JMenuItem(genericMenuOption2.getTitle());
                jMenuItem.addActionListener(actionEvent -> {
                    try {
                        doMenuAction(genericMenuOption2.getActionClassName(), jFrame);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Se ha producido un error inesperado durante la ejecucion de la accion del plugin", (Throwable) e);
                        AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.52"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e);
                    }
                });
                jMenu.add(jMenuItem);
            } else {
                jMenu.add(jMenu2);
            }
        }
    }

    public void setEnabledOpenCommand(boolean z) {
        if (this.abrirMenuItem != null) {
            this.abrirMenuItem.setEnabled(z);
        }
    }

    public void setEnabledSignCommand(boolean z) {
        if (this.firmarMenuItem != null) {
            this.firmarMenuItem.setEnabled(z);
        }
    }

    void showPreferences(EventObject eventObject) {
        PreferencesDialog.show(getParentComponent(), true);
    }

    void showPreferences() {
        PreferencesDialog.show(getParentComponent(), true);
    }

    void showRestoreConfig() {
        RestoreConfigDialog.show(getParentComponent(), true);
    }

    void showPlugingManagement() {
        PluginsManagementDialog.show(getParentComponent(), true);
        if (getSimpleAfirma().getCurrentPanel() instanceof PluginButtonsContainer) {
            getSimpleAfirma().getCurrentPanel().refreshPluginButtonsContainer();
        }
    }

    static void viewSignature(Object obj) {
        try {
            File[] loadFiles = AOUIFactory.getLoadFiles(SimpleAfirmaMessages.getString("MainMenu.35"), null, null, new String[]{"csig", "xsig", "pdf", "sig", "p7s"}, SimpleAfirmaMessages.getString("MainMenu.36"), false, false, null, obj);
            if (loadFiles != null && loadFiles.length > 0 && loadFiles[0] != null && loadFiles[0].isFile()) {
                new VisorFirma(false, null).initialize(false, loadFiles[0]);
            }
        } catch (AOCancelledOperationException e) {
        }
    }

    void showAbout(EventObject eventObject) {
        AboutDialog.showAbout(getParentComponent() == null ? this : getParentComponent());
    }

    boolean exitApplication(EventObject eventObject, Object obj) {
        return this.saf.askForClosing();
    }

    boolean exitApplication() {
        return this.saf.askForClosing();
    }

    public static void doMenuAction(String str, Frame frame) throws PluginException {
        PluginLoader.getPluginAction(str).start(frame);
    }
}
