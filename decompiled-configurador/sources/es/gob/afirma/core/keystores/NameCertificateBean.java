package es.gob.afirma.core.keystores;

import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/keystores/NameCertificateBean.class */
public class NameCertificateBean {
    private final String alias;
    private final String name;
    private final X509Certificate[] certificateChain;

    public NameCertificateBean(String str, String str2, X509Certificate[] x509CertificateArr) {
        this.alias = str;
        this.name = str2;
        this.certificateChain = x509CertificateArr == null ? null : (X509Certificate[]) x509CertificateArr.clone();
    }

    public String getAlias() {
        return this.alias;
    }

    public String getName() {
        return this.name;
    }

    public X509Certificate getCertificate() {
        if (this.certificateChain == null || this.certificateChain.length <= 0) {
            return null;
        }
        return this.certificateChain[0];
    }

    public X509Certificate[] getCertificateChain() {
        if (this.certificateChain == null) {
            return null;
        }
        return (X509Certificate[]) this.certificateChain.clone();
    }

    public String toString() {
        return this.name;
    }
}
