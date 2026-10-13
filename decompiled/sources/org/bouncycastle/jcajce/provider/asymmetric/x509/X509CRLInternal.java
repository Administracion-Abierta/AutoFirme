package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.security.cert.CRLException;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.jcajce.util.JcaJceHelper;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/x509/X509CRLInternal.class */
class X509CRLInternal extends X509CRLImpl {
    private final byte[] encoding;
    private final CRLException exception;

    X509CRLInternal(JcaJceHelper jcaJceHelper, CertificateList certificateList, String str, byte[] bArr, boolean z, byte[] bArr2, CRLException cRLException) {
        super(jcaJceHelper, certificateList, str, bArr, z);
        this.encoding = bArr2;
        this.exception = cRLException;
    }

    @Override // java.security.cert.X509CRL
    public byte[] getEncoded() throws CRLException {
        if (null != this.exception) {
            throw this.exception;
        }
        if (null == this.encoding) {
            throw new CRLException();
        }
        return this.encoding;
    }
}
