package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ConstructedBitStream.class */
class ConstructedBitStream extends InputStream {
    private final ASN1StreamParser _parser;
    private final boolean _octetAligned;
    private boolean _first = true;
    private int _padBits = 0;
    private ASN1BitStringParser _currentParser;
    private InputStream _currentStream;

    ConstructedBitStream(ASN1StreamParser aSN1StreamParser, boolean z) {
        this._parser = aSN1StreamParser;
        this._octetAligned = z;
    }

    int getPadBits() {
        return this._padBits;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (this._currentStream == null) {
            if (!this._first) {
                return -1;
            }
            this._currentParser = getNextParser();
            if (this._currentParser == null) {
                return -1;
            }
            this._first = false;
            this._currentStream = this._currentParser.getBitStream();
        }
        int i3 = 0;
        while (true) {
            int i4 = this._currentStream.read(bArr, i + i3, i2 - i3);
            if (i4 >= 0) {
                i3 += i4;
                if (i3 == i2) {
                    return i3;
                }
            } else {
                this._padBits = this._currentParser.getPadBits();
                this._currentParser = getNextParser();
                if (this._currentParser == null) {
                    this._currentStream = null;
                    if (i3 < 1) {
                        return -1;
                    }
                    return i3;
                }
                this._currentStream = this._currentParser.getBitStream();
            }
        }
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (this._currentStream == null) {
            if (!this._first) {
                return -1;
            }
            this._currentParser = getNextParser();
            if (this._currentParser == null) {
                return -1;
            }
            this._first = false;
            this._currentStream = this._currentParser.getBitStream();
        }
        while (true) {
            int i = this._currentStream.read();
            if (i >= 0) {
                return i;
            }
            this._padBits = this._currentParser.getPadBits();
            this._currentParser = getNextParser();
            if (this._currentParser == null) {
                this._currentStream = null;
                return -1;
            }
            this._currentStream = this._currentParser.getBitStream();
        }
    }

    private ASN1BitStringParser getNextParser() throws IOException {
        ASN1Encodable object = this._parser.readObject();
        if (object == null) {
            if (!this._octetAligned || this._padBits == 0) {
                return null;
            }
            throw new IOException("expected octet-aligned bitstring, but found padBits: " + this._padBits);
        }
        if (!(object instanceof ASN1BitStringParser)) {
            throw new IOException("unknown object encountered: " + object.getClass());
        }
        if (this._padBits != 0) {
            throw new IOException("only the last nested bitstring can have padding");
        }
        return (ASN1BitStringParser) object;
    }
}
