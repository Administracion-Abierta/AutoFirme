package org.bouncycastle.cert.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.cert.X509CertificateHolder;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/jcajce/JcaX509CertificateConverter.class */
public class JcaX509CertificateConverter {
    private CertHelper helper;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/jcajce/JcaX509CertificateConverter$ExCertificateException.class */
    private static class ExCertificateException extends CertificateException {
        private Throwable cause;

        public ExCertificateException(String str, Throwable th) {
            super(str);
            this.cause = th;
        }

        @Override // java.lang.Throwable
        public Throwable getCause() {
            return this.cause;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/jcajce/JcaX509CertificateConverter$ExCertificateParsingException.class */
    private static class ExCertificateParsingException extends CertificateParsingException {
        private Throwable cause;

        public ExCertificateParsingException(String str, Throwable th) {
            super(str);
            this.cause = th;
        }

        @Override // java.lang.Throwable
        public Throwable getCause() {
            return this.cause;
        }
    }

    public JcaX509CertificateConverter() {
        this.helper = new DefaultCertHelper();
        this.helper = new DefaultCertHelper();
    }

    public JcaX509CertificateConverter setProvider(Provider provider) {
        this.helper = new ProviderCertHelper(provider);
        return this;
    }

    public JcaX509CertificateConverter setProvider(String str) {
        this.helper = new NamedCertHelper(str);
        return this;
    }

    public X509Certificate getCertificate(X509CertificateHolder x509CertificateHolder) throws CertificateException {
        try {
            return (X509Certificate) this.helper.getCertificateFactory(XMLX509Certificate.JCA_CERT_ID).generateCertificate(new ByteArrayInputStream(x509CertificateHolder.getEncoded()));
        } catch (IOException e) {
            throw new ExCertificateParsingException("exception parsing certificate: " + e.getMessage(), e);
        } catch (NoSuchProviderException e2) {
            throw new ExCertificateException("cannot find required provider:" + e2.getMessage(), e2);
        }
    }
}
