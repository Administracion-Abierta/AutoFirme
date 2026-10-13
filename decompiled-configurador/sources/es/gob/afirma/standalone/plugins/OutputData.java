package es.gob.afirma.standalone.plugins;

import java.io.File;
import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/plugins/OutputData.class */
public class OutputData {
    private boolean success;
    private File dataFile;
    private String signatureFormat;
    private X509Certificate[] certs;

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean z) {
        this.success = z;
    }

    public File getDataFile() {
        return this.dataFile;
    }

    public void setDataFile(File file) {
        this.dataFile = file;
    }

    public String getSignatureFormat() {
        return this.signatureFormat;
    }

    public void setSignatureFormat(String str) {
        this.signatureFormat = str;
    }

    public X509Certificate[] getCerts() {
        if (this.certs != null) {
            return (X509Certificate[]) this.certs.clone();
        }
        return null;
    }

    public void setCerts(X509Certificate[] x509CertificateArr) {
        this.certs = x509CertificateArr != null ? (X509Certificate[]) x509CertificateArr.clone() : null;
    }
}
