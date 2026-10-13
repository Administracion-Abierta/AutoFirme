package es.gob.afirma.standalone.plugins;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/plugins/GenericMenuOption.class */
public class GenericMenuOption {
    private final String title;
    private String actionClassName;
    private PluginAction action;
    private String dialogClass;
    private List<GenericMenuOption> menus;

    public GenericMenuOption(String str) {
        this(str, null);
    }

    public GenericMenuOption(String str, String str2) {
        this.dialogClass = null;
        this.menus = null;
        this.title = str;
        this.actionClassName = str2;
    }

    public String getTitle() {
        return this.title;
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

    public void addSubMenu(GenericMenuOption genericMenuOption) {
        if (this.menus == null) {
            this.menus = new ArrayList();
        }
        this.menus.add(genericMenuOption);
    }

    public List<GenericMenuOption> getMenus() {
        return this.menus;
    }

    public String getDialogClass() {
        return this.dialogClass;
    }

    public void setDialogClass(String str) {
        this.dialogClass = str;
    }
}
