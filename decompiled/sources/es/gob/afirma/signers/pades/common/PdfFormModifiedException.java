package es.gob.afirma.signers.pades.common;

import es.gob.afirma.core.RuntimeConfigNeededException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/common/PdfFormModifiedException.class */
public final class PdfFormModifiedException extends RuntimeConfigNeededException {
    private static final long serialVersionUID = -4146182718127282210L;
    public static final String REQUESTOR_MSG_CODE = "signingModifiedPdfForm";

    public PdfFormModifiedException(String str) {
        super(str, RuntimeConfigNeededException.RequestType.CONFIRM, REQUESTOR_MSG_CODE, PdfExtraParams.ALLOW_SIGN_MODIFIED_FORM);
    }
}
