package org.spongycastle.crypto.params;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/TweakableBlockCipherParameters.class */
public class TweakableBlockCipherParameters implements CipherParameters {
    private final byte[] tweak;
    private final KeyParameter key;

    public TweakableBlockCipherParameters(KeyParameter key, byte[] tweak) {
        this.key = key;
        this.tweak = Arrays.clone(tweak);
    }

    public KeyParameter getKey() {
        return this.key;
    }

    public byte[] getTweak() {
        return this.tweak;
    }
}
