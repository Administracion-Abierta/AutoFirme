package org.bouncycastle.cert.dane;

import org.bouncycastle.util.Selector;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/dane/DANEEntrySelector.class */
public class DANEEntrySelector implements Selector {
    private final String domainName;

    DANEEntrySelector(String str) {
        this.domainName = str;
    }

    @Override // org.bouncycastle.util.Selector
    public boolean match(Object obj) {
        return ((DANEEntry) obj).getDomainName().equals(this.domainName);
    }

    @Override // org.bouncycastle.util.Selector
    public Object clone() {
        return this;
    }

    public String getDomainName() {
        return this.domainName;
    }
}
