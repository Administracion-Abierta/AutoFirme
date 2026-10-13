package org.spongycastle.jcajce;

import org.spongycastle.crypto.CharToByteConverter;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/PBKDF2Key.class */
public class PBKDF2Key implements PBKDFKey {
    private final char[] password;
    private final CharToByteConverter converter;

    public PBKDF2Key(char[] password, CharToByteConverter converter) {
        this.password = Arrays.clone(password);
        this.converter = converter;
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
