package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.CipherParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/MessageSigner.class */
public interface MessageSigner {
    void init(boolean z, CipherParameters cipherParameters);

    byte[] generateSignature(byte[] bArr);

    boolean verifySignature(byte[] bArr, byte[] bArr2);
}
