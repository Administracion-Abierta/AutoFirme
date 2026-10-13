package org.bouncycastle.crypto.params;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/ECGOST3410Parameters.class */
public class ECGOST3410Parameters extends ECNamedDomainParameters {
    private final ASN1ObjectIdentifier publicKeyParamSet;
    private final ASN1ObjectIdentifier digestParamSet;
    private final ASN1ObjectIdentifier encryptionParamSet;

    public ECGOST3410Parameters(ECDomainParameters eCDomainParameters, ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1ObjectIdentifier aSN1ObjectIdentifier2) {
        this(eCDomainParameters, aSN1ObjectIdentifier, aSN1ObjectIdentifier2, null);
    }

    public ECGOST3410Parameters(ECDomainParameters eCDomainParameters, ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1ObjectIdentifier aSN1ObjectIdentifier2, ASN1ObjectIdentifier aSN1ObjectIdentifier3) {
        super(aSN1ObjectIdentifier, eCDomainParameters);
        if ((eCDomainParameters instanceof ECNamedDomainParameters) && !aSN1ObjectIdentifier.equals((ASN1Primitive) ((ECNamedDomainParameters) eCDomainParameters).getName())) {
            throw new IllegalArgumentException("named parameters do not match publicKeyParamSet value");
        }
        this.publicKeyParamSet = aSN1ObjectIdentifier;
        this.digestParamSet = aSN1ObjectIdentifier2;
        this.encryptionParamSet = aSN1ObjectIdentifier3;
    }

    public ASN1ObjectIdentifier getPublicKeyParamSet() {
        return this.publicKeyParamSet;
    }

    public ASN1ObjectIdentifier getDigestParamSet() {
        return this.digestParamSet;
    }

    public ASN1ObjectIdentifier getEncryptionParamSet() {
        return this.encryptionParamSet;
    }
}
