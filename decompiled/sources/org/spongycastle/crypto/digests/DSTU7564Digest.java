package org.spongycastle.crypto.digests;

import org.spongycastle.crypto.ExtendedDigest;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Memoable;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/digests/DSTU7564Digest.class */
public class DSTU7564Digest implements ExtendedDigest, Memoable {
    private static final int ROWS = 8;
    private static final int REDUCTIONAL_POLYNOMIAL = 285;
    private static final int BITS_IN_BYTE = 8;
    private static final int NB_512 = 8;
    private static final int NB_1024 = 16;
    private static final int NR_512 = 10;
    private static final int NR_1024 = 14;
    private static final int STATE_BYTES_SIZE_512 = 64;
    private static final int STATE_BYTES_SIZE_1024 = 128;
    private int hashSize;
    private int blockSize;
    private int columns;
    private int rounds;
    private byte[] padded;
    private byte[][] state;
    private byte[][] tempState1;
    private byte[][] tempState2;
    private byte[] tempBuffer;
    private byte[] mixColumnsResult;
    private long[] tempLongBuffer;
    private long inputLength;
    private int bufOff;
    private byte[] buf;
    private static final byte[][] mds_matrix = {new byte[]{1, 1, 5, 1, 8, 6, 7, 4}, new byte[]{4, 1, 1, 5, 1, 8, 6, 7}, new byte[]{7, 4, 1, 1, 5, 1, 8, 6}, new byte[]{6, 7, 4, 1, 1, 5, 1, 8}, new byte[]{8, 6, 7, 4, 1, 1, 5, 1}, new byte[]{1, 8, 6, 7, 4, 1, 1, 5}, new byte[]{5, 1, 8, 6, 7, 4, 1, 1}, new byte[]{1, 5, 1, 8, 6, 7, 4, 1}};
    private static final byte[][] sBoxes = {new byte[]{-88, 67, 95, 6, 107, 117, 108, 89, 113, -33, -121, -107, 23, -16, -40, 9, 109, -13, 29, -53, -55, 77, 44, -81, 121, -32, -105, -3, 111, 75, 69, 57, 62, -35, -93, 79, -76, -74, -102, 14, 31, -65, 21, -31, 73, -46, -109, -58, -110, 114, -98, 97, -47, 99, -6, -18, -12, 25, -43, -83, 88, -92, -69, -95, -36, -14, -125, 55, 66, -28, 122, 50, -100, -52, -85, 74, -113, 110, 4, 39, 46, -25, -30, 90, -106, 22, 35, 43, -62, 101, 102, 15, -68, -87, 71, 65, 52, 72, -4, -73, 106, -120, -91, 83, -122, -7, 91, -37, 56, 123, -61, 30, 34, 51, 36, 40, 54, -57, -78, 59, -114, 119, -70, -11, 20, -97, 8, 85, -101, 76, -2, 96, 92, -38, 24, 70, -51, 125, 33, -80, 63, 27, -119, -1, -21, -124, 105, 58, -99, -41, -45, 112, 103, 64, -75, -34, 93, 48, -111, -79, 120, 17, 1, -27, 0, 104, -104, -96, -59, 2, -90, 116, 45, 11, -94, 118, -77, -66, -50, -67, -82, -23, -118, 49, 28, -20, -15, -103, -108, -86, -10, 38, 47, -17, -24, -116, 53, 3, -44, 127, -5, 5, -63, 94, -112, 32, 61, -126, -9, -22, 10, 13, 126, -8, 80, 26, -60, 7, 87, -72, 60, 98, -29, -56, -84, 82, 100, 16, -48, -39, 19, 12, 18, 41, 81, -71, -49, -42, 115, -115, -127, 84, -64, -19, 78, 68, -89, 42, -123, 37, -26, -54, 124, -117, 86, -128}, new byte[]{-50, -69, -21, -110, -22, -53, 19, -63, -23, 58, -42, -78, -46, -112, 23, -8, 66, 21, 86, -76, 101, 28, -120, 67, -59, 92, 54, -70, -11, 87, 103, -115, 49, -10, 100, 88, -98, -12, 34, -86, 117, 15, 2, -79, -33, 109, 115, 77, 124, 38, 46, -9, 8, 93, 68, 62, -97, 20, -56, -82, 84, 16, -40, -68, 26, 107, 105, -13, -67, 51, -85, -6, -47, -101, 104, 78, 22, -107, -111, -18, 76, 99, -114, 91, -52, 60, 25, -95, -127, 73, 123, -39, 111, 55, 96, -54, -25, 43, 72, -3, -106, 69, -4, 65, 18, 13, 121, -27, -119, -116, -29, 32, 48, -36, -73, 108, 74, -75, 63, -105, -44, 98, 45, 6, -92, -91, -125, 95, 42, -38, -55, 0, 126, -94, 85, -65, 17, -43, -100, -49, 14, 10, 61, 81, 125, -109, 27, -2, -60, 71, 9, -122, 11, -113, -99, 106, 7, -71, -80, -104, 24, 50, 113, 75, -17, 59, 112, -96, -28, 64, -1, -61, -87, -26, 120, -7, -117, 70, -128, 30, 56, -31, -72, -88, -32, 12, 35, 118, 29, 37, 36, 5, -15, 110, -108, 40, -102, -124, -24, -93, 79, 119, -45, -123, -30, 82, -14, -126, 80, 122, 47, 116, 83, -77, 97, -81, 57, 53, -34, -51, 31, -103, -84, -83, 114, 44, -35, -48, -121, -66, 94, -90, -20, 4, -58, 3, 52, -5, -37, 89, -74, -62, 1, -16, 90, -19, -89, 102, 33, 127, -118, 39, -57, -64, 41, -41}, new byte[]{-109, -39, -102, -75, -104, 34, 69, -4, -70, 106, -33, 2, -97, -36, 81, 89, 74, 23, 43, -62, -108, -12, -69, -93, 98, -28, 113, -44, -51, 112, 22, -31, 73, 60, -64, -40, 92, -101, -83, -123, 83, -95, 122, -56, 45, -32, -47, 114, -90, 44, -60, -29, 118, 120, -73, -76, 9, 59, 14, 65, 76, -34, -78, -112, 37, -91, -41, 3, 17, 0, -61, 46, -110, -17, 78, 18, -99, 125, -53, 53, 16, -43, 79, -98, 77, -87, 85, -58, -48, 123, 24, -105, -45, 54, -26, 72, 86, -127, -113, 119, -52, -100, -71, -30, -84, -72, 47, 21, -92, 124, -38, 56, 30, 11, 5, -42, 20, 110, 108, 126, 102, -3, -79, -27, 96, -81, 94, 51, -121, -55, -16, 93, 109, 63, -120, -115, -57, -9, 29, -23, -20, -19, -128, 41, 39, -49, -103, -88, 80, 15, 55, 36, 40, 48, -107, -46, 62, 91, 64, -125, -77, 105, 87, 31, 7, 28, -118, -68, 32, -21, -50, -114, -85, -18, 49, -94, 115, -7, -54, 58, 26, -5, 13, -63, -2, -6, -14, 111, -67, -106, -35, 67, 82, -74, 8, -13, -82, -66, 25, -119, 50, 38, -80, -22, 75, 100, -124, -126, 107, -11, 121, -65, 1, 95, 117, 99, 27, 35, 61, 104, 42, 101, -24, -111, -10, -1, 19, 88, -15, 71, 10, 127, -59, -89, -25, 97, 90, 6, 70, 68, 66, 4, -96, -37, 57, -122, 84, -86, -116, 52, 33, -117, -8, 12, 116, 103}, new byte[]{104, -115, -54, 77, 115, 75, 78, 42, -44, 82, 38, -77, 84, 30, 25, 31, 34, 3, 70, 61, 45, 74, 83, -125, 19, -118, -73, -43, 37, 121, -11, -67, 88, 47, 13, 2, -19, 81, -98, 17, -14, 62, 85, 94, -47, 22, 60, 102, 112, 93, -13, 69, 64, -52, -24, -108, 86, 8, -50, 26, 58, -46, -31, -33, -75, 56, 110, 14, -27, -12, -7, -122, -23, 79, -42, -123, 35, -49, 50, -103, 49, 20, -82, -18, -56, 72, -45, 48, -95, -110, 65, -79, 24, -60, 44, 113, 114, 68, 21, -3, 55, -66, 95, -86, -101, -120, -40, -85, -119, -100, -6, 96, -22, -68, 98, 12, 36, -90, -88, -20, 103, 32, -37, 124, 40, -35, -84, 91, 52, 126, 16, -15, 123, -113, 99, -96, 5, -102, 67, 119, 33, -65, 39, 9, -61, -97, -74, -41, 41, -62, -21, -64, -92, -117, -116, 29, -5, -1, -63, -78, -105, 46, -8, 101, -10, 117, 7, 4, 73, 51, -28, -39, -71, -48, 66, -57, 108, -112, 0, -114, 111, 80, 1, -59, -38, 71, 63, -51, 105, -94, -30, 122, -89, -58, -109, 15, 10, 6, -26, 43, -106, -93, 28, -81, 106, 18, -124, 57, -25, -80, -126, -9, -2, -99, -121, 92, -127, 53, -34, -76, -91, -4, -128, -17, -53, -69, 107, 118, -70, 90, 125, 120, 11, -107, -29, -83, 116, -104, 59, 54, 100, 109, -36, -16, 89, -87, 76, 23, 127, -111, -72, -55, 87, 27, -32, 97}};

