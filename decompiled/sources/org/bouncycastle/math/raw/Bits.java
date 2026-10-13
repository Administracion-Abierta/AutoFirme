package org.bouncycastle.math.raw;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/raw/Bits.class */
public abstract class Bits {
    public static int bitPermuteStep(int i, int i2, int i3) {
        int i4 = (i ^ (i >>> i3)) & i2;
        return (i4 ^ (i4 << i3)) ^ i;
    }

    public static long bitPermuteStep(long j, long j2, int i) {
        long j3 = (j ^ (j >>> i)) & j2;
        return (j3 ^ (j3 << i)) ^ j;
    }

    public static int bitPermuteStepSimple(int i, int i2, int i3) {
        return ((i & i2) << i3) | ((i >>> i3) & i2);
    }

    public static long bitPermuteStepSimple(long j, long j2, int i) {
        return ((j & j2) << i) | ((j >>> i) & j2);
    }
}
