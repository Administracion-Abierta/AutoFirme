package org.spongycastle.est;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/est/ESTHijacker.class */
public interface ESTHijacker {
    ESTResponse hijack(ESTRequest eSTRequest, Source source) throws IOException;
}
