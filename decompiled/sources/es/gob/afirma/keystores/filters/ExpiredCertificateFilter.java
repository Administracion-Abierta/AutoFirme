package es.gob.afirma.keystores.filters;

import es.gob.afirma.keystores.CertificateFilter;
import java.security.cert.X509Certificate;
import java.util.Date;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/ExpiredCertificateFilter.class */
public final class ExpiredCertificateFilter extends CertificateFilter {
    private boolean showExpired;

    public ExpiredCertificateFilter() {
        this.showExpired = false;
        this.showExpired = false;
    }

    public ExpiredCertificateFilter(boolean z) {
        this.showExpired = false;
        this.showExpired = z;
    }

    @Override // es.gob.afirma.keystores.CertificateFilter
    public boolean matches(X509Certificate x509Certificate) {
        if (!this.showExpired) {
            try {
                x509Certificate.checkValidity(new Date());
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}
