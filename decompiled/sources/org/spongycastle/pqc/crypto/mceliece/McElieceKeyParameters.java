package org.spongycastle.pqc.crypto.mceliece;

import org.spongycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/mceliece/McElieceKeyParameters.class */
public class McElieceKeyParameters extends AsymmetricKeyParameter {
    private McElieceParameters params;

    public McElieceKeyParameters(boolean isPrivate, McElieceParameters params) {
        super(isPrivate);
        this.params = params;
    }

    public McElieceParameters getParameters() {
        return this.params;
    }
}
