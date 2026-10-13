package nu.xom;

import java.io.Writer;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/Latin1Writer.class */
class Latin1Writer extends TextWriter {
    Latin1Writer(Writer writer, String str) {
        super(writer, str);
    }

    @Override // nu.xom.TextWriter
    boolean needsEscaping(char c) {
        return c > 255;
    }
}
