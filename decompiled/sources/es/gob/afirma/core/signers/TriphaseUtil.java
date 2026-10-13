package es.gob.afirma.core.signers;

import es.gob.afirma.core.misc.Base64;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/signers/TriphaseUtil.class */
public final class TriphaseUtil {
    private static final String INCLUDE_ONLY_SIGNNING_CERTIFICATE = "includeOnlySignningCertificate";
    private static final String PARAM_NAME_CERT_SEPARATOR = ",";

    private TriphaseUtil() {
    }

    public static String prepareCertChainParam(Certificate[] certificateArr, Properties properties) throws CertificateEncodingException {
        if (certificateArr == null || certificateArr.length < 1) {
            throw new IllegalArgumentException("La cadena de certificados no puede ser nula ni vacia");
        }
        if (properties == null || Boolean.parseBoolean(properties.getProperty("includeOnlySignningCertificate", Boolean.FALSE.toString()))) {
            return Base64.encode(certificateArr[0].getEncoded(), true);
        }
        StringBuilder sb = new StringBuilder();
        for (Certificate certificate : certificateArr) {
            sb.append(Base64.encode(certificate.getEncoded(), true));
            sb.append(PARAM_NAME_CERT_SEPARATOR);
        }
        String string = sb.toString();
        return string.substring(0, string.length() - PARAM_NAME_CERT_SEPARATOR.length());
    }
}
