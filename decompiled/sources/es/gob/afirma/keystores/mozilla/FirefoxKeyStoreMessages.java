package es.gob.afirma.keystores.mozilla;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/mozilla/FirefoxKeyStoreMessages.class */
final class FirefoxKeyStoreMessages {
    private static final String BUNDLE_NAME = "firefoxmessages";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());

    private FirefoxKeyStoreMessages() {
    }

    static String getString(String str) {
        try {
            return RESOURCE_BUNDLE.getString(str);
        } catch (MissingResourceException e) {
            Logger.getLogger("es.gob.afirma").severe("No se ha encontrado el recurso textual con clave '" + str + "': " + e);
            return '!' + str + '!';
        }
    }
}
