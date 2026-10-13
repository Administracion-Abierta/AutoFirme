package es.gob.afirma.core;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/SigningLTSException.class */
public class SigningLTSException extends RuntimeConfigNeededException {
    private static final long serialVersionUID = 995443738935981665L;
    public static final String REQUESTOR_MSG_CODE = "signingLts";
    public static final String REQUESTOR_POSSIBLE_MSG_CODE = "signingLtsPossible";
    private static final String EXTRA_PARAM_NEEDED = "allowSignLTSignature";

    public SigningLTSException(String str) {
        super(str, RuntimeConfigNeededException.RequestType.CONFIRM, REQUESTOR_MSG_CODE, "allowSignLTSignature");
    }

    public SigningLTSException(String str, boolean z) {
        super(str, RuntimeConfigNeededException.RequestType.CONFIRM, z ? REQUESTOR_POSSIBLE_MSG_CODE : REQUESTOR_MSG_CODE, "allowSignLTSignature");
    }

    public SigningLTSException(String str, Throwable th, boolean z) {
        super(str, RuntimeConfigNeededException.RequestType.CONFIRM, z ? REQUESTOR_POSSIBLE_MSG_CODE : REQUESTOR_MSG_CODE, "allowSignLTSignature", th);
    }
}
