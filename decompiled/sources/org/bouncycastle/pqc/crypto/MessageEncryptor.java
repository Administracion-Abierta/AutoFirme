package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/MessageEncryptor.class */
public interface MessageEncryptor {
    void init(boolean z, CipherParameters cipherParameters);

    byte[] messageEncrypt(byte[] bArr);

    byte[] messageDecrypt(byte[] bArr) throws InvalidCipherTextException;
}
