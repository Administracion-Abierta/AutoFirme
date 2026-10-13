package es.gob.afirma.signers.xml.style;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/xml/style/XmlStyleMessages.class */
final class XmlStyleMessages {
    private static final String BUNDLE_NAME = "xmlmessages";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());

    private XmlStyleMessages() {
    }

    static String getString(String str) {
        try {
            return RESOURCE_BUNDLE.getString(str);
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").severe("No se ha encontrado el texto con la clave '" + str + "': " + e);
            return '!' + str + '!';
        }
    }

    static String getString(String str, String str2) {
        try {
            return RESOURCE_BUNDLE.getString(str).replace("%0", str2);
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").severe("No se ha encontrado el texto con la clave '" + str + "': " + e);
            return '!' + str + '!';
        }
    }
}