    public DSTU7564Digest(DSTU7564Digest digest) {
        copyIn(digest);
    }

    private void copyIn(DSTU7564Digest digest) {
        this.hashSize = digest.hashSize;
        this.blockSize = digest.blockSize;
        this.columns = digest.columns;
        this.rounds = digest.rounds;
        this.padded = Arrays.clone(digest.padded);
        this.state = Arrays.clone(digest.state);
        this.tempState1 = Arrays.clone(digest.tempState1);
        this.tempState2 = Arrays.clone(digest.tempState2);
        this.tempBuffer = Arrays.clone(digest.tempBuffer);
        this.mixColumnsResult = Arrays.clone(digest.mixColumnsResult);
        this.tempLongBuffer = Arrays.clone(digest.tempLongBuffer);
        this.inputLength = digest.inputLength;
        this.bufOff = digest.bufOff;
        this.buf = Arrays.clone(digest.buf);
    }

    public DSTU7564Digest(int hashSizeBits) {
        if (hashSizeBits == 256 || hashSizeBits == 384 || hashSizeBits == 512) {
            this.hashSize = hashSizeBits / 8;
            if (hashSizeBits > 256) {
                this.blockSize = 128;
                this.columns = 16;
                this.rounds = 14;
                this.state = new byte[128][];
            } else {
                this.blockSize = 64;
                this.columns = 8;
                this.rounds = 10;
                this.state = new byte[64][];
            }
            for (int bufferIndex = 0; bufferIndex < this.state.length; bufferIndex++) {
                this.state[bufferIndex] = new byte[this.columns];
            }
            this.state[0][0] = (byte) this.state.length;
            this.padded = null;
            this.tempState1 = new byte[128][];
            this.tempState2 = new byte[128][];
            for (int bufferIndex2 = 0; bufferIndex2 < this.state.length; bufferIndex2++) {
                this.tempState1[bufferIndex2] = new byte[8];
                this.tempState2[bufferIndex2] = new byte[8];
            }
            this.tempBuffer = new byte[16];
            this.mixColumnsResult = new byte[8];
            this.tempLongBuffer = new long[this.columns];
            this.buf = new byte[this.blockSize];
            return;
        }
        throw new IllegalArgumentException("Hash size is not recommended. Use 256/384/512 instead");
    }

