package es.gob.afirma.standalone.protocol;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/ProtocolMessages.class */
final class ProtocolMessages {
    private static final String BUNDLE_NAME = "properties/protocolmessages";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private ProtocolMessages() {
    }

    static String getString(String str) {
        try {
            return RESOURCE_BUNDLE.getString(str);
        } catch (MissingResourceException e) {
            return '!' + str + '!';
        }
    }
}
