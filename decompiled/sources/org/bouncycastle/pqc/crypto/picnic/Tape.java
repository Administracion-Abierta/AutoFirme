package org.bouncycastle.pqc.crypto.picnic;

import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/picnic/Tape.class */
class Tape {
    byte[][] tapes;
    int pos = 0;
    int nTapes;
    private PicnicEngine engine;

    public Tape(PicnicEngine picnicEngine) {
        this.engine = picnicEngine;
        this.tapes = new byte[picnicEngine.numMPCParties][2 * picnicEngine.andSizeBytes];
        this.nTapes = picnicEngine.numMPCParties;
    }

    protected void setAuxBits(byte[] bArr) {
        int i = this.engine.numMPCParties - 1;
        int i2 = 0;
        int i3 = this.engine.stateSizeBits;
        for (int i4 = 0; i4 < this.engine.numRounds; i4++) {
            for (int i5 = 0; i5 < i3; i5++) {
                int i6 = i2;
                i2++;
                Utils.setBit(this.tapes[i], i3 + (i3 * 2 * i4) + i5, Utils.getBit(bArr, i6));
            }
        }
    }

    protected void computeAuxTape(byte[] bArr) {
        int[] iArr = new int[16];
        int[] iArr2 = new int[16];
        int[] iArr3 = new int[16];
        int[] iArr4 = new int[16];
        int[] iArr5 = new int[16];
        iArr5[this.engine.stateSizeWords - 1] = 0;
        tapesToParityBits(iArr5, this.engine.stateSizeBits);
        KMatricesWithPointer kMatricesWithPointerKMatrixInv = this.engine.lowmcConstants.KMatrixInv(this.engine);
        this.engine.matrix_mul(iArr4, iArr5, kMatricesWithPointerKMatrixInv.getData(), kMatricesWithPointerKMatrixInv.getMatrixPointer());
        if (bArr != null) {
            Pack.intToLittleEndian(iArr4, 0, this.engine.stateSizeWords, bArr, 0);
        }
        for (int i = this.engine.numRounds; i > 0; i--) {
            KMatricesWithPointer kMatricesWithPointerKMatrix = this.engine.lowmcConstants.KMatrix(this.engine, i);
            this.engine.matrix_mul(iArr, iArr4, kMatricesWithPointerKMatrix.getData(), kMatricesWithPointerKMatrix.getMatrixPointer());
            this.engine.xor_array(iArr2, iArr2, iArr, 0);
            KMatricesWithPointer kMatricesWithPointerLMatrixInv = this.engine.lowmcConstants.LMatrixInv(this.engine, i - 1);
            this.engine.matrix_mul(iArr3, iArr2, kMatricesWithPointerLMatrixInv.getData(), kMatricesWithPointerLMatrixInv.getMatrixPointer());
            if (i == 1) {
                System.arraycopy(iArr5, 0, iArr2, 0, iArr5.length);
            } else {
                this.pos = this.engine.stateSizeBits * 2 * (i - 1);
                tapesToParityBits(iArr2, this.engine.stateSizeBits);
            }
            this.pos = (this.engine.stateSizeBits * 2 * (i - 1)) + this.engine.stateSizeBits;
            this.engine.aux_mpc_sbox(iArr2, iArr3, this);
        }
        this.pos = 0;
    }

    private void tapesToParityBits(int[] iArr, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            Utils.setBitInWordArray(iArr, i2, Utils.parity16(tapesToWord()));
        }
    }

    protected int tapesToWord() {
        int i = this.pos >>> 3;
        int i2 = (this.pos & 7) ^ 7;
        int i3 = 1 << i2;
        int i4 = 0 | ((this.tapes[0][i] & i3) << 7) | ((this.tapes[1][i] & i3) << 6) | ((this.tapes[2][i] & i3) << 5) | ((this.tapes[3][i] & i3) << 4) | ((this.tapes[4][i] & i3) << 3) | ((this.tapes[5][i] & i3) << 2) | ((this.tapes[6][i] & i3) << 1) | ((this.tapes[7][i] & i3) << 0) | ((this.tapes[8][i] & i3) << 15) | ((this.tapes[9][i] & i3) << 14) | ((this.tapes[10][i] & i3) << 13) | ((this.tapes[11][i] & i3) << 12) | ((this.tapes[12][i] & i3) << 11) | ((this.tapes[13][i] & i3) << 10) | ((this.tapes[14][i] & i3) << 9) | ((this.tapes[15][i] & i3) << 8);
        this.pos++;
        return i4 >>> i2;
    }
}
