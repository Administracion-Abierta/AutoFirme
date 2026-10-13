package org.spongycastle.jcajce;

import org.spongycastle.crypto.CharToByteConverter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/PBKDF1Key.class */
public class PBKDF1Key implements PBKDFKey {
    private final char[] password;
    private final CharToByteConverter converter;

    public PBKDF1Key(char[] password, CharToByteConverter converter) {
        this.password = new char[password.length];
        this.converter = converter;
        System.arraycopy(password, 0, this.password, 0, password.length);
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
