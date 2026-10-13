package es.gob.afirma.keystores.filters.rfc;

import es.gob.afirma.keystores.CertificateFilter;
import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/rfc/KeyUsageFilter.class */
public class KeyUsageFilter extends CertificateFilter {
    private static final int KEYUSAGE_NBITS = 9;
    private final Boolean[] keyUsageFilter;
    public static final Boolean[] SIGN_CERT_USAGE = {null, Boolean.TRUE, null, null, null, null, null, null, null};

    @Override // es.gob.afirma.keystores.CertificateFilter
    public boolean matches(X509Certificate x509Certificate) {
        boolean[] keyUsage;
        if (x509Certificate != null && this.keyUsageFilter.length == 9 && (keyUsage = x509Certificate.getKeyUsage()) != null) {
            for (int i = 0; i < keyUsage.length; i++) {
                if (this.keyUsageFilter[i] != null && this.keyUsageFilter[i].booleanValue() != keyUsage[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public KeyUsageFilter(Boolean[] boolArr) {
        if (boolArr == null) {
            throw new IllegalArgumentException("El criterio de filtrado no puede ser nulo");
        }
        this.keyUsageFilter = (Boolean[]) boolArr.clone();
    }
}
