package es.gob.jmulticard.card;

import es.gob.jmulticard.JmcLogger;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/CardMessages.class */
public final class CardMessages {
    private static final String BUNDLE_NAME = "cardmessages";
    private static ResourceBundle resourceBundle;

    static {
        try {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
        } catch (Exception e) {
            JmcLogger.log(CardMessages.class.getName(), "static", Level.SEVERE, "No se han podido cargar los textos de 'cardmessages'", e);
            resourceBundle = null;
        }
    }

    private CardMessages() {
    }

    public static String getString(String key, String text) {
        try {
            return resourceBundle.getString(key).replace("%0", text);
        } catch (ClassCastException | NullPointerException | MissingResourceException e) {
            JmcLogger.severe("No se ha encontrado el recurso de texto con clave '" + key + "': " + e);
            return '!' + key + '!';
        }
    }

    public static String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (ClassCastException | NullPointerException | MissingResourceException e) {
            JmcLogger.severe("No se ha encontrado el recurso de texto con clave '" + key + "': " + e);
            return '!' + key + '!';
        }
    }
}
