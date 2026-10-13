package es.gob.afirma.keystores.filters;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.keystores.CertificateFilter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/ThumbPrintCertificateFilter.class */
public final class ThumbPrintCertificateFilter extends CertificateFilter {
    private final String digestAlgorithm;
    private final String thumbprint;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    public ThumbPrintCertificateFilter(String str, String str2) {
        if (str == null || str2 == null) {
            throw new IllegalArgumentException("Se debe indicar tanto el algoritmo como la huella digital del certificado");
        }
        this.digestAlgorithm = str;
        this.thumbprint = str2.replace(" ", "");
    }

    @Override // es.gob.afirma.keystores.CertificateFilter
    public boolean matches(X509Certificate x509Certificate) {
        try {
            return this.thumbprint.equalsIgnoreCase(AOUtil.hexify(MessageDigest.getInstance(this.digestAlgorithm).digest(x509Certificate.getEncoded()), ""));
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warning("Algoritmo de huella digital no reconocido: " + e);
            return false;
        } catch (CertificateEncodingException e2) {
            LOGGER.warning("No se ha podido obtener la codificacion del certificado: " + e2);
            return false;
        } catch (Exception e3) {
            LOGGER.warning("No se ha podido filtrar el certificado: " + e3);
            return false;
        }
    }
}
