package org.bouncycastle.jcajce;

import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/PKIXCertRevocationChecker.class */
public interface PKIXCertRevocationChecker {
    void setParameter(String str, Object obj);

    void initialize(PKIXCertRevocationCheckerParameters pKIXCertRevocationCheckerParameters) throws CertPathValidatorException;

    void check(Certificate certificate) throws CertPathValidatorException;
}
