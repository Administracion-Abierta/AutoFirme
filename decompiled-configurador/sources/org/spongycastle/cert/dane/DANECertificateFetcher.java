package org.spongycastle.cert.dane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.spongycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/dane/DANECertificateFetcher.class */
public class DANECertificateFetcher {
    private final DANEEntryFetcherFactory fetcherFactory;
    private final DANEEntrySelectorFactory selectorFactory;

    public DANECertificateFetcher(DANEEntryFetcherFactory fetcherFactory, DigestCalculator digestCalculator) {
        this.fetcherFactory = fetcherFactory;
        this.selectorFactory = new DANEEntrySelectorFactory(digestCalculator);
    }

    public List fetch(String emailAddress) throws DANEException {
        DANEEntrySelector daneSelector = this.selectorFactory.createSelector(emailAddress);
        List<DANEEntry> matches = this.fetcherFactory.build(daneSelector.getDomainName()).getEntries();
        List certs = new ArrayList(matches.size());
        for (DANEEntry next : matches) {
            if (daneSelector.match(next)) {
                certs.add(next.getCertificate());
            }
        }
        return Collections.unmodifiableList(certs);
    }
}
