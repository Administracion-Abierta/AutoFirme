package org.bouncycastle.mime;

import es.gob.afirma.core.misc.MimeHelper;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.mime.smime.SMimeParserContext;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mime/CanonicalOutputStream.class */
public class CanonicalOutputStream extends FilterOutputStream {
    protected int lastb;
    protected static byte[] newline = new byte[2];
    private final boolean is7Bit;

    public CanonicalOutputStream(SMimeParserContext sMimeParserContext, Headers headers, OutputStream outputStream) {
        super(outputStream);
        this.lastb = -1;
        if (headers.getContentType() != null) {
            this.is7Bit = (headers.getContentType() == null || headers.getContentType().equals(MimeHelper.DEFAULT_CONTENT_DESCRIPTION)) ? false : true;
        } else {
            this.is7Bit = sMimeParserContext.getDefaultContentTransferEncoding().equals("7bit");
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int i) throws IOException {
        if (!this.is7Bit) {
            this.out.write(i);
        } else if (i == 13) {
            this.out.write(newline);
        } else if (i != 10) {
            this.out.write(i);
        } else if (this.lastb != 13) {
            this.out.write(newline);
        }
        this.lastb = i;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] bArr) throws IOException {
        write(bArr, 0, bArr.length);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) throws IOException {
        for (int i3 = i; i3 != i + i2; i3++) {
            write(bArr[i3]);
        }
    }

    public void writeln() throws IOException {
        ((FilterOutputStream) this).out.write(newline);
    }

    static {
        newline[0] = 13;
        newline[1] = 10;
    }
}
