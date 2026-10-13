package org.spongycastle.cms;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSProcessable.class */
public interface CMSProcessable {
    void write(OutputStream outputStream) throws IOException, CMSException;

    Object getContent();
}
