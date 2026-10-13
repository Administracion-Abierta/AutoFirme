package org.spongycastle.crypto.params;

import org.spongycastle.crypto.DerivationParameters;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/params/KDFDoublePipelineIterationParameters.class */
public final class KDFDoublePipelineIterationParameters implements DerivationParameters {
    private static final int UNUSED_R = 32;
    private final byte[] ki;
    private final boolean useCounter;
    private final int r;
    private final byte[] fixedInputData;

    private KDFDoublePipelineIterationParameters(byte[] ki, byte[] fixedInputData, int r, boolean useCounter) {
        if (ki == null) {
            throw new IllegalArgumentException("A KDF requires Ki (a seed) as input");
        }
        this.ki = Arrays.clone(ki);
        if (fixedInputData == null) {
            this.fixedInputData = new byte[0];
        } else {
            this.fixedInputData = Arrays.clone(fixedInputData);
        }
        if (r != 8 && r != 16 && r != 24 && r != 32) {
            throw new IllegalArgumentException("Length of counter should be 8, 16, 24 or 32");
        }
        this.r = r;
        this.useCounter = useCounter;
    }

    public static KDFDoublePipelineIterationParameters createWithCounter(byte[] ki, byte[] fixedInputData, int r) {
        return new KDFDoublePipelineIterationParameters(ki, fixedInputData, r, true);
    }

    public static KDFDoublePipelineIterationParameters createWithoutCounter(byte[] ki, byte[] fixedInputData) {
        return new KDFDoublePipelineIterationParameters(ki, fixedInputData, 32, false);
    }

    public byte[] getKI() {
        return this.ki;
    }

    public boolean useCounter() {
        return this.useCounter;
    }

    public int getR() {
        return this.r;
    }

    public byte[] getFixedInputData() {
        return Arrays.clone(this.fixedInputData);
    }
}
