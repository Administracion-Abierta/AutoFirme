package org.spongycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.crypto.engines.GOST28147Engine;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/spec/GOST28147WrapParameterSpec.class */
public class GOST28147WrapParameterSpec implements AlgorithmParameterSpec {
    private byte[] ukm;
    private byte[] sBox;
    private static Map oidMappings = new HashMap();

    public GOST28147WrapParameterSpec(byte[] sBox) {
        this.ukm = null;
        this.sBox = null;
        this.sBox = new byte[sBox.length];
        System.arraycopy(sBox, 0, this.sBox, 0, sBox.length);
    }

    public GOST28147WrapParameterSpec(byte[] sBox, byte[] ukm) {
        this(sBox);
        this.ukm = new byte[ukm.length];
        System.arraycopy(ukm, 0, this.ukm, 0, ukm.length);
    }

    public GOST28147WrapParameterSpec(String sBoxName) {
        this.ukm = null;
        this.sBox = null;
        this.sBox = GOST28147Engine.getSBox(sBoxName);
    }

    public GOST28147WrapParameterSpec(String sBoxName, byte[] ukm) {
        this(sBoxName);
        this.ukm = new byte[ukm.length];
        System.arraycopy(ukm, 0, this.ukm, 0, ukm.length);
    }

    public GOST28147WrapParameterSpec(ASN1ObjectIdentifier sBoxName, byte[] ukm) {
        this(getName(sBoxName));
        this.ukm = Arrays.clone(ukm);
    }

    public byte[] getSBox() {
        return Arrays.clone(this.sBox);
    }

    public byte[] getUKM() {
        return Arrays.clone(this.ukm);
    }

    static {
        oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet, "E-A");
        oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_B_ParamSet, "E-B");
        oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_C_ParamSet, "E-C");
        oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_D_ParamSet, "E-D");
    }

    private static String getName(ASN1ObjectIdentifier sBoxOid) {
        String sBoxName = (String) oidMappings.get(sBoxOid);
        if (sBoxName == null) {
            throw new IllegalArgumentException("unknown OID: " + sBoxOid);
        }
        return sBoxName;
    }
}
