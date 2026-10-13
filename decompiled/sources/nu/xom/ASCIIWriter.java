package nu.xom;

import java.io.Writer;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/ASCIIWriter.class */
class ASCIIWriter extends TextWriter {
    ASCIIWriter(Writer writer, String str) {
        super(writer, str);
    }

    @Override // nu.xom.TextWriter
    boolean needsEscaping(char c) {
        return c > 127;
    }
}
