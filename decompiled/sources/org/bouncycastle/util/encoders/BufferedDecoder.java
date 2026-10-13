package org.bouncycastle.util.encoders;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/encoders/BufferedDecoder.class */
public class BufferedDecoder {
    protected byte[] buf;
    protected int bufOff;
    protected Translator translator;

    public BufferedDecoder(Translator translator, int i) {
        this.translator = translator;
        if (i % translator.getEncodedBlockSize() != 0) {
            throw new IllegalArgumentException("buffer size not multiple of input block size");
        }
        this.buf = new byte[i];
        this.bufOff = 0;
    }

    public int processByte(byte b, byte[] bArr, int i) {
        int iDecode = 0;
        byte[] bArr2 = this.buf;
        int i2 = this.bufOff;
        this.bufOff = i2 + 1;
        bArr2[i2] = b;
        if (this.bufOff == this.buf.length) {
            iDecode = this.translator.decode(this.buf, 0, this.buf.length, bArr, i);
            this.bufOff = 0;
        }
        return iDecode;
    }

    public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        if (i2 < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int iDecode = 0;
        int length = this.buf.length - this.bufOff;
        if (i2 > length) {
            System.arraycopy(bArr, i, this.buf, this.bufOff, length);
            int iDecode2 = 0 + this.translator.decode(this.buf, 0, this.buf.length, bArr2, i3);
            this.bufOff = 0;
            int i4 = i2 - length;
            int i5 = i + length;
            int i6 = i3 + iDecode2;
            int length2 = i4 - (i4 % this.buf.length);
            iDecode = iDecode2 + this.translator.decode(bArr, i5, length2, bArr2, i6);
            i2 = i4 - length2;
            i = i5 + length2;
        }
        if (i2 != 0) {
            System.arraycopy(bArr, i, this.buf, this.bufOff, i2);
            this.bufOff += i2;
        }
        return iDecode;
    }
}
