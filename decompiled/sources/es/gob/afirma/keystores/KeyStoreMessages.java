package es.gob.afirma.keystores;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/KeyStoreMessages.class */
final class KeyStoreMessages {
    private static final String BUNDLE_NAME = "keystoremessages";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());

    private KeyStoreMessages() {
    }

    static String getString(String str) {
        try {
            return RESOURCE_BUNDLE.getString(str);
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").severe("No se ha encontrado el recurso textual '" + str + "': " + e);
            return '!' + str + '!';
        }
    }

    static String getString(String str, String str2) {
        try {
            return RESOURCE_BUNDLE.getString(str).replace("%0", str2);
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").severe("No se ha encontrado el recurso textual '" + str + "': " + e);
            return '!' + str + '!';
        }
    }
}
