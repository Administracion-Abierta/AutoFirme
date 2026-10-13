package org.spongycastle.operator;

import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/AsymmetricKeyWrapper.class */
public abstract class AsymmetricKeyWrapper implements KeyWrapper {
    private AlgorithmIdentifier algorithmId;

    protected AsymmetricKeyWrapper(AlgorithmIdentifier algorithmId) {
        this.algorithmId = algorithmId;
    }

    @Override // org.spongycastle.operator.KeyWrapper
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.algorithmId;
    }
}
