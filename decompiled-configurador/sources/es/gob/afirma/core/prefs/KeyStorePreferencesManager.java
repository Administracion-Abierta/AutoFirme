package es.gob.afirma.core.prefs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/prefs/KeyStorePreferencesManager.class */
public final class KeyStorePreferencesManager {
    private static final Preferences USER_PREFERENCES;
    private static Preferences SYSTEM_PREFERENCES;
    private static final String KEYSTORES_NODE = "/es/gob/afirma/core/keystores";
    private static final String SYSTEM_UPDATED_KEYSTORES_NODE = "/es/gob/afirma/core/systemkeystores";
    private static final String DUMMY = "dummy";
    private static final String INTERNAL_SYSTEM_PREFERENCE_NODE = "/es/gob/afirma/standalone/ui/internalpreferences";
    private static final String SYSTEM_PREFERENCE_ALLOW_UPDATE_CONFIG = "allowUpdateConfig";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    public static String lastSelectedKeystore = null;
    public static String lastSelectedKeystoreLib = null;

    static {
        Preferences preferencesNode;
        Preferences preferencesUserRoot = Preferences.userRoot();
        USER_PREFERENCES = preferencesUserRoot.node(KEYSTORES_NODE);
        try {
            if (preferencesUserRoot.nodeExists(SYSTEM_UPDATED_KEYSTORES_NODE) && isAutommaticUpdateConfigAllowed()) {
                preferencesNode = preferencesUserRoot.node(SYSTEM_UPDATED_KEYSTORES_NODE);
            } else if (Preferences.systemRoot().nodeExists(KEYSTORES_NODE)) {
                preferencesNode = Preferences.systemRoot().node(KEYSTORES_NODE);
            } else {
                preferencesNode = null;
            }
        } catch (Exception e) {
            LOGGER.warning("No se pueden cargar las preferencias establecidas a nivel de sistema: " + e);
            preferencesNode = null;
        }
        SYSTEM_PREFERENCES = preferencesNode;
    }

    private KeyStorePreferencesManager() {
    }

    public static Map<String, String> getSystemSmartCardsRegistered() {
        HashMap map = new HashMap();
        if (SYSTEM_PREFERENCES != null) {
            try {
                String[] strArrChildrenNames = SYSTEM_PREFERENCES.childrenNames();
                if (strArrChildrenNames != null && strArrChildrenNames.length > 0) {
                    for (int i = 0; i < strArrChildrenNames.length; i++) {
                        String str = SYSTEM_PREFERENCES.node(strArrChildrenNames[i]).keys()[0];
                        map.put(str, SYSTEM_PREFERENCES.node(strArrChildrenNames[i]).get(str, null));
                    }
                }
            } catch (BackingStoreException e) {
                LOGGER.log(Level.SEVERE, "No se han podido obtener los registros sobre tarjetas inteligentes del sistema", (Throwable) e);
            }
        }
        return map;
    }

    public static Map<String, String> getUserSmartCardsRegistered() {
        HashMap map = new HashMap();
        if (USER_PREFERENCES != null) {
            try {
                String[] strArrChildrenNames = USER_PREFERENCES.childrenNames();
                if (strArrChildrenNames != null && strArrChildrenNames.length > 0) {
                    for (int i = 0; i < strArrChildrenNames.length; i++) {
                        String str = USER_PREFERENCES.node(strArrChildrenNames[i]).keys()[0];
                        map.put(str, USER_PREFERENCES.node(strArrChildrenNames[i]).get(str, null));
                    }
                }
            } catch (BackingStoreException e) {
                LOGGER.severe("No se han podido obtener los registros sobre tarjetas inteligentes del usuario: " + e);
            }
        }
        return map;
    }

    public static Map<String, String> getAllSmartCardsMap() {
        HashMap map = new HashMap();
        Map<String, String> userSmartCardsRegistered = getUserSmartCardsRegistered();
        Map<String, String> systemSmartCardsRegistered = getSystemSmartCardsRegistered();
        map.putAll(userSmartCardsRegistered);
        map.putAll(systemSmartCardsRegistered);
        return map;
    }

    public static void putUserSmartCardsMap(Map<String, Object> map) {
        Map<String, String> allSmartCardsMap = getAllSmartCardsMap();
        for (String str : map.keySet()) {
            if (!checkExistsController(allSmartCardsMap, (String) map.get(str))) {
                String strCheckCorrectName = checkCorrectName(allSmartCardsMap, str);
                addSmartCardToUserRec(strCheckCorrectName, (String) map.get(str));
                allSmartCardsMap.put(strCheckCorrectName, (String) map.get(str));
            }
        }
    }

    public static void putSystemSmartCardsMap(Map<String, Object> map) {
        if (SYSTEM_PREFERENCES == null) {
            createSystemPrefs();
        }
        setSystemPermissions();
        try {
            for (String str : SYSTEM_PREFERENCES.childrenNames()) {
                SYSTEM_PREFERENCES.node(str).removeNode();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se pudo borrar una de las tarjetas del sistema anteriormente registrada", (Throwable) e);
        }
        Map<String, String> allSmartCardsMap = getAllSmartCardsMap();
        for (String str2 : map.keySet()) {
            String strCheckCorrectName = checkCorrectName(allSmartCardsMap, str2);
            addSmartCardToSystemRec(strCheckCorrectName, (String) map.get(str2));
            allSmartCardsMap.put(strCheckCorrectName, (String) map.get(str2));
        }
    }

    private static void createSystemPrefs() {
        try {
            SYSTEM_PREFERENCES = Preferences.systemNodeForPackage(KeyStorePreferencesManager.class);
        } catch (Exception e) {
            unlockSystemPreferences();
        }
        setSystemPermissions();
    }

    private static void setSystemPermissions() {
        try {
            SYSTEM_PREFERENCES.putBoolean(DUMMY, true);
            SYSTEM_PREFERENCES.remove(DUMMY);
        } catch (Exception e) {
            if (!SYSTEM_PREFERENCES.isUserNode()) {
                unlockSystemPreferences();
            }
        }
    }

    private static boolean checkExistsController(Map<String, String> map, String str) {
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            if (map.get(it.next()).equals(str)) {
                return true;
            }
        }
        return false;
    }

