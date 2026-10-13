package org.spongycastle.jce.provider;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.MD5Digest;
import org.spongycastle.crypto.digests.RIPEMD160Digest;
import org.spongycastle.crypto.digests.SHA1Digest;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

/* JADX INFO: compiled from: BrokenPBE.java */
/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/provider/OldPKCS12ParametersGenerator.class */
class OldPKCS12ParametersGenerator extends PBEParametersGenerator {
    public static final int KEY_MATERIAL = 1;
    public static final int IV_MATERIAL = 2;
    public static final int MAC_MATERIAL = 3;
    private Digest digest;
    private int u;
    private int v;

    public OldPKCS12ParametersGenerator(Digest digest) {
        this.digest = digest;
        if (digest instanceof MD5Digest) {
            this.u = 16;
            this.v = 64;
        } else if (digest instanceof SHA1Digest) {
            this.u = 20;
            this.v = 64;
        } else {
            if (digest instanceof RIPEMD160Digest) {
                this.u = 20;
                this.v = 64;
                return;
            }
            throw new IllegalArgumentException("Digest " + digest.getAlgorithmName() + " unsupported");
        }
    }

    private void adjust(byte[] a, int aOff, byte[] b) {
        int x = (b[b.length - 1] & 255) + (a[(aOff + b.length) - 1] & 255) + 1;
        a[(aOff + b.length) - 1] = (byte) x;
        int x2 = x >>> 8;
        for (int i = b.length - 2; i >= 0; i--) {
            int x3 = x2 + (b[i] & 255) + (a[aOff + i] & 255);
            a[aOff + i] = (byte) x3;
            x2 = x3 >>> 8;
        }
    }

    private byte[] generateDerivedKey(int idByte, int n) {
        byte[] S;
        byte[] P;
        byte[] D = new byte[this.v];
        byte[] dKey = new byte[n];
        for (int i = 0; i != D.length; i++) {
            D[i] = (byte) idByte;
        }
        if (this.salt != null && this.salt.length != 0) {
            S = new byte[this.v * (((this.salt.length + this.v) - 1) / this.v)];
            for (int i2 = 0; i2 != S.length; i2++) {
                S[i2] = this.salt[i2 % this.salt.length];
            }
        } else {
            S = new byte[0];
        }
        if (this.password != null && this.password.length != 0) {
            P = new byte[this.v * (((this.password.length + this.v) - 1) / this.v)];
            for (int i3 = 0; i3 != P.length; i3++) {
                P[i3] = this.password[i3 % this.password.length];
            }
        } else {
            P = new byte[0];
        }
        byte[] I = new byte[S.length + P.length];
        System.arraycopy(S, 0, I, 0, S.length);
        System.arraycopy(P, 0, I, S.length, P.length);
        byte[] B = new byte[this.v];
        int c = ((n + this.u) - 1) / this.u;
        for (int i4 = 1; i4 <= c; i4++) {
            byte[] A = new byte[this.u];
            this.digest.update(D, 0, D.length);
            this.digest.update(I, 0, I.length);
            this.digest.doFinal(A, 0);
            for (int j = 1; j != this.iterationCount; j++) {
                this.digest.update(A, 0, A.length);
                this.digest.doFinal(A, 0);
            }
            for (int j2 = 0; j2 != B.length; j2++) {
                B[i4] = A[j2 % A.length];
            }
            for (int j3 = 0; j3 != I.length / this.v; j3++) {
                adjust(I, j3 * this.v, B);
            }
            if (i4 == c) {
                System.arraycopy(A, 0, dKey, (i4 - 1) * this.u, dKey.length - ((i4 - 1) * this.u));
            } else {
                System.arraycopy(A, 0, dKey, (i4 - 1) * this.u, A.length);
            }
        }
        return dKey;
    }

    @Override // org.spongycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedParameters(int keySize) {
        int keySize2 = keySize / 8;
        byte[] dKey = generateDerivedKey(1, keySize2);
        return new KeyParameter(dKey, 0, keySize2);
    }

    @Override // org.spongycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedParameters(int keySize, int ivSize) {
        int keySize2 = keySize / 8;
        int ivSize2 = ivSize / 8;
        byte[] dKey = generateDerivedKey(1, keySize2);
        byte[] iv = generateDerivedKey(2, ivSize2);
        return new ParametersWithIV(new KeyParameter(dKey, 0, keySize2), iv, 0, ivSize2);
    }

    @Override // org.spongycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedMacParameters(int keySize) {
        int keySize2 = keySize / 8;
        byte[] dKey = generateDerivedKey(3, keySize2);
        return new KeyParameter(dKey, 0, keySize2);
    }
}
