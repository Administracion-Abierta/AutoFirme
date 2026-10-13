package org.bouncycastle.cert.jcajce;

import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/jcajce/CertHelper.class */
abstract class CertHelper {
    CertHelper() {
    }

    public CertificateFactory getCertificateFactory(String str) throws CertificateException, NoSuchProviderException {
        return createCertificateFactory(str);
    }

    protected abstract CertificateFactory createCertificateFactory(String str) throws CertificateException, NoSuchProviderException;
}
