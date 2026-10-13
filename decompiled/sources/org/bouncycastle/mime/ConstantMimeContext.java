package org.bouncycastle.mime;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mime/ConstantMimeContext.class */
public class ConstantMimeContext implements MimeContext, MimeMultipartContext {
    public static final ConstantMimeContext Instance = new ConstantMimeContext();

    @Override // org.bouncycastle.mime.MimeContext
    public InputStream applyContext(Headers headers, InputStream inputStream) throws IOException {
        return inputStream;
    }

    @Override // org.bouncycastle.mime.MimeMultipartContext
    public MimeContext createContext(int i) throws IOException {
        return this;
    }
}
