package com.aowagie.text.pdf.codec.wmf;

import com.aowagie.text.Utilities;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/wmf/InputMeta.class */
public class InputMeta {
    private final InputStream in;
    private int length;

    public InputMeta(InputStream in) {
        this.in = in;
    }

    public int readWord() throws IOException {
        this.length += 2;
        int k1 = this.in.read();
        if (k1 < 0) {
            return 0;
        }
        return (k1 + (this.in.read() << 8)) & 65535;
    }

    public int readShort() throws IOException {
        int k = readWord();
        if (k > 32767) {
            k -= 65536;
        }
        return k;
    }

    public int readInt() throws IOException {
        this.length += 4;
        int k1 = this.in.read();
        if (k1 < 0) {
            return 0;
        }
        int k2 = this.in.read() << 8;
        int k3 = this.in.read() << 16;
        return k1 + k2 + k3 + (this.in.read() << 24);
    }

    int readByte() throws IOException {
        this.length++;
        return this.in.read() & 255;
    }

    void skip(int len) throws IOException {
        this.length += len;
        Utilities.skip(this.in, len);
    }

    public int getLength() {
        return this.length;
    }

    Color readColor() throws IOException {
        int red = readByte();
        int green = readByte();
        int blue = readByte();
        readByte();
        return new Color(red, green, blue);
    }
}
