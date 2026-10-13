package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.params.KeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/SerpentEngineBase.class */
public abstract class SerpentEngineBase implements BlockCipher {
    protected static final int BLOCK_SIZE = 16;
    static final int ROUNDS = 32;
    static final int PHI = -1640531527;
    protected boolean encrypting;
    protected int[] wKey;
    protected int X0;
    protected int X1;
    protected int X2;
    protected int X3;

    protected abstract int[] makeWorkingKey(byte[] bArr);

    protected abstract void encryptBlock(byte[] bArr, int i, byte[] bArr2, int i2);

    protected abstract void decryptBlock(byte[] bArr, int i, byte[] bArr2, int i2);

    SerpentEngineBase() {
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean encrypting, CipherParameters params) {
        if (params instanceof KeyParameter) {
            this.encrypting = encrypting;
            this.wKey = makeWorkingKey(((KeyParameter) params).getKey());
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to " + getAlgorithmName() + " init - " + params.getClass().getName());
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "Serpent";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 16;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public final int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (this.wKey == null) {
            throw new IllegalStateException(getAlgorithmName() + " not initialised");
        }
        if (inOff + 16 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + 16 > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.encrypting) {
            encryptBlock(in, inOff, out, outOff);
            return 16;
        }
        decryptBlock(in, inOff, out, outOff);
        return 16;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
    }

    protected static int rotateLeft(int x, int bits) {
        return (x << bits) | (x >>> (-bits));
    }

    protected static int rotateRight(int x, int bits) {
        return (x >>> bits) | (x << (-bits));
    }

    protected final void sb0(int a, int b, int c, int d) {
        int t1 = a ^ d;
        int t3 = c ^ t1;
        int t4 = b ^ t3;
        this.X3 = (a & d) ^ t4;
        int t7 = a ^ (b & t1);
        this.X2 = t4 ^ (c | t7);
        int t12 = this.X3 & (t3 ^ t7);
        this.X1 = (t3 ^ (-1)) ^ t12;
        this.X0 = t12 ^ (t7 ^ (-1));
    }

    protected final void ib0(int a, int b, int c, int d) {
        int t1 = a ^ (-1);
        int t2 = a ^ b;
        int t4 = d ^ (t1 | t2);
        int t5 = c ^ t4;
        this.X2 = t2 ^ t5;
        int t8 = t1 ^ (d & t2);
        this.X1 = t4 ^ (this.X2 & t8);
        this.X3 = (a & t4) ^ (t5 | this.X1);
        this.X0 = this.X3 ^ (t5 ^ t8);
    }

    protected final void sb1(int a, int b, int c, int d) {
        int t2 = b ^ (a ^ (-1));
        int t5 = c ^ (a | t2);
        this.X2 = d ^ t5;
        int t7 = b ^ (d | t2);
        int t8 = t2 ^ this.X2;
        this.X3 = t8 ^ (t5 & t7);
        int t11 = t5 ^ t7;
        this.X1 = this.X3 ^ t11;
        this.X0 = t5 ^ (t8 & t11);
    }

    protected final void ib1(int a, int b, int c, int d) {
        int t1 = b ^ d;
        int t3 = a ^ (b & t1);
        int t4 = t1 ^ t3;
        this.X3 = c ^ t4;
        int t7 = b ^ (t1 & t3);
        int t8 = this.X3 | t7;
        this.X1 = t3 ^ t8;
        int t10 = this.X1 ^ (-1);
        int t11 = this.X3 ^ t7;
        this.X0 = t10 ^ t11;
        this.X2 = t4 ^ (t10 | t11);
    }

    protected final void sb2(int a, int b, int c, int d) {
        int t1 = a ^ (-1);
        int t2 = b ^ d;
        int t3 = c & t1;
        this.X0 = t2 ^ t3;
        int t5 = c ^ t1;
        int t6 = c ^ this.X0;
        int t7 = b & t6;
        this.X3 = t5 ^ t7;
        this.X2 = a ^ ((d | t7) & (this.X0 | t5));
        this.X1 = (t2 ^ this.X3) ^ (this.X2 ^ (d | t1));
    }

    protected final void ib2(int a, int b, int c, int d) {
        int t1 = b ^ d;
        int t2 = t1 ^ (-1);
        int t3 = a ^ c;
        int t4 = c ^ t1;
        int t5 = b & t4;
        this.X0 = t3 ^ t5;
        int t7 = a | t2;
        int t8 = d ^ t7;
        int t9 = t3 | t8;
        this.X3 = t1 ^ t9;
        int t11 = t4 ^ (-1);
        int t12 = this.X0 | this.X3;
        this.X1 = t11 ^ t12;
        this.X2 = (d & t11) ^ (t3 ^ t12);
    }

    protected final void sb3(int a, int b, int c, int d) {
        int t1 = a ^ b;
        int t2 = a & c;
        int t3 = a | d;
        int t4 = c ^ d;
        int t5 = t1 & t3;
        int t6 = t2 | t5;
        this.X2 = t4 ^ t6;
        int t8 = b ^ t3;
        int t9 = t6 ^ t8;
        int t10 = t4 & t9;
        this.X0 = t1 ^ t10;
        int t12 = this.X2 & this.X0;
        this.X1 = t9 ^ t12;
        this.X3 = (b | d) ^ (t4 ^ t12);
    }

    protected final void ib3(int a, int b, int c, int d) {
        int t1 = a | b;
        int t2 = b ^ c;
        int t3 = b & t2;
        int t4 = a ^ t3;
        int t5 = c ^ t4;
        int t6 = d | t4;
        this.X0 = t2 ^ t6;
        int t8 = t2 | t6;
        int t9 = d ^ t8;
        this.X2 = t5 ^ t9;
        int t11 = t1 ^ t9;
        int t12 = this.X0 & t11;
        this.X3 = t4 ^ t12;
        this.X1 = this.X3 ^ (this.X0 ^ t11);
    }

    protected final void sb4(int a, int b, int c, int d) {
        int t1 = a ^ d;
        int t2 = d & t1;
        int t3 = c ^ t2;
        int t4 = b | t3;
        this.X3 = t1 ^ t4;
        int t6 = b ^ (-1);
        int t7 = t1 | t6;
        this.X0 = t3 ^ t7;
        int t9 = a & this.X0;
        int t10 = t1 ^ t6;
        int t11 = t4 & t10;
        this.X2 = t9 ^ t11;
        this.X1 = (a ^ t3) ^ (t10 & this.X2);
    }

    protected final void ib4(int a, int b, int c, int d) {
        int t1 = c | d;
        int t2 = a & t1;
        int t3 = b ^ t2;
        int t4 = a & t3;
        int t5 = c ^ t4;
        this.X1 = d ^ t5;
        int t7 = a ^ (-1);
        int t8 = t5 & this.X1;
        this.X3 = t3 ^ t8;
        int t10 = this.X1 | t7;
        int t11 = d ^ t10;
        this.X0 = this.X3 ^ t11;
        this.X2 = (t3 & t11) ^ (this.X1 ^ t7);
    }

    protected final void sb5(int a, int b, int c, int d) {
        int t1 = a ^ (-1);
        int t2 = a ^ b;
        int t3 = a ^ d;
        int t4 = c ^ t1;
        int t5 = t2 | t3;
        this.X0 = t4 ^ t5;
        int t7 = d & this.X0;
        int t8 = t2 ^ this.X0;
        this.X1 = t7 ^ t8;
        int t10 = t1 | this.X0;
        int t11 = t2 | t7;
        int t12 = t3 ^ t10;
        this.X2 = t11 ^ t12;
        this.X3 = (b ^ t7) ^ (this.X1 & t12);
    }

    protected final void ib5(int a, int b, int c, int d) {
        int t1 = c ^ (-1);
        int t2 = b & t1;
        int t3 = d ^ t2;
        int t4 = a & t3;
        int t5 = b ^ t1;
        this.X3 = t4 ^ t5;
        int t7 = b | this.X3;
        int t8 = a & t7;
        this.X1 = t3 ^ t8;
        int t10 = a | d;
        int t11 = t1 ^ t7;
        this.X0 = t10 ^ t11;
        this.X2 = (b & t10) ^ (t4 | (a ^ c));
    }

    protected final void sb6(int a, int b, int c, int d) {
        int t1 = a ^ (-1);
        int t2 = a ^ d;
        int t3 = b ^ t2;
        int t4 = t1 | t2;
        int t5 = c ^ t4;
        this.X1 = b ^ t5;
        int t7 = t2 | this.X1;
        int t8 = d ^ t7;
        int t9 = t5 & t8;
        this.X2 = t3 ^ t9;
        int t11 = t5 ^ t8;
        this.X0 = this.X2 ^ t11;
        this.X3 = (t5 ^ (-1)) ^ (t3 & t11);
    }

    protected final void ib6(int a, int b, int c, int d) {
        int t1 = a ^ (-1);
        int t2 = a ^ b;
        int t3 = c ^ t2;
        int t4 = c | t1;
        int t5 = d ^ t4;
        this.X1 = t3 ^ t5;
        int t7 = t3 & t5;
        int t8 = t2 ^ t7;
        int t9 = b | t8;
        this.X3 = t5 ^ t9;
        int t11 = b | this.X3;
        this.X0 = t8 ^ t11;
        this.X2 = (d & t1) ^ (t3 ^ t11);
    }

    protected final void sb7(int a, int b, int c, int d) {
        int t1 = b ^ c;
        int t2 = c & t1;
        int t3 = d ^ t2;
        int t4 = a ^ t3;
        int t5 = d | t1;
        int t6 = t4 & t5;
        this.X1 = b ^ t6;
        int t8 = t3 | this.X1;
        int t9 = a & t4;
        this.X3 = t1 ^ t9;
        int t11 = t4 ^ t8;
        int t12 = this.X3 & t11;
        this.X2 = t3 ^ t12;
        this.X0 = (t11 ^ (-1)) ^ (this.X3 & this.X2);
    }

    protected final void ib7(int a, int b, int c, int d) {
        int t3 = c | (a & b);
        int t4 = d & (a | b);
        this.X3 = t3 ^ t4;
        int t6 = d ^ (-1);
        int t7 = b ^ t4;
        int t9 = t7 | (this.X3 ^ t6);
        this.X1 = a ^ t9;
        this.X0 = (c ^ t7) ^ (d | this.X1);
        this.X2 = (t3 ^ this.X1) ^ (this.X0 ^ (a & this.X3));
    }

    protected final void LT() {
        int x0 = rotateLeft(this.X0, 13);
        int x2 = rotateLeft(this.X2, 3);
        int x1 = (this.X1 ^ x0) ^ x2;
        int x3 = (this.X3 ^ x2) ^ (x0 << 3);
        this.X1 = rotateLeft(x1, 1);
        this.X3 = rotateLeft(x3, 7);
        this.X0 = rotateLeft((x0 ^ this.X1) ^ this.X3, 5);
        this.X2 = rotateLeft((x2 ^ this.X3) ^ (this.X1 << 7), 22);
    }

    protected final void inverseLT() {
        int x2 = (rotateRight(this.X2, 22) ^ this.X3) ^ (this.X1 << 7);
        int x0 = (rotateRight(this.X0, 5) ^ this.X1) ^ this.X3;
        int x3 = rotateRight(this.X3, 7);
        int x1 = rotateRight(this.X1, 1);
        this.X3 = (x3 ^ x2) ^ (x0 << 3);
        this.X1 = (x1 ^ x0) ^ x2;
        this.X2 = rotateRight(x2, 3);
        this.X0 = rotateRight(x0, 13);
    }
}
