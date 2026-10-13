package es.gob.afirma.signers.pades.common;

import es.gob.afirma.core.RuntimeConfigNeededException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/common/PdfIsCertifiedException.class */
public final class PdfIsCertifiedException extends RuntimeConfigNeededException {
    private static final long serialVersionUID = -5155373390967702913L;
    public static final String REQUESTOR_MSG_CODE = "signingCertifiedPdf";

    public PdfIsCertifiedException(String str) {
        super(str, RuntimeConfigNeededException.RequestType.CONFIRM, REQUESTOR_MSG_CODE, PdfExtraParams.ALLOW_SIGNING_CERTIFIED_PDFS);
    }
}
