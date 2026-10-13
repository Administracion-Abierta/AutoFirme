package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/KemEncapsulationLengthProvider.class */
public interface KemEncapsulationLengthProvider {
    int getEncapsulationLength(AlgorithmIdentifier algorithmIdentifier);
}
