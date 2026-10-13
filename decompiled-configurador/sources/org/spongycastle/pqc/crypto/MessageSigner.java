package org.spongycastle.pqc.crypto;

import org.spongycastle.crypto.CipherParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/MessageSigner.class */
public interface MessageSigner {
    void init(boolean z, CipherParameters cipherParameters);

    byte[] generateSignature(byte[] bArr);

    boolean verifySignature(byte[] bArr, byte[] bArr2);
}
