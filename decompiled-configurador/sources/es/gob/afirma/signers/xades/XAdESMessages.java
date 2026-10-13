package es.gob.afirma.signers.xades;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/xades/XAdESMessages.class */
public class XAdESMessages {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String BUNDLE_NAME = "xadesmessages";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());

    private XAdESMessages() {
    }

    static String getString(String str) {
        try {
            return RESOURCE_BUNDLE.getString(str);
        } catch (Exception e) {
            LOGGER.warning("No se ha encontrado el recurso textual '" + str + "': " + e);
            return '!' + str + '!';
        }
    }

    static String getString(String str, String str2) {
        try {
            return RESOURCE_BUNDLE.getString(str).replace("%0", str2);
        } catch (Exception e) {
            LOGGER.warning("No se ha encontrado el recurso textual '" + str + "': " + e);
            return '!' + str + '!';
        }
    }

    static String getString(String str, String[] strArr) {
        try {
            String string = RESOURCE_BUNDLE.getString(str);
            if (strArr != null) {
                for (int i = 0; i < strArr.length; i++) {
                    string = string.replace("%" + i, strArr[i]);
                }
            }
            return string;
        } catch (Exception e) {
            LOGGER.warning("No se ha encontrado el recurso textual '" + str + "': " + e);
            return '!' + str + '!';
        }
    }
}
