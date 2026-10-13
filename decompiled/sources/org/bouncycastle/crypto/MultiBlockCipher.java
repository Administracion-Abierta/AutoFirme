package org.bouncycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/MultiBlockCipher.class */
public interface MultiBlockCipher extends BlockCipher {
    int getMultiBlockSize();

    int processBlocks(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws IllegalStateException, DataLengthException;
}
