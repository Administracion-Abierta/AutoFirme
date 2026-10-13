package org.bouncycastle.cert.dane;

import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/dane/DANEEntryFetcher.class */
public interface DANEEntryFetcher {
    List getEntries() throws DANEException;
}
