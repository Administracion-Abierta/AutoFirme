package org.bouncycastle.util.test;

import java.math.BigInteger;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/test/TestRandomBigInteger.class */
public class TestRandomBigInteger extends FixedSecureRandom {
    public TestRandomBigInteger(String str) {
        this(str, 10);
    }

    public TestRandomBigInteger(String str, int i) {
        super(new FixedSecureRandom.Source[]{new FixedSecureRandom.BigInteger(BigIntegers.asUnsignedByteArray(new BigInteger(str, i)))});
    }

    public TestRandomBigInteger(byte[] bArr) {
        super(new FixedSecureRandom.Source[]{new FixedSecureRandom.BigInteger(bArr)});
    }

    public TestRandomBigInteger(int i, byte[] bArr) {
        super(new FixedSecureRandom.Source[]{new FixedSecureRandom.BigInteger(i, bArr)});
    }
}
