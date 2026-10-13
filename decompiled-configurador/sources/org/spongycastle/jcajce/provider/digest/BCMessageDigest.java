package org.spongycastle.jcajce.provider.digest;

import java.security.MessageDigest;
import org.spongycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/digest/BCMessageDigest.class */
public class BCMessageDigest extends MessageDigest {
    protected Digest digest;

    protected BCMessageDigest(Digest digest) {
        super(digest.getAlgorithmName());
        this.digest = digest;
    }

    @Override // java.security.MessageDigestSpi
    public void engineReset() {
        this.digest.reset();
    }

    @Override // java.security.MessageDigestSpi
    public void engineUpdate(byte input) {
        this.digest.update(input);
    }

    @Override // java.security.MessageDigestSpi
    public void engineUpdate(byte[] input, int offset, int len) {
        this.digest.update(input, offset, len);
    }

    @Override // java.security.MessageDigestSpi
    public byte[] engineDigest() {
        byte[] digestBytes = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(digestBytes, 0);
        return digestBytes;
    }
}
