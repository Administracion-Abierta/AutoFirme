package org.bouncycastle.jce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST3410NamedParameters;
import org.bouncycastle.asn1.cryptopro.GOST3410ParamSetParameters;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.jce.interfaces.GOST3410Params;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/spec/GOST3410ParameterSpec.class */
public class GOST3410ParameterSpec implements AlgorithmParameterSpec, GOST3410Params {
    private GOST3410PublicKeyParameterSetSpec keyParameters;
    private String keyParamSetOID;
    private String digestParamSetOID;
    private String encryptionParamSetOID;

    public GOST3410ParameterSpec(String str, String str2, String str3) {
        GOST3410ParamSetParameters byOID = null;
        try {
            byOID = GOST3410NamedParameters.getByOID(new ASN1ObjectIdentifier(str));
        } catch (IllegalArgumentException e) {
            ASN1ObjectIdentifier oid = GOST3410NamedParameters.getOID(str);
            if (oid != null) {
                str = oid.getId();
                byOID = GOST3410NamedParameters.getByOID(oid);
            }
        }
        if (byOID == null) {
            throw new IllegalArgumentException("no key parameter set for passed in name/OID.");
        }
        this.keyParameters = new GOST3410PublicKeyParameterSetSpec(byOID.getP(), byOID.getQ(), byOID.getA());
        this.keyParamSetOID = str;
        this.digestParamSetOID = str2;
        this.encryptionParamSetOID = str3;
    }

    public GOST3410ParameterSpec(String str, String str2) {
        this(str, str2, null);
    }

    public GOST3410ParameterSpec(String str) {
        this(str, CryptoProObjectIdentifiers.gostR3411_94_CryptoProParamSet.getId(), null);
    }

    public GOST3410ParameterSpec(GOST3410PublicKeyParameterSetSpec gOST3410PublicKeyParameterSetSpec) {
        this.keyParameters = gOST3410PublicKeyParameterSetSpec;
        this.digestParamSetOID = CryptoProObjectIdentifiers.gostR3411_94_CryptoProParamSet.getId();
        this.encryptionParamSetOID = null;
    }

    @Override // org.bouncycastle.jce.interfaces.GOST3410Params
    public String getPublicKeyParamSetOID() {
        return this.keyParamSetOID;
    }

    @Override // org.bouncycastle.jce.interfaces.GOST3410Params
    public GOST3410PublicKeyParameterSetSpec getPublicKeyParameters() {
        return this.keyParameters;
    }

    @Override // org.bouncycastle.jce.interfaces.GOST3410Params
    public String getDigestParamSetOID() {
        return this.digestParamSetOID;
    }

    @Override // org.bouncycastle.jce.interfaces.GOST3410Params
    public String getEncryptionParamSetOID() {
        return this.encryptionParamSetOID;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof GOST3410ParameterSpec)) {
            return false;
        }
        GOST3410ParameterSpec gOST3410ParameterSpec = (GOST3410ParameterSpec) obj;
        return this.keyParameters.equals(gOST3410ParameterSpec.keyParameters) && (this.digestParamSetOID == gOST3410ParameterSpec.digestParamSetOID || (this.digestParamSetOID != null && this.digestParamSetOID.equals(gOST3410ParameterSpec.digestParamSetOID))) && (this.encryptionParamSetOID == gOST3410ParameterSpec.encryptionParamSetOID || (this.encryptionParamSetOID != null && this.encryptionParamSetOID.equals(gOST3410ParameterSpec.encryptionParamSetOID)));
    }

    public int hashCode() {
        return (this.keyParameters.hashCode() ^ this.digestParamSetOID.hashCode()) ^ (this.encryptionParamSetOID != null ? this.encryptionParamSetOID.hashCode() : 0);
    }

    public static GOST3410ParameterSpec fromPublicKeyAlg(GOST3410PublicKeyAlgParameters gOST3410PublicKeyAlgParameters) {
        if (gOST3410PublicKeyAlgParameters.getEncryptionParamSet() != null) {
            return new GOST3410ParameterSpec(gOST3410PublicKeyAlgParameters.getPublicKeyParamSet().getId(), gOST3410PublicKeyAlgParameters.getDigestParamSet().getId(), gOST3410PublicKeyAlgParameters.getEncryptionParamSet().getId());
        }
        return gOST3410PublicKeyAlgParameters.getDigestParamSet() != null ? new GOST3410ParameterSpec(gOST3410PublicKeyAlgParameters.getPublicKeyParamSet().getId(), gOST3410PublicKeyAlgParameters.getDigestParamSet().getId()) : new GOST3410ParameterSpec(gOST3410PublicKeyAlgParameters.getPublicKeyParamSet().getId(), null);
    }
}
