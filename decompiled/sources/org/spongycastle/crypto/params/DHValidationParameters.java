package org.spongycastle.crypto.params;

import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/DHValidationParameters.class */
public class DHValidationParameters {
    private byte[] seed;
    private int counter;

    public DHValidationParameters(byte[] seed, int counter) {
        this.seed = seed;
        this.counter = counter;
    }

    public int getCounter() {
        return this.counter;
    }

    public byte[] getSeed() {
        return this.seed;
    }

    public boolean equals(Object o) {
        if (!(o instanceof DHValidationParameters)) {
            return false;
        }
        DHValidationParameters other = (DHValidationParameters) o;
        if (other.counter != this.counter) {
            return false;
        }
        return Arrays.areEqual(this.seed, other.seed);
    }

    public int hashCode() {
        return this.counter ^ Arrays.hashCode(this.seed);
    }
}
