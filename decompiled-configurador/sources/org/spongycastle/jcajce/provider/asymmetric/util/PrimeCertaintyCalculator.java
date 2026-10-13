package org.spongycastle.jcajce.provider.asymmetric.util;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/util/PrimeCertaintyCalculator.class */
public class PrimeCertaintyCalculator {
    private PrimeCertaintyCalculator() {
    }

    public static int getDefaultCertainty(int keySizeInBits) {
        if (keySizeInBits <= 1024) {
            return 80;
        }
        return 96 + (16 * ((keySizeInBits - 1) / 1024));
    }
}
