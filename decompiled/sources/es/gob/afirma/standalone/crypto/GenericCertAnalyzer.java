package es.gob.afirma.standalone.crypto;

import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/crypto/GenericCertAnalyzer.class */
public final class GenericCertAnalyzer extends CertAnalyzer {
    @Override // es.gob.afirma.standalone.crypto.CertAnalyzer
    public boolean isValidCert(X509Certificate x509Certificate) {
        return true;
    }

    @Override // es.gob.afirma.standalone.crypto.CertAnalyzer
    public CertificateInfo analyzeCert(X509Certificate x509Certificate) {
        return new CertificateInfo(x509Certificate, null, null, null);
    }
}
