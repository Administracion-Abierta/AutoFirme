package es.gob.afirma.core.misc.protocol;

import java.util.ResourceBundle;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/misc/protocol/ProtocoloMessages.class */
final class ProtocoloMessages {
    private static final String BUNDLE_NAME = "protocolomessages";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private ProtocoloMessages() {
    }

    static String getString(String str) {
        try {
            return RESOURCE_BUNDLE.getString(str);
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").severe("No se ha encontrado el recurso textual con clave '" + str + "': " + e);
            return '!' + str + '!';
        }
    }
}
