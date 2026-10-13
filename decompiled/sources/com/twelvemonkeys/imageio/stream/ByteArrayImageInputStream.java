package com.twelvemonkeys.imageio.stream;

import com.aowagie.text.ElementTags;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import javax.imageio.stream.ImageInputStreamImpl;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/stream/ByteArrayImageInputStream.class */
public final class ByteArrayImageInputStream extends ImageInputStreamImpl {
    private final byte[] data;
    private final int dataOffset;
    private final int dataLength;

    public ByteArrayImageInputStream(byte[] bArr) {
        this(bArr, 0, bArr != null ? bArr.length : -1);
    }

    public ByteArrayImageInputStream(byte[] bArr, int i, int i2) {
        this.data = (byte[]) Validate.notNull(bArr, "data");
        this.dataOffset = isMax(bArr.length, i, ElementTags.OFFSET);
        this.dataLength = isMax(bArr.length - i, i2, XSDatatype.FACET_LENGTH);
    }

    private static int isMax(int i, int i2, String str) {
        return ((Integer) Validate.isTrue(i2 >= 0 && i2 <= i, Integer.valueOf(i2), String.format("%s out of range [0, %d]: %d", str, Integer.valueOf(i), Integer.valueOf(i2)))).intValue();
    }

    public int read() throws IOException {
        if (this.streamPos >= this.dataLength) {
            return -1;
        }
        this.bitOffset = 0;
        byte[] bArr = this.data;
        long j = this.streamPos;
        this.streamPos = j + 1;
        return bArr[((int) j) + this.dataOffset] & 255;
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (this.streamPos >= this.dataLength) {
            return -1;
        }
        int iMin = (int) Math.min(((long) this.dataLength) - this.streamPos, i2);
        this.bitOffset = 0;
        System.arraycopy(this.data, ((int) this.streamPos) + this.dataOffset, bArr, i, iMin);
        this.streamPos += (long) iMin;
        return iMin;
    }

    public long length() {
        return this.dataLength;
    }

    public boolean isCached() {
        return true;
    }

    public boolean isCachedMemory() {
        return true;
    }
}
