package org.spongycastle.pqc.math.ntru.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.spongycastle.pqc.math.ntru.euclid.IntEuclidean;
import org.spongycastle.pqc.math.ntru.polynomial.DenseTernaryPolynomial;
import org.spongycastle.pqc.math.ntru.polynomial.SparseTernaryPolynomial;
import org.spongycastle.pqc.math.ntru.polynomial.TernaryPolynomial;
import org.spongycastle.util.Integers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/ntru/util/Util.class */
public class Util {
    private static volatile boolean IS_64_BITNESS_KNOWN;
    private static volatile boolean IS_64_BIT_JVM;

    public static int invert(int n, int modulus) {
        int n2 = n % modulus;
        if (n2 < 0) {
            n2 += modulus;
        }
        return IntEuclidean.calculate(n2, modulus).x;
    }

    public static int pow(int a, int b, int modulus) {
        int p = 1;
        for (int i = 0; i < b; i++) {
            p = (p * a) % modulus;
        }
        return p;
    }

    public static long pow(long a, int b, long modulus) {
        long p = 1;
        for (int i = 0; i < b; i++) {
            p = (p * a) % modulus;
        }
        return p;
    }

    public static TernaryPolynomial generateRandomTernary(int N, int numOnes, int numNegOnes, boolean sparse, SecureRandom random) {
        if (sparse) {
            return SparseTernaryPolynomial.generateRandom(N, numOnes, numNegOnes, random);
        }
        return DenseTernaryPolynomial.generateRandom(N, numOnes, numNegOnes, random);
    }

    public static int[] generateRandomTernary(int N, int numOnes, int numNegOnes, SecureRandom random) {
        Integer one = Integers.valueOf(1);
        Integer minusOne = Integers.valueOf(-1);
        Integer zero = Integers.valueOf(0);
        List list = new ArrayList();
        for (int i = 0; i < numOnes; i++) {
            list.add(one);
        }
        for (int i2 = 0; i2 < numNegOnes; i2++) {
            list.add(minusOne);
        }
        while (list.size() < N) {
            list.add(zero);
        }
        Collections.shuffle(list, random);
        int[] arr = new int[N];
        for (int i3 = 0; i3 < N; i3++) {
            arr[i3] = ((Integer) list.get(i3)).intValue();
        }
        return arr;
    }

    public static boolean is64BitJVM() {
        if (!IS_64_BITNESS_KNOWN) {
            String arch = System.getProperty("os.arch");
            String sunModel = System.getProperty("sun.arch.data.model");
            IS_64_BIT_JVM = "amd64".equals(arch) || "x86_64".equals(arch) || "ppc64".equals(arch) || "64".equals(sunModel);
            IS_64_BITNESS_KNOWN = true;
        }
        return IS_64_BIT_JVM;
    }

    public static byte[] readFullLength(InputStream is, int length) throws IOException {
        byte[] arr = new byte[length];
        if (is.read(arr) != arr.length) {
            throw new IOException("Not enough bytes to read.");
        }
        return arr;
    }
}
