package es.gob.afirma.standalone.ui.pdf;

import java.util.ResourceBundle;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/SignPdfUiMessages.class */
final class SignPdfUiMessages {
    private static final String BUNDLE_NAME = "properties.signpdfuimessages";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private SignPdfUiMessages() {
    }

    static String getString(String str) {
        try {
            return RESOURCE_BUNDLE.getString(str);
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").warning("Texto '" + str + "' del UI de firma de PDF no encontrado: " + e);
            return '!' + str + '!';
        }
    }

    static String getString(String str, String... strArr) {
        try {
            String string = RESOURCE_BUNDLE.getString(str);
            if (strArr != null && strArr.length > 0) {
                for (int i = 0; i < strArr.length; i++) {
                    if (strArr[i] != null) {
                        string = string.replace("%" + i, strArr[i]);
                    }
                }
            }
            return string;
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").warning("Texto '" + str + "' del UI de firma de PDF no encontrado: " + e);
            return '!' + str + '!';
        }
    }
}
