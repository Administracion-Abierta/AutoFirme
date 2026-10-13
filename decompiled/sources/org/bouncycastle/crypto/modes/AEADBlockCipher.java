package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/AEADBlockCipher.class */
public interface AEADBlockCipher extends AEADCipher {
    BlockCipher getUnderlyingCipher();
}
