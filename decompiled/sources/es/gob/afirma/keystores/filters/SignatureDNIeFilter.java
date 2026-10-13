package es.gob.afirma.keystores.filters;

import es.gob.afirma.keystores.CertificateFilter;
import es.gob.afirma.keystores.filters.rfc.KeyUsageFilter;
import es.gob.afirma.keystores.filters.rfc.RFC2254CertificateFilter;
import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/SignatureDNIeFilter.class */
public final class SignatureDNIeFilter extends CertificateFilter {
    private final RFC2254CertificateFilter rfc2254Filter = new RFC2254CertificateFilter(null, "(&(cn=AC DNIE *)(ou=DNIE)(o=DIRECCION GENERAL DE LA POLICIA)(c=ES))");
    private final KeyUsageFilter keyUsageFilter = new KeyUsageFilter(KeyUsageFilter.SIGN_CERT_USAGE);

    @Override // es.gob.afirma.keystores.CertificateFilter
    public boolean matches(X509Certificate x509Certificate) {
        return this.keyUsageFilter.matches(x509Certificate) && this.rfc2254Filter.matches(x509Certificate);
    }
}
