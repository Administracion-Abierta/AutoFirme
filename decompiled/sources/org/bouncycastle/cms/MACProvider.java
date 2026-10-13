package org.bouncycastle.cms;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/MACProvider.class */
interface MACProvider {
    byte[] getMAC();

    void init() throws IOException;
}
