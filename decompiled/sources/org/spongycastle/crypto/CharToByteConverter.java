package org.spongycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/CharToByteConverter.class */
public interface CharToByteConverter {
    String getType();

    byte[] convert(char[] cArr);
}
