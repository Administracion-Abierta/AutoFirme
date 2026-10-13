package org.spongycastle.util.encoders;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/encoders/BufferedDecoder.class */
public class BufferedDecoder {
    protected byte[] buf;
    protected int bufOff;
    protected Translator translator;

    public BufferedDecoder(Translator translator, int bufSize) {
        this.translator = translator;
        if (bufSize % translator.getEncodedBlockSize() != 0) {
            throw new IllegalArgumentException("buffer size not multiple of input block size");
        }
        this.buf = new byte[bufSize];
        this.bufOff = 0;
    }

    public int processByte(byte in, byte[] out, int outOff) {
        int resultLen = 0;
        byte[] bArr = this.buf;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = in;
        if (this.bufOff == this.buf.length) {
            resultLen = this.translator.decode(this.buf, 0, this.buf.length, out, outOff);
            this.bufOff = 0;
        }
        return resultLen;
    }

    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) {
        if (len < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int resultLen = 0;
        int gapLen = this.buf.length - this.bufOff;
        if (len > gapLen) {
            System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
            int resultLen2 = 0 + this.translator.decode(this.buf, 0, this.buf.length, out, outOff);
            this.bufOff = 0;
            int len2 = len - gapLen;
            int inOff2 = inOff + gapLen;
            int outOff2 = outOff + resultLen2;
            int chunkSize = len2 - (len2 % this.buf.length);
            resultLen = resultLen2 + this.translator.decode(in, inOff2, chunkSize, out, outOff2);
            len = len2 - chunkSize;
            inOff = inOff2 + chunkSize;
        }
        if (len != 0) {
            System.arraycopy(in, inOff, this.buf, this.bufOff, len);
            this.bufOff += len;
        }
        return resultLen;
    }
}
