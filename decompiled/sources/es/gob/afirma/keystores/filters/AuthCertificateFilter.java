package es.gob.afirma.keystores.filters;

import es.gob.afirma.keystores.CertificateFilter;
import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/AuthCertificateFilter.class */
public final class AuthCertificateFilter extends CertificateFilter {
    private final SignatureDNIeFilter signatureDnieCertFilter = new SignatureDNIeFilter();

    @Override // es.gob.afirma.keystores.CertificateFilter
    public boolean matches(X509Certificate x509Certificate) {
        return !this.signatureDnieCertFilter.matches(x509Certificate);
    }
}
