package es.gob.afirma.keystores.filters;

import es.gob.afirma.keystores.CertificateFilter;
import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/SigningCertificateFilter.class */
public final class SigningCertificateFilter extends CertificateFilter {
    private final AuthenticationDNIeFilter authenticationDnieCertFilter = new AuthenticationDNIeFilter();

    @Override // es.gob.afirma.keystores.CertificateFilter
    public boolean matches(X509Certificate x509Certificate) {
        return !this.authenticationDnieCertFilter.matches(x509Certificate);
    }
}
