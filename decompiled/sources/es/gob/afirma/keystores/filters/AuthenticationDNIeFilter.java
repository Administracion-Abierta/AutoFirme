package es.gob.afirma.keystores.filters;

import es.gob.afirma.keystores.CertificateFilter;
import es.gob.afirma.keystores.filters.rfc.KeyUsageFilter;
import es.gob.afirma.keystores.filters.rfc.RFC2254CertificateFilter;
import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/AuthenticationDNIeFilter.class */
public class AuthenticationDNIeFilter extends CertificateFilter {
    private final RFC2254CertificateFilter rfc2254Filter = new RFC2254CertificateFilter(null, "(&(cn=AC DNIE *)(ou=DNIE)(o=DIRECCION GENERAL DE LA POLICIA)(c=ES))");
    private final KeyUsageFilter keyUsageFilter = new KeyUsageFilter(AUTHENTICATION_CERT_USAGE);
    private static final Boolean[] AUTHENTICATION_CERT_USAGE = {Boolean.TRUE, null, null, null, null, null, null, null, null};

    @Override // es.gob.afirma.keystores.CertificateFilter
    public boolean matches(X509Certificate x509Certificate) {
        return this.keyUsageFilter.matches(x509Certificate) && this.rfc2254Filter.matches(x509Certificate);
    }
}
