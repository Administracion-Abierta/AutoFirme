package org.spongycastle.cert.dane;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/dane/DANEEntryStoreBuilder.class */
public class DANEEntryStoreBuilder {
    private final DANEEntryFetcherFactory daneEntryFetcher;

    public DANEEntryStoreBuilder(DANEEntryFetcherFactory daneEntryFetcher) {
        this.daneEntryFetcher = daneEntryFetcher;
    }

    public DANEEntryStore build(String domainName) throws DANEException {
        return new DANEEntryStore(this.daneEntryFetcher.build(domainName).getEntries());
    }
}
