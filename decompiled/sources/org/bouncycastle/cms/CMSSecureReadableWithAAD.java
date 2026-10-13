package org.bouncycastle.cms;

import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSSecureReadableWithAAD.class */
interface CMSSecureReadableWithAAD extends CMSSecureReadable {
    void setAADStream(OutputStream outputStream);

    OutputStream getAADStream();

    byte[] getMAC();
}
