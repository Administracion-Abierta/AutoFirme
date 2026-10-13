package org.spongycastle.crypto;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/SignerWithRecovery.class */
public interface SignerWithRecovery extends Signer {
    boolean hasFullMessage();

    byte[] getRecoveredMessage();

    void updateWithRecoveredMessage(byte[] bArr) throws InvalidCipherTextException;
}
