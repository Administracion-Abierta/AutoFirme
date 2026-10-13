package org.spongycastle.util.io.pem;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import org.spongycastle.util.Strings;
import org.spongycastle.util.encoders.Base64;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/io/pem/PemWriter.class */
public class PemWriter extends BufferedWriter {
    private static final int LINE_LENGTH = 64;
    private final int nlLength;
    private char[] buf;

    public PemWriter(Writer out) {
        super(out);
        this.buf = new char[64];
        String nl = Strings.lineSeparator();
        if (nl != null) {
            this.nlLength = nl.length();
        } else {
            this.nlLength = 2;
        }
    }

    public int getOutputSize(PemObject obj) {
        int size = (2 * (obj.getType().length() + 10 + this.nlLength)) + 6 + 4;
        if (!obj.getHeaders().isEmpty()) {
            for (PemHeader hdr : obj.getHeaders()) {
                size += hdr.getName().length() + ": ".length() + hdr.getValue().length() + this.nlLength;
            }
            size += this.nlLength;
        }
        int dataLen = ((obj.getContent().length + 2) / 3) * 4;
        return size + dataLen + ((((dataLen + 64) - 1) / 64) * this.nlLength);
    }

    public void writeObject(PemObjectGenerator objGen) throws IOException {
        PemObject obj = objGen.generate();
        writePreEncapsulationBoundary(obj.getType());
        if (!obj.getHeaders().isEmpty()) {
            for (PemHeader hdr : obj.getHeaders()) {
                write(hdr.getName());
                write(": ");
                write(hdr.getValue());
                newLine();
            }
            newLine();
        }
        writeEncoded(obj.getContent());
        writePostEncapsulationBoundary(obj.getType());
    }

    private void writeEncoded(byte[] bytes) throws IOException {
        byte[] bytes2 = Base64.encode(bytes);
        int length = 0;
        while (true) {
            int i = length;
            if (i < bytes2.length) {
                int index = 0;
                while (index != this.buf.length && i + index < bytes2.length) {
                    this.buf[index] = (char) bytes2[i + index];
                    index++;
                }
                write(this.buf, 0, index);
                newLine();
                length = i + this.buf.length;
            } else {
                return;
            }
        }
    }

    private void writePreEncapsulationBoundary(String type) throws IOException {
        write("-----BEGIN " + type + "-----");
        newLine();
    }

    private void writePostEncapsulationBoundary(String type) throws IOException {
        write("-----END " + type + "-----");
        newLine();
    }
}
