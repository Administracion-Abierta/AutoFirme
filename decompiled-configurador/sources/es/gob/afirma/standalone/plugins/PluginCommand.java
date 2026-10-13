package es.gob.afirma.standalone.plugins;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/plugins/PluginCommand.class */
public class PluginCommand {
    private final String command;
    private String commandActionClass;
    private String description;

    public PluginCommand(String str) {
        this.command = str;
    }

    public String getCommand() {
        return this.command;
    }

    public void setCommandActionClass(String str) {
        this.commandActionClass = str;
    }

    public String getCommandActionClass() {
        return this.commandActionClass;
    }

    public void setDescription(String str) {
        this.description = str;
    }

    public String getDescription() {
        return this.description;
    }
}
