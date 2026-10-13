package org.spongycastle.crypto.generators;

import com.sun.jna.platform.win32.WinNT;
import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.engines.Salsa20Engine;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/generators/SCrypt.class */
public class SCrypt {
    public static byte[] generate(byte[] P, byte[] S, int N, int r, int p, int dkLen) {
        if (P == null) {
            throw new IllegalArgumentException("Passphrase P must be provided.");
        }
        if (S == null) {
            throw new IllegalArgumentException("Salt S must be provided.");
        }
        if (N <= 1) {
            throw new IllegalArgumentException("Cost parameter N must be > 1.");
        }
        if (r == 1 && N > 65536) {
            throw new IllegalArgumentException("Cost parameter N must be > 1 and < 65536.");
        }
        if (r < 1) {
            throw new IllegalArgumentException("Block size r must be >= 1.");
        }
        int maxParallel = WinNT.MAXLONG / ((128 * r) * 8);
        if (p < 1 || p > maxParallel) {
            throw new IllegalArgumentException("Parallelisation parameter p must be >= 1 and <= " + maxParallel + " (based on block size r of " + r + ")");
        }
        if (dkLen < 1) {
            throw new IllegalArgumentException("Generated key length dkLen must be >= 1.");
        }
        return MFcrypt(P, S, N, r, p, dkLen);
    }

    private static byte[] MFcrypt(byte[] P, byte[] S, int N, int r, int p, int dkLen) {
        int MFLenBytes = r * 128;
        byte[] bytes = SingleIterationPBKDF2(P, S, p * MFLenBytes);
        int[] B = null;
        try {
            int BLen = bytes.length >>> 2;
            int[] B2 = new int[BLen];
            Pack.littleEndianToInt(bytes, 0, B2);
            int MFLenWords = MFLenBytes >>> 2;
            for (int BOff = 0; BOff < BLen; BOff += MFLenWords) {
                SMix(B2, BOff, N, r);
            }
            Pack.intToLittleEndian(B2, bytes, 0);
            byte[] bArrSingleIterationPBKDF2 = SingleIterationPBKDF2(P, bytes, dkLen);
            Clear(bytes);
            return bArrSingleIterationPBKDF2;
        } finally {
            Clear(bytes);
            Clear(B);
        }
    }

    private static byte[] SingleIterationPBKDF2(byte[] P, byte[] S, int dkLen) {
        PBEParametersGenerator pGen = new PKCS5S2ParametersGenerator(new SHA256Digest());
        pGen.init(P, S, 1);
        KeyParameter key = (KeyParameter) pGen.generateDerivedMacParameters(dkLen * 8);
        return key.getKey();
    }

    private static void SMix(int[] B, int BOff, int N, int r) {
        int BCount = r * 32;
        int[] blockX1 = new int[16];
        int[] blockX2 = new int[16];
        int[] blockY = new int[BCount];
        int[] X = new int[BCount];
        int[][] V = new int[N][];
        try {
            System.arraycopy(B, BOff, X, 0, BCount);
            for (int i = 0; i < N; i++) {
                V[i] = Arrays.clone(X);
                BlockMix(X, blockX1, blockX2, blockY, r);
            }
            int mask = N - 1;
            for (int i2 = 0; i2 < N; i2++) {
                int j = X[BCount - 16] & mask;
                Xor(X, V[j], 0, X);
                BlockMix(X, blockX1, blockX2, blockY, r);
            }
            System.arraycopy(X, 0, B, BOff, BCount);
            ClearAll(V);
            ClearAll(new int[][]{X, blockX1, blockX2, blockY});
        } catch (Throwable th) {
            ClearAll(V);
            ClearAll(new int[][]{X, blockX1, blockX2, blockY});
            throw th;
        }
    }

    private static void BlockMix(int[] B, int[] X1, int[] X2, int[] Y, int r) {
        System.arraycopy(B, B.length - 16, X1, 0, 16);
        int BOff = 0;
        int YOff = 0;
        int halfLen = B.length >>> 1;
        for (int i = 2 * r; i > 0; i--) {
            Xor(X1, B, BOff, X2);
            Salsa20Engine.salsaCore(8, X2, X1);
            System.arraycopy(X1, 0, Y, YOff, 16);
            YOff = (halfLen + BOff) - YOff;
            BOff += 16;
        }
        System.arraycopy(Y, 0, B, 0, Y.length);
    }

    private static void Xor(int[] a, int[] b, int bOff, int[] output) {
        for (int i = output.length - 1; i >= 0; i--) {
            output[i] = a[i] ^ b[bOff + i];
        }
    }

    private static void Clear(byte[] array) {
        if (array != null) {
            Arrays.fill(array, (byte) 0);
        }
    }

    private static void Clear(int[] array) {
        if (array != null) {
            Arrays.fill(array, 0);
        }
    }

    private static void ClearAll(int[][] arrays) {
        for (int[] iArr : arrays) {
            Clear(iArr);
        }
    }
}
