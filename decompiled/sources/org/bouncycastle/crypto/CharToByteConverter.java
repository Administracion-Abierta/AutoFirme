package org.bouncycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/CharToByteConverter.class */
public interface CharToByteConverter {
    String getType();

    byte[] convert(char[] cArr);
}
