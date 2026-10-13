package es.gob.afirma.signvalidation;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signvalidation/ValidationMessages.class */
public final class ValidationMessages {
    private static final String BUNDLE_NAME = "validationmessages";
    private static ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME);

    private ValidationMessages() {
    }

    public static String getString(String str) {
        try {
            return bundle.getString(str);
        } catch (MissingResourceException e) {
            return '!' + str + '!';
        }
    }

    public static String getString(String str, String... strArr) {
        try {
            String string = bundle.getString(str);
            if (strArr != null && strArr.length > 0) {
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
