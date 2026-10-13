package es.gob.afirma.standalone.plugins;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/plugins/PluginButton.class */
public class PluginButton {
    private String title = null;
    private String icon = null;
    private String toolTip = null;
    private String accesibleDescription = null;
    private String afirmaWindow = null;
    private String actionClassName = null;
    private PluginAction action = null;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String str) {
        this.icon = str;
    }

    public String getToolTip() {
        return this.toolTip;
    }

    public void setToolTip(String str) {
        this.toolTip = str;
    }

    public String getAccesibleDescription() {
        return this.accesibleDescription;
    }

    public void setAccesibleDescription(String str) {
        this.accesibleDescription = str;
    }

    public String getWindow() {
        return this.afirmaWindow;
    }

    public void setAfirmaWindow(String str) {
        this.afirmaWindow = str;
    }

    public String getActionClassName() {
        return this.actionClassName;
    }

    public void setActionClassName(String str) {
        this.actionClassName = str;
    }

    public PluginAction getAction() {
        return this.action;
    }

    public void setAction(PluginAction pluginAction) {
        this.action = pluginAction;
    }
}
