package org.bouncycastle.pqc.crypto.xmss;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/XMSSUtil.class */
public class XMSSUtil {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/XMSSUtil$CheckingStream.class */
    private static class CheckingStream extends ObjectInputStream {
        private static final Set components = new HashSet();
        private final Class mainClass;
        private boolean found;

        CheckingStream(Class cls, InputStream inputStream) throws IOException {
            super(inputStream);
            this.found = false;
            this.mainClass = cls;
        }

        @Override // java.io.ObjectInputStream
        protected Class<?> resolveClass(ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
            if (this.found) {
                if (!components.contains(objectStreamClass.getName())) {
                    throw new InvalidClassException("unexpected class: ", objectStreamClass.getName());
                }
            } else {
                if (!objectStreamClass.getName().equals(this.mainClass.getName())) {
                    throw new InvalidClassException("unexpected class: ", objectStreamClass.getName());
                }
                this.found = true;
            }
            return super.resolveClass(objectStreamClass);
        }

        static {
            components.add("java.util.TreeMap");
            components.add("java.lang.Integer");
            components.add("java.lang.Number");
            components.add("org.bouncycastle.pqc.crypto.xmss.BDS");
            components.add("java.util.ArrayList");
            components.add("org.bouncycastle.pqc.crypto.xmss.XMSSNode");
            components.add("[B");
            components.add("java.util.LinkedList");
            components.add("java.util.Stack");
            components.add("java.util.Vector");
            components.add("[Ljava.lang.Object;");
            components.add("org.bouncycastle.pqc.crypto.xmss.BDSTreeHash");
        }
    }

    public static int log2(int i) {
        int i2 = 0;
        while (true) {
            int i3 = i >> 1;
            i = i3;
            if (i3 == 0) {
                return i2;
            }
            i2++;
        }
    }

    public static byte[] toBytesBigEndian(long j, int i) {
        byte[] bArr = new byte[i];
        for (int i2 = i - 1; i2 >= 0; i2--) {
            bArr[i2] = (byte) j;
            j >>>= 8;
        }
        return bArr;
    }

    public static void longToBigEndian(long j, byte[] bArr, int i) {
        if (bArr == null) {
            throw new NullPointerException("in == null");
        }
        if (bArr.length - i < 8) {
            throw new IllegalArgumentException("not enough space in array");
        }
        bArr[i] = (byte) ((j >> 56) & 255);
        bArr[i + 1] = (byte) ((j >> 48) & 255);
        bArr[i + 2] = (byte) ((j >> 40) & 255);
        bArr[i + 3] = (byte) ((j >> 32) & 255);
        bArr[i + 4] = (byte) ((j >> 24) & 255);
        bArr[i + 5] = (byte) ((j >> 16) & 255);
        bArr[i + 6] = (byte) ((j >> 8) & 255);
        bArr[i + 7] = (byte) (j & 255);
    }

    public static long bytesToXBigEndian(byte[] bArr, int i, int i2) {
        if (bArr == null) {
            throw new NullPointerException("in == null");
        }
        long j = 0;
        for (int i3 = i; i3 < i + i2; i3++) {
            j = (j << 8) | ((long) (bArr[i3] & 255));
        }
        return j;
    }

    public static byte[] cloneArray(byte[] bArr) {
        if (bArr == null) {
            throw new NullPointerException("in == null");
        }
        byte[] bArr2 = new byte[bArr.length];
        System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
        return bArr2;
    }

    public static byte[][] cloneArray(byte[][] bArr) {
        if (hasNullPointer(bArr)) {
            throw new NullPointerException("in has null pointers");
        }
        byte[][] bArr2 = new byte[bArr.length][];
        for (int i = 0; i < bArr.length; i++) {
            bArr2[i] = new byte[bArr[i].length];
            System.arraycopy(bArr[i], 0, bArr2[i], 0, bArr[i].length);
        }
        return bArr2;
    }

