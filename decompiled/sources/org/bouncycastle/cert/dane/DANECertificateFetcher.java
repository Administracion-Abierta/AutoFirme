package org.bouncycastle.cert.dane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/dane/DANECertificateFetcher.class */
public class DANECertificateFetcher {
    private final DANEEntryFetcherFactory fetcherFactory;
    private final DANEEntrySelectorFactory selectorFactory;

    public DANECertificateFetcher(DANEEntryFetcherFactory dANEEntryFetcherFactory, DigestCalculator digestCalculator) {
        this.fetcherFactory = dANEEntryFetcherFactory;
        this.selectorFactory = new DANEEntrySelectorFactory(digestCalculator);
    }

    public List fetch(String str) throws DANEException {
        DANEEntrySelector dANEEntrySelectorCreateSelector = this.selectorFactory.createSelector(str);
        List<DANEEntry> entries = this.fetcherFactory.build(dANEEntrySelectorCreateSelector.getDomainName()).getEntries();
        ArrayList arrayList = new ArrayList(entries.size());
        for (DANEEntry dANEEntry : entries) {
            if (dANEEntrySelectorCreateSelector.match(dANEEntry)) {
                arrayList.add(dANEEntry.getCertificate());
            }
        }
        return Collections.unmodifiableList(arrayList);
    }
}
