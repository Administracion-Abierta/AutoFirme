package org.bouncycastle.mime;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mime/MimeParserProvider.class */
public interface MimeParserProvider {
    MimeParser createParser(InputStream inputStream) throws IOException;

    MimeParser createParser(Headers headers, InputStream inputStream) throws IOException;
}
