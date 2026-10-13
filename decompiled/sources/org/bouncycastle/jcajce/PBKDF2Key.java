package org.bouncycastle.jcajce;

import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/PBKDF2Key.class */
public class PBKDF2Key implements PBKDFKey {
    private final char[] password;
    private final CharToByteConverter converter;

    public PBKDF2Key(char[] cArr, CharToByteConverter charToByteConverter) {
        this.password = Arrays.clone(cArr);
        this.converter = charToByteConverter;
    }

    public char[] getPassword() {
        return this.password;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "PBKDF2";
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
