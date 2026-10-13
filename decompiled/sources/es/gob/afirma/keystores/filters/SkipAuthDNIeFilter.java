package es.gob.afirma.keystores.filters;

import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/SkipAuthDNIeFilter.class */
public final class SkipAuthDNIeFilter extends AuthenticationDNIeFilter {
    @Override // es.gob.afirma.keystores.filters.AuthenticationDNIeFilter, es.gob.afirma.keystores.CertificateFilter
    public boolean matches(X509Certificate x509Certificate) {
        return !super.matches(x509Certificate);
    }
}
