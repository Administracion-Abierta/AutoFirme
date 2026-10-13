package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.params.KeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/Shacal2Engine.class */
public class Shacal2Engine implements BlockCipher {
    private static final int[] K = {1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, -1866530822, -1538233109, -1090935817, -965641998};
    private static final int BLOCK_SIZE = 32;
    private static final int ROUNDS = 64;
    private boolean forEncryption = false;
    private int[] workingKey = null;

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "Shacal2";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 32;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean _forEncryption, CipherParameters params) throws IllegalArgumentException {
        if (!(params instanceof KeyParameter)) {
            throw new IllegalArgumentException("only simple KeyParameter expected.");
        }
        this.forEncryption = _forEncryption;
        this.workingKey = new int[64];
        setKey(((KeyParameter) params).getKey());
    }

    public void setKey(byte[] kb) {
        if (kb.length == 0 || kb.length > 64 || kb.length < 16 || kb.length % 8 != 0) {
            throw new IllegalArgumentException("Shacal2-key must be 16 - 64 bytes and multiple of 8");
        }
        bytes2ints(kb, this.workingKey, 0, 0);
        for (int i = 16; i < 64; i++) {
            this.workingKey[i] = ((((this.workingKey[i - 2] >>> 17) | (this.workingKey[i - 2] << (-17))) ^ ((this.workingKey[i - 2] >>> 19) | (this.workingKey[i - 2] << (-19)))) ^ (this.workingKey[i - 2] >>> 10)) + this.workingKey[i - 7] + ((((this.workingKey[i - 15] >>> 7) | (this.workingKey[i - 15] << (-7))) ^ ((this.workingKey[i - 15] >>> 18) | (this.workingKey[i - 15] << (-18)))) ^ (this.workingKey[i - 15] >>> 3)) + this.workingKey[i - 16];
        }
    }

    private void encryptBlock(byte[] in, int inOffset, byte[] out, int outOffset) {
        int[] block = new int[8];
        byteBlockToInts(in, block, inOffset, 0);
        for (int i = 0; i < 64; i++) {
            int tmp = ((((block[4] >>> 6) | (block[4] << (-6))) ^ ((block[4] >>> 11) | (block[4] << (-11)))) ^ ((block[4] >>> 25) | (block[4] << (-25)))) + ((block[4] & block[5]) ^ ((block[4] ^ (-1)) & block[6])) + block[7] + K[i] + this.workingKey[i];
            block[7] = block[6];
            block[6] = block[5];
            block[5] = block[4];
            block[4] = block[3] + tmp;
            block[3] = block[2];
            block[2] = block[1];
            block[1] = block[0];
            block[0] = tmp + ((((block[0] >>> 2) | (block[0] << (-2))) ^ ((block[0] >>> 13) | (block[0] << (-13)))) ^ ((block[0] >>> 22) | (block[0] << (-22)))) + (((block[0] & block[2]) ^ (block[0] & block[3])) ^ (block[2] & block[3]));
        }
        ints2bytes(block, out, outOffset);
    }

    private void decryptBlock(byte[] in, int inOffset, byte[] out, int outOffset) {
        int[] block = new int[8];
        byteBlockToInts(in, block, inOffset, 0);
        for (int i = 63; i > -1; i--) {
            int tmp = (block[0] - ((((block[1] >>> 2) | (block[1] << (-2))) ^ ((block[1] >>> 13) | (block[1] << (-13)))) ^ ((block[1] >>> 22) | (block[1] << (-22))))) - (((block[1] & block[2]) ^ (block[1] & block[3])) ^ (block[2] & block[3]));
            block[0] = block[1];
            block[1] = block[2];
            block[2] = block[3];
            block[3] = block[4] - tmp;
            block[4] = block[5];
            block[5] = block[6];
            block[6] = block[7];
            block[7] = (((tmp - K[i]) - this.workingKey[i]) - ((((block[4] >>> 6) | (block[4] << (-6))) ^ ((block[4] >>> 11) | (block[4] << (-11)))) ^ ((block[4] >>> 25) | (block[4] << (-25))))) - ((block[4] & block[5]) ^ ((block[4] ^ (-1)) & block[6]));
        }
        ints2bytes(block, out, outOffset);
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOffset, byte[] out, int outOffset) throws IllegalStateException, DataLengthException {
        if (this.workingKey == null) {
            throw new IllegalStateException("Shacal2 not initialised");
        }
        if (inOffset + 32 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOffset + 32 > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.forEncryption) {
            encryptBlock(in, inOffset, out, outOffset);
            return 32;
        }
        decryptBlock(in, inOffset, out, outOffset);
        return 32;
    }

    private void byteBlockToInts(byte[] bytes, int[] block, int bytesPos, int blockPos) {
        for (int i = blockPos; i < 8; i++) {
            int i2 = bytesPos;
            int bytesPos2 = bytesPos + 1;
            int bytesPos3 = bytesPos2 + 1;
            int i3 = ((bytes[i2] & 255) << 24) | ((bytes[bytesPos2] & 255) << 16);
            int bytesPos4 = bytesPos3 + 1;
            int i4 = i3 | ((bytes[bytesPos3] & 255) << 8);
            bytesPos = bytesPos4 + 1;
            block[i] = i4 | (bytes[bytesPos4] & 255);
        }
    }

    private void bytes2ints(byte[] bytes, int[] block, int bytesPos, int blockPos) {
        for (int i = blockPos; i < bytes.length / 4; i++) {
            int i2 = bytesPos;
            int bytesPos2 = bytesPos + 1;
            int bytesPos3 = bytesPos2 + 1;
            int i3 = ((bytes[i2] & 255) << 24) | ((bytes[bytesPos2] & 255) << 16);
            int bytesPos4 = bytesPos3 + 1;
            int i4 = i3 | ((bytes[bytesPos3] & 255) << 8);
            bytesPos = bytesPos4 + 1;
            block[i] = i4 | (bytes[bytesPos4] & 255);
        }
    }

    private void ints2bytes(int[] block, byte[] out, int pos) {
        for (int i = 0; i < block.length; i++) {
            int i2 = pos;
            int pos2 = pos + 1;
            out[i2] = (byte) (block[i] >>> 24);
            int pos3 = pos2 + 1;
            out[pos2] = (byte) (block[i] >>> 16);
            int pos4 = pos3 + 1;
            out[pos3] = (byte) (block[i] >>> 8);
            pos = pos4 + 1;
            out[pos4] = (byte) block[i];
        }
    }
}
