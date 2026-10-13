package org.spongycastle.cert.dane;

import org.spongycastle.util.Selector;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/dane/DANEEntrySelector.class */
public class DANEEntrySelector implements Selector {
    private final String domainName;

    DANEEntrySelector(String domainName) {
        this.domainName = domainName;
    }

    @Override // org.spongycastle.util.Selector
    public boolean match(Object obj) {
        DANEEntry dEntry = (DANEEntry) obj;
        return dEntry.getDomainName().equals(this.domainName);
    }

    @Override // org.spongycastle.util.Selector
    public Object clone() {
        return this;
    }

    public String getDomainName() {
        return this.domainName;
    }
}
