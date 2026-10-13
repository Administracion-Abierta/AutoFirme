package org.spongycastle.cert.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.cert.X509CRLHolder;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/jcajce/JcaX509CRLConverter.class */
public class JcaX509CRLConverter {
    private CertHelper helper;

    public JcaX509CRLConverter() {
        this.helper = new DefaultCertHelper();
        this.helper = new DefaultCertHelper();
    }

    public JcaX509CRLConverter setProvider(Provider provider) {
        this.helper = new ProviderCertHelper(provider);
        return this;
    }

    public JcaX509CRLConverter setProvider(String providerName) {
        this.helper = new NamedCertHelper(providerName);
        return this;
    }

    public X509CRL getCRL(X509CRLHolder crlHolder) throws CRLException {
        try {
            CertificateFactory cFact = this.helper.getCertificateFactory(XMLX509Certificate.JCA_CERT_ID);
            return (X509CRL) cFact.generateCRL(new ByteArrayInputStream(crlHolder.getEncoded()));
        } catch (IOException e) {
            throw new ExCRLException("exception parsing certificate: " + e.getMessage(), e);
        } catch (NoSuchProviderException e2) {
            throw new ExCRLException("cannot find required provider:" + e2.getMessage(), e2);
        } catch (CertificateException e3) {
            throw new ExCRLException("cannot create factory: " + e3.getMessage(), e3);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/jcajce/JcaX509CRLConverter$ExCRLException.class */
    private class ExCRLException extends CRLException {
        private Throwable cause;

        public ExCRLException(String msg, Throwable cause) {
            super(msg);
            this.cause = cause;
        }

        @Override // java.lang.Throwable
        public Throwable getCause() {
            return this.cause;
        }
    }
}
