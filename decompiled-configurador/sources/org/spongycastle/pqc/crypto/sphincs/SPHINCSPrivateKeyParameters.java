package org.spongycastle.pqc.crypto.sphincs;

import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/sphincs/SPHINCSPrivateKeyParameters.class */
public class SPHINCSPrivateKeyParameters extends AsymmetricKeyParameter {
    private final byte[] keyData;

    public SPHINCSPrivateKeyParameters(byte[] keyData) {
        super(true);
        this.keyData = Arrays.clone(keyData);
    }

    public byte[] getKeyData() {
        return Arrays.clone(this.keyData);
    }
}
