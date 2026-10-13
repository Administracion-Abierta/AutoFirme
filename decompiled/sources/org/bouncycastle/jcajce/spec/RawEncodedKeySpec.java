package org.bouncycastle.jcajce.spec;

import java.security.spec.EncodedKeySpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/RawEncodedKeySpec.class */
public class RawEncodedKeySpec extends EncodedKeySpec {
    public RawEncodedKeySpec(byte[] bArr) {
        super(bArr);
    }

    @Override // java.security.spec.EncodedKeySpec
    public String getFormat() {
        return "RAW";
    }
}
