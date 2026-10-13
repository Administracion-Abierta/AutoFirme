package org.bouncycastle.cert.dane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/dane/DANEEntryStore.class */
public class DANEEntryStore implements Store {
    private final Map entries;

    DANEEntryStore(List list) {
        HashMap map = new HashMap();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            DANEEntry dANEEntry = (DANEEntry) it.next();
            map.put(dANEEntry.getDomainName(), dANEEntry);
        }
        this.entries = Collections.unmodifiableMap(map);
    }

    @Override // org.bouncycastle.util.Store
    public Collection getMatches(Selector selector) throws StoreException {
        if (selector == null) {
            return this.entries.values();
        }
        ArrayList arrayList = new ArrayList();
        for (Object obj : this.entries.values()) {
            if (selector.match(obj)) {
                arrayList.add(obj);
            }
        }
        return Collections.unmodifiableList(arrayList);
    }

    public Store toCertificateStore() {
        Collection matches = getMatches(null);
        ArrayList arrayList = new ArrayList(matches.size());
        Iterator it = matches.iterator();
        while (it.hasNext()) {
            arrayList.add(((DANEEntry) it.next()).getCertificate());
        }
        return new CollectionStore(arrayList);
    }
}
