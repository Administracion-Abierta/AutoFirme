package org.spongycastle.asn1;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/ConstructedOctetStream.class */
class ConstructedOctetStream extends InputStream {
    private final ASN1StreamParser _parser;
    private boolean _first = true;
    private InputStream _currentStream;

    ConstructedOctetStream(ASN1StreamParser parser) {
        this._parser = parser;
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        ASN1OctetStringParser s;
        if (this._currentStream == null) {
            if (!this._first || (s = (ASN1OctetStringParser) this._parser.readObject()) == null) {
                return -1;
            }
            this._first = false;
            this._currentStream = s.getOctetStream();
        }
        int totalRead = 0;
        while (true) {
            int numRead = this._currentStream.read(b, off + totalRead, len - totalRead);
            if (numRead >= 0) {
                totalRead += numRead;
                if (totalRead == len) {
                    return totalRead;
                }
            } else {
                ASN1OctetStringParser aos = (ASN1OctetStringParser) this._parser.readObject();
                if (aos == null) {
                    this._currentStream = null;
                    if (totalRead < 1) {
                        return -1;
                    }
                    return totalRead;
                }
                this._currentStream = aos.getOctetStream();
            }
        }
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        ASN1OctetStringParser s;
        if (this._currentStream == null) {
            if (!this._first || (s = (ASN1OctetStringParser) this._parser.readObject()) == null) {
                return -1;
            }
            this._first = false;
            this._currentStream = s.getOctetStream();
        }
        while (true) {
            int b = this._currentStream.read();
            if (b >= 0) {
                return b;
            }
            ASN1OctetStringParser s2 = (ASN1OctetStringParser) this._parser.readObject();
            if (s2 == null) {
                this._currentStream = null;
                return -1;
            }
            this._currentStream = s2.getOctetStream();
        }
    }
}
