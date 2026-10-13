package es.gob.afirma.standalone.plugins;

import java.util.Properties;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/plugins/PluginPreferences.class */
public class PluginPreferences {
    private final Preferences preferences;
    private static PluginPreferences instance = null;

    public static PluginPreferences getInstance(AfirmaPlugin afirmaPlugin) {
        if (instance == null) {
            instance = new PluginPreferences(afirmaPlugin);
        }
        return instance;
    }

    private PluginPreferences(AfirmaPlugin afirmaPlugin) {
        this.preferences = Preferences.userNodeForPackage(afirmaPlugin.getClass());
    }

    public void saveConfig(Properties properties) {
        try {
            for (String str : this.preferences.childrenNames()) {
                this.preferences.remove(str);
            }
        } catch (BackingStoreException e) {
            Logger.getLogger(PluginPreferences.class.getName()).warning("No se pudo eliminar la configuracion anterior del plugin: " + e);
        }
        for (String str2 : (String[]) properties.keySet().toArray(new String[properties.size()])) {
            this.preferences.put(str2, properties.getProperty(str2));
        }
        try {
            this.preferences.flush();
        } catch (BackingStoreException e2) {
            Logger.getLogger(PluginPreferences.class.getName()).warning("No se pudo guardar la nueva configuracion del plugin: " + e2);
        }
    }

    public Properties recoverConfig() {
        Properties properties = new Properties();
        try {
            for (String str : this.preferences.keys()) {
                properties.setProperty(str, this.preferences.get(str, ""));
            }
        } catch (BackingStoreException e) {
            Logger.getLogger(PluginPreferences.class.getName()).warning("No se pudo recuperar toda la configuracion del plugin: " + e);
        }
        return properties;
    }
}
