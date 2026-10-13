package org.spongycastle.util.test;

import org.spongycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/test/TestRandomData.class */
public class TestRandomData extends FixedSecureRandom {
    public TestRandomData(String encoding) {
        super(new FixedSecureRandom.Source[]{new FixedSecureRandom.Data(Hex.decode(encoding))});
    }

    public TestRandomData(byte[] encoding) {
        super(new FixedSecureRandom.Source[]{new FixedSecureRandom.Data(encoding)});
    }
}
