package org.spongycastle.cert.jcajce;

import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/jcajce/NamedCertHelper.class */
class NamedCertHelper extends CertHelper {
    private final String providerName;

    NamedCertHelper(String providerName) {
        this.providerName = providerName;
    }

    @Override // org.spongycastle.cert.jcajce.CertHelper
    protected CertificateFactory createCertificateFactory(String type) throws CertificateException, NoSuchProviderException {
        return CertificateFactory.getInstance(type, this.providerName);
    }
}
