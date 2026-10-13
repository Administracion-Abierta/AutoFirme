package org.spongycastle.operator;

import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/operator/KeyUnwrapper.class */
public interface KeyUnwrapper {
    AlgorithmIdentifier getAlgorithmIdentifier();

    GenericKey generateUnwrappedKey(AlgorithmIdentifier algorithmIdentifier, byte[] bArr) throws OperatorException;
}
