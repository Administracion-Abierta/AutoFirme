package org.spongycastle.cert.dane;

import java.io.OutputStream;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/dane/TruncatingDigestCalculator.class */
public class TruncatingDigestCalculator implements DigestCalculator {
    private final DigestCalculator baseCalculator;
    private final int length;

    public TruncatingDigestCalculator(DigestCalculator baseCalculator) {
        this(baseCalculator, 28);
    }

    public TruncatingDigestCalculator(DigestCalculator baseCalculator, int length) {
        this.baseCalculator = baseCalculator;
        this.length = length;
    }

    @Override // org.spongycastle.operator.DigestCalculator
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.baseCalculator.getAlgorithmIdentifier();
    }

    @Override // org.spongycastle.operator.DigestCalculator
    public OutputStream getOutputStream() {
        return this.baseCalculator.getOutputStream();
    }

    @Override // org.spongycastle.operator.DigestCalculator
    public byte[] getDigest() {
        byte[] rv = new byte[this.length];
        byte[] dig = this.baseCalculator.getDigest();
        System.arraycopy(dig, 0, rv, 0, rv.length);
        return rv;
    }
}