    public static boolean areEqual(byte[][] bArr, byte[][] bArr2) {
        if (hasNullPointer(bArr) || hasNullPointer(bArr2)) {
            throw new NullPointerException("a or b == null");
        }
        for (int i = 0; i < bArr.length; i++) {
            if (!Arrays.areEqual(bArr[i], bArr2[i])) {
                return false;
            }
        }
        return true;
    }

    public static void dumpByteArray(byte[][] bArr) {
        if (hasNullPointer(bArr)) {
            throw new NullPointerException("x has null pointers");
        }
        for (byte[] bArr2 : bArr) {
            System.out.println(Hex.toHexString(bArr2));
        }
    }

    public static boolean hasNullPointer(byte[][] bArr) {
        if (bArr == null) {
            return true;
        }
        for (byte[] bArr2 : bArr) {
            if (bArr2 == null) {
                return true;
            }
        }
        return false;
    }

    public static void copyBytesAtOffset(byte[] bArr, byte[] bArr2, int i) {
        if (bArr == null) {
            throw new NullPointerException("dst == null");
        }
        if (bArr2 == null) {
            throw new NullPointerException("src == null");
        }
        if (i < 0) {
            throw new IllegalArgumentException("offset hast to be >= 0");
        }
        if (bArr2.length + i > bArr.length) {
            throw new IllegalArgumentException("src length + offset must not be greater than size of destination");
        }
        for (int i2 = 0; i2 < bArr2.length; i2++) {
            bArr[i + i2] = bArr2[i2];
        }
    }

    public static byte[] extractBytesAtOffset(byte[] bArr, int i, int i2) {
        if (bArr == null) {
            throw new NullPointerException("src == null");
        }
        if (i < 0) {
            throw new IllegalArgumentException("offset hast to be >= 0");
        }
        if (i2 < 0) {
            throw new IllegalArgumentException("length hast to be >= 0");
        }
        if (i + i2 > bArr.length) {
            throw new IllegalArgumentException("offset + length must not be greater then size of source array");
        }
        byte[] bArr2 = new byte[i2];
        for (int i3 = 0; i3 < bArr2.length; i3++) {
            bArr2[i3] = bArr[i + i3];
        }
        return bArr2;
    }

    public static boolean isIndexValid(int i, long j) {
        if (j < 0) {
            throw new IllegalStateException("index must not be negative");
        }
        return j < (1 << i);
    }

    public static int getDigestSize(Digest digest) {
        if (digest == null) {
            throw new NullPointerException("digest == null");
        }
        String algorithmName = digest.getAlgorithmName();
        if (algorithmName.equals("SHAKE128")) {
            return 32;
        }
        if (algorithmName.equals("SHAKE256")) {
            return 64;
        }
        return digest.getDigestSize();
    }

    public static long getTreeIndex(long j, int i) {
        return j >> i;
    }

    public static int getLeafIndex(long j, int i) {
        return (int) (j & ((1 << i) - 1));
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }

    public static Object deserialize(byte[] bArr, Class cls) throws ClassNotFoundException, IOException {
        CheckingStream checkingStream = new CheckingStream(cls, new ByteArrayInputStream(bArr));
        Object object = checkingStream.readObject();
        if (checkingStream.available() != 0) {
            throw new IOException("unexpected data found at end of ObjectInputStream");
        }
        if (cls.isInstance(object)) {
            return object;
        }
        throw new IOException("unexpected class found in ObjectInputStream");
    }

    public static int calculateTau(int i, int i2) {
        int i3 = 0;
        for (int i4 = 0; i4 < i2; i4++) {
            if (((i >> i4) & 1) == 0) {
                i3 = i4;
                break;
            }
        }
        return i3;
    }

    public static boolean isNewBDSInitNeeded(long j, int i, int i2) {
        return j != 0 && j % ((long) Math.pow((double) (1 << i), (double) (i2 + 1))) == 0;
    }

    public static boolean isNewAuthenticationPathNeeded(long j, int i, int i2) {
        return j != 0 && (j + 1) % ((long) Math.pow((double) (1 << i), (double) i2)) == 0;
    }
}
