package org.bouncycastle.mime;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mime/MimeMultipartContext.class */
public interface MimeMultipartContext extends MimeContext {
    MimeContext createContext(int i) throws IOException;
}
