package org.bouncycastle.pqc.legacy.crypto.qtesla;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/qtesla/QTESLAPublicKeyParameters.class */
public final class QTESLAPublicKeyParameters extends AsymmetricKeyParameter {
    private int securityCategory;
    private byte[] publicKey;

    public QTESLAPublicKeyParameters(int i, byte[] bArr) {
        super(false);
        if (bArr.length != QTESLASecurityCategory.getPublicSize(i)) {
            throw new IllegalArgumentException("invalid key size for security category");
        }
        this.securityCategory = i;
        this.publicKey = Arrays.clone(bArr);
    }

    public int getSecurityCategory() {
        return this.securityCategory;
    }

    public byte[] getPublicData() {
        return Arrays.clone(this.publicKey);
    }
}
