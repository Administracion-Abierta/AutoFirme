package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.internal.asn1.rosstandart.RosstandartObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/GOST3410ParameterSpec.class */
public class GOST3410ParameterSpec implements AlgorithmParameterSpec {
    private final ASN1ObjectIdentifier publicKeyParamSet;
    private final ASN1ObjectIdentifier digestParamSet;
    private final ASN1ObjectIdentifier encryptionParamSet;

    public GOST3410ParameterSpec(String str) {
        this(getOid(str), getDigestOid(str), null);
    }

    public GOST3410ParameterSpec(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1ObjectIdentifier aSN1ObjectIdentifier2) {
        this(aSN1ObjectIdentifier, aSN1ObjectIdentifier2, null);
    }

    public GOST3410ParameterSpec(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1ObjectIdentifier aSN1ObjectIdentifier2, ASN1ObjectIdentifier aSN1ObjectIdentifier3) {
        this.publicKeyParamSet = aSN1ObjectIdentifier;
        this.digestParamSet = aSN1ObjectIdentifier2;
        this.encryptionParamSet = aSN1ObjectIdentifier3;
    }

    public String getPublicKeyParamSetName() {
        return ECGOST3410NamedCurves.getName(getPublicKeyParamSet());
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

    private static ASN1ObjectIdentifier getOid(String str) {
        return ECGOST3410NamedCurves.getOID(str);
    }

    private static ASN1ObjectIdentifier getDigestOid(String str) {
        if (str.indexOf("12-512") > 0) {
            return RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512;
        }
        return str.indexOf("12-256") > 0 ? RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256 : CryptoProObjectIdentifiers.gostR3411_94_CryptoProParamSet;
    }
}
