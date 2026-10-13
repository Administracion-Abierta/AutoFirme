package org.spongycastle.cert.dane;

import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/dane/DANEEntryFetcher.class */
public interface DANEEntryFetcher {
    List getEntries() throws DANEException;
}
