package es.gob.afirma.ui.core.jse;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/JSEUIMessages.class */
public final class JSEUIMessages {
    private static final String BUNDLE_NAME = "uimessages";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private JSEUIMessages() {
    }

    public static String getString(String str) {
        try {
            return RESOURCE_BUNDLE.getString(str);
        } catch (Exception e) {
            LOGGER.severe("No se ha encontrado el texto para la clave '" + str + "': " + e);
            return '!' + str + '!';
        }
    }

    static String getString(String str, String str2) {
        try {
            return RESOURCE_BUNDLE.getString(str).replace("%0", str2);
        } catch (Exception e) {
            LOGGER.severe("No se ha encontrado el texto para la clave '" + str + "': " + e);
            return '!' + str + '!';
        }
    }

    static String getString(String str, String[] strArr) {
        try {
            String string = RESOURCE_BUNDLE.getString(str);
            if (strArr != null) {
                for (int i = 0; i < strArr.length; i++) {
                    string = string.replace("%" + i, strArr[i]);
                }
            }
            return string;
        } catch (Exception e) {
            LOGGER.severe("No se ha encontrado el texto para la clave '" + str + "': " + e);
            return '!' + str + '!';
        }
    }
}
