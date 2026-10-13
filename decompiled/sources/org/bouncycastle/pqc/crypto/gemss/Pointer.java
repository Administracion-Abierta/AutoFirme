package org.bouncycastle.pqc.crypto.gemss;

import java.security.SecureRandom;
import java.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/Pointer.class */
class Pointer {
    protected long[] array;
    protected int cp;

    public Pointer() {
        this.cp = 0;
    }

    public Pointer(int i) {
        this.array = new long[i];
        this.cp = 0;
    }

    public Pointer(Pointer pointer) {
        this.array = pointer.array;
        this.cp = pointer.cp;
    }

    public Pointer(Pointer pointer, int i) {
        this.array = pointer.array;
        this.cp = pointer.cp + i;
    }

    public long get(int i) {
        return this.array[this.cp + i];
    }

    public long get() {
        return this.array[this.cp];
    }

    public void set(int i, long j) {
        this.array[this.cp + i] = j;
    }

    public void set(long j) {
        this.array[this.cp] = j;
    }

    public void setXor(int i, long j) {
        long[] jArr = this.array;
        int i2 = this.cp + i;
        jArr[i2] = jArr[i2] ^ j;
    }

    public void setXor(long j) {
        long[] jArr = this.array;
        int i = this.cp;
        jArr[i] = jArr[i] ^ j;
    }

    public void setXorRange(Pointer pointer, int i) {
        int i2 = this.cp;
        int i3 = pointer.cp;
        for (int i4 = 0; i4 < i; i4++) {
            long[] jArr = this.array;
            int i5 = i2;
            i2++;
            int i6 = i3;
            i3++;
            jArr[i5] = jArr[i5] ^ pointer.array[i6];
        }
    }

    public void setXorRange(Pointer pointer, int i, int i2) {
        int i3 = this.cp;
        int i4 = i + pointer.cp;
        for (int i5 = 0; i5 < i2; i5++) {
            long[] jArr = this.array;
            int i6 = i3;
            i3++;
            int i7 = i4;
            i4++;
            jArr[i6] = jArr[i6] ^ pointer.array[i7];
        }
    }

    public void setXorRange(int i, Pointer pointer, int i2, int i3) {
        int i4 = i + this.cp;
        int i5 = i2 + pointer.cp;
        for (int i6 = 0; i6 < i3; i6++) {
            long[] jArr = this.array;
            int i7 = i4;
            i4++;
            int i8 = i5;
            i5++;
            jArr[i7] = jArr[i7] ^ pointer.array[i8];
        }
    }

    public void setXorRange_SelfMove(Pointer pointer, int i) {
        int i2 = pointer.cp;
        for (int i3 = 0; i3 < i; i3++) {
            long[] jArr = this.array;
            int i4 = this.cp;
            this.cp = i4 + 1;
            int i5 = i2;
            i2++;
            jArr[i4] = jArr[i4] ^ pointer.array[i5];
        }
    }

    public void setXorMatrix_NoMove(Pointer pointer, int i, int i2) {
        int i3 = this.cp;
        for (int i4 = 0; i4 < i2; i4++) {
            int i5 = i3;
            for (int i6 = 0; i6 < i; i6++) {
                long[] jArr = this.array;
                int i7 = i5;
                i5++;
                long j = jArr[i7];
                long[] jArr2 = pointer.array;
                int i8 = pointer.cp;
                pointer.cp = i8 + 1;
                jArr[i7] = j ^ jArr2[i8];
            }
        }
    }

    public void setXorMatrix(Pointer pointer, int i, int i2) {
        int i3 = this.cp;
        for (int i4 = 0; i4 < i2; i4++) {
            int i5 = i3;
            for (int i6 = 0; i6 < i; i6++) {
                long[] jArr = this.array;
                int i7 = i5;
                i5++;
                long j = jArr[i7];
                long[] jArr2 = pointer.array;
                int i8 = pointer.cp;
                pointer.cp = i8 + 1;
                jArr[i7] = j ^ jArr2[i8];
            }
        }
        this.cp += i;
    }

    public void setXorRangeXor(int i, Pointer pointer, int i2, Pointer pointer2, int i3, int i4) {
        int i5 = i + this.cp;
        int i6 = i2 + pointer.cp;
        int i7 = i3 + pointer2.cp;
        for (int i8 = 0; i8 < i4; i8++) {
            long[] jArr = this.array;
            int i9 = i5;
            i5++;
            int i10 = i6;
            i6++;
            int i11 = i7;
            i7++;
            jArr[i9] = jArr[i9] ^ (pointer.array[i10] ^ pointer2.array[i11]);
        }
    }

