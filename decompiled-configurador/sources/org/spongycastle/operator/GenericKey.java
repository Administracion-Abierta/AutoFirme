package org.spongycastle.operator;

import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/GenericKey.class */
public class GenericKey {
    private AlgorithmIdentifier algorithmIdentifier;
    private Object representation;

    public GenericKey(Object representation) {
        this.algorithmIdentifier = null;
        this.representation = representation;
    }

    public GenericKey(AlgorithmIdentifier algorithmIdentifier, byte[] representation) {
        this.algorithmIdentifier = algorithmIdentifier;
        this.representation = representation;
    }

    protected GenericKey(AlgorithmIdentifier algorithmIdentifier, Object representation) {
        this.algorithmIdentifier = algorithmIdentifier;
        this.representation = representation;
    }

    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.algorithmIdentifier;
    }

    public Object getRepresentation() {
        return this.representation;
    }
}
