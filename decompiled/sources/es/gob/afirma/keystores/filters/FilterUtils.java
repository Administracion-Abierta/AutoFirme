package es.gob.afirma.keystores.filters;

import es.gob.afirma.core.misc.AOUtil;
import java.math.BigInteger;
import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/FilterUtils.class */
final class FilterUtils {
    private static final String[] SUBJECT_SN_RDN = {"serialnumber", "2.5.4.5"};

    private FilterUtils() {
    }

    static String getSubjectSN(X509Certificate x509Certificate) {
        String name = x509Certificate.getSubjectX500Principal().getName();
        for (String str : SUBJECT_SN_RDN) {
            String rDNvalueFromLdapName = AOUtil.getRDNvalueFromLdapName(str, name);
            if (rDNvalueFromLdapName != null) {
                return rDNvalueFromLdapName.replace("#", "");
            }
        }
        return null;
    }

    static String bigIntegerToHex(BigInteger bigInteger) {
        return AOUtil.hexify(bigInteger.toByteArray(), "");
    }
}
