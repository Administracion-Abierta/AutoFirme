package org.spongycastle.operator;

import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/operator/SignatureAlgorithmIdentifierFinder.class */
public interface SignatureAlgorithmIdentifierFinder {
    AlgorithmIdentifier find(String str);
}
