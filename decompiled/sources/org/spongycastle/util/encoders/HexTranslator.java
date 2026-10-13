package org.spongycastle.util.encoders;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/util/encoders/HexTranslator.class */
public class HexTranslator implements Translator {
    private static final byte[] hexTable = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};

    @Override // org.spongycastle.util.encoders.Translator
    public int getEncodedBlockSize() {
        return 2;
    }

    @Override // org.spongycastle.util.encoders.Translator
    public int encode(byte[] in, int inOff, int length, byte[] out, int outOff) {
        int i = 0;
        int j = 0;
        while (i < length) {
            out[outOff + j] = hexTable[(in[inOff] >> 4) & 15];
            out[outOff + j + 1] = hexTable[in[inOff] & 15];
            inOff++;
            i++;
            j += 2;
        }
        return length * 2;
    }

    @Override // org.spongycastle.util.encoders.Translator
    public int getDecodedBlockSize() {
        return 1;
    }

    @Override // org.spongycastle.util.encoders.Translator
    public int decode(byte[] in, int inOff, int length, byte[] out, int outOff) {
        int halfLength = length / 2;
        for (int i = 0; i < halfLength; i++) {
            byte left = in[inOff + (i * 2)];
            byte right = in[inOff + (i * 2) + 1];
            if (left < 97) {
                out[outOff] = (byte) ((left - 48) << 4);
            } else {
                out[outOff] = (byte) (((left - 97) + 10) << 4);
            }
            if (right < 97) {
                int i2 = outOff;
                out[i2] = (byte) (out[i2] + ((byte) (right - 48)));
            } else {
                int i3 = outOff;
                out[i3] = (byte) (out[i3] + ((byte) ((right - 97) + 10)));
            }
            outOff++;
        }
        return halfLength;
    }
}
