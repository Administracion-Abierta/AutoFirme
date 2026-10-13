package org.spongycastle.pqc.crypto.newhope;

import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/newhope/NHPrivateKeyParameters.class */
public class NHPrivateKeyParameters extends AsymmetricKeyParameter {
    final short[] secData;

    public NHPrivateKeyParameters(short[] secData) {
        super(true);
        this.secData = Arrays.clone(secData);
    }

    public short[] getSecData() {
        return Arrays.clone(this.secData);
    }
}
