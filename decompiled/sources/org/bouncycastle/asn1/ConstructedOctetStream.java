package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ConstructedOctetStream.class */
class ConstructedOctetStream extends InputStream {
    private final ASN1StreamParser _parser;
    private boolean _first = true;
    private InputStream _currentStream;

    ConstructedOctetStream(ASN1StreamParser aSN1StreamParser) {
        this._parser = aSN1StreamParser;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        ASN1OctetStringParser nextParser;
        if (this._currentStream == null) {
            if (!this._first || (nextParser = getNextParser()) == null) {
                return -1;
            }
            this._first = false;
            this._currentStream = nextParser.getOctetStream();
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
                ASN1OctetStringParser nextParser2 = getNextParser();
                if (nextParser2 == null) {
                    this._currentStream = null;
                    if (i3 < 1) {
                        return -1;
                    }
                    return i3;
                }
                this._currentStream = nextParser2.getOctetStream();
            }
        }
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        ASN1OctetStringParser nextParser;
        if (this._currentStream == null) {
            if (!this._first || (nextParser = getNextParser()) == null) {
                return -1;
            }
            this._first = false;
            this._currentStream = nextParser.getOctetStream();
        }
        while (true) {
            int i = this._currentStream.read();
            if (i >= 0) {
                return i;
            }
            ASN1OctetStringParser nextParser2 = getNextParser();
            if (nextParser2 == null) {
                this._currentStream = null;
                return -1;
            }
            this._currentStream = nextParser2.getOctetStream();
        }
    }

    private ASN1OctetStringParser getNextParser() throws IOException {
        ASN1Encodable object = this._parser.readObject();
        if (object == null) {
            return null;
        }
        if (object instanceof ASN1OctetStringParser) {
            return (ASN1OctetStringParser) object;
        }
        throw new IOException("unknown object encountered: " + object.getClass());
    }
}
