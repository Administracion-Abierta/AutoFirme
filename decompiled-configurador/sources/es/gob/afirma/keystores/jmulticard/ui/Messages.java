package es.gob.afirma.keystores.jmulticard.ui;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/jmulticard/ui/Messages.class */
public final class Messages {
    private static ResourceBundle bundle = ResourceBundle.getBundle("properties/jmulticarduimessages", Locale.getDefault());

    private Messages() {
    }

    public static String getString(String str) {
        try {
            return bundle.getString(str);
        } catch (MissingResourceException e) {
            Logger.getLogger("es.gob.afirma").severe("No se ha encontrado el recurso textual con clave '" + str + "': " + e);
            return "##ERROR## Cadena no disponible: " + str;
        }
    }
}
