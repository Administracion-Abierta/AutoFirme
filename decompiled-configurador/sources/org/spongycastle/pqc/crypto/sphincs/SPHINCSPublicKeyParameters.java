package org.spongycastle.pqc.crypto.sphincs;

import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/sphincs/SPHINCSPublicKeyParameters.class */
public class SPHINCSPublicKeyParameters extends AsymmetricKeyParameter {
    private final byte[] keyData;

    public SPHINCSPublicKeyParameters(byte[] keyData) {
        super(false);
        this.keyData = Arrays.clone(keyData);
    }

    public byte[] getKeyData() {
        return Arrays.clone(this.keyData);
    }
}
