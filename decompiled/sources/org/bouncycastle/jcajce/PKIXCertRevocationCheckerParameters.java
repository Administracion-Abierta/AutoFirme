package org.bouncycastle.jcajce;

import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.X509Certificate;
import java.util.Date;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/PKIXCertRevocationCheckerParameters.class */
public class PKIXCertRevocationCheckerParameters {
    private final PKIXExtendedParameters paramsPKIX;
    private final Date validDate;
    private final CertPath certPath;
    private final int index;
    private final X509Certificate signingCert;
    private final PublicKey workingPublicKey;

    public PKIXCertRevocationCheckerParameters(PKIXExtendedParameters pKIXExtendedParameters, Date date, CertPath certPath, int i, X509Certificate x509Certificate, PublicKey publicKey) {
        this.paramsPKIX = pKIXExtendedParameters;
        this.validDate = date;
        this.certPath = certPath;
        this.index = i;
        this.signingCert = x509Certificate;
        this.workingPublicKey = publicKey;
    }

    public PKIXExtendedParameters getParamsPKIX() {
        return this.paramsPKIX;
    }

    public Date getValidDate() {
        return new Date(this.validDate.getTime());
    }

    public CertPath getCertPath() {
        return this.certPath;
    }

    public int getIndex() {
        return this.index;
    }

    public X509Certificate getSigningCert() {
        return this.signingCert;
    }

    public PublicKey getWorkingPublicKey() {
        return this.workingPublicKey;
    }
}
