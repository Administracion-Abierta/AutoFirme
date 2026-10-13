package es.gob.afirma.core.ui;

import java.util.ResourceBundle;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/ui/CoreMessages.class */
public final class CoreMessages {
    private static final String BUNDLE_NAME = "coremessages";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private CoreMessages() {
    }

    public static String getString(String str) {
        try {
            return RESOURCE_BUNDLE.getString(str);
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").severe("No se ha encontrado el recurso textual con clave '" + str + "': " + e);
            return '!' + str + '!';
        }
    }

    public static String getString(String str, String str2) {
        try {
            return RESOURCE_BUNDLE.getString(str).replace("%0", str2);
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").severe("No se ha encontrado el texto para la clave '" + str + "': " + e);
            return '!' + str + '!';
        }
    }

    public static String getString(String str, String[] strArr) {
        try {
            String string = RESOURCE_BUNDLE.getString(str);
            if (strArr != null) {
                for (int i = 0; i < strArr.length; i++) {
                    string = string.replace("%" + i, strArr[i]);
                }
            }
            return string;
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").severe("No se ha encontrado el texto para la clave '" + str + "': " + e);
            return '!' + str + '!';
        }
    }
}
