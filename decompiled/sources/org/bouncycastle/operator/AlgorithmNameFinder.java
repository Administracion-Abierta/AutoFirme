package org.bouncycastle.operator;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/AlgorithmNameFinder.class */
public interface AlgorithmNameFinder {
    boolean hasAlgorithmName(ASN1ObjectIdentifier aSN1ObjectIdentifier);

    String getAlgorithmName(ASN1ObjectIdentifier aSN1ObjectIdentifier);

    String getAlgorithmName(AlgorithmIdentifier algorithmIdentifier);
}
