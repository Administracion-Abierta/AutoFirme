package org.bouncycastle.crypto.threshold;

import java.io.IOException;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/threshold/ShamirSplitSecretShare.class */
public class ShamirSplitSecretShare implements SecretShare {
    private final byte[] secretShare;
    final int r;

    public ShamirSplitSecretShare(byte[] bArr, int i) {
        this.secretShare = Arrays.clone(bArr);
        this.r = i;
    }

    public ShamirSplitSecretShare(byte[] bArr) {
        this.secretShare = Arrays.clone(bArr);
        this.r = 1;
    }

    @Override // org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return Arrays.clone(this.secretShare);
    }
}
