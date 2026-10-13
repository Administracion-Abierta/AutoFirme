package org.spongycastle.jcajce;

import org.spongycastle.crypto.PBEParametersGenerator;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/PKCS12Key.class */
public class PKCS12Key implements PBKDFKey {
    private final char[] password;
    private final boolean useWrongZeroLengthConversion;

    public PKCS12Key(char[] password) {
        this(password, false);
    }

    public PKCS12Key(char[] password, boolean useWrongZeroLengthConversion) {
        password = password == null ? new char[0] : password;
        this.password = new char[password.length];
        this.useWrongZeroLengthConversion = useWrongZeroLengthConversion;
        System.arraycopy(password, 0, this.password, 0, password.length);
    }

    public char[] getPassword() {
        return this.password;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "PKCS12";
    }

    @Override // java.security.Key
    public String getFormat() {
        return "PKCS12";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        if (this.useWrongZeroLengthConversion && this.password.length == 0) {
            return new byte[2];
        }
        return PBEParametersGenerator.PKCS12PasswordToBytes(this.password);
    }
}
