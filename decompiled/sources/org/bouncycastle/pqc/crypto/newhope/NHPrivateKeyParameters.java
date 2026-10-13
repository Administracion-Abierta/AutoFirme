package org.bouncycastle.pqc.crypto.newhope;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/newhope/NHPrivateKeyParameters.class */
public class NHPrivateKeyParameters extends AsymmetricKeyParameter {
    final short[] secData;

    public NHPrivateKeyParameters(short[] sArr) {
        super(true);
        this.secData = Arrays.clone(sArr);
    }

    public short[] getSecData() {
        return Arrays.clone(this.secData);
    }
}
