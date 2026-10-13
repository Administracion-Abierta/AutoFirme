package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSReadable.class */
interface CMSReadable {
    InputStream getInputStream() throws CMSException, IOException;
}
