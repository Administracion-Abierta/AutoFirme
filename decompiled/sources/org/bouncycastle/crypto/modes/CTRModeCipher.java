package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.MultiBlockCipher;
import org.bouncycastle.crypto.SkippingStreamCipher;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/CTRModeCipher.class */
public interface CTRModeCipher extends MultiBlockCipher, SkippingStreamCipher {
    BlockCipher getUnderlyingCipher();
}
