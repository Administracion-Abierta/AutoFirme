package org.spongycastle.cms;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/CMSSecureReadable.class */
interface CMSSecureReadable {
    InputStream getInputStream() throws IOException, CMSException;
}
