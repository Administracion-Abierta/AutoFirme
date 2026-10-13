package es.gob.afirma.keystores.filters;

import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.keystores.CertificateFilter;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/EncodedCertificateFilter.class */
public final class EncodedCertificateFilter extends CertificateFilter {
    private final String certEncoded;

    public EncodedCertificateFilter(String str) {
        if (str == null) {
            throw new NullPointerException("No se ha indicado el certificado que se desea obtener");
        }
        this.certEncoded = str;
    }

    @Override // es.gob.afirma.keystores.CertificateFilter
    public boolean matches(X509Certificate x509Certificate) {
        try {
            return this.certEncoded.equals(Base64.encode(x509Certificate.getEncoded()));
        } catch (CertificateEncodingException e) {
            Logger.getLogger("es.gob.afirma").warning("No se ha podido codificar el certificado en base 64. Se ignorara: " + e);
            return false;
        }
    }
}
