package org.spongycastle.pqc.jcajce.spec;

import java.security.spec.KeySpec;
import org.spongycastle.pqc.crypto.rainbow.Layer;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/spec/RainbowPrivateKeySpec.class */
public class RainbowPrivateKeySpec implements KeySpec {
    private short[][] A1inv;
    private short[] b1;
    private short[][] A2inv;
    private short[] b2;
    private int[] vi;
    private Layer[] layers;

    public RainbowPrivateKeySpec(short[][] A1inv, short[] b1, short[][] A2inv, short[] b2, int[] vi, Layer[] layers) {
        this.A1inv = A1inv;
        this.b1 = b1;
        this.A2inv = A2inv;
        this.b2 = b2;
        this.vi = vi;
        this.layers = layers;
    }

    public short[] getB1() {
        return this.b1;
    }

    public short[][] getInvA1() {
        return this.A1inv;
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
}
