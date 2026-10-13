package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.dvcs.Data;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/dvcs/DVCSRequestData.class */
public abstract class DVCSRequestData {
    protected Data data;

    protected DVCSRequestData(Data data) {
        this.data = data;
    }

    public Data toASN1Structure() {
        return this.data;
    }
}
