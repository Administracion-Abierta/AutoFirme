package es.gob.afirma.standalone.crypto;

import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/crypto/CertAnalyzer.class */
public abstract class CertAnalyzer {
    public abstract boolean isValidCert(X509Certificate x509Certificate);

    public abstract CertificateInfo analyzeCert(X509Certificate x509Certificate);

    public static CertificateInfo getCertInformation(X509Certificate x509Certificate) {
        if (x509Certificate.getIssuerX500Principal().toString().contains("AC DNIE")) {
            return new DnieCertAnalyzer().analyzeCert(x509Certificate);
        }
        return new GenericCertAnalyzer().analyzeCert(x509Certificate);
    }
}
