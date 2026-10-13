package org.bouncycastle.mime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mime/LineReader.class */
class LineReader {
    private final InputStream src;
    private int lastC = -1;

    LineReader(InputStream inputStream) {
        this.src = inputStream;
    }

    String readLine() throws IOException {
        int i;
        int i2;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (this.lastC == -1) {
            i = this.src.read();
        } else {
            if (this.lastC == 13) {
                return "";
            }
            i = this.lastC;
            this.lastC = -1;
        }
        while (i >= 0 && i != 13 && i != 10) {
            byteArrayOutputStream.write(i);
            i = this.src.read();
        }
        if (i == 13 && (i2 = this.src.read()) != 10 && i2 >= 0) {
            this.lastC = i2;
        }
        if (i < 0) {
            return null;
        }
        return Strings.fromUTF8ByteArray(byteArrayOutputStream.toByteArray());
    }
}
