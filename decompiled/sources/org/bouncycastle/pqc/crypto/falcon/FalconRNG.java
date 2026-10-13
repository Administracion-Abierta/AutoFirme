package org.bouncycastle.pqc.crypto.falcon;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/falcon/FalconRNG.class */
class FalconRNG {
    byte[] bd = new byte[512];
    long bdummy_u64 = 0;
    int ptr = 0;
    byte[] sd = new byte[256];
    long sdummy_u64 = 0;
    int type = 0;
    FalconConversions convertor = new FalconConversions();

    FalconRNG() {
    }

    void prng_init(SHAKE256 shake256) {
        byte[] bArr = new byte[56];
        shake256.inner_shake256_extract(bArr, 0, 56);
        for (int i = 0; i < 14; i++) {
            System.arraycopy(this.convertor.int_to_bytes((bArr[(i << 2) + 0] & 255) | ((bArr[(i << 2) + 1] & 255) << 8) | ((bArr[(i << 2) + 2] & 255) << 16) | ((bArr[(i << 2) + 3] & 255) << 24)), 0, this.sd, i << 2, 4);
        }
        System.arraycopy(this.convertor.long_to_bytes((((long) this.convertor.bytes_to_int(this.sd, 48)) & 4294967295L) + ((((long) this.convertor.bytes_to_int(this.sd, 52)) & 4294967295L) << 32)), 0, this.sd, 48, 8);
        prng_refill();
    }

    void prng_refill() {
        int[] iArr = {1634760805, 857760878, 2036477234, 1797285236};
        long jBytes_to_long = this.convertor.bytes_to_long(this.sd, 48);
        for (int i = 0; i < 8; i++) {
            int[] iArr2 = new int[16];
            System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
            System.arraycopy(this.convertor.bytes_to_int_array(this.sd, 0, 12), 0, iArr2, 4, 12);
            iArr2[14] = iArr2[14] ^ ((int) jBytes_to_long);
            iArr2[15] = iArr2[15] ^ ((int) (jBytes_to_long >>> 32));
            for (int i2 = 0; i2 < 10; i2++) {
                QROUND(0, 4, 8, 12, iArr2);
                QROUND(1, 5, 9, 13, iArr2);
                QROUND(2, 6, 10, 14, iArr2);
                QROUND(3, 7, 11, 15, iArr2);
                QROUND(0, 5, 10, 15, iArr2);
                QROUND(1, 6, 11, 12, iArr2);
                QROUND(2, 7, 8, 13, iArr2);
                QROUND(3, 4, 9, 14, iArr2);
            }
            for (int i3 = 0; i3 < 4; i3++) {
                int i4 = i3;
                iArr2[i4] = iArr2[i4] + iArr[i3];
            }
            for (int i5 = 4; i5 < 14; i5++) {
                int i6 = i5;
                iArr2[i6] = iArr2[i6] + this.convertor.bytes_to_int(this.sd, (4 * i5) - 16);
            }
            iArr2[14] = iArr2[14] + (this.convertor.bytes_to_int(this.sd, 40) ^ ((int) jBytes_to_long));
            iArr2[15] = iArr2[15] + (this.convertor.bytes_to_int(this.sd, 44) ^ ((int) (jBytes_to_long >>> 32)));
            jBytes_to_long++;
            for (int i7 = 0; i7 < 16; i7++) {
                this.bd[(i << 2) + (i7 << 5) + 0] = (byte) iArr2[i7];
                this.bd[(i << 2) + (i7 << 5) + 1] = (byte) (iArr2[i7] >>> 8);
                this.bd[(i << 2) + (i7 << 5) + 2] = (byte) (iArr2[i7] >>> 16);
                this.bd[(i << 2) + (i7 << 5) + 3] = (byte) (iArr2[i7] >>> 24);
            }
        }
        System.arraycopy(this.convertor.long_to_bytes(jBytes_to_long), 0, this.sd, 48, 8);
        this.ptr = 0;
    }

    void prng_get_bytes(byte[] bArr, int i, int i2) {
        int i3 = i;
        while (i2 > 0) {
            int length = this.bd.length - this.ptr;
            if (length > i2) {
                length = i2;
            }
            System.arraycopy(this.bd, 0, bArr, i3, length);
            i3 += length;
            i2 -= length;
            this.ptr += length;
            if (this.ptr == this.bd.length) {
                prng_refill();
            }
        }
    }

    private void QROUND(int i, int i2, int i3, int i4, int[] iArr) {
        iArr[i] = iArr[i] + iArr[i2];
        iArr[i4] = iArr[i4] ^ iArr[i];
        iArr[i4] = (iArr[i4] << 16) | (iArr[i4] >>> 16);
        iArr[i3] = iArr[i3] + iArr[i4];
        iArr[i2] = iArr[i2] ^ iArr[i3];
        iArr[i2] = (iArr[i2] << 12) | (iArr[i2] >>> 20);
        iArr[i] = iArr[i] + iArr[i2];
        iArr[i4] = iArr[i4] ^ iArr[i];
        iArr[i4] = (iArr[i4] << 8) | (iArr[i4] >>> 24);
        iArr[i3] = iArr[i3] + iArr[i4];
        iArr[i2] = iArr[i2] ^ iArr[i3];
        iArr[i2] = (iArr[i2] << 7) | (iArr[i2] >>> 25);
    }

    long prng_get_u64() {
        int i = this.ptr;
        if (i >= this.bd.length - 9) {
            prng_refill();
            i = 0;
        }
        this.ptr = i + 8;
        return (((long) this.bd[i + 0]) & 255) | ((((long) this.bd[i + 1]) & 255) << 8) | ((((long) this.bd[i + 2]) & 255) << 16) | ((((long) this.bd[i + 3]) & 255) << 24) | ((((long) this.bd[i + 4]) & 255) << 32) | ((((long) this.bd[i + 5]) & 255) << 40) | ((((long) this.bd[i + 6]) & 255) << 48) | ((((long) this.bd[i + 7]) & 255) << 56);
    }

    byte prng_get_u8() {
        byte[] bArr = this.bd;
        int i = this.ptr;
        this.ptr = i + 1;
        byte b = bArr[i];
        if (this.ptr == this.bd.length) {
            prng_refill();
        }
        return b;
    }
}
