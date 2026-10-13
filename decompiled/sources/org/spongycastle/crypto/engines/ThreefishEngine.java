package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.TweakableBlockCipherParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/ThreefishEngine.class */
public class ThreefishEngine implements BlockCipher {
    public static final int BLOCKSIZE_256 = 256;
    public static final int BLOCKSIZE_512 = 512;
    public static final int BLOCKSIZE_1024 = 1024;
    private static final int TWEAK_SIZE_BYTES = 16;
    private static final int TWEAK_SIZE_WORDS = 2;
    private static final int ROUNDS_256 = 72;
    private static final int ROUNDS_512 = 72;
    private static final int ROUNDS_1024 = 80;
    private static final int MAX_ROUNDS = 80;
    private static final long C_240 = 2004413935125273122L;
    private static int[] MOD9 = new int[80];
    private static int[] MOD17 = new int[MOD9.length];
    private static int[] MOD5 = new int[MOD9.length];
    private static int[] MOD3 = new int[MOD9.length];
    private int blocksizeBytes;
    private int blocksizeWords;
    private long[] currentBlock;
    private long[] t = new long[5];
    private long[] kw;
    private ThreefishCipher cipher;
    private boolean forEncryption;

    static {
        for (int i = 0; i < MOD9.length; i++) {
            MOD17[i] = i % 17;
            MOD9[i] = i % 9;
            MOD5[i] = i % 5;
            MOD3[i] = i % 3;
        }
    }

    public ThreefishEngine(int blocksizeBits) {
        this.blocksizeBytes = blocksizeBits / 8;
        this.blocksizeWords = this.blocksizeBytes / 8;
        this.currentBlock = new long[this.blocksizeWords];
        this.kw = new long[(2 * this.blocksizeWords) + 1];
        switch (blocksizeBits) {
            case 256:
                this.cipher = new Threefish256Cipher(this.kw, this.t);
                return;
            case 512:
                this.cipher = new Threefish512Cipher(this.kw, this.t);
                return;
            case 1024:
                this.cipher = new Threefish1024Cipher(this.kw, this.t);
                return;
            default:
                throw new IllegalArgumentException("Invalid blocksize - Threefish is defined with block size of 256, 512, or 1024 bits");
        }
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        byte[] keyBytes;
        byte[] tweakBytes;
        if (params instanceof TweakableBlockCipherParameters) {
            TweakableBlockCipherParameters tParams = (TweakableBlockCipherParameters) params;
            keyBytes = tParams.getKey().getKey();
            tweakBytes = tParams.getTweak();
        } else if (params instanceof KeyParameter) {
            keyBytes = ((KeyParameter) params).getKey();
            tweakBytes = null;
        } else {
            throw new IllegalArgumentException("Invalid parameter passed to Threefish init - " + params.getClass().getName());
        }
        long[] keyWords = null;
        long[] tweakWords = null;
        if (keyBytes != null) {
            if (keyBytes.length != this.blocksizeBytes) {
                throw new IllegalArgumentException("Threefish key must be same size as block (" + this.blocksizeBytes + " bytes)");
            }
            keyWords = new long[this.blocksizeWords];
            for (int i = 0; i < keyWords.length; i++) {
                keyWords[i] = bytesToWord(keyBytes, i * 8);
            }
        }
        if (tweakBytes != null) {
            if (tweakBytes.length != 16) {
                throw new IllegalArgumentException("Threefish tweak must be 16 bytes");
            }
            tweakWords = new long[]{bytesToWord(tweakBytes, 0), bytesToWord(tweakBytes, 8)};
        }
        init(forEncryption, keyWords, tweakWords);
    }

    public void init(boolean forEncryption, long[] key, long[] tweak) {
        this.forEncryption = forEncryption;
        if (key != null) {
            setKey(key);
        }
        if (tweak != null) {
            setTweak(tweak);
        }
    }

    private void setKey(long[] key) {
        if (key.length != this.blocksizeWords) {
            throw new IllegalArgumentException("Threefish key must be same size as block (" + this.blocksizeWords + " words)");
        }
        long knw = 2004413935125273122L;
        for (int i = 0; i < this.blocksizeWords; i++) {
            this.kw[i] = key[i];
            knw ^= this.kw[i];
        }
        this.kw[this.blocksizeWords] = knw;
        System.arraycopy(this.kw, 0, this.kw, this.blocksizeWords + 1, this.blocksizeWords);
    }

    private void setTweak(long[] tweak) {
        if (tweak.length != 2) {
            throw new IllegalArgumentException("Tweak must be 2 words.");
        }
        this.t[0] = tweak[0];
        this.t[1] = tweak[1];
        this.t[2] = this.t[0] ^ this.t[1];
        this.t[3] = this.t[0];
        this.t[4] = this.t[1];
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "Threefish-" + (this.blocksizeBytes * 8);
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return this.blocksizeBytes;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        if (inOff + this.blocksizeBytes > in.length) {
            throw new DataLengthException("Input buffer too short");
        }
        if (outOff + this.blocksizeBytes > out.length) {
            throw new OutputLengthException("Output buffer too short");
        }
        for (int i = 0; i < this.blocksizeBytes; i += 8) {
            this.currentBlock[i >> 3] = bytesToWord(in, inOff + i);
        }
        processBlock(this.currentBlock, this.currentBlock);
        for (int i2 = 0; i2 < this.blocksizeBytes; i2 += 8) {
            wordToBytes(this.currentBlock[i2 >> 3], out, outOff + i2);
        }
        return this.blocksizeBytes;
    }

    public int processBlock(long[] in, long[] out) throws IllegalStateException, DataLengthException {
        if (this.kw[this.blocksizeWords] == 0) {
            throw new IllegalStateException("Threefish engine not initialised");
        }
        if (in.length != this.blocksizeWords) {
            throw new DataLengthException("Input buffer too short");
        }
        if (out.length != this.blocksizeWords) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (this.forEncryption) {
            this.cipher.encryptBlock(in, out);
        } else {
            this.cipher.decryptBlock(in, out);
        }
        return this.blocksizeWords;
    }

    public static long bytesToWord(byte[] bytes, int off) {
        if (off + 8 > bytes.length) {
            throw new IllegalArgumentException();
        }
        int index = off + 1;
        long word = ((long) bytes[off]) & 255;
        int index2 = index + 1;
        long word2 = word | ((((long) bytes[index]) & 255) << 8);
        int index3 = index2 + 1;
        long word3 = word2 | ((((long) bytes[index2]) & 255) << 16);
        int index4 = index3 + 1;
        long word4 = word3 | ((((long) bytes[index3]) & 255) << 24);
        int index5 = index4 + 1;
        long word5 = word4 | ((((long) bytes[index4]) & 255) << 32);
        int index6 = index5 + 1;
        long word6 = word5 | ((((long) bytes[index5]) & 255) << 40);
        int index7 = index6 + 1;
        long word7 = word6 | ((((long) bytes[index6]) & 255) << 48);
        int i = index7 + 1;
        return word7 | ((((long) bytes[index7]) & 255) << 56);
    }

