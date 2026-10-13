package es.gob.afirma.signers.pades.common;

import es.gob.afirma.core.RuntimeConfigNeededException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/common/SuspectedPSAException.class */
public class SuspectedPSAException extends RuntimeConfigNeededException {
    private static final long serialVersionUID = 3307433810679035701L;
    public static final String REQUESTOR_MSG_CODE = "pdfShadowAttackSuspect";

    public SuspectedPSAException(String str) {
        super(str, RuntimeConfigNeededException.RequestType.CONFIRM, REQUESTOR_MSG_CODE, "allowShadowAttack");
    }
}
