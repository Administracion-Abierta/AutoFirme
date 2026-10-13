package org.bouncycastle.est;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/ESTHijacker.class */
public interface ESTHijacker {
    ESTResponse hijack(ESTRequest eSTRequest, Source source) throws IOException;
}
