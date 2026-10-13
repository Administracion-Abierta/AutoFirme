package es.gob.jmulticard.jse.provider;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/JMultiCardProviderMessages.class */
public final class JMultiCardProviderMessages {
    private static final Logger LOGGER = Logger.getLogger(JMultiCardProviderMessages.class.getName());
    private static final String BUNDLE_NAME = "jmulticardprovidermessages";
    private static ResourceBundle resourceBundle;

    static {
        try {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "No se han podido cargar los textos de 'jmulticardprovidermessages'", (Throwable) e);
            resourceBundle = null;
        }
    }

    private JMultiCardProviderMessages() {
    }

    public static String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (ClassCastException | NullPointerException | MissingResourceException e) {
            LOGGER.severe("No se ha encontrado el texto para la clave " + key + " en " + BUNDLE_NAME + ": " + e);
            return '!' + key + '!';
        }
    }
}
