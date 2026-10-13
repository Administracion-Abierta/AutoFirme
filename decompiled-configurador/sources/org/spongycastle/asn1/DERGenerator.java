package org.spongycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/DERGenerator.class */
public abstract class DERGenerator extends ASN1Generator {
    private boolean _tagged;
    private boolean _isExplicit;
    private int _tagNo;

    protected DERGenerator(OutputStream out) {
        super(out);
        this._tagged = false;
    }

    public DERGenerator(OutputStream out, int tagNo, boolean isExplicit) {
        super(out);
        this._tagged = false;
        this._tagged = true;
        this._isExplicit = isExplicit;
        this._tagNo = tagNo;
    }

    private void writeLength(OutputStream out, int length) throws IOException {
        if (length > 127) {
            int size = 1;
            int val = length;
            while (true) {
                int i = val >>> 8;
                val = i;
                if (i == 0) {
                    break;
                } else {
                    size++;
                }
            }
            out.write((byte) (size | 128));
            for (int i2 = (size - 1) * 8; i2 >= 0; i2 -= 8) {
                out.write((byte) (length >> i2));
            }
            return;
        }
        out.write((byte) length);
    }

    void writeDEREncoded(OutputStream out, int tag, byte[] bytes) throws IOException {
        out.write(tag);
        writeLength(out, bytes.length);
        out.write(bytes);
    }

    void writeDEREncoded(int tag, byte[] bytes) throws IOException {
        if (this._tagged) {
            int tagNum = this._tagNo | 128;
            if (this._isExplicit) {
                int newTag = this._tagNo | 32 | 128;
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                writeDEREncoded(bOut, tag, bytes);
                writeDEREncoded(this._out, newTag, bOut.toByteArray());
                return;
            }
            if ((tag & 32) != 0) {
                writeDEREncoded(this._out, tagNum | 32, bytes);
                return;
            } else {
                writeDEREncoded(this._out, tagNum, bytes);
                return;
            }
        }
        writeDEREncoded(this._out, tag, bytes);
    }
}
