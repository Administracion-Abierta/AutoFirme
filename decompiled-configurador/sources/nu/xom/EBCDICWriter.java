package nu.xom;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/EBCDICWriter.class */
class EBCDICWriter extends OutputStreamWriter {
    private static final int NEL = 133;
    private OutputStream raw;

    public EBCDICWriter(OutputStream outputStream) throws UnsupportedEncodingException {
        super(outputStream, "Cp037");
        this.raw = outputStream;
    }

    @Override // java.io.OutputStreamWriter, java.io.Writer
    public void write(int i) throws IOException {
        if (i != 133) {
            super.write(i);
        } else {
            flush();
            this.raw.write(21);
        }
    }
}
