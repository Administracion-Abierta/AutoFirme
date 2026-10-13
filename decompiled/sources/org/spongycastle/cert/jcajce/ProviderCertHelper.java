package org.spongycastle.cert.jcajce;

import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/jcajce/ProviderCertHelper.class */
class ProviderCertHelper extends CertHelper {
    private final Provider provider;

    ProviderCertHelper(Provider provider) {
        this.provider = provider;
    }

    @Override // org.spongycastle.cert.jcajce.CertHelper
    protected CertificateFactory createCertificateFactory(String type) throws CertificateException {
        return CertificateFactory.getInstance(type, this.provider);
    }
}