    @Override // org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        return "DSTU7564";
    }

    @Override // org.spongycastle.crypto.Digest
    public int getDigestSize() {
        return this.hashSize;
    }

    @Override // org.spongycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return this.blockSize;
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte in) {
        byte[] bArr = this.buf;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = in;
        if (this.bufOff == this.blockSize) {
            processBlock(this.buf, 0);
            this.bufOff = 0;
        }
        this.inputLength++;
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte[] in, int inOff, int len) {
        while (this.bufOff != 0 && len > 0) {
            int i = inOff;
            inOff++;
            update(in[i]);
            len--;
        }
        if (len > 0) {
            while (len > this.blockSize) {
                processBlock(in, inOff);
                inOff += this.blockSize;
                this.inputLength += (long) this.blockSize;
                len -= this.blockSize;
            }
            while (len > 0) {
                int i2 = inOff;
                inOff++;
                update(in[i2]);
                len--;
            }
        }
    }

    @Override // org.spongycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        this.padded = pad(this.buf, 0, this.bufOff);
        int paddedLen = this.padded.length;
        int paddedOff = 0;
        while (paddedLen != 0) {
            processBlock(this.padded, paddedOff);
            paddedOff += this.blockSize;
            paddedLen -= this.blockSize;
        }
        byte[][] temp = new byte[128][];
        for (int bufferIndex = 0; bufferIndex < this.state.length; bufferIndex++) {
            temp[bufferIndex] = new byte[8];
            System.arraycopy(this.state[bufferIndex], 0, temp[bufferIndex], 0, 8);
        }
        for (int roundIndex = 0; roundIndex < this.rounds; roundIndex++) {
            for (int columnIndex = 0; columnIndex < this.columns; columnIndex++) {
                byte[] bArr = temp[columnIndex];
                bArr[0] = (byte) (bArr[0] ^ ((byte) ((columnIndex * 16) ^ roundIndex)));
            }
            for (int rowIndex = 0; rowIndex < 8; rowIndex++) {
                for (int columnIndex2 = 0; columnIndex2 < this.columns; columnIndex2++) {
                    temp[columnIndex2][rowIndex] = sBoxes[rowIndex % 4][temp[columnIndex2][rowIndex] & 255];
                }
            }
            int shift = -1;
            for (int rowIndex2 = 0; rowIndex2 < 8; rowIndex2++) {
                if (rowIndex2 == 7 && this.columns == 16) {
                    shift = 11;
                } else {
                    shift++;
                }
                for (int columnIndex3 = 0; columnIndex3 < this.columns; columnIndex3++) {
                    this.tempBuffer[(columnIndex3 + shift) % this.columns] = temp[columnIndex3][rowIndex2];
                }
                for (int columnIndex4 = 0; columnIndex4 < this.columns; columnIndex4++) {
                    temp[columnIndex4][rowIndex2] = this.tempBuffer[columnIndex4];
                }
            }
            for (int columnIndex5 = 0; columnIndex5 < this.columns; columnIndex5++) {
                Arrays.fill(this.mixColumnsResult, (byte) 0);
                for (int rowIndex3 = 7; rowIndex3 >= 0; rowIndex3--) {
                    byte multiplicationResult = 0;
                    for (int rowInternalIndex = 7; rowInternalIndex >= 0; rowInternalIndex--) {
                        multiplicationResult = (byte) (multiplicationResult ^ multiplyGF(temp[columnIndex5][rowInternalIndex], mds_matrix[rowIndex3][rowInternalIndex]));
                    }
                    this.mixColumnsResult[rowIndex3] = multiplicationResult;
                }
                for (int rowIndex4 = 0; rowIndex4 < 8; rowIndex4++) {
                    temp[columnIndex5][rowIndex4] = this.mixColumnsResult[rowIndex4];
                }
            }
        }
        for (int rowIndex5 = 0; rowIndex5 < 8; rowIndex5++) {
            for (int columnIndex6 = 0; columnIndex6 < this.columns; columnIndex6++) {
                byte[] bArr2 = this.state[columnIndex6];
                int i = rowIndex5;
                bArr2[i] = (byte) (bArr2[i] ^ temp[columnIndex6][rowIndex5]);
            }
        }
        byte[] stateBuffer = new byte[8 * this.columns];
        int stateLineIndex = 0;
        for (int columnIndex7 = 0; columnIndex7 < this.columns; columnIndex7++) {
            for (int rowIndex6 = 0; rowIndex6 < 8; rowIndex6++) {
                stateBuffer[stateLineIndex] = this.state[columnIndex7][rowIndex6];
                stateLineIndex++;
            }
        }
        System.arraycopy(stateBuffer, stateBuffer.length - this.hashSize, out, outOff, this.hashSize);
        reset();
        return this.hashSize;
    }

    @Override // org.spongycastle.crypto.Digest
    public void reset() {
        for (int bufferIndex = 0; bufferIndex < this.state.length; bufferIndex++) {
            this.state[bufferIndex] = new byte[this.columns];
        }
        this.state[0][0] = (byte) this.state.length;
        this.inputLength = 0L;
        this.bufOff = 0;
        Arrays.fill(this.buf, (byte) 0);
        if (this.padded != null) {
            Arrays.fill(this.padded, (byte) 0);
        }
    }

    private void processBlock(byte[] input, int inOff) {
        for (int bufferIndex = 0; bufferIndex < this.state.length; bufferIndex++) {
            Arrays.fill(this.tempState1[bufferIndex], (byte) 0);
            Arrays.fill(this.tempState2[bufferIndex], (byte) 0);
        }
        for (int bufferIndex2 = 0; bufferIndex2 < 8; bufferIndex2++) {
            for (int byteIndex = 0; byteIndex < this.columns; byteIndex++) {
                this.tempState1[byteIndex][bufferIndex2] = (byte) (this.state[byteIndex][bufferIndex2] ^ input[((byteIndex * 8) + bufferIndex2) + inOff]);
                this.tempState2[byteIndex][bufferIndex2] = input[(byteIndex * 8) + bufferIndex2 + inOff];
            }
        }
        P();
        Q();
        for (int bufferIndex3 = 0; bufferIndex3 < 8; bufferIndex3++) {
            for (int byteIndex2 = 0; byteIndex2 < this.columns; byteIndex2++) {
                byte[] bArr = this.state[byteIndex2];
                int i = bufferIndex3;
                bArr[i] = (byte) (bArr[i] ^ ((byte) (this.tempState1[byteIndex2][bufferIndex3] ^ this.tempState2[byteIndex2][bufferIndex3])));
            }
        }
    }

    private void Q() {
        for (int roundIndex = 0; roundIndex < this.rounds; roundIndex++) {
            for (int columnIndex = 0; columnIndex < this.columns; columnIndex++) {
                this.tempLongBuffer[columnIndex] = Pack.littleEndianToLong(this.tempState2[columnIndex], 0);
                long[] jArr = this.tempLongBuffer;
                int i = columnIndex;
                jArr[i] = jArr[i] + (67818912035696883L ^ (((((long) ((this.columns - columnIndex) - 1)) * 16) ^ ((long) roundIndex)) << 56));
                Pack.longToLittleEndian(this.tempLongBuffer[columnIndex], this.tempState2[columnIndex], 0);
            }
            for (int rowIndex = 0; rowIndex < 8; rowIndex++) {
                for (int columnIndex2 = 0; columnIndex2 < this.columns; columnIndex2++) {
                    this.tempState2[columnIndex2][rowIndex] = sBoxes[rowIndex % 4][this.tempState2[columnIndex2][rowIndex] & 255];
                }
            }
            int shift = -1;
            for (int rowIndex2 = 0; rowIndex2 < 8; rowIndex2++) {
                if (rowIndex2 == 7 && this.columns == 16) {
                    shift = 11;
                } else {
                    shift++;
                }
                for (int columnIndex3 = 0; columnIndex3 < this.columns; columnIndex3++) {
                    this.tempBuffer[(columnIndex3 + shift) % this.columns] = this.tempState2[columnIndex3][rowIndex2];
                }
                for (int columnIndex4 = 0; columnIndex4 < this.columns; columnIndex4++) {
                    this.tempState2[columnIndex4][rowIndex2] = this.tempBuffer[columnIndex4];
                }
            }
            for (int columnIndex5 = 0; columnIndex5 < this.columns; columnIndex5++) {
                Arrays.fill(this.mixColumnsResult, (byte) 0);
                for (int rowIndex3 = 7; rowIndex3 >= 0; rowIndex3--) {
                    byte multiplicationResult = 0;
                    for (int rowInternalIndex = 7; rowInternalIndex >= 0; rowInternalIndex--) {
                        multiplicationResult = (byte) (multiplicationResult ^ multiplyGF(this.tempState2[columnIndex5][rowInternalIndex], mds_matrix[rowIndex3][rowInternalIndex]));
                    }
                    this.mixColumnsResult[rowIndex3] = multiplicationResult;
                }
                for (int rowIndex4 = 0; rowIndex4 < 8; rowIndex4++) {
                    this.tempState2[columnIndex5][rowIndex4] = this.mixColumnsResult[rowIndex4];
                }
            }
        }
    }

    private void P() {
        for (int roundIndex = 0; roundIndex < this.rounds; roundIndex++) {
            for (int columnIndex = 0; columnIndex < this.columns; columnIndex++) {
                byte[] bArr = this.tempState1[columnIndex];
                bArr[0] = (byte) (bArr[0] ^ ((byte) ((columnIndex * 16) ^ roundIndex)));
            }
            for (int rowIndex = 0; rowIndex < 8; rowIndex++) {
                for (int columnIndex2 = 0; columnIndex2 < this.columns; columnIndex2++) {
                    this.tempState1[columnIndex2][rowIndex] = sBoxes[rowIndex % 4][this.tempState1[columnIndex2][rowIndex] & 255];
                }
            }
            int shift = -1;
            for (int rowIndex2 = 0; rowIndex2 < 8; rowIndex2++) {
                if (rowIndex2 == 7 && this.columns == 16) {
                    shift = 11;
                } else {
                    shift++;
                }
                for (int columnIndex3 = 0; columnIndex3 < this.columns; columnIndex3++) {
                    this.tempBuffer[(columnIndex3 + shift) % this.columns] = this.tempState1[columnIndex3][rowIndex2];
                }
                for (int columnIndex4 = 0; columnIndex4 < this.columns; columnIndex4++) {
                    this.tempState1[columnIndex4][rowIndex2] = this.tempBuffer[columnIndex4];
                }
            }
            for (int columnIndex5 = 0; columnIndex5 < this.columns; columnIndex5++) {
                Arrays.fill(this.mixColumnsResult, (byte) 0);
                for (int rowIndex3 = 7; rowIndex3 >= 0; rowIndex3--) {
                    byte multiplicationResult = 0;
                    for (int rowInternalIndex = 7; rowInternalIndex >= 0; rowInternalIndex--) {
                        multiplicationResult = (byte) (multiplicationResult ^ multiplyGF(this.tempState1[columnIndex5][rowInternalIndex], mds_matrix[rowIndex3][rowInternalIndex]));
                    }
                    this.mixColumnsResult[rowIndex3] = multiplicationResult;
                }
                for (int rowIndex4 = 0; rowIndex4 < 8; rowIndex4++) {
                    this.tempState1[columnIndex5][rowIndex4] = this.mixColumnsResult[rowIndex4];
                }
            }
        }
    }

    private byte multiplyGF(byte x, byte y) {
        byte result = 0;
        for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
            if ((y & 1) == 1) {
                result = (byte) (result ^ x);
            }
            byte highestBit = (byte) (x & (-128));
            x = (byte) (x << 1);
            if (highestBit == -128) {
                x = (byte) (x ^ 285);
            }
            y = (byte) (y >> 1);
        }
        return result;
    }

    private byte[] pad(byte[] in, int inOff, int len) {
        byte[] padded;
        if (this.blockSize - len < 13) {
            padded = new byte[2 * this.blockSize];
        } else {
            padded = new byte[this.blockSize];
        }
        System.arraycopy(in, inOff, padded, 0, len);
        padded[len] = -128;
        Pack.longToLittleEndian(this.inputLength * 8, padded, padded.length - 12);
        return padded;
    }

    @Override // org.spongycastle.util.Memoable
    public Memoable copy() {
        return new DSTU7564Digest(this);
    }

    @Override // org.spongycastle.util.Memoable
    public void reset(Memoable other) {
        DSTU7564Digest d = (DSTU7564Digest) other;
        copyIn(d);
    }
}
