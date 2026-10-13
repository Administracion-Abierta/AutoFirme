package es.gob.afirma.standalone.plugins;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/plugins/PluginInfo.class */
public class PluginInfo extends MinimalPluginInfo {
    private static final int DEFAULT_VERSION_CODE = 1;
    private static final String DEFAULT_VERSION = "1.0";
    private final String name;
    private String description;
    private String version;
    private String[] authors;
    private String[] contacts;
    private String configPanel;
    private String inlineProcessorClassname;
    private Permission[] permissions;
    private GenericMenuOption menu;
    private PluginButton[] buttons;
    private PluginCommand[] commands;

    public PluginInfo(String str, String str2) {
        super(str, 1);
        if (str2 == null) {
            throw new NullPointerException("El nombre del plugin no puede ser nulo");
        }
        this.name = str2;
        this.description = "";
        this.version = "1.0";
        this.authors = null;
        this.contacts = null;
        this.configPanel = null;
        this.inlineProcessorClassname = null;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String str) {
        this.description = str;
    }

    public void setVersionCode(int i) {
        this.versionCode = i;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String str) {
        if (str != null) {
            this.version = str;
        }
    }

    public String[] getAuthors() {
        if (this.authors != null) {
            return (String[]) this.authors.clone();
        }
        return null;
    }

    public void setAuthors(String[] strArr) {
        this.authors = strArr != null ? (String[]) strArr.clone() : null;
    }

    public String[] getContacts() {
        if (this.contacts != null) {
            return (String[]) this.contacts.clone();
        }
        return null;
    }

    public void setContacts(String[] strArr) {
        this.contacts = strArr != null ? (String[]) strArr.clone() : null;
    }

    public String getConfigPanel() {
        return this.configPanel;
    }

    public void setConfigPanel(String str) {
        this.configPanel = str;
    }

    public Permission[] getPermissions() {
        if (this.permissions != null) {
            return (Permission[]) this.permissions.clone();
        }
        return null;
    }

    public void setPermissions(Permission[] permissionArr) {
        this.permissions = permissionArr != null ? (Permission[]) permissionArr.clone() : null;
    }

    public String getInlineProcessorClassname() {
        return this.inlineProcessorClassname;
    }

    public void setInlineProcessorClassname(String str) {
        this.inlineProcessorClassname = str;
    }

    public GenericMenuOption getMenu() {
        return this.menu;
    }

    public void setMenu(GenericMenuOption genericMenuOption) {
        this.menu = genericMenuOption;
    }

    public PluginButton[] getButtons() {
        if (this.buttons != null) {
            return (PluginButton[]) this.buttons.clone();
        }
        return null;
    }

    public void setButtons(PluginButton[] pluginButtonArr) {
        this.buttons = pluginButtonArr != null ? (PluginButton[]) pluginButtonArr.clone() : null;
    }

    public PluginCommand[] getCommands() {
        if (this.commands != null) {
            return (PluginCommand[]) this.commands.clone();
        }
        return null;
    }

    public void setCommands(PluginCommand[] pluginCommandArr) {
        this.commands = pluginCommandArr != null ? (PluginCommand[]) pluginCommandArr.clone() : null;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (obj instanceof PluginInfo) {
            return this.internalName.equals(((PluginInfo) obj).getInternalName());
        }
        return super.equals(obj);
    }

    public int hashCode() {
        return super.hashCode();
    }
}
