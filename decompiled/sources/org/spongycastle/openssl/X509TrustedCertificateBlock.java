package org.spongycastle.openssl;

import java.io.IOException;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/X509TrustedCertificateBlock.class */
public class X509TrustedCertificateBlock {
    private final X509CertificateHolder certificateHolder;
    private final CertificateTrustBlock trustBlock;

    public X509TrustedCertificateBlock(X509CertificateHolder certificateHolder, CertificateTrustBlock trustBlock) {
        this.certificateHolder = certificateHolder;
        this.trustBlock = trustBlock;
    }

    public X509TrustedCertificateBlock(byte[] encoding) throws IOException {
        ASN1InputStream aIn = new ASN1InputStream(encoding);
        this.certificateHolder = new X509CertificateHolder(aIn.readObject().getEncoded());
        this.trustBlock = new CertificateTrustBlock(aIn.readObject().getEncoded());
    }

    public byte[] getEncoded() throws IOException {
        return Arrays.concatenate(this.certificateHolder.getEncoded(), this.trustBlock.toASN1Sequence().getEncoded());
    }

    public X509CertificateHolder getCertificateHolder() {
        return this.certificateHolder;
    }

    public CertificateTrustBlock getTrustBlock() {
        return this.trustBlock;
    }
}
