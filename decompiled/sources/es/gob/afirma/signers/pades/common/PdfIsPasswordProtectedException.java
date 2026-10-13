package es.gob.afirma.signers.pades.common;

import es.gob.afirma.core.RuntimePasswordNeededException;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/common/PdfIsPasswordProtectedException.class */
public final class PdfIsPasswordProtectedException extends RuntimePasswordNeededException {
    private static final long serialVersionUID = -4899395533584256907L;
    public static final String REQUESTOR_MSG_CODE = "pdfpasswordprotected";

    public PdfIsPasswordProtectedException(String str) {
        super(str, REQUESTOR_MSG_CODE, PdfExtraParams.OWNER_PASSWORD_STRING, null);
    }

    public PdfIsPasswordProtectedException(Throwable th) {
        super("El PDF se encuentra protegido con contrasena", REQUESTOR_MSG_CODE, PdfExtraParams.USER_PASSWORD_STRING, th);
    }

    public PdfIsPasswordProtectedException(String str, Throwable th) {
        super(str, REQUESTOR_MSG_CODE, PdfExtraParams.OWNER_PASSWORD_STRING, th);
    }

    @Override // es.gob.afirma.core.RuntimePasswordNeededException
    public void configure(Properties properties, char[] cArr) {
        properties.setProperty(PdfExtraParams.OWNER_PASSWORD_STRING, new String(cArr));
        properties.remove(PdfExtraParams.USER_PASSWORD_STRING);
    }
}
