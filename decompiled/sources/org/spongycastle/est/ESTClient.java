package org.spongycastle.est;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/est/ESTClient.class */
public interface ESTClient {
    ESTResponse doRequest(ESTRequest eSTRequest) throws IOException;
}