    public static void wordToBytes(long word, byte[] bytes, int off) {
        if (off + 8 > bytes.length) {
            throw new IllegalArgumentException();
        }
        int index = off + 1;
        bytes[off] = (byte) word;
        int index2 = index + 1;
        bytes[index] = (byte) (word >> 8);
        int index3 = index2 + 1;
        bytes[index2] = (byte) (word >> 16);
        int index4 = index3 + 1;
        bytes[index3] = (byte) (word >> 24);
        int index5 = index4 + 1;
        bytes[index4] = (byte) (word >> 32);
        int index6 = index5 + 1;
        bytes[index5] = (byte) (word >> 40);
        int index7 = index6 + 1;
        bytes[index6] = (byte) (word >> 48);
        int i = index7 + 1;
        bytes[index7] = (byte) (word >> 56);
    }

    static long rotlXor(long x, int n, long xor) {
        return ((x << n) | (x >>> (-n))) ^ xor;
    }

    static long xorRotr(long x, int n, long xor) {
        long xored = x ^ xor;
        return (xored >>> n) | (xored << (-n));
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/ThreefishEngine$ThreefishCipher.class */
    private static abstract class ThreefishCipher {
        protected final long[] t;
        protected final long[] kw;

        abstract void encryptBlock(long[] jArr, long[] jArr2);

        abstract void decryptBlock(long[] jArr, long[] jArr2);

        protected ThreefishCipher(long[] kw, long[] t) {
            this.kw = kw;
            this.t = t;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/ThreefishEngine$Threefish256Cipher.class */
    private static final class Threefish256Cipher extends ThreefishCipher {
        private static final int ROTATION_0_0 = 14;
        private static final int ROTATION_0_1 = 16;
        private static final int ROTATION_1_0 = 52;
        private static final int ROTATION_1_1 = 57;
        private static final int ROTATION_2_0 = 23;
        private static final int ROTATION_2_1 = 40;
        private static final int ROTATION_3_0 = 5;
        private static final int ROTATION_3_1 = 37;
        private static final int ROTATION_4_0 = 25;
        private static final int ROTATION_4_1 = 33;
        private static final int ROTATION_5_0 = 46;
        private static final int ROTATION_5_1 = 12;
        private static final int ROTATION_6_0 = 58;
        private static final int ROTATION_6_1 = 22;
        private static final int ROTATION_7_0 = 32;
        private static final int ROTATION_7_1 = 32;

        public Threefish256Cipher(long[] kw, long[] t) {
            super(kw, t);
        }

        @Override // org.spongycastle.crypto.engines.ThreefishEngine.ThreefishCipher
        void encryptBlock(long[] block, long[] out) {
            long[] kw = this.kw;
            long[] t = this.t;
            int[] mod5 = ThreefishEngine.MOD5;
            int[] mod3 = ThreefishEngine.MOD3;
            if (kw.length != 9) {
                throw new IllegalArgumentException();
            }
            if (t.length != 5) {
                throw new IllegalArgumentException();
            }
            long b0 = block[0];
            long b1 = block[1];
            long b2 = block[2];
            long b3 = block[3];
            long b4 = b0 + kw[0];
            long b5 = b1 + kw[1] + t[0];
            long b6 = b2 + kw[2] + t[1];
            long b7 = b3 + kw[3];
            for (int d = 1; d < 18; d += 2) {
                int dm5 = mod5[d];
                int dm3 = mod3[d];
                long b8 = b5;
                long b9 = ThreefishEngine.rotlXor(b8, 14, b4 + b5);
                long b10 = b6 + b7;
                long b11 = ThreefishEngine.rotlXor(b7, 16, b10);
                long b12 = b8 + b11;
                long b13 = ThreefishEngine.rotlXor(b11, 52, b12);
                long b14 = b10 + b9;
                long b15 = ThreefishEngine.rotlXor(b9, 57, b14);
                long b16 = b12 + b15;
                long b17 = ThreefishEngine.rotlXor(b15, 23, b16);
                long b18 = b14 + b13;
                long b19 = ThreefishEngine.rotlXor(b13, 40, b18);
                long b20 = b16 + b19;
                long b21 = ThreefishEngine.rotlXor(b19, 5, b20);
                long b22 = b18 + b17;
                long b23 = ThreefishEngine.rotlXor(b17, 37, b22);
                long b24 = b20 + kw[dm5];
                long b25 = b23 + kw[dm5 + 1] + t[dm3];
                long b26 = b22 + kw[dm5 + 2] + t[dm3 + 1];
                long b27 = b21 + kw[dm5 + 3] + ((long) d);
                long b28 = b24 + b25;
                long b29 = ThreefishEngine.rotlXor(b25, 25, b28);
                long b30 = b26 + b27;
                long b31 = ThreefishEngine.rotlXor(b27, 33, b30);
                long b32 = b28 + b31;
                long b33 = ThreefishEngine.rotlXor(b31, 46, b32);
                long b34 = b30 + b29;
                long b35 = ThreefishEngine.rotlXor(b29, 12, b34);
                long b36 = b32 + b35;
                long b37 = ThreefishEngine.rotlXor(b35, 58, b36);
                long b38 = b34 + b33;
                long b39 = ThreefishEngine.rotlXor(b33, 22, b38);
                long b40 = b36 + b39;
                long b41 = ThreefishEngine.rotlXor(b39, 32, b40);
                long b42 = b38 + b37;
                long b43 = ThreefishEngine.rotlXor(b37, 32, b42);
                b4 = b40 + kw[dm5 + 1];
                b5 = b43 + kw[dm5 + 2] + t[dm3 + 1];
                b6 = b42 + kw[dm5 + 3] + t[dm3 + 2];
                b7 = b41 + kw[dm5 + 4] + ((long) d) + 1;
            }
            out[0] = b4;
            out[1] = b5;
            out[2] = b6;
            out[3] = b7;
        }

        @Override // org.spongycastle.crypto.engines.ThreefishEngine.ThreefishCipher
        void decryptBlock(long[] block, long[] state) {
            long[] kw = this.kw;
            long[] t = this.t;
            int[] mod5 = ThreefishEngine.MOD5;
            int[] mod3 = ThreefishEngine.MOD3;
            if (kw.length != 9) {
                throw new IllegalArgumentException();
            }
            if (t.length != 5) {
                throw new IllegalArgumentException();
            }
            long b0 = block[0];
            long b1 = block[1];
            long b2 = block[2];
            long b3 = block[3];
            for (int d = 17; d >= 1; d -= 2) {
                int dm5 = mod5[d];
                int dm3 = mod3[d];
                long b4 = b0 - kw[dm5 + 1];
                long b5 = b1 - (kw[dm5 + 2] + t[dm3 + 1]);
                long b6 = b2 - (kw[dm5 + 3] + t[dm3 + 2]);
                long b7 = ThreefishEngine.xorRotr(b3 - ((kw[dm5 + 4] + ((long) d)) + 1), 32, b4);
                long b8 = b4 - b7;
                long b9 = ThreefishEngine.xorRotr(b5, 32, b6);
                long b10 = b6 - b9;
                long b11 = ThreefishEngine.xorRotr(b9, 58, b8);
                long b12 = b8 - b11;
                long b13 = ThreefishEngine.xorRotr(b7, 22, b10);
                long b14 = b10 - b13;
                long b15 = ThreefishEngine.xorRotr(b13, 46, b12);
                long b16 = b12 - b15;
                long b17 = ThreefishEngine.xorRotr(b11, 12, b14);
                long b18 = b14 - b17;
                long b19 = ThreefishEngine.xorRotr(b17, 25, b16);
                long b20 = b16 - b19;
                long b21 = ThreefishEngine.xorRotr(b15, 33, b18);
                long b22 = b18 - b21;
                long b23 = b20 - kw[dm5];
                long b24 = b19 - (kw[dm5 + 1] + t[dm3]);
                long b25 = b22 - (kw[dm5 + 2] + t[dm3 + 1]);
                long b26 = ThreefishEngine.xorRotr(b21 - (kw[dm5 + 3] + ((long) d)), 5, b23);
                long b27 = b23 - b26;
                long b28 = ThreefishEngine.xorRotr(b24, 37, b25);
                long b29 = b25 - b28;
                long b30 = ThreefishEngine.xorRotr(b28, 23, b27);
                long b31 = b27 - b30;
                long b32 = ThreefishEngine.xorRotr(b26, 40, b29);
                long b33 = b29 - b32;
                long b34 = ThreefishEngine.xorRotr(b32, 52, b31);
                long b35 = b31 - b34;
                long b36 = ThreefishEngine.xorRotr(b30, 57, b33);
                long b37 = b33 - b36;
                b1 = ThreefishEngine.xorRotr(b36, 14, b35);
                b0 = b35 - b1;
                b3 = ThreefishEngine.xorRotr(b34, 16, b37);
                b2 = b37 - b3;
            }
            long b38 = b0 - kw[0];
            long b39 = b1 - (kw[1] + t[0]);
            long b40 = b2 - (kw[2] + t[1]);
            long b41 = b3 - kw[3];
            state[0] = b38;
            state[1] = b39;
            state[2] = b40;
            state[3] = b41;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/ThreefishEngine$Threefish512Cipher.class */
    private static final class Threefish512Cipher extends ThreefishCipher {
        private static final int ROTATION_0_0 = 46;
        private static final int ROTATION_0_1 = 36;
        private static final int ROTATION_0_2 = 19;
        private static final int ROTATION_0_3 = 37;
        private static final int ROTATION_1_0 = 33;
        private static final int ROTATION_1_1 = 27;
        private static final int ROTATION_1_2 = 14;
        private static final int ROTATION_1_3 = 42;
        private static final int ROTATION_2_0 = 17;
        private static final int ROTATION_2_1 = 49;
        private static final int ROTATION_2_2 = 36;
        private static final int ROTATION_2_3 = 39;
        private static final int ROTATION_3_0 = 44;
        private static final int ROTATION_3_1 = 9;
        private static final int ROTATION_3_2 = 54;
        private static final int ROTATION_3_3 = 56;
        private static final int ROTATION_4_0 = 39;
        private static final int ROTATION_4_1 = 30;
        private static final int ROTATION_4_2 = 34;
        private static final int ROTATION_4_3 = 24;
        private static final int ROTATION_5_0 = 13;
        private static final int ROTATION_5_1 = 50;
        private static final int ROTATION_5_2 = 10;
        private static final int ROTATION_5_3 = 17;
        private static final int ROTATION_6_0 = 25;
        private static final int ROTATION_6_1 = 29;
        private static final int ROTATION_6_2 = 39;
        private static final int ROTATION_6_3 = 43;
        private static final int ROTATION_7_0 = 8;
        private static final int ROTATION_7_1 = 35;
        private static final int ROTATION_7_2 = 56;
        private static final int ROTATION_7_3 = 22;

        protected Threefish512Cipher(long[] kw, long[] t) {
            super(kw, t);
        }

        @Override // org.spongycastle.crypto.engines.ThreefishEngine.ThreefishCipher
        public void encryptBlock(long[] block, long[] out) {
            long[] kw = this.kw;
            long[] t = this.t;
            int[] mod9 = ThreefishEngine.MOD9;
            int[] mod3 = ThreefishEngine.MOD3;
            if (kw.length != 17) {
                throw new IllegalArgumentException();
            }
            if (t.length != 5) {
                throw new IllegalArgumentException();
            }
            long b0 = block[0];
            long b1 = block[1];
            long b2 = block[2];
            long b3 = block[3];
            long b4 = block[4];
            long b5 = block[5];
            long b6 = block[6];
            long b7 = block[7];
            long b8 = b0 + kw[0];
            long b9 = b1 + kw[1];
            long b10 = b2 + kw[2];
            long b11 = b3 + kw[3];
            long b12 = b4 + kw[4];
            long b13 = b5 + kw[5] + t[0];
            long b14 = b6 + kw[6] + t[1];
            long b15 = b7 + kw[7];
            for (int d = 1; d < 18; d += 2) {
                int dm9 = mod9[d];
                int dm3 = mod3[d];
                long b16 = b9;
                long b17 = ThreefishEngine.rotlXor(b16, 46, b8 + b9);
                long b18 = b10 + b11;
                long b19 = ThreefishEngine.rotlXor(b11, 36, b18);
                long b20 = b12 + b13;
                long b21 = ThreefishEngine.rotlXor(b13, 19, b20);
                long b22 = b14 + b15;
                long b23 = ThreefishEngine.rotlXor(b15, 37, b22);
                long b24 = b18 + b17;
                long b25 = ThreefishEngine.rotlXor(b17, 33, b24);
                long b26 = b20 + b23;
                long b27 = ThreefishEngine.rotlXor(b23, 27, b26);
                long b28 = b22 + b21;
                long b29 = ThreefishEngine.rotlXor(b21, 14, b28);
                long b30 = b16 + b19;
                long b31 = ThreefishEngine.rotlXor(b19, 42, b30);
                long b32 = b26 + b25;
                long b33 = ThreefishEngine.rotlXor(b25, 17, b32);
                long b34 = b28 + b31;
                long b35 = ThreefishEngine.rotlXor(b31, 49, b34);
                long b36 = b30 + b29;
                long b37 = ThreefishEngine.rotlXor(b29, 36, b36);
                long b38 = b24 + b27;
                long b39 = ThreefishEngine.rotlXor(b27, 39, b38);
                long b40 = b34 + b33;
                long b41 = ThreefishEngine.rotlXor(b33, 44, b40);
                long b42 = b36 + b39;
                long b43 = ThreefishEngine.rotlXor(b39, 9, b42);
                long b44 = b38 + b37;
                long b45 = ThreefishEngine.rotlXor(b37, 54, b44);
                long b46 = b32 + b35;
                long b47 = ThreefishEngine.rotlXor(b35, 56, b46);
                long b48 = b42 + kw[dm9];
                long b49 = b41 + kw[dm9 + 1];
                long b50 = b44 + kw[dm9 + 2];
                long b51 = b47 + kw[dm9 + 3];
                long b52 = b46 + kw[dm9 + 4];
                long b53 = b45 + kw[dm9 + 5] + t[dm3];
                long b54 = b40 + kw[dm9 + 6] + t[dm3 + 1];
                long b55 = b43 + kw[dm9 + 7] + ((long) d);
                long b56 = b48 + b49;
                long b57 = ThreefishEngine.rotlXor(b49, 39, b56);
                long b58 = b50 + b51;
                long b59 = ThreefishEngine.rotlXor(b51, 30, b58);
                long b60 = b52 + b53;
                long b61 = ThreefishEngine.rotlXor(b53, 34, b60);
                long b62 = b54 + b55;
                long b63 = ThreefishEngine.rotlXor(b55, 24, b62);
                long b64 = b58 + b57;
                long b65 = ThreefishEngine.rotlXor(b57, 13, b64);
                long b66 = b60 + b63;
                long b67 = ThreefishEngine.rotlXor(b63, 50, b66);
                long b68 = b62 + b61;
                long b69 = ThreefishEngine.rotlXor(b61, 10, b68);
                long b70 = b56 + b59;
                long b71 = ThreefishEngine.rotlXor(b59, 17, b70);
                long b72 = b66 + b65;
                long b73 = ThreefishEngine.rotlXor(b65, 25, b72);
                long b74 = b68 + b71;
                long b75 = ThreefishEngine.rotlXor(b71, 29, b74);
                long b76 = b70 + b69;
                long b77 = ThreefishEngine.rotlXor(b69, 39, b76);
                long b78 = b64 + b67;
                long b79 = ThreefishEngine.rotlXor(b67, 43, b78);
                long b80 = b74 + b73;
                long b81 = ThreefishEngine.rotlXor(b73, 8, b80);
                long b82 = b76 + b79;
                long b83 = ThreefishEngine.rotlXor(b79, 35, b82);
                long b84 = b78 + b77;
                long b85 = ThreefishEngine.rotlXor(b77, 56, b84);
                long b86 = b72 + b75;
                long b87 = ThreefishEngine.rotlXor(b75, 22, b86);
                b8 = b82 + kw[dm9 + 1];
                b9 = b81 + kw[dm9 + 2];
                b10 = b84 + kw[dm9 + 3];
                b11 = b87 + kw[dm9 + 4];
                b12 = b86 + kw[dm9 + 5];
                b13 = b85 + kw[dm9 + 6] + t[dm3 + 1];
                b14 = b80 + kw[dm9 + 7] + t[dm3 + 2];
                b15 = b83 + kw[dm9 + 8] + ((long) d) + 1;
            }
            out[0] = b8;
            out[1] = b9;
            out[2] = b10;
            out[3] = b11;
            out[4] = b12;
            out[5] = b13;
            out[6] = b14;
            out[7] = b15;
        }

        @Override // org.spongycastle.crypto.engines.ThreefishEngine.ThreefishCipher
        public void decryptBlock(long[] block, long[] state) {
            long[] kw = this.kw;
            long[] t = this.t;
            int[] mod9 = ThreefishEngine.MOD9;
            int[] mod3 = ThreefishEngine.MOD3;
            if (kw.length != 17) {
                throw new IllegalArgumentException();
            }
            if (t.length != 5) {
                throw new IllegalArgumentException();
            }
            long b0 = block[0];
            long b1 = block[1];
            long b2 = block[2];
            long b3 = block[3];
            long b4 = block[4];
            long b5 = block[5];
            long b6 = block[6];
            long b7 = block[7];
            for (int d = 17; d >= 1; d -= 2) {
                int dm9 = mod9[d];
                int dm3 = mod3[d];
                long b8 = b0 - kw[dm9 + 1];
                long b9 = b1 - kw[dm9 + 2];
                long b10 = b2 - kw[dm9 + 3];
                long b11 = b3 - kw[dm9 + 4];
                long b12 = b4 - kw[dm9 + 5];
                long b13 = b5 - (kw[dm9 + 6] + t[dm3 + 1]);
                long b14 = b6 - (kw[dm9 + 7] + t[dm3 + 2]);
                long b15 = b7 - ((kw[dm9 + 8] + ((long) d)) + 1);
                long b16 = ThreefishEngine.xorRotr(b9, 8, b14);
                long b17 = b14 - b16;
                long b18 = ThreefishEngine.xorRotr(b15, 35, b8);
                long b19 = b8 - b18;
                long b20 = ThreefishEngine.xorRotr(b13, 56, b10);
                long b21 = b10 - b20;
                long b22 = ThreefishEngine.xorRotr(b11, 22, b12);
                long b23 = b12 - b22;
                long b24 = ThreefishEngine.xorRotr(b16, 25, b23);
                long b25 = b23 - b24;
                long b26 = ThreefishEngine.xorRotr(b22, 29, b17);
                long b27 = b17 - b26;
                long b28 = ThreefishEngine.xorRotr(b20, 39, b19);
                long b29 = b19 - b28;
                long b30 = ThreefishEngine.xorRotr(b18, 43, b21);
                long b31 = b21 - b30;
                long b32 = ThreefishEngine.xorRotr(b24, 13, b31);
                long b33 = b31 - b32;
                long b34 = ThreefishEngine.xorRotr(b30, 50, b25);
                long b35 = b25 - b34;
                long b36 = ThreefishEngine.xorRotr(b28, 10, b27);
                long b37 = b27 - b36;
                long b38 = ThreefishEngine.xorRotr(b26, 17, b29);
                long b39 = b29 - b38;
                long b40 = ThreefishEngine.xorRotr(b32, 39, b39);
                long b41 = b39 - b40;
                long b42 = ThreefishEngine.xorRotr(b38, 30, b33);
                long b43 = b33 - b42;
                long b44 = ThreefishEngine.xorRotr(b36, 34, b35);
                long b45 = b35 - b44;
                long b46 = ThreefishEngine.xorRotr(b34, 24, b37);
                long b47 = b37 - b46;
                long b48 = b41 - kw[dm9];
                long b49 = b40 - kw[dm9 + 1];
                long b50 = b43 - kw[dm9 + 2];
                long b51 = b42 - kw[dm9 + 3];
                long b52 = b45 - kw[dm9 + 4];
                long b53 = b44 - (kw[dm9 + 5] + t[dm3]);
                long b54 = b47 - (kw[dm9 + 6] + t[dm3 + 1]);
                long b55 = b46 - (kw[dm9 + 7] + ((long) d));
                long b56 = ThreefishEngine.xorRotr(b49, 44, b54);
                long b57 = b54 - b56;
                long b58 = ThreefishEngine.xorRotr(b55, 9, b48);
                long b59 = b48 - b58;
                long b60 = ThreefishEngine.xorRotr(b53, 54, b50);
                long b61 = b50 - b60;
                long b62 = ThreefishEngine.xorRotr(b51, 56, b52);
                long b63 = b52 - b62;
                long b64 = ThreefishEngine.xorRotr(b56, 17, b63);
                long b65 = b63 - b64;
                long b66 = ThreefishEngine.xorRotr(b62, 49, b57);
                long b67 = b57 - b66;
                long b68 = ThreefishEngine.xorRotr(b60, 36, b59);
                long b69 = b59 - b68;
                long b70 = ThreefishEngine.xorRotr(b58, 39, b61);
                long b71 = b61 - b70;
                long b72 = ThreefishEngine.xorRotr(b64, 33, b71);
                long b73 = b71 - b72;
                long b74 = ThreefishEngine.xorRotr(b70, 27, b65);
                long b75 = b65 - b74;
                long b76 = ThreefishEngine.xorRotr(b68, 14, b67);
                long b77 = b67 - b76;
                long b78 = ThreefishEngine.xorRotr(b66, 42, b69);
                long b79 = b69 - b78;
                b1 = ThreefishEngine.xorRotr(b72, 46, b79);
                b0 = b79 - b1;
                b3 = ThreefishEngine.xorRotr(b78, 36, b73);
                b2 = b73 - b3;
                b5 = ThreefishEngine.xorRotr(b76, 19, b75);
                b4 = b75 - b5;
                b7 = ThreefishEngine.xorRotr(b74, 37, b77);
                b6 = b77 - b7;
            }
            long b80 = b0 - kw[0];
            long b81 = b1 - kw[1];
            long b82 = b2 - kw[2];
            long b83 = b3 - kw[3];
            long b84 = b4 - kw[4];
            long b85 = b5 - (kw[5] + t[0]);
            long b86 = b6 - (kw[6] + t[1]);
            long b87 = b7 - kw[7];
            state[0] = b80;
            state[1] = b81;
            state[2] = b82;
            state[3] = b83;
            state[4] = b84;
            state[5] = b85;
            state[6] = b86;
            state[7] = b87;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/ThreefishEngine$Threefish1024Cipher.class */
    private static final class Threefish1024Cipher extends ThreefishCipher {
        private static final int ROTATION_0_0 = 24;
        private static final int ROTATION_0_1 = 13;
        private static final int ROTATION_0_2 = 8;
        private static final int ROTATION_0_3 = 47;
        private static final int ROTATION_0_4 = 8;
        private static final int ROTATION_0_5 = 17;
        private static final int ROTATION_0_6 = 22;
        private static final int ROTATION_0_7 = 37;
        private static final int ROTATION_1_0 = 38;
        private static final int ROTATION_1_1 = 19;
        private static final int ROTATION_1_2 = 10;
        private static final int ROTATION_1_3 = 55;
        private static final int ROTATION_1_4 = 49;
        private static final int ROTATION_1_5 = 18;
        private static final int ROTATION_1_6 = 23;
        private static final int ROTATION_1_7 = 52;
        private static final int ROTATION_2_0 = 33;
        private static final int ROTATION_2_1 = 4;
        private static final int ROTATION_2_2 = 51;
        private static final int ROTATION_2_3 = 13;
        private static final int ROTATION_2_4 = 34;
        private static final int ROTATION_2_5 = 41;
        private static final int ROTATION_2_6 = 59;
        private static final int ROTATION_2_7 = 17;
        private static final int ROTATION_3_0 = 5;
        private static final int ROTATION_3_1 = 20;
        private static final int ROTATION_3_2 = 48;
        private static final int ROTATION_3_3 = 41;
        private static final int ROTATION_3_4 = 47;
        private static final int ROTATION_3_5 = 28;
        private static final int ROTATION_3_6 = 16;
        private static final int ROTATION_3_7 = 25;
        private static final int ROTATION_4_0 = 41;
        private static final int ROTATION_4_1 = 9;
        private static final int ROTATION_4_2 = 37;
        private static final int ROTATION_4_3 = 31;
        private static final int ROTATION_4_4 = 12;
        private static final int ROTATION_4_5 = 47;
        private static final int ROTATION_4_6 = 44;
        private static final int ROTATION_4_7 = 30;
        private static final int ROTATION_5_0 = 16;
        private static final int ROTATION_5_1 = 34;
        private static final int ROTATION_5_2 = 56;
        private static final int ROTATION_5_3 = 51;
        private static final int ROTATION_5_4 = 4;
        private static final int ROTATION_5_5 = 53;
        private static final int ROTATION_5_6 = 42;
        private static final int ROTATION_5_7 = 41;
        private static final int ROTATION_6_0 = 31;
        private static final int ROTATION_6_1 = 44;
        private static final int ROTATION_6_2 = 47;
        private static final int ROTATION_6_3 = 46;
        private static final int ROTATION_6_4 = 19;
        private static final int ROTATION_6_5 = 42;
        private static final int ROTATION_6_6 = 44;
        private static final int ROTATION_6_7 = 25;
        private static final int ROTATION_7_0 = 9;
        private static final int ROTATION_7_1 = 48;
        private static final int ROTATION_7_2 = 35;
        private static final int ROTATION_7_3 = 52;
        private static final int ROTATION_7_4 = 23;
        private static final int ROTATION_7_5 = 31;
        private static final int ROTATION_7_6 = 37;
        private static final int ROTATION_7_7 = 20;

        public Threefish1024Cipher(long[] kw, long[] t) {
            super(kw, t);
        }

        @Override // org.spongycastle.crypto.engines.ThreefishEngine.ThreefishCipher
        void encryptBlock(long[] block, long[] out) {
            long[] kw = this.kw;
            long[] t = this.t;
            int[] mod17 = ThreefishEngine.MOD17;
            int[] mod3 = ThreefishEngine.MOD3;
            if (kw.length != 33) {
                throw new IllegalArgumentException();
            }
            if (t.length != 5) {
                throw new IllegalArgumentException();
            }
            long b0 = block[0];
            long b1 = block[1];
            long b2 = block[2];
            long b3 = block[3];
            long b4 = block[4];
            long b5 = block[5];
            long b6 = block[6];
            long b7 = block[7];
            long b8 = block[8];
            long b9 = block[9];
            long b10 = block[10];
            long b11 = block[11];
            long b12 = block[12];
            long b13 = block[13];
            long b14 = block[14];
            long b15 = block[15];
            long b16 = b0 + kw[0];
            long b17 = b1 + kw[1];
            long b18 = b2 + kw[2];
            long b19 = b3 + kw[3];
            long b20 = b4 + kw[4];
            long b21 = b5 + kw[5];
            long b22 = b6 + kw[6];
            long b23 = b7 + kw[7];
            long b24 = b8 + kw[8];
            long b25 = b9 + kw[9];
            long b110 = b10 + kw[10];
            long b111 = b11 + kw[11];
            long b112 = b12 + kw[12];
            long b113 = b13 + kw[13] + t[0];
            long b114 = b14 + kw[14] + t[1];
            long b115 = b15 + kw[15];
            for (int d = 1; d < 20; d += 2) {
                int dm17 = mod17[d];
                int dm3 = mod3[d];
                long b26 = b17;
                long b27 = ThreefishEngine.rotlXor(b26, 24, b16 + b17);
                long b28 = b18 + b19;
                long b29 = ThreefishEngine.rotlXor(b19, 13, b28);
                long b30 = b20 + b21;
                long b31 = ThreefishEngine.rotlXor(b21, 8, b30);
                long b32 = b22 + b23;
                long b33 = ThreefishEngine.rotlXor(b23, 47, b32);
                long b34 = b24 + b25;
                long b35 = ThreefishEngine.rotlXor(b25, 8, b34);
                long b116 = b110 + b111;
                long b117 = ThreefishEngine.rotlXor(b111, 17, b116);
                long b118 = b112 + b113;
                long b119 = ThreefishEngine.rotlXor(b113, 22, b118);
                long b120 = b114 + b115;
                long b121 = ThreefishEngine.rotlXor(b115, 37, b120);
                long b36 = b26 + b35;
                long b37 = ThreefishEngine.rotlXor(b35, 38, b36);
                long b38 = b28 + b119;
                long b122 = ThreefishEngine.rotlXor(b119, 19, b38);
                long b39 = b32 + b117;
                long b123 = ThreefishEngine.rotlXor(b117, 10, b39);
                long b40 = b30 + b121;
                long b124 = ThreefishEngine.rotlXor(b121, 55, b40);
                long b125 = b116 + b33;
                long b41 = ThreefishEngine.rotlXor(b33, 49, b125);
                long b126 = b118 + b29;
                long b42 = ThreefishEngine.rotlXor(b29, 18, b126);
                long b127 = b120 + b31;
                long b43 = ThreefishEngine.rotlXor(b31, 23, b127);
                long b44 = b34 + b27;
                long b45 = ThreefishEngine.rotlXor(b27, 52, b44);
                long b46 = b36 + b41;
                long b47 = ThreefishEngine.rotlXor(b41, 33, b46);
                long b48 = b38 + b43;
                long b49 = ThreefishEngine.rotlXor(b43, 4, b48);
                long b50 = b40 + b42;
                long b51 = ThreefishEngine.rotlXor(b42, 51, b50);
                long b52 = b39 + b45;
                long b53 = ThreefishEngine.rotlXor(b45, 13, b52);
                long b128 = b126 + b124;
                long b129 = ThreefishEngine.rotlXor(b124, 34, b128);
                long b130 = b127 + b122;
                long b131 = ThreefishEngine.rotlXor(b122, 41, b130);
                long b54 = b44 + b123;
                long b132 = ThreefishEngine.rotlXor(b123, 59, b54);
                long b133 = b125 + b37;
                long b55 = ThreefishEngine.rotlXor(b37, 17, b133);
                long b56 = b46 + b129;
                long b134 = ThreefishEngine.rotlXor(b129, 5, b56);
                long b57 = b48 + b132;
                long b135 = ThreefishEngine.rotlXor(b132, 20, b57);
                long b58 = b52 + b131;
                long b136 = ThreefishEngine.rotlXor(b131, 48, b58);
                long b59 = b50 + b55;
                long b60 = ThreefishEngine.rotlXor(b55, 41, b59);
                long b137 = b130 + b53;
                long b61 = ThreefishEngine.rotlXor(b53, 47, b137);
                long b62 = b54 + b49;
                long b63 = ThreefishEngine.rotlXor(b49, 28, b62);
                long b138 = b133 + b51;
                long b64 = ThreefishEngine.rotlXor(b51, 16, b138);
                long b139 = b128 + b47;
                long b65 = ThreefishEngine.rotlXor(b47, 25, b139);
                long b66 = b56 + kw[dm17];
                long b67 = b61 + kw[dm17 + 1];
                long b68 = b57 + kw[dm17 + 2];
                long b69 = b64 + kw[dm17 + 3];
                long b70 = b59 + kw[dm17 + 4];
                long b71 = b63 + kw[dm17 + 5];
                long b72 = b58 + kw[dm17 + 6];
                long b73 = b65 + kw[dm17 + 7];
                long b74 = b62 + kw[dm17 + 8];
                long b75 = b60 + kw[dm17 + 9];
                long b140 = b138 + kw[dm17 + 10];
                long b141 = b135 + kw[dm17 + 11];
                long b142 = b139 + kw[dm17 + 12];
                long b143 = b136 + kw[dm17 + 13] + t[dm3];
                long b144 = b137 + kw[dm17 + 14] + t[dm3 + 1];
                long b145 = b134 + kw[dm17 + 15] + ((long) d);
                long b76 = b66 + b67;
                long b77 = ThreefishEngine.rotlXor(b67, 41, b76);
                long b78 = b68 + b69;
                long b79 = ThreefishEngine.rotlXor(b69, 9, b78);
                long b80 = b70 + b71;
                long b81 = ThreefishEngine.rotlXor(b71, 37, b80);
                long b82 = b72 + b73;
                long b83 = ThreefishEngine.rotlXor(b73, 31, b82);
                long b84 = b74 + b75;
                long b85 = ThreefishEngine.rotlXor(b75, 12, b84);
                long b146 = b140 + b141;
                long b147 = ThreefishEngine.rotlXor(b141, 47, b146);
                long b148 = b142 + b143;
                long b149 = ThreefishEngine.rotlXor(b143, 44, b148);
                long b150 = b144 + b145;
                long b151 = ThreefishEngine.rotlXor(b145, 30, b150);
                long b86 = b76 + b85;
                long b87 = ThreefishEngine.rotlXor(b85, 16, b86);
                long b88 = b78 + b149;
                long b152 = ThreefishEngine.rotlXor(b149, 34, b88);
                long b89 = b82 + b147;
                long b153 = ThreefishEngine.rotlXor(b147, 56, b89);
                long b90 = b80 + b151;
                long b154 = ThreefishEngine.rotlXor(b151, 51, b90);
                long b155 = b146 + b83;
                long b91 = ThreefishEngine.rotlXor(b83, 4, b155);
                long b156 = b148 + b79;
                long b92 = ThreefishEngine.rotlXor(b79, 53, b156);
                long b157 = b150 + b81;
                long b93 = ThreefishEngine.rotlXor(b81, 42, b157);
                long b94 = b84 + b77;
                long b95 = ThreefishEngine.rotlXor(b77, 41, b94);
                long b96 = b86 + b91;
                long b97 = ThreefishEngine.rotlXor(b91, 31, b96);
                long b98 = b88 + b93;
                long b99 = ThreefishEngine.rotlXor(b93, 44, b98);
                long b100 = b90 + b92;
                long b101 = ThreefishEngine.rotlXor(b92, 47, b100);
                long b102 = b89 + b95;
                long b103 = ThreefishEngine.rotlXor(b95, 46, b102);
                long b158 = b156 + b154;
                long b159 = ThreefishEngine.rotlXor(b154, 19, b158);
                long b160 = b157 + b152;
                long b161 = ThreefishEngine.rotlXor(b152, 42, b160);
                long b104 = b94 + b153;
                long b162 = ThreefishEngine.rotlXor(b153, 44, b104);
                long b163 = b155 + b87;
                long b105 = ThreefishEngine.rotlXor(b87, 25, b163);
                long b106 = b96 + b159;
                long b164 = ThreefishEngine.rotlXor(b159, 9, b106);
                long b107 = b98 + b162;
                long b165 = ThreefishEngine.rotlXor(b162, 48, b107);
                long b108 = b102 + b161;
                long b166 = ThreefishEngine.rotlXor(b161, 35, b108);
                long b109 = b100 + b105;
                long b167 = ThreefishEngine.rotlXor(b105, 52, b109);
                long b168 = b160 + b103;
                long b169 = ThreefishEngine.rotlXor(b103, 23, b168);
                long b170 = b104 + b99;
                long b171 = ThreefishEngine.rotlXor(b99, 31, b170);
                long b172 = b163 + b101;
                long b173 = ThreefishEngine.rotlXor(b101, 37, b172);
                long b174 = b158 + b97;
                long b175 = ThreefishEngine.rotlXor(b97, 20, b174);
                b16 = b106 + kw[dm17 + 1];
                b17 = b169 + kw[dm17 + 2];
                b18 = b107 + kw[dm17 + 3];
                b19 = b173 + kw[dm17 + 4];
                b20 = b109 + kw[dm17 + 5];
                b21 = b171 + kw[dm17 + 6];
                b22 = b108 + kw[dm17 + 7];
                b23 = b175 + kw[dm17 + 8];
                b24 = b170 + kw[dm17 + 9];
                b25 = b167 + kw[dm17 + 10];
                b110 = b172 + kw[dm17 + 11];
                b111 = b165 + kw[dm17 + 12];
                b112 = b174 + kw[dm17 + 13];
                b113 = b166 + kw[dm17 + 14] + t[dm3 + 1];
                b114 = b168 + kw[dm17 + 15] + t[dm3 + 2];
                b115 = b164 + kw[dm17 + 16] + ((long) d) + 1;
            }
            out[0] = b16;
            out[1] = b17;
            out[2] = b18;
            out[3] = b19;
            out[4] = b20;
            out[5] = b21;
            out[6] = b22;
            out[7] = b23;
            out[8] = b24;
            out[9] = b25;
            out[10] = b110;
            out[11] = b111;
            out[12] = b112;
            out[13] = b113;
            out[14] = b114;
            out[15] = b115;
        }

        @Override // org.spongycastle.crypto.engines.ThreefishEngine.ThreefishCipher
        void decryptBlock(long[] block, long[] state) {
            long[] kw = this.kw;
            long[] t = this.t;
            int[] mod17 = ThreefishEngine.MOD17;
            int[] mod3 = ThreefishEngine.MOD3;
            if (kw.length != 33) {
                throw new IllegalArgumentException();
            }
            if (t.length != 5) {
                throw new IllegalArgumentException();
            }
            long b0 = block[0];
            long b1 = block[1];
            long b2 = block[2];
            long b3 = block[3];
            long b4 = block[4];
            long b5 = block[5];
            long b6 = block[6];
            long b7 = block[7];
            long b8 = block[8];
            long b9 = block[9];
            long b10 = block[10];
            long b11 = block[11];
            long b12 = block[12];
            long b13 = block[13];
            long b14 = block[14];
            long b15 = block[15];
            for (int d = 19; d >= 1; d -= 2) {
                int dm17 = mod17[d];
                int dm3 = mod3[d];
                long b16 = b0 - kw[dm17 + 1];
                long b17 = b1 - kw[dm17 + 2];
                long b18 = b2 - kw[dm17 + 3];
                long b19 = b3 - kw[dm17 + 4];
                long b20 = b4 - kw[dm17 + 5];
                long b21 = b5 - kw[dm17 + 6];
                long b22 = b6 - kw[dm17 + 7];
                long b23 = b7 - kw[dm17 + 8];
                long b24 = b8 - kw[dm17 + 9];
                long b25 = b9 - kw[dm17 + 10];
                long b110 = b10 - kw[dm17 + 11];
                long b111 = b11 - kw[dm17 + 12];
                long b112 = b12 - kw[dm17 + 13];
                long b113 = b13 - (kw[dm17 + 14] + t[dm3 + 1]);
                long b114 = b14 - (kw[dm17 + 15] + t[dm3 + 2]);
                long b115 = ThreefishEngine.xorRotr(b15 - ((kw[dm17 + 16] + ((long) d)) + 1), 9, b16);
                long b26 = b16 - b115;
                long b116 = ThreefishEngine.xorRotr(b111, 48, b18);
                long b27 = b18 - b116;
                long b117 = ThreefishEngine.xorRotr(b113, 35, b22);
                long b28 = b22 - b117;
                long b29 = ThreefishEngine.xorRotr(b25, 52, b20);
                long b30 = b20 - b29;
                long b31 = ThreefishEngine.xorRotr(b17, 23, b114);
                long b118 = b114 - b31;
                long b32 = ThreefishEngine.xorRotr(b21, 31, b24);
                long b33 = b24 - b32;
                long b34 = ThreefishEngine.xorRotr(b19, 37, b110);
                long b119 = b110 - b34;
                long b35 = ThreefishEngine.xorRotr(b23, 20, b112);
                long b120 = b112 - b35;
                long b36 = ThreefishEngine.xorRotr(b35, 31, b26);
                long b37 = b26 - b36;
                long b38 = ThreefishEngine.xorRotr(b32, 44, b27);
                long b39 = b27 - b38;
                long b40 = ThreefishEngine.xorRotr(b34, 47, b30);
                long b41 = b30 - b40;
                long b42 = ThreefishEngine.xorRotr(b31, 46, b28);
                long b43 = b28 - b42;
                long b121 = ThreefishEngine.xorRotr(b115, 19, b120);
                long b122 = b120 - b121;
                long b123 = ThreefishEngine.xorRotr(b117, 42, b118);
                long b124 = b118 - b123;
                long b125 = ThreefishEngine.xorRotr(b116, 44, b33);
                long b44 = b33 - b125;
                long b45 = ThreefishEngine.xorRotr(b29, 25, b119);
                long b126 = b119 - b45;
                long b46 = ThreefishEngine.xorRotr(b45, 16, b37);
                long b47 = b37 - b46;
                long b127 = ThreefishEngine.xorRotr(b123, 34, b39);
                long b48 = b39 - b127;
                long b128 = ThreefishEngine.xorRotr(b125, 56, b43);
                long b49 = b43 - b128;
                long b129 = ThreefishEngine.xorRotr(b121, 51, b41);
                long b50 = b41 - b129;
                long b51 = ThreefishEngine.xorRotr(b36, 4, b126);
                long b130 = b126 - b51;
                long b52 = ThreefishEngine.xorRotr(b40, 53, b122);
                long b131 = b122 - b52;
                long b53 = ThreefishEngine.xorRotr(b38, 42, b124);
                long b132 = b124 - b53;
                long b54 = ThreefishEngine.xorRotr(b42, 41, b44);
                long b55 = b44 - b54;
                long b56 = ThreefishEngine.xorRotr(b54, 41, b47);
                long b57 = b47 - b56;
                long b58 = ThreefishEngine.xorRotr(b52, 9, b48);
                long b59 = b48 - b58;
                long b60 = ThreefishEngine.xorRotr(b53, 37, b50);
                long b61 = b50 - b60;
                long b62 = ThreefishEngine.xorRotr(b51, 31, b49);
                long b63 = b49 - b62;
                long b64 = ThreefishEngine.xorRotr(b46, 12, b55);
                long b65 = b55 - b64;
                long b133 = ThreefishEngine.xorRotr(b128, 47, b130);
                long b134 = b130 - b133;
                long b135 = ThreefishEngine.xorRotr(b127, 44, b131);
                long b136 = b131 - b135;
                long b137 = ThreefishEngine.xorRotr(b129, 30, b132);
                long b138 = b132 - b137;
                long b66 = b57 - kw[dm17];
                long b67 = b56 - kw[dm17 + 1];
                long b68 = b59 - kw[dm17 + 2];
                long b69 = b58 - kw[dm17 + 3];
                long b70 = b61 - kw[dm17 + 4];
                long b71 = b60 - kw[dm17 + 5];
                long b72 = b63 - kw[dm17 + 6];
                long b73 = b62 - kw[dm17 + 7];
                long b74 = b65 - kw[dm17 + 8];
                long b75 = b64 - kw[dm17 + 9];
                long b139 = b134 - kw[dm17 + 10];
                long b140 = b133 - kw[dm17 + 11];
                long b141 = b136 - kw[dm17 + 12];
                long b142 = b135 - (kw[dm17 + 13] + t[dm3]);
                long b143 = b138 - (kw[dm17 + 14] + t[dm3 + 1]);
                long b144 = ThreefishEngine.xorRotr(b137 - (kw[dm17 + 15] + ((long) d)), 5, b66);
                long b76 = b66 - b144;
                long b145 = ThreefishEngine.xorRotr(b140, 20, b68);
                long b77 = b68 - b145;
                long b146 = ThreefishEngine.xorRotr(b142, 48, b72);
                long b78 = b72 - b146;
                long b79 = ThreefishEngine.xorRotr(b75, 41, b70);
                long b80 = b70 - b79;
                long b81 = ThreefishEngine.xorRotr(b67, 47, b143);
                long b147 = b143 - b81;
                long b82 = ThreefishEngine.xorRotr(b71, 28, b74);
                long b83 = b74 - b82;
                long b84 = ThreefishEngine.xorRotr(b69, 16, b139);
                long b148 = b139 - b84;
                long b85 = ThreefishEngine.xorRotr(b73, 25, b141);
                long b149 = b141 - b85;
                long b86 = ThreefishEngine.xorRotr(b85, 33, b76);
                long b87 = b76 - b86;
                long b88 = ThreefishEngine.xorRotr(b82, 4, b77);
                long b89 = b77 - b88;
                long b90 = ThreefishEngine.xorRotr(b84, 51, b80);
                long b91 = b80 - b90;
                long b92 = ThreefishEngine.xorRotr(b81, 13, b78);
                long b93 = b78 - b92;
                long b150 = ThreefishEngine.xorRotr(b144, 34, b149);
                long b151 = b149 - b150;
                long b152 = ThreefishEngine.xorRotr(b146, 41, b147);
                long b153 = b147 - b152;
                long b154 = ThreefishEngine.xorRotr(b145, 59, b83);
                long b94 = b83 - b154;
                long b95 = ThreefishEngine.xorRotr(b79, 17, b148);
                long b155 = b148 - b95;
                long b96 = ThreefishEngine.xorRotr(b95, 38, b87);
                long b97 = b87 - b96;
                long b156 = ThreefishEngine.xorRotr(b152, 19, b89);
                long b98 = b89 - b156;
                long b157 = ThreefishEngine.xorRotr(b154, 10, b93);
                long b99 = b93 - b157;
                long b158 = ThreefishEngine.xorRotr(b150, 55, b91);
                long b100 = b91 - b158;
                long b101 = ThreefishEngine.xorRotr(b86, 49, b155);
                long b159 = b155 - b101;
                long b102 = ThreefishEngine.xorRotr(b90, 18, b151);
                long b160 = b151 - b102;
                long b103 = ThreefishEngine.xorRotr(b88, 23, b153);
                long b161 = b153 - b103;
                long b104 = ThreefishEngine.xorRotr(b92, 52, b94);
                long b105 = b94 - b104;
                b1 = ThreefishEngine.xorRotr(b104, 24, b97);
                b0 = b97 - b1;
                b3 = ThreefishEngine.xorRotr(b102, 13, b98);
                b2 = b98 - b3;
                b5 = ThreefishEngine.xorRotr(b103, 8, b100);
                b4 = b100 - b5;
                b7 = ThreefishEngine.xorRotr(b101, 47, b99);
                b6 = b99 - b7;
                b9 = ThreefishEngine.xorRotr(b96, 8, b105);
                b8 = b105 - b9;
                b11 = ThreefishEngine.xorRotr(b157, 17, b159);
                b10 = b159 - b11;
                b13 = ThreefishEngine.xorRotr(b156, 22, b160);
                b12 = b160 - b13;
                b15 = ThreefishEngine.xorRotr(b158, 37, b161);
                b14 = b161 - b15;
            }
            long b106 = b0 - kw[0];
            long b107 = b1 - kw[1];
            long b108 = b2 - kw[2];
            long b109 = b3 - kw[3];
            long b162 = b4 - kw[4];
            long b163 = b5 - kw[5];
            long b164 = b6 - kw[6];
            long b165 = b7 - kw[7];
            long b166 = b8 - kw[8];
            long b167 = b9 - kw[9];
            long b168 = b10 - kw[10];
            long b169 = b11 - kw[11];
            long b170 = b12 - kw[12];
            long b171 = b13 - (kw[13] + t[0]);
            long b172 = b14 - (kw[14] + t[1]);
            long b173 = b15 - kw[15];
            state[0] = b106;
            state[1] = b107;
            state[2] = b108;
            state[3] = b109;
            state[4] = b162;
            state[5] = b163;
            state[6] = b164;
            state[7] = b165;
            state[8] = b166;
            state[9] = b167;
            state[10] = b168;
            state[11] = b169;
            state[12] = b170;
            state[13] = b171;
            state[14] = b172;
            state[15] = b173;
        }
    }
}
