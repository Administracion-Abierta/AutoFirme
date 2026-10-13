package org.bouncycastle.operator;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/SecretKeySizeProvider.class */
public interface SecretKeySizeProvider {
    int getKeySize(AlgorithmIdentifier algorithmIdentifier);

    int getKeySize(ASN1ObjectIdentifier aSN1ObjectIdentifier);
}
