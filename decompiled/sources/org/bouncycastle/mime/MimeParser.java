package org.bouncycastle.mime;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mime/MimeParser.class */
public interface MimeParser {
    void parse(MimeParserListener mimeParserListener) throws IOException;
}
