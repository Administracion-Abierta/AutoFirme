package org.bouncycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/AlphabetMapper.class */
public interface AlphabetMapper {
    int getRadix();

    byte[] convertToIndexes(char[] cArr);

    char[] convertToChars(byte[] bArr);
}
