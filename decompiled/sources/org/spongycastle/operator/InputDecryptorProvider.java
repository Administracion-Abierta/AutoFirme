package org.spongycastle.operator;

import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/operator/InputDecryptorProvider.class */
public interface InputDecryptorProvider {
    InputDecryptor get(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException;
}
