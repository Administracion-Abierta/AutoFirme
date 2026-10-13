package es.gob.afirma.standalone.plugins;

import java.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/plugins/PluginCommandAction.class */
public abstract class PluginCommandAction {
    private final String command;
    private AfirmaPlugin plugin;

    public abstract String getHelpText();

    protected abstract String process(String[] strArr) throws PluginControlledException, IllegalArgumentException;

    public PluginCommandAction(String str) {
        this.command = str;
    }

    public String getCommand() {
        return this.command;
    }

    public final void setPlugin(AfirmaPlugin afirmaPlugin) {
        this.plugin = afirmaPlugin;
    }

    public final AfirmaPlugin getPlugin() {
        return this.plugin;
    }

    public final String start(String[] strArr) throws PluginControlledException, IllegalArgumentException {
        String[] strArr2;
        if (strArr == null) {
            strArr2 = null;
        } else if (strArr.length > 0 && this.command.equalsIgnoreCase(strArr[0])) {
            strArr2 = (String[]) Arrays.copyOfRange(strArr, 1, strArr.length);
        } else {
            strArr2 = (String[]) Arrays.copyOf(strArr, strArr.length);
        }
        return process(strArr2);
    }
}
