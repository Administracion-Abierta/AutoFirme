package es.gob.afirma.signers.pades.common;

import es.gob.afirma.core.RuntimeConfigNeededException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/common/PdfHasUnregisteredSignaturesException.class */
public final class PdfHasUnregisteredSignaturesException extends RuntimeConfigNeededException {
    private static final long serialVersionUID = 8047929185538217109L;
    public static final String REQUESTOR_MSG_CODE = "signingPdfWithUnregisteredSigns";

    public PdfHasUnregisteredSignaturesException(String str) {
        super(str, RuntimeConfigNeededException.RequestType.CONFIRM, REQUESTOR_MSG_CODE, PdfExtraParams.ALLOW_COSIGNING_UNREGISTERED_SIGNATURES);
    }
}
