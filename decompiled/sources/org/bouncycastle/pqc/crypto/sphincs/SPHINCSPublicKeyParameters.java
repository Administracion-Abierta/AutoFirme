package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/sphincs/SPHINCSPublicKeyParameters.class */
public class SPHINCSPublicKeyParameters extends SPHINCSKeyParameters {
    private final byte[] keyData;

    public SPHINCSPublicKeyParameters(byte[] bArr) {
        super(false, null);
        this.keyData = Arrays.clone(bArr);
    }

    public SPHINCSPublicKeyParameters(byte[] bArr, String str) {
        super(false, str);
        this.keyData = Arrays.clone(bArr);
    }

    public byte[] getKeyData() {
        return Arrays.clone(this.keyData);
    }
}
