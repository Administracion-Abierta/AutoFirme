package org.bouncycastle.util.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Provider;
import java.security.SecureRandom;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/test/FixedSecureRandom.class */
public class FixedSecureRandom extends SecureRandom {
    private static java.math.BigInteger REGULAR = new java.math.BigInteger("01020304ffffffff0506070811111111", 16);
    private static java.math.BigInteger ANDROID = new java.math.BigInteger("1111111105060708ffffffff01020304", 16);
    private static java.math.BigInteger CLASSPATH = new java.math.BigInteger("3020104ffffffff05060708111111", 16);
    private static final boolean isAndroidStyle;
    private static final boolean isClasspathStyle;
    private static final boolean isRegularStyle;
    private byte[] _data;
    private int _index;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/test/FixedSecureRandom$BigInteger.class */
    public static class BigInteger extends Source {
        public BigInteger(byte[] bArr) {
            super(bArr);
        }

        public BigInteger(int i, byte[] bArr) {
            super(FixedSecureRandom.expandToBitLength(i, bArr));
        }

        public BigInteger(String str) {
            this(Hex.decode(str));
        }

        public BigInteger(int i, String str) {
            super(FixedSecureRandom.expandToBitLength(i, Hex.decode(str)));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/test/FixedSecureRandom$Data.class */
    public static class Data extends Source {
        public Data(byte[] bArr) {
            super(bArr);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/test/FixedSecureRandom$DummyProvider.class */
    private static class DummyProvider extends Provider {
        DummyProvider() {
            super("BCFIPS_FIXED_RNG", 1.0d, "BCFIPS Fixed Secure Random Provider");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/test/FixedSecureRandom$RandomChecker.class */
    private static class RandomChecker extends SecureRandom {
        byte[] data;
        int index;

        RandomChecker() {
            super(null, new DummyProvider());
            this.data = Hex.decode("01020304ffffffff0506070811111111");
            this.index = 0;
        }

        @Override // java.security.SecureRandom, java.util.Random
        public void nextBytes(byte[] bArr) {
            System.arraycopy(this.data, this.index, bArr, 0, bArr.length);
            this.index += bArr.length;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/test/FixedSecureRandom$Source.class */
    public static class Source {
        byte[] data;

        Source(byte[] bArr) {
            this.data = bArr;
        }
    }

    public FixedSecureRandom(byte[] bArr) {
        this(new Source[]{new Data(bArr)});
    }

    public FixedSecureRandom(byte[][] bArr) {
        this(buildDataArray(bArr));
    }

    private static Data[] buildDataArray(byte[][] bArr) {
        Data[] dataArr = new Data[bArr.length];
        for (int i = 0; i != bArr.length; i++) {
            dataArr[i] = new Data(bArr[i]);
        }
        return dataArr;
    }

    public FixedSecureRandom(Source[] sourceArr) {
        super(null, new DummyProvider());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (!isRegularStyle) {
            if (!isAndroidStyle) {
                throw new IllegalStateException("Unrecognized BigInteger implementation");
            }
            for (int i = 0; i != sourceArr.length; i++) {
                try {
                    if (sourceArr[i] instanceof BigInteger) {
                        byte[] bArr = sourceArr[i].data;
                        int length = bArr.length - (bArr.length % 4);
                        for (int i2 = 0; i2 < length; i2 += 4) {
                            byteArrayOutputStream.write(bArr, bArr.length - (i2 + 4), 4);
                        }
                        if (bArr.length - length != 0) {
                            for (int i3 = 0; i3 != 4 - (bArr.length - length); i3++) {
                                byteArrayOutputStream.write(0);
                            }
                        }
                        for (int i4 = 0; i4 != bArr.length - length; i4++) {
                            byteArrayOutputStream.write(bArr[length + i4]);
                        }
                    } else {
                        byteArrayOutputStream.write(sourceArr[i].data);
                    }
                } catch (IOException e) {
                    throw new IllegalArgumentException("can't save value source.");
                }
            }
        } else if (isClasspathStyle) {
            for (int i5 = 0; i5 != sourceArr.length; i5++) {
                try {
                    if (sourceArr[i5] instanceof BigInteger) {
                        byte[] bArr2 = sourceArr[i5].data;
                        int length2 = bArr2.length - (bArr2.length % 4);
                        for (int length3 = (bArr2.length - length2) - 1; length3 >= 0; length3--) {
                            byteArrayOutputStream.write(bArr2[length3]);
                        }
                        for (int length4 = bArr2.length - length2; length4 < bArr2.length; length4 += 4) {
                            byteArrayOutputStream.write(bArr2, length4, 4);
                        }
                    } else {
                        byteArrayOutputStream.write(sourceArr[i5].data);
                    }
                } catch (IOException e2) {
                    throw new IllegalArgumentException("can't save value source.");
                }
            }
        } else {
            for (int i6 = 0; i6 != sourceArr.length; i6++) {
                try {
                    byteArrayOutputStream.write(sourceArr[i6].data);
                } catch (IOException e3) {
                    throw new IllegalArgumentException("can't save value source.");
                }
            }
        }
        this._data = byteArrayOutputStream.toByteArray();
    }

    @Override // java.security.SecureRandom, java.util.Random
    public void nextBytes(byte[] bArr) {
        System.arraycopy(this._data, this._index, bArr, 0, bArr.length);
        this._index += bArr.length;
    }

    @Override // java.security.SecureRandom
    public byte[] generateSeed(int i) {
        byte[] bArr = new byte[i];
        nextBytes(bArr);
        return bArr;
    }

    @Override // java.util.Random
    public int nextInt() {
        return 0 | (nextValue() << 24) | (nextValue() << 16) | (nextValue() << 8) | nextValue();
    }

    @Override // java.util.Random
    public long nextLong() {
        return 0 | (((long) nextValue()) << 56) | (((long) nextValue()) << 48) | (((long) nextValue()) << 40) | (((long) nextValue()) << 32) | (((long) nextValue()) << 24) | (((long) nextValue()) << 16) | (((long) nextValue()) << 8) | ((long) nextValue());
    }

    public boolean isExhausted() {
        return this._index == this._data.length;
    }

    private int nextValue() {
        byte[] bArr = this._data;
        int i = this._index;
        this._index = i + 1;
        return bArr[i] & 255;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] expandToBitLength(int i, byte[] bArr) {
        if ((i + 7) / 8 <= bArr.length) {
            if (isAndroidStyle && i < bArr.length * 8 && i % 8 != 0) {
                Pack.intToBigEndian(Pack.bigEndianToInt(bArr, 0) << (8 - (i % 8)), bArr, 0);
            }
            return bArr;
        }
        byte[] bArr2 = new byte[(i + 7) / 8];
        System.arraycopy(bArr, 0, bArr2, bArr2.length - bArr.length, bArr.length);
        if (isAndroidStyle && i % 8 != 0) {
            Pack.intToBigEndian(Pack.bigEndianToInt(bArr2, 0) << (8 - (i % 8)), bArr2, 0);
        }
        return bArr2;
    }

    static {
        java.math.BigInteger bigInteger = new java.math.BigInteger(128, new RandomChecker());
        java.math.BigInteger bigInteger2 = new java.math.BigInteger(120, new RandomChecker());
        isAndroidStyle = bigInteger.equals(ANDROID);
        isRegularStyle = bigInteger.equals(REGULAR);
        isClasspathStyle = bigInteger2.equals(CLASSPATH);
    }
}
