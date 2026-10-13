package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSProcessable.class */
public interface CMSProcessable {
    void write(OutputStream outputStream) throws CMSException, IOException;

    Object getContent();
}
