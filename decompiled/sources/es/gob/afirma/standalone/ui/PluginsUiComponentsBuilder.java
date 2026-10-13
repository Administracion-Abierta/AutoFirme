package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.standalone.SimpleAfirma;
import es.gob.afirma.standalone.plugins.AfirmaPlugin;
import es.gob.afirma.standalone.plugins.GenericMenuOption;
import es.gob.afirma.standalone.plugins.Permission;
import es.gob.afirma.standalone.plugins.PluginButton;
import es.gob.afirma.standalone.plugins.PluginInfo;
import es.gob.afirma.standalone.plugins.manager.PermissionChecker;
import es.gob.afirma.standalone.plugins.manager.PluginException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/PluginsUiComponentsBuilder.class */
public class PluginsUiComponentsBuilder {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    public static List<PluginGraphicButton> getPluginsButtons(PluginIntegrationWindow pluginIntegrationWindow) {
        PluginButton[] buttons;
        ArrayList arrayList = new ArrayList();
        List<AfirmaPlugin> pluginsLoadedList = null;
        try {
            pluginsLoadedList = SimpleAfirma.getPluginsManager().getPluginsLoadedList();
        } catch (PluginException e) {
            LOGGER.log(Level.SEVERE, "No se han podido cargar los plugins en la aplicacion", (Throwable) e);
        }
        if (pluginsLoadedList != null) {
            for (AfirmaPlugin afirmaPlugin : pluginsLoadedList) {
                PluginInfo info = afirmaPlugin.getInfo();
                if (PermissionChecker.check(info, Permission.BUTTONS) && (buttons = info.getButtons()) != null) {
                    for (PluginButton pluginButton : buttons) {
                        try {
                            if (PluginIntegrationWindow.getWindow(pluginButton.getWindow()) == pluginIntegrationWindow) {
                                arrayList.add(new PluginGraphicButton(pluginButton, buildButton(pluginButton, afirmaPlugin.getClass().getClassLoader())));
                            }
                        } catch (Exception e2) {
                            LOGGER.log(Level.WARNING, String.format("El plugin %1s ha declarado botones invalidos", afirmaPlugin.getInfo().getName()), (Throwable) e2);
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    public static List<GenericMenuOption> getPluginsMenus() {
        GenericMenuOption menu;
        ArrayList arrayList = new ArrayList();
        List<AfirmaPlugin> pluginsLoadedList = null;
        try {
            pluginsLoadedList = SimpleAfirma.getPluginsManager().getPluginsLoadedList();
        } catch (PluginException e) {
            LOGGER.log(Level.SEVERE, "No se han podido cargar los plugins en la aplicacion", (Throwable) e);
        }
        if (pluginsLoadedList != null) {
            Iterator<AfirmaPlugin> it = pluginsLoadedList.iterator();
            while (it.hasNext()) {
                PluginInfo info = it.next().getInfo();
                if (PermissionChecker.check(info, Permission.MENU) && (menu = info.getMenu()) != null) {
                    arrayList.add(menu);
                }
            }
        }
        return arrayList;
    }

    public static JButton buildButton(PluginButton pluginButton, ClassLoader classLoader) {
        JButton jButton = new JButton();
        jButton.setText(pluginButton.getTitle());
        if (pluginButton.getIcon() != null) {
            try {
                InputStream resourceAsStream = (classLoader != null ? classLoader : PluginsUiComponentsBuilder.class.getClassLoader()).getResourceAsStream(pluginButton.getIcon());
                Throwable th = null;
                try {
                    try {
                        jButton.setIcon(new ImageIcon(AOUtil.getDataFromInputStream(resourceAsStream)));
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
                    } catch (Throwable th3) {
                        th = th3;
                        throw th3;
                    }
                } catch (Throwable th4) {
                    if (resourceAsStream != null) {
                        if (th != null) {
                            try {
                                resourceAsStream.close();
                            } catch (Throwable th5) {
                                th.addSuppressed(th5);
                            }
                        } else {
                            resourceAsStream.close();
                        }
                    }
                    throw th4;
                }
            } catch (IOException e) {
                LOGGER.warning("No se pudo cargar el icono de un boton de plugin: " + e);
            }
        }
        jButton.setToolTipText(pluginButton.getToolTip());
        jButton.getAccessibleContext().setAccessibleDescription(pluginButton.getAccesibleDescription());
        return jButton;
    }
}
