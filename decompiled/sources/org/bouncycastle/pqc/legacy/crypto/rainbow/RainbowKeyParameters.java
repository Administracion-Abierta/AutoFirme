package org.bouncycastle.pqc.legacy.crypto.rainbow;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/rainbow/RainbowKeyParameters.class */
public class RainbowKeyParameters extends AsymmetricKeyParameter {
    private int docLength;

    public RainbowKeyParameters(boolean z, int i) {
        super(z);
        this.docLength = i;
    }

    public int getDocLength() {
        return this.docLength;
    }
}
