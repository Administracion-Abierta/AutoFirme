package org.spongycastle.operator;

import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/SecretKeySizeProvider.class */
public interface SecretKeySizeProvider {
    int getKeySize(AlgorithmIdentifier algorithmIdentifier);

    int getKeySize(ASN1ObjectIdentifier aSN1ObjectIdentifier);
}
