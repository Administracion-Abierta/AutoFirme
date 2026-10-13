package es.gob.afirma.standalone.configurator.common;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/common/ConfiguratorCommonMessages.class */
public final class ConfiguratorCommonMessages {
    private static final String BUNDLE_NAME = ConfiguratorCommonMessages.class.getName();
    private static ResourceBundle bundle;

    static {
        try {
            bundle = ResourceBundle.getBundle(BUNDLE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ConfiguratorCommonMessages() {
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
        bundle = ResourceBundle.getBundle(ConfiguratorCommonMessages.class.getName(), Locale.getDefault());
    }
}
