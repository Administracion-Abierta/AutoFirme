package es.gob.afirma.standalone.ui.plugins;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.CommandLineParameters;
import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.plugins.AfirmaPlugin;
import es.gob.afirma.standalone.plugins.Permission;
import es.gob.afirma.standalone.plugins.PluginControlledException;
import es.gob.afirma.standalone.plugins.PluginInfo;
import es.gob.afirma.standalone.plugins.manager.JarNoSignedException;
import es.gob.afirma.standalone.plugins.manager.JarVerifier;
import es.gob.afirma.standalone.plugins.manager.PermissionChecker;
import es.gob.afirma.standalone.plugins.manager.PluginException;
import es.gob.afirma.standalone.plugins.manager.PluginInstalledException;
import es.gob.afirma.standalone.plugins.manager.PluginsManager;
import es.gob.afirma.standalone.plugins.manager.PluginsPreferences;
import es.gob.afirma.standalone.so.macos.MacUtils;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/plugins/PluginsManagementHandler.class */
public class PluginsManagementHandler implements KeyListener, ListSelectionListener {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String[] PLUGIN_EXTENSIONS = {"jar", "zip"};
    private final PluginsManagementPanel view;
    private final PluginsManager pluginsManager;
    private List<AfirmaPlugin> pluginsList;

    public PluginsManagementHandler(PluginsManagementPanel pluginsManagementPanel, PluginsManager pluginsManager) {
        this.view = pluginsManagementPanel;
        this.pluginsManager = pluginsManager;
    }

    void registerComponents() {
        this.view.getPluginsList().addKeyListener(this);
        this.view.getPluginsList().addListSelectionListener(this);
        this.view.getAddButton().addKeyListener(this);
        this.view.getAddButton().addActionListener(actionEvent -> {
            addPlugin();
        });
        this.view.getRemoveButton().addKeyListener(this);
        this.view.getRemoveButton().addActionListener(actionEvent2 -> {
            removePlugin();
        });
        this.view.getConfigButton().addKeyListener(this);
        this.view.getConfigButton().addActionListener(actionEvent3 -> {
            configPlugin();
        });
        this.view.getCloseButton().addKeyListener(this);
        this.view.getCloseButton().addActionListener(actionEvent4 -> {
            this.view.getParentWindow().dispose();
        });
    }

