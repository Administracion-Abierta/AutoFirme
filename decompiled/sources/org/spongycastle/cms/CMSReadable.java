package org.spongycastle.cms;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSReadable.class */
interface CMSReadable {
    InputStream getInputStream() throws IOException, CMSException;
}
