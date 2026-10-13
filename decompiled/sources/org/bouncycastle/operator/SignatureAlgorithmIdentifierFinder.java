package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/SignatureAlgorithmIdentifierFinder.class */
public interface SignatureAlgorithmIdentifierFinder {
    AlgorithmIdentifier find(String str);
}
