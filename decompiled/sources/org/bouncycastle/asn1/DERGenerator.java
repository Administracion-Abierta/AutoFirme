package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/DERGenerator.class */
public abstract class DERGenerator extends ASN1Generator {
    private boolean _tagged;
    private boolean _isExplicit;
    private int _tagNo;

    protected DERGenerator(OutputStream outputStream) {
        super(outputStream);
        this._tagged = false;
    }

    public DERGenerator(OutputStream outputStream, int i, boolean z) {
        super(outputStream);
        this._tagged = false;
        this._tagged = true;
        this._isExplicit = z;
        this._tagNo = i;
    }

    private void writeLength(OutputStream outputStream, int i) throws IOException {
        if (i <= 127) {
            outputStream.write((byte) i);
            return;
        }
        int i2 = 1;
        int i3 = i;
        while (true) {
            int i4 = i3 >>> 8;
            i3 = i4;
            if (i4 == 0) {
                break;
            } else {
                i2++;
            }
        }
        outputStream.write((byte) (i2 | 128));
        for (int i5 = (i2 - 1) * 8; i5 >= 0; i5 -= 8) {
            outputStream.write((byte) (i >> i5));
        }
    }

    void writeDEREncoded(OutputStream outputStream, int i, byte[] bArr) throws IOException {
        outputStream.write(i);
        writeLength(outputStream, bArr.length);
        outputStream.write(bArr);
    }

    void writeDEREncoded(int i, byte[] bArr) throws IOException {
        if (!this._tagged) {
            writeDEREncoded(this._out, i, bArr);
            return;
        }
        int i2 = this._tagNo | 128;
        if (this._isExplicit) {
            int i3 = this._tagNo | 32 | 128;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            writeDEREncoded(byteArrayOutputStream, i, bArr);
            writeDEREncoded(this._out, i3, byteArrayOutputStream.toByteArray());
            return;
        }
        if ((i & 32) != 0) {
            writeDEREncoded(this._out, i2 | 32, bArr);
        } else {
            writeDEREncoded(this._out, i2, bArr);
        }
    }
}
