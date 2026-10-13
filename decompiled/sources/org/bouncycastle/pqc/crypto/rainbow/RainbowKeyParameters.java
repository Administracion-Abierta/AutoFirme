package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/rainbow/RainbowKeyParameters.class */
public class RainbowKeyParameters extends AsymmetricKeyParameter {
    private final RainbowParameters params;
    private final int docLength;

    public RainbowKeyParameters(boolean z, RainbowParameters rainbowParameters) {
        super(z);
        this.params = rainbowParameters;
        this.docLength = rainbowParameters.getM();
    }

    public RainbowParameters getParameters() {
        return this.params;
    }

    public int getDocLength() {
        return this.docLength;
    }
}
