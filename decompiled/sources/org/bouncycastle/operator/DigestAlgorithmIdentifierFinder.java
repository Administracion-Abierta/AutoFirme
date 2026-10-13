package org.bouncycastle.operator;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/DigestAlgorithmIdentifierFinder.class */
public interface DigestAlgorithmIdentifierFinder {
    AlgorithmIdentifier find(AlgorithmIdentifier algorithmIdentifier);

    AlgorithmIdentifier find(ASN1ObjectIdentifier aSN1ObjectIdentifier);

    AlgorithmIdentifier find(String str);
}