    public void setXorRange(int i, PointerUnion pointerUnion, int i2, int i3) {
        int i4 = i + this.cp;
        int i5 = i2 + pointerUnion.cp;
        if (pointerUnion.remainder == 0) {
            for (int i6 = 0; i6 < i3; i6++) {
                long[] jArr = this.array;
                int i7 = i4;
                i4++;
                int i8 = i5;
                i5++;
                jArr[i7] = jArr[i7] ^ pointerUnion.array[i8];
            }
            return;
        }
        int i9 = pointerUnion.remainder << 3;
        int i10 = (8 - pointerUnion.remainder) << 3;
        for (int i11 = 0; i11 < i3; i11++) {
            long[] jArr2 = this.array;
            int i12 = i4;
            i4++;
            long j = jArr2[i12];
            long j2 = pointerUnion.array[i5] >>> i9;
            i5++;
            jArr2[i12] = j ^ (j2 | (pointerUnion.array[i5] << i10));
        }
    }

    public void setXorRangeAndMask(Pointer pointer, int i, long j) {
        int i2 = this.cp;
        int i3 = pointer.cp;
        for (int i4 = 0; i4 < i; i4++) {
            long[] jArr = this.array;
            int i5 = i2;
            i2++;
            int i6 = i3;
            i3++;
            jArr[i5] = jArr[i5] ^ (pointer.array[i6] & j);
        }
    }

    public void setXorRangeAndMaskMove(Pointer pointer, int i, long j) {
        int i2 = this.cp;
        for (int i3 = 0; i3 < i; i3++) {
            long[] jArr = this.array;
            int i4 = i2;
            i2++;
            long j2 = jArr[i4];
            long[] jArr2 = pointer.array;
            int i5 = pointer.cp;
            pointer.cp = i5 + 1;
            jArr[i4] = j2 ^ (jArr2[i5] & j);
        }
    }

    public void setRangeRotate(int i, Pointer pointer, int i2, int i3, int i4) {
        int i5 = 64 - i4;
        int i6 = i + this.cp;
        int i7 = i2 + pointer.cp;
        for (int i8 = 0; i8 < i3; i8++) {
            long[] jArr = this.array;
            int i9 = i6;
            i6++;
            long j = pointer.array[i7] >>> i5;
            i7++;
            jArr[i9] = j ^ (pointer.array[i7] << i4);
        }
    }

    public void move(int i) {
        this.cp += i;
    }

    public void moveIncremental() {
        this.cp++;
    }

    public long[] getArray() {
        return this.array;
    }

    public int getIndex() {
        return this.cp;
    }

    public void setAnd(int i, long j) {
        long[] jArr = this.array;
        int i2 = this.cp + i;
        jArr[i2] = jArr[i2] & j;
    }

    public void setAnd(long j) {
        long[] jArr = this.array;
        int i = this.cp;
        jArr[i] = jArr[i] & j;
    }

    public void setClear(int i) {
        this.array[this.cp + i] = 0;
    }

    public void changeIndex(Pointer pointer) {
        this.array = pointer.array;
        this.cp = pointer.cp;
    }

    public void changeIndex(int i) {
        this.cp = i;
    }

    public void changeIndex(Pointer pointer, int i) {
        this.array = pointer.array;
        this.cp = pointer.cp + i;
    }

    public void setRangeClear(int i, int i2) {
        int i3 = i + this.cp;
        Arrays.fill(this.array, i3, i3 + i2, 0L);
    }

    public int getLength() {
        return this.array.length - this.cp;
    }

    public void copyFrom(Pointer pointer, int i) {
        System.arraycopy(pointer.array, pointer.cp, this.array, this.cp, i);
    }

    public void copyFrom(int i, Pointer pointer, int i2, int i3) {
        System.arraycopy(pointer.array, pointer.cp + i2, this.array, this.cp + i, i3);
    }

    public void set1_gf2n(int i, int i2) {
        int i3 = this.cp + i;
        int i4 = i3 + 1;
        this.array[i3] = 1;
        for (int i5 = 1; i5 < i2; i5++) {
            int i6 = i4;
            i4++;
            this.array[i6] = 0;
        }
    }

