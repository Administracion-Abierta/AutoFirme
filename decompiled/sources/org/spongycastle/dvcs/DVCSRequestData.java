package org.spongycastle.dvcs;

import org.spongycastle.asn1.dvcs.Data;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/dvcs/DVCSRequestData.class */
public abstract class DVCSRequestData {
    protected Data data;

    protected DVCSRequestData(Data data) {
        this.data = data;
    }

    public Data toASN1Structure() {
        return this.data;
    }
}
