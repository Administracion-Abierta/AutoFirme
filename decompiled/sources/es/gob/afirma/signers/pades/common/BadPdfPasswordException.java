package es.gob.afirma.signers.pades.common;

import es.gob.afirma.core.RuntimePasswordNeededException;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/common/BadPdfPasswordException.class */
public final class BadPdfPasswordException extends RuntimePasswordNeededException {
    private static final long serialVersionUID = 8271382878128711677L;
    public static final String REQUESTOR_MSG_CODE = "pdfbadpassword";

    public BadPdfPasswordException(String str) {
        super(str, REQUESTOR_MSG_CODE, PdfExtraParams.USER_PASSWORD_STRING, null);
    }

    public BadPdfPasswordException(Throwable th) {
        super("La contrasena del PDF no es correcta", REQUESTOR_MSG_CODE, PdfExtraParams.USER_PASSWORD_STRING, th);
    }

    public BadPdfPasswordException(String str, Throwable th) {
        super(str, REQUESTOR_MSG_CODE, PdfExtraParams.USER_PASSWORD_STRING, th);
    }

    @Override // es.gob.afirma.core.RuntimePasswordNeededException
    public void configure(Properties properties, char[] cArr) {
        properties.setProperty(PdfExtraParams.OWNER_PASSWORD_STRING, new String(cArr));
        properties.remove(PdfExtraParams.USER_PASSWORD_STRING);
    }
}
