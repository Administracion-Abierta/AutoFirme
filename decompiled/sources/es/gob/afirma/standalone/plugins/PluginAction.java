package es.gob.afirma.standalone.plugins;

import java.awt.Window;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/plugins/PluginAction.class */
public abstract class PluginAction {
    private AfirmaPlugin plugin = null;

    public final void setPlugin(AfirmaPlugin afirmaPlugin) {
        this.plugin = afirmaPlugin;
    }

    protected final Properties getConfig() {
        if (this.plugin != null) {
            return PluginPreferences.getInstance(this.plugin).recoverConfig();
        }
        return new Properties();
    }

    protected final void saveConfig(Properties properties) {
        if (this.plugin != null) {
            PluginPreferences.getInstance(this.plugin).saveConfig(properties);
        }
    }

    public void start(Window window) {
    }
}
