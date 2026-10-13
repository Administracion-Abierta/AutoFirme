package es.gob.afirma.standalone.configurator;

import java.util.ResourceBundle;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/Messages.class */
final class Messages {
    private static final String BUNDLE_NAME = "es.gob.afirma.standalone.configurator.messages";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages() {
    }

    public static String getString(String str) {
        try {
            return RESOURCE_BUNDLE.getString(str);
        } catch (Exception e) {
            return '!' + str + '!';
        }
    }

    public static String getString(String str, String... strArr) {
        try {
            String string = RESOURCE_BUNDLE.getString(str);
            if (strArr != null && strArr.length > 0) {
                for (int i = 0; i < strArr.length; i++) {
                    if (strArr[i] != null) {
                        string = string.replace("%" + (i + 1), strArr[i]);
                    }
                }
            }
            return string;
        } catch (Exception e) {
            return '!' + str + '!';
        }
    }
}
