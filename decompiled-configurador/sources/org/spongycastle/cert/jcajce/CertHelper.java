package org.spongycastle.cert.jcajce;

import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/jcajce/CertHelper.class */
abstract class CertHelper {
    protected abstract CertificateFactory createCertificateFactory(String str) throws CertificateException, NoSuchProviderException;

    CertHelper() {
    }

    public CertificateFactory getCertificateFactory(String type) throws CertificateException, NoSuchProviderException {
        return createCertificateFactory(type);
    }
}
