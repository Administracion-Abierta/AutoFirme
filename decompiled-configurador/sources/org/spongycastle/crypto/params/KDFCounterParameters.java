package org.spongycastle.crypto.params;

import org.spongycastle.crypto.DerivationParameters;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/params/KDFCounterParameters.class */
public final class KDFCounterParameters implements DerivationParameters {
    private byte[] ki;
    private byte[] fixedInputDataCounterPrefix;
    private byte[] fixedInputDataCounterSuffix;
    private int r;

    public KDFCounterParameters(byte[] ki, byte[] fixedInputDataCounterSuffix, int r) {
        this(ki, null, fixedInputDataCounterSuffix, r);
    }

    public KDFCounterParameters(byte[] ki, byte[] fixedInputDataCounterPrefix, byte[] fixedInputDataCounterSuffix, int r) {
        if (ki == null) {
            throw new IllegalArgumentException("A KDF requires Ki (a seed) as input");
        }
        this.ki = Arrays.clone(ki);
        if (fixedInputDataCounterPrefix == null) {
            this.fixedInputDataCounterPrefix = new byte[0];
        } else {
            this.fixedInputDataCounterPrefix = Arrays.clone(fixedInputDataCounterPrefix);
        }
        if (fixedInputDataCounterSuffix == null) {
            this.fixedInputDataCounterSuffix = new byte[0];
        } else {
            this.fixedInputDataCounterSuffix = Arrays.clone(fixedInputDataCounterSuffix);
        }
        if (r != 8 && r != 16 && r != 24 && r != 32) {
            throw new IllegalArgumentException("Length of counter should be 8, 16, 24 or 32");
        }
        this.r = r;
    }

    public byte[] getKI() {
        return this.ki;
    }

    public byte[] getFixedInputData() {
        return Arrays.clone(this.fixedInputDataCounterSuffix);
    }

    public byte[] getFixedInputDataCounterPrefix() {
        return Arrays.clone(this.fixedInputDataCounterPrefix);
    }

    public byte[] getFixedInputDataCounterSuffix() {
        return Arrays.clone(this.fixedInputDataCounterSuffix);
    }

    public int getR() {
        return this.r;
    }
}
