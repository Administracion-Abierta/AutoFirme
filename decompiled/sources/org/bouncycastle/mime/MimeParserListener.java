package org.bouncycastle.mime;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mime/MimeParserListener.class */
public interface MimeParserListener {
    MimeContext createContext(MimeParserContext mimeParserContext, Headers headers);

    void object(MimeParserContext mimeParserContext, Headers headers, InputStream inputStream) throws IOException;
}
