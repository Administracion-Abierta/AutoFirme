package org.spongycastle.util.test;

import java.math.BigInteger;
import org.spongycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/util/test/TestRandomBigInteger.class */
public class TestRandomBigInteger extends FixedSecureRandom {
    public TestRandomBigInteger(String encoding) {
        this(encoding, 10);
    }

    public TestRandomBigInteger(String encoding, int radix) {
        super(new FixedSecureRandom.Source[]{new FixedSecureRandom.BigInteger(BigIntegers.asUnsignedByteArray(new BigInteger(encoding, radix)))});
    }

    public TestRandomBigInteger(byte[] encoding) {
        super(new FixedSecureRandom.Source[]{new FixedSecureRandom.BigInteger(encoding)});
    }

    public TestRandomBigInteger(int bitLength, byte[] encoding) {
        super(new FixedSecureRandom.Source[]{new FixedSecureRandom.BigInteger(bitLength, encoding)});
    }
}
