package es.gob.afirma.standalone.plugins.manager;

import es.gob.afirma.standalone.plugins.AfirmaPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/plugins/manager/PluginsPreferences.class */
public class PluginsPreferences {
    private final Preferences preferences;
    private final String name;
    private static Map<String, PluginsPreferences> instances = null;

    public static PluginsPreferences getInstance(AfirmaPlugin afirmaPlugin) {
        String internalName = afirmaPlugin.getInfo().getInternalName();
        if (instances == null) {
            instances = new HashMap();
        }
        if (!instances.containsKey(internalName)) {
            instances.put(internalName, new PluginsPreferences(afirmaPlugin));
        }
        return instances.get(internalName);
    }

    private PluginsPreferences(AfirmaPlugin afirmaPlugin) {
        this.name = afirmaPlugin.getInfo().getInternalName();
        this.preferences = Preferences.userNodeForPackage(afirmaPlugin.getClass());
    }

    public void saveConfig(Properties properties) {
        try {
            for (String str : this.preferences.childrenNames()) {
                this.preferences.remove(str);
            }
        } catch (Exception e) {
            Logger.getLogger(PluginsPreferences.class.getName()).warning("No se pudo eliminar la configuracion anterior del plugin: " + e);
        }
        for (String str2 : (String[]) properties.keySet().toArray(new String[properties.size()])) {
            this.preferences.put(str2, properties.getProperty(str2));
        }
    }

    public Properties recoverConfig() {
        Properties properties = new Properties();
        try {
            for (String str : this.preferences.keys()) {
                properties.setProperty(str, this.preferences.get(str, ""));
            }
        } catch (Exception e) {
            Logger.getLogger(PluginsPreferences.class.getName()).warning("No se pudo recuperar toda la configuracion del plugin: " + e);
        }
        return properties;
    }

    public void removeConfig() {
        try {
            this.preferences.removeNode();
        } catch (Exception e) {
            Logger.getLogger(PluginsPreferences.class.getName()).warning("No se pudo eliminar la configuracion del plugin: " + e);
        }
        instances.remove(this.name);
    }
}
