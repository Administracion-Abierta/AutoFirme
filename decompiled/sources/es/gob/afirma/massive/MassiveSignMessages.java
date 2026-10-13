package es.gob.afirma.massive;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/massive/MassiveSignMessages.class */
final class MassiveSignMessages {
    private static final String BUNDLE_NAME = "massivesignmessages";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());

    private MassiveSignMessages() {
    }

    static String getString(String str) {
        try {
            return RESOURCE_BUNDLE.getString(str);
        } catch (MissingResourceException e) {
            return '!' + str + '!';
        }
    }
}
