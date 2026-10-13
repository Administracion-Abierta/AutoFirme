package es.gob.afirma.core;

import es.gob.afirma.signers.xades.XAdESExtraParams;
import java.util.Properties;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/AGEPolicyIncompatibilityException.class */
public class AGEPolicyIncompatibilityException extends CustomRuntimeConfigNeededException {
    private static final long serialVersionUID = 8278487852684799319L;
    public static final String REQUESTOR_SIGN_MSG_CODE = "agePolicyIncompatibilitySign";
    public static final String REQUESTOR_COSIGN_MSG_CODE = "agePolicyIncompatibilityCosign";
    public static final String REQUESTOR_COUNTERSIGN_MSG_CODE = "agePolicyIncompatibilityCounterSign";
    private static final String EXTRA_PARAM_NEEDED = "avoidAGEPolicyIncompatibilities";
    public static final int OP_SIGN = 1;
    public static final int OP_COSIGN = 2;
    public static final int OP_COUNTERSIGN = 3;

    public AGEPolicyIncompatibilityException(String str) {
        this(str, 1);
    }

    public AGEPolicyIncompatibilityException(String str, int i) {
        super(str, RuntimeConfigNeededException.RequestType.CONFIRM, getMessageCode(i), "avoidAGEPolicyIncompatibilities");
    }

    public AGEPolicyIncompatibilityException(String str, Throwable th) {
        this(str, 1, th);
    }

    public AGEPolicyIncompatibilityException(String str, int i, Throwable th) {
        super(str, RuntimeConfigNeededException.RequestType.CONFIRM, getMessageCode(i), "avoidAGEPolicyIncompatibilities", th);
    }

    @Override // es.gob.afirma.core.CustomRuntimeConfigNeededException
    public void prepareOperationWithConfirmation(Properties properties) {
        if (properties != null) {
            properties.remove(XAdESExtraParams.POLICY_IDENTIFIER);
            properties.remove(XAdESExtraParams.POLICY_IDENTIFIER_HASH);
            properties.remove(XAdESExtraParams.POLICY_IDENTIFIER_HASH_ALGORITHM);
            properties.remove(XAdESExtraParams.POLICY_DESCRIPTION);
            properties.remove(XAdESExtraParams.POLICY_QUALIFIER);
        }
    }

    private static String getMessageCode(int i) {
        String str;
        switch (i) {
            case 2:
                str = REQUESTOR_COSIGN_MSG_CODE;
                break;
            case 3:
                str = REQUESTOR_COUNTERSIGN_MSG_CODE;
                break;
            default:
                str = REQUESTOR_SIGN_MSG_CODE;
                break;
        }
        return str;
    }
}
