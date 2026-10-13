package es.gob.afirma.standalone.ui.preferences;

import es.gob.afirma.core.signers.AOSignConstants;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/AgePolicy.class */
public class AgePolicy {
    private static final String URN_OID_PREFIX = "urn:oid:";
    private static final String OID_AGE_1_9 = "2.16.724.1.3.1.1.2.1.9";
    private static final String OID_AGE_1_8 = "2.16.724.1.3.1.1.2.1.8";

    public static boolean isAGEPolicy(String str, String str2) {
        if ("CAdES".equalsIgnoreCase(str2) || AOSignConstants.SIGN_FORMAT_XADES.equalsIgnoreCase(str2)) {
            return OID_AGE_1_9.equals(str) || "urn:oid:2.16.724.1.3.1.1.2.1.9".equalsIgnoreCase(str) || OID_AGE_1_8.equals(str) || "urn:oid:2.16.724.1.3.1.1.2.1.8".equalsIgnoreCase(str);
        }
        if ("PAdES".equalsIgnoreCase(str2)) {
            return OID_AGE_1_9.equals(str) || "urn:oid:2.16.724.1.3.1.1.2.1.9".equalsIgnoreCase(str);
        }
        return false;
    }

    public static boolean isAGEPolicy18(String str) {
        return OID_AGE_1_8.equals(str) || "urn:oid:2.16.724.1.3.1.1.2.1.8".equalsIgnoreCase(str);
    }

    public static boolean isAGEPolicy19(String str) {
        return OID_AGE_1_9.equals(str) || "urn:oid:2.16.724.1.3.1.1.2.1.9".equalsIgnoreCase(str);
    }
}