    public byte[] toBytes(int i) {
        byte[] bArr = new byte[i];
        for (int i2 = 0; i2 < bArr.length; i2++) {
            bArr[i2] = (byte) (this.array[this.cp + (i2 >>> 3)] >>> ((i2 & 7) << 3));
        }
        return bArr;
    }

    public void indexReset() {
        this.cp = 0;
    }

    public void fillRandom(int i, SecureRandom secureRandom, int i2) {
        byte[] bArr = new byte[i2];
        secureRandom.nextBytes(bArr);
        fill(i, bArr, 0, bArr.length);
    }

    public void fill(int i, byte[] bArr, int i2, int i3) {
        int i4 = 0;
        int i5 = this.cp + i;
        while (i5 < this.array.length && i4 + 8 <= i3) {
            this.array[i5] = Pack.littleEndianToLong(bArr, i2);
            i2 += 8;
            i4 += 8;
            i5++;
        }
        if (i4 >= i3 || i5 >= this.array.length) {
            return;
        }
        int i6 = 0;
        this.array[i5] = 0;
        while (i6 < 8 && i4 < i3) {
            long[] jArr = this.array;
            int i7 = i5;
            jArr[i7] = jArr[i7] | ((((long) bArr[i2]) & 255) << (i6 << 3));
            i6++;
            i2++;
            i4++;
        }
    }

    public void setRangeFromXor(int i, Pointer pointer, int i2, Pointer pointer2, int i3, int i4) {
        int i5 = i + this.cp;
        int i6 = i2 + pointer.cp;
        int i7 = i3 + pointer2.cp;
        for (int i8 = 0; i8 < i4; i8++) {
            int i9 = i5;
            i5++;
            int i10 = i6;
            i6++;
            int i11 = i7;
            i7++;
            this.array[i9] = pointer.array[i10] ^ pointer2.array[i11];
        }
    }

    public void setRangeFromXor(Pointer pointer, Pointer pointer2, int i) {
        int i2 = this.cp;
        int i3 = pointer.cp;
        int i4 = pointer2.cp;
        for (int i5 = 0; i5 < i; i5++) {
            int i6 = i2;
            i2++;
            int i7 = i3;
            i3++;
            int i8 = i4;
            i4++;
            this.array[i6] = pointer.array[i7] ^ pointer2.array[i8];
        }
    }

    public void setRangeFromXorAndMask_xor(Pointer pointer, Pointer pointer2, long j, int i) {
        int i2 = this.cp;
        int i3 = pointer.cp;
        int i4 = pointer2.cp;
        for (int i5 = 0; i5 < i; i5++) {
            this.array[i2] = (pointer.array[i3] ^ pointer2.array[i4]) & j;
            long[] jArr = pointer.array;
            int i6 = i3;
            i3++;
            jArr[i6] = jArr[i6] ^ this.array[i2];
            long[] jArr2 = pointer2.array;
            int i7 = i4;
            i4++;
            int i8 = i2;
            i2++;
            jArr2[i7] = jArr2[i7] ^ this.array[i8];
        }
    }

    public int is0_gf2n(int i, int i2) {
        long j = get(i);
        for (int i3 = 1; i3 < i2; i3++) {
            j |= get(i + i3);
        }
        return (int) GeMSSUtils.NORBITS_UINT(j);
    }

    public long getDotProduct(int i, Pointer pointer, int i2, int i3) {
        int i4 = i + this.cp;
        int i5 = i2 + pointer.cp;
        int i6 = i4 + 1;
        int i7 = i5 + 1;
        long j = this.array[i4] & pointer.array[i5];
        for (int i8 = 1; i8 < i3; i8++) {
            int i9 = i6;
            i6++;
            int i10 = i7;
            i7++;
            j ^= this.array[i9] & pointer.array[i10];
        }
        return j;
    }

    public int getD_for_not0_or_plus(int i, int i2) {
        int i3 = 0;
        long jORBITS_UINT = 0;
        int i4 = this.cp;
        for (int i5 = i2; i5 > 0; i5--) {
            int i6 = i4;
            i4++;
            long j = this.array[i6];
            for (int i7 = 1; i7 < i; i7++) {
                int i8 = i4;
                i4++;
                j |= this.array[i8];
            }
            jORBITS_UINT |= GeMSSUtils.ORBITS_UINT(j);
            i3 = (int) (((long) i3) + jORBITS_UINT);
        }
        return i3;
    }

    public int setRange_xi(long j, int i, int i2) {
        int i3 = 0;
        while (i3 < i2) {
            this.array[this.cp + i] = -((j >>> i3) & 1);
            i3++;
            i++;
        }
        return i;
    }

