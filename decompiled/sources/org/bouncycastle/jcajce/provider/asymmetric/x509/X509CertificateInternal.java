package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.security.cert.CertificateEncodingException;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.jcajce.util.JcaJceHelper;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/x509/X509CertificateInternal.class */
class X509CertificateInternal extends X509CertificateImpl {
    private final byte[] encoding;
    private final CertificateEncodingException exception;

    X509CertificateInternal(JcaJceHelper jcaJceHelper, Certificate certificate, BasicConstraints basicConstraints, boolean[] zArr, String str, byte[] bArr, byte[] bArr2, CertificateEncodingException certificateEncodingException) {
        super(jcaJceHelper, certificate, basicConstraints, zArr, str, bArr);
        this.encoding = bArr2;
        this.exception = certificateEncodingException;
    }

    @Override // java.security.cert.Certificate
    public byte[] getEncoded() throws CertificateEncodingException {
        if (null != this.exception) {
            throw this.exception;
        }
        if (null == this.encoding) {
            throw new CertificateEncodingException();
        }
        return this.encoding;
    }
}
