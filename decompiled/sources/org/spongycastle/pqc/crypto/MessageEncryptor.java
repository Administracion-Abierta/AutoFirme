package org.spongycastle.pqc.crypto;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.InvalidCipherTextException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/MessageEncryptor.class */
public interface MessageEncryptor {
    void init(boolean z, CipherParameters cipherParameters);

    byte[] messageEncrypt(byte[] bArr);

    byte[] messageDecrypt(byte[] bArr) throws InvalidCipherTextException;
}
