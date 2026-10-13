package org.spongycastle.cert.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.cert.X509CertificateHolder;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/jcajce/JcaX509CertificateConverter.class */
public class JcaX509CertificateConverter {
    private CertHelper helper;

    public JcaX509CertificateConverter() {
        this.helper = new DefaultCertHelper();
        this.helper = new DefaultCertHelper();
    }

    public JcaX509CertificateConverter setProvider(Provider provider) {
        this.helper = new ProviderCertHelper(provider);
        return this;
    }

    public JcaX509CertificateConverter setProvider(String providerName) {
        this.helper = new NamedCertHelper(providerName);
        return this;
    }

    public X509Certificate getCertificate(X509CertificateHolder certHolder) throws CertificateException {
        try {
            CertificateFactory cFact = this.helper.getCertificateFactory(XMLX509Certificate.JCA_CERT_ID);
            return (X509Certificate) cFact.generateCertificate(new ByteArrayInputStream(certHolder.getEncoded()));
        } catch (IOException e) {
            throw new ExCertificateParsingException("exception parsing certificate: " + e.getMessage(), e);
        } catch (NoSuchProviderException e2) {
            throw new ExCertificateException("cannot find required provider:" + e2.getMessage(), e2);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/jcajce/JcaX509CertificateConverter$ExCertificateParsingException.class */
    private class ExCertificateParsingException extends CertificateParsingException {
        private Throwable cause;

        public ExCertificateParsingException(String msg, Throwable cause) {
            super(msg);
            this.cause = cause;
        }

        @Override // java.lang.Throwable
        public Throwable getCause() {
            return this.cause;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/jcajce/JcaX509CertificateConverter$ExCertificateException.class */
    private class ExCertificateException extends CertificateException {
        private Throwable cause;

        public ExCertificateException(String msg, Throwable cause) {
            super(msg);
            this.cause = cause;
        }

        @Override // java.lang.Throwable
        public Throwable getCause() {
            return this.cause;
        }
    }
}
