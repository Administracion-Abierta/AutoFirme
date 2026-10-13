package org.bouncycastle.jcajce;

import org.bouncycastle.crypto.CharToByteConverter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/PBKDF1Key.class */
public class PBKDF1Key implements PBKDFKey {
    private final char[] password;
    private final CharToByteConverter converter;

    public PBKDF1Key(char[] cArr, CharToByteConverter charToByteConverter) {
        this.password = new char[cArr.length];
        this.converter = charToByteConverter;
        System.arraycopy(cArr, 0, this.password, 0, cArr.length);
    }

    public char[] getPassword() {
        return this.password;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "PBKDF1";
    }

    @Override // java.security.Key
    public String getFormat() {
        return this.converter.getType();
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        return this.converter.convert(this.password);
    }
}
