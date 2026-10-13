package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.MultiBlockCipher;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/CBCModeCipher.class */
public interface CBCModeCipher extends MultiBlockCipher {
    BlockCipher getUnderlyingCipher();
}