    void addPlugin() {
        try {
            File fileSelectPluginFile = selectPluginFile();
            if (!verifyJar(fileSelectPluginFile)) {
                return;
            }
            try {
                AfirmaPlugin afirmaPluginLoadPluginFromFiles = PluginsManager.loadPluginFromFiles(new File[]{fileSelectPluginFile});
                PluginInfo info = afirmaPluginLoadPluginFromFiles.getInfo();
                PluginsManager.closePlugin(afirmaPluginLoadPluginFromFiles);
                if (info.getPermissions() != null && info.getPermissions().length > 0 && !showPermissionDialog(info, this.view.getParentWindow())) {
                    return;
                }
                addPlugin(fileSelectPluginFile, info);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "No se pudo cargar el plugin", (Throwable) e);
                showError(SimpleAfirmaMessages.getString("PluginsManagementHandler.0"), null);
            }
        } catch (AOCancelledOperationException e2) {
        }
    }

    private static boolean showPermissionDialog(PluginInfo pluginInfo, Window window) {
        PermissionsDialog permissionsDialog = new PermissionsDialog(pluginInfo, window);
        permissionsDialog.setVisible(true);
        return permissionsDialog.isAccepted();
    }

    private void addPlugin(File file, PluginInfo pluginInfo) {
        AfirmaPlugin afirmaPluginInstallPlugin;
        try {
            afirmaPluginInstallPlugin = this.pluginsManager.installPlugin(file, pluginInfo.getInternalName());
        } catch (PluginControlledException e) {
            LOGGER.log(Level.WARNING, "El propio plugin devolvio un error durante su instalacion", (Throwable) e);
            showError(e.getLocalizedMessage(), e);
            return;
        } catch (PluginInstalledException e2) {
            LOGGER.log(Level.WARNING, "Ya existe una version instalada del plugin");
            if (JOptionPane.showConfirmDialog(this.view, String.format(SimpleAfirmaMessages.getString("PluginsManagementHandler.1"), pluginInfo.getName()), SimpleAfirmaMessages.getString("PluginsManagementHandler.16"), 1) != 0) {
                return;
            }
            try {
                removeLoadedPlugin(pluginInfo);
                afirmaPluginInstallPlugin = this.pluginsManager.installPlugin(file, pluginInfo.getInternalName());
            } catch (Exception e3) {
                LOGGER.log(Level.WARNING, "No se ha podido reemplazar la version preexistente del plugin", (Throwable) e3);
                showError(SimpleAfirmaMessages.getString("PluginsManagementHandler.15"), e3);
                return;
            }
        } catch (Exception e4) {
            LOGGER.log(Level.WARNING, "Ocurrio un error al instalar el plugin", (Throwable) e4);
            showError(SimpleAfirmaMessages.getString("PluginsManagementHandler.2"), e4);
            return;
        }
        if (PermissionChecker.check(afirmaPluginInstallPlugin.getInfo(), Permission.RESET)) {
            resetApplication();
        }
        showPluginInfo(afirmaPluginInstallPlugin);
    }

    private boolean verifyJar(File file) {
        try {
            return JOptionPane.showConfirmDialog(this.view, new CertificateConfirmPanel((X509Certificate[][]) JarVerifier.verify(file).toArray(new X509Certificate[0][])), SimpleAfirmaMessages.getString("PluginsManagementHandler.25"), 0) == 0;
        } catch (JarNoSignedException e) {
            LOGGER.log(Level.WARNING, "Se han encontrado entradas del plugin sin firmar", (Throwable) e);
            return JOptionPane.showConfirmDialog(this.view, SimpleAfirmaMessages.getString("PluginsManagementHandler.20"), SimpleAfirmaMessages.getString("PluginsManagementHandler.21"), 0, 2) == 0;
        } catch (SecurityException e2) {
            LOGGER.log(Level.WARNING, "Se han encontrado problemas en la firma del plugin", (Throwable) e2);
            return JOptionPane.showConfirmDialog(this.view, SimpleAfirmaMessages.getString("PluginsManagementHandler.22"), SimpleAfirmaMessages.getString("PluginsManagementHandler.23"), 0, 0) == 0;
        } catch (Exception e3) {
            LOGGER.log(Level.WARNING, "Ocurrio un error durante la lectura del fichero de plugin", (Throwable) e3);
            showError(SimpleAfirmaMessages.getString("PluginsManagementHandler.24"), e3);
            return false;
        }
    }

    private File selectPluginFile() {
        return AOUIFactory.getLoadFiles(SimpleAfirmaMessages.getString("PluginsManagementHandler.3"), null, null, PLUGIN_EXTENSIONS, SimpleAfirmaMessages.getString("PluginsManagementHandler.4"), false, false, DesktopUtil.getDefaultDialogsIcon(), this.view)[0];
    }

    private void showPluginInfo(AfirmaPlugin afirmaPlugin) {
        List<AfirmaPlugin> pluginsLoadedList;
        JList<AfirmaPlugin> pluginsList = this.view.getPluginsList();
        DefaultListModel model = pluginsList.getModel();
        model.removeAllElements();
        try {
            pluginsLoadedList = this.pluginsManager.getPluginsLoadedList();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se pudo cargar la nueva lista de plugins cargados, se usara la anterior ", (Throwable) e);
            pluginsLoadedList = this.pluginsList;
        }
        Iterator<AfirmaPlugin> it = pluginsLoadedList.iterator();
        while (it.hasNext()) {
            model.addElement(it.next());
        }
        pluginsList.setSelectedValue(afirmaPlugin, true);
        showPluginDetails(afirmaPlugin.getInfo());
    }

    private void removeLoadedPlugin(PluginInfo pluginInfo) throws PluginException {
        AfirmaPlugin afirmaPlugin = null;
        ListModel model = this.view.getPluginsList().getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (((AfirmaPlugin) model.getElementAt(i)).getInfo().equals(pluginInfo)) {
                afirmaPlugin = (AfirmaPlugin) model.getElementAt(i);
                break;
            }
        }
        if (afirmaPlugin != null) {
            try {
                removePlugin(afirmaPlugin);
            } catch (Exception e) {
                throw new PluginException("Error al eliminar la version preexistente del plugin", e);
            }
        } else {
            try {
                this.pluginsManager.forceRemove(pluginInfo.getInternalName());
            } catch (IOException e2) {
                throw new PluginException("No se pudo eliminar el directorio residual del plugin anterior", e2);
            }
        }
    }

    void removePlugin() {
        AfirmaPlugin afirmaPlugin = (AfirmaPlugin) this.view.getPluginsList().getSelectedValue();
        if (afirmaPlugin == null || 0 != JOptionPane.showConfirmDialog(this.view.getParentWindow(), String.format(SimpleAfirmaMessages.getString("PluginsManagementHandler.5"), afirmaPlugin.getInfo().getName()))) {
            return;
        }
        try {
            removePlugin(afirmaPlugin);
            PluginsPreferences.getInstance(afirmaPlugin).removeConfig();
            if (PermissionChecker.check(afirmaPlugin.getInfo(), Permission.RESET)) {
                resetApplication();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Ocurrio un error al desinstalar el plugin", (Throwable) e);
            showError(SimpleAfirmaMessages.getString("PluginsManagementHandler.6"), e);
        }
    }

    private void removePlugin(AfirmaPlugin afirmaPlugin) throws IOException {
        this.pluginsManager.uninstallPlugin(afirmaPlugin);
        this.view.getPluginsList().getModel().removeElement(afirmaPlugin);
        showPluginDetails(null);
    }

    private static void resetApplication() {
        try {
            List<String> command = getCommand(new File(PluginsManagementHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
            if (command != null) {
                if (JOptionPane.showConfirmDialog((Component) null, SimpleAfirmaMessages.getString("PluginsManagementHandler.19"), SimpleAfirmaMessages.getString("PluginsManagementHandler.18"), 0, 2) == 0) {
                    try {
                        new ProcessBuilder(command).start();
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "No se ha podido arrancar la nueva instancia de la aplicacion", (Throwable) e);
                    }
                    System.exit(0);
                    return;
                }
                return;
            }
            JOptionPane.showMessageDialog((Component) null, SimpleAfirmaMessages.getString("PluginsManagementHandler.17"), SimpleAfirmaMessages.getString("PluginsManagementHandler.18"), 2);
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "No se ha podido identificar el fichero ejecutable", (Throwable) e2);
        }
    }

    private static List<String> getCommand(File file) {
        ArrayList arrayList;
        if (file.isDirectory()) {
            return null;
        }
        if (file.getName().toLowerCase().endsWith(".jar")) {
            File macApp = MacUtils.getMacApp(file);
            if (macApp != null && macApp.isFile()) {
                arrayList = new ArrayList();
                arrayList.add(macApp.getAbsolutePath());
            } else {
                String str = System.getProperty("java.home") + File.separator + CommandLineParameters.FORMAT_HASH_FILE_BIN + File.separator + "java";
                arrayList = new ArrayList();
                arrayList.add(str);
                arrayList.add("-jar");
                arrayList.add(file.getPath());
            }
        } else if (file.getName().toLowerCase().endsWith(".exe")) {
            arrayList = new ArrayList();
            arrayList.add(file.getPath());
        } else {
            arrayList = null;
        }
        return arrayList;
    }

    void configPlugin() {
        AfirmaPlugin afirmaPlugin = (AfirmaPlugin) this.view.getPluginsList().getSelectedValue();
        final PluginsPreferences pluginsPreferences = PluginsPreferences.getInstance(afirmaPlugin);
        try {
            final PluginConfigurationDialog pluginConfigurationDialog = new PluginConfigurationDialog(SwingUtilities.getWindowAncestor(this.view), afirmaPlugin);
            pluginConfigurationDialog.init(pluginsPreferences.recoverConfig());
            pluginConfigurationDialog.addWindowListener(new WindowAdapter() { // from class: es.gob.afirma.standalone.ui.plugins.PluginsManagementHandler.1
                public void windowClosed(WindowEvent windowEvent) {
                    if (pluginConfigurationDialog.isAccepted()) {
                        pluginsPreferences.saveConfig(pluginConfigurationDialog.recoverConfig());
                    }
                }
            });
            pluginConfigurationDialog.setVisible(true);
        } catch (PluginException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar el dialogo de configuracion del plugin " + afirmaPlugin.getInfo().getName(), (Throwable) e);
            showError(SimpleAfirmaMessages.getString("PluginsManagementHandler.13"), e);
        }
    }

    private static void showError(String str, Throwable th) {
        AOUIFactory.showErrorMessage(str, SimpleAfirmaMessages.getString("PluginsManagementHandler.7"), 0, th);
    }

    void showPluginDetails(PluginInfo pluginInfo) {
        StringBuilder sb = new StringBuilder();
        if (pluginInfo != null) {
            sb.append("<html>").append("<b>").append(SimpleAfirmaMessages.getString("PluginsManagementHandler.9")).append("</b><br>").append("<span>&nbsp;&nbsp;").append(pluginInfo.getVersion()).append("</span><br><br>");
            if (pluginInfo.getAuthors() != null && pluginInfo.getAuthors().length > 0) {
                sb.append("<b>").append(SimpleAfirmaMessages.getString("PluginsManagementHandler.10")).append("</b><br>");
                for (String str : pluginInfo.getAuthors()) {
                    sb.append("<span>&nbsp;&nbsp;- ").append(str).append("</span><br>");
                }
                sb.append("<br>");
            }
            if (pluginInfo.getContacts() != null && pluginInfo.getContacts().length > 0) {
                sb.append("<b>").append(SimpleAfirmaMessages.getString("PluginsManagementHandler.11")).append("</b><br>");
                for (String str2 : pluginInfo.getContacts()) {
                    sb.append("<span>&nbsp;&nbsp;").append(str2).append("</span><br>");
                }
                sb.append("<br>");
            }
            sb.append("<b>").append(SimpleAfirmaMessages.getString("PluginsManagementHandler.12")).append("</b><br>").append("<span>").append(pluginInfo.getDescription()).append("</span>").append("</html>");
        }
        this.view.getPluginInfoPane().setText(sb.toString());
        JButton configButton = this.view.getConfigButton();
        boolean z = (pluginInfo == null || pluginInfo.getConfigPanel() == null) ? false : true;
        configButton.setVisible(z);
    }

    void loadViewData() {
        try {
            this.pluginsList = this.pluginsManager.getPluginsLoadedList();
            JList<AfirmaPlugin> pluginsList = this.view.getPluginsList();
            DefaultListModel model = pluginsList.getModel();
            Iterator<AfirmaPlugin> it = this.pluginsList.iterator();
            while (it.hasNext()) {
                model.addElement(it.next());
            }
            if (model.size() > 0) {
                pluginsList.setSelectedIndex(0);
                showPluginDetails(this.pluginsList.get(0).getInfo());
            }
        } catch (PluginException e) {
            LOGGER.severe("No se ha podido cargar la lista de plugins");
            showError(SimpleAfirmaMessages.getString("PluginsManagementHandler.8"), e);
        }
    }

    public void keyPressed(KeyEvent keyEvent) {
    }

    public void keyTyped(KeyEvent keyEvent) {
    }

    public void keyReleased(KeyEvent keyEvent) {
        if (keyEvent != null && keyEvent.getKeyCode() == 27 && !Platform.OS.MACOSX.equals(Platform.getOS())) {
            this.view.getParentWindow().dispose();
        }
    }

    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        if (listSelectionEvent.getSource() instanceof JList) {
            Object selectedValue = ((JList) listSelectionEvent.getSource()).getSelectedValue();
            if (selectedValue instanceof AfirmaPlugin) {
                showPluginDetails(((AfirmaPlugin) selectedValue).getInfo());
            }
        }
    }
}
