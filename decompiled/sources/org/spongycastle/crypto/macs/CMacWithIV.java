package org.spongycastle.crypto.macs;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/macs/CMacWithIV.class */
public class CMacWithIV extends CMac {
    public CMacWithIV(BlockCipher cipher) {
        super(cipher);
    }

    public CMacWithIV(BlockCipher cipher, int macSizeInBits) {
        super(cipher, macSizeInBits);
    }

    @Override // org.spongycastle.crypto.macs.CMac
    void validate(CipherParameters params) {
    }
}
