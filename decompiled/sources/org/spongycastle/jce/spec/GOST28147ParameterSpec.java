package org.spongycastle.jce.spec;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/spec/GOST28147ParameterSpec.class */
public class GOST28147ParameterSpec extends org.spongycastle.jcajce.spec.GOST28147ParameterSpec {
    public GOST28147ParameterSpec(byte[] sBox) {
        super(sBox);
    }

    public GOST28147ParameterSpec(byte[] sBox, byte[] iv) {
        super(sBox, iv);
    }

    public GOST28147ParameterSpec(String sBoxName) {
        super(sBoxName);
    }

    public GOST28147ParameterSpec(String sBoxName, byte[] iv) {
        super(sBoxName, iv);
    }
}
