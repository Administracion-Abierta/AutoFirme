package org.spongycastle.cert.jcajce;

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/jcajce/DefaultCertHelper.class */
class DefaultCertHelper extends CertHelper {
    DefaultCertHelper() {
    }

    @Override // org.spongycastle.cert.jcajce.CertHelper
    protected CertificateFactory createCertificateFactory(String type) throws CertificateException {
        return CertificateFactory.getInstance(type);
    }
}
