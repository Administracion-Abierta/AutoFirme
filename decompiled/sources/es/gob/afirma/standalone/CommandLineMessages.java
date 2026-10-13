package es.gob.afirma.standalone;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/CommandLineMessages.class */
final class CommandLineMessages {
    private static final String BUNDLE_NAME = "properties/commandlinemessages";
    private static ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME);

    private CommandLineMessages() {
    }

    static String getString(String str) {
        try {
            return bundle.getString(str);
        } catch (MissingResourceException e) {
            return '!' + str + '!';
        }
    }

    static String getString(String str, String... strArr) {
        try {
            String string = bundle.getString(str);
            if (strArr != null) {
                for (int i = 0; i < strArr.length; i++) {
                    if (strArr[i] != null) {
                        string = string.replace("%" + i, strArr[i]);
                    }
                }
            }
            return string;
        } catch (Exception e) {
            return '!' + str + '!';
        }
    }

    static void changeLocale() {
        bundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
    }
}
