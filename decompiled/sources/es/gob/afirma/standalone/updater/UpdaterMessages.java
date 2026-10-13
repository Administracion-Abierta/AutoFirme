package es.gob.afirma.standalone.updater;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/updater/UpdaterMessages.class */
final class UpdaterMessages {
    private static final String BUNDLE_NAME = "properties/updatermessages";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private UpdaterMessages() {
    }

    static String getString(String str) {
        try {
            return RESOURCE_BUNDLE.getString(str);
        } catch (MissingResourceException e) {
            return '!' + str + '!';
        }
    }
}
