package org.bouncycastle.asn1;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/DefiniteLengthInputStream.class */
class DefiniteLengthInputStream extends LimitedInputStream {
    private static final byte[] EMPTY_BYTES = new byte[0];
    private final int _originalLength;
    private int _remaining;

    DefiniteLengthInputStream(InputStream inputStream, int i, int i2) {
        super(inputStream, i2);
        if (i <= 0) {
            if (i < 0) {
                throw new IllegalArgumentException("negative lengths not allowed");
            }
            setParentEofDetect(true);
        }
        this._originalLength = i;
        this._remaining = i;
    }

    int getRemaining() {
        return this._remaining;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (this._remaining == 0) {
            return -1;
        }
        int i = this._in.read();
        if (i < 0) {
            throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
        }
        int i2 = this._remaining - 1;
        this._remaining = i2;
        if (i2 == 0) {
            setParentEofDetect(true);
        }
        return i;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (this._remaining == 0) {
            return -1;
        }
        int i3 = this._in.read(bArr, i, Math.min(i2, this._remaining));
        if (i3 < 0) {
            throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
        }
        int i4 = this._remaining - i3;
        this._remaining = i4;
        if (i4 == 0) {
            setParentEofDetect(true);
        }
        return i3;
    }

    void readAllIntoByteArray(byte[] bArr) throws IOException {
        if (this._remaining != bArr.length) {
            throw new IllegalArgumentException("buffer length not right for data");
        }
        if (this._remaining == 0) {
            return;
        }
        int limit = getLimit();
        if (this._remaining >= limit) {
            throw new IOException("corrupted stream - out of bounds length found: " + this._remaining + " >= " + limit);
        }
        int fully = this._remaining - Streams.readFully(this._in, bArr, 0, bArr.length);
        this._remaining = fully;
        if (fully != 0) {
            throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
        }
        setParentEofDetect(true);
    }

    byte[] toByteArray() throws IOException {
        if (this._remaining == 0) {
            return EMPTY_BYTES;
        }
        int limit = getLimit();
        if (this._remaining >= limit) {
            throw new IOException("corrupted stream - out of bounds length found: " + this._remaining + " >= " + limit);
        }
        byte[] bArr = new byte[this._remaining];
        int fully = this._remaining - Streams.readFully(this._in, bArr, 0, bArr.length);
        this._remaining = fully;
        if (fully != 0) {
            throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
        }
        setParentEofDetect(true);
        return bArr;
    }
}
