package es.gob.afirma.keystores.mozilla.bintutil;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/mozilla/bintutil/AOBinUtil.class */
final class AOBinUtil {
    private AOBinUtil() {
    }

    static int getDWord(byte[] bArr, int i) {
        byte[] bArr2 = (byte[]) bArr.clone();
        byte[] bArr3 = {bArr2[i + 7], bArr2[i + 6], bArr2[i + 5], bArr2[i + 4], bArr2[i + 3], bArr2[i + 2], bArr2[i + 1], bArr2[i + 0]};
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < 8; i4++) {
            int i5 = i2;
            i2++;
            i3 = (i3 << 8) | (bArr3[i5] & 255);
        }
        return i3;
    }

    static int getU2(byte[] bArr, int i) {
        return ((bArr[i + 1] & 255) << 8) | (bArr[i] & 255);
    }

    static int getInt(byte[] bArr, int i) {
        byte[] bArr2 = (byte[]) bArr.clone();
        byte[] bArr3 = {bArr2[i + 3], bArr2[i + 2], bArr2[i + 1], bArr2[i]};
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < 4; i4++) {
            int i5 = i2;
            i2++;
            i3 = (i3 << 8) | (bArr3[i5] & 255);
        }
        return i3;
    }
}
