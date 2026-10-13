package org.spongycastle.asn1.eac;

import java.util.Hashtable;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/eac/BidirectionalMap.class */
public class BidirectionalMap extends Hashtable {
    private static final long serialVersionUID = -7457289971962812909L;
    Hashtable reverseMap = new Hashtable();

    public Object getReverse(Object o) {
        return this.reverseMap.get(o);
    }

    @Override // java.util.Hashtable, java.util.Dictionary, java.util.Map
    public Object put(Object key, Object o) {
        this.reverseMap.put(o, key);
        return super.put(key, o);
    }
}
