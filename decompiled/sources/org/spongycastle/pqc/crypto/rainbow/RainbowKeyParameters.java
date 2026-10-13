package org.spongycastle.pqc.crypto.rainbow;

import org.spongycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/rainbow/RainbowKeyParameters.class */
public class RainbowKeyParameters extends AsymmetricKeyParameter {
    private int docLength;

    public RainbowKeyParameters(boolean isPrivate, int docLength) {
        super(isPrivate);
        this.docLength = docLength;
    }

    public int getDocLength() {
        return this.docLength;
    }
}
