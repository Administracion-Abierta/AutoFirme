package org.bouncycastle.util.encoders;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/encoders/HexTranslator.class */
public class HexTranslator implements Translator {
    private static final byte[] hexTable = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};

    @Override // org.bouncycastle.util.encoders.Translator
    public int getEncodedBlockSize() {
        return 2;
    }

    @Override // org.bouncycastle.util.encoders.Translator
    public int encode(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        int i4 = 0;
        int i5 = 0;
        while (i4 < i2) {
            bArr2[i3 + i5] = hexTable[(bArr[i] >> 4) & 15];
            bArr2[i3 + i5 + 1] = hexTable[bArr[i] & 15];
            i++;
            i4++;
            i5 += 2;
        }
        return i2 * 2;
    }

    @Override // org.bouncycastle.util.encoders.Translator
    public int getDecodedBlockSize() {
        return 1;
    }

    @Override // org.bouncycastle.util.encoders.Translator
    public int decode(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        int i4 = i2 / 2;
        for (int i5 = 0; i5 < i4; i5++) {
            byte b = bArr[i + (i5 * 2)];
            byte b2 = bArr[i + (i5 * 2) + 1];
            if (b < 97) {
                bArr2[i3] = (byte) ((b - 48) << 4);
            } else {
                bArr2[i3] = (byte) (((b - 97) + 10) << 4);
            }
            if (b2 < 97) {
                int i6 = i3;
                bArr2[i6] = (byte) (bArr2[i6] + ((byte) (b2 - 48)));
            } else {
                int i7 = i3;
                bArr2[i7] = (byte) (bArr2[i7] + ((byte) ((b2 - 97) + 10)));
            }
            i3++;
        }
        return i4;
    }
}
