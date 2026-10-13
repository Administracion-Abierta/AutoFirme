package org.bouncycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/SignerWithRecovery.class */
public interface SignerWithRecovery extends Signer {
    boolean hasFullMessage();

    byte[] getRecoveredMessage();

    void updateWithRecoveredMessage(byte[] bArr) throws InvalidCipherTextException;
}
