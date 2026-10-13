package org.spongycastle.cert.dane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.spongycastle.util.CollectionStore;
import org.spongycastle.util.Selector;
import org.spongycastle.util.Store;
import org.spongycastle.util.StoreException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/dane/DANEEntryStore.class */
public class DANEEntryStore implements Store {
    private final Map entries;

    DANEEntryStore(List entries) {
        Map entryMap = new HashMap();
        Iterator it = entries.iterator();
        while (it.hasNext()) {
            DANEEntry entry = (DANEEntry) it.next();
            entryMap.put(entry.getDomainName(), entry);
        }
        this.entries = Collections.unmodifiableMap(entryMap);
    }

    @Override // org.spongycastle.util.Store
    public Collection getMatches(Selector selector) throws StoreException {
        if (selector == null) {
            return this.entries.values();
        }
        List results = new ArrayList();
        for (Object next : this.entries.values()) {
            if (selector.match(next)) {
                results.add(next);
            }
        }
        return Collections.unmodifiableList(results);
    }

    public Store toCertificateStore() {
        Collection<DANEEntry> col = getMatches(null);
        List certColl = new ArrayList(col.size());
        for (DANEEntry entry : col) {
            certColl.add(entry.getCertificate());
        }
        return new CollectionStore(certColl);
    }
}
