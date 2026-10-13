package org.bouncycastle.cert.jcajce;

import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/jcajce/NamedCertHelper.class */
class NamedCertHelper extends CertHelper {
    private final String providerName;

    NamedCertHelper(String str) {
        this.providerName = str;
    }

    @Override // org.bouncycastle.cert.jcajce.CertHelper
    protected CertificateFactory createCertificateFactory(String str) throws CertificateException, NoSuchProviderException {
        return CertificateFactory.getInstance(str, this.providerName);
    }
}