    private static String checkCorrectName(Map<String, String> map, String str) {
        int i = 1;
        String str2 = str;
        while (true) {
            String str3 = str2;
            if (map.containsKey(str3)) {
                int i2 = i;
                i++;
                str2 = str + "-" + i2;
            } else {
                return str3;
            }
        }
    }

    public static boolean addSmartCardToUserRec(String str, String str2) {
        boolean z = false;
        boolean z2 = true;
        int i = 1;
        while (z2) {
            try {
                if (!USER_PREFERENCES.nodeExists(Integer.toString(i))) {
                    USER_PREFERENCES.node(Integer.toString(i)).put(str, str2);
                    z = true;
                    z2 = false;
                }
            } catch (BackingStoreException e) {
                LOGGER.severe("No se ha podido agregar la siguiente tarjeta inteligente al registro:" + str + " " + e);
                z2 = false;
            }
            i++;
        }
        return z;
    }

    private static boolean addSmartCardToSystemRec(String str, String str2) {
        boolean z = false;
        boolean z2 = true;
        int i = 1;
        while (z2) {
            try {
                if (!SYSTEM_PREFERENCES.nodeExists(Integer.toString(i))) {
                    SYSTEM_PREFERENCES.node(Integer.toString(i)).put(str, str2);
                    z = true;
                    z2 = false;
                }
            } catch (Exception e) {
                if (!SYSTEM_PREFERENCES.isUserNode()) {
                    unlockSystemPreferences();
                    return addSmartCardToSystemRec(str, str2);
                }
                LOGGER.severe("No se ha podido agregar la tarjeta inteligente " + str + " al registro del sistema: " + e);
                z2 = false;
            }
            i++;
        }
        try {
            SYSTEM_PREFERENCES.flush();
            return z;
        } catch (Exception e2) {
            LOGGER.severe("No se han podido guardar las tarjetas inteligentes en el registro del sistema: " + e2);
            return false;
        }
    }

    private static void unlockSystemPreferences() {
        SYSTEM_PREFERENCES = Preferences.userRoot().node(SYSTEM_UPDATED_KEYSTORES_NODE);
    }

    public static boolean deleteSmartCardRec(String str) {
        boolean z = false;
        try {
            String[] strArrChildrenNames = USER_PREFERENCES.childrenNames();
            if (strArrChildrenNames != null && strArrChildrenNames.length > 0) {
                for (int i = 0; i < strArrChildrenNames.length; i++) {
                    if (str.equals(USER_PREFERENCES.node(strArrChildrenNames[i]).keys()[0])) {
                        USER_PREFERENCES.node(strArrChildrenNames[i]).removeNode();
                        z = true;
                        break;
                    }
                }
            }
        } catch (BackingStoreException e) {
            LOGGER.severe("No se ha podido eliminar la tarjeta inteligente: " + str + " " + e);
        }
        return z;
    }

    public static void setLastSelectedKeystore(String str) {
        lastSelectedKeystore = str;
    }

    public static String getLastSelectedKeystore() {
        return lastSelectedKeystore;
    }

    public static void setLastSelectedKeystoreLib(String str) {
        lastSelectedKeystoreLib = str;
    }

    public static String getLastSelectedKeystoreLib() {
        return lastSelectedKeystoreLib;
    }

    public static void clearKeyStorePrefs() throws BackingStoreException {
        for (String str : USER_PREFERENCES.keys()) {
            USER_PREFERENCES.remove(str);
        }
        USER_PREFERENCES.flush();
    }

    public static void removeSystemPrefs() throws BackingStoreException {
        if (Preferences.systemRoot().nodeExists(KEYSTORES_NODE)) {
            Preferences preferencesNode = Preferences.systemRoot().node(KEYSTORES_NODE);
            Preferences preferencesParent = preferencesNode.parent();
            preferencesNode.removeNode();
            removeEmptyTree(preferencesParent);
        }
    }

    private static void removeEmptyTree(Preferences preferences) throws BackingStoreException {
        Preferences preferences2 = preferences;
        while (true) {
            Preferences preferences3 = preferences2;
            if (!preferences3.name().isEmpty() && preferences3.childrenNames().length == 0 && preferences3.keys().length == 0) {
                Preferences preferencesParent = preferences3.parent();
                preferences3.removeNode();
                preferences2 = preferencesParent;
            } else {
                return;
            }
        }
    }

    private static boolean isAutommaticUpdateConfigAllowed() {
        try {
            Preferences preferencesSystemRoot = Preferences.systemRoot();
            return preferencesSystemRoot.nodeExists(INTERNAL_SYSTEM_PREFERENCE_NODE) && preferencesSystemRoot.node(INTERNAL_SYSTEM_PREFERENCE_NODE).getBoolean(SYSTEM_PREFERENCE_ALLOW_UPDATE_CONFIG, false);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se ha podido comprobar si esta activa la actualizacion automatica de la configuracion", (Throwable) e);
            return false;
        }
    }
}
