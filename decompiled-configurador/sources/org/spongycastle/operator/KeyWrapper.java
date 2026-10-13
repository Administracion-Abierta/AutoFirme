package org.spongycastle.operator;

import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/KeyWrapper.class */
public interface KeyWrapper {
    AlgorithmIdentifier getAlgorithmIdentifier();

    byte[] generateWrappedKey(GenericKey genericKey) throws OperatorException;
}
