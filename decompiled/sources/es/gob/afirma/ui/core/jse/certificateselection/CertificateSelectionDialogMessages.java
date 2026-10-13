package es.gob.afirma.ui.core.jse.certificateselection;

import java.util.Locale;
import java.util.ResourceBundle;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/certificateselection/CertificateSelectionDialogMessages.class */
final class CertificateSelectionDialogMessages {
    private static final String BUNDLE_NAME = "certdialogmessages";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());

    private CertificateSelectionDialogMessages() {
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
            if (strArr != null) {
                for (int i = 0; i < strArr.length; i++) {
                    string = string.replace("%" + i, strArr[i]);
                }
            }
            return string;
        } catch (Exception e) {
            return '!' + str + '!';
        }
    }
}
