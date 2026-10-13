package org.spongycastle.est;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/est/ESTSourceConnectionListener.class */
public interface ESTSourceConnectionListener<T, I> {
    ESTRequest onConnection(Source<T> source, ESTRequest eSTRequest) throws IOException;
}
