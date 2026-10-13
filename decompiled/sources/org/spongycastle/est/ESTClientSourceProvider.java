package org.spongycastle.est;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/est/ESTClientSourceProvider.class */
public interface ESTClientSourceProvider {
    Source makeSource(String str, int i) throws IOException;
}
