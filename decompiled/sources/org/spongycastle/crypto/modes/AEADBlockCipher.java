package org.spongycastle.crypto.modes;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/modes/AEADBlockCipher.class */
public interface AEADBlockCipher {
    void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException;

    String getAlgorithmName();

    BlockCipher getUnderlyingCipher();

    void processAADByte(byte b);

    void processAADBytes(byte[] bArr, int i, int i2);

    int processByte(byte b, byte[] bArr, int i) throws DataLengthException;

    int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException;

    int doFinal(byte[] bArr, int i) throws IllegalStateException, InvalidCipherTextException;

    byte[] getMac();

    int getUpdateOutputSize(int i);

    int getOutputSize(int i);

    void reset();
}
