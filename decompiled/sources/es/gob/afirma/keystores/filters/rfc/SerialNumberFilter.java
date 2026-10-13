package es.gob.afirma.keystores.filters.rfc;

import es.gob.afirma.keystores.CertificateFilter;
import java.math.BigInteger;
import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/rfc/SerialNumberFilter.class */
public class SerialNumberFilter extends CertificateFilter {
    private final BigInteger serialNumber;

    public SerialNumberFilter(BigInteger bigInteger) {
        if (bigInteger == null) {
            throw new IllegalArgumentException("El numero de serie clave del filtro no puede ser nulo");
        }
        this.serialNumber = bigInteger;
    }

    @Override // es.gob.afirma.keystores.CertificateFilter
    public boolean matches(X509Certificate x509Certificate) {
        if (x509Certificate == null) {
            return false;
        }
        return this.serialNumber.equals(x509Certificate.getSerialNumber());
    }
}
