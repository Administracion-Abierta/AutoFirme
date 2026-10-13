package nu.xom;

import java.io.Writer;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/UCSWriter.class */
class UCSWriter extends TextWriter {
    UCSWriter(Writer writer, String str) {
        super(writer, str);
    }

    @Override // nu.xom.TextWriter
    boolean needsEscaping(char c) {
        return c >= 55296 && c <= 57343;
    }
}
