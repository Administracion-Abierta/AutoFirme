package es.gob.afirma.standalone.plugins.manager;

import es.gob.afirma.standalone.plugins.Permission;
import es.gob.afirma.standalone.plugins.PluginInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/plugins/manager/PermissionChecker.class */
public class PermissionChecker {
    public static boolean check(PluginInfo pluginInfo, Permission permission) {
        return check(pluginInfo.getPermissions(), permission);
    }

    public static boolean check(Permission[] permissionArr, Permission permission) {
        for (Permission permission2 : permissionArr) {
            if (permission2 == permission) {
                return true;
            }
        }
        return false;
    }
}
