package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/MacAlgorithmIdentifierFinder.class */
public interface MacAlgorithmIdentifierFinder {
    AlgorithmIdentifier find(String str);
}
