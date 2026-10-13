package org.bouncycastle.cert.crmf.jcajce;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.crmf.CertificateRepMessageBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/jcajce/JcaCertificateRepMessageBuilder.class */
public class JcaCertificateRepMessageBuilder extends CertificateRepMessageBuilder {
    public JcaCertificateRepMessageBuilder(X509Certificate... x509CertificateArr) throws CertificateEncodingException {
        super(convert(x509CertificateArr));
    }

    private static X509CertificateHolder[] convert(X509Certificate... x509CertificateArr) throws CertificateEncodingException {
        X509CertificateHolder[] x509CertificateHolderArr = new X509CertificateHolder[x509CertificateArr.length];
        for (int i = 0; i != x509CertificateHolderArr.length; i++) {
            x509CertificateHolderArr[i] = new JcaX509CertificateHolder(x509CertificateArr[i]);
        }
        return x509CertificateHolderArr;
    }
}
