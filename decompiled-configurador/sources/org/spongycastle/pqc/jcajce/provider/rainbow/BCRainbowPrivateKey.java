package org.spongycastle.pqc.jcajce.provider.rainbow;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.Arrays;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.pqc.asn1.PQCObjectIdentifiers;
import org.spongycastle.pqc.asn1.RainbowPrivateKey;
import org.spongycastle.pqc.crypto.rainbow.Layer;
import org.spongycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.spongycastle.pqc.crypto.rainbow.util.RainbowUtil;
import org.spongycastle.pqc.jcajce.spec.RainbowPrivateKeySpec;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/rainbow/BCRainbowPrivateKey.class */
public class BCRainbowPrivateKey implements PrivateKey {
    private static final long serialVersionUID = 1;
    private short[][] A1inv;
    private short[] b1;
    private short[][] A2inv;
    private short[] b2;
    private Layer[] layers;
    private int[] vi;

    public BCRainbowPrivateKey(short[][] A1inv, short[] b1, short[][] A2inv, short[] b2, int[] vi, Layer[] layers) {
        this.A1inv = A1inv;
        this.b1 = b1;
        this.A2inv = A2inv;
        this.b2 = b2;
        this.vi = vi;
        this.layers = layers;
    }

    public BCRainbowPrivateKey(RainbowPrivateKeySpec keySpec) {
        this(keySpec.getInvA1(), keySpec.getB1(), keySpec.getInvA2(), keySpec.getB2(), keySpec.getVi(), keySpec.getLayers());
    }

    public BCRainbowPrivateKey(RainbowPrivateKeyParameters params) {
        this(params.getInvA1(), params.getB1(), params.getInvA2(), params.getB2(), params.getVi(), params.getLayers());
    }

    public short[][] getInvA1() {
        return this.A1inv;
    }

    public short[] getB1() {
        return this.b1;
    }

    public short[] getB2() {
        return this.b2;
    }

    public short[][] getInvA2() {
        return this.A2inv;
    }

    public Layer[] getLayers() {
        return this.layers;
    }

    public int[] getVi() {
        return this.vi;
    }

    public boolean equals(Object other) {
        if (other == null || !(other instanceof BCRainbowPrivateKey)) {
            return false;
        }
        BCRainbowPrivateKey otherKey = (BCRainbowPrivateKey) other;
        boolean eq = 1 != 0 && RainbowUtil.equals(this.A1inv, otherKey.getInvA1());
        boolean eq2 = eq && RainbowUtil.equals(this.A2inv, otherKey.getInvA2());
        boolean eq3 = eq2 && RainbowUtil.equals(this.b1, otherKey.getB1());
        boolean eq4 = eq3 && RainbowUtil.equals(this.b2, otherKey.getB2());
        boolean eq5 = eq4 && Arrays.equals(this.vi, otherKey.getVi());
        if (this.layers.length != otherKey.getLayers().length) {
            return false;
        }
        for (int i = this.layers.length - 1; i >= 0; i--) {
            eq5 &= this.layers[i].equals(otherKey.getLayers()[i]);
        }
        return eq5;
    }

    public int hashCode() {
        int hash = this.layers.length;
        int hash2 = (((((((((hash * 37) + org.spongycastle.util.Arrays.hashCode(this.A1inv)) * 37) + org.spongycastle.util.Arrays.hashCode(this.b1)) * 37) + org.spongycastle.util.Arrays.hashCode(this.A2inv)) * 37) + org.spongycastle.util.Arrays.hashCode(this.b2)) * 37) + org.spongycastle.util.Arrays.hashCode(this.vi);
        for (int i = this.layers.length - 1; i >= 0; i--) {
            hash2 = (hash2 * 37) + this.layers[i].hashCode();
        }
        return hash2;
    }

    @Override // java.security.Key
    public final String getAlgorithm() {
        return "Rainbow";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        RainbowPrivateKey privateKey = new RainbowPrivateKey(this.A1inv, this.b1, this.A2inv, this.b2, this.vi, this.layers);
        try {
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.rainbow, DERNull.INSTANCE);
            PrivateKeyInfo pki = new PrivateKeyInfo(algorithmIdentifier, privateKey);
            try {
                byte[] encoded = pki.getEncoded();
                return encoded;
            } catch (IOException e) {
                return null;
            }
        } catch (IOException e2) {
            return null;
        }
    }

    @Override // java.security.Key
    public String getFormat() {
        return "PKCS#8";
    }
}
