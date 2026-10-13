package org.spongycastle.x509;

import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.util.Collection;
import java.util.Set;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/x509/PKIXAttrCertChecker.class */
public abstract class PKIXAttrCertChecker implements Cloneable {
    public abstract Set getSupportedExtensions();

    public abstract void check(X509AttributeCertificate x509AttributeCertificate, CertPath certPath, CertPath certPath2, Collection collection) throws CertPathValidatorException;

    public abstract Object clone();
}
