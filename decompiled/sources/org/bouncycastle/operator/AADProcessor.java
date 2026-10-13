package org.bouncycastle.operator;

import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/AADProcessor.class */
public interface AADProcessor {
    OutputStream getAADStream();

    byte[] getMAC();
}
