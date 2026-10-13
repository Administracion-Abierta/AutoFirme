package org.spongycastle.asn1.est;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/est/Utils.class */
class Utils {
    Utils() {
    }

    static AttrOrOID[] clone(AttrOrOID[] ids) {
        AttrOrOID[] tmp = new AttrOrOID[ids.length];
        System.arraycopy(ids, 0, tmp, 0, ids.length);
        return tmp;
    }
}
