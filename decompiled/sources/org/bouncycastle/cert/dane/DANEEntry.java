package org.bouncycastle.cert.dane;

import java.io.IOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/dane/DANEEntry.class */
public class DANEEntry {
    public static final int CERT_USAGE_CA = 0;
    public static final int CERT_USAGE_PKIX_VALIDATE = 1;
    public static final int CERT_USAGE_TRUST_ANCHOR = 2;
    public static final int CERT_USAGE_ACCEPT = 3;
    static final int CERT_USAGE = 0;
    static final int SELECTOR = 1;
    static final int MATCHING_TYPE = 2;
    private final String domainName;
    private final byte[] flags;
    private final X509CertificateHolder certHolder;

    DANEEntry(String str, byte[] bArr, X509CertificateHolder x509CertificateHolder) {
        this.flags = bArr;
        this.domainName = str;
        this.certHolder = x509CertificateHolder;
    }

    public DANEEntry(String str, byte[] bArr) throws IOException {
        this(str, Arrays.copyOfRange(bArr, 0, 3), new X509CertificateHolder(Arrays.copyOfRange(bArr, 3, bArr.length)));
    }

    public byte[] getFlags() {
        return Arrays.clone(this.flags);
    }

    public X509CertificateHolder getCertificate() {
        return this.certHolder;
    }

    public String getDomainName() {
        return this.domainName;
    }

    public byte[] getRDATA() throws IOException {
        byte[] encoded = this.certHolder.getEncoded();
        byte[] bArr = new byte[this.flags.length + encoded.length];
        System.arraycopy(this.flags, 0, bArr, 0, this.flags.length);
        System.arraycopy(encoded, 0, bArr, this.flags.length, encoded.length);
        return bArr;
    }

    public static boolean isValidCertificate(byte[] bArr) {
        return (bArr[0] >= 0 || bArr[0] <= 3) && bArr[1] == 0 && bArr[2] == 0;
    }
}
