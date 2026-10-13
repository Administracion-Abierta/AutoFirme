package org.bouncycastle.util.test;

import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/test/TestRandomData.class */
public class TestRandomData extends FixedSecureRandom {
    public TestRandomData(String str) {
        super(new FixedSecureRandom.Source[]{new FixedSecureRandom.Data(Hex.decode(str))});
    }

    public TestRandomData(byte[] bArr) {
        super(new FixedSecureRandom.Source[]{new FixedSecureRandom.Data(bArr)});
    }
}