    public int searchDegree(int i, int i2, int i3) {
        while (is0_gf2n(i * i3, i3) != 0 && i >= i2) {
            i--;
        }
        return i;
    }

    public void setRangePointerUnion(PointerUnion pointerUnion, int i) {
        if (pointerUnion.remainder == 0) {
            System.arraycopy(pointerUnion.array, pointerUnion.cp, this.array, this.cp, i);
            return;
        }
        int i2 = (8 - pointerUnion.remainder) << 3;
        int i3 = pointerUnion.remainder << 3;
        int i4 = this.cp;
        int i5 = pointerUnion.cp;
        for (int i6 = 0; i6 < i; i6++) {
            long[] jArr = this.array;
            int i7 = i4;
            i4++;
            long j = pointerUnion.array[i5] >>> i3;
            i5++;
            jArr[i7] = j ^ (pointerUnion.array[i5] << i2);
        }
    }

    public void setRangePointerUnion(PointerUnion pointerUnion, int i, int i2) {
        int i3 = i2 & 63;
        int i4 = 64 - i3;
        int i5 = this.cp;
        int i6 = pointerUnion.cp;
        if (pointerUnion.remainder == 0) {
            for (int i7 = 0; i7 < i; i7++) {
                long[] jArr = this.array;
                int i8 = i5;
                i5++;
                long j = pointerUnion.array[i6] >>> i3;
                i6++;
                jArr[i8] = j ^ (pointerUnion.array[i6] << i4);
            }
            return;
        }
        int i9 = pointerUnion.remainder << 3;
        int i10 = (8 - pointerUnion.remainder) << 3;
        for (int i11 = 0; i11 < i; i11++) {
            long[] jArr2 = this.array;
            int i12 = i5;
            i5++;
            long j2 = pointerUnion.array[i6] >>> i9;
            i6++;
            jArr2[i12] = ((j2 | (pointerUnion.array[i6] << i10)) >>> i3) ^ (((pointerUnion.array[i6] >>> i9) | (pointerUnion.array[i6 + 1] << i10)) << i4);
        }
    }

    public void setRangePointerUnion_Check(PointerUnion pointerUnion, int i, int i2) {
        int i3 = i2 & 63;
        int i4 = 64 - i3;
        int i5 = this.cp;
        int i6 = pointerUnion.cp;
        if (pointerUnion.remainder == 0) {
            int i7 = 0;
            while (i7 < i && i6 < pointerUnion.array.length - 1) {
                long[] jArr = this.array;
                int i8 = i5;
                i5++;
                long j = pointerUnion.array[i6] >>> i3;
                i6++;
                jArr[i8] = j ^ (pointerUnion.array[i6] << i4);
                i7++;
            }
            if (i7 < i) {
                this.array[i5] = pointerUnion.array[i6] >>> i3;
                return;
            }
            return;
        }
        int i9 = pointerUnion.remainder << 3;
        int i10 = (8 - pointerUnion.remainder) << 3;
        int i11 = 0;
        while (i11 < i && i6 < pointerUnion.array.length - 2) {
            long[] jArr2 = this.array;
            int i12 = i5;
            i5++;
            long j2 = pointerUnion.array[i6] >>> i9;
            i6++;
            jArr2[i12] = ((j2 | (pointerUnion.array[i6] << i10)) >>> i3) ^ (((pointerUnion.array[i6] >>> i9) | (pointerUnion.array[i6 + 1] << i10)) << i4);
            i11++;
        }
        if (i11 < i) {
            long j3 = pointerUnion.array[i6] >>> i9;
            int i13 = i6 + 1;
            this.array[i5] = ((j3 | (pointerUnion.array[i13] << i10)) >>> i3) ^ ((pointerUnion.array[i13] >>> i9) << i4);
        }
    }

    public int isEqual_nocst_gf2(Pointer pointer, int i) {
        int i2 = pointer.cp;
        int i3 = this.cp;
        for (int i4 = 0; i4 < i; i4++) {
            int i5 = i3;
            i3++;
            int i6 = i2;
            i2++;
            if (this.array[i5] != pointer.array[i6]) {
                return 0;
            }
        }
        return 1;
    }

    public void swap(Pointer pointer) {
        long[] jArr = pointer.array;
        int i = pointer.cp;
        pointer.array = this.array;
        pointer.cp = this.cp;
        this.array = jArr;
        this.cp = i;
    }
}
