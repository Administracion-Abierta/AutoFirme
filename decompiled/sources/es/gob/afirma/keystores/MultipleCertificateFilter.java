package es.gob.afirma.keystores;

import es.gob.afirma.core.keystores.KeyStoreManager;
import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/MultipleCertificateFilter.class */
public final class MultipleCertificateFilter extends CertificateFilter {
    private final CertificateFilter[] filters;

    public MultipleCertificateFilter(CertificateFilter[] certificateFilterArr) {
        if (certificateFilterArr == null) {
            throw new IllegalArgumentException("Listado nulo de filtros de certificados");
        }
        this.filters = (CertificateFilter[]) certificateFilterArr.clone();
    }

    @Override // es.gob.afirma.keystores.CertificateFilter
    public boolean matches(X509Certificate x509Certificate) {
        for (CertificateFilter certificateFilter : this.filters) {
            if (!certificateFilter.matches(x509Certificate)) {
                return false;
            }
        }
        return true;
    }

    @Override // es.gob.afirma.keystores.CertificateFilter
    public String[] matches(String[] strArr, KeyStoreManager keyStoreManager) {
        String[] strArrMatches = (String[]) strArr.clone();
        for (CertificateFilter certificateFilter : this.filters) {
            strArrMatches = certificateFilter.matches(strArrMatches, keyStoreManager);
        }
        return strArrMatches;
    }
}
