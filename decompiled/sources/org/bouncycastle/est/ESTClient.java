package org.bouncycastle.est;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/ESTClient.class */
public interface ESTClient {
    ESTResponse doRequest(ESTRequest eSTRequest) throws IOException;
}
